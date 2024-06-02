package com.srapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.srapp.Adapter.SR_TargetAdapter;
import com.srapp.Model.SR_Target;
import com.srapp.print.ParentActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SR_Target_Activity extends ParentActivity {

    RecyclerView recyclerView;
    ImageView srTargetImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr__target_);


        srTargetImage = (ImageView) findViewById(R.id.home);
        srTargetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SR_Target_Activity.this, Create_New_Gift_Issue.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.SR_Target_recycleView);

        SR_TargetAdapter adapter = new SR_TargetAdapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<SR_Target> getList() {
        List<SR_Target> sr_targetList = new ArrayList<>();
        // src Wikipedia
        sr_targetList.add(new SR_Target("SMC Product Target",null));
        sr_targetList.add(new SR_Target("Monthly Outlet Coverage",null));
        sr_targetList.add(new SR_Target("Monthly Effective Call Target",null));
        sr_targetList.add(new SR_Target("Session Target",null));



        return sr_targetList;
    }
}
