package com.srapp.FaceDetection.FaceRecognition;


import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FaceUtils {

    private static final int CROP_MARGIN_PX = 24; // বক্সের চারপাশে একটু মার্জিন

    /** একটিমাত্র মুখ ধরে ক্রপ করে ফেরত দেয়; না পেলে null */
    public static Bitmap cropSingleFaceBlocking(Bitmap src, FaceDetector detector) {
        try {
            InputImage image = InputImage.fromBitmap(src, 0);
            CountDownLatch latch = new CountDownLatch(1);
            final Bitmap[] result = new Bitmap[1];
            Task<java.util.List<Face>> task = detector.process(image);
            task.addOnCompleteListener((OnCompleteListener<java.util.List<Face>>) t -> {
                if (t.isSuccessful() && t.getResult() != null && t.getResult().size() > 0) {
                    Face f = t.getResult().get(0);
                    Rect r = expandRect(f.getBoundingBox(), src.getWidth(), src.getHeight(), CROP_MARGIN_PX);
                    result[0] = Bitmap.createBitmap(src, r.left, r.top, r.width(), r.height());
                }
                latch.countDown();
            });
            latch.await(1500, TimeUnit.MILLISECONDS); // ~1.5s timeout
            return result[0];
        } catch (Exception e) {
            return null;
        }
    }

    private static Rect expandRect(Rect in, int w, int h, int m) {
        int l = Math.max(0, in.left - m);
        int t = Math.max(0, in.top - m);
        int r = Math.min(w, in.right + m);
        int b = Math.min(h, in.bottom + m);
        return new Rect(l, t, r, b);
    }
}
