package com.srapp.print;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SRStockAdapter extends RecyclerView.Adapter<SRStockAdapter.SingleItemRowHolder> {

    private HashMap<String, ArrayList<String>> itemsList;
    private Context mContext;

    public SRStockAdapter(Context context, HashMap<String, ArrayList<String>> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.print_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        holder.vat.setText(itemsList.get("actual_qty").get(i).split("\\.")[0]);
        holder.price.setText(itemsList.get("invoice_qty").get(i));
        holder.qty.setText(itemsList.get("db_stock").get(i));
        holder.total.setText(itemsList.get("order_qty").get(i));
        holder.itemName.setText(itemsList.get("product_name").get(i));
    }

    @Override
    public int getItemCount() {
        return itemsList.get("product_name").size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemName;
        protected TextView vat;
        protected TextView price;
        protected TextView qty;
        protected TextView total;

        public SingleItemRowHolder(View view) {

            super(view);

            this.itemName = (TextView) view.findViewById(R.id.itemName);
            this.vat = (TextView) view.findViewById(R.id.vat);
            this.price = (TextView) view.findViewById(R.id.price);
            this.qty = (TextView) view.findViewById(R.id.qty);
            this.total = (TextView) view.findViewById(R.id.total);

        }
    }
}
