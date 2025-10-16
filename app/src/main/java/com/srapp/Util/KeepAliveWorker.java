package com.srapp.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;
public class KeepAliveWorker extends Worker {
    public KeepAliveWorker(@NonNull Context context, @NonNull WorkerParameters params) { super(context, params); }

    @NonNull
    @Override public Result doWork() {
        // If service not running, restart
//        if (!isServiceRunning(GPSTracker.class)) {
//            Intent serviceIntent = new Intent(getApplicationContext(), GPSTracker.class);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
//            } else {
//                getApplicationContext().startService(serviceIntent);
//            }
//        }
//        return Result.success();
        if (GPSTracker.IS_RUNNING) return Result.success();

        Intent i = new Intent(getApplicationContext(), GPSTracker.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(getApplicationContext(), i);
        else
            getApplicationContext().startService(i);

        return Result.success();
    }

    private boolean isServiceRunning(Class<?> svc) {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo r : am.getRunningServices(Integer.MAX_VALUE)) {
            if (svc.getName().equals(r.service.getClassName())) return true;
        }
        return false;
    }

    public static void schedule(Context ctx) {
        // run every 15–30 min
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                KeepAliveWorker.class, 15, TimeUnit.MINUTES
        ).setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES).build();

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "keep_alive_task", ExistingPeriodicWorkPolicy.REPLACE, req
        );
    }
}
