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

public class AdapterForAttendanceHistory extends BaseAdapter {

    // Declare Variables
    Activity context;


    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForAttendanceHistory(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
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
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_history_row, null);
        TextView date = (TextView)view2.findViewById(R.id.date);
        TextView check_in_time = (TextView)view2.findViewById(R.id.check_in_time);
        TextView check_out_time = (TextView)view2.findViewById(R.id.check_out_time);

        date.setText(mapContent.get("date"));
        check_in_time.setText(mapContent.get("check_in_time"));
        check_out_time.setText(mapContent.get("check_out_time"));



        return view2;
    }








}
