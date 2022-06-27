package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Model.SR_Account;
import com.srapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SR_AccountAdapter extends RecyclerView.Adapter<SR_AccountAdapter.OrderHolder>{
    private List<SR_Account> orders = new ArrayList();

    public SR_AccountAdapter(List<SR_Account> list) {
        this.orders = list;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sr_account_row,parent,false);
        return new OrderHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        SR_Account singleItem = orders.get(position);
        holder.sr_account.setText(String.valueOf(singleItem.getName()));
        //holder.image.setX((float) 1.0);
        //holder.image.setY((float) 0.7);
        holder.image.setImageResource(R.drawable.arrow_right);

    }
    @Override
    public int getItemCount() {
        return orders.size();
    }
    public void setOrders(List<SR_Account> notes){
        this.orders = notes;
        notifyDataSetChanged();
    }
    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView sr_account;
        ImageView image;
        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            sr_account = itemView.findViewById(R.id.sr_account_text);
            image = itemView.findViewById(R.id.sr_account_image);
        }
    }
}