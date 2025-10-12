package com.srapp.ActivityRecognition;


import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

public class ActivityRecognitionManager {

    private final ActivityRecognitionClient client;
    private final Context ctx;

    public ActivityRecognitionManager(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        this.client = ActivityRecognition.getClient(this.ctx);
    }

    /** intervalMs = কত সময় পরপর আপডেট (e.g., 10000 = 10s) */
    public void start(long intervalMs) {
        PendingIntent pi = ActivityUpdatesReceiver.getPendingIntent(ctx);
        client.requestActivityUpdates(intervalMs, pi)
                .addOnSuccessListener(unused -> Log.d("AR-Manager", "Activity updates started"))
                .addOnFailureListener(e -> Log.e("AR-Manager", "start failed: " + e.getMessage()));
    }

    public void stop() {
        PendingIntent  pi = ActivityUpdatesReceiver.getPendingIntent(ctx);
        client.removeActivityUpdates(pi)
                .addOnSuccessListener(unused -> Log.d("AR-Manager", "Activity updates stopped"))
                .addOnFailureListener(e -> Log.e("AR-Manager", "stop failed: " + e.getMessage()));
    }
}
