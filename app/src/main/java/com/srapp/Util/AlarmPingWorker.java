package com.srapp.Util;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AlarmPingWorker extends Worker {

    public static final String UNIQUE_NAME = "alarm_ping_worker";

    public AlarmPingWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull @Override
    public Result doWork() {
        // Reuse your existing logic by pinging the BroadcastReceiver
        Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
        getApplicationContext().sendBroadcast(i);
        return Result.success();
    }
}
