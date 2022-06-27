package com.srapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.srapp.Db_Actions.Tables;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.Holder> {
    HashMap<String, ArrayList<ArrayList<String>>>data;
    int color[];
    Context ctx;
    int flag=1;

    public PriceListAdapter(HashMap<String, ArrayList<ArrayList<String>>> data, Context context) {
        this.data = data;
        this.ctx = context;
        Log.e("-------->", "PriceListAdapter: "+data );
        /*this.color = new int[data.get(Tables.PRODUCT_PRODUCT_NAME).size()];
        for (int i = 0; i < data.get(Tables.PRODUCT_PRODUCT_NAME).size();i++){
            color[i]=i+1;
        }*/
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.price_list_item,parent,false);
        return new Holder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
//        Log.e("color-------->", "onBindViewHolder: "+color[position]+"---->"+color[position]%2 );
        /*if (color[position]%2==0){
            holder.itemView.setBackgroundColor(Color.parseColor("#255eba"));
            Log.e("------->", "onBindViewHolder: "+data.get(Tables.PRODUCT_PRODUCT_NAME).get(position)+"  is BLUE");
        }*/
        LinearLayout linearLayout = holder.qtyLayout;
        LinearLayout linearLayout1 = holder.priceLayout;

        linearLayout.removeAllViews();
        linearLayout1.removeAllViews();
        try {

            if (data.get(Tables.MinQty).get(position) != null && data.get(Tables.MinQty).get(position).size() > 0) {
                Log.e("null check----->", "onBindViewHolder: " + data.get(Tables.MinQty).get(position).size());
                for (int k = 0; k < data.get(Tables.MinQty).get(position).size(); k++) {

                    TextView valueTV = new TextView(ctx);
                    valueTV.setText(data.get(Tables.MinQty).get(position).get(k));
                    valueTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    valueTV.setBackgroundResource(R.drawable.left_edit_text_table_border);
                    //valueTV.setId(Integer.parseInt("40"+k));
                    valueTV.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    linearLayout.addView(valueTV);

                    TextView valueTV1 = new TextView(ctx);
                    valueTV1.setText(data.get(Tables.MinPrice).get(position).get(k));
                    valueTV1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    valueTV1.setBackgroundResource(R.drawable.right_edit_text_table_border);

                    //valueTV.setId(Integer.parseInt("50"+k));
                    valueTV1.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    linearLayout1.addView(valueTV1);

                }
            }


            // holder.name.setText(data.get(Tables.PRODUCT_PRODUCT_NAME).get(position));
            holder.name.setText(data.get(Tables.ProductName).get(position).get(0));
            holder.qty1.setText("1");
            //holder.qty2.setText(data.get(Tables.MinQty).get(position).get(0));
            Log.e("PRI--->", "onBindViewHolder: ---PRICE 1-------> " + data.get("Main_PRICE"));
            holder.price1.setText(data.get("Main_PRICE").get(position).get(0));
            //holder.price2.setText(data.get(Tables.MinPrice).get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.get(Tables.PRODUCT_PRODUCT_NAME).size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView name,price1,price2,qty1,qty2;
        LinearLayout qtyLayout,priceLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price1 = itemView.findViewById(R.id.price_1);
            price2 = itemView.findViewById(R.id.price_2);
            qty1 = itemView.findViewById(R.id.quantity1);
            qty2 = itemView.findViewById(R.id.quantity2);
            qtyLayout = itemView.findViewById(R.id.slap_layout);
            priceLayout = itemView.findViewById(R.id.price_layout);
        }
    }
}
