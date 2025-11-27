package com.srapp.Util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.core.content.ContextCompat;

public class RestartServiceReceiver extends BroadcastReceiver {
    private static final String HB_KEY = "gps_hb";

    @Override
    public void onReceive(Context c, Intent i) {
        long last = PreferenceManager.getDefaultSharedPreferences(c).getLong(HB_KEY, 0L);
        boolean healthy = System.currentTimeMillis() - last <= 60_000L; // 1min freshness
        String interval = getPreference(c, "interval");
        if (interval != null && !interval.isEmpty()) {
            if (!healthy) {
                Log.w("RestartServiceReceiver", "HB stale → reviving service");
                Intent svc = new Intent(c, GPSTracker.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(c, svc);
                } else {
                    c.startService(svc);
                }
            } else {
                Log.d("RestartServiceReceiver", "HB fresh → nothing to do");
            }
        }
        // Next ping in 15 Minutes
        AlarmScheduler.scheduleExactPing(c.getApplicationContext(), 15 * 60_000L);
    }
    public String getPreference(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, null); // Returns null if key is not found
    }
}
