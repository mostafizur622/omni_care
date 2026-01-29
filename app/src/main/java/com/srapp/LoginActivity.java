package com.srapp;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.FaceDetection.LivenessOverlayActivity.EXTRA_BASE64;
import static com.srapp.LoginImageCapture.LocationUtils.isInsideGeofence;
import static com.srapp.LoginImageCapture.LocationUtils.isWithinRadius;
import static com.srapp.Util.Constants.MIN_ORDER_NUMBER;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bxl.config.editor.BXLConfigLoader;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Difine;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.FaceDetection.FaceRecognition.MatchVerifier;
import com.srapp.FaceDetection.FaceRecognition.VerifyCallback;
import com.srapp.FaceDetection.LivenessOverlayActivity;
import com.srapp.LoginImageCapture.CameraHelper;
import com.srapp.LoginImageCapture.LocationHelper;
import com.srapp.LoginImageCapture.LoginWithImage;
import com.srapp.LoginImageCapture.SimpleFaceCaptureActivity;
import com.srapp.Util.AuthPreference;
import com.srapp.Util.JAPIClient;
import com.srapp.Util.Parent;
import com.srapp.apiService.ApiInterfaceForJava;
import com.srapp.kotlin.DataViewModel;
import com.srapp.kotlin.DataViewModelFactory;
import com.srapp.print.ParentActivity;
import com.srapp.thermalprint.async.usbdevice.UsbDataBinder;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import NewPrint.BixolonPrinter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Parent implements BasicFunctionListener, DBListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION_FINE =11 ;
    private BasicFunction basicFunction;

    TextView textDummyHintUsername;
    TextView textDummyHintPassword;
    EditText editUsername;
    EditText editPassword;
    ImageView login_button;
    int RcCount = 0;
    DBListener dbListener = this;
    Data_Source ds;
    ArrayList<String> data, bbData;
    private int portType = BXLConfigLoader.DEVICE_BUS_USB;
    private String logicalName = "SRP-E302";
    private String address = "";
    static BixolonPrinter printer;
    TextInputLayout usertextinput, passwordtextinput;
    ProgressDialog progressDialog;

    DataViewModel dataViewModel;
    ApiInterfaceForJava api;
    private CameraHelper cameraHelper;
    String imageBase64 = "";
    private LocationHelper locationHelper;
    private volatile Location latestFix;
    private static final int REQ_LIVENESS_OVERLAY = 4411;
    private static final int REQ_SIMPLE_FACE = 4412;
    ImageView showImage;
    Boolean faceMatch=false;
    public LoginActivity() {
        dataViewModel = null;
    }
    String faceVerification="0";
    private AuthPreference authPreference;
    String firebaseToken = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("DeviceName", Build.BRAND );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("DeviceVersion", Build.VERSION.BASE_OS);
        }
        showImage = findViewById(R.id.showImage);
     /*   //-----------------------------------------
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbConnection();*/
        api = JAPIClient.getClient().create(ApiInterfaceForJava.class);
        basicFunction = new BasicFunction(this, this);
        ds = new Data_Source(this, this, this);

       // printer = new BixolonPrinter(this);

        textDummyHintUsername = (TextView) findViewById(R.id.text_dummy_hint_username);
        textDummyHintPassword = (TextView) findViewById(R.id.text_dummy_hint_password);
        authPreference = new AuthPreference(getApplicationContext());
       // editUsername.setText("error");
        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);
        locationHelper = new LocationHelper(this);
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            firebaseToken = token;
                            Log.e("firebaseToken", firebaseToken);
                        } else {
                            Log.e("firebaseToken", "Token retrieval failed", task.getException());
                        }
                    }
                });

        if (!locationHelper.hasFinePermission()) {
            locationHelper.requestFinePermission();
        } else {
            fetchLocationOrAskSettings();
        }
/*        cameraHelper = new CameraHelper(this, new CameraHelper.CameraCallback() {
            @Override
            public void onImageCaptured(Bitmap bitmap, String base64String) {
                imageBase64 = base64String;
                Log.e("ImageBase64", imageBase64);
                Toast.makeText(LoginActivity.this, "Image Captured!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(LoginActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        });*/

        // Trigger the camera flow

        TextView version = findViewById(R.id.version);
        if (URL.Domain.contains("202"))
        version.setText(URL.VERSION_txt+" Local Server");
        else {
            version.setText(URL.VERSION_txt);
        }

        usertextinput = (TextInputLayout) findViewById(R.id.userTextinputLayout);
        passwordtextinput = (TextInputLayout) findViewById(R.id.passwordTextinputLayout);
        if (checkForPermission()) {
           /* TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String MAC_Number = telephonyManager.getDeviceId();
            basicFunction.savePreference("mac", MAC_Number);*/
        }

        if (!basicFunction.getPreference("sr_uname").equalsIgnoreCase("null")){

            editUsername.setText(basicFunction.getPreference("sr_uname"));
            editUsername.setEnabled(false);
        }

        if (basicFunction.getPreference("mac")==null || basicFunction.getPreference("mac").equalsIgnoreCase("null")){
            new advertizingId().execute();
        }

        dataViewModel  = new ViewModelProvider(this, new DataViewModelFactory(this.getApplication())).get(DataViewModel.class);
        // Create the observer which updates the UI.
        final Observer<String> statusObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String status) {
                Log.e("SuccessMessage", status);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
      //  dataViewModel.getSuccessStatus().observe(this, statusObserver);

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latestFix == null) {
                    Toast.makeText(getApplicationContext(), "Getting location... Please wait a moment.", Toast.LENGTH_SHORT).show();
                    fetchLocationOrAskSettings(); // আবার চেষ্টা
                    return;
                }
                if (editUsername.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Insert user name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editPassword.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Insert password", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*login_button.setEnabled(false);
                startActivity(new Intent(LoginActivity.this, Dashboard.class));
                finish();*/



                //if (basicFunction.getPreference("sales_person_id").equalsIgnoreCase("null") || basicFunction.isInternetOn()){

                JSONObject jsonObject = new JSONObject();
                try {
                    Log.e("permission", checkForPermission() + "");
                    if (checkForPermission()) {

                        String manufacturer = Build.MANUFACTURER;
                        String model = Build.MODEL;
                        String versionRelease = Build.VERSION.RELEASE;
                        String deviceInfo = manufacturer + " " + model + " (Android " + versionRelease + " - SDK "  + ")";
                        Log.e("DeviceInfo", deviceInfo);
                        basicFunction.savePreference("mac",  basicFunction.getPreference("mac"));
                        jsonObject.put(Difine.USERENAME, editUsername.getText().toString().trim());
                        jsonObject.put("password", editPassword.getText().toString().trim());
                        jsonObject.put("mac", basicFunction.getPreference("mac"));
                        jsonObject.put("version", URL.VERSION);
//                        jsonObject.put("image", imageBase64);
                        jsonObject.put("lat", latestFix.getLatitude());
                        jsonObject.put("long", latestFix.getLongitude());
                        jsonObject.put("token", firebaseToken);
                        jsonObject.put("manufacturer", manufacturer);
                        jsonObject.put("model", model);
                        jsonObject.put("versionRelease", versionRelease);

                       //  basicFunction.getResponceData(URL.Login, jsonObject.toString(), 101);
                         Log.e("map : ", jsonObject.toString());


                         ProgressDialog dailog = CheckConnection(LoginActivity.this,"Login Check...");
                         if (dailog==null)
                             return;

                        api.loginNew(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                RcCount = 101;
                                login_button.setEnabled(false);
                                dailog.dismiss();
                                Log.e("test",response.body());
                                try {

                                    JSONObject jsonObject=new JSONObject(response.body());
                                    if (jsonObject.getJSONArray("response").getJSONObject(0).getString("status").equalsIgnoreCase("1")) {
                                        basicFunction.savePreference(SR_ID,jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sales_person_id"));
                                        basicFunction.savePreference("sr_uname",editUsername.getText().toString().trim());
                                        FirebaseCrashlytics.getInstance().setUserId(basicFunction.getPreference("sr_uname"));
                                        basicFunction.savePreference("password",editPassword.getText().toString().trim());
                                        basicFunction.savePreference("office_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_id"));
                                        basicFunction.savePreference("territory_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("territory_id"));
                                        basicFunction.savePreference("store_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("store_id"));
                                        basicFunction.savePreference(SR_ID,jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sales_person_id"));
                                        basicFunction.savePreference("office_name",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_name"));
                                        basicFunction.savePreference("office_name_bn",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("name_bangla"));
                                        basicFunction.savePreference("office_address",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_address"));
                                        basicFunction.savePreference("office_address_bn",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_address_bangla"));
                                        basicFunction.savePreference("office_phone",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_phone"));
                                        basicFunction.savePreference("sr_name",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_name"));
                                        basicFunction.savePreference("db_name",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_name"));
                                        basicFunction.savePreference("db_address",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_address"));
                                        basicFunction.savePreference("db_mobile",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_mobile"));
                                        basicFunction.savePreference(MIN_ORDER_NUMBER,jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString(MIN_ORDER_NUMBER));
                                       // basicFunction.savePreference(MIN_ORDER_NUMBER,"1");


                            basicFunction.savePreference("store_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject( "user_info").getString("store_id"));
                            basicFunction.savePreference("ae_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("ae_id"));
                            basicFunction.savePreference("tso_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("tso_id"));
                            basicFunction.savePreference("db_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_id"));
                            basicFunction.savePreference("sr_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_id"));
                            basicFunction.savePreference("sr_code",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_code"));
                            basicFunction.savePreference("start_time",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("start_time"));
                            basicFunction.savePreference("end_time",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("end_time"));
                            basicFunction.savePreference("break_out_start_time",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("break_out_start_time"));
                            basicFunction.savePreference("break_in_start_time",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("break_in_start_time"));
                            basicFunction.savePreference("interval",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("interval"));
                            basicFunction.savePreference("deliveryTime",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("show_delivery_date_time"));
                            basicFunction.savePreference("roll",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("user_type"));
                            basicFunction.savePreference("saveLogin","1");



                            //check location validation type
                            String isChecking= jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("login_checking");
                            String radiusMeter= jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("radius_miter");
                            String locationType= jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("login_type");
                            /*centerLocationLogin*/
                            String loginLatStr = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getJSONArray("coordinates").optJSONObject(0).getString("lat");
                            String loginLngStr = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getJSONArray("coordinates").optJSONObject(0).getString("lng");

                            /*centerLocationLogOut*/
                            String logOutLatStr = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getJSONArray("logout_coordinates").optJSONObject(0).getString("lat");
                            String logOutLngStr = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getJSONArray("logout_coordinates").optJSONObject(0).getString("lng");
                            /*GeoFenceLocation*/
                            JSONArray geoFence = jsonObject.getJSONArray("response")
                                    .getJSONObject(0)
                                    .getJSONObject("user_info")
                                    .getJSONArray("coordinates");

                            String isPreviousImage= jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("image_url");

                            //Store the login location in the shared preferences
                            // Assuming 'myJSONArray' is your JSONArray instance
                            SharedPreferences locationPreferences = getSharedPreferences("Location", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = locationPreferences.edit();

                            editor.putString("radius", radiusMeter);
                            editor.putString("isChecking", isChecking);
                            editor.putString("centerPointLat", loginLatStr);
                            editor.putString("centerPointLong", loginLngStr);
                            editor.putString("logOutLatStr", logOutLatStr);
                            editor.putString("logOutLngStr", logOutLngStr);
                            editor.putString("location_check", locationType);
                            editor.putString("geoFence", geoFence.toString());
                            editor.putString("attendance_online", jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("attendance_online"));
                            editor.apply(); // or editor.commit();

                            faceVerification = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("face_recognition");

                            String serverTime = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("server_time");
                            int allowedTimeDifferenceInMinutes = 1;
                            boolean canLogin = checkTimeDifference(serverTime, ParentActivity.getCurrentTimeAMPM(), allowedTimeDifferenceInMinutes);
                            if (!canLogin) {
                                login_button.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "Device time is not correct. Please adjust your device time.", Toast.LENGTH_LONG).show();
                                Toast.makeText(LoginActivity.this, "Server time is "+serverTime, Toast.LENGTH_LONG).show();
                                return;
                            }
                           authPreference.setDeviceTimeChange("0");
                            if (faceVerification.equalsIgnoreCase("1")){
                                if (isPreviousImage.isEmpty()&&imageBase64.isEmpty()) {
                                    // 🔸 Step 1: কোনো reference image নেই → full normal face capture
                                        cameraHelper = new CameraHelper(LoginActivity.this, new CameraHelper.CameraCallback() {
                                            @Override
                                            public void onImageCaptured(Bitmap bitmap, String base64String) {
                                                imageBase64 = base64String;
                                                Log.d("ImageBase64", imageBase64);
                                                Toast.makeText(LoginActivity.this, "Image Captured!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onPermissionDenied() {
                                                Toast.makeText(LoginActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    // Trigger the camera flow
                                    cameraHelper.startCameraFlow();
                                    login_button.setEnabled(true);
                                    return;
                                }
                                if (imageBase64.isEmpty()){
                                    startActivityForResult(new Intent(getApplicationContext(), LivenessOverlayActivity.class), REQ_LIVENESS_OVERLAY);
                                    Toast.makeText(LoginActivity.this, "Please capture your face image first.", Toast.LENGTH_SHORT).show();
                                    login_button.setEnabled(true);
                                    return;
                                }
                                Log.e("isPreviousImage",isPreviousImage);
                                //checkPreviousImage
                                if (!isPreviousImage.isEmpty()){
                                    Bitmap liveBmp = base64ToBitmap(imageBase64);
                                    //refUrl Image getting from api
                                    String refUrl = isPreviousImage;
                                    if (refUrl == null || refUrl.isEmpty()) {
                                        setResult(Activity.RESULT_CANCELED); finish(); return;
                                    }
                                    verifyAndFinish(liveBmp, refUrl, matched -> {
                                        if (matched) {
                                            try {
                                                //*currentLocation*//*
                                                double currentLat = latestFix.getLatitude();
                                                double currentLng = latestFix.getLongitude();
                                                Location currentLocation = new Location("");
                                                currentLocation.setLatitude(currentLat);
                                                currentLocation.setLongitude(currentLng);
                                                if (isChecking.equalsIgnoreCase("2")){
//                                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                                    ds.getlastupdateddate();
                                                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                                    finish();
                                                }
                                                else if (isChecking.equalsIgnoreCase("1"))
                                                {
                                                    if (locationType.equalsIgnoreCase("1")){
                                                        Log.e("LocationMode V","Radius");
                                                        /*LoginLocation*/
                                                        Log.e("", "Login Location: " + loginLatStr + ", " + loginLngStr);
                                                        double loginLat = Double.parseDouble(loginLatStr);
                                                        double loginLng = Double.parseDouble(loginLngStr);
                                                        Location loginLocation = new Location("");
                                                        loginLocation.setLatitude(loginLat);
                                                        loginLocation.setLongitude(loginLng);
                                                        //checkValidation
                                                        boolean isAllowed = isWithinRadius(currentLocation, loginLocation, Float.parseFloat(radiusMeter));
                                                        if (isAllowed) {
                                                            Toast.makeText(LoginActivity.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
//                                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                                            ds.getlastupdateddate();
                                                            startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                                            finish();
                                                        } else {
                                                            login_button.setEnabled(true);
                                                            Toast.makeText(LoginActivity.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                                            return;
                                                        }
                                                    }
                                                    if (locationType.equalsIgnoreCase("2")){
                                                        Log.e("LocationMode V","GeoFence");
                                                        //log geofencePoints
                                                        List<Location> geofencePoints = new ArrayList<>();
                                                        try {
                                                            // Get geoFence array


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
                                                            Toast.makeText(LoginActivity.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
                                                            startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                                            finish();
//                                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                                            ds.getlastupdateddate();
                                                        } else {
                                                            login_button.setEnabled(true);
                                                            Toast.makeText(LoginActivity.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                                            return;
                                                        }
                                                        Log.d("GeoFenceCheck", "Current Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                                                        Log.d("GeoFenceCheck", "Inside geofence? " + inside);
                                                    }
                                                }
                                                else {
                                                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                                    finish();
//                                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                                    ds.getlastupdateddate();
                                                }
                                            }
                                            catch (NullPointerException e){
                                                e.printStackTrace();
                                            }
                                        }
                                        else {
                                            login_button.setEnabled(true);
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                                            builder.setCancelable(true);
                                            builder.setTitle("Face MichMatch");
                                            builder.setMessage("We can not verify your face do you want capture new image?");
                                            builder.setPositiveButton("yes",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            startActivityForResult(new Intent(getApplicationContext(), LivenessOverlayActivity.class), REQ_LIVENESS_OVERLAY);
                                                            Toast.makeText(LoginActivity.this, "Please capture your face image first.", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    });
                                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            android.app.AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    });
                                }
                                else {
                                    JSONObject srImageObject = new JSONObject();
                                    try {
                                        srImageObject.put("imageBase64",imageBase64);
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                    api.saveSrImage(convertTORequestdata(srImageObject)).enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            Log.e("imageResponse",response.body().toString());
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            dailog.dismiss();
                                        }
                                    });

                                    try {
                                        //*currentLocation*//*
                                        double currentLat = latestFix.getLatitude();
                                        double currentLng = latestFix.getLongitude();
                                        Location currentLocation = new Location("");
                                        currentLocation.setLatitude(currentLat);
                                        currentLocation.setLongitude(currentLng);
                                        if (isChecking.equalsIgnoreCase("2")){
                                            startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                            finish();
//                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                            ds.getlastupdateddate();
                                        }
                                        else if (isChecking.equalsIgnoreCase("1"))
                                        {
                                            if (locationType.equalsIgnoreCase("1")){
                                                Log.e("LocationMode W.V","Radius");
                                                /*LoginLocation*/
                                                Log.e("", "Login Location: " + loginLatStr + ", " + loginLngStr);
                                                double loginLat = Double.parseDouble(loginLatStr);
                                                double loginLng = Double.parseDouble(loginLngStr);
                                                Location loginLocation = new Location("");
                                                loginLocation.setLatitude(loginLat);
                                                loginLocation.setLongitude(loginLng);
                                                //checkValidation
                                                boolean isAllowed = isWithinRadius(currentLocation, loginLocation, Float.parseFloat(radiusMeter));
                                                if (isAllowed) {
                                                    Toast.makeText(LoginActivity.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                                    finish();
//                                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                                    ds.getlastupdateddate();
                                                } else {
                                                    login_button.setEnabled(true);
                                                    Toast.makeText(LoginActivity.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                            }
                                            if (locationType.equalsIgnoreCase("2")){
                                                Log.e("LocationMode W.V","GeoFence");
                                                //log geofencePoints
                                                List<Location> geofencePoints = new ArrayList<>();
                                                try {
                                                    // Get geoFence array


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
                                                    Toast.makeText(LoginActivity.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                                    finish();
//                                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                                    ds.getlastupdateddate();
                                                } else {
                                                    login_button.setEnabled(true);
                                                    Toast.makeText(LoginActivity.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                Log.d("GeoFenceCheck", "Current Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                                                Log.d("GeoFenceCheck", "Inside geofence? " + inside);
                                            }
                                        }
                                        else {
                                            startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                            finish();
//                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                            ds.getlastupdateddate();
                                        }
                                    }
                                    catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                }
                               // verifyAndFinish(liveBmp, refUrl);
                            }
                            else {
                                //*currentLocation*//*
                                double currentLat = latestFix.getLatitude();
                                double currentLng = latestFix.getLongitude();
                                Location currentLocation = new Location("");
                                currentLocation.setLatitude(currentLat);
                                currentLocation.setLongitude(currentLng);
                                if (isChecking.equalsIgnoreCase("2")){
                                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                    finish();
//                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                    ds.getlastupdateddate();
                                }
                                else if (isChecking.equalsIgnoreCase("1"))
                                {
                                    if (locationType.equalsIgnoreCase("1")){
                                        Log.e("LocationMode W.V","Radius");
                                        /*LoginLocation*/
                                        Log.e("", "Login Location: " + loginLatStr + ", " + loginLngStr);
                                        double loginLat = Double.parseDouble(loginLatStr);
                                        double loginLng = Double.parseDouble(loginLngStr);
                                        Location loginLocation = new Location("");
                                        loginLocation.setLatitude(loginLat);
                                        loginLocation.setLongitude(loginLng);
                                        //checkValidation
                                        boolean isAllowed = isWithinRadius(currentLocation, loginLocation, Float.parseFloat(radiusMeter));
                                        if (isAllowed) {
                                            Toast.makeText(LoginActivity.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                            finish();
//                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                            ds.getlastupdateddate();
                                        } else {
                                            login_button.setEnabled(true);
                                            Toast.makeText(LoginActivity.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    if (locationType.equalsIgnoreCase("2")){
                                        Log.e("LocationMode W.V","GeoFence");
                                        //log geofencePoints
                                        List<Location> geofencePoints = new ArrayList<>();
                                        try {
                                            // Get geoFence array


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
                                            Toast.makeText(LoginActivity.this, "Login Successfully....!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                            finish();
//                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                            ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                            ds.getlastupdateddate();
                                        } else {
                                            login_button.setEnabled(true);
                                            Toast.makeText(LoginActivity.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        Log.d("GeoFenceCheck", "Current Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                                        Log.d("GeoFenceCheck", "Inside geofence? " + inside);
                                    }
                                }
                                else {
                                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                    finish();
//                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
//                                    ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
//                                    ds.getlastupdateddate();
                                }
                            }
                            Log.e("FaceMatchLogin", String.valueOf(faceMatch));
                            Log.e("office_name",getPreference("office_name"));

                                    }
                                    else {
                                        login_button.setEnabled(true);
                                        Toast.makeText(LoginActivity.this , jsonObject.getJSONArray("response").getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
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


                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ServiceHandlerOutlets", e.getMessage());
                }
//             }else {
//                  ///  basicFunction.savePreference("end_time","20:00:00");
//                    if (basicFunction.getPreference("password").equalsIgnoreCase(editPassword.getText().toString())) {
//                        startActivity(new Intent(LoginActivity.this, Dashboard.class));
//                        finish();
//                    }else {
//                        Toast.makeText(LoginActivity.this, "Wrong Username Or Password", Toast.LENGTH_LONG).show();
//                    }
//                }
            }

        });

        editUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            textDummyHintUsername.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (editUsername.getText().length() > 0)
                        textDummyHintUsername.setVisibility(View.VISIBLE);
                    else
                        textDummyHintUsername.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Password
        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //passwordtextinput.setBackgroundResource(R.drawable.focused);
                            // Show white background behind floating label
                            textDummyHintPassword.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (editPassword.getText().length() > 0)
                        textDummyHintPassword.setVisibility(View.VISIBLE);
                    else
                        textDummyHintPassword.setVisibility(View.INVISIBLE);
                }
            }
        });




    }
    public static boolean checkTimeDifference(String serverTime, String deviceTime, int allowedMinutes) {
        Log.d("TimeCheck", "Server Time: " + serverTime + " Device Time: " + deviceTime + " Allowed Minutes: " + allowedMinutes);

        try {
            // সার্ভারের টাইম ফরম্যাট সেট করা (যদি সার্ভারের টাইম থাকে 24 ঘণ্টার ফরম্যাটে)
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // সার্ভারের টাইম UTC হিসেবে ধরা হচ্ছে
            Date serverDate = sdf.parse(serverTime);
            long serverTimeInMillis = serverDate.getTime();

            // ডিভাইস টাইম স্ট্রিংকে Date তে কনভার্ট করা
            Date deviceDate = sdf.parse(deviceTime);  // ডিভাইস টাইম স্ট্রিংকে Date-এ কনভার্ট করা
            long deviceTimeInMillis = deviceDate.getTime();  // তারপরে মিলিসেকেন্ডে কনভার্ট

            // সময়ের পার্থক্য বের করা (মিলিসেকেন্ডে)
            long timeDifference = Math.abs(deviceTimeInMillis - serverTimeInMillis);

            // মিনিটে টাইম পার্থক্য
            long allowedTimeDifferenceInMillis = allowedMinutes * 60 * 1000; // মিনিটকে মিলিসেকেন্ডে কনভার্ট করা

            // নির্ধারিত সীমার মধ্যে পার্থক্য হলে লগ ইন হবে
            if (timeDifference <= allowedTimeDifferenceInMillis) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private void verifyAndFinish(Bitmap liveBmp, String refUrl,@Nullable VerifyCallback cb) {
        // ব্যাকগ্রাউন্ডে ভারি কাজ (নেটওয়ার্ক+ML)
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean ok = false;
            try {
                MatchVerifier verifier = new MatchVerifier(this);
                ok = verifier.verify(liveBmp, refUrl);
                verifier.close();
            } catch (Exception ignored) {}

            boolean finalOk = ok;
            runOnUiThread(() -> {
                if (finalOk) {
                    // ✅ ম্যাচ — লগইন Allow
                    // চাইলে সার্ভারে সাইন-ইন API কল করুন; অথবা শুধু রেজাল্ট ফেরত দিন
                    Intent data = new Intent();
                    data.putExtra(EXTRA_BASE64, bitmapToBase64(liveBmp));
                    setResult(Activity.RESULT_OK, data);
                    Toast.makeText(this, "Face Match", Toast.LENGTH_LONG).show();
                } else {
                    // ❌ মিসম্যাচ — লগইন ব্লক
                    Toast.makeText(this, "Face mismatch. Login blocked.", Toast.LENGTH_LONG).show();
                    //setResult(Activity.RESULT_CANCELED);
                }
                if (cb != null) cb.onVerified(finalOk);
                //finish();
            });
        });
    }
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
        return Base64.encodeToString(os.toByteArray(), Base64.NO_WRAP);
    }
    private Bitmap base64ToBitmap(String b64) {
        if (b64 == null) return null;

        // যদি "data:image/jpeg;base64,..." টাইপ প্রিফিক্স থাকে, কেটে দিন
        int comma = b64.indexOf(',');
        if (comma >= 0) {
            b64 = b64.substring(comma + 1);
        }

        try {
            byte[] bytes = Base64.decode(b64.trim(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {


        /* startActivity(new Intent(LoginActivity.this, Dashboard.class));*/
    }



    @Override
    public void OnConnetivityError() {
        Toast.makeText(this,"NO Internet Connection",Toast.LENGTH_SHORT);
        login_button.setEnabled(true);

    }




    private void requestBackgroundLocationPermission() {






        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    MY_PERMISSIONS_REQUEST_LOCATION
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION_FINE
            );
        }


    }

    @Override
    public void OnLocalDBdataRetrive(final String json) {
        Log.e("Json", json);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!json.equalsIgnoreCase("done") && RcCount==101) {
                        JSONObject jsonObject = new JSONObject(json);
                        jsonObject.put("mac", basicFunction.getPreference("mac"));
                       // basicFunction.getResponceData(URL.PULL, jsonObject.toString(), 102);
                        ProgressDialog dailog = CheckConnection(LoginActivity.this,"Downloading Master Data From Server...");
                        if (dailog==null)
                            return;
                        api.PULL(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                JSONObject jsonObject= null;
                                try {
                                    jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                RcCount = 105;
                                ds.excQuery("delete  from product_history");
                                ds.excQuery("delete  from instrument_type");
                                ds.excQuery("delete  from location");
                                ds.excQuery("delete  from materials");
                                ds.excQuery("delete  from stock_info");
                                ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
                                ds.excQuery("delete  from "+ Tables.TABLE_NAME_FISCAL_YEAR);
                                ds.excQuery("delete  from "+ Tables.TABLE_Name_OFFER_TYPE);
                                ds.excQuery("delete  from "+ Tables.TABLE_OUTLET_PERMISSION);
                                ds.insertData(jsonObject.getJSONObject("response").toString(),1);
                                Log.e("102", "OnServerResponce: "+"DELETED ALL DATA---------->" );
                                //basicFunction.getResponceData(URL.Log,jsonObject.getJSONObject("response").toString(),11);
                                login_button.setEnabled(true);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });
                    }

                   else if (json.equalsIgnoreCase("done") && RcCount==102){

                       JSONObject jsonObject =  new JSONObject();
                       jsonObject.put("Territory_Id",basicFunction.getPreference("territory_id"));
                       jsonObject.put("so_id",basicFunction.getPreference("sales_person_id"));
                       jsonObject.put("mac",basicFunction.getPreference("mac"));

                       // basicFunction.getResponceData(URL.Bonus_Policy,jsonObject.toString(),103);
                        ProgressDialog dailog = CheckConnection(LoginActivity.this,"Downloading Bonus Policy Data From Server...");
                        if (dailog==null)
                            return;
                        api.Bonus_Policy(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                    progressDialog = new ProgressDialog(LoginActivity.this);
                                    progressDialog.setMessage("Saving Bonus Policy's ...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                    RcCount = 103;
                                    ds.excQuery("delete from Policy_Table");
                                    ds.excQuery("delete from Bonus_Eligible_Group");
                                    ds.excQuery("delete from Bonus_Eligible_Outlet_Categories");
                                    ds.excQuery("delete from policy_root_product");
                                    ds.excQuery("delete from policy_product_Option");
                                    ds.excQuery("delete from policy_option_price_slab");
                                    ds.excQuery("delete from policy_bonus_product");
                                    ds.excQuery("delete from product_price_other_for_slabs_v2");
                                    ds.excQuery("delete from special_group");
                                    ds.excQuery("delete from special_group_details");
                                    ds.excQuery("delete from product_combination_list");
                                    ds.excQuery("delete from Product_combination_list_details_v2");
                                    ds.excQuery("delete from product_combinations");

                                    JsonObject jsonObj =  new JsonObject();
                                    jsonObj.addProperty("territory_id", basicFunction.getPreference("territory_id"));
                                    jsonObj.addProperty("so_id", basicFunction.getPreference("sales_person_id"));
                                    jsonObj.addProperty("mac", basicFunction.getPreference("mac"));
                                    jsonObj.addProperty("last_update_date","");
                                    jsonObj.addProperty("all","1");

                                    dataViewModel.getProductCombinationV2Data(jsonObj);

                                    dataViewModel.getProductCombinationListData(jsonObj);

                                    dataViewModel.getSpecialGroupData(jsonObj);

                                    ds.insertData(jsonObject,2);

                                    Log.e("Bonus_Combination_Data", " Inserted");


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });



                    }
                   else if (json.equalsIgnoreCase("dbPolicy") && RcCount==103){
                        progressDialog.dismiss();
                       JSONObject jsonObject =  new JSONObject();
                       jsonObject.put("Territory_Id",basicFunction.getPreference("territory_id"));
                       jsonObject.put("so_id",basicFunction.getPreference("sales_person_id"));
                       jsonObject.put("mac",basicFunction.getPreference("mac"));

                       // basicFunction.getResponceData(URL.Bonus_Policy_Outlet,jsonObject.toString(),104);
                        ProgressDialog dailog = CheckConnection(LoginActivity.this,"Downloading Unit Data From Server...");
                        if (dailog==null)
                            return;
                        api.Bonus_Policy_Outlet(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    dailog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    progressDialog = new ProgressDialog(LoginActivity.this);
                                    progressDialog.setMessage("Saving Outlets Data From Server...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    RcCount = 104;
                                    ds.excQuery("delete from Bonus_Eligible_Outlets");
                                    ds.insertData(jsonObject,3);


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });


                    }
                   else if (RcCount==104){
                        progressDialog.dismiss();
                        JSONObject jsonObject =  new JSONObject();
                        jsonObject.put("Territory_Id",basicFunction.getPreference("territory_id"));
                        jsonObject.put("so_id",basicFunction.getPreference("sales_person_id"));
                        jsonObject.put("mac",basicFunction.getPreference("mac"));
                       // basicFunction.getResponceData(URL.Measurement_Unit__Details_Table,jsonObject.toString(),105);
                        ProgressDialog dailog = CheckConnection(LoginActivity.this,"Downloading Unit Data From Server...");
                        if (dailog==null)
                            return;
                        api.Measurement_Unit__Details_Table(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    dailog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    progressDialog = new ProgressDialog(LoginActivity.this);
                                    progressDialog.setMessage("Saving Unit Data From Server...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    RcCount = 105;
                                    ds.excQuery("delete from unit_details");
                                    ds.insertData(jsonObject,4);


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });


                    }
                   else if (RcCount==105){
                       // progressDialog.dismiss();


                        if (json.equalsIgnoreCase("false")){

                            basicFunction.savePreference("sales_person_id","null");
                            login_button.setEnabled(true);
                            Toast.makeText(LoginActivity.this , "Data Can't Load Properly Please try again", Toast.LENGTH_LONG).show();
                        }else {
                            startActivity(new Intent(LoginActivity.this, Dashboard.class));
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jsonexpmain",e.getMessage());
                }

                //basicFunction.getResponceData(URL.Log,createJWT("push",json), 101);
                //generateNoteOnSD(LoginActivity.this,"log.txt",json);
            }
        });

    }

    @Override
    public void OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList) {

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }


    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory() + File.separator + "log");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved" + gpxfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void checkLocationPermission() {

        Toast.makeText(this,"NO Internet Connection",Toast.LENGTH_SHORT);
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestLocationPermission();
                            }
                        });
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission();
            }
        } else {
            checkBackgroundLocation();
        }
    }

    private void checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                },
                MY_PERMISSIONS_REQUEST_LOCATION_FINE
        );
    }


    private boolean checkForPermission() {
        //  Log.e("tag", "Permission");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED   || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            return false;
        } else {
            checkLocationPermission();
            return true;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
/*
        if (requestCode == 2002) {
            cameraHelper.handleActivityResult(requestCode, resultCode, data);
        }
*/
        if (cameraHelper != null) {
            cameraHelper.handleActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQ_LIVENESS_OVERLAY) {
            if (resultCode == RESULT_OK && data != null) {
                String base64 = data.getStringExtra(EXTRA_BASE64);
                if (base64 != null) {
                    Log.e("LoginActivity", "Captured Base64: " + base64);
                    //make toast
                    Toast.makeText(this, "Face captured successfully", Toast.LENGTH_SHORT).show();
                    imageBase64 = base64;
                    //base64 image show in imageview
                    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
                   // showImage.setImageBitmap(decodedByte);
                    // এখানে আপনার পুরোনো CameraHelper-এর callback-এর মত ব্যবহার করুন
                    // উদা: সার্ভারে আপলোড, লোকাল সেভ, ইত্যাদি
                }
            } else {
                Toast.makeText(this, "Liveliness failed or canceled", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 7001) {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (cameraHelper != null) {
            cameraHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission(); // Call to proceed after permission is granted
            } else {
                // Handle the case where the permission is denied
                Toast.makeText(this, "Permission denied. Cannot proceed with location features.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class advertizingId extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            AdvertisingIdClient.Info idInfo = null;
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(LoginActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }

            String advertId = null;
            try{
                advertId = idInfo.getId();
                Log.e("advertId",advertId);
                basicFunction.savePreference("mac",advertId);
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e("advertId",e.getMessage());
            }
            return null;
        }
    }


    //USB---------Connection-------------------------------------------------------------------------
    private TextView mInfo;
    private HashMap<UsbDevice, UsbDataBinder> mHashMap = new HashMap<UsbDevice, UsbDataBinder>();
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;

    private void usbConnection() {
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbAttachReceiver , filter);
        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbDetachReceiver , filter);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        // showDevices();
    }

    BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call your method that cleans up and closes communication with the device
                    UsbDataBinder binder = mHashMap.get(device);
                    if (binder != null) {
                        binder.onDestroy();
                        mHashMap.remove(device);
                    }
                }
            }

        }
    };

    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                 showDevices();
            }
        }
    };

    private static final String ACTION_USB_PERMISSION = "com.srapp.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {

                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            Toast.makeText(LoginActivity.this, "Printer Connected", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setPositiveButton("ok", (dialog, which) -> {

                                    })
                                    .setTitle( "Printer Connected")
                                    .setCancelable(true)
                                    .show();
                        }
                    }

                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            UsbDataBinder binder = new UsbDataBinder(mUsbManager, device);
                            mHashMap.put(device, binder);
                        }
                    } else {
                        // Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private void showDevices() {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            mUsbManager.requestPermission(device, mPermissionIntent);


            mInfo.append(device.getDeviceName() + "\n");
            mInfo.append(device.getDeviceId() + "\n");
            mInfo.append(device.getDeviceProtocol() + "\n");
            mInfo.append(device.getProductId() + "\n");
            mInfo.append(device.getVendorId() + "\n");
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
                Toast.makeText(LoginActivity.this, "Location ready", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(@NonNull String reason) {
                Log.e("Location", "Failed: " + reason);
                Toast.makeText(LoginActivity.this, "Location failed: " + reason, Toast.LENGTH_LONG).show();
            }


            @Override
            public void onResolutionRequired(ResolvableApiException resolvable) {
                try {
                    // ইউজারকে Location On করতে “Turn on location?” ডায়ালগ দেখাবে
                    resolvable.startResolutionForResult(LoginActivity.this, 7001);
                } catch (IntentSender.SendIntentException e) {
                    onFailed("Resolution launch failed");
                }
            }
        });
    }
}
