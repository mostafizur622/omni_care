package com.srapp.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.srapp.Db_Actions.Tables;
import com.srapp.Model.OrderDetailsModel;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SrStoreStatusAdapter extends RecyclerView.Adapter<SrStoreStatusAdapter.Holder> {

    private HashMap<String,ArrayList<String>> srStoreStatusData = new HashMap<>();
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.so_store_status_item,parent,false);
        return new Holder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {




        holder.pname.setText(srStoreStatusData.get(Tables.PRODUCT_PRODUCT_NAME).get(position));

        holder.actual_qty.setText(srStoreStatusData.get("actual_qty").get(position));
        holder.invoice_qty.setText(srStoreStatusData.get("invoice_qty").get(position));
        holder.db_stock.setText(srStoreStatusData.get("db_stock").get(position));
        holder.order_qty.setText(srStoreStatusData.get("order_qty").get(position));

    }


    @Override
    public int getItemCount() {
        return srStoreStatusData.get(Tables.PRODUCT_PRODUCT_NAME).size();
    }

    public void bindData(HashMap<String,ArrayList<String>>srStoreStatusData){
        this.srStoreStatusData = srStoreStatusData;

    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView pname,actual_qty,invoice_qty, db_stock,order_qty;
        LinearLayout mainLayout;


        public Holder(@NonNull View itemView) {




            super(itemView);
            pname = itemView.findViewById(R.id.pname);
            actual_qty = itemView.findViewById(R.id.actual_qty);
            invoice_qty = itemView.findViewById(R.id.invoice_qty);
            db_stock = itemView.findViewById(R.id.db_stock);
            order_qty = itemView.findViewById(R.id.order_qty);

        }
    }
}
