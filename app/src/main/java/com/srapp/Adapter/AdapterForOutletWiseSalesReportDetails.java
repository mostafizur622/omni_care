package com.srapp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForOutletWiseSalesReportDetails extends BaseAdapter {

    // Declare Variables
    Activity context;
    int tec,toc;
    double tqty;

    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForOutletWiseSalesReportDetails(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
        this.context = context;
        BonusItemList = arraylistContent;


    }

    @Override
    public int getCount() {
        return BonusItemList.size();
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

        HashMap<String, String> mapContent = new HashMap<String, String>();
        mapContent = BonusItemList.get(position);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.sublist_details_row, null);
        TextView product_name = (TextView)view2.findViewById(R.id.product_name);
        TextView sqty = (TextView)view2.findViewById(R.id.sqty);
        TextView bqty = (TextView)view2.findViewById(R.id.bqty);
        TextView value = (TextView)view2.findViewById(R.id.value);

        product_name.setText(mapContent.get("product_name"));
        sqty.setText(mapContent.get("sales_qty"));
        bqty.setText(mapContent.get("bonus_qty"));
        value.setText(mapContent.get("value"));



        return view2;
    }








}
