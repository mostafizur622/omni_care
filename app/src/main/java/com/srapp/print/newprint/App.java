package com.srapp.print.newprint;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import com.srapp.Util.AlarmPingWorker;
import com.srapp.Util.GPSTracker;
import com.srapp.Util.KeepAliveWorker;
import com.srapp.Util.LocationSyncWorker;
import com.srapp.Util.UpdateLocationWorker;

import org.json.JSONException;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator
 *
 * @author 猿史森林
 *         Date: 2017/11/28
 *         Class description:
 */
public class App extends Application {

    private static Context mContext;
    private ConnectivityManager.NetworkCallback netCallback;
    private BroadcastReceiver legacyReceiver; // keep reference to unregister
    @Override
    public void onCreate() {
        super.onCreate();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // API 24+
            netCallback = new ConnectivityManager.NetworkCallback() {
                @Override public void onAvailable(Network network) {
                    Log.d("Net", "Network available");
                    kickSyncWork(getApplicationContext());
                }
                @Override public void onLost(Network network) {
                    Log.d("Net", "Network lost");
                }
            };
            cm.registerDefaultNetworkCallback(netCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 21–23
            NetworkRequest req = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            netCallback = new ConnectivityManager.NetworkCallback() {
                @Override public void onAvailable(Network network) {
                    Log.d("Net", "Network available (21–23)");
                    kickSyncWork(getApplicationContext());
                }
                @Override public void onLost(Network network) {
                    Log.d("Net", "Network lost (21–23)");
                }
            };
            cm.registerNetworkCallback(req, netCallback);
        } else {
            // API <21 হলে পুরনো BroadcastReceiver dynamically register করো (runtime), manifest নয়
            IntentFilter f = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            legacyReceiver = new BroadcastReceiver() {
                @Override public void onReceive(Context context, Intent intent) {
                    Log.d("LegacyReceiver", "Connectivity broadcast (pre-21)");
                    //SyncKick.enqueue(getApplicationContext());
                }
            };
            registerReceiver(legacyReceiver, f);
        }
        mContext = getApplicationContext();
        //KeepAliveWorker.schedule(getApplicationContext());
        schedulePeriodicSync(getApplicationContext());
        schedulePendingLocationDataSync(getApplicationContext());
        //AlarmPingScheduler.schedule(getApplicationContext());
        scheduleAlarmPingWithWorkManager(getApplicationContext());
    }
/*    UUID runNow(Context ctx) {
        OneTimeWorkRequest req =
                new OneTimeWorkRequest.Builder(LocationSyncWorker.class)
                        .build(); // no constraints => runs ASAP
        WorkManager.getInstance(ctx).enqueue(req);
        return req.getId();
    }*/
    public static Context getContext() {
        return mContext;
    }
    private void kickSyncWork(Context ctx) {
        OneTimeWorkRequest w = new OneTimeWorkRequest.Builder(UpdateLocationWorker.class).build();
        WorkManager.getInstance(ctx).enqueueUniqueWork(
                "update_location_once",
                ExistingWorkPolicy.REPLACE,
                w
        );
    }
    private void schedulePeriodicSync(Context ctx) {
        PeriodicWorkRequest periodicSync =
                new PeriodicWorkRequest.Builder(UpdateLocationWorker.class, 15, TimeUnit.MINUTES)
                        .addTag("update_location_periodic")
                        .build();

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "update_location_periodic",           // ইউনিক ওয়ার্ক নাম
                ExistingPeriodicWorkPolicy.KEEP,      // আগেরটা থাকলে সেটাই রাখবে
                periodicSync
        );

        Log.d("SyncScheduler", "✅ Periodic sync scheduled every 15 minutes");
    }

    private void schedulePendingLocationDataSync(Context ctx) {
        PeriodicWorkRequest periodicSync =
                new PeriodicWorkRequest.Builder(LocationSyncWorker.class, 15, TimeUnit.MINUTES)
                        .addTag("location_sync_check")
                        .build();

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "location_sync_check",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicSync
        );

        Log.d("PendingLoScheduler", "✅ Periodic sync for pending data scheduled every 15 minutes");
    }

    private void scheduleAlarmPingWithWorkManager(Context ctx) {
        PeriodicWorkRequest req =
                new PeriodicWorkRequest.Builder(AlarmPingWorker.class, 15, TimeUnit.MINUTES)
                        .addTag(AlarmPingWorker.UNIQUE_NAME)

                        .build();

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                AlarmPingWorker.UNIQUE_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                req
        );
        Log.d("PendingServiceStop", "✅ Periodic for service stop  every 15 minutes");
    }
}
