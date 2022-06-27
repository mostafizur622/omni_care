package com.srapp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForOutletWiseSalesReport extends BaseAdapter {

    // Declare Variables
    Activity context;
    int tec,toc;
    double tqty;
    ArrayList< ArrayList<HashMap<String, String>>> details;
    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForOutletWiseSalesReport(Activity context, ArrayList<HashMap<String, String>> arraylistContent, ArrayList< ArrayList<HashMap<String, String>>> details) {
        this.context = context;
        BonusItemList = arraylistContent;
        this.details = details;

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
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.outlet_wise_summery_row, null);
        TextView mmeo_no = (TextView)view2.findViewById(R.id.mmeo_no);
        TextView date = (TextView)view2.findViewById(R.id.date);
        TextView total = (TextView)view2.findViewById(R.id.total);
        ListView sublist = (ListView)view2.findViewById(R.id.sublist);

        mmeo_no.setText(mapContent.get("memo_no"));
        date.setText(mapContent.get("memo_date"));
        total.setText(mapContent.get("total"));

        AdapterForOutletWiseSalesReportDetails adapter = new AdapterForOutletWiseSalesReportDetails(context,details.get(position));

        sublist.setAdapter(adapter);



        return view2;
    }








}
