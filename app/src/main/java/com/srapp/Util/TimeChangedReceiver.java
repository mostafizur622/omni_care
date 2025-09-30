package com.srapp.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class TimeChangedReceiver extends BroadcastReceiver {
    private AuthPreference authPreference;
    private static final String PREFS_NAME = "TimePrefs";
    private static final String PREF_LAST_SYNC_TIME = "lastSyncTime";

    @Override
    public void onReceive(Context context, Intent intent) {
        // চেক করুন যদি টাইম চেঞ্জ বা টাইম সেট করা হয়েছে
        if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction())
                || Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {

            // লগে টাইম চেঞ্জের ঘটনা দেখা
            Log.d("TimeChangedReceiver", "Device time has been changed!");

            // টাইম পরিবর্তন হয়েছে, তাহলে পুনরায় যাচাই করতে হবে
            checkTimeManipulation(context);
        }
    }

    // টাইম ম্যানিপুলেশন চেক করার ফাংশন
    private void checkTimeManipulation(Context context) {
        authPreference = new AuthPreference(context);
        authPreference.setDeviceTimeChange("1");
        Toast.makeText(context, "Device time has been changed!", Toast.LENGTH_LONG).show();
        Log.d("TimeChangedReceiver", "Checking for time manipulation...");

/*        // বর্তমান টাইম
        long currentTime = System.currentTimeMillis();

        // সেভ করা টাইম পাওয়া (লোকাল ডাটাবেস বা SharedPreferences থেকে)
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastSyncTime = prefs.getLong(PREF_LAST_SYNC_TIME, 0);

        // যদি প্রথমবার টাইম সিঙ্ক্রোনাইজ করা না থাকে, তাহলে সেটি সেভ করুন
        if (lastSyncTime == 0) {
            // প্রথমবার সিঙ্ক্রোনাইজ করা
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(PREF_LAST_SYNC_TIME, currentTime);
            editor.apply();
            return;
        }

        // টাইম ম্যানিপুলেশন চেক: টাইমের পার্থক্য বেশি হলে এটা ম্যানিপুলেশন হতে পারে
        long timeDifference = Math.abs(currentTime - lastSyncTime);
        long threshold = 60 * 60 * 1000; // 1 ঘন্টার মধ্যে পার্থক্য হতে পারে

        if (timeDifference > threshold) {
            // টাইম ম্যানিপুলেশন ধরা পড়েছে
            Log.d("TimeChangedReceiver", "Suspicious time change detected!");

            // ইউজারকে সতর্ক করা বা কোনো পদক্ষেপ গ্রহণ করা
            showAlert(context, "Time Manipulation Detected!", "Please check the system time.");

            // এখানে, আপনি সার্ভার টাইম সিঙ্ক্রোনাইজেশন বা অন্যান্য পদক্ষেপ নিতে পারেন
            synchronizeTimeWithServer(context);
        } else {
            // সঠিক সময় সিঙ্ক্রোনাইজ করা
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(PREF_LAST_SYNC_TIME, currentTime);
            editor.apply();
        }*/
    }

    // ইউজারকে সতর্ক করার ফাংশন
    private void showAlert(Context context, String title, String message) {
        // আপনি এখানে একটি পপআপ ডায়ালগ বা টোস্ট বার্তা দেখাতে পারেন
        Log.d("TimeChangedReceiver", title + ": " + message);

        // উদাহরণ: সিস্টেম পপআপ / টোস্ট বার্তা দেখানো (যেকোনো UI মাধ্যমে)
        // Toast.makeText(context, title + ": " + message, Toast.LENGTH_LONG).show();
    }

    // সার্ভারের সাথে টাইম সিঙ্ক্রোনাইজ করার ফাংশন
    private void synchronizeTimeWithServer(Context context) {
        // সার্ভারের সাথে টাইম সিঙ্ক্রোনাইজ করার জন্য API কল করতে পারেন
        // উদাহরণস্বরূপ, NTP (Network Time Protocol) বা REST API ব্যবহার করা যেতে পারে

        // এখানে আমরা শুধু লগ দিয়ে দেখাচ্ছি যে টাইম সিঙ্ক্রোনাইজ করা হচ্ছে
        Log.d("TimeChangedReceiver", "Synchronizing time with server...");

        // সার্ভার থেকে সঠিক সময় পেয়ে সেই অনুযায়ী অ্যাপের সময় আপডেট করুন
    }
}
