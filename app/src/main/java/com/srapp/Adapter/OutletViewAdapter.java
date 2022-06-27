package com.srapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OutletViewAdapter extends RecyclerView.Adapter<OutletViewAdapter.Holder> {

    HashMap<String, ArrayList<String>> data;
    private Context context;

    public OutletViewAdapter(HashMap<String, ArrayList<String>> data, Context context) {
        this.data = data;
        this.context = context;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.outlet_view_item,parent,false);
        return new Holder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        final int ix = position;
        Log.e("adapter...", "onBindViewHolder: "+position+"  "+data.get("Attributes").size() );
        if (position == data.get("Attributes").size()-1) {
            Log.e("view adapter", "onBindViewHolder: enterd............." );
            holder.titles.setVisibility(View.GONE);
            holder.infos.setVisibility(View.GONE);
            ImageView[] imageViews = {holder.img1,holder.img2,holder.img3};

            holder.imgTitle.setVisibility(View.VISIBLE);
            /*holder.img1.setVisibility(View.VISIBLE);
            holder.img2.setVisibility(View.VISIBLE);
            holder.img3.setVisibility(View.VISIBLE);*/
            int k;

            if (data.get("image_list").size()>0){
                for ( k = 0; k<data.get("image_list").size();k++){
                    imageViews[k].setVisibility(View.VISIBLE);
                    final int finalK = k;
                    imageViews[k].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            showPopUp(data.get("image_list").get(finalK));
                        }
                    });
                    Picasso.with(context).
                            load(data.get("image_list").get(k))
                            .into(imageViews[k]);
                }
            }



            /*Picasso.with(context).
                    load(data.get("image_list").get(0))
                    .into(holder.img1);
            Picasso.with(context).
                    load(data.get("image_list").get(1))
                    .into(holder.img2);
            Picasso.with(context).
                    load(data.get("image_list").get(2))
                    .into(holder.img3);*/
        } else{
            holder.titles.setText(data.get("Attributes").get(position));
            holder.infos.setText(data.get("Values").get(position));
    }
        /*if (position%2==0){
            holder.itemView.setBackgroundColor(shadow);
        }*/


    }
    public void showPopUp(String url){
        final Dialog visitDialogue = new Dialog(context);
        visitDialogue.setContentView(R.layout.image_show_pop_up);
        //visitDialogue.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        visitDialogue.show();

        ImageView imageView = visitDialogue.findViewById(R.id.show_image);
        Picasso.with(context).
                load(url)
                .into(imageView);


        Button yesBtn = visitDialogue.findViewById(R.id.yes);
        Button noBtn = visitDialogue.findViewById(R.id.no);
        Button cancelBtn = visitDialogue.findViewById(R.id.cancel_btn);
        Button dltBtn = visitDialogue.findViewById(R.id.delete_btn);
        dltBtn.setVisibility(View.INVISIBLE);
        final LinearLayout yesNoLayout = visitDialogue.findViewById(R.id.yes_no);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoLayout.setVisibility(View.INVISIBLE);
            }
        });

        dltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoLayout.setVisibility(View.VISIBLE);

            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visitDialogue.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.get("Attributes").size();
    }


    public class Holder extends RecyclerView.ViewHolder{
        TextView titles,infos, imgTitle;
        ImageView img1, img2, img3;

        public Holder(@NonNull View itemView) {
            super(itemView);
            titles = itemView.findViewById(R.id.title);
            infos = itemView.findViewById(R.id.info);
            imgTitle = itemView.findViewById(R.id.title_img);
            img1 = itemView.findViewById(R.id.image_view1);
            img2 = itemView.findViewById(R.id.image_view2);
            img3 = itemView.findViewById(R.id.image_view3);


        }
    }
}
