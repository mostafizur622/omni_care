package com.srapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForBonusProductList;
import com.srapp.Adapter.AdapterForProductWiseSaleReport;
import com.srapp.Adapter.AdapterForSalesReport;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.Parent;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.srapp.Db_Actions.Tables.ORDER_order_number;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER_DETAILS;

public class BonusReport extends Parent implements BasicFunctionListener {
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
        setContentView(R.layout.activity_bonus_party_report);
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
                startActivity(new Intent(BonusReport.this, Dashboard.class));
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
                startActivity(new Intent( BonusReport.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( BonusReport.this, Reports_Activity.class));
                finish();
            }
        });


        productsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                TextView textView = (TextView)arg0.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }

                product_id = productIdList.get(arg2);
                Log.e("product_id",product_id);

                DataView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        setProductSp();

    }

    private void setProductSp() {


        productIdList.clear();
        productNameList.clear();


        String query1 = "SELECT DISTINCT(ph.product_id) FROM product_history as ph  WHERE  is_bonus=1";
       Cursor c2 = db.rawQuery(query1);
        Log.e("query", query1 + " " + c2.getCount());
        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {

                    String product_id = c2.getString(0);



                    String query = "SELECT product_name, product_id FROM product WHERE product_id='" + product_id + "'";
                    Cursor c12 = db.rawQuery(query);
                    Log.e("Query", query);
                    if (c12 != null) {
                        if (c12.moveToFirst()) {
                            do {
                                Log.e("c12.getString(1)",c12.getString(1));
                                productIdList.add(c12.getString(1));
                                productNameList.add(c12.getString(0
                                ));

                            } while (c12.moveToNext());
                        }
                    }

                } while (c2.moveToNext());
            }


        }



        /*Cursor c = db.rawQuery("select product_id, product_name from product");

        c.moveToFirst();
        if (c!=null && c.getCount()>0){
            do{

                Cursor c1 = db.rawQuery("select product_id, product_name from product where is_bonus='1'");




            }while (c.moveToNext());

        }*/
        productNameList.add(0,"Select Product");
        productIdList.add(0,"0");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BonusReport.this, R.layout.spinner_text, productNameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productsp.setAdapter(dataAdapter);

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
                setProductSp();
                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));




        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("end_date", dateFormatter.format(newDate.getTime()));
                enddate.setText(bf.getPreference("end_date"));
                setProductSp();
                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        if (i==101){
            try {
                list.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("product_order_report");
                JSONObject object = jsonArray.getJSONObject(0);
                ec.setText(object.getString("total_ec"));
                oc.setText(object.getString("total_oc"));
                tqty.setText(object.getString("total_qty"));
                JSONArray jsonArray1 = object.getJSONArray("report_details");

                for (int pos = 0 ; pos<jsonArray1.length(); pos++){

                    HashMap<String,String>  map = new HashMap<>();

                    map.put("dist_order_no",jsonArray1.getJSONObject(pos).getString("dist_order_no"));
                    map.put("order_date",jsonArray1.getJSONObject(pos).getString("order_date"));
                    map.put("outlet_name",jsonArray1.getJSONObject(pos).getString("outlet_name"));
                    map.put("sales_qty",jsonArray1.getJSONObject(pos).getString("sales_qty"));
                    list.add(map);


                }


                AdapterForProductWiseSaleReport adapterForProductWiseSaleReport = new AdapterForProductWiseSaleReport(this,list);
                listview.setAdapter(adapterForProductWiseSaleReport);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("jsone",e.getMessage());
            }

        }
    }

    @Override
    public void OnConnetivityError() {



    }

    private void DataView() {

        if (!product_id.equalsIgnoreCase("0")) {

            Log.e("DataView", "DataView");

            if (bf.isInternetOn()) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("start_date", startdate.getText().toString().trim());
                    jsonObject.put("end_date", enddate.getText().toString().trim());
                    jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                    jsonObject.put("mac", bf.getPreference("mac"));
                    jsonObject.put("product_id", product_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                bf.getResponceData(URL.PRODUCT_WISE_BONUS, jsonObject.toString(), 101);
            } else {
                Toast.makeText(this, "NO Internet Connection", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(BonusReport.this,"Select A Product First",Toast.LENGTH_SHORT).show();
        }



    }


    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( BonusReport.this, Reports_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
