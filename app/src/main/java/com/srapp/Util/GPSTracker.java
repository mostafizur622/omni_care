package com.srapp.Util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.srapp.ActivityRecognition.ActivityRecognitionManager;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        fused = LocationServices.getFusedLocationProviderClient(this);
      //  Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
        arManager = new ActivityRecognitionManager(this);
        arManager.start(Long.parseLong(getPreference("interval")));



        if (timerTask==null){
            Log.e("text","Location Service2"+getPreference("interval"));

            timerTask = new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("text","Location Service2"+CheckTime_date());
                           // getApplicationContext().getMainLooper();
                            if (CheckTime_date()){
                                //getLocation();
                                fetchCurrentLocationOnce();
                            }else {
                             stopSelf();
                             timerTask.cancel();
                             timer.cancel();
                            }

                        }
                    });


                }
            };
        }
        if (timer==null){
            Log.e("text","Location Service2"+getPreference("interval"));
            timer = new Timer();
            timer.schedule(timerTask,5000 , Long.parseLong(getPreference("interval")));
        }
    }
    // ---- core: always get a fresh fix ----
    private void fetchCurrentLocationOnce() {
        if (!hasLocationPermission()) {
            Log.e("GPSTrackerNew", "Location permission missing.");
            return;
        }

        // Option A: একদম one-shot fresh fix
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
                });
    }

    // Fallback: একবারের আপডেট নিয়ে removeUpdates করা
    private void requestSingleUpdateFallback() {
        if (!hasLocationPermission()) return;

        LocationRequest req = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(0)
                .setMaxUpdates(1)              // একবারই নেবে
                .build();

        LocationCallback cb = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                fused.removeLocationUpdates(this);
                Location loc = result.getLastLocation();
                if (loc != null) saveLocationWithExtras(loc);
            }
        };

        fused.requestLocationUpdates(req, cb, Looper.getMainLooper());
    }

    private boolean hasLocationPermission() {
        boolean fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // যদি background লাগবে:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean bg = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
            // আপনার দরকার অনুযায়ী bg না থাকলেও চলতে পারে যদি সার্ভিস foreground হয়
            return (fine || coarse) && bg;
        }
        return fine || coarse;
    }

    // আপনার ৩টা নতুন ইনফো যুক্ত করে DB তে save
    private void saveLocationWithExtras(Location loc) {
        double lat = loc.getLatitude();
        double lon = loc.getLongitude();
        // আগের লোকেশন আনুন
        double[] last = fetchLastSavedLatLon();
        float distanceM = 0f;
        if (last != null) {
            distanceM = distanceMeters(last[0], last[1], lat, lon); // meters
        }
        // (১) Status (speed-based heuristic)
        // speed m/s: 0=still, ~1.4 = walking, ~5-8 cycling, >8 vehicle
        //String status = deriveStatusFromSpeed(loc);
        SharedPreferences sp = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
        String arStatus = sp.getString("last_activity_status", null);

// যদি AR থেকে পাওয়া থাকে, সেটাই ব্যবহার করুন; না থাকলে আপনার fallback (speed-based)
        String status1 = (arStatus != null && !"unknown".equals(arStatus))
                ? arStatus
                : classifyStatus(loc); // আপনার আগের fallback ফাংশন

       // Toast.makeText(mContext, status1, Toast.LENGTH_SHORT).show();

        // (২) Address (reverse geocode) — try/catch + fallback
        String address = reverseGeocode(lat, lon);

        // (৩) Source/provider
        // Fused হলে provider সাধারণত "fused"; নইলে "gps"/"network"
        String provider = loc.getProvider() != null ? loc.getProvider() : "gps";

//        Log.d("GPSTrackerNew",
//                "Saving: lat=" + lat + " lon=" + lon + " status=" + status + " addr=" + address + " provider=" + provider);

        HashMap<String, String> map = new HashMap<>();
        map.put("latitude", String.valueOf(lat));
        map.put("longitude", String.valueOf(lon));
        map.put("gps_bts", ""); // চাইলে cell info লিখতে পারেন
        map.put("status", status1);
        map.put("address", address);
        map.put("type", provider);           // আপনার GPS_TRACKER_NETWORK_TYPE = "type"
        map.put("distance",  String.valueOf(distanceM));
        map.put("is_pushed", "0");
        map.put("created_at", String.valueOf(System.currentTimeMillis()));
        map.put("updated_at", String.valueOf(System.currentTimeMillis()));

        Data_Source ds = new Data_Source(mContext);
        ds.InsertTable(map, "gps_tracker");
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
    private static float distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        float[] res = new float[1];
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, res);
        return res[0]; // meters
    }
    private String classifyStatus(Location loc) {
        final float STANDING_MAX = 0.5f;     // ~0-1.8 km/h
        final float WALK_MAX     = 2.2f;     // ~8 km/h
        final float BIKE_MAX     = 6.9f;     // ~25 km/h
        final float MAX_VALID_ACCURACY = 50f; // meters
        Double lastLat = null, lastLon = null;
        Long lastTs = null; // epoch millis
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
            if (dt >= 2f) { // খুব ছোট Δt হলে noise বেশি হয়
                float v = d / dt; // m/s
                if (v > 0.3f) speed = v;
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
    private String deriveStatusFromSpeed(Location loc) {
        float speed = loc.hasSpeed() ? loc.getSpeed() : 0f; // m/s
        if (speed < 0.5f) return "standing";
        if (speed < 2.5f) return "walking";
        if (speed < 6.5f) return "bicycle";
        return "vehicle"; // bus/train/car
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

    @RequiresApi(api = Build.VERSION_CODES.M)
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
    }

    public Location getLocationonetime(Context context) {

        try {

            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    Log.d("Network", "Network");
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

                //change

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
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


        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

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

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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

}
