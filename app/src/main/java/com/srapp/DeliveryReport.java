package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.usb.UsbManager;
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

import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.srapp.Adapter.AdapterForOrderReadyForDalivery;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.print.ParentActivity;
import com.srapp.print.PrintActivity;
import com.srapp.print.PrintActivityEN;
import com.srapp.print.PrintAllMemosActivity;
import com.srapp.print.PrintAllMemosActivityEn;
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

public class DeliveryReport extends ParentActivity implements BasicFunctionListener , DBListener {
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
    ArrayList<HashMap<String, String>> list;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    public static  int allprint=0;
    Boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_report);
        allprint=0;
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
                startActivity(new Intent( DeliveryReport.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( DeliveryReport.this, Dashboard.class));
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
        bf.savePreference("data","coustom");
        FirebaseCrashlytics.getInstance().setUserId(bf.getPreference("sr_uname"));
        routeSpinnerSetup();

        setDateTimeField();

       if (getIntent().getIntExtra("a_from",0 )==1){
           flag =false;
           if (!bf.getPreference("data").equalsIgnoreCase("all")){

              // ds.getProcessedOrder(bf.getPreference("Dstart_Date"), bf.getPreference("Dend_date"), bf.getPreference("Droute_id"));

               JSONObject jsonObject = new JSONObject();
               try {
                   jsonObject.put("mac",bf.getPreference("mac"));
                   jsonObject.put("start_Date",bf.getPreference("Dstart_Date"));
                   jsonObject.put("end_date",bf.getPreference("Dend_date"));
                   jsonObject.put("route_id",bf.getPreference("Droute_id"));
                   jsonObject.put(SR_ID,bf.getPreference(SR_ID));
                   flag =false;
                 //  bf.getResponceData(URL.PROCESS_ORDER_LIST_FOR_MEMO,jsonObject.toString(),111);


                   getdata(jsonObject);

               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }else {
               JSONObject jsonObject = new JSONObject();
               try {
                   jsonObject.put("mac",bf.getPreference("mac"));
                   jsonObject.put("start_Date","0");
                   jsonObject.put("end_date","0");
                   jsonObject.put("route_id","0");
                   jsonObject.put(SR_ID,bf.getPreference(SR_ID));
                  // bf.getResponceData(URL.PROCESS_ORDER_LIST_FOR_MEMO,jsonObject.toString(),112);
                   getdata2(jsonObject);
               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }

           start_date.setText(bf.getPreference("Dstart_Date"));
           end_date.setText(bf.getPreference("Dend_date"));


       }else {

           bf.savePreference("Droute_id","0");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mac",bf.getPreference("mac"));
            jsonObject.put("start_Date",bf.getPreference("Dstart_Date"));
            jsonObject.put("end_date",bf.getPreference("Dend_date"));
            jsonObject.put("route_id",bf.getPreference("Droute_id"));
            jsonObject.put(SR_ID,bf.getPreference(SR_ID));
           // bf.getResponceData(URL.PROCESS_ORDER_LIST_FOR_MEMO,jsonObject.toString(),111);
            flag =false;
            getdata(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
       }



//        print.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.gc();
//                Runtime.getRuntime().gc();
//
//                UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(DeliveryReport.this);
//                UsbManager usbManager = (UsbManager) DeliveryReport.this.getSystemService(Context.USB_SERVICE);
//
//               /* if (usbConnection == null || usbManager == null) {
//                    new AlertDialog.Builder(DeliveryReport.this)
//                            .setTitle("USB Connection")
//                            .setMessage("No USB printer found.")
//                            .show();
//                }
//
//                else {*/
//
//                    showPrintDailog();
//
//               // }
//
//            }
//        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bf.savePreference("data","all");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mac",bf.getPreference("mac"));
                    jsonObject.put("start_Date","0");
                    jsonObject.put("end_date","0");
                    jsonObject.put("route_id","0");
                    jsonObject.put(SR_ID,bf.getPreference(SR_ID));
                   // bf.getResponceData(URL.PROCESS_ORDER_LIST_FOR_MEMO,jsonObject.toString(),112);
                    getdata2(jsonObject);
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


                if (flag){
                    bf.savePreference("Droute_id",ThanaID.get(position));
                    bf.savePreference("data","coustom");

                    Log.e("route_id",bf.getPreference("Droute_id"));
                    DataView();
                }else {
                    flag=true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getdata(JSONObject jsonObject) {

        ProgressDialog dailog = CheckConnection(DeliveryReport.this,"Get Orders...");
        if (dailog==null)
            return;
        getJAPi().PROCESS_ORDER_LIST_FOR_MEMO(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    ds.updateWithServerProcessed(jsonObject,111);

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

    private void showPrintDailog() {

        Intent idn = new Intent(DeliveryReport.this, PrintAllMemosActivityEn.class);
        idn.putExtra("FromDate",start_date.getText().toString());
        idn.putExtra("ToDate",end_date.getText().toString());
        startActivity(idn);
        finish();

//        new AlertDialog.Builder(this)
//                .setIcon(R.drawable.alert)
//                .setTitle("Select Print Language")
//                .setMessage("Which Language You want to print?")
//                .setPositiveButton("BN", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent idn = new Intent(DeliveryReport.this, PrintAllMemosActivity.class);
//                        idn.putExtra("FromDate",start_date.getText().toString());
//                        idn.putExtra("ToDate",end_date.getText().toString());
//                        startActivity(idn);
//                        finish();
//                    }
//                })
//                .setNegativeButton("EN",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent idn = new Intent(DeliveryReport.this, PrintAllMemosActivityEn.class);
//                        idn.putExtra("FromDate",start_date.getText().toString());
//                        idn.putExtra("ToDate",end_date.getText().toString());
//                        startActivity(idn);
//                        finish();
//                    }
//                })
//                .show();
    }
    private void getdata2(JSONObject jsonObject) {

        ProgressDialog dailog = CheckConnection(DeliveryReport.this,"Getting Order List...");
        if (dailog==null)
            return;
        getJAPi().PROCESS_ORDER_LIST_FOR_MEMO(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    ds.updateWithServerProcessed(jsonObject,112);

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

    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(this,"NO Internet Connection",Toast.LENGTH_SHORT);

    }

    private void routeSpinnerSetup()
    {
        int position = 0, increment = 1;
        ThanaID.clear();
        ThanaName.clear();
        ThanaID.add("0");
        ThanaName.add("ALL");
        Cursor c = ds.rawQueryCoustom("SELECT * FROM route ORDER BY route_name ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Thana_Id = c.getString(c.getColumnIndex("route_id"));
                    String Thana_Name = c.getString(c.getColumnIndex("route_name"));
                    Log.e("route_id",Thana_Id+"  "+bf.getPreference("Droute_id"));
                    if (Thana_Id.equalsIgnoreCase(bf.getPreference("Droute_id"))){
                        position = increment;
                    }
                    ThanaID.add(Thana_Id);
                    ThanaName.add(Thana_Name);
                    increment++;
                } while (c.moveToNext());
            }

            /*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DeliveryReport.this,R.layout.spinner_text, Route_name);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
            SpinnerAdapter dataAdapter = new SpinnerAdapter(this, R.layout
                    .spinner_item, ThanaName);
            route.setAdapter(dataAdapter);
            route.setSelection(position);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            Intent idd = new Intent(DeliveryReport.this, Dashboard.class);
            startActivity(idd);
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void setDateTimeField() {

        if (getIntent().getIntExtra("a_from",0 )!=1) {
            bf.savePreference("Dstart_Date", bf.getCurrentDate());
            bf.savePreference("Dend_date", bf.getCurrentDate());
            start_date.setText(bf.getCurrentDate());
            end_date.setText(bf.getCurrentDate());
        }

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                bf.savePreference("data","coustom");
                bf.savePreference("Dstart_Date", dateFormatter.format(newDate.getTime()));

                start_date.setText(bf.getPreference("Dstart_Date"));
                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
        fromDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                bf.savePreference("data","coustom");
                bf.savePreference("Dend_date", dateFormatter.format(newDate.getTime()));

                end_date.setText(bf.getPreference("Dend_date"));

                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
        toDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
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
            getdata(jsonObject);
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
                list = arrayList;
                AdapterForOrderReadyForDalivery adapter = new AdapterForOrderReadyForDalivery(DeliveryReport.this,list,start_date.getText().toString(),end_date.getText().toString());
                daliveryList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }
}
