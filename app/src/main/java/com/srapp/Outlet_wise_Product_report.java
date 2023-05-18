package com.srapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForOutletWiseProductReport;
import com.srapp.Adapter.AdapterForOutletWiseSalesReport;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
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


public class Outlet_wise_Product_report extends Parent implements BasicFunctionListener {
    Spinner route, MarketSp, OutletSp;
    ArrayList<String> routeList = new ArrayList<String>();
    ArrayList<String> ThanaName = new ArrayList<String>();
    String routeID;
    Boolean istrue = false;
    Button Sdate, clear, search, edate;
    ArrayList<String> MarketID = new ArrayList<String>();
    ArrayList<String> MarketName = new ArrayList<String>();
    String _MarketID;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog, toDatepickerDialog;
    ArrayList<String> OutletID = new ArrayList<String>();
    ArrayList<String> OutletName = new ArrayList<String>();
    Data_Source db;
    BasicFunction bf;
    String _OutletID = "0";
    ListView order_list;
    ArrayList<HashMap<String, String>> arrayList;
    ImageView homeBtn, backBtn;
    TextView userIdTV, titleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_wise_product_report);
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
                startActivity(new Intent(Outlet_wise_Product_report.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Outlet_wise_Product_report.this, Tools.class));
                finish();
            }
        });

        db = new Data_Source(this);
        route = findViewById(R.id.route);
        Sdate = findViewById(R.id.date);
        edate = findViewById(R.id.edate);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        clear = findViewById(R.id.clear);
        order_list = findViewById(R.id.order_list);
        search = findViewById(R.id.search);
        MarketSp = findViewById(R.id.market);
        OutletSp = findViewById(R.id.outlet);
        arrayList = new ArrayList<>();
        bf = new BasicFunction(this, this);
        ThanaParse();
        bf.savePreference("pDate", "0");
        Sdate.setText(getCurrentDate());
        edate.setText(getCurrentDate());
        route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                TextView textView = (TextView) arg0.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }

                arg1.animate();
                routeID = routeList.get(arg2);
                String name = ThanaName.get(arg2);
                bf.savePreference("pThana", name);
                bf.savePreference("prouteId", routeID);
                MarketParse(routeID);

                bf.savePreference("start_date", dateFormatter.format(Calendar.getInstance().getTime()));
                bf.savePreference("end_date", dateFormatter.format(Calendar.getInstance().getTime()));
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

                Getdata();
            }
        });

        Sdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fromDatePickerDialog.show();
            }
        });

        edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toDatepickerDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("start_date", dateFormatter.format(newDate.getTime()));
                Sdate.setText(bf.getPreference("start_date"));


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
        fromDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
        toDatepickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("end_date", dateFormatter.format(newDate.getTime()));
                edate.setText(bf.getPreference("end_date"));


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatepickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
        toDatepickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());

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


    }

    void Getdata() {
        if (_OutletID.equalsIgnoreCase("0")) {
            Toast.makeText(this, "Please Select a Outlet", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("so_id", bf.getPreference(SR_ID));
            jsonObject.put("start_date", bf.getPreference("start_date"));
            jsonObject.put("end_date", bf.getPreference("end_date"));
            jsonObject.put("outlet_id", bf.getPreference("pOutletID"));
            jsonObject.put("mac", bf.getPreference("mac"));
            //   bf.getResponceData(Outlet_wise_Product_report, jsonObject.toString(), 100);

            ProgressDialog dailog = CheckConnection(Outlet_wise_Product_report.this,"Checking...");
            if (dailog==null)
                return;
            getJAPi().Outlet_wise_Product_report(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();

                        Log.e("responce",response.body());


                        ArrayList<HashMap<String, String>> list = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("res");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("product_id", jsonArray.getJSONObject(i).getString("product_id"));
                            map.put("sales_qty", jsonArray.getJSONObject(i).getString("sales_qty"));
                            map.put("total_value", jsonArray.getJSONObject(i).getString("total_value"));
                            map.put("bonus_qty", jsonArray.getJSONObject(i).getString("bonus_qty"));
                            map.put("product_name", jsonArray.getJSONObject(i).getString("product_name"));


                            list.add(map);

                        }


                        AdapterForOutletWiseProductReport adapterForOutletWiseSalesReport = new AdapterForOutletWiseProductReport(Outlet_wise_Product_report.this,list);
                        order_list.setAdapter(adapterForOutletWiseSalesReport);




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

    private void ThanaParse() {
        routeList.clear();
        ThanaName.clear();
        Cursor c = db.rawQueryCoustom("SELECT * FROM route ORDER BY route_name ASC");
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
        Cursor c = db.rawQueryCoustom("SELECT * FROM markets where route_id=" + "'" + Thana_ID + "' and is_active='1' ORDER BY market_name COLLATE NOCASE ASC");

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
            c = db.rawQueryCoustom(Query);
        } else {
            Query = "SELECT * FROM outlets  WHERE market_id='" + marketID + "' AND outlet_category_id='" + typeID + "' and isActivated='1' ORDER BY outlet_name COLLATE NOCASE ASC";
            c = db.rawQueryCoustom(Query);

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
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {
        try {
            ArrayList<HashMap<String, String>> list = new ArrayList<>();
            ArrayList< ArrayList<HashMap<String, String>>> details = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("res");
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("memo_no", jsonArray.getJSONObject(i).getString("memo_no"));
                map.put("memo_date", jsonArray.getJSONObject(i).getString("memo_date"));
                ArrayList<HashMap<String, String>> sublish = new ArrayList<>();

                JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("details");
                Double Total = 0.00;
                for (int j = 0; j < jsonArray1.length(); j++) {
                    HashMap<String, String> map1 = new HashMap<>();
                    map1.put("product_name", jsonArray1.getJSONObject(j).getString("product_name"));
                    map1.put("sales_qty", jsonArray1.getJSONObject(j).getString("sales_qty"));
                    map1.put("bonus_qty", jsonArray1.getJSONObject(j).getString("bonus_qty"));
                    map1.put("value", jsonArray1.getJSONObject(j).getString("total_value"));
                    Total = Total+Double.parseDouble(jsonArray1.getJSONObject(j).getString("total_value"));
                    sublish.add(map1);

                }
                map.put("total", Total+"");
                list.add(map);
                details.add(sublish);
            }


            AdapterForOutletWiseSalesReport adapterForOutletWiseSalesReport = new AdapterForOutletWiseSalesReport(this,list,details);
            order_list.setAdapter(adapterForOutletWiseSalesReport);

        } catch (JSONException e) {
            Log.e("jsoe",e.getMessage());
            e.printStackTrace();
        }




    }

    @Override
    public void OnConnetivityError() {

    }
}
