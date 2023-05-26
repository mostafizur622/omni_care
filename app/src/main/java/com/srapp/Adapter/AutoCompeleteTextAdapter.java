package com.srapp.Adapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.srapp.R;

/**
 * Created by Tanvir3488 on 5/26/2023.
 */

public class AutoCompeleteTextAdapter extends ArrayAdapter<HashMap<String,String>> {

    Context context;
    int resource;
    int textViewResourceId;
    ArrayList<HashMap<String,String>> _Outlets,ALlOutlets;

    public AutoCompeleteTextAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<HashMap<String,String>> mList) {
        super(context, resource, textViewResourceId, mList);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this._Outlets = mList;
        this.ALlOutlets = new ArrayList<>(mList);

        Log.e("constraint_size",_Outlets.size()+" ");

    }



    @Override
    public HashMap<String,String> getItem(int position) {
        Log.e("constraint_size1",_Outlets.size()+" ");
        return _Outlets.get(position);
    }

    @Override
    public int getCount() {
        Log.e("constraint_size2",_Outlets.size()+" ");

        return _Outlets.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("constraint_size3",_Outlets.size()+" ");
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.outlet_dw, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.outlet_name);
            textView.setText(_Outlets.get(position).get("outlet_name"));
        }
        HashMap<String,String> people = _Outlets.get(position);
        if (people != null) {
            TextView textView = (TextView) view.findViewById(R.id.outlet_name);
            if (textView != null) {
                textView.setText(people.get("outlet_name"));
            }

        }
        return view;
    }


    @Override
    public Filter getFilter() {
        Log.e("constraint_size4",_Outlets.size()+" ");
        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        @Override
        public String convertResultToString(Object resultValue) {
            return ((HashMap<String,String>) resultValue).get("outlet_name");
        }


        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
//            Log.e("constraint_size5",results.values.toString()+" ");
           // Log.e("constraint1",constraint.toString()+" "+LList.size());
            _Outlets.clear();
            if (results.values!=null)
            _Outlets.addAll((ArrayList<HashMap<String,String>>)results.values);
            notifyDataSetChanged();
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
        // Log.e("constraint6",constraint.toString()+" "+ALlOutlets.size());
            FilterResults filterResults = new FilterResults();

            ArrayList<HashMap<String,String>> Outlets = new ArrayList<>();
            if (constraint != null) {
                Outlets.clear();
                for (HashMap<String,String> people : ALlOutlets) {

                    if (people.get("outlet_name").contains(constraint)) {
                        Log.e("constraint",people.get("outlet_name"));
                        Outlets.add(people);
                    }
                }
                filterResults.values = Outlets;
                filterResults.count = Outlets.size();
            }else {
                filterResults.values = ALlOutlets;
                filterResults.count = ALlOutlets.size();
            }


            return filterResults;

        }
    };

}