 package com.srapp;

 import static com.srapp.Db_Actions.Tables.Allfild;
 import static com.srapp.Db_Actions.Tables.SR_ID;
 import static com.srapp.Db_Actions.URL.CheckConnection;
 import static com.srapp.Db_Actions.URL.convertTORequestdata;
 import static com.srapp.Db_Actions.URL.getJAPi;

 import android.app.AlertDialog;
 import android.app.ProgressDialog;
 import android.content.Context;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.database.Cursor;
 import android.graphics.Color;
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
 import android.widget.LinearLayout;
 import android.widget.ListView;
 import android.widget.TextView;
 import android.widget.Toast;
 import androidx.annotation.RequiresApi;

 import com.google.firebase.crashlytics.FirebaseCrashlytics;
 import com.srapp.Adapter.AdapterForSyncSummery;
 import com.srapp.Db_Actions.DBListener;
 import com.srapp.Db_Actions.Data_Source;
 import com.srapp.Db_Actions.Tables;
 import com.srapp.Db_Actions.URL;
 import com.srapp.Util.AuthPreference;
 import com.srapp.Util.Parent;
 import com.srapp.print.ParentActivity;
 import com.tanvir.BasicFun.BasicFunction;
 import com.tanvir.BasicFun.BasicFunctionListener;

 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.concurrent.TimeUnit;

 import retrofit2.Call;
 import retrofit2.Callback;
 import retrofit2.Response;


 public class SyncActivity extends Parent implements DBListener, BasicFunctionListener {

    Button sync_btn, viewSummery;
    TextView pendingMemo,pendingAttendanceStatus,pendingOutlet,pendingMarket;
    Data_Source ds;
    int Flag = 0;
    ListView listView;
    BasicFunction bf;
    ImageView homeBtn, backBtn;
    TextView userIdTV, titleTV, date;
     String loginFacility="1";
     private AuthPreference authPreference;
     LinearLayout pendingAttendance;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);




        bf = new BasicFunction(this, this);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        authPreference = new AuthPreference(getApplicationContext());
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
        pendingOutlet = findViewById(R.id.pendingOutlet);
        pendingMarket = findViewById(R.id.pendingMarket);
        pendingMemo.setText(ds.getPendingOrderCount());
        pendingMarket.setText(ds.getPendingMarket());
        pendingOutlet.setText(ds.getPendingOutlet());
        viewSummery.setVisibility(View.GONE);
         FirebaseCrashlytics.getInstance().setUserId(getPreference("sr_uname"));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        SharedPreferences sharedPreferences = getSharedPreferences("Location", Context.MODE_PRIVATE);
        loginFacility = sharedPreferences.getString("attendance_online", null);
        pendingAttendance = findViewById(R.id.pendingAttendance);
         pendingAttendanceStatus = findViewById(R.id.pendingAttendanceStatus);

        if (loginFacility.equalsIgnoreCase("0")){
            if (authPreference.getPendingAttendance().equalsIgnoreCase("1")) {
                //pendingAttendance.setVisibility(View.VISIBLE);
                pendingAttendanceStatus.setText("Yes");
                pendingAttendanceStatus.setTextColor(Color.parseColor("#004714"));
            }else {
                pendingAttendanceStatus.setText("No");
                pendingAttendanceStatus.setTextColor(Color.parseColor("#ff2200"));
            }
        }else {
           // pendingAttendance.setVisibility(View.GONE);
        }
        Log.e("loginFacility",loginFacility+"");

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
                //if(checkTime())
                if (isInternetOn()){
                    try {
                        bf.savePreference("lastSyncTime",(System.currentTimeMillis()+(5*60*1000))+"");
                        bf.savePreference("lsyncTime", DateFormatedConverter(getCurrentDate()) + " " + getCurrentTime());
                        Log.e("lastSyncTime",(System.currentTimeMillis()+(5*60*1000))+"");
                     //   String url = URL.UpdatePushTime;

                        JSONObject obJson = new JSONObject();
                        obJson.put(SR_ID, getPreference(SR_ID));
                        obJson.put("version", URL.VERSION);
                        obJson.put("mac", bf.getPreference("mac"));
                        Log.e("++", "Update Time json:" + obJson.toString());


                        ProgressDialog dailog = CheckConnection(SyncActivity.this,"Checking...");
                        if (dailog==null)
                            return;
                        getJAPi().UpdatePushTime(convertTORequestdata(obJson)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();

                                    UpdateLocation();

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });


                        //bf.getResponceData(url, obJson.toString(), 1412);

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
     private void getTradeOfferPolicy() {
         JSONObject dataObject = new JSONObject();
         ProgressDialog dailog = CheckConnection(SyncActivity.this,"Saving Attendance Data...");
         if (dailog==null)
             return;
         try {
             dataObject.put("so_id",bf.getPreference(SR_ID));
             dataObject.put("mac",bf.getPreference("mac"));
             dataObject.put("date",authPreference.getCheckInDate());
             dataObject.put("in_time",authPreference.getInTime());
             dataObject.put("out_time",authPreference.getOutTime());
             dataObject.put("in_lat",authPreference.getInLat());
             dataObject.put("in_long",authPreference.getInLong());
             dataObject.put("out_lat",authPreference.getOutLat());
             dataObject.put("out_long",authPreference.getOutLong());
             Log.e("Payload", dataObject.toString());
             getJAPi().Policy_Bonus_Applicable(convertTORequestdata(dataObject)).enqueue(new Callback<String>() {
                 @Override
                 public void onResponse(Call<String> call, Response<String> response) {

                     Log.e("onResponse", "onResponse: "+response.toString() );

                     dailog.dismiss();
                     if (response.code()==200 && response.isSuccessful()) {
                         authPreference.setPendingAttendance("0");

                         Toast.makeText(SyncActivity.this,   "Sync Successfully", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(SyncActivity.this, SyncActivity.class));
                         finish();
                     }else {

                         Toast.makeText(SyncActivity.this,   "Failed try Again", Toast.LENGTH_SHORT).show();
                     }

                 }

                 @Override
                 public void onFailure(Call<String> call, Throwable t) {
                     dailog.dismiss();
                     Toast.makeText(SyncActivity.this,   "Try again", Toast.LENGTH_SHORT).show();
                 }
             });
         } catch (JSONException e) {
             throw new RuntimeException(e);
         }
     }
     private boolean checkTime() {
         if (URL.Domain.contains("202")){
                return true;
         }
         if (bf.getPreference("lastSyncTime").equalsIgnoreCase("null")){
             return true;
         }

         long lastSyncTime = Long.parseLong(bf.getPreference("lastSyncTime"));
         long timeRem = lastSyncTime-System.currentTimeMillis();
         Log.e("exception",timeRem+"");
         if (timeRem<1){
             return true;
         }

         if (timeRem<(60*1000)){
             Toast.makeText(this, "Please Wait less than a Minute TO Sync Again", Toast.LENGTH_SHORT).show();
         }else {
             Toast.makeText(this, "Please Wait "+ TimeUnit.MILLISECONDS.toMinutes(timeRem)+" Minute TO Sync Again", Toast.LENGTH_SHORT).show();
         }

         return false;
     }

     @Override
    public void OnLocalDBdataRetrive(final String json) {


        Log.e("OnLocalDBdataRetrive", "OnLocalDBdataRetrive: "+json );
        runOnUiThread(() -> {
            try {

                if (json.toString().equalsIgnoreCase("done")) {
//                    pendingMemo.setText(ds.getPendingOrderCount());
//                    viewSummery.setVisibility(View.VISIBLE);
                    Toast.makeText(SyncActivity.this, "Sync Complete", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SyncActivity.this, SyncActivity.class));
                    finish();
                }

                JSONObject jsonObject = new JSONObject(json);
                jsonObject.put("mac", bf.getPreference("mac"));

                if (Flag == 1) {
                   // bf.getResponceData(URL.PULL, jsonObject.toString(), 102);

                    ProgressDialog dailog = CheckConnection(SyncActivity.this,"Checking...");
                    if (dailog==null)
                        return;
                    getJAPi().PULL(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                dailog.dismiss();
                                try {
                                    ds.excQuery("delete from product_history");
                                    ds.excQuery("delete from fiscal_year");
                                    ds.excQuery("delete  from stock_info");
                                    ds.excQuery("delete  from "+Tables.TABLE_OUTLET_PERMISSION);
                                    ds.insertData(jsonObject.getJSONObject("response").toString(), 1);
                                    ds.savePreference("dataforsummery", jsonObject.getJSONObject("response").toString());
                                    if (loginFacility.equalsIgnoreCase("0")){
                                        getTradeOfferPolicy();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            dailog.dismiss();
                        }
                    });
                } else if (Flag == 2) {
                    ds.updatePushStatus();
                    Flag = 1;
                    ds.getlastupdateddate();
/*                   // bf.getResponceData(URL.ORDERPUSH, jsonObject.toString(), 103);
                    ProgressDialog dailog = CheckConnection(SyncActivity.this,"Order Sync...");
                    if (dailog==null)
                        return;
                    getJAPi().ORDERPUSH(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                           // Log.e("log", "onResponse: ",response );

                            dailog.dismiss();
                            if (response.code()==200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    int status = jsonObject.getJSONObject("order").getInt("status");

                                    if (status == 1) {

                                        ds.updatePushStatus();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }else {

                                Toast.makeText(SyncActivity.this,   "Order Not push try Again or Check Order Process from More", Toast.LENGTH_SHORT).show();
                            }

                            Flag = 1;
                            ds.getlastupdateddate();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            dailog.dismiss();
                            Toast.makeText(SyncActivity.this,   "Order Not push try Again or Check Order Process from More", Toast.LENGTH_SHORT).show();
                        }
                    });*/
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
         Log.e("JSSSSSS",jsonObject.toString());
        if (jsonObject.toString().equalsIgnoreCase("done")) {

            Toast.makeText(SyncActivity.this, "Complete", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SyncActivity.this, SyncActivity.class));
            finish();
        }

        if (i == 102) {

        } else if (i == 103) {




        }  else if (i == 1412) {




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

    private void updateMarket(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONObject("market").getJSONArray("replaced_relation");

            for (int i = 0; i < jsonArray.length(); i++) {
                if (getPreference("MarketID").equalsIgnoreCase(jsonArray.getJSONObject(i).getString("previous_id"))){
                    savePreference("MarketID",jsonArray.getJSONObject(i).getString("new_id"));
                }
                ds.excQuery("update markets set is_pushed ='1' , market_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where market_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");
                ds.excQuery("update outlets set market_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where market_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");
                ds.excQuery("update temp_outlets set market_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where market_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");
                ds.excQuery("update order_table set market_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where market_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");

            }
        } catch (JSONException e) {
            Log.e("marketjsx",e.getMessage());
            e.printStackTrace();
        }

        UpdateLocation();


    }

     public void UpdateLocation() throws JSONException {

         JSONObject marketObj = new JSONObject();
         JSONArray jsonArray = new JSONArray();
         ds.sqLiteDatabase.execSQL("DELETE FROM gps_tracker\n" +
                 "WHERE _id NOT IN (\n" +
                 "  SELECT MIN(_id) \n" +
                 "  FROM gps_tracker \n" +
                 "  GROUP BY latitude, longitude\n" +
                 ")");
         Cursor c = ds.sqLiteDatabase.rawQuery("select * from gps_tracker where is_pushed='0'",null);
         c.moveToFirst();
         if (c != null && c.getCount() > 0) {
             do {
                 JSONObject jsonObject = new JSONObject();

                 jsonObject.put(Tables.GPS_TRACKER_latitude, c.getString(c.getColumnIndex(Tables.GPS_TRACKER_latitude)));
                 jsonObject.put(Tables.GPS_TRACKER_longitude, c.getString(c.getColumnIndex(Tables.GPS_TRACKER_longitude)));
                 jsonObject.put(Tables.GPS_TRACKER_STATUS, c.getString(c.getColumnIndex(Tables.GPS_TRACKER_STATUS)));
                 jsonObject.put(Tables.GPS_TRACKER_ADDRESS, c.getString(c.getColumnIndex(Tables.GPS_TRACKER_ADDRESS)));
                 //jsonObject.put(Tables.GPS_TRACKER_NETWORK_TYPE, c.getString(c.getColumnIndex(Tables.GPS_TRACKER_NETWORK_TYPE)));
                 jsonObject.put(Tables.GPS_TRACKER_DISTANCE, c.getString(c.getColumnIndex(Tables.GPS_TRACKER_DISTANCE)));
                 jsonObject.put(Tables.BATTERY_LEVEL, c.getString(c.getColumnIndex(Tables.BATTERY_LEVEL)));
                 jsonObject.put(Tables.GPS_TRACKER_created_at, c.getString(c.getColumnIndex(Tables.GPS_TRACKER_created_at)));



                 jsonArray.put(jsonObject);
             } while (c.moveToNext());
         }

         marketObj.put("mac", bf.getPreference("mac"));
         marketObj.put("sales_person_id", bf.getPreference(SR_ID));
         marketObj.put("coordinates", jsonArray);
         ProgressDialog dailog = CheckConnection(SyncActivity.this,"Checking...");
         if (dailog==null)
             return;
         getJAPi().pushLocation(convertTORequestdata(marketObj)).enqueue(new Callback<String>() {
             @Override
             public void onResponse(Call<String> call, Response<String> response) {
                 try {
                     JSONObject jsonObject = new JSONObject(response.body());
                     JSONObject locationObj = jsonObject.getJSONObject("response");
                     String status = locationObj.getString("status");
                     if (status.equalsIgnoreCase("1")){
                         ds.excQuery("UPDATE gps_tracker SET is_pushed='1' WHERE is_pushed='0'");

                         if (loginFacility.equalsIgnoreCase("0")){
                             getTradeOfferPolicy();
                         }else {
                             Toast.makeText(SyncActivity.this, "Sync Successfully", Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(SyncActivity.this, SyncActivity.class));
                             finish();
                         }

                     }
                     dailog.dismiss();

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

     private void updateOutletVisit() throws JSONException {
         JSONObject marketObj = new JSONObject();
         JSONArray jsonArray = new JSONArray();
         Cursor c = ds.sqLiteDatabase.rawQuery("select * from outlet_visit where isPushed='0'",null);
         c.moveToFirst();
         if (c != null && c.getCount() > 0) {
             do {
                 JSONObject jsonObject = new JSONObject();
                 jsonObject.put(Tables.OUTLET_VISIT_OUTLET_ID, c.getString(c.getColumnIndex(Tables.OUTLET_VISIT_OUTLET_ID)));
                 jsonObject.put(Tables.OUTLET_VISIT_LATITUTEDE, c.getString(c.getColumnIndex(Tables.OUTLET_VISIT_LATITUTEDE)));
                 jsonObject.put(Tables.OUTLET_VISIT_LONGITUD, c.getString(c.getColumnIndex(Tables.OUTLET_VISIT_LONGITUD)));
                 jsonObject.put(Tables.OUTLET_VISIT_DATE, c.getString(c.getColumnIndex(Tables.OUTLET_VISIT_DATE)));

                 jsonArray.put(jsonObject);
             } while (c.moveToNext());
         }

         marketObj.put("mac", bf.getPreference("mac"));
         marketObj.put("sales_person_id", bf.getPreference(SR_ID));
         marketObj.put("outlet_visits", jsonArray);

         ProgressDialog dailog = CheckConnection(SyncActivity.this,"Checking...");
         if (dailog==null)
             return;
         getJAPi().pushOutletVisit(convertTORequestdata(marketObj)).enqueue(new Callback<String>() {
             @Override
             public void onResponse(Call<String> call, Response<String> response) {
                 try {
                     JSONObject jsonObject = new JSONObject(response.body());
                     dailog.dismiss();
                     Log.e("PushOutletResponse",jsonObject.toString());
                     Log.e("Status",jsonObject.getJSONObject("outlet_visit").getString("status"));
                     if (jsonObject.getJSONObject("outlet_visit").getString("status").equalsIgnoreCase("1")){
                         ds.excQuery("UPDATE outlet_visit SET isPushed='1' WHERE isPushed='0'");
                     }

                     Flag = 2;
                     ds.generatePushJson();
                     bf.savePreference("lsyncTime", DateFormatedConverter(getCurrentDate()) + " " + getCurrentTime());
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

         generateTempOutletJson();

     }



     private void updateTempOutlet(JSONObject jsonObject) {
         try {


             JSONArray jsonArray = jsonObject.getJSONObject("outlet").getJSONArray("replaced_relation");
             for (int i = 0; i < jsonArray.length(); i++) {
                 if (getPreference("OutletID").equalsIgnoreCase(jsonArray.getJSONObject(i).getString("previous_id"))){
                     savePreference("OutletID",jsonArray.getJSONObject(i).getString("new_id"));
                 }
                 ds.excQuery("update "+Tables.TABLE_NAME_TEMP_OUTLETS+" set isPushed ='1' , outlet_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where outlet_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");
                 ds.excQuery("update order_table set outlet_id='" + jsonArray.getJSONObject(i).getString("new_id") + "' where outlet_id='" + jsonArray.getJSONObject(i).getString("previous_id") + "'");

             }


         } catch (JSONException e) {
             e.printStackTrace();
         }

         try {
             updateOutletVisit();
         } catch (JSONException e) {
             throw new RuntimeException(e);
         }

     }

    private void generatemarketJson() throws JSONException {
        JSONObject marketObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Cursor c = ds.rawQueryCoustom("select * from markets where is_pushed='0'");
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
        //bf.getResponceData(URL.CreateMarket, marketObj.toString(), 109);

        ProgressDialog dailog = CheckConnection(SyncActivity.this,"Checking...");
        if (dailog==null)
            return;
        getJAPi().CreateMarket(convertTORequestdata(marketObj)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();

                    updateMarket(jsonObject);
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

    private void generateOutletJson() {
        JSONObject marketObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
        Log.e("outlet", "outlet");

        Cursor c = ds.rawQueryCoustom("select * from outlets where isPushed='0'");
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
           // bf.getResponceData(URL.CreateOutlet, marketObj.toString(), 110);

        ProgressDialog dailog = CheckConnection(SyncActivity.this,"Checking...");
        if (dailog==null)
            return;
        getJAPi().CreateOutlet(convertTORequestdata(marketObj)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
                    updateOutlet(jsonObject);

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


     private void generateTempOutletJson() {
         JSONObject marketObj = new JSONObject();
         JSONArray jsonArray = new JSONArray();
         try {
             Log.e("outlet", "outlet");

             Cursor c = ds.rawQueryCoustom("select * from "+Tables.TABLE_NAME_TEMP_OUTLETS+" where isPushed='0'");
             c.moveToFirst();
             if (c != null && c.getCount() > 0) {
                 do {
                     JSONObject jsonObject = new JSONObject();

                     for (int i = 2; i < Tables.TEMP_OUTLETS.length; i++) {
                         if (Tables.TEMP_OUTLETS[i].equalsIgnoreCase("outlet_id")) {
                             Log.e("tanvir", c.getString(c.getColumnIndex(Tables.TEMP_OUTLETS[i]))+" idd");
                             jsonObject.put("temp_id", c.getString(c.getColumnIndex(Tables.TEMP_OUTLETS[i])));
                             continue;
                         }

                         jsonObject.put(Tables.TEMP_OUTLETS[i], c.getString(c.getColumnIndex(Tables.TEMP_OUTLETS[i])));

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
         // bf.getResponceData(URL.CreateOutlet, marketObj.toString(), 110);

         ProgressDialog dailog = CheckConnection(SyncActivity.this,"Checking...");
         if (dailog==null)
             return;
         getJAPi().CreateTempOutlet(convertTORequestdata(marketObj)).enqueue(new Callback<String>() {
             @Override
             public void onResponse(Call<String> call, Response<String> response) {
                 try {
                     JSONObject jsonObject = new JSONObject(response.body());
                     dailog.dismiss();
                     updateTempOutlet(jsonObject);

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
