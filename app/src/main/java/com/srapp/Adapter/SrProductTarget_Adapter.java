package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.srapp.Db_Actions.Tables;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SrProductTarget_Adapter extends RecyclerView.Adapter<SrProductTarget_Adapter.Holder>{

    HashMap<String, ArrayList<String>> data;

    public SrProductTarget_Adapter(HashMap<String, ArrayList<String>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sr_product_target_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.product_name.setText(data.get(Tables.ProductName).get(position));
        holder.targetQty.setText(data.get(Tables.TargetQty).get(position));
        holder.achieveQty.setText(data.get(Tables.AchieveQty).get(position));
        holder.achieveVol.setText(data.get(Tables.AchieveVol).get(position));
        holder.targetVol.setText(data.get(Tables.TargetVol).get(position));

    }

    @Override
    public int getItemCount() {
        return data.get(Tables.ProductName).size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView product_name,targetQty,achieveQty, targetVol,achieveVol;

        public Holder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product);
            targetQty = itemView.findViewById(R.id.target_qty);
            achieveQty = itemView.findViewById(R.id.achv_qty);
            targetVol = itemView.findViewById(R.id.target_vol);
            achieveVol = itemView.findViewById(R.id.achv_vol);


        }
    }
}
