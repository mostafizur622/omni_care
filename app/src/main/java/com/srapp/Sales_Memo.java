package com.srapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.srapp.Adapter.SalesOrderAdaper;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.AppManager;
import com.srapp.Util.Parent;
import com.srapp.print.ParentActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.OUTLETS_ID;
import static com.srapp.Db_Actions.Tables.PPRODCUT_TYPE;
import static com.srapp.Db_Actions.Tables.PRODUCT_CATEGORYS;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.editMemo;

public class Sales_Memo extends Parent {


    private static final int VERTICAL_ITEM_SPACE = 48;
    Data_Source ds;
    Button nextBtn;
    HashMap<String, ArrayList<String>> productcatagory = new HashMap<>();
    HashMap<String, ArrayList<String>> product_type = new HashMap<>();
    ListView productRecycleView;
    int backactivity;
    ImageView homeBtn,backBtn;
    SalesOrderAdaper adapter;
    TextView userIdTV,titleTV;
    Spinner productSpinner,product_type_sp;

    String query="";
    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales__memo);
        ds = new Data_Source(this);
        nextBtn = findViewById(R.id.nextBtn);
        savePreference(OUTLETS_ID, TempData.OutletID);

        if (getIntent() != null) {

            backactivity = getIntent().getIntExtra("flag", 0);
        }

         productSpinner =
                findViewById(R.id.product_category_spinner);

         product_type_sp =
                findViewById(R.id.product_type);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        search = findViewById(R.id.search);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent( Sales_Memo.this, Dashboard.class));
            finish();
        });

        backBtn.setOnClickListener(v -> {

            if (editMemo.equalsIgnoreCase("true")){
                startActivity(new Intent( Sales_Memo.this, DetailsOrderReport.class));
                finish();
            }else {

                startActivity(new Intent( Sales_Memo.this, Create_New_Memo.class));
                finish();
            }


        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                 query = editable.toString();
                if (query.length()>0){

                }else {
                    query ="";
                }
                getList();
            }
        });
        nextBtn.setOnClickListener(v -> {
            Runtime.getRuntime().gc(); // use for CursorWindowAllocationException
            AppManager.setCloseKeyBoard(Sales_Memo.this,adapter.editText );

            Boolean validInput = true;

            String query = "SELECT quantity,product_id FROM product_boolean WHERE outlet_id='" + getPreference("OutletID") + "' AND boolean='true' ";
            Cursor c2 = ds.rawQueryCoustom(query);
            int count = c2.getCount();
            Log.e("QUERY COUNT:", "..............." + count + "query" + query);
            if (c2 != null) {
                if (c2.moveToFirst()) {
                    do {

                        if (c2.getString(0).trim().length() == 0) {
                            validInput = false;
                            Log.e("falsevalidation", "falsevalidation");
                        }
                        try {

                            if (Double.parseDouble(c2.getString(0)) <= 0) {
                                validInput = false;
                            }

                        } catch (Exception e) {
                            validInput = false;
                        }

                    } while (c2.moveToNext());
                }
            }

            if (count >= 1 && validInput) {
                if (editMemo.equalsIgnoreCase("true")) {
                    bonus_show();
                    startActivity(new Intent(Sales_Memo.this, ProductSales.class));
                    finish();
                }else {
                    TempData.BonusProductQuantity.clear();
                    TempData.TotalBonusProductList.clear();
                    startActivity(new Intent(Sales_Memo.this, ProductSales.class));
                    finish();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please Enter Quantity For Selected Product!", Toast.LENGTH_LONG).show();
            }

        });
        String outlet = getPreference("OutletID");
        Cursor c3 = ds.rawQueryCoustom("select outlet_category_id from outlets where outlet_id='" + outlet + "'");
        c3.moveToFirst();
        Log.e("count", c3.getCount() + "");
        if (c3 != null && c3.getCount() > 0) {

            TempData.OutletCatagoryID = c3.getString(0);
            Log.e("outletCategory_id__", TempData.OutletCatagoryID);
        }

        productcatagory = ds.getAccessories(true, "00", PRODUCT_CATEGORYS, null);
        product_type = ds.getAccessories(true, "00", PPRODCUT_TYPE, null);
        productcatagory.get(PRODUCT_CATEGORYS[3]).add(0, "All");
        product_type.get(PPRODCUT_TYPE[3]).add(0, "All");
        Log.e("product_type",product_type.get(PPRODCUT_TYPE[3]).toString()+" ");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, productcatagory.get(PRODUCT_CATEGORYS[3]));

        ArrayAdapter<String> product_typeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, product_type.get(PPRODCUT_TYPE[3]));

        productSpinner.setAdapter(arrayAdapter);
        product_type_sp.setAdapter(product_typeAdapter);


        productRecycleView = findViewById(R.id.product_list_recycler);
        productRecycleView.setItemsCanFocus(true);
        product_type.get(PPRODCUT_TYPE[2]).add(0, "0");
        productcatagory.get(PRODUCT_CATEGORYS[2]).add(0, "0");

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }

                getList();
                Log.e("productcatagory", productcatagory.get(PRODUCT_CATEGORYS[2]).get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        product_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }

                getList();
                Log.e("productcatagory", productcatagory.get(PRODUCT_CATEGORYS[2]).get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getList();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (editMemo.equalsIgnoreCase("true")){
                startActivity(new Intent( Sales_Memo.this, DetailsOrderReport.class));
                finish();
            }else {

                startActivity(new Intent( Sales_Memo.this, Create_New_Memo.class));
                finish();
            }

        }
        return super.onKeyDown(keyCode, event);

    }

    private void getList() {
         adapter = new SalesOrderAdaper(Sales_Memo.this, ds.Getproductlist(productcatagory.get(PRODUCT_CATEGORYS[2]).get(productSpinner.getSelectedItemPosition()),product_type.get(PPRODCUT_TYPE[2]).get(product_type_sp.getSelectedItemPosition()),query), "2025", getPreference(Tables.OUTLETS_ID));
         productRecycleView.setAdapter(adapter);
    }


    private void bonus_show() {
//		String bonus_value="";
        TempData.BonusProductQuantity.clear();
        TempData.TotalBonusProductList.clear();
        Log.e("SalesMemoNo", TempData.memoNumber);
        String query1 = "";
        if (MEMO_EDIT){
             query1 = "SELECT MD.product_id, MD.quantity, P.product_name FROM memo_details MD LEFT JOIN product P ON (MD.product_id = P.product_id) WHERE MD.memo_number='" + TempData.memoNumber + "' and MD.is_bonus='1' and product_type='2'";

        }else {
            query1 = "SELECT MD.product_id, MD.quantity, P.product_name FROM ORDER_details MD LEFT JOIN product P ON (MD.product_id = P.product_id) WHERE MD.order_number='" + TempData.orderNumber + "' and MD.is_bonus='1' and product_type='2'";
        }

        Log.e("bonus_show(query1)", "bonus_show(query1)" + query1);
        Cursor c2 = ds.rawQueryCoustom(query1);
        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {

                    String product_id = c2.getString(0);
                    String quantity = c2.getString(1);
                    String product_name = c2.getString(2);
                    HashMap<String, String> product_list_map = new HashMap<String, String>();


                    savePreference("bonus" + product_id, quantity);

                    product_list_map.put("product_id", product_id);
                    product_list_map.put("product_name", product_name);
                    product_list_map.put("quantity", quantity);

                    TempData.BonusProductQuantity.add(product_list_map);
                    TempData.TotalBonusProductList.add(product_list_map);

                    Log.e("TotalBonusProductList", " TotalBonusProductList for Sales Order: " + TempData.TotalBonusProductList);


                } while (c2.moveToNext());
            }
        }

    }
}
