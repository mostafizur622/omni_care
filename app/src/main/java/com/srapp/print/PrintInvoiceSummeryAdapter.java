package com.srapp.print;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.srapp.R;
import java.util.ArrayList;
import java.util.HashMap;

public class PrintInvoiceSummeryAdapter extends RecyclerView.Adapter<PrintInvoiceSummeryAdapter.SingleItemRowHolder> {

    private ArrayList<HashMap<String,String>>  itemsList;
    private Context mContext;

    public PrintInvoiceSummeryAdapter(Context context, ArrayList<HashMap<String,String>> itemsList) {
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
        holder.itemName.setText(itemsList.get(i).get("product_name"));
        holder.vat.setText(itemsList.get(i).get("EC"));
        holder.price.setText(itemsList.get(i).get("OC"));
        holder.qty.setText(itemsList.get(i).get("qty"));
        holder.total.setText(itemsList.get(i).get("bqty"));
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
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

    public String roundTwoDecimals(double d) {

        Log.e("Double:", String.valueOf(d));
        Log.e("Modulus:", String.valueOf(d % 1));
        return String.format("%.2f", d);
    }

}
