package com.srapp.Adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.NCP_replacemnt;
import com.srapp.NcpCollection;
import com.srapp.NcpProductCollection;
import com.srapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class NcpCollectionListAdapter extends BaseAdapter {

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

	public NcpCollectionListAdapter(Context context, ArrayList<HashMap<String, String>> arraylistContent) {
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

		TextView date;
		TextView outlet;
		ImageView action;
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ncp_collection, null);
		date = itemView.findViewById(R.id.date);
		outlet = itemView.findViewById(R.id.outlet);
		action = itemView.findViewById(R.id.edit);

	
		
	
		
		
		HashMap<String, String> mapContent = new HashMap<String, String>();
		mapContent = itemListContent.get(position);

		date.setText(String.valueOf(mapContent.get("collection_date")));

		outlet.setText(String.valueOf(mapContent.get("outlet_name")));

		HashMap<String, String> finalMapContent = mapContent;


		if (finalMapContent.get("status").equalsIgnoreCase("1")) {

			action.setVisibility(View.VISIBLE);
		}else if (finalMapContent.get("status").equalsIgnoreCase("6")) {

			action.setVisibility(View.VISIBLE);
			action.setImageResource(R.drawable.process);
		}

		action.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.e("status",finalMapContent.get("status"));


					Intent i = new Intent(context, NcpCollection.class);
					i.putExtra("map", finalMapContent);
					i.putExtra("is_edit", true);
					context.startActivity(i);









			}
		});



		
		

		
	    	return itemView;
	}
   






	

	

}
