package com.srapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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

import com.srapp.Adapter.AdapterForOrderProcessShow;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.Parent;
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
import static com.srapp.Db_Actions.URL.CheckConnection;

import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderProcess extends Parent implements BasicFunctionListener {

    Button date,processorder,clear;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog;
    ListView order_list;
    AdapterForOrderProcessShow  adapter;
    ArrayList<HashMap<String,String>> arrayList;
    BasicFunction bf;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_process);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( OrderProcess.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( OrderProcess.this, Tools.class));
                finish();
            }
        });

        date = findViewById(R.id.date);
        arrayList = new ArrayList<>();
        processorder = findViewById(R.id.processorder);
        order_list = findViewById(R.id.order_list);
        clear = findViewById(R.id.clear);
        bf = new BasicFunction(this,this);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fromDatePickerDialog.show();
            }
        });
        processorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

       getdata(new JSONObject());

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(SR_ID,bf.getPreference(SR_ID));
                    jsonObject.put("mac",bf.getPreference("mac"));
                    //  jsonObject.put("order_date","all");
                   // bf.getResponceData(ORDERS_FOR_PROCESS,jsonObject.toString(),100);
                    getdata(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("Date", dateFormatter.format(newDate.getTime()));
                date.setText(bf.getPreference("Date"));
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(SR_ID,bf.getPreference(SR_ID));
                    jsonObject.put("mac",bf.getPreference("mac"));
                    jsonObject.put("order_date",bf.getPreference("Date"));
                    //bf.getResponceData(ORDERS_FOR_PROCESS,jsonObject.toString(),100);
                    getdata(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void getdata(JSONObject jsonObject) {


        ProgressDialog dailog = CheckConnection(OrderProcess.this,"Getting Order List...");
        if (dailog==null)
            return;
        getJAPi().ORDERS_FOR_PROCESS(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();


                    arrayList.clear();
                    android.util.Log.e("json",jsonObject.toString());



                        JSONArray jsonArray = jsonObject.getJSONArray("orders");
                        for (int j = 0 ; j<jsonArray.length(); j++){
                            HashMap<String,String> map = new HashMap<>();

                            map.put("order_number",jsonArray.getJSONObject(j).getString("order_number"));
                            map.put("outlet_name",jsonArray.getJSONObject(j).getString("outlet_name"));
                            map.put("gross_value",jsonArray.getJSONObject(j).getString("gross_value"));
                            map.put("order_date",jsonArray.getJSONObject(j).getString("order_date"));
                            map.put("status","2");

                            arrayList.add(map);
                        }

                        adapter = new AdapterForOrderProcessShow(OrderProcess.this,arrayList);
                        order_list.setAdapter(adapter);



                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {



    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(OrderProcess.this,"No Internet Connection", Toast.LENGTH_SHORT).show();


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( OrderProcess.this, Tools.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
