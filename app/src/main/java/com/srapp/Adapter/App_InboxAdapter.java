package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.srapp.Model.App_Inbox;
import com.srapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class App_InboxAdapter extends RecyclerView.Adapter<App_InboxAdapter.messageHolder>{
    private List<App_Inbox> orders = new ArrayList();

    public App_InboxAdapter(List<App_Inbox> list) {
        this.orders = list;
    }

    @NonNull
    @Override
    public messageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.app_inbox_row,parent,false);
        return new messageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull messageHolder holder, int position) {
        App_Inbox singleItem = orders.get(position);
        holder.greetingsHead.setText(String.valueOf(singleItem.getGreetings()));
        holder.description.setText(String.valueOf(singleItem.getDescription()));
        holder.sendername.setText(String.valueOf(singleItem.getSenderName()));
        holder.senderaddress.setText(String.valueOf(singleItem.getSenderAddress()));
        holder.date.setText(String.valueOf(singleItem.getDate()));

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
    public void setOrders(List<App_Inbox> notes){
        this.orders = notes;
        notifyDataSetChanged();
    }
    public class messageHolder extends RecyclerView.ViewHolder {
        TextView greetingsHead,description,sendername,senderaddress,date;
        public messageHolder(@NonNull View itemView) {
            super(itemView);
            greetingsHead = itemView.findViewById(R.id.greetingHeader);
            description = itemView.findViewById(R.id.description);
            sendername = itemView.findViewById(R.id.name);
            senderaddress = itemView.findViewById(R.id.address);
            date = itemView.findViewById(R.id.dateTime);
        }
    }
}