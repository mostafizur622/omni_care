package com.srapp.Util;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.annotation.WorkerThread;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.GpsDao;
import com.srapp.apiService.ApiInterfaceForJava;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
/**
 * Headless sync helper:
 * - কোনো UI/Activity/Lifecycle নেই
 * - Service/Worker থ্রেডে চালিও
 */

public class SyncEngine {

    public static int updateLocation(Context ctx, ApiInterfaceForJava api)
            throws JSONException, IOException {


        ctx = ctx.getApplicationContext();
        GpsDao dao = new GpsDao(ctx);

        // de-dup
        dao.exec(
                "DELETE FROM gps_tracker " +
                        "WHERE _id NOT IN (SELECT MIN(_id) FROM gps_tracker GROUP BY latitude, longitude)"
        );

        // build payload (তোমার আগের মতোই)
        JSONArray coords = new JSONArray();
        Cursor c = null;
        int pending = 0;
        try {
            c = dao.raw("SELECT * FROM gps_tracker WHERE is_pushed='0'");
            if (c != null && c.moveToFirst()) {
                do {
                    JSONObject row = new JSONObject();
                    row.put(Tables.GPS_TRACKER_latitude,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKER_latitude)));
                    row.put(Tables.GPS_TRACKER_longitude,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKER_longitude)));
                    row.put(Tables.GPS_TRACKER_STATUS,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKER_STATUS)));
                    row.put(Tables.GPS_TRACKER_ADDRESS,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKER_ADDRESS)));
                    row.put(Tables.GPS_TRACKER_DISTANCE,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKER_DISTANCE)));

                    // battery optional
                    try {
                        row.put(Tables.BATTERY_LEVEL,
                                c.getString(c.getColumnIndexOrThrow(Tables.BATTERY_LEVEL)));
                    } catch (Exception ignore) {}

                    row.put(Tables.GPS_TRACKER_created_at,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKER_created_at)));

                    coords.put(row);
                    pending++;
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }

        if (pending == 0) return 0;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String mac = sp.getString("mac", "");
        String salesPersonId = sp.getString("sales_person_id", "");

        JSONObject payload = new JSONObject();
        payload.put("mac", mac);
        payload.put("sales_person_id", salesPersonId);
        payload.put("coordinates", coords);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, payload.toString());

        // ✅ তোমার ইন্টারফেসেই pushLocation আছে
        Response<String> resp = api.pushLocation(body).execute();
        if (!resp.isSuccessful() || resp.body() == null) {
            throw new IOException("pushLocation failed: code=" + resp.code());
        }

        JSONObject root = new JSONObject(resp.body());
        JSONObject r = root.optJSONObject("response");
        String status = r != null ? r.optString("status", "0") : "0";

        if ("1".equalsIgnoreCase(status)) {
            // mark pushed
            dao.exec("UPDATE gps_tracker SET is_pushed='1' WHERE is_pushed='0'");
            int affected = (int) android.database.DatabaseUtils.longForQuery(
                    dao.db(), "SELECT changes()", null);
            return affected;
        }
        return 0;
    }

    public static int pushServiceStopIfPending(Context ctx, ApiInterfaceForJava api)
            throws Exception {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        long ts = sp.getLong("service_last_stop_time", 0L);
        if (ts <= 0) return 0; // কিছু pending নেই

        String reason = sp.getString("service_last_stop_reason", "unknown");

        // payload বানাও (তোমার server API অনুযায়ী key নাম বসাও)
        JSONObject payload = new JSONObject();
        payload.put("mac", sp.getString("mac",""));
        payload.put("sales_person_id", sp.getString("sales_person_id",""));
        payload.put("service_stop_time_ms", ts);
        payload.put("service_stop_time_str", formatYmdHms(ts)); // human-readable

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                payload.toString()
        );

        // তোমার API-তে মিলিয়ে নাও — যেমন pushServiceStop(...)
        Response<String> resp = api.pushServiceStop(body).execute();
        if (!resp.isSuccessful() || resp.body() == null) {
            throw new IOException("pushServiceStop failed: code=" + resp.code());
        }

        // সফল হলে prefs ক্লিয়ার
        sp.edit()
                .remove("service_last_stop_time")
                .remove("service_last_stop_reason")
                .apply();

        return 1;
    }

    private static String formatYmdHms(long ms) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(ms));
    }
}

