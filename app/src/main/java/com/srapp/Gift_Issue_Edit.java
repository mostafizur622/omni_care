package com.srapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.srapp.Adapter.ProductAdapter;
import com.srapp.Model.Product;
import com.srapp.print.ParentActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Gift_Issue_Edit extends ParentActivity {

    RecyclerView EditgiftIssueRecycleview;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift__issue__edit);

        EditgiftIssueRecycleview = (RecyclerView) findViewById(R.id.gift_issue_recycler_edit);
        submitBtn = (Button) findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Gift_Issue_Edit.this,Bonus_Party_Affiliation.class);
               startActivity(intent);
            }
        });

        ProductAdapter adapter = new ProductAdapter(getList(),this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        EditgiftIssueRecycleview.setLayoutManager(linearLayoutManager);
        EditgiftIssueRecycleview.setAdapter(adapter);

        EditgiftIssueRecycleview.addItemDecoration(new DividerItemDecoration(Gift_Issue_Edit.this,
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
