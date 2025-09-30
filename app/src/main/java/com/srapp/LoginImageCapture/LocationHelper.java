package com.srapp.LoginImageCapture;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHelper {

    public interface SingleFixCallback {
        void onLocationReady(@NonNull Location loc);
        void onFailed(@NonNull String reason);
        void onResolutionRequired(ResolvableApiException resolvable);
    }

    public static final int REQ_CODE_LOCATION_PERMISSION = 5001;

    private final Activity activity;
    private final Context context;
    private final FusedLocationProviderClient fused;
    private final SettingsClient settingsClient;
    private LocationCallback updatesCallback;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public LocationHelper(@NonNull Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.fused = LocationServices.getFusedLocationProviderClient(activity);
        this.settingsClient = LocationServices.getSettingsClient(activity);
    }

    public boolean hasFinePermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestFinePermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_CODE_LOCATION_PERMISSION
        );
    }

    public boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = false, net = false;
        try { gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER); } catch (Exception ignored) {}
        try { net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER); } catch (Exception ignored) {}
        return gps || net;
    }

    /** Legacy LocationRequest for 19.0.1 */
    private LocationRequest buildRequest() {
        LocationRequest req = LocationRequest.create();
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        req.setInterval(2000);           // 2s
        req.setFastestInterval(1000);    // 1s
        req.setSmallestDisplacement(5f); // 5m
        return req;
    }

    private LocationSettingsRequest buildSettingsRequest() {
        return new LocationSettingsRequest.Builder()
                .addLocationRequest(buildRequest())
                .setAlwaysShow(true)
                .build();
    }

    private boolean isGoodFix(Location loc) {
        if (loc == null) return false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (loc.isFromMockProvider()) return false;
            }
        } catch (Throwable ignored) {}
        if (loc.hasAccuracy() && loc.getAccuracy() > 50f) return false;
        long ageMs = System.currentTimeMillis() - loc.getTime();
        return ageMs <= 20_000;
    }

    public void requestSingleFix(@NonNull SingleFixCallback cb) {
        if (!hasFinePermission()) {
            cb.onFailed("Location permission not granted");
            return;
        }
        settingsClient.checkLocationSettings(buildSettingsRequest())
                .addOnSuccessListener((LocationSettingsResponse resp) -> {
                    grabNow(cb);
                })
                .addOnFailureListener(e -> {
                    if (e instanceof ResolvableApiException) {
                        cb.onResolutionRequired((ResolvableApiException) e);
                    } else {
                        cb.onFailed("Location settings unsatisfied: " + e.getMessage());
                    }
                });
    }

    private void grabNow(@NonNull SingleFixCallback cb) {
        final CancellationTokenSource cts = new CancellationTokenSource();

        // In 19.0.1, getCurrentLocation takes an int priority (LocationRequest.PRIORITY_*)
        fused.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(loc -> {
                    if (isGoodFix(loc)) {
                        cb.onLocationReady(loc);
                    } else {
                        requestShortUpdates(cb);
                    }
                })
                .addOnFailureListener(e -> {
                    requestShortUpdates(cb);
                });

        // Absolute timeout 7s → lastLocation
        handler.postDelayed(() -> {
            try { cts.cancel(); } catch (Exception ignored) {}
            fused.getLastLocation()
                    .addOnSuccessListener(last -> {
                        if (isGoodFix(last)) {
                            cb.onLocationReady(last);
                        } else {
                            //cb.onFailed("Unable to acquire a fresh location fix");
                            cb.onFailed("");
                        }
                    })
                    .addOnFailureListener(err -> cb.onFailed("getLastLocation failed"));
        }, 7_000);
    }

    private void requestShortUpdates(@NonNull SingleFixCallback cb) {
        if (updatesCallback != null) {
            try { fused.removeLocationUpdates(updatesCallback); } catch (Exception ignored) {}
        }
        updatesCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location loc = result.getLastLocation();
                if (isGoodFix(loc)) {
                    try { fused.removeLocationUpdates(this); } catch (Exception ignored) {}
                    updatesCallback = null;
                    cb.onLocationReady(loc);
                }
            }
        };
        fused.requestLocationUpdates(buildRequest(), updatesCallback, Looper.getMainLooper());

        handler.postDelayed(() -> {
            if (updatesCallback != null) {
                try { fused.removeLocationUpdates(updatesCallback); } catch (Exception ignored) {}
                updatesCallback = null;

                fused.getLastLocation()
                        .addOnSuccessListener(last -> {
                            if (isGoodFix(last)) {
                                cb.onLocationReady(last);
                            } else {
                                //cb.onFailed("Timed out waiting for GPS");
                                cb.onFailed("");
                            }
                        })
                        .addOnFailureListener(err -> cb.onFailed("Timed out and lastLocation failed"));
            }
        }, 5_000);
    }

    public void stop() {
        if (updatesCallback != null) {
            try { fused.removeLocationUpdates(updatesCallback); } catch (Exception ignored) {}
            updatesCallback = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}
