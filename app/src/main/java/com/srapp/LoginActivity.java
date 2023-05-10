package com.srapp;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bxl.config.editor.BXLConfigLoader;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Difine;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.JAPIClient;
import com.srapp.Util.Parent;
import com.srapp.apiService.ApiClient;
import com.srapp.apiService.ApiInterface;
import com.srapp.apiService.ApiInterfaceForJava;
import com.srapp.kotlin.DataViewModel;
import com.srapp.kotlin.DataViewModelFactory;
import com.srapp.thermalprint.async.usbdevice.UsbDataBinder;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import NewPrint.BixolonPrinter;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Parent implements BasicFunctionListener, DBListener {

   private BasicFunction basicFunction;

    TextView textDummyHintUsername;
    TextView textDummyHintPassword;
    EditText editUsername;
    EditText editPassword;
    ImageView login_button;
    int RcCount = 0;
    DBListener dbListener = this;
    Data_Source ds;
    ArrayList<String> data, bbData;
    private int portType = BXLConfigLoader.DEVICE_BUS_USB;
    private String logicalName = "SRP-E302";
    private String address = "";
    static BixolonPrinter printer;
    TextInputLayout usertextinput, passwordtextinput;
    ProgressDialog progressDialog;

    DataViewModel dataViewModel;
    ApiInterfaceForJava api;
    public LoginActivity() {
        dataViewModel = null;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("DeviceName", Build.BRAND );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("DeviceVersion", Build.VERSION.BASE_OS);
        }

     /*   //-----------------------------------------
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbConnection();*/
        api = JAPIClient.getClient().create(ApiInterfaceForJava.class);
        basicFunction = new BasicFunction(this, this);
        ds = new Data_Source(this, this, this);

       // printer = new BixolonPrinter(this);

        textDummyHintUsername = (TextView) findViewById(R.id.text_dummy_hint_username);
        textDummyHintPassword = (TextView) findViewById(R.id.text_dummy_hint_password);

       // editUsername.setText("error");
        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);

        TextView version = findViewById(R.id.version);
        if (URL.Domain.contains("202"))
        version.setText(URL.VERSION_txt+" Local Server");
        else {
            version.setText(URL.VERSION_txt);
        }

        usertextinput = (TextInputLayout) findViewById(R.id.userTextinputLayout);
        passwordtextinput = (TextInputLayout) findViewById(R.id.passwordTextinputLayout);
        if (checkForPermission()) {
           /* TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String MAC_Number = telephonyManager.getDeviceId();
            basicFunction.savePreference("mac", MAC_Number);*/
        }

        if (!basicFunction.getPreference("sr_uname").equalsIgnoreCase("null")){

            editUsername.setText(basicFunction.getPreference("sr_uname"));
            editUsername.setEnabled(false);
        }

        if (basicFunction.getPreference("mac")==null || basicFunction.getPreference("mac").equalsIgnoreCase("null")){
            new advertizingId().execute();
        }

        dataViewModel  = new ViewModelProvider(this, new DataViewModelFactory(this.getApplication())).get(DataViewModel.class);
        // Create the observer which updates the UI.
        final Observer<String> statusObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String status) {
                Log.e("SuccessMessage", status);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
      //  dataViewModel.getSuccessStatus().observe(this, statusObserver);

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*login_button.setEnabled(false);
                startActivity(new Intent(LoginActivity.this, Dashboard.class));
                finish();*/

                if (basicFunction.getPreference("sales_person_id").equalsIgnoreCase("null") || basicFunction.isInternetOn()){
                JSONObject jsonObject = new JSONObject();
                try {
                    Log.e("permission", checkForPermission() + "");
                    if (checkForPermission()) {


                        basicFunction.savePreference("mac",  basicFunction.getPreference("mac"));
                        jsonObject.put(Difine.USERENAME, editUsername.getText().toString().trim());
                        jsonObject.put("password", editPassword.getText().toString().trim());
                        jsonObject.put("mac", basicFunction.getPreference("mac"));
                        jsonObject.put("version", URL.VERSION);

                       //  basicFunction.getResponceData(URL.Login, jsonObject.toString(), 101);
                         Log.e("map : ", jsonObject.toString());


                         ProgressDialog dailog = CheckConnection(LoginActivity.this,"Login Check...");
                         if (dailog==null)
                             return;

                        api.Login(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                RcCount = 101;
                                login_button.setEnabled(false);
                                dailog.dismiss();
                                Log.e("test",response.body());
                                try {

                                    JSONObject jsonObject=new JSONObject(response.body());
                                    if (jsonObject.getJSONArray("response").getJSONObject(0).getString("status").equalsIgnoreCase("1")) {
                                        basicFunction.savePreference(SR_ID,jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sales_person_id"));
                                        basicFunction.savePreference("sr_uname",editUsername.getText().toString().trim());
                                        FirebaseCrashlytics.getInstance().setUserId(editUsername.getText().toString().trim());
                                        basicFunction.savePreference("password",editPassword.getText().toString().trim());
                                        basicFunction.savePreference("office_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_id"));
                                        basicFunction.savePreference("territory_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("territory_id"));
                                        basicFunction.savePreference("store_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("store_id"));
                                        basicFunction.savePreference(SR_ID,jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sales_person_id"));
                                        basicFunction.savePreference("office_name",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_name"));
                                        basicFunction.savePreference("office_address",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_address"));
                                        basicFunction.savePreference("office_phone",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("office_phone"));
                                        basicFunction.savePreference("sr_name",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_name"));
                                        basicFunction.savePreference("db_name",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_name"));
                                        basicFunction.savePreference("db_address",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_address"));
                                        basicFunction.savePreference("db_mobile",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_mobile"));


                            basicFunction.savePreference("store_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("store_id"));
                            basicFunction.savePreference("ae_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("ae_id"));
                            basicFunction.savePreference("tso_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("tso_id"));
                            basicFunction.savePreference("db_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("db_id"));
                            basicFunction.savePreference("sr_id",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_id"));
                            basicFunction.savePreference("sr_code",jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("user_info").getString("sr_code"));

                                        Log.e("office_name",getPreference("office_name"));
                                        ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
                                        ds.excQuery("delete  from "+ Tables.TABLE_NAME_DataCheck);
                                        ds.getlastupdateddate();

                                    } else {
                                        login_button.setEnabled(true);
                                        Toast.makeText(LoginActivity.this , jsonObject.getJSONArray("response").getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ServiceHandlerOutlets", e.getMessage());
                }
             }else {
                    if (basicFunction.getPreference("password").equalsIgnoreCase(editPassword.getText().toString())) {
                        startActivity(new Intent(LoginActivity.this, Dashboard.class));
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, "Wrong Username Or Password", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });

        editUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            textDummyHintUsername.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (editUsername.getText().length() > 0)
                        textDummyHintUsername.setVisibility(View.VISIBLE);
                    else
                        textDummyHintUsername.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Password
        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //passwordtextinput.setBackgroundResource(R.drawable.focused);
                            // Show white background behind floating label
                            textDummyHintPassword.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (editPassword.getText().length() > 0)
                        textDummyHintPassword.setVisibility(View.VISIBLE);
                    else
                        textDummyHintPassword.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {


        /* startActivity(new Intent(LoginActivity.this, Dashboard.class));*/
    }



    @Override
    public void OnConnetivityError() {
        Toast.makeText(this,"NO Internet Connection",Toast.LENGTH_SHORT);
        login_button.setEnabled(true);

    }

    @Override
    public void OnLocalDBdataRetrive(final String json) {
        Log.e("Json", json);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!json.equalsIgnoreCase("done") && RcCount==101) {
                        JSONObject jsonObject = new JSONObject(json);
                        jsonObject.put("mac", basicFunction.getPreference("mac"));
                       // basicFunction.getResponceData(URL.PULL, jsonObject.toString(), 102);
                        ProgressDialog dailog = CheckConnection(LoginActivity.this,"Downloading Master Data From Server...");
                        if (dailog==null)
                            return;
                        api.PULL(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                JSONObject jsonObject= null;
                                try {
                                    jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                RcCount = 102;
                                ds.excQuery("delete  from product_history");
                                ds.excQuery("delete  from instrument_type");
                                ds.excQuery("delete  from location");
                                ds.excQuery("delete  from materials");
                                ds.excQuery("delete  from stock_info");
                                ds.excQuery("delete  from "+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
                                ds.excQuery("delete  from "+ Tables.TABLE_NAME_FISCAL_YEAR);
                                ds.insertData(jsonObject.getJSONObject("response").toString(),1);
                                Log.e("102", "OnServerResponce: "+"DELETED ALL DATA---------->" );
                                //basicFunction.getResponceData(URL.Log,jsonObject.getJSONObject("response").toString(),11);
                                login_button.setEnabled(true);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });
                    }

                   else if (json.equalsIgnoreCase("done") && RcCount==102){

                       JSONObject jsonObject =  new JSONObject();
                       jsonObject.put("Territory_Id",basicFunction.getPreference("territory_id"));
                       jsonObject.put("so_id",basicFunction.getPreference("sales_person_id"));
                       jsonObject.put("mac",basicFunction.getPreference("mac"));

                       // basicFunction.getResponceData(URL.Bonus_Policy,jsonObject.toString(),103);
                        ProgressDialog dailog = CheckConnection(LoginActivity.this,"Downloading Bonus Policy Data From Server...");
                        if (dailog==null)
                            return;
                        api.Bonus_Policy(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    dailog.dismiss();
                                    progressDialog = new ProgressDialog(LoginActivity.this);
                                    progressDialog.setMessage("Saving Bonus Policy's ...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                    RcCount = 103;
                                    ds.excQuery("delete from Policy_Table");
                                    ds.excQuery("delete from Bonus_Eligible_Group");
                                    ds.excQuery("delete from Bonus_Eligible_Outlet_Categories");
                                    ds.excQuery("delete from policy_root_product");
                                    ds.excQuery("delete from policy_product_Option");
                                    ds.excQuery("delete from policy_option_price_slab");
                                    ds.excQuery("delete from policy_bonus_product");
                                    ds.excQuery("delete from product_price_other_for_slabs_v2");
                                    ds.excQuery("delete from special_group");
                                    ds.excQuery("delete from special_group_details");
                                    ds.excQuery("delete from product_combination_list");
                                    ds.excQuery("delete from Product_combination_list_details_v2");
                                    ds.excQuery("delete from product_combinations");

                                    JsonObject jsonObj =  new JsonObject();
                                    jsonObj.addProperty("territory_id", basicFunction.getPreference("territory_id"));
                                    jsonObj.addProperty("so_id", basicFunction.getPreference("sales_person_id"));
                                    jsonObj.addProperty("mac", basicFunction.getPreference("mac"));
                                    jsonObj.addProperty("last_update_date","");
                                    jsonObj.addProperty("all","1");

                                    dataViewModel.getProductCombinationV2Data(jsonObj);

                                    dataViewModel.getProductCombinationListData(jsonObj);

                                    dataViewModel.getSpecialGroupData(jsonObj);

                                    ds.insertData(jsonObject,2);

                                    Log.e("Bonus_Combination_Data", " Inserted");


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });



                    } else if (json.equalsIgnoreCase("dbPolicy") && RcCount==103){
                        progressDialog.dismiss();
                       JSONObject jsonObject =  new JSONObject();
                       jsonObject.put("Territory_Id",basicFunction.getPreference("territory_id"));
                       jsonObject.put("so_id",basicFunction.getPreference("sales_person_id"));
                       jsonObject.put("mac",basicFunction.getPreference("mac"));

                       // basicFunction.getResponceData(URL.Bonus_Policy_Outlet,jsonObject.toString(),104);
                        ProgressDialog dailog = CheckConnection(LoginActivity.this,"Downloading Unit Data From Server...");
                        if (dailog==null)
                            return;
                        api.Bonus_Policy_Outlet(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    dailog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    progressDialog = new ProgressDialog(LoginActivity.this);
                                    progressDialog.setMessage("Saving Outlets Data From Server...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    RcCount = 104;
                                    ds.excQuery("delete from Bonus_Eligible_Outlets");
                                    ds.insertData(jsonObject,3);


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });


                    }else if (RcCount==104){
                        progressDialog.dismiss();
                        JSONObject jsonObject =  new JSONObject();
                        jsonObject.put("Territory_Id",basicFunction.getPreference("territory_id"));
                        jsonObject.put("so_id",basicFunction.getPreference("sales_person_id"));
                        jsonObject.put("mac",basicFunction.getPreference("mac"));
                       // basicFunction.getResponceData(URL.Measurement_Unit__Details_Table,jsonObject.toString(),105);
                        ProgressDialog dailog = CheckConnection(LoginActivity.this,"Downloading Unit Data From Server...");
                        if (dailog==null)
                            return;
                        api.Measurement_Unit__Details_Table(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    dailog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    progressDialog = new ProgressDialog(LoginActivity.this);
                                    progressDialog.setMessage("Saving Unit Data From Server...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    RcCount = 105;
                                    ds.excQuery("delete from unit_details");
                                    ds.insertData(jsonObject,4);


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                dailog.dismiss();
                            }
                        });


                    }else if (RcCount==105){
                        progressDialog.dismiss();


                        if (json.equalsIgnoreCase("false")){

                            basicFunction.savePreference("sales_person_id","null");
                            login_button.setEnabled(true);
                            Toast.makeText(LoginActivity.this , "Data Can't Load Properly Please try again", Toast.LENGTH_LONG).show();
                        }else {
                            startActivity(new Intent(LoginActivity.this, Dashboard.class));
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jsonexpmain",e.getMessage());
                }

                //basicFunction.getResponceData(URL.Log,createJWT("push",json), 101);
                //generateNoteOnSD(LoginActivity.this,"log.txt",json);
            }
        });

    }

    @Override
    public void OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList) {

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }


    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory() + File.separator + "log");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved" + gpxfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean checkForPermission() {
        //  Log.e("tag", "Permission");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            return false;
        } else {
            return true;
        }

    }


    public class advertizingId extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            AdvertisingIdClient.Info idInfo = null;
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(LoginActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }

            String advertId = null;
            try{
                advertId = idInfo.getId();
                Log.e("advertId",advertId);
                basicFunction.savePreference("mac",advertId);
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e("advertId",e.getMessage());
            }
            return null;
        }
    }


    //USB---------Connection-------------------------------------------------------------------------
    private TextView mInfo;
    private HashMap<UsbDevice, UsbDataBinder> mHashMap = new HashMap<UsbDevice, UsbDataBinder>();
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;

    private void usbConnection() {
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbAttachReceiver , filter);
        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbDetachReceiver , filter);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        // showDevices();
    }

    BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call your method that cleans up and closes communication with the device
                    UsbDataBinder binder = mHashMap.get(device);
                    if (binder != null) {
                        binder.onDestroy();
                        mHashMap.remove(device);
                    }
                }
            }

        }
    };

    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                 showDevices();
            }
        }
    };

    private static final String ACTION_USB_PERMISSION = "com.srapp.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {

                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            Toast.makeText(LoginActivity.this, "Printer Connected", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setPositiveButton("ok", (dialog, which) -> {

                                    })
                                    .setTitle( "Printer Connected")
                                    .setCancelable(true)
                                    .show();
                        }
                    }

                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            UsbDataBinder binder = new UsbDataBinder(mUsbManager, device);
                            mHashMap.put(device, binder);
                        }
                    } else {
                        // Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private void showDevices() {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            mUsbManager.requestPermission(device, mPermissionIntent);


            mInfo.append(device.getDeviceName() + "\n");
            mInfo.append(device.getDeviceId() + "\n");
            mInfo.append(device.getDeviceProtocol() + "\n");
            mInfo.append(device.getProductId() + "\n");
            mInfo.append(device.getVendorId() + "\n");
        }
    }
}
