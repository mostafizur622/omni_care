package com.srapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import androidx.appcompat.app.AppCompatActivity;

public class Send_Message_Activity extends AppCompatActivity {

    String[] SPINNERLIST = {"Finance Upper Management", "Finance uPPER Maanagemt"};
    Button sendMessageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send__message_);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, SPINNERLIST);
        MaterialBetterSpinner messagetoSpinner = (MaterialBetterSpinner)
                findViewById(R.id.messageto_spinner);
        messagetoSpinner.setAdapter(arrayAdapter);

        sendMessageBtn = (Button) findViewById(R.id.sendMessageBtn);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Send_Message_Activity.this,Day_Close_Activity.class);
                startActivity(intent);
            }
        });

    }
}
