package com.srapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Db_Actions.Tables;
import com.srapp.Model.NCP_Collection;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BonusPartyAffAdapter extends RecyclerView.Adapter<BonusPartyAffAdapter.Holder>{
    HashMap<String, ArrayList<String>> outletHashmap;
    public ArrayList<String>selected = new ArrayList<>();
    public ArrayList<String>unSelected = new ArrayList<>();
    public ArrayList<Boolean>checkState;
    public ArrayList<Integer>initialState;
    public ArrayList<Integer>selectedPos = new ArrayList<>();
    public ArrayList<Integer>unSelectedPos = new ArrayList<>();
    private final boolean[] checkBoxState;


    public BonusPartyAffAdapter(HashMap<String, ArrayList<String>> hash,ArrayList<Boolean>state,ArrayList<Integer>state2) {
        this.outletHashmap = hash;
        this.checkBoxState = new boolean[hash.get(Tables.OUTLETS_ID).size()];
        this.checkState = state;
        initialState = state2;

         Log.e("initial check", "I am Constructor" );

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ncp_collection_recycler_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.productName.setText(outletHashmap.get(Tables.OUTLETS_OUTLET_NAME).get(position));

        holder.cb.setOnCheckedChangeListener(null);

       // holder.cb.setChecked(checkBoxState[position]);
        holder.cb.setChecked(checkState.get(position));
        Log.e("position check", "onBindViewHolder: position"+position+"--"+checkState.get(position) );

        Log.e("cb init------>", "onCheckedChanged:"+position+ "  "+initialState.get(position) );
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //checkBoxState[position]=isChecked;
                Log.e("cb init------>", "onCheckedChanged:"+position+ "  "+initialState.get(position) );

                if (isChecked == true){
                    if (initialState.get(position)== 0) {
                        selected.add(outletHashmap.get(Tables.OUTLETS_ID).get(position));
                        //initialState.set(position,1);
                        selectedPos.add(position);

                    }else {
                        unSelected.remove(outletHashmap.get(Tables.OUTLETS_ID).get(position));
                        //checkState.add(position,false);
                    }
                    Log.e("when update initial--->", "onCheckedChanged: checked --->"+checkState.get(position) );
                    Log.e("when update initial--->", "onCheckedChanged: checked -sel-->"+selected );
                    Log.e("when update initial--->", "onCheckedChanged: checked -unsel-->"+unSelected );
                }else {
                    if (initialState.get(position)==1) {
                        unSelected.add(outletHashmap.get(Tables.OUTLETS_ID).get(position));
                        //initialState.set(position,0);
                        unSelectedPos.add(position);


                    }else {
                        selected.remove(outletHashmap.get(Tables.OUTLETS_ID).get(position));
                    }

                    Log.e("when update initial--->", "onCheckedChanged: unchecked ---> " + checkState.get(position));
                    Log.e("when update initial--->", "onCheckedChanged: unchecked sel---> " + selected);
                    Log.e("when update initial--->", "onCheckedChanged: unchecked unsel---> " + unSelected);
                }

                checkState.set(position,isChecked);
                Log.e("when update initial--->", "onCheckedChanged: final "+checkState.get(position) );
                Log.e("when update initial--->", "onCheckedChanged: final sel"+selected );
                Log.e("when update initial--->", "onCheckedChanged: final unsel"+unSelected );
            }
        });

    }


    @Override
    public int getItemCount() {
        return outletHashmap.get(Tables.OUTLETS_ID).size();
    }
    public ArrayList<String> getSelected(){
        /*selected = new ArrayList<>();
        for (int i = 0; i< checkState.size();i++){
            if (checkState.get(i)== true){
                selected.add(outletHashmap.get(Tables.OUTLETS_ID).get(i));
            }
        }*/
        return selected;

    }
    public ArrayList<String>getUnSelected(){
        /*for (int i=0; i<selected.size();i++){
            for (int j = 0; j<unSelected.size();j++){
                if (selected.get(i).equals(unSelected.get(j))){
                    unSelected.remove(unSelected.get(j));
                    selected.remove(unSelected.get(j));
                }

            }
        }*/
        return unSelected;
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView productName;
        CheckBox cb;

        public Holder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            cb = itemView.findViewById(R.id.check_box);
        }
    }
}
