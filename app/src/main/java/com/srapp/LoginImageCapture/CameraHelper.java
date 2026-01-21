package com.srapp.LoginImageCapture;

/******
 **** Created By  TANVIR3488 AT 18/8/25 11:43 PM
 ******/

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class CameraHelper {

    public interface CameraCallback {
        void onImageCaptured(Bitmap bitmap, String base64String);
        void onPermissionDenied();
    }

    private static final int CAMERA_PERMISSION_CODE = 2001;
    private static final int CAMERA_REQUEST_CODE = 2002;

    private final Activity activity;
    private final CameraCallback callback;
    private Uri currentPhotoUri;
    private File currentPhotoFile;
    public CameraHelper(Activity activity, CameraCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    /** Call this method to start the flow */
    public void startCameraFlow() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE);
        }
    }


    public void handleRequestPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                callback.onPermissionDenied();
            }
        }
    }


    private void openCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(activity.getPackageManager()) != null) {
//            activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
//        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) == null) return;

        File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && !dir.exists()) dir.mkdirs();
        currentPhotoFile = new File(dir, "IMG_" + System.currentTimeMillis() + ".jpg");

        currentPhotoUri = FileProvider.getUriForFile(
                activity, activity.getPackageName() + ".fileprovider", currentPhotoFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }


/*    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = (Bitmap) extras.get("data");
                if (bitmap != null) {
                    String base64Image = convertBitmapToBase64(bitmap);
                    callback.onImageCaptured(bitmap, base64Image);
                }
            }
        }
    }*/

    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // ফুল-রেজুলিউশন ফাইল থেকে পড়ুন
            try (InputStream is = activity.getContentResolver().openInputStream(currentPhotoUri)) {
                Bitmap bmp = BitmapFactory.decodeStream(is); // চাইলে পরে স্কেল করবেন
                // ✅ rotate bitmap using EXIF from the actual file
                Bitmap rotated = rotateBitmapIfRequired(bmp, currentPhotoFile);
                String base64 = convertBitmapToBase64(rotated, 30); // 90–95 ভালো
                callback.onImageCaptured(rotated, base64);
            } catch (Exception e) {
                Toast.makeText(activity, "Read failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String convertBitmapToBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
        return Base64.encodeToString(os.toByteArray(), Base64.NO_WRAP);
    }
//    private String convertBitmapToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] imageBytes = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
//    }
private Bitmap rotateBitmapIfRequired(Bitmap bitmap, File photoFile) {
    try {
        ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
        );

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.preScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.preScale(1, -1);
                break;

            default:
                return bitmap;
        }

        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Optional: free original if different
        if (rotated != bitmap) bitmap.recycle();

        return rotated;

    } catch (Exception e) {
        return bitmap;
    }
}
}

