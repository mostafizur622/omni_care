package com.srapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.srapp.Adapter.GiftIssueAdaper;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiftIssueActivity extends ParentActivity implements View.OnClickListener, BasicFunctionListener {

    GiftIssueAdaper Adapter;
    TextView DateTv;
    private ImageView btnBack, btnHome;
    TextView HeaderTitleTv, CodeNoTv;


    SimpleCursorAdapter adapter;
    String  date="";

    Spinner OutletSp, MarketSp;

    ArrayList<HashMap<String, String>> ItemListFromDB = new ArrayList<HashMap<String, String>>();

    List<String> listThanaId= new ArrayList<String>();
    List<String> listThanaName= new ArrayList<String>();

    String ThanaId,ThanaName;

    ArrayList<String> OutletID=new ArrayList<String>();
    ArrayList<String> OutletName=new ArrayList<String>();
    ArrayList<String> PharmaType=new ArrayList<String>();
    String _OutletID,_OutletName;


    ArrayList<String> MarketID=new ArrayList<String>();
    ArrayList<String> MarketName=new ArrayList<String>();
    String _MarketID;

    int market_pos,outlet_pos;

   public static ArrayList<HashMap<String, String>> itemListContent = new ArrayList<HashMap<String, String>>();
    Spinner ProductCategorySp,ProductNameSp,ThanaNameSp;


    String _product_id="", _product_name="",_product_quantity="",_quantity_for_mini="";
    Data_Source db;
    String giftIssue_id,gift_date;
    int state;
    BasicFunction basicFunction;
    Button SaveButton;
    EditText remarks;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gift_issue);

        db=new Data_Source(this);
        basicFunction = new BasicFunction(this,this);
          remarks = findViewById(R.id.remarks);
          Log.e("is_null",(getIntent()!=null)+"");

        if (getIntent()!=null){
            giftIssue_id = getIntent().getStringExtra("gift_id");
            gift_date = getIntent().getStringExtra("gift_date");
            state = getIntent().getIntExtra("state",5);
           // state = getIntent().getIntExtra("state",5);
            Log.e("state",state+"");
            SaveButton = findViewById(R.id.SaveButton);
            if (state==1) {

                SaveButton.setText("Update");
            }else if (state==0) {
                SaveButton.setVisibility(View.GONE);
            }
            basicFunction.savePreference("gift_id",giftIssue_id);
            basicFunction.savePreference("gift_date",gift_date);
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("so_id",basicFunction.getPreference(SR_ID));
                jsonObject.put("mac",basicFunction.getPreference("mac"));
                jsonObject.put("item_id",giftIssue_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

          //  basicFunction.getResponceData(URL.GIFT_ITEM_Details,jsonObject.toString(),111);

            ProgressDialog dailog = URL.CheckConnection(GiftIssueActivity.this,"Gift Item Details Loading...");
            if (dailog==null)
                return;
            getJAPi().GIFT_ITEM_Details(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();

                            JSONArray jsonArray = jsonObject.getJSONObject("details").getJSONArray("gift_issue_details");
                            for(int i = 0 ; i<jsonArray.length() ; i++){

                                setgift_issue(jsonArray.getJSONObject(i));
                            }

                            setoutletSetAddress(jsonObject.getJSONObject("details").getJSONObject("gift_issue").getString("outlet_id"));

                            remarks.setText(jsonObject.getJSONObject("details").getJSONObject("gift_issue").getString("remarks"));




                    } catch (JSONException e) {
                       Log.e("exception",e.getLocalizedMessage());
                    }

                            Adapter = new GiftIssueAdaper(GiftIssueActivity.this, getPreference("SO"), getPreference("FiscalYearID"),state,itemListContent);
                            ListView listView = (ListView) findViewById(R.id.list);
                            listView.setAdapter(Adapter);
                            if (state==0) {
                                for (int i = 0; i < itemListContent.size(); i++) {
                                    if (itemListContent.get(i).get("quantity").equalsIgnoreCase("")) {
                                        itemListContent.remove(i);
                                        Adapter.notifyDataSetChanged();
                                    }
                                }
                            }



//
//
                        }
                    });


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dailog.dismiss();
                }
            });

        }


        TextView txtUser=(TextView)findViewById(R.id.user_txt_view);
        txtUser.setText(getPreference(SR_ID));


        //SaveButton = (Button)findViewById(R.id.SaveButton);

        HeaderTitleTv = (TextView)findViewById(R.id.title_tv);

        HeaderTitleTv.setText("Gift Issue");
        //CodeNoTv.setText(getPreference("SO"));

        initiateButtons(btnBack, R.id.back);
        initiateButtons(btnHome, R.id.home);

        TextView OutletTV=(TextView)findViewById(R.id.OutletTV);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels/22;


        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutBg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(width, 0, width, 0);
        layout.setLayoutParams(params);



        OutletSp = (Spinner)findViewById(R.id.OutletSp);
        MarketSp = (Spinner)findViewById(R.id.MarketSp);
        ThanaNameSp=(Spinner)findViewById(R.id.spThanaName);






//		MarketParse();
        ThanaParse();


        ThanaNameSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                Log.e("ThanaId","thana");
                ThanaId = listThanaId.get(arg2);
                ThanaName = listThanaName.get(arg2);
                MarketParse(ThanaId);


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        MarketSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _MarketID = MarketID.get(arg2);
                String Market_name = MarketName.get(arg2);
                OutletParse(_MarketID);



            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        OutletSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _OutletID = OutletID.get(arg2);
                _OutletName = OutletName.get(arg2);
                savePreference("PharmaType", PharmaType.get(arg2));
                TempData.OutletID=_OutletID;


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        ProductDetails();

    }



    private void ThanaParse()
    {
        listThanaId.clear();
        listThanaName.clear();
        listThanaId.add("0");
        listThanaName.add("Select Route");
        Cursor c = db.rawQueryCoustom("SELECT * FROM route ORDER BY route_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String thana_id = c.getString(c.getColumnIndex("route_id"));
                    String thana_name = c.getString(c.getColumnIndex("route_name"));

                    listThanaId.add(thana_id);
                    listThanaName.add(thana_name);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GiftIssueActivity.this,R.layout.spinner_text, listThanaName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ThanaNameSp.setAdapter(dataAdapter);
        }
    }




    private void OutletParse(String MarketID)
    {
        OutletID.clear();
        OutletID.add("0");
        OutletName.clear();
        OutletName.add("All");
        PharmaType.clear();
        PharmaType.add("");
        Cursor c;
	/*if(!MarketID.equalsIgnoreCase("0"))
	c=db.rawQuery("SELECT * FROM outlets where market_id = "+"'"+MarketID+"' and isDeactivated!='1' ORDER BY outlet_name COLLATE NOCASE ASC");
	else
		c=db.rawQuery("SELECT * FROM outlets WHERE isDeactivated!='1' ORDER BY outlet_name COLLATE NOCASE ASC");*/
        c=db.rawQueryCoustom("SELECT * FROM outlets where market_id = "+"'"+MarketID+"'  ORDER BY outlet_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String outlet_id = c.getString(c.getColumnIndex("outlet_id"));
                    String outlet_name = c.getString(c.getColumnIndex("outlet_name"));

                    OutletID.add(outlet_id);
                    OutletName.add(outlet_name);
                    PharmaType.add( c.getString(c.getColumnIndex("pharma_type")));

                } while (c.moveToNext());
            }


            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GiftIssueActivity.this,R.layout.spinner_text, OutletName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            OutletSp.setAdapter(dataAdapter);

            if (getIntent()!=null){
                OutletSp.setSelection(outlet_pos);
                Log.e("OutletSp",outlet_pos+"");
            }

        }

    }

    private void MarketParse(String Thana_ID)
    {
        MarketID.clear();
        MarketID.add("0");
        MarketName.clear();
        MarketName.add("All");
        Cursor c = db.rawQueryCoustom("SELECT * FROM markets where  route_id="+"'"+Thana_ID+"' ORDER BY market_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Market_Id = c.getString(c.getColumnIndex("market_id"));
                    String Market_Name = c.getString(c.getColumnIndex("market_name"));

                    MarketID.add(Market_Id);
                    MarketName.add(Market_Name);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GiftIssueActivity.this,R.layout.spinner_text, MarketName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            MarketSp.setAdapter(dataAdapter);

            if (getIntent()!=null){
                MarketSp.setSelection(market_pos);
            }
        }

    }



    private void ProductDetails()
    {
        itemListContent.clear();

        Cursor c1 =db.rawQueryCoustom("SELECT product_id,product_name FROM product  WHERE product_type_id in(3,2) GROUP BY product_id");

        if (c1 != null) {
            if (c1.moveToFirst()) {
                do {


                    String product_id = c1.getString(c1.getColumnIndex("product_id"));




                    HashMap<String, String> product_list_map = new HashMap<String, String>();

                    product_list_map.put("product_id",c1.getString(0));
                    product_list_map.put("product_name",c1.getString(1 ));
                    product_list_map.put("boolean", "false");
                    product_list_map.put("quantity","");
                    itemListContent.add(product_list_map);


                } while (c1.moveToNext());
            }






        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            Intent idd = new Intent(GiftIssueActivity.this, GiftIssueList.class);
            startActivity(idd);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    public void initiateButtons(ImageView btn, int id)
    {
        btn =  findViewById(id);
        btn.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if(v.getId()==R.id.back)
        {

            Intent idd = new Intent(GiftIssueActivity.this, GiftIssueList.class);
            startActivity(idd);
            finish();
        }

        else if(v.getId()==R.id.home){
            Intent intent = new Intent(GiftIssueActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        }

    }


    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {








    }

    private void setoutletSetAddress(String outlet_id) {

        Cursor cursor = db.rawQueryCoustom("select m.route_id , m.market_id from outlets as o inner Join markets as m on o.market_id==m.market_id where o.outlet_id = '"+outlet_id+"'");
        cursor.moveToFirst();
        if (cursor!=null && cursor.getCount()>0 ){
            MarketParse(cursor.getString(0));
            OutletParse(cursor.getString(1));
            ThanaNameSp.setSelection(setthanSelection(cursor.getString(0)));
            Log.e("setMarketSelection",setMarketSelection(cursor.getString(1))+"");
            Log.e("setOutletSelection",setOutletSelection(outlet_id)+"");
            market_pos = setMarketSelection(cursor.getString(1));
            outlet_pos = setOutletSelection(outlet_id);



        }



    }

    private int setOutletSelection(String outlet_id) {
        for (int i=0 ; i<OutletID.size() ;i++){
            if (OutletID.get(i).equalsIgnoreCase(outlet_id)){

                return i;
            }

        }

        return 0;
    }

    private int setMarketSelection(String market_id) {
        for (int i=0 ; i<MarketID.size() ;i++){
            if (MarketID.get(i).equalsIgnoreCase(market_id)){

                return i;
            }

        }

        return 0;
    }

    private int setthanSelection(String route_id) {
        for (int i=0 ; i<listThanaId.size() ;i++){
            if (listThanaId.get(i).equalsIgnoreCase(route_id)){

                return i;
            }

        }

        return 0;

    }

    private void setgift_issue(JSONObject jsonObject) throws JSONException {
        for (int i=0 ; i<itemListContent.size() ; i++){
            if (itemListContent.get(i).get("product_id").equalsIgnoreCase(jsonObject.getString("product_id"))){
                itemListContent.get(i).put("boolean", "true");
                itemListContent.get(i).put("quantity",jsonObject.getString("quantity"));
              //  Adapter.notifyDataSetChanged();
            }


        }


    }

    @Override
    public void OnConnetivityError() {

    }
}
