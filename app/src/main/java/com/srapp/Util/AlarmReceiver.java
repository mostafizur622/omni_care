package com.srapp.Util;
import static com.srapp.Util.JAPIClient.getPreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.srapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Monitors GPSTracker service and plays alarm if service stopped during tracking hours.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static MediaPlayer player;
    private static Vibrator vibrator;
    private static final long VIBRATION_PATTERN_MS = 2000; // 2s vibration per cycle
/*    private static final Handler handler = new Handler();
    private static final Runnable checkRunnable = new Runnable() {
        @Override public void run() {
            // periodic re-check (every 10 sec)
            handler.postDelayed(this, 10_000);
        }
    };*/

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Triggered: checking service state...");
        boolean running = GPSTracker.IS_RUNNING;

        if (!running && isTrackingTime(context)) {
            startAlarm(context);
        } else {
            stopAlarm();
        }

/*        // keep rechecking every 10s even after one broadcast
        handler.removeCallbacks(checkRunnable);
        handler.postDelayed(checkRunnable, 10_000);*/
        //AlarmPingScheduler.rescheduleNext(context);
    }

    /** ✅ Tracking hour checker (same logic as your CheckTime_date) */
    private boolean isTrackingTime(Context ctx) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        try {
            Date start = df.parse(currentDate + " " + getPreference("start_time"));
            Date end   = df.parse(currentDate + " " + getPreference("end_time"));
            Date now   = df.parse(currentDate + " " +
                    calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                    calendar.get(Calendar.MINUTE) + ":" +
                    calendar.get(Calendar.SECOND));

            long t = now.getTime();
            return (t > start.getTime() && t < end.getTime());
        } catch (ParseException e) {
            Log.e("AlarmReceiver", "parse err: " + e.getMessage());
            return false;
        }
    }

    /** 🔊 Start music alarm */
    private void startAlarm(Context ctx) {
        try {
            if (player == null) {
                player = MediaPlayer.create(ctx.getApplicationContext(), R.raw.voicemaker_speech); // put alert_tone.mp3 under res/raw/
                player.setLooping(true);
                player.setVolume(1.0f, 1.0f);
                player.start();
                Log.w("AlarmReceiver", "🚨 GPS Service stopped during tracking — alarm playing...");
            } else if (!player.isPlaying()) {
                player.start();
            }


            // Start vibration
            if (vibrator == null) {
                vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            }

            if (vibrator != null && !vibrator.hasVibrator()) {
                Log.w("AlarmReceiver", "Device has no vibrator");
            } else if (vibrator != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    // Vibrate indefinitely with pattern until stopped
                    VibrationEffect effect = VibrationEffect.createWaveform(
                            new long[]{0, VIBRATION_PATTERN_MS, 1000}, 0 // 1s gap, repeat forever
                    );
                    vibrator.vibrate(effect);
                } else {
                    long[] pattern = {0, VIBRATION_PATTERN_MS, 1000};
                    vibrator.vibrate(pattern, 0);
                }
                Log.w("AlarmReceiver", "📳 Vibration started...");
            }

        } catch (Exception e) {
            Log.e("AlarmReceiver", "Failed to start alarm: " + e.getMessage());
        }
    }

    /** 🔇 Stop alarm */
    private static void stopAlarm() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
            vibrator.cancel();
            Log.i("AlarmReceiver", "🔇 Alarm stopped (service resumed)");
        }
    }

    public static void stopAlarmPublic() {
        stopAlarm();
    }
}
