package com.srapp.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i("BootReceiver", "✅ Device reboot detected — restarting GPSTracker service...");
            try {
                String interval = getPreference(context, "interval");
                // Check if interval is null or empty
                if (interval != null && !interval.isEmpty()) {
                    Log.e("Calll", "ys");
                    KeepAliveWorker.schedule(context);
                    Intent serviceIntent = new Intent(context, GPSTracker.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                    AlarmScheduler.scheduleExactPing(context, 15 * 60_000L);
                } else {
                    Log.w("BootReceiver", "Interval is empty or null. GPSTracker will not start.");
                }

            } catch (Exception e) {
                Log.e("BootReceiver", "Failed to restart GPSTracker: " + e.getMessage());
            }
        }
    }
    public String getPreference(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, null); // Returns null if key is not found
    }
}
