package com.srapp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.srapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;

    public class AdapterForProductOrderWiseSummery extends BaseAdapter {

        // Declare Variables
        Activity context;
        int tec,toc;
        double tqty,tbqty;

        ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();

        public AdapterForProductOrderWiseSummery(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
            this.context = context;
            BonusItemList = arraylistContent;

            for (int i = 0 ; i<BonusItemList.size(); i++){
                HashMap<String, String> mapContent = new HashMap<String, String>();
                mapContent = BonusItemList.get(i);
                tec += Integer.parseInt(mapContent.get("EC"));
                toc += Integer.parseInt(mapContent.get("OC"));
                tqty += Double.parseDouble(mapContent.get("qty"));
                tbqty += Double.parseDouble(mapContent.get("bqty"));
            }
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

        public View getView(final int position, View convertView, ViewGroup parent) {

            TextView toctxt = context.findViewById(R.id.toc);
            TextView tectxt = context.findViewById(R.id.tec);
            TextView tqtytxt = context.findViewById(R.id.tqty);
            TextView tbqtyet = context.findViewById(R.id.tbqty);
            HashMap<String, String> mapContent = new HashMap<String, String>();

            mapContent = BonusItemList.get(position);
            View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_wise_order_summery, null);
            TextView product_name = (TextView)view2.findViewById(R.id.product_name);
            TextView EC = (TextView)view2.findViewById(R.id.ec);
            TextView OC = (TextView)view2.findViewById(R.id.oc);
            TextView bqty = (TextView)view2.findViewById(R.id.bqty);
            TextView sale_quntity = (TextView)view2.findViewById(R.id.qty);

            product_name.setText(mapContent.get(PRODUCT_PRODUCT_NAME));

            EC.setText(mapContent.get("EC"));
            OC.setText(mapContent.get("OC"));
            bqty.setText(mapContent.get("bqty"));
            sale_quntity.setText(mapContent.get("qty"));

            tectxt.setText(tec+"");
            toctxt.setText(toc+"");
            tqtytxt.setText(tqty+"");
            tbqtyet.setText(tbqty+"");

            return view2;
        }

    }


