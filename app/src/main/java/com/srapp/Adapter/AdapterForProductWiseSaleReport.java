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

import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;

public class AdapterForProductWiseSaleReport extends BaseAdapter {

    // Declare Variables
    Activity context;

    Double sum=0d;
    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForProductWiseSaleReport(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
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
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_wise_sales_report, null);
        TextView product_name = (TextView)view2.findViewById(R.id.product_name);
        TextView EC = (TextView)view2.findViewById(R.id.ec);
        TextView total_price = ((Activity) context).findViewById(R.id.total_price);
        TextView sale_quntity = (TextView)view2.findViewById(R.id.sale_quntity);
        TextView salevalue = (TextView)view2.findViewById(R.id.salevalue);

        product_name.setText(mapContent.get("dist_order_no"));
        EC.setText(mapContent.get("sales_qty"));
        sale_quntity.setText(mapContent.get("order_date"));
        salevalue.setText(mapContent.get("outlet_name"));










        return view2;
    }








}
