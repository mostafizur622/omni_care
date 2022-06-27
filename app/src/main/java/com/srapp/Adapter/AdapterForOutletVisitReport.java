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

import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;

public class AdapterForOutletVisitReport extends BaseAdapter {

    // Declare Variables
    Activity context;
    int tec,toc;
    double tqty;

    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForOutletVisitReport(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
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
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.outletvisitreportrow, null);
        TextView route_name = (TextView)view2.findViewById(R.id.route_name);
        TextView total_outlet = (TextView)view2.findViewById(R.id.total_outlet);
        TextView total_visited = (TextView)view2.findViewById(R.id.total_visited);
        TextView total_ec = (TextView)view2.findViewById(R.id.total_ec);

        route_name.setText(mapContent.get("route"));

        total_outlet.setText(mapContent.get("total_outlet"));
        total_visited.setText(mapContent.get("total_visited"));
        total_ec.setText(mapContent.get("total_ec"));



        return view2;
    }








}
