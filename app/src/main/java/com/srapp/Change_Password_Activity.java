package com.srapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.srapp.Db_Actions.URL;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.srapp.Db_Actions.Tables.SR_ID;

public class Change_Password_Activity extends AppCompatActivity implements BasicFunctionListener {

    Button changePasswordBtn;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV,oldpass,newpass,cnewwpass ;
    BasicFunction bf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password_);
        bf = new BasicFunction(this,this);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        oldpass = findViewById(R.id.oldPasswordEdt);
        newpass = findViewById(R.id.newPasswordEdt);
        cnewwpass = findViewById(R.id.confirmPasswordEdt);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Change_Password_Activity.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Change_Password_Activity.this, SR_Account_Activity.class));
                finish();
            }
        });

        changePasswordBtn = (Button) findViewById(R.id.changePasswordBtn);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (bf.getPreference("password").equalsIgnoreCase(oldpass.getText().toString().trim())){

                     if (newpass.getText().toString().trim().equalsIgnoreCase(cnewwpass.getText().toString().trim())){

                         JSONObject jsonObject = new JSONObject();
                         try {
                             jsonObject.put(SR_ID,bf.getPreference(SR_ID));
                             jsonObject.put("mac",bf.getPreference("mac"));
                             jsonObject.put("new_password",newpass.getText().toString().trim());
                             jsonObject.put("old_password",oldpass.getText().toString().trim());
                             bf.getResponceData(URL.CHANGE_PASSWORD,jsonObject.toString(),111);

                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                     }else {

                         Toast.makeText(Change_Password_Activity.this,"New Password & confirmed Password Not Matched ",Toast.LENGTH_SHORT).show();
                     }

                 }else {
                     Toast.makeText(Change_Password_Activity.this,"Old Password Not Matched",Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( Change_Password_Activity.this, SR_Account_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        if (i==111){

            try {
                JSONObject jsonObject1 = jsonObject.getJSONObject("response");

                if (jsonObject1.getString("status").equalsIgnoreCase("1")){
                  bf.savePreference("password",newpass.getText().toString().trim());
                    startActivity(new Intent( Change_Password_Activity.this, SR_Account_Activity.class));
                    finish();
                }
                Toast.makeText(Change_Password_Activity.this,jsonObject1.getString("message"),Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void OnConnetivityError() {

    }
}
