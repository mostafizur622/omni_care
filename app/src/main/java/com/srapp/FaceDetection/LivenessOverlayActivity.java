package com.srapp.FaceDetection;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Size;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.srapp.R;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LivenessOverlayActivity extends AppCompatActivity {

    public static final String EXTRA_BASE64 = "image_base64";

    private PreviewView previewView;
    private CircleLivenessOverlayView  overlay;
    private TextView instructionView;

    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private boolean isFront = true;

    private FaceDetector detector;
    private final BlinkAndMotionValidator validator = new BlinkAndMotionValidator();
    private BlinkAndMotionValidator.Phase lastPhase = null;
    // LivenessOverlayActivity.java (class-এর মধ্যে)
    private static final float MAX_YAW_DEG   = 20f; // বামে/ডানে ঘোরানো
    private static final float MAX_PITCH_DEG = 15f; // ওপর/নিচে
    private static final float MAX_ROLL_DEG  = 15f; // কাত/রোল
    private volatile boolean poseOk = false;
    private final ActivityResultLauncher<String> reqCam =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startCamera();
                else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness);

        previewView = findViewById(R.id.previewView);
        overlay = findViewById(R.id.overlay);
        instructionView = findViewById(R.id.instruction);

        FaceDetectorOptions opts = new FaceDetectorOptions.Builder()
/*                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // eye probs
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)   */
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)       // 👈 landmarks
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)         // 👈 contours (fallback)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)// overlay dots
                .enableTracking()
                .build();
        detector = FaceDetection.getClient(opts);

        cameraExecutor = Executors.newSingleThreadExecutor();
        reqCam.launch(Manifest.permission.CAMERA);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> fut = ProcessCameraProvider.getInstance(this);
        fut.addListener(() -> {
            try {
                ProcessCameraProvider provider = fut.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                analysis.setAnalyzer(cameraExecutor, this::analyze);

                provider.unbindAll();
                provider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview, imageCapture, analysis
                );

            } catch (Exception e) {
                Toast.makeText(this, "Camera start failed", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyze(ImageProxy proxy) {
        try {
            if (proxy.getImage() == null) { proxy.close(); return; }

            int rotationDegrees = proxy.getImageInfo().getRotationDegrees();
            InputImage img = InputImage.fromMediaImage(proxy.getImage(), rotationDegrees);

            // ইমেজ ডাইমেনশন (রোটেশন বিবেচনায়) — overlay mapping-এর জন্য
            int iw = (rotationDegrees == 0 || rotationDegrees == 180) ? proxy.getWidth() : proxy.getHeight();
            int ih = (rotationDegrees == 0 || rotationDegrees == 180) ? proxy.getHeight() : proxy.getWidth();

            detector.process(img)
                    .addOnSuccessListener(faces -> {
                        if (validator.alreadyCaptured()) { proxy.close(); return; }

                        if (faces.size() == 1) {
                            Face f = faces.get(0);
                            Integer tid = f.getTrackingId();
                            float l = f.getLeftEyeOpenProbability() != null ? f.getLeftEyeOpenProbability() : -1f;
                            float r = f.getRightEyeOpenProbability() != null ? f.getRightEyeOpenProbability() : -1f;

                            validator.update(tid, l, r);
// head pose (abs degree)
                            float yaw   = Math.abs(f.getHeadEulerAngleY()); // left/right
                            float pitch = Math.abs(f.getHeadEulerAngleX()); // up/down
                            float roll  = Math.abs(f.getHeadEulerAngleZ()); // tilt

                            poseOk = (yaw <= MAX_YAW_DEG) && (pitch <= MAX_PITCH_DEG) && (roll <= MAX_ROLL_DEG);

                            BlinkAndMotionValidator.Phase ph = validator.getPhase();
                            updateUiForPhase(ph);

                            if (ph == BlinkAndMotionValidator.Phase.VERIFIED) {
                                if (!poseOk) {
                                    // মুখ সোজা না—ক্যাপচার করবেন না
                                    runOnUiThread(() ->
                                            instructionView.setText("মুখটা সোজা রাখুন • খুব বেশি মাথা ঘোরাবেন না"));
                                    return; // captureHighRes() কল করবেন না
                                }
                                validator.markCaptured();
                                runOnUiThread(() -> {
                                    instructionView.setText("✅ ভেরিফায়েড! ছবি তোলা হচ্ছে…");
                                    captureHighRes();
                                });
                            }
                        } else {
                            validator.reset();
                            updateUiForPhase(validator.getPhase());
                        }
                    })
                    .addOnCompleteListener(t -> proxy.close());
        } catch (Exception e) {
            proxy.close();
        }
    }
    private void updateUiForPhase(BlinkAndMotionValidator.Phase ph) {
        if (lastPhase == ph) return;
        lastPhase = ph;

        runOnUiThread(() -> {
            // সার্কুলার রিং আপডেট
            switch (ph) {
                case NEED_OPEN:
                    overlay.updatePhase(CircleLivenessOverlayView.Phase.NEED_OPEN);
                    instructionView.setText("ধাপ ১/৩: ক্যামেরার দিকে তাকিয়ে থাকুন 👀");
                    break;
                case NEED_CLOSED:
                    overlay.updatePhase(CircleLivenessOverlayView.Phase.NEED_CLOSED);
                    instructionView.setText("ধাপ ২/৩: এখন দু’চোখ বন্ধ করুন 🙈");
                    break;
                case NEED_REOPEN:
                    overlay.updatePhase(CircleLivenessOverlayView.Phase.NEED_REOPEN);
                    instructionView.setText("ধাপ ৩/৩: আবার চোখ খুলুন 🙉");
                    break;
                case VERIFIED:
                    overlay.updatePhase(CircleLivenessOverlayView.Phase.VERIFIED);
                    instructionView.setText("✅ ভেরিফায়েড! ছবি তোলা হচ্ছে…");
                    break;
            }
        });
    }
    private void maybeUpdateInstruction(BlinkAndMotionValidator.Phase phase) {
        if (phase == lastPhase) return;
        lastPhase = phase;
        runOnUiThread(() -> {
            switch (phase) {
                case NEED_OPEN:
                    instructionView.setText("ধাপ ১: ক্যামেরার দিকে তাকিয়ে থাকুন 👀");
                    break;
                case NEED_CLOSED:
                    instructionView.setText("ধাপ ২: এখন দু’চোখ বন্ধ করুন 🙈");
                    break;
                case NEED_REOPEN:
                    instructionView.setText("ধাপ ৩: আবার চোখ খুলুন 🙉");
                    break;
                case VERIFIED:
                    instructionView.setText("✅ ভেরিফায়েড! ছবি তোলা হচ্ছে…");
                    break;
            }
        });
    }

    private void captureHighRes() {
        if (imageCapture == null) { setResult(Activity.RESULT_CANCELED); finish(); return; }

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(ImageProxy image) {
                        Bitmap bmp = YuvToRgbConverter.toBitmap(image);
                        image.close();

                        if (bmp == null) { // rare fallback
                            bmp = previewView.getBitmap();
                        }

                        if (bmp != null) {
                            String base64 = bitmapToBase64(bmp);
                            Intent data = new Intent();
                            data.putExtra(EXTRA_BASE64, base64);
                            setResult(Activity.RESULT_OK, data);
                        } else {
                            setResult(Activity.RESULT_CANCELED);
                        }
                        finish();
                    }

                    @Override
                    public void onError(ImageCaptureException exception) {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
        return Base64.encodeToString(os.toByteArray(), Base64.NO_WRAP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) cameraExecutor.shutdown();
        if (detector != null) detector.close();
    }


}
