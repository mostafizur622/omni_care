//package com.srapp.Util;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.srapp.Util.GPSTracker;
//
//public class DevKillReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context ctx, Intent intent) {
//        String mode = intent.getStringExtra("mode");
//        Log.e("DevKill", "Triggered, mode=" + mode);
//
//        if ("SERVICE_ONLY".equals(mode)) {
//            try {
//                ctx.stopService(new Intent(ctx, GPSTracker.class));
//            } catch (Throwable t) {
//                Log.e("DevKill", "stopService failed", t);
//            }
//            return;
//        }
//
//        // PROCESS_KILL (default): simulate system killing the whole app process
//        try {
//            ctx.stopService(new Intent(ctx, GPSTracker.class));
//        } catch (Throwable ignored) {}
//
//        // kill current process (exactly like low-memory/OS kill result for testing recovery paths)
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(10);
//    }
//}
