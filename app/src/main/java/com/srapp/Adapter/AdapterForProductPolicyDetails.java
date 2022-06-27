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

public class AdapterForProductPolicyDetails extends BaseAdapter {

    // Declare Variables
    Activity context;
    int tec,toc;
    double tqty;

    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForProductPolicyDetails(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
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
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.policy_details_row, null);
        TextView bonus_product = (TextView)view2.findViewById(R.id.bonus_product);
        TextView rqty = (TextView)view2.findViewById(R.id.rqty);
        TextView bqty = (TextView)view2.findViewById(R.id.bqty);
        TextView condition = (TextView)view2.findViewById(R.id.condition);
        TextView price = (TextView)view2.findViewById(R.id.price);
        TextView discount = (TextView)view2.findViewById(R.id.discount);

        bonus_product.setText(mapContent.get(PRODUCT_PRODUCT_NAME));

        rqty.setText(mapContent.get("rqty"));
        condition.setText(mapContent.get("condition"));
        bqty.setText(mapContent.get("bqty"));
        price.setText(mapContent.get("price"));
        discount.setText(mapContent.get("discount"));

        return view2;
    }








}


