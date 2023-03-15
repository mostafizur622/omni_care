package com.srapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForAttendanceHistory;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Attendance_history extends AppCompatActivity implements BasicFunctionListener {

    ImageView img;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    Button startdate, enddate;
    HashMap<String, ArrayList<String>> outlate = new HashMap<>();
    HashMap<String, ArrayList<String>> outlet_catagary = new HashMap<>();
    ListView listview;
    Data_Source db;
    TextView total_price,ec,oc,tqty;
    Double sum=0.0;
    BasicFunction bf;
    Spinner outlatesp, outlet_catagary_spinner;
    ArrayList<HashMap<String, String>> list;
    boolean onResunme=false;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    Spinner productsp;
    String product_id="0";
    ArrayList<String> productIdList = new ArrayList<>();
    ArrayList<String> productNameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_c__o_c__report);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        ec = findViewById(R.id.ec);
        oc = findViewById(R.id.oc);
        tqty = findViewById(R.id.tqty);
        productsp = findViewById(R.id.productsp);
        db = new Data_Source(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        total_price = findViewById(R.id.total_price);
        bf = new BasicFunction(this,this);
        startdate = findViewById(R.id.start_date);
        list = new ArrayList<>();
        enddate = findViewById(R.id.end_date);
        listview = findViewById(R.id.list_view);
        img = findViewById(R.id.home);
        setDateTimeField();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Attendance_history.this, Dashboard.class));
            }
        });
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });

        DataView();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Attendance_history.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Attendance_history.this, SR_Attendance.class));
                finish();
            }
        });


        /*productsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                TextView textView = (TextView)arg0.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }

                product_id = productIdList.get(arg2);

                DataView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });*/

    }


    private void setDateTimeField() {
        bf.savePreference("start_Date", bf.getCurrentDate());
        bf.savePreference("end_date", bf.getCurrentDate());
        startdate.setText(bf.getCurrentDate());
        enddate.setText(bf.getCurrentDate());


        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("start_Date", dateFormatter.format(newDate.getTime()));
                startdate.setText(bf.getPreference("start_Date"));
                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));




        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("end_date", dateFormatter.format(newDate.getTime()));
                enddate.setText(bf.getPreference("end_date"));
                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



    }


    private void DataView() {



            Log.e("DataView", "DataView");

            if (bf.isInternetOn()) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("date_from", startdate.getText().toString().trim());
                    jsonObject.put("date_to", enddate.getText().toString().trim());
                    jsonObject.put("so_id", bf.getPreference(SR_ID));
                    jsonObject.put("mac", bf.getPreference("mac"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


               // bf.getResponceData(URL.ATTENDANCE_HISTORY, jsonObject.toString(), 101);

                ProgressDialog dailog = CheckConnection(Attendance_history.this,"History Loading...");
                if (dailog==null)
                    return;
                getJAPi().ATTENDANCE_HISTORY(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            dailog.dismiss();
                            ArrayList<HashMap<String,String>> list = new ArrayList<>();
                            try {
                                for (int i = 0; jsonObject.getJSONArray("report").length() > i; i++) {
                                    HashMap<String,String> map = new HashMap<>();
                                    map.put("date",jsonObject.getJSONArray("report").getJSONObject(i).getString("date"));
                                    map.put("check_in_time",jsonObject.getJSONArray("report").getJSONObject(i).getString("check_in_time"));
                                    map.put("check_out_time",jsonObject.getJSONArray("report").getJSONObject(i).getString("check_out_time"));
                                    list.add(map);
                                }
                            }catch (Exception e){

                            }
                            AdapterForAttendanceHistory adapterForAttendanceHistory = new AdapterForAttendanceHistory(Attendance_history.this,list);
                            listview.setAdapter(adapterForAttendanceHistory);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });


            } else {
                Toast.makeText(this, "NO Internet Connection", Toast.LENGTH_LONG).show();
            }




    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {


    }

    @Override
    public void OnConnetivityError() {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( Attendance_history.this, SR_Attendance.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
