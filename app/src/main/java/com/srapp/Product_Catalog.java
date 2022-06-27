package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.srapp.Adapter.AdapterForProductCatalog;
import com.srapp.Adapter.SalesOrderAdaper;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.Parent;

import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.OUTLETS_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_CATEGORYS;

public class Product_Catalog extends Parent {
    HashMap<String, ArrayList<String>> productcatagory = new HashMap<>();
    ListView productRecycleView;
    ImageView homeBtn,backBtn;
    AdapterForProductCatalog adapter;
    TextView userIdTV,titleTV;
    Data_Source ds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__catalog);
        ds = new Data_Source(this);
        productRecycleView = findViewById(R.id.productRecycleView);


        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Product_Catalog.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Product_Catalog.this, Tools.class));
                finishAffinity();
            }
        });

        productcatagory = ds.getAccessories(true, "00", PRODUCT_CATEGORYS, null);
        productcatagory.get(PRODUCT_CATEGORYS[3]).add(0, "All");
        productcatagory.get(PRODUCT_CATEGORYS[2]).add(0, "0");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, productcatagory.get(PRODUCT_CATEGORYS[3]));
        Spinner productSpinner =
                findViewById(R.id.product_category_spinner);
        productSpinner.setAdapter(arrayAdapter);

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setPadding(0, 0, 0, 0);
                }

                getList(productcatagory.get(PRODUCT_CATEGORYS[2]).get(position));
                Log.e("productcatagory", productcatagory.get(PRODUCT_CATEGORYS[2]).get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getList("0");
    }

    private void getList(String catagotyid) {
        adapter = new AdapterForProductCatalog(this, ds.getProductsForcatalog(catagotyid));
        productRecycleView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent( Product_Catalog.this, Tools.class));
        finishAffinity();
    }
}
