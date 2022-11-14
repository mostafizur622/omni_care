 package com.srapp;

 import static com.srapp.Db_Actions.Tables.Allfild;
 import static com.srapp.Db_Actions.Tables.SR_ID;

 import android.app.AlertDialog;
 import android.app.PendingIntent;
 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.Intent;
 import android.content.IntentFilter;
 import android.content.SharedPreferences;
 import android.database.Cursor;
 import android.hardware.usb.UsbDevice;
 import android.hardware.usb.UsbManager;
 import android.net.ConnectivityManager;
 import android.net.NetworkInfo;
 import android.os.Build;
 import android.os.Bundle;
 import android.preference.PreferenceManager;
 import android.util.Log;
 import android.view.KeyEvent;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.widget.Button;
 import android.widget.ImageView;
 import android.widget.ListView;
 import android.widget.TextView;
 import android.widget.Toast;
 import androidx.annotation.RequiresApi;

 import com.srapp.Adapter.AdapterForSyncSummery;
 import com.srapp.Db_Actions.DBListener;
 import com.srapp.Db_Actions.Data_Source;
 import com.srapp.Db_Actions.Tables;
 import com.srapp.Db_Actions.URL;
 import com.srapp.Util.Parent;
 import com.srapp.thermalprint.async.usbdevice.UsbDataBinder;
 import com.tanvir.BasicFun.BasicFunction;
 import com.tanvir.BasicFun.BasicFunctionListener;

 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;


 public class SyncActivity extends Parent implements DBListener, BasicFunctionListener {

    Button sync_btn, viewSummery;
    TextView pendingMemo;
    Data_Source ds;
    int Flag = 0;
    ListView listView;
    BasicFunction bf;
    ImageView homeBtn, backBtn;
    TextView userIdTV, titleTV, date;




     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);




        bf = new BasicFunction(this, this);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        date = findViewById(R.id.date);
        if (!bf.getPreference("lsyncTime").equalsIgnoreCase("null")) {
            date.setText(bf.getPreference("lsyncTime"));
        }
        ds = new Data_Source(this, this, this);
        sync_btn = (Button) findViewById(R.id.sync_btn);
        viewSummery = (Button) findViewById(R.id.viewSummery);
        pendingMemo = findViewById(R.id.pendingMemo);

        pendingMemo.setText(ds.getPendingOrderCount());
        viewSummery.setVisibility(View.GONE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(SyncActivity.this, Dashboard.class));
            finishAffinity();
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SyncActivity.this, Dashboard.class));
                finishAffinity();
            }
        });

        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInternetOn()){
                    try {

                        String url = URL.UpdatePushTime;

                        JSONObject obJson = new JSONObject();
                        obJson.put(SR_ID, getPreference(SR_ID));
                        obJson.put("version", URL.VERSION);
                        obJson.put("mac", bf.getPreference("mac"));
                        Log.e("++", "Update Time json:" + obJson.toString());
                        bf.getResponceData(url, obJson.toString(), 1412);

                    } catch (Exception e) {
                        Log.e("exception", e.getMessage());
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Your device have no internet connectivity!", Toast.LENGTH_LONG).show();
                }
            }
        });

        viewSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showDetailsDailog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void OnLocalDBdataRetrive(final String json) {


        runOnUiThread(() -> {
            try {

                if (json.toString().equalsIgnoreCase("done")) {
                    pendingMemo.setText(ds.getPendingOrderCount());
                    viewSummery.setVisibility(View.VISIBLE);
                }

                JSONObject jsonObject = new JSONObject(json);
                jsonObject.put("mac", bf.getPreference("mac"));

                if (Flag == 1) {
                    bf.getResponceData(URL.PULL, jsonObject.toString(), 102);
                } else if (Flag == 2) {
                    bf.getResponceData(URL.ORDERPUSH, jsonObject.toString(), 103);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //basicFunction.getResponceData(URL.Log,createJWT("push",json), 101);
            //generateNoteOnSD(LoginActivity.this,"log.txt",json);
        });


    }

    @Override
    public void OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList) {

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        if (jsonObject.toString().equalsIgnoreCase("done")) {


            startActivity(new Intent(SyncActivity.this, SyncActivity.class));
            finish();
        }

        if (i == 102) {
            try {
                ds.excQuery("delete from product_history");
                ds.excQuery("delete from fiscal_year");
                ds.insertData(jsonObject.getJSONObject("response").toString(), 1);
                ds.savePreference("dataforsummery", jsonObject.getJSONObject("response").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (i == 103) {

            try {
                int status = jsonObject.getJSONObject("order").getInt("status");

                if (status==1) {

                    ds.updatePushStatus();
                    Flag = 1;
                    ds.getlastupdateddate();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (i == 110) {


                updateOutlet(jsonObject);



        } else if (i == 109) {

            updateMarket(jsonObject);


        } else if (i == 1412) {

            try {
                generatemarketJson();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            /*Flag = 2;
            ds.generatePushJson();
            bf.savePreference("lsyncTime",DateFormatedConverter(getCurrentDate())+" "+getCurrentTime());*/
        }
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED || connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    private void updateMarket(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONObject("market").getJSONArray("replaced_relation");

            for (int i = 0; i < jsonArray.length(); i++) {
                if (getPreference("MarketID").equalsIgnoreCase(jsonArray.getJSONObject(i).getString("previous_id"))){
                    savePreference("MarketID",jsonArray.getJSONObject(i).getString("new_id"));
                }
                ds.excQuery("update markets set is_pushed ='1' , market_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where market_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");
                ds.excQuery("update outlets set market_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where market_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");
                ds.excQuery("update order_table set market_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where market_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");

            }
        } catch (JSONException e) {
            Log.e("marketjsx",e.getMessage());
            e.printStackTrace();
        }
        generateOutletJson();
    }

    private void updateOutlet(JSONObject jsonObject) {
        try {


            JSONArray jsonArray = jsonObject.getJSONObject("outlet").getJSONArray("replaced_relation");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (getPreference("OutletID").equalsIgnoreCase(jsonArray.getJSONObject(i).getString("previous_id"))){
                    savePreference("OutletID",jsonArray.getJSONObject(i).getString("new_id"));
                }
                ds.excQuery("update outlets set isPushed ='1' , outlet_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where outlet_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");
                ds.excQuery("update order_table set outlet_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where outlet_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Flag = 2;
        ds.generatePushJson();
        bf.savePreference("lsyncTime", DateFormatedConverter(getCurrentDate()) + " " + getCurrentTime());
    }

    private void generatemarketJson() throws JSONException {
        JSONObject marketObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Cursor c = ds.rawQuery("select * from markets where is_pushed='0'");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            do {
                JSONObject jsonObject = new JSONObject();

                for (int i = 2; i < Tables.MARKETS.length; i++) {
                    if (Tables.MARKETS[i].equalsIgnoreCase("market_id")) {
                        jsonObject.put("temp_id", c.getString(c.getColumnIndex(Tables.MARKETS[i])));
                        continue;
                    }

                    jsonObject.put(Tables.MARKETS[i], c.getString(c.getColumnIndex(Tables.MARKETS[i])));
                }
                jsonArray.put(jsonObject);
            } while (c.moveToNext());
        }

        marketObj.put("mac", bf.getPreference("mac"));
        marketObj.put("sales_person_id", bf.getPreference(SR_ID));
        marketObj.put("market_list", jsonArray);
        bf.getResponceData(URL.CreateMarket, marketObj.toString(), 109);

    }

    private void generateOutletJson() {
        JSONObject marketObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
        Log.e("outlet", "outlet");

        Cursor c = ds.rawQuery("select * from outlets where isPushed='0'");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            do {
                JSONObject jsonObject = new JSONObject();

                for (int i = 2; i < Tables.OUTLETS.length; i++) {
                    if (Tables.OUTLETS[i].equalsIgnoreCase("outlet_id")) {
                        Log.e("tanvir", c.getString(c.getColumnIndex(Tables.OUTLETS[i]))+" idd");
                        jsonObject.put("temp_id", c.getString(c.getColumnIndex(Tables.OUTLETS[i])));
                        continue;
                    }

                        jsonObject.put(Tables.OUTLETS[i], c.getString(c.getColumnIndex(Tables.OUTLETS[i])));

                }
                jsonArray.put(jsonObject);
            } while (c.moveToNext());
        }

            marketObj.put("mac", bf.getPreference("mac"));
            marketObj.put("sales_person_id", bf.getPreference(SR_ID));
            marketObj.put("outlet_list", jsonArray);
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }
            bf.getResponceData(URL.CreateOutlet, marketObj.toString(), 110);

    }

    @Override
    public void OnConnetivityError() {

        Toast.makeText(SyncActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(SyncActivity.this, Dashboard.class));
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    private void showDetailsDailog() throws JSONException {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(SyncActivity.this);
        final View vv = inflater.inflate(R.layout.dialog_sync_summery, null);
        listView = vv.findViewById(R.id.details);
        JSONObject jsonObject = new JSONObject(bf.getPreference("dataforsummery"));

        for (int i = 0; i < Allfild.length; i++) {
            HashMap<String, String> map = new HashMap<>();
            if (jsonObject.has(Allfild[i][0])) {

                JSONArray jsonArray = jsonObject.getJSONArray(Allfild[i][0]);

                if (jsonArray.length() > 0) {
                    map.put("dataname", Allfild[i][0]);
                    map.put("dataQty", jsonArray.length() + "");
                    list.add(map);
                }

            }
        }

        AdapterForSyncSummery adapter = new AdapterForSyncSummery(SyncActivity.this, list);
        listView.setAdapter(adapter);


        final AlertDialog.Builder alert = new AlertDialog.Builder(
                SyncActivity.this);
        alert.setView(vv);
        //alert.setCancelable(false);

        final AlertDialog dialog = alert.create();
        dialog.show();

    }
}
