package com.srapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.URL;
import com.srapp.Model.SR_Account;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Outof_Plan_Visit extends AppCompatActivity implements BasicFunctionListener {
    Spinner routesp, marketSpinner;
    ArrayList<String> Route_id = new ArrayList<String>();
    ArrayList<String> Route_name = new ArrayList<String>();
    ArrayList<String> MarketID = new ArrayList<String>();
    ArrayList<String> MarketName = new ArrayList<String>();
    Data_Source db;
    Button date,saveBtn;
    BasicFunction bf;
    ImageView homeBtn,backBtn;
    private DatePickerDialog DatePickerDialog;
    private SimpleDateFormat dateFormatter, dateFormatter2;
    String route_id, market_id,  datet;
    EditText remarks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outof__plan__visit);
        db = new Data_Source(this);
        bf = new BasicFunction(this, this);
        TextView user_txt_view;
        user_txt_view = findViewById(R.id.user_txt_view);
        user_txt_view.setText(bf.getPreference(SR_ID));
        routesp = findViewById(R.id.thana_spinner);
        saveBtn = findViewById(R.id.sendMessageBtn);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        remarks = findViewById(R.id.remarks);
        date = findViewById(R.id.date);
        marketSpinner = findViewById(R.id.market_spinner);
        dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        date.setText(bf.getCurrentDate());
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Outof_Plan_Visit.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Outof_Plan_Visit.this, SR_Account_Activity.class));
                finish();
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("start_Date", dateFormatter.format(newDate.getTime()));
                date.setText(bf.getPreference("start_Date"));
                datet = dateFormatter2.format(newDate.getTime());

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


     /*   ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, );*/
        RouteParse();

        routesp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                route_id = Route_id.get(position);
                MarketParse(route_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        marketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                market_id = MarketID.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.show();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("visit_date",datet);
                    jsonObject.put(SR_ID,bf.getPreference(SR_ID));
                    jsonObject.put("route_id",route_id);
                    jsonObject.put("market_id",market_id);
                    jsonObject.put("mac",bf.getPreference("mac"));
                    jsonObject.put("remarks",remarks.getText().toString().trim());
                  //  bf.getResponceData(URL.OUT_OF_PLAN_VISIT,jsonObject.toString(),100);

                    ProgressDialog dailog = CheckConnection(Outof_Plan_Visit.this,"Get Plan...");
                    if (dailog==null)
                        return;
                    getJAPi().OUT_OF_PLAN_VISIT(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                dailog.dismiss();
                                JSONObject jsonObject1 = jsonObject.getJSONObject("visit_plan");
                                Toast.makeText(Outof_Plan_Visit.this,jsonObject1.getString("message"),Toast.LENGTH_SHORT).show();
                                Intent ii = new Intent(Outof_Plan_Visit.this, SR_Account_Activity.class);
                                startActivity(ii);
                                finish();

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
        });


    }

    private void RouteParse() {
        Route_id.clear();
        Route_name.clear();
        Cursor c = db.rawQuery("SELECT * FROM route ORDER BY route_name ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Thana_Id = c.getString(c.getColumnIndex("route_id"));
                    String Thana_Name = c.getString(c.getColumnIndex("route_name"));

                    Route_id.add(Thana_Id);
                    Route_name.add(Thana_Name);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Outof_Plan_Visit.this, R.layout.spinner_text, Route_name);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            routesp.setAdapter(dataAdapter);

        }

    }

    private void MarketParse(String Route_ID) {
        MarketID.clear();
        MarketName.clear();
        Cursor c = db.rawQuery("SELECT * FROM markets where route_id=" + "'" + Route_ID + "' and is_active='1' ORDER BY market_name COLLATE NOCASE ASC");

        Log.e("querymarket", "SELECT * FROM markets where route_id=" + "'" + Route_ID + "' and is_active!='1' ORDER BY market_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Market_Id = c.getString(c.getColumnIndex("market_id"));
                    String Market_Name = c.getString(c.getColumnIndex("market_name"));

                    MarketID.add(Market_Id);
                    MarketName.add(Market_Name);


                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Outof_Plan_Visit.this, R.layout.spinner_text, MarketName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            marketSpinner.setAdapter(dataAdapter);


        }

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {


    }

    @Override
    public void OnConnetivityError() {

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent idd = new Intent(Outof_Plan_Visit.this, SR_Account_Activity.class);
            startActivity(idd);
            finish();


        }
        return super.onKeyDown(keyCode, event);

    }
}
