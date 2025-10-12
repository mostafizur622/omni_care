package com.srapp.ActivityRecognition;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityUpdatesReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.srapp.ACTIVITY_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ActivityRecognitionResult.hasResult(intent)) return;

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        if (result == null) return;

        // সর্বোচ্চ confidence activity নিন
        DetectedActivity best = getBestActivity(result.getProbableActivities());

        String status = mapActivityToStatus(best);
        int confidence = best.getConfidence();

        Log.d("AR-Receiver", "Detected: " + best.getType() + " conf=" + confidence + " => status=" + status);

        // Confidence threshold (টিউন করতে পারেন)
        if (confidence < 60) {
            // খুব অনিশ্চিত হলে আপডেট না-ও করতে পারেন
            return;
        }

        // SharedPreferences এ সেভ করুন — যাতে location সেভের সময় status পড়তে পারেন
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putString("last_activity_status", status)
                .putLong("last_activity_ts", System.currentTimeMillis())
                .apply();
    }

    private DetectedActivity getBestActivity(List<DetectedActivity> list) {
        DetectedActivity best = null;
        int max = -1;
        for (DetectedActivity a : list) {
            if (a.getConfidence() > max) {
                max = a.getConfidence();
                best = a;
            }
        }
        return best != null ? best : new DetectedActivity(DetectedActivity.UNKNOWN, 0);
    }

    private String mapActivityToStatus(DetectedActivity a) {
        switch (a.getType()) {
            case DetectedActivity.STILL: return "standing";
            case DetectedActivity.ON_FOOT:
            case DetectedActivity.WALKING: return "walking";
            case DetectedActivity.RUNNING: return "running";
            case DetectedActivity.ON_BICYCLE: return "bicycle";
            case DetectedActivity.IN_VEHICLE: return "vehicle";
            case DetectedActivity.TILTING:
            case DetectedActivity.UNKNOWN:
            default: return "unknown";
        }
    }

    // Helper: PendingIntent বানানোর মেথড (S+ এ FLAG_MUTABLE দরকার)
    public static PendingIntent getPendingIntent(Context ctx) {
        Intent i = new Intent(ctx, ActivityUpdatesReceiver.class).setAction(ACTION);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        return PendingIntent.getBroadcast(ctx, 1001, i, flags);
    }
}
