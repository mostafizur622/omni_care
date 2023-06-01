package com.srapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.srapp.Adapter.NCP_CollectionAdapter;
import com.srapp.Model.NCP_Collection;
import com.srapp.Util.RecyclerTouchListener;

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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    startActivity(new Intent(SR_NCP_Activity.this, NcpProductCollection.class));
                    finish();
                } else if (position == 1) {
                    startActivity(new Intent(SR_NCP_Activity.this, NcpCollectionList.class));
                    finish();
                } else if (position == 2) {
                    startActivity(new Intent(SR_NCP_Activity.this, NCP_replacemnt.class));
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private List<NCP_Collection> getList() {
        List<NCP_Collection> ncp_List = new ArrayList<>();
        // src Wikipedia
        ncp_List.add(new NCP_Collection("Add NCP Collection",null));
        ncp_List.add(new NCP_Collection("NCP Collection List",null));
        ncp_List.add(new NCP_Collection("Replacement to Outlet",null));

        return ncp_List;
    }
}
