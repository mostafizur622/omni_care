package com.srapp.Util;

import static com.srapp.Util.VivoAutoStartHelper.openAutoStartSettings;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.srapp.ActivityRecognition.ActivityRecognitionManager;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GPSTracker extends Service implements LocationListener {

    private  Context mContext = getBaseContext();

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 sec

    // Declaring a Location Manager
    protected LocationManager locationManager;

    TimerTask timerTask;
    Timer timer =null;
    private FusedLocationProviderClient fused;
    private ActivityRecognitionManager arManager;
    public static volatile boolean IS_RUNNING = false;
    private static final String PREF_STOP_TIME  = "service_last_stop_time";
    private static final String PREF_STOP_REASON= "service_last_stop_reason";

    private static final String PREF_LOCATION_DENIED  = "location_denied_get_time";
    private static final String PREF_LOCATION_DENIED_REASON= "location_denied_reason";
    // ---- Accuracy / freshness / plausibility thresholds ----
    private static final long   MAX_FIX_AGE_MS      = 15_000L; // fresh <= 15s
    private static final float  MAX_ACCURACY_M      = 50f;    // accept if <= 50m (indoor হলে 75-100m করতে পারো)
    private static final float  MAX_PLAUSIBLE_SPEED = 55f;    // m/s (~198 km/h)

    private static final double SPEED_DT_CAP_S        = 30.0;    // স্পিড ক্যালকে dt upper cap
    private static final long   LONG_GAP_S            = 2 * 60;  // 2 মিনিটের বেশি = long gap
    private static final float  TELEPORT_METERS       = 5000f;   // 5 কিমি লাফ
    private static final long   TELEPORT_WINDOW_S     = 120;     // 2 মিনিটের মধ্যে হলে reject
    private PowerManager.WakeLock wakeLock;
    private Handler handler;
    private Runnable locationRunnable;
    private long interval = 60000; // default 1 minute
    private boolean loopActive = false;
    private Handler worker;
    private HandlerThread workerThread;
    private static final String HB_KEY = "gps_hb";
    private LocationCallback contCallback;
    private boolean contUpdatesStarted = false;
    private volatile Location lastWarmLocation = null;
    private final Object reqLock = new Object();
    private boolean reqInFlight = false;
    private final Object saveLock = new Object();
    private volatile long lastSaveWallMs = 0L; // in-memory throttle
    // fallback attempt time (priority guard)
    private static final long CONT_BACKUP_GAP_MS = 60_000L; // 1 minute
    private volatile long lastContSaveMs = 0L; // cont side memory gate (optional)
    private Double lastLat = null;
    private Double lastLon = null;
    private Long lastTs = null;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        fused = LocationServices.getFusedLocationProviderClient(this);
      //  Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
        arManager = new ActivityRecognitionManager(this);
        //arManager.start(Long.parseLong(getPreference("interval")));
        arManager.start(15_000L);
        // 🟢 Step 1: Foreground notification start (Vivo ইস্যু fix)
        IS_RUNNING = true;
        startForegroundServiceSafe();
        //VivoAutoStartHelper.showAutoStartDialogIfNeeded(this);
        // 🟢 Step 2: Battery optimization ignore request
        requestIgnoreBatteryOptimization();

        scheduleKeepAliveWorker();
        //acquireWakeLock();
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        try {
            interval = Long.parseLong(getPreference("interval"));
        } catch (Exception e) {
            Log.e("GPSTracker", "Invalid interval in preference, using default 60000ms");
        }
/*        locationRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("text", "Location Service (Handler) " + CheckTime_date());
                if (CheckTime_date()) {
                    fetchCurrentLocationOnce();
                }


                // Re-post the runnable after the interval
                handler.postDelayed(this, interval);
            }
        };
        handler.postDelayed(locationRunnable, 5000);*/
        workerThread = new HandlerThread("gps-worker");
        workerThread.start();
        worker = new Handler(workerThread.getLooper());
        checkAndToggleContinuousByGap();
        ensureLoopRunning();
        AlarmScheduler.scheduleExactPing(getApplicationContext(), 15 * 60_000L);
        Log.w("onCreateService","GPSTracker Service is running...");

//        if (timerTask==null){
//            Log.e("text","Location Service2"+getPreference("interval"));
//
//            timerTask = new TimerTask() {
//                @RequiresApi(api = Build.VERSION_CODES.M)
//                @Override
//                public void run() {
//
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("text","Location Service2"+CheckTime_date());
//                           // getApplicationContext().getMainLooper();
//                            if (CheckTime_date()){
//                                //getLocation();
//                                fetchCurrentLocationOnce();
//                            }else {
//                             //stopSelf();
//                             //timerTask.cancel();
//                             //timer.cancel();
//                            }
//
//                        }
//                    });
//
//
//                }
//            };
//        }
//        if (timer==null){
//            Log.e("text","Location Service2"+getPreference("interval"));
//            timer = new Timer();
//            try {
//                timer.schedule(timerTask,5000 , Long.parseLong(getPreference("interval")));
//            } catch (Exception e) {
//                Log.e("GPSTracker", "Invalid interval in preference, using default 60000ms");
//            }
//
//        }
    }
/*    private void ensureLoopRunning() {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
        if (locationRunnable == null) {
            locationRunnable = new Runnable() {
                @Override public void run() {
                    Log.e("text", "Location Service (Handler) " + CheckTime_date());
                    if (CheckTime_date()) fetchCurrentLocationOnce();
                    if (loopActive) handler.postDelayed(this, interval);
                }
            };
        }
        if (!loopActive) {
            loopActive = true;
            handler.postDelayed(locationRunnable, 5000);
            Log.d("GPS","loop started");
        } else {
            Log.d("GPS","loop already active");
        }
    }*/
private final Runnable locationRunnable1 = new Runnable() {
    @Override public void run() {
        try {
            ensureWakeLock();
            boolean inWindow = CheckTime_date();
            Log.d("GPS","tick; window=" + inWindow);
            if (inWindow) fetchCurrentLocationOnce();
            checkAndToggleContinuousByGap();
            markHeartbeat();
        } catch (Throwable t) {
            Log.e("GPS","tick crashed", t); // ✅ crash হলেও লুপ বাঁচবে
        } finally {
            if (loopActive && worker != null) {
                worker.postDelayed(this, interval); // ✅ সব অবস্থায় re-post
            }
        }
    }
};

    private void ensureLoopRunning() {
        if (workerThread == null || !workerThread.isAlive()) {
            workerThread = new HandlerThread("gps-worker");
            workerThread.start();
            worker = new Handler(workerThread.getLooper());
        }
//        if (!loopActive && worker != null) {
//            loopActive = true;
//            worker.postDelayed(locationRunnable1, 2000);
//            Log.d("GPS","loop started");
//        } else {
//            Log.d("GPS","loop already active");
//        }
        if (worker == null) return;

        // ✅ always re-arm the loop
        worker.removeCallbacks(locationRunnable1);
        worker.postDelayed(locationRunnable1, 2000);

        loopActive = true;
        Log.d("GPS","loop (re)armed");
    }

    // ✅ every tick ensure CPU stays awake
    private void ensureWakeLock() {
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm == null) return;

            if (wakeLock == null || !wakeLock.isHeld()) {
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "GPSTracker::WakelockTag");
                wakeLock.setReferenceCounted(false);
                wakeLock.acquire(45 * 60 * 1000L); // 45 min, will renew again
                Log.d("GPS", "wakelock (re)acquired");
            }
        } catch (Exception e) {
            Log.e("GPS", "ensureWakeLock failed", e);
        }
    }
//    private void startContinuousUpdatesIfNeeded() {
//        if (contUpdatesStarted) return;
//        if (!hasLocationPermission()) return;
//
//        try {
//            LocationRequest req = new LocationRequest.Builder(
//                    Priority.PRIORITY_BALANCED_POWER_ACCURACY, 30_000L)
//                    .setMinUpdateIntervalMillis(15_000L)
//                    .setWaitForAccurateLocation(false)
//                    .build();
//
//            contCallback = new LocationCallback() {
//                @Override
//                public void onLocationResult(@NonNull LocationResult r) {
//                    try {
//                        Location loc = r.getLastLocation();
//                        if (loc == null) return;
//                        if (!isGoodFix(loc)) return;
//
//                        lastWarmLocation = loc;
//                        Log.d("GPS", "warm cached acc=" + loc.getAccuracy());
//                        // ✅ Priority guard: fallback tried recently → skip cont save
//                        long now = System.currentTimeMillis();
//                        if (now - lastFallbackAttemptMs < FALLBACK_PRIORITY_WINDOW_MS) {
//                            Log.d("GPS-Save", "skip cont save (fallback priority window)");
//                            return;
//                        }
//                        if (!shouldSaveNow(loc)) return;
//
//                        Location prev = fetchLastSavedLocation();
//                        if (prev != null && !isPlausibleJump(prev, loc)) return;
//
//                        Location toSave = loc;
//                        Handler main = new Handler(Looper.getMainLooper());
//                        main.post(() -> {
//                            try {
//                                saveLocationWithExtras(toSave);
//                                Log.d("GPS-Save", "saved from continuous callback");
//                            } catch (Throwable t) {
//                                Log.e("GPS-Save", "saveLocationWithExtras failed on main", t);
//                            }
//                        });
//
//                    } catch (Throwable t) {
//                        Log.e("GPS", "contCallback crashed", t);
//                    }
//                }
//            };
//
//            fused.requestLocationUpdates(req, contCallback, workerThread.getLooper());
//            contUpdatesStarted = true;
//            Log.d("GPS", "continuous keep-alive started");
//        } catch (Exception e) {
//            Log.e("GPS", "startContinuousUpdatesIfNeeded fail", e);
//        }
//    }
private void checkAndToggleContinuousByGap() {
    Long lastSaved = fetchLastSavedTime();   // DB থেকে সর্বশেষ created_at
    long now = System.currentTimeMillis();

    long gap;
    if (lastSaved == null) {
        // কখনো কিছু সেভ হয়নি → ধরলাম খুব বেশি gap
        //gap = Long.MAX_VALUE;
        gap = 0L;
    } else {
        gap = Math.max(0, now - lastSaved);  // শেষ সেভের পর থেকে কতো ms গেছে
    }

    long thr = CONT_BACKUP_GAP_MS;  // এখানে স্পষ্টভাবে ১ মিনিটই threshold

    if (gap >= thr) {
        // 👉 ১ মিনিট বা তার বেশি সময় ধরে কোনো location সেভ হয়নি
        if (!contUpdatesStarted) {
            Log.d("GPSContinuous", "gap=" + gap + "ms ≥ " + thr + "ms → START continuous backup");
            startContinuousUpdatesIfNeeded();
        }
    } else {
        // 👉 ১ মিনিটের কম গ্যাপ → fallback ঠিকমতো সেভ করছে
        if (contUpdatesStarted) {
            Log.d("GPSContinuous", "gap=" + gap + "ms < " + thr + "ms → STOP continuous backup");
            stopContinuousUpdates();
        }
    }
}
private void startContinuousUpdatesIfNeeded() {
    if (!hasLocationPermission()) return;

    // ✅ ensure worker thread alive (contCallback runs on this looper)
    if (workerThread == null || !workerThread.isAlive()) {
        workerThread = new HandlerThread("gps-worker");
        workerThread.start();
        worker = new Handler(workerThread.getLooper());
        Log.d("GPS", "workerThread restarted for continuous updates");
    }

    try {
        // ✅ if already running, refresh cleanly
        if (fused != null && contCallback != null) {
            fused.removeLocationUpdates(contCallback);
            contCallback = null;
        }
        contUpdatesStarted = false;
    } catch (Exception ignore) {}

    try {
        LocationRequest req = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, 30_000L)
                .setMinUpdateIntervalMillis(15_000L)
                .setWaitForAccurateLocation(false)
                .build();

        contCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult r) {
                try {
                    Location loc = r.getLastLocation();
                    if (loc == null) return;
                    if (!isGoodFix(loc,true)) return;

                    lastWarmLocation = loc;
                    Log.d("GPS", "warm cached acc=" + loc.getAccuracy());

                    long now = System.currentTimeMillis();

                    // ✅ DB থেকে last save time দেখো
                    Long lastSavedDb = fetchLastSavedTime();
                    long lastSavedMs = (lastSavedDb != null) ? lastSavedDb : 0L;

                    // ✅ 1 মিনিটের মধ্যে fallback/অন্য জায়গা থেকে save হয়ে গেলে cont save করবে না
                    if (now - lastSavedMs < CONT_BACKUP_GAP_MS) {
                        Log.d("GPS-Save", "cont skip: last save " + (now-lastSavedMs) + "ms ago");
                        return;
                    }

                    // ✅ cont নিজেও 1 মিনিটের মধ্যে একবারের বেশি save করবে না
                    if (now - lastContSaveMs < CONT_BACKUP_GAP_MS) {
                        Log.d("GPS-Save", "cont throttled by cont memory gate");
                        return;
                    }

                    Location prev = fetchLastSavedLocation();
                    if (prev != null && !isPlausibleJump(prev, loc)) return;

                    Location toSave = loc;

                    // ✅ main-thread safe save (Lifecycle crash avoid)
                    new Handler(Looper.getMainLooper()).post(() -> {
                        try {
                            saveLocationWithExtras(toSave,"backup");
                            lastContSaveMs = System.currentTimeMillis();
                            Log.d("GPS-Save", "saved from continuous callback (backup)");
                        } catch (Throwable t) {
                            Log.e("GPS-Save", "saveLocationWithExtras failed on main", t);
                        }
                    });

                } catch (Throwable t) {
                    Log.e("GPS", "contCallback crashed", t);
                }
            }
        };

        // ✅ NOW runs on workerThread looper
        fused.requestLocationUpdates(req, contCallback, workerThread.getLooper());
        contUpdatesStarted = true;

        Log.d("GPS", "continuous keep-alive started on workerThread");
    } catch (Exception e) {
        Log.e("GPS", "startContinuousUpdatesIfNeeded fail", e);
    }
}
    private void stopContinuousUpdates() {
        try {
            if (fused != null && contCallback != null) {
                fused.removeLocationUpdates(contCallback);
            }
        } catch (Exception ignore) {}
        contUpdatesStarted = false;
        contCallback = null;
        Log.d("GPS", "continuous updates stopped");
    }

    private boolean shouldSaveNow(@NonNull Location loc) {
        // ✅ time window check
        if (!CheckTime_date()) return false;

        // ✅ interval preference (ms)
        long prefInterval;
        try {
            prefInterval = Long.parseLong(getPreference("interval"));
        } catch (Exception e) {
            prefInterval = 60000L; // default 1 minute
        }
      //  prefInterval += 9000L;
        // ✅ minimum safety clamp
        if (prefInterval < 15_000L) prefInterval = 15_000L;

        long now = System.currentTimeMillis();

        synchronized (saveLock) {
            // ✅ in-memory gate (race stop)
            if (now - lastSaveWallMs < prefInterval) return false;

            // ✅ DB gate
            Long lastSavedDb = fetchLastSavedTime();
            if (lastSavedDb != null) {
                long diffDb = Math.max(0, now - lastSavedDb);
                if (diffDb < prefInterval) {
                    lastSaveWallMs = lastSavedDb; // memory sync
                    return false;
                }
            }

            // ✅ reserve this save slot
            lastSaveWallMs = now;
            return true;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IS_RUNNING = true;
        startForegroundServiceSafe();
        try { interval = Long.parseLong(getPreference("interval")); } catch (Exception ignore) {}
        checkAndToggleContinuousByGap();
        ensureLoopRunning();
        Log.w("onStartService","GPSTracker Service is running...");
        return START_STICKY;
    }
    private void markHeartbeat() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putLong(HB_KEY, System.currentTimeMillis())
                .apply();
    }
    private void scheduleKeepAliveWorker() {
        OneTimeWorkRequest keepAliveRequest = new OneTimeWorkRequest.Builder(KeepAliveWorker.class)
                .setInitialDelay(5, TimeUnit.MINUTES) // Delay before starting
                .build();

        WorkManager.getInstance(this).enqueueUniqueWork(
                "keep_alive_task",
                ExistingWorkPolicy.REPLACE,  // Replace the previous work if exists
                keepAliveRequest
        );
    }
    // 🔹 Foreground notification
    private void startForegroundServiceSafe() {
        String CHANNEL_ID = "gps_channel_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "GPS Tracker",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GPS Tracking Active")
                .setContentText("Tracking your location in background")
                .setSmallIcon(R.drawable.new_life) //
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set the priority high to keep it on top
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)  // Makes the notification ongoing (non-dismissable)
                .setAutoCancel(false)  // Disable auto-cancel
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)  // Make the notification visible
                .build();

        startForeground(1, notification);
    }
    // 🔹 Battery optimization
    private void requestIgnoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                try {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("GPSTracker", "Battery opt ignore error: " + e.getMessage());
                }
            }
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

    private static class BatteryInfo {
        int percent = -1;           // 0..100, -1 = unknown
        boolean isCharging = false;
        String chargeSource = "none"; // ac | usb | wireless | none
        float tempC = -1f;            // e.g., 32.1°C
        int health = BatteryManager.BATTERY_HEALTH_UNKNOWN;
    }
    // ▶️ GPSTracker class
    private BatteryInfo getBatteryInfo() {
        BatteryInfo out = new BatteryInfo();

        try {
            // Modern API:  (0–100),
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            if (bm != null) {
                int pct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                if (pct >= 0 && pct <= 100) out.percent = pct;
            }

            // Sticky broadcast:
            IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent i = registerReceiver(null, f);
            if (i != null) {
                int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                out.isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL);

                int plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:        out.chargeSource = "ac"; break;
                    case BatteryManager.BATTERY_PLUGGED_USB:       out.chargeSource = "usb"; break;
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS:  out.chargeSource = "wireless"; break;
                    default:                                       out.chargeSource = "none";
                }

                // Temperature:
                int t = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                if (t > 0) out.tempC = t / 10f;

                out.health = i.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);

                // Fallback percent যদি BATTERY_PROPERTY_CAPACITY -1 দেয়
                if (out.percent < 0) {
                    int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    if (level >= 0 && scale > 0) {
                        out.percent = Math.round((level * 100f) / scale);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("GPSTracker", "getBatteryInfo error: " + e.getMessage());
        }
        return out;
    }
    // ---- core: always get a fresh fix ----
    private void fetchCurrentLocationOnce() {
        if (!hasLocationPermission()) {
            Log.e("GPSTrackerNew", "Location permission missing.");
            saveLocationPermissionDenied("permissionMissing");
            return;
        }
        synchronized (reqLock) {
            if (reqInFlight) return;
            reqInFlight = true;
        }
/*        // Option A: one-shot fresh fix
        fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        saveLocationWithExtras(location);
                    } else {
                        // Option B fallback: small active request (timeout সহ)
                        requestSingleUpdateFallback();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GPSTrackerNew", "getCurrentLocation failed: " + e.getMessage());
                    requestSingleUpdateFallback();
                });*/
        // Optional: very fresh last-known (<= 5s) গ্রহণ করো
/*        fused.getLastLocation().addOnSuccessListener(last -> {
            if (last != null
                    && (System.currentTimeMillis() - (last.getTime() > 0 ? last.getTime() : System.currentTimeMillis())) <= 5_000
                    && isGoodFix(last)) {
                saveLocationWithExtras(last);
            }
        });

        requestSingleUpdateFallback(8_000); // 8s timeout*/


        // (A) last known, only if very fresh
//        Executor main = androidx.core.content.ContextCompat.getMainExecutor(this);
//        fused.getLastLocation().addOnSuccessListener(main, last -> {
//            boolean accepted = false;
//            if (last != null) {
//                long t   = last.getTime() > 0 ? last.getTime() : 0L;
//                long age = (t > 0) ? (System.currentTimeMillis() - t) : Long.MAX_VALUE;
//
//                String prov = (last.getProvider() != null) ? last.getProvider() : "";
//                boolean isGpsOrFused =
//                        "gps".equalsIgnoreCase(prov) || "fused".equalsIgnoreCase(prov);
//
//                boolean strictGood =
//                        age <= 3_000 &&
//                                isGoodFix(last) &&
//                                isGpsOrFused &&
//                                last.hasAccuracy() && last.getAccuracy() <= 25f;
//
//                if (strictGood) {
//                    // ✅ প্লজিবিলিটি (আগের সেভড পয়েন্টের সাথে) — অবাস্তব ‘টেলিপোর্ট’ ব্লক
//                    Location prev = fetchLastSavedLocation(); // তোমারই মেথড
//                    if (prev == null || isPlausibleJump(prev, last)) {
//                        saveLocationWithExtras(last);
//                        accepted = true;
//                    }
//                }
//            }
//            if (!accepted) {
                // (B) fresh request with timeout
            try {
                requestSingleUpdateFallback(8_000);
            } catch (Exception e) {
                Log.e("GPS","fallback start failed", e);
                synchronized (reqLock) { reqInFlight = false; } // ✅ unlock on start failure
            }
//            }
//        });
    }

    private boolean isGoodFix(@NonNull Location loc, boolean isBackup) {
        long nowWall = System.currentTimeMillis();
        long ageMs;
        if (loc.getElapsedRealtimeNanos() > 0) {
            ageMs = (SystemClock.elapsedRealtimeNanos() - loc.getElapsedRealtimeNanos()) / 1_000_000L;
        } else {
            long tWall = (loc.getTime() > 0 ? loc.getTime() : nowWall);
            ageMs = Math.max(0, nowWall - tWall);
        }

        if (!loc.hasAccuracy()) return false;
        float acc = loc.getAccuracy();

        // mock/spoof block
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (loc.isMock()) return false;
        } else if (loc.isFromMockProvider()) {
            return false;
        }

        String p = (loc.getProvider() == null) ? "" : loc.getProvider().toLowerCase(Locale.US);
        float speed = loc.hasSpeed() ? loc.getSpeed() : 0f;

        long maxAge;
        float maxAcc;

        if (acc <= 25f) {
            maxAge = 30_000L;
            maxAcc = 25f;

        } else if (acc <= 100f) {
            maxAge = 20_000L;
            maxAcc = 100f;

        } else {
            // ✅ GPS-only relax for hill/rural
            if ("gps".equals(p) && acc <= 150f && ageMs <= 45_000L && speed <= 25f) {
                maxAge = 45_000L;
                maxAcc = 150f;
            } else {
                return false; // network/fused 100m+ reject
            }
        }

        if (ageMs > maxAge) return false;
        if (acc > maxAcc) return false;

        // ------------------------------------------------------
        // ✅ Standing drift control (primary strict, backup relaxed only when GAP)
        // ------------------------------------------------------
        boolean isNetOrFused = ("network".equals(p) || "fused".equals(p));
        boolean standing = "standing".equalsIgnoreCase(getMotionNow(loc));

        if (standing && isNetOrFused) {

            // DB gap (last saved time)
            long gapMs = 0L;
            Long lastSaved = fetchLastSavedTime(); // created_at millis
            if (lastSaved != null) gapMs = Math.max(0, nowWall - lastSaved);

            boolean gapTooLong = (lastSaved == null) || (gapMs >= CONT_BACKUP_GAP_MS);

            if (!isBackup) {
                // ✅ PRIMARY (fallback) = strict always
                if (acc > 35f) return false;
                if (ageMs > 5_000L) return false;

            } else {
                // ✅ BACKUP (continuous)
                if (!gapTooLong) {
                    // gap কম → backup থেকেও strict (drift avoid)
                    if (acc > 35f) return false;
                    if (ageMs > 5_000L) return false;
                } else {
                    // gap ≥ 60s → backup allow degraded fix (data must be saved)
                    if (acc > 80f) return false;
                    if (ageMs > 15_000L) return false;
                }
            }
        }


        // ✅ strict network/fused gate (city drift stop)
        boolean moving = speed > 4f; // ~15 km/h+
        if (isNetOrFused && !standing) {
            if ("network".equals(p) || "fused".equals(p)) {
                if (!moving) {
                    if (acc > 45f) return false;
                    if (ageMs > 6_000L) return false;
                }else {
                    if (acc > 100f) return false;
                    if (ageMs > 20_000L) return false;
                    if (speed > 22f && acc > 60f) return false; // optional extra
                }
//            if (acc > 40f) return false;
//            if (ageMs > 6_000L) return false;
//            if (speed > 8f && acc > 25f) return false;
            }
        }

        return true;
    }

//    private boolean isPlausibleJump(@Nullable Location last, @NonNull Location curr) {
//        if (last == null) return true;
//
//        long dt = curr.getTime() - last.getTime();
//        if (dt <= 0) return false;
//
//        float d = last.distanceTo(curr);     // meters
//        float v = d / (dt / 1000f);          // m/s
//
//        // accuracy
//        float accSum = (last.hasAccuracy()? last.getAccuracy():0f) + (curr.hasAccuracy()? curr.getAccuracy():0f);
//        if (accSum > 120f) return v <= (MAX_PLAUSIBLE_SPEED * 1.5f);
//
//        return v <= MAX_PLAUSIBLE_SPEED;
//    }
private boolean isPlausibleJump(@androidx.annotation.Nullable Location prev,
                                @androidx.annotation.Nullable Location curr) {
    // ✅ hard null-guards
    if (curr == null) return false; // invalid
    if (prev == null) return true;  // no baseline → accept

    // dt হিসাব: elapsedRealtime থাকলে সেটা, নইলে wall-clock
    long prevErn = prev.getElapsedRealtimeNanos();
    long currErn = curr.getElapsedRealtimeNanos();

    double dt;
    if (prevErn > 0 && currErn > 0) {
        dt = (currErn - prevErn) / 1e9;
    } else {
        dt = (curr.getTime() - prev.getTime()) / 1000.0;
    }

    // dt খারাপ হলে conservative accept করো (baseline issue)
    if (dt <= 0) return true;

    float dist = prev.distanceTo(curr);

    // 1) খুব ছোট উইন্ডোতে বিশাল লাফ → reject
    if (dt < TELEPORT_WINDOW_S && dist > TELEPORT_METERS) return false;

    // 2) Long gap হলে speed-check skip → accept; gap upstream-এ লগ কোরো
    if (dt >= LONG_GAP_S) return true;

    // 3) speed dilution ঠেকাতে dt cap
    double dtUsed = Math.min(dt, SPEED_DT_CAP_S);
    double v = dist / dtUsed;

    float accSum = (prev.hasAccuracy()? prev.getAccuracy():0f)
            + (curr.hasAccuracy()? curr.getAccuracy():0f);
    double vLimit = (accSum > 120f) ? (MAX_PLAUSIBLE_SPEED * 1.5) : MAX_PLAUSIBLE_SPEED;

    return v <= vLimit;
}
    // Fallback:
    private void requestSingleUpdateFallback(long timeoutMs) {
        if (!hasLocationPermission()) {
            synchronized (reqLock) { reqInFlight = false; }
            return;
        }

        LocationRequest.Builder b = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 0)
                .setMinUpdateIntervalMillis(0)
                .setMinUpdateDistanceMeters(0f)
                .setMaxUpdateDelayMillis(0)            // no batching
                .setWaitForAccurateLocation(true)
                .setMaxUpdateAgeMillis(0)// ✅ wait for GPS-grade
                .setMaxUpdates(Integer.MAX_VALUE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            b.setGranularity(Granularity.GRANULARITY_FINE);
        }

        final LocationRequest req = b.build();
        final Location prev = fetchLastSavedLocation();
        final Handler main = new Handler(Looper.getMainLooper());

        // ✅ This guards against late callbacks after timeout/accept
        final AtomicBoolean done = new AtomicBoolean(false);

        final LocationCallback cb = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {

                // ✅ If timeout already happened OR accepted earlier, ignore late results
                if (done.get()) return;

                boolean accepted = false;

                try {
                    if (result != null) {
                        for (Location loc : result.getLocations()) {
                            if (loc == null) continue;
                            if (!isGoodFix(loc,false)) continue;
                            if (prev != null && !isPlausibleJump(prev, loc)) {
                                Log.w("GPS","reject implausible jump");
                                continue;
                            }

                            // ✅ Accept
                            accepted = true;

                            // mark done BEFORE heavy work & removeUpdates
                            if (done.compareAndSet(false, true)) {
                                try { fused.removeLocationUpdates(this); } catch (Exception ignore) {}
                                saveLocationWithExtras(loc,"fallback");
                                Log.d("GPS","accepted acc=" + loc.getAccuracy() +
                                        " prov=" + loc.getProvider());
                            }
                            return;
                        }
                    }

                } finally {
                    // ✅ unlock only when this request is fully "done"
                    if (accepted) {
                        synchronized (reqLock) { reqInFlight = false; }
                    }
                }

                // ❌ Not accepted path
                // If timeout didn't happen yet, then record FAILED
                if (!accepted && done.compareAndSet(false, true)) {
                    synchronized (reqLock) { reqInFlight = false; }

                    HashMap<String, String> map = new HashMap<>();
                    map.put("insert_time", getCurrentDateTime24());
                    map.put("is_pushed", "0");
                    map.put("created_at", String.valueOf(System.currentTimeMillis()));
                    map.put("updated_at",String.valueOf(System.currentTimeMillis()));

                    Data_Source ds = new Data_Source(mContext);
                    ds.InsertTable(map, Tables.TABLE_NAME_GPS_TRACKING_FAILED_TIME);
                    Toast.makeText(mContext, "Location unavailable. Turn on GPS Allow all the time permission or restart your device if the issue continues.", Toast.LENGTH_SHORT).show();
                    Log.w("GPSTracker", "bad/none fix dropped");
                }
            }
        };

        //fused.requestLocationUpdates(req, cb, Looper.getMainLooper());
        // ✅ ONLY CHANGE: wrap requestLocationUpdates with try/catch
        try {
            fused.requestLocationUpdates(req, cb, Looper.getMainLooper());
        } catch (Exception e) {
            Log.e("GPS", "requestLocationUpdates failed", e);
            // prevent reqInFlight stuck
            synchronized (reqLock) { reqInFlight = false; }
            return;
        }
        // ✅ Hard timeout
        main.postDelayed(() -> {
            // if already accepted/failed, do nothing
            if (!done.compareAndSet(false, true)) return;

            try { fused.removeLocationUpdates(cb); } catch (Exception ignore) {}

            synchronized (reqLock) { reqInFlight = false; }

            Log.w("GPS", "timeout: no acceptable fresh fix");
            // NOTE: timeout হলে এখানে আর FAILED_TIME ইনসার্ট করছি না,
            // কারণ late callback ignore হবে এবং false fail গণনা হবে না।
        }, timeoutMs);
    }
    private boolean hasLocationPermission() {
        boolean fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean bg = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
            //
            return (fine || coarse) && bg;
        }
        return fine || coarse;
    }
    //
    private void saveLocationWithExtras(Location loc,String from) {
        double lat = loc.getLatitude();
        double lon = loc.getLongitude();

//        if (!isGoodFix(loc)) {
//            Log.w("GPS", "Rejected (quality): acc=" + loc.getAccuracy() + " age=" + (System.currentTimeMillis()-loc.getTime()));
//            return;
//        }
        // 1) last point নিয়ে plausibility check
        Location lastLoc = fetchLastSavedLocation();
        if (!isPlausibleJump(lastLoc, loc)) {
            Log.w("GPS", "Rejected (outlier jump): " +
                    (lastLoc != null ? lastLoc.getLatitude()+","+lastLoc.getLongitude() : "null") +
                    " -> " + loc.getLatitude()+","+loc.getLongitude());
            return;
        }
        //
        double[] last = fetchLastSavedLatLon();
        float distanceM = 0f;
        if (last != null) {
            distanceM = distanceMeters(last[0], last[1], lat, lon); // meters
        }

        Long lastSaved = fetchLastSavedTime();
        if (lastSaved != null) {
            long now = System.currentTimeMillis();
//            long diff = now - lastSaved;
            long diff = Math.max(0, now - lastSaved);
            //long interval = Long.parseLong(getPreference("interval"));
            long interval = 5 * 60 * 1000;  // Fixed 5 minutes in milliseconds

            if (diff >= interval) {
                String gapText = humanizeDuration(diff);
                Log.w("GPS-GAP", "Gap detected: " + diff + " ms (expected " + interval + ")");
                saveGapRecord(diff, now,gapText,lastSaved);
            }
        }

        SharedPreferences sp = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
        String arStatus = sp.getString("last_activity_status", null);
        long arTs = sp.getLong("last_activity_ts", 0L);
        boolean arFresh = (System.currentTimeMillis() - arTs) <= 30_000L;
        String status12 = (arFresh && arStatus != null && !"unknown".equals(arStatus))
                ? arStatus
                : classifyStatus(loc); //
        String status1 = getMotionNow(loc);
        String address = reverseGeocode(lat, lon);

        // Source/provider

        String provider = loc.getProvider() != null ? loc.getProvider() : "gps";

//        Log.d("GPSTrackerNew",
//                "Saving: lat=" + lat + " lon=" + lon + " status=" + status + " addr=" + address + " provider=" + provider);
        BatteryInfo bi = getBatteryInfo();

        HashMap<String, String> map = new HashMap<>();
        map.put("latitude", String.valueOf(lat));
        map.put("longitude", String.valueOf(lon));
        map.put("gps_bts", from); //
        map.put("status", status1);
        map.put("address", address);
        map.put("type", provider);           //
        map.put("distance",  String.valueOf(distanceM));
        map.put("is_pushed", "0");
        map.put("tracking_date_time", getCurrentDateTime24());
        map.put("battery_level", String.valueOf(bi.percent));
        map.put("created_at", String.valueOf(System.currentTimeMillis()));
        map.put("updated_at", String.valueOf(System.currentTimeMillis()));

        Data_Source ds = new Data_Source(mContext);
        ds.InsertTable(map, "gps_tracker");

        //Toast.makeText(mContext, "Service Running", Toast.LENGTH_SHORT).show();
        Log.d("Service Running","Yes");
        markHeartbeat();
    }

    private void saveGapRecord(long gapeMillis, long insertTime, @NonNull String gapText,long lastSaved) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("insert_time", getCurrentDateTime24());
            map.put("gape_time", gapText);
            map.put("last_time", String.valueOf(lastSaved));
            map.put("is_pushed", "0");
            map.put("created_at", String.valueOf(System.currentTimeMillis()));
            map.put("updated_at",String.valueOf(System.currentTimeMillis()));

            Data_Source ds = new Data_Source(mContext);
            ds.InsertTable(map, Tables.TABLE_NAME_GPS_TRACKING_GAPE_TIME);

            Log.i("GPS-GAP", "Saved gap record: " + gapeMillis + " ms");
        } catch (Exception e) {
            Log.e("GPS-GAP", "Failed to save gap: " + e.getMessage());
        }
    }

    private static String humanizeDuration(long ms) {
        if (ms < 0) ms = 0;
        long totalSec = ms / 1000;
        long days  = totalSec / 86400;            // 24*60*60
        long rem1  = totalSec % 86400;
        long hours = rem1 / 3600;
        long rem2  = rem1 % 3600;
        long mins  = rem2 / 60;
        long secs  = rem2 % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" Day");
            if (hours > 0) sb.append(" ").append(hours).append(" Hours");
            return sb.toString();
        }
        if (hours > 0) {
            sb.append(hours).append(" Hours");
            if (mins > 0) sb.append(" ").append(mins).append(" Minutes");
            return sb.toString();
        }
        if (mins > 0) {
            sb.append(mins).append(" Minutes");
            if (secs > 0) sb.append(" ").append(secs).append(" Seconds");
            return sb.toString();
        }
        // < 1 minute
        sb.append(secs).append(" Seconds");
        return sb.toString();
    }
    public String getCurrentDateTime24()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String CurrentDate =dateFormat.format(date);
        return CurrentDate;

    }
/*    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null && wakeLock == null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GPSTracker::WakelockTag");
            wakeLock.setReferenceCounted(false);
            wakeLock.acquire(30 * 60 * 1000L); // 30min
        }
    }*/

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
    @Nullable
    private Location fetchLastSavedLocation() {
        try {
            String sql = "SELECT latitude, longitude, tracking_date_time " +
                    "FROM gps_tracker WHERE latitude IS NOT NULL AND longitude IS NOT NULL " +
                    "ORDER BY created_at DESC LIMIT 1";

            android.database.sqlite.SQLiteDatabase db =
                    android.database.sqlite.SQLiteDatabase.openDatabase(
                            mContext.getDatabasePath(Tables.DATABASE_NAME).getPath(),
                            null,
                            android.database.sqlite.SQLiteDatabase.OPEN_READONLY
                    );

            android.database.Cursor c = db.rawQuery(sql, null);
            Location L = null;
            if (c.moveToFirst()) {
                double lat = c.getDouble(0);
                double lon = c.getDouble(1);
                String ts  = c.getString(2); // "yyyy-MM-dd HH:mm:ss"

                L = new Location("db");
                L.setLatitude(lat);
                L.setLongitude(lon);
                try {
                    long t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(ts).getTime();
                    L.setTime(t);
                } catch (Exception ignore) {}
                L.setAccuracy(25f);
            }
            c.close(); db.close();
            return L;
        } catch (Exception e) {
            Log.e("fetchLastSavedLocation", "err: " + e.getMessage());
            return null;
        }
    }

        @Nullable
    private double[] fetchLastSavedLatLon() {
        try {
            String sql = "SELECT latitude, longitude " +
                    "FROM gps_tracker " +
                    "WHERE latitude IS NOT NULL AND longitude IS NOT NULL " +
                    "ORDER BY created_at DESC " +
                    "LIMIT 1";

            android.database.sqlite.SQLiteDatabase db =
                    android.database.sqlite.SQLiteDatabase.openDatabase(
                            mContext.getDatabasePath(Tables.DATABASE_NAME).getPath(),
                            null,
                            android.database.sqlite.SQLiteDatabase.OPEN_READONLY
                    );

            android.database.Cursor c = db.rawQuery(sql, null);
            double[] pair = null;
            if (c.moveToFirst()) {
                double lat = c.getDouble(0);
                double lon = c.getDouble(1);
                pair = new double[]{lat, lon};
            }
            c.close();
            db.close();
            return pair;
        } catch (Exception e) {
            Log.e("fetchLastSavedLatLon", "err: " + e.getMessage());
            return null;
        }
    }
    @Nullable
    private Long fetchLastSavedTime() {
        try {
            String sql = "SELECT created_at " +
                    "FROM gps_tracker " +
                    "WHERE created_at IS NOT NULL " +
                    "ORDER BY created_at DESC " +
                    "LIMIT 1 OFFSET 0";

            android.database.sqlite.SQLiteDatabase db =
                    android.database.sqlite.SQLiteDatabase.openDatabase(
                            mContext.getDatabasePath(Tables.DATABASE_NAME).getPath(),
                            null,
                            android.database.sqlite.SQLiteDatabase.OPEN_READONLY
                    );

            android.database.Cursor c = db.rawQuery(sql, null);
            Long lastTime = null;
            if (c.moveToFirst()) {
                lastTime = c.getLong(0); // created_at is in millis
            }
            c.close();
            db.close();
            return lastTime;
        } catch (Exception e) {
            Log.e("fetchLastSavedTime", "err: " + e.getMessage());
            return null;
        }
    }
    private static float distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        float[] res = new float[1];
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, res);
        return res[0]; // meters
    }
    private String classifyStatus(Location loc) {
        final float STANDING_MAX = 0.5f;     // ~0-1.8 km/h
        final float WALK_MAX     = 2.2f;     // ~8 km/h
        final float BIKE_MAX     = 6.9f;     // ~25 km/h
        final float MAX_VALID_ACCURACY = 100f; // meters

        if (loc.hasAccuracy() && loc.getAccuracy() > MAX_VALID_ACCURACY) {
            return "unknown";
        }

        float speed = 0f;
        if (loc.hasSpeed()) {
            speed = loc.getSpeed(); // m/s
        }

        long now = loc.getTime() > 0 ? loc.getTime() : System.currentTimeMillis();
        if ((speed <= STANDING_MAX || !loc.hasSpeed()) && lastLat != null && lastLon != null && lastTs != null) {
            float d = distanceMeters(lastLat, lastLon, loc.getLatitude(), loc.getLongitude());
            float dt = (now - lastTs) / 1000f; // seconds
            if (dt >= 5f) { // খুব ছোট Δt হলে noise বেশি হয়
                float v = d / dt; // m/s
                if (v > 0.5f) speed = v;
            }
        }
        final String status;
        if (speed <= STANDING_MAX) {
            status = "standing";
        } else if (speed <= WALK_MAX) {
            status = "walking";
        } else if (speed <= BIKE_MAX) {
            status = "bicycle";
        } else {
            status = "vehicle";
        }
        lastLat = loc.getLatitude();
        lastLon = loc.getLongitude();
        lastTs  = now;

        return status;
    }
    private String reverseGeocode(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && !list.isEmpty()) {
                Address a = list.get(0);
                String line = a.getMaxAddressLineIndex() >= 0 ? a.getAddressLine(0) : null;
                if (line != null && !line.trim().isEmpty()) return line;
                // fallback compose
                return (a.getSubLocality() != null ? a.getSubLocality() + ", " : "") +
                        (a.getLocality() != null ? a.getLocality() + ", " : "") +
                        (a.getAdminArea() != null ? a.getAdminArea() + ", " : "") +
                        (a.getCountryName() != null ? a.getCountryName() : "");
            }
        } catch (IOException e) {
            Log.e("GPSTrackerNew", "Geocoder failed: " + e.getMessage());
        }
        return "";
    }
    private boolean CheckTime_date()  {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Get the current date as string in "yyyy-MM-dd" format
        Calendar calendar = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        Date starttimme = null;
        try {
            starttimme = df.parse(currentDate + " " + getPreference("start_time"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date endtime = null;
        try {
            endtime = df.parse(currentDate + " " + getPreference("end_time"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar c = Calendar.getInstance();
        Date currennttime = null;
        try {
            currennttime = df.parse(currentDate + " " +c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        long current_time = currennttime.getTime();
        Log.e("Time","current_time= "+currennttime+" start_time_milis= "+starttimme.getTime()+" end_time_milis= "+ endtime.getTime());
        if (current_time>starttimme.getTime() && current_time<endtime.getTime()){
            Log.e("Time","true");
            return true;
        }else {
            Log.e("Time","false");
            return false;
        }
    }

/*    @RequiresApi(api = Build.VERSION_CODES.M)
    public Location getLocation() {

        try {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.e("no_provider", "Network");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return location;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.e("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Log.e("latitude", "" + latitude);
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.e("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            Log.e("getLocation", "getLocation: " + e.getLocalizedMessage());
        }
        Log.e("getLocation", "getLocation: " + location);

        Data_Source data_source = new Data_Source(mContext);

       // Toast.makeText(mContext,"Location Inserted",Toast.LENGTH_LONG).show();
        HashMap<String, String> map = new HashMap<String, String>();
        if (location != null) {
            map.put("latitude", location.getLatitude() + "");
            map.put("longitude", location.getLongitude() + "");
            map.put("is_pushed", "0");
            map.put("created_at", System.currentTimeMillis() + "");
            data_source.InsertTable(map, "gps_tracker");
        }


        return location;
    }*/
    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public String getPreference(String key)
    {
        String value="";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        value = prefs.getString(key, "0");

        return value;

    }

    @Override
    public void onDestroy() {
        IS_RUNNING = false;
        saveServiceStopTime("onDestroy");
        scheduleKeepAliveWorker();
        Log.d("Service,","onDestroy");
        stopHandler();
        releaseWakeLock();
        stopContinuousUpdates();
        loopActive = false;
        if (worker != null) worker.removeCallbacksAndMessages(null);
        if (workerThread != null) { workerThread.quitSafely(); workerThread = null; }
        try { if (arManager != null) arManager.stop(); } catch (Exception ignore) {}
        AlarmScheduler.scheduleExactPing(getApplicationContext(), 15 * 60_000L);
        pingAlarmReceiver(this);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        //scheduleKeepAliveWorker();
        // Relaunch self (best effort)
        Log.d("Service,","onTaskRemoved");
        try {
            saveServiceStopTime("task_removed");
            // Stop current handler safely (prevent leaks)
            stopHandler();
            Intent restartService = new Intent(getApplicationContext(), GPSTracker.class);
            restartService.setPackage(getPackageName());
            PendingIntent restartPendingIntent =
                    PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartPendingIntent
            );
            pingAlarmReceiver(this);
        }catch (Exception e) {
        Log.e("GPSTracker", "Error in onTaskRemoved", e);
       }


    }
    // ✅ Safe stop method (to prevent leaks / crashes)
    private void stopHandler() {
        try {
            IS_RUNNING = false;
            loopActive = false; // ✅ add this
            if (worker != null) worker.removeCallbacks(locationRunnable1);
            if (handler != null && locationRunnable != null) {
                handler.removeCallbacks(locationRunnable);
            }
        } catch (Exception e) {
            Log.e("GPSTracker", "Error stopping handler", e);
        }
    }
    private void saveServiceStopTime(String reason) {
        long now = System.currentTimeMillis();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putLong(PREF_STOP_TIME, now)
                .putString(PREF_STOP_REASON, reason) // চাইলে পাঠাবে, না চাইলে বাদ
                .apply();
    }
    private void saveLocationPermissionDenied(String reason) {
        long now = System.currentTimeMillis();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putLong(PREF_LOCATION_DENIED, now)
                .putString(PREF_LOCATION_DENIED_REASON, reason)
                .apply();
    }
    private void pingAlarmReceiver(Context context) {
        Intent i = new Intent(context, AlarmReceiver.class);
        context.sendBroadcast(i);
    }
    private @NonNull String getMotionNow(@Nullable Location hint) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String ar = sp.getString("last_activity_status", null);
        long arTs = sp.getLong("last_activity_ts", 0L);
        //boolean arFresh = (System.currentTimeMillis() - arTs) <= 30_000L;
        String arStatus = (ar == null) ? null
                : ar.trim().toLowerCase(Locale.US);

        long ageMs = System.currentTimeMillis() - arTs;
//        Log.d("arStatus", "raw=" + ar + " norm=" + arStatus
//                + " ageMs=" + ageMs);
        //if (arFresh && ar != null && !"unknown".equals(ar)) return ar;
        boolean arFresh = (System.currentTimeMillis() - arTs) <= 90_000L;
        if (arFresh && isValidAr(ar)) return ar;
        if (hint != null) {
            // ✅ speed based quick classify before full classifyStatus
            if (hint.hasSpeed() && hint.getSpeed() >= 1.2f) return "walking";
            // running না থাকলেও walking return দিলেই HIGH trigger হবে

            String st = classifyStatus(hint);
            if (st != null) return st;
        }
        return "unknown";
    }
    private static boolean isValidAr(String s) {
        if (s == null) return false;
        switch (s) {
            case "standing":
            case "walking":
            case "running":
            case "bicycle":
            case "vehicle":
                return true;
            default:
                return false;
        }
    }
}
