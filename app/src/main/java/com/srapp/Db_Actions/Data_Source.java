package com.srapp.Db_Actions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.srapp.TempData;
import com.srapp.Util.Parent;
import com.srapp.bonusPolicy.ExclusionProduct;
import com.srapp.bonusPolicy.PolicyBonusProduct;
import com.srapp.bonusPolicy.PolicyID;
import com.srapp.bonusPolicy.PolicyProduct;
import com.srapp.bonusPolicy.PolicyWiseSlab;
import com.srapp.pricing.CombinationPrices;
import com.srapp.pricing.CombinationProductDetails;
import com.srapp.pricing.GSOPrice;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import static com.srapp.Db_Actions.Tables.Allfild;
import static com.srapp.Db_Actions.Tables.GIFT_ISSUE;
import static com.srapp.Db_Actions.Tables.GIFT_ISSUE_DETAILS;
import static com.srapp.Db_Actions.Tables.MARKET_ID;
import static com.srapp.Db_Actions.Tables.MEMOS;
import static com.srapp.Db_Actions.Tables.MEMOS_gross_value;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_date;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_number;
import static com.srapp.Db_Actions.Tables.MEMO_DETAILS;
import static com.srapp.Db_Actions.Tables.MEMO_DETAILS_Unit_id;
import static com.srapp.Db_Actions.Tables.ORDER;
import static com.srapp.Db_Actions.Tables.ORDER_BOUNCE;
import static com.srapp.Db_Actions.Tables.ORDER_DETAILS;
import static com.srapp.Db_Actions.Tables.ORDER_STATUS;
import static com.srapp.Db_Actions.Tables.ORDER_gross_value;
import static com.srapp.Db_Actions.Tables.ORDER_is_pushed;
import static com.srapp.Db_Actions.Tables.ORDER_order_date;
import static com.srapp.Db_Actions.Tables.ORDER_order_number;
import static com.srapp.Db_Actions.Tables.ORDER_outlet_id;
import static com.srapp.Db_Actions.Tables.OUTLETS_ID;
import static com.srapp.Db_Actions.Tables.OUTLETS_OUTLET_NAME;
import static com.srapp.Db_Actions.Tables.OUTLET_CATEGORY_CATEGORY_ID;
import static com.srapp.Db_Actions.Tables.PROCESSING_COMPELETE;
import static com.srapp.Db_Actions.Tables.PROCESSING_PENDING;
import static com.srapp.Db_Actions.Tables.PRODUCT_BOOLEAN_OUTLET_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_BOOLEAN_PRODUCT_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_BOOLEAN_QUANTITY;
import static com.srapp.Db_Actions.Tables.PRODUCT_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRICE_EFFECTIVE_DATE;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_ID;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_LOCATION;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MARKETS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MEMOS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_OUTLETS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_OUTLET_CATEGORY;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_PRODUCT;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_PRODUCT_BOOLEAN;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_PRODUCT_COMBINATION;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_PRODUCT_PRICE;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ROOT;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_THANA;
import static com.srapp.TempData.PROCESSING_ON_SERVER;

public class Data_Source extends Parent {

    public DBListener dbListener;
    BasicFunction basicFunction;
    ProgressDialog progressDialog;
    private final DB_Helper dBhelper;
    public SQLiteDatabase sqLiteDatabase;
    private final Context context;

    public Data_Source(Context context, DBListener dbListener, BasicFunctionListener basicFunctionListener) {
        this.context = context;
        this.dbListener = dbListener;
        dBhelper = new DB_Helper(context);
        basicFunction = new BasicFunction(basicFunctionListener, context);
        Log.e("create",(basicFunction==null)+"");
        FirebaseCrashlytics.getInstance().setUserId(getPreference("sr_uname"));
        open();
    }

    public Data_Source(Context context, DBListener dbListener) {
        this.context = context;
        this.dbListener = dbListener;
        dBhelper = new DB_Helper(context);
        FirebaseCrashlytics.getInstance().setUserId(getPreference("sr_uname"));
        open();
    }

    public Data_Source(Context context) {
        this.dBhelper = new DB_Helper(context);
        this.context = context;
        FirebaseCrashlytics.getInstance().setUserId(getPreference("sr_uname"));
        open();
    }


    public void open() {

        sqLiteDatabase = dBhelper.getWritableDatabase();
    }

    public void close() {

        //sqLiteDatabase.close();
    }

    public void excQuery(String Query) {
        //Loge("execSQL", Query);
        open();
        sqLiteDatabase.execSQL(Query);
        close();
    }

    //.................Price List BY SLAP...................//

    public HashMap<String, ArrayList<ArrayList<String>>> getSlapWisePriceList(String date) {

        //Loge("slap------->", "getSlapWisePriceList: ---->CALLED.....>");

        this.open();

        HashMap<String, ArrayList<ArrayList<String>>> priceListHashMap = new HashMap<>();
        ArrayList<ArrayList<String>> productNameList = new ArrayList<>();
        ArrayList<ArrayList<String>> productIdList = new ArrayList<>();
        ArrayList<ArrayList<String>> mainPriceList = new ArrayList<>();
        ArrayList<ArrayList<String>> slapPriceList = new ArrayList<>();
        ArrayList<ArrayList<String>> slapQtyList = new ArrayList<>();

        //String query = "SELECT DISTINCT(product_id) FROM "+ TABLE_NAME_PRODUCT_PRICE+" where price > 0 and effective_date<='"+getCurrentDate()+"'";
        String query = "SELECT DISTINCT(pp.product_id) FROM " + TABLE_NAME_PRODUCT_PRICE + " as pp inner join product as p on p.product_id=pp.product_id where pp.effective_date<='" + getCurrentDate() + "' order by p.product_order ASC";
        Cursor csr = sqLiteDatabase.rawQuery(query, null);
        csr.moveToFirst();
        if (csr != null && csr.getCount() > 0) {
            for (int i = 0; i < csr.getCount(); i++) {
                ArrayList<String> preNameList = new ArrayList<>();
                ArrayList<String> preIdList = new ArrayList<>();
                //preNameList.add(csr.getString(csr.getColumnIndex(Tables.PRODUCT_PRODUCT_NAME)));
                ////Loge("--->", "getSlapWisePriceList:----------> "+cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_PRODUCT_NAME)) );
                // productNameList.add(preNameList);

                String productId = csr.getString(csr.getColumnIndex(Tables.PRODUCT_PRODUCT_ID));
                preIdList.add(productId);
                productIdList.add(preIdList);
                HashMap<String, ArrayList<ArrayList<String>>> qtyandPrice = getQtyAndPriceByProductId(productId, date);
                if (qtyandPrice.get(Tables.ProductName).get(0) != null) {

                    try {
                        productNameList.add(qtyandPrice.get(Tables.ProductName).get(0));
                        //Loge("---", "getSlapWisePriceList: --------->" + productIdList);

                        mainPriceList.add(qtyandPrice.get("Main_Price").get(0));
                        //Loge("--------->", "getSlapWisePriceList:-----> main price " + mainPriceList);
                        slapPriceList.add(qtyandPrice.get(Tables.MinPrice).get(0));
                        slapQtyList.add(qtyandPrice.get(Tables.MinQty).get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                csr.moveToNext();
            }

            priceListHashMap.put(Tables.ProductName, productNameList);
            priceListHashMap.put("Main_PRICE", mainPriceList);
            priceListHashMap.put(Tables.MinQty, slapQtyList);
            priceListHashMap.put(Tables.MinPrice, slapPriceList);

        }
        csr.close();

        return priceListHashMap;
    }

    public HashMap<String, ArrayList<ArrayList<String>>> getQtyAndPriceByProductId(String productId, String date) {

        this.open();

        HashMap<String, ArrayList<ArrayList<String>>> qtyAndPriceHashMap = new HashMap<>();
        ArrayList<ArrayList<String>> qtyList = new ArrayList<>();
        ArrayList<ArrayList<String>> priceList = new ArrayList<>();
        ArrayList<ArrayList<String>> mainPriceList = new ArrayList<>();
        ArrayList<ArrayList<String>> mainNameList = new ArrayList<>();
        ArrayList<String> preMinQty = new ArrayList<>();
        ArrayList<String> preMinPriceList = new ArrayList<>();
        ArrayList<String> prePriceList = new ArrayList<>();
        ArrayList<String> preNameList = new ArrayList<>();

        //String query1 = "SELECT DISTINCT "+Tables.PRODUCT_COMBINATION_MIN_QUANTITY+" FROM "+ TABLE_NAME_PRODUCT_COMBINATION+" where  price>0 and  "+ PRODUCT_PRODUCT_ID+" = "+productId+" and effective_date <= (SELECT max(effective_date) from product_combinations where product_id='"+productId+"' AND effective_date<='"+date+"') ORDER BY "+Tables.PRODUCT_COMBINATION_MIN_QUANTITY+" ASC ";
        Cursor dateQuery = sqLiteDatabase.rawQuery("SELECT max(effective_date) from product_combinations where product_id='" + productId + "' AND effective_date<='" + getCurrentDate() + "' and combination_id='0' order by product_price_id DESC", null);
        dateQuery.moveToFirst();
        //Loge("querytt", "SELECT max(effective_date) from product_combinations where product_id='" + productId + "' AND effective_date<='" + getCurrentDate() + "' and combination_id='0'" + dateQuery.getCount());
        if (dateQuery != null && dateQuery.getCount() > 0) {

            date = dateQuery.getString(0);

        }
        dateQuery.close();
        String query1 = "SELECT DISTINCT " + Tables.PRODUCT_COMBINATION_MIN_QUANTITY + " FROM " + TABLE_NAME_PRODUCT_COMBINATION + " where  price>0 and  " + PRODUCT_PRODUCT_ID + " = '" + productId + "' and effective_date = '" + date + "' ORDER BY " + Tables.PRODUCT_COMBINATION_MIN_QUANTITY + " ASC ";
        //Loge("minnQquery1", " " + query1);
        Cursor cursor = sqLiteDatabase.rawQuery(query1, null);
        cursor.moveToFirst();

        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String min_qty = cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_COMBINATION_MIN_QUANTITY));

                preMinQty.add(min_qty);

                String query2 = "SELECT " + Tables.PRODUCT_COMBINATION_PRICE + " from " + Tables.TABLE_NAME_PRODUCT_COMBINATION + " where " + PRODUCT_PRODUCT_ID + " = " + productId + " AND " + Tables.PRODUCT_COMBINATION_MIN_QUANTITY + " = " + min_qty + " AND " +
                        "" + Tables.PRODUCT_COMBINATION_EFFECTIVE + "<=  (SELECT max(effective_date) from product_combinations where product_id='" + productId + "' AND effective_date<='" + date + "')" + " ORDER BY " + Tables.PRODUCT_COMBINATION_EFFECTIVE + " DESC, product_price_id DESC LIMIT 1 ";
                //Loge("minriceslap", "getQtyAndPriceByProductId:---------->price slap--------> " + query2);
                Cursor cursor1 = sqLiteDatabase.rawQuery(query2, null);
                cursor1.moveToFirst();
                if (cursor1 != null && cursor1.getCount() > 0) {
                    String price = cursor1.getString(cursor1.getColumnIndex(Tables.PRODUCT_COMBINATION_PRICE));
                    preMinPriceList.add(price);
                }
                cursor.moveToNext();
                cursor1.close();
            }
            qtyList.add(preMinQty);
            priceList.add(preMinPriceList);

            qtyAndPriceHashMap.put(Tables.MinQty, qtyList);
            qtyAndPriceHashMap.put(Tables.MinPrice, priceList);
            cursor.close();
        }
        cursor.close();

        String query3 = "SELECT " + Tables.PRODUCT_PRICE_PRICE + " FROM " + TABLE_NAME_PRODUCT_PRICE + " WHERE " + PRODUCT_PRODUCT_ID + " = " + productId + "";
        Cursor priceCursor = sqLiteDatabase.rawQuery(query3, null);
        priceCursor.moveToFirst();
        if (priceCursor != null && priceCursor.getCount() > 0) {
            String price = priceCursor.getString(priceCursor.getColumnIndex(Tables.PRODUCT_PRICE_PRICE));
            prePriceList.add(price);
        }

        priceCursor.close();

        String query4 = "SELECT " + Tables.PRODUCT_PRODUCT_NAME + " FROM " + TABLE_NAME_PRODUCT + " WHERE " + PRODUCT_PRODUCT_ID + " = " + productId + "";
        Cursor cursor4 = sqLiteDatabase.rawQuery(query4, null);
        //Loge("PRODUCT_NAMEttt", query4 + " size=" + cursor4.getCount());
        cursor4.moveToFirst();
        if (cursor4 != null && cursor4.getCount() > 0) {
            String name = cursor4.getString(cursor4.getColumnIndex(Tables.PRODUCT_PRODUCT_NAME));
            //Loge("PRODUCT_NAMEttt", name + " 000");
            preNameList.add(name);
        }

        cursor4.close();
        mainPriceList.add(prePriceList);
        mainNameList.add(preNameList);
        qtyAndPriceHashMap.put("Main_Price", mainPriceList);
        qtyAndPriceHashMap.put(Tables.ProductName, mainNameList);

        //Loge("test---->", "getQtyAndPriceByProductId:--------> " + qtyAndPriceHashMap);

        sqLiteDatabase.close();
        return qtyAndPriceHashMap;


    }


    public HashMap<String, String> getMinQtyAndPrice(String productId, String date) {
        this.open();
        String qty = null;
        String minPrice = null;
        String price = null;
        HashMap<String, String> hashMap = new HashMap<>();
        String query = "SELECT " + Tables.PRODUCT_COMBINATION_MIN_QUANTITY + ", " + Tables.PRODUCT_COMBINATION_PRICE + " FROM " + TABLE_NAME_PRODUCT_COMBINATION + " WHERE " + PRODUCT_PRODUCT_ID + " = " + productId + " ORDER BY  " + PRODUCT_PRICE_EFFECTIVE_DATE + " <= " + date + " DESC LIMIT 1 ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            qty = cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_COMBINATION_MIN_QUANTITY));
            minPrice = cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_COMBINATION_PRICE));
        }
        String query2 = "SELECT " + Tables.PRODUCT_PRICE_PRICE + " FROM " + TABLE_NAME_PRODUCT_PRICE + " WHERE " + PRODUCT_PRODUCT_ID + " = " + productId + " ORDER BY  " + PRODUCT_PRICE_EFFECTIVE_DATE + " DESC LIMIT 1";
        Cursor priceCursor = sqLiteDatabase.rawQuery(query2, null);
        priceCursor.moveToFirst();
        if (priceCursor != null && priceCursor.getCount() > 0) {
            price = priceCursor.getString(priceCursor.getColumnIndex(Tables.PRODUCT_PRICE_PRICE));
        }
        priceCursor.close();
        hashMap.put(Tables.MinQty, qty);
        hashMap.put(Tables.MinPrice, minPrice);
        hashMap.put(Tables.PRODUCT_PRICE_PRICE, price);
        return hashMap;
    }

    //...............................Price List retrive...........................//
    public HashMap<String, ArrayList<String>> getPriceList(String date) {
        this.open();
        HashMap<String, ArrayList<String>> priceListHashmap = new HashMap<>();
        ArrayList<String> productNameList = new ArrayList<>();
        ArrayList<String> productIdList = new ArrayList<>();
        ArrayList<String> priceList1 = new ArrayList<>();
        ArrayList<String> priceList2 = new ArrayList<>();
        ArrayList<String> quantityList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME_PRODUCT + "";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                productNameList.add(cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_PRODUCT_NAME)));
                productIdList.add(cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_PRODUCT_ID)));
                // now retrive price 1, price2, quantity according to product id
                String productId = cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_PRODUCT_ID));
                priceList1.add(getMinQtyAndPrice(productId, date).get(Tables.PRODUCT_PRICE_PRICE));
                priceList2.add(getMinQtyAndPrice(productId, date).get(Tables.MinPrice));
                quantityList.add(getMinQtyAndPrice(productId, date).get(Tables.MinQty));
                cursor.moveToNext();
            }
        }
        cursor.close();
        priceListHashmap.put(PRODUCT_ID, productIdList);
        priceListHashmap.put(Tables.PRODUCT_PRODUCT_NAME, productNameList);
        priceListHashmap.put(Tables.PRODUCT_PRICE_PRICE, priceList1);
        priceListHashmap.put(Tables.MinPrice, priceList2);
        priceListHashmap.put(Tables.MinQty, quantityList);
        return priceListHashmap;
    }

    public ArrayList<HashMap<String, String>> Getproductlist(String CategoryID,String type_id,String search) {


        ArrayList<HashMap<String, String>> ItemListFromDB = new ArrayList<>();
        open();
        String query = "SELECT DISTINCT * FROM " + TABLE_NAME_PRODUCT_BOOLEAN + " WHERE outlet_id=" + "'" + getPreference(OUTLETS_ID) + "'";;
        if (!CategoryID.equalsIgnoreCase("0"))
            query += " AND product_category_id=" + "'" + CategoryID + "'";

         if (!type_id.equalsIgnoreCase("0"))
            query += " AND product_type_id=" + "'" + type_id + "'";
         if (!search.equalsIgnoreCase(""))
            query += " AND product_name LIKE" + " '%" + search + "%'";



        //subrata da..................................................
        ItemListFromDB.clear();
        Cursor c = sqLiteDatabase.rawQuery(query, null);
        Log.e("INquery", query + " size=" + c.getCount());
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    String outlet_id = c.getString(c.getColumnIndex(OUTLETS_ID));
                    String product_id = c.getString(c.getColumnIndex(PRODUCT_PRODUCT_ID));
                    String quantity = c.getString(c.getColumnIndex("product_id"));
                    String productSales = c.getString(c.getColumnIndex("product_sales"));

                    String product_name = "";
                    String query1 = "SELECT product_name FROM " + TABLE_NAME_PRODUCT + " WHERE product_id='" + product_id + "'";
                    Cursor c2 = sqLiteDatabase.rawQuery(query1, null);
                    if (c2 != null) {
                        if (c2.moveToFirst()) {
                            do {
                                product_name = c2.getString(0);
                            } while (c2.moveToNext());
                        }
                    }


                    c2.close();


                    String boolean12 = c.getString(c.getColumnIndex("boolean"));


                    //Loge("GGGGGGGGGGGGGGGGGGGGGGG", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH outlet_id " + outlet_id);
                    //Loge("GGGGGGGGGGGGGGGGGGGGGGG", "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG product_id " + product_id);
                    //Loge("GGGGGGGGGGGGGGGGGGGGGGG", "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG product_name " + product_name);
                    //Loge("GGGGGGGGGGGGGGGGGGGGGGG", "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG product_boolean " + boolean12);

                    HashMap<String, String> product_list_map = new HashMap<String, String>();

                    product_list_map.put("product_category_id", CategoryID);
                    product_list_map.put("product_id", product_id);
                    product_list_map.put("product_name", getProductName(product_id,product_name));
                    product_list_map.put("boolean", boolean12);
                    product_list_map.put("productSales", productSales);


                /*    if (TempData.OutletCatagoryID.equalsIgnoreCase("17")) {
                        Cursor ispoduct = db.rawQuery("Select * from distibutor_product_prices where product_id='" + product_id + "'");
                        if (ispoduct != null && ispoduct.getCount() > 0)
                            ItemListFromDB.add(product_list_map);

                    } else {


                    }*/


                    ItemListFromDB.add(product_list_map);

                } while (c.moveToNext());
            }
            c.close();


        }
        close();
        return ItemListFromDB;
    }


    public ArrayList<HashMap<String, String>> GetproductlistbyCategory(String CategoryID) {


        ArrayList<HashMap<String, String>> ItemListFromDB = new ArrayList<>();
        open();
        String query = "";
        if (!CategoryID.equalsIgnoreCase("0"))
            query = "SELECT DISTINCT * FROM " + TABLE_NAME_PRODUCT + " WHERE product_category_id=" + "'" + CategoryID + "'" ;
        else
            query = "SELECT DISTINCT * FROM " + TABLE_NAME_PRODUCT ;

        ItemListFromDB.clear();
        Cursor c = sqLiteDatabase.rawQuery(query, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    String product_id = c.getString(c.getColumnIndex(PRODUCT_PRODUCT_ID));
                    String product_name = c.getString(c.getColumnIndex("product_name"));



                    HashMap<String, String> product_list_map = new HashMap<String, String>();
                    product_list_map.put("product_id", product_id);
                    product_list_map.put("product_name", getProductName(product_id,product_name));

                    ItemListFromDB.add(product_list_map);

                } while (c.moveToNext());
            }
            c.close();


        }
        close();
        return ItemListFromDB;
    }

    public String getProductName(String product_id, String product_name) {

        Cursor virtual_cursor = sqLiteDatabase.rawQuery("select is_virtual,parent_id from product where product_id="+product_id,null);
        //Loge("virtual_cursor", "select is_virtual,parent_id from products where product_id="+product_id);
        virtual_cursor.moveToFirst();
        if (virtual_cursor!=null && virtual_cursor.getCount()>0){

            if (virtual_cursor.getInt(0)==1){
                //Loge("cname", "SELECT quantity-booking_quantity as quantity from stock_info where product_id="+virtual_cursor.getString(1));
                Cursor cname = sqLiteDatabase.rawQuery("SELECT quantity-booking_quantity as quantity from stock_info where product_id="+virtual_cursor.getString(1),null);
                cname.moveToFirst();

                if (cname!=null && cname.getCount()>0) {
                    //Loge("cname", cname.getCount()+" "+cname.getDouble(0));
                    if (cname.getDouble(0)<=0) {
                        //Loge("childCount", "SELECT count(p._id) from Product as p INNER JOIN stock_info as vs on p.product_id=vs.product_id WHERE vs.quantity>0 and p.parent_id=" + virtual_cursor.getString(1));
                        Cursor childCount = sqLiteDatabase.rawQuery("SELECT count(p._id) from Product as p INNER JOIN stock_info as vs on p.product_id=vs.product_id WHERE vs.quantity>0 and p.parent_id=" + virtual_cursor.getString(1),null);
                        childCount.moveToFirst();
                        if (childCount.getInt(0) == 1) {
                            Cursor pro_name = sqLiteDatabase.rawQuery("select product_name from product where product_id=" + virtual_cursor.getString(1),null);
                            pro_name.moveToFirst();
                            product_name = pro_name.getString(0);
                        }
                    }

                }
                cname.close();
            }
            virtual_cursor.close();
        }

        //Loge("product_name", product_name);



        return product_name;
    }
    public void insertData(String json, int code) {

        new InsertData(json, code).execute();

    }

    public void insertData(JSONObject json, int code) {

        new InsertData(json, code).execute();

    }

    public void generatePushJson() {

        new generatePushJson().execute();

    }

    public void generateRequirJson() {

        new Requireddata().execute();

    }

    public void getlastupdateddate() {

        new lastUpdated().execute();

    }

    public void insertBoolenatable(ContentValues contentValues, String table_name) {
        open();

        long id = sqLiteDatabase.insert(table_name, null, contentValues);
        //Loge("id", id + "");

        close();


    }

    public Cursor rawQueryCoustom(String query) {
        this.open();
        Log.e("Outlet_status",query);
        Cursor c = sqLiteDatabase.rawQuery(query, null);
       /* close();
        c.close();*/
        return c;
    }


    public Cursor rawQueryCoustom(String query, String tag) {
        this.open();
        Cursor c = sqLiteDatabase.rawQuery(query, null);
        //Loge(tag, query);
        //db.close();
        //c.close();
        return c;
    }

    public long createNewOutlet(HashMap<String, String> newOutlet) {
        this.open();
        Date currentTime = Calendar.getInstance().getTime();
        ContentValues contentValues = new ContentValues();
        for (String i : newOutlet.keySet()) {
            contentValues.put(i, newOutlet.get(i));
        }
        contentValues.put(Tables.OUTLETS_CREATED_AT, String.valueOf(currentTime));
        long id = sqLiteDatabase.insert(Tables.TABLE_NAME_TEMP_OUTLETS, null, contentValues);
        return id;
    }

    public boolean checkOutletCreatePermission(String currentDateTime) {
        this.open();
        Log.e("query","Select * from "+Tables.TABLE_OUTLET_PERMISSION+" where '"+currentDateTime+"' between start_time and end_time and no_of_outlet>0");
        Cursor c = sqLiteDatabase.rawQuery("Select * from "+Tables.TABLE_OUTLET_PERMISSION+" where '"+currentDateTime+"' between start_time and end_time and no_of_outlet>0",null);

        c.moveToFirst();
        if (c.getCount()>0 && c!=null){

            return true;
        }

        this.close();

        return false;

    }
    public long updateOutlet(HashMap<String, String> updatedOutlet, String outletId) {
        this.open();
        String[] id = {outletId};
        Date currentTime = Calendar.getInstance().getTime();
        ContentValues contentValues = new ContentValues();
        for (String i : updatedOutlet.keySet()) {
            contentValues.put(i, updatedOutlet.get(i));
        }
        //Loge("outlet_id", updatedOutlet.get("temp_id") + " if");
        contentValues.put(Tables.OUTLETS_CREATED_AT, String.valueOf(currentTime));
        long x = sqLiteDatabase.update(Tables.TABLE_NAME_OUTLETS, contentValues, Tables.OUTLETS_ID + " =?", id);
        return x;
    }

    public long insertIntoMarket(HashMap<String, String> dataForMarketCreate) {
        this.open();
        Date currentTime = Calendar.getInstance().getTime();
        ContentValues contentValues = new ContentValues();
        for (String i : dataForMarketCreate.keySet()) {
            contentValues.put(i, dataForMarketCreate.get(i));
        }
        contentValues.put(Tables.MARKETS_created_at, String.valueOf(currentTime));
        long id = sqLiteDatabase.insert(Tables.TABLE_NAME_MARKETS, null, contentValues);
        this.close();
        //  //Loge("Insert to Market Table", "onClick: " + id + " n" + dataForMarketCreate);
        return id;
    }

    public String getUnitName(String unitId) {
        this.open();
        String unitName = null;
        String query = "SELECT " + Tables.UNIT_UNAME + " FROM " + Tables.TABLE_NAME_UNIT + " WHERE " + Tables.UNIT_U_ID + " = " + unitId + " ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            unitName = cursor.getString(cursor.getColumnIndex(Tables.UNIT_UNAME));
        }
        return unitName;
    }

    public String getProductName(String productId) {
        this.open();
        String productName = null;
        String query = "SELECT " + Tables.PRODUCT_PRODUCT_NAME + " FROM " + TABLE_NAME_PRODUCT + " WHERE " + PRODUCT_PRODUCT_ID + " = " + productId + " ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            productName = cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_PRODUCT_NAME));
        }
        return productName;
    }

    public HashMap<String, String> getMinQtyAndPrice(String productId) {
        this.open();
        String qty = null;
        String price = null;
        HashMap<String, String> hashMap = new HashMap<>();
        String query = "SELECT " + Tables.PRODUCT_COMBINATION_MIN_QUANTITY + ", " + Tables.PRODUCT_COMBINATION_PRICE + " FROM " + TABLE_NAME_PRODUCT_COMBINATION + " WHERE " + PRODUCT_PRODUCT_ID + " = " + productId + " ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            qty = cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_COMBINATION_MIN_QUANTITY));
            price = cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_COMBINATION_PRICE));
        }
        hashMap.put("min_qty", qty);
        hashMap.put("min_price", price);
        return hashMap;
    }

    public HashMap<String, ArrayList<String>> getProductTypeList() {
        this.open();
        HashMap<String, ArrayList<String>> productTypeHashmap = new HashMap<>();
        ArrayList<String> productTypeNameList;
        ArrayList<String> productTypeIdList;
        String query;
        query = "SELECT " + Tables.PRODUCT_TYPE_ID + ", " + Tables.PPRODCUT_TYPE_PRODUCT_NAME + " FROM " + Tables.TABLE_NAME_PRODCUT_TYPE + "";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        productTypeNameList = new ArrayList<>();
        productTypeIdList = new ArrayList<>();
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
           /*thanaIdList.add("0");
           thanaNameList.add("");*/
            for (int i = 0; i < cursor.getCount(); i++) {
                productTypeIdList.add(cursor.getString(cursor.getColumnIndex(Tables.PRODUCT_TYPE_ID)));
                productTypeNameList.add(cursor.getString(cursor.getColumnIndex(Tables.PPRODCUT_TYPE_PRODUCT_NAME)));
                cursor.moveToNext();
            }
            productTypeHashmap.put(Tables.PPRODCUT_TYPE_PRODUCT_NAME, productTypeNameList);
            productTypeHashmap.put(Tables.PRODUCT_TYPE_ID, productTypeIdList);
        }
        this.close();
        return productTypeHashmap;
    }


    public HashMap<String, ArrayList<String>> getProductCategoryeList() {
        this.open();
        HashMap<String, ArrayList<String>> productCategoryHashmap = new HashMap<>();
        ArrayList<String> productCatgNameList;
        ArrayList<String> productCatgIdList;
        String query;
        query = "SELECT " + Tables.PPRODUCT_CATEGORY_C_id + ", " + Tables.PPRODUCT_CATEGORY_C_NAME + " FROM " + Tables.TABLE_NAME_PRODUCT_CATEGORY + "";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        productCatgNameList = new ArrayList<>();
        productCatgIdList = new ArrayList<>();
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                productCatgIdList.add(cursor.getString(cursor.getColumnIndex(Tables.PPRODUCT_CATEGORY_C_id)));
                productCatgNameList.add(cursor.getString(cursor.getColumnIndex(Tables.PPRODUCT_CATEGORY_C_NAME)));
                cursor.moveToNext();
            }
            productCategoryHashmap.put(Tables.PPRODUCT_CATEGORY_C_NAME, productCatgNameList);
            productCategoryHashmap.put(Tables.PPRODUCT_CATEGORY_C_id, productCatgIdList);
        }
        this.close();
        return productCategoryHashmap;
    }

    public long InsertTable(HashMap<String, String> data, String TableName) {

        //  //Loge("Map--", "InsertTable: "+data.get(Tables.MEMOS_latitude) );


        ContentValues values = new ContentValues();

        Iterator it = data.entrySet().iterator();
        long insertedID = 0;

        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry) it.next();
            values.put(pair.getKey(), pair.getValue()); // Contact Name
            //Loge("Map", "InsertTable: content values--" + pair.getKey() + " " + values.get(pair.getKey()));

            it.remove();
        }
        if (TableName.equalsIgnoreCase("gps_tracker")) {
            Log.e("Content producthistory:", values.toString());
        }
        //Loge(" Content values  :", values.toString());
        //Loge(TableName, "   :" + "INSERTED");
        open();
        insertedID = sqLiteDatabase.insert(TableName, null, values);

        close();
        return insertedID;

    }

    //................Retrieve Territory List for Market Create .........................//

    public HashMap<String, ArrayList<String>> getTerritoryList(String salesPersonId) {

        this.open();

        HashMap<String, ArrayList<String>> territoryHashMap = new HashMap<>();
        ArrayList<String> territoryNameList;
        ArrayList<String> territoryIdList;

        String id = salesPersonId;
        String query = "SELECT " + Tables.TERRITORY_NAME + ", " + Tables.TERRITORY_T_id + " FROM " + Tables.TABLE_NAME_TERRITORY + " WHERE " + Tables.SR_ID + " = " + id + "";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        territoryNameList = new ArrayList<>();
        territoryIdList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            /*territoryIdList.add("0");
            territoryNameList.add("");*/

            for (int i = 0; i < cursor.getCount(); i++) {


                territoryIdList.add(cursor.getString(cursor.getColumnIndex(Tables.TERRITORY_T_id)));
                territoryNameList.add(cursor.getString(cursor.getColumnIndex(Tables.TERRITORY_NAME)));
                cursor.moveToNext();
            }

            territoryHashMap.put(Tables.TERRITORY_NAME, territoryNameList);
            territoryHashMap.put(Tables.TERRITORY_T_id, territoryIdList);


        }

        this.close();

        return territoryHashMap;

    }

    //........................RETRIVE INDIVIDUAL OUTLET DETAILS ................//

    public HashMap<String, String> getIndivOutletDetails(String outletId) {

        this.open();
        HashMap<String, String> indivOutlet = new HashMap<>();
        String query = "SELECT * FROM " + Tables.TABLE_NAME_OUTLETS + " WHERE " + Tables.OUTLETS_ID + " = '" + outletId + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        //Loge("query", query + "  " + cursor.getCount() + "");
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 1; i < Tables.OUTLETS.length; i++) {
                // //Loge(Tables.OUTLETS[i],cursor.getString(cursor.getColumnIndex(Tables.OUTLETS[i])));
                indivOutlet.put(Tables.OUTLETS[i], cursor.getString(cursor.getColumnIndex(Tables.OUTLETS[i])));


            }

        }
        this.close();

        return indivOutlet;

    }

    public HashMap<String, ArrayList<String>> getIndividualOutlet(String outletId) {

        this.open();

        HashMap<String, ArrayList<String>> outletHashMap = new HashMap<>();
        ArrayList<String> outletAttributeList;
        ArrayList<String> outletValueList;
        String qry1, qry2, qry3;
        String categoryId = null, thanaId = null, marketId = null;
        String category = null, thana = null, market = null;


        String query = "SELECT * FROM " + Tables.TABLE_NAME_OUTLETS + " WHERE " + Tables.OUTLETS_ID + " = " + outletId + "";


        Cursor cursor = sqLiteDatabase.rawQuery(query, null);


        outletAttributeList = new ArrayList<>();
        outletValueList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            categoryId = cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_CATAGORY_ID));
            thanaId = cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_THANA_ID));
            marketId = cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_MARKET_ID));

            qry1 = "SELECT " + Tables.OUTLET_CATEGORY_NAME + " FROM " + Tables.TABLE_NAME_OUTLET_CATEGORY + " WHERE " + Tables.OUTLET_CATEGORY_CATEGORY_ID + " = " + categoryId + "";
            qry2 = "SELECT " + Tables.THANA_NAME + " FROM " + Tables.TABLE_NAME_THANA + " WHERE " + Tables.THANA_TH_id + " = " + thanaId + "";
            qry3 = "SELECT " + Tables.MARKETS_market_name + " FROM " + Tables.TABLE_NAME_MARKETS + " WHERE " + Tables.MARKETS_market_id + " = " + marketId + "";

            Cursor c1 = sqLiteDatabase.rawQuery(qry1, null);
            c1.moveToFirst();
            if (c1 != null && c1.getCount() > 0) {
                category = c1.getString(c1.getColumnIndex(Tables.OUTLET_CATEGORY_NAME));

            }
            c1.close();

            Cursor c2 = sqLiteDatabase.rawQuery(qry2, null);
            c2.moveToFirst();
            if (c2 != null && c2.getCount() > 0) {
                thana = c2.getString(c2.getColumnIndex(Tables.THANA_NAME));
            }
            c2.close();

            Cursor c3 = sqLiteDatabase.rawQuery(qry3, null);
            c3.moveToFirst();
            if (c3 != null && c3.getCount() > 0) {
                market = c3.getString(c3.getColumnIndex(Tables.MARKETS_market_name));
            }
            c3.close();


            outletAttributeList.add("Outlet Name");
            outletValueList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_OUTLET_NAME)));

            outletAttributeList.add("Outlet Type");
            outletValueList.add(category);

            outletAttributeList.add("NGO/Ins");
            if (cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_ISNGO)).equals("0")) {
                outletValueList.add("No");
            } else {
                outletValueList.add("Yes");
            }

            outletAttributeList.add("NGO Name");
            outletValueList.add("");

            outletAttributeList.add("Pharma Type");
            outletValueList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_PHARMA_TYPE)));

            outletAttributeList.add("Incharge Name");
            outletValueList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_INCHARGE_NAME)));

            outletAttributeList.add("Owner Name");
            outletValueList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_OWNER_NAME)));

            outletAttributeList.add("Market Name");
            outletValueList.add(market);

            outletAttributeList.add("Thana Name");
            outletValueList.add(thana);

            outletAttributeList.add("Mobile");
            outletValueList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_MOBILE)));

            outletAttributeList.add("Address");
            outletValueList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_ADDRESS)));


        }


        outletHashMap.put("Attributes", outletAttributeList);
        outletHashMap.put("Values", outletValueList);

        cursor.close();

        this.close();

        return outletHashMap;
    }


    //................Retrieve mARKET List for oUTLET Create .........................//
    public HashMap<String, ArrayList<String>> getMarketData(String byRouteId, String byThanaId) {

        this.open();

        String query = null;
        HashMap<String, ArrayList<String>> marketHashMap = new HashMap<>();
        ArrayList<String> marketNameList;
        ArrayList<String> marketIdList;

        if (byRouteId.equals("no") && byThanaId.equals("no")) {
            query = "SELECT " + Tables.MARKETS_market_name + ", " + Tables.MARKETS_market_id + " FROM " + Tables.TABLE_NAME_MARKETS + "";
        } else if (!byRouteId.equals("no") && byThanaId.equals("no")) {

            query = "SELECT " + Tables.MARKETS_market_name + ", " + Tables.MARKETS_market_id + " FROM " + Tables.TABLE_NAME_MARKETS + " WHERE " + Tables.ROUTE_ID + " = " + byRouteId + "";
        } else if (!byThanaId.equals("no") && byRouteId.equals("no")) {

            query = "SELECT " + Tables.MARKETS_market_name + ", " + Tables.MARKETS_market_id + " FROM " + Tables.TABLE_NAME_MARKETS + " WHERE " + Tables.MARKETS_thana_id + " = " + byThanaId + "";
        }

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        marketNameList = new ArrayList<>();
        marketIdList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            /*marketIdList.add("0");
            marketNameList.add("");*/

            for (int i = 0; i < cursor.getCount(); i++) {

                marketIdList.add(cursor.getString(cursor.getColumnIndex(Tables.MARKETS_market_id)));
                marketNameList.add(cursor.getString(cursor.getColumnIndex(Tables.MARKETS_market_name)));
                cursor.moveToNext();
            }

            marketHashMap.put(Tables.MARKETS_market_name, marketNameList);
            marketHashMap.put(Tables.MARKETS_market_id, marketIdList);

        }

        this.close();

        return marketHashMap;

    }

    //..............Retrive Outlet data against Market id.........................//
    public HashMap<String, ArrayList<String>> getOutletData(String byThanaId, String byMarketId) {

        this.open();

        String query = null;
        HashMap<String, ArrayList<String>> outletHashMap = new HashMap<>();
        ArrayList<String> outletNameList;
        ArrayList<String> outletIdList;

        if (byMarketId.equals("no") && byThanaId.equals("no")) {
            query = "SELECT " + Tables.OUTLETS_OUTLET_NAME + ", " + Tables.OUTLETS_ID + " FROM " + Tables.TABLE_NAME_OUTLETS + "";
        } else if (byThanaId.equals("no") && !byMarketId.equals("no")) {

            query = "SELECT " + Tables.OUTLETS_OUTLET_NAME + ", " + Tables.OUTLETS_ID + " FROM " + Tables.TABLE_NAME_OUTLETS + " WHERE " + Tables.OUTLETS_MARKET_ID + " = " + byMarketId + "";
        } else if (!byThanaId.equals("no") && byMarketId.equals("no")) {

            query = "SELECT " + Tables.OUTLETS_OUTLET_NAME + ", " + Tables.OUTLETS_ID + " FROM " + Tables.TABLE_NAME_OUTLETS + " WHERE " + Tables.OUTLETS_THANA_ID + " = " + byThanaId + "";
        } else if (!byThanaId.equals("no") && !byMarketId.equals("no")) {

            query = "SELECT " + Tables.OUTLETS_OUTLET_NAME + ", " + Tables.OUTLETS_ID + " FROM " + Tables.TABLE_NAME_OUTLETS + " WHERE " + Tables.OUTLETS_THANA_ID + " = " + byThanaId + " AND " + Tables.OUTLETS_MARKET_ID + " = " + byMarketId + "";
        }

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        outletNameList = new ArrayList<>();
        outletIdList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            outletIdList.add("0");
            outletNameList.add("Select a Outlet");

            for (int i = 0; i < cursor.getCount(); i++) {

                outletIdList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_ID)));
                outletNameList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLETS_OUTLET_NAME)));
                cursor.moveToNext();
            }

            outletHashMap.put(Tables.OUTLETS_OUTLET_NAME, outletNameList);
            outletHashMap.put(Tables.OUTLETS_ID, outletIdList);

        }

        this.close();

        return outletHashMap;
    }


    //................Retrieve Thana List for Market Create .........................//
    public HashMap<String, ArrayList<String>> getThanaList(String byTerritoryId) {

        this.open();

        HashMap<String, ArrayList<String>> thanaHashMap = new HashMap<>();
        ArrayList<String> thanaNameList;
        ArrayList<String> thanaIdList;
        String query;

        String id = byTerritoryId;

        if (id.equals("no")) {

            query = "SELECT " + Tables.THANA_NAME + ", " + Tables.THANA_ID + " FROM " + TABLE_NAME_THANA + "";

        } else {
            query = "SELECT " + Tables.THANA_NAME + ", " + Tables.THANA_ID + " FROM " + Tables.TABLE_NAME_THANA + " WHERE " + Tables.ROOT_P_id + " = " + id + "";
        }

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        thanaNameList = new ArrayList<>();
        thanaIdList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            /*thanaIdList.add("0");
            thanaNameList.add("");*/

            for (int i = 0; i < cursor.getCount(); i++) {

                thanaIdList.add(cursor.getString(cursor.getColumnIndex(Tables.THANA_TH_id)));
                thanaNameList.add(cursor.getString(cursor.getColumnIndex(Tables.THANA_NAME)));
                cursor.moveToNext();
            }

            thanaHashMap.put(Tables.THANA_NAME, thanaNameList);
            thanaHashMap.put(Tables.THANA_TH_id, thanaIdList);

        }

        this.close();

        return thanaHashMap;

    }

    public HashMap<String, ArrayList<String>> getRouteList(String byTerritoryId) {

        this.open();

        HashMap<String, ArrayList<String>> thanaHashMap = new HashMap<>();
        ArrayList<String> thanaNameList;
        ArrayList<String> thanaIdList;
        String query;

        String id = byTerritoryId;

        if (id.equals("no")) {

            query = "SELECT " + Tables.ROUTE_NAME + ", " + Tables.ROUTE_ID + " FROM " + TABLE_NAME_ROOT + "";

        } else {
            query = "SELECT " + Tables.ROUTE_NAME + ", " + Tables.ROUTE_ID + " FROM " + Tables.TABLE_NAME_ROOT + " WHERE " + Tables.ROOT_P_id + " = " + id + "";
        }

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        thanaNameList = new ArrayList<>();
        thanaIdList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            /*thanaIdList.add("0");
            thanaNameList.add("");*/

            for (int i = 0; i < cursor.getCount(); i++) {

                thanaIdList.add(cursor.getString(cursor.getColumnIndex(Tables.ROUTE_ID)));
                thanaNameList.add(cursor.getString(cursor.getColumnIndex(Tables.ROUTE_NAME)));
                cursor.moveToNext();
            }

            thanaHashMap.put(Tables.THANA_NAME, thanaNameList);
            thanaHashMap.put(Tables.THANA_TH_id, thanaIdList);

        }

        this.close();

        return thanaHashMap;

    }

    //................Retrieve Route List for Market Create .........................//
    public HashMap<String, ArrayList<String>> getRouteList() {
        this.open();
        HashMap<String, ArrayList<String>> routeHashMap = new HashMap<>();
        ArrayList<String> routeNameList;
        ArrayList<String> routeIdList;
        String query = "SELECT " + Tables.ROUTE_NAME + ", " + Tables.ROUTE_ID + " FROM " + Tables.TABLE_NAME_ROOT + "";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        routeNameList = new ArrayList<>();
        routeIdList = new ArrayList<>();
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
           /*routeIdList.add("0");
           routeNameList.add("");*/
            for (int i = 0; i < cursor.getCount(); i++) {
                routeIdList.add(cursor.getString(cursor.getColumnIndex(Tables.ROUTE_ID)));
                routeNameList.add(cursor.getString(cursor.getColumnIndex(Tables.ROUTE_NAME)));
                cursor.moveToNext();
            }
            routeHashMap.put(Tables.ROUTE_ID, routeIdList);
            routeHashMap.put(Tables.ROUTE_NAME, routeNameList);
        }
        this.close();
        return routeHashMap;
    }

    //................Retrieve Location List for Market Create .........................//
    public HashMap<String, ArrayList<String>> getLocationList() {

        this.open();

        HashMap<String, ArrayList<String>> locationHashMap = new HashMap<>();
        ArrayList<String> locationNameList;
        ArrayList<String> locationIdList;

        String query = "SELECT " + Tables.LOCATION_location_name + ", " + Tables.LOCATION_location_id + " FROM " + Tables.TABLE_NAME_LOCATION + "";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        locationNameList = new ArrayList<>();
        locationIdList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            /*locationIdList.add("0");
            locationNameList.add("");*/

            for (int i = 0; i < cursor.getCount(); i++) {

                locationIdList.add(cursor.getString(cursor.getColumnIndex(Tables.LOCATION_location_id)));
                locationNameList.add(cursor.getString(cursor.getColumnIndex(Tables.LOCATION_location_name)));
                cursor.moveToNext();
            }

            locationHashMap.put(Tables.LOCATION_location_name, locationNameList);
            locationHashMap.put(Tables.LOCATION_location_id, locationIdList);

        }

        this.close();

        return locationHashMap;

    }

    //................Retrieve Outlet Category List for Outlet Create .........................//
    public HashMap<String, ArrayList<String>> getOutletCategories() {

        this.open();

        HashMap<String, ArrayList<String>> outletCategoryHashMap = new HashMap<>();
        ArrayList<String> outletCategoryNameList;
        ArrayList<String> outletCategoryIdList;

        String query = "SELECT " + Tables.OUTLET_CATEGORY_NAME + ", " + Tables.OUTLET_CATEGORY_CATEGORY_ID + " FROM " + Tables.TABLE_NAME_OUTLET_CATEGORY + "";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        outletCategoryNameList = new ArrayList<>();
        outletCategoryIdList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            /*outletCategoryIdList.add("0");
            outletCategoryNameList.add("");*/

            for (int i = 0; i < cursor.getCount(); i++) {

                outletCategoryIdList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLET_CATEGORY_CATEGORY_ID)));
                outletCategoryNameList.add(cursor.getString(cursor.getColumnIndex(Tables.OUTLET_CATEGORY_NAME)));
                cursor.moveToNext();
            }

            outletCategoryHashMap.put(Tables.OUTLET_CATEGORY_NAME, outletCategoryNameList);
            outletCategoryHashMap.put(Tables.OUTLET_CATEGORY_CATEGORY_ID, outletCategoryIdList);

        }

        this.close();

        return outletCategoryHashMap;

    }

    //................Retrieve Productt Category List ........................................//
    public HashMap<String, ArrayList<String>> getProductCategories() {

        this.open();

        HashMap<String, ArrayList<String>> productCategoryHashMap = new HashMap<>();
        ArrayList<String> producttCategoryNameList;
        ArrayList<String> productCategoryIdList;

        String query = "SELECT " + Tables.PPRODUCT_CATEGORY_C_id + ", " + Tables.PPRODUCT_CATEGORY_C_NAME + " FROM " + Tables.TABLE_NAME_PRODUCT_CATEGORY + "";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        producttCategoryNameList = new ArrayList<>();
        productCategoryIdList = new ArrayList<>();

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            /*productCategoryIdList.add("0");
            producttCategoryNameList.add("");*/
            productCategoryIdList.add("0");
            producttCategoryNameList.add("All");
            for (int i = 0; i < cursor.getCount(); i++) {

                productCategoryIdList.add(cursor.getString(cursor.getColumnIndex(Tables.PPRODUCT_CATEGORY_C_id)));
                producttCategoryNameList.add(cursor.getString(cursor.getColumnIndex(Tables.PPRODUCT_CATEGORY_C_NAME)));
                cursor.moveToNext();
            }

            productCategoryHashMap.put(Tables.PPRODUCT_CATEGORY_C_NAME, producttCategoryNameList);
            productCategoryHashMap.put(Tables.PPRODUCT_CATEGORY_C_id, productCategoryIdList);

        }

        this.close();

        return productCategoryHashMap;

    }

    //................Retrieve  thana id , territory id List for Market Create .........................//
    public ArrayList<String> getIdsFromMarketToOutlet(String marketId) {

        this.open();

        ArrayList<String> thanaAndTerritoryId = new ArrayList<>();
        //Loge("for outlet create", "getIdsFromMarketToOutlet: " + marketId);

        String query = "SELECT " + Tables.MARKETS_thana_id + ", " + Tables.MARKETS_Territory_id + ", " + Tables.MARKETS_root_id + " FROM " + Tables.TABLE_NAME_MARKETS + " WHERE " + Tables.MARKETS_market_id + " = '" + marketId + "'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            thanaAndTerritoryId.add(cursor.getString(cursor.getColumnIndex(Tables.MARKETS_thana_id)));
            thanaAndTerritoryId.add(cursor.getString(cursor.getColumnIndex(Tables.MARKETS_Territory_id)));
            thanaAndTerritoryId.add(cursor.getString(cursor.getColumnIndex(Tables.MARKETS_root_id)));

        }

        this.close();

        return thanaAndTerritoryId;

    }

    public void deleteproductboolean(int prductId) {
        open();

        sqLiteDatabase.execSQL("delete from " + TABLE_NAME_PRODUCT_BOOLEAN + " where " + PRODUCT_BOOLEAN_PRODUCT_ID + " = '" + prductId + "'");

        Cursor c = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME_PRODUCT_BOOLEAN, null);
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {


            //Loge("productname", c.getString(c.getColumnIndex(PRODUCT_BOOLEAN_PRODUCT_ID)));

            c.moveToNext();
        }


        close();

    }

    public void update(String tableNameProductBoolean, ContentValues contentValues, int prductId, String outlet) {
        open();

        String[] ar = {prductId + "", outlet};
        long id = sqLiteDatabase.update(tableNameProductBoolean, contentValues, PRODUCT_BOOLEAN_PRODUCT_ID + " =? and " + PRODUCT_BOOLEAN_OUTLET_ID + " =?", ar);
        //Loge("id", ar.toString() + " quantity " + contentValues.toString() + " id " + id);
        close();
    }

    public Double getQuantity(String outlet, int prductId) {
        Double Quantity = 0.00;
        open();
        Cursor c = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME_PRODUCT_BOOLEAN + " where " + PRODUCT_BOOLEAN_PRODUCT_ID + " = '" + prductId + "' and " + PRODUCT_BOOLEAN_OUTLET_ID + " = '" + outlet + "'", null);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            Quantity = c.getDouble(c.getColumnIndex(PRODUCT_BOOLEAN_QUANTITY));
            //Loge("quantityget", Quantity + " pid " + prductId);

        }
        //Loge("quantitynot", Quantity + "");
        return Quantity;
    }

    public HashMap<String, ArrayList<String>> getAccessories(boolean flag, String value, String[] tableName, String filter) {


        String query;
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        ArrayList<String> idlist = new ArrayList<String>();
        ArrayList<String> namelist = new ArrayList<String>();
        if (tableName[0].equalsIgnoreCase(TABLE_NAME_THANA) || tableName[0].equalsIgnoreCase(TABLE_NAME_OUTLET_CATEGORY)) {
            idlist.add("0");
            namelist.add("All");
        }
        if (value.equalsIgnoreCase("00")) {
            query = "select * from " + tableName[0];
        } else {

            query = "select * from " + tableName[0] + " where " + filter + " = '" + value + "'";
        }

       /* if (tableName[0].equalsIgnoreCase(TABLE_NAME_OUTLETS) && !getPreference(OUTLET_CATAGORY[2]).equalsIgnoreCase("00") && !getPreference(MARKETS[2]).equalsIgnoreCase("00")) {

            query = "select * from " + tableName[0] + " where " + OUTLET_CATEGORY_CATEGORY_ID + " = '" + getPreference(OUTLET_CATAGORY[2]) + "' and " + OUTLETS_MARKET_ID + " = '" + getPreference(MARKETS[2]) + "'";

        }*/


        open();

        Cursor c = sqLiteDatabase.rawQuery(query, null);
        //Loge("getAccessories", query + " " + c.getCount());
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            do {
                // //Loge("log", c.getString(c.getColumnIndex(tableName[2])) + " yesss " + c.getString(c.getColumnIndex(tableName[3])));
                idlist.add(c.getString(c.getColumnIndex(tableName[2])));
                namelist.add(c.getString(c.getColumnIndex(tableName[3])));

            } while (c.moveToNext());
        }
        map.put(tableName[2], idlist);
        map.put(tableName[3], namelist);
        close();


        return map;


    }

    public ArrayList<HashMap<String, String>> getProducts(String outletID) {

        ArrayList<HashMap<String, String>> order_reportList = new ArrayList<>();


        String query = "Select * from " + Tables.TABLE_NAME_PRODUCT;
        open();
        Cursor c = sqLiteDatabase.rawQuery(query, null);

        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            do {


                HashMap<String, String> map = new HashMap<>();

                map.put("product_name", c.getString(c.getColumnIndex(Tables.PRODUCT_PRODUCT_NAME)));
                map.put("boolean", "false");
                map.put("so_id", getPreference(Tables.DELETED_MEMOS_sales_person_id));
                map.put("outlet_id", outletID);
                map.put("product_id", c.getString(c.getColumnIndex(PRODUCT_PRODUCT_ID)));

                ContentValues values = new ContentValues();
                values.put("boolean", "false");
                values.put("so_id", getPreference(Tables.DELETED_MEMOS_sales_person_id));
                values.put("outlet_id", outletID);
                values.put("product_id", c.getString(c.getColumnIndex(PRODUCT_PRODUCT_ID)));

                insertBoolenatable(values, Tables.TABLE_NAME_PRODUCT_BOOLEAN);

                order_reportList.add(map);
            } while (c.moveToNext());
        }
        close();

        return order_reportList;
    }

    public ArrayList<HashMap<String, String>> getProductsForcatalog(String category_id) {

        ArrayList<HashMap<String, String>> order_reportList = new ArrayList<>();


        String query = "Select * from " + Tables.TABLE_NAME_PRODUCT + " where product_type_id='1' order by product_order ASC";
        if (!category_id.equalsIgnoreCase("0")) {
            query = "Select * from " + Tables.TABLE_NAME_PRODUCT + " where product_category_id='" + category_id + "' and product_type_id='1' order by product_order ASC";
        }
        open();
        //Loge("productCatagoryq", query);
        Cursor c = sqLiteDatabase.rawQuery(query, null);

        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            do {


                HashMap<String, String> map = new HashMap<>();

                map.put(Tables.PRODUCT_PRODUCT_NAME, c.getString(c.getColumnIndex(Tables.PRODUCT_PRODUCT_NAME)));
                map.put(PRODUCT_PRODUCT_ID, c.getString(c.getColumnIndex(PRODUCT_PRODUCT_ID)));

                order_reportList.add(map);
            } while (c.moveToNext());
        }
        close();

        return order_reportList;
    }

    public void insertIntobooolean(String _OutletID) {

        open();
        String query1 = "SELECT * FROM " + TABLE_NAME_PRODUCT + "  group by product_id,product_name,product_category_id,product_type_id ORDER BY product_order ASC";
        //Loge("query", query1);
        Cursor c2 = sqLiteDatabase.rawQuery(query1, null);
        c2.moveToFirst();
        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {

                    //Loge("EEEEEEEEEEEEEEEEEE", "-----------***********----------");


                    String product_id = c2.getString(c2.getColumnIndex("product_id"));
                    String product_name = c2.getString(c2.getColumnIndex("product_name"));
                    String product_category_id = c2.getString(c2.getColumnIndex("product_category_id"));
                    String product_type_id = c2.getString(c2.getColumnIndex("product_type_id"));


                    //   savePreference(product_id, "0.0");


                    ContentValues map = new ContentValues();

                    map.put("outlet_id", _OutletID);
                    map.put("product_id", product_id);
                    map.put("quantity", "0");
                    map.put("boolean", "false");
                    map.put("product_category_id", product_category_id);
                    map.put("product_type_id", product_type_id);
                    open();
                    Cursor cursor = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME_PRODUCT_BOOLEAN + " Where " + PRODUCT_BOOLEAN_OUTLET_ID + " = '" + _OutletID + "' and " + PRODUCT_BOOLEAN_PRODUCT_ID + " = '" + product_id + "'", null);
                    if (cursor.getCount() <= 0) {
                        insertBoolenatable(map, TABLE_NAME_PRODUCT_BOOLEAN);
                    }


                } while (c2.moveToNext());
            }
        }

        close();
    }

    public ArrayList<HashMap<String, String>> getSelectedProducts() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        open();
        String queryy = "SELECT p.product_name ,pb.product_category_id, pb.quantity ,p.product_id from product as p INNER JOIN product_boolean as pb on p.product_id=pb.product_id  WHERE pb.boolean='true' AND pb.outlet_id='" + getPreference(OUTLETS_ID) + "'";

        Cursor c = sqLiteDatabase.rawQuery(queryy, null);
        //Loge("query", queryy + c.getCount());
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            do {
                HashMap<String, String> product_list_map = new HashMap<String, String>();
                product_list_map.put("product_id", c.getString(3));
                product_list_map.put("product_name", c.getString(0));
                product_list_map.put("quantity", c.getString(2));
                product_list_map.put("product_category_id", c.getString(1));

                list.add(product_list_map);


            } while (c.moveToNext());


        }


        close();
        return list;
    }

    public String getOutletCatagoryId() {
        open();
        String catagotyid = "100";
        Cursor c = sqLiteDatabase.rawQuery("select " + OUTLET_CATEGORY_CATEGORY_ID + " from " + TABLE_NAME_OUTLETS + " where " + OUTLETS_ID + " = '" + getPreference(OUTLETS_ID) + "'", null);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            catagotyid = c.getString(0);
        }
        close();
        return catagotyid;
    }

    public void deleteRowOfMemo(String payments, String memo_no6) {
    }

    public String getLastmemodate(String outletID) {
        String memodate = "No Memo On this Outlet";


        String Query = "SELECT order_date from ORDER_table WHERE outlet_id = '" + outletID + "' ORDER by order_date_time DESC LIMIT 1";

        Cursor c = sqLiteDatabase.rawQuery(Query, null);

        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            return c.getString(0);


        }


        return memodate;
    }

     public String getProductID(String product_id) {
        open();
        Cursor virtual_cursor = sqLiteDatabase.rawQuery("select is_virtual,parent_id from product where product_id="+product_id,null);
        //Loge("p_id", "select is_virtual,parent_id from products where product_id="+product_id);
        virtual_cursor.moveToFirst();
        if (virtual_cursor!=null && virtual_cursor.getCount()>0){
            if (virtual_cursor.getInt(0)==1){

                product_id=virtual_cursor.getString(1);

            }

        }
            close();
        return product_id;
    }

    public String getVirtualProductID(String product_id) {
        open();
        Cursor virtual_cursor = sqLiteDatabase.rawQuery("select is_virtual,parent_id from product where product_id="+product_id,null);
        virtual_cursor.moveToFirst();
        if (virtual_cursor!=null && virtual_cursor.getCount()>0){
            if (virtual_cursor.getInt(0)==0){

                product_id="0";

            }

        }
        virtual_cursor.close();
        close();

        return product_id;
    }

    public void prepareDataForOrder(String _OutletID) {

        new prepareDataForOrder(_OutletID).execute();


    }

    public ArrayList<HashMap<String, String>> updateWithServer(JSONObject jsonObject,int code) throws JSONException {

        try {
            JSONArray jsonArray;
            if (jsonObject.has("orders"))
             jsonArray = jsonObject.getJSONArray("orders");
            else {
                jsonArray = jsonObject.getJSONArray("order_table");
            }

            for (int j = 0; j < jsonArray.length(); j++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                //Loge("jsonobj", jsonObject1.getString(ORDER[2]));


                ContentValues contentValues = new ContentValues();
                for (int i = 2; i < ORDER.length; i++) {

                    if (jsonObject1.has(ORDER[i])) {
                        try {
                            //Loge(ORDER[i], jsonObject1.getString(ORDER[i]));
                            if (ORDER[i].equalsIgnoreCase(ORDER_STATUS)){
                                if (CheckOrderStatus(jsonObject1.getString(ORDER_order_number))==PROCESSING_ON_SERVER){
                                    contentValues.put(ORDER[i], PROCESSING_ON_SERVER);
                                    continue;
                                }

                            }
                            contentValues.put(ORDER[i], jsonObject1.getString(ORDER[i]));
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            //Loge("updateOrderWithServer1", ex.getMessage());
                        }
                    }


                }

                String[] ar = {jsonObject1.getString(ORDER[2])};
                open();
                //Loge("cv", contentValues.toString());
                if (updateOrInsert(ORDER[0], ORDER[2], jsonObject1.getString(ORDER[2]))) {
                    open();
                    sqLiteDatabase.update(ORDER[0], contentValues, ORDER_order_number + " =? ", ar);
                    close();
                }else {

                    contentValues.put(ORDER_is_pushed, 1);
                    contentValues.put(ORDER_STATUS, PROCESSING_PENDING);
                    open();
                    sqLiteDatabase.insert(ORDER[0], null, contentValues);
                    close();
                }




                excQuery("DELETE from ORDER_DETAILS where order_number ='" + jsonObject1.getString(ORDER[2]) + "'");
                JSONArray jsonArray1 = jsonObject1.getJSONArray("order_details");

                for (int k = 0; k < jsonArray1.length(); k++) {
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                    ContentValues contentValues2 = new ContentValues();
                    for (int i = 2; i < ORDER_DETAILS.length; i++) {

                        if (jsonObject2.has(ORDER_DETAILS[i]))
                            contentValues2.put(ORDER_DETAILS[i], jsonObject2.getString(ORDER_DETAILS[i]));

                    }

                    open();


                    long insert = sqLiteDatabase.insert(ORDER_DETAILS[0], null, contentValues2);
                    //Loge("cv2", contentValues2.toString() + "  insert" + insert);
                    close();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            //Loge("updateOrderWithServer", e.getMessage());
        }
        close();

        if (code==5){
            progressDialog.dismiss();
            dbListener.OnLocalDBdataRetrive(code+"");
        }


        return null;
    }

    private int CheckOrderStatus(String string) {
        this.open();
        int status =0 ;
        Cursor c = sqLiteDatabase.rawQuery("Select " + ORDER_STATUS +  " from " + TABLE_NAME_ORDER + " where  order_number='" + string + "'", null);

        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            status = c.getInt(0);
        }
        close();
        return status;
    }

    public ArrayList<HashMap<String, String>> updateMemoWithServer(JSONObject jsonObject,int code) throws JSONException {

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("memos");

            for (int j = 0; j < jsonArray.length(); j++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                //Loge("jsonobj", jsonObject1.getString(MEMOS[2]));


                ContentValues contentValues = new ContentValues();
                for (int i = 2; i < MEMOS.length; i++) {

                    if (jsonObject1.has(MEMOS[i])) {
                        try {
                            //Loge(MEMOS[i], jsonObject1.getString(MEMOS[i]));
                            contentValues.put(MEMOS[i], jsonObject1.getString(MEMOS[i]));
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            //Loge("updateOrderWithServer1", ex.getMessage());
                        }
                    }


                }

                String[] ar = {jsonObject1.getString(MEMOS[2])};

                //Loge("cv", contentValues.toString());
                if (updateOrInsert(MEMOS[0], MEMOS[2], jsonObject1.getString(MEMOS[2]))) {
                    open();
                    sqLiteDatabase.update(MEMOS[0], contentValues, MEMOS_memo_number + " =? ", ar);

                }else {
                    contentValues.put(ORDER_is_pushed, 1);
                    // contentValues.put(ORDER_STATUS, PROCESSING_PENDING);
                    open();
                    sqLiteDatabase.insert(MEMOS[0], null, contentValues);

                }
                close();



                excQuery("DELETE from memo_details where memo_number ='" + jsonObject1.getString(MEMOS[2]) + "'");
                JSONArray jsonArray1 = jsonObject1.getJSONArray("memo_details");

                for (int k = 0; k < jsonArray1.length(); k++) {
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                    ContentValues contentValues2 = new ContentValues();
                    for (int i = 2; i < MEMO_DETAILS.length; i++) {

                        if (jsonObject2.has(MEMO_DETAILS[i]))
                            contentValues2.put(MEMO_DETAILS[i], jsonObject2.getString(MEMO_DETAILS[i]));



                    }

                    open();


                    long insert = sqLiteDatabase.insert(MEMO_DETAILS[0], null, contentValues2);
                    //Loge("cv2", contentValues2.toString() + "  insert" + insert);
                    close();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("updateMemoWithServer", e.getMessage());
        }
        if (code==6){
            progressDialog.dismiss();
            if (dbListener!=null)
            dbListener.OnLocalDBdataRetrive(code+"");
        }

        return null;
    }


    public ArrayList<HashMap<String, String>> updateWithServerProcessed(JSONObject jsonObject, int i) {

        new updateorderWithServer(jsonObject, i).execute();


        return null;
    }

    public ArrayList<HashMap<String, String>> getPolicyList(String product_id) {

        ArrayList<HashMap<String, String>> list = null;
        try {
            list = new ArrayList<>();

            Cursor cursor = sqLiteDatabase.rawQuery("select policy_id from policy_root_product where root_product_id='" + product_id + "'",null);
            //Loge("DataPrint0", DatabaseUtils.dumpCursorToString(cursor));

            cursor.moveToFirst();

            if (cursor != null && cursor.getCount() > 0) {
                do {

                    Cursor cursor1 = sqLiteDatabase.rawQuery("select * from policy_product_option where policy_id='" + cursor.getString(0) + "'",null);
                    //Loge("DataPrint", DatabaseUtils.dumpCursorToString(cursor1));
                    cursor1.moveToFirst();
                    if (cursor1 != null && cursor1.getCount() > 0) {
                        do {

                            HashMap<String, String> map = new HashMap<>();

                            map.put("rqty", cursor1.getString(cursor1.getColumnIndex("min_qty")));
                            if (cursor1.getInt(cursor1.getColumnIndex("policy_type")) == 1) {   // PolicyType 1=
                                map.put("discount", "------");
                                map.put("condition", "only");
                                map.put("price", "------");

                                Cursor cursor2 = sqLiteDatabase.rawQuery("select * from policy_bonus_product where policy_id='" + cursor1.getString(cursor1.getColumnIndex("policy_id")) + "' and option_id='" + cursor1.getString(cursor1.getColumnIndex("option_id")) + "'",null);
                                //Loge("DataPrint", DatabaseUtils.dumpCursorToString(cursor2));

                                cursor2.moveToFirst();
                                if (cursor2 != null && cursor2.getCount() > 0) {
                                    do {

                                        map.put("product_name", getProductName(cursor2.getString(cursor2.getColumnIndex("bonus_product_id"))));
                                        map.put("bqty", cursor2.getString(cursor2.getColumnIndex("bonus_qty")) + "(" + getUnitName(cursor2.getString(cursor2.getColumnIndex("unit_id"))) + ")");


                                    } while (cursor2.moveToNext());

                                }


                            } else if (cursor1.getInt(cursor1.getColumnIndex("policy_type")) == 2) { // policyType 2=
                                map.put("product_name", "------");
                                map.put("bqty", "------");
                                if (cursor1.getInt(cursor1.getColumnIndex("discount_type")) == 0)
                                    map.put("discount", cursor1.getString(cursor1.getColumnIndex("discount_amount")) + "%");
                                else
                                    map.put("discount", cursor1.getString(cursor1.getColumnIndex("discount_amount")));

                                map.put("condition", "only");
                                //Loge("option_id", cursor1.getString(cursor1.getColumnIndex("option_id")));
                                map.put("price", getproductPrice(cursor1.getString(cursor1.getColumnIndex("option_id")), product_id));
                            }

                            else if (cursor1.getInt(cursor1.getColumnIndex("policy_type")) == 0) {
                                Cursor cursor2 = sqLiteDatabase.rawQuery("select * from policy_bonus_product where policy_id='" + cursor1.getString(cursor1.getColumnIndex("policy_id")) + "' and option_id='" + cursor1.getString(cursor1.getColumnIndex("option_id")) + "'",null);
                                cursor2.moveToFirst();
                                if (cursor2 != null && cursor2.getCount() > 0) {
                                    do {

                                        map.put("product_name", getProductName(cursor2.getString(cursor2.getColumnIndex("bonus_product_id"))));
                                        map.put("bqty", cursor2.getString(cursor2.getColumnIndex("bonus_qty")) + "(" + getUnitName(cursor2.getString(cursor2.getColumnIndex("measurement_unit_id"))) + ")");


                                    } while (cursor2.moveToNext());

                                }
                                map.put("condition", "AND");

                                if (cursor1.getInt(cursor1.getColumnIndex("discount_type")) == 0)
                                    map.put("discount", cursor1.getString(cursor1.getColumnIndex("discount_amount")) + "%");
                                else
                                    map.put("discount", cursor1.getString(cursor1.getColumnIndex("discount_amount")));


                                    map.put("price", getproductPrice(cursor1.getString(cursor1.getColumnIndex("option_id")), product_id));


                            } else if (cursor1.getInt(cursor1.getColumnIndex("policy_type")) == 3) {
                                Cursor cursor2 = sqLiteDatabase.rawQuery("select * from policy_bonus_product where policy_id='" + cursor1.getString(cursor1.getColumnIndex("policy_id")) + "' and option_id='" + cursor1.getString(cursor1.getColumnIndex("option_id")) + "'",null);
                                cursor2.moveToFirst();
                                if (cursor2 != null && cursor2.getCount() > 0) {
                                    do {

                                        map.put("product_name", getProductName(cursor2.getString(cursor2.getColumnIndex("bonus_product_id"))));
                                        map.put("bqty", cursor2.getString(cursor2.getColumnIndex("bonus_qty")) + "(" + getUnitName(cursor2.getString(cursor2.getColumnIndex("measurement_unit_id"))) + ")");


                                    } while (cursor2.moveToNext());

                                }
                                cursor2.close();
                                map.put("condition", "OR");

                                if (cursor1.getInt(cursor1.getColumnIndex("discount_type")) == 0)
                                    map.put("discount", cursor1.getString(cursor1.getColumnIndex("discount_amount")) + "%");
                                else
                                    map.put("discount", cursor1.getString(cursor1.getColumnIndex("discount_amount")));

                                //Loge("option_id", cursor1.getString(cursor1.getColumnIndex("option_id")));
                                map.put("price", getproductPrice(cursor1.getString(cursor1.getColumnIndex("option_id")), product_id));


                            }


                            list.add(map);
                        } while (cursor1.moveToNext());

                        cursor1.close();
                    }


                } while (cursor.moveToNext());

                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }

    private String getproductPrice(String option_id, String product_id) {
        //Loge("option_idingetproductPrice", option_id);
        open();
        Cursor c = sqLiteDatabase.rawQuery("select pc.price from product_combinations as pc inner join  policy_option_price_slab as pops on pops.slab_id = pc.slab_id where pops.option_product_id='" + product_id + "' and  pops.policy_product_Option_id='" + option_id + "'", null);
        c.moveToFirst();
        String price ="0";
        if (c != null && c.getCount() > 0) {

            price= c.getString(0);
        }
        c.close();
        close();
        return price;

    }

    private boolean updateOrInsert(String table_name, String check_colum, String check_value, String check_colum2, String check_value2) {
        Boolean update = false;
        open();
        Cursor c = sqLiteDatabase.rawQuery("Select * from " + table_name + " where " + check_colum + " ='" + check_value + "' and " + check_colum2 + " ='" + check_value2 + "'", null);
        //Loge("updateOrInsert2", "Select * from " + table_name + " where " + check_colum + " ='" + check_value + "' and " + check_colum2 + " ='" + check_value2 + "'" + c.getCount());
        if (c != null && c.getCount() > 0) {

            update = true;
        }
        //Loge("updateOrInsertdetails", update + "   " + check_value2);
        return update;
    }

    private boolean updateOrInsert(String table_name, String check_colum, String check_value) {
        Boolean update = false;
        open();
        Cursor c = sqLiteDatabase.rawQuery("Select * from " + table_name + " where " + check_colum + " ='" + check_value + "'", null);

        if (c != null && c.getCount() > 0) {

            update = true;
        }
        c.close();
        //Loge("updateOrInsert", update + "");

        return update;

    }

    public ArrayList<HashMap<String, String>> getNotPushedOrder(String start_date, String end_date, String outlate_id, String outlate_category_id,int offset) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        //Loge("Item", outlate_id + "  " + outlate_category_id);
        open();
        String Query = "";

        if (outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT * FROM " + Tables.TABLE_NAME_ORDER + " where " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "'  ORDER BY order_number ASC";
        else if (!outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT * FROM " + Tables.TABLE_NAME_ORDER + " where " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "' AND " + ORDER_outlet_id + "='" + outlate_id + "' ORDER BY order_number ASC";
        else if (!outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT * FROM " + Tables.TABLE_NAME_ORDER + " as ot INNER join outlets as o on ot.outlet_id=o.outlet_id  where o.outlet_category_id='" + outlate_category_id + "' and  " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "' AND ot." + ORDER_outlet_id + "='" + outlate_id + "' ORDER BY order_number ASC";
        else if (outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT * FROM " + Tables.TABLE_NAME_ORDER + " as ot INNER join outlets as o on ot.outlet_id=o.outlet_id  where o.outlet_category_id='" + outlate_category_id + "' and  " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "'  ORDER BY order_number ASC";
        //Loge("DataView2", Query);
        Query+= " LIMIT "+ (offset*25)+" , 25";
        Cursor c = sqLiteDatabase.rawQuery(Query, null);
        Log.e("query_order_report", "getNotPushedOrder: "+Query);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            do {

                HashMap<String, String> map = new HashMap<>();

                for (int i = 2; i < ORDER.length; i++) {

                    if (ORDER[i].equalsIgnoreCase(ORDER_outlet_id)) {

                        map.put(ORDER[i], c.getString(c.getColumnIndex(ORDER[i])));
                        map.put(OUTLETS_OUTLET_NAME, getSingleFilter(TABLE_NAME_OUTLETS, c.getString(c.getColumnIndex(ORDER[i])), OUTLETS_ID, OUTLETS_OUTLET_NAME));
                    } else {
                        map.put(ORDER[i], c.getString(c.getColumnIndex(ORDER[i])));
                        //Loge(ORDER[i], c.getString(c.getColumnIndex(ORDER[i])) + "");
                    }
                }

                list.add(map);

            } while (c.moveToNext());
        }


        return list;
    }

    public void getProcessedOrder(String start_date, String end_date, String outlate_id) {

        new getProcessedOrders(start_date, end_date, outlate_id).execute();

    }

    public ArrayList<HashMap<String, String>> getMemos(String start_date, String end_date, String outlate_id, String outlate_category_id,int offset) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        open();
        String Query = "";
        if (outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT * FROM " + TABLE_NAME_MEMOS + " where " + MEMOS_memo_date + ">=" + "'" + start_date + "'" + " and " + Tables.MEMOS_memo_date + " <=" + "'" + end_date + "'  ORDER BY order_number ASC";
        else if (!outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT * FROM " + Tables.TABLE_NAME_MEMOS + " where " + Tables.MEMOS_memo_date + ">=" + "'" + start_date + "'" + " and " + Tables.MEMOS_memo_date + " <=" + "'" + end_date + "' AND " + ORDER_outlet_id + "='" + outlate_id + "' ORDER BY order_number ASC";
        else if (!outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT * FROM " + Tables.TABLE_NAME_MEMOS + " as ot INNER join outlets as o on ot.outlet_id=o.outlet_id  where o.outlet_category_id='" + outlate_category_id + "' and  " + Tables.MEMOS_memo_date + ">=" + "'" + start_date + "'" + " and " + Tables.MEMOS_memo_date + " <=" + "'" + end_date + "' AND ot." + ORDER_outlet_id + "='" + outlate_id + "' ORDER BY order_number ASC";
        else if (outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT * FROM " + Tables.TABLE_NAME_MEMOS + " as ot INNER join outlets as o on ot.outlet_id=o.outlet_id  where o.outlet_category_id='" + outlate_category_id + "' and  " + Tables.MEMOS_memo_date + ">=" + "'" + start_date + "'" + " and " + Tables.MEMOS_memo_date + " <=" + "'" + end_date + "'  ORDER BY order_number ASC";
        //Loge("DataView", Query);
        Query+= " LIMIT "+ (offset*25)+" , 25";
        Cursor c = sqLiteDatabase.rawQuery(Query, null);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            do {

                HashMap<String, String> map = new HashMap<>();

                for (int i = 2; i < MEMOS.length; i++) {

                    if (MEMOS[i].equalsIgnoreCase(ORDER_outlet_id)) {

                        map.put(MEMOS[i], c.getString(c.getColumnIndex(MEMOS[i])));
                        map.put(OUTLETS_OUTLET_NAME, getSingleFilter(TABLE_NAME_OUTLETS, c.getString(c.getColumnIndex(MEMOS[i])), OUTLETS_ID, OUTLETS_OUTLET_NAME));
                    } else {
                        map.put(MEMOS[i], c.getString(c.getColumnIndex(MEMOS[i])));
                        //Loge(MEMOS[i], c.getString(c.getColumnIndex(MEMOS[i])) + "");
                    }
                }

                list.add(map);

            } while (c.moveToNext());
        }


        return list;
    }





    public HashMap<String, String> getEcandMemoAmount(String start_date, String end_date, String outlate_id, String outlate_category_id) {
     HashMap<String, String> map = new HashMap<>();
        open();
        String Query = "";
        if (outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT Sum(gross_value) FROM " + TABLE_NAME_MEMOS + " where " + MEMOS_memo_date + ">=" + "'" + start_date + "'" + " and " + Tables.MEMOS_memo_date + " <=" + "'" + end_date + "'  ORDER BY order_number ASC";
        else if (!outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT Sum(gross_value) FROM " + Tables.TABLE_NAME_MEMOS + " where " + Tables.MEMOS_memo_date + ">=" + "'" + start_date + "'" + " and " + Tables.MEMOS_memo_date + " <=" + "'" + end_date + "' AND " + ORDER_outlet_id + "='" + outlate_id + "' ORDER BY order_number ASC";
        else if (!outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT Sum(gross_value) FROM " + Tables.TABLE_NAME_MEMOS + " as ot INNER join outlets as o on ot.outlet_id=o.outlet_id  where o.outlet_category_id='" + outlate_category_id + "' and  " + Tables.MEMOS_memo_date + ">=" + "'" + start_date + "'" + " and " + Tables.MEMOS_memo_date + " <=" + "'" + end_date + "' AND ot." + ORDER_outlet_id + "='" + outlate_id + "' ORDER BY order_number ASC";
        else if (outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT Sum(gross_value) FROM " + Tables.TABLE_NAME_MEMOS + " as ot INNER join outlets as o on ot.outlet_id=o.outlet_id  where o.outlet_category_id='" + outlate_category_id + "' and  " + Tables.MEMOS_memo_date + ">=" + "'" + start_date + "'" + " and " + Tables.MEMOS_memo_date + " <=" + "'" + end_date + "'  ORDER BY order_number ASC";
        //Loge("DataView", Query);

        Cursor c = sqLiteDatabase.rawQuery(Query, null);
        Log.e("getEcandMemoAmount", "getNotPushedOrder: "+Query);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            map.put("memo_value",c.getString(0));
        }else {
            map.put("memo_value","0.00");
        }

        if (outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query= Query.replace("Sum(gross_value)","count(_id)");
        else if (!outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query= Query.replace("Sum(gross_value)","count(_id)");
        else if (!outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query= Query.replace("Sum(gross_value)","count(ot._id)");
        else if (outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query= Query.replace("Sum(gross_value)","count(ot._id)");




        Cursor c1 = sqLiteDatabase.rawQuery(Query, null);
        Log.e("getNotPushedOrder", "getNotPushedOrder: "+Query);
        c1.moveToFirst();
        if (c1 != null && c1.getCount() > 0) {

            map.put("EC",c1.getString(0));
        }else {
            map.put("EC","0");
        }

        Log.e("getEcandMemoAmount", map.toString() +"  "+Query);
        return map ;
    }

    private String getSingleFilter(String tableName, String value, String outletsId, String retunr_field) {
        open();
        String valur = "Not Exist";
        Cursor cursor = sqLiteDatabase.rawQuery("select " + retunr_field + " from " + tableName + " where " + outletsId + "='" + value + "'", null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {


            valur = cursor.getString(0);


        }

        cursor.close();


        return valur;
    }

    public void getOrderDetails() {
        open();


    }

    public void generateSingleOrder(final String orderNo) {
        //Loge("order", orderNo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new generatePushJsonForSingle(orderNo).execute();
            }
        });

    }

    public void generateSingleTempMemo(final String memoNo) {
        //Loge("order", memoNo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new generatePushJsonForSingleTempMemo(memoNo).execute();
            }
        });
    }

    public void generateSingleMemo(final String memoNo) {
        //Loge("order", memoNo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new generatePushJsonForSingleMemo(memoNo).execute();
            }
        });
    }

    public void updateOrderStatus(String dist_order_no, String process_status) {
        open();
        sqLiteDatabase.execSQL("update " + TABLE_NAME_ORDER + " set " + Tables.ORDER_STATUS + " = '" + process_status + "' where " + ORDER_order_number + " ='" + dist_order_no + "'");
        close();
    }

    public void updatepushStatus(String dist_order_no, String IS_PUSH) {
        open();
        sqLiteDatabase.execSQL("update " + TABLE_NAME_ORDER + " set " + ORDER_is_pushed + " = '" + IS_PUSH + "' where " + ORDER_order_number + " ='" + dist_order_no + "'");
        close();
    }

    public long updateMarket(HashMap<String, String> updatedMarket, String marketId) {
        this.open();
        String[] id = {marketId};
        Date currentTime = Calendar.getInstance().getTime();
        ContentValues contentValues = new ContentValues();
        for (String i : updatedMarket.keySet()) {
            contentValues.put(i, updatedMarket.get(i));
        }
        //contentValues.put(Tables.OUT,String.valueOf(currentTime));
        long x = sqLiteDatabase.update(Tables.TABLE_NAME_MARKETS, contentValues, Tables.MARKETS_market_id + " =?", id);
        return x;
    }

    public String getPendingOrderCount() {

        open();
        Cursor c = sqLiteDatabase.rawQuery("SELECT count(_id) from ORDER_table WHERE is_Pushed='0'", null);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            return c.getString(0);
        }
        return "0";
    }

    public void updatePushStatus() {
        //Loge("log", "update ORDER_table set is_pushed ='1'");
        excQuery("update ORDER_table set is_pushed ='1'");
    }

    public String getTotalCashOfCurrentDay() {

        open();
        Cursor c = sqLiteDatabase.rawQuery("SELECT sum(gross_value) FROM ORDER_table  where order_date>='" + getCurrentDate() + "' and order_date <='" + getCurrentDate() + "' ORDER BY _id DESC", null);

        //Loge("Query_orderAmt", "SELECT sum(gross_value) FROM ORDER_table  where order_date>='" + getCurrentDate() + "'  and order_date <='" + getCurrentDate() + "' ORDER BY _id DESC");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            return c.getString(0);

        }

        return "0";
    }

    public String getTotalOCofCurrentDay() {

        open();
        Cursor c = sqLiteDatabase.rawQuery("SELECT count(DISTINCT(outlet_id)) FROM ORDER_table  where order_date>='" + getCurrentDate() + "'  and order_date <='" + getCurrentDate() + "' ORDER BY _id DESC", null);
        //Loge("Query", "SELECT count(_id) FROM ORDER_table  where order_date>='" + getCurrentDate() + "'  and order_date <='" + getCurrentDate() + "' ORDER BY _id DESC");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            return c.getString(0);

        }

        return "0";
    }

    public String getOc() {

        open();
        Cursor c = sqLiteDatabase.rawQuery("SELECT count(DISTINCT(outlet_id)) FROM outlet_visit  where created_at>='" + getCurrentDate() + "'  and created_at <='" + getCurrentDate() + "' ORDER BY _id DESC", null);
        //Loge("Query", "SELECT count(_id) FROM ORDER_table  where order_date>='" + getCurrentDate() + "'  and order_date <='" + getCurrentDate() + "' ORDER BY _id DESC");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            return c.getString(0);

        }

        return "0";
    }

    public ArrayList<HashMap<String, String>> getlastorders(String outlate_id, String memoNumber) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        open();

        Cursor c = sqLiteDatabase.rawQuery("Select " + ORDER_order_number + " , " + ORDER_order_date + " , " + ORDER_gross_value + " from " + TABLE_NAME_ORDER + " where status !='2' and outlet_id='" + outlate_id + "'", null);

        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(ORDER_order_number, c.getString(c.getColumnIndex(ORDER_order_number)));
                map.put(ORDER_order_date, c.getString(c.getColumnIndex(ORDER_order_date)));
                map.put(ORDER_gross_value, c.getString(c.getColumnIndex(ORDER_gross_value)));
                map.put("is_memo", "0");


                list.add(map);

            } while (c.moveToNext());
        }

        Cursor c1 = sqLiteDatabase.rawQuery("Select " + MEMOS_memo_number + " , " + MEMOS_memo_date + " , " + MEMOS_gross_value + " from " + TABLE_NAME_MEMOS + " where memo_number ='" + memoNumber + "' and outlet_id='" + outlate_id + "'", null);
        c1.moveToFirst();
        //Loge("Query ", "Select " + MEMOS_memo_number + " , " + MEMOS_memo_date + " , " + MEMOS_gross_value + " from " + TABLE_NAME_MEMOS + " where memo_number !='" + memoNumber + "' and outlet_id='" + outlate_id + "'" + " " + c1.getCount());
        if (c1 != null && c1.getCount() > 0) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(ORDER_order_number, c1.getString(c1.getColumnIndex(MEMOS_memo_number)));
                map.put(ORDER_order_date, c1.getString(c1.getColumnIndex(MEMOS_memo_date)));
                map.put(ORDER_gross_value, c1.getString(c1.getColumnIndex(MEMOS_gross_value)));
                map.put("is_memo", "1");


                list.add(0, map);

            } while (c1.moveToNext());

        }


        return list;
    }

    public boolean getDuplicateOutlet(String outletName, String marketId) {
        this.open();

        Cursor c = sqLiteDatabase.rawQuery("select * from outlets where outlet_name='" + outletName + "' and market_id='" + marketId + "'", null);

        return c != null && c.getCount() > 0;
    }

    private JSONObject getGiftIssue(JSONObject finaljsonObject, String order_number) {


        String[] operation = GIFT_ISSUE;
        open();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + operation[0] + " where " + Tables.ORDER_is_pushed + " ='0' and " + ORDER_order_number + " = '" + order_number + "'", null);
        cursor.moveToFirst();

        JSONArray jsonArray = new JSONArray();
        if (cursor != null && cursor.getCount() > 0) {
            do {

                JSONObject jsonObject = new JSONObject();
                //  jsonObject = gethelper(jsonObject,cursor.getString(cursor.getColumnIndex(MARKET_ID)));
                for (int i = 1; i < operation.length; i++) {

                    try {
                        //Loge(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                        if (operation[i].equalsIgnoreCase("territory_id")) {

                            jsonObject.put(operation[i], "123456");
                        } else
                            jsonObject.put(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Loge("json", e.getMessage());
                    }

                }
                String[] operationin = GIFT_ISSUE_DETAILS;
                Cursor cursorin = sqLiteDatabase.rawQuery("select * from " + operationin[0] + " where " + Tables.GIFT_ISSUE_DETAILS_gift_issue_id + " ='" + order_number + "'", null);
                cursorin.moveToFirst();

                JSONArray jsonArrayin = new JSONArray();
                if (cursorin != null && cursorin.getCount() > 0) {
                    do {
                        JSONObject jsonObjectin = new JSONObject();
                        for (int i = 2; i < operationin.length - 2; i++) {

                            try {
                                //Loge(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                if (operationin[i].equalsIgnoreCase("territory_id")) {

                                    jsonObjectin.put(operationin[i], "123456");
                                } else {
                                    if (cursorin.getString(cursorin.getColumnIndex(operationin[i])) == null)
                                        jsonObjectin.put(operationin[i], 0);
                                    else
                                        jsonObjectin.put(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Loge("json", e.getMessage());
                            }

                        }


                        jsonArrayin.put(jsonObjectin);


                    } while (cursorin.moveToNext());


                }

                cursorin.close();

                try {
                    jsonObject.put(operationin[0], jsonArrayin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                jsonArray.put(jsonObject);
            } while (cursor.moveToNext());


        }

        try {
            finaljsonObject.put(operation[0], jsonArray);


            // progressDialog.dismiss();
                close();

        } catch (JSONException e) {
            e.printStackTrace();
            //Loge("log", e.getMessage());
        }


        return finaljsonObject;
    }

    private JSONObject gethelper(JSONObject jsonObject, String market_id) {
      open();
        Cursor c = sqLiteDatabase.rawQuery("Select thana_id , route_id from markets where market_id='" + market_id + "'", null);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {


            try {
                jsonObject.put("thana_id", c.getString(0));
                jsonObject.put("route_id", c.getString(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        c.close();
        close();

        return jsonObject;

    }

    private void insertbonusPolicyData(JSONObject jsonObject) {
        try {

            if (jsonObject != null) {
                JSONObject Bonus_policy_Main;
                //Loge("response_Bonus_Policy ", jsonObject.toString());
                JSONObject jsonObj = new JSONObject(jsonObject.toString());

                // Getting JSON Array node
                HashMap<String,String> datacheck1 = new HashMap<String, String>();
                datacheck1.put("table_name","Policy_Table");
                datacheck1.put("item_count",jsonObj.getJSONArray("policy_list").length()+"");
                InsertTable(datacheck1,"dataCheck");
                for (int i = 0; i < jsonObj.getJSONArray("policy_list").length(); i++) {
                    //Loge("policy_table", "policy_table.toString()");

                    JSONObject policy_table = null;

                    JSONArray policy_group,excludingOutletGroupSr, policy_outlet_category, policy_root_product, policy_product_option,
                            policy_option_price_slap, policy_bonus_product, policy_special_group, policy_option_exclusion, policy_option_inclusion;


                    Bonus_policy_Main = jsonObj.getJSONArray("policy_list").getJSONObject(i);


                    policy_table = Bonus_policy_Main.getJSONObject("DiscountBonusPolicy");//DiscountBonusPolicy

                    policy_group = Bonus_policy_Main.getJSONArray("DiscountBonusPolicyToOutletGroupSr");
                    excludingOutletGroupSr = Bonus_policy_Main.getJSONArray("DiscountBonusPolicyToExcludingOutletGroupSr");//-Nov 15, 2021

                    policy_outlet_category = Bonus_policy_Main.getJSONArray("DiscountBonusPolicyToOutletCategorySr");
                    policy_root_product = Bonus_policy_Main.getJSONArray("DiscountBonusPolicyProduct");
                    policy_product_option = Bonus_policy_Main.getJSONArray("DiscountBonusPolicyOptionSr");
                    policy_special_group = Bonus_policy_Main.getJSONArray("DiscountBonusPolicyToSpecialGroupSr");//DiscountBonusPolicyToSpecialGroupSo -new add

                    try {
                        //Loge("policy_table", policy_table.toString());
                        HashMap<String, String> map = new HashMap<>();
                        map.put("policy_id", policy_table.getString("id"));
                        map.put("start_date", policy_table.getString("start_date"));
                        map.put("policy_name", policy_table.getString("name"));
                        map.put("end_date", policy_table.getString("end_date"));
                        map.put("updated_at", policy_table.getString("updated_at"));
                        map.put("bonus_applicable", policy_table.getString("policy_applicable"));
                        InsertTable(map, "Policy_Table");
                    } catch (Exception e) {
                        //Loge("policy_tablex1", e.getMessage());
                    }

                    HashMap<String,String> datacheck = new HashMap<String, String>();
                    datacheck.put("table_name","Bonus_Eligible_Group");
                    datacheck.put("item_count",policy_group.length()+"");
                    InserOrupdate(datacheck,"dataCheck");
                    for (int t = 0; t < policy_group.length(); t++) {
                        HashMap<String, String> map1 = new HashMap<>();
                        map1.put("outlet_group_id", policy_group.getJSONObject(t).getString("reffrence_id")); //outlet_group_id
                        map1.put("policy_id", policy_group.getJSONObject(t).getString("discount_bonus_policy_id"));
                        InsertTable(map1, "Bonus_Eligible_Group");
                    }

                    datacheck = new HashMap<String, String>();
                    datacheck.put("table_name","Bonus_Eligible_Outlet_Categories");
                    datacheck.put("item_count",policy_outlet_category.length()+"");
                    InserOrupdate(datacheck,"dataCheck");
                    for (int t = 0; t < policy_outlet_category.length(); t++) {
                        HashMap<String, String> map1 = new HashMap<>();
                        map1.put("category_id", policy_outlet_category.getJSONObject(t).getString("reffrence_id")); //outlet_category_id
                        map1.put("policy_id", policy_outlet_category.getJSONObject(t).getString("discount_bonus_policy_id"));
                        InsertTable(map1, "Bonus_Eligible_Outlet_Categories");
                    }

                    // Added 16 Nov 2021
                    datacheck = new HashMap<String, String>();
                    datacheck.put("table_name","DiscountBonusPolicyToExcludingOutletGroupSr");
                    datacheck.put("item_count",excludingOutletGroupSr.length()+"");
                    InserOrupdate(datacheck,"dataCheck");
                    for (int exsp = 0; exsp < excludingOutletGroupSr.length(); exsp++) {
                        HashMap<String, String> exspMap = new HashMap<>();
                        exspMap.put("_id", excludingOutletGroupSr.getJSONObject(exsp).getString("id"));
                        exspMap.put("discount_bonus_policy_id", excludingOutletGroupSr.getJSONObject(exsp).getString("discount_bonus_policy_id"));
                        exspMap.put("for_so_sr", excludingOutletGroupSr.getJSONObject(exsp).getString("for_so_sr"));
                        exspMap.put("create_for", excludingOutletGroupSr.getJSONObject(exsp).getString("create_for"));
                        exspMap.put("reffrence_id", excludingOutletGroupSr.getJSONObject(exsp).getString("reffrence_id"));
                        InsertTable(exspMap, "DiscountBonusPolicyToExcludingOutletGroupSr");
                    }

                    datacheck = new HashMap<String, String>();
                    datacheck.put("table_name","DiscountBonusPolicyToSpecialGroupSo");
                    datacheck.put("item_count",policy_special_group.length()+"");
                    InserOrupdate(datacheck,"dataCheck");
                    // Added 23 April 2021
                    for (int sp = 0; sp < policy_special_group.length(); sp++) {
                        HashMap<String, String> spMap = new HashMap<>();
                        spMap.put("_id", policy_special_group.getJSONObject(sp).getString("id"));
                        spMap.put("discount_bonus_policy_id", policy_special_group.getJSONObject(sp).getString("discount_bonus_policy_id"));
                        spMap.put("create_for", policy_special_group.getJSONObject(sp).getString("create_for"));
                        spMap.put("reffrence_id", policy_special_group.getJSONObject(sp).getString("reffrence_id"));
                        InsertTable(spMap, "DiscountBonusPolicyToSpecialGroupSo");
                    }
                    datacheck = new HashMap<String, String>();
                    datacheck.put("table_name","policy_root_product");
                    datacheck.put("item_count",policy_root_product.length()+"");
                    InserOrupdate(datacheck,"dataCheck");
                    for (int t = 0; t < policy_root_product.length(); t++) {
                        HashMap<String, String> map1 = new HashMap<>();
                        map1.put("root_product_id", policy_root_product.getJSONObject(t).getString("product_id"));
                        map1.put("policy_id", policy_root_product.getJSONObject(t).getString("discount_bonus_policy_id"));
                        InsertTable(map1, "policy_root_product");
                    }
                    datacheck = new HashMap<String, String>();
                    datacheck.put("table_name","policy_product_Option");
                    datacheck.put("item_count",policy_product_option.length()+"");
                    InserOrupdate(datacheck,"dataCheck");
                    for (int t = 0; t < policy_product_option.length(); t++) {
                        HashMap<String, String> map1 = new HashMap<>();
                        map1.put("policy_id", policy_product_option.getJSONObject(t).getString("discount_bonus_policy_id"));//group_wise_discount_bonus_policy_id
                        map1.put("option_id", policy_product_option.getJSONObject(t).getString("id"));
                        map1.put("main_min_qty", policy_product_option.getJSONObject(t).getString("min_qty")); // Added 12 January 2021
                        map1.put("min_qty", policy_product_option.getJSONObject(t).getString("min_qty_sale_unit")); // Added 20 Jan 2022
                        map1.put("bonus_product_id", policy_product_option.getJSONObject(t).getString("bonus_product_id"));
                        map1.put("bonus_qty", policy_product_option.getJSONObject(t).getString("bonus_qty"));
                        map1.put("discount_type", policy_product_option.getJSONObject(t).getString("disccount_type"));
                        map1.put("discount_amount", policy_product_option.getJSONObject(t).getString("discount_amount"));
                        map1.put("policy_type", policy_product_option.getJSONObject(t).getString("policy_type"));
                        map1.put("unit_id", policy_product_option.getJSONObject(t).has("measurement_unit_id") ? policy_product_option.getJSONObject(t).getString("measurement_unit_id") : "null");
                        map1.put("min_memo_val", policy_product_option.getJSONObject(t).getString("min_memo_value")); //23 April2021 Added
                        map1.put("formula", policy_product_option.getJSONObject(t).getString("bonus_formula_text_with_product_id"));//23 April2021 Added
                        map1.put("formula_text", policy_product_option.getJSONObject(t).getString("bonus_formula_text"));//23 Jan 2021 Added
                        map1.put("qty_value_flag", policy_product_option.getJSONObject(t).getString("qty_value_flag"));//23 Jan 2021 Added
                        map1.put("deduct_from_value", policy_product_option.getJSONObject(t).getString("deduct_from_value"));//23 Jan 2021 Added
                        map1.put("min_value", policy_product_option.getJSONObject(t).getString("min_value"));//23 Jan 2021 Added

                        InsertTable(map1, "policy_product_Option");


                        policy_option_price_slap = policy_product_option.getJSONObject(t).
                                getJSONArray("DiscountBonusPolicyOptionPriceSlab");
                        policy_bonus_product = policy_product_option.getJSONObject(t).
                                getJSONArray("DiscountBonusPolicyOptionBonusProduct");
                        policy_option_exclusion = policy_product_option.getJSONObject(t).
                                getJSONArray("DiscountBonusPolicyOptionExclusionProduct");
                        policy_option_inclusion = policy_product_option.getJSONObject(t).
                                getJSONArray("DiscountBonusPolicyOptionInclusionProduct");

                        JSONArray policy_deafult_product = policy_product_option.getJSONObject(t).
                                getJSONArray("DiscountBonusPolicyDefaultBonusProductSelection");

                        datacheck = new HashMap<String, String>();
                        datacheck.put("table_name","policy_option_price_slab");
                        datacheck.put("item_count",policy_option_price_slap.length()+"");
                        InserOrupdate(datacheck,"dataCheck");
                        for (int b = 0; b < policy_option_price_slap.length(); b++) {
                            HashMap<String, String> map2 = new HashMap<>();
                            map2.put("policy_product_option_id", policy_option_price_slap.getJSONObject(b).getString("discount_bonus_policy_option_id"));// group_wise_discount_bonus_policy_option_id
                            map2.put("option_product_id", policy_option_price_slap.getJSONObject(b).getString("discount_product_id"));
                            map2.put("slab_id", policy_option_price_slap.getJSONObject(b).getString("sr_slab_id")); //slab_id
                            InsertTable(map2, "policy_option_price_slab");

                        }
                        datacheck = new HashMap<String, String>();
                        datacheck.put("table_name","policy_default_product");
                        datacheck.put("item_count",policy_deafult_product.length()+"");
                        InserOrupdate(datacheck,"dataCheck");
                         for (int b = 0; b < policy_deafult_product.length(); b++) {
                            HashMap<String, String> map2 = new HashMap<>();
                            map2.put("policy_id", policy_deafult_product.getJSONObject(b).getString("discount_bonus_policy_id"));// group_wise_discount_bonus_policy_option_id
                            map2.put("discount_bonus_policy_option_id", policy_deafult_product.getJSONObject(b).getString("discount_bonus_policy_option_id"));
                            map2.put("product_id", policy_deafult_product.getJSONObject(b).getString("product_id")); //slab_id
                            InsertTable(map2, "policy_default_product");

                        }

                        datacheck = new HashMap<String, String>();
                        datacheck.put("table_name","policy_bonus_product");
                        datacheck.put("item_count",policy_bonus_product.length()+"");
                        InserOrupdate(datacheck,"dataCheck");

                        for (int tt = 0; tt < policy_bonus_product.length(); tt++) {
                            HashMap<String, String> map2 = new HashMap<>();
                            try {
                                map2.put("policy_id", policy_bonus_product.getJSONObject(tt).getString("discount_bonus_policy_id"));  //group_wise_discount_bonus_policy_id
                                map2.put("option_id", policy_bonus_product.getJSONObject(tt).getString("discount_bonus_policy_option_id"));//group_wise_discount_bonus_policy_option_id
                                map2.put("bonus_product_id", policy_bonus_product.getJSONObject(tt).getString("bonus_product_id"));
                                //Loge("bonus_product_id898", policy_bonus_product.getJSONObject(tt).getString("bonus_product_id"));
                                map2.put("bonus_qty", policy_bonus_product.getJSONObject(tt).getString("bonus_qty"));
                                map2.put("relation", policy_bonus_product.getJSONObject(tt).getString("relation"));
                                map2.put("unit_id", policy_bonus_product.getJSONObject(tt).has("measurement_unit_id") ? policy_bonus_product.getJSONObject(tt).getString("measurement_unit_id") : "null");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            InsertTable(map2, "policy_bonus_product");

                        }
                        datacheck = new HashMap<String, String>();
                        datacheck.put("table_name","DiscountBonusPolicyOptionExclusionProduct");
                        datacheck.put("item_count",policy_option_exclusion.length()+"");
                        InserOrupdate(datacheck,"dataCheck");
                        //  added to 23 April 2021
                        for (int ex = 0; ex < policy_option_exclusion.length(); ex++) {
                            HashMap<String, String> map2 = new HashMap<>();
                            map2.put("discount_bonus_policy_id", policy_option_exclusion.getJSONObject(ex).getString("discount_bonus_policy_id"));
                            map2.put("discount_bonus_policy_option_id", policy_option_exclusion.getJSONObject(ex).getString("discount_bonus_policy_option_id"));
                            map2.put("product_id", policy_option_exclusion.getJSONObject(ex).getString("product_id"));
                            map2.put("min_qty", policy_option_exclusion.getJSONObject(ex).getString("min_qty"));
                            InsertTable(map2, "DiscountBonusPolicyOptionExclusionProduct");
                        }
                        datacheck = new HashMap<String, String>();
                        datacheck.put("table_name","DiscountBonusPolicyOptionInclusionProduct");
                        datacheck.put("item_count",policy_option_inclusion.length()+"");
                        InserOrupdate(datacheck,"dataCheck");
                        //  added to 23 April 2021
                        for (int in = 0; in < policy_option_inclusion.length(); in++) {
                            HashMap<String, String> map2 = new HashMap<>();
                            map2.put("discount_bonus_policy_id", policy_option_inclusion.getJSONObject(in).getString("discount_bonus_policy_id"));
                            map2.put("discount_bonus_policy_option_id", policy_option_inclusion.getJSONObject(in).getString("discount_bonus_policy_option_id"));
                            map2.put("product_id", policy_option_inclusion.getJSONObject(in).getString("product_id"));
                            map2.put("min_qty", policy_option_inclusion.getJSONObject(in).getString("min_qty"));
                            InsertTable(map2, "DiscountBonusPolicyOptionInclusionProduct");
                        }

                    }

                }

            } else {
                //Loge("ServiceHandler", "Couldn't get any data from the url");
                progressDialog.dismiss();
            }
            progressDialog.dismiss();
            dbListener.OnLocalDBdataRetrive("dbPolicy");
        } catch (Exception e) {
            //Loge("ServiceHandler", e.getMessage());
            /*Toast.makeText(getApplicationContext(), Constants.SERVER_MESSAGE, 1000).show();*/
        }
    }

    private void InserOrupdate(HashMap<String, String> map, String dataCheck) {
        try {
            //Loge("InserOrupdate: ", "InserOrupdate: "+ map.get("table_name"));
            open();
            Cursor c = sqLiteDatabase.rawQuery("Select * from "+dataCheck+" where table_name= '"+map.get("table_name")+"'",null);
            if (c!=null&&c.getCount()>0){
                excQuery("update dataCheck set item_count=item_count+"+map.get("item_count")+" where table_name='"+map.get("table_name")+"'");
            }else {
                InsertTable(map,dataCheck);
            }
            c.close();
            close();
        }catch (Exception e){
            //Loge("exception", "InserOrupdate: "+e.getMessage());
        }



    }

    private void saveImage(String urls, String product_name) throws IOException {

        if (urls.equalsIgnoreCase("http://202.126.123.157/smc_test/app/webroot/img/product_img/")||urls.equalsIgnoreCase("http://182.160.103.236:8079/app/webroot/img/product_img/")||urls.equalsIgnoreCase("http://182.160.103.234:8079/app/webroot/img/product_img/")) {
            return;
        }
        java.net.URL url = new java.net.URL(urls);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = in.read(buf);
        while (n != -1) {
            out.write(buf, 0, n);
            n = in.read(buf);
        }

        out.close();
        in.close();
        byte[] response = out.toByteArray();

        File filepath = context.getFilesDir();
        File dir = new File(filepath.getAbsolutePath()
                + "/SMC_image/");

        if (!dir.exists()) {
            dir.mkdir();
        }

        Log.e("FilePath",dir.getPath());
        File file = new File(dir, product_name + ".png");
        //Loge("saveimagepath", file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(response);
        fos.flush();
        fos.close();




        /*String filePath=getFilesDir().getPath() + File.separator + "SMC_image" +File.separator+ product_name;


        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(response);
        fos.close();*/


    }

    private void insertbonusUnitDetails(JSONObject jsonObj) throws JSONException {

        JSONArray MeasurementUnitList = jsonObj.getJSONArray("unit_info");
        HashMap<String,String> datacheck = new HashMap<String, String>();
        datacheck.put("table_name","unit_details");
        datacheck.put("item_count",MeasurementUnitList.length()+"");
        InsertTable(datacheck,"dataCheck");

        for (int i = 0; i < MeasurementUnitList.length(); i++) {
            JSONObject c = MeasurementUnitList.getJSONObject(i);
            JSONObject obj = c.getJSONObject("ProductMeasurement");
            String name = c.getJSONObject("MeasurementUnit").getString("name").replaceAll("and", "&");
            String updated_at = obj.getString("updated_at");
            String measurement_unit_id = obj.getString("measurement_unit_id");
            String product_id = obj.getString("product_id");
            String qty_in_base = obj.getString("qty_in_base");

            String action = obj.getString("action");


            HashMap<String, String> map = new HashMap<String, String>();
            map.put("measurement_unit_id", measurement_unit_id);
            map.put("product_id", product_id);
            map.put("unit_name", name);
            map.put("qty_in_base", qty_in_base);
            map.put("updated_at", updated_at);


            InsertTable(map, "unit_details");


        }



            ////Loge("test", checkData()+"" );

        if (checkData()){
            progressDialog.dismiss();
            dbListener.OnLocalDBdataRetrive("unit_details");
        }else {
            progressDialog.dismiss();
            dbListener.OnLocalDBdataRetrive("false");
        }



    }

    private boolean checkData() {
        open();
        Boolean can_login = true;
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from dataCheck",null);
        cursor.moveToFirst();
        Log.e("checkData","Select * from dataCheck "+cursor.getCount());
        if (cursor!=null && cursor.getCount()>0){
            do {
               String table_name = cursor.getString(1);

              int items = getitemcount(table_name);
                Log.e("checks", "table_name="+table_name+" total_data_in_main_table="+items+" data_in_table="+cursor.getInt(2) );
              if (items<cursor.getInt(2)){

                  return false;
              }

            }while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return can_login;
    }

    private int getitemcount(String table_name) {
        open();

        Cursor c = sqLiteDatabase.rawQuery("select * from "+table_name,null);
        c.moveToFirst();
        int size = c.getCount();
        c.close();
        return size;
    }

    public long InsertTablemap(HashMap<String, String> data, String TableName) {


        ContentValues values = new ContentValues();

        Iterator it = data.entrySet().iterator();
        long insertedID = 0;

        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry) it.next();
            values.put(pair.getKey(), pair.getValue()); // Contact Name

            it.remove();
        }
        if (TableName.equalsIgnoreCase("product_history")) {
            //Loge("Content producthistory:", values.toString());
        }
        //Loge(" Content values  :", values.toString());
        //Loge(TableName, "   :" + "INSERTED");
        this.open();
        insertedID = sqLiteDatabase.insert(TableName, null, values);

        this.close();
        close();
        return insertedID;

    }



    private void insertbonusPolicyOutletData(JSONObject jsonObject) {
        JSONArray Bonus_policy_outlet;


        try {

            if (jsonObject != null) {

                //Loge("response_Bonus_Policy ", jsonObject.toString());
                JSONObject jsonObj = new JSONObject(jsonObject.toString());

                HashMap<String,String> datacheck = new HashMap<String, String>();
                datacheck.put("table_name","Bonus_Eligible_Outlets");
                datacheck.put("item_count",jsonObj.getJSONArray("outlet_group_list").length()+"");
                InsertTable(datacheck,"dataCheck");

                // Getting JSON Array node
                Bonus_policy_outlet = jsonObj.getJSONArray("outlet_group_list");
                for (int i = 0; i < jsonObj.getJSONArray("outlet_group_list").length(); i++) {

                    JSONObject object = Bonus_policy_outlet.getJSONObject(i).getJSONObject("OutletGroupToOutlet");

                    HashMap<String, String> map = new HashMap<>();
                    map.put("outlet_id", object.getString("outlet_id"));
                    map.put("Group_id", object.getString("outlet_group_id"));
                    InsertTable(map, "Bonus_Eligible_Outlets");

                }

            } else {
                //Loge("ServiceHandler", "Couldn't get any data from the url");
            }
            progressDialog.dismiss();
            dbListener.OnLocalDBdataRetrive("done");
        } catch (Exception e) {/*Toast.makeText(getApplicationContext(), Constants.SERVER_MESSAGE, 1000).show();*/}


    }

    @Override
    public String getCurrentDateTime24() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//Here you say to java the initial timezone. This is the secret
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//Will print in UTC
        System.out.println(sdf.format(calendar.getTime()));

//Here you set to your timezone
        sdf.setTimeZone(TimeZone.getDefault());
//Will print on your default Timezone
        //Loge("time", sdf.format(calendar.getTime()));
        return sdf.format(calendar.getTime());
    }

    public void savePreference(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key) {
        String value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        value = prefs.getString(key, "0");

        return value;

    }

    public String getRouteName(String routeId) {
        this.open();
        String routeName = null;
        String query = "SELECT " + Tables.ROUTE_NAME + " FROM " + Tables.TABLE_NAME_ROOT + " WHERE " + Tables.ROUTE_ID + " = " + routeId + " ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            routeName = cursor.getString(cursor.getColumnIndex(Tables.ROUTE_NAME));
        }
        return routeName;
    }

    public ArrayList<String> SpecialGroup_ID_List(String memodate, String outlet_ID, String outletCategoryId) {
        open();
        ArrayList<String> sgIDArrayList = new ArrayList<String>();

        String Special_G_ID = "";
        String query2 = "select sg.pid from special_group sg\n" +
                "left join special_group_details sgdo on sg.pid=sgdo.special_group_id and sgdo.create_for=4\n" +
                "left join Bonus_Eligible_Outlets eo on eo.Group_id=sgdo.reffrence_id and eo.outlet_id= '" + outlet_ID + "' \n" + "\n" +
                "left join special_group_details sgdc on sg.pid=sgdc.special_group_id and sgdc.create_for=3 and" +
                " sgdc.reffrence_id= '" + outletCategoryId + "' \n" + "\n" +
                "where '" + memodate + "' between sg.start_date and sg.end_date\n" +
                "and (eo._id is not null or sgdc._id is not null)\n" +
                "group by sg.pid";

        //Loge("SG_ID_QUERY__:", query2);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Special_G_ID = cursor.getString(0);
                //Loge("SG_ID", cursor.getString(0));
                // Adding SG_ID to list
                sgIDArrayList.add(Special_G_ID);
            } while (cursor.moveToNext());
        }
        // return contact list
        return sgIDArrayList;
    }

    public ArrayList<GSOPrice> GSOPriceList(String product_id, String minQty, String str, String memodate, String outletCategoryId) {
        ArrayList<GSOPrice> pricessArrayList = new ArrayList<GSOPrice>();
//        pc.effective_date <='" + memodate + "' and
        String query2 = "select \n" +
                "pc.slab_id,\n" +
                "pc.effective_date,\n" +
                "pc.min_quantity,\n" +
                "pc.price,\n" +
                "pc_for_special.reference_id as special_group_id,\n" +
                "pc_for_special.price as special_price,\n" +
                "pc_for_outlet_category.reference_id as outlet_category_id,\n" +
                "pc_for_outlet_category.price as outlet_category_price\n" +
                "from product_combinations pc \n" +
                "inner join (select effective_date from product_price where product_id=" + product_id + " and effective_date <='" + memodate + "' order by effective_date desc,_id desc limit 1) pp on pp.effective_date=pc.effective_date\n" +
                "left join product_price_other_for_slabs_v2  pc_for_special on pc_for_special.product_combination_id=pc.slab_id and pc_for_special.type=1 and pc_for_special.reference_id in (" + str + ")\n" +
                "left join product_price_other_for_slabs_v2 pc_for_outlet_category on pc_for_outlet_category.product_combination_id=pc.slab_id and pc_for_outlet_category.type=2 and pc_for_outlet_category.reference_id= '" + outletCategoryId + "' \n" + "\n" +
                "where pc.min_quantity <=" + minQty + " and pc.product_id=" + product_id + "\n" + "\n" +
                "order by pc.effective_date desc,pc.min_quantity desc,pc_for_special.reference_id desc,pc.product_price_id DESC,pc_for_outlet_category.reference_id desc\n" +
                "limit 1";

        //Loge("SG_ID_QUERY:", query2);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    GSOPrice gsoPrice = new GSOPrice();
                    gsoPrice.setSlab_id(cursor.getString(0));

                    gsoPrice.setPriceSlabEffectiveDate(cursor.getString(1));
                    gsoPrice.setMin_quantity(cursor.getString(2));
                    gsoPrice.setPrice(cursor.getString(3));
                    if (cursor.getString(4) != null) {
                        gsoPrice.setSpecial_group_id(cursor.getString(4));
                    } else {
                        gsoPrice.setSpecial_group_id("0");
                    }
                    gsoPrice.setSpecial_price(cursor.getString(5));
                    if (cursor.getString(6) != null) {
                        gsoPrice.setOutlet_category_id(cursor.getString(6));
                    } else {
                        gsoPrice.setOutlet_category_id("0");
                    }
                    gsoPrice.setOutlet_category_price(cursor.getString(7));

                    // Adding price to list
                    pricessArrayList.add(gsoPrice);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return contact list
       // //Loge("arraylist", pricessArrayList.get(0).getPrice());
        return pricessArrayList;
    }

    public ArrayList<CombinationPrices> CombinationPriceList(String product_id, String createFor, String ReferenceId, String memDate, String priceSlabEffectiveDate) {

        ArrayList<CombinationPrices> combinePricesArrayList = new ArrayList<CombinationPrices>();
        String query2 = "select \n" +
                "\tpcl._id,\n" +
                "\tpcl.effective_date,\n" +
                "\tpcl.combined_qty\n" +
                "from \n" +
                "product_combination_list pcl\n" +
                "inner join Product_combination_list_details_v2 pcld on pcl._id=pcld.combination_id\n" +
                "inner join product_combinations pc on pc.min_quantity=pcl.combined_qty and pc.effective_date='" + priceSlabEffectiveDate + "'\n" +
                "inner join (\n" +
                "\t\tselect\n" +
                "\t\t\tpcl.combined_qty,\n" +
                "\t\t\tmax(pcl.effective_date) as effective_date\n" +
                "\t\tfrom \n" +
                "\t\tproduct_combination_list pcl\n" +
                "\t\tinner join Product_combination_list_details_v2 pcld on pcl._id=pcld.combination_id\n" +
                "\t\tinner join product_combinations pc on pc.min_quantity=pcl.combined_qty and pc.effective_date='" + priceSlabEffectiveDate + "'\n" +
                "\t\twhere\n" +
                "\t\t\tpcl.effective_date <='" + memDate + "'\n" +
                "\t\t\tand pcld.product_id=" + product_id + "\n" +
                "\t\tgroup by\n" +
                "\t\t\tpcl.combined_qty\n" +
                "\t) mes on mes.combined_qty=pcl.combined_qty and mes.effective_date=pcl.effective_date\n" +
                "where\n" +
                "\tpcl.effective_date <='" + memDate + "'\n" +
                "\tand pcld.product_id=" + product_id + "\n" +
                "group by \n" +
                "\tpcl._id,\n" +
                "\tpcl.effective_date,\n" +
                "\tpcl.combined_qty\n" +
                "order by\n" +
                "\tpcl.combined_qty desc";

        //Loge("SG_ID_com_QUERY:", query2, null);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CombinationPrices comPrice = new CombinationPrices();
                comPrice.set_id(cursor.getString(0));
                comPrice.setEffective_date(cursor.getString(1));
                comPrice.setCombinedQty(cursor.getString(2));

                //Loge("min_qty", comPrice.getCombinedQty());

                // Adding price to list
                combinePricesArrayList.add(comPrice);
            } while (cursor.moveToNext());
        }
        // return contact list
        return combinePricesArrayList;
    }

    public ArrayList<CombinationProductDetails> CombinationProductList(String combination_id, Double combined_qty, String createFor, String reference_id, String memodate) {

        String extra_field = "";
        String join = "";

        if (createFor.equals("4")) {
            extra_field += ", ppos.price as category_price";
            join += "inner join product_price_other_for_slabs_v2 ppos on ppos.product_combination_id=pc.slab_id and ppos.type=2 and ppos.reference_id=" + reference_id;
        } else if (createFor.equals("6")) {
            extra_field += ", ppos.price as special_price";
            join += "inner join product_price_other_for_slabs_v2 ppos on ppos.product_combination_id=pc.slab_id and ppos.type=1 and ppos.reference_id=" + reference_id;
        }

        ArrayList<CombinationProductDetails> combineDetailsArrayList = new ArrayList<CombinationProductDetails>();
        String query2 = "select \n" +
                "\tpcl.product_id,\n" +
                "\t pc.slab_id as product_combination_id,\n" +
                "\tpc.price\n" +
                extra_field +
                "\nfrom \n" +
                "Product_combination_list_details_v2 pcl\n" +
                "inner join product_combinations pc on pc.product_id=pcl.product_id and pc.min_quantity=" + combined_qty + "\n" +
                "inner join (select product_id,max(effective_date) as effective_date from product_price where effective_date <='" + memodate + "'  group by product_id) pp on pp.product_id=pc.product_id and pp.effective_date=pc.effective_date \n" +
                join +
                "\nwhere \n" +
                "\tpcl.combination_id=" + combination_id + "\n" +
                "\t\n" +
                "group by\n" +
                "\tpcl.product_id,\n" +
                "\tpcl.product_combination_id,\n" +
                "\tpc.price";

        //Loge("query_com_details:", query2);

        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CombinationProductDetails comPrice = new CombinationProductDetails();
                comPrice.setProducct_id(cursor.getString(0));
                comPrice.setProduct_com_id(cursor.getString(1));
                comPrice.setPrice(cursor.getString(2));
                if (createFor.equals("6")) {
                    comPrice.setSpecial_price(cursor.getString(3));
                } else if (createFor.equals("4")) {
                    comPrice.setCategory_price(cursor.getString(3));
                }
                //Loge("price", comPrice.getPrice());

                // Adding price to list
                combineDetailsArrayList.add(comPrice);
            } while (cursor.moveToNext());
        }
        // return contact list
        return combineDetailsArrayList;
    }

    public ArrayList<PolicyID> getPolicy_ID_List(String specialGroupId, String rootProductId, String memodate, String outlet_ID, String outletCategoryId) {

        ArrayList<String> excludeArrayList = new ArrayList<String>();
        String exclude_policy_ID = "";

        String excluding_outlet_query="select \n" +
                "     pt.policy_id,\n" +
                "     pt.policy_name,\n" +
                "     pt.start_date,\n" +
                "     pt.end_date \n" +
                "    from Policy_Table pt\n" +
                "    inner join policy_root_product prp on pt.policy_id=prp.policy_id\n" +
                "    inner join DiscountBonusPolicyToExcludingOutletGroupSr exclud_ot on exclud_ot.discount_bonus_policy_id=pt.policy_id and exclud_ot.create_for=5\n" +
                "    inner join Bonus_Eligible_Outlets beo on beo.group_id=exclud_ot.reffrence_id and beo.outlet_id=  '" + outlet_ID + "'\n" +
                "    where\n" +
                "'" + memodate + "' between pt.start_date and pt.end_date\n" +
                "     and prp.root_product_id in (" + rootProductId + ")\n" +
                "    group by \n" +
                "    pt.policy_id,\n" +
                "    pt.policy_name,\n" +
                "    pt.start_date,\n" +
                "    pt.end_date";

        //Loge("POLICY_ID_QUERY_1:", excluding_outlet_query);

        Cursor cursorEx = sqLiteDatabase.rawQuery(excluding_outlet_query, null);
        if (cursorEx.moveToFirst()) {
            do {
                exclude_policy_ID = cursorEx.getString(0);
                //Loge("ex_policy_id_cr", exclude_policy_ID);
                // Adding price to list
                excludeArrayList.add(exclude_policy_ID);
            } while (cursorEx.moveToNext());
        }

        //Logi(TAG, "excludeArrayList_: " + new Gson().toJson(excludeArrayList));


        String ExcludepolicyIdsString = TextUtils.join(", ", excludeArrayList);// Convert policy ids arrayList to string
        String excluding_condition="";
        if(ExcludepolicyIdsString.length()>0)
            excluding_condition="and pt.policy_id not in ("+ExcludepolicyIdsString+")";


        //------------------------------------------------------------------------------------------
        ArrayList<PolicyID> policyIDSArrayList = new ArrayList<>();

        String query2 = "select \n" +
                " pt.policy_id,\n" +
                " pt.policy_name,\n" +
                " pt.start_date,\n" +
                " pt.end_date \n" +
                "from Policy_Table pt\n" +
                "inner join policy_root_product prp on pt.policy_id=prp.policy_id\n" +
                "left join Bonus_Eligible_Group beg on beg.policy_id=pt.policy_id\n" +
                "left join Bonus_Eligible_Outlets beo on beo.group_id=beg.outlet_group_id and beo.outlet_id= '" + outlet_ID + "' \n" + "\n" +
                "left join Bonus_Eligible_Outlet_Categories beoc on beoc.policy_id=pt.policy_id and beoc.category_id= '" + outletCategoryId + "' \n" + "\n" +
                "left join DiscountBonusPolicyToSpecialGroupSo spg on spg.discount_bonus_policy_id=pt.policy_id and spg.reffrence_id in (" + specialGroupId + ")\n" +
                "where\n" +
                "'" + memodate + "' between pt.start_date and pt.end_date\n" +
                "and pt.bonus_applicable=1 and prp.root_product_id in (" + rootProductId + ")\n" +
                " and (beo._id is not null or beoc._id is not null or spg.id is not null)\n"+
                excluding_condition+
                "group by \n" +
                "pt.policy_id,\n" +
                "pt.policy_name,\n" +
                "pt.start_date,\n" +
                "pt.end_date";

        //Loge("POLICY_ID_QUERY2:", query2);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PolicyID policyID = new PolicyID();
                policyID.setPolicy_id(cursor.getString(0));
                policyID.setPolicy_name(cursor.getString(1));
                policyID.setStart_date(cursor.getString(2));
                policyID.setEnd_date(cursor.getString(3));

                //Loge("policy_id_from_cursor", policyID.getPolicy_id());

                // Adding price to list
                policyIDSArrayList.add(policyID);
            } while (cursor.moveToNext());
        }
        // return policy Id list
        return policyIDSArrayList;
    }

    public ArrayList<PolicyProduct> getPolicy_Product_List(String policyIds) {

        ArrayList<PolicyProduct> policyProductsArrayList = new ArrayList<PolicyProduct>();

        String query2 = "select\n" +
                "policy_id,\n" +
                "root_product_id\n" +
                "from policy_root_product \n" +
                "where policy_id in (" + policyIds + ")\n" +
                "order by policy_id";

        //Loge("POLICY_PRODUCT_QUERY:", query2);

        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PolicyProduct policyProduct = new PolicyProduct();
                policyProduct.setPolicy_id(cursor.getString(0));
                policyProduct.setRoot_product_id(cursor.getString(1));

                //Loge("root_product_id_cur", policyProduct.getPolicy_id());

                // Adding policy product to list
                policyProductsArrayList.add(policyProduct);

            } while (cursor.moveToNext());
        }
        // return policy product list
        return policyProductsArrayList;
    }

    public ArrayList<PolicyWiseSlab> getPolicy_Wise_Slab_List(String policyId, String minQty,String minvalue) {

        ArrayList<PolicyWiseSlab> policyWiseSlabsArrayList = new ArrayList<PolicyWiseSlab>();
        String query2 = "select \n" +
                "policy_id,\n" +
                "option_id,\n" +
                "min_qty,\n" +
                "policy_type,\n" +
                "discount_amount,\n" +
                "discount_type,\n" +
                "min_memo_val,\n" +
                "formula,\n" +
                "min_value,\n" +
                "deduct_from_value,\n" +
                "qty_value_flag\n" +
                "from policy_product_Option\n" +
                "where \n" +
                "policy_id=" + policyId + "\n" + "\n" +
                "and (min_qty!=0 and min_qty <=" + minQty + "\n" + "\n" +
                " or min_value <=" + minvalue + ")\n" + "\n" +
                "order by min_qty desc,min_value desc";

        //Loge("POLICY_SLAB_QUERY:", query2);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PolicyWiseSlab policyWiseSlab = new PolicyWiseSlab();
                policyWiseSlab.setPolicy_id(cursor.getString(0));
                policyWiseSlab.setOption_id(cursor.getString(1));
                policyWiseSlab.setMin_qty(cursor.getString(2));
                policyWiseSlab.setPolicy_type(cursor.getString(3));
                policyWiseSlab.setDiscount_amt(cursor.getString(4));
                policyWiseSlab.setDiscount_type(cursor.getString(5));
                policyWiseSlab.setMin_memo_val(cursor.getString(6));
                policyWiseSlab.setFormula(cursor.getString(7));
                policyWiseSlab.setMin_value(cursor.getString(8));
                policyWiseSlab.setDeduct_from_value(cursor.getString(9));
                policyWiseSlab.setQty_value_flag(cursor.getString(10));
                policyWiseSlab.setCartValue(minvalue);
                //Loge("root_product_slab_cur", policyWiseSlab.getPolicy_id());

                // Adding policy wise slab to list
                policyWiseSlabsArrayList.add(policyWiseSlab);
            } while (cursor.moveToNext());
        }
        // return policy product list
        return policyWiseSlabsArrayList;
    }

    public ArrayList<PolicyWiseSlab> getPolicy_Wise_Slab_ListForproductWiseMin(String policyId, String minvalue) {

        ArrayList<PolicyWiseSlab> policyWiseSlabsArrayList = new ArrayList<PolicyWiseSlab>();
        String query2 = "select \n" +
                "policy_id,\n" +
                "option_id,\n" +
                "min_qty,\n" +
                "policy_type,\n" +
                "discount_amount,\n" +
                "discount_type,\n" +
                "min_memo_val,\n" +
                "formula,\n" +
                "min_value,\n" +
                "deduct_from_value,\n" +
                "qty_value_flag\n" +
                "from policy_product_Option\n" +
                "where \n" +
                "policy_id=" + policyId + "\n" + "\n" +
                "and " +
                " min_value <=" + minvalue + "\n" + "\n" +
                "order by min_value desc";

        Log.e("getPolicy_Wise_Slab_ListForproductWiseMin:", query2);
        Cursor cursor =  sqLiteDatabase.rawQuery(query2,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PolicyWiseSlab policyWiseSlab = new PolicyWiseSlab();
                policyWiseSlab.setPolicy_id(cursor.getString(0));
                policyWiseSlab.setOption_id(cursor.getString(1));
                policyWiseSlab.setMin_qty(cursor.getString(2));
                policyWiseSlab.setPolicy_type(cursor.getString(3));
                policyWiseSlab.setDiscount_amt(cursor.getString(4));
                policyWiseSlab.setDiscount_type(cursor.getString(5));
                policyWiseSlab.setMin_memo_val(cursor.getString(6));
                policyWiseSlab.setFormula(cursor.getString(7));
                policyWiseSlab.setMin_value(cursor.getString(8));
                policyWiseSlab.setDeduct_from_value(cursor.getString(9));
                policyWiseSlab.setQty_value_flag(cursor.getString(10));
                policyWiseSlab.setCartValue(minvalue);

                Log.e("root_product_slab_cur", policyWiseSlab.getPolicy_id());

                // Adding policy wise slab to list
                policyWiseSlabsArrayList.add(policyWiseSlab);
            } while (cursor.moveToNext());
        }
        // return policy product list
        return policyWiseSlabsArrayList;
    }

    public ArrayList<ExclusionProduct> getExclusion_Product_List(String policyId) {

        ArrayList<ExclusionProduct> exclusionProductsArrayList = new ArrayList<ExclusionProduct>();
        String query2 = "select * from DiscountBonusPolicyOptionExclusionProduct \n" +
                "where \n" +
                "discount_bonus_policy_option_id=" + policyId;

        //Loge("Exclusion_QUERY:", query2);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExclusionProduct exclusionProduct = new ExclusionProduct();
                exclusionProduct.setId(cursor.getString(0));
                exclusionProduct.setDis_bonus_policy_id(cursor.getString(1));
                exclusionProduct.setDis_bonus_policy_option_id(cursor.getString(2));
                exclusionProduct.setProduct_id(cursor.getString(3));
                exclusionProduct.setMin_qty(cursor.getString(4));
                exclusionProduct.setUpdated_at(cursor.getString(5));

                //Loge("ex_bonus_id_cur", exclusionProduct.getDis_bonus_policy_id());

                // Adding exclusion product to list
                exclusionProductsArrayList.add(exclusionProduct);
            } while (cursor.moveToNext());
        }
        // return exclusion product list
        return exclusionProductsArrayList;
    }

    public ArrayList<ExclusionProduct> getInclusion_Product_List(String policyId) {

        ArrayList<ExclusionProduct> inclusionProductsArrayList = new ArrayList<ExclusionProduct>();
        String query2 = "select * from DiscountBonusPolicyOptioninclusionProduct \n" +
                "where \n" +
                "discount_bonus_policy_option_id=" + policyId;

        //Loge("Inclusion_QUERY:", query2);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExclusionProduct inclusionProduct = new ExclusionProduct();
                inclusionProduct.setId(cursor.getString(0));
                inclusionProduct.setDis_bonus_policy_id(cursor.getString(1));
                inclusionProduct.setDis_bonus_policy_option_id(cursor.getString(2));
                inclusionProduct.setProduct_id(cursor.getString(3));
                inclusionProduct.setMin_qty(cursor.getString(4));
                inclusionProduct.setUpdated_at(cursor.getString(5));

                //Loge("in_bonus_product_id_cur", inclusionProduct.getProduct_id());

                // Adding exclusion product to list
                inclusionProductsArrayList.add(inclusionProduct);
            } while (cursor.moveToNext());
        }
        // return exclusion product list
        return inclusionProductsArrayList;
    }

    /* Bondhon Code Begaing*/

    public ArrayList<PolicyBonusProduct> getBonusProductList(String optionId, String product_id) {
        String ohter_con = "";
        if (!product_id.equals("")) {
            ohter_con += " AND bonus_product_id =" + product_id;
        }

        ArrayList<PolicyBonusProduct> policyBonusProductsArrayList = new ArrayList<PolicyBonusProduct>();
        String query2 = "select \n" +
                "\tpbp.policy_id,\n" +
                "\tpbp.option_id,\n" +
                "\tpbp.bonus_product_id,\n" +
                "\tpbp.bonus_qty,\n" +
                "\tpbp.unit_id,\n" +
                "\tp.product_name,\n" +
                "\tu.unit_name\n" +
                "from  policy_bonus_product  pbp\n" +
                "left join product p on p.product_id=pbp.bonus_product_id\n" +
                "left join unit u on u.unit_id=pbp.unit_id\n" +
                "where option_id=" + optionId + ohter_con;

        Log.e("policy_Bonus_Query:", query2);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PolicyBonusProduct policyBonusProduct = new PolicyBonusProduct();
                policyBonusProduct.setPolicy_id(cursor.getString(0));
                policyBonusProduct.setOption_id(cursor.getString(1));
                policyBonusProduct.setBonus_product_id(cursor.getString(2));
                policyBonusProduct.setBonus_qty(cursor.getString(3));
                policyBonusProduct.setUnit_id(cursor.getString(4));
                policyBonusProduct.setProduct_name(cursor.getString(5));
                policyBonusProduct.setUnit_name(cursor.getString(6));

                //Loge("policyBonusProduct", policyBonusProduct.getBonus_product_id());

                // Adding bonus product to list
                policyBonusProductsArrayList.add(policyBonusProduct);
            } while (cursor.moveToNext());
        }
        // return bonus product list
        return policyBonusProductsArrayList;
    }

    //------------------------Price-----------------------------------------------------------------------------------------------------

    public ArrayList<HashMap<String, String>> getOption_List(String option_id) {

        ArrayList<HashMap<String, String>> policyWiseSlabsArrayList = new ArrayList<>();

        String query2 = "select \n" +
                "policy_id,\n" +
                "option_id,\n" +
                "min_qty,\n" +
                "policy_type,\n" +
                "discount_amount,\n" +
                "discount_type,\n" +
                "min_memo_val,\n" +
                "formula\n" +
                "from policy_product_Option\n" +
                "where \n" +
                "option_id=" + option_id + "\n" + "\n" +
                "order by min_qty desc";

        //Loge("POLICY_SLAB_QUERY:", query2);
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> policyWiseSlab = new HashMap<>();
                policyWiseSlab.put("Policy_id", cursor.getString(0));
                policyWiseSlab.put("Option_id", cursor.getString(1));
                policyWiseSlab.put("Min_qty", cursor.getString(2));
                policyWiseSlab.put("Policy_type", cursor.getString(3));
                policyWiseSlab.put("Discount_amt", cursor.getString(4));
                policyWiseSlab.put("Discount_type", cursor.getString(5));
                policyWiseSlab.put("Min_memo_val", cursor.getString(6));
                policyWiseSlab.put("Formula", cursor.getString(7));
                // Adding policy wise slab to list
                policyWiseSlabsArrayList.add(policyWiseSlab);
            } while (cursor.moveToNext());
        }
        // return policy product list
        return policyWiseSlabsArrayList;
    }

    public int getOrderPushStatus(String order_number) {

        String query2 = "select is_pushed from order_table where order_number='"+order_number+"'";
        open();
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
        cursor.moveToFirst();
        int status = 1;
        if (cursor!=null && cursor.getCount()>0){

            status= cursor.getInt(0);
        }
        cursor.close();
        close();

        return status;
    }

    public HashMap<String, String> getEcandOrderAmount(String start_date, String end_date, String outlate_id, String outlate_category_id) {

        HashMap<String, String> map = new HashMap<>();

        //Loge("Item", outlate_id + "  " + outlate_category_id);
        open();
        String Query = "";

        if (outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT Sum(gross_value) FROM " + Tables.TABLE_NAME_ORDER + " where " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "'  ORDER BY order_number ASC";
        else if (!outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT Sum(gross_value) FROM " + Tables.TABLE_NAME_ORDER + " where " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "' AND " + ORDER_outlet_id + "='" + outlate_id + "' ORDER BY order_number ASC";
        else if (!outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT Sum(gross_value) FROM " + Tables.TABLE_NAME_ORDER + " as ot INNER join outlets as o on ot.outlet_id=o.outlet_id  where o.outlet_category_id='" + outlate_category_id + "' and  " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "' AND ot." + ORDER_outlet_id + "='" + outlate_id + "' ORDER BY order_number ASC";
        else if (outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query = "SELECT Sum(gross_value) FROM " + Tables.TABLE_NAME_ORDER + " as ot INNER join outlets as o on ot.outlet_id=o.outlet_id  where o.outlet_category_id='" + outlate_category_id + "' and  " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "'  ORDER BY order_number ASC";
        //Loge("DataView2", Query);
        ///Query+= " LIMIT "+ (offset*25)+" , 25";
        Cursor c = sqLiteDatabase.rawQuery(Query, null);
        Log.e("getEcandMemoAmount", "getNotPushedOrder: "+Query);
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {

            map.put("memo_value",c.getString(0));
        }else {
            map.put("memo_value","0.00");
        }




        if (outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query= Query.replace("Sum(gross_value)","count(_id)");
        else if (!outlate_id.equals("0") && outlate_category_id.equalsIgnoreCase("0"))
            Query= Query.replace("Sum(gross_value)","count(_id)");
        else if (!outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query= Query.replace("Sum(gross_value)","count(ot._id)");
        else if (outlate_id.equals("0") && !outlate_category_id.equalsIgnoreCase("0"))
            Query= Query.replace("Sum(gross_value)","count(ot._id)");
        Cursor c1 = sqLiteDatabase.rawQuery(Query, null);
        Log.e("getNotPushedOrder", "getNotPushedOrder: "+Query);
        c1.moveToFirst();
        if (c1 != null && c1.getCount() > 0) {

            map.put("EC",c1.getString(0));
        }else {
            map.put("EC","0");
        }

        Log.e("getEcandMemoAmount", map.toString() +"  "+Query);
        return map ;
    }

    public ArrayList<HashMap<String,String>> getproductListWithbatch(ArrayList<String> products) {

        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        for (int i = 0 ; i < products.size() ; i++) {
            HashMap<String,String> map = new HashMap<>();

            String product_name = getProductName(products.get(i),getProductName(products.get(i)));
            Log.e("name",product_name);
            map.put("product_name",product_name);
            map.put("product_id",products.get(i));
            map.put("qty","");
            map.put("batch","");
            map.put("exp","");

            list.add(map);
        }
        return list;


    }

    public void updateOutletCount(String currentDateTime) {

            excQuery( "update outlet_create_permission_list set no_of_outlet=(no_of_outlet-1) where '"+currentDateTime+"' between start_time and end_time and no_of_outlet>0");


    }

    public void deleteAllRow(String table_name) {
        excQuery("Delete * from "+table_name);
    }


    private class updateorderWithServer extends AsyncTask<String, String, String> {
        JSONObject jsonObject = new JSONObject();
        int i;
        String Order_NUMBER;
        ProgressDialog progressDialog;

        public updateorderWithServer(JSONObject jsonObject, int i) {
            this.jsonObject = jsonObject;
            this.i = i;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Update DB With Server...");
            progressDialog.show();


        }

        @Override
        protected String doInBackground(String... strings) {
            open();


            try {
                //Loge("updateWithServerProcessed", jsonObject + "");
                JSONArray jsonArray = jsonObject.getJSONArray("orders");

                for (int j = 0; j < jsonArray.length(); j++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                    //Loge("jsonobj", jsonObject1.getString(ORDER[2]));


                    ContentValues contentValues = new ContentValues();
                    for (int i = 2; i < ORDER.length; i++) {

                        if (jsonObject1.has(ORDER[i])) {
                            try {
                                //Loge(ORDER[i], jsonObject1.getString(ORDER[i]));
                                contentValues.put(ORDER[i], jsonObject1.getString(ORDER[i]));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                //Loge("updateOrderWithServer1", ex.getMessage());
                            }
                        }


                    }

                    String[] ar = {jsonObject1.getString(ORDER[2])};
                    open();
                    //Loge("cv", contentValues.toString());
                    if (updateOrInsert(ORDER[0], ORDER[2], jsonObject1.getString(ORDER[2])))
                        sqLiteDatabase.update(ORDER[0], contentValues, ORDER_order_number + " =? ", ar);
                    else {
                        contentValues.put(ORDER_is_pushed, 1);
                        contentValues.put(ORDER_STATUS, jsonObject1.getString("status"));
                        sqLiteDatabase.insert(ORDER[0], null, contentValues);
                    }

                    close();


                    excQuery("DELETE from ORDER_DETAILS where order_number ='" + jsonObject1.getString(ORDER[2]) + "'");
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("order_details");

                    for (int k = 0; k < jsonArray1.length(); k++) {
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                        ContentValues contentValues2 = new ContentValues();
                        for (int i = 2; i < ORDER_DETAILS.length; i++) {

                            if (jsonObject2.has(ORDER_DETAILS[i]))
                                contentValues2.put(ORDER_DETAILS[i], jsonObject2.getString(ORDER_DETAILS[i]));


                        }

                        open();
                        //Loge("cv2", contentValues2.toString());

                        sqLiteDatabase.insert(ORDER_DETAILS[0], null, contentValues2);

                        close();
                    }
                }


                progressDialog.dismiss();
                dbListener.OnLocalDBdataRetrive(i + "");

            } catch (JSONException e) {
                e.printStackTrace();
                //Loge("log", e.getMessage());
            }

            return null;
        }
    }

    private class getProcessedOrders extends AsyncTask<String, String, String> {
        String start_date;
        String end_date;
        String outlate_id;
        ProgressDialog progressDialog;

        public getProcessedOrders(String start_date, String end_date, String outlate_id) {
            this.start_date = start_date;
            this.end_date = end_date;
            this.outlate_id = outlate_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Activity activity = (Activity)context;
            if (activity.isFinishing()) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Getting Ready Invoices ...");
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            open();

            ArrayList<HashMap<String, String>> list = new ArrayList<>();
            open();
            String Query = "";

            if (outlate_id.equals("0") && !start_date.equalsIgnoreCase("0"))
                Query = "SELECT * FROM " + Tables.TABLE_NAME_ORDER + " where " + Tables.ORDER_order_date + ">=" + "'" + start_date + "' and " + ORDER_STATUS + " IN (" + PROCESSING_COMPELETE + ", " + ORDER_BOUNCE + ")" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "'  ORDER BY order_number Asc";
            else if (!outlate_id.equals("0") && !start_date.equalsIgnoreCase("0"))
                Query = "SELECT * FROM " + Tables.TABLE_NAME_ORDER + " as ot join markets as m on ot.market_id=m.market_id and m.route_id='" + outlate_id + "' where " + Tables.ORDER_order_date + ">=" + "'" + start_date + "'and " + ORDER_STATUS + " IN (" + PROCESSING_COMPELETE + ", " + ORDER_BOUNCE + ")" + " and " + Tables.ORDER_order_date + " <=" + "'" + end_date + "' ORDER BY order_number Asc";
            else if (outlate_id.equals("0") && start_date.equalsIgnoreCase("0") && end_date.equalsIgnoreCase("0")) {
                Query = "SELECT * FROM " + Tables.TABLE_NAME_ORDER + " where  " + ORDER_STATUS + " IN (" + PROCESSING_COMPELETE + ", " + ORDER_BOUNCE + ")" + " ORDER BY order_number Asc";
            }

            //Loge("DataView", Query);

            Cursor c = sqLiteDatabase.rawQuery(Query, null);
            c.moveToFirst();
            if (c != null && c.getCount() > 0) {

                do {

                    HashMap<String, String> map = new HashMap<>();

                    for (int i = 2; i < ORDER.length; i++) {

                        if (ORDER[i].equalsIgnoreCase(ORDER_outlet_id)) {

                            map.put(ORDER[i], c.getString(c.getColumnIndex(ORDER[i])));
                            map.put(OUTLETS_OUTLET_NAME, getSingleFilter(TABLE_NAME_OUTLETS, c.getString(c.getColumnIndex(ORDER[i])), OUTLETS_ID, OUTLETS_OUTLET_NAME));
                        } else {
                            map.put(ORDER[i], c.getString(c.getColumnIndex(ORDER[i])));
                            //Loge(ORDER[i], c.getString(c.getColumnIndex(ORDER[i])) + "");
                        }
                    }
                    map.put(ORDER_STATUS, c.getString(c.getColumnIndex(ORDER_STATUS)));
                    list.add(map);

                } while (c.moveToNext());
            }
            if (progressDialog!=null)
            progressDialog.dismiss();
            dbListener.OnLocalDBdataRetrive(list);

            return null;
        }
    }

    private class lastUpdated extends AsyncTask<String, String, String> {
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jsonObject = new JSONObject();
        }

        @Override
        protected String doInBackground(String... strings) {
            open();
            for (int i = 0; i < Tables.All_TABLE_NAME.length; i++) {

                JSONObject jsonObject1 = new JSONObject();


                Cursor c = sqLiteDatabase.rawQuery("select " + Tables.MESSAGE_UPDATED_AT + " from " + Tables.All_TABLE_NAME[i] + " order by _id desc limit 1", null);
                c.moveToFirst();
                if (c != null && c.getCount() > 0) {

                    //Loge(Tables.MESSAGE_UPDATED_AT, c.getString(c.getColumnIndex(Tables.MESSAGE_UPDATED_AT)) + " ");
                    if (c.getString(c.getColumnIndex(Tables.MESSAGE_UPDATED_AT)) == null) {

                        try {
                            jsonObject1.put("last_update", "1995-01-18 12:30:12");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {


                        try {
                            jsonObject1.put("last_update", c.getString(c.getColumnIndex(Tables.MESSAGE_UPDATED_AT)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    try {
                        jsonObject1.put("last_update", "1995-01-18 12:30:12");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }




               /* for (int j = 0; j < TABLE_NAME_ALL_DATA.length; j++) {

                    if (TABLE_NAME_ALL_DATA[j].equalsIgnoreCase(Tables.All_TABLE_NAME[i])) {
                        try {
                            //Loge("Table_name", Tables.All_TABLE_NAME[i]);
                            jsonObject1.put("last_update", "1995/01/18");
                            break;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }*/

                try {
                    jsonObject.put(Tables.All_TABLE_NAME[i], jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            close();
            try {

                jsonObject.put("sales_person_id", basicFunction.getPreference(SR_ID));
                dbListener.OnLocalDBdataRetrive(jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Bonus Policy ---------------------------------------------------------------------------------------------------------------------

    private class prepareDataForOrder extends AsyncTask<String, String, String> {

        String _OutletID;

        public prepareDataForOrder(String _OutletID) {
            this._OutletID = _OutletID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {


            Cursor c2 = sqLiteDatabase.rawQuery("SELECT\n" +
                    "  P.product_id,\n" +
                    "  P.product_name,\n" +
                    "  P.product_category_id,\n" +
                    "  P.product_type_id\n," +
                    "  P.product_sales\n" +
                    "FROM product AS P\n" +
                    "GROUP BY P.product_id,\n" +
                    "         P.product_name,\n" +
                    "         P.product_category_id,\n" +
                    "         P.product_type_id\n" +
                    "ORDER BY P.product_order ASC",null);
            //   Cursor c2 = rawQuery("SELECT P.product_id,P.product_name,P.product_category_id,P.product_type_id FROM product as P INNER Join product_price as pp on p.product_id=pp.product_id INNER JOIN product_combinations as pc on  p.product_id=pc.product_id group by P.product_id,P.product_name,P.product_category_id,P.product_type_id ORDER BY P.product_order ASC");
            if (c2 != null) {
                if (c2.moveToFirst()) {
                    do {

                        //Loge("EEEEEEEEEEEEEEEEEE", "-----------***********----------");


                        String product_id = c2.getString(c2.getColumnIndex("product_id"));
                        String product_name = c2.getString(c2.getColumnIndex("product_name"));

                        String product_category_id = c2.getString(c2.getColumnIndex("product_category_id"));
                        String product_type_id = c2.getString(c2.getColumnIndex("product_type_id"));
                        String product_sales = c2.getString(c2.getColumnIndex("product_sales"));


                        savePreference(product_id, "0.0");


                        String query = "SELECT * FROM " + Tables.TABLE_NAME_PRODUCT_BOOLEAN + " WHERE product_id='" + product_id + "' AND outlet_id='" + _OutletID + "'";
                        //Loge("ProductInsertCheckQuery", query);
                        Cursor c1 = sqLiteDatabase.rawQuery(query,null);
                        //Loge("lkog", c1.getCount() + "");
                        if (c1.getCount() == 0) {
                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("outlet_id", _OutletID);
                            map.put("product_id", product_id);
                            map.put("product_name", product_name);
                            map.put("quantity", "0.00");
                            map.put("boolean", "false");
                            map.put("product_category_id", product_category_id);
                            map.put("product_type_id", product_type_id);
                            map.put("product_sales", product_sales);

                            InsertTable(map, Tables.TABLE_NAME_PRODUCT_BOOLEAN);
                        }
                    } while (c2.moveToNext());
                }
            }

            progressDialog.dismiss();
            try {
                dbListener.OnLocalDBdataRetrive("done");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    private class generatePushJson extends AsyncTask<String, String, String> {
        JSONObject finaljsonObject = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Getting Ready For Sync...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            open();
            String[] operation = ORDER;

            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + operation[0] + " where " + Tables.ORDER_is_pushed + " ='0' and " + ORDER_STATUS + " !='" + PROCESSING_COMPELETE + "'", null);
            cursor.moveToFirst();
            //Loge("Query", "select * from " + operation[0] + " where " + Tables.ORDER_is_pushed + " ='0' and " + ORDER_STATUS + " !='" + PROCESSING_COMPELETE + "'");
            JSONArray jsonArray = new JSONArray();
            if (cursor != null && cursor.getCount() > 0) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject = gethelper(jsonObject, cursor.getString(cursor.getColumnIndex(MARKET_ID)));
                    for (int i = 1; i < operation.length; i++) {

                        try {
                            //Loge(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                            if (operation[i].equalsIgnoreCase("territory_id")) {

                                jsonObject.put(operation[i], "123456");
                            } else
                                jsonObject.put(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Loge("json", e.getMessage());
                        }

                    }
                    String[] operationin = ORDER_DETAILS;
                    open();
                    Cursor cursorin = sqLiteDatabase.rawQuery("select * from " + operationin[0] + " where " + Tables.ORDER_order_number + " ='" + cursor.getString(cursor.getColumnIndex(operation[2])) + "'", null);
                    cursorin.moveToFirst();

                    JSONArray jsonArrayin = new JSONArray();
                    if (cursorin != null && cursorin.getCount() > 0) {
                        do {
                            JSONObject jsonObjectin = new JSONObject();
                            for (int i = 2; i < operationin.length - 2; i++) {

                                try {
                                    //Loge(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    if (operationin[i].equalsIgnoreCase("territory_id")) {

                                        jsonObjectin.put(operationin[i], "123456");
                                    } else if (operationin[i].equalsIgnoreCase(MEMO_DETAILS_Unit_id)) {

                                        jsonObjectin.put("measurement_unit_id", cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    } else {
                                        if (cursorin.getString(cursorin.getColumnIndex(operationin[i])) == null)
                                            jsonObjectin.put(operationin[i], 0);
                                        else
                                            jsonObjectin.put(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //Loge("json", e.getMessage());
                                }

                            }


                            jsonArrayin.put(jsonObjectin);


                        } while (cursorin.moveToNext());


                    }

                    try {
                        jsonObject.put("order_details", jsonArrayin);
                        jsonObject = getGiftIssue(jsonObject, cursor.getString(cursor.getColumnIndex(operation[2])));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());


            }

            try {
                finaljsonObject.put("order_list", jsonArray);
                finaljsonObject.put("mac", basicFunction.getPreference("mac"));
                finaljsonObject.put(Tables.SR_ID, basicFunction.getPreference(Tables.SR_ID));
                /*finaljsonObject.put("office_id", basicFunction.getPreference("office_id"));
                finaljsonObject.put("distributor_id", basicFunction.getPreference("db_id"));
                finaljsonObject.put("store_id", basicFunction.getPreference("store_id"));
                finaljsonObject.put("sales_representative_id", basicFunction.getPreference("sr_id"));
                finaljsonObject.put("sales_representative_code", basicFunction.getPreference("sr_code"));
                finaljsonObject.put("territory_id", basicFunction.getPreference("territory_id"));
                finaljsonObject.put("tso_id", basicFunction.getPreference("tso_id"));
                finaljsonObject.put("ae_id", basicFunction.getPreference("ae_id"));*/



                progressDialog.dismiss();
                dbListener.OnLocalDBdataRetrive(finaljsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                //Loge("log", e.getMessage());
            }

            return null;
        }
    }

    private class generatePushJsonForSingle extends AsyncTask<String, String, String> {
        JSONObject finaljsonObject = new JSONObject();
        String OrderNO;


        public generatePushJsonForSingle(String orderNO) {
            OrderNO = orderNO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Getting Ready For Push...");
            progressDialog.show();


        }

        @Override
        protected String doInBackground(String... strings) {
            open();
            String[] operation = ORDER;

            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + operation[0] + " where  " + ORDER_order_number + " = '" + OrderNO + "'", null);

            //Loge("generatePushJsonForSingle", "select * from " + operation[0] + " where " + Tables.ORDER_is_pushed + " ='1' and " + ORDER_order_number + " = '" + OrderNO + "'");
            cursor.moveToFirst();

            JSONArray jsonArray = new JSONArray();
            if (cursor != null && cursor.getCount() > 0) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject = gethelper(jsonObject, cursor.getString(cursor.getColumnIndex(MARKET_ID)));
                    for (int i = 1; i < operation.length; i++) {

                        try {
                            //Loge(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                            if (operation[i].equalsIgnoreCase("territory_id")) {

                                jsonObject.put(operation[i], "123456");
                            } else
                                jsonObject.put(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Loge("json", e.getMessage());
                        }

                    }
                    String[] operationin = ORDER_DETAILS;
                    open();
                    Cursor cursorin = sqLiteDatabase.rawQuery("select * from " + operationin[0] + " where " + Tables.ORDER_order_number + " ='" + cursor.getString(cursor.getColumnIndex(operation[2])) + "'", null);
                    cursorin.moveToFirst();

                    JSONArray jsonArrayin = new JSONArray();
                    if (cursorin != null && cursorin.getCount() > 0) {
                        do {
                            JSONObject jsonObjectin = new JSONObject();
                            for (int i = 2; i < operationin.length - 2; i++) {

                                try {
                                    //Loge(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    if (operationin[i].equalsIgnoreCase("territory_id")) {

                                        jsonObjectin.put(operationin[i], "123456");
                                    } else {
                                        if (cursorin.getString(cursorin.getColumnIndex(operationin[i])) == null)
                                            jsonObjectin.put(operationin[i], 0);
                                        else
                                            jsonObjectin.put(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //Loge("json", e.getMessage());
                                }

                            }


                            jsonArrayin.put(jsonObjectin);


                        } while (cursorin.moveToNext());


                    }
                    close();
                    try {
                        jsonObject.put("order_details", jsonArrayin);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());


            }

            try {
                finaljsonObject.put("order_list", jsonArray);
                finaljsonObject.put("mac", basicFunction.getPreference("mac"));
                finaljsonObject.put(Tables.SR_ID, basicFunction.getPreference(Tables.SR_ID));
                finaljsonObject = getGiftIssue(finaljsonObject, OrderNO);

                progressDialog.dismiss();
                dbListener.OnLocalDBdataRetrive(finaljsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                //Loge("log", e.getMessage());
            }

            return null;
        }
    }

    private class generatePushJsonForSingleTempMemo extends AsyncTask<String, String, String> {
        JSONObject finaljsonObject = new JSONObject();
        String OrderNO;
        String Order_NUMBER;

        public generatePushJsonForSingleTempMemo(String orderNO) {
            OrderNO = orderNO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            runOnUiThread(new Runnable() {
                public void run() {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Getting Ready For Push...");
                    progressDialog.show();
                }
            });

        }

        @Override
        protected String doInBackground(String... strings) {
            open();
            String[] operation = MEMOS;

            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + "temp_memos" + " where  " + MEMOS_memo_number + " = '" + OrderNO + "'", null);

            //Loge("generatePushJsonForSingleMemo", "select * from " + "temp_memos" + " where " + Tables.ORDER_is_pushed + " ='1' and " + MEMOS_memo_number + " = '" + OrderNO + "'");

            cursor.moveToFirst();

            JSONArray jsonArray = new JSONArray();
            if (cursor != null && cursor.getCount() > 0) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject = gethelper(jsonObject, cursor.getString(cursor.getColumnIndex(MARKET_ID)));
                    for (int i = 1; i < operation.length; i++) {

                        if (operation[i].equalsIgnoreCase(Tables.MEMOS_ORDER_NUMBER)) {

                            Order_NUMBER = cursor.getString(cursor.getColumnIndex(Tables.MEMOS_ORDER_NUMBER));
                        }
                        try {
                            //Loge(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                            if (operation[i].equalsIgnoreCase("territory_id")) {

                                jsonObject.put(operation[i], "123456");
                            } else
                                jsonObject.put(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Loge("json", e.getMessage());
                        }

                    }
                    String[] operationin = MEMO_DETAILS;
                    Cursor cursorin = sqLiteDatabase.rawQuery("select * from " + "temp_memo_details" + " where " + Tables.MEMOS_memo_number + " ='" + cursor.getString(cursor.getColumnIndex(operation[2])) + "'", null);
                    cursorin.moveToFirst();

                    JSONArray jsonArrayin = new JSONArray();
                    if (cursorin != null && cursorin.getCount() > 0) {
                        do {
                            JSONObject jsonObjectin = new JSONObject();
                            for (int i = 2; i < operationin.length - 2; i++) {

                                try {
                                    //Loge(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    if (operationin[i].equalsIgnoreCase("territory_id")) {

                                        jsonObjectin.put(operationin[i], "123456");
                                    } else {
                                        if (cursorin.getString(cursorin.getColumnIndex(operationin[i])) == null)
                                            jsonObjectin.put(operationin[i], 0);
                                        else
                                            jsonObjectin.put(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //Loge("json", e.getMessage());
                                }

                                catch (IllegalStateException ill){
                                    ill.printStackTrace();
                                }

                            }


                            jsonArrayin.put(jsonObjectin);


                        } while (cursorin.moveToNext());


                    }

                    try {
                        jsonObject.put("memo_details", jsonArrayin);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());


            }

            try {
                finaljsonObject.put("memo_list", jsonArray);
                finaljsonObject.put("mac", basicFunction.getPreference("mac"));
                finaljsonObject.put(Tables.SR_ID, basicFunction.getPreference(Tables.SR_ID));
                finaljsonObject = getGiftIssue(finaljsonObject, Order_NUMBER);
                //Loge("MemoJson",finaljsonObject.toString());
                if (progressDialog!=null)
                progressDialog.dismiss();
                dbListener.OnLocalDBdataRetrive(finaljsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                //Loge("log", e.getMessage());
            }

            return null;
        }
    }

    private class generatePushJsonForSingleMemo extends AsyncTask<String, String, String> {
        JSONObject finaljsonObject = new JSONObject();
        String OrderNO;
        String Order_NUMBER;

        public generatePushJsonForSingleMemo(String orderNO) {
            OrderNO = orderNO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            runOnUiThread(new Runnable() {
                public void run() {
                    /*progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Getting Ready For Push...");
                    progressDialog.show();*/
                }
            });

        }

        @Override
        protected String doInBackground(String... strings) {
            open();
            String[] operation = MEMOS;

            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + "memos" + " where  " + MEMOS_memo_number + " = '" + OrderNO + "'", null);

            //Loge("generatePushJsonForSingleMemo", "select * from " + "memos" + " where " + Tables.ORDER_is_pushed + " ='1' and " + MEMOS_memo_number + " = '" + OrderNO + "'");
            cursor.moveToFirst();

            JSONArray jsonArray = new JSONArray();
            if (cursor != null && cursor.getCount() > 0) {
                do {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject = gethelper(jsonObject, cursor.getString(cursor.getColumnIndex(MARKET_ID)));
                    for (int i = 1; i < operation.length; i++) {

                        if (operation[i].equalsIgnoreCase(Tables.MEMOS_ORDER_NUMBER)) {

                            Order_NUMBER = cursor.getString(cursor.getColumnIndex(Tables.MEMOS_ORDER_NUMBER));
                        }

                        try {
                            //Loge(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                            if (operation[i].equalsIgnoreCase("territory_id")) {

                                jsonObject.put(operation[i], "123456");
                            } else if (cursor.getString(cursor.getColumnIndex(operation[i])) == null) {

                                jsonObject.put(operation[i], "0");
                            } else
                                jsonObject.put(operation[i], cursor.getString(cursor.getColumnIndex(operation[i])) + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Loge("json", e.getMessage());
                        }

                    }
                    String[] operationin = MEMO_DETAILS;
                    open();
                    Cursor cursorin = sqLiteDatabase.rawQuery("select * from " + "memo_details" + " where " + Tables.MEMOS_memo_number + " ='" + cursor.getString(cursor.getColumnIndex(operation[2])) + "'", null);
                    cursorin.moveToFirst();

                    JSONArray jsonArrayin = new JSONArray();
                    if (cursorin != null && cursorin.getCount() > 0) {
                        do {
                            JSONObject jsonObjectin = new JSONObject();
                            for (int i = 2; i < operationin.length - 2; i++) {

                                try {
                                    //Loge(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    if (operationin[i].equalsIgnoreCase("territory_id")) {

                                        jsonObjectin.put(operationin[i], "123456");
                                    } else {
                                        if (cursorin.getString(cursorin.getColumnIndex(operationin[i])) == null)
                                            jsonObjectin.put(operationin[i], 0);
                                        else
                                            jsonObjectin.put(operationin[i], cursorin.getString(cursorin.getColumnIndex(operationin[i])) + "");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //Loge("json", e.getMessage());
                                }

                            }


                            jsonArrayin.put(jsonObjectin);


                        } while (cursorin.moveToNext());


                    }
                    cursorin.close();
                    close();
                    try {
                        jsonObject.put("memo_details", jsonArrayin);
                    } catch (JSONException e) {
                        e.printStackTrace();
                       /* if (progressDialog != null)
                            progressDialog.dismiss();*/

                        Toast.makeText(context,"Can't get Order From DB",Toast.LENGTH_LONG).show();
                    }


                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());


            }

            try {
                finaljsonObject.put("memo_list", jsonArray);
                finaljsonObject.put("mac", basicFunction.getPreference("mac"));
                finaljsonObject.put(Tables.SR_ID, basicFunction.getPreference(Tables.SR_ID));
                finaljsonObject = getGiftIssue(finaljsonObject, Order_NUMBER);
                /*if (progressDialog != null)
                    progressDialog.dismiss();*/
                dbListener.OnLocalDBdataRetrive(finaljsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                //Loge("log", e.getMessage());
            }

            return null;
        }
    }

    private class Requireddata extends AsyncTask<String, String, String> {
        JSONObject finaljsonObject = new JSONObject();

        @Override
        protected String doInBackground(String... strings) {
            open();
            try {


                for (int i = 0; i < Tables.Allfild.length; i++) {

                    JSONArray jsonArray = new JSONArray();
                    String query = "select * from " + Tables.Allfild[i][0];

                    Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                    //Loge("data", query + " size" + cursor.getCount());
                    if (cursor != null && cursor.getCount() > 0) {

                        do {

                            JSONObject jsonObject = new JSONObject();
                            for (int j = 2; j < Tables.Allfild[i].length - 2; j++) {

                                if (Tables.Allfild[i][j].equalsIgnoreCase(Tables.MEMOS_is_pushed) || Tables.Allfild[i][j].equalsIgnoreCase("isPushed"))
                                    continue;

                                //    //Loge("error", Tables.Allfild[i][j] + " table name" + Tables.Allfild[i][0] + " j= " + j + " length" + (Tables.Allfild[i].length - 2));
                                jsonObject.put(Tables.Allfild[i][j], Tables.Allfild[i][j]);

                            }

                            jsonArray.put(jsonObject);


                        } while (cursor.moveToNext());


                    } else {
                        JSONObject jsonObject = new JSONObject();
                        for (int j = 2; j < (Tables.Allfild[i].length) - 2; j++) {
                            if (Tables.Allfild[i][j].equalsIgnoreCase(Tables.MEMOS_is_pushed) || Tables.Allfild[i][j].equalsIgnoreCase("isPushed"))
                                continue;
                            //Loge("error1", Tables.Allfild[i][j] + " table name" + Tables.Allfild[i][0] + " j= " + j + " length" + (Tables.Allfild[i].length - 2));
                            jsonObject.put(Tables.Allfild[i][j], "Demo_velue");
                        }

                        jsonArray.put(jsonObject);
                    }
                    finaljsonObject.put(Tables.Allfild[i][0], jsonArray);
                }

                finaljsonObject.put("seals_person_id", TempData.sales_person_id);

            } catch (JSONException e) {

            }

            try {
                dbListener.OnLocalDBdataRetrive(finaljsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // insertData(finaljsonObject.toString());

            return null;
        }
    }

    private class InsertData extends AsyncTask<String, String, String> {

        String json;
        JSONObject jsonObject;
        int code;

        public InsertData(String json, int code) {
            this.json = json;
            this.code = code;
        }

        public InsertData(JSONObject jsonObject, int code) {
            this.jsonObject = jsonObject;
            this.code = code;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading Data From Server...");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (code == 1) {
                    JSONObject jsonObject = new JSONObject(json);



                    for (int i = 0; i < Allfild.length; i++) {

                        Log.e("Logfile", Allfild[i][0] + " sda" + jsonObject.has(Allfild[i][0]));


                        if (jsonObject.has(Allfild[i][0])) {


                            JSONArray jsonArray = jsonObject.getJSONArray(Allfild[i][0]);
                            ContentValues contentValues = new ContentValues();

                            contentValues.put(Tables.DataCheck_table_name,Allfild[i][0]);
                            contentValues.put(Tables.DataCheck_Item_count,jsonArray.length());
                            open();
                            sqLiteDatabase.insert(Tables.TABLE_NAME_DataCheck,null,contentValues);
                            for (int t = 0; t < jsonArray.length(); t++) {

                                JSONObject jsonObjecttemp = jsonArray.getJSONObject(t);

                                ContentValues cv = new ContentValues();
                                for (int j = 2; j < Allfild[i].length; j++) {

                                     // Log.e("table",Allfild[i][0]);

                                    if (Allfild[i][j].equalsIgnoreCase(Tables.MESSAGE_ISPUSHED) || Allfild[i][j].equalsIgnoreCase(Tables.NCP_RETURN_ISPUSHED)) {
                                        cv.put(Allfild[i][j], "1");
                                        ////Loge("Check",Allfild[i][j]);

                                    } else if (Allfild[i][j].equalsIgnoreCase(Tables.NCP_RETURN_CREATE_AT)) {
                                        cv.put(Allfild[i][j], getCurrentDateTime24());

                                    } else if (Allfild[i][j].equalsIgnoreCase(Tables.UNIT_UPDATED_AT)) {
                                        cv.put(Allfild[i][j], getCurrentDateTime24());

                                    } else {
                                   /* if (Allfild[i][j].equalsIgnoreCase("plan_id"))
                                       // //Loge("plan_id",Allfild[i][j]+"  "+jsonObjecttemp.toString());*/

                                        if (!jsonObjecttemp.has(Allfild[i][j])) {
                                            continue;
                                        }
                                        if (Allfild[i][j].equalsIgnoreCase(Tables.PRODUCT_IMAGE_URL)) {
                                            cv.put(Allfild[i][j], getCurrentDateTime24());
                                            try {
                                                saveImage(jsonObjecttemp.getString(Allfild[i][j]), jsonObjecttemp.getString(PRODUCT_ID));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        cv.put(Allfild[i][j], jsonObjecttemp.getString(Allfild[i][j]));

                                    }


                                }


                                if (Allfild[i][0].equalsIgnoreCase(TABLE_NAME_THANA) || Allfild[i][0].equalsIgnoreCase(TABLE_NAME_OUTLETS) ||
                                        Allfild[i][0].equalsIgnoreCase(TABLE_NAME_ROOT) || Allfild[i][0].equalsIgnoreCase(TABLE_NAME_MARKETS) ||
                                        Allfild[i][0].equalsIgnoreCase(TABLE_NAME_ORDER) || Allfild[i][0].equalsIgnoreCase(TABLE_NAME_LOCATION) ||
                                        Allfild[i][0].equalsIgnoreCase(TABLE_NAME_MEMOS) ||
                                        Allfild[i][0].equalsIgnoreCase(TABLE_NAME_PRODUCT)) {

                                    excQuery("delete  from " + Allfild[i][0] + " where " + Allfild[i][2] + "='" + jsonObjecttemp.getString(Allfild[i][2]) + "'");
                                }

                                open();
                                sqLiteDatabase.insert(Allfild[i][0], null, cv);
                                //Loge("Table & data", Allfild[i][0] + "  " + cv.toString());
                                close();
                            }

                        }
                    }

                    updateWithServer(jsonObject,code);
                    updateMemoWithServer(jsonObject,code);
                    progressDialog.dismiss();
                    dbListener.OnLocalDBdataRetrive("done");
                }
                else if (code == 2) {
                    open();
                    sqLiteDatabase.execSQL("delete from Policy_Table");
                    sqLiteDatabase.execSQL("delete from Bonus_Eligible_Group");
                    sqLiteDatabase.execSQL("delete from Bonus_Eligible_Outlet_Categories");
                    sqLiteDatabase.execSQL("delete from policy_root_product");
                    sqLiteDatabase.execSQL("delete from policy_product_Option");
                    sqLiteDatabase.execSQL("delete from policy_option_price_slab");
                    sqLiteDatabase.execSQL("delete from policy_bonus_product");
                    sqLiteDatabase.execSQL("delete from policy_default_product");
                    sqLiteDatabase.execSQL("delete from DiscountBonusPolicyToSpecialGroupSo");
                    sqLiteDatabase.execSQL("delete from DiscountBonusPolicyOptionExclusionProduct");
                    sqLiteDatabase.execSQL("delete from DiscountBonusPolicyOptionInclusionProduct");
                    sqLiteDatabase.execSQL("delete from DiscountBonusPolicyToExcludingOutletGroupSr");

                    insertbonusPolicyData(jsonObject);
                }
                else if (code == 3) {
                    insertbonusPolicyOutletData(jsonObject);
                } else if (code == 4) {
                    insertbonusUnitDetails(jsonObject);
                }else if (code == 5) {
                    updateWithServer(jsonObject,code);
                }else if (code == 6) {
                    updateMemoWithServer(jsonObject,code);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Loge("Json", e.getMessage());
            }

            return null;
        }
    }

}
