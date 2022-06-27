package com.srapp.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.srapp.Db_Actions.Tables;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Parent extends AppCompatActivity {

    public static String Domain = "http://arenaphone.us/tutor_management/";
    public static String UserInfo = Domain+"receive_api_user.php";
    public static String Chek_Code = Domain+"check_security_code.php";
    public static String Login = Domain+"check_user_expiry_date.php";
    Context context;



    public String getPreference(String key)
    {
        String value="";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        value = prefs.getString(key, "0");

        return value;

    }

    /*public static void setFont (){

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("Font/FuturaPTBook.otf")
                .setFontAttrId(R.attr.fontPath).build());
    }*/

    public void savePreference(String key, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public void buildAlertMessageNoGps() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Parent.this);
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
    public String gettext(int id){
        EditText editText = findViewById(id);
        String text = editText.getText().toString().trim();
        return text;
    }
    public void settext(int id, String text){
        EditText editText = findViewById(id);
        editText.setText(text);

    }


    public HashMap<String,String> jsontohasmap(JSONObject jObject){
        HashMap<String, String> map = new HashMap<String, String>();
        try {

        Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = null;

            value = jObject.getString(key);

            map.put(key, value);
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }

        return map;

    }

    public String getCurrentDate (){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);

    }

    public int getIndex(ArrayList<String> arrayList,String value){

        int pos=-1;
        for (int i = 0;i<arrayList.size();i++){
            Log.e("getIndex",arrayList.get(i)+" Value "+value);
            if (arrayList.get(i).equalsIgnoreCase(value)){
                Log.e("match Index",arrayList.get(i)+" Value "+i);
                return i;
            }

        }

        Log.e("notmatch",pos+"");
        return pos;
    }

    public String createJWT(String msg,String data)  {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
       String privateKey = "MIICXAIBAAKBgQC8kGa1pSjbSYZVebtTRBLxBz5H4i2p/llLCrEeQhta5kaQu/RnvuER4W8oDH3+3iuIYW4VQAzyqFpwuzjkDI+17t5t0tyazyZ8JXw+KgXTxldMPEL9 5+qVhgXvwtihXC1c5oGbRlEDvDF6Sa53rcFVsYJ4ehde/zUxo6UvS7UrBQIDAQAB AoGAb/MXV46XxCFRxNuB8LyAtmLDgi/xRnTAlMHjSACddwkyKem8//8eZtw9fzxz bWZ/1/doQOuHBGYZU8aDzzj59FZ78dyzNFoF91hbvZKkg+6wGyd/LrGVEB+Xre0J Nil0GReM2AHDNZUYRv+HYJPIOrB0CRczLQsgFJ8K6aAD6F0CQQDzbpjYdx10qgK1 cP59UHiHjPZYC0loEsk7s+hUmT3QHerAQJMZWC11Qrn2N+ybwwNblDKv+s5qgMQ5 5tNoQ9IfAkEAxkyffU6ythpg/H0Ixe1I2rd0GbF05biIzO/i77Det3n4YsJVlDck ZkcvY3SK2iRIL4c9yY6hlIhs+K9wXTtGWwJBAO9Dskl48mO7woPR9uD22jDpNSwe k90OMepTjzSvlhjbfuPN1IdhqvSJTDychRwn1kIJ7LQZgQ8fVz9OCFZ/6qMCQGOb qaGwHmUK6xzpUbbacnYrIM6nLSkXgOAwv7XXCojvY614ILTK3iXiLBOxPu5Eu13k eUz9sHyD6vkgZzjtxXECQAkp4Xerf5TGfQXGXhxIX52yH+N2LtujCdkQZjXAsGdm B2zNzvrlgRmgBrklMTrMYgm1NPcW+bRLGcwgW2PTvNM=";
        Key signingKey = new SecretKeySpec(privateKey.getBytes(), signatureAlgorithm.getJcaName());
        HashMap<String,Object> map = new HashMap<>();
        map.put("typ","JWT");
        map.put("alg","HS256");
        JwtBuilder builder = Jwts.builder()
                .setHeader(map)
                  .setPayload(data)
                .signWith(signatureAlgorithm,
                        signingKey);
        JSONObject object = new JSONObject();

        Log.e("data",data);
        try {
            object.put("msg",msg);
            object.put("data",builder.compact());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return object.toString();
    }


    public String getCurrentDate2 (){
        Calendar cal = Calendar.getInstance();
        // calx.set(calx.get(Calendar.YEAR), calx.get(Calendar.MONTH), calx.get(Calendar.DAY_OF_MONTH));
        Date date = cal.getTime();
        DateFormat fmt = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        return fmt.format(date);

    }
    public String getCurrentMonth (){
        Calendar cal = Calendar.getInstance();
        // calx.set(calx.get(Calendar.YEAR), calx.get(Calendar.MONTH), calx.get(Calendar.DAY_OF_MONTH));
        Date date = cal.getTime();
        DateFormat fmt = new SimpleDateFormat("MMMM , yyyy", Locale.US);
        return fmt.format(date);

    }

    public String getCurrentDate1 (){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);

    }

    String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }




    public String getCurrentTime (){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
        Date date = cal.getTime();
        Time tme = new Time(cal.get(Calendar.HOUR),cal.get(Calendar.MINUTE),0);
        //seconds by
        // default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        String time = formatter.format(tme);
        Log.e("time",time);
        String string[] = time.toString().split(" ");
        if (cal.get(Calendar.AM_PM)==Calendar.AM){
            time=string[0]+" am";
        }else {

            time=string[0]+" pm";
        } Log.e("time",time);
        return time;

    }

    public void ShowToast (String text ){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();

    }

    public String getDayname(){
        String DayName="";

        Calendar cal = Calendar.getInstance();
        Log.e("dayno",cal.get(Calendar.DAY_OF_WEEK)+"");
        switch (cal.get(Calendar.DAY_OF_WEEK)){
            case 7 :
                DayName = "Saturday";
                break;
            case 1 :
                DayName = "Sunday";
                break;
            case 2 :
                DayName = "Monday";
                break;
            case 3 :
                DayName = "Tuesday";
                break;
            case 4 :
                DayName = "Wednesday";
                break;
            case 5 :
                DayName = "Thursday";
                break;
            case 6 :
                DayName = "Friday";
                break;


        }


        return DayName;
    }

    public String roundTwoDecimals(double d)
    {



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
    public String getCurrentTime24ForMemo()
    {
        DateFormat dateFormat = new SimpleDateFormat("kkmmss");
        Date date = new Date();
        String CurrentDate =dateFormat.format(date);
        return CurrentDate;

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
    public String getCurrentDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String CurrentDate =dateFormat.format(date);

        return CurrentDate;

    }
    /*public String JsonCreate(String data[],int status){

        JSONObject jsonObject = new JSONObject();

        for (int i=0 ; i<Tables.UCL.length;i++){
            try {
                jsonObject.put(Tables.UCL[i],data[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            jsonObject.put("flag",status);
            jsonObject.put("security","Tutor");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();

    }*/
}
