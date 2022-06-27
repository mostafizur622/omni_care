package com.srapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.srapp.Util.Parent;
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



	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.splash);


          basicFunction = new BasicFunction(this,this);

          new advertizingId().execute();
	        Handler handler = new Handler();

	        handler.postDelayed(new Runnable() {
	 
	            @Override
	            public void run() {
	            	
	            	Log.e("HHHHHHHHHHHHHHHHHHHHH", "HHHHHHHH  getPreference(SO)  HHHHHHHHHHH"+getPreference("SO"));
	            	finish();
           		 	Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
	               startActivity(intent);
	            	
	            }
	 
	        }, 2000);
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
                basicFunction.savePreference("mac",advertId);
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e("advertId",e.getMessage());
            }
            return null;
        }
    }
}
