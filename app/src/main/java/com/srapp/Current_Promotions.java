package com.srapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.srapp.Adapter.App_InboxAdapter;
import com.srapp.Model.App_Inbox;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Current_Promotions extends AppCompatActivity {

    String[] SPINNERLIST = {"Soma-Ject", "Orsaline-N", "Panther Dotted", "Raja Plain"};
    RecyclerView recyclerView;
    ImageView homeCurrentPromotion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current__promotions);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, SPINNERLIST);
        MaterialBetterSpinner productNameSpinner = (MaterialBetterSpinner)
                findViewById(R.id.product_name_spinner);

        productNameSpinner.setAdapter(arrayAdapter);

        recyclerView = (RecyclerView) findViewById(R.id.currentPromotion_recycleView);
        homeCurrentPromotion = (ImageView) findViewById(R.id.home);

        homeCurrentPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Current_Promotions.this,Reports_Activity.class);
                startActivity(intent);
            }
        });

        App_InboxAdapter adapter = new App_InboxAdapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<App_Inbox> getList() {
        List<App_Inbox> appInbox_List = new ArrayList<>();
        // src Wikipedia
        appInbox_List.add(new App_Inbox("Good Morning","Dear Collegues,by grace of allah i think you all are well,during collection of E-SALES device please contact with me as soon as possible","Fahad Ibne Sayed","SR,AAO,Noakhali","Date:10-03-2019,16:11:23"));
        appInbox_List.add(new App_Inbox("Good Morning","Dear Collegues,by grace of allah i think you all are well,during collection of E-SALES device please contact with me as soon as possible","Fahad Ibne Sayed","SR,AAO,Noakhali","Date:10-03-2019,16:11:23"));
        appInbox_List.add(new App_Inbox("Good Morning","Dear Collegues,by grace of allah i think you all are well,during collection of E-SALES device please contact with me as soon as possible","Fahad Ibne Sayed","SR,AAO,Noakhali","Date:10-03-2019,16:11:23"));
        appInbox_List.add(new App_Inbox("Good Morning","Dear Collegues,by grace of allah i think you all are well,during collection of E-SALES device please contact with me as soon as possible","Fahad Ibne Sayed","SR,AAO,Noakhali","Date:10-03-2019,16:11:23"));
        appInbox_List.add(new App_Inbox("Good Morning","Dear Collegues,by grace of allah i think you all are well,during collection of E-SALES device please contact with me as soon as possible","Fahad Ibne Sayed","SR,AAO,Noakhali","Date:10-03-2019,16:11:23"));

        return appInbox_List;
    }
}
