package com.srapp;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.MarketListAdapter;
import com.srapp.Adapter.Market_Adapter;
import com.srapp.Adapter.VisitPlanMktListAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.RecyclerTouchListener;
import com.srapp.print.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Visit_Plan_Activity extends ParentActivity implements BasicFunctionListener {

    RecyclerView recyclerView;
    String[] SPINNERLIST = {"10-06-2019", "11-06-2019", "12-06-2019", "13-06-2019"};
    ImageView homevisitplan;
    TextView datePick, rtMktLable, dayLable, routeLable;
    Button routeBtn;
    RecyclerTouchListener touchListener;

    DatePickerDialog datePickerDialog;
    Calendar calendar;
    int day, month, year, weekNumber;
    BasicFunction basicFunction;
    HashMap<String,ArrayList<String>> routeList,marketListByRoute;
    ArrayList<String>routeIdList, routeNameList;
    String daysStatus[];
    ArrayList<String[]> daysList;
    String[] daysName = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"};
    Data_Source ds;
    Boolean isLoading = false;
    int initialPageIndex = 1;
    String routeId;
    private HashMap<String, ArrayList<String>> markets;
    private ArrayList<String> marketIdList;
    private ArrayList<String> marketnameList;
    private VisitPlanMktListAdapter marketListAdapter;
    private int THREAT_SHOT = 5;
    int showing = 0;
    LinearLayout linearLayout;
    int noInternet = 0;

    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit__plan_);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        basicFunction = new BasicFunction(this,this);
        ds = new Data_Source(this);

        routeBtn = findViewById(R.id.route_btn);
        rtMktLable = findViewById(R.id.mkt_rt_lable);
        dayLable = findViewById(R.id.days_lable);
        routeLable = findViewById(R.id.route_lable);
        linearLayout = findViewById(R.id.route_days_lbles);


        recyclerView = findViewById(R.id.Market_recycleView);
        homevisitplan = findViewById(R.id.home);
        homevisitplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Visit_Plan_Activity.this, Outof_Plan_Visit.class);
               startActivity(intent);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Visit_Plan_Activity.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Visit_Plan_Activity.this, SR_Account_Activity.class));
                finish();
            }
        });

        datePick = findViewById(R.id.visit_date_picker);
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        weekNumber = calendar.get(Calendar.WEEK_OF_MONTH)-1;
        int mont = month+1;

        if (day <=7){
            weekNumber=1;
        }else if (day>7 && day<13){
            weekNumber = 2;
        }else if (day>13 && day<20){
            weekNumber = 3;
        }else {
            weekNumber = 4;
        }

        datePick.setText(day+"/"+mont+"/"+year +"  W"+weekNumber );

        //................ call api for this week...........//
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac",basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
            primaryData.put("week_id",weekNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // basicFunction.getResponceData(URL.VisitPlan,primaryData.toString(),901);
        getData(primaryData);

        //..............api call end..................//

        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                datePickerDialog = new DatePickerDialog(Visit_Plan_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int monthX = monthOfYear+1;

                        if (dayOfMonth <=7){
                            weekNumber=1;
                        }else if (dayOfMonth>7 && dayOfMonth<=14){
                            weekNumber = 2;
                        }else if (dayOfMonth>14 && dayOfMonth<=21){
                            weekNumber = 3;
                        }else {
                            weekNumber = 4;
                        }

                        datePick.setText(dayOfMonth+"/"+monthX+"/"+year+" "+" w"+weekNumber);

                        //................ call api for this week...........//
                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac",basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                            primaryData.put("week_id",weekNumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //basicFunction.getResponceData(URL.VisitPlan,primaryData.toString(),901);
                        getData(primaryData);
                        //..............api call end..................//



                    }
                },year,month,day);
                datePickerDialog.show();

            }
        });

        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    initialPageIndex = 1;
                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac", basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                        primaryData.put("week_id", initialPageIndex);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                   // basicFunction.getResponceData(URL.VisitPlan, primaryData.toString(), 901);
                    getData(primaryData);
                if (noInternet != 1) {
                    showing = 0;
                    linearLayout.setVisibility(View.INVISIBLE);
                    routeBtn.setVisibility(View.INVISIBLE);
                    rtMktLable.setText("Route List");
                }

            }
        });




        /*Market_Adapter adapter = new Market_Adapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);*/

        /*touchListener = new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });*/


    }

    private void getData(JSONObject primaryData) {
        ProgressDialog dailog = CheckConnection(Visit_Plan_Activity.this,"Get Visit Plan...");
        if (dailog==null)
            return;
        getJAPi().VisitPlan(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();



                    routeList = new HashMap<>();
                    routeIdList = new ArrayList<>();
                    routeNameList = new ArrayList<>();
                    daysList = new ArrayList<String[]>();


                    try {

                        for (int x = 0; x < jsonObject.getJSONArray("visit_list").length(); x++) {
                            daysStatus = new String[7];
                            routeIdList.add(jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("route_id"));
                            routeNameList.add(jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("route_name"));
                            daysStatus[0] = jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("sat");
                            daysStatus[1] = jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("sun");
                            daysStatus[2] = jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("mon");
                            daysStatus[3] = jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("tue");
                            daysStatus[4] = jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("wed");
                            daysStatus[5] = jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("thu");
                            daysStatus[6] = jsonObject.getJSONArray("visit_list").getJSONObject(x).getString("fri");

                            daysList.add(daysStatus);
                        }

                        routeList.put(Tables.ROUTE_ID, routeIdList);
                        routeList.put(Tables.ROUTE_NAME, routeNameList);

                        Market_Adapter adapter = new Market_Adapter(routeList.get(Tables.ROUTE_NAME),daysList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Visit_Plan_Activity.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new Market_Adapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Log.e("days status------>", "onClick: --------->"+daysList.size()+" "+routeList.get(Tables.ROUTE_NAME).size() );
                                if (basicFunction.isInternetOn()) {
                                    routeId = routeIdList.get(position);
                                    rtMktLable.setText("Market List");
                                    linearLayout.setVisibility(View.VISIBLE);
                                    routeLable.setText("Route: " + routeNameList.get(position));
                                    routeBtn.setVisibility(View.VISIBLE);
                                    showing = 1;
                                    String txt = "";
                                    for (int i = 0; i < daysName.length; i++) {
                                        if (daysList.get(position)[i].equals("1")) {
                                            txt = txt + daysName[i] + " ";
                                        }
                                    }
                                    dayLable.setText("Days: " + txt.trim());

                                  getMarketlist();



                                    //...........api call end..........//
                                }else {
                                    Toast.makeText(Visit_Plan_Activity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onEditClick(int position) {

                            }

                            @Override
                            public void onViewClick(int position) {

                            }
                        });




                    }catch (JSONException e) {
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

    private void getMarketlist() {

        //.....call api for market list for route.....//
        final int[] initialPageIndex = {1};
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac", basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
            primaryData.put("route_id", routeId);
            primaryData.put("page", initialPageIndex[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //basicFunction.getResponceData(URL.MarketList, String.valueOf(primaryData), 902);

        ProgressDialog dailog = CheckConnection(Visit_Plan_Activity.this,"Get Market List...");
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

                            //recyclerView = new RecyclerView(this);

                            marketListAdapter = new VisitPlanMktListAdapter(markets);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Visit_Plan_Activity.this));
                            recyclerView.setAdapter(marketListAdapter);

                        }else {
                            recyclerView.setAdapter(null);
                        }

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
                                    initialPageIndex[0]++;
                                    loadMoreData(initialPageIndex[0],routeId);

                                    Toast.makeText(Visit_Plan_Activity.this," "+ initialPageIndex[0],Toast.LENGTH_SHORT).show();

                                    Log.e("last position", "onScrolled: "+linManager.findLastVisibleItemPosition() );
                                }
                            }

                        });

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

    private ArrayList<String> getList() {
        ArrayList<String> market_List = new ArrayList<>();
        market_List.add("Adarshapara");
        market_List.add("Arpara Bazar");
        market_List.add("Bijoypur Bazar(PCHP)");
        market_List.add("Nowabpur");
        market_List.add("Bijoypur Bazar(PCHP)");
        market_List.add("Nowbabpur");

        return market_List;
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {




    }

    @Override
    public void OnConnetivityError() {
        noInternet = 1;
        Toast.makeText(Visit_Plan_Activity.this,"No Internet Connection",Toast.LENGTH_LONG).show();

    }

    public void loadMoreData(int page, String routeId){
        isLoading= true;
        Log.e("page number......", ".......>>loadMoreData: "+page );
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac",basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));

            primaryData.put("route_id",routeId);
            primaryData.put("page",page);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //basicFunction.getResponceData(URL.MarketList, String.valueOf(primaryData),903);


        ProgressDialog dailog = CheckConnection(Visit_Plan_Activity.this,"Checking...");
        if (dailog==null)
            return;
        getJAPi().MarketList(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();

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
    public void onBackPressed() {
        //super.onBackPressed();
        if (showing == 1) {
            linearLayout.setVisibility(View.INVISIBLE);
            routeBtn.setVisibility(View.INVISIBLE);
            rtMktLable.setText("Route List");
            initialPageIndex = 1;
            JSONObject primaryData = new JSONObject();
            try {
                primaryData.put("mac", basicFunction.getPreference("mac"));
                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                primaryData.put("week_id", initialPageIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showing = 0;

            //basicFunction.getResponceData(URL.VisitPlan, primaryData.toString(), 901);
            getData(primaryData);
        }else {
            super.onBackPressed();
            startActivity(new Intent( Visit_Plan_Activity.this, SR_Account_Activity.class));
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            if (showing == 1) {
                linearLayout.setVisibility(View.INVISIBLE);
                routeBtn.setVisibility(View.INVISIBLE);
                rtMktLable.setText("Route List");
                initialPageIndex = 1;
                JSONObject primaryData = new JSONObject();
                try {
                    primaryData.put("mac", basicFunction.getPreference("mac"));
                    primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                    primaryData.put("week_id", initialPageIndex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showing = 0;

               // basicFunction.getResponceData(URL.VisitPlan, primaryData.toString(), 901);
                getData(primaryData);
            }else {
                //super.onBackPressed();
                startActivity(new Intent( Visit_Plan_Activity.this, SR_Account_Activity.class));
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
