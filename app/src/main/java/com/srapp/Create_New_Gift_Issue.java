package com.srapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.srapp.Adapter.ProductAdapter;
import com.srapp.Model.Product;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Create_New_Gift_Issue extends AppCompatActivity {

    String[] SPINNERLIST = {"Khilkhet", "Khilgaon", "Gulshan", "Badda"};
    RecyclerView giftIssueRecycleview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__new__gift__issue);

        ArrayAdapter<String> ThanaarrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, SPINNERLIST);
        MaterialBetterSpinner thanaSpinner = (MaterialBetterSpinner)
                findViewById(R.id.thana_spinner);
        thanaSpinner.setAdapter(ThanaarrayAdapter);

        ArrayAdapter<String> MarketarrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, SPINNERLIST);
        MaterialBetterSpinner marketSpinner = (MaterialBetterSpinner)
                findViewById(R.id.market_spinner);
        marketSpinner.setAdapter(MarketarrayAdapter);

        ArrayAdapter<String> OutletarrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, SPINNERLIST);
        MaterialBetterSpinner outletSpinner = (MaterialBetterSpinner)
                findViewById(R.id.outlet_spinner);
        outletSpinner.setAdapter(OutletarrayAdapter);

        giftIssueRecycleview = (RecyclerView) findViewById(R.id.gift_issue_recycler);

        ProductAdapter adapter = new ProductAdapter(getList(),this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        giftIssueRecycleview.setLayoutManager(linearLayoutManager);
        giftIssueRecycleview.setAdapter(adapter);

        giftIssueRecycleview.addItemDecoration(new DividerItemDecoration(Create_New_Gift_Issue.this,
                DividerItemDecoration.VERTICAL));
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
