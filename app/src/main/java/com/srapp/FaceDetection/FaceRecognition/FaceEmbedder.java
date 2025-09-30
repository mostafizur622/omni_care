package com.srapp.FaceDetection.FaceRecognition;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class FaceEmbedder {
    private static final String MODEL_FILE = "mobilefacenet.tflite"; // assets/ এ রাখবেন
    private static final int INPUT_SIZE = 112; // আপনার মডেল যদি 160/224 নেয়, সেটি দিন
    private static final boolean NORMALIZE_MINUS1_TO_1 = true;

    private final Interpreter tflite;

    public FaceEmbedder(Context ctx) throws IOException {
        Interpreter.Options opts = new Interpreter.Options();
        opts.setNumThreads(2);
        tflite = new Interpreter(loadModelFile(ctx, MODEL_FILE), opts);
    }

    private MappedByteBuffer loadModelFile(Context context, String model) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(model);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /** মুখের ক্রপকৃত bitmap দিন; embedding ফেরত পাবেন (L2-normalized) */
    public float[] embed(Bitmap face) {
        Bitmap input = resizeAndPad(face, INPUT_SIZE, INPUT_SIZE);
        float[][][][] blob = new float[1][INPUT_SIZE][INPUT_SIZE][3];

        int[] px = new int[INPUT_SIZE * INPUT_SIZE];
        input.getPixels(px, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);
        for (int i = 0; i < px.length; i++) {
            int c = px[i];
            float r = (float) ((c >> 16) & 0xFF);
            float g = (float) ((c >> 8) & 0xFF);
            float b = (float) (c & 0xFF);
            if (NORMALIZE_MINUS1_TO_1) {
                blob[0][i / INPUT_SIZE][i % INPUT_SIZE][0] = (r - 127.5f) / 128f;
                blob[0][i / INPUT_SIZE][i % INPUT_SIZE][1] = (g - 127.5f) / 128f;
                blob[0][i / INPUT_SIZE][i % INPUT_SIZE][2] = (b - 127.5f) / 128f;
            } else {
                blob[0][i / INPUT_SIZE][i % INPUT_SIZE][0] = r / 255f;
                blob[0][i / INPUT_SIZE][i % INPUT_SIZE][1] = g / 255f;
                blob[0][i / INPUT_SIZE][i % INPUT_SIZE][2] = b / 255f;
            }
        }

        // আউটপুট ডাইমেনশন: আপনার মডেল 128/512 যা-ই হোক
        float[][] embedding = new float[1][192]; // যদি 512 হয়, 128→512 করুন
        tflite.run(blob, embedding);

        // L2 normalize
        float[] v = embedding[0];
        float norm = 0f;
        for (float x : v) norm += x * x;
        norm = (float) Math.sqrt(Math.max(norm, 1e-10));
        for (int i = 0; i < v.length; i++) v[i] /= norm;
        return v;
    }

    /** স্কোয়ারে রিসাইজ (center-crop) */
    private Bitmap resizeAndPad(Bitmap src, int tw, int th) {
        float scale = Math.max(tw / (float) src.getWidth(), th / (float) src.getHeight());
        Matrix m = new Matrix();
        m.setScale(scale, scale);
        Bitmap scaled = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
        Bitmap out = Bitmap.createBitmap(tw, th, Config.ARGB_8888);
        Canvas c = new Canvas(out);
        int dx = (tw - scaled.getWidth()) / 2;
        int dy = (th - scaled.getHeight()) / 2;
        c.drawBitmap(scaled, dx, dy, null);
        return out;
    }

    public static float cosineSimilarity(float[] a, float[] b) {
        float dot = 0f, na = 0f, nb = 0f;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        float denom = (float)(Math.sqrt(na) * Math.sqrt(nb));
        return (denom > 0f) ? dot / denom : -1f;
    }

    public void close() { tflite.close(); }
}
