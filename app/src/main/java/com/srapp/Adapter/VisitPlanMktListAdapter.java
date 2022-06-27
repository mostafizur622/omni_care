package com.srapp.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Db_Actions.Tables;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;





public class VisitPlanMktListAdapter extends RecyclerView.Adapter<VisitPlanMktListAdapter.MarketHolder>{
    HashMap<String, ArrayList<String>> data;

    public VisitPlanMktListAdapter(HashMap<String, ArrayList<String>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MarketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.market_list_row,parent,false);
        return new MarketHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MarketHolder holder, int position) {

        holder.marketsTxt.setText(String.valueOf(data.get(Tables.MARKETS_market_name).get(position)));
        //holder.image.setX((float) 1.0);
        //holder.image.setY((float) 0.7);
        //holder.marketsImage.setImageResource(R.drawable.arrow_right);

    }
    @Override
    public int getItemCount() {
        return data.get(Tables.MARKETS_market_id).size();
    }

    public class MarketHolder extends RecyclerView.ViewHolder {
        TextView marketsTxt;
        ImageView marketsImage;
        public MarketHolder(@NonNull View itemView) {
            super(itemView);
            marketsTxt = itemView.findViewById(R.id.market_text);
            marketsImage = itemView.findViewById(R.id.market_image);
        }
    }
}