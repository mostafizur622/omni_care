package com.srapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.srapp.GiftIssueActivity;
import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;

public class AdapterForGiftIssueList extends BaseAdapter {

    // Declare Variables
    Activity context;
    int tec,toc;
    double tqty;


    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    public AdapterForGiftIssueList(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
        this.context = context;
        BonusItemList = arraylistContent;



    }

    @Override
    public int getCount() {
        return BonusItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
//		return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {


        HashMap<String, String> mapContent = new HashMap<String, String>();
        mapContent = BonusItemList.get(position);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_issue_list_row, null);
        TextView date = (TextView)view2.findViewById(R.id.date);
        TextView outlet = (TextView)view2.findViewById(R.id.outlet);
        Button view = view2.findViewById(R.id.view);
        Button edit = view2.findViewById(R.id.edit);

        date.setText(mapContent.get("date"));


        if (mapContent.get("is_editable").equalsIgnoreCase("0")){
            edit.setVisibility(View.INVISIBLE);
        }

        outlet.setText(mapContent.get("outlet"));
        final HashMap<String, String> finalMapContent = mapContent;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, GiftIssueActivity.class);
                intent.putExtra("gift_id",finalMapContent.get("id"));
                intent.putExtra("gift_date",finalMapContent.get("date"));
                intent.putExtra("state",0);
                context.startActivity(intent );

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, GiftIssueActivity.class);
                intent.putExtra("gift_id",finalMapContent.get("id"));
                intent.putExtra("gift_date",finalMapContent.get("date"));
                intent.putExtra("state",1);
                context.startActivity(intent );

            }
        });




        return view2;
    }








}
