package com.srapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.srapp.Adapter.App_InboxAdapter;
import com.srapp.Model.App_Inbox;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SR_App_Inbox extends AppCompatActivity {

    RecyclerView recyclerView;
    Button sendMessageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr__app__inbox);

        recyclerView = (RecyclerView) findViewById(R.id.AppInbox_recycleView);
        sendMessageBtn = (Button) findViewById(R.id.sendMessageBtn);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SR_App_Inbox.this,About_App_Activity.class);
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
