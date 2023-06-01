package com.srapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.SR_AccountAdapter;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.SR_Account;
import com.srapp.Util.DatabaseBackManager;
import com.srapp.Util.Parent;
import com.srapp.Util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.srapp.Db_Actions.Tables.SR_ID;

public class SR_Account_Activity extends Parent {

    RecyclerView recyclerView;
    ImageView accountImage;
    List<SR_Account> sr_targetList;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr__account);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        recyclerView = (RecyclerView) findViewById(R.id.SR_recycleView);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SR_Account_Activity.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SR_Account_Activity.this, Dashboard.class));
                finishAffinity();
            }
        });

        SR_AccountAdapter adapter = new SR_AccountAdapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
               // Toast.makeText(SR_Account_Activity.this, ""+position, Toast.LENGTH_SHORT).show();
                if(position == 7){
                    startActivity(new Intent(SR_Account_Activity.this,Faq.class));
                    finish();
                }else if(position == 6){
                    startActivity(new Intent(SR_Account_Activity.this,About_App_Activity.class));
                    finish();
                }else if(position == 5){
                    startActivity(new Intent(SR_Account_Activity.this,Change_Password_Activity.class));
                    finish();
                }else if(position == 4){
                    DatabaseBackUp();
                }else if(position == 0){
                    startActivity(new Intent(SR_Account_Activity.this,Visit_Plan_Activity.class));
                    finish();
                }else if(position == 3){
                    startActivity(new Intent(SR_Account_Activity.this,Bonus_Party_Affiliation.class));
                    finish();
                }else if(position == 2){
                    startActivity(new Intent(SR_Account_Activity.this,SrProductTarget.class));
                    finish();
                }else if(position == 1){
                    startActivity(new Intent(SR_Account_Activity.this,Outof_Plan_Visit.class));
                    finish();
                }else if(position == 8){
                    startActivity(new Intent(SR_Account_Activity.this,SR_Attendance.class));
                    finish();
                }else if(position == 9){
                    startActivity(new Intent(SR_Account_Activity.this,SR_NCP_Activity.class));
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    public void DatabaseBackUp()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(SR_Account_Activity.this);
        final EditText edittext = new EditText(SR_Account_Activity.this);



        alert.setTitle("Enter Your Password");

        alert.setView(edittext);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                Editable YouEditTextValue = edittext.getText();
                //OR
                String result = edittext.getText().toString();

                String so=getPreference(SR_ID);
              //  DatabaseBackManager.setBackUp(SR_Account_Activity.this);
                if(result.equals(so)){
                    DatabaseBackManager.setBackUp(SR_Account_Activity.this);
                }else{
                    Toast.makeText(getApplicationContext(), "Your Password is Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

    }

    private List<SR_Account> getList() {
        List<SR_Account> sr_targetList = new ArrayList<>();
        // src Wikipedia
       // sr_targetList.add(new SR_Account("Apps Inbox",null));
        //sr_targetList.add(new SR_Account("Current Promotion",null));
        sr_targetList.add(new SR_Account("Visit Plan",null));
        sr_targetList.add(new SR_Account("Out of Visit Plan ",null));
        sr_targetList.add(new SR_Account("SR Target",null));
        sr_targetList.add(new SR_Account("Incentive Party",null));
        sr_targetList.add(new SR_Account("Database Backup",null));
        sr_targetList.add(new SR_Account("Change Password",null));
        sr_targetList.add(new SR_Account("About",null));
        sr_targetList.add(new SR_Account("FAQ",null));
        sr_targetList.add(new SR_Account("Attendance",null));
        sr_targetList.add(new SR_Account("NCP",null));


        return sr_targetList;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( SR_Account_Activity.this, Dashboard.class));
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
