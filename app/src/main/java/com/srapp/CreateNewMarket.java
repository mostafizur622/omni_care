package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.StaticFlags;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewMarket extends AppCompatActivity implements BasicFunctionListener {
    ImageView homeBtn,backBtn;
    Toolbar toolbar;
    Spinner territorySpinner, thanaSpinner, routeSpinner,locationTypeSpinner;
    EditText marketNameTv, addressTv;
    Button saveBtn;
    Data_Source ds;
    TextView lable;
    TextView territoryLable, thanaLable,routeLable,locationLable;
    //ArrayList<String> dataForMarketCreate;

    HashMap<String,ArrayList<String>> territoryData;
    HashMap<String,ArrayList<String>> thanaData;
    HashMap<String,ArrayList<String>> locationData;
    HashMap<String,ArrayList<String>> routeData;
    int page_from;
    String territoryId,thanaId,locationId,routeId, marketName, address, salesPersonId;
    HashMap<String,String> dataForMarketCreate;

    int routeSelection, thanaSelection, locationaSelection;
        BasicFunction basic;
    //.........intent rcv.......//
    HashMap<String,String>markets;
    private String market_id_byIntent;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_market);
        ds = new Data_Source(this);
        dataForMarketCreate = new HashMap<>();
        basic = new BasicFunction(this,this);
        TextView user_txt_view;
        user_txt_view = findViewById(R.id.user_txt_view);
        user_txt_view.setText(basic.getPreference(SR_ID));
        //................intent part...............//
        Intent intent = getIntent();
        market_id_byIntent = intent.getStringExtra("market_id");
        page_from = intent.getIntExtra("page_from",0);
        routeId = intent.getStringExtra("Route_id");
        Log.e("Route_id123",routeId+" 123"+market_id_byIntent);

        //................spinner lables................//
        //territoryLable = findViewById(R.id.territory_name_tv);
        thanaLable = findViewById(R.id.thana_name_tv);
        locationLable = findViewById(R.id.location_name_tv);
        routeLable = findViewById(R.id.route_name_tv);

        lable = findViewById(R.id.market_create_lable);
        saveBtn = findViewById(R.id.save_btn);



        String mark = getColoredSpanned("*","#ff6c58");

//        territoryLable.setText(Html.fromHtml("Territory Name"+mark));
        thanaLable.setText(Html.fromHtml("Thana Name"+mark));
        locationLable.setText(Html.fromHtml("Location Type"+mark));
        routeLable.setText(Html.fromHtml("Route"+mark));

        territoryData = new HashMap<>();
        thanaData = new HashMap<>();
        locationData = new HashMap<>();
        routeData = new HashMap<>();

        territoryId = basic.getPreference("territory_id");
        salesPersonId = basic.getPreference("sales_person_id");

        //territoryData = ds.getTerritoryList("1008");
        locationData = ds.getLocationList();
        routeData = ds.getRouteList();
        thanaData = ds.getThanaList("no");
        Log.e("null check", "setAdapterToThanaSpinner: "+thanaData );


        Log.e("Location Spinner Log", "onCreate: "+locationData );

        //territorySpinner = findViewById(R.id.terrritory_spinner);
        thanaSpinner = findViewById(R.id.thana_spinner);
        routeSpinner = findViewById(R.id.route_spinner);
        locationTypeSpinner = findViewById(R.id.location_type_spinner);

        routeSpinner.setDropDownWidth(300);



        Log.e("territories for spinner", "onCreate: "+territoryData );

       // SpinnerAdapter territorySpinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, territoryData.get(Tables.TERRITORY_NAME));

        //thana spinner adapter setup
        if (thanaData != null && thanaData.size()>0) {
            SpinnerAdapter thanaSpinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, thanaData.get(Tables.THANA_NAME));
            thanaSpinner.setAdapter(thanaSpinnerAdapter);
        }else {
            thanaSpinner.setAdapter(null);
        }

        //route spinner adapter setup
        if (routeData != null && routeData.size()>0) {
            SpinnerAdapter routeSpinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, routeData.get(Tables.ROUTE_NAME));
            routeSpinner.setAdapter(routeSpinnerAdapter);
            if (page_from==25)
            routeSpinner.setSelection(getPosition(routeData.get(Tables.ROUTE_ID),routeId));
        }else {
            routeSpinner.setAdapter(null);

        }

        // location type spinner adapter setup
        if (locationData != null && locationData.size()>0) {
            SpinnerAdapter locationTypeSpinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, locationData.get(Tables.LOCATION_location_name));
            locationTypeSpinner.setAdapter(locationTypeSpinnerAdapter);
        }else {
            locationTypeSpinner.setAdapter(null);

        }



//        territorySpinner.requestFocus();
        thanaSpinner.requestFocus();
        routeSpinner.requestFocus();
        locationTypeSpinner.requestFocus();

        /*territorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                territoryId = territoryData.get(Tables.TERRITORY_territory_id).get(position);
                Log.e("Terr Spinn Select", "onItemSelected: "+territoryId );

                setAdapterToThanaSpinner();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        thanaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                thanaId = thanaData.get(Tables.THANA_TH_id).get(position);
                Log.e("Thana Spinn Select", "onItemSelected: "+thanaId );

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                routeId = routeData.get(Tables.ROUTE_ID).get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.e("loc id befor select", "onCreate: "+locationId );
        locationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                locationId = locationData.get(Tables.LOCATION_location_id).get(position);



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        marketNameTv = findViewById(R.id.market_name_tv);
        //marketNameTv.setHint(Html.fromHtml("Market Name"+mark));
        addressTv = findViewById(R.id.address_tv);

        //........EDIT...............//
        if (market_id_byIntent != null){

            StaticFlags.MARKET_ID_FOR_POSITION = market_id_byIntent;

           // Toast.makeText(CreateNewMarket.this,""+market_id_byIntent,Toast.LENGTH_SHORT).show();

            lable.setText("Market Update");
            saveBtn.setText("Update");


            JSONObject primaryData = new JSONObject();
            try {
                primaryData.put("mac",basic.getPreference("mac"));
                primaryData.put("sales_person_id",basic.getPreference("sales_person_id"));
                primaryData.put("market_id",market_id_byIntent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //basic.getResponceData(URL.MarketDetails, String.valueOf(primaryData),801);

            ProgressDialog dailog = CheckConnection(CreateNewMarket.this,"Market Details Loading...");
            if (dailog==null)
                return;
            getJAPi().MarketDetails(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();
                        markets = new HashMap<>();

                        markets.put(Tables.MARKETS_market_id,jsonObject.getJSONArray("markets").getJSONObject(0).getString("market_id"));
                        markets.put(Tables.MARKETS_market_name,jsonObject.getJSONArray("markets").getJSONObject(0).getString("market_name"));
                        markets.put(Tables.MARKETS_thana_id,jsonObject.getJSONArray("markets").getJSONObject(0).getString("thana_id"));
                        markets.put(Tables.MARKETS_location_type_id,jsonObject.getJSONArray("markets").getJSONObject(0).getString("location_type_id"));
                        markets.put(Tables.MARKETS_address,jsonObject.getJSONArray("markets").getJSONObject(0).getString("address"));
                        markets.put(Tables.MARKETS_root_id,jsonObject.getJSONArray("markets").getJSONObject(0).getString("route_id"));

                        // markets = ds.getIndivMarketDetails(market_id_byIntent);


                        routeSelection = getPosition(routeData.get(Tables.ROUTE_ID),markets.get(Tables.MARKETS_root_id));
                        thanaSelection = getPosition(thanaData.get(Tables.THANA_TH_id),markets.get(Tables.MARKETS_thana_id));
                        locationaSelection = getPosition(locationData.get(Tables.LOCATION_location_id),markets.get(Tables.MARKETS_location_type_id));

                        routeSpinner.setSelection(routeSelection);
                        thanaSpinner.setSelection(thanaSelection);
                        locationTypeSpinner.setSelection(locationaSelection);

                        marketNameTv.setText(markets.get(Tables.MARKETS_market_name));
                        addressTv.setText(markets.get(Tables.MARKETS_address));

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });


        }


        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( CreateNewMarket.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page_from==25){
                    Intent intent = new Intent(CreateNewMarket.this,Create_New_Memo.class);
                    startActivity(intent);
                    finish();

                }else {

                    Intent intent = new Intent(CreateNewMarket.this, MarketList.class);
                    intent.putExtra("resume", 1);
                    startActivity(intent);
                    finish();
                }
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    address = addressTv.getText().toString().trim();
                    marketName = marketNameTv.getText().toString().trim();

                    dataForMarketCreate.put(Tables.MARKETS_Territory_id,territoryId);
                   // dataForMarketCreate.put(Tables.SR_ID, salesPersonId);
                    dataForMarketCreate.put(Tables.MARKETS_thana_id, thanaId);
                    dataForMarketCreate.put(Tables.ROUTE_ID, routeId);
                    dataForMarketCreate.put(Tables.MARKETS_location_type_id, locationId);
                    dataForMarketCreate.put(Tables.MARKETS_market_name, marketName);
                    dataForMarketCreate.put(Tables.MARKETS_is_active, "1");
                    dataForMarketCreate.put(Tables.MARKETS_is_pushed, "0");
                    dataForMarketCreate.put(Tables.MARKETS_address, address);


                    if (thanaId != null && locationId != null && routeId != null && !TextUtils.isEmpty(marketName)) {
                    Log.e("market_id_byIntent",String.valueOf(market_id_byIntent==null));
                        if (market_id_byIntent != null) {
                            //update here

                            //................Push to Server..............//
                            //use code 202
                            JSONObject marketObj = new JSONObject();
                            JSONArray marketList = new JSONArray();
                            JSONObject market = new JSONObject();
                            try {
                                //Date currentTime = Calendar.getInstance().getTime();
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date dateobj = new Date();
                                String currentTime = df.format(dateobj);


                               /* for (String i: dataForMarketCreate.keySet()){
                                    if (i.equals(Tables.MARKETS_market_id)){
                                        market.put("temp_id",market_id_byIntent);
                                    }else if (i.equals(Tables.SR_ID)){
                                        continue;

                                    }
                                    else {
                                        market.put(i, dataForMarketCreate.get(i));
                                    }
                                }*/
                                market.put("territory_id",basic.getPreference("territory_id"));
                                market.put("code","0");
                              //  market.put("updated_at",currentTime);

                                marketList.put(market);
                                marketObj.put("mac",basic.getPreference("mac"));
                                marketObj.put("sales_person_id",basic.getPreference(SR_ID));
                                marketObj.put("market_list",marketList);

                                Log.e("hashmap1", "onClick: "+dataForMarketCreate );
                                Log.e("json test", "onClick: "+marketObj );

                                //api call to push
                               // basic.getResponceData(URL.CreateMarket, marketObj.toString(), 202);

                                ds.updateMarket(dataForMarketCreate,market_id_byIntent);





                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }





                        } else {
                            dataForMarketCreate.put(Tables.MARKETS_market_id, "M"+salesPersonId + System.currentTimeMillis());
                            //................ push to server..........//
                            JSONObject marketObj = new JSONObject();
                            JSONArray marketList = new JSONArray();
                            JSONObject market = new JSONObject();
                            try {
                                //Date currentTime = Calendar.getInstance().getTime();
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date dateobj = new Date();
                                String currentTime = df.format(dateobj);


                                for (String i: dataForMarketCreate.keySet()){
                                    if (i.equals(Tables.MARKETS_market_id)){
                                        market.put("temp_id","mk"+dataForMarketCreate.get(i));
                                    }else if (i.equals(Tables.SR_ID)){
                                        continue;

                                    }
                                    else {
                                        market.put(i, dataForMarketCreate.get(i));
                                    }
                                }
                                market.put("territory_id",basic.getPreference("territory_id"));
                                market.put("code","0");
                               // market.put("updated_at",currentTime);

                                marketList.put(market);
                                marketObj.put("mac","354214080719502");
                                marketObj.put("sales_person_id","20423");
                                marketObj.put("market_list",marketList);

                                Log.e("hashmap", "onClick: "+dataForMarketCreate );
                                Log.e("json test", "onClick: "+marketObj );

                                //api call to push
                                //basic.getResponceData(URL.CreateMarket, marketObj.toString(), 201);


                                ds.insertIntoMarket(dataForMarketCreate);



                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }


                        if (page_from==25){
                            savePreference("MarketID", dataForMarketCreate.get("temp_id"));
                            Intent intent = new Intent(CreateNewMarket.this,Create_New_Memo.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                        Intent intent = new Intent(CreateNewMarket.this,MarketList.class);
                        intent.putExtra("resume",2);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(CreateNewMarket.this, "Please fill up mandatory fields ", Toast.LENGTH_SHORT).show();
                    }


            }
        });


    }

    private void setAdapterToThanaSpinner() {

        if (territoryId != null && !territoryId.equals("0")) {

            thanaData = ds.getThanaList(territoryId);
            Log.e("null check", "setAdapterToThanaSpinner: "+thanaData );

            if (thanaData != null && thanaData.size()>0) {
                SpinnerAdapter thanaSpinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, thanaData.get(Tables.THANA_NAME));
                thanaSpinner.setAdapter(thanaSpinnerAdapter);
            }else {
                thanaSpinner.setAdapter(null);
            }
        }
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    public int getPosition(ArrayList<String>data,String id){

        int position = data.indexOf(id);

        return  position;
    }


    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

        Log.e("server response", "OnServerResponce: "+jsonObject );
        if (RequestCode == 201){
            int response = 0;
            try {
                response = jsonObject.getJSONObject("market").getInt("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (response == 1  ){
                // add add market id to dataForMarketCreate from response
                try {
                    String marketIdFinal = jsonObject.getJSONObject("market").getJSONArray("replaced_relation").getJSONObject(0).getString("new_id");
                    dataForMarketCreate.put(Tables.MARKETS_market_id,marketIdFinal);
                    dataForMarketCreate.remove(Tables.SR_ID);
                    StaticFlags.MARKET_ID_FOR_POSITION= marketIdFinal;
                    if (page_from==25)
                        savePreference("MarketID", marketIdFinal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StaticFlags.MARKET_THANA_ID = thanaId;

                //create market and push to server

                long id = ds.insertIntoMarket(dataForMarketCreate);
                Log.e("after response", "OnServerResponce: "+dataForMarketCreate );



                /*addressTv.setText("");
                marketNameTv.setText("");
                routeSpinner.setSelection(0);
                thanaSpinner.setSelection(0);
                locationTypeSpinner.setSelection(0);*/
                // territorySpinner.setSelection(0);
                if (id > 0) {

                    Toast.makeText(CreateNewMarket.this, "Market Created ", Toast.LENGTH_LONG).show();
                    saveBtn.setEnabled(false);
                } else {
                    Toast.makeText(CreateNewMarket.this, "Whoops !", Toast.LENGTH_SHORT).show();
                }
                if (page_from==25){
                    Intent intent = new Intent(CreateNewMarket.this,Create_New_Memo.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                Intent intent = new Intent(CreateNewMarket.this,MarketList.class);
                intent.putExtra("resume",2);
                startActivity(intent);
                finish();

            }else {
                Toast.makeText(CreateNewMarket.this, "Server Problem", Toast.LENGTH_LONG).show();
            }
        }

        else if (RequestCode == 202){
            // update local database here

            int response = 0;
            try {
                response = jsonObject.getJSONObject("market").getInt("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (response == 1) {



            dataForMarketCreate.remove(Tables.MARKETS_market_id);
            dataForMarketCreate.remove(Tables.SR_ID);
            long id = ds.updateMarket(dataForMarketCreate,market_id_byIntent);
            if (id >0) {
                saveBtn.setEnabled(false);
                Toast.makeText(CreateNewMarket.this, "Updated", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(CreateNewMarket.this, "Whoops !", Toast.LENGTH_SHORT).show();
            }

            StaticFlags.MARKET_ID_FOR_POSITION = market_id_byIntent;
            StaticFlags.MARKET_THANA_ID = thanaId;

                Intent intent = new Intent(CreateNewMarket.this,MarketList.class);
                intent.putExtra("resume",2);
                startActivity(intent);
                finish();


            }
        }


    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(CreateNewMarket.this,"No Internet Connection",Toast.LENGTH_SHORT).show();

    }

    public void savePreference(String key, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {

            if (page_from==25){
                Intent intent = new Intent(CreateNewMarket.this,Create_New_Memo.class);
                startActivity(intent);
                finish();

            }else {

                Intent intent = new Intent(CreateNewMarket.this, MarketList.class);
                intent.putExtra("resume", 1);
                startActivity(intent);
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
