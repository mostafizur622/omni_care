package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.R;

import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NcpCollectionAdapter extends RecyclerView.Adapter<NcpCollectionAdapter.Holder> {

    ArrayList<String>data = new ArrayList<>();

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ncp_collection_item,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.product.setText(String.valueOf(data.get(position)));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void setData(ArrayList<String> data){
        this.data = data;
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView product;
        EditText price, quantity, total;
        ImageView action;

        public Holder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            total = itemView.findViewById(R.id.total_price);
            action = itemView.findViewById(R.id.dlt_product_sales_item);
        }
    }
}
