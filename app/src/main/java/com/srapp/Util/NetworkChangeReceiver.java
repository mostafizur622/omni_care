package com.srapp.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.srapp.SyncActivity;

import org.json.JSONException;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                // Check if the network is connected (either mobile data or Wi-Fi)
                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.d("NetworkChangeReceiver", "Network is connected, triggering UpdateLocation...");

                    // Call the UpdateLocation method here
                    try {
                        // Assuming 'this' is the context, update as needed
                        // Assuming 'context' is SyncActivity or an instance that can call UpdateLocation
                        SyncActivity syncActivity = new SyncActivity();
                        syncActivity.UpdateLocation();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Network disconnected
                Log.d("NetworkChangeReceiver", "Network is disconnected.");
            }
        }
    }
}
