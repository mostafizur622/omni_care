package com.srapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.ProductSales;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.Util.AppManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AdapterForBonusProductList extends BaseAdapter {

    // Declare Variables
    Context context;
    String SO_ID;
    Data_Source db;

    Cursor c;
    ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
    Button NextButton;

//	public static  ArrayList<HashMap<String, String>> ProductQuantity = new ArrayList<HashMap<String, String>>();

//	public static  ArrayList<String> chxSelected=new ArrayList<String>();


    //	HomeProjectListAdapter featuredProjectsListAdapter;

    String nextStratus="";
    int BatchCount=0;




    public AdapterForBonusProductList(Context context, ArrayList<HashMap<String, String>> arraylistContent, String _SO_ID) {
        this.context = context;

        itemListContent = arraylistContent;
        SO_ID = _SO_ID;

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




        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, null);
        TextView NameTv = (TextView)view2.findViewById(R.id.product_name);
        final EditText QuantityEd = (EditText)view2.findViewById(R.id.quantity);
        final CheckBox chbox = (CheckBox)view2.findViewById(R.id.checkbox);


        HashMap<String, String> mapContent = new HashMap<String, String>();

        mapContent = itemListContent.get(position);





        final String product_name= mapContent.get("product_name");
//        final String _product_type_id= mapContent.get("product_type_id");
        final String _product_id= mapContent.get("product_id");
        final String product_category_id= mapContent.get("product_category_id");




        NameTv.setText(product_name);





        Log.e("BonusProductQuantity", " BonusProductQuantity for Bonus: "+ TempData.BonusProductQuantity);
        Log.e("TotalBonusProductList", " TotalBonusProductList for Bonus: "+TempData.TotalBonusProductList);



        if(getPreference("bonus"+_product_id).equalsIgnoreCase(""))
        {
            savePreference("bonus"+_product_id,"0.0");
            Log.e("outer ","outer: "+getPreference("bonus"+_product_id));
        }


//        if(TempData.BonusChxSelected.contains(""+position)&&!(getPreference("bonus"+_product_id)).equalsIgnoreCase("0"))
        if(!(getPreference("bonus"+_product_id).equalsIgnoreCase("0.0")))
        {
            Log.e("b_productID","------"+"bonus"+_product_id);
            Log.e("b_product_quantity","++++++"+getPreference("bonus"+_product_id));
            chbox.setChecked(true);
            TempData.BonusChxSelected.add(""+position);
            QuantityEd.setVisibility(View.VISIBLE);
            QuantityEd.setText(getPreference("bonus"+_product_id));

        }


        chbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked)
                {
                    AppManager.setOpenKeyBoard(context);
                    chbox.setFocusable(true);
                    //QuantityEd.setFocusable(true);
                    Log.e("Position:", ""+position);
                    TempData.BonusChxSelected.add(""+position);
                    QuantityEd.setVisibility(View.VISIBLE);
                    QuantityEd.setFocusableInTouchMode(true);
                    QuantityEd.requestFocus();
                    QuantityEd.setText("");
                    // QuantityEd.setText("");
                    //				savePreference(_product_id, QuantityEd.getText().toString());
                    Log.e("GETINDEX ProductID",""+QuantityEd.getText().toString());
                    if(QuantityEd.getText().toString().length()>=1)
                    {
                        Log.e("GETINDEX ProductID",""+_product_id);
//                        Log.e("GETINDEX ProductTYPEID",""+_product_type_id);
                        Log.e("GETINDEX",""+getIndex(_product_id));
                        Log.e("ProductQuantity Push",""+TempData.BonusProductQuantity.toString());
                        if(getIndex(_product_id)<0)
                        {
                            HashMap<String,String> map=new HashMap<String,String>();
                            map.put("product_id", _product_id);
                            map.put("product_name", product_name);
                            map.put("quantity", getPreference("bonus"+_product_id));
                            TempData.BonusProductQuantity.add(map);
                        }
                        else
                        {
                            TempData.BonusProductQuantity.get(getIndex(_product_id)).put("quantity",  getPreference("bonus"+_product_id));
                        }
                    }

                    for(int i=0;i<TempData.BonusChxSelected.size();i++)
                        Log.e("SELECTED POSITION:", ""+TempData.BonusChxSelected.get(i));


                }
                else
                {

                    savePreference("bonus"+_product_id,"0.0");
                    Log.e("afterfalse",  product_name+" ansjd" +product_name+getPreference("bonus"+_product_id));
                    AppManager.setCloseKeyBoard(context, QuantityEd);
                    TempData.BonusChxSelected.remove(""+position);
                    for(int i=0;i<TempData.BonusChxSelected.size();i++)
                        Log.e("SELECTED POSITION:", ""+TempData.BonusChxSelected.get(i));



                    int index=getIndex(_product_id);
                    Log.e("INDEX", ""+index);

                    Log.e("BEFORE", TempData.BonusProductQuantity.toString());
                    if(index!=-1)
                    {
                        TempData.BonusProductQuantity.remove(index);
                    }

                    Log.e("AFTER", TempData.BonusProductQuantity.toString());


                    QuantityEd.setVisibility(View.INVISIBLE);
                }
            }
        });








       /* Log.e("++++++++++++++++", "(ProductQuantity)  "+TempData.BonusProductQuantity.toString());
        Log.e("++++++++++++++++", "(SampleProductList)  "+TempData.SampleProductList.toString());
        Log.e("++++++++++++++++", "(GiftProductList)  "+TempData.GiftProductList.toString());
        Log.e("++++++++++++++++", "(TotalProductList)  "+TempData.TotalBonusProductList.toString());*/



        NextButton = (Button)((Activity) context).findViewById(R.id.NextButton);
        NextButton.setText("Save");
        NextButton.setOnClickListener(new OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {

                Log.e("++++++++++++++++", "++++++ProductQuantity++++++++++++"+TempData.BonusProductQuantity.toString());
                if(CheckValidation())
                {
                    if(TempData.BonusProductQuantity.size()>=1)
                    {

                        Log.e("Hello", TempData.BonusProductQuantity.get(0).get("quantity"));
                        // TODO Auto-generated method stub
                        Log.e("Sized:", ""+TempData.BonusProductQuantity.size());
                        String ProductName="";



//                        Log.e("ProductQuantity Array Before", ""+""+TempData.BonusProductQuantity.toString());
                      /*  TempData.SampleProductList.clear();
                        TempData.GiftProductList.clear();*/
                        TempData.TotalBonusProductList.clear();


                        for(int i=0;i<TempData.BonusProductQuantity.size();i++)
                        {


                            Log.e("ProductID", TempData.BonusProductQuantity.get(i).get("product_id"));
//                            Log.e("ProductTypeId", " "+TempData.BonusProductQuantity.get(i).get("product_type_id"));
                            Log.e("QUANTITY", " "+TempData.BonusProductQuantity.get(i).get("quantity"));
                            Log.e("QUANTITY111", " "+TempData.BonusProductQuantity.get(i).get("quantity").length());
                            Log.e("", "...................................................");


                            Log.e("ProductQuantity Array", ""+""+TempData.BonusProductQuantity.toString());
//                            Log.e("ProductQuantity length for an index", ""+TempData.BonusProductQuantity.get(i).get("quantity").trim().length());
                            Log.e("ProductQuantity Index", ""+""+""+i);






                            HashMap<String,String> map=new HashMap<String,String>();
                            map.put("product_id", TempData.BonusProductQuantity.get(i).get("product_id"));
                            map.put("product_name", TempData.BonusProductQuantity.get(i).get("product_name"));
                            map.put("quantity", TempData.BonusProductQuantity.get(i).get("quantity"));
                            Log.e("TotalProductList ", map.toString());

                            TempData.TotalBonusProductList.add(map);


                        }

                    }else if(TempData.BonusProductQuantity.toString().equalsIgnoreCase("[]"))
                    {
                        TempData.TotalBonusProductList.clear();
                    }

                    Set<String> hs = new HashSet<String>();
                    hs.addAll(TempData.BonusChxSelected);
                    TempData.BonusChxSelected.clear();
                    TempData.BonusChxSelected.addAll(hs);


                    Log.e("Size1", ""+(TempData.BonusChxSelected.size()+BatchCount));

                    for(int i=0;i<TempData.BonusChxSelected.size();i++){
                        Log.e("chxSelected", ""+TempData.BonusChxSelected.get(i));
                    }


                    for(int i=0;i<TempData.TotalBonusProductList.size();i++)
                        Log.e("TotalBonusProductList", ""+TempData.TotalBonusProductList.get(i));

                    for(int i=0;i<TempData.BonusProductQuantity.size();i++)
                        Log.e("BonusProductQuantity", ""+TempData.BonusProductQuantity.get(i));



                    Log.e("================", "++++++chxSelected.size()  +++++"+ TempData.BonusChxSelected.size());
                    Log.e("================", "++++++TotalBonusProductList.size()  +++++"+ TempData.TotalBonusProductList.size());
                  /*  for(int i=0;i<itemListContent.size();i++){
                       String product_id = itemListContent.get(position).get("product_id");

                        savePreference("bonus"+product_id,"0.0");

                    }*/



                    Intent idn = new Intent(context, ProductSales.class);
                    context.startActivity(idn);
//                    ((Activity) context).finish();
                    TempData.policyClear=false;
                    ((Activity) context).finishAffinity();

                   /* if(TempData.TotalBonusProductList.size()==(TempData.BonusChxSelected.size()))
                    {

                        Intent idn = new Intent(context, SalesOrderDetailsActivity.class);
                        context.startActivity(idn);
                        ((Activity) context).finish();

                    }
                    else if(TempData.BonusChxSelected.size()==0)
                    {
                        TempData.BonusChxSelected.clear();
                        TempData.BonusProductQuantity.clear();
//                        TempBonus();
                        Intent idn = new Intent(context, SalesOrderDetailsActivity.class);
                        context.startActivity(idn);
                        ((Activity) context).finish();
                    }
                    else
                    {
                        Toast.makeText((Activity) context, "Quantity not set for selected product!", Toast.LENGTH_LONG).show();
                    }*/
                }
                else
                {
                    Toast.makeText(context, "Enter all feilds/Correct value!", Toast.LENGTH_LONG).show();
                }



            }
        });






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


				/*if(getIndex(_product_id)<0)
		    {
			savePreference(_product_id, s.);
			HashMap<String,String> map=new HashMap<String,String>();
			map.put("product_id", _product_id);
//			map.put("product_type_id", _product_type_id);
			map.put("quantity", getPreference("bonus"+_product_id));
			ProductQuantity.add(map);
		    }
		    else
		    {
		    	ProductQuantity.get(getIndex(_product_id)).put("quantity",  getPreference("bonus"+_product_id));
		    }*/

//				String Quantity =  s.toString().trim();
                Log.e("Length:", "......................"+s.length());
//				if(s.length()<=0)
                if(QuantityEd.getText().toString().length()<=0)
                {
                    Log.e("b_product_quantity:", ".........."+getPreference("bonus"+_product_id));
                    savePreference("bonus"+_product_id,"0.0");
                    Log.e("a_product_quantity:", "..........."+getPreference("bonus"+_product_id));

                    if(getIndex(_product_id)<0)
                    {
                        HashMap<String,String> map=new HashMap<String,String>();
                        map.put("product_id", _product_id);
                        map.put("product_name", product_name);
//                        map.put("product_type_id", _product_type_id);
                        map.put("quantity", getPreference("bonus"+_product_id));
                        TempData.BonusProductQuantity.add(map);


                    }
                    else
                    {
                        TempData.BonusProductQuantity.get(getIndex(_product_id)).put("quantity",  getPreference("bonus"+_product_id));
                    }

                }

				/*if(s.length()>0)
				{




					Log.e("INDEX2", ""+getIndex(_product_id));

					Log.e("ProductID", _product_id);
					savePreference(_product_id, Quantity);
					Log.e("SEARCHING ID(IN ELSE)", ""+getIndex(_product_id));
					if(getIndex(_product_id)<0)
					{
						HashMap<String,String> map=new HashMap<String,String>();
						map.put("product_id", _product_id);
						map.put("product_type_id", _product_type_id);
						map.put("quantity", getPreference(_product_id));
						TempData.BonusProductQuantity.add(map);
					}
					else
					{
						TempData.BonusProductQuantity.get(getIndex(_product_id)).put("quantity",  getPreference(_product_id));
					}

				}*/


                   if(QuantityEd.getText().toString().length()>0)
                    {
                        if(isDouble(QuantityEd.getText().toString()))
                        {

//                            Log.e("---------------TempData.SESSIONID-----------", TempData.SESSIONID);
                            double boolean_quantity = 0.0;
                            Cursor c5= db.rawQueryCoustom("SELECT quantity FROM product_boolean WHERE  product_id = '"+_product_id+"' and outlet_id='"+TempData.OutletID+"'");
                            if(c5!=null)
                            {
                                if(c5.moveToFirst())
                                {
                                    do{
                                        boolean_quantity = c5.getDouble(0);

                                    }while(c5.moveToNext());
                                }
                            }
                            c5.close();
                            db.close();


                            double memo_details_quantity = 0.0;
                            Cursor c2= db.rawQueryCoustom("SELECT quantity FROM memo_details WHERE  " +
                                    "product_id = '"+_product_id+"' and memo_number='"+TempData
                                    .orderNumber +"'" +" AND price > 0");
                            if(c2!=null)
                            {
                                if(c2.moveToFirst())
                                {
                                    do{
                                        memo_details_quantity = c2.getDouble(0);

                                    }while(c2.moveToNext());

                                }
                            }
                            c2.close();
                            db.close();


                            double bonus_quantity = 0.0;
                            Cursor c3= db.rawQueryCoustom("SELECT quantity FROM memo_details WHERE  " +
                                    "product_id = '"+_product_id+"' and memo_number='"+TempData
                                    .orderNumber +"'" +" AND price = 0");
                            if(c3!=null)
                            {
                                if(c3.moveToFirst())
                                {
                                    do{
                                        bonus_quantity = c3.getDouble(0);

                                    }while(c3.moveToNext()); ///Change


                                }
                            }
                            c3.close();
                            db.close();
                            Log.e("bonus_quantity: ", "bonus_quantity: "+memo_details_quantity);

                            String Quantity =  QuantityEd.getText().toString().trim();
                            double giventQty=Double.parseDouble(Quantity);

                            /* c= db.rawQuery("SELECT SUM(quantity) FROM van_stocks WHERE  product_id = '"+_product_id+"' and so_id='"+SO_ID+"'");
                            Log.e("Gitt Issue", "SELECT SUM(quantity)  FROM van_stocks WHERE  product_id = '"+_product_id+"' and so_id='"+SO_ID+"'");
                            Log.e(".......COUNT:", ""+c.getCount());
                            String Quantity =  QuantityEd.getText().toString().trim();
                            double giventQty=Double.parseDouble(Quantity);
                            double stockqty=0;
                            if(c!=null)
                            {  // c.close();
                                if(c.moveToFirst())
                                {
                                    do{

                                        Log.e("vanqty", ""+c.getDouble(0));

                                        Log.e("Total vanqty", ""+stockqty);
                                        if(TempData.editMemo.equalsIgnoreCase("true")){
                                           // stockqty=memo_details_quantity+c.getDouble(0);
                                            Log.e("Loggggg",boolean_quantity+" "+memo_details_quantity);
                                            stockqty = c.getDouble(0)-( boolean_quantity -
                                                    memo_details_quantity)+bonus_quantity;


                                        }
                                        else
                                        {
                                            stockqty=c.getDouble(0)-boolean_quantity;


                                        }



                                    }while(c.moveToNext());

                                }



                            }
                            c.close();
                            db.close();*/

                           // stockqty = Double.parseDouble(roundTwoDecimals(stockqty));

                         /*   if(giventQty>stockqty)
                            {



                                QuantityEd.setText(String.valueOf(stockqty));
                                savePreference("bonus"+_product_id, String.valueOf(stockqty));


                                if(getIndex(_product_id)<0)
                                {
                                    HashMap<String,String> map=new HashMap<String,String>();
                                    map.put("product_id", _product_id);
                                    map.put("product_name", product_name);
//                                    map.put("product_type_id", _product_type_id);
                                    map.put("quantity", getPreference("bonus"+_product_id));
                                    TempData.BonusProductQuantity.add(map);
                                }
                                else
                                {

                                }

                                TempData.BonusProductQuantity.get(getIndex(_product_id)).put("quantity",  getPreference("bonus"+_product_id));
                               // Toast.makeText(context, "Your stock limit for this product is "+stockqty, Toast.LENGTH_LONG).show();

                            }
                            else
                            {*/
//					        	 QuantityEd.setText(String.valueOf(giventQty));
                                savePreference("bonus"+_product_id, String.valueOf(giventQty));


                                if(getIndex(_product_id)<0)
                                {
                                    HashMap<String,String> map=new HashMap<String,String>();
                                    map.put("product_id", _product_id);
                                    map.put("product_name", product_name);
//                                    map.put("product_type_id", _product_type_id);
                                    map.put("quantity", getPreference("bonus"+_product_id));
                                    TempData.BonusProductQuantity.add(map);
                                }
                                else
                                {
                                    TempData.BonusProductQuantity.get(getIndex(_product_id)).put("quantity",  getPreference("bonus"+_product_id));
                                }
                            //}

                        }


                        else
                        {
                           // Toast.makeText(context, "Please enter correct number format!", Toast.LENGTH_SHORT).show();
                            QuantityEd.setText("");
                        }



                    }


            }
        });






        return view2;
    }




    int getIndex(String value){
        int pos = -1;
        for(int i=0;i<TempData.BonusProductQuantity.size();i++)
        {
            HashMap<String,String> map=TempData.BonusProductQuantity.get(i);
            Log.e("PRODUCT_ID", map.get("product_id"));
            if(map.get("product_id").equalsIgnoreCase(value))
                pos=i;
        }

        return pos;
    }

    public void savePreference(String key, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public String getPreference(String key)
    {
        String value="";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        value = prefs.getString(key, "");

        return value;

    }



    public boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean CheckValidation()
    {
//        Log.e("ItemListFromDBForDetails",TempData.BonusProductQuantity.toString());
        for(int i=0;i<TempData.BonusProductQuantity.size();i++)
        {
            HashMap<String,String> map=TempData.BonusProductQuantity.get(i);
            Log.e("quantity", map.get("quantity"));
            Log.e("product_ID", map.get("product_id"));


            if(map.get("quantity").equalsIgnoreCase("0.0")||map.get("quantity").equalsIgnoreCase("0")||map.get("quantity").equalsIgnoreCase("")) {
//			if(map.get("Flag").equals("1"))

                Log.e("invalide", "Invalide");
                return false;
            }

        }
        return true;
    }


   /* private void TempBonus()
    {

        for(int k=0; k<TempData.TotalBonusProductList.size(); k++)
        {
            savePreference("bonus"+TempData.TotalBonusProductList.get(k).get("product_id"),"0.0");
            Log.e("TempBonus()", "TempBonus()"+getPreference("bonus"+TempData.TotalBonusProductList.get(k).get("product_id")));
        }
    }*/


    public String roundTwoDecimals(double d)
    {
        Log.e("tEST:", String.format("%.2f", d));

        Log.e("Double:", String.valueOf(d));
        Log.e("Modulus:", String.valueOf(d%1));
        return String.format("%.2f", d);
    }

}
