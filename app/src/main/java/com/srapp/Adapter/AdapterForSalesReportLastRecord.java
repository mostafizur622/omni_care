package com.srapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.DetailsOrderReport;
import com.srapp.LastOrderDetails;
import com.srapp.R;
import com.srapp.TempData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.ORDER_STATUS;
import static com.srapp.Db_Actions.Tables.ORDER_editable;
import static com.srapp.Db_Actions.Tables.ORDER_for_order_delete;
import static com.srapp.Db_Actions.Tables.ORDER_from_app;
import static com.srapp.Db_Actions.Tables.ORDER_gross_value;
import static com.srapp.Db_Actions.Tables.ORDER_is_pushed;
import static com.srapp.Db_Actions.Tables.ORDER_market_id;
import static com.srapp.Db_Actions.Tables.ORDER_order_date;
import static com.srapp.Db_Actions.Tables.ORDER_order_date_time;
import static com.srapp.Db_Actions.Tables.ORDER_order_number;
import static com.srapp.Db_Actions.Tables.ORDER_outlet_id;
import static com.srapp.Db_Actions.Tables.OUTLETS_ISNGO;
import static com.srapp.Db_Actions.Tables.OUTLETS_IS_WITHIN_GROUP;
import static com.srapp.Db_Actions.Tables.OUTLETS_NGO_INST_ID;
import static com.srapp.Db_Actions.Tables.OUTLETS_OUTLET_NAME;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.ORDER_TO_MEMO;

public class AdapterForSalesReportLastRecord extends BaseAdapter {

	// Declare Variables
	Context context;
	String SO_ID;
	Data_Source db;

	ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
	public AdapterForSalesReportLastRecord(Context context, ArrayList<HashMap<String, String>> arraylistContent) {
		this.context = context;
		itemListContent = arraylistContent;
		db=new Data_Source(context);
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
		final TextView sl_no_tv,sale_date_tv,memo_tv, outlet_tv,mamount_tv;
		ImageView ActionBtn;
		View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order_list_last_record, null);
		LinearLayout linearLayout = view2.findViewById(R.id.linear_layout);
		sl_no_tv = view2.findViewById(R.id.sr_no);
		sale_date_tv = view2.findViewById(R.id.sale_date);
		memo_tv = view2.findViewById(R.id.memo);
		//outlet_tv = view2.findViewById(R.id.outlet);
		mamount_tv = view2.findViewById(R.id.m_account);
		ActionBtn = view2.findViewById(R.id.icon);
		ActionBtn.setImageResource(R.drawable.eye_icon);
		sl_no_tv.setText(String.valueOf(position+1));
		
		HashMap<String, String> mapContent = new HashMap<String, String>();
		mapContent = itemListContent.get(position);
		sale_date_tv.setText( mapContent.get(Tables.ORDER_order_date));
		memo_tv.setText( mapContent.get(ORDER_order_number));
		mamount_tv.setText(mapContent.get(ORDER_gross_value));

		if (mapContent.get("is_memo").equalsIgnoreCase("1")) {
			linearLayout.setBackgroundColor(Color.parseColor("#B8E3FA"));
		}

		final HashMap<String, String> finalMapContent = mapContent;
		ActionBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				MEMO_EDIT=false;
				if (finalMapContent.get("is_memo").equalsIgnoreCase("1")) {

					MEMO_EDIT=true;
					TempData.memoNumber =  finalMapContent.get(ORDER_order_number);
				}
				Intent i = new Intent(context, LastOrderDetails.class);
				TempData.orderNumber = finalMapContent.get(ORDER_order_number);
				context.startActivity(i);
			}
		});
		
		return view2;
	}

		




	public String getPreference(String key)
	{
		String value="";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		value = prefs.getString(key, "NO PREFERENCE");

		return value;

	}


	public void savePreference(String key, String value)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}


}
