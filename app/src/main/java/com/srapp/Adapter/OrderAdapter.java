package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Model.Order_Report;
import com.srapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder>{
    private List<Order_Report> orders = new ArrayList();

    public OrderAdapter(List<Order_Report> list) {
        this.orders = list;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_order_list,parent,false);
        return new OrderHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        Order_Report singleOrder = orders.get(position);
        holder.sl_no_tv.setText(String.valueOf(singleOrder.getSrl_no()));
        holder.sale_date_tv.setText(String.valueOf(singleOrder.getSale_date()));
        holder.memo_tv.setText(String.valueOf(singleOrder.getMemo()));
        holder.outlet_tv.setText(String.valueOf(singleOrder.getOutlet()));
        holder.mamount_tv.setText(String.valueOf(singleOrder.getM_amount()));
        holder.image.setX((float) 1.0);
        holder.image.setY((float) 0.7);
        holder.image.setImageResource(R.drawable.eye_icon);

    }
    @Override
    public int getItemCount() {
        return orders.size();
    }
    public void setOrders(List<Order_Report> notes){
        this.orders = notes;
        notifyDataSetChanged();
    }
    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView sl_no_tv,sale_date_tv,memo_tv, outlet_tv,mamount_tv;
        ImageView image;
        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            sl_no_tv = itemView.findViewById(R.id.sr_no);
            sale_date_tv = itemView.findViewById(R.id.sale_date);
            memo_tv = itemView.findViewById(R.id.memo);
            outlet_tv = itemView.findViewById(R.id.outlet);
            mamount_tv = itemView.findViewById(R.id.m_account);
            image = itemView.findViewById(R.id.icon);
        }
    }
}