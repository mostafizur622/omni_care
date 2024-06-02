package com.srapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.BonusPartyAffAdapter;
import com.srapp.Adapter.OutletListAdapter;
import com.srapp.Adapter.ProductAdapter;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Model.Product;
import com.srapp.Util.StaticFlags;
import com.srapp.print.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.LinearLayout.VERTICAL;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bonus_Party_Affiliation extends ParentActivity implements BasicFunctionListener {

    RecyclerView recyclerView;
    String[] SPINNERLIST = {"Khilkhet", "Khilgaon", "Gulshan", "Badda"};
    ImageView imageview;
    Spinner thanaSpinner, marketSpinner, bonusSpinner;
    ArrayList<String> outlets, bonuses;
    BonusPartyAffAdapter outletListAdapter;
    Data_Source ds;
    private HashMap<String, ArrayList<String>> outletList;
    ArrayList<String>outletIdList;
    ArrayList<String>outletNameList;
    private HashMap<String, ArrayList<String>> markets,thanas;
    private String thanaId;
    private String marketId;
    ArrayList<String>all,blank;
    int thanSpinnerPosition=0, marketSpinnerPosition =0;
    BasicFunction basicFunction;
    int initialPageIndex = 1;
    String nowCaling = "all_data";
    String WITH_MARKET_ID = "with_market_id";
    String WITH_THANA_ID = "with_thana_id";
    Boolean isLoading = false;
    ArrayList<String>outletThanaIdList;
    ArrayList<String>outletMarketIdList;
    private final int THREAT_SHOT = 5;
    Button submitBtn;
    ArrayList<Boolean>checkBoxStateList;
    ArrayList<Integer>checkBoxStateListint;
    String bonusId;
    int forBonusSelect = 0;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus__party__affiliation);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        ds = new Data_Source(this);
        basicFunction = new BasicFunction(this,this);
        thanaSpinner = findViewById(R.id.thana_spinner);
        marketSpinner = findViewById(R.id.market_spinner);
        bonusSpinner = findViewById(R.id.bonus_spinner);
        submitBtn = findViewById(R.id.submit_btn);

        all = new ArrayList<>();

        all.add("All");
        blank = new ArrayList<>();
        blank.add("");
        bonuses = new ArrayList<>();
        bonuses.add("Small Bonus");
        bonuses.add("Big Bonus");

        final SpinnerAdapter bonusData = new SpinnerAdapter(this, R.layout
                .spinner_item, bonuses);
        bonusSpinner.setAdapter(bonusData);

        markets = new HashMap<>();
        thanas = new HashMap<>();
        outletList = new HashMap<>();
        outlets = new ArrayList<>();
        outletIdList = new ArrayList<>();
        outletNameList = new ArrayList<>();
        outletThanaIdList = new ArrayList<>();
        outletMarketIdList = new ArrayList<>();

        thanas = ds.getThanaList("no");

        //add adapter to thana
        if (thanas != null && thanas.size()>0) {

            thanas.get(Tables.THANA_TH_id).add(0,"x0x");
            thanas.get(Tables.THANA_NAME).add(0,"All");

            SpinnerAdapter thanaData = new SpinnerAdapter(this, R.layout
                    .spinner_item, thanas.get(Tables.THANA_NAME));
            thanaSpinner.setAdapter(thanaData);

        }

        recyclerView = findViewById(R.id.bonus_party_recycler);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);
        //outletListAdapter = new OutletListAdapter();

        thanaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);
                thanSpinnerPosition = position;

                thanaId = thanas.get(Tables.THANA_TH_id).get(position);
                Log.e("thana id seletion", "onItemSelected: "+thanaId );

                if (!thanaId.equals("x0x") ){
                    //set adapter to market
                    markets = ds.getMarketData("no",thanaId);
                    Log.e(" thana id != xox", "onItemSelected: "+markets );
                    if (markets!=null && markets.size()>0) {
                        markets.get(Tables.MARKETS_market_id).add(0, "x0x");
                        markets.get(Tables.MARKETS_market_name).add(0, "All");
                        setSpinnerAdapter(markets, marketSpinner, Tables.MARKETS_market_id, Tables.MARKETS_market_name);
                    }
                    else {
                        //markets = null;
                        Log.e("markets...", "onItemSelected: "+"entered to else for markets "+markets );
                        final SpinnerAdapter marketDataAdapter = new SpinnerAdapter(Bonus_Party_Affiliation.this, R.layout
                                .spinner_item, blank);
                        marketSpinner.setAdapter(marketDataAdapter);

                    }

                }
                else {
                    // here is the for the very first adapter setup for market spinner
                    /*final SpinnerAdapter marketDataAdapter = new SpinnerAdapter(OutletList.this, R.layout
                            .spinner_item, all);
                    marketSpinner.setAdapter(marketDataAdapter);*/

                    markets = ds.getMarketData("no","no");
                    if (markets!=null && markets.size()>0) {
                        markets.get(Tables.MARKETS_market_id).add(0, "x0x");
                        markets.get(Tables.MARKETS_market_name).add(0, "All");
                        setSpinnerAdapter(markets, marketSpinner, Tables.MARKETS_market_id, Tables.MARKETS_market_name);
                    }
                    else {
                        //markets = null;
                        final SpinnerAdapter marketDataAdapter = new SpinnerAdapter(Bonus_Party_Affiliation.this, R.layout
                                .spinner_item, blank);
                        marketSpinner.setAdapter(marketDataAdapter);

                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        marketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                marketSpinnerPosition = position;

                Log.e("market select pos", "onItemSelected: "+position );

                Log.e("crashhhh", "onItemSelected.......>>: thana id "+thanaId+" mrkt id "+marketId+" pos:"+position );
                Log.e("crashhhh", "onItemSelected.......>>: market id list "+markets.get(Tables.MARKETS_market_id) );

                /*if (!thanaId.equals("x0x") && position != 0) {
                    marketId = markets.get(Tables.MARKETS_market_id).get(position);
                }else if (!thanaId.equals("x0x") && position == 0){
                    marketId.equals("x0x");
                }*/
                if (markets != null & markets.size()>0) {
                    marketId = markets.get(Tables.MARKETS_market_id).get(position);
                }else {
                    marketId = "x0x";
                }
                //----------------CALL API FOR RECYCLERVIEW--------------//

                //set adapter to recycler view for all filter
                if (thanaId.equals("x0x") && position==0 ) {

                    Log.e("market id crash...", "onItemSelected: "+marketId );
                    //data wii come from server here
                    /*outletList = ds.getOutletData("no", "no");
                    outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);*/

                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    initialPageIndex = 1;


                  //  basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),401);

                    Getdata(primaryData);



                }


                // filter for thana
                else if (!thanaId.equals("x0x") && position == 0) {

                    /*outletList = ds.getOutletData(thanaId, "no");
                    outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);*/
                    //............. call api....................

                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                        primaryData.put("thana_id",thanaId);
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    nowCaling = WITH_THANA_ID;
                    initialPageIndex = 1;

                    Log.e("thana filter req", "onItemSelected: "+primaryData );

                    //basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),401);
                    Getdata(primaryData);
                    //............api call end....................
                }

                //filter for market when thana id is selected
                else if (!thanaId.equals("x0x") && !marketId.equals("x0x")){

                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                        primaryData.put("market_id",marketId);
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    nowCaling = WITH_THANA_ID;
                    initialPageIndex = 1;

                    Log.e("thana filter req", "onItemSelected: "+primaryData );


                    //basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),401);
                    Getdata(primaryData);

                }

                //filter for market when thana id is selected
                else if (thanaId.equals("x0x") && !marketId.equals("x0x")){

                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                        primaryData.put("market_id",marketId);
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("thana filter req", "onItemSelected: "+primaryData );

                    nowCaling = WITH_MARKET_ID;
                    initialPageIndex = 1;
                    Getdata(primaryData);
                    //basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),401);

                }

                //--------------------END API CALL--------------------------//


                    /*else {

                        outletList = ds.getOutletData(thanaId, "no");
                        outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);

                        recyclerView.setLayoutManager(new LinearLayoutManager(OutletList.this));
                        if (outlets != null && outlets.size() > 0) {
                            outletListAdapter.setOutletNames(outlets);
                            recyclerView.setAdapter(outletListAdapter);
                        } else {
                            recyclerView.setAdapter(null);
                        }
                    }*/



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }



        });
        bonusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                if (position==0){
                    bonusId = "1";

                }else {
                    bonusId = "2";


                }

                //----------------CALL API FOR RECYCLERVIEW--------------//
                if (forBonusSelect != 0) {

                    //set adapter to recycler view for all filter
                    if (thanaId.equals("x0x") && marketSpinnerPosition == 0) {

                        Log.e("market id crash...", "onItemSelected: " + marketId);
                        //data wii come from server here
                    /*outletList = ds.getOutletData("no", "no");
                    outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);*/

                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac", basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                            primaryData.put("page", 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        initialPageIndex = 1;


                       // basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData), 401);
                        Getdata(primaryData);

                    }


                    // filter for thana
                    else if (!thanaId.equals("x0x") && marketSpinnerPosition == 0) {

                    /*outletList = ds.getOutletData(thanaId, "no");
                    outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);*/
                        //............. call api....................

                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac", basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                            primaryData.put("thana_id", thanaId);
                            primaryData.put("page", 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        nowCaling = WITH_THANA_ID;
                        initialPageIndex = 1;

                        Log.e("thana filter req", "onItemSelected: " + primaryData);


                      //  basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData), 401);
                        Getdata(primaryData);
                        //............api call end....................


                    }

                    //filter for market when thana id is selected
                    else if (!thanaId.equals("x0x") && !marketId.equals("x0x")) {


                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac", basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                            primaryData.put("market_id", marketId);
                            primaryData.put("page", 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        nowCaling = WITH_THANA_ID;
                        initialPageIndex = 1;

                        Log.e("thana filter req", "onItemSelected: " + primaryData);


                      //  basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData), 401);
                        Getdata(primaryData);

                    }

                    //filter for market when thana id is selected
                    else if (thanaId.equals("x0x") && !marketId.equals("x0x")) {


                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac", basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                            primaryData.put("market_id", marketId);
                            primaryData.put("page", 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e("thana filter req", "onItemSelected: " + primaryData);

                        nowCaling = WITH_MARKET_ID;
                        initialPageIndex = 1;


                        //basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData), 401);
                        Getdata(primaryData);

                    }

                    //--------------------END API CALL--------------------------//
                }

                Log.e("bonus---->", "onItemSelected: "+bonusId+" thanaId "+thanaId+"  marketId "+marketId+" mkt pos "+marketSpinnerPosition );




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }



        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Bonus_Party_Affiliation.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Bonus_Party_Affiliation.this, SR_Account_Activity.class));
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("initial......>", "onClick: "+outletListAdapter.initialState );
                Log.e("checked......>", "onClick: "+outletListAdapter.checkState );

                Log.e("selected......>", "onClick: "+outletListAdapter.getSelected() );
                Log.e("unSelected......>", "onClick: "+outletListAdapter.getUnSelected() );
                JSONObject bonusPartyData = new JSONObject();
                JSONObject primaryData = new JSONObject();
                JSONObject mainData = new JSONObject();
                JSONArray selectedOutlets = new JSONArray();
                JSONArray unSelectedOutlets = new JSONArray();
                try {
                    primaryData.put("mac",basicFunction.getPreference("mac"));
                    primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                    mainData.put("bonus_type_id", bonusId);
                    if (outletListAdapter.getSelected() != null && outletListAdapter.getSelected().size()>0) {
                        for (int k = 0; k < outletListAdapter.getSelected().size(); k++) {
                            selectedOutlets.put(outletListAdapter.getSelected().get(k));
                        }
                    }
                    if (outletListAdapter.getUnSelected() != null && outletListAdapter.getUnSelected().size()>0) {
                        for (int k = 0; k < outletListAdapter.getUnSelected().size(); k++) {
                            unSelectedOutlets.put(outletListAdapter.getUnSelected().get(k));
                        }
                    }
                    //mainData.put("outlets", selectedOutlets);
                    mainData.put("selected_outlets", selectedOutlets);
                    mainData.put("unselected_outlets", unSelectedOutlets);

                    primaryData.put("details",mainData);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (outletListAdapter.getSelected() != null && outletListAdapter.getSelected().size()>0 || outletListAdapter.getUnSelected() != null && outletListAdapter.getUnSelected().size() > 0) {

                    //basicFunction.getResponceData(URL.BonusParty, String.valueOf(primaryData), 101);
                    ProgressDialog dailog = CheckConnection(Bonus_Party_Affiliation.this,"Bonus Party Loading...");
                    if (dailog==null)
                        return;
                    getJAPi().BonusParty(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                dailog.dismiss();
                                isLoading = false;

                                try {
                                    if (jsonObject.getJSONObject("res").getString("status").equals("1")){

                                        Log.e("size--->", "OnServerResponce: "+outletListAdapter.selected.size() );
                                        Log.e("size--->", "OnServerResponce: "+outletListAdapter.unSelected.size() );

                                        if (outletListAdapter.selected != null && outletListAdapter.selected.size()>0 ){
                                            for (int j = 0; j<outletListAdapter.selected.size();j++){
                                                Log.e("index--->", "OnServerResponce: "+outletListAdapter.initialState.indexOf(outletListAdapter.selected.get(j)) );
                                                outletListAdapter.initialState.set(outletIdList.indexOf(outletListAdapter.selected.get(j)),1);
                                            }
                                        }

                                        if (outletListAdapter.unSelected != null &&outletListAdapter.unSelected.size()>0 ){
                                            for (int j = 0; j<outletListAdapter.unSelected.size();j++){
                                                outletListAdapter.initialState.set(outletIdList.indexOf(outletListAdapter.unSelected.get(j)),0);
                                            }
                                        }

                                        outletListAdapter.selected.clear();
                                        outletListAdapter.unSelected.clear();

                                        Toast.makeText(Bonus_Party_Affiliation.this,"Save SuccessFully",Toast.LENGTH_SHORT).show();

                                    }

                                    setupDetails();

                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                }
                else {
                    Toast.makeText(Bonus_Party_Affiliation.this,"No Change Occurred",Toast.LENGTH_SHORT).show();
                }

                Log.e("json----->", "onClick: "+primaryData );
            }
        });

    }

    private void Getdata(JSONObject primaryData) {
        ProgressDialog dailog = CheckConnection(Bonus_Party_Affiliation.this,"Getting Outlet List...");
        if (dailog==null)
            return;
        getJAPi().OutletList(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    isLoading = false;
                    forBonusSelect = 1;

                    outletList = new HashMap<>();
                    outletIdList = new ArrayList<>();
                    outletNameList = new ArrayList<>();
                    outlets = new ArrayList<>();
                    outletMarketIdList = new ArrayList<>();
                    outletThanaIdList = new ArrayList<>();
                    checkBoxStateList = new ArrayList<>();
                    checkBoxStateListint = new ArrayList<>();
                    try {
                        for (int j = 0; j < jsonObject.getJSONArray("outlets").length(); j++) {
                            if (!jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name").equals("")) {


                                String checked = jsonObject.getJSONArray("outlets").getJSONObject(j).getString("bonus_type_id");
                                Log.e("checked-------->", "OnServerResponce: "+checked );
                        /*if (checked.equals("null")){
                            Log.e("null--------->", "OnServerResponce: "+"its null" );
                        }*/

                                if (!checked.equals("null") && checked.equals(bonusId)){
                                    checkBoxStateList.add(true);
                                    checkBoxStateListint.add(1);
                                    outletIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_id"));
                                    outletNameList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name"));
                                    outletThanaIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("thana_id"));
                                    outletMarketIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("market_id"));
                                }else if (checked.equals("null") || checked.equals("0") || checked.equals("")){
                                    Log.e("blank state_or_free", "OnServerResponce: "+"entered" );
                                    checkBoxStateList.add(false);
                                    checkBoxStateListint.add(0);
                                    outletIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_id"));
                                    outletNameList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name"));
                                    outletThanaIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("thana_id"));
                                    outletMarketIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("market_id"));
                                }
                            }

                        }

                        Log.e("------->cb list", "OnServerResponce:-----check box state0--------> "+checkBoxStateList );

                        outletList.put(Tables.OUTLETS_ID, outletIdList);
                        outletList.put(Tables.OUTLETS_OUTLET_NAME, outletNameList);
                        outletList.put(Tables.OUTLETS_THANA_ID, outletThanaIdList);
                        outletList.put(Tables.OUTLETS_MARKET_ID, outletMarketIdList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Log.e("json response", "OnServerResponce:" + jsonObject.getJSONArray("outlets").length());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);



                    if (outlets != null && outlets.size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(Bonus_Party_Affiliation.this));
                /*checkBoxStateList = new ArrayList<>();
                for (int j = 0 ; j<outlets.size();j++){
                    checkBoxStateList.add(false);

                }*/
                        outletListAdapter = new BonusPartyAffAdapter(outletList,checkBoxStateList,checkBoxStateListint);
                        recyclerView.setAdapter(outletListAdapter);
                        setupDetails();
                        Log.e("50 data setes", "OnServerResponce: Adapter seted...");
                    } else {
                        recyclerView.setAdapter(null);
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
    }


    private void setSpinnerAdapter(HashMap<String,ArrayList<String> >dataMap, Spinner spinner, String idKey, String nameKey) {
        if (dataMap != null && dataMap.size()>0) {

            /*dataMap.get(idKey).add(0, "x0x");
            dataMap.get(nameKey).add(0, "All");*/

            //add adapter to market spinner

            final SpinnerAdapter marketDataAdapter = new SpinnerAdapter(Bonus_Party_Affiliation.this, R.layout
                    .spinner_item, dataMap.get(nameKey));
            spinner.setAdapter(marketDataAdapter);
        }else {
            spinner.setAdapter(null);
        }
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, final int i) {




    }

    public void setupDetails(){
        final LinearLayoutManager linManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                //Log.e("entered scrolled.....", "onScrolled: "+"On Scroll.." );

                if (!isLoading && linManager.getItemCount() - THREAT_SHOT== linManager.findLastVisibleItemPosition()){

                    Log.e("entr scrolled..0n 402", "onScrolled: "+"On Scroll.." );

                    initialPageIndex++;

                    loadMoreData(initialPageIndex);

                    // Toast.makeText(Bonus_Party_Affiliation.this," "+initialPageIndex,Toast.LENGTH_SHORT).show();

                    Log.e("last_position", "onScrolled: "+linManager.findLastVisibleItemPosition() );
                }
            }
        });
    }


    public void loadMoreData(int page){
        isLoading= true;
        Log.e("page_number......", ".......>>loadMoreData: "+page );
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac",basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
            if (nowCaling.equals(WITH_MARKET_ID)){
                primaryData.put("market_id",marketId);

            }else if (nowCaling.equals(WITH_THANA_ID)){
                primaryData.put("thana_id",thanaId);
            }
            primaryData.put("page",page);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),402);

        ProgressDialog dailog = CheckConnection(Bonus_Party_Affiliation.this,"Outlets Loading...");
        if (dailog==null)
            return;
        getJAPi().OutletList(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();


                    isLoading = false;


                    try {
                        for (int j = 0; j < jsonObject.getJSONArray("outlets").length(); j++) {
                            if (!jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name").equals("")) {
                                String checked = jsonObject.getJSONArray("outlets").getJSONObject(j).getString("bonus_type_id");

                                if (!checked.equals("null") && checked.equals(bonusId)){
                                    checkBoxStateList.add(true);
                                    checkBoxStateListint.add(1);
                                    outletIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_id"));
                                    outletNameList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name"));
                                    outletThanaIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("thana_id"));
                                    outletMarketIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("market_id"));
                                }else if (checked.equals("null") || checked.equals("0") || checked.equals("")){
                                    Log.e("blank state_or_free", "OnServerResponce: "+"entered" );
                                    checkBoxStateList.add(false);
                                    checkBoxStateListint.add(0);
                                    outletIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_id"));
                                    outletNameList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name"));
                                    outletThanaIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("thana_id"));
                                    outletMarketIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("market_id"));
                                }
                            }

                        }

                        outletList.put(Tables.OUTLETS_ID, outletIdList);
                        outletList.put(Tables.OUTLETS_OUTLET_NAME, outletNameList);
                        outletList.put(Tables.OUTLETS_THANA_ID, outletThanaIdList);
                        outletList.put(Tables.OUTLETS_MARKET_ID, outletMarketIdList);
                        setupDetails();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Log.e("json response", "OnServerResponce:" + jsonObject.getJSONArray("outlets").length());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);


                    if (outlets != null && outlets.size() > 0) {


                        recyclerView.getAdapter().notifyDataSetChanged();

                        Log.e("50 data setes", "OnServerResponce: Adapter seted...");
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

        //isLoading=false;

    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(Bonus_Party_Affiliation.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent( Bonus_Party_Affiliation.this, SR_Account_Activity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( Bonus_Party_Affiliation.this, SR_Account_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private List<Product> getList() {
        List<Product> order_reportList = new ArrayList<>();
        order_reportList.add(new Product(1,"Raja Super",0,false));
        order_reportList.add(new Product(2,"Hero 3s",0,false));
        order_reportList.add(new Product(3,"Xtreme 3 in 1",0,false));
        order_reportList.add(new Product(4,"Amore Gold 3",0,false));
        order_reportList.add(new Product(4,"Raja Plain",0,false));
        order_reportList.add(new Product(5,"Panther Dotted",0,false));
        order_reportList.add(new Product(6,"Noret-28",0,false));
        order_reportList.add(new Product(7,"Femicon",0,false));
        order_reportList.add(new Product(8,"Orsaline-N",0,false));
        order_reportList.add(new Product(9,"Joya 8s wings",0,false));
        order_reportList.add(new Product(10,"Tast me(mango)",0,false));
        return order_reportList;
    }

}
