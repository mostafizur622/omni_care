package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bxl.config.editor.BXLConfigLoader;
import com.google.gson.Gson;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Adapter.SrStoreStatusAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Model.OrderDetailsModel;
import com.srapp.Util.ParentActivity;
import com.srapp.print.StockPrintActivity;
import com.srapp.print.SummeryPrintActivity;
import com.srapp.print.Util;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import NewPrint.BixolonPrinter;

public class SrStoreStatus extends ParentActivity implements BasicFunctionListener {

    RecyclerView recyclerView;
    SrStoreStatusAdapter statusAdapter;
    ArrayList<String>productNameList, actual_qtyList, invoice_qtyList, db_stockList, order_qtyList;
    OrderDetailsModel obj;
    Spinner productTypeSpinner, productCategorySpinner;
    ImageView img;
    Data_Source ds;
    HashMap<String,ArrayList<String>> productCategoryList,productTypeList,stock;
    BasicFunction basicFunction;
    String typeId, categoryId;
    int filter= 0, byTypeId = 1, byCatId =2, byBoth = 3;
    boolean isLoading = false;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    ImageButton print;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr_store_status);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        print = findViewById(R.id.print);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        basicFunction = new BasicFunction(this,this);

        recyclerView = findViewById(R.id.sr_status_list_recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        ds = new Data_Source(this);

        productCategoryList = new HashMap<>();
        productCategoryList = ds.getProductCategoryeList();
        productTypeList =new HashMap<>();
        productTypeList = ds.getProductTypeList();


        productTypeSpinner = findViewById(R.id.thana_spinner);
        productCategorySpinner = findViewById(R.id.market_spinner);

        if (productTypeList != null && productTypeList.get(Tables.PPRODCUT_TYPE_PRODUCT_NAME).size()>0) {

            productTypeList.get(Tables.PPRODCUT_TYPE_PRODUCT_NAME).add(0, "All");
            productTypeList.get(Tables.PRODUCT_TYPE_ID).add(0, "x0x");

            SpinnerAdapter typeData = new SpinnerAdapter(this, R.layout
                    .spinner_item, productTypeList.get(Tables.PPRODCUT_TYPE_PRODUCT_NAME));
            productTypeSpinner.setAdapter(typeData);

            productCategoryList.get(Tables.PPRODUCT_CATEGORY_C_NAME).add(0, "All");
            productCategoryList.get(Tables.PPRODUCT_CATEGORY_C_id).add(0, "x0x");

            final SpinnerAdapter categoryData = new SpinnerAdapter(this, R.layout
                    .spinner_item, productCategoryList.get(Tables.PPRODUCT_CATEGORY_C_NAME));
            productCategorySpinner.setAdapter(categoryData);
        }

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SrStoreStatus.this, StockPrintActivity.class));
                finish();
              //printReport();
            }
        });


        productTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                typeId = productTypeList.get(Tables.PRODUCT_TYPE_ID).get(position);
                Log.e("type & cat--->", "onTypeSelect: "+typeId+"  "+categoryId );
                if (typeId != null) {

                    // only filter by type id
                    if ( !typeId.equals("x0x")) {
                        // call api by type id only

                        if (categoryId != null && !categoryId.equals("x0x")) {
                            JSONObject primaryData = new JSONObject();
                            try {
                                Log.e("if---->", "onItemSelected: " + typeId);
                                primaryData.put("mac", basicFunction.getPreference("mac"));
                                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                                primaryData.put("product_type_id", typeId);
                                primaryData.put("product_category_id", categoryId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                        }
                        else {

                            JSONObject primaryData = new JSONObject();
                            try {
                                Log.e("if---->", "onItemSelected: " + typeId);
                                primaryData.put("mac", basicFunction.getPreference("mac"));
                                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                                primaryData.put("product_type_id", typeId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                        }
                    }
                    else {
                        if (categoryId != null && !categoryId.equals("x0x")) {
                            JSONObject primaryData = new JSONObject();
                            try {
                                Log.e("if---->", "onItemSelected: " + typeId);
                                primaryData.put("mac", basicFunction.getPreference("mac"));
                                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                                primaryData.put("product_category_id", categoryId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                        }
                        else if (categoryId != null && categoryId.equals("x0x")){
                            JSONObject primaryData = new JSONObject();
                            try {
                                Log.e("if---->", "onItemSelected: type " + typeId+" cat "+categoryId);
                                primaryData.put("mac", basicFunction.getPreference("mac"));
                                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                        }

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                categoryId = productCategoryList.get(Tables.PPRODUCT_CATEGORY_C_id).get(position);
                Log.e("type & cat--->", "onCatSelect: "+typeId+"  "+categoryId );

                if ( typeId.equals("x0x") && categoryId.equals("x0x")){
                    //call api for all data in very 1st
                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

                }else {
                    if (typeId.equals("x0x") && !categoryId.equals("x0x")){
                        // call api by category id
                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac",basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                            primaryData.put("product_category_id",categoryId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

                    }
                    else if (!typeId.equals("x0x") && !categoryId.equals("x0x")){
                        //call api by type and cat id
                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac",basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                            primaryData.put("product_category_id",categoryId);
                            primaryData.put("product_type_id",typeId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

                    }
                    else if (!typeId.equals("x0x") && categoryId.equals("x0x")){
                        //call api by type and cat id
                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac",basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                            primaryData.put("product_type_id",typeId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.e("type & cat--->", "onCreate: "+typeId+"  "+categoryId );
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SrStoreStatus.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SrStoreStatus.this, Dashboard.class));
                finishAffinity();
            }
        });
    }


    private void printReport() {
        int portType = BXLConfigLoader.DEVICE_BUS_USB;
        String logicalName = "SRP-E302";
        String address = "";
        BixolonPrinter printer;
        boolean print=false;
        printer= new BixolonPrinter(this);
        print =  printer.printerOpen(portType,logicalName,address,true);
        printer.printText(MakeReportText(), 1, 2, (0 + 1));

    }


    private String MakeReportText() {

        String MainFormate = "%1$-18s %2$10s %3$10s %4$10s  %5$11s";
        StringBuffer receiptHeadBuffer = new StringBuffer();
        // String value = String.format("%1$-20s   %2$18s  %3$20s", "Start Date:"+startdate.getText().toString()," ", "End Date:"+enddate.getText().toString());
        //receiptHeadBuffer.append(value);
        receiptHeadBuffer.append("\n");
        receiptHeadBuffer.append(Util.center("SR Store Status",64));
        receiptHeadBuffer.append(Util.center("SMC Enterprise Ltd.",64));


        receiptHeadBuffer.append("\n................................................................");
        //receiptHeadBuffer.append("\n");

        String value4 = String.format(MainFormate, "Items","Actual", "Invoice", "DB  ", "Order");
        String value5 = String.format(MainFormate, "","Qty  ", "Qty  ", "Stock", "Qty  ");
        receiptHeadBuffer.append(value4);
        receiptHeadBuffer.append(value5);
        receiptHeadBuffer.append("\n................................................................");
        for(int i = 0 ; i<productNameList.size() ; i++) {

            String product_name = productNameList.get(i);
            String EC = roundTwoDecimals(actual_qtyList.get(i));
            String OC = roundTwoDecimals(invoice_qtyList.get(i));
            String Qty = roundTwoDecimals(db_stockList.get(i));
            String BQty = roundTwoDecimals(order_qtyList.get(i));



            if (product_name.length() <= 18) {
                String value1 = String.format(MainFormate, product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append(value1);
            } else {
                String firstPart = product_name.substring(0, 18);
                String secondPart = product_name.substring(18, product_name.length());
                String value1 = String.format(MainFormate, firstPart, EC, OC, Qty, BQty);
                receiptHeadBuffer.append(value1);
                String value2 = String.format(MainFormate, secondPart, "", "", "", "");
                receiptHeadBuffer.append(value2);
                //receiptHeadBuffer.append("\n");
            }
        }

        receiptHeadBuffer.append("\n................................................................");
        receiptHeadBuffer.append("\n\n"+"Sales Representative");
        receiptHeadBuffer.append("\n"+getPreference("sr_name")+"  DB:("+getPreference("db_name")+")");
        receiptHeadBuffer.append("\nAddress: "+getPreference("db_address"));
        receiptHeadBuffer.append("\nMobile No: "+getPreference("db_mobile"));

        receiptHeadBuffer.append("\n\n"+"Printing date: "+basicFunction.getCurrentDateTime());
        receiptHeadBuffer.append("\n"+"Thank You!");
        receiptHeadBuffer.append("\n");



        return receiptHeadBuffer.toString();


    }

    public String getPreference(String key)
    {
        String value="";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        value = prefs.getString(key, "0");

        return value;

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        Log.e("json response", "OnServerResponce: ---> "+jsonObject );
        if (i==101){
            productNameList = new ArrayList<>();
            actual_qtyList = new ArrayList<>();
            invoice_qtyList = new ArrayList<>();
            db_stockList = new ArrayList<>();
            order_qtyList = new ArrayList<>();
            stock = new HashMap<>();
            try {
                if (jsonObject.getJSONArray("stock_info").length()>0){
                    for (int x = 0; x < jsonObject.getJSONArray("stock_info").length();x++){
                        productNameList.add( jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("product_name"));
                        String id = jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("quantity");
                        actual_qtyList.add(id);
                        double stock, book, available,bonusQty,bonusBookQty,bonusAvailable;

                        stock = Double.parseDouble(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("actual_invoice_qty"));

                        invoice_qtyList.add( String.valueOf(stock));
                        bonusQty = Double.parseDouble(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("actual_invoice_qty"));
                        bonusBookQty = Double.parseDouble(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("quantity"));
                        bonusAvailable = bonusQty + bonusBookQty;

                        db_stockList.add(String.valueOf(bonusAvailable));
                        order_qtyList.add(String.valueOf(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("actual_booking_quantity")));
                    }

                    stock.put(Tables.PRODUCT_PRODUCT_NAME,productNameList);
                    stock.put("actual_qty", actual_qtyList);
                    stock.put("invoice_qty", invoice_qtyList);
                    stock.put("db_stock", db_stockList);
                    stock.put("order_qty", order_qtyList);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("json", "OnServerResponce: got res");
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            if (stock != null && stock.size()>0 ) {
                Log.e("inside if---->", "OnServerResponce: "+stock );
                statusAdapter = new SrStoreStatusAdapter();
                statusAdapter.bindData(stock);
                recyclerView.setAdapter(statusAdapter);

                TempData.STOCK_STORE_PRINT.putAll(stock);

                Log.i("STOCK_STORE_PRINT", new Gson().toJson(TempData.STOCK_STORE_PRINT));
            }else {
                recyclerView.setAdapter(null);
            }

        }

    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(SrStoreStatus.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
    }

    public void loadMoreData(int page){
        isLoading= true;
        Log.e("page number......", ".......>>loadMoreData: "+page );
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac",basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
            if (filter == byTypeId){
                primaryData.put("product_type_id",typeId);

            }else if (filter == byCatId){
                primaryData.put("product_category_id",categoryId);
            }else if (filter == byBoth){
                primaryData.put("product_type_id",typeId);
                primaryData.put("product_category_id",categoryId);
            }
            primaryData.put("page",page);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),102);

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( SrStoreStatus.this, Dashboard.class));
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}



/*
package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bxl.config.editor.BXLConfigLoader;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Adapter.SrStoreStatusAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Model.OrderDetailsModel;
import com.srapp.Util.ParentActivity;
import com.srapp.print.Util;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import NewPrint.BixolonPrinter;

public class SrStoreStatus extends ParentActivity implements BasicFunctionListener {

    RecyclerView recyclerView;
    SrStoreStatusAdapter statusAdapter;
    ArrayList<String>productNameList, actual_qtyList, invoice_qtyList, db_stockList, order_qtyList;
    OrderDetailsModel obj;
     Spinner productTypeSpinner, productCategorySpinner;
    ImageView img;
    Data_Source ds;
    HashMap<String,ArrayList<String>> productCategoryList,productTypeList,stock;
    BasicFunction basicFunction;
    String typeId, categoryId;
    int filter= 0, byTypeId = 1, byCatId =2, byBoth = 3;
    boolean isLoading = false;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    ImageButton print;
    // Create image-----------------------
    ProgressDialog pd;
    Button saveButton;

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr_store_status);
        //Start--Create image for print android ----------------------------
        ActivityCompat.requestPermissions(SrStoreStatus.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        pd = new ProgressDialog(SrStoreStatus.this);

        //---------End-------------------------------------------------------
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        print = findViewById(R.id.print);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        basicFunction = new BasicFunction(this,this);

        recyclerView = findViewById(R.id.sr_status_list_recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        ds = new Data_Source(this);

        productCategoryList = new HashMap<>();
        productCategoryList = ds.getProductCategoryeList();
        productTypeList =new HashMap<>();
        productTypeList = ds.getProductTypeList();


        productTypeSpinner = findViewById(R.id.thana_spinner);
        productCategorySpinner = findViewById(R.id.market_spinner);

        if (productTypeList != null && productTypeList.get(Tables.PPRODCUT_TYPE_PRODUCT_NAME).size()>0) {

            productTypeList.get(Tables.PPRODCUT_TYPE_PRODUCT_NAME).add(0, "All");
            productTypeList.get(Tables.PRODUCT_TYPE_ID).add(0, "x0x");

            SpinnerAdapter typeData = new SpinnerAdapter(this, R.layout
                    .spinner_item, productTypeList.get(Tables.PPRODCUT_TYPE_PRODUCT_NAME));
            productTypeSpinner.setAdapter(typeData);

            productCategoryList.get(Tables.PPRODUCT_CATEGORY_C_NAME).add(0, "All");
            productCategoryList.get(Tables.PPRODUCT_CATEGORY_C_id).add(0, "x0x");

            final SpinnerAdapter categoryData = new SpinnerAdapter(this, R.layout
                    .spinner_item, productCategoryList.get(Tables.PPRODUCT_CATEGORY_C_NAME));
            productCategorySpinner.setAdapter(categoryData);
        }

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //printReport();

                if (flag==0){
                    ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                    scrollView.setVisibility(View.VISIBLE);
                    MakeReportText();
                    flag=1;
                }

                else {
                    SaveClick(); // Save converted layout image to storage
                    printReport();
                }
            }
        });


        productTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                typeId = productTypeList.get(Tables.PRODUCT_TYPE_ID).get(position);
                Log.e("type & cat--->", "onTypeSelect: "+typeId+"  "+categoryId );
                if (typeId != null) {

                    // only filter by type id
                    if ( !typeId.equals("x0x")) {
                        // call api by type id only

                        if (categoryId != null && !categoryId.equals("x0x")) {
                            JSONObject primaryData = new JSONObject();
                            try {
                                Log.e("if---->", "onItemSelected: " + typeId);
                                primaryData.put("mac", basicFunction.getPreference("mac"));
                                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                                primaryData.put("product_type_id", typeId);
                                primaryData.put("product_category_id", categoryId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                        }
                        else {

                            JSONObject primaryData = new JSONObject();
                            try {
                                Log.e("if---->", "onItemSelected: " + typeId);
                                primaryData.put("mac", basicFunction.getPreference("mac"));
                                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                                primaryData.put("product_type_id", typeId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                        }
                    }
                    else {
                        if (categoryId != null && !categoryId.equals("x0x")) {
                            JSONObject primaryData = new JSONObject();
                            try {
                                Log.e("if---->", "onItemSelected: " + typeId);
                                primaryData.put("mac", basicFunction.getPreference("mac"));
                                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));
                                primaryData.put("product_category_id", categoryId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                        }
                        else if (categoryId != null && categoryId.equals("x0x")){
                            JSONObject primaryData = new JSONObject();
                            try {
                                Log.e("if---->", "onItemSelected: type " + typeId+" cat "+categoryId);
                                primaryData.put("mac", basicFunction.getPreference("mac"));
                                primaryData.put("sales_person_id", basicFunction.getPreference("sales_person_id"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                        }

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                categoryId = productCategoryList.get(Tables.PPRODUCT_CATEGORY_C_id).get(position);
                Log.e("type & cat--->", "onCatSelect: "+typeId+"  "+categoryId );

                if ( typeId.equals("x0x") && categoryId.equals("x0x")){
                    //call api for all data in very 1st
                    JSONObject primaryData = new JSONObject();
                    try {
                        primaryData.put("mac",basicFunction.getPreference("mac"));
                        primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

                }else {
                    if (typeId.equals("x0x") && !categoryId.equals("x0x")){
                        // call api by category id
                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac",basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                            primaryData.put("product_category_id",categoryId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

                    }
                    else if (!typeId.equals("x0x") && !categoryId.equals("x0x")){
                        //call api by type and cat id
                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac",basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                            primaryData.put("product_category_id",categoryId);
                            primaryData.put("product_type_id",typeId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

                    }
                    else if (!typeId.equals("x0x") && categoryId.equals("x0x")){
                        //call api by type and cat id
                        JSONObject primaryData = new JSONObject();
                        try {
                            primaryData.put("mac",basicFunction.getPreference("mac"));
                            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                            primaryData.put("product_type_id",typeId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.e("type & cat--->", "onCreate: "+typeId+"  "+categoryId );
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SrStoreStatus.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SrStoreStatus.this, Dashboard.class));
                finishAffinity();
            }
        });
    }

    private void printReport() {
        int portType = BXLConfigLoader.DEVICE_BUS_USB;
        String logicalName = "SRP-E302";
        String address = "";
        BixolonPrinter printer;
        boolean print=false;
        printer= new BixolonPrinter(this);
        print =  printer.printerOpen(portType,logicalName,address,true);
       // printer.printText(MakeReportText(), 1, 2, (0 + 1)); //previous System data print

        Bitmap reportPrintBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/report.png"); // print report as a image from storage for supporting all font like bangla arabic and so on
        printer.printImage(reportPrintBitmap, 1200,2, 300);

    }


    private String MakeReportText() {

        String MainFormate = "%1$-18s      %2$10s   %3$10s   %4$10s    %5$11s";
        String SecondFormate = "%1$-18s             %2$10s     %3$10s    %4$10s    %5$11s";
        StringBuffer receiptHeadBuffer = new StringBuffer();
       //String value = String.format("%1$-20s   %2$18s  %3$20s", "Start Date:"+startdate.getText().toString()," ", "End Date:"+enddate.getText().toString());
        //receiptHeadBuffer.append(value);
        //receiptHeadBuffer.append("\n");
        receiptHeadBuffer.append(Util.center("\t\t\t\t\t\tSR Store Status\n\n",64));
        receiptHeadBuffer.append(Util.center("SMC Enterprise Ltd.\n",64));

        receiptHeadBuffer.append("\n..............................................................................................................\n");
        //receiptHeadBuffer.append("\n");

        String value4 = String.format(MainFormate, "Items","Actual", "Invoice", "DB   ", "Order");
        String value5 = String.format(SecondFormate, "     ","Qty  ", "Qty    ", "Stock", "Qty  ");
        receiptHeadBuffer.append("\n"+value4);
        receiptHeadBuffer.append("\n"+value5);
        receiptHeadBuffer.append("\n..............................................................................................................\n");
        for(int i = 0 ; i<productNameList.size() ; i++) {

            String product_name = productNameList.get(i);
            String EC = roundTwoDecimals(actual_qtyList.get(i));
            String OC = roundTwoDecimals(invoice_qtyList.get(i));
            String Qty = roundTwoDecimals(db_stockList.get(i));
            String BQty = roundTwoDecimals(order_qtyList.get(i));

            if (product_name.length() <= 18) {
                String value1 = "";
                if (product_name.length() == 7 || product_name.length() == 8) {
                     value1 = String.format("%1$-18s      %2$10s   %3$10s   %4$10s    %5$11s", product_name, EC, OC, Qty, BQty);
                     receiptHeadBuffer.append("\n"+value1+"\n");
                }

                else if (product_name.length() == 14 || product_name.length() == 15 || product_name.length() == 16 || product_name.length() == 17) {
                    value1 = String.format("%1$-18s    %2$10s   %3$10s   %4$10s    %5$11s", product_name, EC, OC, Qty, BQty);
                    receiptHeadBuffer.append("\n"+value1+"\n");
                }

                else {
                    value1 = String.format(MainFormate, product_name, EC, OC, Qty, BQty);
                    receiptHeadBuffer.append("\n"+value1+"\n");
                }
            } else {
                String firstPart = product_name.substring(0, 18);
                String secondPart = product_name.substring(18, product_name.length());
                String value1 = String.format(MainFormate, firstPart, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n"+value1+"\n");
                String value2 = String.format(MainFormate, secondPart, "", "", "", "");
                receiptHeadBuffer.append(value2+"\n");
                //receiptHeadBuffer.append("\n");
            }
        }

        receiptHeadBuffer.append("\n................................................................");
        receiptHeadBuffer.append("\n\n"+"Sales Representative");
        receiptHeadBuffer.append("\n"+getPreference("sr_name")+"  DB:("+getPreference("db_name")+")");
        receiptHeadBuffer.append("\nAddress: "+getPreference("db_address"));
        receiptHeadBuffer.append("\nMobile No: "+getPreference("db_mobile"));

        receiptHeadBuffer.append("\n\n"+"Printing date: "+basicFunction.getCurrentDateTime());
        receiptHeadBuffer.append("\n"+"Thank You!");
        receiptHeadBuffer.append("\n");

        TextView txtText = (TextView) findViewById(R.id.txtText);
        txtText.setText(receiptHeadBuffer.toString());


        return receiptHeadBuffer.toString();

    }

    public String getPreference(String key)
    {
        String value="";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        value = prefs.getString(key, "0");

        return value;

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        Log.e("json response", "OnServerResponce: ---> "+jsonObject );
        if (i==101){
            productNameList = new ArrayList<>();
            actual_qtyList = new ArrayList<>();
            invoice_qtyList = new ArrayList<>();
            db_stockList = new ArrayList<>();
            order_qtyList = new ArrayList<>();
            stock = new HashMap<>();
            try {
                if (jsonObject.getJSONArray("stock_info").length()>0){
                    for (int x = 0; x < jsonObject.getJSONArray("stock_info").length();x++){
                        productNameList.add( jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("product_name"));
                        String id = jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("quantity");
                        actual_qtyList.add(id);
                        double stock, book, available,bonusQty,bonusBookQty,bonusAvailable;


                        stock = Double.parseDouble(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("actual_invoice_qty"));
                       */
/* Log.e("stockQuantity",stock+"");
                        book=0.00;
                        if (!jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("booking_quantity").equalsIgnoreCase("null"))
                        book = Double.parseDouble(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("booking_quantity"));
                        available = stock - book;*//*

                        invoice_qtyList.add( String.valueOf(stock));
                        bonusQty = Double.parseDouble(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("actual_invoice_qty"));
                        bonusBookQty = Double.parseDouble(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("quantity"));
                        bonusAvailable = bonusQty + bonusBookQty;

                        db_stockList.add(String.valueOf(bonusAvailable));
                        order_qtyList.add(String.valueOf(jsonObject.getJSONArray("stock_info").getJSONObject(x).getString("actual_booking_quantity")));
                    }

                    stock.put(Tables.PRODUCT_PRODUCT_NAME,productNameList);
                    stock.put("actual_qty", actual_qtyList);
                    stock.put("invoice_qty", invoice_qtyList);
                    stock.put("db_stock", db_stockList);
                    stock.put("order_qty", order_qtyList);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("json", "OnServerResponce: got res");
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            if (stock != null && stock.size()>0 ) {
                Log.e("inside if---->", "OnServerResponce: "+stock );
                statusAdapter = new SrStoreStatusAdapter();
                statusAdapter.bindData(stock);
                recyclerView.setAdapter(statusAdapter);
            }else {
                recyclerView.setAdapter(null);
            }

        }

    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(SrStoreStatus.this,"No Internet Connection",Toast.LENGTH_SHORT).show();

    }

    public void loadMoreData(int page){
        isLoading= true;
        Log.e("page number......", ".......>>loadMoreData: "+page );
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac",basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
            if (filter == byTypeId){
                primaryData.put("product_type_id",typeId);

            }else if (filter == byCatId){
                primaryData.put("product_category_id",categoryId);
            }else if (filter == byBoth){
                primaryData.put("product_type_id",typeId);
                primaryData.put("product_category_id",categoryId);
            }
            primaryData.put("page",page);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),102);

    }


    // Create image for printing Bangla----------------------------------------------------------------------------
    public void SaveClick(){
        pd.setMessage("saving your image");
        pd.show();

        LinearLayout savingLayout =(LinearLayout)findViewById(R.id.layout_save);
        File file = saveBitMap(SrStoreStatus.this, savingLayout);
        if (file != null) {
            pd.cancel();
            Log.i("TAG", "Drawing saved to the gallery!");
        } else {
            pd.cancel();
            Log.i("TAG", "Oops! Image could not be saved.");
        }
    }

    // Create image for printing Bangla----------------------------------------------------------------------------


    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
        Log.d("imagepath", String.valueOf(pictureFileDir));

        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save the image");
            return null;
        }

        // String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".png";
        String filename = pictureFileDir.getPath() + File.separator +"report"+".png";
        Log.d("imagename", filename);

        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( SrStoreStatus.this, Dashboard.class));
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
*/
