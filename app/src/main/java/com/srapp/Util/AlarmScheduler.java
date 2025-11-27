package com.srapp.Util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

public final class AlarmScheduler {
    private static final String TAG = "AlarmScheduler";
    private static final int REQ_ID = 991;

    private AlarmScheduler() {}

    /** 15 মিনিটের মতো delay দিয়ে exact+idle-সেফ alarm শিডিউল করুন */
    public static void scheduleExactPing(Context c, long delayMs) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            Log.e(TAG, "AlarmManager null");
            return;
        }

        long triggerAt = SystemClock.elapsedRealtime() + Math.max(60_000L, delayMs);
        PendingIntent pi = buildPingPI(c);

        // S+ এ exact alarm পারমিশন না থাকলে সিস্টেম ডেফার করতে পারে
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) {
                Log.w(TAG, "Missing SCHEDULE_EXACT_ALARM; scheduling best-effort (may defer)");
                // best-effort: inexact set (still posts a wakeup)
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi);
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi);
        } else {
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi);
        }
        Log.d(TAG, "Exact ping scheduled in " + (delayMs/1000) + "s");
    }

    private static PendingIntent buildPingPI(Context c) {
        Intent i = new Intent(c, RestartServiceReceiver.class);
        return PendingIntent.getBroadcast(
                c,
                REQ_ID,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    @RequiresApi(Build.VERSION_CODES.S)
    public static void requestExactAlarmPermission(Activity activity) {
        AlarmManager am = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        if (am != null && !am.canScheduleExactAlarms()) {
            try {
                Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        .setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(i);
            } catch (Exception e) {
                Log.e(TAG, "Exact alarm settings screen not available", e);
                // TODO: fallback UI: নির্দেশনা দেখান কীভাবে হাতে গিয়ে টগল অন করতে হয়
            }
        }
    }
}
