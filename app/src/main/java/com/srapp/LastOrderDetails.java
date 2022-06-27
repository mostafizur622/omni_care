package com.srapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.srapp.Adapter.AdapterForMemoDetails;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Model.OrderDetailsModel;
import com.srapp.Util.Parent;
import com.srapp.print.PrintActivity;
import com.srapp.print.PrintActivity_For_Memo;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.MEMOS_ORDER_NUMBER;
import static com.srapp.Db_Actions.Tables.MEMOS_TOTAL_Vat;
import static com.srapp.Db_Actions.Tables.MEMOS_editable;
import static com.srapp.Db_Actions.Tables.MEMOS_for_memo_delete;
import static com.srapp.Db_Actions.Tables.MEMOS_from_app;
import static com.srapp.Db_Actions.Tables.MEMOS_gross_value;
import static com.srapp.Db_Actions.Tables.MEMOS_is_pushed;
import static com.srapp.Db_Actions.Tables.MEMOS_market_id;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_date;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_date_time;
import static com.srapp.Db_Actions.Tables.MEMOS_memo_number;
import static com.srapp.Db_Actions.Tables.MEMOS_outlet_id;
import static com.srapp.Db_Actions.Tables.MEMOS_table_id;
import static com.srapp.Db_Actions.Tables.ORDER_STATUS;
import static com.srapp.Db_Actions.Tables.ORDER_TOTAL_VAT;
import static com.srapp.Db_Actions.Tables.ORDER_editable;
import static com.srapp.Db_Actions.Tables.ORDER_for_order_delete;
import static com.srapp.Db_Actions.Tables.ORDER_from_app;
import static com.srapp.Db_Actions.Tables.ORDER_gross_value;
import static com.srapp.Db_Actions.Tables.ORDER_is_pushed;
import static com.srapp.Db_Actions.Tables.ORDER_market_id;
import static com.srapp.Db_Actions.Tables.ORDER_order_date;
import static com.srapp.Db_Actions.Tables.ORDER_order_date_time;
import static com.srapp.Db_Actions.Tables.ORDER_order_number;
import static com.srapp.Db_Actions.Tables.ORDER_outlet_id;
import static com.srapp.Db_Actions.Tables.ORDER_table_id;
import static com.srapp.Db_Actions.Tables.PROCESSING_COMPELETE;
import static com.srapp.Db_Actions.Tables.PROCESSING_PENDING;
import static com.srapp.Db_Actions.Tables.PRODUCT_BOOLEAN_QUANTITY;
import static com.srapp.Db_Actions.Tables.PRODUCT_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRICE_PRICE;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MEMOS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_MEMO_DETAILS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_ORDER_DETAILS;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_PRODUCT;
import static com.srapp.Db_Actions.Tables.TABLE_NAME_PRODUCT_BOOLEAN;
import static com.srapp.TempData.BonusArrayList;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.ORDER_TO_MEMO;
import static com.srapp.TempData.TotalBonusProductList;


public class LastOrderDetails extends Parent implements BasicFunctionListener {

    double subtotal=0.00;
    ListView list;
    AdapterForMemoDetails adapter;
    OrderDetailsModel orderDetailsModel;
    ArrayList<HashMap<String,String>> Data;
    ImageView imageView;
    TextView txtGift,txtBonus,total_price,discountt,sub_total,vat,user_txt_view;
    Data_Source db;
LinearLayout temp;
    int memo=0;
    ImageView homeBtn,backBtn;
    HashMap<String,String> map;
    BasicFunction bf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_order_report);
        db = new Data_Source(this);
        bf= new BasicFunction(this,this);
        imageView = findViewById(R.id.home);
        txtGift = findViewById(R.id.txtGift);
        txtBonus = findViewById(R.id.txtBonus);
        user_txt_view = findViewById(R.id.user_txt_view);
        temp = findViewById(R.id.temp);
        discountt = findViewById(R.id.discount);
        vat = findViewById(R.id.vat);
        sub_total = findViewById(R.id.sub_total);
        total_price = findViewById(R.id.total_price);
        list = findViewById(R.id.list);
        user_txt_view.setText(bf.getPreference(SR_ID));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LastOrderDetails.this,OutletList.class));
            }
        });
        temp.setVisibility(View.GONE);
        if (getIntent()!=null) {
            memo = getIntent().getIntExtra("memo", 0);


        }
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {


                startActivity(new Intent( LastOrderDetails.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {


                startActivity(new Intent( LastOrderDetails.this, ViewLastRecord.class));
                finish();


            }
        });

        String Query="";
        if (!TempData.MEMO_EDIT)
            Query="SELECT "+ ORDER_TOTAL_VAT+" FROM "+TABLE_NAME_ORDER+" where "+ ORDER_order_number+"='"+TempData.orderNumber +"'";
        else
            Query= "SELECT "+MEMOS_TOTAL_Vat +" FROM "+TABLE_NAME_MEMOS+" where "+ MEMOS_memo_number+"='"+TempData.orderNumber +"'";
        Cursor c = db.rawQuery(Query);
        Log.e("Quearymem",Query+" sixe"+c.getCount());
        c.moveToFirst();

        if (c!=null && c.getCount()>0){

            vat.setText(c.getString(0));
        }

        Log.e("ordernum", TempData.orderNumber+"no");
        db.getOrderDetails();

        SaleAbleList();
        SaleAbleListForAll();
        Bonus();
        Gift();

    }

    private void SaleAbleList() {
        Data=new ArrayList<HashMap<String,String>>();
        Data.clear();
        String memo_id="";
        if (!MEMO_EDIT)
            memo_id="SELECT "+PRODUCT_ID+", "+PRODUCT_BOOLEAN_QUANTITY+", "+PRODUCT_PRICE_PRICE+" FROM "+TABLE_NAME_ORDER_DETAILS+" WHERE "+ORDER_order_number+"='"+TempData.orderNumber+"' AND product_type='0'";
        else
            memo_id="SELECT "+PRODUCT_ID+", "+PRODUCT_BOOLEAN_QUANTITY+", "+PRODUCT_PRICE_PRICE+" FROM "+TABLE_NAME_MEMO_DETAILS+" WHERE "+MEMOS_memo_number+"='"+TempData.memoNumber+"' AND product_type='0'";

        Log.e("MemoDetails",memo_id);

        Cursor cursor=db.rawQuery(memo_id);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                String product_name="";
                do{
                    Cursor c1=db.rawQuery("SELECT "+PRODUCT_PRODUCT_NAME+" From "+TABLE_NAME_PRODUCT+" WHERE "+PRODUCT_ID+"='"+cursor.getString(0)+"'");
                    if(c1!=null)
                    {
                        if(c1.moveToFirst())
                        {
                            do{
                                product_name=c1.getString(0);
                            }while(c1.moveToNext());
                        }

                    }
                    map = new HashMap<String,String>();
                    map.put(PRODUCT_PRODUCT_NAME, product_name);
                    map.put(PRODUCT_BOOLEAN_QUANTITY, String.valueOf(cursor.getDouble(1)));
                    map.put(PRODUCT_PRICE_PRICE, roundTwoDecimals(cursor.getDouble(2)));
                    String tPrice=roundTwoDecimals(cursor.getDouble(1)*cursor.getDouble(2));

                    map.put("total_price", String.valueOf(tPrice));
                    map.put(PRODUCT_ID, cursor.getString(0));
                    Data.add(map);
                    subtotal = subtotal+Double.parseDouble(tPrice);
                }while(cursor.moveToNext());
            }
        }
        total_price.setText(subtotal+"");
        TempData.InvoiceTotal = String.valueOf(subtotal);

        Log.e("Size", String.valueOf(Data.size()));

        adapter=new AdapterForMemoDetails(LastOrderDetails.this,Data);

        list.setAdapter(adapter);

        getDiscount(subtotal+"");
        //TempData.INVOICE_DETAILS=Data;



    }
    private void SaleAbleListForAll() {
        // TODO Auto-generated method stub


        ArrayList<HashMap<String,String>> Data=new ArrayList<HashMap<String,String>>();
        Data.clear();
        String memo_id="";
        if (!MEMO_EDIT)
            memo_id="SELECT "+PRODUCT_ID+", "+PRODUCT_BOOLEAN_QUANTITY+", "+PRODUCT_PRICE_PRICE+" FROM "+TABLE_NAME_ORDER_DETAILS+" WHERE "+ORDER_order_number+"='"+TempData.orderNumber+"'";
        else
            memo_id="SELECT "+PRODUCT_ID+", "+PRODUCT_BOOLEAN_QUANTITY+", "+PRODUCT_PRICE_PRICE+" FROM "+TABLE_NAME_MEMO_DETAILS+" WHERE "+MEMOS_memo_number+"='"+TempData.memoNumber +"'";

        Log.e("MemoDetails",memo_id);

        Cursor cursor=db.rawQuery(memo_id);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                String product_name="";
                do{
                    String productId =cursor.getString(0);
                    Log.e("productId: ", productId);
                    Cursor c1=db.rawQuery("SELECT "+PRODUCT_PRODUCT_NAME+" From "+TABLE_NAME_PRODUCT+" WHERE "+PRODUCT_ID+"='"+productId+"'");
                    Log.e("raw Query : ", "SELECT product_name From products WHERE product_id='"+productId+"'");
                    if(c1!=null)
                    {
                        if(c1.moveToFirst())
                        {
                            do{
                                product_name=c1.getString(0);
                            }while(c1.moveToNext());
                        }

                    }
                    HashMap<String,String> map=new HashMap<String,String>();
                    map.put(PRODUCT_PRODUCT_NAME, product_name);
                    map.put(PRODUCT_BOOLEAN_QUANTITY, String.valueOf(cursor.getDouble(1)));
                    map.put(PRODUCT_PRICE_PRICE, roundTwoDecimals(cursor.getDouble(2)));
                    String tPrice=roundTwoDecimals(cursor.getDouble(1)*cursor.getDouble(2));
                    map.put("total_price", String.valueOf(tPrice));
                    map.put(PRODUCT_ID, cursor.getString(0));
                    Data.add(map);

                }while(cursor.moveToNext());
            }
        }
        Log.e("Size", String.valueOf(Data.size()));

	/*	adapter=new AdapterForMemoDetails(MemoDetails.this,Data);
		ListView inst_list=(ListView)findViewById(R.id.inst_list);
		inst_list.setAdapter(adapter);*/

        TempData.INVOICE_DETAILS_FOR_ALL_PRODUCT=Data;


    }
    private void Bonus() {
        Log.e("T1",TempData.TempBonus+"no data");
        TempData.TempBonus="";
        // TODO Auto-generated method stub
        ArrayList<HashMap<String,String>> bonusList=new ArrayList<HashMap<String,String>>();

        String memoQuery="";
        if (!MEMO_EDIT)
            memoQuery="SELECT "+PRODUCT_ID+", "+PRODUCT_BOOLEAN_QUANTITY+" FROM "+TABLE_NAME_ORDER_DETAILS+" WHERE "+ORDER_order_number+"='"+TempData.orderNumber +"' AND product_type='2'";
        else
            memoQuery="SELECT "+PRODUCT_ID+", "+PRODUCT_BOOLEAN_QUANTITY+" FROM "+TABLE_NAME_MEMO_DETAILS+" WHERE "+MEMOS_memo_number+"='"+TempData.memoNumber +"' AND product_type='2'";

        Log.e("memoQuery",memoQuery);
        Cursor cursor=db.rawQuery(memoQuery);
        Log.e("memoQueryCount", String.valueOf(cursor.getCount()));
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                String product_name="";
                do{
                    String productId =cursor.getString(0);
                    Cursor c1=db.rawQuery("SELECT "+PRODUCT_PRODUCT_NAME+" From "+TABLE_NAME_PRODUCT+" WHERE "+PRODUCT_ID+"='"+productId+"'");
                    if(c1!=null)
                    {
                        if(c1.moveToFirst())
                        {
                            do{
                                product_name=c1.getString(0);
                            }while(c1.moveToNext());
                        }

                    }

                    HashMap<String,String> map=new HashMap<String,String>();
                    map.put("product_id", cursor.getString(0));
                    map.put("product_name", product_name);
                    map.put("quantity", cursor.getString(1));
                    savePreference("bonus"+cursor.getString(0),cursor.getString(1));
                    bonusList.add(map);
                }while(cursor.moveToNext());
            }
        }

        String bonus="";
        TempData.BonusShowList.clear();
        for(int i=0;i<bonusList.size();i++)
        {



            HashMap<String, String> map=bonusList.get(i);
            bonus=bonus+map.get("product_name")+"("+map.get("quantity")+")";

            HashMap<String, String> product_map = new HashMap<String, String>();

            product_map.put("bonus_name",map.get("product_name"));
            product_map.put("quantity",map.get("quantity"));


//            BonusItemList.add(product_map);
            TempData.BonusShowList.add(product_map);

            if(i!=bonusList.size()-1)
                bonus=bonus+", ";
        }
        if(bonus.length()>0)
        {
            TempData.TempBonus="";
            txtBonus.setText(bonus);
            TempData.TempBonus=bonus;
        }
        else
        {
            LinearLayout llBonus=(LinearLayout)findViewById(R.id.llBonus);
            llBonus.setVisibility(View.GONE);
        }
        //txtBonus.setText("Nill");
        BonusArrayList=bonusList;

        Log.e("T",TempData.TempBonus+"no data");
    }







    private void Gift() {
        // TODO Auto-generated method stub
        ArrayList<HashMap<String,String>> giftList=new ArrayList<HashMap<String,String>>();

        String memoGIFTQuery="SELECT "+PRODUCT_ID+", "+PRODUCT_BOOLEAN_QUANTITY+" FROM "+TABLE_NAME_ORDER_DETAILS+" WHERE "+ORDER_order_number+"='"+TempData.orderNumber +"' AND product_type='1'";
        Log.e("memoGIFTQuery",memoGIFTQuery);
        Cursor cursor=db.rawQuery(memoGIFTQuery);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                String product_name="";
                do{

                    String productId =cursor.getString(0);
                    Cursor c1=db.rawQuery("SELECT "+PRODUCT_PRODUCT_NAME+" From "+TABLE_NAME_PRODUCT+" WHERE "+PRODUCT_ID+"='"+productId+"'");

                    if(c1!=null)
                    {
                        if(c1.moveToFirst())
                        {
                            do{
                                product_name=c1.getString(0);

                            }while(c1.moveToNext());
                        }

                    }
                    HashMap<String,String> map=new HashMap<String,String>();
                    map.put("product_id", cursor.getString(0));
                    map.put("product_name", product_name);
                    map.put("quantity", cursor.getString(1));
                    giftList.add(map);
                }while(cursor.moveToNext());
            }
        }

        Log.e("giftList : ", giftList.toString());

        String gift="";
        for(int i=0;i<giftList.size();i++)
        {
            HashMap<String, String> map=giftList.get(i);
            gift=gift+map.get("product_name")+"("+map.get("quantity")+")";
            if(i!=giftList.size()-1)
                gift=gift+", ";

            Log.e("gift: ", gift);
        }
        if(gift.length()>0)
        {
            txtGift.setText(gift);
            TempData.TempGift=gift;
        }
        else
        {
            LinearLayout llGift=(LinearLayout)findViewById(R.id.llGift);
            llGift.setVisibility(View.GONE);
        }
        TempData.GiftArrayList=giftList;
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        if (i==111){

            db.excQuery("delete from "+TABLE_NAME_ORDER+" where "+ORDER_order_number+" ='"+TempData.orderNumber+"'");
            db.excQuery("delete from "+TABLE_NAME_ORDER_DETAILS+" where "+ORDER_order_number+" ='"+TempData.orderNumber+"'");
            finish();
        }
    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(this,"NO Internet Connection",Toast.LENGTH_SHORT);
    }


    private void getDiscount(String s) {

        int distype=0;
        Double discountp = 0.0;
        Cursor cursor =  db.rawQuery("select discount_percent,discount_type from discounts where memo_value <="+s+" and date_from <= '"+TempData.MemoDate+"' and date_to>='"+TempData.MemoDate+"' order by memo_value DESC limit 1");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {

            discountp = cursor.getDouble(0);
            distype = TempData.DISTYPE = cursor.getInt(1);
        }
        Double discount = 0.0;
        Double memoValue = Double.parseDouble(s);
        if (distype==1) {
            TempData.DISCOUNTP = discountp;
            discount = (memoValue * discountp) / 100;
        }else if (distype==2){

            discount =discountp;
        }
        discountt.setText(roundTwoDecimals(discount));
        sub_total.setText(roundTwoDecimals(memoValue - discount));


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            startActivity(new Intent( LastOrderDetails.this, ViewLastRecord.class));
            finish();

        }
        return super.onKeyDown(keyCode, event);

    }
}
