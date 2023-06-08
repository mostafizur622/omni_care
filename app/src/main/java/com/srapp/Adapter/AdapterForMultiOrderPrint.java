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
                /*if (button.getText().toString().equalsIgnoreCase("Print")) {
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

                    }

                } else {
                    context.startActivity(new Intent(context, Dashboard.class));
                    button.setText("cancel");
                }*/
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

    private void showDetailsDailog(int position) {
        ditailslist.clear();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View vv = inflater.inflate(R.layout.dialog_for_details, null);
        TextView order_num = vv.findViewById(R.id.order_num);
        TextView status = vv.findViewById(R.id.status);
        listView = vv.findViewById(R.id.details);
        if (maplist.get(position).get("status").equalsIgnoreCase("1")) {
            status.setText("Success");
        } else if (maplist.get(position).get("status").equalsIgnoreCase("0")) {
            status.setText("Failed");
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mac", bf.getPreference("mac"));
            jsonObject.put(SR_ID, bf.getPreference(SR_ID));
            jsonObject.put("order_number", maplist.get(position).get("order_number"));
          //  bf.getResponceData(ORDER_DETAILS, jsonObject.toString(), 1005);

            ProgressDialog dailog = CheckConnection(context,"Get Order Details...");
            if (dailog==null)
                return;
            getJAPi().ORDER_DETAILS(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();
                        JSONArray jsonArray = new JSONArray();

                        jsonArray = jsonObject.getJSONArray("schedule_order_details");

                        for (int k = 0; k < jsonArray.length(); k++) {
                            HashMap<String, String> map = new HashMap<>();

                            map.put("product_name", jsonArray.getJSONObject(k).getString("product_name"));
                            map.put("order_qty", jsonArray.getJSONObject(k).getString("order_qty"));
                            map.put("invoice_qty", jsonArray.getJSONObject(k).getString("invoice_qty"));
                            map.put("status", jsonArray.getJSONObject(k).getString("status"));

                            ditailslist.add(map);

                        }
                        AdapterForOrderDetailsShedule adapter = new AdapterForOrderDetailsShedule(context, ditailslist);
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

    }

    boolean isInList(String orderNO) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(orderNO)) {
                return true;
            }
        }

        return false;
    }

    int isInMainList(String orderNO) {
        for (int i = 0; i < maplist.size(); i++) {
            if (maplist.get(i).get("order_number").equalsIgnoreCase(orderNO)) {

                return i;
            }

        }


        return -1;
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

    // Create image for printing Bangla----------------------------------------------------------------------------
    public void SaveClick() {
        for (int i = 0; i < list.size(); i++) {

            File file = saveBitMap(context, linear_saveimage, i);
            if (file != null) {
                Log.i("TAG", "Drawing saved to the gallery!");
            } else {
                Log.i("TAG", "Oops! Image could not be saved.");
            }
        }
    }

    private File saveBitMap(Context context, View drawView, int i) {

        File pictureFileDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));

        Log.d("imagepath", String.valueOf(pictureFileDir));

        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save the image");
            return null;
        }

        // String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".png";
        String filename = pictureFileDir.getPath() + File.separator + i + ".png";
        Log.d("imagename", filename);

        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = null;
            try {
                oStream = new FileOutputStream(pictureFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        /*int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());*/

        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(700, 1100, Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    // used for scanning gallery
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue scanning gallery.");
        }
    }

    public void MakePrintData() {

        TempData.TempGift = "";
        TempData.TempBonus_EN = "";

        String AllPrint = "";
        Data_Source db = new Data_Source(context);

        Log.e("List_data_print", new Gson().toJson(list));

        for (int i = 0; i < list.size(); i++) {

            Cursor c = db.rawQueryCoustom("SELECT order_number, order_date_time, outlet_id, gross_value, cash_received, credit_amount, market_id,discount_value,total_vat,total_discount FROM order_table where order_number=" + "'" + list.get(i) + "'");
            Log.e("Query", "SELECT order_number, order_date_time, outlet_id, gross_value, cash_received, credit_amount, market_id,discount_value,total_vat,total_discount FROM order_table where order_number=" + "'" + list.get(i) + "'");
            String memo_no = "";
            if (c != null && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {

                        memo_no = c.getString(0);
                        String date = c.getString(1);
                        String outlet_id = c.getString(2);
                        Double gross_value = c.getDouble(3);
                        Double cash_received = c.getDouble(4);
                        Double credit_amount = c.getDouble(5);
                        String market_id = c.getString(6);
                        TempData.DISCOUNT = c.getDouble(7);
                        TempData.VAT = c.getDouble(8);

                        Cursor c2 = db.rawQueryCoustom("SELECT O.outlet_name, M.market_name,O.address, O.mobile FROM outlets O LEFT JOIN markets M ON(O.market_id=M.market_id) WHERE O.outlet_id='" + outlet_id + "'");
                        if (c2 != null) {
                            if (c2.moveToFirst()) {
                                do {

                                    TempData.OutletName = c2.getString(0);
                                    TempData.tempMarket = c2.getString(1);
                                    TempData.Outlet_Address = c2.getString(2);
                                    TempData.Outlet_Mobile = c2.getString(3);

                                } while (c2.moveToNext());
                            }
                        }

                        Cursor c3 = db.rawQueryCoustom("SELECT T.thana_name FROM markets M LEFT JOIN thana T ON(M.thana_id=T.thana_id) WHERE M.market_id='" + market_id + "'");
                        if (c3 != null) {
                            if (c3.moveToFirst()) {
                                do {
                                    TempData.tempThana = c3.getString(0);

                                } while (c3.moveToNext());
                            }
                        }

                        Cursor c4 = db.rawQueryCoustom("SELECT OC.outlet_category_name FROM outlets O LEFT JOIN outlet_categories OC ON (O.outlet_category_id = OC.outlet_category_id) where O.outlet_id='" + outlet_id + "'");
                        if (c4.getCount() > 0) {
                            if (c4.moveToFirst()) {
                                do {

                                    String outlet_category_name = c4.getString(0);
                                    savePreference("OutletCategoryName", outlet_category_name);

                                } while (c4.moveToNext());
                            }
                        }

                        date = date.substring(0, date.length() - 3);

                        String MemoDetails = "SELECT MD.product_id,P.product_name ,MD.quantity, MD.price,MD.vat,MD.discount_type,MD.discount_amount,P.product_name_bangla FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='" + memo_no + "' and MD.product_type='0'";
                        Log.e("MemoDetails", "MemoDetails: " + MemoDetails);
                        discount_info = "";
                        discount_info_BN = "";
                        Cursor cursor = db.rawQueryCoustom(MemoDetails);

                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                do {
                                    discount_info = discount_info + getdiscount(cursor.getDouble(2), cursor.getDouble(3), cursor.getInt(5), cursor.getDouble(6), cursor.getString(1));
                                    discount_info_BN = discount_info_BN + getdiscountBN(cursor.getDouble(2), cursor.getDouble(3), cursor.getInt(5), cursor.getDouble(6), cursor.getString(7));

                                    String product_id = cursor.getString(0);
                                    String product_name = cursor.getString(1);
                                    String quantity = cursor.getString(2);
                                    String price = cursor.getString(3);
                                    String vat = cursor.getString(4);
                                    if (TextUtils.isEmpty(vat)) {
                                        vat = "0.0%";
                                    }

                                    String total_price = roundTwoDecimals(cursor.getDouble(2) * cursor.getDouble(3));

                                    Log.e("productname22", product_name);

                                    map = new HashMap<String, String>();

                                    map.put(PRODUCT_PRODUCT_NAME, product_name);
                                    map.put(PRODUCT_BOOLEAN_QUANTITY, quantity);
                                    map.put(PRODUCT_PRICE_PRICE, price);

                                    map.put("total_price", String.valueOf(total_price));
                                    map.put("vat", roundTwoDecimals(cursor.getDouble(4)) + "%");
                                    map.put(PRODUCT_ID, product_id);
                                    TempData.INVOICE_DETAILS_PRINT.add(map);

                                } while (cursor.moveToNext());
                            }
                        }

                        String Gift1 = "";
                        String MemoDetailsForGift = "SELECT MD.product_id,P.product_name ,MD.quantity FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='" + memo_no + "' and MD.product_type='1'";
                        Log.e("MemoDetailsForGift", "MemoDetailsForGift: " + MemoDetailsForGift);
                        Cursor cur2 = db.rawQueryCoustom(MemoDetailsForGift);
                        if (cur2 != null) {
                            if (cur2.moveToFirst()) {
                                do {
                                    String product_id = cur2.getString(0);
                                    String product_name = cur2.getString(1);
                                    String quantity = cur2.getString(2);
                                    String Gift2 = product_name + "(" + quantity + ")";
                                    Gift1 = Gift1 + "," + Gift2;
                                    String Gift = Gift1.substring(1);
                                    TempData.TempGift = Gift;

                                } while (cur2.moveToNext());
                            }
                        }

                        String Bonus1 = "", Bonus = "";
                        String Bonus1_BN = "", Bonus_BN = "";
                        String MemoDetailsForBonus = "SELECT MD.product_id,P.product_name ,MD.quantity , MD.measurement_unit_id  FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='" + memo_no + "' and MD.product_type='2'";
                        Log.e("MemoDetailsForBonus", "MemoDetailsForBonus: " + MemoDetailsForBonus);
                        Cursor cur = db.rawQueryCoustom(MemoDetailsForBonus);
                        if (cur != null) {
                            if (cur.moveToFirst()) {
                                do {
                                    String product_id = cur.getString(0);
                                    String product_name = cur.getString(1);
                                    String quantity = cur.getString(2);
                                    String Bonus2 = product_name + "(" + quantity + " " + getMeasurementUnitName(cur.getString(3),"unit_name") + ")";
                                    Bonus1 = Bonus1 + "," + Bonus2;
                                    Bonus = Bonus1.substring(1);

                                    String Bonus2BN = product_name + "(" + quantity + " " + getMeasurementUnitName(cur.getString(3),"unit_name_bangla") + ")";
                                    Bonus1_BN = Bonus1_BN + "," + Bonus2BN;
                                    Bonus_BN = Bonus1_BN.substring(1);
                                    TempData.TempBonus_EN = Bonus;
                                    TempData.TempBonus_BN = Bonus_BN;

                                } while (cur.moveToNext());
                            }
                        }

                        office_copy.setText("Office Copy");
                        area_office.setText("Area Office: " + getPreference("office_name") + getPreference("office_address") + ", " + getPreference("office_phone"));
                        outlet_name_category_address.setText(TempData.OutletName + " " + "(" + getPreference("OutletCategoryName") + ") ," + getPreference("Outletaddress  "));
                        market_thana.setText(TempData.tempMarket + ", " + TempData.tempThana);
                        if (MEMO_EDIT)
                            order_memo_no_date.setText("Memo# " + TempData.memoNumber + ", " + date);
                        else
                            order_memo_no_date.setText("Order# " + TempData.orderNumber + ", " + date);

                        sr_db_name.setText(getPreference("sr_name") + "  DB:(" + getPreference("db_name") + ")");
                        mobile_no.setText("Mobile No: " + getPreference("db_mobile"));
                        sr_address.setText("Address: " + getPreference("db_address"));

                        total_bill.setText("Total Bill:   " + roundTwoDecimals(gross_value));
                        discount.setText("Discount:   " + roundTwoDecimals(TempData.DISCOUNT));
                        vatCal.setText("Vat:   " + roundTwoDecimals(TempData.VAT));
                        net_payable.setText("Net-Payable:   " + roundTwoDecimals(gross_value - TempData.DISCOUNT));

                        if (TempData.TempGift != "Nill" && TempData.TempGift != "") {
                            gift.setText("Gift:" + TempData.TempGift);
                        }
                        if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus.equals(""))
                            bonus.setText("Bonus:" + TempData.TempBonus_EN);

                        if (TempData.TempBonus_EN.equals("") && TempData.TempExtraBonus != "")
                            bonus.setText("Bonus:" + TempData.TempExtraBonus);

                        if (!DetailsOrderReport.discount_info.equals("") && DetailsOrderReport.discount_info.length() > 0)
                            discountTxt.setText("Discount:" + DetailsOrderReport.discount_info);

                        if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus != "")
                            bonus.setText("Bonus:" + TempData.TempBonus_EN + "," + TempData.TempExtraBonus);

                        TempData.AllprintName = "AllprintName";
                        TempData.AllprintValue = AllPrint;

                        TempData.TempGift = "";
                        TempData.TempBonus_EN = "";
                        TempData.TempExtraBonus = "";

                    } while (c.moveToNext());
                }
            }
        }

        Log.e("List_data_print_", new Gson().toJson(TempData.INVOICE_DETAILS_PRINT));

        PrintRecyclerAdapter mAdapter = new PrintRecyclerAdapter(context, TempData.INVOICE_DETAILS_PRINT);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplication());
        Log.e("List_data_print__", new Gson().toJson(TempData.INVOICE_DETAILS_PRINT));

        printRecylerview.setLayoutManager(mLayoutManager);
        printRecylerview.setAdapter(mAdapter);

       /* try {
            SaveClick();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private String getMeasurementUnitName(String mesurement_unit_id,String column) {
        Data_Source db = new Data_Source(context);
        Cursor c = db.rawQueryCoustom("select "+column+" from unit where unit_id='" + mesurement_unit_id + "'");
        c.moveToFirst();
        if (c.getCount() > 0) {
            return c.getString(0);
        }
        return "Not specified";
    }

    private String getOutletAddress(String string) {
        Data_Source db = new Data_Source(context);
        Cursor c = db.rawQueryCoustom("select address from outlets where outlet_id='" + string + "'");

        if (c != null && c.getCount() > 0) {
            if (c.getString(0) != null && c.getString(0).equalsIgnoreCase("null"))
                return c.getString(0);
            else
                return "";

        }
        return "";
    }

    private String getdiscount(double qty, double price, int distype, double disamount, String product_name) {

        if (disamount <= 0) {
            return "";
        }

        Double discount = 0.0;

        Log.e("discount_type", distype + "");

        return product_name + "(" + (roundTwoDecimals(disamount * qty)) + ")";

    }

    private String getdiscountBN(double qty, double price, int distype, double disamount, String product_name) {

        if (disamount <= 0) {
            return "";
        }

        Double discount = 0.0;

        Log.e("discount_type", distype + "");

        return product_name + "(" + ConvertTOBangla(roundTwoDecimals(disamount * qty)) + ")";

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
