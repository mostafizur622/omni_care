package com.srapp.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.srapp.Gift_Issue_Edit;
import com.srapp.Model.SR_Account;
import com.srapp.R;
import com.srapp.Util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SR_Account_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView accountImage;
    List<SR_Account> sr_targetList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr__account);

        recyclerView = (RecyclerView) findViewById(R.id.SR_recycleView);
        accountImage = (ImageView) findViewById(R.id.home);

        accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SR_Account_Activity.this, Gift_Issue_Edit.class);
                startActivity(intent);
            }
        });

        SR_AccountAdapter adapter = new SR_AccountAdapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(SR_Account_Activity.this, ""+position, Toast.LENGTH_SHORT).show();
                if(position == 7){

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private List<SR_Account> getList() {
        List<SR_Account> sr_targetList = new ArrayList<>();
        // src Wikipedia
        sr_targetList.add(new SR_Account("Apps Inbox",null));
        sr_targetList.add(new SR_Account("Current Promotion",null));
        sr_targetList.add(new SR_Account("Visit Plan",null));
        sr_targetList.add(new SR_Account("SR Target",null));
        sr_targetList.add(new SR_Account("SR Store Status",null));
        sr_targetList.add(new SR_Account("Bonus Party Affiliation",null));
        sr_targetList.add(new SR_Account("Day Close",null));
        sr_targetList.add(new SR_Account("Database Backup",null));
        sr_targetList.add(new SR_Account("Change Password",null));
        sr_targetList.add(new SR_Account("About",null));
        sr_targetList.add(new SR_Account("FAQ",null));


        return sr_targetList;
    }
}
