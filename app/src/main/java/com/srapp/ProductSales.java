package com.srapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.srapp.Adapter.SalesOrderDetailsAdaper1;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.OrderDetailsModel;
import com.srapp.Util.Parent;
import com.srapp.print.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.srapp.Db_Actions.Tables.MEMOS;
import static com.srapp.Db_Actions.Tables.MEMOS_latitude;
import static com.srapp.Db_Actions.Tables.MEMOS_longitude;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_number;
import static com.srapp.Db_Actions.Tables.MEMO_DETAILS;
import static com.srapp.Db_Actions.Tables.MESSAGE_UPDATED_AT;
import static com.srapp.Db_Actions.Tables.NCP_RETURN_CREATE_AT;
import static com.srapp.Db_Actions.Tables.ORDER_DETAILS_is_bonus;
import static com.srapp.Db_Actions.Tables.ORDER_END_TIME;
import static com.srapp.Db_Actions.Tables.ORDER_START_TIME;
import static com.srapp.Db_Actions.Tables.ORDER_order_number;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_GIFT_ISSUE;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MEMOS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MEMO_DETAILS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER_DETAILS;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;
import static com.srapp.TempData.BPSelected_bonus;
import static com.srapp.TempData.BPSelected_product;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.ORDER_TO_MEMO;
import static com.srapp.TempData.combination_idMap;
import static com.srapp.TempData.discountmap;
import static com.srapp.TempData.discountoffer;
import static com.srapp.TempData.discountofferPolicyid;
import static com.srapp.TempData.discounttype;
import static com.srapp.TempData.editMemo;
import static com.srapp.TempData.policyMap;
import static com.srapp.TempData.price_idMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductSales extends Parent implements BasicFunctionListener, DBListener {

    private static final int REQUEST_LOCATION = 1;
    ImageView imageView;
    ListView listView;
    OrderDetailsModel orderDetailsModel;
    ArrayList<HashMap<String, String>> productList;
    ArrayList<HashMap<String, String>> Data = new ArrayList<HashMap<String, String>>();
    int urlcall;
    //SalesOrderDetailsAdaper Adapter;
    SalesOrderDetailsAdaper1 Adapter;
    ListView list_add_vehicle_details;
    ImageView homeBtn, backBtn;
    Button SaveBtn;
    TextView TotalPrice, discount, total_price, CreditTv, txtGift, txtBonus, txtBonusExtra, vattv, policy_tag;
    ArrayList<HashMap<String, String>> product_history;
    ArrayList<Integer> num = new ArrayList<Integer>();
    String price = "";
    String orderNo = "", lattitude, longitude;
    String memoNo = "";
    String FromApp = "";
    String for_memo_delete = "";
    String OrderDate = "";
    String orderdateTime = "";
    TextView button_with_icon;
    String date = "";
    BasicFunction bf;
    String editableAllow = "0";
    Data_Source db;
    ArrayList<HashMap<String, String>> ItemListFromDB = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> GiftItem = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> Bonus = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> orderList = new ArrayList<HashMap<String, String>>();
    TextView addBonusBtn, ddate;
    LocationManager locationManager;
    String is_out_of_plan = "0", plan_id = "0";
    String lat = "", lng = "";

    private DatePickerDialog CurrentDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private int year1;
    private int month1;
    private int day1;
    private String ForEditMeno;
    private String memodate;

    public static String hexDump(byte[] data, String encoding) {
        StringBuilder sb = new StringBuilder();

        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }

        final int bytesPerLine = 16;

        int i = 0;
        for (i = 0; i < data.length; i++) {
            if (i % bytesPerLine == 0) {
                if (i > 0) {
                    sb.append("  ");
                    try {
                        sb.append(new String(data, i - bytesPerLine, bytesPerLine, encoding));
                    } catch (UnsupportedEncodingException e) {

                    }
                    sb.append("\n");
                }
                sb.append(String.format("%08x:", i));
            } else if (i % 4 == 0) {
                sb.append(' ');
            }

            sb.append(String.format(" %02x", data[i]));
        }

//		    Log.v(Constants.LOG_TAG, "data size: " + data.length + ", dumped size: " + i);

        if (i % bytesPerLine != 0) {
            if (i / bytesPerLine > 0) {
                for (int j = i; j % bytesPerLine != 0; j++) {
                    if (j % 4 == 0) {
                        sb.append(" ");
                    }
                    sb.append("   ");
                }
            }
            sb.append("  ");
            try {
                sb.append(new String(data, i - i % bytesPerLine, i % bytesPerLine, encoding));
            } catch (UnsupportedEncodingException e) {

            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_sales);
        db = new Data_Source(this, this, this);
        bf = new BasicFunction(this, this);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            ForEditMeno = b.getString("ForEditMeno");
        }

        if (TempData.editMemo.equalsIgnoreCase("true")) {
            date = TempData.MemoDateTime;
        } else {
            date = getCurrentDate();
        }

        Log.e("getPreference(ORDER_START_TIME)",getPreference(ORDER_START_TIME));

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        policy_tag = findViewById(R.id.policy_tag);

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProductSales.this, Dashboard.class));
            finish();
        });

        backBtn.setOnClickListener(v -> {
            if (TempData.DELIVERY_EDIT){
                startActivity(new Intent(ProductSales.this, DeliveryReport.class));
                finish();
                return;
            }
            if (MEMO_EDIT){
                startActivity(new Intent(ProductSales.this, MemoReport.class));
                finish();
                return;
            }
            startActivity(new Intent(ProductSales.this, Sales_Memo.class));
            finish();

        });
        TextView user_txt_view;
        user_txt_view = findViewById(R.id.user_txt_view);
        user_txt_view.setText(bf.getPreference(SR_ID));
        if (!editMemo.equalsIgnoreCase("true"))
            orderdata();


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check gps is enable or not

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            OnGPS();
        } else {
            //GPS is already On then
            getLocation();
            Log.e("Map1", "onCreate: " + "Map1 started for lat and long" + lattitude);
        }
        Log.e("Date:", String.valueOf(date));
        Log.e("Date:", TempData.orderNumber + " no");
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Date datevalue = null;
        try {
            datevalue = simpleDateFormat.parse(String.valueOf(date));

            simpleDateFormat.applyPattern(pattern);
            memodate = simpleDateFormat.format(datevalue);

            //memodate = datevalue.toString();
            Log.e("MemoDate:", String.valueOf(memodate));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / 15;


        Log.e("INSTRITUTE-ID", "..........." + TempData.InstituteID);


        TempData.INVOICE_DETAILS.clear();
        list_add_vehicle_details = findViewById(R.id.product_sales_rec);

        txtGift = findViewById(R.id.txtGift);
        txtBonus = findViewById(R.id.txtBonus);
        txtBonusExtra = findViewById(R.id.txtBonusExtra);

        LinearLayout BonusLayout = findViewById(R.id.BonusLayout);
        addBonusBtn = findViewById(R.id.addBonusBtn);
        button_with_icon = findViewById(R.id.button_with_icon);
        button_with_icon.setText(DateFormatedConverter(getCurrentDate()));

        ddate = findViewById(R.id.ddate);

        setDeliveryDate();


        TotalPrice = findViewById(R.id.sub_total);
        discount = findViewById(R.id.discount);
        total_price = findViewById(R.id.total_price);
        vattv = findViewById(R.id.vat);


        SaveBtn = findViewById(R.id.SaveButton);

        //ddate.setText(db.getNextVisitday(bf.getPreference("thanaID")));


        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        BonusButtonShow();

        ProduSctDisplayListView();

        bonus_show_WithoutDB();


        addBonusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent idn = new Intent(ProductSales.this, BonusProductListActivity.class);
                startActivity(idn);
//				finish();
            }
        });
        policy_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent idn = new Intent(ProductSales.this, BonusPolicySet.class);
                startActivity(idn);
//				finish();
            }
        });


        BonusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  TempData.TempBonus=txtBonus.getText().toString();
                TempData.TempExtraBonus = txtBonus.getText().toString();
                Log.e("++++++", "TempData.TempBonus: " + TempData.TempBonus_EN);
                Log.e("++++++", "TempData.TempExtraBonus: " + TempData.TempExtraBonus);
                Log.e("++++++", "TempData.BonusShowList " + TempData.BonusShowList);

                Intent idn = new Intent(ProductSales.this, com.srapp.BonusShowListActivity.class);
                startActivity(idn);

            }
        });

       /* DraftPrintButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Cursor c2 = db.rawQuery("SELECT * FROM product ORDER BY product_order ASC");
                if (c2 != null) {
                    if (c2.moveToFirst()) {
                        do {

                            String product_id = c2.getString(c2.getColumnIndex("product_id"));
                            savePreference(product_id, "0.0");

                        } while (c2.moveToNext());
                    }
                }

                if(!TotalPrice.getText().toString().equals("0.00"))
                {
                    bonus_Draft_WithoutDB();
                    DaftPrintDataToTable();

                } }
        });*/


        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  SavePrintButton.setEnabled(false);
                if (SalesOrderDetailsAdaper1.not_bonus==1){
                    Toast.makeText(getApplicationContext(), "This Order/Memo Doesn't meet Bonus Policy!", Toast.LENGTH_LONG).show();
                    return;
                }
                SaveBtn.setEnabled(false);
                Cursor c1 = db.rawQueryCoustom("SELECT * FROM memos where from_app='1' order by _id DESC limit 1");
                c1.moveToFirst();
                if (c1.getCount() > 0) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date date = null;
                    try {
                        date = format.parse(c1.getString(c1.getColumnIndex("memo_date")));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar timetcal = Calendar.getInstance();

                    Date currentDate = timetcal.getTime();

                    Log.e("date", String.valueOf(date));
                    if (TempData.editMemo.equalsIgnoreCase("false")) {

                        SaveBtn.setEnabled(false);

                        if (!currentDate.after(date)) {
                            Log.e("date", c1.getString(c1.getColumnIndex("memo_date")));
                            Toast.makeText(ProductSales.this, "Invalid Date", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                Cursor c2 = db.rawQueryCoustom("SELECT * FROM product ORDER BY product_order ASC");
                if (c2 != null) {
                    if (c2.moveToFirst()) {
                        do {

                            String product_id = c2.getString(c2.getColumnIndex("product_id"));
                            savePreference(product_id, "0.0");

                        } while (c2.moveToNext());
                    }
                }

                /*  Log.e("PaymmentText",""+etPayment.getText().toString());*/


                if (EmptyPaymentValidation()) {
                    if (TempData.editMemo.equalsIgnoreCase("true"))
                        AlertDialog();
                    else {
                        if (injectable_product_check().equalsIgnoreCase("0")) {
                            if (TempData.editMemo.equalsIgnoreCase("true"))
                                AlertDialog();
                            else {
                                if (!TotalPrice.getText().toString().equals("0.00")) {

                                    new SaveData().execute();
                                }
                            }

                        } else if (injectable_product_check().equalsIgnoreCase("1") && getPreference("Is_WithinGroup").equalsIgnoreCase("1")) {
                            if (TempData.editMemo.equalsIgnoreCase("true"))
                                AlertDialog();
                            else {
                                if (!TotalPrice.getText().toString().equals("0.00")) {

                                    new SaveData().execute();
                                }
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Not Saleable Product!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

    }

    private void getplansdetails() {


    }

    private void setDeliveryDate() {
        boolean loop = true;
        int current_salesWeek = getWeekId();
        int[] weekid = {1, 2, 3, 4};

        int[] days_of_week = {1, 2, 3, 4, 5, 6, 7};
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e("dayOfWeek", dayOfWeek + "");
        for (int i = current_salesWeek; i <= weekid.length; i++) {


            Cursor c = db.rawQueryCoustom("select * from visit_list where route_id='" + bf.getPreference("thanaID") + "' and week_id='" + (i) + "'");
            Log.e("Queryt" + i, "select * from visit_list where route_id='" + bf.getPreference("thanaID") + "' and week_id='" + (i) + "' " + c.getCount());
            c.moveToFirst();
            if (c != null && c.getCount() > 0) {
                int j = 0;
                if (i == current_salesWeek) {
                    j = dayOfWeek;
                    Log.e("J", j + "");
                }
                for (; j < days_of_week.length; j++) {
                    if (i == current_salesWeek && dayOfWeek == days_of_week[j]) {
                        continue;
                    }
                    Log.e("J+1", j + "");
                    if (c.getInt(days_of_week[j] + 1) == 1) {
                        calculateDate(i, days_of_week[j], current_salesWeek);
                        break;


                    }

                }
                break;
            }

            if (i == 4 && loop) {
                i = 0;
                loop = false;
            }

        }


    }

    private void calculateDate(int week_id, int day_of_week, int current_sales_week) {

        Log.e("week_id", week_id + " ," + day_of_week + " ," + current_sales_week);

        Calendar c = Calendar.getInstance();
        if (week_id < current_sales_week)
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        if (c.get(Calendar.MONTH) > 11) {
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
        }

        c.set(Calendar.DAY_OF_WEEK, day_of_week);
        c.set(Calendar.WEEK_OF_MONTH, week_id);
        Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        Log.e("Calender", sdf.format(date));
        ddate.setText(sdf.format(date));

    }

    void orderdata() {
        Calendar cal = Calendar.getInstance();
        cal.get(Calendar.DAY_OF_WEEK);
        Cursor c = db.rawQueryCoustom("select * from visit_list where route_id='" + bf.getPreference("thanaID") + "' and week_id='" + getWeekId() + "'");
        c.moveToFirst();
        Log.e("datofweek", cal.get(Calendar.DAY_OF_WEEK) + "  " + "select * from visit_list where route_id='" + bf.getPreference("thanaID") + "' and week_id='" + getWeekId() + "'");
        if (c != null & c.getCount() > 0) {

            Log.e("test", cal.get(Calendar.DAY_OF_WEEK) + 1 + " 0");
            if (c.getString(cal.get(Calendar.DAY_OF_WEEK) + 1).equalsIgnoreCase("1")) {

                is_out_of_plan = "1";
                plan_id = c.getString(1);
            }

        }

    }

    private int getWeekId() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int weekNumber = calendar.get(Calendar.WEEK_OF_MONTH) - 1;

        if (day <= 7) {
            weekNumber = 1;
        } else if (day > 7 && day <= 13) {
            weekNumber = 2;
        } else if (day > 13 && day <= 20) {
            weekNumber = 3;
        } else {
            weekNumber = 4;
        }

        return weekNumber;
    }

    private int getstartdate(int weekNumber) {

        int day;

        if (weekNumber == 1) {
            day = 1;
        } else if (weekNumber == 2) {
            day = 8;
        } else if (weekNumber == 3) {
            day = 14;
        } else {
            day = 21;
        }

        return day;
    }

    @Override
    public void OnLocalDBdataRetrive(final String json) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (urlcall == 102) {
                   // bf.getResponceData(URL.CREATE_MEMO, json, 102);

                    ProgressDialog dailog = CheckConnection(ProductSales.this,"Creating Memo...");
                    if (dailog==null)
                        return;
                    try {
                        getJAPi().CREATE_MEMO(convertTORequestdata(new JSONObject(json))).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                    if (jsonObject.has("NAME"))
                                        if (jsonObject.getString("NAME").equalsIgnoreCase("TANVIR")) {
                                            db.excQuery("delete from temp_memos");
                                            db.excQuery("delete from temp_memo_details");
                                            db.excQuery("update ORDER_table set is_complete='1' , status = '2' WHERE order_number = '" + orderNo + "'");
                                            Toast.makeText(ProductSales.this, "Memo Create Successfully ", Toast.LENGTH_LONG).show();
                                            SaveBtn.setEnabled(true);
                                            // return;
                                        }
                                    if (MEMO_EDIT) {
                                        startActivity(new Intent(ProductSales.this, MemoReport.class));
                                        finish();
                                        Log.e("memoredirect", "MemoReport");
                                    } else if (ORDER_TO_MEMO == 1) {

                                        startActivity(new Intent(ProductSales.this, DeliveryReport.class));
                                        finish();
                                        Log.e("memoredirect", "Order_Report_Activity");
                                    }

                                    if (jsonObject.getJSONObject("memo").getString("status").equalsIgnoreCase("1")) {
                                        db.excQuery("update ORDER_table set is_complete='1' , status = '2' WHERE order_number = '" + orderNo + "'");
                                        saveMemos();
                                    } else {

                                        db.excQuery("delete from temp_memos");
                                        db.excQuery("delete from temp_memo_details");
                                        Toast.makeText(ProductSales.this, jsonObject.getJSONObject("memo").getString("message"), Toast.LENGTH_LONG).show();
                                        SaveBtn.setEnabled(true);

                                    }


                                    Log.e("json", jsonObject.toString());
                                    if (MEMO_EDIT) {
                                        Toast.makeText(ProductSales.this, jsonObject.getJSONObject("memo").getString("message"), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(ProductSales.this, MemoReport.class));
                                        finish();
                                        Log.e("memoredirect", "MemoReport");
                                    } else if (ORDER_TO_MEMO == 1) {
                                        Toast.makeText(ProductSales.this, jsonObject.getJSONObject("memo").getString("message"), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(ProductSales.this, DeliveryReport.class));
                                        finish();
                                        Log.e("memoredirect", "Order_Report_Activity");
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
                        throw new RuntimeException(e);
                    }

                } else{
                   // bf.getResponceData(URL.ORDERPUSH, json, 101);

                    ProgressDialog dailog = CheckConnection(ProductSales.this,"Updating Memo...");
                    if (dailog==null)
                        return;
                    try {
                        getJAPi().ORDERPUSH(convertTORequestdata(new JSONObject(json))).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                    Toast.makeText(ProductSales.this, jsonObject.getJSONObject("order").getString("message"), Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ProductSales.this, Order_Report_Activity.class));
                                    Log.e("memoredirect", "DeliveryReport");
                                    finishAffinity();

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
                        throw new RuntimeException(e);
                    }
                }

            }
        });

    }

    @Override
    public void OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList) {

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {


    }

    private void saveMemos() {
        if (MEMO_EDIT) {
            orderNo = TempData.orderNumber;
            db.excQuery("DELETE FROM " + TABLE_NAME_MEMOS + " WHERE " + MEMOS_memo_number + "='" + TempData.memoNumber + "'");
            db.excQuery("DELETE FROM " + TABLE_NAME_MEMO_DETAILS + " WHERE " + MEMOS_memo_number + "='" + TempData.memoNumber + "'");
            db.excQuery("DELETE  FROM gift_issue WHERE gift_issue_id='" + TempData.orderNumber + "'");
            db.excQuery("DELETE  FROM gift_issue_details WHERE gift_issue_id='" + TempData.orderNumber + "'");
        }

        try {
            Cursor c1 = db.rawQueryCoustom("SELECT * FROM temp_memos  WHERE " + MEMOS_memo_number + "='" + TempData.memoNumber + "'");
            Log.e("query__saveMemos", "SELECT * FROM temp_memos  WHERE " + MEMOS_memo_number + "='" + TempData.memoNumber + "'");
            c1.moveToFirst();
            if (c1 != null && c1.getCount() > 0) {

                HashMap<String, String> map = new HashMap<>();
                for (int i = 2; i < MEMOS.length - 2; i++) {

                    map.put(MEMOS[i], c1.getString(c1.getColumnIndex(MEMOS[i])));
                }
                map.put(NCP_RETURN_CREATE_AT, getCurrentDateTime());
                map.put(MESSAGE_UPDATED_AT, getCurrentDateTime());
                db.InsertTable(map, "memos");

            }

            Cursor c2 = db.rawQueryCoustom(" SELECT * FROM temp_memo_details  WHERE " + MEMOS_memo_number + "='" + TempData.memoNumber + "'");

            c2.moveToFirst();
            if (c2 != null && c2.getCount() > 0) {

                HashMap<String, String> map = new HashMap<>();
                for (int i = 2; i < MEMO_DETAILS.length - 2; i++) {

                    map.put(MEMO_DETAILS[i], c2.getString(c2.getColumnIndex(MEMO_DETAILS[i])));
                }
                map.put(NCP_RETURN_CREATE_AT, getCurrentDateTime());
                map.put(MESSAGE_UPDATED_AT, getCurrentDateTime());
                Log.e("MemoInsertMap", new Gson().toJson(map));
                db.InsertTable(map, "memo_details");

            }
        } catch (IllegalStateException e) {

            Log.e("exception", e.getMessage());
           // e.printStackTrace();

        }

        db.excQuery("delete from temp_memos");
        db.excQuery("delete from temp_memo_details");
    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(this, "NO Internet Connection", Toast.LENGTH_SHORT);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void SaveDataToTable() {
        orderList = Adapter.getAdapterHashMapList();
        Log.e("ReturnAdapter ", "ReturnAdapter " + orderList.toString());
        double MemoQuantity = 0.0;

        String query = "SELECT product_id, quantity FROM product_boolean WHERE outlet_id='" + getPreference("OutletID") + "' AND boolean='true'";
        Cursor c = db.rawQueryCoustom(query);
        int count = c.getCount();
        int VanQtyCheckFlag = 1;
        Log.e("QUERY COUNT:", "..............." + count);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String productId = c.getString(0);
                    double Quantity = c.getDouble(1);

                    Log.e("productId: ", "" + productId);
                    Log.e("Quantity: ", "" + Quantity);
                    Log.e("orderNumber: ", "" + TempData.orderNumber);

                    String Memoquery = "SELECT SUM(quantity) FROM memo_details WHERE product_id='" + productId + "' and " + Tables.MEMOS_memo_number + "='" + TempData.orderNumber + "'";
                    Cursor c3 = db.rawQueryCoustom(Memoquery);
                    int count3 = c3.getCount();

                    if (c3 != null) {
                        if (c3.moveToFirst()) {
                            do {

                                MemoQuantity = c3.getDouble(0);


                            } while (c3.moveToNext());
                        }
                    }
                } while (c.moveToNext());
            }
        }

        VanQtyCheckFlag = 1;
        if (VanQtyCheckFlag == 1) {
            VanQtyCheckFlag = 1;

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
            Calendar cal = Calendar.getInstance();


            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                if (TempData.editMemo.equalsIgnoreCase("true")) {
                    Log.e("order", TempData.orderNumber + "");
                    orderNo = TempData.orderNumber;
                    if (MEMO_EDIT) {
                        memoNo = TempData.memoNumber;
                    }
                    FromApp = TempData.From_App;
                    for_memo_delete = TempData.for_memo_delete;
                    OrderDate = TempData.MemoDate;
                    orderdateTime = TempData.MemoDateTime;
                    //new EditMemoDatabaseHelper().UpdateVan(ProductSales.this, orderNo);
                    SalesOrderNO(false);
                    editableAllow = TempData.DayCloseMemoEditable;
                    if (ORDER_TO_MEMO == 1) {
                        SalesMemoNO(true);
                    }

                } else {
                    FromApp = "1";
                    for_memo_delete = "0";
                    OrderDate = getCurrentDate();
                    orderdateTime = getCurrentDateTime();
                    SalesOrderNO(true);
                    editableAllow = "0";
                }

                Memo();

                Log.e("MEMO", orderNo);

                HashMap<String, String> map1 = new HashMap<String, String>();

                map1.put("outlet_id", TempData.OutletID);
                map1.put("gift_issue_id", "" + orderNo);
                map1.put("gift_issued_by", getPreference("SO"));
                map1.put("gift_issue_date", OrderDate);
                map1.put("order_number", orderNo);
                map1.put("is_Pushed", "0");
                db.InsertTable(map1, "gift_issue");


                Log.e("MEMO", orderNo);
                TempData.orderNumber = orderNo;
                TempData.INVOICE_DETAILS.clear();

                Log.e("SiZED IN SAVE button:", String.valueOf(orderList.size()));

                Bonus = Adapter.getBonusMapList();
                GiftItem = GiftItem;

                int currentMonth1 = Calendar.getInstance().get(Calendar.MONTH);

                Log.e("orderList size", orderList.size() + "");


                    for (int i = 0; i < orderList.size(); i++) {
                        Log.e("TAG:", "target");
                        //SO TARGETS
                        double totalAmount = Double.parseDouble(orderList.get(i).get("quantity")) * Double.parseDouble(orderList.get(i).get("general_price"));
                        Log.e("totalAmount", "" + totalAmount);
                        Log.e("PRODUCT_ID(TARGET)", orderList.get(i).get("product_id"));
                        Log.e("SO TARGET query:", "UPDATE sales_targets SET achieve_quantity=achieve_quantity+" + Double.parseDouble(orderList.get(i).get("quantity")) + ", achieve_amount=achieve_amount+" + totalAmount + " WHERE product_id=" + orderList.get(i).get("product_id") + " AND month_id=" + currentMonth1 + " AND territory_id=" + getPreference("Territory_Id") + " AND so_id=" + getPreference("SO"));
                        //	               db.excQuery("UPDATE sales_targets SET achieve_quantity=achieve_quantity+"+Double.parseDouble(orderList.get(i).get("quantity"))+", achieve_amount=achieve_amount+"+totalAmount+" WHERE product_id="+orderList.get(i).get("product_id")+" AND month_id="+currentMonth1+" AND territory_id="+getPreference("Territory_Id")+" AND so_id="+getPreference("SO"));

                    }


                for (int i = 0; i < orderList.size(); i++) {
                    Log.e("vatprice", orderList.get(i).get("vat"));
                    //UpdateStock(orderList.get(i).get("product_id"),orderList.get(i).get("quantity"));

                    //UpdateVan(orderList.get(i).get("product_id"),orderList.get(i).get("quantity"));

                    MemoDetails("0",
                            orderList.get(i).get("product_id"),
                            orderList.get(i).get("quantity"),
                            orderList.get(i).get("general_price"),
                            orderList.get(i).get("product_price_id"),
                            orderList.get(i).get("vat"),
                            orderList.get(i).get("combination_id")
                    );

                    InvoiceTemporary(orderList.get(i).get("product_id"),
                            orderList.get(i).get("product_name"),
                            orderList.get(i).get("quantity"),
                            orderList.get(i).get("general_price"),
                            orderList.get(i).get("vat"));
                    //UpdateLastLifting(orderList.get(i).get("product_id"),orderList.get(i).get("product_name"),orderList.get(i).get("quantity"),orderList.get(i).get("general_price"));
                    db.excQuery("UPDATE product_boolean SET quantity ='', boolean = 'false' WHERE product_id = " + "'" + orderList.get(i).get("product_id") + "'" + "" + " and outlet_id=" + "'" + getPreference("OutletID") + "'");

                }

               /* for (int i = 0; i < orderList.size(); i++) {
                    //UpdateStock(orderList.get(i).get("product_id"),orderList.get(i).get("quantity"));
                    //UpdateVan(orderList.get(i).get("product_id"),orderList.get(i).get("quantity"));
                    MemoDetails("0", orderList.get(i).get("product_id"),
                    orderList.get(i).get("quantity"),
                    orderList.get(i).get("general_price"),
                    orderList.get(i).get("vat"));
                    InvoiceTemporary(orderList.get(i).get("product_id"),
                     orderList.get(i).get("product_name"),
                     orderList.get(i).get("quantity"),
                      orderList.get(i).get("general_price"));
                    //UpdateLastLifting(orderList.get(i).get("product_id"),orderList.get(i).get("product_name"),orderList.get(i).get("quantity"),orderList.get(i).get("general_price"));
                    db.excQuery("UPDATE product_boolean SET quantity ='', boolean = 'false' WHERE product_id = " + "'" + orderList.get(i).get("product_id") + "'" + "" + " and outlet_id=" + "'" + getPreference("OutletID") + "'");

                }*/

                for (int i = 0; i < Bonus.size(); i++) {
                    //UpdateStock(Bonus.get(i).get("product_id"),Bonus.get(i).get("quantity"));
                    // UpdateVan(Bonus.get(i).get("product_id"),Bonus.get(i).get("quantity"));
                    MemoDetails("2", Bonus.get(i).get("product_id"), Bonus.get(i).get("quantity"), "", "", "", "");
                }

                for (int i = 0; i < BPSelected_product.size(); i++) {
                    //UpdateStock(Bonus.get(i).get("product_id"),Bonus.get(i).get("quantity"));
                    //UpdateVanBP(BPSelected_product.get(i).get("product_id"),BPSelected_product.get(i).get("qty"),BPSelected_product.get(i).get("measurement_unit_id"));
                    MemoDetailsBP("2",
                            BPSelected_product.get(i).get("product_id"),
                            BPSelected_product.get(i).get("qty"),
                            "",
                            "",
                            "",
                            BPSelected_product.get(i).get("measurement_unit_id"),
                            BPSelected_product.get(i).get("policy_id"),
                            BPSelected_product.get(i).get("policy_type"),
                            BPSelected_product.get(i).get("set")
                    );
                    Log.e("bp_bonus_loop", "bonus loop list");

                }

               /* for (int i = 0; i < BPbonus_product.size(); i++) {
                    //UpdateStock(Bonus.get(i).get("product_id"),Bonus.get(i).get("quantity"));
                    // UpdateVan(Bonus.get(i).get("product_id"),Bonus.get(i).get("quantity"));
                    MemoDetailsBP("2", BPbonus_product.get(i).get("product_id"), BPbonus_product.get(i).get("quantity"), "", "",BPbonus_product.get(i).get("measurement_unit_id"),BPbonus_product.get(i).get("policy_id"));
                }*/

                for (int i = 0; i < GiftItem.size(); i++) {
                    MemoDetails("1", GiftItem.get(i).get("product_id"), GiftItem.get(i).get("quantity"), "", "", "", "");
                    //  UpdateVan(GiftItem.get(i).get("product_id"),GiftItem.get(i).get("quantity"));
                    db.excQuery("UPDATE product_boolean SET quantity ='', boolean = 'false' WHERE product_id = " + "'" + GiftItem.get(i).get("product_id") + "'" + " and outlet_id=" + "'" + getPreference("OutletID") + "'");
                }


                for (int i = 0; i < TempData.TotalBonusProductList.size(); i++) {
                    // UpdateVan(TempData.TotalBonusProductList.get(i).get("product_id"), TempData.TotalBonusProductList.get(i).get("quantity"));
                    MemoDetailsForBonus(TempData.TotalBonusProductList.get(i).get("product_id"), TempData.TotalBonusProductList.get(i).get("quantity"), "", "");
                }



//			     db.excQuery("UPDATE memos_draft SET isPushed ='1' WHERE outlet_id='"+getPreference("OutletID")+"'");
                //db.deleteRowOFDraft("memos_draft", getPreference("OutletID"));


                TempData.InvoiceTotal = TotalPrice.getText().toString();


                TempData.TempGift = txtGift.getText().toString();
                TempData.TempBonus_EN = txtBonus.getText().toString();
                TempData.TempExtraBonus = txtBonusExtra.getText().toString();
                TempData.TempGift = txtGift.getText().toString();

                writeToFile("smc");

                DeleteBefore3DaysMemos();

                if (TempData.editMemo.equalsIgnoreCase("true")) {

                    if (TempData.isPushed.equalsIgnoreCase("1") && ORDER_TO_MEMO != 1 && !MEMO_EDIT) {
                        urlcall = 101;
                        generateMemoForPush(orderNo);
                    } else if (ORDER_TO_MEMO == 1) {
                        urlcall = 102;
                        db.generateSingleTempMemo(memoNo);
                    } else if (MEMO_EDIT) {
                        urlcall = 102;
                        Log.e("memo", memoNo);
                        db.generateSingleTempMemo(memoNo);

                    }

                    // TempData.orderNumber = "";
                    TempData.From_App = "";
                    tempClean();
                    TempBonus();
                    TempData.TotalBonusProductList.clear();
                    TempData.BonusChxSelected.clear();
                    TempData.BonusProductQuantity.clear();
                    TempData.TempBonus_EN = "";
                    TempData.TempExtraBonus = "";
                    TempData.editMemo = "";
                    //db.deleteRowOFDraft("bonus_items", TempData.OutletID);
                    if (TempData.isPushed.equalsIgnoreCase("0") && !MEMO_EDIT) {
                        startActivity(new Intent(ProductSales.this, DetailsOrderReport.class));
                        finish();
                    }

                } else {
                    tempClean();
                    TempBonus();
                    TempData.TotalBonusProductList.clear();
                    TempData.BonusChxSelected.clear();
                    TempData.BonusProductQuantity.clear();
                    TempData.TempBonus_EN = "";
                    TempData.TempExtraBonus = "";
                    // db.deleteRowOFDraft("bonus_items", TempData.OutletID);
                    startActivity(new Intent(ProductSales.this, Create_New_Memo.class));
                    finish();
                }
            }
        }
    }

    private void generateMemoForPush(String orderNo) {

        db.generateSingleOrder(orderNo);
    }

    public boolean EmptyPaymentValidation() {
       /* if(etPayment.getText().toString().equalsIgnoreCase("") || etPayment.getText().toString().equalsIgnoreCase("."))
        {
            Toast.makeText(getApplicationContext(), "Enter into payment field!", Toast.LENGTH_LONG).show();
            return false;
        }*/
        if (TempData.MarketID.equalsIgnoreCase("") || TempData.MarketID.equalsIgnoreCase
                ("null")) {
            Toast.makeText(getApplicationContext(), "No Found Market!", Toast.LENGTH_LONG).show();
            return false;
        } else if (TempData.OutletID.equalsIgnoreCase("") || TempData.OutletID.equalsIgnoreCase
                ("null")) {
            Toast.makeText(getApplicationContext(), "No Found Outlet", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Memo() {

        if ( bf.getPreference(SR_ID).equalsIgnoreCase("null")){
            Toast.makeText(this,"Please Do online Login and try again",Toast.LENGTH_LONG).show();
            return;
        }

        if (TempData.editMemo.equalsIgnoreCase("true") && ORDER_TO_MEMO == 0 && !MEMO_EDIT) {
            orderNo = TempData.orderNumber;
            db.excQuery("DELETE FROM " + TABLE_NAME_ORDER + " WHERE " + ORDER_order_number + "='" + TempData.orderNumber + "'");
            db.excQuery("DELETE FROM " + TABLE_NAME_ORDER_DETAILS + " WHERE " + ORDER_order_number + "='" + TempData.orderNumber + "'");
            db.excQuery("DELETE FROM " + TABLE_NAME_GIFT_ISSUE + " WHERE " + ORDER_order_number + "='" + TempData.orderNumber + "'");
            db.excQuery("DELETE  FROM gift_issue WHERE gift_issue_id='" + TempData.orderNumber + "'");

            db.excQuery("DELETE  FROM gift_issue_details WHERE gift_issue_id='" + TempData.orderNumber + "'");
        }

        db.excQuery("DELETE  FROM temp_memos");
        db.excQuery("DELETE  FROM temp_memo_details");


        lat = lattitude;
        lng = longitude;

        Log.e("lat: ", "" + lat);
        Log.e("lng: ", "" + lng);

        Log.e("----FromApp Memo----", FromApp);

        HashMap<String, String> map1 = new HashMap<String, String>();

        Log.e("Map", "Memo: " + ORDER_TO_MEMO);


        if (ORDER_TO_MEMO == 1) {
            Log.e("Map1 1st if", "Memo: " + ORDER_TO_MEMO);
            Log.e("orderNo", orderNo);
            map1.put(Tables.ORDER_order_number, orderNo);
            map1.put(Tables.MEMOS_memo_number, memoNo);
/*            map1.put(Tables.MEMOS_memo_date, getCurrentDate());
            map1.put(Tables.MEMOS_memo_date_time, getCurrentDateTime());*/
            map1.put(Tables.MEMOS_memo_date, TempData.selectDeliveryDate);
            map1.put(Tables.MEMOS_memo_date_time, TempData.selectDeliveryDateTime);
            map1.put(Tables.SR_ID, bf.getPreference(SR_ID));
            map1.put(Tables.MEMOS_sales_to, "0");
            map1.put(Tables.OUTLETS_ID, TempData.OutletID);
            map1.put(Tables.MARKET_ID, TempData.MarketID);
            map1.put(Tables.MEMOS_gross_value, TotalPrice.getText().toString());
            map1.put(Tables.MEMOS_is_active, "1");
            map1.put(MEMOS_latitude, lat);
            map1.put(MEMOS_longitude, lng);
            map1.put(Tables.MEMOS_total_discount, discount.getText().toString());
            map1.put(Tables.MEMOS_TOTAL_Vat, vattv.getText().toString().trim());
            map1.put(Tables.MEMOS_discount_amount, discount.getText().toString().trim());
            if (TempData.DISTYPE == 1) {
                map1.put(Tables.MEMOS_discount_Percentage, TempData.DISCOUNTP + "");
            } else if (TempData.DISTYPE == 2) {

                map1.put(Tables.MEMOS_discount_Percentage, discount.getText().toString().trim() + "");
            } else {
                map1.put(Tables.MEMOS_discount_Percentage, "0");
            }

            map1.put(Tables.MEMOS_DISCOUNT_TYPE, TempData.DISTYPE + "");
            String outlate = "0";
            if (TempData.OutletCatagoryID.equalsIgnoreCase("17")) {
                outlate = "1";
            }
            map1.put(Tables.MEMOS_is_distributor, outlate);
            map1.put(Tables.MEMOS_from_app, FromApp);
            map1.put(Tables.MEMOS_for_memo_delete, for_memo_delete);
            map1.put(Tables.MEMOS_editable, editableAllow);
            Log.e("ProductSalesUsingFor", "Order To Memo");
            map1.put(Tables.ORDER_is_pushed, "1");
            map1.put(Tables.MEMOS_ORDER_DATE, TempData.MemoAndOrderDate);
            map1.put(Tables.ORDER_created_at, bf.getCurrentDateTime());
            map1.put(Tables.ORDER_updated_at, bf.getCurrentDateTime());
            db.InsertTable(map1, "temp_memos");

            Log.e("Product Sales For", "Order TO MEMo ___BOND_ADDED_MAP_1" + map1);

        } else if (ORDER_TO_MEMO == 0 && !MEMO_EDIT || !TempData.editMemo.equalsIgnoreCase("true") && !MEMO_EDIT) {
            Log.e("Map1 2nd if", "Memo: " + ORDER_TO_MEMO);
            map1.put(Tables.ORDER_order_number, orderNo);
            map1.put(Tables.ORDER_order_date, OrderDate);
            map1.put(Tables.ORDER_order_date_time, orderdateTime);
            map1.put(Tables.SR_ID, bf.getPreference(SR_ID));
            map1.put(Tables.ORDER_sales_to, "0");
            map1.put(Tables.OUTLETS_ID, TempData.OutletID);
            map1.put(Tables.MARKET_ID, TempData.MarketID);

            map1.put(MEMOS_latitude, lat);
            map1.put(MEMOS_longitude, lng);
            Log.e("Map", "Memo: " + MEMOS_latitude + " " + lat + " test get" + map1.get(MEMOS_latitude));


            if (TempData.editMemo.equalsIgnoreCase("true")) {
                plan_id = TempData.ORDER_plan_id + "";
                is_out_of_plan = TempData.ORDER_PlanVisit + "";
            }
            map1.put(Tables.ORDER_plan_id, plan_id);
            map1.put(Tables.ORDER_is_out_of_plan, is_out_of_plan);
            map1.put(Tables.ORDER_is_Complete, "0");
            map1.put(Tables.ORDER_STATUS, "0");
            map1.put(Tables.ORDER_TOTAL_VAT, vattv.getText().toString().trim());
            if (TempData.DISTYPE == 1) {
                map1.put(Tables.ORDER_discount_Percentage, TempData.DISCOUNTP + "");
            } else if (TempData.DISTYPE == 2) {

                map1.put(Tables.ORDER_discount_Percentage, discount.getText().toString().trim() + "");
            } else {
                map1.put(Tables.ORDER_discount_Percentage, "0");
            }




            map1.put(Tables.ORDER_discount_amount, discount.getText().toString().trim());
            map1.put(Tables.ORDER_gross_value, TotalPrice.getText().toString());
            map1.put(Tables.ORDER_is_active, "1");
            map1.put(Tables.ORDER_latitude, lat);
            map1.put(Tables.ORDER_longitude, lng);
            map1.put(ORDER_START_TIME, getPreference(ORDER_START_TIME));
            map1.put(ORDER_END_TIME, getCurrentDateTime());
            map1.put(Tables.MEMOS_DISCOUNT_TYPE, TempData.DISTYPE + "");
            String outlate = "0";
            if (TempData.OutletCatagoryID.equalsIgnoreCase("17")) {
                outlate = "1";
            }

            Log.e("ProductSalesUsingFor", "Memo Edit & Create");
            map1.put(Tables.ORDER_is_distributor, outlate);
            map1.put(Tables.ORDER_from_app, FromApp);
            map1.put(Tables.ORDER_for_order_delete, for_memo_delete);
            map1.put(Tables.ORDER_editable, editableAllow);
            map1.put(Tables.MEMOS_total_discount, discount.getText().toString());
            map1.put(Tables.ORDER_is_pushed, TempData.isPushed);

            if (!TempData.editMemo.equalsIgnoreCase("true"))
                map1.put(Tables.ORDER_created_at, bf.getCurrentDateTime());
            map1.put(Tables.ORDER_updated_at, bf.getCurrentDateTime());
            Log.e("Map", "Memo: befor insert" + MEMOS_latitude + " " + lat + " test get" + map1.get(MEMOS_longitude));
            db.InsertTable(map1, Tables.TABLE_NAME_ORDER);
            Log.e("Product Sales For", "Order Create");
        } else if (MEMO_EDIT) {
            Log.e("Map1 3rd if", "Memo: " + ORDER_TO_MEMO);
            Log.e("orderNo", orderNo);
            map1.put(Tables.ORDER_order_number, orderNo);
            map1.put(Tables.MEMOS_memo_number, memoNo);
            map1.put(Tables.MEMOS_memo_date, TempData.MemoDate);
            map1.put(Tables.MEMOS_memo_date_time, TempData.MemoDateTime);
            map1.put(Tables.SR_ID, bf.getPreference(SR_ID));
            map1.put(Tables.MEMOS_sales_to, "0");
            map1.put(Tables.OUTLETS_ID, TempData.OutletID);
            map1.put(Tables.MARKET_ID, TempData.MarketID);
            map1.put(Tables.MEMOS_gross_value, TotalPrice.getText().toString());
            map1.put(Tables.MEMOS_is_active, "1");
            map1.put(MEMOS_latitude, lat);
            map1.put(Tables.MEMOS_longitude, lng);
            map1.put(Tables.MEMOS_total_discount, discount.getText().toString().trim());
            map1.put(Tables.MEMOS_discount_amount, discount.getText().toString().trim());
            if (TempData.DISTYPE == 1) {
                map1.put(Tables.MEMOS_discount_Percentage, TempData.DISCOUNTP + "");
            } else if (TempData.DISTYPE == 2) {

                map1.put(Tables.MEMOS_discount_Percentage, discount.getText().toString().trim() + "");
            } else {
                map1.put(Tables.MEMOS_discount_Percentage, "0");
            }
            String outlate = "0";
            if (TempData.OutletCatagoryID.equalsIgnoreCase("17")) {
                outlate = "1";
            }
            Log.e("ProductSalesUsingFor", "MemoEdit");
            map1.put(Tables.MEMOS_is_distributor, outlate);
            map1.put(Tables.MEMOS_from_app, FromApp);
            map1.put(Tables.MEMOS_for_memo_delete, for_memo_delete);
            map1.put(Tables.MEMOS_DISCOUNT_TYPE, TempData.DISTYPE + "");
            map1.put(Tables.MEMOS_editable, editableAllow);
            map1.put(Tables.MEMOS_TOTAL_Vat, vattv.getText().toString().trim());
            map1.put(Tables.ORDER_is_pushed, "1");
            map1.put(Tables.MEMOS_ORDER_DATE, TempData.MemoAndOrderDate);
            map1.put(Tables.ORDER_created_at, TempData.MemoDateTime);
            map1.put(Tables.ORDER_updated_at, bf.getCurrentDateTime());
            db.InsertTable(map1, "temp_memos");
        }

        Log.e("Map", "Memo: " + MEMOS_latitude + " " + lat + " test get" + map1.get(MEMOS_longitude));


        db.excQuery("UPDATE outlet_visit SET ispushed='3'  WHERE  date(updated_at)='" + getCurrentDate() + "' and outlet_id ='" + getPreference("OutletID") + "'");


        Log.e("Outside Check!", "Checkede!");

        double paymentAmount = 0.0;

        double memo_value = Double.parseDouble(TotalPrice.getText().toString());
        double paid_amount = memo_value;

    }

    public void MemoDetails(String type, String product_id, String qunatity, String price, String product_price_id, String vat, String combination_id) {

        //Toast.makeText(this, "sample", Toast.LENGTH_SHORT).show();
        Log.e("CurrentInventoryID: ", TempData.CurrentInventoryID);
        HashMap<String, String> map = new HashMap<String, String>();


        if (type.equalsIgnoreCase("1")) {
            HashMap<String, String> map2 = new HashMap<String, String>();
            map2.put("gift_issue_id", "" + orderNo);
            map2.put("product_id", db.getProductID(product_id));
            map2.put("quantity", qunatity);
            db.InsertTable(map2, "gift_issue_details");
        }


        if (ORDER_TO_MEMO == 1) {

            map.put(Tables.MEMO_DETAILS_memo_number, memoNo);
            map.put(Tables.MEMO_DETAILS_order_number, orderNo);
            map.put(Tables.MEMO_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.MEMO_DETAILS_product_type, type);
            map.put(Tables.MEMO_DETAILS_quantity, qunatity);
            map.put(Tables.MEMO_DETAILS_price, price);
            map.put(Tables.MEMO_DETAILS_memo_date, getCurrentDate());
            map.put(Tables.MEMO_DETAILS_is_bonus, "0");
            map.put(Tables.MEMO_DETAILS_is_bonus, "0");
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.MEMO_DETAILS_vat, vat);
            map.put(Tables.MEMO_DETAILS_created_at, getCurrentDateTime());
            map.put(Tables.MEMO_DETAILS_POLICY_Id, discountofferPolicyid.get(product_id));
            map.put(Tables.MEMO_DETAILS_updated_at, getCurrentDateTime());
            map.put(Tables.MEMO_DETAILS_discount_amount, discountmap.get(product_id));
            map.put(Tables.MEMO_DETAILS_discount_type, discounttype.get(product_id));
            map.put(Tables.MEMO_DETAILS_policy_type, discountoffer.get(product_id));
            if (price_idMap.get(product_id) != null)
                map.put(Tables.MEMO_DETAILS_PRICE_Id, price_idMap.get(product_id));
            if (combination_idMap.get(product_id) != null)
                map.put(Tables.MEMO_DETAILS_COMBINATION_Id, combination_idMap.get(product_id));

            db.InsertTable(map, "temp_memo_details");

        } else if (ORDER_TO_MEMO == 0 && !MEMO_EDIT || !TempData.editMemo.equalsIgnoreCase("true") && !MEMO_EDIT) {

            map.put(Tables.ORDER_DETAILS_order_number, orderNo);
            map.put(Tables.ORDER_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.ORDER_DETAILS_product_type, type);
            map.put(Tables.ORDER_DETAILS_quantity, qunatity);
            map.put(Tables.ORDER_DETAILS_price, price);
            map.put(Tables.ORDER_DETAILS_order_date, OrderDate);
            map.put(ORDER_DETAILS_is_bonus, "0");
            map.put(Tables.MEMO_DETAILS_vat, vat);
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.ORDER_DETAILS_policy_ID, discountofferPolicyid.get(product_id));
            map.put(Tables.ORDER_DETAILS_updated_at, getCurrentDateTime());
            map.put(Tables.ORDER_DETAILS_discount_amount, discountmap.get(product_id));
            map.put(Tables.ORDER_DETAILS_discount_type, discounttype.get(product_id));
            map.put(Tables.ORDER_DETAILS_policy_type, discountoffer.get(product_id));
            if (price_idMap.get(product_id) != null)
                map.put(Tables.ORDER_DETAILS_PRICE_Id, price_idMap.get(product_id));
            if (combination_idMap.get(product_id) != null)
                map.put(Tables.ORDER_DETAILS_COMBINATION_Id, combination_idMap.get(product_id));
            db.InsertTable(map, TABLE_NAME_ORDER_DETAILS);
        } else if (MEMO_EDIT) {

            map.put(Tables.MEMO_DETAILS_memo_number, memoNo);
            map.put(Tables.MEMO_DETAILS_order_number, orderNo);
            map.put(Tables.MEMO_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.MEMO_DETAILS_product_type, type);
            map.put(Tables.MEMO_DETAILS_quantity, qunatity);
            map.put(Tables.MEMO_DETAILS_price, price);
            map.put(Tables.MEMO_DETAILS_vat, vat);
            map.put(Tables.MEMO_DETAILS_memo_date, TempData.MemoDate);
            map.put(Tables.MEMO_DETAILS_is_bonus, "0");
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.MEMO_DETAILS_created_at, TempData.MemoDateTime);
            map.put(Tables.MEMO_DETAILS_updated_at, getCurrentDateTime());
            map.put(Tables.MEMO_DETAILS_POLICY_Id, discountofferPolicyid.get(product_id));
            map.put(Tables.MEMO_DETAILS_discount_amount, discountmap.get(product_id));
            map.put(Tables.MEMO_DETAILS_discount_type, discounttype.get(product_id));
            map.put(Tables.MEMO_DETAILS_policy_type, discountoffer.get(product_id));
            if (price_idMap.get(product_id) != null)
                map.put(Tables.MEMO_DETAILS_PRICE_Id, price_idMap.get(product_id));
            if (combination_idMap.get(product_id) != null)
                map.put(Tables.MEMO_DETAILS_COMBINATION_Id, combination_idMap.get(product_id));
            db.InsertTable(map, "temp_memo_details");
            Log.e("ProductSales", "MemoEdit");

        }

    }

    public void MemoDetailsBP(String type, String product_id, String qunatity, String price, String product_price_id, String vat, String measurement_unit_id, String Policy_id, String policy_type, String set) {

        //Toast.makeText(this, "sample", Toast.LENGTH_SHORT).show();
        Log.e("CurrentInventoryID: ", TempData.CurrentInventoryID);

        Log.e("CurrentInventoryID: ", new Gson().toJson(BPSelected_bonus));
        //Log.e("CurrentInventoryID: ", BPSelected_bonus.get(40).get(set).get(product_id).get("provided_qty"));

        HashMap<String, String> map = new HashMap<String, String>();


        if (type.equalsIgnoreCase("1")) {
            HashMap<String, String> map2 = new HashMap<String, String>();
            map2.put("gift_issue_id", "" + orderNo);
            map2.put("product_id", db.getProductID(product_id));
            map2.put("quantity", qunatity);
            db.InsertTable(map2, "gift_issue_details");
        }


        if (ORDER_TO_MEMO == 1) {

            map.put(Tables.MEMO_DETAILS_memo_number, memoNo);
            map.put(Tables.MEMO_DETAILS_order_number, orderNo);
            map.put(Tables.MEMO_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.MEMO_DETAILS_product_type, type);
            map.put(Tables.MEMO_DETAILS_quantity, qunatity);
            map.put(Tables.MEMO_DETAILS_price, price);
            map.put(Tables.MEMO_DETAILS_memo_date, getCurrentDate());
            map.put(Tables.MEMO_DETAILS_is_bonus, "3");
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.MEMO_DETAILS_policy_type, policy_type);
            map.put(Tables.MEMO_DETAILS_POLICY_Id, Policy_id);
            map.put(Tables.MEMO_DETAILS_vat, vat);
            map.put(Tables.MEMO_DETAILS_Unit_id, measurement_unit_id);
            map.put(Tables.MEMO_DETAILS_selected_set, set);
            map.put(Tables.MEMO_DETAILS_provided_qty, BPSelected_bonus.get(Policy_id).get(set).get(product_id).get("provided_qty"));
            map.put(Tables.MEMO_DETAILS_created_at, getCurrentDateTime());
            map.put(Tables.MEMO_DETAILS_updated_at, getCurrentDateTime());
            db.InsertTable(map, "temp_memo_details");

        } else if (ORDER_TO_MEMO == 0 && !MEMO_EDIT || !TempData.editMemo.equalsIgnoreCase("true") && !MEMO_EDIT) {

            map.put(Tables.ORDER_DETAILS_order_number, orderNo);
            map.put(Tables.ORDER_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.ORDER_DETAILS_product_type, type);
            map.put(Tables.ORDER_DETAILS_quantity, qunatity);
            map.put(Tables.ORDER_DETAILS_policy_ID, Policy_id);
            map.put(Tables.ORDER_DETAILS_price, price);
            map.put(Tables.ORDER_DETAILS_order_date, OrderDate);
            map.put(ORDER_DETAILS_is_bonus, "3");
            map.put(Tables.MEMO_DETAILS_vat, vat);
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.MEMO_DETAILS_Unit_id, measurement_unit_id);
            map.put(Tables.ORDER_DETAILS_updated_at, getCurrentDateTime());
            map.put(Tables.ORDER_DETAILS_selected_set, set);
            map.put(Tables.ORDER_DETAILS_policy_type, policy_type);
            map.put(Tables.ORDER_DETAILS_provided_qty, BPSelected_bonus.get(Policy_id).get(set).get(product_id).get("provided_qty"));
            db.InsertTable(map, TABLE_NAME_ORDER_DETAILS);
        } else if (MEMO_EDIT) {

            map.put(Tables.MEMO_DETAILS_memo_number, memoNo);
            map.put(Tables.MEMO_DETAILS_order_number, orderNo);
            map.put(Tables.MEMO_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.MEMO_DETAILS_POLICY_Id, Policy_id);
            map.put(Tables.MEMO_DETAILS_product_type, type);
            map.put(Tables.MEMO_DETAILS_quantity, qunatity);
            map.put(Tables.MEMO_DETAILS_price, price);
            map.put(Tables.MEMO_DETAILS_vat, vat);
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.MEMO_DETAILS_Unit_id, measurement_unit_id);
            map.put(Tables.MEMO_DETAILS_memo_date, TempData.MemoDate);
            map.put(Tables.MEMO_DETAILS_is_bonus, "3");
            map.put(Tables.MEMO_DETAILS_policy_type, policy_type);
            map.put(Tables.MEMO_DETAILS_selected_set, set);
            map.put(Tables.MEMO_DETAILS_provided_qty, BPSelected_bonus.get(Policy_id).get(set).get(product_id).get("provided_qty"));
            map.put(Tables.MEMO_DETAILS_created_at, TempData.MemoDateTime);
            map.put(Tables.MEMO_DETAILS_updated_at, getCurrentDateTime());
            db.InsertTable(map, "temp_memo_details");
            Log.e("ProductSalesUsing", "MemoEdit");

        }

    }

    public void MemoDetailsForBonus(String product_id, String qunatity, String set, String policy_is) {

//		db.excQuery("DELETE FROM bonus_items WHERE memo_no='"+TempData.orderNumber+"'");

        //Toast.makeText(this, "total bonus product list", Toast.LENGTH_SHORT).show();
        HashMap<String, String> map = new HashMap<String, String>();
        if (ORDER_TO_MEMO == 1) {

            map.put(Tables.MEMO_DETAILS_memo_number, memoNo);
            map.put(Tables.MEMO_DETAILS_order_number, orderNo);
            map.put(Tables.MEMO_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.MEMO_DETAILS_product_type, "2");
            map.put(Tables.MEMO_DETAILS_quantity, qunatity);
            map.put(Tables.MEMO_DETAILS_price, price);
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.MEMO_DETAILS_memo_date, getCurrentDate());
            map.put(Tables.MEMO_DETAILS_is_bonus, "1");
            map.put(Tables.MEMO_DETAILS_created_at, getCurrentDateTime());
            map.put(Tables.MEMO_DETAILS_updated_at, getCurrentDateTime());
            db.InsertTable(map, "temp_memo_details");

        } else if (ORDER_TO_MEMO == 0 && !MEMO_EDIT || !TempData.editMemo.equalsIgnoreCase("true") && !MEMO_EDIT) {

            map.put(Tables.ORDER_DETAILS_order_number, orderNo);
            map.put(Tables.ORDER_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.ORDER_DETAILS_product_type, "2");
            map.put(Tables.ORDER_DETAILS_quantity, qunatity);
            map.put(Tables.ORDER_DETAILS_price, price);
            map.put(Tables.ORDER_DETAILS_order_date, OrderDate);
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.ORDER_DETAILS_policy_ID, policyMap.get(product_id));
            map.put(ORDER_DETAILS_is_bonus, "1");
            map.put(Tables.ORDER_DETAILS_updated_at, getCurrentDateTime());
            db.InsertTable(map, TABLE_NAME_ORDER_DETAILS);
        } else if (MEMO_EDIT) {

            map.put(Tables.MEMO_DETAILS_memo_number, memoNo);
            map.put(Tables.MEMO_DETAILS_order_number, orderNo);
            map.put(Tables.MEMO_DETAILS_current_inventory_id, TempData.CurrentInventoryID);
            map.put(Tables.PRODUCT_ID, db.getProductID(product_id));
            map.put(Tables.MEMO_DETAILS_product_type, "2");
            map.put(Tables.MEMO_DETAILS_quantity, qunatity);
            map.put(Tables.MEMO_DETAILS_price, price);
            map.put(Tables.MEMO_DETAILS_virtual_id, db.getVirtualProductID(product_id));
            map.put(Tables.ORDER_DETAILS_policy_ID, policyMap.get(product_id));
            map.put(Tables.MEMO_DETAILS_memo_date, TempData.MemoDate);
            map.put(Tables.MEMO_DETAILS_is_bonus, "1");
            map.put(Tables.MEMO_DETAILS_created_at, TempData.MemoDateTime);
            map.put(Tables.MEMO_DETAILS_updated_at, getCurrentDateTime());
            db.InsertTable(map, "temp_memo_details");
            Log.e("ProductSalesUsingFor", "MemoEdit");
        }


    }

    public void SalesOrderNO(Boolean createMemo) {
        final Calendar c = Calendar.getInstance();
        year1 = c.get(Calendar.YEAR);
        month1 = c.get(Calendar.MONTH);
        day1 = c.get(Calendar.DAY_OF_MONTH);

        String day = "", month = "", year;


        String year_ = String.valueOf(year1);
        year = year_.substring(2, 4);


        if (day1 < 10)
            day = "0" + day1;
        else
            day = String.valueOf(day1);
        if (month1 + 1 < 10)
            month = "0" + (month1 + 1);
        else
            month = String.valueOf(month1 + 1);
        date = year1 + "-" + month + "-" + day;
        Log.e("OutletID", TempData.OutletID);
        Log.e("SO", getPreference(SR_ID));
        Log.e("Day", day);
        Log.e("year", year);
        Log.e("Month", month);
        Log.e("Hour", "" + c.get(Calendar.HOUR_OF_DAY));
        Log.e("Minute", "" + c.get(Calendar.MINUTE));
        Log.e("SECOND", "" + c.get(Calendar.SECOND));

        if (createMemo) {
            orderNo = "O" + bf.getPreference(SR_ID) + year + day + month + getCurrentTime24ForMemo();
        }
    }

    public void SalesMemoNO(Boolean createMemo) {
        final Calendar c = Calendar.getInstance();
        year1 = c.get(Calendar.YEAR);
        month1 = c.get(Calendar.MONTH);
        day1 = c.get(Calendar.DAY_OF_MONTH);

        String day = "", month = "", year;


        String year_ = String.valueOf(year1);
        year = year_.substring(2, 4);


        if (day1 < 10)
            day = "0" + day1;
        else
            day = String.valueOf(day1);
        if (month1 + 1 < 10)
            month = "0" + (month1 + 1);
        else
            month = String.valueOf(month1 + 1);
        date = year1 + "-" + month + "-" + day;
        Log.e("OutletID", TempData.OutletID);
        Log.e("SO", getPreference("SO"));
        Log.e("Day", day);
        Log.e("year", year);
        Log.e("Month", month);
        Log.e("Hour", "" + c.get(Calendar.HOUR_OF_DAY));
        Log.e("Minute", "" + c.get(Calendar.MINUTE));
        Log.e("SECOND", "" + c.get(Calendar.SECOND));

        if (createMemo) {
            memoNo = "M" + getPreference(SR_ID) + year + month + day + getCurrentTime24ForMemo();
            Log.e("MemoNo", memoNo);
        }
    }

    public void InvoiceTemporary(String product_id, String product_name, String quantity, String price, String vat) {

        //Log.e("Cursor","null");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("product_name", product_name);
        map.put("quantity", quantity);
        map.put("price", price);
        map.put("vat", vat);
        TempData.INVOICE_DETAILS.add(map);

    }

    private void ProduSctDisplayListView() {


        ItemListFromDB.clear();

        Cursor c = db.rawQueryCoustom("SELECT DISTINCT * FROM product_boolean where boolean=" + "'" + "true" + "'" + " AND outlet_id='" + getPreference("OutletID") + "'");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String product_id = c.getString(c.getColumnIndex("product_id"));
                    String is_bonus = "";
                    String products_query = "SELECT * FROM product_history WHERE start_date<=" + "'" + memodate + "'" + " and end_date>=" + "'" + memodate + "' and product_id='" + product_id + "'";
                    Log.e("products_query", products_query);
                    Cursor c5 = db.rawQueryCoustom(products_query);
                    Log.e("QUERY COUNT:", "..............." + products_query);
                    if (c5 != null) {
                        if (c5.moveToFirst()) {
                            do {
                                String product_id_ = c5.getString(c5.getColumnIndex("product_id"));
                                is_bonus = c5.getString(c5.getColumnIndex("is_bonus"));

                                if (TempData.editMemo.equalsIgnoreCase("true")) {
                                    String Unitprice = "";
                                    Log.e("is it edit?", "YES");
                                    Log.e("salesrrrr", "SELECT * FROM " + TABLE_NAME_ORDER_DETAILS + " where product_id='" + product_id_ + "' and " + Tables.ORDER_order_number + "='" + TempData.orderNumber + "' and is_bonus='0'");
                                    Cursor c2 = db.rawQueryCoustom("SELECT * FROM " + TABLE_NAME_ORDER_DETAILS + " where product_id='" + product_id_ + "' and " + Tables.ORDER_order_number + "='" + TempData.orderNumber + "' and is_bonus='0'");
                                    if (c2 != null) {
                                        if (c2.moveToFirst()) {
                                            do {

                                                Unitprice = c2.getString(c2.getColumnIndex("price"));

                                                Log.e("salesrrrr ", "Unitprice: " + Unitprice);
                                                if (!TextUtils.isEmpty(Unitprice))
                                                    savePreference("up" + product_id, Unitprice);

                                            } while (c2.moveToNext());
                                        }
                                    }
                                }


                            } while (c5.moveToNext());
                        }
                    }


                    String product_name = "";
                    String query1 = "SELECT product_name FROM product WHERE product_id='" + product_id + "'";
                    Cursor c2 = db.rawQueryCoustom(query1);
                    if (c2 != null) {
                        if (c2.moveToFirst()) {
                            do {
                                product_name = c2.getString(0);
                            } while (c2.moveToNext());
                        }
                    }

                    Log.e("product_quentity:",  product_name);


                    String quantity = c.getString(c.getColumnIndex("quantity"));

                    Log.e("QUANTITY:", "  " + quantity);

                    String product_category_id = c.getString(c.getColumnIndex("product_category_id"));
                    String product_type_id = c.getString(c.getColumnIndex("product_type_id"));


                    if (product_type_id.equalsIgnoreCase("1")) {
                        HashMap<String, String> product_list_map = new HashMap<String, String>();
                        product_list_map.put("product_id", product_id);
                        product_list_map.put("product_name", product_name);
                        product_list_map.put("quantity", quantity);
                        product_list_map.put("is_bonus", is_bonus);
                        product_list_map.put("product_category_id", product_category_id);
                        product_list_map.put("vatt", vatCalculation(product_id, Double.parseDouble(quantity)) + "");
                       // product_list_map.put("vat", vatCalculation(product_id, Double.parseDouble(quantity)) + "");

                        ItemListFromDB.add(product_list_map);
                    } else {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("Name", product_name);
                        map.put("quantity", quantity);
                        map.put("product_id", product_id);

                        GiftItem.add(map);
                    }


                } while (c.moveToNext());
            }
            Log.e("ItemListFromDB", new Gson().toJson(ItemListFromDB));


            Log.e("ItemListFromDB prd", "ItemListFromDB prd: " + ItemListFromDB);
            Adapter = new SalesOrderDetailsAdaper1(this, ItemListFromDB, getPreference("SO"), getPreference("OutletID"));
            //		  ListView listView = (ListView) findViewById(R.id.ProductListView);
            list_add_vehicle_details.setAdapter(Adapter);

            String gift = "";
            for (int i = 0; i < GiftItem.size(); i++) {
                HashMap<String, String> map = GiftItem.get(i);
                gift = gift + map.get("Name") + "(" + map.get("quantity") + ")";
                if (i != GiftItem.size() - 1)
                    gift = gift + ", ";
            }
            if (gift.length() > 0)
                txtGift.setText(gift);
            else
                txtGift.setText("Nill");

            Log.e("GIFT", gift);
        }

        Log.e("ProductSales", " End");
    }

    private double vatCalculation(String product_id, Double quantity) {

        Double vatprice = 0.0, vatofvat = 0.0, price = 0.0, vat = 0.0;
        Cursor c = db.rawQueryCoustom("select vat from product_price where product_id='" + product_id + "' and vat!='null'");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {


            return c.getDouble(0);
        }
        return vat;
    }

    public void writeToFile(String fileName) {
        FileOutputStream fos = null;

        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SMC_Print/");

            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("ALERT", "could not create the directories");
                }
            }

            final File myFile = new File(dir, fileName + ".txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
            }

            fos = new FileOutputStream(myFile);

            String data = TempData.INVOICE_DETAILS.toString() + TempData.TempGift + TempData.TempBonus_EN;
            String hexDump = hexDump(data.getBytes(), data);
            Log.e(" hext dump:", "..." + hexDump);
            fos.write(hexDump.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProductSales.this);
        builder.setMessage(" Are you sure?");
        builder.setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        if (!TotalPrice.getText().toString().equals("0.00")) {

                            new SaveData().execute();
                        }

                        // dialog.dismiss();

                    }


                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        ///SavePrintButton.setEnabled(true);
                        SaveBtn.setEnabled(true);
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void DeleteBefore3DaysMemos() {
        String memo_no1 = "";
        String AllMemoNos = "";
        String not_in_condition = "";


        db.excQuery("DELETE FROM product_boolean WHERE outlet_id ='" + TempData
                .OutletID + "'");

        Cursor cd = db.rawQueryCoustom(" DELETE FROM product_boolean where strftime('%Y/%m/%d', updated_at) < ( strftime('%Y/%m/%d', date('now','-2 day')))");


        if (memo_no1.length() > 1) {
            AllMemoNos = memo_no1.substring(1);
            not_in_condition = "AND memo_no NOT IN (" + AllMemoNos + ")";
            Log.e("AllMemoNos ", "AllMemoNos: " + AllMemoNos);
//				Cursor c6 =db.rawQuery("SELECT memo_no FROM memos where outlet_id ='"+TempData.OutletID+"' and memo_date<=date('"+OrderDate+"') "+not_in_condition);
            String QRY = "SELECT memo_no FROM memos where outlet_id ='" + TempData.OutletID + "' and memo_date < (SELECT DATETIME('now', '-3  day')) " + not_in_condition + " and isPushed=1";
            Log.e("QRY ", "QRY " + QRY);
            Cursor c6 = db.rawQueryCoustom(QRY);
            if (c6 != null) {
                if (c6.moveToFirst()) {
                    do {
                        String memo_no6 = c6.getString(0);

                        Log.e("memo_no6 ", "memo_no6: " + memo_no6);

                        db.deleteRowOfMemo("memos", memo_no6);
                        db.deleteRowOfMemo("memo_details", memo_no6);
                        db.deleteRowOfMemo("credit_collections", memo_no6);
                        db.deleteRowOfMemo("payments", memo_no6);

                    } while (c6.moveToNext());
                }
            }
        } else {
            String QRY = "SELECT " + Tables.ORDER_order_number + " FROM order_table where outlet_id ='" + TempData.OutletID + "' and order_date < (SELECT DATETIME('now', '-3  day'))" + " and is_Pushed=1";
            Log.e("QRY ", "QRY " + QRY);
            Cursor c6 = db.rawQueryCoustom(QRY);
            if (c6 != null) {
                if (c6.moveToFirst()) {
                    do {
                        String memo_no6 = c6.getString(0);

                        Log.e("memo_no6 ", "memo_no6: " + memo_no6);

                        db.deleteRowOfMemo("memos", memo_no6);
                        db.deleteRowOfMemo("memo_details", memo_no6);
                        db.deleteRowOfMemo("credit_collections", memo_no6);
                        db.deleteRowOfMemo("payments", memo_no6);

                    } while (c6.moveToNext());
                }
            }
        }

    }

    private void tempClean() {
        orderList = Adapter.getAdapterHashMapList();
        for (int k = 0; k < orderList.size(); k++) {

            savePreference("up" + orderList.get(k).get("product_id"), "0");
//			savePreference(TempData.OutletID+orderList.get(k).get("product_id"),"0");
        }
    }

    private void TempBonus() {

        for (int k = 0; k < TempData.TotalBonusProductList.size(); k++) {
            savePreference("bonus" + TempData.TotalBonusProductList.get(k).get("product_id"), "0.0");
            Log.e("TempBonus()", "TempBonus()" + getPreference("bonus" + TempData.TotalBonusProductList.get(k).get("product_id")));
        }
    }

    private void bonus_show_WithoutDB() {
        Log.e("TotalBonusProductList", " TotalBonusProductList for Sales Order details: " + TempData.TotalBonusProductList);
        String bonus_value1 = "";
        Log.e("TemData.TotlBonsProList.size()", TempData.TotalBonusProductList.size() + "");
        for (int i = 0; i < TempData.TotalBonusProductList.size(); i++) {

            HashMap<String, String> product_list_map2 = new HashMap<String, String>();

            product_list_map2 = TempData.TotalBonusProductList.get(i);

            Log.e("product_id: ", "" + product_list_map2.get("product_id"));
            Log.e("product_name: ", "" + product_list_map2.get("product_name"));
            Log.e("quantity: ", "" + product_list_map2.get("quantity"));

            String product_id = product_list_map2.get("product_id");
            String product_name = product_list_map2.get("product_name");
            String quantity = product_list_map2.get("quantity");

            String bonus_value2 = product_name + "(" + quantity + ")";
            bonus_value1 = bonus_value1 + "," + bonus_value2;
            String bonus_value = bonus_value1.substring(1);

            Log.e("bonus_value", bonus_value);
            txtBonusExtra.setText(bonus_value);
            Log.e("txtBonusExtra", txtBonusExtra.getText().toString());
            txtBonusExtra.setVisibility(View.VISIBLE);

        }
    }

    private void BonusButtonShow() {
        double boolean_quantity = 0.0;
        double memo_details_quantity = 0.0;

        ArrayList<HashMap<String, String>> BonusItemListFromDB = new ArrayList<HashMap<String, String>>();
        BonusItemListFromDB.clear();
        Cursor c2 = null;
        c2 = db.rawQueryCoustom("SELECT product_id FROM product_history WHERE start_date<=" + "'" + memodate + "'" + " and end_date>=" + "'" + memodate + "' AND is_bonus=1");
        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {

                    String product_id = c2.getString(0);
                    String EditQuery = " and quantity>0";
                    if (TempData.editMemo.equalsIgnoreCase("true")) {
                        EditQuery = "and quantity>=0";
                    }
                } while (c2.moveToNext());

				/*c2.close();
				db.close();*/
            }
            Log.e("BonusItemListFromDB--", "BonusItemListFromDB--" + BonusItemListFromDB.toString());

            Log.e("AddBonusBtn", memo_details_quantity + "-" + boolean_quantity
                    + "=" + (memo_details_quantity - boolean_quantity));

        }
    }

    public String injectable_product_check() {

        return "0";
        /*String is_injectable = "0";

        String outlet = getPreference("OutletID");
        Log.d("outlete", outlet);

        String query2 = "SELECT product_id FROM product_boolean WHERE outlet_id='" + getPreference("OutletID") + "' AND boolean='true'";
        Cursor c23 = db.rawQueryCoustom(query2);
        int count22 = c23.getCount();
        Log.e("QUERY COUNT:", "..............." + count22);
        if (c23 != null) {
            if (c23.moveToFirst()) {
                do {
                    String productId = c23.getString(0);
                    String query = "SELECT is_injectable FROM product WHERE product_id='" + productId + "'";
                    Log.e("QUERY:", "..............." + query);
                    Cursor c = db.rawQueryCoustom(query);
                    int count23 = c.getCount();
                    Log.e("QUERY COUNT:", "..............." + count23);

                    if (c != null) {
                        if (c.moveToFirst()) {
                            do {
                                is_injectable = c.getString(0);
                                Log.d("isinjectableoutuput", is_injectable + "productid" + productId);
                            } while (c.moveToNext());
                        }
                    }


                } while (c23.moveToNext());
            }
        }

        return is_injectable;*/
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (TempData.DELIVERY_EDIT){
                startActivity(new Intent(ProductSales.this, DeliveryReport.class));
                finish();
            }
            else if (MEMO_EDIT){
                startActivity(new Intent(ProductSales.this, MemoReport.class));
                finish();
            }else {
                Intent idd = new Intent(ProductSales.this, Sales_Memo.class);
                startActivity(idd);
                finish();
            }


        }
        return super.onKeyDown(keyCode, event);

    }

    private void getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(ProductSales.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProductSales.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            android.location.Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            android.location.Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            android.location.Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps != null) {
                double latGPS = LocationGps.getLatitude();
                double longiGPS = LocationGps.getLongitude();
                lattitude = String.valueOf(latGPS);
                longitude = String.valueOf(longiGPS);

                Log.e("Location", "getLocation: +" + "Your Location:" + "\n" + "Latitude= " + lattitude + "\n" + "Longitude= " + longitude);
                // Toast.makeText(ProductSales.this,"Lat : "+lattitude+" Long: "+longitude, Toast.LENGTH_LONG).show();
            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi = LocationNetwork.getLongitude();

                lattitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                // Log.e("Loc", "getLocation: "+"Your Location:"+"\n"+"Latitude= "+lattitude+"\n"+"Longitude= "+longitude);
                //Toast.makeText(ProductSales.this,"Lat : "+lattitude+" Long: "+longitude, Toast.LENGTH_LONG).show();

                bf.savePreference("lat", lattitude);
                bf.savePreference("lon", longitude);
            } else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi = LocationPassive.getLongitude();

                lattitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                bf.savePreference("lat", lattitude);
                bf.savePreference("lon", longitude);

                Log.e("Loc", "getLocation: " + "Your Location:" + "\n" + "Latitude= " + lattitude + "\n" + "Longitude= " + longitude);
                // Toast.makeText(ProductSales.this,"Lat : "+lattitude+" Long: "+longitude, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }

            //Thats All Run Your App
        }

    }

    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public class SaveData extends AsyncTask<Void, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Void doInBackground(Void... voids) {

            SaveDataToTable();

            return null;
        }
    }


}
