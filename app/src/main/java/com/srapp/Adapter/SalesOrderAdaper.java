package com.srapp.Adapter;

import java.util.ArrayList;
import java.util.HashMap;


import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.R;
import com.srapp.Util.AppManager;

import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

public class SalesOrderAdaper extends BaseAdapter {

    // Declare Variables
    Context context;
    public static SQLiteDatabase myDB;
    String SO_ID;
    String OutletID;
    String qty = "";
    public EditText editText;
    ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();


    ArrayList<String> SELECTED_POS = new ArrayList<String>();
    int CheckCount = 0;

    Data_Source db;
    private Activity activity;
//	HomeProjectListAdapter featuredProjectsListAdapter;


    public SalesOrderAdaper(Context context, ArrayList<HashMap<String, String>> arraylistContent, String _SO_ID, String _OutletID) {
        this.context = context;
        this.activity = ((Activity)context);

        itemListContent = arraylistContent;
        SO_ID = _SO_ID;
        OutletID = _OutletID;
        db = new Data_Source(context);
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


        final View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_sales_memo, null);
        TextView NameTv = (TextView) view2.findViewById(R.id.product_name);
        final EditText QuantityEd = (EditText) view2.findViewById(R.id.quantity);
        final CheckBox chbox = (CheckBox) view2.findViewById(R.id.checkbox);
        QuantityEd.setKeyListener(new DigitsKeyListener());
        editText = QuantityEd;
        HashMap<String, String> mapContent = new HashMap<String, String>();
        mapContent = itemListContent.get(position);
        String product_name = mapContent.get("product_name");
        final String boolean100 = mapContent.get("boolean");
        final String _product_id = mapContent.get("product_id");
        NameTv.setText(product_name);
        Log.e("~~~~~~~~~~~~~~~~~~~~~~~~~", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + _product_id+"ksdma"+mapContent.get("boolean"));

        Boolean b = Boolean.valueOf(mapContent.get("boolean"));

        // Log.e("CHECK STATUS:", String.valueOf(b));


        chbox.setChecked(b);
        if (mapContent.get("boolean").equals("true")) {

            Log.e("Edit Memo", "quantity for edit memo");
            CheckCount = CheckCount + 1;
            Log.e("getArrayListIndex",getArrayListIndex("" + position)+"");
            if (getArrayListIndex("" + position) < 0)
                SELECTED_POS.add("" + position);
            QuantityEd.setVisibility(View.VISIBLE);
            Log.e("product_id", _product_id);
            Log.e("Edited Products From Memo Details Should Go Here", "SELECT * FROM "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" where product_id = " + _product_id + " and outlet_id=" + "'" + OutletID + "'");
            Cursor c = db.rawQueryCoustom("SELECT * FROM "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" where product_id = " + _product_id + " and outlet_id=" + "'" + OutletID + "'");
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        Log.e("Edit Memo in query", "quantity for edit memo");
                        String product_quantity = c.getString(c.getColumnIndex("quantity"));
                        Log.e("ProductQuantity:", product_quantity);
                        QuantityEd.setText(product_quantity);

                    } while (c.moveToNext());
                }
            }

        }


        chbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (chbox.isChecked()) {
                    AppManager.setOpenKeyBoard(context);


                    db.excQuery("UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET quantity = " + "'" + qty + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");

                    Log.e("UPDATED AFTER CHECKED:", "UPDATED");

                    CheckCount = CheckCount + 1;
                    if (getArrayListIndex("" + position) < 0)
                        SELECTED_POS.add("" + position);

                    // Log.e("CHECKED", "true");

                    QuantityEd.setVisibility(View.VISIBLE);
                    itemListContent.get(position).put("boolean", "true");

                    //QuantityEd.setFocusableInTouchMode(true);
                   QuantityEd.requestFocus();

                    Cursor c = db.rawQueryCoustom("SELECT * FROM "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" where product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");
                    if (c != null) {
                        if (c.moveToFirst()) {
                            do {
                                String product_quantity = c.getString(c.getColumnIndex("quantity"));
                                QuantityEd.setText(product_quantity);
                                Log.e("product_quantity", " " + product_quantity);

                            } while (c.moveToNext());
                        }
                    }


                    QuantityEd.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            // TODO Auto-generated method stub
                            // String Quantity =  s.toString();
                            //LoginActivity.myDB.execSQL("UPDATE product_table SET product_quantity ="+"'"+Quantity+"'" +", boolean12 = "+"'"+chbox.isChecked()+"'"+" WHERE product_id = "+"'"+_product_id+"'"+"and so_id="+"'"+SO_ID+"'"+" and outlet_id="+"'"+OutletID+"'");

                            Log.e("LENGTH:", "" + s.toString().length());
                            if (s.toString().length() == 0) {
                                db.excQuery("UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET quantity =" + "'" + qty + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");
                                Log.e("AFTER EDIT1 :", "UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET quantity =" + "'" + qty + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + "and so_id=" + "'" + SO_ID + "'" + " and outlet_id=" + "'" + OutletID + "'");

                                Log.e("UPDATED IN TEXTCHANGED:", "UPDATED");
                            }

                            if (s.toString().length() >= 1) {

                                if (isDouble(s.toString())) {
                                    String Quantity = QuantityEd.getText().toString().trim();
//								  String Quantity =  s.toString().trim();
                                    double giventQty = Double.parseDouble(Quantity);


                                    Log.e("giventQty", "" + giventQty);

                                    String Updatequery = "UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET quantity =" + "'" + Quantity + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'";
                                    Log.e("Updatequery", "" + Updatequery);
                                    db.excQuery(Updatequery);


                                } else {
                                    Toast.makeText(context, "Please enter correct number format!", Toast.LENGTH_LONG).show();
                                    QuantityEd.setText("");
                                }


                            }

                        }
                    });

                } else {
                    AppManager.setCloseKeyBoard(context, QuantityEd);
                    CheckCount = CheckCount - 1;
                    if (getArrayListIndex("" + position) >= 0)
                        SELECTED_POS.remove("" + position);

                    db.excQuery("UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET quantity =" + "'" + qty + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");
                    Log.e("UNCHECKED:", "UPDATE product_table SET product_quantity =" + "'" + qty + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");


                    // Log.e("CHECKED", "False");
                    // Log.e("EEEEEEEEEEEEEEEE", "EEEEEEEEEEEEEEEEEEEEEEEEE"   + chbox.isChecked());

                    QuantityEd.setVisibility(View.INVISIBLE);

                    //saikat
                    itemListContent.get(position).put("boolean", "false");
                    savePreference(itemListContent.get(position).get("product_id"), "0.0");

                    Cursor c = db.rawQueryCoustom("SELECT * FROM "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" where product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");
                    if (c != null) {
                        if (c.moveToFirst()) {
                            do {
                                String product_quantity = c.getString(c.getColumnIndex("quantity"));
                                QuantityEd.setText(product_quantity);

                            } while (c.moveToNext());
                        }
                    }
                }
                db.excQuery("UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" +  " and outlet_id=" + "'" + OutletID + "'");

            }
        });


        if (chbox.isChecked()) {

            QuantityEd.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    // String Quantity =  s.toString();
                    //LoginActivity.myDB.execSQL("UPDATE product_table SET product_quantity ="+"'"+Quantity+"'" +", boolean12 = "+"'"+chbox.isChecked()+"'"+" WHERE product_id = "+"'"+_product_id+"'"+"and so_id="+"'"+SO_ID+"'"+" and outlet_id="+"'"+OutletID+"'");

                    Log.e("LENGTH:", "" + s.toString().length());
                    if (s.toString().length() == 0) {
                        db.excQuery("UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET quantity =" + "'" + qty + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");
                        Log.e("AFTER EDIT:", "UPDATE product_table SET product_quantity =" + "'" + qty + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + "and so_id=" + "'" + SO_ID + "'" + " and outlet_id=" + "'" + OutletID + "'");

                        Log.e("UPDATED IN TEXTCHANGED:", "UPDATED");
                    }

                    if (s.toString().length() >= 1) {
                        if (isDouble(s.toString())) {
                            String Quantity = s.toString().trim();
                            double giventQty = Double.parseDouble(Quantity);
                            Log.e("giventQty", "" + giventQty);
                            Log.e("Quantity", "Quantity: " + Quantity);
                            showLimitExeednotification(giventQty,_product_id);
                            db.excQuery("UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET quantity =" + "'" + Quantity + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");


                        } else {
                            Toast.makeText(context, "Please enter correct number format!", 1000).show();
                            QuantityEd.setText("");

                        }
                    }
                }


            });


            if (chbox.isChecked()) {
                Log.e("checkboxcheck", String.valueOf(chbox.isChecked()));
                QuantityEd.setVisibility(View.VISIBLE);
                db.excQuery("UPDATE "+Tables.TABLE_NAME_PRODUCT_BOOLEAN+" SET quantity =" + "'" + QuantityEd.getText().toString() + "'" + ", boolean = " + "'" + chbox.isChecked() + "'" + " WHERE product_id = " + "'" + _product_id + "'" + " and outlet_id=" + "'" + OutletID + "'");
            }

        }




        return view2;
    }

    private void showLimitExeednotification(double giventQty, String product_id) {

       Cursor  c = db.rawQueryCoustom("SELECT quantity-booking_quantity>"+giventQty+" as p from stock_info WHERE  product_id = '"+product_id+"'");
        Log.e("SalesMemo","SELECT quantity-booking_quantity>"+giventQty+" as p from stock_info WHERE  product_id = '"+product_id+"'");
       c.moveToFirst();
        if (c != null && c.getCount() > 0) {
            if (c.getInt(0) == 0) {
                Toast.makeText(context, "Your Stock Limit Exceeded", Toast.LENGTH_SHORT).show();
            }
        }


    }


    public int CheckCountQuantity () {
        return CheckCount;
    }

    public ArrayList<String> SelectedQuantity () {

        return SELECTED_POS;

    }


    public int getArrayListIndex (String value)
    {
        return SELECTED_POS.indexOf(value);
    }

    public ArrayList<HashMap<String, String>> SItemMap ()
    {
        return itemListContent;
    }

    public void savePreference (String key, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public boolean isDouble (String value){
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
