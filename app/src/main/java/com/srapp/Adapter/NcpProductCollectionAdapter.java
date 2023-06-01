package com.srapp.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.srapp.NcpProductCollection;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NcpProductCollectionAdapter extends RecyclerView.Adapter<NcpProductCollectionAdapter.Holder> {

    ArrayList<HashMap<String,String>>products;
    private final boolean[] checkBoxState;

    NcpProductCollection ncp ;


    public NcpProductCollectionAdapter(ArrayList<HashMap<String,String>> products, Activity activity) {
        this.products = products;
        this.checkBoxState = new boolean[products.size()];
        this.ncp = (NcpProductCollection)activity;


       // Log.e("initial check", "NcpProductCollectionAdapter: "+checkBoxState );

    }



    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ncp_collection_recycler_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.productName.setText(products.get(position).get("product_name"));

        holder.cb.setOnCheckedChangeListener(null);

        holder.cb.setChecked(ncp.isproduct_selected(products.get(holder.getBindingAdapterPosition()).get("product_id")));
        Log.e("position check", "onBindViewHolder: position"+position+"--"+checkBoxState[position] );


        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    ncp.addproduct(products.get(holder.getBindingAdapterPosition()).get("product_id"));
                }else {
                    ncp.remove_product(products.get(holder.getBindingAdapterPosition()).get("product_id"));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    /*public void setData(ArrayList<String>products){
        this.products = products;

    }*/

    public class Holder extends RecyclerView.ViewHolder{
        TextView productName;
        CheckBox cb;

        public Holder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            cb = itemView.findViewById(R.id.check_box);
        }
    }
}
