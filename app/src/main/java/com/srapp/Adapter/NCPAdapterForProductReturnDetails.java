package com.srapp.Adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.srapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class NCPAdapterForProductReturnDetails extends BaseAdapter {

	// Declare Variables
	Context context;
	String SO_ID;
	
	HashMap<String,Double> stockmap;
	 static final int DATE_PICKER_ID1 = 1111; 
	 ArrayList<String> product_id;
	 private int year1;
	 private int month1;
	 private int day1;
	 int pos=0;
	
	ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
	
	public NCPAdapterForProductReturnDetails(Context context, ArrayList<HashMap<String, String>> arraylistContent) {
		this.context = context;
		itemListContent = arraylistContent;
		stockmap = new HashMap<>();
		product_id = new ArrayList<>();
	




	}

	
	@Override
	public int getCount() {
		return itemListContent.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
//		return position;
	}

	public ArrayList<HashMap<String, String>>  getAlldata() {
		return itemListContent;
	}

	
	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, final ViewGroup parent) {

		TextView product;
		EditText batch, quantity;
		TextView exp;
		ImageView action;
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ncp_collection_item, null);
		product = itemView.findViewById(R.id.product_name);
		batch = itemView.findViewById(R.id.batch);
		exp = itemView.findViewById(R.id.exp_date);
		quantity = itemView.findViewById(R.id.qty);
		action = itemView.findViewById(R.id.dlt_product_sales_item);

	
		
	
		
		
		HashMap<String, String> mapContent = new HashMap<String, String>();
		mapContent = itemListContent.get(position);

		product.setText(String.valueOf(mapContent.get("product_name")));
		batch.setText(String.valueOf(mapContent.get("batch")));
		quantity.setText(String.valueOf(mapContent.get("qty")));
		exp.setText(String.valueOf(mapContent.get("exp")));



		
		
		final String productID=mapContent.get("product_id");

		action.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {


				itemListContent.remove(position);
				
				//TempData.RerurnProductDetails.remove(position);
				
				
				notifyDataSetChanged();
				
			}
		});

		
		
	
		batch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().length()>0)
				{

				itemListContent.get(position).put("batch", s.toString());
				//notifyDataSetChanged();
				}
		}
		});
		
		quantity.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				/*if (s.toString().length()>0){

					double GivenQuantity=Double.parseDouble(etQuantity.getText().toString());
					 stockmap.put(itemListContent.get(position).get("product_id"), stockmap.get(itemListContent.get(position).get("product_id"))+GivenQuantity);
				}*/

			}
			@Override
			public void afterTextChanged(Editable s) {



						itemListContent.get(position).put("qty", s.toString());

			}
		});
		

		exp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pos=position;
			    final Calendar c = Calendar.getInstance();
		        year1  = c.get(Calendar.YEAR);
		        month1 = c.get(Calendar.MONTH);
		        day1   = c.get(Calendar.DAY_OF_MONTH);

				
				Log.e("Called", "Date Picker!");
				new DatePickerDialog(context, dateListener, year1, month1,
                        day1).show();			
				
			}
		});
		

		
	    	return itemView;
	}
   
	
 DatePickerDialog.OnDateSetListener dateListener =  new DatePickerDialog.OnDateSetListener() {

         @Override
         public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
                 int selectedDay) {
             // TODO Auto-generated method stub
         
	            year1  = selectedYear;
	            month1 = selectedMonth;
	            day1  = selectedDay;
	            String day="",month="", MONTH="";
	            if(day1<10)
	            day="0"+String.valueOf(day1);
	            else
	            day=String.valueOf(day1);	
	            if(month1+1<10)
	            	month="0"+String.valueOf(month1+1);
	            else
	            month=String.valueOf(month1+1);
	            Log.e("day","Day"+ String.valueOf(day));
	            Log.e("month", String.valueOf(month));

	           if(month.equals("01"))
	           {
	        	   MONTH = "Jan";
	           }
	           else if(month.equals("02"))
	           {
	        	   MONTH = "Feb";
	           }
	           else if(month.equals("03"))
	           {
	        	   MONTH = "Mar";
	           }
	           else if(month.equals("04"))
	           {
	        	   MONTH = "Apr";
	           }
	           else if(month.equals("05"))
	           {
	        	   MONTH = "May";
	           }
	           else if(month.equals("06"))
	           {
	        	   MONTH = "Jun";
	           }
	           else if(month.equals("07"))
	           {
	        	   MONTH = "Jul";
	           }
	           else if(month.equals("08"))
	           {
	        	   MONTH = "Aug";
	           }
	           else if(month.equals("09"))
	           {
	        	   MONTH = "Sep";
	           }
	           else if(month.equals("10"))
	           {
	        	   MONTH = "Oct";
	           }
	           else if(month.equals("11"))
	           {
	        	   MONTH = "Nov";
	           }
	           else if(month.equals("12"))
	           {
	        	   MONTH = "Dec";
	           }
	            String date=MONTH+"-"+String.valueOf(year1).substring(2,4);

				itemListContent.get(pos).put("exp", date);
                notifyDataSetChanged();
	            
	   
	          
         }
         
         

  }; 





	

	

}
