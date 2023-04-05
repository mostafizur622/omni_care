package com.srapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Bonus_policy;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.Util.Parent;
import com.srapp.Util.ParentActivity;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.srapp.TempData.BPbonus_product;
import static com.srapp.TempData.BPbonus_product_t;
import static com.srapp.TempData.BPbonus_product_temp;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.discountmap;
import static com.srapp.TempData.discountoffer;
import static com.srapp.TempData.discounttype;
import static com.srapp.TempData.policyMap;

public class SalesOrderDetailsAdaper extends BaseAdapter {
    boolean memoeditable = true;
    // Declare Variables
    Context context;
    String SO_ID;
    int bonusPolicy = 0;
    int policy_offer = 0;
    ArrayList<String> inPolicyProductlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
    //	ArrayList<HashMap<String, String>> itemListData = new ArrayList<HashMap<String, String>>();
    //HomeProjectListAdapter featuredProjectsListAdapter;
    ArrayList<HashMap<String, String>> returnContent = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> Bonus = new ArrayList<HashMap<String, String>>();
    ArrayList<Double> num = new ArrayList<Double>();
    HashMap<String, String> priceMap = new HashMap<String, String>();
    HashMap<String, String> policy_map = new HashMap<String, String>();
    String price = "";
    int state = 0;
    String TargetCustomer = "0";
    String InstietuteID = "";
    ArrayList<String> policy_ids = new ArrayList<String>();
    ArrayList<String> productList = new ArrayList<String>();
    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> root_product_id = new ArrayList<String>();
    Double discountp = 0.0;
    TextView NameTv, TPrice, txtTotal, discount, subt, vattxt, vattv, discount_details, txtBonusPolicy, netpayable;
    EditText QuantityEd, UPrice;
    ImageView ActionBtn;
    String product_name = "", Quantity = "", product_id = "", has_combination = "", general_price = "";
    Double sum = 0.0, vat = 0.0;
    String outlet_ID = "";
    String date, memodate;
    Double Total_Discount = 0.0;
    String Discounttext = "";
    Data_Source db;
    public static Bonus_policy bonus_policylistener;

    public SalesOrderDetailsAdaper(Context context, ArrayList<HashMap<String, String>> arraylistContent, String _SO_ID, String outlet_ID) {
        this.context = context;
        this.outlet_ID = outlet_ID;
        itemListContent = arraylistContent;

        db = new Data_Source(context);
        if (TempData.policyClear) {
            db.excQuery("delete from policy_product_Option_temp");
            TempData.policyClear = true;
        }
        SO_ID = _SO_ID;
        bonus_policylistener = new Bonus_policy() {
            @Override
            public void setBonus_type(int state1) {
                Log.e("state123", state1 + "");
                Runtime.getRuntime().freeMemory();
                state = state1;
                if (state1 != 2)
                    UpdatePrice();
                else
                    setBPProductBonus();
            }
        };

        if (TempData.editMemo.equalsIgnoreCase("true")) {
            date = TempData.MemoDate;
        } else {
            date = getCurrentDate();
        }

        Log.e("Date:", String.valueOf(date));
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Date datevalue = null;
        try {
            datevalue = simpleDateFormat.parse(String.valueOf(date));

            simpleDateFormat.applyPattern(pattern);
            memodate = simpleDateFormat.format(datevalue);

            Log.e("MemoDate:", String.valueOf(memodate));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        InstietuteID = TempData.InstituteID;
        Log.e("Target:" + TargetCustomer, "Institude: " + InstietuteID);
        sum = 0.0;
        txtTotal = (TextView) ((Activity) context).findViewById(R.id.total_price);
        discount = (TextView) ((Activity) context).findViewById(R.id.discount);
        subt = (TextView) ((Activity) context).findViewById(R.id.sub_total);
        vattxt = (TextView) ((Activity) context).findViewById(R.id.vat);
        discount_details = (TextView) ((Activity) context).findViewById(R.id.discount_details);
        txtBonusPolicy = (TextView) ((Activity) context).findViewById(R.id.txtBonusPolicy);


        if (!TempData.OutletCatagoryID.equalsIgnoreCase("17"))
            UpdatePrice();
        else {
            UpdateDistPrice();
        }
    }

    @Override
    public int getCount() {
        return itemListContent.size();
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


        returnContent = itemListContent;

        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_sales_item, null);

        NameTv = (TextView) view2.findViewById(R.id.product_name);
        UPrice = (EditText) view2.findViewById(R.id.price);
        vattv = view2.findViewById(R.id.vat);
        TPrice = (TextView) view2.findViewById(R.id.total_price);
        QuantityEd = (EditText) view2.findViewById(R.id.quantity);
        ActionBtn = (ImageView) view2.findViewById(R.id.dlt_product_sales_item);

        HashMap<String, String> mapContent = new HashMap<String, String>();
        mapContent = itemListContent.get(position);
        product_name = mapContent.get("product_name");
        Quantity = mapContent.get("quantity");
        product_id = mapContent.get("product_id");

        vattv.setText(mapContent.get("vatt") + "%");
        Log.e("vat", mapContent.get("vatt") + "%");
        has_combination = mapContent.get("has_combination");
        general_price = mapContent.get("general_price");
        Log.e("general_price", "....." + general_price);

        if (mapContent.get("is_bonus").equalsIgnoreCase("0")) {

            UPrice.setEnabled(true);
        } else {
            UPrice.setEnabled(false);
        }


        if (!Quantity.equals("")) {

            if (general_price == null) {
                NameTv.setText(product_name);
                QuantityEd.setText(Quantity);

            } else {
                Double _generalPrice = Double.parseDouble(general_price);
                Double _Quantity = Double.parseDouble(Quantity);
                Double T_Price = _generalPrice * _Quantity;

                NameTv.setText(product_name);
                if (Quantity.equalsIgnoreCase("0")) {
                    QuantityEd.setText("");
                    UPrice.setText(String.valueOf(roundTwoDecimals(Double.parseDouble("0.00"))));

                } else {
                    Log.e("UPrice product_id: ", "" + getPreference("up" + itemListContent.get(position).get("product_id")));
                    Log.e("SalesUPrice: ", TempData.SalesUPricePosition + "00");

                    if (getPreference("up" + itemListContent.get(position).get("product_id")).equalsIgnoreCase("0")) {
                        UPrice.setText(String.valueOf(roundTwoDecimals(Double.parseDouble(general_price))));

                    } else if (getPreference("up" + itemListContent.get(position).get("product_id")).equalsIgnoreCase("")) {
                        UPrice.setText(String.valueOf(roundTwoDecimals(Double.parseDouble(general_price))));

                    } else {
                        UPrice.setText(String.valueOf(roundTwoDecimals(Double.parseDouble(getPreference("up" + itemListContent.get(position).get("product_id"))))));
                        T_Price = Double.parseDouble(getPreference("up" + itemListContent.get(position).get("product_id"))) * _Quantity;
//						UpdatePrice();
                    }

                    QuantityEd.setText(Quantity);
                }

                Log.e("SCROLLING PRICE:", "......." + T_Price);
                Log.e("Valuettt :", getPreference("up" + mapContent.get("product_id")) + "   n");
                //TPrice.setText(""+T_Price);

                TPrice.setText(String.valueOf(roundTwoDecimals(Double.parseDouble("" + T_Price))));


                if (TempData.editMemo.equalsIgnoreCase("true")) { // if memo in edit moad then & open  price applied then open price will show from memo
                    Log.e("Valuettt", getPreference("up" + mapContent.get("product_id")));
                    if (getPreference("up" + itemListContent.get(position).get("product_id")).equalsIgnoreCase("0")) {
                        Log.e("Value", getPreference("up" + mapContent.get("product_id")));
                        UPrice.setText(String.valueOf(((Parent) context).roundTwoDecimals(Double.parseDouble(general_price))));

                    } else {

                        /* if ()*/

                        UPrice.setText(String.valueOf(roundTwoDecimals(Double.parseDouble(getPreference("up" + mapContent.get("product_id"))))));
                    }


                }

            }

        }

        // TODO Auto-generated method stub
        QuantityEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Log.e("AFTER:", s.toString());

                if (s.toString().length() == 0) {
                    itemListContent.get(position).put("quantity", "0.00");
                    String query = "UPDATE product_boolean SET quantity = " + "'" + s.toString() + "'" + " WHERE product_id = " + "'" + itemListContent.get(position).get("product_id") + "'" + " AND outlet_id='" + outlet_ID + "'";
                    db.excQuery(query);
                    Log.e("QUERY", query);
                    if (!TempData.OutletCatagoryID.equalsIgnoreCase("17"))
                        UpdatePrice();
                    else {
                        UpdateDistPrice();
                    }
                } else {
                    if (isDouble(s.toString())) {


                        String QUANTITY = "";

                        Log.e("quantity..........", "" + QUANTITY);
                        Log.e("QuantityEd quantity: ", "" + QuantityEd.getText().toString());
                        //QuantityEd.setText("0");
                        QUANTITY = s.toString();
                        String query = "UPDATE product_boolean SET quantity = " + "'" + QUANTITY + "'" + " WHERE product_id = " + "'" + itemListContent.get(position).get("product_id") + "'" + " AND outlet_id='" + outlet_ID + "'";
                        Log.e("QUERY", query);
                        db.excQuery(query);
                        itemListContent.get(position).put("quantity", QUANTITY);


                        showLimitExeednotification(Double.parseDouble(QUANTITY), itemListContent.get(position).get("product_id"));
                        if (!TempData.OutletCatagoryID.equalsIgnoreCase("17"))
                            UpdatePrice();
                        else {
                            UpdateDistPrice();
                        }
                    } else {
                        Toast.makeText(context, "Please enter correct number format!", Toast.LENGTH_LONG).show();
                        QuantityEd.setText("");

                    }
                }
            }
        });


        // TODO Auto-generated method stub
        UPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(".")) {


                }


                // TODO Auto-generated method stub
                Log.e("AFTER:", s.toString());
                Log.e("Position Logg:", "" + position);


                //assign position string value to a temp variable
                TempData.SalesUPricePosition = "" + position;

                TempData.SalesUPrice = "" + s.toString();
                savePreference("up" + itemListContent.get(position).get("product_id"), s.toString());
                Log.e("product_id_Position", itemListContent.get(position).get("product_id"));
                Log.e("product_id_Sales", getPreference(itemListContent.get(position).get("product_id")));

                if (s.toString().length() == 0) {
                    if (!TempData.OutletCatagoryID.equalsIgnoreCase("17"))
                        UpdatePrice();
                    else {
                        UpdateDistPrice();
                    }
                } else {
                    if (isDouble(s.toString())) {
                        HashMap<String, String> mapContent = new HashMap<String, String>();
                        mapContent = itemListContent.get(position);
                        //product_name= mapContent.get("product_name");
                        String Quantity = mapContent.get("quantity");

                        double giventQty = Double.parseDouble(Quantity);
                        double stockqty = 0;

                        String QUANTITY = "";

                        QUANTITY = "" + giventQty;

                        Log.e("quantity..........", "" + QUANTITY);
                        if (!TempData.OutletCatagoryID.equalsIgnoreCase("17"))
                            UpdatePrice();
                        else {
                            UpdateDistPrice();
                        }
                    } else {
                        Toast.makeText(context, "Please enter correct number format!", Toast.LENGTH_LONG).show();
                        QuantityEd.setText("");

                    }
                }
            }
        });

        ActionBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Log.e("positionclick", position + " size " + itemListContent.size());
                db.excQuery("UPDATE product_boolean SET boolean = " + "'" + "false" + "'" + ", quantity=" + "'" + "" + "'" + " WHERE product_id = " + "'" + itemListContent.get(position).get("product_id") + "'" + " AND outlet_id='" + outlet_ID + "'");
                Toast.makeText(context, "Product has deleted successfully", Toast.LENGTH_LONG).show();

                Log.e("ProductID_FromPosition", itemListContent.get(position).get("product_id"));

                savePreference(itemListContent.get(position).get("product_id"), "0.0");
                savePreference("up" + itemListContent.get(position).get("product_id"), "0");
                itemListContent.remove(position);
                notifyDataSetChanged();
                if (!TempData.OutletCatagoryID.equalsIgnoreCase("17"))
                    UpdatePrice();
                else {
                    UpdateDistPrice();
                }


            }
        });


        return view2;
    }

    public boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void UpdatePrice() {
        ArrayList<ArrayList<String>> ArrCombine = new ArrayList<ArrayList<String>>();
        ArrayList<String> slaveID = new ArrayList<String>();
        ArrayList<String> generalID = new ArrayList<String>();
        ArrayList<String> combineID = new ArrayList<String>();
        ArrayList<String> inList = new ArrayList<String>();
        ArrayList<String> quantity = new ArrayList<String>();

        for (int i = 0; i < itemListContent.size(); i++) {
            inList.add(itemListContent.get(i).get("product_id"));
            Log.e("IDDDDDDDDDDDDDDDDDD:", itemListContent.get(i).get("product_id"));
        }
        for (int i = 0; i < itemListContent.size(); i++) {
            quantity.add(itemListContent.get(i).get("quantity"));
            savePreference(itemListContent.get(i).get("product_id"), itemListContent.get(i).get("quantity"));
            if (!productList.contains(itemListContent.get(i).get("product_id")))
                productList.add(itemListContent.get(i).get("product_id"));
            Log.e("quantity:", itemListContent.get(i).get("quantity"));
        }

        bonusPolicy = 0;
        priceMap.clear();
        policyMap.clear();

        String byPassNow = "YES";
        if (!TargetCustomer.equalsIgnoreCase("0")) {
            //for(int i=0;i<inList.size();i++)
            //priceMap.put(inList.get(i), NGO_price(inList.get(i),quantity.get(i)));
        }

        if (TargetCustomer.equalsIgnoreCase("0") || byPassNow == "YES") {
            InstietuteID = "0";

            for (int i = 0; i < inList.size(); i++) {
                Log.e("PRISING TYPE: ", inList.get(i) + ": " + CheckPriceType(inList.get(i)));
                if (CheckPriceType(inList.get(i)).equalsIgnoreCase("COMBINE"))
                    combineID.add(inList.get(i));
                if (CheckPriceType(inList.get(i)).equalsIgnoreCase("SLAVE"))
                    slaveID.add(inList.get(i));
                if (CheckPriceType(inList.get(i)).equalsIgnoreCase("GENERAL"))
                    generalID.add(inList.get(i));
            }


            Log.e("COMBINE SIZE:", "" + combineID.size());
            String combination_id = "";
            for (int i = 0; i < combineID.size(); i++) {
                Cursor cursor = db.rawQueryCoustom("SELECT combination_id FROM product_combinations WHERE product_id= '" + combineID.get(i) + "' AND combination_id!=0 AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='" + combineID.get(i) + "' AND effective_date<='" + memodate + "')");


                Log.e("OUTER_QUERY:", "SELECT combination_id FROM product_combinations WHERE product_id= '" + combineID.get(i) + "' AND combination_id!=0");
                Log.e("OUTER_QUERY_COUNT", "" + cursor.getCount());
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            combination_id = cursor.getString(0);
                            Cursor cursor1 = db.rawQueryCoustom("SELECT * FROM product_combinations WHERE combination_id= '" + cursor.getString(0) + "'");
                            Log.e("INNER QUERY:", "SELECT * FROM product_combinations WHERE combination_id= '" + cursor.getString(0) + "'");
                            Log.e("INNER QUERY_COUNT:", "" + cursor1.getCount());
                            if (cursor1 != null) {
                                if (cursor1.moveToFirst()) {
                                    ArrayList<String> combinationList = new ArrayList<String>();
                                    do {
                                        //.....combine id added  to list.......
                                        if (!combinationList.contains(cursor1.getString(1)))
                                            combinationList.add(cursor1.getString(1));
                                        Log.e("CombineID", cursor1.getString(1));

                                    } while (cursor1.moveToNext());

                                    if (ArrCombine.size() == 0) {
                                        ArrCombine.add(combinationList);
                                        Log.e("added to combine list", "added");
                                    } else {
                                        if (ArrCombine.contains(combinationList))
                                            Log.e("Duplicate", "True");
                                        else
                                            ArrCombine.add(combinationList);
                                    }
                                }
                            }
                            cursor1.close();

                        } while (cursor.moveToNext());
                    }
                }
                cursor.close();
            }

            Log.e("COMBINE ARRAY:", ArrCombine.toString());
            Log.e("ArrCombine.size()", ArrCombine.size() + "");
            Log.e("QUANTITY ARRAY", quantity.toString());

            for (int i = 0; i < ArrCombine.size(); i++) {
                double QUANTITY = 0;

                ArrayList<String> arrCheck = ArrCombine.get(i);
                Log.e("ArraySize:", arrCheck.size() + "");
              /*  String comProduct = combineID.get(i);
                int index = inList.indexOf(combineID.get(i));*/

                for (int j = 0; j < arrCheck.size(); j++) {

                    Log.e(".............", ".............");
                    Log.e("COMBINE ID:", arrCheck.get(j));
                    if (!getPreference(arrCheck.get(j)).equalsIgnoreCase(""))
                        QUANTITY = QUANTITY + Double.parseDouble(getPreference(arrCheck.get(j)));

                }
                Log.e("Given....QUANTITY", "" + QUANTITY);
                Log.e("INDi....inList", "" + inList.toString());
                Log.e("INDi....arrCheck", "" + arrCheck.toString());
                /// sql query to find combined quantity

                Double Combined_Quantity = 0.0;

                Cursor cursor = db.rawQueryCoustom("SELECT combination_id,min_quantity FROM product_combinations WHERE product_id= '" + arrCheck.get(0) + "' AND combination_id!=0 AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='" + arrCheck.get(0) + "' AND effective_date<='" + memodate + "')");
                Log.e("check1", "SELECT combination_id,min_quantity FROM product_combinations WHERE product_id= '" + arrCheck.get(0) + "' AND combination_id!=0 AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='" + arrCheck.get(0) + "' AND effective_date<='" + memodate + "')");
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
//                            Combined_Quantity = Double.parseDouble(cursor.getString(1));
                            if (QUANTITY >= Double.parseDouble(cursor.getString(1))) {
                                Combined_Quantity = Double.parseDouble(cursor.getString(1));
                            }
                        } while (cursor.moveToNext());
                    }
                }

                cursor.close();

                Log.e("Combined_Quantity: ", "" + Combined_Quantity);

//
                if (QUANTITY >= Combined_Quantity) {


                    Log.e("QUANTITY111", "" + QUANTITY);

                    String ProductCombinationID = "";
//                    String query3="SELECT combination_id FROM product_combinations WHERE product_id='"+ArrCombine.get(i).get(0) +"' AND combination_id!=0  AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='"+ArrCombine.get(i).get(0)+"' AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"')";
                    String query3 = "SELECT combination_id FROM product_combinations WHERE product_id='" + ArrCombine.get(i).get(0) + "' AND combination_id!=0 AND min_quantity<=" + QUANTITY + "  AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='" + ArrCombine.get(i).get(0) + "' AND effective_date<='" + memodate + "')";

                    Cursor cursor3 = db.rawQueryCoustom(query3);
                    Log.e("chcek1", query3 + " " + cursor3.getCount());
                    if (cursor3.moveToFirst()) {
                        do {
                            ProductCombinationID = cursor3.getString(0);
                        } while (cursor3.moveToNext());
                    }
                    cursor3.close();
                    Log.e("ProductCombinationID", "..........." + ProductCombinationID);
                    if (CheckCombineQuantity(QUANTITY, ArrCombine.get(i).get(0), ProductCombinationID)) {
                        for (int j = 0; j < arrCheck.size(); j++) {
                            String combinationID = "";
//                            String query1="SELECT combination_id FROM product_combinations WHERE product_id='"+arrCheck.get(j) +"' AND combination_id!=0 AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='"+arrCheck.get(j)+"' AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"')";
                            String query1 = "SELECT combination_id FROM product_combinations WHERE product_id='" + arrCheck.get(j) + "' AND combination_id!=0 AND min_quantity<=" + QUANTITY + " AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='" + arrCheck.get(j) + "' AND effective_date<='" + memodate + "')  order by min_quantity DESC limit 1";
                            Log.e("query1", query1);
                            Cursor cursor1 = db.rawQueryCoustom(query1);
                            if (cursor1.moveToFirst()) {
                                do {
                                    combinationID = cursor1.getString(0);
                                } while (cursor1.moveToNext());
                            }
                            cursor1.close();
                            Log.e("combinationID", combinationID);
                            String query2 = "SELECT price From product_combinations WHERE product_id='" + arrCheck.get(j) + "' AND combination_id='" + combinationID + "' AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='" + arrCheck.get(j) + "' AND effective_date<='" + memodate + "') AND min_quantity<=" + QUANTITY + "  order by effective_date LIMIT 1";
                            Log.e("COMBINATION PRICE QRY:", query2);
                            Cursor cursor2 = db.rawQueryCoustom(query2);
                            if (cursor2.moveToFirst()) {
                                do {
                                    Log.e("COMBINATION_Price:", cursor2.getString(0));
                                    priceMap.put(arrCheck.get(j), cursor2.getString(0));
                                } while (cursor2.moveToNext());
                            }

                        }
                    } else {
                        slaveID.addAll(ArrCombine.get(i));
                        //ArrCombine.remove(i);
                        Log.e("RETURN1", "return1");
                    }
                } else {
                    slaveID.addAll(ArrCombine.get(i));
                }


            }
        }

        Log.e("..........", "......................");


        for (int i = 0; i < inList.size(); i++)
            Log.e("Selected ID", inList.get(i));


        Log.e("SLIVE SIZE:", "" + slaveID.size());
        for (int i = 0; i < slaveID.size(); i++) {
            if (inList.contains(slaveID.get(i))) {
                int index = inList.indexOf(slaveID.get(i));
                Log.e("quqntity", quantity.get(index));
                double Quantity = Double.parseDouble(quantity.get(index));
                if (!priceMap.containsKey(slaveID.get(i))) {

                    Log.e("product_id" + slaveID.get(i), CheckSlapPrice(Quantity, slaveID.get(i)));
                    priceMap.put(slaveID.get(i), CheckSlapPrice(Quantity, slaveID.get(i)));
                }
            }

        }

        for (int i = 0; i < generalID.size(); i++) {
            if (!priceMap.containsKey(generalID.get(i)))
                priceMap.put(generalID.get(i), General_price(generalID.get(i)));

        }

        BPbonus_product.clear();
        BPbonus_product_temp.clear();
        discountmap.clear();
        discounttype.clear();
        Total_Discount = 0.0;
        Discounttext = "";
        txtBonusPolicy.setText("");
        discount_details.setText(Discounttext);
        root_product_id.clear();
        savePreference("tqty", "0");
        discount.setText("0.0");
        policy_ids.clear();
        checkBonusPolicy();
        if (TempData.editMemo.equalsIgnoreCase("true") && checkEligiblity(TempData.memoNumber) && memoeditable) {
            //BPbonus_product_temp.clear();
            BPbonus_product.clear();
            memoeditable = false;
            getmemoBonusProduct();
            Log.e("spsizetemp", "sjkadn" + BPbonus_product.size());

            setBPProductBonus();
        }


        for (int i = 0; i < inList.size(); i++) {
            Log.e("ID: " + inList.get(i), "Price:" + priceMap.get(inList.get(i)));
            Log.e("...........", "......................");
            Log.e("...........", "..........product_id............" + getPreference("up" + itemListContent.get(i).get("product_id")));


            if (getPreference("up" + itemListContent.get(i).get("product_id")).equalsIgnoreCase("0")) {
                itemListContent.get(i).put("general_price", priceMap.get(inList.get(i)));
                Log.e("vvv", "vvv value=  " + i);
            } else if (getPreference("up" + itemListContent.get(i).get("product_id")).equalsIgnoreCase("")) {
                itemListContent.get(i).put("general_price", priceMap.get(inList.get(i)));
                Log.e("vvv", "vvv value=  " + i);
            } else {
                itemListContent.get(i).put("general_price", getPreference("up" + itemListContent.get(i).get("product_id")));
                Log.e("", "++++++++++ general_price  " + getPreference("up" + itemListContent.get(i).get("product_id")));
                Log.e("", "i value=  " + i);
            }


        }
        sum = 0.0;
        vat = 0.0;

        for (int i = 0; i < itemListContent.size(); i++) {
            HashMap<String, String> map = itemListContent.get(i);

            sum = sum + (Double.parseDouble(map.get("general_price")) * Double.parseDouble(map.get("quantity")));

            vatCalculation(map.get("product_id"), Double.parseDouble(map.get("quantity")), Double.parseDouble(map.get("general_price")), i);
        }
        txtTotal.setText(String.valueOf(roundTwoDecimals(Double.parseDouble("" + sum))));//;//String.valueOf(((ParentActivity) context).roundTwoDecimals(Double.parseDouble(""+sum))));
        vattxt.setText(roundTwoDecimal(vat));
        getDiscount(roundTwoDecimals(Double.parseDouble("" + sum)));

        BonusCalculation(itemListContent);
        notifyDataSetChanged();
    }

    private void getmemoBonusProduct() {
        String Query = "select product_id,quantity,measurement_unit_id,policy_id from order_details where order_number='" + TempData.orderNumber + "' and is_bonus='3'";
        if (MEMO_EDIT) {
            Query = "select product_id,quantity,measurement_unit_id,policy_id from memo_details where memo_number='" + TempData.memoNumber + "' and is_bonus='3'";
        }


        Cursor cursor = db.rawQueryCoustom(Query);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            do {
                String mesurement_unit_id = cursor.getString(2);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("product_id", cursor.getString(0));
                map.put("item", getProductName(cursor.getString(0)));
                map.put("selected_s", "true");
                map.put("policy_id", cursor.getString(3));
                if (mesurement_unit_id != null) {
                    map.put("measurement_unit_id", mesurement_unit_id);
                    map.put("measurement_unit_name", getMeasurementUnitName(mesurement_unit_id));

                }
                Log.e("quantitytask", cursor.getString(1));
                map.put("quantity", cursor.getString(1));
                BPbonus_product.add(map);
                // BPbonus_product_temp.add(map);
                //containbonusProductcal1(map);


            } while (cursor.moveToNext());
        }
        cursor.close();


    }

    private String getProductName(String bonus_product_id) {
        String pname = "";
        String productsquery = "SELECT product_id,product_name FROM  product WHERE product_id='" + bonus_product_id + "' limit 1";
        Log.e("STOCK QUERY:", productsquery);
        Cursor cursor = db.rawQueryCoustom(productsquery, "Bonus_product_Policy8");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            pname = cursor.getString(1);
            cursor.close();
            return pname;
        }
        cursor.close();
        return "unDefine";
    }

    private boolean checkEligiblity(String salesMemoNo) {


        String Query = "select product_id,quantity, measurement_unit_id from order_details where order_number='" + TempData.orderNumber + "' and product_type='0'";
        if (MEMO_EDIT) {
            Query = "select product_id,quantity, measurement_unit_id from memo_details where memo_number='" + salesMemoNo + "' and product_type='0'";
        }

        boolean flag = true;

        Cursor cursor = db.rawQueryCoustom(Query);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            do {

                if (cursor.getDouble(1) != Double.parseDouble(getPreference(cursor.getString(0)))) {

                    return false;
                }

            } while (cursor.moveToNext());

        }
        cursor.close();


        String Query1 = "select count(_id) from order_details where order_number='" + TempData.orderNumber + "' and product_type='0'";
        if (MEMO_EDIT) {
            Query1 = "select count(_id) from memo_details where memo_number='" + salesMemoNo + "' and product_type='0'";
        }

        Cursor cursor1 = db.rawQueryCoustom(Query1);
        Cursor cursor2 = db.rawQueryCoustom("select count(_id) from product_boolean where outlet_id='" + TempData.OutletID + "' and boolean='true'", "cursor2");
        cursor2.moveToFirst();
        cursor1.moveToFirst();
        if (cursor1 != null && cursor2 != null) {
            Log.e("check123", cursor2.getInt(0) + " " + cursor1.getInt(0));
            if (cursor2.getInt(0) != cursor1.getInt(0)) {
                return false;
            }
        }


        return flag;

    }

    private void checkBonusPolicy() {

        Cursor cursor = db.rawQueryCoustom("select beg.policy_id from Bonus_Eligible_Outlets as beo inner Join Bonus_Eligible_Group beg on beg.group_id = beo.group_id where beo.outlet_id='" + outlet_ID + "'", "Bonus_product_Policy1");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                setBonusPolicy(cursor.getString(0));
            } while (cursor.moveToNext());


        }

        Cursor cursor1 = db.rawQueryCoustom("select beoc.policy_id from outlets as o inner Join Bonus_Eligible_Outlet_Categories beoc on beoc.category_id = o.outlet_category_id where o.outlet_id='" + outlet_ID + "'", "Bonus_product_Policy2");

        if (cursor1 != null && cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            do {
                setBonusPolicy(cursor1.getString(0));
            } while (cursor1.moveToNext());

        }
        cursor.close();
        cursor1.close();


    }

    private void setBonusPolicy(String policy_id) {

        Cursor cursor = db.rawQueryCoustom("select * from Policy_Table where policy_id='" + policy_id + "' and start_date<='" + memodate + "' and end_date>='" + memodate + "'", "Bonus_product_Policy3");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            if (isRootProductAvailable(policy_id)) {

                get_Option_products(policy_id);

            } else
                messionFailed(policy_id);

        } else
            messionFailed(policy_id);

        cursor.close();
    }

    private void get_Option_products(String policy_id) {

        Double qty = 0.0;

        inPolicyProductlist.clear();

        Cursor cursor = db.rawQueryCoustom("select distinct root_product_id from policy_root_product where policy_id='" + policy_id + "'", "Bonus_product_Policy4");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {


            for (int i = 0; i < cursor.getCount(); i++) {

                Log.e("policyqty", getPreference(cursor.getString(0)));
                if (Double.parseDouble(getPreference(cursor.getString(0))) > 0) {
                    inPolicyProductlist.add(cursor.getString(0));
                    qty = qty + Double.parseDouble(getPreference(cursor.getString(0)));
                }

                cursor.moveToNext();

            }


            setPolicyType(qty, policy_id);


            //TODO show discount amount

        } else {
            messionFailed(policy_id);
        }
        cursor.close();

    }

    private void setPolicyType(Double qty, String policy_id) {

        Log.e("minqty", qty + "");

        if (qty <= 0)
            return;

        final Cursor cursor1 = db.rawQueryCoustom("select * from policy_product_Option where  min_qty<='" + qty + "' and policy_id ='" + policy_id + "' order by min_qty DESC", "Bonus_product_Policy5");
        cursor1.moveToFirst();
        if (cursor1.getCount() <= 0) {
            messionFailed(policy_id);
            Log.e("Bonus_product_Policy5", cursor1.getCount() + "");
            cursor1.close();
            return;

        }
        double recallQty = 0.0;


        if (cursor1 != null && cursor1.getCount() > 0) {
            recallQty = qty - cursor1.getDouble(cursor1.getColumnIndex("min_qty"));

            for (int i = 0; i < inPolicyProductlist.size(); i++) {


                int policy_type = cursor1.getInt(cursor1.getColumnIndex("policy_type")); //0 stands for only Bonus 1 for only discount 2 discount & bonus 3 discount or bonus

                if (policy_type == 0) {

                    setOnlydiscount(cursor1.getString(cursor1.getColumnIndex("option_id")), cursor1.getString(cursor1.getColumnIndex("discount_type")), cursor1.getString(cursor1.getColumnIndex("discount_amount")), inPolicyProductlist.get(i), cursor1.getString(cursor1.getColumnIndex("policy_id")), recallQty + "");
                } else if (policy_type == 1) {

                    String query = "select * from policy_bonus_product where policy_id ='" + policy_id + "' and option_id ='" + cursor1.getString(cursor1.getColumnIndex("option_id")) + "'";
                    Cursor bonus_product = db.rawQueryCoustom(query, "Bonus_product_Policy99");
                    bonus_product.moveToFirst();
                    if (bonus_product != null && bonus_product.getCount() > 0) {
                        if (bonus_product.getInt(bonus_product.getColumnIndex("relation")) == 0) {

                            HashMap<String, String> map = new HashMap<>();
                            map.put("policy_id", cursor1.getString(cursor1.getColumnIndex("policy_id")));
                            map.put("option_id", cursor1.getString(cursor1.getColumnIndex("option_id")));
                            map.put("policy_type", "5");

                            Cursor c = db.rawQueryCoustom("select policy_type from policy_product_Option_temp where policy_id='" + cursor1.getString(cursor1.getColumnIndex("policy_id")) + "'");
                            c.moveToFirst();
                            Log.e("Cursor", c.getCount() + "");
                            if (c != null && c.getCount() == 0)
                                db.InsertTable(map, "policy_product_Option_temp");
                        }


                    }
                    bonus_product.close();
                    while (Double.parseDouble(qty + "") > 0) {
                        final Cursor cursor12 = db.rawQueryCoustom("select * from policy_product_Option where  min_qty<='" + qty + "' and policy_id ='" + policy_id + "' order by min_qty DESC", "Bonus_product_Policy");
                        Log.e("min_qty_query", "select * from policy_product_Option where  min_qty<='" + qty + "' and policy_id ='" + policy_id + "' order by min_qty DESC");
                        cursor12.moveToFirst();
                        if (cursor12 != null && cursor12.getCount() > 0) {
                            qty = qty - cursor12.getDouble(cursor12.getColumnIndex("min_qty"));
                            setProductBonusdiscountcalculation(cursor12.getString(cursor12.getColumnIndex("bonus_product_id")), cursor12.getString(cursor12.getColumnIndex("bonus_qty")), cursor12.getString(cursor12.getColumnIndex("policy_id")), recallQty + "", cursor12.getString(cursor12.getColumnIndex("measurement_unit_id")), cursor12.getString(cursor12.getColumnIndex("option_id")));
                            setProductBonusdiscount(cursor12.getString(cursor12.getColumnIndex("bonus_product_id")), cursor12.getString(cursor12.getColumnIndex("bonus_qty")), cursor12.getString(cursor12.getColumnIndex("policy_id")), recallQty + "", cursor12.getString(cursor12.getColumnIndex("measurement_unit_id")), cursor12.getString(cursor12.getColumnIndex("option_id")));


                        } else
                            break;

                        cursor12.close();
                    }
                } else if (policy_type == 2) {
                    setOnlydiscount(cursor1.getString(cursor1.getColumnIndex("option_id")), cursor1.getString(cursor1.getColumnIndex("discount_type")), cursor1.getString(cursor1.getColumnIndex("discount_amount")), inPolicyProductlist.get(i), cursor1.getString(cursor1.getColumnIndex("policy_id")), recallQty + "");
                    String query = "select * from policy_bonus_product where policy_id ='" + policy_id + "' and option_id ='" + cursor1.getString(cursor1.getColumnIndex("option_id")) + "'";
                    Cursor bonus_product = db.rawQueryCoustom(query, "Bonus_product_Policy99");
                    bonus_product.moveToFirst();
                    if (bonus_product != null && bonus_product.getCount() > 0) {
                        if (bonus_product.getInt(bonus_product.getColumnIndex("relation")) == 0) {

                            HashMap<String, String> map = new HashMap<>();
                            map.put("policy_id", cursor1.getString(cursor1.getColumnIndex("policy_id")));
                            map.put("option_id", cursor1.getString(cursor1.getColumnIndex("option_id")));
                            map.put("policy_type", "5");

                            Cursor c = db.rawQueryCoustom("select policy_type from policy_product_Option_temp where policy_id='" + cursor1.getString(cursor1.getColumnIndex("policy_id")) + "'");
                            c.moveToFirst();
                            Log.e("Cursor", c.getCount() + "");
                            if (c != null && c.getCount() == 0)
                                db.InsertTable(map, "policy_product_Option_temp");
                        }


                    }
                    bonus_product.close();
                    while (Double.parseDouble(qty + "") > 0) {
                        final Cursor cursor12 = db.rawQueryCoustom("select * from policy_product_Option where  min_qty<='" + qty + "' and policy_id ='" + policy_id + "' order by min_qty DESC", "Bonus_product_Policy");
                        Log.e("min_qty_query", "select * from policy_product_Option where  min_qty<='" + qty + "' and policy_id ='" + policy_id + "' order by min_qty DESC");
                        cursor12.moveToFirst();
                        if (cursor12 != null && cursor12.getCount() > 0) {
                            qty = qty - cursor12.getDouble(cursor12.getColumnIndex("min_qty"));
                            setProductBonusdiscountcalculation(cursor12.getString(cursor12.getColumnIndex("bonus_product_id")), cursor12.getString(cursor12.getColumnIndex("bonus_qty")), cursor12.getString(cursor12.getColumnIndex("policy_id")), recallQty + "", cursor12.getString(cursor12.getColumnIndex("measurement_unit_id")), cursor12.getString(cursor12.getColumnIndex("option_id")));
                            setProductBonusdiscount(cursor12.getString(cursor12.getColumnIndex("bonus_product_id")), cursor12.getString(cursor12.getColumnIndex("bonus_qty")), cursor12.getString(cursor12.getColumnIndex("policy_id")), recallQty + "", cursor12.getString(cursor12.getColumnIndex("measurement_unit_id")), cursor12.getString(cursor12.getColumnIndex("option_id")));
                        } else
                            break;

                        cursor12.close();

                    }


                } else if (policy_type == 3) {
                    if (TempData.editMemo.equalsIgnoreCase("true")) {
                        policy_offer = getPolicy_offer(inPolicyProductlist.get(i));
                    }
                    // messionFailed(cursor1.getString(cursor1.getColumnIndex("policy_id")));
                    HashMap<String, String> map = new HashMap<>();
                    map.put("policy_id", cursor1.getString(cursor1.getColumnIndex("policy_id")));
                    map.put("option_id", cursor1.getString(cursor1.getColumnIndex("option_id")));
                    map.put("policy_type", policy_offer + "");
                    Cursor c = db.rawQueryCoustom("select policy_type from policy_product_Option_temp where policy_id='" + cursor1.getString(cursor1.getColumnIndex("policy_id")) + "'");
                    c.moveToFirst();
                    Log.e("Cursor", c.getCount() + "");
                    if (c != null && c.getCount() == 0)
                        db.InsertTable(map, "policy_product_Option_temp");
                    else if (c.getCount() > 0) {
                        db.excQuery("update policy_product_Option_temp set option_id='" + cursor1.getString(cursor1.getColumnIndex("option_id")) + "' where policy_id='" + cursor1.getString(cursor1.getColumnIndex("policy_id")) + "'");
                        policy_offer = c.getInt(c.getColumnIndex("policy_type"));
                        Log.e("Cursor", policy_offer + "");
                    }
                    c.close();
                    //bonusPolicy = 1;
                    discountoffer.put(inPolicyProductlist.get(i), policy_offer + "");


                    if (policy_offer == 0) {
                        setOnlydiscount(cursor1.getString(cursor1.getColumnIndex("option_id")), cursor1.getString(cursor1.getColumnIndex("discount_type")), cursor1.getString(cursor1.getColumnIndex("discount_amount")), inPolicyProductlist.get(i), cursor1.getString(cursor1.getColumnIndex("policy_id")), recallQty + "");
                        Log.e("setOnlydiscount", "setOnlydiscount");

                    } else {
                        while (Double.parseDouble(qty + "") > 0) {
                            final Cursor cursor12 = db.rawQueryCoustom("select * from policy_product_Option where  min_qty<='" + qty + "' and policy_id ='" + policy_id + "' order by min_qty DESC", "Bonus_product_Policy");

                            cursor12.moveToFirst();
                            if (cursor12 != null && cursor12.getCount() > 0) {
                                qty = qty - cursor12.getDouble(cursor12.getColumnIndex("min_qty"));
                                setProductBonusdiscountcalculation(cursor12.getString(cursor12.getColumnIndex("bonus_product_id")), cursor12.getString(cursor12.getColumnIndex("bonus_qty")), cursor12.getString(cursor12.getColumnIndex("policy_id")), recallQty + "", cursor12.getString(cursor12.getColumnIndex("measurement_unit_id")), cursor12.getString(cursor12.getColumnIndex("option_id")));
                                setProductBonusdiscount(cursor12.getString(cursor12.getColumnIndex("bonus_product_id")), cursor12.getString(cursor12.getColumnIndex("bonus_qty")), cursor12.getString(cursor12.getColumnIndex("policy_id")), recallQty + "", cursor12.getString(cursor12.getColumnIndex("measurement_unit_id")), cursor12.getString(cursor12.getColumnIndex("option_id")));


                            } else
                                break;

                            cursor12.close();

                        }
                        Log.e("setOnlydiscount", "setBonusdiscount");
                    }
                }


            }

            cursor1.close();
        }
        cursor1.close();
        for (int i = 0; i < inPolicyProductlist.size(); i++) {

            if (discountmap.get(inPolicyProductlist.get(i)) != null) {
                Double discount = Double.parseDouble(discountmap.get(inPolicyProductlist.get(i))) * Double.parseDouble(getPreference(inPolicyProductlist.get(i)));
                Discounttext = Discounttext + getDiscountText(inPolicyProductlist.get(i), roundTwoDecimal(discount));
                Total_Discount = Total_Discount + discount;
            }


        }
        if (Total_Discount > 0.0) {
            discount.setText(roundTwoDecimal(Total_Discount) + "");
            discount.setVisibility(View.VISIBLE);
            discount_details.setText(Discounttext.substring(0, Discounttext.length() - 1));
        } else
            discount.setVisibility(View.GONE);


        BPbonus_product_t.clear();
        BPbonus_product_t.addAll(BPbonus_product);
        for (int i = 0; i < BPbonus_product.size(); i++) {
            Log.e("valueofbp", BPbonus_product.get(i).toString());
        }

        setBPProductBonus();


    }

    private void setProductBonusdiscountcalculation(String bonus_product_id, String bonus_qty, String policy_id, String recall_Qty, String mesurement_unit_id, String option_id) {

        //bonus_qty = ConvertToDispencer(bonus_product_id,bonus_qty,mesurement_unit_id);

      /* if (!policy_ids.contains(policy_id))
        policy_ids.add(policy_id);
       else
           return;*/

        String query = "select * from policy_bonus_product where policy_id ='" + policy_id + "' and option_id ='" + option_id + "'";

      /*  if (!getPreference("p=" + policy_id + "o=").equalsIgnoreCase("0")) {
            query = "select * from policy_bonus_product where policy_id ='" + policy_id + "' and option_id ='" + option_id + "' and bonus_product_id='" + getPreference("p=" + policy_id + "o=") + "'";
        }*/

        Cursor bonus_product = db.rawQueryCoustom(query, "Bonus_product_Policy998");

        bonus_product.moveToFirst();
        if (bonus_product != null && bonus_product.getCount() > 0) {

            do {


                bonus_product_id = bonus_product.getString(bonus_product.getColumnIndex("bonus_product_id"));
                bonus_qty = bonus_product.getString(bonus_product.getColumnIndex("bonus_qty"));
                mesurement_unit_id = bonus_product.getString(bonus_product.getColumnIndex("measurement_unit_id"));

                double stockQty = 0;
                if (bonus_product_id.equalsIgnoreCase("null")) {
                    Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                    return;
                }

                String productsquery = "SELECT product_id,product_name FROM  product WHERE product_id='" + bonus_product_id + "' limit 1";
                Log.e("STOCK QUERY:", productsquery);
                Cursor cursor = db.rawQueryCoustom(productsquery, "Bonus_product_Policy8");
                cursor.moveToFirst();
                if (cursor != null && cursor.getCount() > 0) {
                    HashMap<String, String> map = new HashMap<String, String>();


                    map.put("product_id", cursor.getString(0));
                    map.put("policy_id", policy_id);
                    map.put("item", cursor.getString(1));
                    map.put("selected_s", "true");
                    map.put("relation", bonus_product.getString(bonus_product.getColumnIndex("relation")));
                    Log.e("product12" + cursor.getString(0), policy_id);
                    policy_map.put(cursor.getString(0), policy_id);
                    if (mesurement_unit_id != null) {
                        map.put("measurement_unit_id", mesurement_unit_id);
                        map.put("measurement_unit_name", getMeasurementUnitName(mesurement_unit_id));

                    }
                    map.put("quantity", bonus_qty);
                    map.put("min_qty", recall_Qty);

                    // if (bonus_product.getInt(bonus_product.getColumnIndex("relation")) != 0)
                    checkBonusproductscalculation(map);
                }


                if (bonus_product.getInt(bonus_product.getColumnIndex("relation")) == 0) {


                }

            } while (bonus_product.moveToNext());


        }

    }

    private void checkBonusproductscalculation(HashMap<String, String> map) {
        Log.e("mapDetailstemp", map.toString());


        if (BPbonus_product_temp.size() == 0) {
            Log.e("size_Zero", map.toString());
            BPbonus_product_temp.add(map);
            return;
        } else {
            for (int i = 0; i < BPbonus_product_temp.size(); i++) {
                Log.e("mapdental", map.toString() + " i=" + i);
                Log.e("size_product_id", BPbonus_product_temp.get(i).get("product_id") + "  size " + BPbonus_product_temp.size() + "  " + BPbonus_product_temp.get(i).get("measurement_unit_id") + " " + map.get("quantity"));
                if (BPbonus_product_temp.get(i).get("product_id").equalsIgnoreCase(map.get("product_id")) && BPbonus_product_temp.get(i).get("measurement_unit_id").equalsIgnoreCase(map.get("measurement_unit_id")) && BPbonus_product_temp.get(i).get("policy_id").equalsIgnoreCase(map.get("policy_id"))) {
                    //Log.e("mapdental",map.toString());
                    Log.e("map123", map.get("product_id") + "  BPbonus_product_temp  " + Double.parseDouble(map.get("quantity")) + " " + BPbonus_product_temp.get(i).get("quantity") + " i=" + i);
                    if (BPbonus_product_temp.get(i).get("quantity") != null) {
                        Log.e("map1234", map.get("product_id") + " BPbonus_product_temp  " + (Double.parseDouble(BPbonus_product_temp.get(i).get("quantity")) + Double.parseDouble(map.get("quantity"))) + " i=" + i);
                        BPbonus_product_temp.get(i).put("quantity", (Double.parseDouble(BPbonus_product_temp.get(i).get("quantity")) + Double.parseDouble(map.get("quantity"))) + "");
                    }
                    return;

                } else {
                    if (containbonusProductcal(map)) {
                        BPbonus_product_temp.add(map);
                        return;
                    }

                    Log.e("not_match", map.toString());

                }

            }


        }


    }

    private boolean containbonusProductcal(HashMap<String, String> map) {
        for (int i = 0; i < BPbonus_product_temp.size(); i++) {
            Log.e("size_product_id", BPbonus_product_temp.get(i).get("product_id") + "  size " + BPbonus_product_temp.size() + "  " + BPbonus_product_temp.get(i).get("measurement_unit_id"));
            if (BPbonus_product_temp.get(i).get("product_id").equalsIgnoreCase(map.get("product_id")) && BPbonus_product_temp.get(i).get("measurement_unit_id").equalsIgnoreCase(map.get("measurement_unit_id")) && BPbonus_product_temp.get(i).get("policy_id").equalsIgnoreCase(map.get("policy_id"))) {

                // Log.e("map1", "  BPbonus_product  " + BPbonus_product.get(i).toString());
                //Log.e("map12", "  BPbonus_product  " + (Double.parseDouble(BPbonus_product.get(i).get("quantity")) + Double.parseDouble(map.get("quantity"))));
                //  BPbonus_product.get(i).put("quantity", (Double.parseDouble(BPbonus_product.get(i).get("quantity")) + Double.parseDouble(map.get("quantity"))) + "");
                return false;

            }
        }
        return true;
    }

    private int getPolicy_offer(String Product_id) {
        int policy_type = 0;
        String uid = TempData.orderNumber;
        String column = "order_number";
        String Table_name = "order_details";

        if (MEMO_EDIT) {
            Table_name = "memo_details";
            uid = TempData.memoNumber;
            column = "memo_number";
        }

        Cursor cursor = db.rawQueryCoustom("select policy_type from  " + Table_name + " where product_id='" + Product_id + "' and " + column + "='" + uid + "'", "getPolicy_offer");

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            policy_type = cursor.getInt(0);
        }

        return policy_type;
    }

    private void setBPProductBonus() {

        TempData.BonusShowList.clear();
        TempData.BonusShowList.addAll(BPbonus_product);
        String bonusString = "";
        for (int i = 0; i < BPbonus_product.size(); i++) {
            bonusString = bonusString + BPbonus_product.get(i).get("item") + "(" + BPbonus_product.get(i).get("quantity") + " " + BPbonus_product.get(i).get("measurement_unit_name") + ")";

            HashMap<String, String> product_map = new HashMap<String, String>();

            product_map.put("bonus_name", BPbonus_product.get(i).get("item"));
            product_map.put("quantity", BPbonus_product.get(i).get("quantity"));

//            BPbonus_productItemList.add(product_map);


            if (i != BPbonus_product.size() - 1)
                bonusString = bonusString + ", ";
        }


        if (bonusString.length() > 0) {
            Log.e("bonusStringT", bonusString);
            txtBonusPolicy.setVisibility(View.VISIBLE);
            txtBonusPolicy.setText(bonusString);

        } else
            txtBonusPolicy.setText("");
    }

    private String getDiscountText(String s, String discount) {
        String line = "";
        String productsquery = "SELECT product_id,product_name FROM  product WHERE product_id='" + s + "' limit 1";
        Log.e("STOCK QUERY:", productsquery);
        Cursor cursor = db.rawQueryCoustom(productsquery, "Bonus_product_Policy8");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            line = cursor.getString(1) + "(" + discount + "),";
        }

        return line;
    }


    private void messionFailed(String policy_id) {
        db.excQuery("delete from policy_product_Option_temp where policy_id ='" + policy_id + "'");

    }

    private void setProductBonusdiscount(String bonus_product_id, String bonus_qty, String policy_id, String recall_Qty, String mesurement_unit_id, String option_id) {

        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc(); //use for CursorWindowAllocationException

        double tempbqty = 0.00;
        String query = "select * from policy_bonus_product where policy_id ='" + policy_id + "' and option_id ='" + option_id + "'";


        Cursor bonus_product = db.rawQueryCoustom(query, "Bonus_product_Policy998");

        bonus_product.moveToFirst();
        if (bonus_product != null && bonus_product.getCount() > 0) {

            do {


                bonus_product_id = bonus_product.getString(bonus_product.getColumnIndex("bonus_product_id"));
                bonus_qty = bonus_product.getString(bonus_product.getColumnIndex("bonus_qty"));
                mesurement_unit_id = bonus_product.getString(bonus_product.getColumnIndex("measurement_unit_id"));

                double stockQty = 0;
                if (bonus_product_id.equalsIgnoreCase("null")) {
                    Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                    continue;
                }
                if (TempData.editMemo.equalsIgnoreCase("true") && memoeditable && checkEligiblity(TempData.memoNumber)) {
                    if (getmemoqty(bonus_product_id) == 0) {
                        Log.e("no_no", getmemoqty(bonus_product_id) + "");
                        continue;
                    }
                }

                String productsquery = "SELECT product_id,product_name FROM  product WHERE product_id='" + bonus_product_id + "' limit 1";
                Log.e("STOCK QUERY:", productsquery);
                Cursor cursor = db.rawQueryCoustom(productsquery, "Bonus_product_Policy8");
                cursor.moveToFirst();


                if (cursor != null && cursor.getCount() > 0) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("product_id", cursor.getString(0));
                    map.put("item", cursor.getString(1));
                    map.put("selected_s", "true");
                    map.put("policy_id", policy_id);
                    map.put("relation", bonus_product.getString(bonus_product.getColumnIndex("relation")));

                    Log.e("policy_idpolicy_id", policy_id + "tanvir");
                    if (mesurement_unit_id != null) {
                        map.put("measurement_unit_id", mesurement_unit_id);
                        map.put("measurement_unit_name", getMeasurementUnitName(mesurement_unit_id));

                    }
                    map.put("quantity", bonus_qty);
                    map.put("min_qty", recall_Qty);
                    // if (bonus_product.getInt(bonus_product.getColumnIndex("relation")) != 0)
                    checkBonusproducts(map);


                }


                if (bonus_product.getInt(bonus_product.getColumnIndex("relation")) == 0) {

                    savePreference("p=" + policy_id + "o=", bonus_product_id);
                    break;
                }

              /*  Log.e("qty6666",policy_id+" "+getqtywithpolicy(policy_id)+" "+bonus_qty);
                if (bonus_product.getInt(bonus_product.getColumnIndex("relation")) == 0 && getqtywithpolicy(policy_id)>=getTotalQty(bonus_product_id,policy_id)) {

                    break;
                }*/


            } while (bonus_product.moveToNext());


        }
        bonus_product.close();
    }

    private boolean checkpolicyStock(String policy_id, String option_id) {

        double stockQty, tempbqty = 0.00;
        String query = "select * from policy_bonus_product where policy_id ='" + policy_id + "' and option_id ='" + option_id + "'";

        String bonus_product_id, bonus_qty, mesurement_unit_id;
        Cursor bonus_product = db.rawQueryCoustom(query, "Bonus_product_Policy998");

        bonus_product.moveToFirst();
        if (bonus_product != null && bonus_product.getCount() > 0) {

            do {


                bonus_product_id = bonus_product.getString(bonus_product.getColumnIndex("bonus_product_id"));
                bonus_qty = bonus_product.getString(bonus_product.getColumnIndex("bonus_qty"));
                mesurement_unit_id = bonus_product.getString(bonus_product.getColumnIndex("measurement_unit_id"));

                String stockQtyquey = "SELECT SUM(quantity) as quantity FROM  van_stocks WHERE product_id='" + bonus_product_id + "'";
                Log.e("STOCK_QUERY:", stockQtyquey);
                Cursor cursor1 = db.rawQueryCoustom(stockQtyquey, "Bonus_product_Policy7");
                if (cursor1 != null) {
                    if (cursor1.moveToFirst()) {
                        if (!TempData.editMemo.equalsIgnoreCase("true"))
                            stockQty = cursor1.getDouble(0) - Double.parseDouble(getPreference(bonus_product_id));
                        else {
                            stockQty = cursor1.getDouble(0);
                        }

                        double bonusqty = Double.parseDouble(ConvertToDispencer(bonus_product_id, bonus_qty, mesurement_unit_id));
                        Log.e("stockQty", stockQty + " no " + cursor1.getDouble(0) + " yes" + getPreference(bonus_product_id) + "bonus_product_id");
                        if (TempData.editMemo.equalsIgnoreCase("true")) {
                            stockQty = stockQty + Double.parseDouble(ConvertToDispencer(bonus_product_id, getmemoqtywithpolicy(policy_id) + "", mesurement_unit_id));
                            Log.e("stockQtyfinal", stockQty + "" + getProductName(bonus_product_id));
                            Log.e("tanvirccccc", getmemoqty(bonus_product_id) + " " + getProductName(bonus_product_id));
                            if (getmemoqty(bonus_product_id) > 0)
                                bonusqty = getmemoqty(bonus_product_id);

                            tempbqty = tempbqty + Double.parseDouble(ConvertToAnyUnitFromDispencer(bonus_product_id, getmemoqty(bonus_product_id) + "", mesurement_unit_id));
                            bonus_qty = bonusqty + "";
                        }

                        Log.e("stockQty", stockQty + "");
                        String productsquery = "SELECT product_id,product_name FROM  product WHERE product_id='" + bonus_product_id + "' limit 1";
                        Log.e("STOCK QUERY:", productsquery);
                        Cursor cursor = db.rawQueryCoustom(productsquery, "Bonus_product_Policy8");
                        cursor.moveToFirst();
                        Log.e("selected_s", ConvertToDispencer(bonus_product_id, bonus_qty, mesurement_unit_id));
                        if (stockQty < bonusqty) {
                            return true;
                        }

                    }

                }


            } while (bonus_product.moveToNext());
        }

        return false;
    }

    private double getmemoqtywithpolicy(String policy_id) {

        Cursor cursor = db.rawQueryCoustom("select Sum(quantity) from memo_details where memo_number='" + TempData.memoNumber + "' and policy_id ='" + policy_id + "' and is_bonus='3'", "getmemoqty");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            return cursor.getDouble(0);
        }
        cursor.close();
        return 0.0;
    }

    private String getInBPqty(String bonus_product_id, String policy_id) {

        double qty = 0.00;
        for (int i = 0; i < BPbonus_product.size(); i++) {
            if (BPbonus_product.get(i).get("policy_id").equalsIgnoreCase(policy_id) && BPbonus_product.get(i).get("product_id").equalsIgnoreCase(bonus_product_id)) {
                qty = qty + Double.parseDouble(BPbonus_product.get(i).get("quantity"));
            }

        }
        Log.e("getInBPqty", qty + "");
        return qty + "";
    }

    private String ConvertToDispencer(String product_id, String quantity, String measurement_unit_id) {
        if (measurement_unit_id == null || measurement_unit_id.equalsIgnoreCase("null")) {
            return quantity;
        }
        Double quantityd = Double.parseDouble(quantity);
        Double measurement_unit_idd = Double.parseDouble(measurement_unit_id);

        if (measurement_unit_idd == 7) {
            return quantity;
        } else {

            Cursor cursor = db.rawQueryCoustom("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='" + measurement_unit_id + "'");

            cursor.moveToFirst();

            if (cursor.getCount() > 0 && cursor != null) {

                quantityd = quantityd * cursor.getDouble(0);
                cursor.close();
                Log.e("quantityd*", quantityd + "");
                Cursor cursor1 = db.rawQueryCoustom("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='7'");

                cursor1.moveToFirst();

                if (cursor1.getCount() > 0 && cursor1 != null) {

                    quantityd = quantityd / cursor1.getDouble(0);
                    quantityd = Double.parseDouble(SpecialroundTwoDecimals(quantityd + ""));
                    Log.e("quantityd/", quantityd + "");
                }
                cursor1.close();


            } else {
                Cursor cursor1 = db.rawQueryCoustom("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='7'");

                cursor1.moveToFirst();

                if (cursor1.getCount() > 0 && cursor1 != null) {

                    quantityd = quantityd / cursor1.getDouble(0);
                    quantityd = Double.parseDouble(SpecialroundTwoDecimals(quantityd + ""));
                    Log.e("quantityd/", quantityd + "");
                }
                cursor1.close();
            }

            cursor.close();

            Log.e("quantityd", quantityd + " pn" + getProductName(product_id));
            return quantityd + "";

        }


    }

    public static String SpecialroundTwoDecimals(String number) {
        String totalNumber = "0.00";
        String remainder = "0.00";
        String firstnumber = "0.00";
        if (Double.parseDouble(number) != Math.floor(Double.parseDouble(number))) {
            remainder = number.replace(".", ",").split(",")[1];
            firstnumber = number.replace(".", ",").split(",")[0];
            if (remainder.length() > 2) {
                remainder = remainder.substring(0, 2);
            }
            return firstnumber + "." + remainder;
        } else {
            return number;
        }


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

            Cursor cursor = db.rawQueryCoustom("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='" + measurement_unit_id + "'");

            cursor.moveToFirst();

            if (cursor.getCount() > 0 && cursor != null) {

                quantityd = quantityd * cursor.getDouble(0);
                cursor.close();
                Log.e("quantityd*", quantityd + "");
                Cursor cursor1 = db.rawQueryCoustom("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='7'");

                cursor1.moveToFirst();

                if (cursor1.getCount() > 0 && cursor1 != null) {

                    quantityd = quantityd / cursor1.getDouble(0);
                    quantityd = Double.parseDouble(SpecialroundTwoDecimals(quantityd + ""));
                    Log.e("quantityd/", quantityd + "");
                }
                cursor1.close();


            } else {
                Cursor cursor1 = db.rawQueryCoustom("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='7'");

                cursor1.moveToFirst();

                if (cursor1.getCount() > 0 && cursor1 != null) {

                    quantityd = quantityd * cursor1.getDouble(0);
                    quantityd = Double.parseDouble(SpecialroundTwoDecimals(quantityd + ""));
                    Log.e("quantityd/", quantityd + "");
                }
                cursor1.close();
            }

            cursor.close();

            Log.e("quantityd", quantityd + " pn" + getProductName(product_id));
            return quantityd + "";

        }


    }

    private String getMeasurementUnitName(String mesurement_unit_id) {
        Cursor c = db.rawQueryCoustom("select unit_name from unit where unit_id='" + mesurement_unit_id + "'");
        c.moveToFirst();
        if (c.getCount() > 0) {
            return c.getString(0);
        }
        return "Not specified";
    }

    private void checkBonusproducts(HashMap<String, String> map) {
        Log.e("mapDetails5555", map.toString());
        Log.e("mapDetails5555", map.get("policy_id"));
        for (int i = 0; i < BPbonus_product.size(); i++) {
            Log.e("all_print", BPbonus_product.get(i).toString());
        }

        if (BPbonus_product.size() == 0) {
            Log.e("size_Zero", map.toString());
            BPbonus_product.add(map);
            return;
        } else {
            for (int i = 0; i < BPbonus_product.size(); i++) {
                Log.e("BPbonus_product", BPbonus_product.get(i).toString() + " i=" + i);
                Log.e("size_product_id", BPbonus_product.get(i).get("product_id") + "  size " + BPbonus_product.size() + "  " + BPbonus_product.get(i).get("measurement_unit_id") + " " + map.get("quantity"));
                if (BPbonus_product.get(i).get("product_id").equalsIgnoreCase(map.get("product_id")) && BPbonus_product.get(i).get("measurement_unit_id").equalsIgnoreCase(map.get("measurement_unit_id")) && BPbonus_product.get(i).get("policy_id").equalsIgnoreCase(map.get("policy_id"))) {
                    //Log.e("mapdental",map.toString());
                    Log.e("map123", map.get("product_id") + "  BPbonus_product  " + Double.parseDouble(map.get("quantity")) + " " + BPbonus_product.get(i).get("quantity") + " i=" + i);
                    if (BPbonus_product.get(i).get("quantity") != null) {
                        Log.e("map1234", map.get("product_id") + " BPbonus_product  " + (Double.parseDouble(BPbonus_product.get(i).get("quantity")) + Double.parseDouble(map.get("quantity"))) + " i=" + i);
                        BPbonus_product.get(i).put("quantity", (Double.parseDouble(BPbonus_product.get(i).get("quantity")) + Double.parseDouble(map.get("quantity"))) + "");
                    }
                    return;

                } else {
                    Log.e("not_match", map.toString());
                    if (containbonusProduct(map)) {
                        BPbonus_product.add(map);

                        return;
                    }


                }

            }


        }


    }

    private boolean containbonusProduct(HashMap<String, String> map) {
        Log.e("containbonusProduct", map.toString());
        for (int i = 0; i < BPbonus_product.size(); i++) {
            Log.e("BPbonus_product", BPbonus_product.get(i).toString() + " i=" + i);
            Log.e("size_product_id", BPbonus_product.get(i).get("product_id") + "  size " + BPbonus_product.size() + "  " + BPbonus_product.get(i).get("measurement_unit_id"));
            if (BPbonus_product.get(i).get("product_id").equalsIgnoreCase(map.get("product_id")) && BPbonus_product.get(i).get("measurement_unit_id").equalsIgnoreCase(map.get("measurement_unit_id")) && BPbonus_product.get(i).get("policy_id").equalsIgnoreCase(map.get("policy_id"))) {

                // Log.e("map1", "  BPbonus_product  " + BPbonus_product.get(i).toString());
                //Log.e("map12", "  BPbonus_product  " + (Double.parseDouble(BPbonus_product.get(i).get("quantity")) + Double.parseDouble(map.get("quantity"))));
                //  BPbonus_product.get(i).put("quantity", (Double.parseDouble(BPbonus_product.get(i).get("quantity")) + Double.parseDouble(map.get("quantity"))) + "");
                return false;

            }
        }
        return true;
    }

    private boolean unitExistwithproduct(HashMap<String, String> map) {
        boolean ttt = true;

        for (int i = 0; i < BPbonus_product.size(); i++) {
            Log.e("size_product_i1d", BPbonus_product.get(i).get("product_id") + "  size " + BPbonus_product.size() + "  " + BPbonus_product.get(i).get("measurement_unit_id") + " map  measurement_unit_id" + map.get("measurement_unit_id") + " product_id " + map.get("product_id"));
            if (BPbonus_product.get(i).get("product_id").equalsIgnoreCase(map.get("product_id")) && BPbonus_product.get(i).get("measurement_unit_id").equalsIgnoreCase(map.get("measurement_unit_id"))) {
                ttt = false;
                return ttt;
            }
        }
        return ttt;
    }

    private double getmemoqty(String bonus_product_id) {

        Cursor cursor = db.rawQueryCoustom("select quantity from order_details where order_number='" + TempData.orderNumber + "' and product_id ='" + bonus_product_id + "' and is_bonus='3'", "getmemoqty");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            return cursor.getDouble(0);
        }
        return 0.0;
    }

    private void setOnlydiscount(String option_id, String discount_type, String discount_amount, String option_product_id, String policy_id, String recall_Qty) {

        //discount_type 0 % and 1 amount
        Cursor cursor = db.rawQueryCoustom("select pc.price from product_combinations as pc inner join policy_option_price_slab as pops on pops.slab_id = pc.slab_id where pops.policy_product_option_id='" + option_id + "' and pops.option_product_id='" + option_product_id + "' and pc.product_id = '" + option_product_id + "'", "Bonus_product_Policy6");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            Double price, discount = 0.0;

            Log.e("discount_type", discount_type);
            discounttype.put(option_product_id, discount_type);
            if (Integer.parseInt(discount_type) == 0) {

                discount = (Double.parseDouble(cursor.getString(0)) / 100.00) * Double.parseDouble(discount_amount);
                price = Double.parseDouble(cursor.getString(0)) - discount;
            } else {
                discount = Double.parseDouble(discount_amount);
                price = Double.parseDouble(cursor.getString(0)) - discount;
            }

            Log.e("discountmap", discount + "temp+ price =" + cursor.getString(0) + " discount_amount" + discount_amount);
            savePreference("up" + option_product_id, "0");
            priceMap.put(option_product_id, cursor.getString(0) + ""); //51482
            discountmap.put(option_product_id, roundTwoDecimal(discount) + "");


        }
        cursor.close();

    }

    private boolean isRootProductAvailable(String policy_id) {
        boolean allproductIn = false;
        Log.e("Size", root_product_id.size() + "");
        Log.e("productList", productList.toString() + "");

        Cursor cursor = db.rawQueryCoustom("select * from policy_root_product where policy_id='" + policy_id + "'", "Bonus_product_Policy10");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            for (int i = 0; i < cursor.getCount(); i++) {
                if (!root_product_id.contains(cursor.getString(cursor.getColumnIndex("root_product_id")))) {
                    root_product_id.add(cursor.getString(cursor.getColumnIndex("root_product_id")));
                    Log.e("Size2", false + " " + cursor.getString(cursor.getColumnIndex("root_product_id")));
                } else {
                    Log.e("Size1", false + " " + cursor.getString(cursor.getColumnIndex("root_product_id")));
                    return false;

                }
                if (productList.contains(cursor.getString(cursor.getColumnIndex("root_product_id")))) {
                    allproductIn = true;
                    return allproductIn;
                }

                cursor.moveToNext();

            }


        }
        cursor.close();
        return allproductIn;
    }

    private void vatCalculation(String product_id, Double quantity, Double price, int pos) {
        Double vatprice = 0.0, vatofvat = 0.0;
        Cursor c = db.rawQueryCoustom("select vat from product_price where product_id='" + product_id + "' and vat!='null'");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            vatprice = (price * 100) / (100 + c.getDouble(0));
            vatofvat = (price - vatprice);
            itemListContent.get(pos).put("vat", c.getDouble(0) + "");
            vat = vat + (vatofvat * quantity);
        }
        c.close();
    }

    private void getDiscount(String s) {
        int distype = 0;
        discountp = 0.0;
        Cursor cursor = db.rawQueryCoustom("select discount_percent,discount_type from discounts where memo_value <=" + s + " and date_from <= '" + memodate + "' and date_to>='" + memodate + "' order by memo_value DESC limit 1");
        Log.e("DisQuery", "select discount_percent,discount_type from discounts where memo_value <=" + s + " and date_from <= '" + memodate + "' and date_to>='" + memodate + "' order by memo_value DESC limit 1");
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
        Total_Discount = Total_Discount + discount;
        this.discount.setText(roundTwoDecimals(Total_Discount));

        subt.setText(roundTwoDecimals(memoValue - Total_Discount));


    }

    public ArrayList<HashMap<String, String>> getAdapterHashMapList() {
        return returnContent;
    }

    public ArrayList<HashMap<String, String>> getBonusMapList() {
        return Bonus;
    }


    public void BonusCalculation(ArrayList<HashMap<String, String>> itemListContent) {
        TextView txtBonus = (TextView) ((Activity) context).findViewById(R.id.txtBonus);
        txtBonus.setText("");

        Bonus.clear();

        for (int i = 0; i < itemListContent.size(); i++) {
            String givenQty = itemListContent.get(i).get("quantity");
            String ProductID = itemListContent.get(i).get("product_id");
            Log.e("BONUS_QUANTITY------", givenQty);
            Log.e("PRODUCT_ID------", ProductID);

            //String currentDate=getCurrentDate();
            //Log.e("CURRENTDATE",currentDate);

            String query = "SELECT B.bonus_product_id, P.product_name, B.bonus_product_qty, B.mother_product_qty FROM bonuses B LEFT JOIN product P ON(B.bonus_product_id=P.product_id)  WHERE B.mother_product_id='" + ProductID + "' AND  B.mother_product_qty<='" + givenQty + "' AND '" + memodate + "'>=B.start_date  AND '" + memodate + "'<=B.end_date limit 1";
            Log.e("BONUS_QUERY:", query);

            Cursor cursor = db.rawQueryCoustom(query);
            Log.e("COURSOR_COUNT:", "" + cursor.getCount());
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("product_id", cursor.getString(0));
                        map.put("item", cursor.getString(1));

                        double bonusQty = Double.parseDouble(cursor.getString(2)); //1
                        double tableQty = Double.parseDouble(cursor.getString(3));//12

                        double qty = (Double.parseDouble(givenQty) / tableQty) * bonusQty; //4.66*1.0

                        qty = (int) qty; // 4
                        String QuantityCheck = "";

                        Log.e("QUANTITY_TABLE:", "" + tableQty);
                        Log.e("QUANTITY_GIVEN:", "" + givenQty);
                        Log.e("QUANTITY_PARSE:", "" + qty);

                     /*   double stockQty=0;
                        String stockQtyquey="SELECT SUM(quantity) as quantity FROM  van_stocks WHERE product_id='"+ProductID+ "'";
                        Log.e("STOCK QUERY:", stockQtyquey);
                        Cursor cursor1=db.rawQuery(stockQtyquey);
                        if(cursor1!=null)
                        {
                            if(cursor1.moveToFirst())
                            {
                                do{
//                                    stockQty=Double.parseDouble(cursor1.getString(0));  //0 edit
                                    stockQty=cursor1.getDouble(0);  //0 edit 14.05.2018

                                    if(TempData.editMemo.equalsIgnoreCase("true")){
                                        ///TempData.orderNumber memo no
                                        /// query and get all memo details from memo_details table with memo no and product id cursor.getString(0))
                                        // loop through the memo details records and get quantity for cursor.getString(0)) product

                                        String MemoTablequey="SELECT SUM(quantity) as quantity FROM  memo_details WHERE product_id='"+ProductID+ "' and order_number='"+ TempData.orderNumber +"'";
                                        Cursor cur=db.rawQuery(MemoTablequey);
                                        if(cur!=null)
                                        {
                                            if(cur.moveToFirst())
                                            {
                                                do{
                                                    stockQty = stockQty + cur.getDouble(0);
                                                }while(cur.moveToNext());
                                            }
                                        }
                                    }

                                }while(cursor1.moveToNext());
                            }
                        }*/


                        if (getIndex(cursor.getString(0)) < 0) {
                            Log.e("String.valueOf(qty)", String.valueOf(qty));
                            map.put("quantity", String.valueOf(qty));
                            Bonus.add(map);
                        } else {
                            double QUANTITY = Double.parseDouble(Bonus.get(getIndex(cursor.getString(4))).get("quantity")) + qty;
                            Log.e("Quantity", String.valueOf(QUANTITY));
                            Bonus.get(getIndex(cursor.getString(4))).put("quantity", String.valueOf(QUANTITY));  ///// Why index 4 where as query has 3 indexes
                        }
                    } while (cursor.moveToNext());


                }
            }
            cursor.close();

        }

        String bonusString = "";
        TempData.BonusShowList.clear();
        for (int i = 0; i < Bonus.size(); i++) {
            bonusString = bonusString + Bonus.get(i).get("item") + "(" + Bonus.get(i).get("quantity") + ")";

            HashMap<String, String> product_map = new HashMap<String, String>();

            product_map.put("bonus_name", Bonus.get(i).get("item"));
            product_map.put("quantity", Bonus.get(i).get("quantity"));


//            maplist.add(product_map);
            TempData.BonusShowList.add(product_map);


            if (i != Bonus.size() - 1)
                bonusString = bonusString + ", ";
        }


        if (bonusString.length() > 0) {
            Log.e("bonusString", bonusString);
            txtBonus.setText(bonusString);
            txtBonus.setVisibility(View.VISIBLE);
            TempData.Test = BonusItemList.toString();
        } else
//            txtBonus.setText("");
            txtBonus.setVisibility(View.GONE);

    }


    public String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String CurrentDate = "" + dateFormat.format(date);

        return CurrentDate;

    }

    public String roundTwoDecimals(double d) {


        Log.e("tEST:", String.format("%.2f", d));


        Log.e("Double:", String.valueOf(d));
        Log.e("Modulus:", String.valueOf(d % 1));

		 /* if(d%1>0.0)
		  {

	     DecimalFormat twoDForm = new DecimalFormat("#.##");
	     String value= String.valueOf(twoDForm.format(d));
	     Log.e("value:", ""+value);
	     Log.e("value.indexOf('.'):", ""+value.indexOf('.'));
	     Log.e("value.length:", ""+value.length());
	     String subValue=value.substring(value.indexOf('.'),value.length());
	     Log.e("LENGTH:", subValue);
	              //if(subValue.length()>1)
	        	 //return String.valueOf(twoDForm.format(d));
	             // else
	 	        	 //return String.valueOf(twoDForm.format(d))+"0";


		  }
		  else
		  {

			     //return String.valueOf(d)+"0";
		  }*/

        return String.format("%.2f", d);
    }

    public void UpdateDistPrice() {
        ArrayList<ArrayList<String>> ArrCombine = new ArrayList<ArrayList<String>>();
        ArrayList<String> slaveID = new ArrayList<String>();
        ArrayList<String> generalID = new ArrayList<String>();
        ArrayList<String> combineID = new ArrayList<String>();
        ArrayList<String> inList = new ArrayList<String>();
        ArrayList<String> quantity = new ArrayList<String>();

        for (int i = 0; i < itemListContent.size(); i++) {
            inList.add(itemListContent.get(i).get("product_id"));
            Log.e("IDDDDDDDDDDDDDDDDDD:", itemListContent.get(i).get("product_id"));
        }
        for (int i = 0; i < itemListContent.size(); i++) {
            quantity.add(itemListContent.get(i).get("quantity"));
            savePreference(itemListContent.get(i).get("product_id"), itemListContent.get(i).get("quantity"));

            Log.e("quantity:", itemListContent.get(i).get("quantity"));
        }

        HashMap<String, String> priceMap = new HashMap<String, String>();

        String byPassNow = "YES";
        if (!TargetCustomer.equalsIgnoreCase("0")) {
            //for(int i=0;i<inList.size();i++)
            //priceMap.put(inList.get(i), NGO_price(inList.get(i),quantity.get(i)));
        }

        if (TargetCustomer.equalsIgnoreCase("0") || byPassNow == "YES") {
            InstietuteID = "0";

            for (int i = 0; i < inList.size(); i++) {
                Log.e("PRISING TYPE: ", inList.get(i) + ": " + DistCheckPriceType(inList.get(i)));
                if (DistCheckPriceType(inList.get(i)).equalsIgnoreCase("COMBINE"))
                    combineID.add(inList.get(i));
                if (DistCheckPriceType(inList.get(i)).equalsIgnoreCase("SLAVE"))
                    slaveID.add(inList.get(i));
                if (DistCheckPriceType(inList.get(i)).equalsIgnoreCase("GENERAL"))
                    generalID.add(inList.get(i));
            }


            Log.e("COMBINE SIZE:", "" + combineID.size());
            String combination_id = "";
            for (int i = 0; i < combineID.size(); i++) {
                Cursor cursor = db.rawQueryCoustom("SELECT combination_id FROM distributor_product_combinations WHERE product_id= '" + combineID.get(i) + "' AND combination_id!=0 AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='" + combineID.get(i) + "' AND effective_date<='" + memodate + "')");


                Log.e("OUTER_QUERY:", "SELECT combination_id FROM distributor_product_combinations WHERE product_id= '" + combineID.get(i) + "' AND combination_id!=0");
                Log.e("OUTER_QUERY_COUNT", "" + cursor.getCount());
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            combination_id = cursor.getString(0);
                            Cursor cursor1 = db.rawQueryCoustom("SELECT * FROM distributor_product_combinations WHERE combination_id= '" + cursor.getString(0) + "'");
                            Log.e("INNER QUERY:", "SELECT * FROM distributor_product_combinations WHERE combination_id= '" + cursor.getString(0) + "'");
                            Log.e("INNER QUERY_COUNT:", "" + cursor1.getCount());
                            if (cursor1 != null) {
                                if (cursor1.moveToFirst()) {
                                    ArrayList<String> combinationList = new ArrayList<String>();
                                    do {
                                        //.....combine id added  to list.......
                                        if (!combinationList.contains(cursor1.getString(1)))
                                            combinationList.add(cursor1.getString(1));
                                        Log.e("CombineID", cursor1.getString(1));

                                    } while (cursor1.moveToNext());

                                    if (ArrCombine.size() == 0) {
                                        ArrCombine.add(combinationList);
                                        Log.e("added to combine list", "added");
                                    } else {
                                        if (ArrCombine.contains(combinationList))
                                            Log.e("Duplicate", "True");
                                        else
                                            ArrCombine.add(combinationList);
                                    }
                                }
                            }


                        } while (cursor.moveToNext());
                    }
                }
            }

            Log.e("COMBINE ARRAY:", ArrCombine.toString());
            Log.e("ArrCombine.size()", ArrCombine.size() + "");
            Log.e("QUANTITY ARRAY", quantity.toString());

            for (int i = 0; i < ArrCombine.size(); i++) {
                double QUANTITY = 0;

                ArrayList<String> arrCheck = ArrCombine.get(i);
                Log.e("ArraySize:", arrCheck.size() + "");
                String comProduct = combineID.get(i);
                int index = inList.indexOf(combineID.get(i));

                for (int j = 0; j < arrCheck.size(); j++) {

                    Log.e(".............", ".............");
                    Log.e("COMBINE ID:", arrCheck.get(j));
                    if (!getPreference(arrCheck.get(j)).equalsIgnoreCase(""))
                        QUANTITY = QUANTITY + Double.parseDouble(getPreference(arrCheck.get(j)));

                }
                Log.e("Given....QUANTITY", "" + QUANTITY);
                Log.e("INDi....inList", "" + inList.toString());
                Log.e("INDi....arrCheck", "" + arrCheck.toString());
                /// sql query to find combined quantity

                Double Combined_Quantity = 0.0;

                Cursor cursor = db.rawQueryCoustom("SELECT combination_id,min_quantity FROM distributor_product_combinations WHERE product_id= '" + arrCheck.get(0) + "' AND combination_id!=0 AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='" + arrCheck.get(0) + "' AND effective_date<='" + memodate + "')");
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
//                            Combined_Quantity = Double.parseDouble(cursor.getString(1));
                            if (QUANTITY >= Double.parseDouble(cursor.getString(1))) {
                                Combined_Quantity = Double.parseDouble(cursor.getString(1));
                            }
                        } while (cursor.moveToNext());
                    }
                }

                Log.e("Combined_Quantity: ", "" + Combined_Quantity);

//		    	     if(inList.containsAll(arrCheck))
                if (QUANTITY >= Combined_Quantity) {

                    //Log.e("ArrCombine.get(i).get(index))",""+ArrCombine.get(i).get(index));
                    //Log.e("ArrCombine.get(index).get(i))",""+ArrCombine.get(index).get(i));
                    Log.e("QUANTITY111", "" + QUANTITY);

                    String ProductCombinationID = "";
//                    String query3="SELECT combination_id FROM distributor_product_combinations WHERE product_id='"+ArrCombine.get(i).get(0) +"' AND combination_id!=0  AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='"+ArrCombine.get(i).get(0)+"' AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"')";
                    String query3 = "SELECT combination_id FROM distributor_product_combinations WHERE product_id='" + ArrCombine.get(i).get(0) + "' AND combination_id!=0 AND min_quantity<=" + QUANTITY + "  AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='" + ArrCombine.get(i).get(0) + "' AND effective_date<='" + memodate + "')";
                    Cursor cursor3 = db.rawQueryCoustom(query3);
                    if (cursor3.moveToFirst()) {
                        do {
                            ProductCombinationID = cursor3.getString(0);
                        } while (cursor3.moveToNext());
                    }

                    Log.e("ProductCombinationID", "..........." + ProductCombinationID);
                    if (DistCheckCombineQuantity(QUANTITY, ArrCombine.get(i).get(0), ProductCombinationID)) {
                        for (int j = 0; j < arrCheck.size(); j++) {
                            String combinationID = "";
//                            String query1="SELECT combination_id FROM distributor_product_combinations WHERE product_id='"+arrCheck.get(j) +"' AND combination_id!=0 AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='"+arrCheck.get(j)+"' AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"')";
                            String query1 = "SELECT combination_id FROM distributor_product_combinations WHERE product_id='" + arrCheck.get(j) + "' AND combination_id!=0 AND min_quantity<=" + QUANTITY + " AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='" + arrCheck.get(j) + "' AND effective_date<='" + memodate + "')";
                            Log.e("query1", query1);
                            Cursor cursor1 = db.rawQueryCoustom(query1);
                            if (cursor1.moveToFirst()) {
                                do {
                                    combinationID = cursor1.getString(0);
                                } while (cursor1.moveToNext());
                            }

                            Log.e("combinationID", combinationID);
                            String query2 = "SELECT price From distributor_product_combinations WHERE product_id='" + arrCheck.get(j) + "' AND combination_id='" + combinationID + "' AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='" + arrCheck.get(j) + "' AND effective_date<='" + memodate + "') AND min_quantity<=" + QUANTITY + "  order by effective_date LIMIT 1";
                            Log.e("COMBINATION PRICE QRY:", query2);
                            Cursor cursor2 = db.rawQueryCoustom(query2);
                            if (cursor2.moveToFirst()) {
                                do {
                                    Log.e("COMBINATION_Price:", cursor2.getString(0));
                                    priceMap.put(arrCheck.get(j), cursor2.getString(0));
                                } while (cursor2.moveToNext());
                            }
                        }
                    } else {
                        slaveID.addAll(ArrCombine.get(i));
                        //ArrCombine.remove(i);
                        Log.e("RETURN1", "return1");
                    }
                } else {
                    slaveID.addAll(ArrCombine.get(i));
                    //ArrCombine.remove(i);
                }


            }
        }

        Log.e("..........", "......................");


        for (int i = 0; i < inList.size(); i++)
            Log.e("Selected ID", inList.get(i));


        Log.e("SLIVE SIZE:", "" + slaveID.size());
        for (int i = 0; i < slaveID.size(); i++) {
            if (inList.contains(slaveID.get(i))) {
                int index = inList.indexOf(slaveID.get(i));
                double Quantity = Double.parseDouble(quantity.get(index));
                if (!priceMap.containsKey(slaveID.get(i)))
                    priceMap.put(slaveID.get(i), DistCheckSlapPrice(Quantity, slaveID.get(i)));
            }

        }

        for (int i = 0; i < generalID.size(); i++) {
            if (!priceMap.containsKey(generalID.get(i)))
                priceMap.put(generalID.get(i), DistGeneral_price(generalID.get(i)));
            //priceMap.put(generalID.get(i), "0");
        }

        for (int i = 0; i < inList.size(); i++) {
            Log.e("ID: " + inList.get(i), "Price:" + priceMap.get(inList.get(i)));
            Log.e("...........", "......................");
            Log.e("...........", "..........product_id............" + getPreference("up" + itemListContent.get(i).get("product_id")));

            /*if(!getPreference("up"+itemListContent.get(i).get("product_id")).equalsIgnoreCase("0"))
            {
                itemListContent.get(i).put("general_price", getPreference("up"+itemListContent.get(i).get("product_id")));
                Log.e("", "++++++++++ general_price  "+getPreference("up"+itemListContent.get(i).get("product_id")));
                Log.e("", "i value=  "+i);
            }else {
                itemListContent.get(i).put("general_price", priceMap.get(inList.get(i)));
                Log.e("vvv", "vvv value=  "+i);
            }*/


            if (getPreference("up" + itemListContent.get(i).get("product_id")).equalsIgnoreCase("0")) {
                itemListContent.get(i).put("general_price", priceMap.get(inList.get(i)));
                Log.e("vvv", "vvv value=  " + i);
            } else if (getPreference("up" + itemListContent.get(i).get("product_id")).equalsIgnoreCase("")) {
                itemListContent.get(i).put("general_price", priceMap.get(inList.get(i)));
                Log.e("vvv", "vvv value=  " + i);
            } else {
                itemListContent.get(i).put("general_price", getPreference("up" + itemListContent.get(i).get("product_id")));
                Log.e("", "++++++++++ general_price  " + getPreference("up" + itemListContent.get(i).get("product_id")));
                Log.e("", "i value=  " + i);
            }


        }
        sum = 0.0;

        for (int i = 0; i < itemListContent.size(); i++) {
            HashMap<String, String> map = itemListContent.get(i);
            Log.e("Tgeneral price list", map.get("general_price"));
            Log.e("", "XXXXXXXXXXXXXXXXXXXXXXXXX  general_price  " + map.get("general_price"));
            Log.e("", "XXXXXXXXXXXXXXXXXXXXXXXXX  quantity " + map.get("quantity"));
            sum = sum + (Double.parseDouble(map.get("general_price")) * Double.parseDouble(map.get("quantity")));
        }
        txtTotal.setText(String.valueOf(((ParentActivity) context).roundTwoDecimals(Double.parseDouble("" + sum))));//;//String.valueOf(((ParentActivity) context).roundTwoDecimals(Double.parseDouble(""+sum))));
        getDiscount(roundTwoDecimals(Double.parseDouble("" + sum)));


        DistBonusCalculation(itemListContent);
        notifyDataSetChanged();
    }

    public void DistBonusCalculation(ArrayList<HashMap<String, String>> itemListContent) {
        TextView txtBonus = (TextView) ((Activity) context).findViewById(R.id.txtBonus);
        txtBonus.setText("");

        Bonus.clear();

        for (int i = 0; i < itemListContent.size(); i++) {
            String givenQty = itemListContent.get(i).get("quantity");
            String ProductID = itemListContent.get(i).get("product_id");
            Log.e("BONUS_QUANTITY------", givenQty);
            Log.e("PRODUCT_ID------", ProductID);

            //String currentDate=getCurrentDate();
            //Log.e("CURRENTDATE",currentDate);

            String query = "SELECT B.bonus_product_id, P.product_name, B.bonus_product_qty, B.mother_product_qty FROM bonuses B LEFT JOIN product P ON(B.bonus_product_id=P.product_id)  WHERE B.mother_product_id='" + ProductID + "' AND  B.mother_product_qty<='" + givenQty + "' AND '" + memodate + "'>=B.start_date  AND '" + memodate + "'<=B.end_date limit 1";
            Log.e("BONUS_QUERY:", query);

            Cursor cursor = db.rawQueryCoustom(query);
            Log.e("COURSOR_COUNT:", "" + cursor.getCount());
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("product_id", cursor.getString(0));
                        map.put("item", cursor.getString(1));

                        double bonusQty = Double.parseDouble(cursor.getString(2)); //1
                        double tableQty = Double.parseDouble(cursor.getString(3));//12

                        double qty = (Double.parseDouble(givenQty) / tableQty) * bonusQty; //4.66*1.0

                        qty = (int) qty; // 4
                        String QuantityCheck = "";

                        Log.e("QUANTITY_TABLE:", "" + tableQty);
                        Log.e("QUANTITY_GIVEN:", "" + givenQty);
                        Log.e("QUANTITY_PARSE:", "" + qty);

                        double stockQty = 0;
                        String stockQtyquey = "SELECT SUM(quantity) as quantity FROM  van_stocks WHERE product_id='" + ProductID + "'";
                        Log.e("STOCK QUERY:", stockQtyquey);
                        Cursor cursor1 = db.rawQueryCoustom(stockQtyquey);
                        if (cursor1 != null) {
                            if (cursor1.moveToFirst()) {
                                do {
//                                    stockQty=Double.parseDouble(cursor1.getString(0));  //0 edit
                                    stockQty = cursor1.getDouble(0);  //0 edit 14.05.2018

                                    if (TempData.editMemo.equalsIgnoreCase("true")) {
                                        ///TempData.orderNumber memo no
                                        /// query and get all memo details from memo_details table with memo no and product id cursor.getString(0))
                                        // loop through the memo details records and get quantity for cursor.getString(0)) product

                                        String MemoTablequey = "SELECT SUM(quantity) as quantity FROM  memo_details WHERE product_id='" + ProductID + "' and order_number='" + TempData.orderNumber + "'";
                                        Cursor cur = db.rawQueryCoustom(MemoTablequey);
                                        if (cur != null) {
                                            if (cur.moveToFirst()) {
                                                do {
                                                    stockQty = stockQty + cur.getDouble(0);
                                                } while (cur.moveToNext());
                                            }
                                        }
                                    }

                                } while (cursor1.moveToNext());
                            }
                        }

                        double GivenAndBonusQty = Double.parseDouble(givenQty) + qty;  //22 edit
                        Log.e("stockQty", "" + stockQty);
                        Log.e("GivenAndBonusQty", "" + GivenAndBonusQty);


                        if (GivenAndBonusQty > stockQty) {
                            qty = stockQty - Double.parseDouble(givenQty);
                            Log.e("quantityvalue", String.valueOf(qty));
                        }


                        if (getIndex(cursor.getString(0)) < 0) {
                            Log.e("String.valueOf(qty)", String.valueOf(qty));
                            map.put("quantity", String.valueOf(qty));
                            Bonus.add(map);
                        } else {
                            double QUANTITY = Double.parseDouble(Bonus.get(getIndex(cursor.getString(4))).get("quantity")) + qty;
                            Log.e("Quantity", String.valueOf(QUANTITY));
                            Bonus.get(getIndex(cursor.getString(4))).put("quantity", String.valueOf(QUANTITY));  ///// Why index 4 where as query has 3 indexes
                        }
                    } while (cursor.moveToNext());
                }
            }

        }

        String bonusString = "";
        TempData.BonusShowList.clear();
        for (int i = 0; i < Bonus.size(); i++) {
            bonusString = bonusString + Bonus.get(i).get("item") + "(" + Bonus.get(i).get("quantity") + ")";

            HashMap<String, String> product_map = new HashMap<String, String>();

            product_map.put("bonus_name", Bonus.get(i).get("item"));
            product_map.put("quantity", Bonus.get(i).get("quantity"));


//            maplist.add(product_map);
            TempData.BonusShowList.add(product_map);


            if (i != Bonus.size() - 1)
                bonusString = bonusString + ", ";
        }


        if (bonusString.length() > 0) {
            Log.e("bonusString", bonusString);
            txtBonus.setText(bonusString);
            TempData.Test = BonusItemList.toString();
        } else
//            txtBonus.setText("");
            txtBonus.setVisibility(View.GONE);

    }


    public String DistGeneral_price(String product_id) {
        String query2 = "SELECT price From distibutor_product_prices WHERE product_id='" + product_id + "' AND target_custommer='" + TargetCustomer + "' AND institute_id='" + InstietuteID + "' AND effective_date<='" + memodate + "' order by effective_date DESC, _id DESC LIMIT 1";
        Log.e("NGO_QUERY:", query2);


        Cursor cursor2 = db.rawQueryCoustom(query2);
        Log.e("count", " : " + cursor2.getCount());
        if (cursor2.getCount() > 0) {
            if (cursor2.moveToFirst()) {
                do {
                    return cursor2.getString(0);
                } while (cursor2.moveToNext());
            }
        } else {
            String query3 = "SELECT price From distibutor_product_prices WHERE product_id='" + product_id + "' AND target_custommer=0 AND price !='0.0' AND effective_date<='" + memodate + "' order by effective_date, _id DESC LIMIT 1";
            Cursor cursor3 = db.rawQueryCoustom(query3);
            if (cursor3.getCount() > 0) {
                if (cursor3.moveToFirst()) {
                    do {
                        return cursor3.getString(0);
                    } while (cursor3.moveToNext());
                }
            }
        }
        return "0.0";
    }

    public String DistCheckSlapPrice(double quantity, String product_id) {
        num.clear();
        //String query1="SELECT min_quantity From distributor_product_combinations WHERE product_id='"+product_id+"' AND combination_id=0 AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"' order by effective_date LIMIT 1";
        String query1 = "SELECT min_quantity From distributor_product_combinations WHERE product_id='" + product_id + "' AND combination_id=0 AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='" + String.valueOf(product_id) + "' AND combination_id=0 AND effective_date<='" + memodate + "') order by min_quantity DESC LIMIT 1";
        Cursor cursor = db.rawQueryCoustom(query1);
        Log.e("QUERY", query1);
        Log.e("COUNT", "" + cursor.getCount());

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    num.add(Double.parseDouble(cursor.getString(0)));
                } while (cursor.moveToNext());
            }
        }
        num.add(0, 0.0);
        num.add(quantity);
        num.add(8080.0);
        Collections.sort(num);


        int i = 0;
        for (int k = 0; k < num.size(); k++) {
            Log.e("NUM", " " + num.get(k));
            if (num.get(k) == quantity)
                i = k;
        }

        String query = "SELECT price From distributor_product_combinations WHERE product_id='" + String.valueOf(product_id) + "' AND combination_id=0 AND min_quantity <=" + String.valueOf(num.get(i)) + "  AND effective_date=(SELECT max(effective_date) from distributor_product_combinations where product_id='" + String.valueOf(product_id) + "' AND combination_id=0 AND effective_date<='" + memodate + "') ORDER BY  min_quantity DESC LIMIT 1";

        Log.e("QUERY PRICE VALUE: ", query);
        Cursor cursor1 = db.rawQueryCoustom(query);
        Log.e("Second Cursor:", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            try {
                if (cursor1.moveToFirst()) {
                    do {
                        price = cursor1.getString(0);
                        Log.e("Slave price:", price);
                        return price;
                    } while (cursor1.moveToNext());
                }

            } finally {
                try {
                    cursor1.close();
                } catch (Exception ignore) {
                }
            }

        } else {
            //String query2="SELECT price From distibutor_product_prices WHERE product_id='"+product_id+ "' AND target_custommer=0 AND price!='0.0' AND effective_date=(SELECT max(effective_date) from distibutor_product_prices where product_id='"+product_id+"' AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"')";
            String query2 = "SELECT price From distibutor_product_prices WHERE product_id='" + product_id + "' AND target_custommer=0 AND price!='0.0' AND effective_date<='" + memodate + "' ORDER by effective_date DESC , _id DESC limit 1";

            Log.e("query2", query2);
            Cursor cursor2 = db.rawQueryCoustom(query2);
            if (cursor2.getCount() == 0) {
                return "0.0";
            }

            if (cursor2.moveToFirst()) {
                do {
                    Log.e("General price:", cursor2.getString(0));
                    return cursor2.getString(0);
                    //return "0";
                } while (cursor2.moveToNext());
            }
        }
        return "0";
    }


    public boolean DistCheckCombineQuantity(double quantity, String product_id, String combination_id) {

        Log.e("Calling this method:", "called for " + product_id);
        Log.e("quantity", "" + quantity);
        Log.e("product_id", "" + product_id);
        Log.e("combination_id", "" + combination_id);

        //Cursor cursor=db.rawQuery("SELECT *FROM distributor_product_combinations WHERE product_id= '"+product_id+"' AND combination_id='"+combination_id+"'", null);
        Cursor cursor = db.rawQueryCoustom("SELECT *FROM distributor_product_combinations WHERE combination_id='" + combination_id + "'");
        Log.e("CheckCombineQtyCount", "" + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                double qty = Double.parseDouble(cursor.getString(3));
                Log.e("Pass Quantity:", "" + quantity);
                Log.e("Quantity:", "" + qty);
                if (quantity >= qty) {
                    Log.e("TRUE", "true");
                    return true;
                } else {
                    Log.e("FALSE", "false");
                    return false;
                }

            } while (cursor.moveToNext());
        }
        return false;
    }

    public String DistCheckPriceType(String ID) {


        Cursor cursor = db.rawQueryCoustom("SELECT * FROM distributor_product_combinations WHERE product_id= '" + ID + "' AND effective_date = (SELECT max(effective_date) from distributor_product_combinations where product_id='" + ID + "' AND effective_date<='" + memodate + "') ORDER BY combination_id desc");

        Log.e("PRICE TYPE QUERY:", "SELECT * FROM distributor_product_combinations WHERE product_id= '" + ID + "' AND effective_date = (SELECT max(effective_date) from distributor_product_combinations where product_id='" + ID + "' AND effective_date<='" + memodate + "') ORDER BY combination_id desc");

        if (cursor.getCount() == 0) {
            db.close();
            cursor.close();
            return "GENERAL";
        } else {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Log.e("COMBINATION ID", cursor.getString(2));
                        if (!cursor.getString(2).equalsIgnoreCase("0")) {
                            db.close();
                            cursor.close();
                            return "COMBINE";
                        } else {
                            db.close();
                            cursor.close();
                            return "SLAVE";
                        }
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
            }
        }

        db.close();
        cursor.close();
        return "";
    }


    public boolean CheckCombineQuantity(double quantity, String product_id, String combination_id) {

        Log.e("Calling this method:", "called for " + product_id);
        Log.e("quantity", "" + quantity);
        Log.e("product_id", "" + product_id);
        Log.e("combination_id", "" + combination_id);

        //Cursor cursor=db.rawQuery("SELECT *FROM product_combinations WHERE product_id= '"+product_id+"' AND combination_id='"+combination_id+"'", null);
        Cursor cursor = db.rawQueryCoustom("SELECT * FROM product_combinations WHERE combination_id='" + combination_id + "'");
        Log.e("CheckCombineQtyCount", "" + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                double qty = Double.parseDouble(cursor.getString(3));
                Log.e("Pass Quantity:", "" + quantity);
                Log.e("Quantity:", "" + qty);
                if (quantity >= qty) {
                    Log.e("TRUE", "true");
                    return true;
                } else {
                    Log.e("FALSE", "false");
                    return false;
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    public String CheckPriceType(String ID) {


        Cursor cursor = db.rawQueryCoustom("SELECT * FROM product_combinations WHERE product_id= '" + ID + "' AND effective_date = (SELECT max(effective_date) from product_combinations where product_id='" + ID + "' AND effective_date<='" + memodate + "') ORDER BY combination_id desc");

        Log.e("PRICE TYPE QUERY:", "SELECT * FROM product_combinations WHERE product_id= '" + ID + "' AND effective_date = (SELECT max(effective_date) from product_combinations where product_id='" + ID + "' AND effective_date<='" + memodate + "') ORDER BY combination_id desc");

        if (cursor.getCount() == 0) {
            db.close();
            cursor.close();
            return "GENERAL";
        } else {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Log.e("COMBINATION ID", cursor.getString(2));
                        if (!cursor.getString(2).equalsIgnoreCase("0")) {
                            db.close();
                            cursor.close();
                            return "COMBINE";
                        } else {
                            db.close();
                            cursor.close();
                            return "SLAVE";
                        }
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
            }
        }

        db.close();
        cursor.close();
        return "";
    }

    public String General_price(String product_id) {
        String query2 = "SELECT price From product_price WHERE product_id='" + product_id + "' AND target_custommer='" + TargetCustomer + "' AND institute_id='" + InstietuteID + "' AND effective_date<='" + memodate + "' order by effective_date DESC LIMIT 1";
        Log.e("NGO_QUERY:", query2);


        Cursor cursor2 = db.rawQueryCoustom(query2);
        Log.e("count", " : " + cursor2.getCount());
        if (cursor2.getCount() > 0) {
            if (cursor2.moveToFirst()) {
                do {
                    return cursor2.getString(0);
                } while (cursor2.moveToNext());
            }
        } else {
            String query3 = "SELECT price From " + Tables.TABLE_NAME_PRODUCT_PRICE + " WHERE product_id='" + product_id + "' AND target_custommer=0 AND price !='0.0' AND effective_date<='" + memodate + "' order by effective_date LIMIT 1";
            Cursor cursor3 = db.rawQueryCoustom(query3);
            if (cursor3.getCount() > 0) {
                if (cursor3.moveToFirst()) {
                    do {
                        return cursor3.getString(0);
                    } while (cursor3.moveToNext());
                }
            }
        }
        return "0.0";
    }


    public String NGO_price(String product_id, String quantity) {
        String query2 = "SELECT price From product_price WHERE product_id='" + product_id + "' AND target_custommer='" + TargetCustomer + "' AND institute_id='" + InstietuteID + "' AND effective_date<='" + memodate + "' order by effective_date LIMIT 1";
        Log.e("NGO_QUERY:", query2);


        Cursor cursor2 = db.rawQueryCoustom(query2);
        Log.e("count", " : " + cursor2.getCount());
        if (cursor2.getCount() > 0) {
            if (cursor2.moveToFirst()) {
                do {
                    return cursor2.getString(0);
                } while (cursor2.moveToNext());
            }
        } else {
            return CheckSlapPrice(Double.parseDouble(quantity), product_id);
/*			   String query3="SELECT price From product_price WHERE id='"+product_id+"' AND target_custommer=0 AND price !='0.0' AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"' order by effective_date LIMIT 1";
			   Cursor  cursor3= db.rawQuery(query3, null);
			   if(cursor3.getCount() > 0)
			   {
			   if(cursor3.moveToFirst())
			     {
			    	 do{
			    		 return cursor3.getString(0);
			    	 }while(cursor3.moveToNext());
			     }
			   }
*/
        }
        return "0.0";
    }

    public String CheckSlapPrice(double quantity, String product_id) {
        num.clear();
        //String query1="SELECT min_quantity From product_combinations WHERE product_id='"+product_id+"' AND combination_id=0 AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"' order by effective_date LIMIT 1";
        String query1 = "SELECT min_quantity From product_combinations WHERE product_id='" + product_id + "' AND combination_id=0 AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='" + String.valueOf(product_id) + "' AND combination_id=0 AND effective_date<='" + memodate + "') order by min_quantity DESC LIMIT 1";
        Cursor cursor = db.rawQueryCoustom(query1);
        Log.e("QUERY", query1);
        Log.e("COUNT", "" + cursor.getCount());

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    num.add(Double.parseDouble(cursor.getString(0)));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        num.add(0, 0.0);
        num.add(quantity);
        num.add(8080.0);
        Collections.sort(num);


        int i = 0;
        for (int k = 0; k < num.size(); k++) {
            Log.e("NUM", " " + num.get(k));
            if (num.get(k) == quantity)
                i = k;
        }


        //String query="SELECT price From product_combinations WHERE product_id="+String.valueOf(product_id)+" AND combination_id=0 " +" AND "+ "min_quantity>="+String.valueOf(num.get(i-1))+" AND "+ "min_quantity <"+String.valueOf(num.get(i+1));
        //String query="SELECT price From product_combinations WHERE product_id='"+String.valueOf(product_id)+"' AND combination_id=0 AND min_quantity <="+String.valueOf(num.get(i))+"  AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"' ORDER BY effective_date, min_quantity DESC LIMIT 1";
        //String query="SELECT price From product_combinations WHERE product_id='"+String.valueOf(product_id)+"' AND combination_id=0 AND min_quantity <="+String.valueOf(num.get(i))+"  AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"' ORDER BY effective_date, min_quantity DESC LIMIT 1";

        String query = "SELECT price From product_combinations WHERE product_id='" + String.valueOf(product_id) + "' AND combination_id=0 AND min_quantity <=" + String.valueOf(num.get(i)) + "  AND effective_date=(SELECT max(effective_date) from product_combinations where product_id='" + String.valueOf(product_id) + "' AND combination_id=0 AND effective_date<='" + memodate + "') ORDER BY  min_quantity DESC LIMIT 1";

        Log.e("QUERY PRICE VALUE: ", query);
        Cursor cursor1 = db.rawQueryCoustom(query);
        Log.e("Second Cursor:", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {

            if (cursor1.moveToFirst()) {
                do {
                    price = cursor1.getString(0);
                    Log.e("Slave price:", price);
                    savePreference("up" + product_id, "0");
                    return price;
                } while (cursor1.moveToNext());


            }

            cursor1.close();
        } else {
            //String query2="SELECT price From product_price WHERE product_id='"+product_id+ "' AND target_custommer=0 AND price!='0.0' AND effective_date=(SELECT max(effective_date) from product_price where product_id='"+product_id+"' AND effective_date<='"+((ParentActivity) context).getCurrentDate()+"')";
            String query2 = "SELECT price From product_price WHERE product_id='" + product_id + "' AND target_custommer=0 AND price!='0.0' AND effective_date<='" + memodate + "' ORDER by effective_date DESC limit 1";

            Log.e("query2", query2);
            Cursor cursor2 = db.rawQueryCoustom(query2);
            if (cursor2.getCount() == 0) {
                return "0.0";
            }

            if (cursor2.moveToFirst()) {
                do {
                    Log.e("General price:", cursor2.getString(0));
                    savePreference("up" + product_id, "0");
                    return cursor2.getString(0);
                    //return "0";
                } while (cursor2.moveToNext());
            }
            cursor2.close();
        }

        cursor1.close();
        return "0";
    }


    int getIndex(String value) {
        int pos = -1;
        for (int i = 0; i < Bonus.size(); i++) {
            HashMap<String, String> map = Bonus.get(i);
            Log.e("PRODUCT_ID", map.get("product_id"));
            if (map.get("product_id").equalsIgnoreCase(value))
                pos = i;
        }

        return pos;
    }

    public void savePreference(String key, String value) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key) {
        String value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        value = prefs.getString(key, "0");

        return value;

    }

    public String roundTwoDecimal(double d) {

        Log.e("Double:", String.valueOf(d));
        Log.e("Modulus:", String.valueOf(d % 1));

        if (d % 1 > 0.0) {

            DecimalFormat twoDForm = new DecimalFormat("#.##");
            String value = String.valueOf(twoDForm.format(d));
            /* String subValue = value.substring(value.indexOf('.'), (value.length() - 1));*/


            return String.valueOf(value);

        } else {

            return String.valueOf(d) + "0";
        }
    }


    private void showLimitExeednotification(double giventQty, String product_id) {

        Cursor c = db.rawQueryCoustom("SELECT quantity-booking_quantity>" + giventQty + " as p from stock_info WHERE  product_id = '" + product_id + "'");
        Log.e("ProductSales", "SELECT quantity-booking_quantity>" + giventQty + " as p from stock_info WHERE  product_id = '" + product_id + "'");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            if (c.getInt(0) == 0) {
                Toast.makeText(context, "Your Stock Limit Exceeded", Toast.LENGTH_SHORT).show();
            }
        }


    }
}