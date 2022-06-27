package com.srapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.srapp.Adapter.AdapterForBonusProductList;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Model.Product;
import com.srapp.Util.Parent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.srapp.Db_Actions.Tables.ORDER_order_number;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER_DETAILS;

public class BonusProductListActivity extends Parent {
    Button NextButton, CancelButton;

    /* Spinner ProductCategorySp;*/
    //	LinearLayout ChallanNoLayout;
    AdapterForBonusProductList dataAdapter = null;


    ArrayList<String> ProductCategoryID = new ArrayList<String>();
    ArrayList<String> ProductCategoryName = new ArrayList<String>();
    String _ProductCategoryID;

    ArrayList<String> ProductID = new ArrayList<String>();
    ArrayList<String> ProductName = new ArrayList<String>();

    ArrayList<Product> ProductList = new ArrayList<Product>();

    ArrayList<HashMap<String, String>> BonusItemListFromDB = new ArrayList<HashMap<String, String>>();
    String back = "";
//    String ActivityName="";


    ArrayList<String> ClaimID = new ArrayList<String>();
    ArrayList<String> ClaimName = new ArrayList<String>();
    String _ClaimType;

    ArrayList<String> ChallanID = new ArrayList<String>();
    ArrayList<String> ChallanNO = new ArrayList<String>();
    public String _ChallanID = "", _ChallanNO = "";
    String date, memodate;
    String product_name, product_type_id;
    Data_Source db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_product_list);
        db = new Data_Source(this);
        /*TextView txtUser=(TextView)findViewById(R.id.txtUser);
        txtUser.setText(getPreference("UserName"));*/

      /*  HeaderTitleTv = (TextView)findViewById(R.id.txtTitleName);
        CodeNoTv = (TextView)findViewById(R.id.txtSOCode);
        HeaderTitleTv.setText("Products");
        CodeNoTv.setText(getPreference("SO"));*/

       /* initiateButtons(btnBack, R.id.btn_back);
        initiateButtons(btnHome, R.id.btn_home);*/

       /* DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels/24;


        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutBg);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.setMargins(width, 0, width, 0);
        layout.setLayoutParams(params);*/


        NextButton = (Button) findViewById(R.id.NextButton);
        CancelButton = (Button) findViewById(R.id.CancelButton);


        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if (TempData.editMemo.equalsIgnoreCase("true")) {
            date = TempData.MemoDateTime;
        } else {
            date = getCurrentDate();
        }


        Log.e("DateforBonus:", String.valueOf(date));
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


        // ProductCategoryParse();
        //		ProductDisplayListView();
        ProductDisplayListView("0");
     /*   ProductCategorySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _ProductCategoryID = ProductCategoryID.get(arg2);
                Log.e("_ProductCategoryID",""+_ProductCategoryID);
                String _ProductCategoryName = ProductCategoryName.get(arg2);
                //ProductDisplayListView(_ProductCategoryID);

                Log.e("****************", "&&&&&&&&&&&&&&&&&&&&&&"+_ProductCategoryID+"     "+_ProductCategoryName);
                if(_ProductCategoryID.equals("0"))
                {
                    ProductDisplayListView("0");
                }
                else
                {
                    ProductDisplayListView(_ProductCategoryID);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });*/
    }


    private void ProductCategoryParse() {
        ProductCategoryID.clear();
        ProductCategoryName.clear();
        ProductCategoryID.add("0");
        ProductCategoryName.add("All");

        String proIn = "IN(";
        Cursor c1 = db.rawQuery("SELECT DISTINCT product_category_id FROM so_stocks where so_id=" + "'" + getPreference("SO") + "'");
        Log.e("Count", "" + c1.getCount());
        if (c1 != null) {
            if (c1.moveToFirst()) {
                do {

                    String product_id = c1.getString(c1.getColumnIndex("product_category_id"));
                    proIn = proIn + "'" + product_id + "'" + ",";

                } while (c1.moveToNext());
            }
            //Log.e("proIn", " "+proIn);
            if (proIn != "IN(")
                proIn = proIn.substring(0, proIn.length() - 1);
        }
        proIn = proIn + ")";
        Log.e("proIn", " " + proIn);


        Log.e("Query", "SELECT * FROM product_categories where so_id=" + "'" + getPreference("SO") + "' AND category_id " + proIn);
        //Cursor c = db.rawQuery("SELECT * FROM product_categories where so_id="+"'"+getPreference("SO")+"' AND category_id "+proIn);
        Cursor c = db.rawQuery("SELECT * FROM product_categories where so_id=" + "'" + getPreference("SO") + "'");
        Log.e("CategoryCont:", c.getCount() + "");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String product_category_id = c.getString(c.getColumnIndex("category_id"));
                    String product_category_name = c.getString(c.getColumnIndex("category_name"));
                    Log.e("product_category_id", " " + product_category_id);
                    Log.e("product_category_name", " " + product_category_name);


                    ProductCategoryID.add(product_category_id);
                    ProductCategoryName.add(product_category_name);


                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BonusProductListActivity.this, R.layout.spinner_text, ProductCategoryName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // ProductCategorySp.setAdapter(dataAdapter);

        }

    }

    private void ProductDisplayListView(String CategoryID) {

        BonusItemListFromDB.clear();
        Cursor c2 = null;
//            c2 = db.rawQuery("SELECT product_id, product_name, product_type_id FROM products where product_type_id='1'");
//            c2 = db.rawQuery("SELECT product_id, product_name, product_type_id FROM products WHERE price_bonus_start<="+"'2017-11-17'"+" and price_bonus_end >="+"'2017-11-17' and product_type_id='1'");
        if (TempData.editMemo.equalsIgnoreCase("true")){
            memodate = TempData.MemoDate;
        }else {
            memodate = getCurrentDate();
        }

        String query1 = "SELECT DISTINCT(product_id) FROM product_history WHERE start_date<=" + "'" + memodate + "'" + " and end_date>=" + "'" + memodate + "' AND is_bonus=1";
        c2 = db.rawQuery(query1);
        Log.e("query", query1 + " " + c2.getCount());
        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {

                    String product_id = c2.getString(0);


                    String EditQuery = ">";
                    if (TempData.editMemo.equalsIgnoreCase("true")) {

                        EditQuery = ">=";
                    }
                    String query = "SELECT product_name, product_type_id FROM product WHERE product_id='" + product_id + "'";
                    Cursor c12 = db.rawQuery(query);
                    Log.e("Query", query);
                    if (c12 != null) {
                        if (c12.moveToFirst()) {
                            do {
                                product_name = c12.getString(0);
                                product_type_id = c12.getString(1);

                            } while (c12.moveToNext());
                        }
                    }
                    c12.close();
                    Log.e("~~~~~~~~~~~~~~~~~~~~", "product_id : " + product_id);
                    Log.e("~~~~~~~~~~~~~~~~~~~~", "product_name : " + product_name);
                    Log.e("~~~~~~~~~~~~~~~~~~~~", "product_type_id : " + product_type_id);
                    HashMap<String, String> product_list_map = new HashMap<String, String>();
                    product_list_map.put("product_category_id", CategoryID);
                    product_list_map.put("product_id", product_id);
                    product_list_map.put("product_name", product_name);
                    product_list_map.put("product_type_id", product_type_id);
                    if (getIndex(product_id, BonusItemListFromDB) < 0)
                        if (TempData.editMemo.equalsIgnoreCase("true")) {
                            if (Double.parseDouble("2.00") > 0) {
                                BonusItemListFromDB.add(product_list_map);
                            } else {
                                Cursor cMD = db.rawQuery("select * from "+TABLE_NAME_ORDER_DETAILS+" where product_id = '" + product_id + "' and "+ORDER_order_number+"='" + TempData.orderNumber + "' and is_bonus='1'");
                                if (cMD != null & cMD.getCount() > 0) {
                                    BonusItemListFromDB.add(product_list_map);
                                }
                            }
                        } else {
                            BonusItemListFromDB.add(product_list_map);
                        }
                } while (c2.moveToNext());
            }
            c2.close();
            Log.e("BonusItemListFromDB--", "BonusItemListFromDB--" + BonusItemListFromDB.toString());
            if (BonusItemListFromDB.toString().equalsIgnoreCase("[]")) {
                ListView listView = (ListView) findViewById(R.id.ProductListView);
                listView.setVisibility(View.GONE);
            } else {
                dataAdapter = new AdapterForBonusProductList(this, BonusItemListFromDB, getPreference("SO"));
                ListView listView = (ListView) findViewById(R.id.ProductListView);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(dataAdapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }

        }
    }

    int getIndex(String value, ArrayList<HashMap<String, String>> orderList) {
        int pos = -1;
        for (int i = 0; i < orderList.size(); i++) {
            HashMap<String, String> map = orderList.get(i);
            Log.e("PRODUCT_ID", map.get("product_id"));
            if (map.get("product_id").equalsIgnoreCase(value))
                pos = i;
        }

        return pos;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            TempBonus();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initiateButtons(Button btn, int id) {
        btn = (Button) findViewById(id);
        btn.setOnClickListener((View.OnClickListener) this);
    }


    public void savePreferenceChallan(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BonusProductListActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    private void TempBonus() {

        for (int k = 0; k < TempData.TotalBonusProductList.size(); k++) {
            savePreference("bonus" + TempData.TotalBonusProductList.get(k).get("product_id"), "0.0");
            Log.e("TempBonus()", "TempBonus()" + getPreference("bonus" + TempData.TotalBonusProductList.get(k).get("product_id")));
        }
    }
}
