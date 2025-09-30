package com.srapp;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.srapp.Adapter.LeaveListAdapter;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.JAPIClient;
import com.srapp.apiService.ApiInterfaceForJava;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveList extends AppCompatActivity {
    RecyclerView recyclerView;
    ApiInterfaceForJava api;
    LeaveListAdapter adapter = null;
    ArrayList<com.srapp.Model.LeaveList> leaveLists=new ArrayList<>();
    Button newApplyBtn;
    ImageView homeBtn,backBtn;
    TextView userIdTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_list);
        api = JAPIClient.getClient().create(ApiInterfaceForJava.class);
        recyclerView=findViewById(R.id.recyclerView);
        newApplyBtn=findViewById(R.id.newApply);
        newApplyBtn.setOnClickListener(v -> {
                    startActivity(new Intent(LeaveList.this,LeaveApply.class));
       });
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        userIdTV = findViewById(R.id.user_txt_view);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( LeaveList.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( LeaveList.this, SR_Account_Activity.class));
                finishAffinity();
            }
        });

        JSONObject jsonObject = new JSONObject();
        // basicFunction.getResponceData(URL.PULL, jsonObject.toString(), 102);
        ProgressDialog dailog = CheckConnection(LeaveList.this,"Getting Leave List From Server...");
        if (dailog==null)
            return;
        api.leaveList(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    JSONArray jsonArray=jsonObject.getJSONArray("leaves");
                    for (int k =0; k<jsonArray.length();k++) {
                        JSONObject dataObject = jsonArray.getJSONObject(k);
                        leaveLists.add(
                                new com.srapp.Model.LeaveList(
                                        dataObject.getString("apply_date"),
                                        dataObject.getString("type_name"),
                                        dataObject.getString("start_date"),
                                        dataObject.getString("end_date"),
                                        "Pending"

                                ));
                    }
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(LeaveList.this);
                    adapter=new LeaveListAdapter(leaveLists,LeaveList.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dailog.dismiss();
            }
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( LeaveList.this, SR_Account_Activity.class));
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}