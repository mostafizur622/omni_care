package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.srapp.Model.OrderDetailsModel;
import com.srapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductSalesAdapter extends RecyclerView.Adapter<ProductSalesAdapter.OrderHolder>{

    private List<OrderDetailsModel> orders = new ArrayList();


    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_sales_item,parent,false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        OrderDetailsModel singleOrder = orders.get(position);
        holder.product_name_tv.setText(String.valueOf(singleOrder.getProductName()));
        holder.price_tv.setText(String.valueOf(singleOrder.getPrice()));
        holder.quantity_tv.setText(String.valueOf(singleOrder.getQuantity()));
        holder.total_tv.setText(String.valueOf(singleOrder.getTotal()));

    }



    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<OrderDetailsModel> notes){
        this.orders = notes;
        notifyDataSetChanged();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {

        TextView product_name_tv,price_tv,quantity_tv, total_tv;
        ImageView dlt;


        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            product_name_tv = itemView.findViewById(R.id.product_name);
            price_tv = itemView.findViewById(R.id.price);
            quantity_tv = itemView.findViewById(R.id.quantity);
            total_tv = itemView.findViewById(R.id.total_price);
            dlt = itemView.findViewById(R.id.dlt_product_sales_item);

        }
    }
}
