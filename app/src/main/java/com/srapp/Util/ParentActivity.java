package com.srapp.Util;


import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.isInternetOn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.R;
import com.srapp.apiService.ApiInterfaceForJava;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParentActivity extends AppCompatActivity {
	public static SQLiteDatabase myDB;
    public ProgressDialog pDialog;
    Data_Source db;
	ApiInterfaceForJava api;
	AlertDialog progressDialog;
	AlertDialog.Builder builder;
	String dateTag = "Date";
	String serverTime = "Time";
	String IsTryTOFake = "try_to_fake";

	Boolean dailogshowing = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = JAPIClient.getClient().create(ApiInterfaceForJava.class);
/*        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
*/

		Log.e("Parent","ParentActivity");

			//getServerDateTime();

	}
	@Override
	protected void onResume() {
		super.onResume();
		getServerDateTime();
	}
	public void getServerDateTime()  {

		if (isInternetOn(this)){
			JSONObject object = null;
			try {
				object = new JSONObject().put("mac", getPreference("mac")).put(IsTryTOFake,getPreference(IsTryTOFake));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			api.getTimeDate(convertTORequestdata(object)).enqueue(new Callback<String>() {
				@Override
				public void onResponse(Call<String> call, Response<String> response) {
					try {
						savePreference(dateTag,new JSONObject(response.body()).getString("date"));
						savePreference(serverTime,new JSONObject(response.body()).getString("time"));
						savePreference(IsTryTOFake,"0");
						showPrintDailog();
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public void onFailure(Call<String> call, Throwable throwable) {

				}
			});
		}else {
			showPrintDailog();
		}
	}

	private void showPrintDailog() {

		if (getPreference(dateTag).equalsIgnoreCase(NO_PREFERENCE)){
			return;
		}
		if (!getCurrentDate().equalsIgnoreCase(getPreference(dateTag))) {
			Log.e("dateSpookyes",getCurrentDate()+" "+getPreference(dateTag));
			savePreference(IsTryTOFake,"1");
			if (builder==null) {
				builder = new AlertDialog.Builder(this)
						.setIcon(R.drawable.alert)
						.setTitle("Warning")
						.setMessage("Device date is wrong.")
						.setCancelable(false);
			}
			if (progressDialog==null)
			progressDialog = builder.create();
			if (!progressDialog.isShowing()){
				progressDialog.show();
			}
		}else {
			Log.e("dateSpookNO",getCurrentDate()+" "+getPreference(dateTag));
			if (progressDialog!=null)
			if (progressDialog.isShowing()){
				progressDialog.dismiss();
			}
		}

	}



	public ArrayList<HashMap<String,String>> reOrder(ArrayList<HashMap<String,String>> hashArray)
	{
		
		db=new Data_Source(this);
		Log.e("hashArray", hashArray.toString());
		ArrayList<Integer> order= new ArrayList<Integer>();
		ArrayList<String> orderWithID= new ArrayList<String>();
		ArrayList<HashMap<String,String>> reOrderList=new ArrayList<HashMap<String,String>>();
		reOrderList.clear();
		for(int i=0;i<hashArray.size();i++)
		{
			String query="SELECT product_order FROM products WHERE product_id='"+hashArray.get(i).get("product_id")+"'";
			Cursor c=db.rawQueryCoustom(query);
			if(c!=null)
			{
				if(c.moveToFirst())
				{
					do{
						order.add(Integer.parseInt(c.getString(0)));
						Log.e(".....ORDER CODE........", c.getString(0));
						orderWithID.add(c.getString(0));
						
					}while(c.moveToNext());
				}
			}

			c.close();
			db.close();
		}
		int[] orderArray = new int[order.size()];
		for (int i = 0; i < order.size(); i++) {
			orderArray[i] = order.get(i);
		}
		Arrays.sort(orderArray);
		
		Log.e("hashArray", hashArray.toString());
		Log.e("hashArray Length:", ""+hashArray.size());
		

		Log.e("orderArray Length:", ""+orderArray.length);
		Log.e("orderWithID Length:", ""+orderWithID.size());
		Log.e("orderWithID ARRAY:", ""+orderWithID.toString());

		for(int i=0;i<orderWithID.size();i++)
		{
			int pos=orderWithID.indexOf(String.valueOf(orderArray[i]));
		    Log.e("OrderArray", ""+orderArray[i]);
		    Log.e("POSITION", ""+pos);
		    reOrderList.add(hashArray.get(pos));
		
		}
		Log.e("RETURN LIST:", reOrderList.toString());
		return reOrderList;
	
		//return hashArray;	
		
	}

	public static String SpecialroundTwoDecimals(String number)
	{
		String totalNumber="0.00";
		String remainder="0.00";
		String firstnumber="0.00";
		if (Double.parseDouble(number)!=Math.floor(Double.parseDouble(number))) {
			remainder = number.replace(".", ",").split(",")[1];
			firstnumber = number.replace(".", ",").split(",")[0];
			if (remainder.length()>2){
				remainder= remainder.substring(0,2);
			}
			return   firstnumber+"."+remainder;
		}else {
			return number;
		}





	}


	public String roundTwoDecimals(double d) {

		
		Log.e("tEST:", String.format("%.2f", d));

		
		Log.e("Double:", String.valueOf(d));
		Log.e("Modulus:", String.valueOf(d%1));

		 /* if(d%1>0.0)
		  {

	     DecimalFormat twoDForm = new DecimalFormat("#.##");
	     String value= String.valueOf(twoDForm.format(d));
	     Log.e("value:", ""+value);
	     Log.e("value.indexOf('.'):", ""+value.indexOf('.'));
	     Log.e("value.length:", ""+value.length());
	     String subValue=value.substring(value.indexOf('.'),value.length());
	     Log.e("LENGTH:", subValue);
	              //if(subValue.length()>1)
	        	 //return String.valueOf(twoDForm.format(d));
	             // else
	 	        	 //return String.valueOf(twoDForm.format(d))+"0";


		  }
		  else
		  {

			     //return String.valueOf(d)+"0";
		  }*/
		  
		  return String.format("%.2f", d);
	 }
    public String roundTwoDecimals(String number)
    {

        double d = Double.parseDouble(number);



        Log.e("tEST:", String.format("%.2f", d));




        Log.e("Double:", String.valueOf(d));
        Log.e("Modulus:", String.valueOf(d%1));

		 /* if(d%1>0.0)
		  {

	     DecimalFormat twoDForm = new DecimalFormat("#.##");
	     String value= String.valueOf(twoDForm.format(d));
	     Log.e("value:", ""+value);
	     Log.e("value.indexOf('.'):", ""+value.indexOf('.'));
	     Log.e("value.length:", ""+value.length());
	     String subValue=value.substring(value.indexOf('.'),value.length());
	     Log.e("LENGTH:", subValue);
	              //if(subValue.length()>1)
	        	 //return String.valueOf(twoDForm.format(d));
	             // else
	 	        	 //return String.valueOf(twoDForm.format(d))+"0";


		  }
		  else
		  {

			     //return String.valueOf(d)+"0";
		  }*/

        return String.format("%.2f", d);
    }



	 public String injectable_product_check()
	 {
		 String is_injectable="0";

		 String outlet = getPreference("OutletID");
		 Log.d("outlete",outlet);

		 String query2="SELECT product_id FROM product_boolean_table WHERE outlet_id='"+getPreference("OutletID")+"' AND boolean='true'";
		 Cursor c23=db.rawQueryCoustom(query2);
		 int count22=c23.getCount();
		 Log.e("QUERY COUNT:", "..............."+count22);
		 if(c23!=null)
		 {
			 if(c23.moveToFirst())
			 {
				 do{
				 	 String productId=c23.getString(0);
					 String query="SELECT is_injectable FROM products WHERE product_id='"+productId+"'";
					 Log.e("QUERY:", "..............."+query);
					 Cursor c=db.rawQueryCoustom(query);
					 int count23=c.getCount();
					 Log.e("QUERY COUNT:", "..............."+count23);

					 if(c!=null)
					 {
						 if(c.moveToFirst())
						 {
							 do{
								 is_injectable=c.getString(0);
								 Log.d("isinjectableoutuput",is_injectable+"productid"+productId);
							 }while(c.moveToNext());
						 }
					 }


				 }while(c23.moveToNext());
			 }
		 }

		 return is_injectable;
	 }

	public void update_table_with_id(String table_name,String table_id,String newID, String previousID)
	{
		 String queryUpdateTable="UPDATE "+table_name+" SET "+table_id+"='"+ newID + "'  WHERE "+ table_id +"='"+previousID+"'";
	     myDB.execSQL(queryUpdateTable);
	     Log.e("queryUpdateTable : "+table_name, queryUpdateTable);
	}


	public void delete_table_with_id(String table_name,String table_id, String previousID)
	{
		 String queryDeleteTable="DELETE FROM "+table_name+"  WHERE "+ table_id +"='"+previousID+"'";
	     myDB.execSQL(queryDeleteTable);
	     Log.e("queryDelete : "+table_name, queryDeleteTable);
	}

	public void buildAlertMessageNoGps() {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder builder = new AlertDialog.Builder(ParentActivity.this);
				builder.setMessage("You need to enable GPS, do you want to enable it?")
						.setCancelable(false)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog, final int id) {
								startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog, final int id) {
								dialog.cancel();
							}
						});
				final AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}
	public void CreateTableForVehicle(ArrayList<String> key) {
		
	/*	
	       myDB =  this.openOrCreateDatabase("sales_db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
	        myDB.setVersion(1);
	        myDB.setLocale(Locale.getDefault());
	        
*/
		String tableCreate="CREATE TABLE IF NOT EXISTS ";
        String tableName=key.get(0);
        String primaryKey="(_id INTEGER PRIMARY KEY,";
        String tableRowName="";
        
        
        for(int i=1;i<key.size();i++)
        {    
        	if(i!=6) 
        	{	
        	 if(i!=key.size()-1)
        	 tableRowName=tableRowName+" "+key.get(i)+" TEXT,";
        	 else
             tableRowName=tableRowName+" "+key.get(i)+" TEXT);";
        	}
        	else
        	{
        		tableRowName=tableRowName+" "+key.get(i)+" TEXT,";
        	}
        }
        
        String sqlString=tableCreate+tableName+primaryKey+tableRowName;
        Log.e("Hello", sqlString);
        myDB.execSQL(sqlString);
	}
	

	
	public void CreateTableForBonus(ArrayList<String> key) {
		
		Log.e("YESSSS", "YESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
		
	/*
	       myDB =  this.openOrCreateDatabase("SMC_SALES_db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
	        myDB.setVersion(1);
	        myDB.setLocale(Locale.getDefault());
	        
*/
		String tableCreate="CREATE TABLE IF NOT EXISTS ";
     String tableName=key.get(0);
     String primaryKey="(_id INTEGER PRIMARY KEY,";
     String tableRowName="";
     
     
     for(int i=1;i<key.size();i++)
     {    
    	 Log.e("key Bonus", ""+i);
    	 Log.e("Field Bonus", key.get(i));
    	 if(i==1||i==4)
    	 {
    		 tableRowName=tableRowName+" "+key.get(i)+" Integer,"; 
    	 }
    	 
    	 else if(i==3||i==6)
    	 {
    		 tableRowName=tableRowName+" "+key.get(i)+" Float,"; 
    	 }
    	 else if(i==8)
    	 {
    		 tableRowName=tableRowName+" "+key.get(i)+" TEXT);"; 
    	 }
    	 else
    	 {
    	    	if(i==7) 
    	     	{	
    	     	 
    	          tableRowName=tableRowName+" "+key.get(i)+" TEXT,";
    	     	}
    	     	else
    	     	{
    	     		tableRowName=tableRowName+" "+key.get(i)+" TEXT,";
    	     	}
    	     	
    	 
    	 }
    	 
    	 
     
     	}
  
     
     String sqlString=tableCreate+tableName+primaryKey+tableRowName;
     Log.e("Hello", sqlString);
  myDB.execSQL(sqlString);
	}
	



	public void InsertTable(ArrayList<String> key)
	{
     /*   myDB =  this.openOrCreateDatabase("SMC_SALES_db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        myDB.setVersion(1);
        myDB.setLocale(Locale.getDefault());
*/
	    String InsertString="INSERT INTO ";
		String TableName=key.get(0);
		String Values=" Values(null,";
        for(int i=1;i<key.size();i++)
        {    
        	 if(i!=key.size()-1)
        		 Values=Values+" '"+key.get(i)+"',";
        	 else
        		 Values=Values+" '"+key.get(i)+"');";
        }
        String INSERT=InsertString+TableName+Values;
        
        Log.e("INSERT", INSERT);
       myDB.execSQL(INSERT);
	}
	
	public void CreateTableWithDate(ArrayList<String> key)
	{
     /*   myDB =  this.openOrCreateDatabase("SMC_SALES_db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        myDB.setVersion(1);
        myDB.setLocale(Locale.getDefault());
*/
		String tableCreate="CREATE TABLE IF NOT EXISTS ";
        String tableName=key.get(0);
        String primaryKey="(_id INTEGER PRIMARY KEY," +key.get(1)+ " Date,";
        
        String tableRowName="";
        for(int i=2;i<key.size();i++)
        {    
        	 if(i!=key.size()-1)
        	 tableRowName=tableRowName+" "+key.get(i)+" TEXT,";
        	 else
             tableRowName=tableRowName+" "+key.get(i)+" TEXT);";
        }

        String sqlString=tableCreate+tableName+primaryKey+tableRowName;
           Log.e("Hello", sqlString);
        myDB.execSQL(sqlString);
	
	}
	public void DropTable(String table_name)
	{
     /*   myDB =  this.openOrCreateDatabase("SMC_SALES_db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        myDB.setVersion(1);
        myDB.setLocale(Locale.getDefault());
     */   myDB.execSQL("DROP TABLE IF EXISTS '" + table_name + "'");
	}
	
	
	
	public Cursor GetAllFields(String table_name)
	{
	      /*  myDB =  this.openOrCreateDatabase("SMC_SALES_db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
	        myDB.setVersion(1);
	        myDB.setLocale(Locale.getDefault());
			*/Cursor  cursor = myDB.rawQuery("select * from "+table_name,null);

		return cursor;
		
	}
	
	public ArrayList<HashMap<String, String>> AllDataFromTable(String table_name)
	{
       /* myDB =  this.openOrCreateDatabase("SMC_SALES_db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        myDB.setVersion(1);
        myDB.setLocale(Locale.getDefault());
		*/Cursor  cursor = myDB.rawQuery("select * from "+table_name,null);
		 ArrayList<HashMap<String, String>> Data= new  ArrayList<HashMap<String, String>>();
		
	    try {

            if (cursor.moveToFirst()) {
                do {
                	HashMap<String, String> map= new HashMap<String, String>();
                	if(table_name.equalsIgnoreCase("doctor_table"))
                	{
                    map.put("id", cursor.getString(1));
                    map.put("name", cursor.getString(2));
                    map.put("type", cursor.getString(3));
                	}
                	
                	else if(table_name.equalsIgnoreCase("bank_branch_table"))
                	{
                    map.put("id", cursor.getString(3));
                    map.put("name", cursor.getString(2));
                	}
               	
                	else
                	{
                     map.put("id", cursor.getString(1));
                     map.put("name", cursor.getString(2));
                		
                	}
                   Data.add(map);
                } while (cursor.moveToNext());
            }

        } finally {
            try { cursor.close(); } catch (Exception ignore) {}
        }


		
		return Data;
		
	}
	
//sharedPreferance....................................................................................
	public void savePreference(String key, String value) 
	{  Log.e("valutt",key+"  "+value);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getPreference(String key)
	{
		String value="";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		value = prefs.getString(key, "NO PREFERENCE");
		
		return value;
		
	}

	String NO_PREFERENCE = "NO PREFERENCE";
	
	public static String getCurrentDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String CurrentDate = ""+dateFormat.format(date);
		
		return CurrentDate;
		
	}

	public static String getCurrentDateforChallan()
	{
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String CurrentDate = ""+dateFormat.format(date);

		return CurrentDate;

	}

	public static String formateDate_forChallan(Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String CurrentDate = ""+dateFormat.format(date);

		return CurrentDate;

	}

	public static String formateDate_forChallanreport(Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String CurrentDate = ""+dateFormat.format(date);

		return CurrentDate;

	}


	public String getCurrentTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String CurrentDate =dateFormat.format(date);

		return CurrentDate;

	}
	public static String getCurrentTime_() {
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
		Date date = new Date();
		String currentTime = dateFormat.format(date);

		return currentTime;
	}
	public String getPrvious3monthsDate()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH,1);
		String formateddate = format.format(cal.getTime());
		return formateddate;
	}

	public String getCurrentDateTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String CurrentDate =dateFormat.format(date);

		return CurrentDate;

	}

	public String getCurrentDateTime24()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String CurrentDate =dateFormat.format(date);
		return CurrentDate;

	}

	public String getCurrentTime24ForMemo()
	{
		DateFormat dateFormat = new SimpleDateFormat("kkmmss");
		Date date = new Date();
		String CurrentDate =dateFormat.format(date);
		return CurrentDate;

	}

	public String getCurrentDateTimeAMPM()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		Date date = new Date();
		String CurrentDate =dateFormat.format(date);
		
		return CurrentDate;
		
	}


	public static  String getCurrentTimeAMPM()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String CurrentDate =dateFormat.format(date);

		return CurrentDate;

	}

	public String ViewFormatedDate(String date)
	{
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		String formateddate = formatter.format(Date.parse(date));
		return formateddate;
	}

//Asynctask Service.........................................................................................................
	
	public String DateFormatedYearFirst(String oldDateString)
	{
		final String OLD_FORMAT = "yyyy-mm-dd";
		final String NEW_FORMAT = "dd-MMM-yyyy";
		Date d = null;

		// August 12, 2010
//		String oldDateString = "12/08/2010";
		String newDateString;

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
	
		try {
			d = sdf.parse(oldDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		newDateString = sdf.format(d);
		
		return newDateString;
		
	}  
	
	
	public String DateFormatedConverter(String oldDateString)
	{
		DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date date;
		String formattedDate="";
		try {
			date = originalFormat.parse(oldDateString);
			 formattedDate = targetFormat.format(date); 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formattedDate;
		
	}  
	
	public String DateFormatedConverter1(String oldDateString)
	{
		DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
		DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date date;
		String formattedDate="";
		try {
			date = originalFormat.parse(oldDateString);
			 formattedDate = targetFormat.format(date); 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("DATE AFTER CONVERT:", formattedDate);
		return formattedDate;
		
	}  






}
