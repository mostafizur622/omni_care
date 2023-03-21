package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
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

public class SR_Attendance extends AppCompatActivity implements BasicFunctionListener {

    TextView date,checkIntime,checkouttime;
    BasicFunction bf ;
    int attendance=-1;
    Button attendance_btn,history;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    String checkout="";
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

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SR_Attendance.this, Attendance_history.class));
                finishAffinity();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SR_Attendance.this, SR_Account_Activity.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SR_Attendance.this, SR_Account_Activity.class));
                finishAffinity();
            }
        });

        bf = new BasicFunction(this,this);
        try {
            ProgressDialog dailog = CheckConnection(SR_Attendance.this,"getting Attendance Status...");
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

        attendance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attendance_btn.getText().toString().equalsIgnoreCase("Check IN")){
                    try {
                       // bf.getResponceData(URL.SET_ATTENDANCE_STATUS, String.valueOf(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","0")),101);

                        ProgressDialog dailog = CheckConnection(SR_Attendance.this,"Checking...");
                        if (dailog==null)
                            return;
                        getJAPi().SET_ATTENDANCE_STATUS(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","0"))).enqueue(new Callback<String>() {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if (attendance_btn.getText().toString().equalsIgnoreCase("Check Out")){
                    try {
                     //   bf.getResponceData(URL.SET_ATTENDANCE_STATUS, String.valueOf(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","1")),101);
                        ProgressDialog dailog = CheckConnection(SR_Attendance.this,"Checking...");
                        if (dailog==null)
                            return;
                        getJAPi().SET_ATTENDANCE_STATUS(convertTORequestdata(new JSONObject().put("so_id",bf.getPreference(SR_ID)).put("mac",bf.getPreference("mac")).put("type","1"))).enqueue(new Callback<String>() {
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
}
