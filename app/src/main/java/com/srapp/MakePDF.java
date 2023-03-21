package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForOrderReadyForPDF;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.print.PrintSelectedOrdersActivity;
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

public class MakePDF extends AppCompatActivity implements  BasicFunctionListener, DBListener {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    BasicFunction bf;
    Data_Source ds;
    Spinner route;
    Button start_date,end_date,all,print;
    ListView daliveryList;
    ArrayList<String> ThanaID=new ArrayList<String>();
    ArrayList<String> ThanaName=new ArrayList<String>();
    String _ThanaID;
    boolean isnetworkcalling;
    ArrayList<HashMap<String, String>> list;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_pdf);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( MakePDF.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( MakePDF.this, Dashboard.class));
                finishAffinity();
            }
        });


        route= findViewById(R.id.route);
        print= findViewById(R.id.print);
        start_date= findViewById(R.id.start_date);
        end_date= findViewById(R.id.end_date);
        all= findViewById(R.id.all);
        list= new ArrayList<>();
        daliveryList= findViewById(R.id.daliveryList);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        bf = new BasicFunction(this,this);
        ds = new Data_Source(this,this);
        bf.savePreference("Droute_id","0");

        routeSpinnerSetup();
        setDateTimeField();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mac",bf.getPreference("mac"));
            jsonObject.put("start_Date",bf.getPreference("Dstart_Date"));
            jsonObject.put("end_date",bf.getPreference("Dend_date"));
            jsonObject.put("route_id",bf.getPreference("Droute_id"));
            jsonObject.put(SR_ID,bf.getPreference(SR_ID));
          //  bf.getResponceData(URL.PROCESS_ORDER_LIST_FOR_MEMO,jsonObject.toString(),111);
            getProcessOrderList(jsonObject,111);
            isnetworkcalling = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent idn = new Intent(MakePDF.this, PrintSelectedOrdersActivity.class);
                idn.putExtra("FromDate",start_date.getText().toString());
                idn.putExtra("ToDate",end_date.getText().toString());
                startActivity(idn);
                finish();


            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mac",bf.getPreference("mac"));
                    jsonObject.put("start_Date","0");
                    jsonObject.put("end_date","0");
                    jsonObject.put("route_id","0");
                    jsonObject.put(SR_ID,bf.getPreference(SR_ID));
                  //  bf.getResponceData(URL.PROCESS_ORDER_LIST_FOR_MEMO,jsonObject.toString(),112);
                    getProcessOrderList(jsonObject,112);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }
                bf.savePreference("Droute_id",ThanaID.get(position));
                Log.e("logtt","DataView");
                if (!isnetworkcalling)
                DataView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getProcessOrderList(JSONObject jsonObject,int code) {
        ProgressDialog dailog = CheckConnection(MakePDF.this,"Get Memos...");
        if (dailog==null)
            return;
        getJAPi().PROCESS_ORDER_LIST_FOR_MEMO(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    if(code==112){
                        ds.updateWithServerProcessed(jsonObject,code);
                    }else
                    ds.updateWithServerProcessed(jsonObject,code);

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

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        isnetworkcalling=false;

    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(this,"NO Internet Connection",Toast.LENGTH_SHORT);

    }

    private void routeSpinnerSetup()
    {
        ThanaID.clear();
        ThanaName.clear();
        Cursor c = ds.rawQuery("SELECT * FROM route ORDER BY route_name ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Thana_Id = c.getString(c.getColumnIndex("route_id"));
                    String Thana_Name = c.getString(c.getColumnIndex("route_name"));

                    ThanaID.add(Thana_Id);
                    ThanaName.add(Thana_Name);

                } while (c.moveToNext());
            }
            ThanaID.add(0,"0");
            ThanaName.add(0,"ALL");
            /*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MakePDF.this,R.layout.spinner_text, Route_name);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
            SpinnerAdapter dataAdapter = new SpinnerAdapter(this, R.layout
                    .spinner_item, ThanaName);
            route.setAdapter(dataAdapter);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            Intent idd = new Intent(MakePDF.this, Dashboard.class);
            startActivity(idd);
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void setDateTimeField() {
        bf.savePreference("Dstart_Date", bf.getCurrentDate());
        bf.savePreference("Dend_date", bf.getCurrentDate());
        start_date.setText(bf.getCurrentDate());
        end_date.setText(bf.getCurrentDate());

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("Dstart_Date", dateFormatter.format(newDate.getTime()));
                start_date.setText(bf.getPreference("Dstart_Date"));
                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("Dend_date", dateFormatter.format(newDate.getTime()));
                end_date.setText(bf.getPreference("Dend_date"));
                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));




    }

    private void DataView() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mac",bf.getPreference("mac"));
            jsonObject.put("start_Date",bf.getPreference("Dstart_Date"));
            jsonObject.put("end_date",bf.getPreference("Dend_date"));
            jsonObject.put("route_id",bf.getPreference("Droute_id"));
            jsonObject.put(SR_ID,bf.getPreference(SR_ID));
            //bf.getResponceData(URL.PROCESS_ORDER_LIST_FOR_MEMO,jsonObject.toString(),111);
            getProcessOrderList(jsonObject,111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnLocalDBdataRetrive(final String json) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (json.equalsIgnoreCase("111")){

                    ds.getProcessedOrder(bf.getPreference("Dstart_Date"), bf.getPreference("Dend_date"), bf.getPreference("Droute_id"));


                }else if(json.equalsIgnoreCase("112")){

                    ds.getProcessedOrder("0", "0", "0");



                }
            }
        });


    }

    @Override
    public void OnLocalDBdataRetrive(final ArrayList<HashMap<String, String>> arrayList) {

      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              list=arrayList;
              AdapterForOrderReadyForPDF adapter = new AdapterForOrderReadyForPDF(MakePDF.this,list);
              daliveryList.setAdapter(adapter);
          }
      });

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }
}
