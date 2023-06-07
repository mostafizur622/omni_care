package com.srapp;


import static com.srapp.Db_Actions.Tables.MEMOS;
import static com.srapp.Db_Actions.Tables.MEMOS_ORDER_NUMBER;
import static com.srapp.Db_Actions.Tables.MEMOS_TOTAL_Vat;
import static com.srapp.Db_Actions.Tables.MEMOS_editable;
import static com.srapp.Db_Actions.Tables.MEMOS_for_memo_delete;
import static com.srapp.Db_Actions.Tables.MEMOS_from_app;
import static com.srapp.Db_Actions.Tables.MEMOS_gross_value;
import static com.srapp.Db_Actions.Tables.MEMOS_is_pushed;
import static com.srapp.Db_Actions.Tables.MEMOS_market_id;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_date;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_date_time;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_number;
import static com.srapp.Db_Actions.Tables.MEMOS_outlet_id;
import static com.srapp.Db_Actions.Tables.MEMOS_table_id;
import static com.srapp.Db_Actions.Tables.MEMO_DETAILS;
import static com.srapp.Db_Actions.Tables.MEMO_DETAILS_Unit_id;
import static com.srapp.Db_Actions.Tables.MEMO_DETAILS_memo_number;
import static com.srapp.Db_Actions.Tables.MEMO_DETAILS_order_number;
import static com.srapp.Db_Actions.Tables.MEMO_DETAILS_vat;
import static com.srapp.Db_Actions.Tables.ORDER_STATUS;
import static com.srapp.Db_Actions.Tables.ORDER_TOTAL_VAT;
import static com.srapp.Db_Actions.Tables.ORDER_editable;
import static com.srapp.Db_Actions.Tables.ORDER_for_order_delete;
import static com.srapp.Db_Actions.Tables.ORDER_from_app;
import static com.srapp.Db_Actions.Tables.ORDER_gross_value;
import static com.srapp.Db_Actions.Tables.ORDER_is_out_of_plan;
import static com.srapp.Db_Actions.Tables.ORDER_is_pushed;
import static com.srapp.Db_Actions.Tables.ORDER_market_id;
import static com.srapp.Db_Actions.Tables.ORDER_order_date;
import static com.srapp.Db_Actions.Tables.ORDER_order_date_time;
import static com.srapp.Db_Actions.Tables.ORDER_order_number;
import static com.srapp.Db_Actions.Tables.ORDER_outlet_id;
import static com.srapp.Db_Actions.Tables.ORDER_plan_id;
import static com.srapp.Db_Actions.Tables.ORDER_table_id;
import static com.srapp.Db_Actions.Tables.PROCESSING_COMPELETE;
import static com.srapp.Db_Actions.Tables.PROCESSING_PENDING;
import static com.srapp.Db_Actions.Tables.PRODUCT_BOOLEAN_QUANTITY;
import static com.srapp.Db_Actions.Tables.PRODUCT_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRICE_PRICE;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME_BN;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MEMOS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MEMO_DETAILS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER_DETAILS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_PRODUCT;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_PRODUCT_BOOLEAN;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;
import static com.srapp.TempData.BPSelected_bonus;
import static com.srapp.TempData.BPSelected_option_id;
import static com.srapp.TempData.BPSelected_policy_type;
import static com.srapp.TempData.BPSelected_product;
import static com.srapp.TempData.BPSelected_set;
import static com.srapp.TempData.BonusArrayList;
import static com.srapp.TempData.ConvertTOBangla;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.ORDER_TO_MEMO;
import static com.srapp.TempData.PROCESSING_ON_SERVER;
import static com.srapp.TempData.PolicySetRelation;
import static com.srapp.TempData.policyArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.srapp.Adapter.AdapterForMemoDetails;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.OrderDetailsModel;
import com.srapp.Util.Parent;
import com.srapp.print.PrintActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsOrderReport extends Parent implements BasicFunctionListener, DBListener {
    public static String discount_info = "";
    public static String discount_info_BN = "";
    String memoNo;
    double subtotal = 0.00;
    ListView list;
    Button btnEdit, cancel, print;
    AdapterForMemoDetails adapter;
    OrderDetailsModel orderDetailsModel;
    ArrayList<HashMap<String, String>> Data;
    TextView txtGift, txtBonus, total_price, discountt, sub_total, title, vat, user_txt_view, discount_details,warning;
    Data_Source db;
    Double tdiscount = 0.0;
    ImageView homeBtn, backBtn;
    int memo = 0;
    int Back = 0;
    boolean memo_create = true;
    HashMap<String, String> map;
    BasicFunction bf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_order_report);
        db = new Data_Source(this, this, this);
        bf = new BasicFunction(this, this);
        txtGift = findViewById(R.id.txtGift);
        btnEdit = findViewById(R.id.btnEdit);
        discountt = findViewById(R.id.discount);
        sub_total = findViewById(R.id.sub_total);
        warning = findViewById(R.id.warning);
        txtBonus = findViewById(R.id.txtBonus);
        discount_details = findViewById(R.id.discount_details);
        vat = findViewById(R.id.vat);
        cancel = findViewById(R.id.cancel);
        print = findViewById(R.id.print);
        total_price = findViewById(R.id.total_price);
        list = findViewById(R.id.list);
        title = findViewById(R.id.title);
        user_txt_view = findViewById(R.id.user_txt_view);
        user_txt_view.setText(bf.getPreference(SR_ID));
        FirebaseCrashlytics.getInstance().setUserId(getPreference("sr_uname"));
        if (MEMO_EDIT) {
            title.setText("Invoice Details");
            btnEdit.setText("Edit Invoice");
            print.setText("Print Invoice");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date1 = simpleDateFormat.parse(TempData.MemoDate);
                Date date2 = simpleDateFormat.parse(getCurrentDate());
                if (printDifference(date1, date2) > 3) {
                    btnEdit.setVisibility(View.GONE);
                }
                if (TempData.DayCloseMemoEditable != null)
                    if (TempData.DayCloseMemoEditable.equalsIgnoreCase("1")) {
                        btnEdit.setVisibility(View.VISIBLE);
                    }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        } else {

            title.setText("Order Details");
            btnEdit.setText("Edit Order");
            print.setText("Print Order");
            cancel.setText("Cancel Order");

            if (ORDER_TO_MEMO == 1) {
                cancel.setText("Deliver Now");
                cancel.setVisibility(View.VISIBLE);
            }

            if (TempData.ORDER_STATUE == 2) {
                cancel.setVisibility(View.GONE);
            }
        }
        String Query = "";
        if (!TempData.MEMO_EDIT)
            Query = "SELECT " + ORDER_TOTAL_VAT + " FROM " + TABLE_NAME_ORDER + " where " + ORDER_order_number + "='" + TempData.orderNumber + "'";
        else
            Query = "SELECT " + MEMOS_TOTAL_Vat + " FROM " + TABLE_NAME_MEMOS + " where " + MEMOS_memo_number + "='" + TempData.memoNumber + "'";
        Cursor c = db.rawQueryCoustom(Query);
        Log.e("Quearymem", Query + " sixe" + c.getCount());
        c.moveToFirst();

        if (c != null && c.getCount() > 0) {

            vat.setText(c.getString(0));
        }


        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {


                startActivity(new Intent(DetailsOrderReport.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (Back == 1) {
                    startActivity(new Intent(DetailsOrderReport.this, DeliveryReport.class).putExtra("a_from", 1));
                    finish();
                } else if (MEMO_EDIT) {
                    startActivity(new Intent(DetailsOrderReport.this, MemoReport.class));
                    finish();

                } else if (ORDER_TO_MEMO == 1) {
                    startActivity(new Intent(DetailsOrderReport.this, DeliveryReport.class).putExtra("a_from", 1));
                    finish();

                } else if (ORDER_TO_MEMO == 0 && !MEMO_EDIT) {

                    startActivity(new Intent(DetailsOrderReport.this, Order_Report_Activity.class));
                    finish();
                }

            }
        });


        if (getIntent() != null) {
            memo = getIntent().getIntExtra("memo", 0);

        }
        if (MEMO_EDIT) {
            cancel.setVisibility(View.GONE);
        } else {
            if (TempData.ORDER_STATUE == 1 && ORDER_TO_MEMO == 0)
                cancel.setVisibility(View.GONE);
        }
        if (TempData.ORDER_STATUE == PROCESSING_ON_SERVER){
            warning.setVisibility(View.VISIBLE);
        }

        Log.e("ordernum", TempData.orderNumber + "no");
        db.getOrderDetails();

        SaleAbleList();
        SaleAbleListForAll();
        Bonus();
        Gift();


        print.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String Query = "Log";

                if (MEMO_EDIT) {
                    Query = "SELECT outlet_id,market_id,discount_value,total_vat FROM " + TABLE_NAME_MEMOS + " where " + MEMOS_memo_number + "='" + TempData.memoNumber + "'";

                } else {

                    Query = "SELECT outlet_id,market_id,discount_value,total_vat FROM " + TABLE_NAME_ORDER + " where " + ORDER_order_number + "='" + TempData.orderNumber + "'";
                }

                Log.e("Query", Query);
                Cursor c7 = db.rawQueryCoustom(Query);
                if (c7.getCount() > 0) {
                    if (c7.moveToFirst()) {
                        do {

                            String outletId = c7.getString(0);
                            String market_id = c7.getString(1);
                            TempData.DISCOUNT = c7.getDouble(2);
                            TempData.VAT = c7.getDouble(3);
                            Log.e("printVat", String.valueOf(TempData.VAT));

                            vat.setText(TempData.VAT + "");
                            Cursor c8 = db.rawQueryCoustom("SELECT m.market_name,t.thana_name FROM outlets O LEFT JOIN markets m ON (O.market_id = m.market_id) INNER JOIN thana as t on m.thana_id=t.thana_id where o.outlet_id='" + outletId + "' limit 1");
                            if (c8.getCount() > 0) {
                                if (c8.moveToFirst()) {
                                    do {

                                        TempData.tempMarket = c8.getString(0);
                                        TempData.tempThana = c8.getString(1);


                                    } while (c8.moveToNext());
                                }
                            }


                            Cursor c6 = db.rawQueryCoustom("SELECT OC.outlet_category_name , o.address, o.mobile  , oc.outlet_category_name_bangla FROM outlets O LEFT JOIN outlet_categories OC ON (O.outlet_category_id = OC.outlet_category_id) where outlet_id='" + outletId + "'");

                            if (c6.getCount() > 0) {
                                if (c6.moveToFirst()) {
                                    do {

                                        String outlet_category_name = c6.getString(0);
                                        String outlet_category_name_bn = c6.getString(3);
                                        savePreference("OutletCategoryName", outlet_category_name);
                                        savePreference("OutletCategoryNameBN", outlet_category_name_bn);
                                        savePreference("Outletaddress", c6.getString(1));
                                        savePreference("OutletMobile", c6.getString(2));


                                    } while (c6.moveToNext());
                                }
                            }


                        } while (c7.moveToNext());
                    }
                }

                //TempData.TempBonus="";
                if (MEMO_EDIT) {
                    Intent idn = new Intent(DetailsOrderReport.this, PrintActivity.class);
                    idn.putExtra("From", "MemoDetails");
                    startActivity(idn);
                    finish();
                } else if (ORDER_TO_MEMO == 1) {
                    Intent idn = new Intent(DetailsOrderReport.this, PrintActivity.class);
                    idn.putExtra("From", "delivery");
                    startActivity(idn);
                    finish();

                } else {
                   Intent idn;

                   /*  if (Build.BRAND.equals("HUAWEI")){
                        idn = new Intent(DetailsOrderReport.this, PrintActivityP.class);
                    }

                    else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) { //Symphony
                        idn = new Intent(DetailsOrderReport.this, PrintActivityP.class);
                    }

                    else {
                        idn = new Intent(DetailsOrderReport.this, PrintActivity.class);
                    }*/

                    idn = new Intent(DetailsOrderReport.this, PrintActivity.class);
                    idn.putExtra("From", "MemoDetails");
                    startActivity(idn);

                    finish();

                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ORDER_TO_MEMO != 1) {

                    if (TempData.ORDER_STATUE != PROCESSING_COMPELETE) {
                        if (TempData.isPushed.equalsIgnoreCase("1")) {
                            if (bf.isInternetOn()) {

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("mac", bf.getPreference("mac"));
                                    jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                                    jsonObject.put(ORDER_order_number, TempData.orderNumber);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                              //  bf.getResponceData(URL.CANCEL_ORDER, jsonObject.toString(), 111);

                                ProgressDialog dailog = CheckConnection(DetailsOrderReport.this,"Getting Orders...");
                                if (dailog==null)
                                    return;
                                getJAPi().CANCEL_ORDER(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.body());
                                            dailog.dismiss();
                                            db.excQuery("delete from " + TABLE_NAME_ORDER + " where " + ORDER_order_number + " ='" + TempData.orderNumber + "'");
                                            db.excQuery("delete from " + TABLE_NAME_ORDER_DETAILS + " where " + ORDER_order_number + " ='" + TempData.orderNumber + "'");
                                            finish();

                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        dailog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(DetailsOrderReport.this, "NO Internet Connection", Toast.LENGTH_LONG).show();
                                btnEdit.setEnabled(true);
                                return;
                            }
                        } else {

                            db.excQuery("delete from " + TABLE_NAME_ORDER + " where " + ORDER_order_number + " ='" + TempData.orderNumber + "'");
                            db.excQuery("delete from " + TABLE_NAME_ORDER_DETAILS + " where " + ORDER_order_number + " ='" + TempData.orderNumber + "'");
                            finish();
                        }
                    } else {
                        Toast.makeText(DetailsOrderReport.this, "This Order is Already Processed Please cancel invoice to Edit", Toast.LENGTH_LONG).show();
                    }
                } else if (ORDER_TO_MEMO == 1) {
                    cancel.setEnabled(false);
                    if (bf.isInternetOn() && memo_create) {
                        memo_create = false;
                        MakeMemo();

                        Back = 1;
                    } else {
                        cancel.setEnabled(true);
                        Toast.makeText(DetailsOrderReport.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    }


                }

            }
        });


        btnEdit.setOnClickListener(v -> {
            btnEdit.setEnabled(false);
            clearPrefarance();

            db.excQuery("delete from " + TABLE_NAME_PRODUCT_BOOLEAN);
            Log.e("disable", "disable");
            TempData.editMemo = "true";
            //  Log.e("editMemo No:", TempData.orderNumber = TempData.orderNumber);
            String Query1 = "";
            if (!TempData.MEMO_EDIT)
                Query1 = "SELECT " + ORDER_table_id + "," + ORDER_order_number + ", " + ORDER_is_pushed + "," + ORDER_outlet_id + "," + ORDER_order_date + "," + ORDER_market_id + ", " + ORDER_from_app + ", " + ORDER_order_date_time + ", " + ORDER_editable + ", " + ORDER_gross_value + "," + ORDER_for_order_delete + "," + ORDER_STATUS + "," + ORDER_is_out_of_plan + "," + ORDER_plan_id + " FROM " + TABLE_NAME_ORDER + " where " + ORDER_order_number + "='" + TempData.orderNumber + "'";
            else
                Query1 = "SELECT " + MEMOS_table_id + "," + MEMOS_ORDER_NUMBER + ", " + MEMOS_is_pushed + "," + MEMOS_outlet_id + "," + MEMOS_memo_date + "," + MEMOS_market_id + ", " + MEMOS_from_app + ", " + MEMOS_memo_date_time + ", " + MEMOS_editable + ", " + MEMOS_gross_value + "," + MEMOS_for_memo_delete + "," + MEMOS_memo_number + " FROM " + TABLE_NAME_MEMOS + " where " + MEMOS_memo_number + "='" + TempData.orderNumber + "'";


            Cursor c1 = db.rawQueryCoustom(Query1);
            Log.e("Quearymem", Query1 + " sixe" + c1.getCount());
            if (c1 != null) {

                Log.e("NOt", "null" + c1.getCount());

                if (c1.moveToFirst()) {
                    do {

                        TempData.orderNumber = c1.getString(1);
                        TempData.MemoTotalView = c1.getString(0);
                        TempData.isPushed = c1.getString(2);
                        TempData.OutletID = c1.getString(3);
                        TempData.MemoDate = c1.getString(4);
                        TempData.MarketID = c1.getString(5);
                        TempData.From_App = c1.getString(6);
                        TempData.MemoDateTime = c1.getString(7);
                        TempData.DayCloseMemoEditable = c1.getString(8);
                        TempData.gross_value = c1.getString(9);
                        TempData.for_memo_delete = c1.getString(10);
                        if (!TempData.MEMO_EDIT) {
                            TempData.ORDER_STATUE = c1.getInt(11);
                            TempData.ORDER_PlanVisit = c1.getInt(12);
                            TempData.ORDER_plan_id = c1.getInt(13);
                        } else {
                            TempData.memoNumber = c1.getString(11);
                        }

                        Log.e("OutletID test------", "~~~~~~" + TempData.OutletID);
                        Log.e("MemoTotalView test----", "~~~~~~" + TempData.MemoTotalView);
                        Log.e("----PrimaryID----", "~~~~~~" + TempData.orderNumber);
                        Log.e("----MarketID----", "~~~~~~" + TempData.MarketID);
                        Log.e("----From_App----", "~~~~~~" + TempData.From_App);
                        Log.e("----for_memo_delete----", "~~~~~~" + TempData.for_memo_delete);
                        Log.e("----ORDER_STATUE----", "~~~~~~" + TempData.ORDER_STATUE);
                        Log.e("----ORDER_id----", "~~~~~~" + TempData.SelectedOrderID);


                    } while (c1.moveToNext());
                }
            }

            BPSelected_bonus.clear();
            BPSelected_product.clear();
            BPSelected_set.clear();
            BPSelected_policy_type.clear();
            BPSelected_option_id.clear();
            policyArrayList.clear();
            PolicySetRelation.clear();

            Cursor setzerotoqty = db.rawQueryCoustom("SELECT * FROM product ORDER BY product_order ASC");

            if (setzerotoqty != null) {
                if (setzerotoqty.moveToFirst()) {
                    do {
                        String product_id = setzerotoqty.getString(setzerotoqty.getColumnIndex("product_id"));
                        savePreference(product_id, "0.0");

                    } while (setzerotoqty.moveToNext());
                }
            }
            exiting_policy_map_createdOrder(TempData.orderNumber);

            /*if (!TempData.MEMO_EDIT) {
                exiting_policy_map_createdOrder(TempData.orderNumber);
            }
            else {
                exiting_policy_map_createdOrder(TempData.orderNumber);
            }*/

            db.excQuery("delete from product_boolean where outlet_id='" + TempData.OutletID + "'");

            if (TempData.ORDER_STATUE == PROCESSING_PENDING || TempData.ORDER_STATUE == 3 || ORDER_TO_MEMO == 1 || MEMO_EDIT) {


                if (TempData.isPushed.equalsIgnoreCase("1")) {
                    if (bf.isInternetOn()) {
                        SwitchToSaleOrder();
                    } else {
                        Toast.makeText(DetailsOrderReport.this, "NO Internet Connection", Toast.LENGTH_LONG).show();
                        btnEdit.setEnabled(true);
                        return;
                    }
                } else {

                    SwitchToSaleOrder();
                }
            } else {
                Toast.makeText(DetailsOrderReport.this, "This Order is Already Processed Please cancel invoice to Edit", Toast.LENGTH_LONG).show();
            }


        });



     /*   productList = new ArrayList<>();
        oAdapter = new OderDetailsAdapter();
        oAdapter.setOrders(productList);*/
    }

    private void exiting_policy_map_createdOrder(String order_no) {

        String table_name="";
        String order_no_con="";
        if (!TempData.MEMO_EDIT)
        {
            table_name="order_details ";
            order_no_con=" order_number='"+order_no+"' ";
        }
        else
        {
            table_name="memo_details ";
            order_no_con=" memo_number='"+TempData.memoNumber+"' ";
        }
        String query = "SELECT " +
                "product_id,"+
                "quantity,"+
                "policy_id,"+
                "policy_type,"+
                "selected_set,"+
                "provided_qty,"+
                "is_bonus "+
                "virtual_product_id "+
                "FROM " +
                table_name +
                "WHERE " +
                order_no_con+" AND " +
                "policy_id is not null AND policy_id !='null'" +
                " AND policy_id !=''  AND policy_id!=0";
        Cursor policy_wise_order_details = db.rawQueryCoustom(query);
        Log.e("MemoDetailsQuery",query);
        Log.e("policy_memo_data_print", DatabaseUtils.dumpCursorToString(policy_wise_order_details));


        try {
            if (policy_wise_order_details != null) {
                if (policy_wise_order_details.moveToFirst()) {
                    do {

                        if(policy_wise_order_details.getString((policy_wise_order_details.getColumnIndex("is_bonus"))).equals("3")
                                && policy_wise_order_details.getString((policy_wise_order_details.getColumnIndex("policy_type"))).equals("3")
                        ) {
                            BPSelected_policy_type.put(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id")),"2");
                        }
                        else if(policy_wise_order_details.getString((policy_wise_order_details.getColumnIndex("policy_type")))=="3")
                        {
                            BPSelected_policy_type.put(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id")),"1");
                        }
                        String selected_set=policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("selected_set"));
                        if(selected_set==null)
                            continue;
                        BPSelected_set.put(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id")),selected_set);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_id", swapProductID(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("product_id")),policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("virtual_product_id"))));
                        map.put("policy_id", policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id")));
                        map.put("set", selected_set);
                        map.put("qty", policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("quantity")));
                        try {
                            if(
                                    !policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("provided_qty")).equals("")
                                            && Double.valueOf(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("provided_qty")))>0.0
                            ) {
                                map.put("provided_qty", policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("provided_qty")));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(BPSelected_bonus.get(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id")))==null)
                        {
                            HashMap<String, HashMap<String, HashMap<String, String>>> set_map = new HashMap<>();
                            HashMap<String, HashMap<String, String>> product_map = new HashMap<>();
                            product_map.put(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("product_id")), map);
                            set_map.put(selected_set,product_map );
                            BPSelected_bonus.put(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id")),set_map);
                        }
                        else if(BPSelected_bonus.get(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id"))).get(selected_set)==null)
                        {
                            HashMap<String, HashMap<String, String>> product_map = new HashMap<>();
                            product_map.put(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("product_id")), map);

                            BPSelected_bonus.get(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id"))).put(selected_set,product_map);
                        }
                        BPSelected_bonus.get(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("policy_id"))).get(selected_set).put(policy_wise_order_details.getString(policy_wise_order_details.getColumnIndex("product_id")),map);
                    } while (policy_wise_order_details.moveToNext());
                }
                policy_wise_order_details.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("BPSelected_bonus_me",new Gson().toJson(BPSelected_bonus));
        Log.e("BPSelected_set_me",new Gson().toJson(BPSelected_set));
        Log.e("BPSelected_type_me",new Gson().toJson(BPSelected_policy_type));
    }
    public static String swapProductID(String product_id, String virtual_product_id) {


        if (virtual_product_id!=null && !virtual_product_id.equalsIgnoreCase("0") && !virtual_product_id.equalsIgnoreCase("null")){
            Log.e("swapProductID",   product_id+"  "+virtual_product_id);
            return virtual_product_id;
        }else {
            return product_id;
        }

    }
    private void MakeMemo() {

        Cursor c = db.rawQueryCoustom("select * from order_table where order_number='" + TempData.orderNumber + "'");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 7; i < MEMOS.length - 2; i++) {

                map.put(MEMOS[i], c.getString(c.getColumnIndex(MEMOS[i])));

            }
            map.put(MEMOS_gross_value, c.getString(c.getColumnIndex(ORDER_gross_value)));
            map.put(MEMOS_ORDER_NUMBER, c.getString(c.getColumnIndex(ORDER_order_number)));
            map.put(MEMOS_memo_date, getCurrentDate());
            map.put(MEMOS_memo_date_time, getCurrentDateTime());
            map.put(MEMOS_memo_number, SalesMemoNO());
            map.put(MEMOS_for_memo_delete, c.getString(c.getColumnIndex(ORDER_for_order_delete)));
            map.put(Tables.MEMOS_created_at, getCurrentDateTime());
            map.put(Tables.MEMOS_updated_at, getCurrentDateTime());
            db.InsertTable(map, "memos");

        }

        Cursor c1 = db.rawQueryCoustom("select * from order_details where order_number='" + TempData.orderNumber + "'");

        c1.moveToFirst();
        if (c1 != null && c1.getCount() > 0) {
            do {
                HashMap<String, String> map1 = new HashMap<>();
                for (int i = 4; i < MEMO_DETAILS.length - 2; i++) {
                    Log.e("column&value", MEMO_DETAILS[i] + " & " + c1.getString(c1.getColumnIndex(MEMO_DETAILS[i])));
                    map1.put(MEMO_DETAILS[i], c1.getString(c1.getColumnIndex(MEMO_DETAILS[i])));

                }
                map1.put(MEMO_DETAILS_memo_number, memoNo);
                map1.put(MEMO_DETAILS_vat, "0");
                map1.put(MEMO_DETAILS_order_number, TempData.orderNumber);
                map1.put(Tables.MEMOS_created_at, getCurrentDateTime());
                map1.put(Tables.MEMO_DETAILS_memo_date, getCurrentDate());
                map1.put(Tables.MEMOS_updated_at, getCurrentDateTime());
                db.InsertTable(map1, "memo_details");
            } while (c1.moveToNext());

        }

        db.generateSingleMemo(memoNo);

    }

    public String SalesMemoNO() {
        final Calendar c = Calendar.getInstance();
        int year1 = c.get(Calendar.YEAR);
        int month1 = c.get(Calendar.MONTH);
        int day1 = c.get(Calendar.DAY_OF_MONTH);

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
        String date = year1 + "-" + month + "-" + day;
        Log.e("OutletID", TempData.OutletID);
        Log.e("SO", getPreference("SO"));
        Log.e("Day", day);
        Log.e("year", year);
        Log.e("Month", month);
        Log.e("Hour", "" + c.get(Calendar.HOUR_OF_DAY));
        Log.e("Minute", "" + c.get(Calendar.MINUTE));
        Log.e("SECOND", "" + c.get(Calendar.SECOND));


        memoNo = "M" + getPreference(SR_ID) + year + month + day + getCurrentTime24ForMemo();
        Log.e("MemoNo", memoNo);
        TempData.memoNumber = memoNo;

        return memoNo;
    }


    public long printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;


        return elapsedDays;
    }

    private void SaleAbleList() {
        discount_info = "";
        discount_info_BN = "";
        Data = new ArrayList<HashMap<String, String>>();
        Data.clear();
        String memo_id = "";
        if (!MEMO_EDIT)
            memo_id = "SELECT " + PRODUCT_ID + ", " + PRODUCT_BOOLEAN_QUANTITY + ", " + PRODUCT_PRICE_PRICE + ",vat,discount_type,discount_amount, virtual_product_id FROM " + TABLE_NAME_ORDER_DETAILS + " WHERE " + ORDER_order_number + "='" + TempData.orderNumber + "' AND product_type='0'";
        else
            memo_id = "SELECT " + PRODUCT_ID + ", " + PRODUCT_BOOLEAN_QUANTITY + ", " + PRODUCT_PRICE_PRICE + ",vat,discount_type,discount_amount, virtual_product_id FROM " + TABLE_NAME_MEMO_DETAILS + " WHERE " + MEMOS_memo_number + "='" + TempData.memoNumber + "' AND product_type='0'";

        Log.e("MemoDetails", memo_id);

        Cursor cursor = db.rawQueryCoustom(memo_id, "testeeee");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String product_name = "";
                String product_namebn = "";
                do {
                    Cursor c1 = db.rawQueryCoustom("SELECT " + PRODUCT_PRODUCT_NAME +","+PRODUCT_PRODUCT_NAME_BN+ " From " + TABLE_NAME_PRODUCT + " WHERE " + PRODUCT_ID + "='" + cursor.getString(0) + "'");
                    if (c1 != null) {
                        if (c1.moveToFirst()) {
                            do {
                                product_name = c1.getString(0);
                                product_namebn = c1.getString(1);
                            } while (c1.moveToNext());
                        }

                    }
                    map = new HashMap<String, String>();
                    map.put(PRODUCT_PRODUCT_NAME, product_name);
                    map.put(PRODUCT_PRODUCT_NAME_BN, product_namebn);
                    map.put(PRODUCT_BOOLEAN_QUANTITY, String.valueOf(cursor.getDouble(1)));
                    map.put("quantity_bn", ConvertTOBangla(String.valueOf(cursor.getDouble(1))));
                    map.put(PRODUCT_PRICE_PRICE, roundTwoDecimals(cursor.getDouble(2)));
                    map.put("price_bn", ConvertTOBangla(roundTwoDecimals(cursor.getDouble(2))));
                    String tPrice = roundTwoDecimals(cursor.getDouble(1) * cursor.getDouble(2));

                    map.put("total_price_bn", ConvertTOBangla(String.valueOf(tPrice)));
                    map.put("total_price", String.valueOf(tPrice));
                    map.put("vat_bn", ConvertTOBangla(roundTwoDecimals(cursor.getDouble(3))) + "%");
                    map.put("vat", roundTwoDecimals(cursor.getDouble(3)) + "%");
                    map.put(PRODUCT_ID, swapProductID(cursor.getString(0),cursor.getString(6)));
                    Data.add(map);
                    subtotal = subtotal + Double.parseDouble(tPrice);
                    discount_info = discount_info + getdiscount(cursor.getDouble(1), cursor.getDouble(2), cursor.getInt(4), cursor.getDouble(5), product_name);
                    discount_info_BN = discount_info_BN + getdiscountBN(cursor.getDouble(1), cursor.getDouble(2), cursor.getInt(4), cursor.getDouble(5), product_namebn);

                } while (cursor.moveToNext());
            }
        }
        total_price.setText(subtotal + "");
        TempData.InvoiceTotal = String.valueOf(subtotal);
        discount_details.setText(discount_info);
       // discount_details.setText(discount_data);

        Log.e("Size", String.valueOf(Data.size()));

        adapter = new AdapterForMemoDetails(DetailsOrderReport.this, Data);

        list.setAdapter(adapter);

        //TempData.INVOICE_DETAILS.addAll(Data);
          TempData.INVOICE_DETAILS = Data;

          getDiscount(subtotal + "");


    }

    private String getdiscount(double qty, double price, int distype, double disamount, String product_name) {

        Log.e("dicunttexttest", qty + " " + price + " " + distype + " " + disamount + " " + product_name);

        if (disamount <= 0) {
            return "";
        }
        Double discount = 0.0;

        Log.e("discount_type", distype + "");

        /*if (distype == 0) {

            discount = (price / 100.00) * disamount;

        } else {
            discount = disamount;

        }*/

        Log.e("discount_type", tdiscount + "  , " + discount);
        tdiscount = tdiscount + (disamount * qty);


        return product_name + "(" + (roundTwoDecimals(disamount * qty)) + ")";

    }
 private String getdiscountBN(double qty, double price, int distype, double disamount, String product_name) {

        Log.e("dicunttexttest", qty + " " + price + " " + distype + " " + disamount + " " + product_name);

        if (disamount <= 0) {
            return "";
        }
        Double discount = 0.0;

        Log.e("discount_type", distype + "");

        /*if (distype == 0) {

            discount = (price / 100.00) * disamount;

        } else {
            discount = disamount;

        }*/

        Log.e("discount_type", tdiscount + "  , " + discount);
        tdiscount = tdiscount + (disamount * qty);


        return product_name + "(" + ConvertTOBangla(roundTwoDecimals(disamount * qty)) + ")";

    }


    private void getDiscount(String s) {
        int distype = 0;
        Double discountp = 0.0;
        Cursor cursor = db.rawQueryCoustom("select discount_percent,discount_type from discounts where memo_value <=" + s + " and date_from <= '" + TempData.MemoDate + "' and date_to>='" + TempData.MemoDate + "' order by memo_value DESC limit 1");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            discountp = cursor.getDouble(0);
            distype = TempData.DISTYPE = cursor.getInt(1);
        }
        Double discount = 0.0;
        Double memoValue = Double.parseDouble(s);
        if (distype == 1) {
            TempData.DISCOUNTP = discountp;
            discount = (memoValue * discountp) / 100;
        } else if (distype == 2) {

            discount = discountp;
        }

        String memo_id = "";
        if (!MEMO_EDIT)
            memo_id = "SELECT discount_value ,gross_value FROM " + TABLE_NAME_ORDER + " WHERE " + ORDER_order_number + "= '" + TempData.orderNumber+"'" ;
        else
            memo_id = "SELECT discount_value ,gross_value FROM " + TABLE_NAME_MEMOS + " WHERE " + MEMOS_memo_number + "= '" + TempData.memoNumber+"'" ;



        Cursor c = db.rawQueryCoustom(memo_id);
        c.moveToFirst();
        if (c.getCount()>0 && c!=null){
            discountt.setText(c.getString(0));
            sub_total.setText(c.getString(1));
        }



       /* tdiscount = tdiscount + discount;
        Log.e("discounttt", tdiscount + " ," + discount);
        discountt.setText(roundTwoDecimals(tdiscount));
        sub_total.setText(roundTwoDecimals(memoValue - tdiscount));*/


    }

    private void SaleAbleListForAll() {
        // TODO Auto-generated method stub


        ArrayList<HashMap<String, String>> Data = new ArrayList<HashMap<String, String>>();
        Data.clear();
        String memo_id = "";
        if (!MEMO_EDIT)
            memo_id = "SELECT " + PRODUCT_ID + ", " + PRODUCT_BOOLEAN_QUANTITY + ", " + PRODUCT_PRICE_PRICE + " , virtual_product_id FROM " + TABLE_NAME_ORDER_DETAILS + " WHERE " + ORDER_order_number + "='" + TempData.orderNumber + "'";
        else
            memo_id = "SELECT " + PRODUCT_ID + ", " + PRODUCT_BOOLEAN_QUANTITY + ", " + PRODUCT_PRICE_PRICE + " , virtual_product_id FROM " + TABLE_NAME_MEMO_DETAILS + " WHERE " + MEMOS_memo_number + "='" + TempData.memoNumber + "'";

        Log.e("MemoDetails", memo_id);

        Cursor cursor = db.rawQueryCoustom(memo_id);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String product_name = "";
                String product_name_bn = "";
                do {
                    String productId = cursor.getString(0);
                    Log.e("productId: ", productId);
                    Cursor c1 = db.rawQueryCoustom("SELECT " +  PRODUCT_PRODUCT_NAME +","+PRODUCT_PRODUCT_NAME_BN+  " From " + TABLE_NAME_PRODUCT + " WHERE " + PRODUCT_ID + "='" + productId + "'");
                    Log.e("raw Query : ", "SELECT product_name From products WHERE product_id='" + productId + "'");
                    if (c1 != null) {
                        if (c1.moveToFirst()) {
                            do {
                                product_name = c1.getString(0);
                                product_name_bn = c1.getString(1);
                            } while (c1.moveToNext());
                        }

                    }
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(PRODUCT_PRODUCT_NAME, product_name);
                    map.put(PRODUCT_PRODUCT_NAME_BN, product_name_bn);
                    map.put(PRODUCT_BOOLEAN_QUANTITY, String.valueOf(cursor.getDouble(1)));
                    map.put(PRODUCT_BOOLEAN_QUANTITY+"_bn", ConvertTOBangla(String.valueOf(cursor.getDouble(1))));
                    map.put(PRODUCT_PRICE_PRICE, roundTwoDecimals(cursor.getDouble(2)));
                    map.put(PRODUCT_PRICE_PRICE+"_bn", ConvertTOBangla(roundTwoDecimals(cursor.getDouble(2))));
                    String tPrice = roundTwoDecimals(cursor.getDouble(1) * cursor.getDouble(2));
                    map.put("total_price", String.valueOf(tPrice));
                    map.put("total_price_bn", ConvertTOBangla(String.valueOf(tPrice)));
                    map.put(PRODUCT_ID, swapProductID(cursor.getString(0),cursor.getString(3)));
                    Data.add(map);

                } while (cursor.moveToNext());
            }
        }
        Log.e("Size", String.valueOf(Data.size()));

	/*	adapter=new AdapterForMemoDetails(MemoDetails.this,Data);
		ListView inst_list=(ListView)findViewById(R.id.inst_list);
		inst_list.setAdapter(adapter);*/

        TempData.INVOICE_DETAILS_FOR_ALL_PRODUCT = Data;


    }


    private void Bonus() {

        Log.e("T1", TempData.TempBonus_EN + "no data");
        TempData.TempBonus_EN = "";

        ArrayList<HashMap<String, String>> bonusList = new ArrayList<HashMap<String, String>>();

        String memoQuery = "";
        if (!MEMO_EDIT)
            memoQuery = "SELECT " + PRODUCT_ID + ", " + PRODUCT_BOOLEAN_QUANTITY + ", " + MEMO_DETAILS_Unit_id + ", virtual_product_id FROM " + TABLE_NAME_ORDER_DETAILS + " WHERE " + ORDER_order_number + "='" + TempData.orderNumber + "' AND product_type='2'";
        else
            memoQuery = "SELECT " + PRODUCT_ID + ", " + PRODUCT_BOOLEAN_QUANTITY + ", " + MEMO_DETAILS_Unit_id + " , virtual_product_id FROM " + TABLE_NAME_MEMO_DETAILS + " WHERE " + MEMOS_memo_number + "='" + TempData.memoNumber + "' AND product_type='2'";

        Log.e("memoQuery", memoQuery);

        Cursor cursor = db.rawQueryCoustom(memoQuery);
        Log.e("memoQueryCount", String.valueOf(cursor.getCount()));

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String product_name = "";
                String product_name_bn = "";
                do {
                    String productId = cursor.getString(0);

                    Cursor c1 = db.rawQueryCoustom("SELECT " +  PRODUCT_PRODUCT_NAME +","+PRODUCT_PRODUCT_NAME_BN + " From " + TABLE_NAME_PRODUCT + " WHERE " + PRODUCT_ID + "='" + productId + "'");
                    if (c1 != null) {
                        if (c1.moveToFirst()) {
                            do {
                                product_name = c1.getString(0);
                                product_name_bn = cursor.getString(1);
                            } while (c1.moveToNext());
                        }

                    }

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("product_id", swapProductID(cursor.getString(0),cursor.getString(3)));
                    map.put("product_name", product_name);
                    map.put("product_name_bn", product_name_bn);
                    map.put("quantity", cursor.getString(1));
                    map.put("quantity_bn", ConvertTOBangla(cursor.getString(1)));
                    map.put("Unit_id", cursor.getString(2));
                    savePreference("bonus" + cursor.getString(0), cursor.getString(1));
                    bonusList.add(map);
                } while (cursor.moveToNext());
            }
        }


        String bonus_bn = "";
        String bonus_en = "";

         TempData.BonusShowList.clear();

        for (int i = 0; i < bonusList.size(); i++) {

            HashMap<String, String> map = bonusList.get(i);
            bonus_bn = bonus_bn + map.get("product_name_bn") + "(" + map.get("quantity_bn") + getUnitname(map.get("Unit_id"),"unit_name_bangla") + ")";
            bonus_en = bonus_bn + map.get("product_name") + "(" + map.get("quantity") + getUnitname(map.get("Unit_id"),"unit_name") + ")";

            HashMap<String, String> product_map = new HashMap<String, String>();

            product_map.put("bonus_name", map.get("product_name"));
            product_map.put("quantity", map.get("quantity"));

            TempData.BonusShowList.add(product_map);

            if (i != bonusList.size() - 1) {
                bonus_bn = bonus_bn + ", ";
                bonus_en = bonus_en + ", ";
            }

        }

        Log.e("bonusShow", bonus_bn);


        if (bonus_en.length() > 0) {
            TempData.TempBonus_EN = "";
            Log.e("bonusShow", bonus_bn);
            LinearLayout llBonus = (LinearLayout) findViewById(R.id.llBonus);
            llBonus.setVisibility(View.VISIBLE);

            txtBonus.setText(bonus_en);
            TempData.TempBonus_EN = bonus_en;
            TempData.TempBonus_BN = bonus_bn;

        } else {
            LinearLayout llBonus = (LinearLayout) findViewById(R.id.llBonus);
            llBonus.setVisibility(View.INVISIBLE);
        }

        Log.e("T", TempData.TempBonus_EN + "no data");
    }


    private String getUnitname(String unit_id,String column) {

        if (unit_id == null) {
            return "";
        }

        Cursor c = db.rawQueryCoustom("select "+column+" from unit where unit_id='" + unit_id + "'", "getunitname");
        c.moveToFirst();
        if (c.getCount() > 0) {
            return " " + c.getString(0);
        }
        return "";
    }

    void clearPrefarance() {


        Cursor c = db.rawQueryCoustom("select product_id from product");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            do {
                savePreference("bonus" + c.getString(0), "0.0");
            } while (c.moveToNext());


        }

    }

    public void SwitchToSaleOrder() {


        savePreference("OutletID", TempData.OutletID);
        Log.e("BonusArrayList:", "..........." + BonusArrayList.toString());
        Log.e("GiftArrayList:", "..........." + TempData.GiftArrayList.toString());


        new productToBolleanTable().execute();


    }

    @Override
    public void OnLocalDBdataRetrive(final String json) {

        runOnUiThread(new Runnable() {
            public void run() {
              //  bf.getResponceData(URL.CREATE_MEMO, json, 102);

                ProgressDialog dailog = CheckConnection(DetailsOrderReport.this,"Creating Memo...");
                if (dailog==null)
                    return;
                try {
                    getJAPi().CREATE_MEMO(convertTORequestdata(new JSONObject(json))).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                dailog.dismiss();


                                try {

                                   /* if (jsonObject.has("NAME"))
                                        if (jsonObject.getString("NAME").equalsIgnoreCase("TANVIR")) {
                                            db.excQuery("delete from memos where memo_number='" + memoNo + "'");
                                            db.excQuery("delete from memos where memo_number='" + memoNo + "'");
                                            db.excQuery("update ORDER_table set is_complete='1' , status = '2' WHERE order_number = '" + TempData.orderNumber + "'");
                                            Toast.makeText(DetailsOrderReport.this, "Memo Create Failed For  Internet Problem", Toast.LENGTH_LONG).show();
                                            ORDER_TO_MEMO = 0;
                                            MEMO_EDIT = true;
                                            Back = 1;
                                            return;
                                        }*/

                                    if (jsonObject.getJSONObject("memo").getString("status").equalsIgnoreCase("1")) {
                                        db.excQuery("update ORDER_table set is_complete='1' , status = '2' WHERE order_number = '" + TempData.orderNumber + "'");
                                        Toast.makeText(DetailsOrderReport.this, jsonObject.getJSONObject("memo").getString("message"), Toast.LENGTH_LONG).show();
                                        cancel.setVisibility(View.GONE);
                                        ORDER_TO_MEMO = 0;
                                        MEMO_EDIT = true;
                                        Back = 1;


                                    } else {

                                        db.excQuery("delete from memos  WHERE memo_number = '" + memoNo + "'");
                                        db.excQuery("delete from memo_details  WHERE memo_number = '" + memoNo + "'");
                                        Toast.makeText(DetailsOrderReport.this, jsonObject.getJSONObject("memo").getString("message"), Toast.LENGTH_LONG).show();


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    db.excQuery("delete from memos  WHERE memo_number = '" + memoNo + "'");
                                    db.excQuery("delete from memo_details  WHERE memo_number = '" + memoNo + "'");
                                }


                                Log.e("json", jsonObject.toString());

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            db.excQuery("delete from memos  WHERE memo_number = '" + memoNo + "'");
                            db.excQuery("delete from memo_details  WHERE memo_number = '" + memoNo + "'");
                            dailog.dismiss();
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
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

    private void setBonus() {
        //prTotalBonusProductList
    }

    int getIndexSaleAble(String value) {
        int pos = -1;
        for (int i = 0; i < TempData.INVOICE_DETAILS.size(); i++) {
            HashMap<String, String> map = TempData.INVOICE_DETAILS.get(i);
            // Log.e("PRODUCT_ID salable", map.get("product_id"));
            if (map.get("product_id").equalsIgnoreCase(value))
                pos = i;
        }

        return pos;
    }

    int getIndexBonus(String value) {
        int pos = -1;
        for (int i = 0; i < BonusArrayList.size(); i++) {
            HashMap<String, String> map = BonusArrayList.get(i);
            // Log.e("PRODUCT_ID bonus", map.get("product_id"));
            if (map.get("product_id").equalsIgnoreCase(value))
                pos = i;
        }

        return pos;
    }

    int getIndexGift(String value) {
        int pos = -1;
        for (int i = 0; i < TempData.GiftArrayList.size(); i++) {
            HashMap<String, String> map = TempData.GiftArrayList.get(i);
            //Log.e("PRODUCT_ID gift", map.get("product_id"));
            if (map.get("product_id").equalsIgnoreCase(value))
                pos = i;
        }

        return pos;
    }

    private void Gift() {
        // TODO Auto-generated method stub
        ArrayList<HashMap<String, String>> giftList = new ArrayList<HashMap<String, String>>();
        String memoGIFTQuery;
        if (!MEMO_EDIT)
            memoGIFTQuery = "SELECT " + PRODUCT_ID + ", " + PRODUCT_BOOLEAN_QUANTITY + " FROM " + TABLE_NAME_ORDER_DETAILS + " WHERE " + ORDER_order_number + "='" + TempData.orderNumber + "' AND product_type='1'";
        else
            memoGIFTQuery = "SELECT " + PRODUCT_ID + ", " + PRODUCT_BOOLEAN_QUANTITY + " FROM " + TABLE_NAME_MEMO_DETAILS + " WHERE " + MEMO_DETAILS_memo_number + "='" + TempData.memoNumber + "' AND product_type='1'";
        Log.e("memoGIFTQuery", memoGIFTQuery);
        Cursor cursor = db.rawQueryCoustom(memoGIFTQuery);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String product_name = "";
                String product_name_bn = "";
                do {

                    String productId = cursor.getString(0);
                    Cursor c1 = db.rawQueryCoustom("SELECT " + PRODUCT_PRODUCT_NAME +","+PRODUCT_PRODUCT_NAME_BN + " From " + TABLE_NAME_PRODUCT + " WHERE " + PRODUCT_ID + "='" + productId + "'");

                    if (c1 != null) {
                        if (c1.moveToFirst()) {
                            do {
                                product_name = c1.getString(0);
                                product_name_bn = c1.getString(0);

                            } while (c1.moveToNext());
                        }

                    }
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("product_id", cursor.getString(0));
                    map.put("product_name", product_name);
                    map.put("product_name_BN", product_name);
                    map.put("quantity", cursor.getString(1));
                    giftList.add(map);
                } while (cursor.moveToNext());
            }
        }

        Log.e("giftList : ", giftList.toString());

        String gift = "";
        for (int i = 0; i < giftList.size(); i++) {
            HashMap<String, String> map = giftList.get(i);
            gift = gift + map.get("product_name") + "(" + map.get("quantity") + ")";
            if (i != giftList.size() - 1)
                gift = gift + ", ";

            Log.e("gift: ", gift);
        }
        if (gift.length() > 0) {
            txtGift.setText(gift);
            TempData.TempGift = gift;
        } else {
            LinearLayout llGift = (LinearLayout) findViewById(R.id.llGift);
            llGift.setVisibility(View.GONE);
        }
        TempData.GiftArrayList = giftList;
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        if (i == 111) {


        }

        if (ORDER_TO_MEMO == 1) {
            startActivity(new Intent(DetailsOrderReport.this, DeliveryReport.class).putExtra("a_from", 1));
            finish();
            Log.e("memoredirect", "Order_Report_Activity");
        }


    }

    @Override
    public void OnConnetivityError() {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Log.e("Back", Back + "" + MEMO_EDIT + "" + ORDER_TO_MEMO);

            if (Back == 1) {
                startActivity(new Intent(DetailsOrderReport.this, DeliveryReport.class).putExtra("a_from", 1));
                finish();
                Log.e("Back1", Back + "" + MEMO_EDIT + "" + ORDER_TO_MEMO);
            } else if (MEMO_EDIT) {
                startActivity(new Intent(DetailsOrderReport.this, MemoReport.class));
                finish();
                Log.e("Back2", Back + "" + MEMO_EDIT + "" + ORDER_TO_MEMO);

            } else if (ORDER_TO_MEMO == 1) {
                startActivity(new Intent(DetailsOrderReport.this, DeliveryReport.class).putExtra("a_from", 1));
                finish();
                Log.e("Back3", Back + "" + MEMO_EDIT + "" + ORDER_TO_MEMO);

            } else if (ORDER_TO_MEMO == 0 && !MEMO_EDIT) {

                startActivity(new Intent(DetailsOrderReport.this, Order_Report_Activity.class));
                finish();
                Log.e("Back4", Back + "" + MEMO_EDIT + "" + ORDER_TO_MEMO);
            }

        }
        return super.onKeyDown(keyCode, event);

    }

    public class productToBolleanTable extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(DetailsOrderReport.this);
            progressDialog.setTitle("Please Wait p.....");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }


        @Override
        protected Void doInBackground(Void... voids) {
            Cursor c3 = db.rawQueryCoustom("SELECT\n" +
                    "  P.product_id,\n" +
                    "  P.product_name,\n" +
                    "  P.product_category_id,\n" +
                    "  P.product_type_id\n" +
                    "FROM product AS P\n" +
                    "inner join product_combinations pc on pc.product_id=P.product_id\n" +
                    "WHERE p.product_type_id = '1'\n" +
                    "GROUP BY P.product_id,\n" +
                    "         P.product_name,\n" +
                    "         P.product_category_id,\n" +
                    "         P.product_type_id\n" +
                    "ORDER BY P.product_order ASC");


            if (c3 != null) {
                if (c3.moveToFirst()) {
                    do {

                        String product_id = c3.getString(c3.getColumnIndex("product_id"));
                        String product_name = c3.getString(c3.getColumnIndex("product_name"));

                        String product_category_id = c3.getString(c3.getColumnIndex("product_category_id"));
                        String product_type_id = c3.getString(c3.getColumnIndex("product_type_id"));

                        boolean is_checked = false;

                        int saleable = getIndexSaleAble(product_id);
                        int gift = getIndexGift(product_id);
                        int bonus = getIndexBonus(product_id);
                        String product_quantity = "0.00";
                        Log.e("tquery",TempData.INVOICE_DETAILS.toString());
                        Log.e("saleablequery",saleable+" "+product_id);
                        if (saleable >= 0) {
                            is_checked = true;
                            product_quantity = TempData.INVOICE_DETAILS.get(saleable).get("quantity");


                        } else if (gift >= 0) {
                            is_checked = true;
                            product_quantity = TempData.GiftArrayList.get(gift).get("quantity");
                            Log.e("query",TempData.GiftArrayList.get(saleable).get("quantity"));

                        }/*else if (bonus>=0){
                            is_checked=true;
                            product_quantity=TempData.BonusArrayList.get(bonus).get("quantity");
                            Log.e("type_id",product_type_id);


                        }*/

                        //TotalBonusProductList = BonusArrayList;


                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("outlet_id", getPreference("OutletID"));
                        map.put("product_id", product_id);
                        map.put("quantity", product_quantity);
                        map.put("boolean", is_checked + "");
                        map.put("product_category_id", product_category_id);
                        map.put("product_type_id", product_type_id);

                        db.InsertTable(map, TABLE_NAME_PRODUCT_BOOLEAN);


                    } while (c3.moveToNext());

                }
            }

            setBonus();

            Intent idn = new Intent(DetailsOrderReport.this, Sales_Memo.class);
            idn.putExtra("flag", 1);
            progressDialog.dismiss();
            startActivity(idn);
            finish();


            return null;
        }
    }




}
