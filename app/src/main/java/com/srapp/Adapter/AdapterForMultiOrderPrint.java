package com.srapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.google.gson.Gson;
import com.srapp.Dashboard;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.DetailsOrderReport;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.print.PrintRecyclerAdapter;
import com.srapp.print.PrintSelectedOrdersActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.PROCESSING_PENDING;
import static com.srapp.Db_Actions.Tables.PRODUCT_BOOLEAN_QUANTITY;
import static com.srapp.Db_Actions.Tables.PRODUCT_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRICE_PRICE;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;
import static com.srapp.DetailsOrderReport.discount_info;
import static com.srapp.DetailsOrderReport.discount_info_BN;
import static com.srapp.TempData.ConvertTOBangla;
import static com.srapp.TempData.MEMO_EDIT;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterForMultiOrderPrint extends BaseAdapter implements BasicFunctionListener {

    //Declare Variables
    Activity context;
    ListView listView;
    boolean is_true = false;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ditailslist = new ArrayList<HashMap<String, String>>();
    BasicFunction bf;
    Data_Source db;

    LinearLayout linear_saveimage;
    HashMap<String, String> map;
    TextView area_office, outlet_name_category_address, market_thana, order_memo_no_date;
    TextView discountTxt, bonus, gift, total_bill, discount, vatCal, net_payable, sr_db_name, mobile_no, sr_address, office_copy;
    public RecyclerView printRecylerview;

   // private printRecyclerAdapter mAdapter;

    public AdapterForMultiOrderPrint(Activity context, ArrayList<HashMap<String, String>> arraylistContent) {
        this.context = context;
        maplist = arraylistContent;
        bf = new BasicFunction(this, context);
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
        TextView date = (TextView) view2.findViewById(R.id.date);
        LinearLayout linearLayout = view2.findViewById(R.id.linlay);
        linear_saveimage = view2.findViewById(R.id.linear_saveimage);
        TextView outlet = (TextView) view2.findViewById(R.id.outlet);
        TextView amount = (TextView) view2.findViewById(R.id.amount);

        //----------------------------------------------------------------------------------------
        printRecylerview = view2.findViewById(R.id.product_list_recycler_view);

        area_office = view2.findViewById(R.id.area_office);
        outlet_name_category_address = view2.findViewById(R.id.outlet_name_category_address);
        market_thana = view2.findViewById(R.id.market_thana);
        order_memo_no_date = view2.findViewById(R.id.order_memo_no_date);

        discountTxt = view2.findViewById(R.id.discountTxt);
        bonus = view2.findViewById(R.id.bonus);
        total_bill = view2.findViewById(R.id.total_bill);
        discount = view2.findViewById(R.id.discount);
        vatCal = view2.findViewById(R.id.vatCal);
        net_payable = view2.findViewById(R.id.net_payable);
        sr_db_name = view2.findViewById(R.id.sr_db_name);
        sr_address = view2.findViewById(R.id.sales_officer_address);
        mobile_no = view2.findViewById(R.id.sales_officer_phone);
        office_copy = view2.findViewById(R.id.office_copy);

        final Button button = ((Activity) context).findViewById(R.id.processorder);
        button.setText("Print");
        final CheckBox bigcheck = ((Activity) context).findViewById(R.id.bigch);

        final HashMap<String, String> finalMapContent1 = mapContent;

        date.setText(mapContent.get("order_date"));
        outlet.setText(mapContent.get("outlet_name"));
        amount.setText(mapContent.get("gross_value"));
        CheckBox checkBox1 = (CheckBox) view2.findViewById(R.id.checkbox);

        checkBox1.setChecked(isInList(mapContent.get("order_number")));

        final HashMap<String, String> finalMapContent = mapContent;

        checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {

            Log.e("isChecked", isChecked + "");

            //linear_saveimage.setVisibility(View.VISIBLE);

            if (isChecked) {

                list.add(finalMapContent.get("order_number"));

                /*MakePrintData();

                SaveClick();*/

                if (list.size() == maplist.size()) {
                    bigcheck.setChecked(true);
                }

                //Log.e("hello",list.get(list.size()-1));

            } else {
                linear_saveimage.setVisibility(View.GONE);

                list.remove(finalMapContent.get("order_number"));
                if (list.size() < maplist.size()) {
                    is_true = true;
                    bigcheck.setChecked(false);
                }

                // Log.e("hello",list.get(list.size()-1));
            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (button.getText().toString().equalsIgnoreCase("Print")) {
                    button.setText("OK");

                    UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(context);
                    UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

                    if (usbConnection == null || usbManager == null) {
                        new AlertDialog.Builder(context)
                                .setTitle("USB Connection")
                                .setMessage("No USB printer found.")
                                .show();
                    }
                    else {
                        if (list.size() >= 1) {
                            Log.e("list_size", list.size() + "");
                            Intent idn = new Intent(context, PrintSelectedOrdersActivity.class);
                            idn.putExtra("FromDate", list);
                            context.startActivity(idn);
                            context.finish();

                            Log.e("ListData", Arrays.deepToString(list.toArray()));

                        } else {
                            Toast.makeText(context, "Please select At least 1 order", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
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
                } else {
                    if (!is_true) {
                        list.clear();

                    }

                    is_true = false;
                }

                notifyDataSetChanged();
            }
        });

        return view2;
    }



    boolean isInList(String orderNO) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(orderNO)) {
                return true;
            }
        }

        return false;
    }



    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {

            if (i == 1001) {

                //JSONObject jsonArray = jsonObject.getJSONObject("schedule");
                for (int j = 0; j < list.size(); j++) {

                    db.updateOrderStatus(list.get(j), PROCESSING_PENDING + "");

                }
                notifyDataSetChanged();


            } else if (i == 1005) {


            }


    }

    @Override
    public void OnConnetivityError() {

    }




    public String roundTwoDecimals(double d) {

        Log.e("Double:", String.valueOf(d));
        Log.e("Modulus:", String.valueOf(d % 1));
        return String.format("%.2f", d);
    }

    public void savePreference(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key) {
        String value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        value = prefs.getString(key, "0");

        return value;

    }

}
