package com.srapp.Adapter;

import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tanvir3488 on 5/16/2023.
 */

public class AdapterForOutletWiseProductReport extends BaseAdapter {

    // Declare Variables
    Activity context;
    int tec,toc;
    double tqty;

    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForOutletWiseProductReport(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
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

       /* TextView toctxt = context.findViewById(R.id.toc);
        TextView tectxt = context.findViewById(R.id.tec);
        TextView tqtytxt = context.findViewById(R.id.tqty);*/
        HashMap<String, String> mapContent = new HashMap<String, String>();
        mapContent = BonusItemList.get(position);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.outlet_wise_product_report, null);
        TextView product_name = (TextView)view2.findViewById(R.id.product_name);
        TextView EC = (TextView)view2.findViewById(R.id.ec);
        TextView OC = (TextView)view2.findViewById(R.id.oc);
        TextView sale_quntity = (TextView)view2.findViewById(R.id.qty);

        product_name.setText(mapContent.get(PRODUCT_PRODUCT_NAME));

        EC.setText(mapContent.get("sales_qty"));
        OC.setText(mapContent.get("bonus_qty"));
        sale_quntity.setText(mapContent.get("total_value"));


      /*  tectxt.setText(tec+"");
        toctxt.setText(toc+"");
        tqtytxt.setText(tqty+"");*/

        return view2;
    }








}
