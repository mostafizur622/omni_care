package com.srapp.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.srapp.R;
import com.srapp.Util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForPendingOutlet extends BaseAdapter {

    // Declare Variables
    Activity context;


    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForPendingOutlet(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
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


        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_outlet_row, null);
        TextView outlet_name = (TextView)view2.findViewById(R.id.outlet_name);
        TextView market = (TextView)view2.findViewById(R.id.market_name);
        TextView status = (TextView)view2.findViewById(R.id.status);

        HashMap<String, String> mapContent = new HashMap<String, String>();
        mapContent = BonusItemList.get(position);
        Log.e("","bonus_name"+mapContent.get("bonus_name"));
      //  sl.setText(mapContent.get("sl"));
        outlet_name.setText(mapContent.get("outlet_name"));
        market.setText(mapContent.get("market_name"));

        if (Integer.parseInt(mapContent.get("status"))== Constants.SUCCESS) {
            status.setText("Accepted");
        }else if (Integer.parseInt(mapContent.get("status"))== Constants.PENDING) {
            status.setText("Pending");
        }else if (Integer.parseInt(mapContent.get("status"))== Constants.REJECT) {
            status.setText("Rejected");
        }


        return view2;
    }








}
