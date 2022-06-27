package com.srapp.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.R;
import com.srapp.TempData;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.srapp.TempData.BPSelected_bonus;
import static com.srapp.TempData.BPbonus_product_t;

public class AdapterForBonusPolicyProductSelection extends BaseAdapter {

    Activity context;
    double qty;
    Data_Source db;
    ArrayList<HashMap<String, String>> bonus_product = new ArrayList<HashMap<String, String>>();
    double providedQty =0.00;

    public AdapterForBonusPolicyProductSelection(Activity context, ArrayList<HashMap<String, String>> arraylistContent, double qty) {
        db = new Data_Source(context);
        this.context = context;
        bonus_product = arraylistContent;
        providedQty=0.00;
        this.qty = 0.00;
        this.qty = qty;
        Log.e("qtytt", qty + "");
        Log.e("qtyttestttt", qty + "");
    }



    @Override
    public int getCount() {

        providedQty =0.00;
        return bonus_product.size();
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

        Log.e("BPSelected_adapter",new Gson().toJson(BPSelected_bonus));
        final View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, null);
        TextView NameTv = (TextView) view2.findViewById(R.id.product_name);
        final EditText QuantityEd = (EditText) view2.findViewById(R.id.quantity);
        final CheckBox chbox = (CheckBox) view2.findViewById(R.id.checkbox);

        NameTv.setText(bonus_product.get(position).get("item"));
        chbox.setChecked(Boolean.parseBoolean(bonus_product.get(position).get("product_selected")));
        QuantityEd.setEnabled(Boolean.parseBoolean(bonus_product.get(position).get("editable")));
        QuantityEd.setText(bonus_product.get(position).get("quantity"));
        Log.e("check_quentity", bonus_product.get(position).get("quantity"));

        NameTv.setTextColor(Color.parseColor("#000000"));
        QuantityEd.setTextColor(Color.parseColor("#000000"));

        providedQty = providedQty+Double.parseDouble(bonus_product.get(position).get("quantity"));

        Log.e("check", providedQty+" "+qty);

        if (Boolean.parseBoolean(bonus_product.get(position).get("editable")) && providedQty>qty )   {
            Log.e("try",providedQty+"");
            QuantityEd.setText("0.00");
            bonus_product.get(position).put("quantity", "0.00");
        }

        Log.e("quantityf", bonus_product.get(position).get("quantity"));

        if (TempData.editMemo.equalsIgnoreCase("true")) {
            chbox.setChecked(Boolean.parseBoolean(bonus_product.get(position).get("selected_s")));
            Log.i(TAG, "bonus_product_select: " + new Gson().toJson(BPbonus_product_t));
        }

        else {
            chbox.setChecked(Boolean.parseBoolean(bonus_product.get(position).get("selected_s")));
            Log.i(TAG, "bonus_product_select: " + new Gson().toJson(bonus_product));
        }

        if (Boolean.parseBoolean(bonus_product.get(position).get("selected_s"))) {
            QuantityEd.setVisibility(View.VISIBLE);
        }

        chbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bonus_product.get(position).put("selected_s", "true");
                bonus_product.get(position).put("editable", "true");
                QuantityEd.setEnabled(Boolean.parseBoolean(bonus_product.get(position).get("editable")));
                QuantityEd.setVisibility(View.VISIBLE);
            } else {

                bonus_product.get(position).put("selected_s", "false");
                bonus_product.get(position).put("editable", "false");
                BPSelected_bonus.get(bonus_product.get(position).get("policy_id")).get(bonus_product.get(position).get("set")).get(bonus_product.get(position).get("product_id")).put("qty", "0.0");
                Log.e("selected_item", bonus_product.get(position).get("quantity"));
                QuantityEd.setVisibility(View.INVISIBLE);

                bonus_product.get(position).put("quantity",  "0.00");
                QuantityEd.setText("0.00");


            }

        });


        QuantityEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(s)) {

                    QuantityEd.setText("0.00");

                } else {
                    Double qty1 = 0.00;
                    try {
                        qty1 = Double.parseDouble(s.toString());
                        Log.e("qty_", qty1 + "");

                    } catch (Exception e) {
                        Toast.makeText(context, "Please input correct number format ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.e("BPSelected_adapter",new Gson().toJson(BPSelected_bonus));
                    if ((qty1 + getrestpqty(bonus_product.get(position).get("product_id"))) <= qty && (qty - (qty1 + getrestpqty(bonus_product.get(position).get("product_id")))) >= 0) {
                        bonus_product.get(position).put("quantity", qty1 + "");
                        BPSelected_bonus.get(bonus_product.get(position).get("policy_id")).get(bonus_product.get(position).get("set")).get(bonus_product.get(position).get("product_id")).put("qty", String.valueOf(qty1));
                        Log.e("iamhere", qty1 + " " + getrestpqty(bonus_product.get(position).get("product_id")) + " " + qty);

                    } else {

                        if ((qty - (qty1 + getrestpqty(bonus_product.get(position).get("product_id")))) < 0) {
                            qty1 = qty - getrestpqty(bonus_product.get(position).get("product_id"));

                            QuantityEd.setText(qty1 + "");
                            bonus_product.get(position).put("quantity", qty1 + "");
                            BPSelected_bonus.get(bonus_product.get(position).get("policy_id")).get(bonus_product.get(position).get("set")).get(bonus_product.get(position).get("product_id")).put("qty", String.valueOf(qty1));

                            Log.e("iamhere1_", qty1 + " " + getrestpqty(bonus_product.get(position).get("product_id")) + " " + qty);

                        }
                    }

                }


            }
        });


        return view2;
    }


    private double getrestpqty(String product_id) {

        double qty = 0.00;
        for (int i = 0; i < bonus_product.size(); i++) {
            if (!bonus_product.get(i).get("product_id").equalsIgnoreCase(product_id))
                qty = qty + Double.parseDouble(bonus_product.get(i).get("quantity"));
        }

        Log.d("getrestpqty", "getrestpqty: "+qty);
        return qty;
    }

    public ArrayList<HashMap<String, String>> getdata() {
        return bonus_product;
    }

}