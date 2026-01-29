package com.srapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.LoginImageCapture.LocationHelper;
import com.srapp.Util.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreakTimeManagement extends ParentActivity implements BasicFunctionListener {

    TextView date,checkIntime,checkouttime;
    BasicFunction bf ;
    int attendance=-1;
    Button attendance_btn,history;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    String checkout="";
    private LocationHelper locationHelper;
    private volatile Location latestFix;
    EditText remarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_time_managment);
        date = findViewById(R.id.date);
        checkIntime = findViewById(R.id.checkIntime);
        history = findViewById(R.id.history);
        checkouttime = findViewById(R.id.checkouttime);
        attendance_btn = findViewById(R.id.attendance);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        locationHelper = new LocationHelper(this);
        remarks = findViewById(R.id.remarks);
        if (!locationHelper.hasFinePermission()) {
            locationHelper.requestFinePermission();
        } else {
            fetchLocationOrAskSettings();
        }
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( BreakTimeManagement.this, Attendance_history.class));
                finishAffinity();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( BreakTimeManagement.this, SR_Account_Activity.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( BreakTimeManagement.this, Dashboard.class));
                finishAffinity();
            }
        });

        bf = new BasicFunction(this,this);
        try {
            ProgressDialog dailog = CheckConnection(BreakTimeManagement.this,"Getting Attendance Status...");
            if (dailog==null)
                return;
            getJAPi().GET_ATTENDANCE_STATUS_BK(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")))).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    try {
                        dailog.dismiss();
                        JSONObject jsonObject = new JSONObject(response.body());
                        attendance = jsonObject.getJSONObject("res").getInt("status");
                        date.setText(jsonObject.getJSONObject("res").getString("date"));

                        if (attendance==0){
                            attendance_btn.setText("Break Start");
                        }else if (attendance==1 && jsonObject.getJSONObject("res").getString("check_out").equalsIgnoreCase("0")){
                            checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in"));
                            attendance_btn.setText("Break End");
                        }else if (attendance==1 && !jsonObject.getJSONObject("res").getString("check_out").equalsIgnoreCase("0")){
                            checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in"));
                            //attendance_btn.setEnabled(false);
                            attendance_btn.setText("Break End");
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

        attendance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latestFix == null) {
                    Toast.makeText(getApplicationContext(), "Getting location... Please wait a moment.", Toast.LENGTH_SHORT).show();
                    fetchLocationOrAskSettings();
                    return;
                }
                if (attendance_btn.getText().toString().equalsIgnoreCase("Break Start")){
                    try {
                        // bf.getResponceData(URL.SET_ATTENDANCE_STATUS, String.valueOf(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","0")),101);

                        ProgressDialog dailog = CheckConnection(BreakTimeManagement.this,"Checking...");
                        if (dailog==null)
                            return;
                        getJAPi().SET_ATTENDANCE_STATUS_Bk(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","0").
                                put("lat",latestFix.getLatitude()).
                                put("note",remarks.getText().toString().trim()).
                                put("long",latestFix.getLongitude()))).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                    String status= jsonObject.getJSONObject("res").getString("status");
                                    String msg= jsonObject.getJSONObject("res").getString("msg");
                                    if (status.equalsIgnoreCase("1")){
                                        date.setText(jsonObject.getJSONObject("res").getString("date"));
                                        checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in_time"));
                                        if (jsonObject.getJSONObject("res").has("check_out_time")) {
                                            if (jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                                remarks.getText().clear();
                                                attendance_btn.setText("Break End");
                                            }

                                            if (!jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                                checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out_time"));
                                                //attendance_btn.setEnabled(false);
                                            }
                                        }else {
                                            remarks.getText().clear();
                                            attendance_btn.setText("Break End");
                                        }
                                    }

                                    Toast.makeText(BreakTimeManagement.this, msg, Toast.LENGTH_SHORT).show();


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
                }else if (attendance_btn.getText().toString().equalsIgnoreCase("Break End")){
                    try {
                        //   bf.getResponceData(URL.SET_ATTENDANCE_STATUS, String.valueOf(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","1")),101);
                        ProgressDialog dailog = CheckConnection(BreakTimeManagement.this,"Checking...");
                        if (dailog==null)
                            return;
                        getJAPi().SET_ATTENDANCE_STATUS_Bk(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","1").
                                put("lat",latestFix.getLatitude()).
                                put("note",remarks.getText().toString().trim()).
                                put("long",latestFix.getLongitude()))).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                    String msg= jsonObject.getJSONObject("res").getString("msg");
                                    date.setText(jsonObject.getJSONObject("res").getString("date"));
                                    checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in_time"));
                                    if (jsonObject.getJSONObject("res").has("check_out_time")) {
                                        if (jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                            remarks.getText().clear();
                                            attendance_btn.setText("Break End");
                                        }

                                        if (!jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                                            checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out_time"));
                                            //attendance_btn.setEnabled(false);
                                        }
                                    }else {
                                        remarks.getText().clear();
                                        attendance_btn.setText("Break End");
                                    }
                                    Toast.makeText(BreakTimeManagement.this, msg, Toast.LENGTH_SHORT).show();
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
                    attendance_btn.setText("Break Start");
                }else if (attendance==1 && jsonObject.getJSONObject("res").getString("check_out").equalsIgnoreCase("0")){
                    checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in"));
                    attendance_btn.setText("Break End");
                }else if (attendance==1 && !jsonObject.getJSONObject("res").getString("check_out").equalsIgnoreCase("0")){
                    checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in"));
                    //attendance_btn.setEnabled(false);
                    attendance_btn.setText("Break End");
                    checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out"));
                }

            }else {
                date.setText(jsonObject.getJSONObject("res").getString("date"));
                checkIntime.setText(jsonObject.getJSONObject("res").getString("check_in_time"));
                if (jsonObject.getJSONObject("res").has("check_out_time")) {
                    if (jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                        attendance_btn.setText("Break End");
                    }

                    if (!jsonObject.getJSONObject("res").getString("check_out_time").equalsIgnoreCase("0")) {
                        checkouttime.setText(jsonObject.getJSONObject("res").getString("check_out_time"));
                        //attendance_btn.setEnabled(false);
                    }
                }else {
                    attendance_btn.setText("Break End");
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
                Toast.makeText(BreakTimeManagement.this, "Location ready", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(@NonNull String reason) {
                Log.e("Location", "Failed: " + reason);
                Toast.makeText(BreakTimeManagement.this, "Location failed: " + reason, Toast.LENGTH_LONG).show();
            }


            @Override
            public void onResolutionRequired(ResolvableApiException resolvable) {
                try {
                    // ইউজারকে Location On করতে “Turn on location?” ডায়ালগ দেখাবে
                    resolvable.startResolutionForResult(BreakTimeManagement.this, 7001);
                } catch (IntentSender.SendIntentException e) {
                    onFailed("Resolution launch failed");
                }
            }
        });
    }
}