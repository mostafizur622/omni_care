package com.srapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
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

import com.google.gson.Gson;
import com.srapp.Bonus_policy;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.Util.Parent;
import com.srapp.Util.ParentActivity;
import com.srapp.bonusPolicy.ExclusionProduct;
import com.srapp.bonusPolicy.PolicyBonusProduct;
import com.srapp.bonusPolicy.PolicyID;
import com.srapp.bonusPolicy.PolicyProduct;
import com.srapp.bonusPolicy.PolicyWiseSlab;
import com.srapp.pricing.CombinationPrices;
import com.srapp.pricing.CombinationProductDetails;
import com.srapp.pricing.GSOPrice;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.srapp.TempData.BPBonusProductView;
import static com.srapp.TempData.BPSelected_bonus;
import static com.srapp.TempData.BPSelected_option_id;
import static com.srapp.TempData.BPSelected_policy_type;
import static com.srapp.TempData.BPSelected_product;
import static com.srapp.TempData.BPSelected_set;
import static com.srapp.TempData.BPbonus_product;
import static com.srapp.TempData.BPbonus_product_t;
import static com.srapp.TempData.BonusShowList;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.OldBPSelected_bonus;
import static com.srapp.TempData.OldBPSelected_option_id;
import static com.srapp.TempData.OldBPSelected_policy_type;
import static com.srapp.TempData.OldBPSelected_set;
import static com.srapp.TempData.PolicySetRelation;
import static com.srapp.TempData.combination_idMap;
import static com.srapp.TempData.discount_data;
import static com.srapp.TempData.discountmap;
import static com.srapp.TempData.discountmapview;
import static com.srapp.TempData.discountoffer;
import static com.srapp.TempData.discountofferPolicyid;
import static com.srapp.TempData.discounttype;
import static com.srapp.TempData.policyArrayList;
import static com.srapp.TempData.policyBonusProductArrList;
import static com.srapp.TempData.priceMap;
import static com.srapp.TempData.price_idMap;
import static com.srapp.TempData.vatMap;

public class SalesOrderDetailsAdaper1 extends BaseAdapter {
    public static Bonus_policy bonus_policylistener;
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
    //HashMap<String, String> priceMap = new HashMap<String, String>();
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
    //Add 6/01/2021
    String vatvalue = "";
    //-------------------pricing------------------------------------------
    ArrayList<GSOPrice> pricesArrayList;
    ArrayList<String> sgidArrayList;
    ArrayList<CombinationPrices> combinationPricesArrayList;
    ArrayList<CombinationProductDetails> combinationProductDetailsArrayList;


    //-------------------bonus_policy------------------------------------------
    ArrayList<PolicyID> policyIdsArrayList;
    ArrayList<PolicyProduct> policyProductArrayList;
    ArrayList<PolicyWiseSlab> policyWiseSlabArrayList;
    ArrayList<ExclusionProduct> exclusionArrayList;
    ArrayList<ExclusionProduct> inclusionArrayList;
    ArrayList<PolicyBonusProduct> policyBonusProductArrayList;

    public SalesOrderDetailsAdaper1(Context context, ArrayList<HashMap<String, String>> arraylistContent, String _SO_ID, String outlet_ID) {
        this.context = context;
        this.outlet_ID = outlet_ID;
        itemListContent = arraylistContent;

        db = new Data_Source(context);
        if (TempData.policyClear) {
            db.excQuery("delete from policy_product_Option_temp");
            TempData.policyClear = true;
        }

        SO_ID = _SO_ID;

        bonus_policylistener = state1 -> {
            Log.e("state123", state1 + "");
            state = state1;
            UpdatePrice1();
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
        Log.e("outletCategory_id_", TempData.OutletCatagoryID);

        sum = 0.0;
        txtTotal = ((Activity) context).findViewById(R.id.total_price);
        discount = ((Activity) context).findViewById(R.id.discount);
        subt = ((Activity) context).findViewById(R.id.sub_total);
        vattxt = ((Activity) context).findViewById(R.id.vat);
        discount_details = ((Activity) context).findViewById(R.id.discount_details);
        txtBonusPolicy = ((Activity) context).findViewById(R.id.txtBonusPolicy);

        /*for (int i=0; i<itemListContent.size(); i++){
            if (itemListContent.get(i).get("general_price")==null){
                itemListContent.remove(i).remove("product_id");
            }
        }*/

        UpdatePrice1();

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

        NameTv = view2.findViewById(R.id.product_name);
        UPrice = view2.findViewById(R.id.price);
        vattv = view2.findViewById(R.id.vat);
        TPrice = view2.findViewById(R.id.total_price);
        QuantityEd = view2.findViewById(R.id.quantity);
        ActionBtn = view2.findViewById(R.id.dlt_product_sales_item);

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


      /*  if (general_price==null){
            db.excQuery("UPDATE product_boolean SET boolean = " + "'" + "false" + "'" + ", quantity=" + "'" + "" + "'" + " WHERE product_id = " + "'" + itemListContent.get(position).get("product_id") + "'" + " AND outlet_id='" + outlet_ID + "'");
            savePreference(itemListContent.get(position).get("product_id"), "0.0");
            savePreference("up" + itemListContent.get(position).get("product_id"), "0");
            itemListContent.remove(position);
            notifyDataSetChanged();
            UpdatePrice1();
        }*/


        UPrice.setEnabled(mapContent.get("is_bonus").equalsIgnoreCase("0"));


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
                    }

                    QuantityEd.setText(Quantity);
                }

                Log.e("SCROLLING PRICE:", "......." + T_Price);
                Log.e("Valuettt :", getPreference("up" + mapContent.get("product_id")) + "   n");

                TPrice.setText(String.valueOf(roundTwoDecimals(Double.parseDouble("" + T_Price))));

                if (TempData.editMemo.equalsIgnoreCase("true")) { // if memo in edit moad then & open  price applied then open price will show from memo
                    Log.e("Valuettt", getPreference("up" + mapContent.get("product_id")));
                    if (getPreference("up" + itemListContent.get(position).get("product_id")).equalsIgnoreCase("0")) {
                        Log.e("Value", getPreference("up" + mapContent.get("product_id")));
                        UPrice.setText(String.valueOf(((Parent) context).roundTwoDecimals(Double.parseDouble(general_price))));

                    } else {


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
                    UpdatePrice1();
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

                        UpdatePrice1();

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
                    UpdatePrice1();

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
                        UpdatePrice1();

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
                UpdatePrice1();
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


    public void UpdatePrice1() {

        Log.e("outletCategory__", TempData.OutletCatagoryID);

        sgidArrayList = db.SpecialGroup_ID_List(memodate, outlet_ID, TempData.OutletCatagoryID);

        String specialGroupIds = TextUtils.join(", ", sgidArrayList);// Convert Special group id arrayList to string
        Log.e("Special_group_id", String.valueOf(specialGroupIds));

        ArrayList<String> productIDList = new ArrayList<String>();

        ArrayList<String> quantity = new ArrayList<String>();

        for (int i = 0; i < itemListContent.size(); i++) {
            productIDList.add(itemListContent.get(i).get("product_id"));
            Log.e("IDDDDDDDDDDDDDDDDDD:", itemListContent.get(i).get("product_id"));
            savePreference(itemListContent.get(i).get("product_id"), itemListContent.get(i).get("quantity"));
            if (!productList.contains(itemListContent.get(i).get("product_id")))
                productList.add(itemListContent.get(i).get("product_id"));
        }

        String cartProductIDs = TextUtils.join(", ", productIDList);// Convert product id arrayList to string
        Log.e("Cart_product_id", String.valueOf(cartProductIDs));

        Log.e("startLog_", String.valueOf(itemListContent.size()));


        try {
            for (int i = 0; i <itemListContent.size(); i++) {

                pricesArrayList = db.GSOPriceList(itemListContent.get(i).get("product_id"), itemListContent.get(i).get("quantity"), specialGroupIds, memodate, TempData.OutletCatagoryID);

                Log.e("startLog", "start");
                Log.e("startLog", pricesArrayList.get(0).getSpecial_group_id());

                if (!pricesArrayList.get(0).getSpecial_group_id().equals("0")) {
                    Log.e("startLog", "specialLog");

                    priceMap.put(itemListContent.get(i).get("product_id"), pricesArrayList.get(0).getSpecial_price());
                    vatMap.put(itemListContent.get(i).get("product_id"), String.valueOf(CheckVat(itemListContent.get(i).get("product_id"))));
                    price_idMap.put(itemListContent.get(i).get("product_id"), pricesArrayList.get(0).getSlab_id());
                    TempData.PriceSlabID = pricesArrayList.get(0).getSlab_id();

                    combinationPricesArrayList = db.CombinationPriceList(
                            itemListContent.get(i).get("product_id"),
                            "6",
                            pricesArrayList.get(0).getSpecial_group_id(),
                            memodate,
                            pricesArrayList.get(0).getPriceSlabEffectiveDate());

                    Log.e("special_price:", pricesArrayList.get(0).getSpecial_price());

                    if (combinationPricesArrayList.size() > 0) {

                        for (int j = 0; j < combinationPricesArrayList.size(); j++) {
                            Double price = 0.0;
                            Double CartProductQtysum = 0.0;
                            Integer priceId = 0;
                            Double combinedQty = Double.valueOf(combinationPricesArrayList.get(j).getCombinedQty());
                            String combinationId = combinationPricesArrayList.get(j).get_id();

                            combinationProductDetailsArrayList = db.CombinationProductList(combinationPricesArrayList.get(j).get_id(), combinedQty,
                                    "6", pricesArrayList.get(0).getSpecial_group_id(), memodate);

                            Log.e("combinedQty__:", String.valueOf(combinedQty));

                            for (int k = 0; k < combinationProductDetailsArrayList.size(); k++) {
                                Integer product_id = Integer.valueOf(combinationProductDetailsArrayList.get(k).getProducct_id());
                                String quentity = getPreference(String.valueOf(product_id));
                                CartProductQtysum = CartProductQtysum + Double.valueOf(quentity);

                                if (product_id.equals(Integer.valueOf(itemListContent.get(i).get("product_id")))) {
                                    price = Double.valueOf(combinationProductDetailsArrayList.get(k).getSpecial_price());
                                    priceId = Integer.valueOf(combinationProductDetailsArrayList.get(k).getProduct_com_id());
                                    Log.e("combinedQty_price_:", String.valueOf(price));
                                }

                                Log.e("combinedQty_pid:", String.valueOf(product_id));
                                Log.e("combinedQty_qty:", quentity);
                                Log.e("combinedQty_cart:", String.valueOf(CartProductQtysum));
                                Log.e("combinedQty_price:", combinationProductDetailsArrayList.get(k).getSpecial_price());

                                if (CartProductQtysum >= combinedQty) {
                                    priceMap.put(itemListContent.get(i).get("product_id"), String.valueOf(price));
                                    vatMap.put(itemListContent.get(i).get("product_id"), String.valueOf(CheckVat(itemListContent.get(i).get("product_id"))));
                                    combination_idMap.put(itemListContent.get(i).get("product_id"), combinationId);
                                    price_idMap.put(itemListContent.get(i).get("product_id"), String.valueOf(priceId));
                                    TempData.PriceCombinationID = String.valueOf(priceId);

                                    //  break;
                                }
                            }
                        }
                    }

                }
                else if (!pricesArrayList.get(0).getOutlet_category_id().equals("0")) {

                    Log.e("startLog", "outletcategory");

                    priceMap.put(itemListContent.get(i).get("product_id"), pricesArrayList.get(0).getOutlet_category_price());
                    vatMap.put(itemListContent.get(i).get("product_id"), String.valueOf(CheckVat(itemListContent.get(i).get("product_id"))));
                    price_idMap.put(itemListContent.get(i).get("product_id"), pricesArrayList.get(0).getSlab_id());
                    TempData.PriceSlabID = pricesArrayList.get(0).getSlab_id();

                    Log.e("outlet_price:", pricesArrayList.get(0).getOutlet_category_price());
                    Log.e("OutletCatagoryID:", TempData.OutletCatagoryID);

                    combinationPricesArrayList = db.CombinationPriceList(
                            itemListContent.get(i).get("product_id"),
                            "4",
                            pricesArrayList.get(0).getOutlet_category_id(),
                            memodate,
                            pricesArrayList.get(0).getPriceSlabEffectiveDate());

                    if (combinationPricesArrayList.size() > 0) {
                        for (int j = 0; j < combinationPricesArrayList.size(); j++) {
                            Double price = 0.0;
                            Double CartProductQtysum = 0.0;
                            Integer priceId = 0;

                            Double combinedQty = Double.valueOf(combinationPricesArrayList.get(j).getCombinedQty());
                            combinationProductDetailsArrayList = db.CombinationProductList(combinationPricesArrayList.get(j).get_id(), combinedQty,
                                    "4", pricesArrayList.get(0).getOutlet_category_id(), memodate);

                            Log.e("combinedQty__:", String.valueOf(combinedQty));

                            for (int k = 0; k < combinationProductDetailsArrayList.size(); k++) {
                                Integer product_id = Integer.valueOf(combinationProductDetailsArrayList.get(k).getProducct_id());
                                String quentity = getPreference(String.valueOf(product_id));
                                CartProductQtysum = CartProductQtysum + Double.valueOf(quentity);

                                if (product_id.equals(Integer.valueOf(itemListContent.get(i).get("product_id")))) {
                                    price = Double.valueOf(combinationProductDetailsArrayList.get(k).getCategory_price());
                                    priceId = Integer.valueOf(combinationProductDetailsArrayList.get(k).getProduct_com_id());
                                }

                                Log.e("combinedQty_pid:", String.valueOf(product_id));
                                Log.e("combinedQty_qty:", quentity);
                                Log.e("combinedQty_price:", combinationProductDetailsArrayList.get(k).getCategory_price());

                                if (CartProductQtysum >= combinedQty) {
                                    priceMap.put(itemListContent.get(i).get("product_id"), String.valueOf(price));
                                    vatMap.put(itemListContent.get(i).get("product_id"), String.valueOf(CheckVat(itemListContent.get(i).get("product_id"))));
                                    combination_idMap.put(itemListContent.get(i).get("product_id"), combinationPricesArrayList.get(j).get_id());
                                    price_idMap.put(itemListContent.get(i).get("product_id"), String.valueOf(priceId));
                                    TempData.PriceCombinationID = String.valueOf(priceId);
                                    // break;
                                }

                            }

                        }
                    }
                } else {
                    Log.e("startLog", "general");
                    priceMap.put(itemListContent.get(i).get("product_id"), pricesArrayList.get(0).getPrice());
                    Log.e("tradePrice", pricesArrayList.get(0).getPrice());
                    vatMap.put(itemListContent.get(i).get("product_id"), String.valueOf(CheckVat(itemListContent.get(i).get("product_id"))));
                    price_idMap.put(itemListContent.get(i).get("product_id"), pricesArrayList.get(0).getSlab_id());
                    TempData.PriceSlabID = pricesArrayList.get(0).getSlab_id();

                    combinationPricesArrayList = db.CombinationPriceList(
                            itemListContent.get(i).get("product_id"),
                            "1",
                            "0",
                            memodate,
                            pricesArrayList.get(0).getPriceSlabEffectiveDate());

                    Log.e("combinedQty_Cartpid:", itemListContent.get(i).get("product_id"));


                    if (combinationPricesArrayList.size() > 0) {

                        for (int j = 0; j < combinationPricesArrayList.size(); j++) {
                            Double price = 0.0;
                            Double CartProductQtysum = 0.0;
                            Integer priceId = 0;
                            Double combinedQty = Double.valueOf(combinationPricesArrayList.get(j).getCombinedQty());
                            combinationProductDetailsArrayList = db.CombinationProductList(combinationPricesArrayList.get(j).get_id(), combinedQty, "1", "0", memodate);

                            Log.e("combinedQty__:", String.valueOf(combinedQty));

                            for (int k = 0; k < combinationProductDetailsArrayList.size(); k++) {
                                Integer product_id = Integer.valueOf(combinationProductDetailsArrayList.get(k).getProducct_id());
                                String quentity = getPreference(String.valueOf(product_id));
                                CartProductQtysum = CartProductQtysum + Double.valueOf(quentity);

                                if (product_id.equals(Integer.valueOf(itemListContent.get(i).get("product_id")))) {
                                    price = Double.valueOf(combinationProductDetailsArrayList.get(k).getPrice());
                                    priceId = Integer.valueOf(combinationProductDetailsArrayList.get(k).getProduct_com_id());
                                }

                                Log.e("combinedQty_pid:", String.valueOf(product_id));
                                Log.e("combinedQty_qty:", quentity);
                                Log.e("combinedQty_price:", combinationProductDetailsArrayList.get(k).getPrice());

                                if (CartProductQtysum >= combinedQty) { // If total cart product greater/equal than combined quantity
                                    priceMap.put(itemListContent.get(i).get("product_id"), String.valueOf(price));
                                    Log.e("combined_price", String.valueOf(price));
                                    Log.e("sum", String.valueOf(CartProductQtysum));
                                    Log.e("combinedqty", String.valueOf(combinedQty) + CartProductQtysum);
                                    vatMap.put(itemListContent.get(i).get("product_id"), String.valueOf(CheckVat(itemListContent.get(i).get("product_id"))));

                                    combination_idMap.put(itemListContent.get(i).get("product_id"), combinationPricesArrayList.get(j).get_id());
                                    price_idMap.put(itemListContent.get(i).get("product_id"), String.valueOf(priceId));
                                    TempData.PriceCombinationID = String.valueOf(priceId);

                                    // break;
                                }
                            }
                        }
                    }

                    Log.e("flat_price:", pricesArrayList.get(0).getPrice());
                }

                Log.e("startLog", "end");

            }
        } catch (Exception e) {
            Log.e("startLog", e.getMessage());

            e.printStackTrace();
        }


        discountmap.clear();
        discountmapview.clear();
        discounttype.clear();
        discountoffer.clear();
        discountofferPolicyid.clear();

        Total_Discount = 0.0;
        Discounttext = "";
        txtBonusPolicy.setText("");
        discount_details.setText(Discounttext);
        root_product_id.clear();
        savePreference("tqty", "0");
        discount.setText("0.0");
        policy_ids.clear();


        Total_Discount = 0.0;
        Discounttext = "";
        txtBonusPolicy.setText("");
        discount_details.setText(Discounttext);
        root_product_id.clear();
        savePreference("tqty", "0");
        discount.setText("0.0");
        policy_ids.clear();


        double sum = 0.0;
        double sumvat = 0.0;

        try {

            for (int i = 0; i < itemListContent.size(); i++) {
                HashMap<String, String> map = itemListContent.get(i);
                Log.e("Tgeneral price list", priceMap.get(productIDList.get(i)) + "loth");
                Log.e("", "XXXXXXXXXXXXXXXXXXXXXXXXX  quantity " + map.get("quantity"));
                sum = sum + (Double.parseDouble(priceMap.get(productIDList.get(i))) * Double.parseDouble(map.get("quantity")));

                String vatget = vatMap.get(productIDList.get(i));
                Log.e("vatsvalue", vatget);

                Double val = calculateVat(priceMap.get(productIDList.get(i)), vatget, map.get("quantity"), map.get("product_id"));
                vatvalue = String.valueOf(val);
                Log.e("vatssvaluess", String.valueOf(vat));

                //Log.e("vatval",vatvalue);
                sumvat = sumvat + Double.parseDouble(vatvalue);
            }

            Log.e("sumvat", String.valueOf(sumvat));
            Log.e("sumv", String.valueOf(sum));

        } catch (Exception e) {
            e.printStackTrace();
        }


        BonusPolicy(specialGroupIds, cartProductIDs, sum);
        Log.e("priceMap", priceMap.toString());
        for (int i = 0; i < itemListContent.size(); i++) {

            if (getPreference("up" + itemListContent.get(i).get("product_id")).equalsIgnoreCase("0")) {
                itemListContent.get(i).put("general_price", priceMap.get(productIDList.get(i)));
                Log.e("pricevalue", "vvv value=  " + priceMap.get(productIDList.get(i)));
            } else if (getPreference("up" + itemListContent.get(i).get("product_id")).equalsIgnoreCase("")) {
                itemListContent.get(i).put("general_price", priceMap.get(productIDList.get(i)));
                Log.e("pricevalue", "vvv value=  " + priceMap.get(productIDList.get(i)));
            } else {
                itemListContent.get(i).put("general_price", getPreference("up" + itemListContent.get(i).get("product_id")));
                Log.e("price", "++++++++++ general_price  " + getPreference("up" + itemListContent.get(i).get("product_id")));
                Log.e("pricevalue", "i value=  " + i);
            }

            itemListContent.get(i).put("vat", vatMap.get(productIDList.get(i)));

        }

        sum = 0.0;
        vat = 0.0;

        Double SP_discount = 0.0;
        Double totalPrice = 0.0;
        Double discountval = 0.0;
        String [] sp_products = {"135","136","137","138","142","143","144","145","146"};
        List<String> sp_product_list = Arrays.asList(sp_products);
        List<HashMap<String,String>> products = getSelectedProduct();


        try {
            for (int i = 0; i < itemListContent.size(); i++) {

                HashMap<String, String> map = itemListContent.get(i);
                Log.e("price",map.get("general_price")+" "+map.get("quantity"));
                sum = sum + (Double.parseDouble(map.get("general_price")) * Double.parseDouble(map.get("quantity")));
                for (int ini=0; ini<products.size() ; ini++) {
                    if (Arrays.asList(products.get(ini).get("products").split(",")).contains(map.get("product_id"))) {

                        products.get(ini).put("total_price", String.valueOf(Double.parseDouble(products.get(ini).get("total_price"))+(Double.parseDouble(priceMap.get(productIDList.get(i))) * Double.parseDouble(map.get("quantity")))));
                    }
                }
                vatCalculation(map.get("product_id"), Double.parseDouble(map.get("quantity")), Double.parseDouble(map.get("general_price")), i);
            }
            for (int t=0; t<products.size(); t++) {
                HashMap<String,String> discountDetails = getDiscountDetails(products.get(t).get("id"),products.get(t).get("total_price"));

                if (!discountDetails.containsKey("total_price")){
                    continue;
                }
                if (Double.parseDouble(products.get(t).get("total_price")) >=Double.parseDouble(discountDetails.get("total_price"))  && checkEligiblity(discountDetails.get("start_date"),discountDetails.get("end_date"))) {

                for (int i = 0; i < itemListContent.size(); i++) {
                    HashMap<String, String> map = itemListContent.get(i);
                    if (Arrays.asList(products.get(t).get("products").split(",")).contains(map.get("product_id"))) {

                        discountval=((Double.parseDouble(priceMap.get(productIDList.get(i))))* Double.parseDouble(discountDetails.get("percentage")))/100;
                        Log.e("spdisc", "UpdatePrice1: "+discountval );
                        SP_discount+=discountval*Double.parseDouble(map.get("quantity"));
                        //discount10.put(map.get("product_id"),discountval.toString());
                        discountmap.put(map.get("product_id"),discountval.toString());
                        discounttype.put(map.get("product_id"),"0");

                        Discounttext = Discounttext + getDiscountText(map.get("product_id"), roundTwoDecimal(discountval*Double.parseDouble(map.get("quantity"))));
                        discount_details.setText(Discounttext);
                        discount_details.setVisibility(View.VISIBLE);
                    }
                }

                }
            }

            txtTotal.setText(String.valueOf(roundTwoDecimals(Double.parseDouble("" + sum))));//;//String.valueOf(((ParentActivity) context).roundTwoDecimals(Double.parseDouble(""+sum))));
            vattxt.setText(roundTwoDecimal(vat));
            getDiscount(SP_discount,roundTwoDecimals(Double.parseDouble("" + sum)));
        } catch (Exception e) {
            e.printStackTrace();
        }


        notifyDataSetChanged();
    }

    private HashMap<String, String> getDiscountDetails(String id,String total_price) {
        Cursor c = db.rawQuery("select * from discount_policy where dis_policy='"+id+"' and total_price<="+total_price+"  ORDER by total_price DESC LIMIT 1");
        HashMap<String,String> map = new HashMap();
        c.moveToFirst();
        if (c.getCount()>0 && c!=null){
            do {

                map.put("start_date",c.getString(2));
                map.put("end_date",c.getString(3));
                map.put("total_price",c.getString(4));
                map.put("percentage",c.getString(5));
                map.put("dis_policy",c.getString(1));


            }while (c.moveToNext());


        }

        Log.e("discount_policy", map.toString() );

        return map;

    }
    private List<HashMap<String, String>> getSelectedProduct() {
        Cursor c = db.rawQuery("select * from discount_policy_products");
        ArrayList<HashMap<String, String>> list = new ArrayList();
        c.moveToFirst();
        if (c.getCount()>0 && c!=null){
            do {
                HashMap<String,String> map = new HashMap<>();
                map.put("products",c.getString(1));
                map.put("id",c.getString(0));
                map.put("total_price","0");
                list.add(map);
            }while (c.moveToNext());


        }

        Log.e("discount_policy_products", list.toString() );

        return list;

    }
    private boolean checkEligiblity(String start_date , String end_date) throws ParseException {
        Boolean isEligible = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date from_date = sdf.parse(start_date);
        Date to_date = sdf.parse(end_date);
        Date m_date = sdf.parse(memodate);

        if (m_date.getTime()>=from_date.getTime() && m_date.getTime()<=to_date.getTime()){
            isEligible = true;
        }

        Log.e("checkEligiblity", "checkEligiblity: "+isEligible );
        return isEligible;

    }
    @SuppressLint("SetTextI18n")
    public void BonusPolicy(String specialGroupIds, String cartProductIDs, Double memoTotal) {

        OldBPSelected_bonus.clear();
        OldBPSelected_set.clear();
        OldBPSelected_policy_type.clear();
        OldBPSelected_option_id.clear();

        OldBPSelected_bonus.putAll(BPSelected_bonus);
        OldBPSelected_set.putAll(BPSelected_set);
        OldBPSelected_policy_type.putAll(BPSelected_policy_type);
        OldBPSelected_option_id.putAll(BPSelected_option_id);

        Log.e("Selected_bonus_old", new Gson().toJson(OldBPSelected_policy_type));
        Log.e("Selected_bonus", new Gson().toJson(OldBPSelected_policy_type));

        BPSelected_bonus.clear();
        BPSelected_product.clear();
        BPSelected_set.clear();
        BPSelected_policy_type.clear();
        BPSelected_option_id.clear();
        policyArrayList.clear();
        PolicySetRelation.clear();

        Log.e("Selected_bonus_old", new Gson().toJson(OldBPSelected_policy_type));
        Double CartProductQtysum = 0.0;

        ArrayList<String> policyIds = new ArrayList<String>();

        policyIdsArrayList = db.getPolicy_ID_List(specialGroupIds, cartProductIDs, memodate, outlet_ID, TempData.OutletCatagoryID); // getting policy ids list

        TempData.policyIdsArrList = policyIdsArrayList;

        if (policyIdsArrayList.size() == 0)
            return;
        for (int i = 0; i < policyIdsArrayList.size(); i++) {
            policyIds.add(policyIdsArrayList.get(i).getPolicy_id());
        }

        String policyIdsString = TextUtils.join(", ", policyIds); // Convert policy ids arrayList to string
        Log.e("policy_id_string", String.valueOf(policyIdsString));
        policyProductArrayList = db.getPolicy_Product_List(policyIdsString);
        if (policyProductArrayList.size() == 0)
            return;

        //-------- making policy wise product list start------------------
        String policyId = policyProductArrayList.get(0).getPolicy_id();
        StringBuilder policyProductId = new StringBuilder();
        policyProductId.append(policyProductArrayList.get(0).getRoot_product_id());
        HashMap<String, String> policyProductMap = new HashMap<>();

        for (int p = 1; p < policyProductArrayList.size(); p++) {
            if (!policyId.equals(policyProductArrayList.get(p).getPolicy_id())) {
                policyProductMap.put(policyId, String.valueOf(policyProductId));
                policyId = policyProductArrayList.get(p).getPolicy_id();
                policyProductId.setLength(0);
                policyProductId.append(policyProductArrayList.get(p).getRoot_product_id());

            } else {
                policyProductId.append(",").append(policyProductArrayList.get(p).getRoot_product_id());
            }
        }
        policyProductMap.put(policyId, String.valueOf(policyProductId));
        Log.i(TAG, "policyProductArrayList: " + new Gson().toJson(policyProductMap));

        //-------- making policy wise product list--- end------------------------------------------------------------------------------

        for (int i = 0; i < policyIdsArrayList.size(); i++) {
            String policyProductsId = policyProductMap.get(policyIdsArrayList.get(i).getPolicy_id());
            if (policyProductsId == null) {
                continue;
            }
            Log.e("policyProductsId", policyProductsId);

            String[] policyProductsIdArray = policyProductsId.split(",");
            Log.i(TAG, "policyProductsIdArray: " + new Gson().toJson(policyProductsIdArray));
            CartProductQtysum = 0.0;
            for (int ar = 0; ar < policyProductsIdArray.length; ar++) {
                Integer product_id = Integer.valueOf(policyProductsIdArray[ar]);
                String quentity = getPreference(String.valueOf(product_id));
                Log.e("quentiry_product_id", quentity + "---" + product_id);
                CartProductQtysum += ParseDouble(quentity);
            }

            Log.e("product_id_quentity", String.valueOf(CartProductQtysum));

            policyWiseSlabArrayList = db.getPolicy_Wise_Slab_List(policyIdsArrayList.get(i).getPolicy_id(), String.valueOf(CartProductQtysum));
            Log.i(TAG, "policyWiseSlabArrayList: " + new Gson().toJson(policyWiseSlabArrayList));

              /*
                  Po ==policy slab
             */
            if (policyWiseSlabArrayList.size() == 0)
                continue;

            int effective_options_slab_index = 99999;//

            for (int po = 0; po < policyWiseSlabArrayList.size(); po++) {
                String minMemoVal = policyWiseSlabArrayList.get(po).getMin_memo_val();
                Log.e("minMemoVal", String.valueOf(minMemoVal));
                Log.e("minMemoVal_", String.valueOf(memoTotal));

                if (!minMemoVal.equals("null") && !minMemoVal.equals("0") && Double.valueOf(minMemoVal) > memoTotal) {
                    Log.e("minMemoVal__", String.valueOf(memoTotal));

                    continue;
                }

                exclusionArrayList = db.getExclusion_Product_List(policyWiseSlabArrayList.get(po).getOption_id());
                int is_excluded = 0;
                Log.i(TAG, "exclusionArrayList: " + new Gson().toJson(exclusionArrayList));
                if (exclusionArrayList.size() > 0) {
                    for (int exc = 0; exc < exclusionArrayList.size(); exc++) {
                        int exProductId = Integer.parseInt(exclusionArrayList.get(exc).getProduct_id());
                        String cartProductId = getPreference(String.valueOf(exProductId));
                        Log.e("cartProductId", cartProductId);

                        String cartProductQty = getPreference(String.valueOf(exProductId));

                        try {
                            String exMinQty = exclusionArrayList.get(exc).getMin_qty();

                            if (!exMinQty.equals("null") && !exMinQty.equals("0.0") && !exMinQty.equals("0")) {
                                if (Double.parseDouble(cartProductQty) >= Double.parseDouble(exMinQty)) {
                                    is_excluded = 1;
                                    break;
                                }
                            } else {
                                if (!cartProductId.equals("0.0")) {
                                    is_excluded = 1;
                                    break;
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (is_excluded == 1) {
                    continue;
                }

                inclusionArrayList = db.getInclusion_Product_List(policyWiseSlabArrayList.get(po).getOption_id());
                int is_included = 1;
                Log.i(TAG, "inclusionArrayList: " + new Gson().toJson(inclusionArrayList));
                if (inclusionArrayList.size() > 0) {
                    for (int in = 0; in < inclusionArrayList.size(); in++) {

                        int exProductId = Integer.parseInt(inclusionArrayList.get(in).getProduct_id());

                        String cartProductId = getPreference(String.valueOf(exProductId));

                        Log.e("cartProductId", cartProductId);

                        String cartProductQty = getPreference(String.valueOf(exProductId));

                        String exMinQty = inclusionArrayList.get(in).getMin_qty();

                        if (!exMinQty.equals("null") && !exMinQty.equals("0.0") && !exMinQty.equals("0")) {
                            if (Double.parseDouble(cartProductQty) < Double.parseDouble(exMinQty)) {
                                is_included = 0;
                                break;
                            }
                        } else {
                            if (cartProductId.equals("0.0")) {
                                is_included = 0;
                                break;
                            }
                        }
                    }
                }

                if (is_included == 0) {
                    continue;
                }
                effective_options_slab_index = po;
                break;
            }

            if (effective_options_slab_index == 99999) {
                continue;
            }
            Log.e("policy_id_main_loop", policyIdsArrayList.get(i).getPolicy_name());
            Log.e("effectiv_option", String.valueOf(effective_options_slab_index));
            Log.i(TAG, "effectie_price_slab: " + new Gson().toJson(policyWiseSlabArrayList.get(effective_options_slab_index)));

            int policy_type = Integer.parseInt(policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_type());
            TempData.policy_type = policy_type;
            Log.e("Policy_type", String.valueOf(policy_type));

            if (
                    OldBPSelected_option_id.get(policyIdsArrayList.get(i).getPolicy_id()) != null
                            && !OldBPSelected_option_id.get(policyIdsArrayList.get(i).getPolicy_id()).equals(policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id())
            ) {

                Log.e("Selected_option_id", OldBPSelected_option_id.get(policyIdsArrayList.get(i).getPolicy_id()) + "----" + policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id());
                OldBPSelected_bonus.remove(policyIdsArrayList.get(i).getPolicy_id());
                OldBPSelected_set.remove(policyIdsArrayList.get(i).getPolicy_id());
                OldBPSelected_policy_type.remove(policyIdsArrayList.get(i).getPolicy_id());
            }
            BPSelected_option_id.put(policyIdsArrayList.get(i).getPolicy_id(), policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id());

            if (policy_type == 0 || policy_type == 2) {

                for (int ar = 0; ar < policyProductsIdArray.length; ar++) {
                    Integer product_id = Integer.valueOf(policyProductsIdArray[ar]);

                    setOnlydiscount(policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getDiscount_type(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getDiscount_amt(),
                            String.valueOf(product_id),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id(),
                            "0",
                            policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_type()
                    );
                }

            }

            if (policy_type == 1 || policy_type == 2) {
                Quantity = String.valueOf(CartProductQtysum);

                if (policyWiseSlabArrayList.get(effective_options_slab_index).getFormula().equals("")) {
                    onlyBonus(
                            policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_type(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id(),
                            policyIdsArrayList.get(i).getPolicy_name(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getMin_qty());


                }// end  if is  formula is null or empty

                else {

                    ArrayList<HashMap<String, String>> formulaArrayMap = parseFormula(
                            policyWiseSlabArrayList.get(effective_options_slab_index).getFormula(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id());

                    formulaBonus(
                            policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_type(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id(),
                            policyIdsArrayList.get(i).getPolicy_name(),
                            policyWiseSlabArrayList.get(effective_options_slab_index).getMin_qty(),
                            formulaArrayMap);
                    // else  formula found ---------------------------------------------------------

                }
            }

            if (policy_type == 3) {
                Quantity = String.valueOf(CartProductQtysum);
                String selected_policy_type = "1";
                if (OldBPSelected_policy_type.get(policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id()) != null) {
                    selected_policy_type = OldBPSelected_policy_type.get(policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id());
                }
                BPSelected_policy_type.put(policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id(), selected_policy_type);
                HashMap<String, String> policyMap = new HashMap<>();
                policyMap.put("policy_type", policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_type());
                policyMap.put("option_id", policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id());
                policyMap.put("policy_id", policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id());
                policyMap.put("policy_name", policyIdsArrayList.get(i).getPolicy_name());
                policyMap.put("formula", policyWiseSlabArrayList.get(effective_options_slab_index).getFormula());
                policyMap.put("min_qty", policyWiseSlabArrayList.get(effective_options_slab_index).getMin_qty());
                policyMap.put("combinedQty", Quantity);
                policyMap.put("setSelection", "1");
                policyMap.put("disSelection", selected_policy_type);

                policyArrayList.add(policyMap);
                Log.e("PolicyArrayList_3", new Gson().toJson(policyArrayList));
                if (selected_policy_type.equals("1")) {
                    for (int ar = 0; ar < policyProductsIdArray.length; ar++) {
                        Integer product_id = Integer.valueOf(policyProductsIdArray[ar]);
                        setOnlydiscount(policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getDiscount_type(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getDiscount_amt(),
                                String.valueOf(product_id),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id(),
                                "0",
                                policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_type()
                        );
                    }
                } else {
                    if (policyWiseSlabArrayList.get(effective_options_slab_index).getFormula().equals("")) {
                        onlyBonus(
                                policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_type(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id(),
                                policyIdsArrayList.get(i).getPolicy_name(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getMin_qty());


                    }// end  if is  formula is null or empty

                    else {

                        ArrayList<HashMap<String, String>> formulaArrayMap = parseFormula(
                                policyWiseSlabArrayList.get(effective_options_slab_index).getFormula(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id()
                        ); // Parse mula
                        formulaBonus(
                                policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_type(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getOption_id(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getPolicy_id(),
                                policyIdsArrayList.get(i).getPolicy_name(),
                                policyWiseSlabArrayList.get(effective_options_slab_index).getMin_qty(),
                                formulaArrayMap
                        );
                        // else  formula found ----

                    }
                }

            }

            for (int ar = 0; ar < policyProductsIdArray.length; ar++) {
                if (discountmapview.get(policyProductsIdArray[ar]) != null) {
                    Double discount = Double.parseDouble(discountmapview.get(policyProductsIdArray[ar])) * Double.parseDouble(getPreference(policyProductsIdArray[ar]));
                    Log.e("discount=", discount + " " + discountmapview.get(policyProductsIdArray[ar]) + " " + Double.parseDouble(getPreference(policyProductsIdArray[ar])));
                    Log.e("discount_____", String.valueOf(discount));

                    if (Double.parseDouble(roundTwoDecimal(discount)) > 0.0) {
                        Discounttext = Discounttext + getDiscountText(policyProductsIdArray[ar], roundTwoDecimal(discount));
                        discount_data = Discounttext;
                        Log.i(TAG, "discount_data_: " + new Gson().toJson(discount_data));
                    }
                    Total_Discount = Total_Discount + discount;
                }
            }

            if (Total_Discount > 0.0) {

                discount.setText(roundTwoDecimal(Total_Discount) + "");
                discount.setVisibility(View.VISIBLE);
                discount_details.setText(discount_data.substring(0, Discounttext.length() - 1));

            } else{
               // discount.setVisibility(View.GONE);
            }

            discountmapview.clear();
        }

        Log.i(TAG, "edit_data_remove: " + new Gson().toJson(TempData.edit_data_remove));

        Log.e("BonusSelectedProduct",new Gson().toJson(BPSelected_product));

        setBPProductBonus();

    }

    private Double calculateVat(String general_price, String vatvalue, String quantity, String product_id) {

        String vatval = "";

        if (vatvalue.equals("null")) {
            vatval = "0.00";
        } else {
            vatval = vatvalue;
        }

        Double vatvalues = Double.parseDouble(vatval);
        vatvalues = vatvalues + 100;


        Double product_price = Double.parseDouble(general_price);
        if (discountmap.get(product_id) != null) {
            Log.e("product_priceb" + product_id, product_price + "");
            product_price = product_price - Double.parseDouble(discountmap.get(product_id));
            Log.e("product_pricea" + product_id, product_price + "");
        }
        Double vatprice = product_price - product_price * 100 / vatvalues;
        Double totalvat = vatprice * Double.parseDouble(quantity);
        return totalvat;
    }

    public void onlyBonus(String policyType, String optionId, String policyId, String policyName, String minQty) {
        policyBonusProductArrayList = db.getBonusProductList(optionId, "");
        policyBonusProductArrList = policyBonusProductArrayList;
        Log.i(TAG, "policyBonusProductArrayList: " + new Gson().toJson(policyBonusProductArrayList));

        if (policyBonusProductArrayList.size() >= 2) {
            HashMap<String, String> policyMap = new HashMap<String, String>();
            policyMap.put("policy_type", policyType);
            policyMap.put("option_id", optionId);
            policyMap.put("policy_id", policyId);
            policyMap.put("policy_name", policyName);
            if (!policyType.equals("3")) {
                policyArrayList.add(policyMap);
            }
        }

        double provide_qty = 0.0;

        ArrayList<HashMap<String, String>> bonus_list_view = new ArrayList<HashMap<String, String>>();

        HashMap<String, HashMap<String, HashMap<String, String>>> set_map = new HashMap<>();
        HashMap<String, HashMap<String, String>> product_map = new HashMap<>();
        String set = "1";

        for (int vs = 0; vs < policyBonusProductArrayList.size(); vs++) {

            String bonus_qty = policyBonusProductArrayList.get(vs).getBonus_qty();

            String bonus_product_id = policyBonusProductArrayList.get(vs).getBonus_product_id();

            bonus_qty = String.valueOf(((Double.parseDouble(bonus_qty) * Double.parseDouble(Quantity)) / Double.parseDouble(minQty))); // Bonus Policy Change nasir vai for Joya 8 s belt

            bonus_qty = String.valueOf(Math.floor(Float.parseFloat(bonus_qty)));
            Log.e("bonus_qty_ob", bonus_qty);
            String mesurement_unit_id = policyBonusProductArrayList.get(vs).getUnit_id();

            double stockQty = 0;
            if (bonus_product_id.equalsIgnoreCase("null")) {
                Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                continue;
            }

            Double bonus_value = 0.00;
            String selected = "false";
            if (OldBPSelected_bonus.get(policyId) != null
                    && OldBPSelected_bonus.get(policyId).get(set) != null
                    && (
                    (
                            OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) == null
                                    ||
                                    (
                                            OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                    && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty") == null
                                    )
                    )
                            ||
                            (
                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                            && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty")).equals(Double.valueOf(bonus_qty))
                            )
            )
            ) {
                if (
                        OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty") != null
                                && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty")) > 0.0
                ) {
                    bonus_value = Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty"));
                    selected = "true";
                } else {
                    bonus_value = 0.0;
                    selected = "false";
                }
            } else {
                    if(provide_qty != Double.valueOf(bonus_qty)) {
                        provide_qty+=Double.valueOf(bonus_qty);
                        bonus_value = Double.valueOf(bonus_qty);
                        selected = "true";
                    }
            }

            Log.e("bonus_value", bonus_value + "");
            HashMap<String, String> map = new HashMap<>();
            map.put("product_id", policyBonusProductArrList.get(vs).getBonus_product_id());
            map.put("policy_id", policyBonusProductArrList.get(vs).getPolicy_id());
            map.put("set", set);
            map.put("measurement_unit_id", mesurement_unit_id);
            map.put("qty", String.valueOf(bonus_value));
            map.put("provided_qty", bonus_qty);
            product_map.put(policyBonusProductArrList.get(vs).getBonus_product_id(), map);
            if (bonus_value > 0) {
                map.put("item", policyBonusProductArrayList.get(vs).getProduct_name());
                map.put("measurement_unit_name", policyBonusProductArrayList.get(vs).getUnit_name());
                map.put("policy_type", policyType);
                BPSelected_product.add(map);

            }
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("set", "1");
            map1.put("item", policyBonusProductArrayList.get(vs).getProduct_name());
            map1.put("measurement_unit_name", policyBonusProductArrayList.get(vs).getUnit_name());
            map1.put("quantity", String.valueOf(bonus_value));
            map1.put("provided_qty", bonus_qty);
            map1.put("product_id", policyBonusProductArrayList.get(vs).getBonus_product_id());
            map1.put("selected_s", selected);
            map1.put("editable", selected);
            map1.put("policy_id", policyBonusProductArrayList.get(vs).getPolicy_id());
            map1.put("measurement_unit_id", mesurement_unit_id);
            map1.put("relation", "OR");
            bonus_list_view.add(map1);

        }

        set_map.put(set, product_map);
        BPSelected_bonus.put(policyId, set_map);
        BPBonusProductView.put(policyId, bonus_list_view);
        Log.e(TAG, "BPSelected_bonus_OB: " + new Gson().toJson(BPSelected_bonus));
        Log.e(TAG, "BPSelected_product_OB: " + new Gson().toJson(BPSelected_product));
        Log.e(TAG, "BPSelected_bonus_OB: " + new Gson().toJson(BPBonusProductView));

    }

    private void formulaBonus(String policyType, String option_id, String policyId, String policyName, String minQty, ArrayList<HashMap<String, String>> formulaArrayMap) {
        ArrayList<HashMap<String, String>> bonus_list_view = new ArrayList<HashMap<String, String>>();
        if (formulaArrayMap.size() > 1) {
            if (PolicySetRelation.get(policyId).equals("AND")) {
                HashMap<String, String> policyMap = new HashMap<String, String>();

                HashMap<String, HashMap<String, HashMap<String, String>>> set_map = new HashMap<>();
                for (int fm = 0; fm < formulaArrayMap.size(); fm++) {
                    String set = String.valueOf(fm + 1);
                    HashMap<String, HashMap<String, String>> product_map = new HashMap<>();

                    if (formulaArrayMap.get(fm).get("relation").equals("AND")) {
                        double provide_qty = 0.0;

                        String[] elementSplit = formulaArrayMap.get(fm).get("element").split(",");

                        for (int es = 0; es < elementSplit.length; es++) {
                            ArrayList<PolicyBonusProduct> policyBonusProductArrList = db.getBonusProductList(option_id, elementSplit[es]);


                            String bonus_qty = policyBonusProductArrList.get(0).getBonus_qty();

                            Log.e("bonusqty_from", String.valueOf(bonus_qty));

                            String bonus_product_id = policyBonusProductArrList.get(0).getBonus_product_id();
                            Log.e("bonusqty_check_quantity", String.valueOf(Quantity));

                            bonus_qty = String.valueOf(((Double.parseDouble(bonus_qty) * Double.parseDouble(Quantity)) / Double.parseDouble(minQty))); // Bonus Policy Change nasir vai for Joya 8 s belt

                            bonus_qty = String.valueOf(Math.floor(Float.parseFloat(bonus_qty)));

                            String mesurement_unit_id = policyBonusProductArrList.get(0).getUnit_id();

                            Log.e("bonusqty_check_quantity", String.valueOf(Quantity));
                            Log.e("policy_min_quantity", minQty);
                            Log.e("bonusqty_check", String.valueOf(Math.round(Float.parseFloat(bonus_qty))));

                            double stockQty = 0;
                            if (bonus_product_id.equalsIgnoreCase("null")) {
                                Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                                continue;
                            }
                            Double bonus_value = 0.00;
                            String selected = "false";


                            if (
                                    OldBPSelected_bonus.get(policyId) != null
                                            && OldBPSelected_bonus.get(policyId).get(set) != null
                                            && (
                                            (
                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) == null
                                                            ||
                                                            (
                                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                            && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty") == null
                                                            )
                                            )
                                                    ||
                                                    (
                                                            OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                    && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty")).equals(Double.valueOf(bonus_qty))
                                                    )
                                    )

                            ) {
                                if (
                                        OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null &&
                                                OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty") != null
                                                && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty")) > 0.0
                                ) {
                                    bonus_value = Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty"));
                                    selected = "true";
                                } else {
                                    bonus_value = 0.0;
                                    selected = "false";
                                }
                            } else {
                                if(provide_qty != Double.valueOf(bonus_qty)) {
                                    provide_qty+=Double.valueOf(bonus_qty);
                                    bonus_value = Double.valueOf(bonus_qty);
                                    selected = "true";
                                }
                            }


                            HashMap<String, String> map = new HashMap<>();
                            map.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                            map.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                            map.put("set", set);
                            map.put("measurement_unit_id", mesurement_unit_id);
                            map.put("qty", String.valueOf(bonus_value));
                            map.put("provided_qty", bonus_qty);
                            product_map.put(policyBonusProductArrList.get(0).getBonus_product_id(), map);
                            if (bonus_value > 0) {
                                map.put("item", policyBonusProductArrList.get(0).getProduct_name());
                                map.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                                map.put("policy_type", policyType);
                                BPSelected_product.add(map);
                            }
                            HashMap<String, String> map1 = new HashMap<>();
                            map1.put("item", policyBonusProductArrList.get(0).getProduct_name());
                            map1.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                            map1.put("set", set);
                            map1.put("quantity", String.valueOf(bonus_value));
                            map1.put("provided_qty", bonus_qty);
                            map1.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                            map1.put("selected_s", selected);
                            map1.put("editable", selected);
                            map1.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                            map1.put("measurement_unit_id", mesurement_unit_id);
                            map1.put("relation", "AND");
                            bonus_list_view.add(map1);

                        }
                    }//-------------------------end of AND

                    else if (formulaArrayMap.get(fm).get("relation").equals("OR")) {
                        double provide_qty = 0.0;

                        policyMap.put("policy_type", policyType);
                        policyMap.put("option_id", option_id);
                        policyMap.put("policy_id", policyId);
                        policyMap.put("policy_name", policyName);
                        policyMap.put("min_qty", minQty);
                        policyMap.put("combinedQty", Quantity);
                        policyMap.put("setSelection", "1");

                        String[] elementSplit = formulaArrayMap.get(fm).get("element").split(",");


                        for (int es = 0; es < elementSplit.length; es++) {
                            ArrayList<PolicyBonusProduct> policyBonusProductArrList = db.getBonusProductList(option_id, elementSplit[es]);
                            String bonus_qty = policyBonusProductArrList.get(0).getBonus_qty();
                            String bonus_product_id = policyBonusProductArrList.get(0).getBonus_product_id();

                            bonus_qty = String.valueOf(((Double.parseDouble(bonus_qty) * Double.parseDouble(Quantity)) / Double.parseDouble(minQty))); // Bonus Policy Change nasir vai for Joya 8 s belt

                            bonus_qty = String.valueOf(Math.floor(Float.parseFloat(bonus_qty)));

                            String mesurement_unit_id = policyBonusProductArrList.get(0).getUnit_id();

                            Log.e("bonusqty_check_quantity", String.valueOf(Quantity));
                            Log.e("policy_min_quantity", minQty);
                            Log.e("bonusqty_check", String.valueOf(Math.round(Float.parseFloat(bonus_qty))));

                            double stockQty = 0;
                            if (bonus_product_id.equalsIgnoreCase("null")) {
                                Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                                continue;
                            }

                            Double bonus_value = 0.00;
                            String selected = "false";


                            if (
                                    OldBPSelected_bonus.get(policyId) != null
                                            && OldBPSelected_bonus.get(policyId).get(set) != null
                                            && (
                                            (
                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) == null
                                                            ||
                                                            (
                                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                            && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty") == null
                                                            )
                                            )
                                                    ||
                                                    (
                                                            OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                    && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty")).equals(Double.valueOf(bonus_qty))
                                                    )
                                    )

                            ) {
                                if (
                                        OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null &&
                                                OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty") != null
                                                && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty")) > 0.0
                                ) {
                                    bonus_value = Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty"));
                                    selected = "true";
                                } else {
                                    bonus_value = 0.0;
                                    selected = "false";
                                }
                            } else {

                                if(provide_qty != Double.valueOf(bonus_qty)) {
                                    provide_qty+=Double.valueOf(bonus_qty);
                                    bonus_value = Double.valueOf(bonus_qty);
                                    selected = "true";
                                }
                            }


                            Log.e("bonus_value", bonus_value + "");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                            map.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                            map.put("set", set);
                            map.put("measurement_unit_id", mesurement_unit_id);
                            map.put("qty", String.valueOf(bonus_value));
                            map.put("provided_qty", bonus_qty);
                            product_map.put(policyBonusProductArrList.get(0).getBonus_product_id(), map);
                            if (bonus_value > 0) {
                                map.put("item", policyBonusProductArrList.get(0).getProduct_name());
                                map.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                                map.put("policy_type", policyType);
                                BPSelected_product.add(map);
                            }
                            HashMap<String, String> map1 = new HashMap<>();
                            map1.put("item", policyBonusProductArrList.get(0).getProduct_name());
                            map1.put("set", set);
                            map1.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                            map1.put("quantity", String.valueOf(bonus_value));
                            map1.put("provided_qty", bonus_qty);
                            map1.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                            map1.put("selected_s", selected);
                            map1.put("editable", selected);
                            map1.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                            map1.put("measurement_unit_id", mesurement_unit_id);
                            map1.put("relation", "OR");
                            bonus_list_view.add(map1);
                        }

                        Log.i(TAG, "BPbonus_product: " + new Gson().toJson(BPbonus_product));
                        Log.i(TAG, "BPbonus_product: " + new Gson().toJson(BPbonus_product_t));

                    } // end of OR---------------
                    set_map.put(set, product_map);
                }
                BPSelected_bonus.put(policyId, set_map);
                if (!policyType.equals("3")) {
                    policyArrayList.add(policyMap);
                }

            } else if (PolicySetRelation.get(policyId).equals("OR")) {
                String selected_set = "1";
                Log.e("selected_set", selected_set);
                Log.e("selected_set_map_old", new Gson().toJson(OldBPSelected_set));
                if (OldBPSelected_set.get(policyId) != null) {
                    selected_set = OldBPSelected_set.get(policyId);
                }
                BPSelected_set.put(policyId, selected_set);
                Log.e("selected_set", selected_set);
                Log.e("selected_set_map", new Gson().toJson(BPSelected_set));
                HashMap<String, String> policyMap = new HashMap<>();
                policyMap.put("policy_type", policyType);
                policyMap.put("option_id", option_id);
                policyMap.put("policy_id", policyId);
                policyMap.put("policy_name", policyName);
                policyMap.put("min_qty", minQty);
                policyMap.put("combinedQty", Quantity);
                policyMap.put("setSelection", selected_set);
                if (!policyType.equals("3")) {
                    policyArrayList.add(policyMap);
                }
                int from_set = 0;
                int end_set = 1;
                if (selected_set.equals("1")) {
                    from_set = 0;
                    end_set = 1;
                } else {
                    from_set = 1;
                    end_set = formulaArrayMap.size();
                }
                Log.e("selected_from_set", from_set + "---" + end_set);
                for (int fm = from_set; fm < end_set; fm++) {
                    HashMap<String, HashMap<String, HashMap<String, String>>> set_map = new HashMap<>();
                    String set = String.valueOf(fm + 1);
                    HashMap<String, HashMap<String, String>> product_map = new HashMap<>();
                    if (formulaArrayMap.get(fm).get("relation").equals("AND")) {
                        Log.e("ProductRelation", formulaArrayMap.get(fm).get("element"));
                        String[] elementSplit = formulaArrayMap.get(fm).get("element").split(",");

                        for (int es = 0; es < elementSplit.length; es++) {
                            ArrayList<PolicyBonusProduct> policyBonusProductArrList = db.getBonusProductList(option_id, elementSplit[es]);

                            Log.e("fm", fm + "");

                            String bonus_qty = policyBonusProductArrList.get(0).getBonus_qty();

                            Log.e("bonusqty_from", String.valueOf(bonus_qty));

                            String bonus_product_id = policyBonusProductArrList.get(0).getBonus_product_id();
                            Log.e("bonusqty_check_quantity", String.valueOf(Quantity));

                            bonus_qty = String.valueOf(((Double.parseDouble(bonus_qty) * Double.parseDouble(Quantity)) / Double.parseDouble(minQty))); // Bonus Policy Change nasir vai for Joya 8 s belt

                            bonus_qty = String.valueOf(Math.floor(Float.parseFloat(bonus_qty)));

                            String mesurement_unit_id = policyBonusProductArrList.get(0).getUnit_id();

                            Log.e("bonusqty_check_quantity", String.valueOf(Quantity));
                            Log.e("policy_min_quantity", minQty);
                            Log.e("bonusqty_check", String.valueOf(Math.round(Float.parseFloat(bonus_qty))));

                            double stockQty = 0;
                            if (bonus_product_id.equalsIgnoreCase("null")) {
                                Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                                continue;
                            }


                            Double bonus_value = 0.00;
                            String selected = "false";


                            bonus_value = Double.valueOf(bonus_qty);

                            selected = "true";

                            HashMap<String, String> map = new HashMap<>();

                            map.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                            map.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                            map.put("set", set);
                            map.put("measurement_unit_id", mesurement_unit_id);
                            map.put("qty", String.valueOf(bonus_value));
                            map.put("provided_qty", bonus_qty);
                            product_map.put(policyBonusProductArrList.get(0).getBonus_product_id(), map);
                            if (bonus_value > 0) {
                                map.put("item", policyBonusProductArrList.get(0).getProduct_name());
                                map.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                                map.put("policy_type", policyType);
                                Log.e("selected_map", new Gson().toJson(map));
                                BPSelected_product.add(map);
                            }
                            HashMap<String, String> map1 = new HashMap<>();
                            map1.put("item", policyBonusProductArrList.get(0).getProduct_name());
                            map1.put("set", set);
                            map1.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                            map1.put("quantity", String.valueOf(bonus_value));
                            map1.put("provided_qty", bonus_qty);
                            map1.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                            map1.put("selected_s", selected);
                            map1.put("editable", selected);
                            map1.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                            map1.put("measurement_unit_id", mesurement_unit_id);
                            map1.put("relation", "AND");
                            bonus_list_view.add(map1);
                            Log.e("stockQty", stockQty + "");

                        }
                    }//-------------------------end of AND

                    else if (formulaArrayMap.get(fm).get("relation").equals("OR")) {
                        double provide_qty = 0.0;

                        String[] elementSplit = formulaArrayMap.get(fm).get("element").split(",");


                        for (int es = 0; es < elementSplit.length; es++) {
                            ArrayList<PolicyBonusProduct> policyBonusProductArrList = db.getBonusProductList(option_id, elementSplit[es]);
                            String bonus_qty = policyBonusProductArrList.get(0).getBonus_qty();
                            String bonus_product_id = policyBonusProductArrList.get(0).getBonus_product_id();

                            bonus_qty = String.valueOf(((Double.parseDouble(bonus_qty) * Double.parseDouble(Quantity)) / Double.parseDouble(minQty))); // Bonus Policy Change nasir vai for Joya 8 s belt

                            bonus_qty = String.valueOf(Math.floor(Float.parseFloat(bonus_qty)));

                            String mesurement_unit_id = policyBonusProductArrList.get(0).getUnit_id();

                            Log.e("bonusqty_check_quantity", String.valueOf(Quantity));
                            Log.e("policy_min_quantity", minQty);
                            Log.e("bonusqty_check", String.valueOf(Math.round(Float.parseFloat(bonus_qty))));

                            double stockQty = 0;
                            if (bonus_product_id.equalsIgnoreCase("null")) {
                                Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                                continue;
                            }

                            Double bonus_value = 0.00;
                            String selected = "false";


                            if (
                                    OldBPSelected_bonus.get(policyId) != null
                                            && OldBPSelected_bonus.get(policyId).get(set) != null
                                            && (
                                            (
                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) == null
                                                            ||
                                                            (
                                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                            && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty") == null
                                                            )
                                            )
                                                    ||
                                                    (
                                                            OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                    && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty")).equals(Double.valueOf(bonus_qty))
                                                    )
                                    )

                            ) {
                                if (
                                        OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty") != null
                                                && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty")) > 0.0
                                ) {
                                    bonus_value = Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty"));
                                    selected = "true";
                                } else {
                                    bonus_value = 0.0;
                                    selected = "false";
                                }
                            } else {

                                if(provide_qty != Double.valueOf(bonus_qty)) {
                                    provide_qty+=Double.valueOf(bonus_qty);
                                    bonus_value = Double.valueOf(bonus_qty);
                                    selected = "true";
                                }
                            }



                            Log.e("bonus_value", bonus_value + "");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                            map.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                            map.put("set", set);
                            map.put("measurement_unit_id", mesurement_unit_id);
                            map.put("qty", String.valueOf(bonus_value));
                            map.put("provided_qty", bonus_qty);
                            product_map.put(policyBonusProductArrList.get(0).getBonus_product_id(), map);
                            if (bonus_value > 0) {
                                map.put("item", policyBonusProductArrList.get(0).getProduct_name());
                                map.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                                map.put("policy_type", policyType);
                                BPSelected_product.add(map);
                            }
                            HashMap<String, String> map1 = new HashMap<>();
                            map1.put("item", policyBonusProductArrList.get(0).getProduct_name());
                            map1.put("set", set);
                            map1.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                            map1.put("quantity", String.valueOf(bonus_value));
                            map1.put("provided_qty", bonus_qty);
                            map1.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                            map1.put("selected_s", selected);
                            map1.put("editable", selected);
                            map1.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                            map1.put("measurement_unit_id", mesurement_unit_id);
                            map1.put("relation", "OR");
                            bonus_list_view.add(map1);
                        }

                    } // end of OR---------------
                    set_map.put(set, product_map);
                    BPSelected_bonus.put(policyId, set_map);
                }

            }

        } else {
            String set = "1";
            HashMap<String, HashMap<String, HashMap<String, String>>> set_map = new HashMap<>();
            HashMap<String, HashMap<String, String>> product_map = new HashMap<>();

            if (formulaArrayMap.get(0).get("relation").equals("AND")) {
                String[] elementSplit = formulaArrayMap.get(0).get("element").split(",");
                double provide_qty = 0.0;

                for (int es = 0; es < elementSplit.length; es++) {

                    ArrayList<PolicyBonusProduct> policyBonusProductArrList = db.getBonusProductList(option_id, elementSplit[es]);

                    String bonus_qty = policyBonusProductArrList.get(0).getBonus_qty();

                    Log.e("bonusqty_from", String.valueOf(bonus_qty));

                    String bonus_product_id = policyBonusProductArrList.get(0).getBonus_product_id();
                    Log.e("bonusqty_check_quantity", String.valueOf(Quantity));

                    bonus_qty = String.valueOf(((Double.parseDouble(bonus_qty) * Double.parseDouble(Quantity)) / Double.parseDouble(minQty))); // Bonus Policy Change nasir vai for Joya 8 s belt

                    bonus_qty = String.valueOf(Math.floor(Float.parseFloat(bonus_qty)));

                    String mesurement_unit_id = policyBonusProductArrList.get(0).getUnit_id();

                    Log.e("bonusqty_check_quantity", String.valueOf(Quantity));
                    Log.e("policy_min_quantity", minQty);
                    Log.e("bonusqty_check", String.valueOf(Math.round(Float.parseFloat(bonus_qty))));

                    if (bonus_product_id.equalsIgnoreCase("null")) {
                        Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                        continue;
                    }

                    Double bonus_value = 0.00;
                    String selected = "false";


                    if (
                            OldBPSelected_bonus.get(policyId) != null
                                    && OldBPSelected_bonus.get(policyId).get(set) != null
                                    && (
                                    (
                                            OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) == null
                                                    ||
                                                    (
                                                            OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                    && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty") == null
                                                    )
                                    )
                                            ||
                                            (
                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                            && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty")).equals(Double.valueOf(bonus_qty))
                                            )
                            )

                    ) {
                        if (
                                OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                        && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty") != null
                                        && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty")) > 0.0
                        ) {
                            bonus_value = Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty"));
                            selected = "true";
                        } else {
                            bonus_value = 0.0;
                            selected = "false";
                        }
                    } else {

                        if(provide_qty != Double.valueOf(bonus_qty)) {
                            provide_qty+=Double.valueOf(bonus_qty);
                            bonus_value = Double.valueOf(bonus_qty);
                            selected = "true";
                        }
                    }

                    Log.e("bonus_value", bonus_value + "");
                    HashMap<String, String> map = new HashMap<>();

                    map.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                    map.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                    map.put("set", set);
                    map.put("measurement_unit_id", mesurement_unit_id);
                    map.put("qty", String.valueOf(bonus_value));
                    map.put("provided_qty", bonus_qty);
                    product_map.put(policyBonusProductArrList.get(0).getBonus_product_id(), map);
                    if (bonus_value > 0) {
                        map.put("item", policyBonusProductArrList.get(0).getProduct_name());
                        map.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                        map.put("policy_type", policyType);
                        BPSelected_product.add(map);
                    }
                    HashMap<String, String> map1 = new HashMap<>();
                    map1.put("item", policyBonusProductArrList.get(0).getProduct_name());
                    map1.put("set", "1");
                    map1.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                    map1.put("quantity", String.valueOf(bonus_value));
                    map1.put("provided_qty", bonus_qty);
                    map1.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                    map1.put("selected_s", selected);
                    map1.put("editable", selected);
                    map1.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                    map1.put("measurement_unit_id", mesurement_unit_id);
                    map1.put("relation", "OR");
                    bonus_list_view.add(map1);
                }
            } else if (formulaArrayMap.get(0).get("relation").equals("OR")) {
                double provide_qty = 0.0;

                HashMap<String, String> policyMap = new HashMap<String, String>();
                policyMap.put("policy_type", policyType);
                policyMap.put("option_id", option_id);
                policyMap.put("policy_id", policyId);
                policyMap.put("policy_name", policyName);
                if (!policyType.equals("3")) {
                    policyArrayList.add(policyMap);
                }

                String[] elementSplit = formulaArrayMap.get(0).get("element").split(",");

                for (int es = 0; es < elementSplit.length; es++) {
                    ArrayList<PolicyBonusProduct> policyBonusProductArrList = db.getBonusProductList(option_id, elementSplit[es]);
                    String bonus_qty = policyBonusProductArrList.get(0).getBonus_qty();
                    String bonus_product_id = policyBonusProductArrList.get(0).getBonus_product_id();

                    bonus_qty = String.valueOf(((Double.parseDouble(bonus_qty) * Double.parseDouble(Quantity)) / Double.parseDouble(minQty))); // Bonus Policy Change nasir vai for Joya 8 s belt

                    bonus_qty = String.valueOf(Math.floor(Float.parseFloat(bonus_qty)));

                    String mesurement_unit_id = policyBonusProductArrList.get(0).getUnit_id();

                    Log.e("bonusqty_check_quantity", String.valueOf(Quantity));
                    Log.e("policy_min_quantity", minQty);
                    Log.e("bonusqty_check", String.valueOf(Math.round(Float.parseFloat(bonus_qty))));


                    if (bonus_product_id.equalsIgnoreCase("null")) {
                        Toast.makeText(context, "Bonus Product Id is Null", Toast.LENGTH_LONG).show();
                        continue;
                    }

                    Double bonus_value = 0.00;
                    String selected = "false";

                    if (
                            OldBPSelected_bonus.get(policyId) != null
                                    && OldBPSelected_bonus.get(policyId).get(set) != null
                                    &&
                                    (
                                            (
                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) == null
                                                            ||
                                                            (
                                                                    OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                            && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty") == null
                                                            )
                                            )
                                                    ||
                                                    (
                                                            OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                                                    && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("provided_qty")).equals(Double.valueOf(bonus_qty))
                                                    )
                                    )

                    ) {
                        if (
                                OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id) != null
                                        && OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty") != null
                                        && Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty")) > 0.0
                        ) {
                            bonus_value = Double.valueOf(OldBPSelected_bonus.get(policyId).get(set).get(bonus_product_id).get("qty"));
                            selected = "true";
                        } else {
                            bonus_value = 0.0;
                            selected = "false";
                        }
                    } else {
                        if(provide_qty != Double.valueOf(bonus_qty)) {
                            provide_qty+=Double.valueOf(bonus_qty);
                            bonus_value = Double.valueOf(bonus_qty);
                            selected = "true";
                        }
                    }


                    Log.e("bonus_value_or", bonus_value + "");
                    HashMap<String, String> map = new HashMap<>();

                    map.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                    map.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                    map.put("set", set);
                    map.put("measurement_unit_id", mesurement_unit_id);
                    map.put("qty", String.valueOf(bonus_value));
                    map.put("provided_qty", bonus_qty);
                    product_map.put(policyBonusProductArrList.get(0).getBonus_product_id(), map);
                    if (bonus_value > 0) {
                        map.put("item", policyBonusProductArrList.get(0).getProduct_name());
                        map.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                        map.put("policy_type", policyType);
                        BPSelected_product.add(map);
                    }
                    HashMap<String, String> map1 = new HashMap<>();
                    map1.put("item", policyBonusProductArrList.get(0).getProduct_name());
                    map1.put("set", "1");
                    map1.put("measurement_unit_name", policyBonusProductArrList.get(0).getUnit_name());
                    map1.put("quantity", String.valueOf(bonus_value));
                    map1.put("provided_qty", bonus_qty);
                    map1.put("product_id", policyBonusProductArrList.get(0).getBonus_product_id());
                    map1.put("selected_s", selected);
                    map1.put("editable", selected);
                    map1.put("policy_id", policyBonusProductArrList.get(0).getPolicy_id());
                    map1.put("measurement_unit_id", mesurement_unit_id);
                    map1.put("relation", "OR");
                    bonus_list_view.add(map1);

                }

                Log.i(TAG, "BPbonus_product: " + new Gson().toJson(BPbonus_product));
                Log.i(TAG, "BPbonus_product: " + new Gson().toJson(BPbonus_product_t));

            }
            set_map.put(set, product_map);
            BPSelected_bonus.put(policyId, set_map);
        }

        BPBonusProductView.put(policyId, bonus_list_view);
    }

    public ArrayList<HashMap<String, String>> parseFormula(String str, String policy_id) {

        String[] arrOfStr = str.split(" ");
        String set_relation = "";
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        Log.e("formula_", str);

        String element = "";
        String relation = "";
        for (int a = 0; a < arrOfStr.length; a++) {
            if (arrOfStr[a].equals("(")) {
                continue;
            } else if (arrOfStr[a].equals(")")) {
                HashMap<String, String> map = new HashMap<>();
                if (relation.length() == 0) {
                    relation = "AND";
                }
                map.put("element", element);
                map.put("relation", relation);
                arrayList.add(map);
                if (a + 1 < arrOfStr.length) {
                    set_relation = arrOfStr[a + 1];
                    a++;
                }
                element = "";
                relation = "";
                continue;
            } else {
                if (arrOfStr[a].matches("-?\\d+(\\.\\d+)?")) {
                    if (element.equals("")) {

                        element += arrOfStr[a];
                        continue;

                    } else {
                        element += "," + arrOfStr[a];
                        continue;
                    }
                } else {
                    relation = arrOfStr[a];
                    // Log.e("formula_", relation);

                    continue;
                }
            }

        }
        if (!element.equals("") && relation.equals("")) {
            relation = "AND";
        }
        if (!element.equals("") && !relation.equals("")) {
            HashMap<String, String> map = new HashMap<>();
            map.put("element", element);
            map.put("relation", relation);
            arrayList.add(map);
        }

        PolicySetRelation.put(policy_id, set_relation);
        TempData.SetRelation = set_relation;
        Log.e("formula_", TempData.SetRelation);
        Log.e("PolicySetRelationmap", PolicySetRelation.toString());

        Log.i(TAG, "formula_: " + new Gson().toJson(arrayList));


        return arrayList;
    }


    private void setOnlydiscount(String option_id, String discount_type, String discount_amount, String option_product_id, String policy_id, String recall_Qty,String policy_type) {
        Log.e("dis_count_type", discount_type);

        try {
            //discount_type 0 % and 1 amount
            Cursor cursor = db.rawQuery("select pc.price, pc.slab_id from product_combinations as pc inner join policy_option_price_slab as pops on pops.slab_id = pc.slab_id where pops.policy_product_option_id='" + option_id + "' and pops.option_product_id='" + option_product_id + "' and pc.product_id = '" + option_product_id + "'", "Bonus_product_Policy6");
            Log.d("discount", DatabaseUtils.dumpCursorToString(cursor) + " : " + discount_amount);

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
                discountmapview.put(option_product_id, roundTwoDecimal(discount) + "");
                vatMap.put(option_product_id, String.valueOf(CheckVat(option_product_id)));
                price_idMap.put(option_product_id, cursor.getString(1) + ""); //51482
                discountoffer.put(option_product_id, policy_type);
                discountofferPolicyid.put(option_product_id, policy_id);

                Log.d("discountmapview", new Gson().toJson(discountmapview));
              /*  if(Double.parseDouble(recall_Qty)>0)
                setPolicyType(Double.parseDouble(recall_Qty),policy_id);*/

            }
            cursor.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }


    private String ConvertDisToBonusMeasurement(String product_id, String quantity, String measurement_unit_id) {
        if (measurement_unit_id == null || measurement_unit_id.equalsIgnoreCase("null")) {
            return quantity;
        }

        Double quantityd = Double.parseDouble(quantity);
        Double measurement_unit_idd = Double.parseDouble(measurement_unit_id);

        Cursor cursor = db.rawQuery("select \n" +
                "\tROUND(((case when ud.qty_in_base is null then 1 else ud.qty_in_base end)*" + quantityd + "),0)\n" +
                "from \n" +
                "products p\n" +
                "left join unit_details ud on ud.product_id=p.product_id and ud.measurement_unit_id=p.sale_unit_id\n" +
                "where \n" +
                "\tp.product_id=" + product_id + "\n" +
                "limit 1");
        Log.e("ConvertCursor", "select \n" +
                "\tROUND(((case when ud.qty_in_base is null then 1 else ud.qty_in_base end)*" + quantityd + "),0)\n" +
                "from \n" +
                "products p\n" +
                "left join unit_details ud on ud.product_id=p.product_id and ud.measurement_unit_id=p.sale_unit_id\n" +
                "where \n" +
                "\tp.product_id=" + product_id + "\n" +
                "limit 1");
        cursor.moveToFirst();

        if (cursor.getCount() > 0 && cursor != null) {

            quantityd = cursor.getDouble(0);
            cursor.close();
            Log.e("quantityd*", quantityd + "");
            Cursor cursor1 = db.rawQuery("select \n" +
                    "\tprintf('%.2f'," + quantityd + "/(case when ud.qty_in_base is null then 1 else ud.qty_in_base end))\n" +
                    "from \n" +
                    "products p\n" +
                    "left join unit_details ud on ud.product_id=p.product_id and ud.measurement_unit_id=" + measurement_unit_id + "\n" +
                    "where \n" +
                    "\tp.product_id==" + product_id + "\n" +
                    "limit 1");
            Log.e("ConvertCursor1", "select \n" +
                    "\tprintf('%.2f'," + quantityd + "/(case when ud.qty_in_base is null then 1 else ud.qty_in_base end))\n" +
                    "from \n" +
                    "products p\n" +
                    "left join unit_details ud on ud.product_id=p.product_id and ud.measurement_unit_id=" + measurement_unit_id + "\n" +
                    "where \n" +
                    "\tp.product_id==" + product_id + "\n" +
                    "limit 1");
            cursor1.moveToFirst();

            if (cursor1.getCount() > 0 && cursor1 != null) {

                quantityd = cursor1.getDouble(0);
                quantityd = Double.parseDouble(ParentActivity.SpecialroundTwoDecimals(quantityd + ""));
                Log.e("quantityd/", quantityd + "");
            }

            cursor1.close();

        }
        return quantityd + "";


    }

    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    public Double CheckVat(String product_id) {
        try {
            String queryforvat = "SELECT vat From product_price WHERE product_id='" + product_id + "' AND effective_date=(SELECT max(effective_date) from product_price where product_id='" + product_id + "' AND effective_date<='" + memodate + "') ORDER BY  vat DESC LIMIT 1";
            Log.e("queryvatnormal", queryforvat);
            Cursor cursorvat = db.rawQuery(queryforvat);
            vat = 0.0;
            if (cursorvat.getCount() > 0) {
                try {
                    if (cursorvat.moveToFirst()) {
                        do {
                            vat = cursorvat.getDouble(0);
                        } while (cursorvat.moveToNext());
                    }

                } finally {
                    try {
                        cursorvat.close();
                    } catch (Exception ignore) {
                    }
                }
                cursorvat.close();
            }
            cursorvat.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vat;
    }

    private String getProductName(String bonus_product_id) {
        String pname = "";
        String productsquery = "SELECT product_id,product_name FROM  product WHERE product_id='" + bonus_product_id + "' limit 1";
        Log.e("STOCK QUERY:", productsquery);
        Cursor cursor = db.rawQuery(productsquery, "Bonus_product_Policy8");
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

        Cursor cursor = db.rawQuery(Query);
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

        Cursor cursor1 = db.rawQuery(Query1);
        Cursor cursor2 = db.rawQuery("select count(_id) from product_boolean where outlet_id='" + TempData.OutletID + "' and boolean='true'", "cursor2");
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

    private void setBPProductBonus() {

        TempData.BonusShowList.clear();
        //TempData.BonusShowList.addAll(BPbonus_product);

        String bonusString = "";

        Log.e("BPbonus_product_e", new Gson().toJson(BPSelected_product));

        for (int i = 0; i < BPSelected_product.size(); i++) {
            bonusString = bonusString + BPSelected_product.get(i).get("item") + "(" + BPSelected_product.get(i).get("qty") + " " + BPSelected_product.get(i).get("measurement_unit_name") + ")";

            if (i != BPSelected_product.size() - 1)
                bonusString = bonusString + ",";
        }

        if (bonusString.length() > 0) {

            Log.e("bonusStringT__", bonusString);
            txtBonusPolicy.setVisibility(View.VISIBLE);
            txtBonusPolicy.setText(bonusString);
        } else
        {
            txtBonusPolicy.setText("");
            discount_details.setVisibility(View.VISIBLE);

        }

    }

    private String getDiscountText(String s, String discount) {
        String line = "";
        String productsquery = "SELECT product_id,product_name FROM  product WHERE product_id='" + s + "' limit 1";
        Log.e("STOCK QUERY:", productsquery);
        Cursor cursor = db.rawQuery(productsquery, "Bonus_product_Policy8");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            line = cursor.getString(1) + "(" + discount + "),";
        }

        return line;
    }


    private double getmemoqtywithpolicy(String policy_id) {

        Cursor cursor = db.rawQuery("select Sum(quantity) from memo_details where memo_number='" + TempData.memoNumber + "' and policy_id ='" + policy_id + "' and is_bonus='3'", "getmemoqty");
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

            Cursor cursor = db.rawQuery("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='" + measurement_unit_id + "'");

            cursor.moveToFirst();

            if (cursor.getCount() > 0 && cursor != null) {

                quantityd = quantityd * cursor.getDouble(0);
                cursor.close();
                Log.e("quantityd*", quantityd + "");
                Cursor cursor1 = db.rawQuery("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='7'");

                cursor1.moveToFirst();

                if (cursor1.getCount() > 0 && cursor1 != null) {

                    quantityd = quantityd / cursor1.getDouble(0);
                    quantityd = Double.parseDouble(SpecialroundTwoDecimals(quantityd + ""));
                    Log.e("quantityd/", quantityd + "");
                }
                cursor1.close();


            } else {
                Cursor cursor1 = db.rawQuery("select qty_in_base from unit_details where product_id='" + product_id + "' and measurement_unit_id ='7'");

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

    private double getmemoqty(String bonus_product_id) {

        Cursor cursor = db.rawQuery("select quantity from order_details where order_number='" + TempData.orderNumber + "' and product_id ='" + bonus_product_id + "' and is_bonus='3'", "getmemoqty");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            return cursor.getDouble(0);
        }
        return 0.0;
    }


    private void vatCalculation(String product_id, Double quantity, Double price, int pos) {
        Double vatprice = 0.0, vatofvat = 0.0;
        Cursor c = db.rawQuery("select vat from product_price where product_id='" + product_id + "' and vat!='null'");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            vatprice = (price * 100) / (100 + c.getDouble(0));
            vatofvat = (price - vatprice);
            itemListContent.get(pos).put("vat", c.getDouble(0) + "");
            vat = vat + (vatofvat * quantity);
        }
        c.close();
    }

    private void getDiscount(Double sp_discount,String s) {
        int distype = 0;
        discountp = 0.0;
        Cursor cursor = db.rawQuery("select discount_percent,discount_type from discounts where memo_value <=" + s + " and date_from <= '" + memodate + "' and date_to>='" + memodate + "' order by memo_value DESC limit 1");
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
        Total_Discount = Total_Discount + discount+sp_discount;
        this.discount.setText(roundTwoDecimals(Total_Discount));

        subt.setText(roundTwoDecimals(memoValue - Total_Discount));

    }

    public ArrayList<HashMap<String, String>> getAdapterHashMapList() {
        return returnContent;
    }

    public ArrayList<HashMap<String, String>> getBonusMapList() {
        return Bonus;
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


            return value;

        } else {

            return d + "0";
        }
    }

    private void showLimitExeednotification(double giventQty, String product_id) {

        Cursor c = db.rawQuery("SELECT quantity-booking_quantity>" + giventQty + " as p from stock_info WHERE  product_id = '" + product_id + "'");
        Log.e("ProductSales", "SELECT quantity-booking_quantity>" + giventQty + " as p from stock_info WHERE  product_id = '" + product_id + "'");
        c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            if (c.getInt(0) == 0) {
                Toast.makeText(context, "Your Stock Limit Exceeded", Toast.LENGTH_SHORT).show();
            }
        }


    }
}