package com.srapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForBonusPartyReport extends BaseAdapter {

	// Declare Variables
	Context context;
	String SO_ID;

	ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
	public AdapterForBonusPartyReport(Context context, ArrayList<HashMap<String, String>> arraylistContent) {
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
	
		
		View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.bonus_row, null);
		TextView txtOutlet = (TextView)view2.findViewById(R.id.txtOutlet);
		TextView txtProduct = (TextView)view2.findViewById(R.id.txtProduct);
		TextView txtTarget = (TextView)view2.findViewById(R.id.txtTarget);
		TextView txtAchieve = (TextView)view2.findViewById(R.id.txtAchieve);
		TextView txtStamp = (TextView)view2.findViewById(R.id.txtStamp);
		
		
		HashMap<String, String> mapContent = new HashMap<String, String>();
		mapContent = itemListContent.get(position);
		txtOutlet.setText( mapContent.get("outlet_name"));
		txtProduct.setText( mapContent.get("product_name"));
		
		  Log.e("-------- target XXXXXXXXXXXXX", ""+mapContent.get("target"));
		  
		txtTarget.setText( mapContent.get("target"));
		txtAchieve.setText( mapContent.get("achieve"));
		txtStamp.setText( mapContent.get("stamp"));


		return view2;
	}

		

   


	
	

	

}
