package com.srapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.srapp.Adapter.AdapterForBonusPolicyProductSelection;
import com.srapp.Adapter.AdapterForBonusPolicyProductSelection1;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Util.Parent;
import com.srapp.Util.ParentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static com.srapp.Adapter.SalesOrderDetailsAdaper1.bonus_policylistener;
import static com.srapp.TempData.BPBonusProductView;
import static com.srapp.TempData.BPSelected_policy_type;
import static com.srapp.TempData.BPSelected_set;
import static com.srapp.TempData.PolicySetRelation;

public class policy_set_for_dis_or_bonus extends Parent {

    Button bonus_product, set_discount, CancelButton, set1, set2;
    Data_Source db;
    String polictype, policy_id, option_id, formula;
    ListView listView, ListViewSetRelation;
    LinearLayout bproducts, policy_set;
    View devider;
    TextView ProductName;
    TextView edqty;
    AdapterForBonusPolicyProductSelection adapterForBonusPolicyProductSelection;
    AdapterForBonusPolicyProductSelection1 adapterForBonusPolicyProductSelection1;
    String minQty, Quantity, setSelection, disSelection;
    int setSelectionPostion, disPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_set_for_dis_or_bonus);
        Log.e("outletCategory_id_", TempData.OutletCatagoryID);

        db = new Data_Source(this);
        bonus_product = findViewById(R.id.bonus_product);
        set_discount = findViewById(R.id.set_discount);
        set1 = findViewById(R.id.set1);
        set2 = findViewById(R.id.set2);
        edqty = findViewById(R.id.edqty);
        ProductName = findViewById(R.id.ProductName);
        listView = findViewById(R.id.ProductListView);
        ListViewSetRelation = findViewById(R.id.ProductListView1);// set relation list view---------
        bproducts = findViewById(R.id.policy_tag_layout);
        policy_set = findViewById(R.id.policy_set);
        devider = findViewById(R.id.devider);
        CancelButton = findViewById(R.id.CancelButton);

        if (getIntent() != null) {

            polictype = getIntent().getStringExtra("policy_type");
            policy_id = getIntent().getStringExtra("policy_id");
            option_id = getIntent().getStringExtra("option_id");
            option_id = getIntent().getStringExtra("option_id");

            if (getIntent().hasExtra("disPosition")) {
                disSelection = getIntent().getStringExtra("disSelection");
                disPosition = getIntent().getIntExtra("disPosition", 0);
                Log.i("disSelection", String.valueOf(disSelection));
            }

            if (getIntent().hasExtra("formula")) {
                formula = getIntent().getStringExtra("formula");
                setSelection = getIntent().getStringExtra("setSelection");
                setSelectionPostion = getIntent().getIntExtra("setPosition", 0);
                Log.i("setSelection", String.valueOf(setSelection));
            } else {
                formula = "";
            }

            if (getIntent().hasExtra("min_qty")) {
                minQty = getIntent().getStringExtra("min_qty");
            } else {
                minQty = "";
            }

            if (getIntent().hasExtra("combinedQty")) {
                Quantity = getIntent().getStringExtra("combinedQty");
            } else {
                Quantity = "";
            }

            ProductName.setText(getIntent().getStringExtra("policy_name"));
            Log.e("productpolictype", "polictype=" + polictype + "policy_id=" + policy_id + "option_id=" + option_id + "" + formula);
        }

        int dm = 0, dm_chk = 1;

        if (disSelection.equals("1")) {
            dm = 0;
            dm_chk = 1;
            set_discount.setBackgroundResource(R.drawable.bg_btn_submit);
            listView.setVisibility(View.VISIBLE);
            bonus_product.setBackgroundColor(Color.parseColor("#DDDDDD"));
            set_discount.setTextColor(Color.parseColor("#ffffff"));
            bonus_product.setTextColor(Color.parseColor("#000000"));

        } else if (disSelection.equals("2")) {
            dm = 1;
            dm_chk = 2;
            bonus_product.setBackgroundResource(R.drawable.bg_btn_submit);
            set_discount.setBackgroundColor(Color.parseColor("#DDDDDD"));
            bonus_product.setTextColor(Color.parseColor("#ffffff"));
            set_discount.setTextColor(Color.parseColor("#000000"));


            ArrayList<HashMap<String, String>> BPBonusProductView_policy_id = new ArrayList<HashMap<String, String>>();
            BPBonusProductView_policy_id = BPBonusProductView.get(policy_id);

            Log.e("SetRelation", PolicySetRelation.get(policy_id) + "---");
            if (PolicySetRelation.get(policy_id) == "" || PolicySetRelation.get(policy_id) == null || PolicySetRelation.get(policy_id).equals("AND")) {
                ArrayList<HashMap<String, String>> set1 = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> set2 = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < BPBonusProductView_policy_id.size(); i++) {
                    if (BPBonusProductView_policy_id.get(i).get("set").equals("1")) {
                        set1.add(BPBonusProductView_policy_id.get(i));
                    } else if (BPBonusProductView_policy_id.get(i).get("set").equals("2")) {
                        set2.add(BPBonusProductView_policy_id.get(i));
                    }
                }

                Log.e(TAG, "Set1: " + new Gson().toJson(set1));
                Log.e(TAG, "Set2: " + new Gson().toJson(set2));
                if (set1.size() > 0 && !set1.get(0).get("relation").equals("AND")) {
                    adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, set1, Double.parseDouble(set1.get(0).get("provided_qty")));
                    listView.setAdapter(adapterForBonusPolicyProductSelection);
                } else {
                    listView.setVisibility(View.GONE);
                    devider.setVisibility(View.GONE);
                }
                if (set2.size() > 0 && !set2.get(0).get("relation").equals("AND")) {
                    adapterForBonusPolicyProductSelection1 = new AdapterForBonusPolicyProductSelection1(this, set2, Double.parseDouble(set2.get(0).get("provided_qty")));
                    ListViewSetRelation.setAdapter(adapterForBonusPolicyProductSelection1);
                } else {
                    ListViewSetRelation.setVisibility(View.GONE);
                    devider.setVisibility(View.GONE);
                }

            } else {
                set1.setVisibility(View.VISIBLE);
                set2.setVisibility(View.VISIBLE);
                if (BPSelected_set.get(policy_id).equals("1")) {
                    set1.setTextColor(Color.parseColor("#fff"));
                    set1.setBackgroundColor(Color.parseColor("#00a65a"));

                    set2.setBackgroundColor(Color.parseColor("#fafafa"));
                    set2.setTextColor(Color.parseColor("#666"));
                } else {
                    set2.setBackgroundColor(Color.parseColor("#00a65a"));
                    set2.setTextColor(Color.parseColor("#fff"));

                    set1.setBackgroundColor(Color.parseColor("#fafafa"));
                    set1.setTextColor(Color.parseColor("#666"));
                }
                if (BPBonusProductView_policy_id.size() > 0 && !BPBonusProductView_policy_id.get(0).get("relation").equals("AND")) {
                    adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, BPBonusProductView_policy_id, Double.parseDouble(BPBonusProductView_policy_id.get(0).get("provided_qty")));
                    listView.setAdapter(adapterForBonusPolicyProductSelection);
                    ListViewSetRelation.setVisibility(View.GONE);
                    devider.setVisibility(View.GONE);
                }
            }
        }

        bonus_product.setOnClickListener(view -> {
            BPSelected_policy_type.put(policy_id,"2");
            bonus_policylistener.setBonus_type(2);
            listView.setVisibility(View.VISIBLE);
            bonus_product.setBackgroundResource(R.drawable.bg_btn_submit);
            set_discount.setBackgroundColor(Color.parseColor("#DDDDDD"));
            bonus_product.setTextColor(Color.parseColor("#ffffff"));
            set_discount.setTextColor(Color.parseColor("#000000"));

            set1.setOnClickListener(v -> {
                BPSelected_set.put(policy_id,"1");
                bonus_policylistener.setBonus_type(2);

                set1.setTextColor(Color.parseColor("#FFFFFF"));
                set1.setBackgroundColor(Color.parseColor("#00A65A"));

                set2.setBackgroundColor(Color.parseColor("#FAFAFA"));
                set2.setTextColor(Color.parseColor("#666666"));

                ArrayList<HashMap<String, String>> BPBonusProductView_policy_id = new ArrayList<HashMap<String, String>>();
                BPBonusProductView_policy_id=BPBonusProductView.get(policy_id);
                if(BPBonusProductView_policy_id.size()>0 && !BPBonusProductView_policy_id.get(0).get("relation").equals("AND"))
                {
                    adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, BPBonusProductView_policy_id, Double.parseDouble(BPBonusProductView_policy_id.get(0).get("provided_qty")));
                    listView.setAdapter(adapterForBonusPolicyProductSelection);
                }
                else
                {
                    listView.setAdapter(null);
                }
            });

            set2.setOnClickListener(v -> {
                BPSelected_set.put(policy_id,"2");
                bonus_policylistener.setBonus_type(2);

                set2.setBackgroundColor(Color.parseColor("#00A65A"));
                set2.setTextColor(Color.parseColor("#FFFFFF"));

                set1.setBackgroundColor(Color.parseColor("#FAFAFA"));
                set1.setTextColor(Color.parseColor("#666666"));

                ArrayList<HashMap<String, String>> BPBonusProductView_policy_id = new ArrayList<HashMap<String, String>>();
                BPBonusProductView_policy_id=BPBonusProductView.get(policy_id);
                if(BPBonusProductView_policy_id.size()>0 && !BPBonusProductView_policy_id.get(0).get("relation").equals("AND"))
                {
                    adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, BPBonusProductView_policy_id, Double.parseDouble(BPBonusProductView_policy_id.get(0).get("provided_qty")));
                    listView.setAdapter(adapterForBonusPolicyProductSelection);
                }
                else
                {
                    listView.setAdapter(null);
                }
            });

            ArrayList<HashMap<String, String>> BPBonusProductView_policy_id = new ArrayList<HashMap<String, String>>();
            BPBonusProductView_policy_id=BPBonusProductView.get(policy_id);
            Log.e(TAG, "BPBonusProductView_policy_id: " + new Gson().toJson(BPBonusProductView_policy_id));
            Log.e(TAG, "PolicySetRelation: " + new Gson().toJson(PolicySetRelation));
            Log.e("SetRelation",PolicySetRelation.get(policy_id)+"---");
            if(PolicySetRelation.get(policy_id) == null || PolicySetRelation.get(policy_id).equals("") || PolicySetRelation.get(policy_id).equals("AND") )
            {

                ArrayList<HashMap<String, String>> set1 = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> set2 = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < BPBonusProductView_policy_id.size(); i++) {
                    if(BPBonusProductView_policy_id.get(i).get("set").equals("1"))
                    {
                        set1.add(BPBonusProductView_policy_id.get(i));
                    }
                    else if(BPBonusProductView_policy_id.get(i).get("set").equals("2"))
                    {
                        set2.add(BPBonusProductView_policy_id.get(i));
                    }
                }

                Log.e(TAG, "Set1: " + new Gson().toJson(set1));
                Log.e(TAG, "Set2: " + new Gson().toJson(set2));
                if(set1.size()>0 && set1.get(0).get("relation").equals("OR")) {

                    adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, set1, Double.parseDouble(set1.get(0).get("provided_qty")));
                    listView.setAdapter(adapterForBonusPolicyProductSelection);
                }
                else if(set2.size()>0 && set2.get(0).get("relation").equals("OR"))
                {
                    Log.e("Debug_T8", "Set1: " + new Gson().toJson(set2));
                    adapterForBonusPolicyProductSelection1 = new AdapterForBonusPolicyProductSelection1(this, set2, Double.parseDouble(set2.get(0).get("provided_qty")));
                    ListViewSetRelation.setAdapter(adapterForBonusPolicyProductSelection1);
                }
                else if(set1.size()>0 && set1.get(0).get("relation").equals("AND")) {
                    Double eligeble_qty=0.0;
                    for (int t=0; t<set1.size();t++){
                        eligeble_qty+=Double.parseDouble(set1.get(t).get("provided_qty"));
                    }

                    Log.e("Debug_T9",eligeble_qty+"");
                    adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, set1, eligeble_qty);
                    listView.setAdapter(adapterForBonusPolicyProductSelection);
                }
                else if(set2.size()>0 && set2.get(0).get("relation").equals("AND"))
                { Double eligeble_qty=0.0;
                    for (int t=0; t<set2.size();t++){
                        eligeble_qty+=Double.parseDouble(set2.get(t).get("provided_qty"));
                    }
                    Log.e("Debug_T8", "Set1: " + new Gson().toJson(set2));
                    adapterForBonusPolicyProductSelection1 = new AdapterForBonusPolicyProductSelection1(this, set2, eligeble_qty);
                    ListViewSetRelation.setAdapter(adapterForBonusPolicyProductSelection1);
                }
                else
                {
                    ListViewSetRelation.setVisibility(View.GONE);
                    devider.setVisibility(View.GONE);
                }

            }
            else
            {
                set1.setVisibility(View.VISIBLE);
                set2.setVisibility(View.VISIBLE);
                set1.setVisibility(View.VISIBLE);
                set2.setVisibility(View.VISIBLE);
                if(BPSelected_set.get(policy_id).equals("1"))
                {
                    set1.setBackgroundColor(Color.parseColor("#00A65A"));
                    set1.setTextColor(Color.parseColor("#FFFFFF"));

                    set2.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    set2.setTextColor(Color.parseColor("#666666"));
                }
                else
                {
                    set2.setBackgroundColor(Color.parseColor("#00A65A"));
                    set2.setTextColor(Color.parseColor("#FFFFFF"));

                    set1.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    set1.setTextColor(Color.parseColor("#666666"));
                }
                if(BPBonusProductView_policy_id.size()>0 && !BPBonusProductView_policy_id.get(0).get("relation").equals("AND")) {
                    adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, BPBonusProductView_policy_id, Double.parseDouble(BPBonusProductView_policy_id.get(0).get("provided_qty")));
                    listView.setAdapter(adapterForBonusPolicyProductSelection);
                    ListViewSetRelation.setVisibility(View.GONE);
                    devider.setVisibility(View.GONE);
                }
            }

        });

        set_discount.setOnClickListener(view -> {
            BPSelected_policy_type.put(policy_id, "1");
            bonus_policylistener.setBonus_type(2);

            set_discount.setBackgroundResource(R.drawable.bg_btn_submit);
            listView.setVisibility(View.INVISIBLE);
            bonus_product.setBackgroundColor(Color.parseColor("#DDDDDD"));
            set_discount.setTextColor(Color.parseColor("#ffffff"));
            bonus_product.setTextColor(Color.parseColor("#000000"));
        });

        CancelButton.setOnClickListener(v -> {

            if (adapterForBonusPolicyProductSelection != null) {

                ArrayList<HashMap<String, String>> list = adapterForBonusPolicyProductSelection.getdata();
                Log.e(TAG, "AdapterList1: " + new Gson().toJson(list));
                for (int i = 0; i < list.size(); i++) {
                    if (Double.parseDouble(list.get(i).get("quantity")) <= 0 && Boolean.parseBoolean(list.get(i).get("selected_s"))) {
                        Toast.makeText(getApplicationContext(), "Can Not select 0 Quantity For " + list.get(i).get("item"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                 /*   if (
                            !chekVanstock(
                                    list.get(i).get("product_id"),
                                    Double.parseDouble(list.get(i).get("quantity")),
                                    Boolean.parseBoolean(list.get(i).get("selected_s")),
                                    list.get(i).get("policy_id"),
                                    list.get(i).get("measurement_unit_id"))
                    ) {
                        Toast.makeText(getApplicationContext(), "Van Stock not available of " + list.get(i).get("item"), Toast.LENGTH_SHORT).show();
                        return;
                    }*/

                }
            }
            if (adapterForBonusPolicyProductSelection1 != null) {
                ArrayList<HashMap<String, String>> list = adapterForBonusPolicyProductSelection1.getdata();
                Log.e(TAG, "AdapterList2: " + new Gson().toJson(list));
                for (int i = 0; i < list.size(); i++) {
                    if (Double.parseDouble(list.get(i).get("quantity")) <= 0 && Boolean.parseBoolean(list.get(i).get("selected_s"))) {
                        Toast.makeText(getApplicationContext(), "Can Not select 0 Quantity For " + list.get(i).get("item"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                 /*     if (
                            !chekVanstock(
                                    list.get(i).get("product_id"),
                                    Double.parseDouble(list.get(i).get("quantity")),
                                    Boolean.parseBoolean(list.get(i).get("selected_s")),
                                    list.get(i).get("policy_id"),
                                    list.get(i).get("measurement_unit_id"))
                    ) {
                        Toast.makeText(getApplicationContext(), "Van Stock not available of " + list.get(i).get("item"), Toast.LENGTH_SHORT).show();
                        return;
                    }*/

                }
            }

            finish();

        });

    }

    private String getProductName(String bonus_product_id) {
        String pname = "";
        String productsquery = "SELECT product_id,product_name FROM  products WHERE product_id='" + bonus_product_id + "' limit 1";
        Log.e("STOCK QUERY:", productsquery);
        Cursor cursor = db.rawQuery(productsquery, "Bonus_product_Policy8");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            return cursor.getString(1);
        }

        return "unDefine";
    }

    private String ConvertToAnyUnitFromDispencer(String product_id, String quantity, String measurement_unit_id) {
        if (measurement_unit_id == null || measurement_unit_id.equalsIgnoreCase("null")) {
            return quantity;
        }
        Double quantityd = Double.parseDouble(quantity);
        Double measurement_unit_idd = Double.parseDouble(measurement_unit_id);

        if (measurement_unit_idd == 7) {
            return quantity;
        } else {

            Cursor cursor = db.rawQuery("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='7'");

            cursor.moveToFirst();

            if (cursor.getCount() > 0 && cursor != null) {

                quantityd = quantityd * cursor.getDouble(0);
                cursor.close();
                Log.e("quantityd*", quantityd + "");
                Cursor cursor1 = db.rawQuery("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='" + measurement_unit_id + "'");

                cursor1.moveToFirst();

                if (cursor1.getCount() > 0 && cursor1 != null) {

                    quantityd = quantityd / cursor1.getDouble(0);
                    quantityd = Double.parseDouble(ParentActivity.SpecialroundTwoDecimals(quantityd + ""));
                    Log.e("quantityd/", quantityd + "");
                }
                cursor1.close();


            } else {
                Cursor cursor1 = db.rawQuery("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='7'");

                cursor1.moveToFirst();

                if (cursor1.getCount() > 0 && cursor1 != null) {

                    quantityd = quantityd * cursor1.getDouble(0);
                    quantityd = Double.parseDouble(ParentActivity.SpecialroundTwoDecimals(quantityd + ""));
                    Log.e("quantityd/", quantityd + "");
                }
                cursor1.close();
            }

            cursor.close();

            Log.e("quantityd", quantityd + " pn" + getProductName(product_id));
            return quantityd + "";

        }

    }

    private boolean chekVanstock(String product_id, Double qty, boolean b, String policy_id, String measurement_unit_id) {
        if (!b) {
            return true;
        }
        boolean isstockavilable = true;
        String stockQtyquey = "SELECT SUM(quantity) as quantity FROM  van_stocks WHERE product_id='" + product_id + "'";
        Log.e("STOCK_QUERY:", stockQtyquey);
        double stockQty = 0;
        Cursor cursor1 = db.rawQuery(stockQtyquey, "Bonus_product_Policy7");
        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                Double stock = cursor1.getDouble(0);

                Log.e("stocke", stock + "");
                if (TempData.editMemo.equalsIgnoreCase("true")) {
                    stock = stock + getvanqtyafteredit(product_id);
                } else
                    stock = stock - Double.parseDouble(getPreference(product_id));
                Log.e("stocke1", stock + "  , " + Double.parseDouble(getPreference(product_id)));
                stock = Double.parseDouble(ConvertToAnyUnitFromDispencer(product_id, stock + "", measurement_unit_id));
                Log.e("stocke2", stock + "" + getPreference(product_id));
                if (TempData.editMemo.equalsIgnoreCase("true")) {
                    stock = stock + getmemoqty(product_id, policy_id);
                }
                Log.e("stocvsqty", stock + " " + qty);
                if (stock < qty)
                    isstockavilable = false;


            }
        } else {
            isstockavilable = false;
        }
        cursor1.close();
        Log.e("isstockavilable", getProductName(product_id) + " " + isstockavilable);
        return isstockavilable;
    }

    private Double getvanqtyafteredit(String product_id) {

        Cursor cursor = db.rawQuery("select quantity from memo_details where memo_no='" + TempData.memoNumber + "' and product_id ='" + product_id + "' and is_bonus='0' and product_type='" + 0 + "'", "getmemoqty");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            return cursor.getDouble(0) - Double.parseDouble(getPreference(product_id));
        }
        return 0.00;
    }

    private double getmemoqty(String bonus_product_id, String policy_id) {

        Cursor cursor = db.rawQuery("select quantity from memo_details where memo_no='" + TempData.memoNumber + "' and product_id ='" + bonus_product_id + "' and is_bonus='3' and policy_id='" + policy_id + "'", "getmemoqty");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            return cursor.getDouble(0);
        }
        return 0.00;
    }

    public void savePreference(String key, String value) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key) {
        String value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        value = prefs.getString(key, "0");

        return value;

    }

}