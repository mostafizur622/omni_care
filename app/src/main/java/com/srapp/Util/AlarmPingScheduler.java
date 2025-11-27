//package com.srapp.Util;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//public final class AlarmPingScheduler {
//    private static final int REQ_CODE = 4242;
//
//    public static void schedule(Context ctx) {
//        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(ctx, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(
//                ctx, REQ_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        long now = System.currentTimeMillis();
//        long interval = 60_000L; // 1 minute
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, now + interval, pi);
//        }
//    }
//
//    public static void rescheduleNext(Context ctx) {
//        schedule(ctx);
//    }
//
//    public static void cancel(Context ctx) {
//        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(ctx, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(
//                ctx, REQ_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        am.cancel(pi);
//    }
//}
