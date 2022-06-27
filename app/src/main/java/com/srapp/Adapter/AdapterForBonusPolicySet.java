package com.srapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.R;
import com.srapp.policy_set;
import com.srapp.policy_set_for_dis_or_bonus;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForBonusPolicySet extends BaseAdapter {

    Activity context;
    Data_Source db;
    ArrayList<HashMap<String, String>> BonusItemList;

    public AdapterForBonusPolicySet(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
        db = new Data_Source(context);
        this.context = context;
        BonusItemList = arraylistContent;
    }

    @Override
    public int getCount() {
        return BonusItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row_policy_set, null);
        TextView txtName = (TextView) view2.findViewById(R.id.policy_tag);

        view2.setOnClickListener(v -> {
            if(BonusItemList.get(position).get("policy_type").equals("3")){
                Intent intent = new Intent(context, policy_set_for_dis_or_bonus.class);
                intent.putExtra("policy_type", BonusItemList.get(position).get("policy_type"));
                intent.putExtra("policy_id", BonusItemList.get(position).get("policy_id"));
                intent.putExtra("option_id", BonusItemList.get(position).get("option_id"));
                intent.putExtra("policy_name", BonusItemList.get(position).get("policy_name"));
                intent.putExtra("disPosition", position);
                intent.putExtra("disSelection", BonusItemList.get(position).get("disSelection"));

                if (BonusItemList.get(position).get("min_qty") != null) {
                    intent.putExtra("min_qty", BonusItemList.get(position).get("min_qty"));
                }

                if (BonusItemList.get(position).get("combinedQty") != null) {
                    intent.putExtra("combinedQty", BonusItemList.get(position).get("combinedQty"));
                }

                if (BonusItemList.get(position).get("formula") != null) {
                    intent.putExtra("formula", BonusItemList.get(position).get("formula"));
                    intent.putExtra("setSelection", BonusItemList.get(position).get("setSelection"));
                    intent.putExtra("setPosition", position);
                }
                context.startActivity(intent);
            }
            else {

                Intent intent = new Intent(context, policy_set.class);
                intent.putExtra("policy_type", BonusItemList.get(position).get("policy_type"));
                intent.putExtra("policy_id", BonusItemList.get(position).get("policy_id"));
                intent.putExtra("option_id", BonusItemList.get(position).get("option_id"));
                intent.putExtra("policy_name", BonusItemList.get(position).get("policy_name"));

                if (BonusItemList.get(position).get("min_qty") != null) {
                    intent.putExtra("min_qty", BonusItemList.get(position).get("min_qty"));
                }
                if (BonusItemList.get(position).get("combinedQty") != null) {
                    intent.putExtra("combinedQty", BonusItemList.get(position).get("combinedQty"));
                }
                if (BonusItemList.get(position).get("formula") != null) {
                    intent.putExtra("formula", BonusItemList.get(position).get("formula"));
                    intent.putExtra("setSelection", BonusItemList.get(position).get("setSelection"));
                    intent.putExtra("setPosition", position);
                }
                context.startActivity(intent);
            }
        });

        view2.setVisibility(View.VISIBLE);
        txtName.setVisibility(View.VISIBLE);
        txtName.setText(BonusItemList.get(position).get("policy_name"));
        txtName.setTextColor(Color.parseColor("#000000"));

        return view2;
    }

}

