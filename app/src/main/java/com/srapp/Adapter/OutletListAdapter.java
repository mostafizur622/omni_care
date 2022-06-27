package com.srapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.srapp.CreateOutlet;
import com.srapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OutletListAdapter extends RecyclerView.Adapter<OutletListAdapter.OutletListViewHolder> {
    ArrayList<String>data = new ArrayList<>();
    private OnItemClickListener mListener;

    @NonNull
    @Override
    public OutletListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.outlet_list_item,parent,false);
        return new OutletListViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OutletListViewHolder holder, int position) {
        holder.name.setText(String.valueOf(data.get(position)));
        /*holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOutletNames(ArrayList<String>data){
        this.data = data;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onEditClick(int position);
        void onViewClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }



    public class OutletListViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        Button edit, views;

        public OutletListViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.name_outlet_item);
            edit = itemView.findViewById(R.id.edit_outlet_item);
            views = itemView.findViewById(R.id.view_outlet_item);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                }
            });

            views.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onViewClick(position);
                        }
                    }

                }
            });

        }
    }


}
