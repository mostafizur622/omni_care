package com.srapp.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Model.GpsDao;

public class LocationSyncWorker extends Worker {

    public LocationSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        // interval read
        String s = getPreference(ctx, "interval");
        if (s == null || s.isEmpty()) return Result.success();

        long intervalMs;
        try { intervalMs = Math.max(30_000L, Long.parseLong(s)); }
        catch (Exception e) { intervalMs = 60_000L; }

        // healthy window = max(2*interval, 90s)
        long now = System.currentTimeMillis();
        long hb  = PreferenceManager.getDefaultSharedPreferences(ctx).getLong("gps_hb", 0L);
        long healthyWindow = Math.max(2L * intervalMs, 90_000L);
        boolean healthy = (now - hb) <= healthyWindow;

        if (!healthy) {
            // 1) Try to start FGS
            Intent svc = new Intent(ctx, GPSTracker.class);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(ctx, svc);
                } else {
                    ctx.startService(svc);
                }
            } catch (Throwable t) {
                // Android 12+: ForegroundServiceStartNotAllowedException ইত্যাদি
                Log.w("LocationSyncWorker", "FGS start failed, fallback to receiver", t);
                // 2) Fallback: broadcast → Alarm/receiver path kick
                ctx.sendBroadcast(new Intent(ctx, RestartServiceReceiver.class));
            }
        }

        return Result.success();
    }
    public String getPreference(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, null); // Returns null if key is not found
    }
}
