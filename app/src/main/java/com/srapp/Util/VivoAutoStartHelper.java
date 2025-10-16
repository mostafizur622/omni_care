package com.srapp.Util;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class VivoAutoStartHelper {

    private static final String TAG = "VivoAutoStartHelper";

    /**
     * Detects if the device manufacturer is Vivo, Oppo, Xiaomi, etc.,
     * and tries to open the appropriate "Auto Start" / "Background" settings screen.
     */
    public static void openAutoStartSettings(Context ctx) {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            if (manufacturer.contains("vivo")) {
                // ✅ Vivo specific Auto Start manager
                intent.setComponent(new ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                ));
                ctx.startActivity(intent);
            } else if (manufacturer.contains("oppo")) {
                // ✅ Oppo specific
                intent.setComponent(new ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                ));
                ctx.startActivity(intent);
            } else if (manufacturer.contains("xiaomi")) {
                // ✅ Xiaomi specific
                intent.setComponent(new ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                ));
                ctx.startActivity(intent);
            } else if (manufacturer.contains("realme")) {
                // ✅ Realme same as Oppo
                intent.setComponent(new ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                ));
                ctx.startActivity(intent);
            } else {
                // fallback → Battery Optimization screen
                openBatteryOptimization(ctx);
            }
        } catch (Exception e) {
            Log.e(TAG, "AutoStart screen open failed: " + e.getMessage());
            Toast.makeText(ctx, "Please enable Auto Start manually in settings", Toast.LENGTH_LONG).show();
            openBatteryOptimization(ctx);
        }
    }

    /**
     * Opens generic Battery Optimization ignore screen.
     */
    private static void openBatteryOptimization(Context ctx) {
        try {
            Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            i.setData(Uri.parse("package:" + ctx.getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Exception e) {
            Log.e(TAG, "Battery opt screen open failed: " + e.getMessage());
        }
    }

    /**
     * Checks if device needs manual prompt (for Vivo, Oppo, Xiaomi, etc.)
     * and shows a user-friendly dialog.
     */

}
