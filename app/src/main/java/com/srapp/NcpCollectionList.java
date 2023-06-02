package com.srapp;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.srapp.Adapter.AdapterForAttendanceHistory;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NcpCollectionList extends AppCompatActivity implements BasicFunctionListener {

    Spinner thanaSpinner, marketSpinner, outletSpinner;

    HashMap<String, ArrayList<String>> marketData;
    HashMap<String,ArrayList<String>> routeData;
    HashMap<String,ArrayList<String>> outletData;
    HashMap<String,ArrayList<String>> productCategoryData;
    BasicFunction bf;
    Data_Source ds;
    String routeId;
    String marketId;
    String outlet_id="0";

    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    Button startdate, enddate;

    ListView collectionlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_collection_list);
        ds= new Data_Source(this);
        thanaSpinner = findViewById(R.id.thana_spinner);
        marketSpinner = findViewById(R.id.market_spinner);
        outletSpinner = findViewById(R.id.outlet_spinner);
        startdate = findViewById(R.id.start_date);
        enddate = findViewById(R.id.end_date);
        bf = new BasicFunction(this,this);
        collectionlist = findViewById(R.id.ncp_collection_rec);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(SR_ID, "0");
        userIdTV.setText(value);
        titleTV.setText("NCP Collection List");

        routeData = new HashMap<>();
        marketData = new HashMap<>();
        productCategoryData = new HashMap<>();
        outletData = new HashMap<>();

        setdatepicker();

        routeData = ds.getRouteList();
        productCategoryData = ds.getProductCategories();

        SpinnerAdapter routeSpinnerAdapter = new SpinnerAdapter(this, R.layout
                .spinner_item, routeData.get(Tables.ROUTE_NAME));
        thanaSpinner.setAdapter(routeSpinnerAdapter);

        Log.e("mkt data test", "onCreate: "+marketData );

        thanaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null)
                    textView.setTextColor(getResources().getColor(R.color.background_card));

                routeId = routeData.get(Tables.ROUTE_ID).get(position);

                setAdapterToMarketSpinner();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        marketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null)
                    textView.setTextColor(getResources().getColor(R.color.background_card));

                marketId = marketData.get(Tables.MARKETS_market_id).get(position);
                setAdapterToOutletSpinner();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        outletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null)
                    textView.setTextColor(getResources().getColor(R.color.background_card));

                outlet_id = outletData.get(Tables.OUTLETS_ID).get(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







    }

    private void setdatepicker() {

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

        bf.savePreference("end_date", bf.getCurrentDate());
        bf.savePreference("start_Date", bf.getCurrentDate());
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


        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("start_date",bf.getPreference("start_Date"));
            jsonObject.put("end_date",bf.getPreference("end_date"));
            jsonObject.put("outlet_id",outlet_id);
            jsonObject.put("so_id",bf.getPreference(SR_ID));



            ProgressDialog dailog = CheckConnection(NcpCollectionList.this,"NCP Loading...");
            if (dailog==null)
                return;
            getJAPi().GET_NCP_Collection(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        ArrayList<HashMap<String,String>> collections = new ArrayList<>();
                        JSONArray jsonarray = new JSONObject(response.body()).getJSONArray("res");
                        dailog.dismiss();
                        for (int i=0 ; i<jsonarray.length() ; i++){
                            HashMap<String,String> map = new HashMap<>();

                            String collection_date = jsonarray.getJSONObject(i).getJSONObject("DistNcpCollection").getString("collection_date");
                            String quantity = jsonarray.getJSONObject(i).getJSONObject("DistNcpCollectionDetail").getString("quantity");
                            String batch_id = jsonarray.getJSONObject(i).getJSONObject("DistNcpCollectionDetail").getString("batch_id");
                            String product_name = jsonarray.getJSONObject(i).getJSONObject("Product").getString("name");
                            String product_id = jsonarray.getJSONObject(i).getJSONObject("Product").getString("id");
                            String outlet_id = jsonarray.getJSONObject(i).getJSONObject("DistOutlet").getString("id");
                            String outlet_name = jsonarray.getJSONObject(i).getJSONObject("DistOutlet").getString("name");
                            String expire_date = (jsonarray.getJSONObject(i).has("expire_date")) ?jsonarray.getJSONObject(i).getString("expire_date"):"no Ex date in api ";
                            String remarks = (jsonarray.getJSONObject(i).has("remarks")) ?jsonarray.getJSONObject(i).getString("remarks"):"no remarks in api ";


                            map.put("product_name",product_name);
                            map.put("product_id",product_id);
                            map.put("qty",quantity);
                            map.put("batch",batch_id);
                            map.put("exp",expire_date);
                            map.put("outlet_id",outlet_id);
                            map.put("outlet_name",outlet_name);
                            map.put("collection",collection_date);
                            map.put("remarks",collection_date);
                            collections.add(map);


                        }






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
            throw new RuntimeException(e);
        }
    }

    private void setAdapterToOutletSpinner() {
        if (marketId != null && !marketId.equals("0")) {
            outletData = ds.getOutletData("no",marketId);

            if (outletData != null && outletData.size()>0) {
                SpinnerAdapter outletSpinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, outletData.get(Tables.OUTLETS_OUTLET_NAME));
                outletSpinner.setAdapter(outletSpinnerAdapter);
            }else {
                outletSpinner.setAdapter(null);
            }
        }
    }

    private void setAdapterToMarketSpinner() {

        if (routeId != null && !routeId.equals("0")) {

            marketData = ds.getMarketData(routeId,"no");
            Log.e("null check", "setAdapterToMarketSpinner: "+marketData );

            if (marketData != null && marketData.size()>0) {
                SpinnerAdapter marketSpinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, marketData.get(Tables.MARKETS_market_name));
                marketSpinner.setAdapter(marketSpinnerAdapter);
            }else {
                marketSpinner.setAdapter(null);
            }
        }
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

    }

    @Override
    public void OnConnetivityError() {

    }
}
