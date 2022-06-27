package com.srapp.print;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import jpos.JposConst;
import jpos.POSPrinter;

import com.srapp.Util.Parent;

public class ParentActivity extends Parent {
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	}
	 
	public void logout(int btnLogout)
	{
		Button Logout = (Button) findViewById(btnLogout);
		Logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Constants.RELOADING="reloading";
				savePreference("loginStatus", "logout");
				//startActivity(new Intent(ParentActivity.this, Login.class));
				//finish(); 
			} 
		});
	}
	 
	public void toastShort(String message) {
		Toast.makeText(ParentActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	public void toastLong(String message) {
		Toast.makeText(ParentActivity.this, message, Toast.LENGTH_LONG).show();
	}
//  sharedPreferance

	
	public void exitFromApplicationNotification(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ParentActivity.this);
		builder.setMessage("Are you sure you want to exit?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				//savePreference("isPaused", "no");
				savePreference("isExited", "yes"); 
				savePreference("ApplicationLaunchingStatus", "Not Saved");
				//	System.exit(0);   
				finish();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		}).show();  

	}

	public void exit()
	{
		savePreference("isExited", "yes"); 
		savePreference("ApplicationLaunchingStatus", "Not Saved"); 
		finish();
	}
	
	
	
	
 
}
