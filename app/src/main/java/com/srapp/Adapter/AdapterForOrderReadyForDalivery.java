package com.srapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.DeliveryReport;
import com.srapp.DetailsOrderReport;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.print.PrintAllMemosActivityEn;
import com.srapp.print.PrintSelectedOrdersActivityEN;

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
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER;
import static com.srapp.TempData.DELIVERY_EDIT;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.ORDER_TO_MEMO;

public class AdapterForOrderReadyForDalivery extends BaseAdapter {


    Activity context;
    ArrayList<String> list = new ArrayList<>();
    Data_Source db;
    Button printBtn;
    ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
    String start_date,end_date;
    public AdapterForOrderReadyForDalivery(Activity context, ArrayList<HashMap<String, String>> arraylistContent,String start_date,String end_date) {
        this.context = context;
        maplist = arraylistContent;
        db = new Data_Source(context);
        this.start_date=start_date;
        this.end_date=end_date;
    }

    @Override
    public int getCount() {
        return maplist.size();
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
        final HashMap<String,String> map = maplist.get(position);

        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_ready_for_delivery_row, null);
        TextView sirial = view2.findViewById(R.id.sirial);
        TextView date = view2.findViewById(R.id.date);
        TextView outlet = view2.findViewById(R.id.outlet);
        TextView amount = view2.findViewById(R.id.amount);
        Button status = view2.findViewById(R.id.status);

        sirial.setText(String.valueOf(position+1));
        date.setText(map.get(Tables.ORDER_order_date));
        outlet.setText(map.get(Tables.OUTLETS_OUTLET_NAME));
        amount.setText(map.get(Tables.ORDER_gross_value));
        //Start_setOrderPrintCheckItem
        CheckBox checkBox1 = (CheckBox) view2.findViewById(R.id.checkbox);

        checkBox1.setChecked(isInList(map.get(ORDER_order_number)));

        final ArrayList<HashMap<String, String>> finalMapContent = maplist;

        checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {

            Log.e("isChecked", isChecked + "");

            //linear_saveimage.setVisibility(View.VISIBLE);

            if (isChecked) {

                list.add(finalMapContent.get(position).get(ORDER_order_number));
                Log.e("saveOrderNumber",finalMapContent.get(position).get(ORDER_order_number));


            } else {
                list.remove(finalMapContent.get(position).get(ORDER_order_number));

            }

        });
        printBtn = (Button)((Activity) context).findViewById(R.id.print);
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size() == 0){
                    Intent idn = new Intent(context, PrintAllMemosActivityEn.class);
                    idn.putExtra("FromDate",start_date);
                    idn.putExtra("ToDate",end_date);
                    context.startActivity(idn);
                    context.finish();
                }else {
                    Intent idn = new Intent(context, PrintSelectedOrdersActivityEN.class);
                    idn.putExtra("FromDate", list);
                    context.startActivity(idn);
                    context.finish();
                }


            }
        });

        //End_setOrderPrintCheckItem
        if (map.get(Tables.ORDER_STATUS).equalsIgnoreCase("1")) {
            status.setText("Pending");
            status.setBackgroundResource(R.drawable.red_button);
        }
        else if (map.get(Tables.ORDER_STATUS).equalsIgnoreCase("4")) {
            status.setText("Bounce");
            status.setBackgroundResource(R.drawable.bounce_button);
        }
        else {
            status.setText("Complete");
            status.setBackgroundResource(R.drawable.green_button);
        }
        final String orderNo = map.get(ORDER_order_number);
        Log.e("---------orderNo--------",orderNo);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("memo NO test--------",""+orderNo);

                String comma = " , ";
                TempData.MarketID="";
                TempData.From_App="";
                TempData.DayCloseMemoEditable="";
                Cursor c =db.rawQueryCoustom("SELECT _id,"+ ORDER_order_number+comma+ORDER_is_pushed+comma+ORDER_outlet_id+comma+ORDER_order_date+comma+ORDER_market_id+comma+ORDER_from_app+comma+ORDER_order_date_time+comma+ORDER_editable+comma+ORDER_gross_value+comma+ORDER_for_order_delete+comma+ORDER_STATUS+"  FROM "+TABLE_NAME_ORDER+" where "+ORDER_order_number+"='"+orderNo+"'");
                if(c!=null)
                {

                    if(c.moveToFirst())
                    {do{
                        TempData.orderNumber =c.getString(1);
                        TempData.isPushed=c.getString(2);
                        TempData.OutletID=c.getString(3);
                        TempData.MemoDate=c.getString(4);
                        TempData.MemoAndOrderDate=c.getString(4);
                        TempData.MarketID=c.getString(5);
                        TempData.From_App=c.getString(6);
                        TempData.MemoDateTime=c.getString(7);
                        TempData.DayCloseMemoEditable = c.getString(8);
                        TempData.gross_value = c.getString(9);
                        TempData.for_memo_delete = c.getString(10);
                        TempData.ORDER_STATUE = c.getInt(11);

                        Log.e("----ORDER_STATUE----", "~~~~~~"+TempData.ORDER_STATUE);


                    }while(c.moveToNext());
                    }
                }

                Cursor c3 = db.rawQueryCoustom("SELECT * FROM outlets  WHERE outlet_id='"+TempData.OutletID+"'");
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


                TempData.OutletName =map.get(Tables.OUTLETS_OUTLET_NAME);
                Intent in=new Intent(context, DetailsOrderReport.class);
                ORDER_TO_MEMO = 1;
                MEMO_EDIT =false;
                DELIVERY_EDIT = true;
                in.putExtra("memo",1);
                context.startActivity(in);


            }
        });

        return view2;
    }

    boolean isInList(String orderNO) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(orderNO)) {
                return true;
            }
        }

        return false;
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
