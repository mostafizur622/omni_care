//package com.srapp.Util;
//
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.SystemClock;
//
//public class DevKillUtils {
//
//    public enum KillMode { PROCESS_KILL, SERVICE_ONLY }
//
//    public static void scheduleDevKill(Context ctx, long delayMs, KillMode mode) {
//        Intent i = new Intent(ctx, DevKillReceiver.class)
//                .putExtra("mode", mode == KillMode.SERVICE_ONLY ? "SERVICE_ONLY" : "PROCESS_KILL");
//
//        PendingIntent pi = PendingIntent.getBroadcast(
//                ctx,
//                3210,
//                i,
//                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
//        long triggerAt = SystemClock.elapsedRealtime() + Math.max(1000, delayMs);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi);
//        } else {
//            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi);
//        }
//    }
//}
