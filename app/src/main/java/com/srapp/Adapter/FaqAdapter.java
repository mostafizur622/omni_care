package com.srapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.srapp.Model.FaqModel;
import com.srapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.Holder> {

    ArrayList<FaqModel> faqList = new ArrayList<>();


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.expandable_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.ttl.setText(position+1+". "+String.valueOf(faqList.get(position).getTitle()));
        holder.txt.setText(String.valueOf(faqList.get(position).getDetails()));

    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }



    public  void setData(ArrayList<FaqModel> faqList){
        this.faqList = faqList;

    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView ttl, txt;
        LinearLayout lin;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ttl = itemView.findViewById(R.id.title);
            txt = itemView.findViewById(R.id.text);
            lin = itemView.findViewById(R.id.expand);

            ttl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*lin.setVisibility(View.VISIBLE);
                    ttl.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.arrow_up,0);*/

                    if(lin.getVisibility() == View.GONE) {
                        lin.setVisibility(View.VISIBLE);
                        ttl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                    }else {
                        lin.setVisibility(View.GONE);
                        ttl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrowspin, 0);
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {


        }
    }
}
