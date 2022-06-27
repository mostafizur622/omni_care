package com.srapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForBonusPartyReport;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.URL;
import com.srapp.Model.Reports;
import com.srapp.Util.ParentActivity;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.srapp.Db_Actions.Tables.MARKET_ID;
import static com.srapp.Db_Actions.Tables.SR_ID;

public class ReportBonusPartyReport extends ParentActivity implements View.OnClickListener, BasicFunctionListener {

    TextView  DateTv;
    private ImageView btnBack, btnHome;
    TextView HeaderTitleTv, CodeNoTv;

    ListView listViewMiniStore;
    SimpleCursorAdapter adapter;
    String  date="";

    Spinner ThanaSp, MarketSp, OutletCategorySp,BonusSp;



    ArrayList<String> ThanaID=new ArrayList<String>();
    ArrayList<String> ThanaName=new ArrayList<String>();
    String _ThanaID;


    ArrayList<String> MarketID=new ArrayList<String>();
    ArrayList<String> MarketName=new ArrayList<String>();
    String _MarketID;


    ArrayList<String> BonusID=new ArrayList<String>();
    ArrayList<String> BonusName=new ArrayList<String>();
    String _BonusIDD;

    ArrayList<String> FiscalYearID =new ArrayList<String>();
    ArrayList<String> FIscalyearCode =new ArrayList<String>();
    String _outletIDD;

    Button search;
    Spinner ProductCategorySp,ProductNameSp;
    Data_Source db;
    Spinner fiscalyearsp;
    BasicFunction bf;
    ArrayList<HashMap<String,String>> itemListDB=new ArrayList<HashMap<String,String>>();


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_report_bonus_party_report);
        db=new Data_Source(getApplicationContext());
        listViewMiniStore = (ListView)findViewById(R.id.list);
        bf = new BasicFunction(this,this);
        TextView txtUser=(TextView)findViewById(R.id.user_txt_view);
        txtUser.setText(getPreference(SR_ID));


        LinearLayout llOutlet=(LinearLayout)findViewById(R.id.llOutlet);
        //llOutlet.setVisibility(View.GONE);


        HeaderTitleTv = (TextView)findViewById(R.id.title_tv);
        CodeNoTv = (TextView)findViewById(R.id.user_txt_view);
        search = findViewById(R.id.search);
        HeaderTitleTv.setText("Bonus Party Report");
        CodeNoTv.setText(getPreference(SR_ID));

        initiateButtons(btnBack, R.id.btn_back);
        initiateButtons(btnHome, R.id.btn_home);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels/22;


        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutBg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(width, 0, width, 0);
        layout.setLayoutParams(params);



        ThanaSp = (Spinner)findViewById(R.id.ThanaSp);
        MarketSp = (Spinner)findViewById(R.id.MarketSp);
        BonusSp = (Spinner)findViewById(R.id.BonusTypeSp);
        fiscalyearsp = (Spinner)findViewById(R.id.spOutlets);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BonusPartyReports();
            }
        });


        FiscalyearParse();
        ThanaParse();
        BonusType();
        ThanaSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _ThanaID = ThanaID.get(arg2);
                String name = ThanaName.get(arg2);
                MarketParse(_ThanaID);
                //BonusPartyReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        MarketSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                _MarketID = MarketID.get(arg2);
                //OutletParse(_MarketID);
              //  BonusPartyReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        fiscalyearsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

               // BonusPartyReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });


        BonusSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

               // BonusPartyReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });


    }





    private void  BonusPartyReports()
    {

        if (fiscalyearsp.getSelectedItemPosition()<=0){
            Toast.makeText(this,"Please Select Fiscal year first",Toast.LENGTH_LONG).show();
            return;
        }
        if (ThanaSp.getSelectedItemPosition()<=0){
            Toast.makeText(this,"Please Select Route",Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("so_id",getPreference(SR_ID));
            jsonObject.put("fiscal_year_id",FiscalYearID.get(fiscalyearsp.getSelectedItemPosition()));
            jsonObject.put("route_id",ThanaID.get(ThanaSp.getSelectedItemPosition()));
            jsonObject.put("market_id",MarketID.get(MarketSp.getSelectedItemPosition()));
            jsonObject.put("bonus_type_id",BonusID.get(BonusSp.getSelectedItemPosition()));
            jsonObject.put("mac",bf.getPreference("mac"));

            bf.getResponceData(URL.INCENTIVE_PARTY,jsonObject.toString(),111);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        /*
        itemListDB.clear();
        String whereCon="";
        int i = 0;
        if(MarketSp.getSelectedItemPosition()>0&& BonusSp.getSelectedItemPosition()>0)
        {
            whereCon=whereCon+" WHERE BP.market_id='"+MarketID.get(MarketSp.getSelectedItemPosition())+"'";
            whereCon=whereCon+" AND BP.bonus_party_id="+BonusID.get(BonusSp.getSelectedItemPosition());
            i=1;
        }

        if(MarketSp.getSelectedItemPosition()>0&& BonusSp.getSelectedItemPosition()==0)
        {
            whereCon=whereCon+" WHERE BP.market_id='"+MarketID.get(MarketSp.getSelectedItemPosition())+"'";
            i=1;
        }


        if(MarketSp.getSelectedItemPosition()==0 && BonusSp.getSelectedItemPosition()>0)
        {
            whereCon=whereCon+" WHERE BP.bonus_party_id="+BonusID.get(BonusSp.getSelectedItemPosition());
            i=1;
        }

        if (ThanaSp.getSelectedItemPosition()==0){
            whereCon="";
        }
        if (ThanaSp.getSelectedItemPosition()==0 &  BonusSp.getSelectedItemPosition()>0){
            whereCon=whereCon+" WHERE BP.bonus_party_id="+BonusID.get(BonusSp.getSelectedItemPosition());
            i=1;
        }

        if (ThanaSp.getSelectedItemPosition()>0 && MarketSp.getSelectedItemPosition()==0){
            if (i==0) {
                whereCon = whereCon + " WHERE BP.market_id in (select market_id from markets where thana_id = '" + ThanaID.get(ThanaSp.getSelectedItemPosition()) + "')";
            }else {
                whereCon = whereCon + " AND BP.market_id in (select market_id from markets where thana_id = '" + ThanaID.get(ThanaSp.getSelectedItemPosition()) + "')";
            }
            i=1;
        }

        if (ThanaSp.getSelectedItemPosition()>0 && MarketSp.getSelectedItemPosition()==0 &&  BonusSp.getSelectedItemPosition()>0){
            if (i==0) {
                whereCon = whereCon + " WHERE BP.market_id in (select market_id from markets where thana_id = '" + ThanaID.get(ThanaSp.getSelectedItemPosition()) + "')AND BP.bonus_party_id='"+BonusID.get(BonusSp.getSelectedItemPosition())+"'";
            }else {
                whereCon = whereCon + " AND BP.market_id in (select market_id from markets where thana_id = '" + ThanaID.get(ThanaSp.getSelectedItemPosition()) + "') AND BP.bonus_party_id='"+BonusID.get(BonusSp.getSelectedItemPosition())+"'";
            }
            i=1;
        }
        if (fiscalyearsp.getSelectedItemPosition()>0){
            if (i==0) {
                whereCon = whereCon + " WHERE BP.fiscal_year_id  = '" + FiscalYearID.get(fiscalyearsp.getSelectedItemPosition()) + "'";
            }else {
                whereCon = whereCon + " AND BP.fiscal_year_id  = '" + FiscalYearID.get(fiscalyearsp.getSelectedItemPosition()) + "'";
            }
            i=1;
        }

        String query="SELECT O.outlet_name, P.product_name, BP.target_quantity,BP.achieve_quantity,BP.stump FROM bonus_party BP LEFT JOIN outlets O ON(BP.outlet_id=O.outlet_id) LEFT JOIN products P ON(BP.product_id=P.product_id) "+whereCon;
        Log.e("QUERY:", query);
        Cursor c=db.rawQuery(query);
        if(c.getCount()>0)
        {
            if(c.moveToFirst())
            {
                do{

                    Log.e("-------- target --------", ""+Double.valueOf(c.getDouble(2)).longValue());
                    HashMap<String,String> map=new HashMap<String,String>();
                    map.put("outlet_name", c.getString(0));
                    map.put("product_name", c.getString(1));
                    map.put("target", ""+Double.valueOf(c.getDouble(2)).longValue());
                    map.put("achieve", ""+Double.valueOf(c.getDouble(3)).longValue());
                    map.put("stamp", ""+c.getString(4));

                    itemListDB.add(map);
                }while(c.moveToNext());
            }
        }*/



    }

    private void ThanaParse()
    {
        ThanaID.clear();
        ThanaName.clear();
        Cursor c = db.rawQuery("SELECT * FROM route");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Thana_Id = c.getString(c.getColumnIndex("route_id"));
                    String Thana_Name = c.getString(c.getColumnIndex("route_name"));

                    ThanaID.add(Thana_Id);
                    ThanaName.add(Thana_Name);

                } while (c.moveToNext());
            }

            ThanaName.add(0,"All");
            ThanaID.add(0,"0");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ReportBonusPartyReport.this,R.layout.spinner_text, ThanaName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ThanaSp.setAdapter(dataAdapter);

        }

    }

    private void MarketParse(String Thana_ID)
    {
        MarketID.clear();
        MarketName.clear();
        MarketID.add("0");
        MarketName.add("All");
        Cursor c = db.rawQuery("SELECT * FROM markets where route_id='"+Thana_ID+"'   ORDER BY market_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Market_Id = c.getString(c.getColumnIndex("market_id"));
                    String Market_Name = c.getString(c.getColumnIndex("market_name"));

                    MarketID.add(Market_Id);
                    MarketName.add(Market_Name);

                    Log.e("MarketID", Market_Id);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ReportBonusPartyReport.this,R.layout.spinner_text, MarketName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            MarketSp.setAdapter(dataAdapter);
        }

    }

    private void BonusType()
    {
        BonusID.clear();
        BonusName.clear();
        BonusID.add("0");
        BonusName.add("All");
        BonusID.add("1");
        BonusName.add("Small Bonus");
        BonusID.add("2");
        BonusName.add("Big bonus");



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ReportBonusPartyReport.this,R.layout.spinner_text, BonusName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BonusSp.setAdapter(dataAdapter);


    }

    private void FiscalyearParse()
    {
        FiscalYearID.clear();
        FIscalyearCode.clear();
        FiscalYearID.add("0");
        FIscalyearCode.add("All");

        Cursor c ;

        c= db.rawQuery("SELECT * FROM fiscal_year ");



        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Market_Id = c.getString(c.getColumnIndex("id"));
                    String Market_Name = c.getString(c.getColumnIndex("year_code"));

                    FiscalYearID.add(Market_Id);
                    FIscalyearCode.add(Market_Name);

                    Log.e("OUTLET ID", Market_Id);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ReportBonusPartyReport.this,R.layout.spinner_text, FIscalyearCode);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fiscalyearsp.setAdapter(dataAdapter);
        }

    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            Intent idd = new Intent(ReportBonusPartyReport.this, Reports_Activity.class);
            startActivity(idd);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    public void initiateButtons(ImageView btn, int id)
    {
        btn = (ImageView) findViewById(id);
        btn.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if(v.getId()==R.id.btn_back)
        {

            Intent idd = new Intent(ReportBonusPartyReport.this, Reports_Activity.class);
            startActivity(idd);
            finish();
        }

        else if(v.getId()==R.id.btn_home){
            Intent intent = new Intent(ReportBonusPartyReport.this, Dashboard.class);
            startActivity(intent);
            finish();
        }

    }


    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {
        itemListDB.clear();
        try {
            JSONArray jsonArray = jsonObject.getJSONObject("report").getJSONArray("report_data");
            for(int i = 0 ; i<jsonArray.length() ; i++){

                HashMap<String,String> map=new HashMap<String,String>();
                map.put("outlet_name", jsonArray.getJSONObject(i).getString("outlet_name"));
                map.put("product_name", jsonArray.getJSONObject(i).getString("product_name"));
                map.put("target", jsonArray.getJSONObject(i).getString("target"));
                map.put("achieve", jsonArray.getJSONObject(i).getString("achievement"));
                map.put("stamp", jsonArray.getJSONObject(i).getString("stamp"));

                itemListDB.add(map);
            }

            AdapterForBonusPartyReport adpater=new AdapterForBonusPartyReport(getApplicationContext(), itemListDB);
            listViewMiniStore.setAdapter(adpater);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void OnConnetivityError() {

    }
}
