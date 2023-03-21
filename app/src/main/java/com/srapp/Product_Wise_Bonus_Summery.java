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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForOrderSummery;
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
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Product_Wise_Bonus_Summery extends AppCompatActivity implements BasicFunctionListener {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    ListView listview;
    Button startdate, enddate;
    BasicFunction bf;
    boolean onstart =false;
    ArrayList<HashMap<String,String>> list;
    ImageView homeBtn,backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__wise__bonus__summery);
        startdate = findViewById(R.id.start_date);
        list= new ArrayList<>();
        listview = findViewById(R.id.list_view);
        enddate = findViewById(R.id.end_date);
        homeBtn = findViewById(R.id.home);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        backBtn = findViewById(R.id.back);

       TextView userIdTV = findViewById(R.id.user_txt_view);
        TextView titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Product_Wise_Bonus_Summery.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Product_Wise_Bonus_Summery.this, Reports_Activity.class));
                finish();
            }
        });
        bf = new BasicFunction(this,this);
        setDateTimeField();

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
                jsonObject.put("start_date", startdate.getText().toString().trim());
                jsonObject.put("end_date", enddate.getText().toString().trim());
                jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                jsonObject.put("mac", bf.getPreference("mac"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //bf.getResponceData(URL.PRODUCT_WISE_BONUS_SUMMERY, jsonObject.toString(), 101);
            ProgressDialog dailog = CheckConnection(Product_Wise_Bonus_Summery.this,"Getting Report...");
            if (dailog==null)
                return;
            getJAPi().PRODUCT_WISE_BONUS_SUMMERY(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();
                        list.clear();
                        onstart=true;

                        JSONArray jsonArray = jsonObject.getJSONArray("product_bonus_report");
                        for (int j = 0 ; j<jsonArray.length(); j++ ){
                            if (Double.parseDouble(jsonArray.getJSONObject(j).getString("total_qty"))>0) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("product_name", jsonArray.getJSONObject(j).getString("product_name"));
                                //Log.println(1,"total_qty",jsonArray.getJSONObject(j).getString("total_qty"));
                                Log.e("total_qty",jsonArray.getJSONObject(j).getString("total_qty"));
                                map.put("EC", jsonArray.getJSONObject(j).getString("total_ec"));
                                map.put("OC", jsonArray.getJSONObject(j).getString("total_oc"));
                                map.put("qty", jsonArray.getJSONObject(j).getString("total_qty"));

                                //map.put("total_price", jsonArray.getJSONObject(j).getString("total_price"));
                                list.add(map);
                            }

                        }
                        AdapterForOrderSummery adapterForOrderSummery = new AdapterForOrderSummery(Product_Wise_Bonus_Summery.this,list);
                        listview.setAdapter(adapterForOrderSummery);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dailog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "NO Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {




    }

    @Override
    public void OnConnetivityError() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( Product_Wise_Bonus_Summery.this, Reports_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
