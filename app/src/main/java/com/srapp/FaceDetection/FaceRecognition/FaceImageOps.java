package com.srapp.FaceDetection.FaceRecognition;

// FaceImageOps.java (নতুন)

import android.graphics.*;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;
import android.graphics.PointF;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FaceImageOps {

    // ====== Public helpers ======
    private static final float[] ARC_DST_3PT = new float[] {
            38.2946f, 51.6963f,   // left eye
            73.5318f, 51.5014f,   // right eye
            56.0252f, 71.7366f    // nose
    };

    public static Bitmap alignToArcface112(Bitmap src, FaceDetector detector) {
        try {
            Face face = detectSingleFace(src, detector);
            if (face == null) return null;

            // landmarks
            PointF le = getEyeCenter(face, true);
            PointF re = getEyeCenter(face, false);
            PointF nose = getNose(face);

            // fallback: নাক না পেলে মুখের কেন্দ্র (মাউথ লেফট/রাইট গড়)
            if (nose == null) {
                PointF ml = getMouthCorner(face, true);
                PointF mr = getMouthCorner(face, false);
                if (ml != null && mr != null) {
                    nose = new PointF((ml.x + mr.x) / 2f, (ml.y + mr.y) / 2f);
                }
            }
            if (le == null || re == null || nose == null) {
                Log.w("Align", "missing points le=" + (le!=null) + " re=" + (re!=null) + " nose=" + (nose!=null));
                return null;
            }

            float[] srcPts = new float[] { le.x, le.y, re.x, re.y, nose.x, nose.y };
            float[] dstPts = ARC_DST_3PT.clone();

            Matrix M = new Matrix();
            boolean ok = M.setPolyToPoly(srcPts, 0, dstPts, 0, 3);
            if (!ok) {
                Log.e("Align","setPolyToPoly failed");
                return null;
            }

            // আউটপুট 112x112
            Bitmap out = Bitmap.createBitmap(112, 112, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(out);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            c.drawBitmap(src, M, p);
            return out;

        } catch (Exception e) {
            Log.e("Align","ex: "+e);
            return null;
        }
    }

    public static Bitmap unmirror(Bitmap src) {
        Matrix m = new Matrix();
        m.preScale(-1f, 1f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
    }

    /** 112×112 RGB মুখ (আলIGNED) রিটার্ন করে; ব্যর্থ হলে null */
    public static Bitmap alignAndCropBest(Bitmap src, FaceDetector detector) {
        try {
            Face face = detectSingleFace(src, detector);
            if (face == null) return null;

            // 1) চোখের সেন্টার আনুন (landmark বা contour)
            PointF le = getEyeCenter(face, true);
            PointF re = getEyeCenter(face, false);

            // যদি landmark না থাকে (কিছু কনফিগে আসতে পারে), বাউন্ডিং বক্সে fallback
            if (le == null || re == null) {
                Log.w("FaceImageOps","eye landmarks missing -> fallback to bbox center crop");
                return centerCropTo112(src, expandRect(face.getBoundingBox(), src.getWidth(), src.getHeight(), 28));
            }

            // 2) চোখ horizontal করতে ঘোরান
            float angle = (float) Math.toDegrees(Math.atan2(re.y - le.y, re.x - le.x));
            Bitmap rotated = rotate(src, -angle);

            // ঘোরানোর পর বক্সও ঘোরে—সহজ রাখতে, নতুন করে face detect করুন (স্লো কিন্তু একবারই)
            Face face2 = detectSingleFace(rotated, detector);
            if (face2 == null) return null;

            Rect box = expandRect(face2.getBoundingBox(), rotated.getWidth(), rotated.getHeight(), 32);

            // 3) বক্স থেকে স্কোয়ার ক্রপ + 112×112
            return cropSquareTo112(rotated, box);

        } catch (Exception e) {
            Log.e("FaceImageOps","align ex: "+e);
            return null;
        }
    }

    // ====== Internals ======
    private static Face detectSingleFace(Bitmap src, FaceDetector detector) throws InterruptedException {
        InputImage img = InputImage.fromBitmap(src, 0);
        CountDownLatch latch = new CountDownLatch(1);
        final Face[] out = new Face[1];

        detector.process(img)
                .addOnSuccessListener(faces -> {
                    if (faces != null && faces.size() > 0) out[0] = faces.get(0);
                })
                .addOnCompleteListener(t -> latch.countDown());

        latch.await(1200, TimeUnit.MILLISECONDS);
        return out[0];
    }

    private static Bitmap rotate(Bitmap bm, float deg) {
        Matrix m = new Matrix();
        m.postRotate(deg);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
    }

    private static Rect expandRect(Rect in, int w, int h, int m) {
        int l = Math.max(0, in.left - m);
        int t = Math.max(0, in.top - m);
        int r = Math.min(w, in.right + m);
        int b = Math.min(h, in.bottom + m);
        return new Rect(l, t, r, b);
    }

    private static Bitmap cropSquareTo112(Bitmap src, Rect box) {
        // স্কোয়ার নিন
        int bw = box.width(), bh = box.height();
        int side = Math.min(Math.max(bw, bh), Math.min(src.getWidth(), src.getHeight()));
        int cx = box.centerX(), cy = box.centerY();
        int left = Math.max(0, cx - side/2);
        int top  = Math.max(0, cy - side/2);
        left = Math.min(left, src.getWidth() - side);
        top  = Math.min(top,  src.getHeight() - side);

        Bitmap square = Bitmap.createBitmap(src, left, top, side, side);
        return Bitmap.createScaledBitmap(square, 112, 112, true);
    }

    private static Bitmap centerCropTo112(Bitmap src, Rect box) {
        return cropSquareTo112(src, box);
    }
    private static PointF getNose(Face face) {
        FaceLandmark lm = face.getLandmark(FaceLandmark.NOSE_BASE);
        if (lm != null && lm.getPosition() != null) {
            PointF p = lm.getPosition();
            return new PointF(p.x, p.y);
        }
        return null;
    }

    private static PointF getMouthCorner(Face face, boolean left) {
        FaceLandmark lm = face.getLandmark(left ? FaceLandmark.MOUTH_LEFT : FaceLandmark.MOUTH_RIGHT);
        if (lm != null && lm.getPosition() != null) {
            PointF p = lm.getPosition();
            return new PointF(p.x, p.y);
        }
        return null;
    }
    private static PointF getEyeCenter(Face face, boolean left) {
        FaceLandmark lm = face.getLandmark(left ? FaceLandmark.LEFT_EYE : FaceLandmark.RIGHT_EYE);
        if (lm != null && lm.getPosition() != null) {
            PointF p = lm.getPosition();
            return new PointF(p.x, p.y); // android.graphics.PointF
        }
        java.util.List<FaceContour> contours = face.getAllContours();
        if (contours != null) {
            int type = left ? FaceContour.LEFT_EYE : FaceContour.RIGHT_EYE;
            for (FaceContour c : contours) {
                if (c.getFaceContourType() == type && c.getPoints().size() > 0) {
                    float sx=0, sy=0; int n=0;
                    for (PointF q : c.getPoints()) { sx+=q.x; sy+=q.y; n++; }
                    return new PointF(sx/n, sy/n);
                }
            }
        }
        return null;
    }
}
