package com.srapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.srapp.SyncActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.PROCESSING_COMPELETE;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;

import static com.srapp.TempData.PROCESSING_ON_SERVER;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterForOrderProcessShow extends BaseAdapter implements BasicFunctionListener {

    //Declare Variables
    Activity context;
    ListView listView;
    boolean loaddeails=true;
    boolean is_true=false;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ditailslist = new ArrayList<HashMap<String, String>>();
    BasicFunction bf;
    Data_Source db;
    int flag=0;

    public AdapterForOrderProcessShow(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
        this.context = context;
        maplist = arraylistContent;
        bf = new BasicFunction(this,context);
        db = new Data_Source(context);
        insertinall();
    }

    public AdapterForOrderProcessShow(Activity context, ArrayList<HashMap<String, String>> arraylistContent,int flag) {
        this.context = context;
        maplist = arraylistContent;
        bf = new BasicFunction(this,context);
        db = new Data_Source(context);
        this.flag=flag;
        //-insertinall();
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
        final Button button = ((Activity)context).findViewById(R.id.processorder);
        final CheckBox bigcheck = ((Activity)context).findViewById(R.id.bigch);

        final HashMap<String, String> finalMapContent1 = mapContent;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!finalMapContent1.get("status").equalsIgnoreCase("2")&&flag==0) {
                    if (loaddeails)
                        showDetailsDailog(position);
                }

            }
        });

        if (mapContent.get("status").equalsIgnoreCase("0"))
            linearLayout.setBackgroundColor(Color.parseColor("#ede500"));



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
                if (button.getText().toString().equalsIgnoreCase("Process For Delivery")) {
                    button.setText("Back");
                    if (list.size() >= 1) {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < list.size(); i++) {
                            jsonArray.put(list.get(i));
                            //db.updateOrderStatus(list.get(i),PROCESSING_ON_SERVER+"");

                        }

                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("order_numbers", jsonArray);
                            jsonObject.put("mac", bf.getPreference("mac"));
                            jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                          //  bf.getResponceData(URL.PROCESS_ORDER_LIST, jsonObject.toString(), 1001);

                            ProgressDialog dailog = CheckConnection(context,"Getting Orders...");
                            if (dailog==null)
                                return;
                            getJAPi().PROCESS_ORDER_LIST(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        dailog.dismiss();
                                        JSONArray jsonArray = jsonObject.getJSONArray("schedule");
                                        for (int j = 0; j < jsonArray.length(); j++) {
                                            if (isInMainList(jsonArray.getJSONObject(j).getString("dist_order_no")) >= 0)
                                                maplist.get(isInMainList(jsonArray.getJSONObject(j).getString("dist_order_no"))).put("status", jsonArray.getJSONObject(j).getString("status"));

                                            Log.e("dialog_for_details", isInMainList(jsonArray.getJSONObject(j).getString("dist_order_no")) + " ");
                                            if (jsonArray.getJSONObject(j).getString("status").equalsIgnoreCase("1")) {
                                                db.updateOrderStatus(jsonArray.getJSONObject(j).getString("dist_order_no"),PROCESSING_COMPELETE+"");
                                            }
                                        }
                                        notifyDataSetChanged();

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    dailog.dismiss();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("error",e.getMessage());
                        }
                    } else {

                        Toast.makeText(context, "Please select At least 1 order", Toast.LENGTH_LONG).show();
                        button.setText("Process For Delivery");
                    }
                }else if (button.getText().toString().equalsIgnoreCase("Cancel Orders")){
                    button.setText("back");
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
                          //  bf.getResponceData(URL.CANCEL_ORDER_LIST, jsonObject.toString(), 1002);

                            ProgressDialog dailog = CheckConnection(context,"Cancel Order List...");
                            if (dailog==null)
                                return;
                            getJAPi().CANCEL_ORDER_LIST(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        dailog.dismiss();
                                        if (jsonObject.getJSONObject("schedule").getString("status").equalsIgnoreCase("1")) {
                                            for (int j = 0; j < list.size(); j++) {
                                                db.excQuery("delete from ORDER_table where order_number='" + list.get(j) + "'");
                                                db.excQuery("delete from ORDER_DETAILS where order_number='" + list.get(j) + "'");

                                            }
                                        }

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    dailog.dismiss();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("error",e.getMessage());
                        }
                    } else {

                        Toast.makeText(context, "Please select At least 1 order", Toast.LENGTH_LONG).show();
                        button.setText("Cancel Orders");
                    }

                }   else
                 {
                    context.startActivity(new Intent(context, Dashboard.class));

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
        loaddeails= false;
        ditailslist.clear();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View vv = inflater.inflate(R.layout.dialog_for_details, null);
        TextView order_num = vv.findViewById(R.id.order_num);
        TextView status = vv.findViewById(R.id.status);
        Button close = vv.findViewById(R.id.close);
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
            //bf.getResponceData(ORDER_DETAILS,jsonObject.toString(),1005);

            ProgressDialog dailog = CheckConnection(context,"Order Details Loading...");
            if (dailog==null)
                return;
            getJAPi().ORDER_DETAILS(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();
                        loaddeails = true;

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



                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dailog.dismiss();
                }
            });


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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });



    }


    void insertinall(){
        final CheckBox bigcheck = ((Activity)context).findViewById(R.id.bigch);
        for (int i=0; i<maplist.size(); i++) {

            if (!isInList(maplist.get(i).get("order_number"))) {
                list.add(maplist.get(i).get("order_number"));
            }
        }
        bigcheck.setChecked(true);
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

    }

    @Override
    public void OnConnetivityError() {

    }
}
