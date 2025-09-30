package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.SpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import com.srapp.Adapter.LeaveListAdapter;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.JAPIClient;
import com.srapp.Util.ParentActivity;
import com.srapp.apiService.ApiInterfaceForJava;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveApply extends AppCompatActivity {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    Button start_date,end_date;
    Spinner route;
    ArrayList<String> ThanaID=new ArrayList<String>();
    ArrayList<String> ThanaName=new ArrayList<String>();
    Button apply;
    String _typeId;
    ApiInterfaceForJava api;
    EditText remarks;
    ImageView homeBtn,backBtn;
    TextView userIdTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_apply);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        route= findViewById(R.id.leave_type);
        start_date= findViewById(R.id.start_date);
        end_date= findViewById(R.id.end_date);
        userIdTV = findViewById(R.id.user_txt_view);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        api = JAPIClient.getClient().create(ApiInterfaceForJava.class);
        remarks=findViewById(R.id.remarks);
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( LeaveApply.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( LeaveApply.this, LeaveList.class));
                finishAffinity();
            }
        });
        setDateTimeField();
        routeSpinnerSetup();
        route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                _typeId = ThanaID.get(arg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //TODO Auto-generated method stub

            }
        });
        JSONObject jsonObject = new JSONObject();
        ProgressDialog dailog = CheckConnection(LeaveApply.this,"Getting Leave Type...");
        if (dailog==null)
            return;
        api.leaveType(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    JSONArray jsonArray=jsonObject.getJSONArray("leaves_types");
                    for (int k =0; k<jsonArray.length();k++) {
                        JSONObject dataObject = jsonArray.getJSONObject(k);
                        ThanaID.add(dataObject.getString("id"));
                        ThanaName.add(dataObject.getString("name"));
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
        apply =findViewById(R.id.newApply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("leaveType",_typeId);
                Log.e("StartDate",start_date.getText().toString().trim());
                Log.e("EndDate",end_date.getText().toString().trim());
                if (_typeId.equals("0")){
                    Toast.makeText(LeaveApply.this, "Please Select Leave Type", Toast.LENGTH_SHORT).show();
                }
                else if (start_date.getText().toString().trim().equalsIgnoreCase("Pick start date")){
                    Toast.makeText(LeaveApply.this, "Please Select Start Date", Toast.LENGTH_SHORT).show();

                }
                else if (end_date.getText().toString().trim().equalsIgnoreCase("Pick end date")){
                    Toast.makeText(LeaveApply.this, "Please Select End Date", Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("apply_date", ParentActivity.getCurrentDate());
                        jsonObject.put("leave_type", _typeId);
                        jsonObject.put("start_date", start_date.getText().toString().trim());
                        jsonObject.put("end_date", end_date.getText().toString().trim());
                        jsonObject.put("remarks", remarks.getText().toString().trim());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    Log.e("applyLeaveRequest",jsonObject.toString());
                    ProgressDialog dailog = CheckConnection(LeaveApply.this,"Please wait...");
                    if (dailog==null)
                        return;
                    api.leaveCreate(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            JSONObject jsonObject= null;
                            try {
                                jsonObject = new JSONObject(response.body());
                                dailog.dismiss();
                                if (jsonObject.getJSONObject("res").getString("status").equalsIgnoreCase("1")) {
                                    Toast.makeText(LeaveApply.this, jsonObject.getJSONObject("res").getString("message"), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LeaveApply.this, LeaveList.class));
                                    finishAffinity();
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
        });


    }

    private void setDateTimeField() {



        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                start_date.setText(dateFormatter.format(newDate.getTime()));

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

//        fromDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
//        fromDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                end_date.setText(dateFormatter.format(newDate.getTime()));

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

//        toDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
//        toDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
    }

    private void routeSpinnerSetup()
    {
        int position = 0;
        ThanaID.clear();
        ThanaName.clear();
        ThanaID.add("0");
        ThanaName.add("ALL");

            SpinnerAdapter dataAdapter = new SpinnerAdapter(this, R.layout
                    .spinner_item, ThanaName);
            route.setAdapter(dataAdapter);
            route.setSelection(position);

        }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( LeaveApply.this, LeaveList.class));
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

}