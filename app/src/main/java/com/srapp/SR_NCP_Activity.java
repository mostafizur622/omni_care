package com.srapp;

import static com.srapp.TempData.editMemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

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
    ImageView homeBtn,backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr__ncp_);

        recyclerView = (RecyclerView) findViewById(R.id.NCP_Collection_recycleView);

        NCP_CollectionAdapter adapter = new NCP_CollectionAdapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent( SR_NCP_Activity.this, Dashboard.class));
            finish();
        });

        backBtn.setOnClickListener(v -> {

                startActivity(new Intent( SR_NCP_Activity.this, SR_Account_Activity.class));
                finish();

        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    startActivity(new Intent(SR_NCP_Activity.this, NcpProductCollection.class));
                    finish();
                } else if (position == 1) {
                    startActivity(new Intent(SR_NCP_Activity.this, NcpCollectionList.class));
                    finish();
                } /*else if (position == 2) {
                    startActivity(new Intent(SR_NCP_Activity.this, NCP_replacemnt.class));
                    finish();
                }*/
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( SR_NCP_Activity.this, SR_Account_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private List<NCP_Collection> getList() {
        List<NCP_Collection> ncp_List = new ArrayList<>();
        // src Wikipedia
        ncp_List.add(new NCP_Collection("Add NCP Collection",null));
        ncp_List.add(new NCP_Collection("NCP Collection List",null));
       // ncp_List.add(new NCP_Collection("Replacement to Outlet",null));

        return ncp_List;
    }
}
