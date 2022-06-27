package com.srapp;

import android.os.Bundle;

import com.srapp.Adapter.NCP_CollectionAdapter;
import com.srapp.Model.NCP_Collection;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SR_NCP_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr__ncp_);

        recyclerView = (RecyclerView) findViewById(R.id.NCP_Collection_recycleView);

        NCP_CollectionAdapter adapter = new NCP_CollectionAdapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<NCP_Collection> getList() {
        List<NCP_Collection> ncp_List = new ArrayList<>();
        // src Wikipedia
        ncp_List.add(new NCP_Collection("Add NCP Collection",null));
        ncp_List.add(new NCP_Collection("NCP Collection List",null));
        ncp_List.add(new NCP_Collection("Challan Request To Area Office",null));
        ncp_List.add(new NCP_Collection("Challan Receive From Area Office",null));
        ncp_List.add(new NCP_Collection("NCP Stock Status",null));
        ncp_List.add(new NCP_Collection("Replacement to Outlet",null));

        return ncp_List;
    }
}
