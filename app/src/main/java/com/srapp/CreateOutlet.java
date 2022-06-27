package com.srapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.StaticFlags;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.TempData.OldBPSelected_policy_type;

public class CreateOutlet extends AppCompatActivity implements BasicFunctionListener {
    Spinner outletCategoySpinner, marketSpinner, outletSpinner;
    TextView title;
    ImageView home;
    ImageView back;
    CheckBox is_active;
    Data_Source ds;
    HashMap<String, ArrayList<String>> outletCategories, markets;
    HashMap<String,String> dataForOutletInsert;
    RadioGroup pharmaType;
    EditText outletName_et, incharge_name_et, owner_name_et, mobile_num_et, address_et;
    String marketName, outletCategory, outletName, inchargeName,ownerName, mobileNum, address,pharma,status,lattitude,longitude;
    Switch status_switch;
    Button saveBtn;
    String salePersonId, territoryId ,thanaId,outlet_addrerss;
    int page_from;
    String marketId;
    ArrayList<String>thanaTerritory;
    CheckBox isNgo;
    private BroadcastReceiver broadcastReceiver;
    private String ngo="0";
    private String routeId;
    LocationManager locationManager;
    ImageView captureButton;
    private static final int CAMERA_REQUEST = 1888;
    ImageView outletImage1,outletImage2,outletImage3;
    ImageView[] outletImgViewList;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static  final int REQUEST_LOCATION=1;
    String encoded1 =null,encoded2 = null,encoded3=null;

    BasicFunction basicFunction;
    ArrayList<String> idList, valueList;
    String mktIdResponse, outletCategoryIdResponse,outletNameResponse,contNameRespo,mobileRespo,img1Respo,img2Respo,img3Respo, pharmaTypeRespo, mobileRespone;
    ArrayList<String> imgRespoList;
    //.........intent rcv.......//
    HashMap<String,String>outlet;
    private String outlet_id;
    int marketSelection, marketcategorySelection;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outlet);

        basicFunction = new BasicFunction(this,this);
        idList = new ArrayList<>();
        valueList = new ArrayList<>();
        imgRespoList = new ArrayList<>();
        TextView user_txt_view;
        user_txt_view = findViewById(R.id.user_txt_view);
        user_txt_view.setText(basicFunction.getPreference(SR_ID));
        territoryId = basicFunction.getPreference("territory_id");
        salePersonId = basicFunction.getPreference("sales_person_id");


        //...........location permission check ............//
        if (ActivityCompat.checkSelfPermission(CreateOutlet.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateOutlet.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        //..............permission check end...................//
        ds = new Data_Source(this);

        Intent intent =getIntent();

        outlet_id = intent.getStringExtra("outlet_id_byClick");
        page_from = intent.getIntExtra("page_from",-2);
        Log.e("Intent receive", "onCreate: "+outlet_id );
        Log.e("page_from",page_from+"");

        home = findViewById(R.id.home);
        is_active = findViewById(R.id.is_active);
        back = findViewById(R.id.back);
        saveBtn = findViewById(R.id.save_btn);
        title = findViewById(R.id.page_title);
        address_et = findViewById(R.id.address);
        if (page_from==-2||page_from==-25){
            is_active.setChecked(true);
            Log.e("page_from",page_from+"");
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( CreateOutlet.this, Dashboard.class));
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page_from==-25){
                    Intent intent = new Intent(CreateOutlet.this,Create_New_Memo.class);
                    startActivity(intent);
                    finish();

                }else {

                    StaticFlags.OUTLET_ID_FOR_SCROLL = outlet_id;
                    Intent intent = new Intent(CreateOutlet.this, OutletList.class);
                    intent.putExtra("resume", 1);
                    startActivity(intent);
                    finish();
                }
            }
        });

        outletName_et = findViewById(R.id.outlet_name_et);
        //incharge_name_et = findViewById(R.id.incharge_name_et);
        owner_name_et = findViewById(R.id.owner_name_et);
        mobile_num_et = findViewById(R.id.mobilee_et);
        //address_et = findViewById(R.id.address_et);

        //status_switch = findViewById(R.id.outlet_status);
        pharmaType = findViewById(R.id.pharma_type_radio_group);


        marketSpinner = findViewById(R.id.market_spinner);
        outletCategoySpinner = findViewById(R.id.outlet_category_spinner);

        captureButton = findViewById(R.id.capture);
        outletImage1 = findViewById(R.id.img1);
        outletImage2 = findViewById(R.id.img2);
        outletImage3 = findViewById(R.id.img3);

        outletImgViewList = new ImageView[]{outletImage1, outletImage2, outletImage3};


        dataForOutletInsert = new HashMap<>();
        thanaTerritory = new ArrayList<>();



        markets = new HashMap<>();
        markets = ds.getMarketData("no","no");

        Log.e("size test", "onCreate: "+markets.size());


        if (markets != null && markets.size()>0) {

            final SpinnerAdapter marketDataAdapter = new SpinnerAdapter(this, R.layout
                    .spinner_item, markets.get(Tables.MARKETS_market_name));
            marketSpinner.setAdapter(marketDataAdapter);

            if (page_from==-25)
                marketSpinner.setSelection( getPosition(markets.get(Tables.MARKET_ID),intent.getStringExtra("MarketID")));
        }

        outletCategories = new HashMap<>();
        outletCategories = ds.getOutletCategories();
        Log.e("outlet cat hash", "onCreate: "+outletCategories );

        if (outletCategories !=null && outletCategories.size()>0) {

            SpinnerAdapter outletDataAdapter = new SpinnerAdapter(this, R.layout
                    .spinner_item, outletCategories.get(Tables.OUTLET_CATEGORY_NAME));
            outletCategoySpinner.setAdapter(outletDataAdapter);
            if (page_from==-25)
                outletCategoySpinner.setSelection( getPosition(outletCategories.get(Tables.OUTLET_CATEGORY_CATEGORY_ID),intent.getStringExtra("OutletCategoryID")));

        }

        marketSpinner.requestFocus();

        outletCategoySpinner.requestFocus();

        marketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }

                marketId = markets.get(Tables.MARKETS_market_id).get(position);
                if (!marketId.equals("0")) {
                    thanaTerritory = ds.getIdsFromMarketToOutlet(marketId);
                    thanaId = thanaTerritory.get(0);
                    territoryId = thanaTerritory.get(1);
                    routeId = thanaTerritory.get(2);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        outletCategoySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.background_card));
                textView.setPadding(0,0,0,0);

                outletCategory = outletCategories.get(Tables.OUTLET_CATEGORY_CATEGORY_ID).get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        /*status_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status = "1";

                } else {
                    status = "0";

                }
            }
        });*/
        /*if (!runtime_permission()){
            enableButtons();
            Intent i = new Intent(getApplicationContext(), GPS_Service.class);
            startService(i);
        }*/

        /*isNgo = findViewById(R.id.is_ngo_cb);
        isNgo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ngo = "1";
                }else {
                    ngo = "0";
                }
            }
        });*/

        //..............Image Capture...............//

        captureButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });






        //......for edit...............//
        if (outlet_id != null){

            is_active.setVisibility(View.VISIBLE);

            Log.e("out intent", "<---------onCreate:--------> "+outlet_id );

            JSONObject primaryData = new JSONObject();
            try {
                primaryData.put("mac",basicFunction.getPreference("mac"));
                primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                primaryData.put("outlet_id",outlet_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            basicFunction.getResponceData(URL.OutletDetails, String.valueOf(primaryData),601);


        }
        //................................//

        territoryId = basicFunction.getPreference("territory_id");
        salePersonId = basicFunction.getPreference("sales_person_id");

        saveBtn.setOnClickListener(v -> {

            Log.e(" base 64", "onClick: "+encoded1 );
            if (pharmaType.getCheckedRadioButtonId() != -1) {

                pharma = ((RadioButton) findViewById(pharmaType.getCheckedRadioButtonId())).getText().toString();
                if (pharma.equals("Non Pharma")){
                    pharma = "0";
                }else {
                    pharma = "1";
                }
            }

            outletName = outletName_et.getText().toString().trim();
            if (page_from!=-2) {
                if (ds.getDuplicateOutlet(outletName, marketId)) {
                    Toast.makeText(CreateOutlet.this, "This Outlet is  already in this market", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //inchargeName = incharge_name_et.getText().toString().trim();
            ownerName = owner_name_et.getText().toString().trim();
            mobileNum = mobile_num_et.getText().toString().trim();
            //address = address_et.getText().toString().trim();

            //.........location new.....//
            locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

            //Check gps is enable or not

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                //Write Function To enable gps
                OnGPS();
            }
            else
            {
                //GPS is already On then
                getLocation();
            }

            //new location end.........//



            //dataForOutletInsert.put(Tables.OUTLETS_ID,salePersonId+System.currentTimeMillis());
            //dataForOutletInsert.put(Tables.SR_ID,salePersonId);
            // dataForOutletInsert.put(Tables.TERRITORY_T_id,territoryId);
            dataForOutletInsert.put(Tables.OUTLETS_THANA_ID,thanaId);
            dataForOutletInsert.put(Tables.OUTLETS_MARKET_ID,marketId);
            dataForOutletInsert.put(Tables.OUTLETS_CATAGORY_ID,outletCategory);
            dataForOutletInsert.put(Tables.OUTLETS_OUTLET_NAME,outletName);
            //dataForOutletInsert.put(Tables.OUTLETS_INCHARGE_NAME,inchargeName);
            dataForOutletInsert.put(Tables.OUTLETS_OWNER_NAME,ownerName);
            dataForOutletInsert.put(Tables.OUTLETS_ADDRESS,address_et.getText().toString().trim());
            if (is_active.isChecked()){
                dataForOutletInsert.put(Tables.OUTLETS_ISACTIVATED,"1");
            }else {
                dataForOutletInsert.put(Tables.OUTLETS_ISACTIVATED,"0");
            }

           // dataForOutletInsert.put(Tables.OUTLETS_ISACTIVATED,ownerName);
            dataForOutletInsert.put(Tables.OUTLETS_MOBILE,mobileNum);
            //dataForOutletInsert.put(Tables.OUTLETS_ADDRESS,address);
            dataForOutletInsert.put(Tables.OUTLETS_PHARMA_TYPE,pharma);
            dataForOutletInsert.put(Tables.OUTLETS_LATITUDE,lattitude);
            dataForOutletInsert.put(Tables.OUTLETS_LONGITUTE,longitude);
            dataForOutletInsert.put(Tables.OUTLETS_ISPUSHED,"0");
            // is activated by default
            //dataForOutletInsert.put(Tables.OUTLETS_ISACTIVATED,"1");
            //dataForOutletInsert.put(Tables.OUTLETS_ISNGO,ngo);
            dataForOutletInsert.put(Tables.ROUTE_ID,routeId);
            // dataForOutletInsert.put(Tables.OUTLETS_IS_WITHIN_GROUP,status);

            /*if (encoded1 != null){
                dataForOutletInsert.put(Tables.OUTLETS_IMAGE_1,"encode1");
            }
            if (encoded2 != null){
                dataForOutletInsert.put(Tables.OUTLETS_IMAGE_2,"encode2");
            }
            if (encoded3 != null){
                dataForOutletInsert.put(Tables.OUTLETS_IMAGE_3,"encoded3");
            }*/

            Log.e("tesingggggg", "onClick: mkt "+marketId+" cat "+outletCategory+" out nm "+outletName+" con nam "+ownerName+" phone "+mobileNum+" pharma "+pharma+" " );




            if (marketId != null && outletCategory != null &&  !TextUtils.isEmpty(outletName) && !TextUtils.isEmpty(ownerName)
                    && !TextUtils.isEmpty(mobileNum) ) {
                // if for edit
                if (outlet_id != null){
                    Log.e(" intent outlet_id", "onClick: "+outlet_id );
                    saveBtn.setEnabled(false);
                    // dataForOutletInsert.remove(Tables.OUTLETS_ID);

                    //.........push to server.......//
                    //Date currentTime = Calendar.getInstance().getTime();
                    DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
                    Date dateobj = new Date();
                    String currentTime = df.format(dateobj);

                    JSONObject outlet = new JSONObject();
                    JSONObject primaryData = new JSONObject();
                    JSONArray image = new JSONArray();
                    JSONObject imgWithStatus1 = new JSONObject();
                    JSONObject imgWithStatus2 = new JSONObject();
                    JSONObject imgWithStatus3 = new JSONObject();


                    JSONArray outletList = new JSONArray();
                    try {

                        for (String i : dataForOutletInsert.keySet()) {

                            outlet.put(i, dataForOutletInsert.get(i));

                        }


                        if (encoded1 != null && outletImage1.getVisibility() == View.VISIBLE){
                            imgWithStatus1.put("image_url", encoded1);
                            imgWithStatus1.put("status", 1);
                        }else if ( encoded1 == null && outletImage1.getVisibility() ==  View.VISIBLE){
                            imgWithStatus1.put("image_url", imgRespoList.get(0));
                            imgWithStatus1.put("status", "0");
                        }

                        if (encoded2 != null && outletImage2.getVisibility()== View.VISIBLE){
                            imgWithStatus2.put("image_url", encoded2);
                            imgWithStatus2.put("status", 1);
                        }else if(encoded2 == null &&outletImage2.getVisibility()== View.VISIBLE) {
                            imgWithStatus2.put("image_url", imgRespoList.get(1));
                            imgWithStatus2.put("status", "0");
                        }

                        if (encoded3 != null && outletImage3.getVisibility()== View.VISIBLE){
                            imgWithStatus3.put("image_url", encoded3);
                            imgWithStatus3.put("status", 1);
                        }else if (encoded3 == null && outletImage3.getVisibility()== View.VISIBLE){
                            imgWithStatus3.put("image_url", imgRespoList.get(2));
                            imgWithStatus3.put("status", "0");
                        }


                        if (imgWithStatus1 != null && imgWithStatus1.length()>0) {
                            image.put(imgWithStatus1);
                        }
                        if (imgWithStatus2 != null && imgWithStatus2.length()>0) {
                            image.put(imgWithStatus2);
                        }
                        if (imgWithStatus3 != null && imgWithStatus3.length()>0) {
                            image.put(imgWithStatus3);
                        }

                        outlet.put("temp_id",outlet_id);
                        outlet.put("created_at",currentTime);
                        outlet.put("images",image);

                        outletList.put(outlet);


                        primaryData.put("mac","354214080719502");
                        primaryData.put("sales_person_id",salePersonId);
                        primaryData.put("outlet_list",outletList);

                        dataForOutletInsert.put(Tables.OUTLETS_Image, String.valueOf(image));

                        ds.updateOutlet(dataForOutletInsert,outlet_id);


                       // basicFunction.getResponceData(URL.CreateOutlet,primaryData.toString(),302);

                        Log.e("json test...", "onClick: "+primaryData.toString() );
                        Log.e(";k5 rrwjrrrrrrrrrrre...", "onClick: "+primaryData.toString() );

                        Log.e("dataForOutletInsert", new Gson().toJson(dataForOutletInsert));

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //...............................//

                }else {
                    saveBtn.setEnabled(false);

                    // push to serve

                    //Date currentTime = Calendar.getInstance().getTime();
                    DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
                    Date dateobj = new Date();
                    String currentTime = df.format(dateobj);

                    JSONObject outlet = new JSONObject();
                    JSONObject primaryData = new JSONObject();
                    JSONArray image = new JSONArray();

                    JSONArray outletList = new JSONArray();
                    try {


                        for (String i : dataForOutletInsert.keySet()) {

                            outlet.put(i, dataForOutletInsert.get(i));
                        }

                        if (encoded1 != null) {
                            image.put(encoded1);
                        }
                        if (encoded2 != null) {
                            image.put(encoded2);
                        }
                        if (encoded3 != null) {
                            image.put(encoded3);
                        }


                        outlet.put("images",image);
                        outlet.put("temp_id","OT"+salePersonId+System.currentTimeMillis());
                       // outlet.put("created_at",currentTime);

                        outletList.put(outlet);


                        primaryData.put("mac","354214080719502");
                        primaryData.put("sales_person_id",salePersonId);
                        primaryData.put("outlet_list",outletList);
                        dataForOutletInsert.put(Tables.OUTLETS_Image,image.toString());
                        dataForOutletInsert.put(Tables.OUTLET_ID,"OT"+salePersonId+System.currentTimeMillis());

                        //basicFunction.getResponceData(URL.CreateOutlet,primaryData.toString(),301);

                        Log.e("json test...", "onClick: "+primaryData.toString() );
                        ds.InsertTablemap(dataForOutletInsert,Tables.TABLE_NAME_OUTLETS);
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                /*marketSpinner.setSelection(0);
                outletCategoySpinner.setSelection(0);
                outletName_et.setText("");
                //incharge_name_et.setText("");
                owner_name_et.setText("");
                mobile_num_et.setText("");*/
                //address_et.setText("");
                //status_switch.setChecked(false);
                //isNgo.setChecked(false);
                //Toast.makeText(CreateOutlet.this, "New Outlet Added " + lattitude + " " + longitude, Toast.LENGTH_LONG).show();
                if (page_from==-25){

                    basicFunction.savePreference("OutletID",dataForOutletInsert.get("temp_id"));
                }

                if (page_from==-25){
                    Intent intent1 = new Intent(CreateOutlet.this,Create_New_Memo.class);
                    startActivity(intent1);
                    finish();

                }else {

                    Intent intent1 = new Intent(CreateOutlet.this, OutletList.class);
                    intent1.putExtra("resume", 2);
                    startActivity(intent1);
                    finish();
                }

            }else {
                Toast.makeText(CreateOutlet.this,"Please fill up mandatory fields ",Toast.LENGTH_SHORT).show();

            }






        });



    }

   /* private void enableButtons() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
            }
        });
    }*/

    /*private boolean runtime_permission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }

        return false;
    }*/

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                //................. start the location service here................................

                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);

                enableButtons();

            }
            else {
                runtime_permission();
            }
        }
    }*/



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            if (page_from==-25){
                Intent intent = new Intent(CreateOutlet.this,Create_New_Memo.class);
                startActivity(intent);
                finish();

            }else {

                StaticFlags.OUTLET_ID_FOR_SCROLL = outlet_id;


                Intent intent = new Intent(CreateOutlet.this, OutletList.class);
                intent.putExtra("resume", 1);
                startActivity(intent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    private void getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(CreateOutlet.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateOutlet.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            android.location.Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            android.location.Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            android.location.Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps !=null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();
                lattitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                Log.e("Location", "getLocation: +"+"Your Location:"+"\n"+"Latitude= "+lattitude+"\n"+"Longitude= "+longitude);
                //Toast.makeText(CreateOutlet.this,"Lat : "+lattitude+" Long: "+longitude, Toast.LENGTH_LONG).show();
            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                lattitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                Log.e("Loc", "getLocation: "+"Your Location:"+"\n"+"Latitude= "+lattitude+"\n"+"Longitude= "+longitude);
               // Toast.makeText(CreateOutlet.this,"Lat : "+lattitude+" Long: "+longitude, Toast.LENGTH_LONG).show();
            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                lattitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                Log.e("Loc", "getLocation: "+"Your Location:"+"\n"+"Latitude= "+lattitude+"\n"+"Longitude= "+longitude);
               // Toast.makeText(CreateOutlet.this,"Lat : "+lattitude+" Long: "+longitude, Toast.LENGTH_LONG).show();
            }
            else
            {
               // Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }

            //Thats All Run Your App
        }

    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public int getPosition(ArrayList<String>data,String id){

        int position = data.indexOf(id);

        return  position;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("camera result", "onActivityResult: result code"+resultCode+" data "+data );
        if (data!=null) {
            if (requestCode == CAMERA_REQUEST) {
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                    //encode  to byte format
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    if (outletImage1.getVisibility() == View.INVISIBLE) {
                        outletImage1.setVisibility(View.VISIBLE);
                        outletImage1.setImageBitmap(bitmap);
                        //base 64 convert
                        encoded1 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    } else if (outletImage1.getVisibility() == View.VISIBLE && outletImage2.getVisibility() == View.INVISIBLE) {
                        outletImage2.setVisibility(View.VISIBLE);
                        outletImage2.setImageBitmap(bitmap);
                        encoded2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    } else if (outletImage2.getVisibility() == View.VISIBLE && outletImage3.getVisibility() == View.INVISIBLE) {
                        outletImage3.setVisibility(View.VISIBLE);
                        outletImage3.setImageBitmap(bitmap);
                        encoded3 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    } else if (outletImage3.getVisibility() == View.VISIBLE) {
                        Toast.makeText(CreateOutlet.this, "Maximum 3 photos are allowed", Toast.LENGTH_SHORT).show();

                    }
                    //
                }
            }
        }

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {

        Log.e("SERVER RESPO", "OnServerResponce: "+RequestCode +" jsonResp"+jsonObject );

        if (RequestCode == 301){
            // check response
            int response = 0;
            try {
                response = jsonObject.getJSONObject("outlet").getInt("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (response == 1) {
                //take outlet id
                try {
                    String outtletIdFinal = jsonObject.getJSONObject("outlet").getJSONArray("replaced_relation").getJSONObject(0).getString("new_id");


                    if (page_from==-25){

                        basicFunction.savePreference("OutletID",outtletIdFinal);
                    }
                    dataForOutletInsert.put(Tables.OUTLETS_ID,outtletIdFinal);
                    StaticFlags.OUTLET_ID_FOR_SCROLL = outtletIdFinal;

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                long id = ds.createNewOutlet(dataForOutletInsert);

                StaticFlags.OUTLET_THANA_ID = thanaId;
                StaticFlags.OUTLET_MARKET_ID = marketId;

                if (page_from==-25){
                    Intent intent = new Intent(CreateOutlet.this,Create_New_Memo.class);
                    startActivity(intent);
                    finish();

                }else {

                    Intent intent = new Intent(CreateOutlet.this, OutletList.class);
                    intent.putExtra("resume", 2);
                    startActivity(intent);
                    finish();
                }


                Log.e("Outlet create", "onClick: " + id);


            }else {
                saveBtn.setEnabled(true);
            }

            // value sales persion id and outlet id to hasmap

            // toast
        }
        else if (RequestCode == 302){

            int response = 0;
            try {
                response = jsonObject.getJSONObject("outlet").getInt("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (response == 1) {
                //take outlet id
                try {
                    String outtletIdFinal = jsonObject.getJSONObject("outlet").getJSONArray("replaced_relation").getJSONObject(0).getString("new_id");

                    dataForOutletInsert.put(Tables.OUTLETS_ID,outtletIdFinal);
                    StaticFlags.OUTLET_ID_FOR_SCROLL = outtletIdFinal;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                long id = ds.updateOutlet(dataForOutletInsert,outlet_id);
                Log.e("Outlet create", "onClick: " + id);

                StaticFlags.OUTLET_THANA_ID = thanaId;
                StaticFlags.OUTLET_MARKET_ID = marketId;

                if (page_from==25){
                    Intent intent = new Intent(CreateOutlet.this,Create_New_Memo.class);
                    startActivity(intent);
                    finish();

                }else {

                    Intent intent = new Intent(CreateOutlet.this, OutletList.class);
                    intent.putExtra("resume", 2);
                    startActivity(intent);
                    finish();
                }

            }else {
                saveBtn.setEnabled(true);
            }

        }

        else if (RequestCode == 601){

            try {

                mktIdResponse =jsonObject.getJSONObject("outlets").getJSONObject("0").getString("market_id");
                outletCategoryIdResponse = jsonObject.getJSONObject("outlets").getJSONObject("0").getString("outlet_category_id");

                outletNameResponse = jsonObject.getJSONObject("outlets").getJSONObject("0").getString("outlet_name");
                mobileRespone = jsonObject.getJSONObject("outlets").getJSONObject("0").getString("mobile");
                outlet_addrerss = jsonObject.getJSONObject("outlets").getJSONObject("0").has("address")?jsonObject.getJSONObject("outlets").getJSONObject("0").getString("address"):"";
                contNameRespo = jsonObject.getJSONObject("outlets").getJSONObject("0").getString("owner_name");
                pharmaTypeRespo = jsonObject.getJSONObject("outlets").getJSONObject("0").getString("pharma_type");
               if (jsonObject.getJSONObject("outlets").getJSONObject("0").getString("isActivated").equalsIgnoreCase("1")){
                is_active.setChecked(true);
               }else {
                   is_active.setChecked(false);
               }

                for (int k = 0; k <jsonObject.getJSONObject("outlets").getJSONArray("images").length(); k++){
                    imgRespoList.add(jsonObject.getJSONObject("outlets").getJSONArray("images").getString(k));
                }

                Log.e("image_list", "OnServerResponce--------------->>: "+imgRespoList );



                // outlet = ds.getIndivOutletDetails(outlet_id);

                Log.e(" for edit", "outlet.........>> " + outlet_id + " details " + outlet);
                marketSelection = getPosition(markets.get(Tables.MARKETS_market_id), mktIdResponse);
                Log.e("market spinnr position", "onCreate: " + marketSelection);

                marketcategorySelection = getPosition(outletCategories.get(Tables.OUTLETS_CATAGORY_ID), outletCategoryIdResponse);
                Log.e("------>", "OnServerResponce:---------> mkt id respo: "+mktIdResponse+" selection : "+marketSelection );
                if (marketSelection == -1){
                    marketSelection = 0;
                }
                marketSpinner.setSelection(marketSelection);
                outletCategoySpinner.setSelection(marketcategorySelection);

                if (pharmaTypeRespo.equals("1") ||pharmaTypeRespo.equals("Pharma")) {

                    RadioButton b = findViewById(pharmaType.getChildAt(0).getId());
                    b.setChecked(true);

                } else if (pharmaTypeRespo.equals("Non Pharma") || pharmaTypeRespo.equals("0")) {
                    RadioButton b = findViewById(pharmaType.getChildAt(1).getId());
                    b.setChecked(true);

                }

                outletName_et.setText(outletNameResponse);
                //incharge_name_et.setText(outlet.get(Tables.OUTLETS_INCHARGE_NAME));
                owner_name_et.setText(contNameRespo);
                address_et.setText(outlet_addrerss);
                mobile_num_et.setText(mobileRespone);
                //address_et.setText(outlet.get(Tables.OUTLETS_ADDRESS));

                Log.e("Image respo List", "OnServerResponce......>>: "+imgRespoList );
                if (imgRespoList.size()>0){
                    for (int k = 0; k <imgRespoList.size();k++){
                        outletImgViewList[k].setVisibility(View.VISIBLE);
                        Picasso.with(this).
                                load(imgRespoList.get(k))
                                .into(outletImgViewList[k]);

                    }
                }
                    /*for (int x = 0; x <imgRespoList.size();x++){
                        if (imgRespoList.get(x) != null && !imgRespoList.get(x).equals("")){
                            outletImgViewList[x].setVisibility(View.VISIBLE);
                            Picasso.with(this).
                                    load(imgRespoList.get(x))
                                    .into(outletImgViewList[x]);

                        }
                    }
*/
                Log.e("encode testt...", "OnServerResponce:..........> "+encoded1 );



            /*if (outlet.get(Tables.OUTLETS_ISACTIVATED) != null &&outlet.get(Tables.OUTLETS_ISACTIVATED).equals("1")){
                status_switch.setChecked(true);
            }else {
                status_switch.setChecked(false);
            }*/

            /*if (outlet.get(Tables.OUTLETS_ISNGO)!= null &&outlet.get(Tables.OUTLETS_ISNGO).equals("1")){
                isNgo.setChecked(true);
            }else {
                isNgo.setChecked(false);
            }*/

            }catch (JSONException e) {
                e.printStackTrace();
            }

            saveBtn.setText("Update");
            title.setText("Update Outlet");

        }

    }

    @Override
    public void OnConnetivityError() {

        Toast.makeText(CreateOutlet.this,"No Internet Connection",Toast.LENGTH_SHORT).show();

    }

    public void onImageClick(View view) {
        ImageView imageView = findViewById(view.getId());
        if (imageView!=null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            showPopUp(view.getId(), bitmap);
        }

    }
    public void showPopUp(int imageViewId,Bitmap imageResource){
        final Dialog visitDialogue = new Dialog(CreateOutlet.this);
        visitDialogue.setContentView(R.layout.image_show_pop_up);
        //visitDialogue.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        visitDialogue.show();

        ImageView imageView = visitDialogue.findViewById(R.id.show_image);
        imageView.setImageBitmap(imageResource);
        final ImageView outletImage = findViewById(imageViewId);


        Button yesBtn = visitDialogue.findViewById(R.id.yes);
        Button noBtn = visitDialogue.findViewById(R.id.no);
        Button cancelBtn = visitDialogue.findViewById(R.id.cancel_btn);
        Button dltBtn = visitDialogue.findViewById(R.id.delete_btn);
        final LinearLayout yesNoLayout = visitDialogue.findViewById(R.id.yes_no);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outletImage.setImageBitmap(null);
                outletImage.setVisibility(View.INVISIBLE);
                visitDialogue.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoLayout.setVisibility(View.INVISIBLE);
            }
        });

        dltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoLayout.setVisibility(View.VISIBLE);

            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visitDialogue.dismiss();
            }
        });
    }

}

