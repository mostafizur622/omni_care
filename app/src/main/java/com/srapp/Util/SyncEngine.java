package com.srapp.Util;


import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.location.LocationManager;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import androidx.annotation.WorkerThread;
import androidx.core.app.ActivityCompat;

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

    public static int updateLostTime(Context ctx, ApiInterfaceForJava api)
            throws JSONException, IOException {


        ctx = ctx.getApplicationContext();
        GpsDao dao = new GpsDao(ctx);


        // build payload (তোমার আগের মতোই)
        JSONArray coords = new JSONArray();
        Cursor c = null;
        int pendingLost = 0;
        try {
            c = dao.raw("SELECT * FROM gps_tracking_gape_time WHERE is_pushed='0'");
            if (c != null && c.moveToFirst()) {
                do {
                    JSONObject row = new JSONObject();
                    row.put(Tables.GPS_TRACKING_INSERT_TIME,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKING_INSERT_TIME)));
                    row.put(Tables.GPS_TRACKING_GAPE_TIME,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKING_GAPE_TIME)));
                    row.put(Tables.GPS_TRACKING_GAPE_LAST_TIME,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKING_GAPE_LAST_TIME)));

                    coords.put(row);
                    pendingLost++;
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }

        if (pendingLost == 0) return 0;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String mac = sp.getString("mac", "");
        String salesPersonId = sp.getString("sales_person_id", "");

        JSONObject payload = new JSONObject();
        payload.put("mac", mac);
        payload.put("sales_person_id", salesPersonId);
        payload.put("lost_times", coords);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, payload.toString());

        // ✅ তোমার ইন্টারফেসেই pushLocation আছে
        Response<String> resp = api.lostTime(body).execute();
        if (!resp.isSuccessful() || resp.body() == null) {
            throw new IOException("pushLostTime failed: code=" + resp.code());
        }

        JSONObject root = new JSONObject(resp.body());
        JSONObject r = root.optJSONObject("res");
        String status = r != null ? r.optString("status", "0") : "0";

        if ("1".equalsIgnoreCase(status)) {
            // mark pushed
            dao.exec("UPDATE gps_tracking_gape_time SET is_pushed='1' WHERE is_pushed='0'");
            int affected = (int) android.database.DatabaseUtils.longForQuery(
                    dao.db(), "SELECT changes()", null);
            return affected;
        }
        return 0;
    }

    public static int updateFailedTime(Context ctx, ApiInterfaceForJava api)
            throws JSONException, IOException {


        ctx = ctx.getApplicationContext();
        GpsDao dao = new GpsDao(ctx);


        // build payload (তোমার আগের মতোই)
        JSONArray coords = new JSONArray();
        Cursor c = null;
        int pendingFailed = 0;
        try {
            c = dao.raw("SELECT * FROM gps_tracking_failed_time WHERE is_pushed='0'");
            if (c != null && c.moveToFirst()) {
                do {
                    JSONObject row = new JSONObject();
                    row.put(Tables.GPS_TRACKING_INSERT_FAILED_TIME,
                            c.getString(c.getColumnIndexOrThrow(Tables.GPS_TRACKING_INSERT_FAILED_TIME)));

                    coords.put(row);
                    pendingFailed++;
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }

        if (pendingFailed == 0) return 0;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String mac = sp.getString("mac", "");
        String salesPersonId = sp.getString("sales_person_id", "");

        JSONObject payload = new JSONObject();
        payload.put("mac", mac);
        payload.put("sales_person_id", salesPersonId);
        payload.put("failed_times", coords);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, payload.toString());

        // ✅ তোমার ইন্টারফেসেই pushLocation আছে
        Response<String> resp = api.failedTime(body).execute();
        if (!resp.isSuccessful() || resp.body() == null) {
            throw new IOException("pushFailedTime failed: code=" + resp.code());
        }

        JSONObject root = new JSONObject(resp.body());
        JSONObject r = root.optJSONObject("res");
        String status = r != null ? r.optString("status", "0") : "0";

        if ("1".equalsIgnoreCase(status)) {
            // mark pushed
            dao.exec("UPDATE gps_tracking_failed_time SET is_pushed='1' WHERE is_pushed='0'");
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

    public static int pushLocationDeniedIfPending(Context ctx, ApiInterfaceForJava api)
            throws Exception {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        long ts = sp.getLong("location_denied_get_time", 0L);
        if (ts <= 0) return 0; // কিছু pending নেই

        String reason = sp.getString("location_denied_reason", "unknown");

        // payload বানাও (তোমার server API অনুযায়ী key নাম বসাও)
        JSONObject payload = new JSONObject();
        payload.put("mac", sp.getString("mac",""));
        payload.put("sales_person_id", sp.getString("sales_person_id",""));
        payload.put("location_denied_time_ms", ts);
        payload.put("location_denied_time_str", formatYmdHms(ts)); // human-readable

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                payload.toString()
        );

        Response<String> resp = api.pushLocationDenied(body).execute();
        if (!resp.isSuccessful() || resp.body() == null) {
            throw new IOException("pushLocationDenied failed: code=" + resp.code());
        }

        sp.edit()
                .remove("location_denied_get_time")
                .remove("location_denied_reason")
                .apply();

        return 1;
    }

    public static int pushTrackingSnapshot(Context ctx, ApiInterfaceForJava api)
            throws Exception {

        ctx = ctx.getApplicationContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

        JSONObject payload = new JSONObject();

        // identity
        payload.put("mac", sp.getString("mac",""));
        payload.put("sales_person_id", sp.getString("sales_person_id",""));

        // ✅ nested objects
        payload.put("diagnostics_static", buildTrackingDiagnostics(ctx));
        //payload.put("health_15m", buildHealthSnapshot(ctx));
        Log.e("SyncEngine", "snapshot payload: " + payload.toString());

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                payload.toString()
        );

        Response<String> resp = api.trackingSnapshot(body).execute();
        if (!resp.isSuccessful() || resp.body() == null) {
            throw new IOException("pushTrackingSnapshot failed: code=" + resp.code());
        }

        return 1;
    }

    private static String formatYmdHms(long ms) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(ms));
    }




    public static JSONObject buildTrackingDiagnostics(Context ctx) {
        JSONObject j = new JSONObject();
        try {
            String pkg = ctx.getPackageName();

            // Device
            j.put("brand", Build.BRAND);
            j.put("manufacturer", Build.MANUFACTURER);
            j.put("model", Build.MODEL);
            j.put("sdkInt", Build.VERSION.SDK_INT);
            j.put("release", Build.VERSION.RELEASE);

            // Permissions
            JSONObject perms = new JSONObject();
            perms.put("fine",
                    ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED);

            perms.put("coarse",
                    ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                perms.put("background_location",
                        ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED);

                perms.put("activity_recognition",
                        ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACTIVITY_RECOGNITION)
                                == PackageManager.PERMISSION_GRANTED);
            } else {
                perms.put("background_location", true);
                perms.put("activity_recognition", true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                perms.put("foreground_service",
                        ActivityCompat.checkSelfPermission(ctx, Manifest.permission.FOREGROUND_SERVICE)
                                == PackageManager.PERMISSION_GRANTED);
            } else {
                perms.put("foreground_service", true);
            }

            j.put("permissions", perms);

            // Location enabled
            LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            j.put("location_enabled", lm != null && lm.isLocationEnabled());

            // Battery restriction
            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            boolean powerSave = pm != null && pm.isPowerSaveMode();
            j.put("power_save_mode", powerSave);

            boolean ignoringOpt = false;
            if (pm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ignoringOpt = pm.isIgnoringBatteryOptimizations(pkg);
            }
            j.put("ignoring_battery_optimizations", ignoringOpt);

            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            boolean restricted = false;
            if (am != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                restricted = am.isBackgroundRestricted();
            }
            j.put("background_restricted", restricted);

            // Notifications enabled
            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            boolean notifEnabled = true;
            if (nm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notifEnabled = nm.areNotificationsEnabled();
            }
            j.put("notifications_enabled", notifEnabled);


            // --- Device Admin state ---
            DevicePolicyManager dpm =
                    (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName admin = new ComponentName(ctx, DeviceAdministrator.class);

            JSONObject adminObj = new JSONObject();
            if (dpm != null) {
                adminObj.put("device_admin_active", dpm.isAdminActive(admin));
                //adminObj.put("device_owner", dpm.isDeviceOwnerApp(pkg));
            } else {
                adminObj.put("device_admin_active", false);
                //adminObj.put("device_owner", false);
            }
            j.put("device_admin", adminObj);

            j.put("foreground_notification_alive",
                    hasForegroundNotification(ctx));
            //idle
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && pm != null) {
                j.put("device_mode", pm.isDeviceIdleMode());
            }

            // --- minimal runtime fields you wanted ---
            Long lastSaved = fetchLastSavedTimeStatic(ctx);
            j.put("last_saved_time_ms", lastSaved != null ? lastSaved : 0L);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            long hb = sp.getLong("gps_hb", 0L); // HB_KEY যদি অন্য নাম হয়, বদলাও
            j.put("last_heartbeat_ms", hb);

        } catch (Exception ignore) {

        }
        return j;
    }
    @WorkerThread
    private static Long fetchLastSavedTimeStatic(Context ctx) {
        try {
            GpsDao dao = new GpsDao(ctx.getApplicationContext());
            Cursor c = dao.raw(
                    "SELECT created_at FROM gps_tracker " +
                            "WHERE created_at IS NOT NULL " +
                            "ORDER BY created_at DESC LIMIT 1 "
            );
            Long t = null;
            if (c != null && c.moveToFirst()) t = c.getLong(0);
            if (c != null) c.close();
            return t;
        } catch (Exception e) {
            return null;
        }
    }
    public static boolean hasForegroundNotification(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Android < 6 এ activeNotifications API নাই,
            // ধরে নাও foreground হলে notification থাকার কথা
            return true;
        }

        try {
            NotificationManager nm =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return false;

            StatusBarNotification[] active = nm.getActiveNotifications();
            if (active == null) return false;

            for (StatusBarNotification sbn : active) {
                if (sbn.getId() == 1) {
                    return true;
                }
            }
        } catch (Exception e) {
            // কোন OEM/permission ইস্যু হলে safe fallback false
            Log.e("GPS-DIAG", "hasForegroundNotification err: " + e.getMessage());
        }
        return false;
    }
}

