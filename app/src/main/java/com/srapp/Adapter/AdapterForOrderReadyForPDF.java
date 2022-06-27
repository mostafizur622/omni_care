package com.srapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Dashboard;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.URL;
import com.srapp.R;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.ORDER_DETAILS;

public class AdapterForOrderReadyForPDF extends BaseAdapter implements BasicFunctionListener {


    Activity context;
    ListView listView;
    boolean is_true=false;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ditailslist = new ArrayList<HashMap<String, String>>();
    BasicFunction bf;
    Data_Source db;
    public AdapterForOrderReadyForPDF(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
        this.context = context;
        maplist = arraylistContent;
        bf = new BasicFunction(this,context);
        db = new Data_Source(context);
    }

    @Override
    public int getCount() {
        return maplist.size();
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

        HashMap<String, String> mapContent = new HashMap<String, String>();
        mapContent = maplist.get(position);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_process_row, null);
        TextView date = (TextView)view2.findViewById(R.id.date);
        LinearLayout linearLayout = view2.findViewById(R.id.linlay);
        TextView outlet = (TextView)view2.findViewById(R.id.outlet);
        TextView amount = (TextView)view2.findViewById(R.id.amount);
        final Button button = ((Activity)context).findViewById(R.id.generatePdf);
        final CheckBox bigcheck = ((Activity)context).findViewById(R.id.bigch);

        final HashMap<String, String> finalMapContent1 = mapContent;
       /* linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!finalMapContent1.get("status").equalsIgnoreCase("2"))

                showDetailsDailog(position);

            }
        });*/

      /*  if (mapContent.get("status").equalsIgnoreCase("1"))
            linearLa  yout.setBackgroundColor(Color.parseColor("#0aab55"));
        else if (mapContent.get("status").equalsIgnoreCase("0"))
            linearLayout.setBackgroundColor(Color.parseColor("#ede500"));*/


        date.setText(mapContent.get("order_date"));
        outlet.setText(mapContent.get("outlet_name"));
        amount.setText(mapContent.get("gross_value"));
        CheckBox checkBox1 = (CheckBox)view2.findViewById(R.id.checkbox);

        checkBox1.setChecked(isInList(mapContent.get("order_number")));

        final HashMap<String, String> finalMapContent = mapContent;
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("isChecked",isChecked+"");
                if (isChecked){

                    list.add(finalMapContent.get("order_number"));
                    if (list.size()==maplist.size()){

                        bigcheck.setChecked(true);
                    }
                    //Log.e("hello",list.get(list.size()-1));
                }else {

                    list.remove(finalMapContent.get("order_number"));
                    if (list.size()<maplist.size()){
                        is_true=true;
                        bigcheck.setChecked(false);

                    }


                    // Log.e("hello",list.get(list.size()-1));
                }


            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().toString().equalsIgnoreCase("Generate PDF")) {
                    button.setText("OK");
                    if (list.size() >= 1) {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < list.size(); i++) {
                            jsonArray.put(list.get(i));

                        }

                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("order_numbers", jsonArray);
                            jsonObject.put("mac", bf.getPreference("mac"));
                            jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                            bf.getResponceData(URL.MAKE_PFD, jsonObject.toString(), 1001);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        Toast.makeText(context, "Please select At least 1 order", Toast.LENGTH_LONG).show();
                    }
                }else {
                    context.startActivity(new Intent(context, Dashboard.class));
                    button.setText("cancel");
                }


            }
        });

        bigcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    list.clear();
                    for (int i = 0; i < maplist.size(); i++) {

                        list.add(maplist.get(i).get("order_number"));
                    }
                }else {
                    if (!is_true) {
                        list.clear();

                    }

                    is_true=false;
                }

                notifyDataSetChanged();
            }
        });


        return view2;
    }

    private void showDetailsDailog(int position) {
        ditailslist.clear();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View vv = inflater.inflate(R.layout.dialog_for_details, null);
        TextView order_num = vv.findViewById(R.id.order_num);
        TextView status = vv.findViewById(R.id.status);
        listView = vv.findViewById(R.id.details);
        if (maplist.get(position).get("status").equalsIgnoreCase("1")){
            status.setText("Success");
        }else if (maplist.get(position).get("status").equalsIgnoreCase("0")){
            status.setText("Failed");
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mac",bf.getPreference("mac"));
            jsonObject.put(SR_ID,bf.getPreference(SR_ID));
            jsonObject.put("order_number",maplist.get(position).get("order_number"));
            bf.getResponceData(ORDER_DETAILS,jsonObject.toString(),1005);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        order_num.setText(maplist.get(position).get("order_number"));

        final AlertDialog.Builder alert = new AlertDialog.Builder(
                context);
        alert.setView(vv);
        //alert.setCancelable(false);


        final AlertDialog dialog = alert.create();
        dialog.show();



    }

    boolean isInList(String orderNO){
        for (int i = 0 ; i<list.size(); i++){
            if (list.get(i).equalsIgnoreCase(orderNO)){

                return true;
            }

        }

        return false;
    }


    int isInMainList(String orderNO){
        for (int i = 0; i< maplist.size(); i++){
            if (maplist.get(i).get("order_number").equalsIgnoreCase(orderNO)){

                return i;
            }

        }


        return -1;
    }


    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        try {
            if (i==1001) {

                //JSONObject jsonArray = jsonObject.getJSONObject("schedule");

                Log.e("Link",jsonObject.getJSONObject("outlet").getString("link"));
                DownloadManager mManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request mRqRequest = new DownloadManager.Request(
                        Uri.parse(jsonObject.getJSONObject("outlet").getString("link")));
                mRqRequest.setDescription("File Downloading");
                mRqRequest.setDestinationInExternalPublicDir("/SR_App_Invoice_Documents","SR_App(Invoice)+"+bf.getCurrentDateTime()+".pdf");
//  mRqRequest.setDestinationUri(Uri.parse("give your local path"));
                mRqRequest.allowScanningByMediaScanner();
                mRqRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                long idDownLoad = mManager.enqueue(mRqRequest);

              /*  for (int j = 0; j < list.size(); j++) {

                    db.updateOrderStatus(list.get(j),PROCESSING_PENDING+"");

                }*/
                notifyDataSetChanged();


            }else if(i==1005){

                JSONArray jsonArray = new JSONArray();

                jsonArray = jsonObject.getJSONArray("schedule_order_details");

                for (int k = 0 ; k<jsonArray.length() ; k++){
                    HashMap<String,String> map = new HashMap<>();

                    map.put("product_name",jsonArray.getJSONObject(k).getString("product_name"));
                    map.put("order_qty",jsonArray.getJSONObject(k).getString("order_qty"));
                    map.put("invoice_qty",jsonArray.getJSONObject(k).getString("invoice_qty"));
                    map.put("status",jsonArray.getJSONObject(k).getString("status"));

                    ditailslist.add(map);


                }


                AdapterForOrderDetailsShedule adapter = new AdapterForOrderDetailsShedule(context,ditailslist);
                listView.setAdapter(adapter);


            }



        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("log", e.getMessage());
        }
    }

    @Override
    public void OnConnetivityError() {

    }
}
