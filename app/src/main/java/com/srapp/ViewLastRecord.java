package com.srapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.srapp.Adapter.AdapterForSalesReportLastRecord;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.OUTLET_ID;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewLastRecord extends AppCompatActivity implements BasicFunctionListener {

    BasicFunction bF;
    String outlate_id, outlate_name, memo_no;
    Data_Source data_source;
    ArrayList<HashMap<String, String>> arrayList;
    ListView listView;

    TextView userIdTV, titleTV, title2,user_txt_view;
    String memoNumber;
    ImageView backBtn, homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_last_record);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewLastRecord.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewLastRecord.this, Create_New_Memo.class));
                finish();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);


        listView = findViewById(R.id.list_view);

        title2 = findViewById(R.id.title2);


        data_source = new Data_Source(this);
        bF = new BasicFunction(this, this);
        user_txt_view = findViewById(R.id.user_txt_view);
        user_txt_view.setText(bF.getPreference(SR_ID));
        outlate_id = TempData.OutletID;
        outlate_name = TempData.OutletName;
        outlate_name = TempData.OutletName;
        arrayList = new ArrayList<>();

        title2.setText("Last Orders Of " + outlate_name);


        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(SR_ID, bF.getPreference(SR_ID));
            jsonObject.put(OUTLET_ID, outlate_id);
            jsonObject.put("mac", bF.getPreference("mac"));
           // bF.getResponceData(URL.LAST_MEMO, jsonObject.toString(), 111);

            ProgressDialog dailog = CheckConnection(ViewLastRecord.this,"Get Memos...");
            if (dailog==null)
                return;
            getJAPi().LAST_MEMO(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();
                        JSONArray jsonArray = jsonObject.getJSONArray("last_memo_details");
                        if (jsonArray.length()>0) {
                            memoNumber = jsonArray.getJSONObject(0).getString("memo_number");
                            memo_no = jsonArray.getJSONObject(0).getString("memo_number");
                            TempData.MemoDate = jsonArray.getJSONObject(0).getString("memo_date");
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("memos", jsonArray);
                            data_source.updateMemoWithServer(jsonObject1);
                        }
                        getOrders();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dailog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void getOrders() {

        arrayList = data_source.getlastorders(outlate_id, memoNumber);

        AdapterForSalesReportLastRecord adapter = new AdapterForSalesReportLastRecord(this, arrayList);
        listView.setAdapter(adapter);
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {



    }

    @Override
    public void OnConnetivityError() {

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            startActivity(new Intent(ViewLastRecord.this, Create_New_Memo.class));
            finish();

        }
        return super.onKeyDown(keyCode, event);

    }
}
