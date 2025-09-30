package com.srapp.FaceDetection.FaceRecognition;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.mlkit.vision.face.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MatchVerifier {

    // আপনার API: ক্যাপচার্ড ইউজারের জন্য রেফারেন্স ইমেজ দেয়
    // উদাহরণ: GET https://example.com/api/users/{userId}/photo -> { "image_url": "..." } বা সরাসরি ইমেজ
    private final OkHttpClient http = new OkHttpClient();
    private final FaceEmbedder embedder;
    private final FaceDetector faceDetector;

    // থ্রেশহোল্ড: cosine similarity 0..1 (উচ্চতর মান = বেশি ম্যাচ)
    // MobileFaceNet/ArcFace এ 0.5–0.7 ভালো স্টার্ট; পরিবেশ দেখে টিউন করুন
    private static final float SIM_THRESH = 0.45f;

    public MatchVerifier(Context ctx) throws Exception {
        embedder = new FaceEmbedder(ctx);
        FaceDetectorOptions opts = new FaceDetectorOptions.Builder()
              /*  .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)*/
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)   // 👈 জরুরি
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
              /*  .enableTracking()*/
                .build();
        faceDetector = FaceDetection.getClient(opts);
    }

    /**
     * liveBmp = ক্যাপচার্ড (liveness পাস করা) ফটো
     * refUrlOrDirect = আপনার API যেটা দেয় (ডাইরেক্ট ইমেজ URL হলে ভালো)
     */
    public boolean verify(Bitmap liveBmp, String refImageUrl) {
        try {
            Bitmap refBmp = downloadBitmap(refImageUrl);
            if (refBmp == null) { Log.e("Verify", "ref download null"); return false; }

            // --- (A) ফ্রন্ট-ক্যামেরা মিরর টগল টেস্ট (ডিবাগের জন্য) ---
            // liveBmp একবার unmirror করে, আরেকবার না করে—দুটো similarity লগ করুন
            Bitmap liveUnmir = FaceImageOps.unmirror(liveBmp);  // নিচে helper দিলাম

            // --- (B) ভালো ক্রপ/অ্যালাইন (চোখ সোজা) ---
            Bitmap liveAligned = FaceImageOps.alignToArcface112(liveUnmir, faceDetector); // 112x112
            Bitmap refAligned  = FaceImageOps.alignToArcface112(refBmp,   faceDetector); // 112x112

            if (liveAligned == null || refAligned == null) {
                Log.e("Verify","align crop failed: live=" + (liveAligned!=null) + " ref=" + (refAligned!=null));
                return false;
            }

            float[] e1 = embedder.embed(liveAligned);
            float[] e2 = embedder.embed(refAligned);
            float simUnmir = FaceEmbedder.cosineSimilarity(e1, e2);
            Log.d("Verify", "sim (unmirrored live) = " + simUnmir);

            // আরেকটা টেস্ট: liveBmp কে unmirror না করে একই অ্যালাইনমেন্টে নিন
            Bitmap liveAlignedNoFlip = FaceImageOps.alignAndCropBest(liveBmp, faceDetector);
            if (liveAlignedNoFlip != null) {
                float[] e1b = embedder.embed(liveAlignedNoFlip);
                float simNoFlip = FaceEmbedder.cosineSimilarity(e1b, e2);
                Log.d("Verify", "sim (no flip live) = " + simNoFlip);
            }

            boolean ok = simUnmir >= SIM_THRESH; // থ্রেশহোল্ড 0.45–0.60 টিউন করবেন
            Log.d("Verify","TH=" + SIM_THRESH + " -> " + (ok ? "MATCH" : "MISMATCH"));
            return ok;
        } catch (Exception e) {
            Log.e("Verify","ex: "+e);
            return false;
        }
    }

    private Bitmap downloadBitmap(String url) {
        try {
            Request req = new Request.Builder().url(url).build();
            try (Response resp = http.newCall(req).execute()) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    android.util.Log.e("MatchVerifier", "HTTP failed code=" + (resp != null ? resp.code() : -1));
                    return null;
                }
                byte[] bytes = resp.body().bytes();
                Bitmap raw = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap fixed = applyExifOrientation(bytes, raw);

                android.util.Log.d("MatchVerifier", "ref bytes=" + bytes.length);
                return ensureMaxSize(fixed, 1280);
            }
        } catch (Exception e) {
            android.util.Log.e("MatchVerifier", "download ex: " + e);
            return null;
        }
    }

    public void close() {
        try { faceDetector.close(); } catch (Exception ignore) {}
        embedder.close();
    }
    private Bitmap applyExifOrientation(byte[] jpegBytes, Bitmap bmp) {
        try {
            androidx.exifinterface.media.ExifInterface exif =
                    new androidx.exifinterface.media.ExifInterface(new java.io.ByteArrayInputStream(jpegBytes));
            int o = exif.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL);
            android.graphics.Matrix m = new android.graphics.Matrix();
            switch (o) {
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:  m.postRotate(90); break;
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180: m.postRotate(180); break;
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270: m.postRotate(270); break;
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL: m.preScale(-1,1); break;
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_VERTICAL:   m.preScale(1,-1); break;
                default: return bmp;
            }
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
        } catch (Exception e) {
            return bmp;
        }
    }

    private Bitmap ensureMaxSize(Bitmap src, int maxDim) {
        int w = src.getWidth(), h = src.getHeight();
        int md = Math.max(w, h);
        if (md <= maxDim) return src;
        float s = maxDim / (float) md;
        return Bitmap.createScaledBitmap(src, Math.round(w*s), Math.round(h*s), true);
    }
}
