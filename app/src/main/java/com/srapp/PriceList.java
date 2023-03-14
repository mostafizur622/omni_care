package com.srapp;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.srapp.Adapter.PriceListAdapter;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.kotlin.DataViewModel;
import com.srapp.kotlin.DataViewModelFactory;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceList extends AppCompatActivity implements BasicFunctionListener, DBListener {
    ImageView img;
    RecyclerView recyclerView;
    BasicFunction basicFunction;
    int initialPageIndex=1;
    ArrayList<String[]> priceList;
    ArrayList<String>productNameList,priceList1,priceList2,minQtyList;
    HashMap<String, ArrayList<ArrayList<String>>> priceLishHashMap;
    boolean isLoading= false;
    Data_Source ds;

    Button update_price;

    String date;

    ImageView backBtn, homeBtn;

    TextView userIdTV,titleTV ;
    DataViewModel dataViewModel;

    public PriceList() {
        dataViewModel = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);

        basicFunction = new BasicFunction(this,this);

        ds = new Data_Source(this,this,this);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        date = sdf.format(new Date());

        recyclerView = findViewById(R.id.price_list_rec);
        update_price = findViewById(R.id.update_price);
        priceList = new ArrayList<>();
        priceLishHashMap = new HashMap<>();

        Log.e("----", "onCreate: price list-------->"+priceLishHashMap );
        Log.e("----", "onCreate: price list-------->"+date );

        dataViewModel  = new ViewModelProvider(this, new DataViewModelFactory(this.getApplication())).get(DataViewModel.class);
        // Create the observer which updates the UI.
        final Observer<String> statusObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String status) {
                Log.e("SuccessMessage", status);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        dataViewModel.getSuccessStatus().observe(this, statusObserver);

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent( PriceList.this, Dashboard.class));
            finish();
        });

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent( PriceList.this, Tools.class));
            finish();
        });

        //........use this section in update price button..........//
        update_price.setOnClickListener(v -> {

            JSONObject primaryData = new JSONObject();

            try {
                JSONObject jsonObject =  new JSONObject();
                jsonObject.put("Territory_Id",basicFunction.getPreference("territory_id"));
                jsonObject.put("so_id",basicFunction.getPreference("sales_person_id"));
                jsonObject.put("mac",basicFunction.getPreference("mac"));
                //basicFunction.getResponceData(URL.Bonus_Policy, jsonObject.toString(), 103);

                ProgressDialog dailog = CheckConnection(PriceList.this,"Checking...");
                if (dailog==null)
                    return;
                getJAPi().Bonus_Policy(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            dailog.dismiss();
                            ds.excQuery("delete from Policy_Table");
                            ds.excQuery("delete from Bonus_Eligible_Group");
                            ds.excQuery("delete from Bonus_Eligible_Outlet_Categories");
                            ds.excQuery("delete from policy_root_product");
                            ds.excQuery("delete from policy_product_Option");
                            ds.excQuery("delete from policy_option_price_slab");
                            ds.excQuery("delete from policy_bonus_product");
                            ds.excQuery("delete from product_price_other_for_slabs_v2");
                            ds.excQuery("delete from special_group");
                            ds.excQuery("delete from special_group_details");
                            ds.excQuery("delete from product_combination_list");
                            ds.excQuery("delete from Product_combination_list_details_v2");
                            ds.excQuery("delete from product_combinations");

                            ds.insertData(jsonObject,2);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
                //primaryData.put("page",initialPageIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //......................xxxxxxxxxxxxxxxxxxxxxxx..............................//
        priceLishHashMap = ds.getSlapWisePriceList(date);
        Log.e("<-------->", "onCreate: slap wise---------> "+priceLishHashMap );
        if (priceLishHashMap != null && priceLishHashMap.size()>0) {
            PriceListAdapter  priceListAdapter = new PriceListAdapter(priceLishHashMap, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(priceListAdapter);
        }
       // recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), VERTICAL));
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        Log.e("resp---->", "OnServerResponce: "+i+"---- " +jsonObject );
        if (i==101) {

            try {

                ds.excQuery("delete  from product_price");
                ds.excQuery("delete from product_price_other_for_slabs_v2");
                ds.excQuery("delete from special_group");
                ds.excQuery("delete from special_group_details");
                ds.excQuery("delete from product_combination_list");
                ds.excQuery("delete from Product_combination_list_details_v2");
                ds.excQuery("delete from product_combinations");

                ds.insertData(jsonObject.getJSONObject("dist_product_price").toString(),1);
                //ds.insertData(jsonObject,2);

                JsonObject jsonObj =  new JsonObject();
                jsonObj.addProperty("territory_id",basicFunction.getPreference("territory_id"));
                jsonObj.addProperty("so_id",basicFunction.getPreference("sales_person_id"));
                jsonObj.addProperty("mac",basicFunction.getPreference("mac"));
                jsonObj.addProperty("last_update_date","");
                jsonObj.addProperty("all","1");

                dataViewModel.getProductCombinationV2Data(jsonObj);
                dataViewModel.getProductCombinationListData(jsonObj);
                dataViewModel.getSpecialGroupData(jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(i==103){



        }
    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(PriceList.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnLocalDBdataRetrive(String json) throws JSONException {

        if (json.equalsIgnoreCase("dbPolicy")) {

            JSONObject primaryData = new JSONObject();

            try {
                primaryData.put("mac",basicFunction.getPreference("mac"));
                primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                primaryData.put("last_update","all");
                //primaryData.put("page",initialPageIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  basicFunction.getResponceData(URL.PriceList,primaryData.toString(),101);
                              }
                          }
                );

        }else  if(json.equalsIgnoreCase("done")){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        priceLishHashMap = ds.getSlapWisePriceList(date);
                        PriceListAdapter priceListAdapter = new PriceListAdapter(priceLishHashMap,PriceList.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PriceList.this));
                        recyclerView.setAdapter(priceListAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), VERTICAL));
                }
            });
        }

    }

    @Override
    public void OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList) {

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( PriceList.this, Tools.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

}
