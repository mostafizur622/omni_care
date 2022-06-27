package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.srapp.Adapter.AdapterForProductCatalog;
import com.srapp.Adapter.AdapterForProductPolicyDetails;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;

import java.util.ArrayList;
import java.util.HashMap;

public class Product_policy_Details extends AppCompatActivity {
    HashMap<String, ArrayList<String>> productcatagory = new HashMap<>();
    ListView productRecycleView;
    ImageView homeBtn,backBtn;
    AdapterForProductCatalog adapter;
    TextView userIdTV,titleTV,product_nametv;
    Data_Source ds;
    String product_name , product_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_policy__details);
        ds= new Data_Source(this);
        productRecycleView = findViewById(R.id.productRecycleView);
        if (getIntent()!=null) {
            product_name = getIntent().getStringExtra("product_name");
            product_id = getIntent().getStringExtra("product_id");
        }
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        product_nametv = findViewById(R.id.product_name);
        titleTV = findViewById(R.id.title_tv);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        product_nametv.setText(product_name);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Product_policy_Details.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Product_policy_Details.this, Product_Catalog.class));
                finishAffinity();
            }
        });


        AdapterForProductPolicyDetails adapterForProductPolicyDetails = new AdapterForProductPolicyDetails(this,ds.getPolicyList(product_id));
        productRecycleView.setAdapter(adapterForProductPolicyDetails);


    }
}
