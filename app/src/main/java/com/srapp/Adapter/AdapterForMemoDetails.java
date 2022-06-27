package com.srapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.srapp.R;
import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForMemoDetails extends BaseAdapter {

	// Declare Variables
	Context context;
	String SO_ID;

	ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
	public AdapterForMemoDetails(Context context, ArrayList<HashMap<String, String>> arraylistContent) {
		this.context = context;
		itemListContent = arraylistContent;
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

	
	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
	  
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_order_item, null);
		TextView product_name_tv,price_tv,quantity_tv, total_tv;

		product_name_tv = itemView.findViewById(R.id.product_name);
		price_tv = itemView.findViewById(R.id.price);
		quantity_tv = itemView.findViewById(R.id.quantity);
		total_tv = itemView.findViewById(R.id.total_price);
		
		HashMap<String, String> mapContent = new HashMap<String, String>();
		mapContent = itemListContent.get(position);
		product_name_tv.setText( mapContent.get("product_name"));
		price_tv.setText( mapContent.get("price"));
		quantity_tv.setText( mapContent.get("quantity"));
		total_tv.setText( mapContent.get("total_price"));

		
	
		return itemView;
	}

		

   


	
	

	

}
