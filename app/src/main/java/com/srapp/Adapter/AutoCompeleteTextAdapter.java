package com.srapp.Adapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
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
    List<HashMap<String,String>> mList, filteredPeople, mListAll;

    public AutoCompeleteTextAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<HashMap<String,String>> mList) {
        super(context, resource, textViewResourceId, mList);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.mList = mList;
        mListAll = mList;
        filteredPeople = new ArrayList<HashMap<String,String>>();
    }

    @Override
    public HashMap<String,String> getItem(int position) {

        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.outlet_dw, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.outlet_name);
            textView.setText(mList.get(position).get("outlet_name"));
        }
        HashMap<String,String> people = mList.get(position);
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

        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            List<HashMap<String,String>> filteredList = (List<HashMap<String,String>>) results.values;

            if (results != null && results.count > 0) {
                clear();
                for (HashMap<String,String> people : filteredList) {
                    add(people);
                }
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.e("constraint",constraint.toString()+" "+mListAll.size());
            FilterResults filterResults = new FilterResults();
            if (constraint != null) {
                filteredPeople.clear();
                for (HashMap<String,String> people : mListAll) {
                    Log.e("constraint",people.get("outlet_name"));
                    if (people.get("outlet_name").contains(constraint)) {

                        filteredPeople.add(people);
                    }
                }
                filterResults.values = filteredPeople;
                filterResults.count = filteredPeople.size();
            }

            Activity activity = (Activity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
            return filterResults;

        }
    };

}