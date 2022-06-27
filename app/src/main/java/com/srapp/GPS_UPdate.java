package com.srapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.Parent;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.srapp.Db_Actions.Tables.OUTLETS_LATITUDE;
import static com.srapp.Db_Actions.Tables.OUTLETS_LONGITUTE;
import static com.srapp.Db_Actions.Tables.OUTLET_ID;

public class GPS_UPdate extends Parent implements BasicFunctionListener {

    TextView location,clocation;
    Button update_loc;
    private static  final int REQUEST_LOCATION=1;
    String lattitude,longitude;
    LocationManager locationManager;
    ImageView backBtn, homeBtn;
    BasicFunction bF;
    TextView userIdTV,titleTV;
    Data_Source db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps__update);
        location = findViewById(R.id.location);
        clocation = findViewById(R.id.clocation);
        update_loc = findViewById(R.id.update_loc);
        bF = new BasicFunction(this,this);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        db = new Data_Source(this);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( GPS_UPdate.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( GPS_UPdate.this, Create_New_Memo.class));
                finish();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        
        

        clocation.setText("Not Updated");
        getOutLetLocation();
        
        update_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //Check gps is enable or not

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    //Write Function To enable gps
                    OnGPS();
                }
                else
                {
                    //GPS is already On then
                    getLocation();
                }
                
                
            }
        });
        
    }

    private void getOutLetLocation() {

        Cursor c = db.rawQuery("select latitude,longitude from outlets where outlet_id='"+bF.getPreference("OutletID")+"'");
            c.moveToFirst();
        if (c.getCount()>0 && c!=null){

            location.setText(c.getString(0)+" ,\n"+c.getString(1));

        }


    }


    private void pushLocationInfo(String lattitude, String longitude) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OUTLET_ID,bF.getPreference("OutletID"));
            jsonObject.put(OUTLETS_LATITUDE,lattitude);
            jsonObject.put(OUTLETS_LONGITUTE,longitude);
            jsonObject.put("mac",bF.getPreference("mac"));
            jsonObject.put("sales_person_id",bF.getPreference("sales_person_id"));
            bF.getResponceData(URL.GPS_UPDATE,jsonObject.toString(),100);
        } catch (JSONException e) {
            e.printStackTrace();
        }





    }

    private void getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(GPS_UPdate.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPS_UPdate.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            android.location.Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            android.location.Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            android.location.Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps !=null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();
                lattitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                Log.e("Location", "getLocation: +"+"Your Location:"+"\n"+"Latitude= "+lattitude+"\n"+"Longitude= "+longitude);
                Toast.makeText(GPS_UPdate.this,"GPS Updated Successfully", Toast.LENGTH_LONG).show();
                pushLocationInfo(lattitude,longitude);
            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                lattitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                clocation.setText(getPreference("lat")+" ,\n"+getPreference("lon"));
                Log.e("Loc", "getLocation: "+"Your Location:"+"\n"+"Latitude= "+lattitude+"\n"+"Longitude= "+longitude);
                Toast.makeText(GPS_UPdate.this,"GPS Updated Successfully", Toast.LENGTH_LONG).show();
                pushLocationInfo(lattitude,longitude);
                savePreference("lat",lattitude);
               savePreference("lon",longitude);
            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                lattitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
               savePreference("lat",lattitude);
                savePreference("lon",longitude);
                clocation.setText(getPreference("lat")+" ,\n"+getPreference("lon"));

                Log.e("Loc", "getLocation: "+"Your Location:"+"\n"+"Latitude= "+lattitude+"\n"+"Longitude= "+longitude);
                // Toast.makeText(GPS_UPdate.this,"Lat : "+lattitude+" Long: "+longitude, Toast.LENGTH_LONG).show();
                pushLocationInfo(lattitude,longitude);
            }
            else
            {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }

            //Thats All Run Your App
        }

    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent idd = new Intent(GPS_UPdate.this, Create_New_Memo.class);
            startActivity(idd);
            finish();


        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {

        db.excQuery("update outlets set "+OUTLETS_LATITUDE+"='"+lattitude+"' , "+OUTLETS_LONGITUTE+"='"+longitude+"' where outlet_id='"+bF.getPreference("OutletID")+"'");

    }

    @Override
    public void OnConnetivityError() {

    }
}
