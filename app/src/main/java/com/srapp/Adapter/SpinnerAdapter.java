package com.srapp.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        /*Typeface font = Typeface.createFromAsset(getContext().getAssets(),
        "Font/FuturaPTBook.otf");*/
        List<String> items;
// (In reality I used a manager which caches the Typeface objects)
// Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);

public SpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.items = items;
        }

// Affects default (closed) state of the spinner
@Override
public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (position<items.size()) {
                 view = (TextView) super.getView(position, convertView, parent);
        }else {
                 view = (TextView) super.getView((items.size()-1), convertView, parent);
        }
        /*view.setTypeface(font);*/
        return view;
        }

// Affects opened state of the spinner
@Override
public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        /*view.setTypeface(font);*/
        return view;
        }
}