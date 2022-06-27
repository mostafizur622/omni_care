package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Model.NCP_Collection;
import com.srapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NCP_CollectionAdapter extends RecyclerView.Adapter<NCP_CollectionAdapter.OrderHolder>{
    private List<NCP_Collection> orders = new ArrayList();

    public NCP_CollectionAdapter(List<NCP_Collection> list) {
        this.orders = list;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ncp_collection_row,parent,false);
        return new OrderHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        NCP_Collection singleItem = orders.get(position);
        holder.sr_account.setText(String.valueOf(singleItem.getName()));
        //holder.image.setX((float) 1.0);
        //holder.image.setY((float) 0.7);
        holder.image.setImageResource(R.drawable.arrow_right);

    }
    @Override
    public int getItemCount() {
        return orders.size();
    }
    public void setOrders(List<NCP_Collection> notes){
        this.orders = notes;
        notifyDataSetChanged();
    }
    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView sr_account;
        ImageView image;
        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            sr_account = itemView.findViewById(R.id.ncp_collection_text);
            image = itemView.findViewById(R.id.ncp_collection_image);
        }
    }
}