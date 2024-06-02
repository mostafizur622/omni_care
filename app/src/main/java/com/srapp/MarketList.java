package com.srapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.MarketListAdapter;
import com.srapp.Adapter.OutletListAdapter;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.StaticFlags;
import com.srapp.print.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.widget.LinearLayout.VERTICAL;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketList extends ParentActivity implements BasicFunctionListener {

    Spinner thanaSpinner;
    Button createMarketBtn;
    RecyclerView recyclerView;
    ArrayList<String> outlets;
    MarketListAdapter marketListAdapter;
    private HashMap<String, ArrayList<String>> thanas;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV;
    Data_Source ds;

    private String thanaId;
    private String marketId;
    ArrayList<String>all,blank;
    int thanSpinnerPosition;
    private HashMap<String, ArrayList<String>> markets;
    BasicFunction basicFunction;
    Boolean isLoading = false;
    private ArrayList<String> marketIdList;
    private ArrayList<String> marketnameList;
    private final int THREAT_SHOT = 5;
    int initialPageIndex = 1;
    String nowCalling = "without_thana_id";
    int rcvIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_list);
        basicFunction = new BasicFunction(this,this);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        Intent intent = getIntent();
        rcvIntent = intent.getIntExtra("resume",0);


        ds = new Data_Source(this);
        all = new ArrayList<>();
        all.add("All");
        blank = new ArrayList<>();
        blank.add("");
        marketListAdapter = new MarketListAdapter(null);

        recyclerView = findViewById(R.id.outlet_list_recycler);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        thanaSpinner = findViewById(R.id.thana_spinner);
        createMarketBtn = findViewById(R.id.create_market_btn);

        createMarketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MarketList.this,CreateNewMarket.class));
                finish();
            }
        });



        outlets = new ArrayList<>();


        for (int i = 0; i<100; i++){
            outlets.add("Samox Outlet");
        }

        thanas = new HashMap<>();
        thanas = ds.getRouteList("no");

        if (thanas != null && thanas.size()>0) {
            thanas.get(Tables.THANA_TH_id).add(0,"x0x");
            thanas.get(Tables.THANA_NAME).add(0,"All");

            SpinnerAdapter thanaData = new SpinnerAdapter(this, R.layout
                    .spinner_item, thanas.get(Tables.THANA_NAME));
            thanaSpinner.setAdapter(thanaData);
            thanaSpinner.requestFocus();

            if (rcvIntent == 2){
                thanaSpinner.setSelection(getPosition(thanas.get(Tables.THANA_TH_id),StaticFlags.MARKET_THANA_ID));
            }else if (rcvIntent == 1){
                thanaSpinner.setSelection(StaticFlags.MARKET_THANA_SPINNER_POSITION);
            }
        }/*else {
            SpinnerAdapter thanaData = new SpinnerAdapter(this, R.layout
                    .spinner_item, blank);
            thanaSpinner.setAdapter(thanaData);

        }*/

        thanaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }
                thanSpinnerPosition = position;

                thanaId = thanas.get(Tables.THANA_TH_id).get(position);
                Log.e("thana id seletion", "onItemSelected: "+thanaId );
                StaticFlags.MARKET_THANA_SPINNER_POSITION = position;

                if (thanaId != null &&!thanaId.equals("x0x") ){
                    //set adapter to recyclerview

                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                        primaryData.put("route_id",thanaId);
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    nowCalling = "with_thana_id";
                    Log.e("select thana-->", "onItemSelected: "+nowCalling );
                    initialPageIndex = 1;


                  //  basicFunction.getResponceData(URL.MarketList, String.valueOf(primaryData),701);

                    getdata(primaryData);


                }

                else {
                    // here is the for the very first recycler adaprt setup

                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                        primaryData.put("page",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    nowCalling = "without_thana_id";
                    Log.e("select thana-->", "onItemSelected: "+nowCalling );
                    initialPageIndex = 1;


                    //basicFunction.getResponceData(URL.MarketList, String.valueOf(primaryData),701);

                    getdata(primaryData);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( MarketList.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticFlags.MARKET_ID_FOR_POSITION = null;
                StaticFlags.MARKET_THANA_ID = null;
                StaticFlags.MARKET_THANA_SPINNER_POSITION = 0;
                startActivity(new Intent( MarketList.this, Tools.class));
                finish();
            }
        });
    }

    private void getdata(JSONObject primaryData) {
        ProgressDialog dailog = CheckConnection(MarketList.this,"Getting Market List...");
        if (dailog==null)
            return;
        getJAPi().MarketList(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    isLoading = false;
                    markets = new HashMap<>();
                    marketIdList = new ArrayList<>();
                    marketnameList = new ArrayList<>();

                    try {
                        for (int j = 0; j < jsonObject.getJSONArray("markets").length(); j++) {
                            if (!jsonObject.getJSONArray("markets").getJSONObject(j).getString("market_name").equals("")) {
                                marketIdList.add(jsonObject.getJSONArray("markets").getJSONObject(j).getString("market_id"));
                                marketnameList.add(jsonObject.getJSONArray("markets").getJSONObject(j).getString("market_name"));
                            }

                        }

                        markets.put(Tables.MARKETS_market_id,marketIdList);
                        markets.put(Tables.MARKETS_market_name,marketnameList);

                        if (markets.get(Tables.MARKETS_market_name) != null && markets.get(Tables.MARKETS_market_name).size()>0) {

                            marketListAdapter = new MarketListAdapter(markets);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MarketList.this));
                            recyclerView.setAdapter(marketListAdapter);

                            if (rcvIntent >0){
                                recyclerView.scrollToPosition(getPosition(marketIdList,StaticFlags.MARKET_ID_FOR_POSITION));

                            }

                            marketListAdapter.setOnItemClickListener(new MarketListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                }

                                @Override
                                public void onEditClick(int position) {
                                    Intent intent = new Intent(MarketList.this, CreateNewMarket.class);
                                    intent.putExtra("market_id", markets.get(Tables.MARKETS_market_id).get(position));
                                    startActivity(intent);
                                    finish();
                                    Log.e("edit click", "onEditClick: " + markets.get(Tables.MARKETS_market_id).get(position));

                                }

                                @Override
                                public void onViewClick(int position) {

                                }
                            });
                        }else {
                            recyclerView.setAdapter(null);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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
    protected void onResume() {
        super.onResume();
        /*thanaSpinner.setSelection(thanSpinnerPosition);
        if (thanSpinnerPosition == 0){
            markets = ds.getMarketData("no","no");
        }else if (thanSpinnerPosition > 0){
            markets = ds.getMarketData("no",thanas.get(Tables.THANA_TH_id).get(thanSpinnerPosition));

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (markets!=null && markets.size()>0) {
            marketListAdapter = new MarketListAdapter(markets);
            marketListAdapter.setOnItemClickListener(new MarketListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                }

                @Override
                public void onEditClick(int position) {
                    Intent intent = new Intent(MarketList.this,CreateNewMarket.class);
                    intent.putExtra("market_id",markets.get(Tables.MARKETS_market_id).get(position));
                    startActivity(intent);
                    finish();
                    Log.e("edit click", "onEditClick: "+markets.get(Tables.MARKETS_market_id).get(position) );

                }

                @Override
                public void onViewClick(int position) {

                }
            });
            recyclerView.setAdapter(marketListAdapter);
        }*/
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {



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


                if (!isLoading && linManager.getItemCount() - THREAT_SHOT== linManager.findLastVisibleItemPosition()){
                    Log.e("entr scrolled..0n 402", "onScrolled: "+"On Scroll.." );
                    initialPageIndex++;
                    loadMoreData(initialPageIndex);

                    //  Toast.makeText(MarketList.this," "+initialPageIndex,Toast.LENGTH_SHORT).show();

                    Log.e("last position", "onScrolled: "+linManager.findLastVisibleItemPosition() );
                }
            }

        });
    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(MarketList.this,"No Internet Connection",Toast.LENGTH_SHORT).show();

    }

    public void loadMoreData(int page){
        isLoading= true;
        Log.e("page number......", ".......>>loadMoreData: "+page );
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac",basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
            Log.e("inside Loadmore", "loadMoreData: "+nowCalling );
            if (nowCalling.equals("with_thana_id")){
                Log.e("Now Calling", "loadMoreData: TAKING THANA ID filter" );
                primaryData.put("thana_id",thanaId);
            }
            primaryData.put("page",page);
        } catch (JSONException e) {
            e.printStackTrace();
        }


       // basicFunction.getResponceData(URL.MarketList, String.valueOf(primaryData),702);

        ProgressDialog dailog = CheckConnection(MarketList.this,"Market List Loading...");
        if (dailog==null)
            return;
        getJAPi().MarketList(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    isLoading = false;
                    try {
                        for (int j = 0; j < jsonObject.getJSONArray("markets").length(); j++) {
                            if (!jsonObject.getJSONArray("markets").getJSONObject(j).getString("market_name").equals("")) {
                                marketIdList.add(jsonObject.getJSONArray("markets").getJSONObject(j).getString("market_id"));
                                marketnameList.add(jsonObject.getJSONArray("markets").getJSONObject(j).getString("market_name"));

                            }

                        }

                        markets.put(Tables.MARKETS_market_id, marketIdList);
                        markets.put(Tables.MARKETS_market_name, marketnameList);

                        recyclerView.getAdapter().notifyDataSetChanged();

                        Log.e("50 data setes", "OnServerResponce: Adapter seted...");

                    } catch (JSONException e) {
                        e.printStackTrace();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            StaticFlags.MARKET_ID_FOR_POSITION = null;
            StaticFlags.MARKET_THANA_ID = null;
            StaticFlags.MARKET_THANA_SPINNER_POSITION = 0;
            startActivity(new Intent( MarketList.this, Tools.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    public int getPosition(ArrayList<String>data,String id){

        int position = data.indexOf(id);

        return  position;
    }
}
