package com.srapp;

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

import androidx.appcompat.app.AppCompatActivity;

import com.srapp.Adapter.AdapterForMultiOrderPrint;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Multiple_Invoice_print extends AppCompatActivity implements BasicFunctionListener {
    Spinner route, MarketSp, OutletSp;
    ArrayList<String> routeList = new ArrayList<String>();
    ArrayList<String> ThanaName = new ArrayList<String>();
    String routeID;
    Boolean istrue = false;
    Button date, clear, search;
    ArrayList<String> MarketID = new ArrayList<String>();
    ArrayList<String> MarketName = new ArrayList<String>();
    String _MarketID;
    ArrayList<String> OutletID = new ArrayList<String>();
    ArrayList<String> OutletName = new ArrayList<String>();
    Data_Source db;
    BasicFunction bf;
    String _OutletID;
    ListView order_list;
    AdapterForMultiOrderPrint adapter;
    ArrayList<HashMap<String, String>> arrayList;
    ImageView homeBtn, backBtn;
    TextView userIdTV, titleTV;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_process_order);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        titleTV.setText("Print Invoice");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(Multiple_Invoice_print.this, Dashboard.class));
            finish();
        });

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(Multiple_Invoice_print.this, Tools.class));
            finish();
        });

        db = new Data_Source(this);
        route = findViewById(R.id.route);
        date = findViewById(R.id.date);
        dateFormatter = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);
        clear = findViewById(R.id.clear);
        order_list = findViewById(R.id.order_list);
        search = findViewById(R.id.search);
        MarketSp = findViewById(R.id.market);
        OutletSp = findViewById(R.id.outlet);
        arrayList = new ArrayList<>();
        bf = new BasicFunction(this, this);
        ThanaParse();
        bf.savePreference("pDate", "0");
        route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                TextView textView = (TextView) arg0.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0, 0, 0, 0);

                arg1.animate();
                routeID = routeList.get(arg2);
                String name = ThanaName.get(arg2);
                bf.savePreference("pThana", name);
                bf.savePreference("prouteId", routeID);
                MarketParse(routeID);

                Log.e("****************", "&&&&&&&&&&&&&&&&&&&&&&" + routeID + "     " + name);
                if (!routeList.equals("00")) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {

                jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                jsonObject.put("market_id", bf.getPreference("pMarketID"));
                jsonObject.put("route_id", bf.getPreference("prouteId"));
                jsonObject.put("outlet_id", bf.getPreference("pOutletID"));
                jsonObject.put("mac", bf.getPreference("mac"));
                                   jsonObject.put("order_date", bf.getPreference("pDate"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Getdata(jsonObject);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fromDatePickerDialog.show();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                    jsonObject.put("market", "0");
                    jsonObject.put("route", "0");
                    jsonObject.put("outlet", "0");
                    jsonObject.put("mac", bf.getPreference("mac"));
                    jsonObject.put("order_date", "0");
                   // bf.getResponceData(ORDERS_FOR_UNPROCESS, jsonObject.toString(), 100);
                    Getdata(jsonObject);
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

                bf.savePreference("pDate", dateFormatter.format(newDate.getTime()));
                date.setText(bf.getPreference("pDate"));


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
        fromDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
        MarketSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                TextView textView = (TextView) arg0.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0, 0, 0, 0);

                _MarketID = MarketID.get(arg2);
                bf.savePreference("pMarketID", _MarketID);
                String Market_name = MarketName.get(arg2);
                bf.savePreference("pMarket", Market_name);

                //OutletCategoriesTableParse();

                Log.e("****************", "&&&&&&&&&&&&&&&&&&&&&&_Market  " + _MarketID + "     " + Market_name);

                OutletAllTableParse(_MarketID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        OutletSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                TextView textView = (TextView) arg0.getChildAt(0);
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }
                _OutletID = OutletID.get(arg2);
                String _OutletName = OutletName.get(arg2);
                bf.savePreference("pOutletName", _OutletName);
                bf.savePreference("pOutletID", _OutletID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SR_ID, bf.getPreference(SR_ID));
            jsonObject.put("market", "0");
            jsonObject.put("route", "0");
            jsonObject.put("outlet", "0");
            jsonObject.put("mac", bf.getPreference("mac"));
            jsonObject.put("order_date", "0");
            //bf.getResponceData(ORDERS_FOR_UNPROCESS, jsonObject.toString(), 100);
            Getdata(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void Getdata(JSONObject jsonObject) {




            //bf.getResponceData(ORDERS_FOR_UNPROCESS, jsonObject.toString(), 100);

            ProgressDialog dailog = CheckConnection(Multiple_Invoice_print.this,"Checking...");
            if (dailog==null)
                return;
            getJAPi().ORDERS_FOR_UNPROCESS(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();
                        arrayList.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("orders");
                            for (int j = 0; j < jsonArray.length(); j++) {
                                HashMap<String, String> map = new HashMap<>();

                                map.put("order_number", jsonArray.getJSONObject(j).getString("order_number"));
                                map.put("outlet_name", jsonArray.getJSONObject(j).getString("outlet_name"));
                                map.put("gross_value", jsonArray.getJSONObject(j).getString("gross_value"));
                                map.put("order_date", jsonArray.getJSONObject(j).getString("order_date"));
                                //map.put("status","2");

                                //    arrayList.sort(Comparator.comparing(m -> map.get("order_date"), Comparator.nullsLast(Comparator.naturalOrder())));


                                arrayList.add(map);
                            }

                            adapter = new AdapterForMultiOrderPrint(Multiple_Invoice_print.this, arrayList);
                            order_list.setAdapter(adapter);
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

    private void ThanaParse() {
        routeList.clear();
        ThanaName.clear();
        Cursor c = db.rawQuery("SELECT * FROM route ORDER BY route_name ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Thana_Id = c.getString(c.getColumnIndex("route_id"));
                    String Thana_Name = c.getString(c.getColumnIndex("route_name"));

                    routeList.add(Thana_Id);
                    ThanaName.add(Thana_Name);

                } while (c.moveToNext());
            }
            routeList.add(0, "0");
            ThanaName.add(0, "All");
            /*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UnProcessOrder.this,R.layout.spinner_text, Route_name);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);*/
            SpinnerAdapter dataAdapter = new SpinnerAdapter(this, R.layout
                    .spinner_item, ThanaName);
            route.setAdapter(dataAdapter);

            if (!bf.getPreference("pthanaID").equalsIgnoreCase("NO PREFERENCE") && !bf.getPreference("pthanaID").equalsIgnoreCase("")) {
                int SelectedPos = routeList.indexOf(bf.getPreference("pthanaID"));
                Log.e("MARKET POS:", ".........." + SelectedPos);
                route.setSelection(SelectedPos);
            }


        }

    }

    private void MarketParse(String Thana_ID) {
        String Market_Id = "0";
        MarketID.clear();
        MarketName.clear();
        Cursor c = db.rawQuery("SELECT * FROM markets where route_id=" + "'" + Thana_ID + "' and is_active='1' ORDER BY market_name COLLATE NOCASE ASC");

        Log.e("querymarket", "SELECT * FROM markets where route_id=" + "'" + Thana_ID + "' and is_active!='1' ORDER BY market_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    Market_Id = c.getString(c.getColumnIndex("market_id"));
                    String Market_Name = c.getString(c.getColumnIndex("market_name"));

                    MarketID.add(Market_Id);
                    MarketName.add(Market_Name);


                } while (c.moveToNext());
            }
            MarketID.add(0, "0");
            MarketName.add(0, "All");
            /*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UnProcessOrder.this,R.layout.spinner_text, MarketName);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);*/
            SpinnerAdapter dataAdapter = new SpinnerAdapter(this, R.layout
                    .spinner_item, MarketName);
            MarketSp.setAdapter(dataAdapter);
            OutletAllTableParse(Market_Id);
            if (!bf.getPreference("pMarketID").equalsIgnoreCase("NO PREFERENCE") && !bf.getPreference("pMarketID").equalsIgnoreCase("")) {
                int SelectedPos = MarketID.indexOf(bf.getPreference("pMarketID"));
                Log.e("MARKET POS:", ".........." + SelectedPos);
                MarketSp.setSelection(SelectedPos);
            }
            OutletAllTableParse("00");

        }

    }

    private void OutletAllTableParse(String marketID) {

        OutletID.clear();

        OutletName.clear();

        String typeID = "0";
        Cursor c;
        String Query;
        if (typeID.equalsIgnoreCase("0")) {
            Query = "SELECT * FROM outlets  WHERE market_id='" + marketID + "' and isActivated='1' ORDER BY outlet_name COLLATE NOCASE ASC";
            c = db.rawQuery(Query);
        } else {
            Query = "SELECT * FROM outlets  WHERE market_id='" + marketID + "' AND outlet_category_id='" + typeID + "' and isActivated='1' ORDER BY outlet_name COLLATE NOCASE ASC";
            c = db.rawQuery(Query);
        }

        c.moveToFirst();
        Log.e("courser count", Query + " " + c.getCount() + "");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String outlet_id = c.getString(c.getColumnIndex("outlet_id"));
                    String outlet_name = c.getString(c.getColumnIndex("outlet_name"));

                    OutletID.add(outlet_id);
                    Log.e("outlateID", outlet_id);
                    OutletName.add(outlet_name);

                } while (c.moveToNext());
            }

            for (int i = 0; i < OutletName.size(); i++) {
                Log.e("OUTLET_NAME:", OutletName.get(i));
                Log.e("OUTLET_ID:", OutletID.get(i));
            }

            OutletID.add(0, "0");
            OutletName.add(0, "All");

            /*ArrayAdapter<String>  dataAdapter = new ArrayAdapter<String>(UnProcessOrder.this,R.layout.spinner_text, OutletName);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);*/

            SpinnerAdapter outletDataAdapter = new SpinnerAdapter(this, R.layout
                    .spinner_item, OutletName);

            OutletSp.setAdapter(outletDataAdapter);

            if (!bf.getPreference("pOutletID").equalsIgnoreCase("NO PREFERENCE") && !bf.getPreference("pOutletID").equalsIgnoreCase("")) {
                int SelectedPos = OutletID.indexOf(bf.getPreference("pOutletID").trim());
                if (SelectedPos >= 0) {
                    Log.e("OUTlET POS:", ".........." + SelectedPos + " " + bf.getPreference("pOutletID") + " " + OutletID.size());
                    OutletSp.setSelection(SelectedPos);
                    TempData.OutletID = OutletID.get(SelectedPos);
                    TempData.OutletName = OutletName.get(SelectedPos);
                    _OutletID = OutletID.get(SelectedPos);

                    bf.savePreference("pOutletID", _OutletID);
                } else {
                    if (OutletName.size() > 0) {

                        TempData.OutletID = OutletID.get(0);
                        TempData.OutletName = OutletName.get(0);

                        _OutletID = OutletID.get(0);

                        bf.savePreference("pOutletID", _OutletID);
                    }

                }
            } else {
                if (OutletName.size() > 0) {

                    TempData.OutletID = OutletID.get(0);
                    _OutletID = OutletID.get(0);
                    TempData.OutletName = OutletName.get(0);
                }
            }

        }

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {


    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(Multiple_Invoice_print.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(Multiple_Invoice_print.this, Tools.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
