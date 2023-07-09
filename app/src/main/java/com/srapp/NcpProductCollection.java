package com.srapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

import com.srapp.Adapter.NcpProductCollectionAdapter;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;

import java.util.ArrayList;
import java.util.HashMap;

public class NcpProductCollection extends AppCompatActivity {

    NcpProductCollectionAdapter adapter;
    ArrayList<String> Selected_products;
    RecyclerView recyclerView;
    Button nextBtn;
    Spinner rouuteSpinner, marketSpinner, outletSpinner, productCategorySpinner;

    HashMap<String,ArrayList<String>> marketData;
    HashMap<String,ArrayList<String>> routeData;
    HashMap<String,ArrayList<String>> outletData;
    HashMap<String,ArrayList<String>> productCategoryData;

    Data_Source ds;
    String routeId;
    String marketId;
    String outlet_id="0";
    String productCategoryId;

    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_product_collection);

        ds = new Data_Source(this);

        rouuteSpinner = findViewById(R.id.route_spinner);
        marketSpinner = findViewById(R.id.market_spinner);
        outletSpinner = findViewById(R.id.outlet_spinner);
        productCategorySpinner = findViewById(R.id.product_category_spinner);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        nextBtn = findViewById(R.id.nextBtn);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        titleTV.setText("NCP Collection");

        recyclerView = findViewById(R.id.ncp_collection_rec);
        Selected_products = new ArrayList<>();

        routeData = new HashMap<>();
        marketData = new HashMap<>();
        productCategoryData = new HashMap<>();
        outletData = new HashMap<>();

        routeData = ds.getRouteList();
        productCategoryData = ds.getProductCategories();

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent( NcpProductCollection.this, Dashboard.class));
            finish();
        });

        backBtn.setOnClickListener(v -> {

            startActivity(new Intent( NcpProductCollection.this, SR_NCP_Activity.class));
            finish();

        });

        SpinnerAdapter routeSpinnerAdapter = new SpinnerAdapter(this, R.layout
                .spinner_item, routeData.get(Tables.ROUTE_NAME));
        rouuteSpinner.setAdapter(routeSpinnerAdapter);

        Log.e("mkt data test", "onCreate: "+marketData );

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Selected_products.size()>0) {
                    if (!outlet_id.equalsIgnoreCase("0")) {
                            Intent i = new Intent(NcpProductCollection.this, NcpCollection.class);
                        i.putExtra("products", Selected_products);
                        i.putExtra("outlet_id", outlet_id);
                        startActivity(i);
                    }else {
                        Toast.makeText(NcpProductCollection.this, "Please select a Outlet", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(NcpProductCollection.this, "Please select Product", Toast.LENGTH_SHORT).show();
                }
            }
        });


        rouuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        SpinnerAdapter productCategoryAdapter =  new SpinnerAdapter(this, R.layout
                .spinner_item, productCategoryData.get(Tables.PPRODUCT_CATEGORY_C_NAME));
        productCategorySpinner.setAdapter(productCategoryAdapter);

        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null)
                textView.setTextColor(getResources().getColor(R.color.background_card));

               productCategoryId = productCategoryData.get(Tables.PPRODUCT_CATEGORY_C_id).get(position);

                getProductList(productCategoryId);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        getProductList("0");
       


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(NcpProductCollection.this, SR_NCP_Activity.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void addproduct(String product_id){

        Selected_products.add(product_id);
    }

    public void remove_product(String product_id){

        Selected_products.remove(product_id);
    }

    public boolean isproduct_selected(String product_id){

        return Selected_products.contains(product_id);
    }

    private void getProductList(String catagotyid) {



        
        
        adapter = new NcpProductCollectionAdapter(ds.GetproductlistbyCategory(catagotyid),this);
        //adapter.setData(products);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
}
