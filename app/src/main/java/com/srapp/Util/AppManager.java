package com.srapp.Util;

import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppManager {

	public static void setOpenKeyBoard(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);
		}
	}

	public static void setCloseKeyBoard(Context context,EditText QuantityEd) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm != null){
	        imm.hideSoftInputFromWindow(QuantityEd.getWindowToken(), 0);
	    }
	}
	
	public static long printDifference(long startDate, long endDate){
	        long different = endDate - startDate;
	        long secondsInMilli = 1000;
	        long minutesInMilli = secondsInMilli * 60;
	        long hoursInMilli = minutesInMilli * 60;

	        long elapsedHours = different / hoursInMilli;

		    Log.d("values",hoursInMilli+"endate"+endDate+"startdate"+startDate+"elapsedhour"+elapsedHours);
	        return elapsedHours;
	    }
	 
	public static long milliseconds(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date mDate = sdf.parse(date);
			long timeInMilliseconds = mDate.getTime();
			System.out.println("Date in milli :: " + timeInMilliseconds);
			return timeInMilliseconds;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
