package com.srapp.print;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import jpos.JposConst;
import jpos.POSPrinter;

import com.srapp.R;
import com.srapp.Util.JAPIClient;
import com.srapp.Util.Parent;
import com.srapp.apiService.ApiInterfaceForJava;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ParentActivity extends com.srapp.Util.ParentActivity {


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
	public static String getResponsiveText(String db_name) {
		Log.e("text",db_name.length()+"");
		String sr_name[] =("Distribution House: "+db_name).split(" ");
		String db_name_srt = "";
		for (int i = 0 ; i<sr_name.length;i++){
			db_name_srt+=checkName(sr_name,i);
		}

		return db_name_srt;
	}

	public static String checkName(String[] sr_name, int i) {

		if (sr_name.length>i){
			String part = sr_name[i]+" ";
			if (i%4==0 && i!=0){
				part="\n"+part;
			}
			return part;
		}else {
			return "";
		}

	}

	public String DateFormatedConverter(String oldDateString)
	{
		DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);
		DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
		Date date;
		String formattedDate="";
		try {
			date = originalFormat.parse(oldDateString);
			formattedDate = targetFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("date",formattedDate);
		return formattedDate;

	}

	public String getDbAddressMobile() {

		return  "Address: "+getPreference("db_address")+", "+getPreference("db_mobile");
	}





}
