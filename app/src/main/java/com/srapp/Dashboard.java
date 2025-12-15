package com.srapp;

import static com.srapp.Util.VivoAutoStartHelper.openAutoStartSettings;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.GpsDao;
import com.srapp.Util.AlarmReceiver;
import com.srapp.Util.AlarmScheduler;
import com.srapp.Util.DeviceAdministrator;
import com.srapp.Util.GPSTracker;
import com.srapp.Util.Parent;
import com.srapp.Util.TrackingChecklist;
import com.srapp.Util.VivoAutoStartHelper;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONObject;
import android.Manifest;
import java.util.ArrayDeque;

public class Dashboard extends Parent implements BasicFunctionListener {

    Button  toolsBtn ;
    LinearLayout stockBtn,deliveryBtn,syncBtn,accountBtn;
    ImageView backBtn, homeBtn;
    LinearLayout order_or_delivery;
    TextView cash_number, oc_value, userIdTV, titleTV,oc,srName;
    Data_Source ds;
    BasicFunction basicFunction;
    String roll="";
    TableRow stockAndDeliveryOption;
    LinearLayout ecOc;
    CardView listCard;
    private static final int REQ_AR = 7001;
    private static final String PREF_ASKED_EXACT_ALARM = "asked_exact_alarm";
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
            builder.setCancelable(true);
            builder.setTitle("App close");
            builder.setMessage("Do You Want to Exit?");
            builder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        basicFunction = new BasicFunction(this, this);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        FirebaseCrashlytics.getInstance().setUserId(basicFunction.getPreference("sr_uname"));
        roll=basicFunction.getPreference("roll");
        srName=findViewById(R.id.userName);
        srName.setText(basicFunction.getPreference("sr_name"));
        Log.e("UserRoll",roll);
        userIdTV.setText(value);
        ds = new Data_Source(this);
        //reportBtn = findViewById(R.id.reportBtn);
        cash_number = findViewById(R.id.cash_number);
        oc_value = findViewById(R.id.oc_value);
        oc = findViewById(R.id.oc);
        stockBtn = findViewById(R.id.stockBtn);
        accountBtn = findViewById(R.id.accountBtn);
        syncBtn = findViewById(R.id.sync_btn);
        toolsBtn = findViewById(R.id.toolBtn);
        deliveryBtn = findViewById(R.id.deliveryBtn);
        backBtn = findViewById(R.id.back);
        homeBtn = findViewById(R.id.home);
        stockAndDeliveryOption=findViewById(R.id.stockAndDeliveryOption);
        ecOc=findViewById(R.id.ecOc);
        listCard=findViewById(R.id.cardView);
        if (roll.equalsIgnoreCase("1")){
            stockAndDeliveryOption.setVisibility(View.GONE);
        }
        //openOemAutoStart(getApplicationContext());
        ComponentName componentName = new ComponentName(this, DeviceAdministrator.class);
        Intent intent = new Intent(android.app.admin.DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(android.app.admin.DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(android.app.admin.DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Please enable device admin to allow background location tracking even in idle mode.");
        startActivityForResult(intent, 1001);

        maybePromptAutoStart(this);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            if (checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION)
//                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION}, REQ_AR);
//            }
//        }
        order_or_delivery = findViewById(R.id.logo);
        if (roll.equalsIgnoreCase("2")){
            order_or_delivery.setVisibility(View.GONE);
            ecOc.setVisibility(View.GONE);
            listCard.setBackgroundColor(Color.parseColor("#EDF2F6"));
        }
/*        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Reports_Activity.class);
                startActivity(intent);
                finish();
            }
        });*/
      try {
          deleteBeforeLocationData();
          deleteBeforeLostTimeData();
          deleteBeforeFailedTimeData();
      }catch (NullPointerException e){
          Log.d("text",e.getLocalizedMessage());
      }

        deliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Visit_Plan_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        stockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, SR_Attendance.class);
                startActivity(intent);
                finish();
            }
        });

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, LeaveList.class);
                startActivity(intent);
                finish();
            }
        });


        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, SyncActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //more button
        toolsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Tools.class);
                startActivity(intent);
                finish();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                builder.setCancelable(true);
                builder.setTitle("Logout");
                builder.setMessage("Do You Want to Logout?");
                builder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                                startActivity(intent);
                                finishAffinity();
                                finish();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        //outlets button
        order_or_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, Create_New_Memo.class);
                i.putExtra("flag1", 1);
                startActivity(i);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        cash_number.setText(ds.getTotalCashOfCurrentDay());
        oc_value.setText(ds.getTotalOCofCurrentDay());
        oc.setText(ds.getOc());
        Log.e("text","Location Service");

        //DevKillUtils.scheduleDevKill(this, 60_000, DevKillUtils.KillMode.PROCESS_KILL);
    }
    private boolean hasExactAlarmPrivilege() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true;
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        return am != null && am.canScheduleExactAlarms();
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private void promptExactAlarmIfNeeded() {
        if (hasExactAlarmPrivilege()) return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean alreadyAsked = sp.getBoolean(PREF_ASKED_EXACT_ALARM, false);
        if (alreadyAsked) {
            // আগে জিজ্ঞেস করা হয়েছে—সরাসরি settings ওপেন করতে পারেন বা শুধু নোটিশ দিন
            AlarmScheduler.requestExactAlarmPermission(this);
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Allow exact alarms")
                .setMessage("To keep background location reliable during idle/Doze, please allow exact alarms for this app.")
                .setPositiveButton("Open Settings", (d, w) -> {
                    sp.edit().putBoolean(PREF_ASKED_EXACT_ALARM, true).apply();
                    AlarmScheduler.requestExactAlarmPermission(this);
                })
                .setNegativeButton("Not now", null)
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("StartMyService onResume","true");
        cash_number.setText(ds.getTotalCashOfCurrentDay());
        oc_value.setText(ds.getTotalOCofCurrentDay());
        oc.setText(ds.getOc());
        //TrackingChecklist.maybeShowChecklist(this);

        try{
            //startService(new Intent(Dashboard.this, GPSTracker.class));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(
                        this,
                        new Intent(this, GPSTracker.class)
                );
            } else {
                startService(new Intent(this, GPSTracker.class));
            }
        }catch (Exception e){
            Log.e("textE",e.getLocalizedMessage());
            try{
                //startService(new Intent(Dashboard.this, GPSTracker.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(
                            this,
                            new Intent(this, GPSTracker.class)
                    );
                } else {
                    startService(new Intent(this, GPSTracker.class));
                }
            }catch (Exception et){

                Log.e("textE",et.getLocalizedMessage());
            }

        }
        new Handler(getMainLooper()).postDelayed(() -> {
            boolean healthy = GPSTracker.IS_RUNNING || isGpsHealthy();
            if (healthy) {
                AlarmReceiver.stopAlarmPublic();
            } else {
                AlarmScheduler.scheduleExactPing(getApplicationContext(), 15 * 60_000L);
            }
        }, 600);
        //2) S+ exact alarm privilege
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasExactAlarmPrivilege()) {
                promptExactAlarmIfNeeded();
            }
        }
    }
    private boolean isGpsHealthy() {
        long last = PreferenceManager.getDefaultSharedPreferences(this).getLong("gps_hb", 0L);
        return System.currentTimeMillis() - last <= 60_000L;
    }
    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {

    }

    @Override
    public void OnConnetivityError() {

    }
    public static void openOemAutoStart(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("tracking_prefs", Context.MODE_PRIVATE);
        boolean alreadyOpened = prefs.getBoolean("autostart_allowed", false);

        if (alreadyOpened) return; // skip if already granted or shown once

        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        try {
            if (manufacturer.contains("xiaomi")) {
                i.setComponent(new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity"));
            } else if (manufacturer.contains("oppo")) {
                i.setComponent(new ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if (manufacturer.contains("vivo")) {
                i.setComponent(new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"));
            } else {
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.setData(Uri.parse("package:" + ctx.getPackageName()));
            }

            ctx.startActivity(i);

            // save flag so next time it won’t open again
            prefs.edit().putBoolean("autostart_allowed", true).apply();

        } catch (Exception e) {
            // fallback
            Intent fallback = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            fallback.setData(Uri.parse("package:" + ctx.getPackageName()));
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(fallback);
        }
    }
    public static void maybePromptAutoStart(Context ctx) {
        // 1) Battery ignore already? তাহলে দরকার নেই
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            if (pm != null && pm.isIgnoringBatteryOptimizations(ctx.getPackageName())) return;
        }
        // 2) একবারই দেখাও
        SharedPreferences p = ctx.getSharedPreferences("tracking_prefs", Context.MODE_PRIVATE);
        if (p.getBoolean("autostart_prompted", false)) return;

        // 3) শুধু Vivo/Oppo/Realme/Xiaomi হলে দেখাও
        String m = Build.MANUFACTURER.toLowerCase();
        if (m.contains("vivo") || m.contains("oppo") || m.contains("realme") || m.contains("xiaomi")) {
            p.edit().putBoolean("autostart_prompted", true).apply();
            showAutoStartDialogIfNeeded(ctx); // বা openOemAutoStart(ctx)
        }
    }
    public static void showAutoStartDialogIfNeeded(Context ctx) {
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        if (manufacturer.contains("vivo") ||
                manufacturer.contains("oppo") ||
                manufacturer.contains("realme") ||
                manufacturer.contains("xiaomi")) {

            new AlertDialog.Builder(ctx)
                    .setTitle("Allow Background Tracking")
                    .setMessage(
                            "To ensure location tracking works properly in the background, " +
                                    "please enable:\n\n" +
                                    "✅ Auto Start\n" +
                                    "✅ Battery Optimization Ignore\n" +
                                    "✅ Lock App in Recents\n\n" +
                                    "Tap 'Open Settings' to go directly to your phone's settings."
                    )
                    .setPositiveButton("Open Settings", (d, w) -> openAutoStartSettings(ctx))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
    public void deleteBeforeLocationData(){
        ds.sqLiteDatabase.execSQL("DELETE FROM gps_tracker\n" +
                "WHERE is_pushed = '1'\n" +
                "  AND created_at < (strftime('%s','now','-2 days') * 1000);");
    }
    public void deleteBeforeLostTimeData(){
        ds.sqLiteDatabase.execSQL("DELETE FROM gps_tracking_gape_time\n" +
                "WHERE is_pushed = '1'\n" +
                "  AND created_at < (strftime('%s','now','-2 days') * 1000);");
    }
    public void deleteBeforeFailedTimeData(){
        ds.sqLiteDatabase.execSQL("DELETE FROM gps_tracking_failed_time\n" +
                "WHERE is_pushed = '1'\n" +
                "  AND created_at < (strftime('%s','now','-2 days') * 1000);");
    }
/*    public static JSONObject buildTrackingDiagnostics(Context ctx) {
        JSONObject j = new JSONObject();

        try {
            PackageManager pm = ctx.getPackageManager();
            String pkg = ctx.getPackageName();

            // --- Device info ---
            j.put("brand", Build.BRAND);
            j.put("manufacturer", Build.MANUFACTURER);
            j.put("model", Build.MODEL);
            j.put("sdkInt", Build.VERSION.SDK_INT);
            j.put("release", Build.VERSION.RELEASE);

            // --- Permissions ---
            JSONObject perms = new JSONObject();
            perms.put("fine", hasPerm(ctx, Manifest.permission.ACCESS_FINE_LOCATION));
            perms.put("coarse", hasPerm(ctx, Manifest.permission.ACCESS_COARSE_LOCATION));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                perms.put("background_location", hasPerm(ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION));
                perms.put("activity_recognition", hasPerm(ctx, Manifest.permission.ACTIVITY_RECOGNITION));
            } else {
                perms.put("background_location", true); // pre-Q implicit
                perms.put("activity_recognition", true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                perms.put("foreground_service", hasPerm(ctx, Manifest.permission.FOREGROUND_SERVICE));
            }

            j.put("permissions", perms);

            // --- Location enabled ---
            LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            boolean locEnabled = lm != null && lm.isLocationEnabled();
            j.put("location_enabled", locEnabled);

            // --- Battery optimization / restriction ---
            PowerManager power = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            boolean ignoringOpt = false;
            boolean powerSave = false;
            if (power != null) {
                powerSave = power.isPowerSaveMode();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ignoringOpt = power.isIgnoringBatteryOptimizations(pkg);
                }
            }
            j.put("power_save_mode", powerSave);
            j.put("ignoring_battery_optimizations", ignoringOpt);

            // ✅ Foreground notification alive?
            j.put("foreground_notification_alive",
                    hasForegroundNotification(ctx));


            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                j.put("background_restricted", am.isBackgroundRestricted());
            } else {
                j.put("background_restricted", false);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                UsageStatsManager usm = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
                if (usm != null) {
                    int bucket = usm.getAppStandbyBucket();
                    j.put("standby_bucket", bucket); // 10=active, 20=working_set, 30=frequent, 40=rare, 50=never
                }
            }

            // --- Device Admin state ---
            DevicePolicyManager dpm = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName admin = new ComponentName(ctx, DeviceAdministrator.class);

            JSONObject adminObj = new JSONObject();
            if (dpm != null) {
                adminObj.put("device_admin_active", dpm.isAdminActive(admin));
                adminObj.put("device_owner", dpm.isDeviceOwnerApp(pkg));
            } else {
                adminObj.put("device_admin_active", false);
                adminObj.put("device_owner", false);
            }
            j.put("device_admin", adminObj);

            // --- Notification enabled (Foreground service visibility) ---
            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            boolean notiEnabled = true;
            if (nm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notiEnabled = nm.areNotificationsEnabled();
            }
            j.put("notifications_enabled", notiEnabled);

        } catch (Exception e) {
            // swallow
        }

        return j;
    }

    public static JSONObject buildHealthSnapshot(Context ctx) {
        JSONObject j = new JSONObject();
        long now = System.currentTimeMillis();

        try {
            // ---- last saved time from DB ----
            Long lastSaved = fetchLastSavedTimeStatic(ctx); // helper below
            j.put("last_saved_time_ms", lastSaved != null ? lastSaved : 0L);
            j.put("minutes_since_last_save",
                    lastSaved != null ? (now - lastSaved) / 60000.0 : -1);

            // ---- heartbeat ----
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            long hb = sp.getLong("gps_hb", 0L);
            j.put("last_heartbeat_ms", hb);
            j.put("minutes_since_heartbeat",
                    hb > 0 ? (now - hb) / 60000.0 : -1);

            // ---- service stop context ----
            long stopMs = sp.getLong("service_last_stop_time", 0L);
            String stopReason = sp.getString("service_last_stop_reason", null);

            JSONObject service = new JSONObject();
            service.put("last_stop_time_ms", stopMs);
            service.put("last_stop_reason", stopReason);
            service.put("minutes_since_stop",
                    stopMs > 0 ? (now - stopMs) / 60000.0 : -1);

            // IS_RUNNING flag prefs-এ না থাকলে omit করা যায়
            service.put("is_running_pref", sp.getBoolean("gps_is_running", false));

            j.put("service", service);

            // ---- AR health ----
            String arStatus = sp.getString("last_activity_status", null);
            long arTs = sp.getLong("last_activity_ts", 0L);

            JSONObject ar = new JSONObject();
            ar.put("last_status", arStatus);
            ar.put("last_ts_ms", arTs);
            ar.put("minutes_since_ar",
                    arTs > 0 ? (now - arTs) / 60000.0 : -1);

            j.put("activity_recognition", ar);

        } catch (Exception ignore) {}

        return j;
    }
    private static Long fetchLastSavedTimeStatic(Context ctx) {
        try {
            GpsDao dao = new GpsDao(ctx.getApplicationContext());
            Cursor c = dao.raw(
                    "SELECT created_at FROM gps_tracker " +
                            "WHERE created_at IS NOT NULL " +
                            "ORDER BY created_at DESC LIMIT 1"
            );
            Long t = null;
            if (c != null && c.moveToFirst()) {
                t = c.getLong(0);
            }
            if (c != null) c.close();
            return t;
        } catch (Exception e) {
            Log.e("fetchLastSaved","err="+e.getMessage());
            return null;
        }
    }
    public static boolean hasPerm(Context c, String p) {
        return ActivityCompat.checkSelfPermission(c, p) == PackageManager.PERMISSION_GRANTED;
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
                if (sbn.getId() == 1) {   // ✅ তোমার startForeground(1, notification)
                    return true;
                }
            }
        } catch (Exception e) {
            // কোন OEM/permission ইস্যু হলে safe fallback false
            Log.e("GPS-DIAG", "hasForegroundNotification err: " + e.getMessage());
        }
        return false;
    }*/
}
