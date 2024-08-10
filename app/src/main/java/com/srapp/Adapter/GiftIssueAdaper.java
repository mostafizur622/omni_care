package com.srapp.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.URL;
import com.srapp.GiftIssueList;
import com.srapp.R;
import com.srapp.SyncActivity;
import com.srapp.TempData;
import com.srapp.Util.AppManager;
import com.srapp.Util.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiftIssueAdaper extends BaseAdapter implements BasicFunctionListener {

	// Declare Variables
	Context context;
	public static SQLiteDatabase myDB;
	String SO_ID;
	String OutletID;


	public  ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
	ArrayList<String> SELECTED_POS=new ArrayList<String>();
    Data_Source db;
	Boolean validInput;
	BasicFunction bf;
	int state;

	public GiftIssueAdaper(Context context, String _SO_ID, String _OutletID, int state,ArrayList<HashMap<String, String>> itemListContent) {
		this.context = context;
		db=new Data_Source(context);
		SO_ID = _SO_ID;
		OutletID = _OutletID;
		this.state = state;
		bf = new BasicFunction(this,context);
		this.itemListContent = itemListContent;


	}

	@Override
	public int getCount() {
		if (state==0) {
			for (int i = 0; i < itemListContent.size(); i++) {
				Log.e("itemListContent0",itemListContent.get(i).toString());
				if (itemListContent.get(i).get("quantity").equalsIgnoreCase("") || itemListContent.get(i).get("boolean").equalsIgnoreCase("false")) {
					itemListContent.remove(i);
				}
			}


		}
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




		// Declare Variables
		//		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_row, null);
		View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_issue_row, null);

		TextView NameTv = (TextView)view2.findViewById(R.id.ProductNametxt);
		final EditText etQuantity = (EditText)view2.findViewById(R.id.etQuantity);
		final CheckBox chbox = (CheckBox)view2.findViewById(R.id.checkBox1);

		Button SaveButton = (Button)((Activity) context).findViewById(R.id.SaveButton);
		final EditText remarks = (EditText) ((Activity) context).findViewById(R.id.remarks);
		HashMap<String, String> mapContent = new HashMap<String, String>();
		mapContent = itemListContent.get(position);


		//		String product_id= mapContent.get("product_id");
		String product_name= mapContent.get("product_name");
		final String quantity= mapContent.get("quantity");
		final String _product_id = mapContent.get("product_id");
		Boolean b = Boolean.valueOf(mapContent.get("boolean"));

		chbox.setChecked(b);
		if (state==0){
			chbox.setEnabled(false);
			etQuantity.setEnabled(false);
			remarks.setEnabled(false);
		}

		if(mapContent.get("boolean").equals("true"))
		{

			etQuantity.setText(mapContent.get("quantity"));
			//bf.savePreference("gift"+itemListContent.get(position).get("product_id"),etQuantity.getText().toString());

	     }


		NameTv.setText(product_name);


		chbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if(isChecked)
				{
					//validInput = true;
					AppManager.setOpenKeyBoard(context);
					etQuantity.setVisibility(View.VISIBLE);
					etQuantity.setFocusable(true);
					etQuantity.requestFocus();

					if(getArrayListIndex(""+position)<0)
						SELECTED_POS.add(""+position);
					itemListContent.get(position).put("boolean", "true");

					etQuantity.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
						}

						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
													  int after) {
						}

						@Override
						public void afterTextChanged(Editable s) {

							if(s.toString().length()>0)
							{
								itemListContent.get(position).put("quantity", etQuantity.getText().toString());
								//bf.savePreference("gift"+itemListContent.get(position).get("product_id"),etQuantity.getText().toString());
								Log.e("afterTextChanged", "~~~~~~~~~~~~~~s.toString()~~~~~~~~~~~~"+etQuantity.getText().toString());
							}
						}
					} );

				}
				else
				{
					//validInput = false;
					if(getArrayListIndex(""+position)>=0)
						SELECTED_POS.remove(""+position);
					itemListContent.get(position).put("boolean", "false");
					AppManager.setCloseKeyBoard(context, etQuantity);
					etQuantity.setVisibility(View.INVISIBLE);
					itemListContent.get(position).put("quantity", "");

				}

			}
		});



		SaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(!TempData.OutletID.equalsIgnoreCase("0")) {

					if (hasSelect()) {
							DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
							Calendar cal = Calendar.getInstance();


							String tempId = "GIFT_" + getPreference("SO") + dateFormat.format(cal.getTime());

							/*int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
							if (getPreference("PharmaType").equalsIgnoreCase("1"))
								db.excQuery("UPDATE so_targets SET achieve_pharma_oc=achieve_pharma_oc+1 WHERE month_id=" + currentMonth + " AND territory_id=" + getPreference("Territory_Id") + " AND so_id=" + getPreference("SO"));
							else
								db.excQuery("UPDATE so_targets SET achieve_non_pharma_oc=achieve_non_pharma_oc+1 WHERE month_id=" + currentMonth + " AND territory_id=" + getPreference("Territory_Id") + " AND so_id=" + getPreference("SO"));*/


							String giftID = getPreference("SO") + dateFormat.format(cal.getTime());

                        JSONObject finalJsonobj = new JSONObject();
                        JSONArray giftissuearray = new JSONArray();




							JSONObject map = new JSONObject();
                        try {
                            map.put("so_id", getPreference(SR_ID));
							if (state==1){
								map.put("temp_id", bf.getPreference("gift_id"));
							}else
                            map.put("temp_id", tempId);
                            map.put("outlet_id", TempData.OutletID);
                            if (state==1){
								map.put("gift_issue_id", bf.getPreference("gift_id"));
							}else
                            map.put("gift_issue_id", "" + giftID);
                            map.put("gift_issued_by", getPreference(SR_ID));
                            map.put("remarks", remarks.getText().toString().trim());
							if (state==1){
								map.put("gift_issue_date", bf.getPreference("gift_date"));
							}else
                            map.put("gift_issue_date", ParentActivity.getCurrentDate());
                            map.put("mac",bf.getPreference("mac"));





							for (int i = 0; i < itemListContent.size(); i++) {

								Log.e("~~~~~~~~~~~~~~~~~~", "~~~~~~~~~~~~~~itemListContent~~~~~~~~~~~~" + itemListContent.toString());

								if (!itemListContent.get(i).get("quantity").equalsIgnoreCase("") && !itemListContent.get(i).get("quantity").equalsIgnoreCase("0.0")) {
									JSONObject map1 = new JSONObject();



									map1.put("gift_issue_id", "" + giftID);
									map1.put("product_id", itemListContent.get(i).get("product_id"));
									map1.put("quantity", itemListContent.get(i).get("quantity"));
                                    giftissuearray.put(map1);
								}
							}

							finalJsonobj.put("gift_issue",map);
							finalJsonobj.put("gift_issue_details",giftissuearray);
							finalJsonobj.put("mac",bf.getPreference("mac"));


                           // bf.getResponceData(URL.GIFT_ISSUE,finalJsonobj.toString(),11);

							ProgressDialog dailog = CheckConnection(context,"Gift Issue Loading...");
							if (dailog==null)
								return;
							getJAPi().GIFT_ISSUE(convertTORequestdata(finalJsonobj)).enqueue(new Callback<String>() {
								@Override
								public void onResponse(Call<String> call, Response<String> response) {
									try {
										JSONObject jsonObject = new JSONObject(response.body());
										dailog.dismiss();

										String msg = " ";
										int status= 0 ;
										try {

											status = jsonObject.getJSONObject("giftitem_received").getInt("status");
											if (status==1)
												msg = jsonObject.getJSONObject("giftitem_received").getString("message");
											else {
												msg = jsonObject.getJSONObject("giftitem_received").getJSONArray("replaced_relation").getJSONObject(0).getString("messege");
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}


										Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
										if (status!=2) {
											Intent idn = new Intent(context, GiftIssueList.class);
											((Activity) context).finish();
											context.startActivity(idn);
										}
									} catch (JSONException e) {
										throw new RuntimeException(e);
									}
								}

								@Override
								public void onFailure(Call<String> call, Throwable t) {
									dailog.dismiss();
								}
							});

							/*TempData.OutletID = "";

							((Activity) context).finish();*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
						} else
							Toast.makeText(context, "Select gift item!", Toast.LENGTH_LONG).show();
				}else
						Toast.makeText(context, "Select Outlet!", Toast.LENGTH_LONG).show();
			  }
		});



		etQuantity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				Log.e("Length:", "......................"+s.length());
				if(s.length()<=0)
				{
					Log.e("b_product_quantity:", ".........."+getPreference(_product_id));
					savePreference(_product_id," ");
					//validInput = false;
					Log.e("a_product_quantity:", "..........."+getPreference(_product_id));
				}

				Log.e("Quantity_Length:", ""+s.length());

				if(s.length()>0)
				{
					//validInput = true;
					if(isInteger(s.toString())) {
						itemListContent.get(position).put("quantity", etQuantity.getText().toString());

                    }
					else
					{
						Toast.makeText(context, "Please enter correct number format!", Toast.LENGTH_SHORT).show();
						etQuantity.setText("");
					}
				}
			}
		});

		if(chbox.isChecked())
		{
			Log.e("checkboxcheck", String.valueOf(chbox.isChecked()));
			etQuantity.setVisibility(View.VISIBLE);
			//db.excQuery("UPDATE product_boolean_table SET quantity ="+"'"+QuantityEd.getText().toString()+"'" +", boolean = "+"'"+chbox.isChecked()+"'"+" WHERE product_id = "+"'"+_product_id+"'"+"and so_id="+"'"+SO_ID+"'"+" and outlet_id="+"'"+OutletID+"'");
		}
		return view2;
	}

	private boolean CheckValidation() {

		Log.e("itemlistcontent", String.valueOf(itemListContent.size()));
		Boolean reBool=true;
		for(int x=0;x<itemListContent.size();x++){
			Log.e("itemlist",itemListContent.get(x).get("quantity"));
		}
		return reBool;

	}

	public int getArrayListIndex(String value)
	{
		return SELECTED_POS.indexOf(value);
	}


	public Boolean hasSelect()
	{
		Boolean retBoll=false;
		for(int i=0;i<itemListContent.size();i++)
		{
			if(itemListContent.get(i).get("quantity").length()>0)
			{
				double qty =Double.parseDouble(itemListContent.get(i).get("quantity"));
				Log.e("qty", String.valueOf(qty));
				if(qty>0)
				{
					retBoll=true;
				}
			}
		}
		return retBoll;
	}



	/*public boolean isDouble(String value) {
	    try {
	        Double.parseDouble(value);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}*/

	public boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	//	int getIndex(String value){
//		  int pos = -1;
//		  for(int i=0;i<ProductQuantity.size();i++)
//		  {
//			  HashMap<String,String> map=ProductQuantity.get(i);
//			  Log.e("PRODUCT_ID", map.get("product_id"));
//			  if(map.get("product_id").equalsIgnoreCase(value))
//				  pos=i;
//		  }
//
//		return pos;
//		}
	public String getPreference(String key)
	{
		String value="";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		value = prefs.getString(key, "0");

		return value;

	}

	public void savePreference(String key, String value)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();

	}

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {


    }

    @Override
    public void OnConnetivityError() {

    }
}
