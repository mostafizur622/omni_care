package com.srapp.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.Product;
import com.srapp.R;
import com.srapp.Util.AppManager;
import com.srapp.Util.Parent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.OrderHolder> {

    private List<Product> products = new ArrayList();
    Data_Source ds;
    Context context;

    public ProductAdapter(List<Product> list, Context context) {
        this.products = list;
        ds = new Data_Source(context);
        this.context = context;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_sales_memo, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderHolder holder, final int position) {

        holder.productname_tv.setText(String.valueOf(products.get(position).getProduct_name()));
        //holder.quantity.setText(String.valueOf(singleProduct.getQuantity()));
        holder.quantity.setVisibility(View.INVISIBLE);
        Log.e("visible", products.get(position).isSelected()  +" visible "+(products.get(position).getQuantity()>0.0)+" pid "+products.get(position).getPrductId());
        if ( products.get(position).isSelected() && products.get(position).getQuantity()>0.0) {
            Log.e("visible", "visible");
           // AppManager.setOpenKeyBoard(context);
            holder.checkBox.setChecked(true);
            holder.quantity.setVisibility(View.VISIBLE);

            holder.quantity.setText(products.get(position).getQuantity()+"");


        } else {

            holder.checkBox.setChecked(false);
        }
        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.e("text",s.toString()+((products.get(position).isSelected()) +""+ (Double.parseDouble(s.toString())>0)));

                if (products.get(position).isSelected() && Double.parseDouble(s.toString())>0) {

                    products.get(position).setQuantity(Double.parseDouble(s.toString()));

                }else {

                    holder.checkBox.setChecked(products.get(position).isSelected());
                }
            }
        });



        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    products.get(position).setSelected(true);
                    Log.e("boolean",products.get(position).isSelected()+"")  ;
                    holder.quantity.setVisibility(View.VISIBLE);
                    //products.remove(position);

                }else {
                    holder.quantity.setVisibility(View.INVISIBLE);
                    //products.get(position).setSelected(false);
                    //products.get(position).setQuantity(0.00);

                }


            }
        });
        //holder.checkBox.setText(String.valueOf(singleProduct.g));


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setOrders(List<Product> notes) {
        this.products = notes;
        notifyDataSetChanged();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView productname_tv;
        EditText quantity;
        CheckBox checkBox;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            productname_tv = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            checkBox = itemView.findViewById(R.id.checkbox);

        }
    }

    public String getPreference(String key) {
        String value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        value = prefs.getString(key, "0");

        return value;

    }

    /*public static void setFont (){

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("Font/FuturaPTBook.otf")
                .setFontAttrId(R.attr.fontPath).build());
    }*/

    public void savePreference(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}