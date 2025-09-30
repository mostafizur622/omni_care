package com.srapp;

import java.util.ArrayList;
import java.util.HashMap;


import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.srapp.Adapter.AutoCompeleteTextAdapter;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.GPSTracker;
import com.srapp.Util.Parent;
import com.srapp.Util.ParentActivity;
import com.tanvir.BasicFun.BasicFunctionListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;

import static com.srapp.Db_Actions.Tables.ORDER_START_TIME;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;
import static com.srapp.TempData.BPSelected_bonus;
import static com.srapp.TempData.BPSelected_option_id;
import static com.srapp.TempData.BPSelected_policy_type;
import static com.srapp.TempData.BPSelected_product;
import static com.srapp.TempData.BPSelected_set;
import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.ORDER_TO_MEMO;
import static com.srapp.TempData.PolicySetRelation;
import static com.srapp.TempData.policyArrayList;
import static com.srapp.Util.Constants.MIN_ORDER_NUMBER;
import static com.srapp.Util.Constants.PUSH_Failed_TIME;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Create_New_Memo extends Parent implements OnClickListener, DBListener , BasicFunctionListener {

    Button btnNext;
    Spinner routeSp, MarketSp, OutletCategorySp, OutletSp,offer_type_sp;
    int autoPos;
    ArrayAdapter<String> dataAdapter;
    ArrayAdapter<String> marketAdapter;

    ArrayList<String> Route_id = new ArrayList<String>();
    ArrayList<String> Route_name = new ArrayList<String>();
    String _routeid;
    String offer_type_id;

    ArrayList<String> MarketID = new ArrayList<String>();
    ArrayList<String> MarketName = new ArrayList<String>();
    String _MarketID;

    int DBRetriveStatus = 0;
    ArrayList<String> OutletCategoryID = new ArrayList<String>();
    ArrayList<String> OutletCategoryName = new ArrayList<String>();


    ArrayList<String> offer_id_list = new ArrayList<String>();
    ArrayList<String> offer_name_list = new ArrayList<String>();
    String _OutletCategoryID;


    ArrayList<String> OutletID = new ArrayList<String>();
    ArrayList<String> OutletName = new ArrayList<String>();


    ArrayList<String> Is_Pharma_Type = new ArrayList<String>();
    ArrayList<String> OutletCode = new ArrayList<String>();

    ArrayList<String> ProjectID = new ArrayList<String>();
    ArrayList<String> InstituteID = new ArrayList<String>();
    ArrayList<String> BonusPartyType = new ArrayList<String>();
    ArrayList<String> IsWithinGroup = new ArrayList<String>();
    AutoCompleteTextView outltateauto , marketauto;

    AutoCompeleteTextAdapter outlet_adapter, market_adapter;

    ArrayList<HashMap<String,String>> outlet_list = new ArrayList<>();

    String _OutletID, _InstituteID, _ProjectID, OUTLET_ID = "", _Is_Pharma_Type = "", _is_group_within_group;


    private Button btnBack, btnHome;
    TextView lastmemo, HeaderTitleTv, CodeNoTv, lastoutlatememodate;

    ArrayList<HashMap<String, String>> list;


    String Distributor_Id = "", RSO_Id = "", RSO_Code = "";
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    String itemName = "";
    Button marketAddBtn,addoutlet;
    ImageView homeBtn, backBtn;
    TextView userIdTV, titleTV;
    Button OuletlinkBtn, MarketLinkBtn;

    LinearLayout offer_type_lay;

    CheckBox radio_GPSUpdate, radio_ViewLastMemo, radio_SalesMemo, radio_CreditCollection, radio_CollectedList, radio_ProductReturn,outlet_visit,offer_type;

    Data_Source db;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_new_memo);
        outltateauto = findViewById(R.id.outltateauto);
        marketauto = findViewById(R.id.marketauto);
        _OutletID="00";
        TextView txtUser = (TextView) findViewById(R.id.user_txt_view);
        title = (TextView) findViewById(R.id.title);
        txtUser.setText(getPreference("UserName"));
        db = new Data_Source(this, this,this);
        //outltateauto.setText("");
        //savePreference("OutletID", "0") ;
        homeBtn = findViewById(R.id.home);
        offer_type_lay = findViewById(R.id.offer_type_lay);
        backBtn = findViewById(R.id.back);
        addoutlet = findViewById(R.id.addoutlet);
        marketAddBtn = findViewById(R.id.marketAddBtn);
        outlet_visit = findViewById(R.id.outlet_visit);
        offer_type = findViewById(R.id.offer_type);
        offer_type_sp = findViewById(R.id.offer_type_sp);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Create_New_Memo.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Create_New_Memo.this, Dashboard.class));
                finishAffinity();
            }
        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / 15;
        btnNext = (Button) findViewById(R.id.nextBtn);
        routeSp = (Spinner) findViewById(R.id.thana_Spinner);
        MarketSp = (Spinner) findViewById(R.id.market_Spinner);
        OutletCategorySp = (Spinner) findViewById(R.id.outlet_Type_Spinner);
        OutletSp = (Spinner) findViewById(R.id.outlet_spinner);

        radio_GPSUpdate = findViewById(R.id.gpsUpdateChk);
        radio_ViewLastMemo = findViewById(R.id.viewlastMemoChk);
        radio_SalesMemo = findViewById(R.id.salesMemoChk);
        lastmemo = findViewById(R.id.lastmemo);
        radio_ProductReturn = findViewById(R.id.outletReturnChk);
        FirebaseCrashlytics.getInstance().setUserId(getPreference("sr_uname"));
        radio_SalesMemo.setChecked(true);
        outltateauto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              //  outltateauto.setText("");
                outltateauto.showDropDown();

            }
        });

        marketauto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              //  outltateauto.setText("");
                marketauto.showDropDown();

            }
        });

        MarketLinkBtn = findViewById(R.id.marketAddBtn);
        OuletlinkBtn = findViewById(R.id.outletadd);

        itemName = radio_SalesMemo.getText().toString();
        radio_GPSUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                offer_type_lay.setVisibility(View.GONE);

                itemName = radio_GPSUpdate.getText().toString();
                itemName = "GPS Update";
                title.setText(itemName);
                settallfalse(new CheckBox[]{radio_GPSUpdate, radio_ViewLastMemo, radio_SalesMemo, radio_ProductReturn,outlet_visit,offer_type}, 0);
            }
        });
        radio_ViewLastMemo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                offer_type_lay.setVisibility(View.GONE);
                itemName = "View Last Memo";
                title.setText(itemName);
                settallfalse(new CheckBox[]{radio_GPSUpdate, radio_ViewLastMemo, radio_SalesMemo, radio_ProductReturn,outlet_visit,offer_type}, 1);
            }
        });
        radio_SalesMemo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                offer_type_lay.setVisibility(View.GONE);
                itemName = "Sales Order";
                title.setText(itemName);
                settallfalse(new CheckBox[]{radio_GPSUpdate, radio_ViewLastMemo, radio_SalesMemo, radio_ProductReturn,outlet_visit,offer_type}, 2);
            }
        });
        radio_ProductReturn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                offer_type_lay.setVisibility(View.GONE);
                itemName = "Collect Ncp";
                title.setText(itemName);
                settallfalse(new CheckBox[]{radio_GPSUpdate, radio_ViewLastMemo, radio_SalesMemo, radio_ProductReturn,outlet_visit,offer_type}, 3);
            }
        });

        outlet_visit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                offer_type_lay.setVisibility(View.GONE);
                itemName = "Outlet Visit";
                title.setText(itemName);
                settallfalse(new CheckBox[]{radio_GPSUpdate, radio_ViewLastMemo, radio_SalesMemo, radio_ProductReturn,outlet_visit,offer_type}, 4);
            }
        });

        offer_type.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                offer_type_lay.setVisibility(View.VISIBLE);
                itemName = "offer type";
                title.setText(itemName);
                settallfalse(new CheckBox[]{radio_GPSUpdate, radio_ViewLastMemo, radio_SalesMemo, radio_ProductReturn,outlet_visit,offer_type}, 5);
            }
        });
        MarketLinkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               /* final String route_id = getPreference("thanaID");
                Intent idn = new Intent(Create_New_Memo.this, CreateNewMarket.class);

                Log.e("Route_id123",route_id);
               // finish();
                startActivity(idn);*/
            }
        });
        Log.e("Route_id123", getPreference("thanaID"));
        OuletlinkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (db.checkOutletCreatePermission(getCurrentDateTime())){
                    Intent idn = new Intent(Create_New_Memo.this, CreateOutlet.class);
                    idn.putExtra("PathName", "OutletAccount");
                    startActivity(idn);
                    finish();
                }else {
                    Toast.makeText(Create_New_Memo.this,"You Don't Have Permission To Create Outlet",Toast.LENGTH_LONG).show();
                }



            }
        });

        marketAddBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Create_New_Memo.this,CreateNewMarket.class);
                i.putExtra("page_from",25);
                i.putExtra("PathName", "OutletAccount");
                i.putExtra("Route_id",getPreference("thanaID"));
                startActivity(i);
            }
        });

        addoutlet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
 //               if (db.checkOutletCreatePermission(getCurrentDateTime())) {
                    Intent i = new Intent(Create_New_Memo.this, CreateOutlet.class);
                    i.putExtra("page_from", -25);
                    i.putExtra("MarketID", getPreference("MarketID"));
                    i.putExtra("OutletCategoryID", getPreference("OutletCategoryID"));
                    startActivity(i);
//                }else {
//                    Toast.makeText(Create_New_Memo.this,"You Don't Have Permission To Create Outlet",Toast.LENGTH_LONG).show();
//                }
            }
        });

        ThanaParse();
        OfferType();
        OutletCategoriesTableParse();

        //.................spinnner selecton................................


        //.................spinnner selecton................................

        routeSp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _routeid = Route_id.get(arg2);
                String name = Route_name.get(arg2);
                savePreference("Thana", name);
                savePreference("thanaID", _routeid);

                MarketParse(_routeid);
                Log.e("****************", "&&&&&&&&&&&&&&&&&&&&&&" + _routeid + "     " + name);
                if (!Route_id.equals("00")) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //TODO Auto-generated method stub

            }
        });

        MarketSp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _MarketID = MarketID.get(arg2);
                savePreference("MarketID", _MarketID);
                String Market_name = MarketName.get(arg2);
                savePreference("Market", Market_name);

                //OutletCategoriesTableParse();

                Log.e("****************", "&&&&&&&&&&&&&&&&&&&&&&_Market  " + _MarketID + "     " + Market_name);


                if (!_MarketID.equals("00")) {
                    if (MarketID.size() > 0 && OutletCategoryID.size() > 0)
                        OutletAllTableParse(_MarketID, OutletCategoryID.get(OutletCategorySp.getSelectedItemPosition()));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        offer_type_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                offer_type_id = offer_id_list.get(arg2);
                savePreference("offer_type_id", offer_type_id);


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        marketauto.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               int  autoPos=MarketName.indexOf(marketAdapter.getItem(position));

                _MarketID = MarketID.get(autoPos);
                savePreference("MarketID", _MarketID);
                String Market_name = MarketName.get(autoPos);
                savePreference("Market", Market_name);

                //OutletCategoriesTableParse();

                Log.e("****************", "&&&&&&&&&&&&&&&&&&&&&&_Market  " + _MarketID + "     " + Market_name);

                if (!_MarketID.equals("00")) {
                    if (MarketID.size() > 0 && OutletCategoryID.size() > 0)
                        OutletAllTableParse(_MarketID, OutletCategoryID.get(OutletCategorySp.getSelectedItemPosition()));
                }
            }
        });

        OutletCategorySp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _OutletCategoryID = OutletCategoryID.get(arg2);
                savePreference("OutletCategoryID", _OutletCategoryID);
                String _OutletCategoryName = OutletCategoryName.get(arg2);
                savePreference("OutletCategoryName", _OutletCategoryName);

                // OutletTableParse();

                Log.d("OutletCategoryName",  _OutletCategoryID + OutletCategoryName );


                Log.e("MarketID size", "...." + MarketID.size());
                Log.e("utletCategoryID size", "...." + OutletCategoryID.size());
                Log.e("utletCategoryID size", "...." + MarketSp.getSelectedItemPosition());

               /* if (MarketSp.getSelectedItemPosition() >= 0) {
                    if (MarketID.size() > 0 && OutletCategoryID.size() > 0) {

                        OutletAllTableParse(MarketID.get(MarketSp.getSelectedItemPosition()), _OutletCategoryID);
                    }
                }*/

                try {
                    if (!_MarketID.equals("00")) {
                        if (MarketID.size() > 0 && OutletCategoryID.size() > 0)
                            OutletAllTableParse(_MarketID, OutletCategoryID.get(OutletCategorySp.getSelectedItemPosition()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        outltateauto.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int arg2, long id) {





              HashMap<String,String> map = outlet_adapter.getItem(arg2);

                Log.e("UUUUUUUUUUUUUUUUUU","UUUUUUUUUUU  _OutletID  UUUUUUUUUUUUU "+outlet_adapter.getItem(arg2).toString());
                _OutletID =map.get("Outlet_id");
                autoPos=OutletID.indexOf(_OutletID);
                String _OutletCode =  OutletCode.get(autoPos);;
                String _OutletName = map.get("outlet_name");

                String ins = InstituteID.get(autoPos);
                String ngo = ProjectID.get(autoPos);
                String is_withinGroup = IsWithinGroup.get(autoPos);

                savePreference("OutletName", _OutletName) ;
                savePreference("OutletID", _OutletID) ;
                setLastMemoDate(_OutletID);
                savePreference("BonusPartyType", BonusPartyType.get(arg2));
                savePreference("Is_WithinGroup", is_withinGroup);

                Log.e("------------","--  is_withinGroup  -- "+is_withinGroup);
                Log.e("------------","--  Is_WithinGroup  -- "+getPreference("Is_WithinGroup"));

                TempData.OutletID= _OutletID;
                TempData.OutletName=_OutletName;

                TempData.TargetCustomer = ngo;
                TempData.InstituteID = ins;


                Log.e("TargetCustomer", " "+ngo);
                Log.e("InstituteID", " "+ins);


                if(!_OutletID.equals("00"))
                {
                    savePreference("OutletCode", _OutletCode);


                }
                lastmemo.setText("Last Memo Date: " + db.getLastmemodate(_OutletID));

                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });

        OutletSp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _OutletID = OutletID.get(arg2);

                Log.e("UUUUUUUUUUUUUUUUUU", "UUUUUUUUUUU  _OutletID  UUUUUUUUUUUUU " + _OutletID);
                String _OutletCode = OutletCode.get(arg2);
                String _OutletName = OutletName.get(arg2);
                String ins = InstituteID.get(arg2);
                String ngo = ProjectID.get(arg2);
                String is_withinGroup = IsWithinGroup.get(arg2);

                savePreference("OutletName", _OutletName);
                savePreference("OutletID", _OutletID);
                savePreference("BonusPartyType", BonusPartyType.get(arg2));
                savePreference("Is_WithinGroup", is_withinGroup);

                Log.e("------------", "--  is_withinGroup  -- " + is_withinGroup);
                Log.e("------------", "--  Is_WithinGroup  -- " + getPreference("Is_WithinGroup"));

                TempData.OutletID = _OutletID;
                TempData.OutletName = _OutletName;

                TempData.TargetCustomer = ngo;
                TempData.InstituteID = ins;


                Log.e("TargetCustomer", " " + ngo);
                Log.e("InstituteID", " " + ins);

                lastmemo.setText("Last Memo Date: " + db.getLastmemodate(_OutletID));

                if (!_OutletID.equals("00")) {
                    savePreference("OutletCode", _OutletCode);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        itemName = "Sales Order";
        btnNext.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {
                Runtime.getRuntime().freeMemory();//
               // Runtime.getRuntime().gc();

                System.gc();

                btnNext.setEnabled(false);
                 clearPrefarance();
                // TODO Auto-generated method stub
                savePreference(OUTLET_ID, _OutletID);
                Log.e("itemName",itemName);
                TempData.SaleScreen = "OutletAccount";
                if (CheckOutletValidation()) {
                    TempData.MarketID = _MarketID;
                    Cursor c5 = db.rawQueryCoustom("SELECT * FROM outlets where outlet_id = " + "'" + _OutletID + "'");
                    if (c5 != null) {
                        if (c5.moveToFirst()) {
                            do {
                                _Is_Pharma_Type = c5.getString(c5.getColumnIndex("pharma_type"));
                                savePreference("PharmaType", _Is_Pharma_Type);
                            } while (c5.moveToNext());
                        }
                    }


                    if (itemName.equals("Sales Order")) {



                        


                        if (checkMemo()){

                            if (isInternetOn()){
                                DBRetriveStatus = 101;
                                db.generatePushJson();

                            }else {
                                //hold for local uat
                               // switchToSales();
                                Toast.makeText(Create_New_Memo.this,"Please turn On Internet TO Sync",Toast.LENGTH_LONG).show();
                            }

                        }else {
                            switchToSales();
                        }



                    }
                    if (itemName.equalsIgnoreCase("View Last Memo")) {
                        Intent idn = new Intent(Create_New_Memo.this, ViewLastRecord.class);
                        startActivity(idn);
                        finish();
                    }
                    if (itemName.equalsIgnoreCase("GPS Update")) {
                        Intent idn = new Intent(Create_New_Memo.this, GPS_UPdate.class);
                        startActivity(idn);
                        finish();
                    }
                    else if (itemName.equalsIgnoreCase("Outlet Visit")) {
                        visitAOutlet();
                    } else if (itemName.equalsIgnoreCase("offer type")) {
                        if (!getPreference("offer_type_id").equalsIgnoreCase("0")) {
                            if (isInternetOn())
                            new getToken().execute();
                            else {
                                Toast.makeText(Create_New_Memo.this, "Please Turn On Internet", Toast.LENGTH_LONG).show();

                                btnNext.setEnabled(true);
                            }
                            }else {
                            Toast.makeText(Create_New_Memo.this,"Please select a Offer Type",Toast.LENGTH_LONG).show();
                            btnNext.setEnabled(true);
                        }
                    }
                  /*  if(itemName.equalsIgnoreCase("Collected List"))
                    {
                        Intent idn = new Intent(OutletAccountActivity.this, CollectedListActivity.class);
                        startActivity(idn);
                        finish();
                    }

                    if(itemName.equalsIgnoreCase("Outlet Return NCP to SO"))
                    {

                        Intent idn = new Intent(OutletAccountActivity.this, SO_ProductReturnActivity.class);
                        TempData.RerurnProductDetails.clear();
                        TempData.SelectedProductList.clear();
                        idn.putExtra("From", "MEMO");
                        TempData.OutletID=getPreference("OutletID");
                        startActivity(idn);
                        finish();

                    }*/

/*				Intent idn = new Intent(OutletAccountActivity.this, SalesOrderActivity.class);
				idn.putExtra("OutletID", _OutletID);
				startActivity(idn);
				finish();*/

                }

            }

        });

        if (getIntent().getIntExtra("flag1",0)==1){
            savePreference("OutletID", "0") ;
            outltateauto.setText("");
        }
    }


    public boolean checkMemo() {

        Cursor count = db.rawQueryCoustom("Select count(_id) from "+ Tables.TABLE_NAME_ORDER +" where "+Tables.ORDER_is_pushed+"='0'");

        count.moveToFirst();

        long failed_MEMO_PUSH = Long.parseLong(getPreference(PUSH_Failed_TIME));
        Log.e("failed_memo_push_time",(failed_MEMO_PUSH-System.currentTimeMillis())+"");
        if (failed_MEMO_PUSH>0){
            if (failed_MEMO_PUSH>System.currentTimeMillis()){
                return false;
            }

        }


        int push_count = Integer.parseInt(getPreference(MIN_ORDER_NUMBER));
        if (push_count==0){
            return false;
        }
        if (count.getInt(0)>=push_count){
            return true;
        }




        return false;
    }

    private void switchToSales() {
        if(getPreference(SR_ID).equalsIgnoreCase("0")){
            Toast.makeText(Create_New_Memo.this,"First Make Online Login And then Try again",Toast.LENGTH_LONG).show();
            return;
        }
        BPSelected_bonus.clear();
        BPSelected_product.clear();
        BPSelected_set.clear();
        BPSelected_policy_type.clear();
        BPSelected_option_id.clear();
        policyArrayList.clear();
        PolicySetRelation.clear();

        TempData.isPushed = "0";
        savePreference("Thana", routeSp.getSelectedItem().toString());
        TempData.tempThana = routeSp.getSelectedItem().toString();
        savePreference("Thana", routeSp.getSelectedItem().toString());
        TempData.tempThana = routeSp.getSelectedItem().toString();
        if (autoPos<BonusPartyType.size())
            savePreference("BonusPartyType", BonusPartyType.get(autoPos));
        TempData.editMemo = "false";
        DBRetriveStatus = 100;
        db.prepareDataForOrder(_OutletID);
        ORDER_TO_MEMO = 0;
        MEMO_EDIT = false;
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED || connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

    }

    @Override
    public void OnConnetivityError() {

    }

    class getToken extends AsyncTask<String, String, String> {
        String RESPONSE;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Create_New_Memo.this);
            progressDialog.setTitle("Getting Token.......");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... args) {

            RESPONSE = "";

            try {
                String url = "http://182.160.103.234:8079/api_data_webtoapp_retrives/get_so_token.json";

                String requestJson = generateTokenJson();
                Log.e("InstrumentNo Json", "" + requestJson);
                RESPONSE = JsonParse.makeServiceCall(url, requestJson);

            } catch (Exception e) {
            }



            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        progressDialog.dismiss();
                        Log.e("token: ", "token: " + RESPONSE);
                        btnNext.setEnabled(true);
                        JSONObject jsonObject = new JSONObject(RESPONSE);
                        if (RESPONSE != null) {

                            Intent intent = new Intent(Create_New_Memo.this,WebView.class);
                            intent.putExtra("url",jsonObject.getString("form_url"));
                            startActivity(intent);

//								savePreference("CreditCollectionCheck", "1");
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                        }

                    } catch (Exception e) {
                    }
                }
            });



        }

    }

    private String generateTokenJson() {



        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("office_id",  getPreference("Office_Id"));
            jsonObject.put("mac",  getPreference("mac"));
            jsonObject.put("so_id",getPreference(SR_ID));
            jsonObject.put("offer_type_id",getPreference("offer_type_id"));
            jsonObject.put("outlet_id",TempData.OutletID);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    private void visitAOutlet() {

        GPSTracker gps = new GPSTracker();
        Location location = null;
        String lat = "";
        String lang = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {


            location = gps.getLocationonetime(this);
            if (location!=null){
                lat = location.getLatitude()+"";
                lang = location.getLongitude()+"";
            }
            HashMap<String,String> visit =  new HashMap<String,String>();
            visit.put("latitude",lat);
            visit.put("outlet_id",_OutletID);
            visit.put("longitude",lang);
            visit.put("visit_date",getCurrentDateTime());
            visit.put("created_at",getCurrentDate());
            visit.put("isPushed","0");
            if (!alreadvisited(getCurrentDate(),_OutletID)) {
                db.InsertTable(visit, "outlet_visit");
                Toast.makeText(this,"Successfully visited",Toast.LENGTH_LONG).show();
            }
            else
            Toast.makeText(this,"Outlet Already visited",Toast.LENGTH_LONG).show();

        }

        btnNext.setEnabled(true);


    }

    private boolean alreadvisited(String currentDate, String outletID) {

       Cursor c = db.sqLiteDatabase.rawQuery("select * from outlet_visit where outlet_id='"+outletID+"' and created_at='"+currentDate+"'",null);

       if (c.getCount()>0){
           return true;
       }else {
           return false;
       }
    }


    private void settallfalse(CheckBox[] checkBoxes, int i) {

        for (int j = 0; j < checkBoxes.length; j++) {

            if (j != i) {

                checkBoxes[j].setChecked(false);
            }
        }


    }

    void clearPrefarance(){


        Cursor c = db.rawQueryCoustom("select product_id from product");
        c.moveToFirst();
        if (c!=null && c.getCount()>0){

            do {
                savePreference("bonus"+c.getString(0),"0.0");
            }while (c.moveToNext());


        }



    }

    private void setLastMemoDate(String outletID) {

       /* Cursor memodate = db.rawQuery("SELECT memo_date FROM memos WHERE outlet_id ='"+outletID+"' ORDER by _id DESC LIMIT 1");
        memodate.moveToFirst();
        if (memodate!=null & memodate.getCount()>0){
            lastoutlatememodate.setText(memodate.getString(0));

        }else {
            lastoutlatememodate.setText("NO Memo in Last 40 days");
        }*/
    }

    public int getPosofOutlate(String id) {

        for (int i = 0; i < OutletID.size(); i++) {
            if (OutletID.get(i).equalsIgnoreCase(id)) {
                Log.e("match", OutletID.get(i) + " " + id + " " + i + " " + OutletID.size());
                return i;
            }
        }

        return -1;
    }

    public boolean CheckOutletValidation() {
    Log.e("getPreference",getPreference("OutletID"));
        if (OutletName.size() <= 0 || TextUtils.isEmpty(outltateauto.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "Select outlet first!", Toast.LENGTH_LONG).show();
            btnNext.setEnabled(true);
            return false;
        }

        return true;

    }

    private void ThanaParse() {
        Route_id.clear();
        Route_name.clear();
        Cursor c = db.rawQueryCoustom("SELECT * FROM route ORDER BY route_name ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Thana_Id = c.getString(c.getColumnIndex("route_id"));
                    String Thana_Name = c.getString(c.getColumnIndex("route_name"));

                    Route_id.add(Thana_Id);
                    Route_name.add(Thana_Name);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Create_New_Memo.this, R.layout.spinner_text, Route_name);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            routeSp.setAdapter(dataAdapter);

            if (!getPreference("thanaID").equalsIgnoreCase("NO PREFERENCE") && !getPreference("thanaID").equalsIgnoreCase("")) {
                int SelectedPos = Route_id.indexOf(getPreference("thanaID"));
                Log.e("thanaID POS:", ".........." + SelectedPos);
                routeSp.setSelection(SelectedPos);
            }


        }

    }

    private void MarketParse(String Thana_ID) {
        MarketID.clear();
        MarketName.clear();
        Cursor c = db.rawQueryCoustom("SELECT * FROM markets where route_id=" + "'" + Thana_ID + "' and is_active='1' ORDER BY market_name COLLATE NOCASE ASC");

        Log.e("querymarket", "SELECT * FROM markets where route_id=" + "'" + Thana_ID + "' and is_active!='1' ORDER BY market_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Market_Id = c.getString(c.getColumnIndex("market_id"));
                    String Market_Name = c.getString(c.getColumnIndex("market_name"));

                    MarketID.add(Market_Id);
                    MarketName.add(Market_Name);


                } while (c.moveToNext());
            }

            marketAdapter= new ArrayAdapter<String>(Create_New_Memo.this, R.layout.spinner_text, MarketName);
            marketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
           // MarketSp.setAdapter(dataAdapter);
            marketauto.setAdapter(marketAdapter);
          //  marketauto.setThreshold(1);
            if (MarketID.size()>0) {
                Log.e("MarketID", getPreference("MarketID"));
                if (!getPreference("MarketID").equalsIgnoreCase("NO PREFERENCE") && !getPreference("MarketID").equalsIgnoreCase("")) {
                    int SelectedPos = MarketID.indexOf(getPreference("MarketID").trim());

                    if (SelectedPos >= 0) {


                        TempData.MarketID = MarketID.get(SelectedPos);

                        marketauto.setListSelection(SelectedPos);

                        marketauto.setText(MarketName.get(SelectedPos));
                        _MarketID = MarketID.get(SelectedPos);
                        Log.e("OUTlET POS:", ".........." + SelectedPos + " " + getPreference("MarketID") + " " + MarketName.size()+" "+marketauto.getListSelection());
                        savePreference("MarketID", _MarketID);
                    } else {

                        marketauto.setText(MarketName.get(0));
                        TempData.MarketID = MarketID.get(0);
                        _MarketID = MarketID.get(0);
                        savePreference("MarketID", _MarketID);


                    }

                } else {
                    marketauto.setText(MarketName.get(0));
                    TempData.MarketID = MarketID.get(0);
                    _MarketID = MarketID.get(0);
                    savePreference("MarketID", _MarketID);


                }
            }else
                marketauto.setText("");
            if (MarketID.size()>0)
            OutletAllTableParse(TempData.MarketID, getPreference("OutletCategoryID"));
            else {
                OutletAllTableParse("12584654654678745", getPreference("OutletCategoryID"));
            }

        }

    }

    private void OutletCategoriesTableParse() {
        OutletCategoryID.clear();
        OutletCategoryName.clear();
        OutletCategoryID.add("0");
        OutletCategoryName.add("All");

        Cursor c = db.rawQueryCoustom("SELECT * FROM outlet_categories ORDER BY outlet_category_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String outlet_category_id = c.getString(c.getColumnIndex("outlet_category_id"));
                    String outlet_category_name = c.getString(c.getColumnIndex("outlet_category_name"));

                    OutletCategoryID.add(outlet_category_id);
                    OutletCategoryName.add(outlet_category_name);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Create_New_Memo.this, R.layout.spinner_text, OutletCategoryName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            OutletCategorySp.setAdapter(dataAdapter);

            if (!getPreference("OutletCategoryID").equalsIgnoreCase("NO PREFERENCE") && !getPreference("OutletCategoryID").equalsIgnoreCase("")) {
                int SelectedPos = OutletCategoryID.indexOf(getPreference("OutletCategoryID"));
                Log.e("Category POS:", ".........." + SelectedPos);
                OutletCategorySp.setSelection(SelectedPos);
            }

        }

    }

    private void OfferType() {
        offer_id_list.clear();
        offer_name_list.clear();
        offer_id_list.add("0");
        offer_name_list.add("select Offer Type");

        Cursor c = db.rawQueryCoustom("SELECT * FROM offer_type where status=1 and start_date>="+getCurrentDate()+" and "+getCurrentDate()+"<= end_date  ORDER BY name ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String offer_id = c.getString(c.getColumnIndex("offer_id"));
                    String offer_name = c.getString(c.getColumnIndex("name"));

                    offer_id_list.add(offer_id);
                    offer_name_list.add(offer_name);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Create_New_Memo.this, R.layout.spinner_text, offer_name_list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            offer_type_sp.setAdapter(dataAdapter);



        }

    }
    private void OutletAllTableParse(String marketID, String typeID) {

        OutletID.clear();
        OutletCode.clear();
        OutletName.clear();
        ProjectID.clear();
        InstituteID.clear();
        IsWithinGroup.clear();
        BonusPartyType.clear();
        outlet_list.clear();

        Cursor c;
        String Query;
        if (typeID.equalsIgnoreCase("0")) {
            Query = "SELECT * FROM outlets  WHERE market_id='" + marketID + "' and isActivated='1' GROUP BY outlet_name ORDER BY outlet_name COLLATE NOCASE ASC";
            c = db.rawQueryCoustom(Query);
        } else {
            Query = "SELECT * FROM outlets  WHERE market_id='" + marketID + "' AND outlet_category_id='" + typeID + "' and isActivated='1' GROUP BY outlet_name ORDER BY outlet_name COLLATE NOCASE ASC";
            c = db.rawQueryCoustom(Query);
        }
        c.moveToFirst();
        Log.e("courser count", Query + " " + c.getCount() + "");
        Log.d("sql", Query);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String outlet_id = c.getString(c.getColumnIndex("outlet_id"));
                    String outlet_code = c.getString(c.getColumnIndex("outlet_code"));
                    String outlet_name = c.getString(c.getColumnIndex("outlet_name"));
                    String institute_id = c.getString(c.getColumnIndex("ngo_institute_id"));
                    String project_id = c.getString(c.getColumnIndex("isNgo"));
                    String bonusPartyType = c.getString(c.getColumnIndex("bonus_party_type"));
                    String is_within_group = c.getString(c.getColumnIndex("is_within_group"));

                    BonusPartyType.add(bonusPartyType);
                    OutletID.add(outlet_id);
                    Log.e("outlateID", outlet_id+"");
                   // OutletID.add(outlet_id);
                    OutletCode.add(outlet_code);
                    OutletName.add(outlet_name);
                    ProjectID.add(project_id);
                    InstituteID.add(institute_id);
                    IsWithinGroup.add(is_within_group);
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("outlet_name",outlet_name);
                    map.put("Outlet_id",outlet_id);
                    outlet_list.add(map);

                } while (c.moveToNext());
            }

         /*   for (int i = 0; i < OutletName.size(); i++) {
                Log.e("OUTLET_NAME:", OutletName.get(i));
                Log.e("OUTLET_ID:", OutletID.get(i));

            }*/


           // dataAdapter = new ArrayAdapter<String>(Create_New_Memo.this, R.layout.outlet_dw, OutletName);
            outlet_adapter = new AutoCompeleteTextAdapter(Create_New_Memo.this,R.layout.outlet_dw,R.id.outlet_name,outlet_list);
            outlet_adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            //OutletSp.setAdapter(dataAdapter);
            outltateauto.setAdapter(outlet_adapter);
            outltateauto.setThreshold(1);
            Log.e("constraint",outlet_list.size()+"");
            if (!getPreference("OutletID").equalsIgnoreCase("NO PREFERENCE") && !getPreference("OutletID").equalsIgnoreCase("")) {
                int SelectedPos = OutletID.indexOf(getPreference("OutletID").trim());
                if (SelectedPos >= 0) {

                    OutletSp.setSelection(SelectedPos);
                    TempData.OutletID = OutletID.get(SelectedPos);
                    TempData.OutletName = OutletName.get(SelectedPos);
                    setLastMemoDate(TempData.OutletID);
                    outltateauto.setListSelection(SelectedPos);
                    outltateauto.setText(OutletName.get(SelectedPos));
                    _OutletID = OutletID.get(SelectedPos);
                    TempData.TargetCustomer = ProjectID.get(SelectedPos);
                    TempData.InstituteID = InstituteID.get(SelectedPos);
                    Log.e("OUTlET POS:", ".........." + SelectedPos + " " + getPreference("MarketID") + " " + MarketName.size()+" "+outltateauto.getListSelection());
                    savePreference("OutletID", _OutletID);
                } else {
                    if (OutletName.size() > 0) {
                        outltateauto.setText(OutletName.get(0));
                        TempData.OutletID = OutletID.get(0);
                        TempData.OutletName = OutletName.get(0);
                        setLastMemoDate(TempData.OutletID);
                        _OutletID = OutletID.get(0);
                        TempData.TargetCustomer = ProjectID.get(0);
                        TempData.InstituteID = InstituteID.get(0);
                        savePreference("OutletID", _OutletID);
                    }else {
                        outltateauto.setText("");
                    }

                }
            } else {
                if (OutletName.size() > 0) {
                    outltateauto.setText(OutletName.get(0));
                    TempData.OutletID = OutletID.get(0);
                    _OutletID = OutletID.get(0);
                    TempData.OutletName = OutletName.get(0);
                    setLastMemoDate(TempData.OutletID);
                    TempData.TargetCustomer = ProjectID.get(0);
                    TempData.InstituteID = InstituteID.get(0);
                    savePreference("OutletID", _OutletID);
                }else {
                    outltateauto.setText("");
                }


            }

        }

        if (getIntent().getIntExtra("flag1",0)==1){
            outltateauto.setText("");
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent idd = new Intent(Create_New_Memo.this, Dashboard.class);
            startActivity(idd);
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.back) {

            Intent idd = new Intent(Create_New_Memo.this, Dashboard.class);
            startActivity(idd);
            finish();
        } else if (v.getId() == R.id.home) {
            Intent intent = new Intent(Create_New_Memo.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void OnLocalDBdataRetrive(String json) {

        Log.e("onResponse", "onResponse: "+DBRetriveStatus);
        if ( DBRetriveStatus == 100) {
            Intent idn = new Intent(Create_New_Memo.this, Sales_Memo.class);
            idn.putExtra("OutletID", _OutletID);
            idn.putExtra("flag", 2);
            savePreference(ORDER_START_TIME, getCurrentDateTime());
            MEMO_EDIT = false;
            ORDER_TO_MEMO = 0;
            TempData.editMemo = "false";
            startActivity(idn);
        } else if (DBRetriveStatus == 101) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    orderPush(json);
                }
            });

        }

    }

    private void orderPush(String jsonObject) {
        ProgressDialog dailog = CheckConnection(Create_New_Memo.this,"Order Sync...");
        if (dailog==null)
            return;
        try {
            Log.e("onResponse", "api_call onResponse: ");
            btnNext.setEnabled(true);
            getJAPi().ORDERPUSH(convertTORequestdata(new JSONObject(jsonObject))).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                     Log.e("onResponse", "onResponse: "+response.toString() );

                    dailog.dismiss();
                    if (response.code()==200 && response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            int status = jsonObject.getJSONObject("order").getInt("status");

                            if (status == 1) {
                                DBRetriveStatus = 102;
                                db.updatePushStatus();
                                getTradeOfferPolicy();

                            }

                        } catch (JSONException e) {
                           // e.printStackTrace();
                            savePreference(PUSH_Failed_TIME,(System.currentTimeMillis()+180000)+"");
                        }catch (Exception e){
                            savePreference(PUSH_Failed_TIME,(System.currentTimeMillis()+180000)+"");
                        }


                    }else {

                        Toast.makeText(Create_New_Memo.this,   "Order Not push try Again or Check Order Process from More", Toast.LENGTH_SHORT).show();
                    }

                    //db.getlastupdateddate();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    savePreference(PUSH_Failed_TIME,(System.currentTimeMillis()+180000)+"");
                    dailog.dismiss();
                    Toast.makeText(Create_New_Memo.this,   "Order Not push try Again or Check Order Process from More", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void getTradeOfferPolicy() {
        JSONObject dataObject = new JSONObject();
        ProgressDialog dailog = CheckConnection(Create_New_Memo.this,"Trade Offer Policy Loading...");
        if (dailog==null)
            return;
        try {
            dataObject.put("so_id","");
            Log.e("onResponse", "api_call onResponse: ");
            btnNext.setEnabled(true);
            getJAPi().Policy_Bonus_Applicable(convertTORequestdata(dataObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Log.e("onResponse", "onResponse: "+response.toString() );

                    dailog.dismiss();
                    if (response.code()==200 && response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            Log.e("policyTrade",jsonObject.toString());
                            JSONArray array=jsonObject.getJSONArray("policy_data");
                            for (int i =0 ; i<array.length();i++){
                                JSONObject policyOBject=array.getJSONObject(i);
                                String policyId=policyOBject.getString("policy_id");
                                String bonusApplicable=policyOBject.getString("policy_applicable");
                                Log.e("policyId",policyId);
                                Log.e("bonusApplicable",bonusApplicable);
                                String queryUpdateTable = "UPDATE " + "Policy_Table" + " SET " + "bonus_applicable" + "='" + bonusApplicable + "'  WHERE " + "policy_id" + "='" + policyId + "'";
                                db.excQuery(queryUpdateTable);
                            }


                        } catch (JSONException e) {
                            // e.printStackTrace();

                        }catch (Exception e){

                        }


                    }else {

                        Toast.makeText(Create_New_Memo.this,   "Failed try Again", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dailog.dismiss();
                    Toast.makeText(Create_New_Memo.this,   "Try again", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList) {

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }
}
