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

		TextView product;
		TextView batch, quantity;
		TextView outlet;
		ImageView action;
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ncp_collection, null);
		product = itemView.findViewById(R.id.product_name);
		batch = itemView.findViewById(R.id.batch);
		outlet = itemView.findViewById(R.id.outlet);
		quantity = itemView.findViewById(R.id.qty);
		action = itemView.findViewById(R.id.edit);

	
		
	
		
		
		HashMap<String, String> mapContent = new HashMap<String, String>();
		mapContent = itemListContent.get(position);

		product.setText(String.valueOf(mapContent.get("product_name")));
		batch.setText(String.valueOf(mapContent.get("batch")));
		quantity.setText(String.valueOf(mapContent.get("qty")));
		outlet.setText(String.valueOf(mapContent.get("exp")));

		HashMap<String, String> finalMapContent = mapContent;
		action.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(context, NcpCollection.class);
				i.putExtra("map", finalMapContent);
				i.putExtra("is_edit", true);
				context.startActivity(i);


			}
		});



		
		

		
	    	return itemView;
	}
   






	

	

}
