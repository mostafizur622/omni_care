package com.srapp;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }
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

                            //basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

                            getStocks(primaryData);

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

                            //basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);
                            getStocks(primaryData);

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

                            getStocks(primaryData);
                            //basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);

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

                            //basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData), 101);
                            getStocks(primaryData);

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
                    getStocks(primaryData);
                    //basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);

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

                       // basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);
                        getStocks(primaryData);

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

                       // basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);
                        getStocks(primaryData);

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

                       // basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),101);
                        getStocks(primaryData);

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

    private void getStocks(JSONObject primaryData) {

        ProgressDialog dailog = CheckConnection(SrStoreStatus.this,"Stock Checking...");
        if (dailog==null)
            return;
        getJAPi().Stock(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    dailog.dismiss();
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(SrStoreStatus.this));
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

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

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

       // basicFunction.getResponceData(URL.Stock, String.valueOf(primaryData),102);

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




