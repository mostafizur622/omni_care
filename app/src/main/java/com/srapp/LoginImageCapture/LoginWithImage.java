package com.srapp.LoginImageCapture;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.LoginImageCapture.LocationUtils.isInsideGeofence;
import static com.srapp.LoginImageCapture.LocationUtils.isWithinRadius;
import static com.srapp.Util.Constants.MIN_ORDER_NUMBER;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.srapp.Dashboard;
import com.srapp.Db_Actions.Difine;
import com.srapp.Db_Actions.URL;
import com.srapp.GPS_UPdate;
import com.srapp.R;
import com.srapp.Util.JAPIClient;
import com.srapp.apiService.ApiInterfaceForJava;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/******
 **** Created By  TANVIR3488 AT 18/8/25 11:44 PM
 ******/


public class LoginWithImage extends AppCompatActivity implements BasicFunctionListener {

    private CameraHelper cameraHelper;
    ImageView login_button;
    String imageBase64;
    EditText editUsername;
    EditText editPassword;
    private BasicFunction basicFunction;
    ApiInterfaceForJava api;
    LocationManager locationManager;
    String currentLatitude, currentLongitude;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationHelper locationHelper;
    private volatile Location latestFix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize FusedLocationProviderClient
/*
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            // Request for permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
*/
        locationHelper = new LocationHelper(this);
        if (!locationHelper.hasFinePermission()) {
            locationHelper.requestFinePermission();
        } else {
            fetchLocationOrAskSettings();
        }

        cameraHelper = new CameraHelper(this, new CameraHelper.CameraCallback() {
            @Override
            public void onImageCaptured(Bitmap bitmap, String base64String) {
                imageBase64 = base64String;
                Toast.makeText(LoginWithImage.this, "Image Captured!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(LoginWithImage.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        // Trigger the camera flow
        cameraHelper.startCameraFlow();
        api = JAPIClient.getClient().create(ApiInterfaceForJava.class);

        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);
        basicFunction = new BasicFunction(this, this);
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latestFix == null) {
                    Toast.makeText(getApplicationContext(), "Getting location... Please wait a moment.", Toast.LENGTH_SHORT).show();
                    fetchLocationOrAskSettings(); // আবার চেষ্টা
                    return;
                }
                Log.e("ImageBase64", imageBase64);
                /*login_button.setEnabled(false);
                startActivity(new Intent(LoginActivity.this, Dashboard.class));
                finish();*/


                if (basicFunction.getPreference("sales_person_id").equalsIgnoreCase("null") || basicFunction.isInternetOn()) {
                    JSONObject jsonObject = new JSONObject();
                    try {


                        basicFunction.savePreference("mac", basicFunction.getPreference("mac"));
                        jsonObject.put(Difine.USERENAME, editUsername.getText().toString().trim());
                        jsonObject.put("password", editPassword.getText().toString().trim());
                        jsonObject.put("mac", basicFunction.getPreference("mac"));
                        jsonObject.put("version", URL.VERSION);
                        jsonObject.put("image_base64", imageBase64);

                        //  basicFunction.getResponceData(URL.Login, jsonObject.toString(), 101);
                        Log.e("map : ", jsonObject.toString());


                        ProgressDialog dailog = CheckConnection(LoginWithImage.this, "Login Check...");
                        if (dailog == null)
                            return;

                        api.loginNew(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                login_button.setEnabled(false);
                                dailog.dismiss();
                                Log.e("test", response.body());
                                try {

                                    JSONObject jsonObject = new JSONObject(response.body());
                                    if (jsonObject.getJSONArray("response").getJSONObject(0).getString("status").equalsIgnoreCase("1")) {
                                        basicFunction.savePreference(SR_ID, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sales_person_id"));
                                        basicFunction.savePreference("sr_uname", editUsername.getText().toString().trim());
                                        FirebaseCrashlytics.getInstance().setUserId(basicFunction.getPreference("sr_uname"));
                                        basicFunction.savePreference("password", editPassword.getText().toString().trim());
                                        basicFunction.savePreference("office_id", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_id"));
                                        basicFunction.savePreference("territory_id", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("territory_id"));
                                        basicFunction.savePreference("store_id", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("store_id"));
                                        basicFunction.savePreference(SR_ID, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sales_person_id"));
                                        basicFunction.savePreference("office_name", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_name"));
                                        basicFunction.savePreference("office_name_bn", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("name_bangla"));
                                        basicFunction.savePreference("office_address", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_address"));
                                        basicFunction.savePreference("office_address_bn", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_address_bangla"));
                                        basicFunction.savePreference("office_phone", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_phone"));
                                        basicFunction.savePreference("sr_name", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_name"));
                                        basicFunction.savePreference("db_name", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_name"));
                                        basicFunction.savePreference("db_address", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_address"));
                                        basicFunction.savePreference("db_mobile", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_mobile"));
                                        basicFunction.savePreference(MIN_ORDER_NUMBER, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString(MIN_ORDER_NUMBER));
                                        // basicFunction.savePreference(MIN_ORDER_NUMBER,"1");


                                        basicFunction.savePreference("store_id", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("store_id"));
                                        basicFunction.savePreference("ae_id", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("ae_id"));
                                        basicFunction.savePreference("tso_id", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("tso_id"));
                                        basicFunction.savePreference("db_id", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_id"));
                                        basicFunction.savePreference("sr_id", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_id"));
                                        basicFunction.savePreference("sr_code", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_code"));
                                        basicFunction.savePreference("start_time", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("start_time"));
                                        basicFunction.savePreference("end_time", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("end_time"));
                                        basicFunction.savePreference("interval", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("interval"));
                                        basicFunction.savePreference("deliveryTime", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("show_delivery_date_time"));
                                        basicFunction.savePreference("roll", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("user_type"));
                                        //check location validation type
                                        String is_geofence_radius= jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("is_geofance_redias");
                                        String radiusMeter= jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("rediasMeter");
                                        Log.e("GeoFenceCheck", "is_geofence_radius: " + is_geofence_radius);

                                        //*currentLocation*//*
                                        double currentLat = latestFix.getLatitude();
                                        double currentLng = latestFix.getLongitude();
                                        Location currentLocation = new Location("");
                                        currentLocation.setLatitude(currentLat);
                                        currentLocation.setLongitude(currentLng);

                                        if (is_geofence_radius.equalsIgnoreCase("2")){
                                            /*LoginLocation*/
                                            String loginLatStr = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getJSONObject("centerPoint").getString("lat");
                                            String loginLngStr = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getJSONObject("centerPoint").getString("lng");
                                            Log.e("", "Login Location: " + loginLatStr + ", " + loginLngStr);
                                            double loginLat = Double.parseDouble(loginLatStr);
                                            double loginLng = Double.parseDouble(loginLngStr);
                                            Location loginLocation = new Location("");
                                            loginLocation.setLatitude(loginLat);
                                            loginLocation.setLongitude(loginLng);
                                            //checkValidation
                                            boolean isAllowed = isWithinRadius(currentLocation, loginLocation, Float.parseFloat(radiusMeter));
                                            if (isAllowed) {
                                                Toast.makeText(LoginWithImage.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(LoginWithImage.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                            }
                                        }else {
/*                                            List<Location> geofencePoints = new ArrayList<>();
                                            JSONArray geoFence = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getJSONArray("geoFance");

                                            for (int i = 0; i < geoFence.length(); i++) {
                                                JSONObject pin = geoFence.getJSONObject(i);
                                                //log oin
                                                Log.e("GeoFenceCheck", "Pin " + i + ": lat=" + pin.getDouble("lat") + ", lng=" + pin.getDouble("lng"));
                                                double lat = pin.getDouble("lat");
                                                double lng = pin.getDouble("lng");

                                                // Create a Location object for each pin
                                                Location location = new Location("");
                                                location.setLatitude(lat);
                                                location.setLongitude(lng);

                                                // Add the location to the geofencePoints list
                                                geofencePoints.add(location);
                                            }*/
/*                                            List<Location> geofencePoints = new ArrayList<>();


                                            double[][] pins = {
                                                    {23.78129335551351, 90.399823061772}, // ✅ {latitude, longitude}
                                                    {23.780356593359997, 90.399823061772},
                                                    {23.780356593359997, 90.40163266005396},
                                                    {23.78129335551351, 90.40163266005396},
                                                    {23.78129335551351, 90.399823061772}  // close the loop
                                            };
                                            for (double[] pin : pins) {
                                                Location point = new Location("");
                                                point.setLatitude(pin[0]);  // ✅ latitude
                                                point.setLongitude(pin[1]); // ✅ longitude
                                                geofencePoints.add(point);
                                            }*/
                                            //log geofencePoints
                                            List<Location> geofencePoints = new ArrayList<>();
                                            try {
                                                // Get geoFance array
                                                JSONArray geoFence = jsonObject.getJSONArray("response")
                                                        .getJSONObject(0)
                                                        .getJSONObject("user_info")
                                                        .getJSONArray("geoFance");

                                                Log.e("GeoFenceCheck", "GeoFence Points Count: " + geoFence.length());

                                                for (int i = 0; i < geoFence.length(); i++) {
                                                    JSONObject pin = geoFence.getJSONObject(i);
                                                    double lat = pin.getDouble("lat");
                                                    double lng = pin.getDouble("lng");

                                                    Log.e("GeoFenceCheck", "Pin " + i + ": lat=" + lat + ", lng=" + lng);

                                                    // Create a Location object for each pin
                                                    Location location = new Location("");
                                                    location.setLatitude(lat);
                                                    location.setLongitude(lng);

                                                    // Add the location to the geofencePoints list
                                                    geofencePoints.add(location);
                                                }

                                                // Check if the geofencePoints are populated correctly
                                                Log.e("GeoFenceCheck", "Geofence Points Size: " + geofencePoints.size());
                                            } catch (JSONException e) {
                                                Log.e("GeoFenceCheck", "Error parsing geoFance: " + e.getMessage());
                                            }

                                            Log.e("GeoFenceCheck", "Geofence Points Count: " + geofencePoints.size());
                                            boolean inside = isInsideGeofence(currentLocation, geofencePoints);
                                            if (inside) {
                                                Toast.makeText(LoginWithImage.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(LoginWithImage.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                            }
                                            Log.d("GeoFenceCheck", "Current Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                                            Log.d("GeoFenceCheck", "Inside geofence? " + inside);
                                        }
                                        /*



                                            *//*checkValidation*//*
*/

                                        //GeoFencePoints



/*                                        double[][] pins = {
                                                {23.78129335551351, 90.399823061772}, // ✅ {latitude, longitude}
                                                {23.780356593359997, 90.399823061772},
                                                {23.780356593359997, 90.40163266005396},
                                                {23.78129335551351, 90.40163266005396},
                                                {23.78129335551351, 90.399823061772}  // close the loop
                                        };
                                        for (double[] pin : pins) {
                                            Location point = new Location("");
                                            point.setLatitude(pin[0]);  // ✅ latitude
                                            point.setLongitude(pin[1]); // ✅ longitude
                                            geofencePoints.add(point);
                                        }
                                        boolean inside = isInsideGeofence(currentLocation, geofencePoints);
                                        if (inside) {
                                            Toast.makeText(LoginWithImage.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(LoginWithImage.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                        }*/
                                        login_button.setEnabled(true);


                                    } else {
                                        login_button.setEnabled(true);
                                        Toast.makeText(LoginWithImage.this, jsonObject.getJSONArray("response").getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ServiceHandlerOutlets", e.getMessage());
                    }
                } else {
                    ///  basicFunction.savePreference("end_time","20:00:00");
                    if (basicFunction.getPreference("password").equalsIgnoreCase(editPassword.getText().toString())) {
                        startActivity(new Intent(LoginWithImage.this, Dashboard.class));
                        finish();
                    } else {
                        Toast.makeText(LoginWithImage.this, "Wrong Username Or Password", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        cameraHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.REQ_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationOrAskSettings();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraHelper.handleActivityResult(requestCode, resultCode, data);
        if (requestCode == 7001) {
            fetchLocationOrAskSettings();
        }
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

    }

    @Override
    public void OnConnetivityError() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates when activity is paused
        if (locationHelper != null) locationHelper.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Request location updates if permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //getLocation();
        }
    }

private void fetchLocationOrAskSettings() {
    if (!locationHelper.hasFinePermission()) {
        locationHelper.requestFinePermission();
        return;
    }
    locationHelper.requestSingleFix(new LocationHelper.SingleFixCallback() {
        @Override
        public void onLocationReady(@NonNull Location loc) {
            latestFix = loc;
            Log.e("Location", "Fix: lat=" + loc.getLatitude() + " lon=" + loc.getLongitude()
                    + " acc=" + loc.getAccuracy());
            Toast.makeText(LoginWithImage.this, "Location ready", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(@NonNull String reason) {
            Log.e("Location", "Failed: " + reason);
            Toast.makeText(LoginWithImage.this, "Location failed: " + reason, Toast.LENGTH_LONG).show();
        }


        @Override
        public void onResolutionRequired(ResolvableApiException resolvable) {
            try {
                // ইউজারকে Location On করতে “Turn on location?” ডায়ালগ দেখাবে
                resolvable.startResolutionForResult(LoginWithImage.this, 7001);
            } catch (IntentSender.SendIntentException e) {
                onFailed("Resolution launch failed");
            }
        }
    });
}
}

