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
import android.os.Build;
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
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;

public class SplashActivity extends Parent implements BasicFunctionListener {

    BasicFunction basicFunction;
    private LocationHelper locationHelper;
    private volatile Location latestFix;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQ_ONE_BY_ONE = 501;
    // ---- queue to hold pending permissions ----
    private final ArrayDeque<String> pendingPerms = new ArrayDeque<>();

    private boolean isGranted(String perm) {
        return ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;
    }
    private void enqueueNeededPermissions() {
        pendingPerms.clear();

        // 1) CAMERA (সব ভার্সনে লাগবে)
        if (!isGranted(Manifest.permission.CAMERA)) {
            pendingPerms.add(Manifest.permission.CAMERA);
        }

        // 2) Activity Recognition (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!isGranted(Manifest.permission.ACTIVITY_RECOGNITION)) {
                pendingPerms.add(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        }

        // 3) Post Notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isGranted(Manifest.permission.POST_NOTIFICATIONS)) {
                pendingPerms.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void requestNextPermission() {
        if (pendingPerms.isEmpty()) {
            onAllPermissionsDone();
            return;
        }
        String next = pendingPerms.peek(); // look only
        ActivityCompat.requestPermissions(this, new String[]{ next }, REQ_ONE_BY_ONE);
    }

    private boolean allRequiredGranted() {
        boolean camera = isGranted(Manifest.permission.CAMERA);
        boolean ar = (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) || isGranted(Manifest.permission.ACTIVITY_RECOGNITION);
        boolean notif = (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) || isGranted(Manifest.permission.POST_NOTIFICATIONS);
        return camera && ar && notif;
    }
    private void onAllPermissionsDone() {
        // সব দরকারি permission প্রসেস শেষ — grant থাকুক/না থাকুক এখানেই ফাইনাল ডিসিশন নাও
        // এখানে তোমার requirement: সবগুলো grant হলে তবেই login এ যাবে
        if (allRequiredGranted()) {
            startLoginActivity();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Permissions required")
                    .setMessage("Please grant Camera, Activity Recognition (Android 10+), and Notification (Android 13+) permissions to continue.")
                    .setPositiveButton("Try Again", (d, w) -> {
                        enqueueNeededPermissions();
                        requestNextPermission();
                    })
                    .setNegativeButton("Exit", (d, w) -> finish())
                    .show();
        }
    }


	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.splash);


          basicFunction = new BasicFunction(this,this);
          locationHelper = new LocationHelper(this);
          enqueueNeededPermissions();

          if (pendingPerms.isEmpty()) {
              // সব আগে থেকেই granted থাকলে সরাসরি Login
              startLoginActivity();
          } else {
              requestNextPermission();
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
/*    @Override
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
    }*/
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode != REQ_ONE_BY_ONE || permissions.length == 0) return;

    String asked = permissions[0];
    boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

    if (granted) {
        // remove and move next
        pendingPerms.poll();
        requestNextPermission();
    } else {
        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, asked);
        if (!showRationale) {
            // permanently denied
            new AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Please enable \"" + asked + "\" from App Settings to continue.")
                    .setPositiveButton("Open Settings", (d, w) -> {
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.setData(android.net.Uri.parse("package:" + getPackageName()));
                        startActivity(i);
                    })
                    .setNegativeButton("Cancel", (d, w) -> onAllPermissionsDone())
                    .show();
        } else {
            // soft deny — retry or skip (skip করলে পরেরটায় যাবে)
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("App needs \"" + asked + "\" permission to work properly.")
                    .setPositiveButton("Allow", (d, w) -> requestNextPermission()) // re-ask same
                    .setNegativeButton("Skip", (d, w) -> {
                        pendingPerms.poll(); // drop current and move next
                        requestNextPermission();
                    })
                    .show();
        }
    }
}
    // Method to proceed with starting LoginActivity
    private void startLoginActivity() {
        if (basicFunction.getPreference("saveLogin").equalsIgnoreCase("1")){
            Intent intent = new Intent(SplashActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        }else {
            finish();
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
