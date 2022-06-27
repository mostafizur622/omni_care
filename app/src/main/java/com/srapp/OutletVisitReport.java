package com.srapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForOrderSummery;
import com.srapp.Adapter.AdapterForOutletVisitReport;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.srapp.Db_Actions.Tables.SR_ID;

public class OutletVisitReport extends AppCompatActivity implements BasicFunctionListener {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    ListView listview;
    Button startdate, enddate;
    BasicFunction bf;
    boolean onstart;
    ImageView homeBtn,backBtn;
    ArrayList<HashMap<String,String>> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_visit_report);
        startdate = findViewById(R.id.start_date);
        enddate = findViewById(R.id.end_date);
        homeBtn = findViewById(R.id.home);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        listview = findViewById(R.id.list_view);
        list = new ArrayList<>();
        backBtn = findViewById(R.id.back);
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        TextView userIdTV = findViewById(R.id.user_txt_view);
        TextView titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( OutletVisitReport.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( OutletVisitReport.this, Reports_Activity.class));
                finish();
            }
        });
        bf = new BasicFunction(this,this);
        setDateTimeField();
        DataView();
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
                if (onstart)
                    DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));






        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("end_date", dateFormatter.format(newDate.getTime()));
                enddate.setText(bf.getPreference("end_date"));
                if (onstart) {
                    DataView();
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


	       /* Intent idn = new Intent(SO_TargetActivity.this, SO_TargetActivity.class);
			 startActivity(idn);
			 finish();*/
    }

    private void DataView() {

        if (bf.isInternetOn()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("date_to",  bf.getPreference("end_date"));
                jsonObject.put("date_from", bf.getPreference("start_Date"));
                jsonObject.put("so_id", bf.getPreference(SR_ID));
                jsonObject.put("mac", bf.getPreference("mac"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("JSON",jsonObject.toString());



            bf.getResponceData(URL.OUTLET_VISIT_REPORT, jsonObject.toString(), 101);
        } else {
            Toast.makeText(this, "NO Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        list.clear();
        onstart=true;
        if (i==101){
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("report_data");
                for (int j = 0 ; j<jsonArray.length(); j++ ){

                        HashMap<String, String> map = new HashMap<>();
                        map.put("route", jsonArray.getJSONObject(j).getString("route"));
                        map.put("total_outlet", jsonArray.getJSONObject(j).getString("total_outlet"));
                        map.put("total_visited", jsonArray.getJSONObject(j).getString("total_visited"));
                        map.put("total_ec", jsonArray.getJSONObject(j).getString("total_ec"));
                        list.add(map);


                }

                AdapterForOutletVisitReport adapterForOrderSummery = new AdapterForOutletVisitReport(this,list);
                listview.setAdapter(adapterForOrderSummery);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("error",e.getMessage());
            }

        }

    }

    @Override
    public void OnConnetivityError() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( OutletVisitReport.this, Reports_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}