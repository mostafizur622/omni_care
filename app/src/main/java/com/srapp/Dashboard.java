package com.srapp;

import static com.srapp.Util.VivoAutoStartHelper.openAutoStartSettings;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import com.srapp.Util.AlarmReceiver;
import com.srapp.Util.DeviceAdministrator;
import com.srapp.Util.GPSTracker;
import com.srapp.Util.Parent;
import com.srapp.Util.VivoAutoStartHelper;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONObject;

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


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("StartMyService onResume","true");
        cash_number.setText(ds.getTotalCashOfCurrentDay());
        oc_value.setText(ds.getTotalOCofCurrentDay());
        oc.setText(ds.getOc());
        if (GPSTracker.IS_RUNNING) {
            AlarmReceiver.stopAlarmPublic();
            Log.i("Dashboard", "Stopped alarm since service is running");
        }

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
            Log.e("text",e.getLocalizedMessage());
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

                Log.e("text",et.getLocalizedMessage());
            }

        }
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
                "  AND created_at < (strftime('%s','now','-3 days') * 1000);");
        Cursor c = ds.sqLiteDatabase.rawQuery("select * from gps_tracker where is_pushed='0'",null);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
        }
    }
    public void deleteBeforeLostTimeData(){
        ds.sqLiteDatabase.execSQL("DELETE FROM gps_tracking_gape_time\n" +
                "WHERE is_pushed = '1'\n" +
                "  AND created_at < (strftime('%s','now','-3 days') * 1000);");
        Cursor c = ds.sqLiteDatabase.rawQuery("select * from gps_tracking_gape_time where is_pushed='0'",null);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
        }
    }
    public void deleteBeforeFailedTimeData(){
        ds.sqLiteDatabase.execSQL("DELETE FROM gps_tracking_failed_time\n" +
                "WHERE is_pushed = '1'\n" +
                "  AND created_at < (strftime('%s','now','-3 days') * 1000);");
        Cursor c = ds.sqLiteDatabase.rawQuery("select * from gps_tracking_failed_time where is_pushed='0'",null);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

        }
    }
}
