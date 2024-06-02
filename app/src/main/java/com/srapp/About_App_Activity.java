package com.srapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.print.ParentActivity;

import androidx.appcompat.app.AppCompatActivity;

public class About_App_Activity extends ParentActivity {

    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about__app_);
        TextView version = findViewById(R.id.version);
        TextView release_date = findViewById(R.id.release_date);
        version.setText(URL.VERSION_txt);
        release_date.setText(URL.release_date);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( About_App_Activity.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( About_App_Activity.this, SR_Account_Activity.class));
                finish();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( About_App_Activity.this, SR_Account_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
