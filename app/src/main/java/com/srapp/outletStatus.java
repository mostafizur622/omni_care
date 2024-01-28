package com.srapp;


import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.srapp.Adapter.AdapterForPendingOutlet;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.Constants;
import com.srapp.Util.JAPIClient;
import com.srapp.Util.StaticFlags;
import com.srapp.apiService.ApiInterfaceForJava;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class outletStatus extends  AppCompatActivity implements BasicFunctionListener {

    ImageView homeBtn, backBtn;
    BasicFunction bf;
    AdapterForPendingOutlet adapter;
    ListView listview;

    Data_Source db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_status);
        TextView userIdTV = findViewById(R.id.user_txt_view);
        TextView titleTV = findViewById(R.id.title_tv);
        listview = findViewById(R.id.list_view);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        db = new Data_Source(this);


        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(outletStatus.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(outletStatus.this, Tools.class));
                finish();
            }
        });

        bf = new BasicFunction(this, this);


        ProgressDialog dailog = CheckConnection(outletStatus.this,"Getting Status...");
        if (dailog==null)
            return;
        JAPIClient.getClient().create(ApiInterfaceForJava.class).tempOutletStatus(convertTORequestdata(getOutlets())).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                android.util.Log.e("outlet",response.body());
               String responseMemos = response.body();
                dailog.dismiss();
                try{
                    if (responseMemos !=null)
                    {
                        ArrayList<HashMap<String,String>> list = new ArrayList<>();
                        JSONObject jsonMemo = new JSONObject(responseMemos);
                        JSONArray array = jsonMemo.getJSONArray("res");

                        for (int i = 0 ; i<array.length() ; i++){
                            JSONObject obj = array.getJSONObject(i);
                            HashMap<String,String> map= getTempOutlet(obj.getString("id"),obj.getString("approval_status"),obj.getString("main_id"));
                            list.add(map);
                            if (obj.getInt("approval_status")== Constants.SUCCESS){
                                Log.e("approval_status1",obj.getInt("approval_status")+"");


                                db.excQuery("INSERT INTO outlets SELECT * FROM temp_outlets WHERE outlet_id='"+obj.getString("id")+"'");
                                //Log.e("approval_status1","INSERT INTO outlets SELECT * FROM temp_outlets WHERE outlet_id='"+obj.getString("id")+"'    "+ c.getCount());
                                db.excQuery("Update outlets set outlet_id ='"+obj.getString("main_id")+"' WHERE outlet_id='"+obj.getString("id")+"'");
                                db.excQuery("delete from temp_outlets WHERE outlet_id='"+obj.getString("id")+"'");
                            } else if (obj.getInt("approval_status")==Constants.REJECT) {
                                db.excQuery("delete from temp_outlets WHERE outlet_id='"+obj.getString("id")+"'");
                            }



                        }

                        adapter = new AdapterForPendingOutlet(outletStatus.this,list);
                        listview.setAdapter(adapter);


                    }
                    else
                    {
                        dailog.dismiss();
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                }catch(Exception e){/*Toast.makeText(getApplicationContext(), Constants.SERVER_MESSAGE, 1000).show();*/}

            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    private HashMap<String, String> getTempOutlet(String id, String approval_status, String main_id) {
        Log.e("getTempOutlet",approval_status+" "+id+" "+main_id+" "+(!main_id.equalsIgnoreCase("null")));
        HashMap<String, String> map = new HashMap<>();
//        if (!main_id.equalsIgnoreCase("null")){
//            return getOutlet(id,approval_status,main_id);
//        }

        Cursor c = db.rawQueryCoustom("select outlet_id,outlet_name,market_name from temp_outlets as o Inner Join markets as m on o.market_id=m.market_id where outlet_id='"+id+"'");
        Log.e("Query","select outlet_id,outlet_name,market_name from temp_outlets as o Inner Join markets as m on o.market_id=m.market_id where outlet_id='"+id+"'"+c.getCount());
        c.moveToFirst();
        if (c.getCount()>0 && c!=null){

            map.put("outlet_id",id);
            map.put("market_name",c.getString(2));
            map.put("outlet_name",c.getString(1));
            map.put("status",approval_status);
            map.put("main_id",main_id);

            Log.e("approval_status",approval_status);
        }

        return map;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {



                Intent intent = new Intent(outletStatus.this, Tools.class);
                intent.putExtra("resume", 1);
                startActivity(intent);
                finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private HashMap<String, String> getOutlet(String id, String approval_status, String main_id) {
        Log.e("getTempOutlet1",approval_status+" "+id+" "+main_id+" "+(!main_id.equalsIgnoreCase("null")));
        HashMap<String, String> map = new HashMap<>();
        Cursor c = db.rawQueryCoustom("select outlet_id,outlet_name,market_name from outlets as o Inner Join markets as m on o.market_id=m.market_id where outlet_id='"+main_id+"'");
        Log.e("Query2","select outlet_id,outlet_name,market_name from temp_outlets as o Inner Join markets as m on o.market_id=m.market_id where outlet_id='"+main_id+"'"+c.getCount());
        c.moveToFirst();
        if (c.getCount()>0 && c!=null){

            map.put("outlet_id",id);
            map.put("market_name",c.getString(2));
            map.put("outlet_name",c.getString(1));
            map.put("status",approval_status);
            map.put("main_id",main_id);

            Log.e("approval_status",approval_status);
        }

        return map;
    }

    private JSONObject getOutlets()  {
        JSONObject outlets=null;
        Cursor c = db.rawQueryCoustom("select Outlet_id from temp_outlets");


        JSONArray array = new JSONArray();

        c.moveToFirst();
        if (c.getCount()>0 && c!=null){

            for (int i = 0 ; i<c.getCount() ; i++) {
                array.put(c.getString(0));
                c.moveToNext();
            }
        }

        try {
            outlets = new JSONObject().put("temp_outlet_id",array);
        } catch (JSONException e) {

        }
        return outlets;
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

    }

    @Override
    public void OnConnetivityError() {

    }
}