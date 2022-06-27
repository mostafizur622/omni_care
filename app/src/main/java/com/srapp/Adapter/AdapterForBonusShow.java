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

public class AdapterForBonusShow extends BaseAdapter {

    // Declare Variables
    Activity context;


    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForBonusShow(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
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


        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, null);
        TextView txtName = (TextView)view2.findViewById(R.id.product_name);
        EditText QuantityEdTxtforProduct = (EditText)view2.findViewById(R.id.quantity);
        CheckBox checkBox1 = (CheckBox)view2.findViewById(R.id.checkbox);

        QuantityEdTxtforProduct.setEnabled(false);
        QuantityEdTxtforProduct.setVisibility(View.VISIBLE);

        Log.e("","itemListContent"+BonusItemList.toString());

        HashMap<String, String> mapContent = new HashMap<String, String>();
        mapContent = BonusItemList.get(position);
        Log.e("","bonus_name"+mapContent.get("bonus_name"));
        txtName.setText(mapContent.get("bonus_name"));
        QuantityEdTxtforProduct.setText(mapContent.get("quantity"));
        checkBox1.setVisibility(View.INVISIBLE);


        return view2;
    }








}
