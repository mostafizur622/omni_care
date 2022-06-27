package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Model.Reports;
import com.srapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Reports_Adapter extends RecyclerView.Adapter<Reports_Adapter.ReportsHolder>{
    private List<Reports> orders = new ArrayList();

    public Reports_Adapter(List<Reports> list) {
        this.orders = list;
    }

    @NonNull
    @Override
    public ReportsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.reports_row,parent,false);
        return new ReportsHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReportsHolder holder, int position) {
        Reports singleItem = orders.get(position);
        holder.reportsTxt.setText(String.valueOf(singleItem.getName()));
        //holder.image.setX((float) 1.0);
        //holder.image.setY((float) 0.7);
        holder.reportsImage.setImageResource(R.drawable.arrow_right);

    }
    @Override
    public int getItemCount() {
        return orders.size();
    }
    public void setOrders(List<Reports> notes){
        this.orders = notes;
        notifyDataSetChanged();
    }
    public class ReportsHolder extends RecyclerView.ViewHolder {
        TextView reportsTxt;
        ImageView reportsImage;
        public ReportsHolder(@NonNull View itemView) {
            super(itemView);
            reportsTxt = itemView.findViewById(R.id.reports_text);
            reportsImage = itemView.findViewById(R.id.reports_image);
        }
    }
}