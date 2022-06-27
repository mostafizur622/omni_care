package com.srapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Day_Close_Activity extends AppCompatActivity {

    Button dayCloseBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day__close_);

        dayCloseBtn = (Button) findViewById(R.id.dayCloseBtn);
        dayCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Day_Close_Activity.this, Change_Password_Activity.class);
                startActivity(intent);
            }
        });
    }
}
