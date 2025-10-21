package com.srapp.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Model.GpsDao;

public class LocationSyncWorker extends Worker {

    public LocationSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        GpsDao dao = new GpsDao(getApplicationContext());


        Cursor c = dao.raw("SELECT * FROM gps_tracker WHERE is_pushed='0'");
        if (c != null && c.getCount() > 0) {

            // ✅ Safe way: explicitly post on main looper
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                Toast.makeText(
                        getApplicationContext(),
                        "Your location is pending. Please go online and sync data.",
                        Toast.LENGTH_LONG
                ).show();
            });

            Log.w("LocationSyncWorker", "Pending location data found.");
        }

        if (c != null) c.close();
        return Result.success();
    }
}
