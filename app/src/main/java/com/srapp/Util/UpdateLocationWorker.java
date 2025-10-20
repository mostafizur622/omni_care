package com.srapp.Util;

import static com.srapp.Db_Actions.URL.getJAPi;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.srapp.apiService.ApiClient;
import com.srapp.apiService.ApiInterfaceForJava;

import java.io.IOException;


// com.srapp.Util.UpdateLocationWorker
public class UpdateLocationWorker extends Worker {
    public UpdateLocationWorker(@NonNull Context ctx, @NonNull WorkerParameters p) { super(ctx, p); }

    @NonNull @Override public Result doWork() {
        try {
            ApiInterfaceForJava api = getJAPi();
            int pushed = SyncEngine.updateLocation(getApplicationContext(), api);
            Log.i("UpdateLocationWorker", "pushed rows = " + pushed);
            int pushedStop = SyncEngine.pushServiceStopIfPending(getApplicationContext(), api);
            Log.i("UpdateLocationWorker", "pushedLoc=" + pushed + ", pushedStop=" + pushedStop);
            int pushedLostTime = SyncEngine.updateLostTime(getApplicationContext(), api);
            return Result.success();
        } catch (IOException io) {
            Log.e("UpdateLocationWorker", "io: " + io.getMessage());
            return Result.retry();
        } catch (Exception e) {
            Log.e("UpdateLocationWorker", "fail: " + e.getMessage());
            return Result.failure();
        }
    }
}
