package com.srapp.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForOrderDetailsShedule extends BaseAdapter {

    // Declare Variables
    Activity context;

    ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
    public AdapterForOrderDetailsShedule(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
        this.context = context;
        maplist = arraylistContent;
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


        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_details_row, null);
        TextView product_name = (TextView)view2.findViewById(R.id.product_name);
        TextView od = (TextView)view2.findViewById(R.id.od);
        TextView id = (TextView)view2.findViewById(R.id.id);
        TextView status = (TextView)view2.findViewById(R.id.status);
        product_name.setText(maplist.get(position).get("product_name"));
        od.setText(maplist.get(position).get("order_qty"));
        id.setText(maplist.get(position).get("invoice_qty"));

        if (maplist.get(position).get("status").equalsIgnoreCase("1")){
            status.setText("Success");

        }else {
            status.setText("Failed");
        }

        return view2;
    }








}
