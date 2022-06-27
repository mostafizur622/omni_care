package com.srapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.srapp.Adapter.NcpCollectionAdapter;

import java.util.ArrayList;

public class NcpCollection extends AppCompatActivity {

    NcpCollectionAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<String>data;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_collection);
        data = new ArrayList<>();
        for (int i = 0; i<100; i++){
            data.add("Exo Lim");
        }
        adapter = new NcpCollectionAdapter();
        adapter.setData(data);

        recyclerView = findViewById(R.id.ncp_collection_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        img = findViewById(R.id.home);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NcpCollection.this, NcpCollectionList.class));
            }
        });
    }
}
