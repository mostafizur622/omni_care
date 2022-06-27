package com.srapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.srapp.Db_Actions.Tables;
import com.srapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.Holder> {
    HashMap<String, ArrayList<String>> data;
    private OnItemClickListener mlistener;

    public MarketListAdapter(HashMap<String, ArrayList<String>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.market_list_item,parent,false);
        return new Holder(view,mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.name.setText(String.valueOf(data.get(Tables.MARKETS_market_name).get(position)));
    }

    @Override
    public int getItemCount() {
        return data.get(Tables.MARKETS_market_id).size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onEditClick(int position);
        void onViewClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
        Log.e("m listener set", "setOnItemClickListener: "+"listener setted" );
    }


    public class Holder extends RecyclerView.ViewHolder {

        TextView name;
        Button editBtn;
        //View line;


        public Holder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.name_market_list_item);
            editBtn = itemView.findViewById(R.id.edit_market_list_item);
            //line = itemView.findViewById(R.id.line);

            editBtn.setOnClickListener(new View.OnClickListener() {
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

        }
    }
}


