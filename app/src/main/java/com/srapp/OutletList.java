package com.srapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.OutletListAdapter;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.ParentActivity;
import com.srapp.Util.RecyclerTouchListener;
import com.srapp.Util.StaticFlags;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.widget.LinearLayout.VERTICAL;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletList extends ParentActivity implements BasicFunctionListener {
    Spinner thanaSpinner, marketSpinner;
    RecyclerView recyclerView;
    ArrayList<String> outlets;
    OutletListAdapter outletListAdapter;
    Button createOutletBtn;
    ImageView imageView;
    Data_Source ds;
    BasicFunction basicFunction;
    private HashMap<String, ArrayList<String>> outletList;
    ArrayList<String>outletIdList;
    ArrayList<String>outletNameList;
    ArrayList<String>outletThanaIdList;
    ArrayList<String>outletMarketIdList;
    private HashMap<String, ArrayList<String>> markets,thanas;
    private String thanaId;
    private String marketId;
    ArrayList<String>all,blank;
    int thanSpinnerPosition=0, marketSpinnerPosition =0;
    Boolean isLoading = false;
    private final int THREAT_SHOT = 5;
    int initialPageIndex = 1;
    String nowCaling = "all_data";
    String WITH_MARKET_ID = "with_market_id";
    String WITH_THANA_ID = "with_thana_id";
    int thanaSpinResume = 0,marketSpinResume = 0;
    TextView user_txt_view;
    ImageView homeBtn,backBtn;
    int rcvIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_list);

        basicFunction = new BasicFunction(this,this);

        ds = new Data_Source(this);
        all = new ArrayList<>();
        all.add("All");
        blank = new ArrayList<>();
        blank.add("");
        user_txt_view = findViewById(R.id.user_txt_view);
        user_txt_view.setText(basicFunction.getPreference(SR_ID));
        Intent intent = getIntent();
        rcvIntent = intent.getIntExtra("resume",0);

        thanaSpinner = findViewById(R.id.thana_spinner);
        marketSpinner = findViewById(R.id.market_spinner);

        markets = new HashMap<>();
        thanas = new HashMap<>();
        outletIdList = new ArrayList<>();
        outletNameList = new ArrayList<>();
        outletThanaIdList = new ArrayList<>();
        outletMarketIdList = new ArrayList<>();

        thanas = ds.getRouteList("no");


        Log.e("thana test", "onCreate: "+thanas+" size "+thanas.size());



        //add adapter to thana
        if (thanas != null && thanas.size()>0) {

            thanas.get(Tables.THANA_TH_id).add(0,"x0x");
            thanas.get(Tables.THANA_NAME).add(0,"All");


            SpinnerAdapter thanaData = new SpinnerAdapter(this, R.layout
                    .spinner_item, thanas.get(Tables.THANA_NAME));
            thanaSpinner.setAdapter(thanaData);

        }




        recyclerView = findViewById(R.id.outlet_list_recycler);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);


        outletListAdapter = new OutletListAdapter();


        outletList = new HashMap<>();
        outlets = new ArrayList<>();

        Log.e("------>", "onCreate: "+rcvIntent );

        if (rcvIntent == 1){
            thanaSpinner.setSelection(StaticFlags.OUTLET_THANA_SPINNER_POSITION);
        }else if (rcvIntent == 2){
            thanaSpinner.setSelection(getPosition(thanas.get(Tables.THANA_TH_id),StaticFlags.OUTLET_THANA_ID));
        }

        thanaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);
                thanSpinnerPosition = position;

                thanaId = thanas.get(Tables.THANA_TH_id).get(position);
                Log.e("thana id seletion", "onItemSelected: "+thanaId );

                StaticFlags.OUTLET_THANA_SPINNER_POSITION = position;
                StaticFlags.OUTLET_THANA_ID = thanaId;

                if (!thanaId.equals("x0x") ){


                    //set adapter to market
                    markets = ds.getMarketData(thanaId,"no");
                    Log.e(" thana id != xox", "onItemSelected: "+markets );
                    if (markets!=null && markets.size()>0) {
                        markets.get(Tables.MARKETS_market_id).add(0, "x0x");
                        markets.get(Tables.MARKETS_market_name).add(0, "All");
                        setSpinnerAdapter(markets, marketSpinner, Tables.MARKETS_market_id, Tables.MARKETS_market_name);
                        //if navigates from outlet view
                        if (rcvIntent == 1 ){
                            marketSpinner.setSelection(StaticFlags.OUTLET_MARKET_SPINNER_POSITION);
                        }else if (rcvIntent == 2){
                            marketSpinner.setSelection(getPosition(markets.get(Tables.MARKETS_market_id),StaticFlags.OUTLET_MARKET_ID));
                        }
                    }
                    else {
                        //markets = null;
                        Log.e("markets...", "onItemSelected: "+"entered to else for markets "+markets );
                        final SpinnerAdapter marketDataAdapter = new SpinnerAdapter(OutletList.this, R.layout
                                .spinner_item, blank);
                        marketSpinner.setAdapter(marketDataAdapter);
                    }

                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                        primaryData.put("route_id",thanaId);
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    nowCaling = WITH_THANA_ID;

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

                        //if navigates from outlet view
                        if (rcvIntent == 1 ){
                            marketSpinner.setSelection(StaticFlags.OUTLET_MARKET_SPINNER_POSITION);
                        }else if (rcvIntent == 2){
                            marketSpinner.setSelection(getPosition(markets.get(Tables.MARKETS_market_id),StaticFlags.OUTLET_MARKET_ID));
                            Log.e("------->", "onItemSelected: market pos--------> "+getPosition(markets.get(Tables.MARKETS_market_id),StaticFlags.OUTLET_MARKET_ID) );
                        }
                    }
                    else {
                        //markets = null;
                        final SpinnerAdapter marketDataAdapter = new SpinnerAdapter(OutletList.this, R.layout
                                .spinner_item, blank);
                        marketSpinner.setAdapter(marketDataAdapter);

                    }


                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                        primaryData.put("route_id","0");
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    nowCaling = "all_data";

                }
                initialPageIndex = 1;
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

                StaticFlags.OUTLET_MARKET_SPINNER_POSITION = position;
                StaticFlags.OUTLET_MARKET_ID = marketId;

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
                    nowCaling=WITH_THANA_ID;

                   // basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),401);
                    getdata(primaryData);

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
                        primaryData.put("route_id",thanaId);
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    nowCaling = WITH_THANA_ID;
                    initialPageIndex = 1;

                    Log.e("thana filter req", "onItemSelected: "+primaryData );


                    //basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),401);
                    getdata(primaryData);
                    //............api call end................................................................
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

                    nowCaling = WITH_MARKET_ID;
                    initialPageIndex = 1;

                    Log.e("thana filter req", "onItemSelected: "+primaryData );


                    //basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),401);
                    getdata(primaryData);

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


                   // basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),401);
                    getdata(primaryData);

                }


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



        /*outletListAdapter.setOnItemClickListener(new OutletListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // initialize in viewholder class ---> itemview.setOnclik listener  line edit.setOnclick
            }

            @Override
            public void onEditClick(int position) {
                Toast.makeText(OutletList.this,"Outlet Id"+outletList.get(Tables.OUTLETS_ID).get(position),Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(OutletList.this,CreateOutlet.class);
                intent.putExtra("outlet_id_byClick",outletList.get(Tables.OUTLETS_ID).get(position));
                startActivity(intent);

            }

            @Override
            public void onViewClick(int position) {
                Log.e("view btn click", "onViewClick: "+position );

                Intent intent = new Intent(OutletList.this,OutletView.class);
                intent.putExtra("outlet_id",outletList.get(Tables.OUTLETS_ID).get(position));
                startActivity(intent);


            }
        });*/

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( OutletList.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( OutletList.this, SR_Account_Activity.class));
                finish();
            }
        });

        createOutletBtn = findViewById(R.id.create_outlet_btn);
        createOutletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

  //              if (ds.checkOutletCreatePermission(getCurrentDateTime())) {
                    startActivity(new Intent(OutletList.this, CreateOutlet.class));
                    finish();
//                }else {
//                    Toast.makeText(OutletList.this,"You Don't Have Permission To Create Outlet",Toast.LENGTH_LONG).show();
//                }
            }
        });


        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(OutletList.this,""+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OutletList.this,CreateOutlet.class);
                intent.putExtra("outlet_id_byClick",outletList.get(Tables.OUTLETS_ID).get(position));
                startActivity(intent);

                if (view.getId() == R.id.view_outlet_item){
                    Log.e("Recycle button check", "onClick: "+"View Buton it is" );
                }else {
                    Log.e("Recycle button check", "onClick: "+"not identified it is"+view.getId() );
            }


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/

    }

    private void getdata(JSONObject primaryData) {

        ProgressDialog dailog = CheckConnection(OutletList.this,"getting Outlets...");
        if (dailog==null)
            return;
        getJAPi().OutletList(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    isLoading = false;

                    outletListAdapter = new OutletListAdapter();
                    outletList = new HashMap<>();
                    outletIdList = new ArrayList<>();
                    outletNameList = new ArrayList<>();
                    outlets = new ArrayList<>();
                    outletMarketIdList = new ArrayList<>();
                    outletThanaIdList = new ArrayList<>();
                    try {
                        for (int j = 0; j < jsonObject.getJSONArray("outlets").length(); j++) {
                            if (!jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name").equals("")) {
                                outletIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_id"));
                                outletNameList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name"));
                                outletThanaIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("thana_id"));
                                outletMarketIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("market_id"));
                            }

                        }

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
                        recyclerView.setLayoutManager(new LinearLayoutManager(OutletList.this));
                        outletListAdapter.setOutletNames(outletNameList);
                        recyclerView.setAdapter(outletListAdapter);
                        if (rcvIntent>0) {
                            recyclerView.scrollToPosition(getPosition(outletIdList, StaticFlags.OUTLET_ID_FOR_SCROLL));
                        }
                        Log.e("50 data setes", "OnServerResponce: Adapter seted...");
                    } else {
                        recyclerView.setAdapter(null);
                    }

                    outletListAdapter.setOnItemClickListener(new OutletListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            // initialize in viewholder class ---> itemview.setOnclik listener  line edit.setOnclick
                        }

                        @Override
                        public void onEditClick(int position) {
                            // Toast.makeText(OutletList.this,"Outlet Id"+outletList.get(Tables.OUTLETS_ID).get(position),Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(OutletList.this,CreateOutlet.class);
                            intent.putExtra("outlet_id_byClick",outletList.get(Tables.OUTLETS_ID).get(position));
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onViewClick(int position) {
                            Log.e("view btn click", "onViewClick: "+position );

                            Intent intent = new Intent(OutletList.this,OutletView.class);
                            intent.putExtra("outlet_id",outletList.get(Tables.OUTLETS_ID).get(position));
                            startActivity(intent);
                            finish();


                        }
                    });


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

            final SpinnerAdapter marketDataAdapter = new SpinnerAdapter(OutletList.this, R.layout
                    .spinner_item, dataMap.get(nameKey));
            spinner.setAdapter(marketDataAdapter);
        }else {
            spinner.setAdapter(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*thanaSpinner.setSelection(thanSpinnerPosition);
        marketSpinner.setSelection(marketSpinnerPosition);
        if (thanSpinnerPosition == 0 && marketSpinnerPosition ==0){
            outletList = ds.getOutletData("no","no");
            outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);
        }else if (thanSpinnerPosition > 0 && marketSpinnerPosition ==0){
            outletList = ds.getOutletData(thanas.get(Tables.THANA_TH_id).get(thanSpinnerPosition),"no");
            outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);

        }else if (thanSpinnerPosition > 0 && marketSpinnerPosition > 0){
            outletList = ds.getOutletData(thanas.get(Tables.THANA_TH_id).get(thanSpinnerPosition),markets.get(Tables.MARKETS_market_id).get(marketSpinnerPosition));
            outlets = outletList.get(Tables.OUTLETS_OUTLET_NAME);

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (outlets!=null && outlets.size()>0) {
            outletListAdapter.setOutletNames(outlets);
            recyclerView.setAdapter(outletListAdapter);
        }*/
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, final int i) {

        Log.e("response code....", "OnServerResponce: "+i );


        Log.e("json response", "OnServerResponce: "+jsonObject+"  size.."+jsonObject.length() );

        if (i == 401) {



        }

        else if (i == 402){







        }



    }

    public void setup(){
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

                if (linManager!=null) {
                    Log.e("testcount",  linManager.getItemCount()- THREAT_SHOT+ "On Scroll.."+linManager.findLastVisibleItemPosition()+" "+isLoading);
                    if (!isLoading && linManager.getItemCount() - THREAT_SHOT == linManager.findLastVisibleItemPosition()) {

                        initialPageIndex++;
                        loadMoreData(initialPageIndex);

                        //  Toast.makeText(OutletList.this," "+initialPageIndex,Toast.LENGTH_SHORT).show();

                        Log.e("last position", "onScrolled: " + linManager.findLastVisibleItemPosition());
                    }else {

                        Log.e("not_in_if", "onScrolled: " + linManager.findLastVisibleItemPosition());
                    }
                }else {
                    Log.e("not_in_if", "onScrolled: " + linManager.findLastVisibleItemPosition());
                }
            }

        });
    }


    public void loadMoreData(int page){
        isLoading= true;
        Log.e("page number......", ".......>>loadMoreData: "+page );
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac",basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
            if (nowCaling.equals(WITH_MARKET_ID) && !marketId.equalsIgnoreCase("x0x")){
                primaryData.put("market_id",marketId);

            }else if (nowCaling.equals(WITH_THANA_ID) && !thanaId.equalsIgnoreCase("x0x")){
                primaryData.put("route_id",thanaId);
            }
            primaryData.put("page",page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //basicFunction.getResponceData(URL.OutletList, String.valueOf(primaryData),402);
        ProgressDialog dailog = CheckConnection(OutletList.this,"Checking...");
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
                                outletIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_id"));
                                outletNameList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("outlet_name"));
                                outletThanaIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("thana_id"));
                                outletMarketIdList.add(jsonObject.getJSONArray("outlets").getJSONObject(j).getString("market_id"));
                            }

                        }

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


                        recyclerView.getAdapter().notifyDataSetChanged();

                        Log.e("50 data setes", "OnServerResponce: Adapter seted...");
                    }
                    setup();
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
    public void OnConnetivityError() {
        Toast.makeText(OutletList.this,"No Internet Connection",Toast.LENGTH_SHORT).show();

    }

    public int getPosition(ArrayList<String>data,String id){

        int position = data.indexOf(id);

        return  position;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            StaticFlags.OUTLET_ID_FOR_SCROLL = null;
            StaticFlags.OUTLET_MARKET_ID = null;
            StaticFlags.OUTLET_THANA_ID = null;
            StaticFlags.OUTLET_MARKET_SPINNER_POSITION = 0;
            StaticFlags.OUTLET_SCROL_POSITION = 0;
            StaticFlags.OUTLET_THANA_SPINNER_POSITION = 0;
            startActivity(new Intent( OutletList.this, SR_Account_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
