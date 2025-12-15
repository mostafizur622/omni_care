package com.srapp.Util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.srapp.R;
import com.srapp.Util.DeviceAdministrator; // তোমার admin class

import java.util.ArrayList;
import java.util.List;

public class TrackingChecklist {

    private static class CheckRow {
        final String key;
        final String label;
        final boolean ok;

        CheckRow(String key, String label, boolean ok) {
            this.key = key;
            this.label = label;
            this.ok = ok;
        }
    }

    // 🔹 তোমার আগের foreground_notification_alive util এর মত
    public static boolean hasForegroundNotification(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // পুরনো ডিভাইস — ধরলাম দেখাচ্ছে (চেক করার সহজ উপায় নাই)
            return true;
        }
        NotificationManager nm =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return false;

        try {
            Notification[] notes;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.service.notification.StatusBarNotification[] sbn = nm.getActiveNotifications();
                String pkg = ctx.getPackageName();
                for (android.service.notification.StatusBarNotification n : sbn) {
                    if (pkg.equals(n.getPackageName())) {
                        return true;
                    }
                }
            }
        } catch (Throwable t) {
            // কোনো কারণে ফেল করলে safe side এ true করতে পারো
            return true;
        }
        return false;
    }

    /**
     * Activity থেকে কল করবে।
     * যদি সব কিছু ঠিক থাকে -> কিছুই করবে না।
     * যদি কোনো কিছু ভুল থাকে -> ডায়লগ দেখাবে।
     */
    public static void maybeShowChecklist(Activity activity) {
        Context ctx = activity.getApplicationContext();
        String pkg = ctx.getPackageName();

        List<CheckRow> rows = new ArrayList<>();

        // ----- Permissions -----
        boolean fine =
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean coarse =
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean bgLoc = true;
        boolean actRec = true;
        boolean fgService = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bgLoc =
                    ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;

            actRec =
                    ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACTIVITY_RECOGNITION)
                            == PackageManager.PERMISSION_GRANTED;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fgService =
                    ActivityCompat.checkSelfPermission(ctx, Manifest.permission.FOREGROUND_SERVICE)
                            == PackageManager.PERMISSION_GRANTED;
        }

        rows.add(new CheckRow("fine", "Precise location (ACCESS_FINE_LOCATION)", fine));
        rows.add(new CheckRow("coarse", "Approximate location (ACCESS_COARSE_LOCATION)", coarse));
        rows.add(new CheckRow("background_location", "Background location permission", bgLoc));
        rows.add(new CheckRow("activity_recognition", "Activity recognition permission", actRec));
        rows.add(new CheckRow("foreground_service", "Foreground service permission", fgService));

        // ----- Location switch -----
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean locEnabled = lm != null && lm.isLocationEnabled();
        rows.add(new CheckRow("location_enabled", "Device location switch ON", locEnabled));

        // ----- Battery / power -----
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        boolean powerSave = pm != null && pm.isPowerSaveMode();
        boolean ignoringOpt = false;
        if (pm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ignoringOpt = pm.isIgnoringBatteryOptimizations(pkg);
        }

        rows.add(new CheckRow("power_save_mode", "Battery Saver OFF", !powerSave));
        rows.add(new CheckRow("ignoring_battery_optimizations",
                "Ignore battery optimizations", ignoringOpt));

        // ----- Background restricted -----
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        boolean bgRestricted = false;
        if (am != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bgRestricted = am.isBackgroundRestricted();
        }
        rows.add(new CheckRow("background_restricted", "Background restriction disabled", !bgRestricted));

        // ----- Notifications -----
        NotificationManager nm =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        boolean notifEnabled = true;
        if (nm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notifEnabled = nm.areNotificationsEnabled();
        }
        rows.add(new CheckRow("notifications_enabled", "Notifications allowed for this app", notifEnabled));

        // ----- Device admin -----
        DevicePolicyManager dpm =
                (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(ctx, DeviceAdministrator.class);
        boolean isAdminActive = dpm != null && dpm.isAdminActive(admin);
        rows.add(new CheckRow("device_admin_active", "App is device admin (recommended)", isAdminActive));

        // ----- Foreground notification alive -----
        boolean fgNotif = hasForegroundNotification(ctx);
        rows.add(new CheckRow("foreground_notification_alive",
                "Tracking notification visible", fgNotif));

        // ---------- সব OK নাকি? ----------
        boolean anyBad = false;
        for (CheckRow r : rows) {
            if (!r.ok) {
                anyBad = true;
                break;
            }
        }
        if (!anyBad) {
            // সব ঠিক আছে, কিছু দেখানোর দরকার নেই
            return;
        }

        // ---------- ডায়লগ মেসেজ বানানো ----------
        StringBuilder msg = new StringBuilder();
        msg.append("To keep location tracking working properly, please fix the ❌ items:\n\n");

        for (CheckRow r : rows) {
            msg.append(r.ok ? "✅ " : "❌ ");
            msg.append(r.label).append("\n");
        }

        new AlertDialog.Builder(activity)
                .setTitle("Location tracking setup")
                .setMessage(msg.toString())
                .setCancelable(true)
                .setPositiveButton("Open Settings", (d, w) -> {
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.fromParts("package", activity.getPackageName(), null));
                    activity.startActivity(i);
                })
                .setNegativeButton("Later", null)
                .show();
    }
}
