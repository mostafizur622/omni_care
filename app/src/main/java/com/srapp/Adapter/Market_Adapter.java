package com.srapp.Adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Model.Market;
import com.srapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Market_Adapter extends RecyclerView.Adapter<Market_Adapter.MarketHolder>{
    private ArrayList<String> markets;
    private ArrayList<String[]>days;
    String[] daysName = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"};
    OnItemClickListener mlistener;

    public Market_Adapter(ArrayList<String> list, ArrayList<String[]>daysList) {
        this.markets = list;
        this.days = daysList;
    }

    @NonNull
    @Override
    public MarketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.visit_plan_item,parent,false);
        return new MarketHolder(view,mlistener);
    }
    @Override
    public void onBindViewHolder(@NonNull MarketHolder holder, int position) {

        holder.marketsTxt.setText(markets.get(position));
        //holder.image.setX((float) 1.0);
        //holder.image.setY((float) 0.7);
        holder.marketsImage.setImageResource(R.drawable.arrow_right);
        String txt = "";
        for (int i =0; i<daysName.length;i++){
            if (days.get(position)[i].equals("1")){
                txt = txt+ daysName[i]+" ";
            }
        }
        holder.daysTxt.setText(txt.trim());
        holder.daysTxt.setTextColor(Color.GREEN);

    }
    @Override
    public int getItemCount() {
        return markets.size();
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

    public class MarketHolder extends RecyclerView.ViewHolder {
        TextView marketsTxt, daysTxt;
        ImageView marketsImage;
        public MarketHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            marketsTxt = itemView.findViewById(R.id.market_text);
            marketsImage = itemView.findViewById(R.id.market_image);
            daysTxt = itemView.findViewById(R.id.days_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}