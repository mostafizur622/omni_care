package com.srapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.LoginImageCapture.LocationHelper;
import com.srapp.Util.AuthPreference;
import com.srapp.Util.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;
import static com.srapp.LoginImageCapture.LocationUtils.isInsideGeofence;
import static com.srapp.LoginImageCapture.LocationUtils.isWithinRadius;
import static com.srapp.print.newprint.App.getContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SR_Attendance extends ParentActivity implements BasicFunctionListener {

    TextView date,checkIntime,checkouttime;
    BasicFunction bf ;
    int attendance=-1;
    Button attendance_btn,history;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    String checkout="";
    String radiusMeter;
    String is_geofence_radius;
    String loginLatStr;
    String loginLngStr;
    String locationType;
    String isChecking;
    private LocationHelper locationHelper;
    private volatile Location latestFix;
    JSONArray geoFenceArray;
    private AuthPreference authPreference;
    String currentDate="";
    String serverTime="";
    String currentTime="";
    String loginFacility="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_r__attendance);
        date = findViewById(R.id.date);
        checkIntime = findViewById(R.id.checkIntime);
        history = findViewById(R.id.history);
        checkouttime = findViewById(R.id.checkouttime);
        attendance_btn = findViewById(R.id.attendance);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        authPreference = new AuthPreference(getApplicationContext());
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        }
        date.setText(currentDate);

        locationHelper = new LocationHelper(this);
        if (!locationHelper.hasFinePermission()) {
            locationHelper.requestFinePermission();
        } else {
            fetchLocationOrAskSettings();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("Location", Context.MODE_PRIVATE);
        String geoFenceString = sharedPreferences.getString("geoFence", null);
        radiusMeter = sharedPreferences.getString("radius", null);
        isChecking = sharedPreferences.getString("isChecking", null);
        is_geofence_radius = sharedPreferences.getString("location_type", null);
        loginLatStr = sharedPreferences.getString("centerPointLat", null);
        loginLngStr = sharedPreferences.getString("centerPointLong", null);
        locationType = sharedPreferences.getString("location_check", null);
        loginFacility = sharedPreferences.getString("attendance_online", null);
        //print all value
//        Log.e("geoFence", "Radius: " + radiusMeter);
//        Log.e("geoFence", "isChecking: " + is_geofence_radius);
//        Log.e("geoFence", "loginLatStr: " + loginLatStr);
//        Log.e("geoFence", "loginLngStr: " + loginLngStr);
//        Log.e("geoFence", "location_check: " + locationType);
//        Log.e("geoFence", "geoFenceString: " + geoFenceString);

        if (geoFenceString != null) {
            try {
                geoFenceArray = new JSONArray(geoFenceString);
                Log.e("geoFenceString",geoFenceString.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authPreference.clearAllPreferences();
                startActivity(new Intent( SR_Attendance.this, Attendance_history.class));
                finishAffinity();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SR_Attendance.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SR_Attendance.this, Dashboard.class));
                finishAffinity();
            }
        });

        bf = new BasicFunction(this,this);

        if (loginFacility.equalsIgnoreCase("1")){
                    try {
            ProgressDialog dailog = CheckConnection(SR_Attendance.this,"Getting Attendance Status...");
            if (dailog==null)
                return;
            getJAPi().GET_ATTENDANCE_STATUS(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")))).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    try {
                        dailog.dismiss();
                        JSONObject jsonObject = new JSONObject(response.body());
                        attendance = jsonObject.getJSONObject("res").getInt("status");
                        date.setText(jsonObject.getJSONObject("res").getString("date"));

                        if (attendance==0){
                            attendance_btn.setText("Check IN");
                        }else if (attendance==1 && jsonObject.getJSONObject("res").getString("check_out").equalsIgnoreCase("0")){
                            checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in"));
                            attendance_btn.setText("Check Out");
                        }else if (attendance==1 && !jsonObject.getJSONObject("res").getString("check_out").equalsIgnoreCase("0")){
                            checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in"));
                            //attendance_btn.setEnabled(false);
                            attendance_btn.setText("Check Out");
                            checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out"));
                        }
                    }catch (JSONException e){

                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dailog.dismiss();
                }
            });

          //  bf.getResponceData(URL.GET_ATTENDANCE_STATUS, String.valueOf(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac"))),100);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        }else {
            if (authPreference.getCheckInDate().equals(currentDate) && authPreference.getCheckInStatus().equalsIgnoreCase("1")){
                checkIntime.setText(authPreference.getInTime());
                attendance_btn.setText("Check Out");
                if (authPreference.getCheckOutStatus().equalsIgnoreCase("2")){
                    checkouttime.setText(authPreference.getOutTime());
                   // attendance_btn.setEnabled(false);
                }
            }else {
                attendance_btn.setText("Check IN");
            }
        }


        attendance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latestFix == null) {
                    Toast.makeText(getApplicationContext(), "Getting location... Please wait a moment.", Toast.LENGTH_SHORT).show();
                    fetchLocationOrAskSettings(); // আবার চেষ্টা
                    return;
                }
                //checkServerTime
                if (attendance_btn.getText().toString().equalsIgnoreCase("Check IN")){
                    try {
                       // bf.getResponceData(URL.SET_ATTENDANCE_STATUS, String.valueOf(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","0")),101);
                        //checkLocationValidation
                        //*currentLocation*//*
                        double currentLat = latestFix.getLatitude();
                        double currentLng = latestFix.getLongitude();
                        Location currentLocation = new Location("");
                        currentLocation.setLatitude(currentLat);
                        currentLocation.setLongitude(currentLng);
                        if (isChecking.equalsIgnoreCase("2")){
                            Log.e("LocationCheck","Attendance");
                            if (locationType.equalsIgnoreCase("1")){
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
                                    if (authPreference.getDeviceTimeChange().equalsIgnoreCase("1")){
                                        Toast.makeText(getContext(), "Your device time has been changed. Please contact with admin or login again.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    if (loginFacility.equalsIgnoreCase("0")){
                                        authPreference.setCurrentDate(currentDate);
                                        authPreference.setCheckInStatus("1");
                                        authPreference.setCheckOutStatus("0");
                                        authPreference.setInTime(currentTime);
                                        checkIntime.setText(authPreference.getInTime());
                                        attendance_btn.setText("Check Out");
                                        authPreference.setInLat(String.valueOf(currentLat));
                                        authPreference.setInLong(String.valueOf(currentLng));
                                        authPreference.setPendingAttendance("1");
                                        Toast.makeText(getContext(), "Check in Success...", Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {
                                        if (authPreference.getDeviceTimeChange().equalsIgnoreCase("1")){
                                            Toast.makeText(getContext(), "Your device time has been changed. Please contact with admin or login again.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        try {
                                            ProgressDialog dailog = CheckConnection(SR_Attendance.this,"Checking...");
                                            if (dailog==null)
                                                return;
                                            getJAPi().SET_ATTENDANCE_STATUS(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","0").put("lat",latestFix.getLatitude()).put("long",latestFix.getLongitude()))).enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response.body());
                                                        dailog.dismiss();
                                                        date.setText(jsonObject.getJSONObject("res").getString("date"));
                                                        checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in_time"));
                                                        if (jsonObject.getJSONObject("res").has("check_out_time")) {
                                                            if (jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                                                attendance_btn.setText("Check Out");
                                                            }

                                                            if (!jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                                                checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out_time"));
                                                                //attendance_btn.setEnabled(false);
                                                            }
                                                        }else {
                                                            attendance_btn.setText("Check Out");
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
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }

                                } else {
                                    Toast.makeText(SR_Attendance.this, "You are not allowed to attendance from this location.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            if (locationType.equalsIgnoreCase("2")){
                                //log geofencePoints
                                List<Location> geofencePoints = new ArrayList<>();
                                try {
                                    // Get geoFence array


                                    Log.e("GeoFenceCheck", "GeoFence Points Count: " + geoFenceArray.length());

                                    for (int i = 0; i < geoFenceArray.length(); i++) {
                                        JSONObject pin = geoFenceArray.getJSONObject(i);
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
                                    if (authPreference.getDeviceTimeChange().equalsIgnoreCase("1")){
                                        Toast.makeText(getContext(), "Your device time has been changed. Please contact with admin or login again.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    if (loginFacility.equalsIgnoreCase("0")){
                                        authPreference.setCurrentDate(currentDate);
                                        authPreference.setCheckInStatus("1");
                                        authPreference.setCheckOutStatus("0");
                                        authPreference.setInTime(currentTime);
                                        checkIntime.setText(authPreference.getInTime());
                                        attendance_btn.setText("Check Out");
                                        authPreference.setInLat(String.valueOf(currentLat));
                                        authPreference.setInLong(String.valueOf(currentLng));
                                        authPreference.setPendingAttendance("1");
                                        Toast.makeText(getContext(), "Check in Success...", Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {
                                        if (authPreference.getDeviceTimeChange().equalsIgnoreCase("1")){
                                            Toast.makeText(getContext(), "Your device time has been changed. Please contact with admin or login again.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        try {
                                            ProgressDialog dailog = CheckConnection(SR_Attendance.this,"Checking...");
                                            if (dailog==null)
                                                return;
                                            getJAPi().SET_ATTENDANCE_STATUS(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","0").put("lat",latestFix.getLatitude()).put("long",latestFix.getLongitude()))).enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response.body());
                                                        dailog.dismiss();
                                                        date.setText(jsonObject.getJSONObject("res").getString("date"));
                                                        checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in_time"));
                                                        if (jsonObject.getJSONObject("res").has("check_out_time")) {
                                                            if (jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                                                attendance_btn.setText("Check Out");
                                                            }

                                                            if (!jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                                                checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out_time"));
                                                                //attendance_btn.setEnabled(false);
                                                            }
                                                        }else {
                                                            attendance_btn.setText("Check Out");
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
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }

                                } else {

                                    Toast.makeText(SR_Attendance.this, "You are not allowed to login from this location.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Log.d("GeoFenceCheck", "Current Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                                Log.d("GeoFenceCheck", "Inside geofence? " + inside);
                            }
                            return;
                        }

                        ProgressDialog dailog = CheckConnection(SR_Attendance.this,"Checking...");
                        if (dailog==null)
                            return;
                        getJAPi().SET_ATTENDANCE_STATUS(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","0").put("lat",latestFix.getLatitude()).put("long",latestFix.getLongitude()))).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                    if (jsonObject.getJSONObject("res").getString("status").equalsIgnoreCase("1")){
                                        authPreference.setCurrentDate(currentDate);
                                        authPreference.setCheckInStatus("1");
                                        authPreference.setCheckOutStatus("0");
                                        authPreference.setInTime(currentTime);
                                        checkIntime.setText(authPreference.getInTime());
                                        attendance_btn.setText("Check Out");
                                        authPreference.setInLat(String.valueOf(currentLat));
                                        authPreference.setInLong(String.valueOf(currentLng));
                                        Toast.makeText(getContext(), "Check in Success...", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getContext(), jsonObject.getJSONObject("res").getString("msg"), Toast.LENGTH_SHORT).show();
                                    }
//                                    date.setText(jsonObject.getJSONObject("res").getString("date"));
//                                    checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in_time"));
//                                    if (jsonObject.getJSONObject("res").has("check_out_time")) {
//                                        if (jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
//
//                                        }
//
//                                        if (!jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
//                                            checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out_time"));
//                                            //attendance_btn.setEnabled(false);
//                                        }
//                                    }else {
//                                        attendance_btn.setText("Check Out");
//                                    }

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
                    }
                }
                else if (attendance_btn.getText().toString().equalsIgnoreCase("Check Out")){
                    Log.e("deviceTimeChange",authPreference.getDeviceTimeChange());
                    if (authPreference.getDeviceTimeChange().equalsIgnoreCase("1")){
                        Toast.makeText(getContext(), "Your device time has been changed. Please contact with admin or login again.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (loginFacility.equalsIgnoreCase("0")){
                        authPreference.setCheckOutStatus("2");
                        authPreference.setOutTime(currentTime);
                        checkIntime.setText(authPreference.getInTime());
                        checkouttime.setText(currentTime);
//                        //attendance_btn.setEnabled(false);
                        authPreference.setOutLat(String.valueOf(latestFix.getLatitude()));
                        authPreference.setOutLong(String.valueOf(latestFix.getLongitude()));
                        authPreference.setPendingAttendance("1");
                        Toast.makeText(getContext(), "Check out Success...", Toast.LENGTH_SHORT).show();
                    }else {
                        if (authPreference.getDeviceTimeChange().equalsIgnoreCase("1")){
                            Toast.makeText(getContext(), "Your device time has been changed. Please contact with admin or login again.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            //   bf.getResponceData(URL.SET_ATTENDANCE_STATUS, String.valueOf(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","1")),101);
                            ProgressDialog dailog = CheckConnection(SR_Attendance.this,"Checking...");
                            if (dailog==null)
                                return;
                            getJAPi().SET_ATTENDANCE_STATUS(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","1").put("lat",latestFix.getLatitude()).put("long",latestFix.getLongitude()))).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        dailog.dismiss();
                                        date.setText(jsonObject.getJSONObject("res").getString("date"));
                                        checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in_time"));
                                        if (jsonObject.getJSONObject("res").has("check_out_time")) {
                                            if (jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                                attendance_btn.setText("Check Out");
                                            }

                                            if (!jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                                checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out_time"));
                                                //attendance_btn.setEnabled(false);
                                            }
                                        }else {
                                            attendance_btn.setText("Check Out");
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
                        catch (JSONException e) {
                            e.printStackTrace();


                        }
                    }

                }
            }
        });


    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

        try {
        if (RequestCode==100){

                attendance = jsonObject.getJSONObject("res").getInt("status");
                date.setText(jsonObject.getJSONObject("res").getString("date"));

            if (attendance==0){
                attendance_btn.setText("Check IN");
            }else if (attendance==1 && jsonObject.getJSONObject("res").getString("check_out").equalsIgnoreCase("0")){
                checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in"));
                attendance_btn.setText("Check Out");
            }else if (attendance==1 && !jsonObject.getJSONObject("res").getString("check_out").equalsIgnoreCase("0")){
                checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in"));
                //attendance_btn.setEnabled(false);
                attendance_btn.setText("Check Out");
                checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out"));
            }

        }else {
            date.setText(jsonObject.getJSONObject("res").getString("date"));
            checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in_time"));
            if (jsonObject.getJSONObject("res").has("check_out_time")) {
                if (jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                    attendance_btn.setText("Check Out");
                }

                if (!jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                    checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out_time"));
                    //attendance_btn.setEnabled(false);
                }
            }else {
                attendance_btn.setText("Check Out");
            }


        }

        } catch (JSONException e) {
            e.printStackTrace();

            Log.e("jsecp",e.getMessage());
        }

    }

    @Override
    public void OnConnetivityError() {

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
                Toast.makeText(SR_Attendance.this, "Location ready", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(@NonNull String reason) {
                Log.e("Location", "Failed: " + reason);
                Toast.makeText(SR_Attendance.this, "Location failed: " + reason, Toast.LENGTH_LONG).show();
            }


            @Override
            public void onResolutionRequired(ResolvableApiException resolvable) {
                try {
                    // ইউজারকে Location On করতে “Turn on location?” ডায়ালগ দেখাবে
                    resolvable.startResolutionForResult(SR_Attendance.this, 7001);
                } catch (IntentSender.SendIntentException e) {
                    onFailed("Resolution launch failed");
                }
            }
        });
    }
}
