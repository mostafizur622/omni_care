package com.srapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.srapp.Adapter.SalesOrderDetailsAdaper1.bonus_policylistener;
import static com.srapp.TempData.BPBonusProductView;
import static com.srapp.TempData.BPSelected_set;
import static com.srapp.TempData.PolicySetRelation;

public class policy_set extends Parent {
    Button CancelButton, set1, set2;
    Data_Source db;
    String polictype, policy_id, option_id, formula;
    ListView listView, ListViewSetRelation;
    LinearLayout bproducts;
    TextView ProductName;
    TextView edqty;
    AdapterForBonusPolicyProductSelection adapterForBonusPolicyProductSelection;
    AdapterForBonusPolicyProductSelection1 adapterForBonusPolicyProductSelection1;
    String minQty, Quantity, setSelection;
    int setSelectionPostion;
    ArrayList<HashMap<String, String>> formulaArrayMap = new ArrayList<>();
    View devider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_policy_set);
        db = new Data_Source(this);
        set1 = findViewById(R.id.set1);
        set2 = findViewById(R.id.set2);
        edqty = findViewById(R.id.edqty);
        ProductName = findViewById(R.id.ProductName);
        listView = findViewById(R.id.ProductListView);
        devider = findViewById(R.id.devider);
        ListViewSetRelation = findViewById(R.id.ProductListView1);// set relation list view--------------
        CancelButton = findViewById(R.id.CancelButton);

        if (getIntent() != null) {
            polictype = getIntent().getStringExtra("policy_type");
            policy_id = getIntent().getStringExtra("policy_id");
            option_id = getIntent().getStringExtra("option_id");
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

        if (formula != null) {
            formulaArrayMap = parseFormula(formula);
        }

        set1.setOnClickListener(v -> {
            BPSelected_set.put(policy_id, "1");
            bonus_policylistener.setBonus_type(2);

            set1.setTextColor(Color.parseColor("#FFFFFF"));
            set1.setBackgroundColor(Color.parseColor("#00A65A"));

            set2.setBackgroundColor(Color.parseColor("#FAFAFA"));
            set2.setTextColor(Color.parseColor("#666666"));

            ArrayList<HashMap<String, String>> BPBonusProductView_policy_id = new ArrayList<HashMap<String, String>>();
            BPBonusProductView_policy_id = BPBonusProductView.get(policy_id);
            if (BPBonusProductView_policy_id.size() > 0 && !BPBonusProductView_policy_id.get(0).get("relation").equals("AND")) {
                adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, BPBonusProductView_policy_id, Double.parseDouble(BPBonusProductView_policy_id.get(0).get("provided_qty")));
                listView.setAdapter(adapterForBonusPolicyProductSelection);
            } else {
                listView.setAdapter(null);
            }
        });

        set2.setOnClickListener(v -> {
            BPSelected_set.put(policy_id, "2");
            bonus_policylistener.setBonus_type(2);

            set2.setBackgroundColor(Color.parseColor("#00A65A"));
            set2.setTextColor(Color.parseColor("#FFFFFF"));

            set1.setBackgroundColor(Color.parseColor("#FAFAFA"));
            set1.setTextColor(Color.parseColor("#666666"));

            ArrayList<HashMap<String, String>> BPBonusProductView_policy_id = new ArrayList<HashMap<String, String>>();
            BPBonusProductView_policy_id = BPBonusProductView.get(policy_id);
            if (BPBonusProductView_policy_id.size() > 0 && !BPBonusProductView_policy_id.get(0).get("relation").equals("AND")) {
                adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, BPBonusProductView_policy_id, Double.parseDouble(BPBonusProductView_policy_id.get(0).get("provided_qty")));
                listView.setAdapter(adapterForBonusPolicyProductSelection);
            } else {
                listView.setAdapter(null);
            }
        });

        ArrayList<HashMap<String, String>> BPBonusProductView_policy_id = new ArrayList<HashMap<String, String>>();

        BPBonusProductView_policy_id = BPBonusProductView.get(policy_id);

            Log.e("SetRelation", PolicySetRelation.get(policy_id) + "---");
            Log.e("SetRelation", BPBonusProductView.get(policy_id) + "---");

            if (PolicySetRelation.get(policy_id) == null || PolicySetRelation.get(policy_id).equals("") || PolicySetRelation.get(policy_id).equals("AND")) {
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

            }

            else {

                set1.setVisibility(View.VISIBLE);
                set2.setVisibility(View.VISIBLE);
                if (BPSelected_set.get(policy_id).equals("1")) {
                    set1.setTextColor(Color.parseColor("#FFFFFF"));
                    set1.setBackgroundColor(Color.parseColor("#00A65A"));

                    set2.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    set2.setTextColor(Color.parseColor("#666666"));
                } else {
                    set2.setBackgroundColor(Color.parseColor("#00A65A"));
                    set2.setTextColor(Color.parseColor("#FFFFFF"));

                    set1.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    set1.setTextColor(Color.parseColor("#666666"));
                }
                if (BPBonusProductView_policy_id.size() > 0 && !BPBonusProductView_policy_id.get(0).get("relation").equals("AND")) {
                    adapterForBonusPolicyProductSelection = new AdapterForBonusPolicyProductSelection(this, BPBonusProductView_policy_id, Double.parseDouble(BPBonusProductView_policy_id.get(0).get("provided_qty")));
                    listView.setAdapter(adapterForBonusPolicyProductSelection);
                    ListViewSetRelation.setVisibility(View.GONE);
                    devider.setVisibility(View.GONE);
                }
            }

        CancelButton.setOnClickListener(v -> {

            if (adapterForBonusPolicyProductSelection != null) {

                ArrayList<HashMap<String, String>> list = adapterForBonusPolicyProductSelection.getdata();
                Log.e(TAG, "AdapterList1: " + new Gson().toJson(list));
                for (int i = 0; i < list.size(); i++) {
                    if (Double.parseDouble(list.get(i).get("quantity")) <= 0 && Boolean.parseBoolean(list.get(i).get("selected_s"))) {
                        Toast.makeText(getApplicationContext(), "Can Not select 0 Quantity For " + list.get(i).get("item"), Toast.LENGTH_SHORT).show();
                        return;
                    }

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

                }
            }

            finish();

        });

    }

    @Override
    public void onBackPressed() {
        return;
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

    public ArrayList<HashMap<String, String>> parseFormula(String str) {

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
        if (!element.equals("") && !relation.equals("")) {
            HashMap<String, String> map = new HashMap<>();
            map.put("element", element);
            map.put("relation", relation);
            arrayList.add(map);
        }

        TempData.SetRelation = set_relation;
        Log.e("formula_", TempData.SetRelation);

        Log.i(TAG, "formula_: " + new Gson().toJson(arrayList));


        return arrayList;
    }

}