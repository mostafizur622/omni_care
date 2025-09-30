package com.srapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.srapp.Db_Actions.URL;
import com.srapp.LoginImageCapture.LocationHelper;
import com.srapp.LoginImageCapture.LoginWithImage;
import com.srapp.Util.Parent;
import com.srapp.Util.ParentActivity;
import com.srapp.print.PrintActivity;
import com.srapp.thermalprint.async.usbdevice.UsbDataBinder;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class SplashActivity extends Parent implements BasicFunctionListener {

    BasicFunction basicFunction;
    private LocationHelper locationHelper;
    private volatile Location latestFix;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.splash);


          basicFunction = new BasicFunction(this,this);
          locationHelper = new LocationHelper(this);
          // Check for camera permission
          if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
              // Request camera permission if not granted
              ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
          } else {
              // If permission is already granted, proceed with the login activity
              startLoginActivity();
          }
/*          if (!locationHelper.hasFinePermission()) {
              locationHelper.requestFinePermission();
          } else {
              fetchLocationOrAskSettings();
          }
          if (latestFix == null) {
              Toast.makeText(getApplicationContext(), "Getting location...", Toast.LENGTH_SHORT).show();
              fetchLocationOrAskSettings();
          }*/
          new advertizingId().execute();
	        Handler handler = new Handler();

/*	        handler.postDelayed(new Runnable() {

	            @Override
	            public void run() {

	            	Log.e("HHHHHHHHHHHHHHHHHHHHH", "HHHHHHHH  getPreference(SO)  HHHHHHHHHHH"+getPreference("SO"));
	            	finish();
           		 	Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
	               startActivity(intent);

	            }

	        }, 2000);*/
	  }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

    }

    @Override
    public void OnConnetivityError() {

    }


    public class advertizingId extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            AdvertisingIdClient.Info idInfo = null;
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(SplashActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }

            String advertId = null;
            try{
                advertId = idInfo.getId();
                Log.e("advertId",advertId);
                //d07e9cab-ec01-4b0e-9dd0-4b754073e16b
                if (URL.Domain.contains("202.126.123.157"))
                basicFunction.savePreference("mac","tanvir");
                else
                    basicFunction.savePreference("mac",advertId);
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e("advertId",e.getMessage());
            }
            return null;
        }
    }
    private void fetchLocationOrAskSettings() {
        if (!locationHelper.hasFinePermission()) {
            locationHelper.requestFinePermission();
            return;
        }
        locationHelper.requestSingleFix(new LocationHelper.SingleFixCallback() {
            @Override
            public void onLocationReady(@NonNull Location loc) {
                latestFix = loc;
                Log.e("Location", "Fix: lat=" + loc.getLatitude() + " lon=" + loc.getLongitude()
                        + " acc=" + loc.getAccuracy());
               // Toast.makeText(SplashActivity.this, "Location ready", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(@NonNull String reason) {
                Log.e("Location", "Failed: " + reason);
              //  Toast.makeText(SplashActivity.this, "Location failed: " + reason, Toast.LENGTH_LONG).show();
            }


            @Override
            public void onResolutionRequired(ResolvableApiException resolvable) {
                try {
                    // ইউজারকে Location On করতে “Turn on location?” ডায়ালগ দেখাবে
                    resolvable.startResolutionForResult(SplashActivity.this, 7001);
                } catch (IntentSender.SendIntentException e) {
                    onFailed("Resolution launch failed");
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with login
                startLoginActivity();
            } else {
                // Camera permission denied, show a message
                Toast.makeText(this, "Camera permission is required to proceed", Toast.LENGTH_LONG).show();
                finish(); // Optionally close the app or stay on this screen
            }
        }
    }

    // Method to proceed with starting LoginActivity
    private void startLoginActivity() {
        finish();
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
