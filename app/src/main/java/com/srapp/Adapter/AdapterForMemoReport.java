package com.srapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.srapp.R;
import com.srapp.TempData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.MEMOS_ORDER_NUMBER;
import static com.srapp.Db_Actions.Tables.MEMOS_editable;
import static com.srapp.Db_Actions.Tables.MEMOS_for_memo_delete;
import static com.srapp.Db_Actions.Tables.MEMOS_from_app;
import static com.srapp.Db_Actions.Tables.MEMOS_gross_value;
import static com.srapp.Db_Actions.Tables.MEMOS_is_pushed;
import static com.srapp.Db_Actions.Tables.MEMOS_market_id;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_date;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_date_time;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_number;
import static com.srapp.Db_Actions.Tables.MEMOS_outlet_id;
import static com.srapp.Db_Actions.Tables.ORDER_gross_value;
import static com.srapp.Db_Actions.Tables.OUTLETS_ISNGO;
import static com.srapp.Db_Actions.Tables.OUTLETS_IS_WITHIN_GROUP;
import static com.srapp.Db_Actions.Tables.OUTLETS_NGO_INST_ID;
import static com.srapp.Db_Actions.Tables.OUTLETS_OUTLET_NAME;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MEMOS;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.ORDER_TO_MEMO;

public class AdapterForMemoReport extends BaseAdapter {

	// Declare Variables
	Context context;
	String SO_ID;
	Data_Source db;

	ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
	public AdapterForMemoReport(Context context, ArrayList<HashMap<String, String>> arraylistContent) {
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
		View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order_list, null);

		sl_no_tv = view2.findViewById(R.id.sr_no);
		sale_date_tv = view2.findViewById(R.id.sale_date);
		memo_tv = view2.findViewById(R.id.memo);
		outlet_tv = view2.findViewById(R.id.outlet);
		mamount_tv = view2.findViewById(R.id.m_account);
		ActionBtn = view2.findViewById(R.id.icon);
		ActionBtn.setImageResource(R.drawable.eye_icon);
		sl_no_tv.setText(String.valueOf(position+1));
		
		HashMap<String, String> mapContent = new HashMap<String, String>();
		mapContent = itemListContent.get(position);
		sale_date_tv.setText( mapContent.get(MEMOS_memo_date));
		memo_tv.setText( mapContent.get(MEMOS_memo_number));
		outlet_tv.setText( mapContent.get(OUTLETS_OUTLET_NAME));
//		double amount=roundTwoDecimals(Double.parseDouble(mapContent.get("amount")));

		mamount_tv.setText(mapContent.get(ORDER_gross_value));
		
		LinearLayout txtAction12 = (LinearLayout)view2.findViewById(R.id.txtAction12);
		
		
		

		
		
		
		final String memoNo = mapContent.get(MEMOS_memo_number);
		Log.e("---------orderNo--------",memoNo);
		
		/*TextView txtTotalAmount=(TextView)((Activity) context).findViewById(R.id.TotalAmount);
		TextView TotalEC=(TextView)((Activity) context).findViewById(R.id.TotalEC);
		txtTotalAmount.setText("0.0");
		Double sum=0.0;
		int countEC=0;
		for(int i=0;i<itemListContent.size();i++)
		{
			sum=sum+Double.parseDouble(itemListContent.get(i).get(ORDER_gross_value));
			countEC = countEC+1;
		}
		
//		sum=(Double) roundTwoDecimals(sum);
//		txtTotalAmount.setText(String.valueOf(sum));
		txtTotalAmount.setText(String.format("%.2f", sum));
		TotalEC.setText(String.valueOf(countEC));*/
		
		
		

		
		

		
	
		
		ActionBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Log.e("memo NO test--------",""+memoNo);
				
				String comma = " , ";
				TempData.MarketID="";
				TempData.From_App="";
				TempData.DayCloseMemoEditable="";
				Cursor c =db.rawQuery("SELECT _id,"+ MEMOS_memo_number+comma+MEMOS_is_pushed+comma+MEMOS_outlet_id+comma+MEMOS_memo_date+comma+MEMOS_market_id+comma+MEMOS_from_app+comma+MEMOS_memo_date_time+comma+MEMOS_editable+comma+MEMOS_gross_value+comma+MEMOS_for_memo_delete+comma+MEMOS_ORDER_NUMBER+"  FROM "+TABLE_NAME_MEMOS+" where "+MEMOS_memo_number+"='"+memoNo+"'");
				//Log.e("Querymemo","SELECT _id,"+ MEMOS_memo_number+comma+MEMOS_is_pushed+comma+MEMOS_outlet_id+comma+MEMOS_memo_date+comma+MEMOS_market_id+comma+MEMOS_from_app+comma+MEMOS_memo_date_time+comma+MEMOS_editable+comma+MEMOS_gross_value+comma+MEMOS_for_memo_delete+"  FROM "+TABLE_NAME_MEMOS+" where "+MEMOS_memo_number+"='"+memoNo+"'");
				if(c!=null)
				{
					
					if(c.moveToFirst())
					{do{
						TempData.memoNumber =c.getString(1);
						//TempData.MemoTotalView=c.getString(0);
						TempData.isPushed=c.getString(2);
						TempData.OutletID=c.getString(3);
						TempData.MemoDate=c.getString(4);
						TempData.MarketID=c.getString(5);
						TempData.From_App=c.getString(6);
						TempData.MemoDateTime=c.getString(7);
						//TempData.TeritoryID = c.getString(8);
						TempData.DayCloseMemoEditable = c.getString(8);
						TempData.gross_value = c.getString(9);
						TempData.for_memo_delete = c.getString(10);
						TempData.ORDER_STATUE = 1;
						TempData.orderNumber=c.getString(11);

						Log.e("ORDER_STATUE", " "+TempData.orderNumber);


					}while(c.moveToNext());
					}
				}

				Cursor c3 = db.rawQuery("SELECT * FROM outlets  WHERE outlet_id='"+TempData.OutletID+"'");
				  if (c3 != null) {
						if (c3.moveToFirst()) {
							do {
								
								

								String institute_id = c3.getString(c3.getColumnIndex(OUTLETS_NGO_INST_ID));
								String project_id = c3.getString(c3.getColumnIndex(OUTLETS_ISNGO));
								String is_withinGroup = c3.getString(c3.getColumnIndex(OUTLETS_IS_WITHIN_GROUP));

								TempData.TargetCustomer = project_id;
								TempData.InstituteID = institute_id;
								savePreference("Is_WithinGroup", is_withinGroup);

								Log.e("------------","--  is_withinGroup  -- "+is_withinGroup);
								Log.e("------------","--  Is_WithinGroup  -- "+getPreference("Is_WithinGroup"));
								
							} while (c3.moveToNext());
						}
				  }
						
				
				TempData.OutletName =outlet_tv.getText().toString();
				Intent in=new Intent(context, DetailsOrderReport.class);
				in.putExtra("memo",5);
				ORDER_TO_MEMO = 0;
				MEMO_EDIT =true;
				context.startActivity(in);

			}
		});
		
		return view2;
	}

		

	 double roundTwoDecimals(double d)
	 {
	     DecimalFormat twoDForm = new DecimalFormat("#.######");
	     return Double.valueOf(twoDForm.format(d));
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
