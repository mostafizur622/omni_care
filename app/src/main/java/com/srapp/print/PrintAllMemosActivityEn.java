package com.srapp.print;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bxl.config.editor.BXLConfigLoader;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.google.gson.Gson;
import com.gprinter.io.PortManager;
import com.gprinter.io.UsbPort;
import com.gprinter.utils.Command;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.DeliveryReport;
import com.srapp.R;
import com.srapp.TempData;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import NewPrint.BixolonPrinter;

import static NewPrint.EscapeSequence.ESCAPE_CHARACTERS;
import static com.srapp.Db_Actions.Tables.PRODUCT_BOOLEAN_QUANTITY;
import static com.srapp.Db_Actions.Tables.PRODUCT_ID;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRICE_PRICE;
import static com.srapp.Db_Actions.Tables.PRODUCT_PRODUCT_NAME;
import static com.srapp.DetailsOrderReport.discount_info;
import static com.srapp.TempData.discount_data;
import static com.srapp.print.newprint.Constant.CONN_STATE_DISCONN;
import static com.srapp.print.newprint.Constant.Connect_cuccess;
import static com.srapp.print.newprint.Constant.Connect_fail;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bxl.config.editor.BXLConfigLoader;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.parser.Line;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.DeliveryReport;
import com.srapp.DetailsOrderReport;
import com.srapp.Multiple_Invoice_print;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.Util.PrintedListener;
import com.srapp.print.newprint.Constant;
import com.srapp.print.newprint.PrintContent;
import com.srapp.print.newprint.ThreadPool;
import com.srapp.print.newprint.Utils;
import com.srapp.thermalprint.async.AsyncEscPosPrinter;
import com.srapp.thermalprint.async.AsyncUsbEscPosPrint;
import com.srapp.thermalprint.async.PrinterTextParserImg;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class PrintAllMemosActivityEn extends ParentActivity {
    private Button mConnectBtn;
    private Button mEnableBtn;
    private Button mPrintDemoBtn;
    String JSONResponse_Route = "";
    private Spinner mDeviceSp;
    BluetoothDevice mmDevice;
    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;
    BluetoothSocket mmSocket;
    private BluetoothAdapter mBluetoothAdapter;
    ProgressDialog pDialog;
    private P25Connector mConnector;
    JSONArray OrderList = null;
    JSONArray details = null;
    String invoice_no = "", shop_code = "", total_priceMain = "", date12 = "";
    ArrayList<String> arrayList;
    String appName = "";
    String appContent = "";
    String bottomContent = "";
    ArrayList<String> productList = new ArrayList<String>();
    ArrayList<String> quantityList = new ArrayList<String>();
    ArrayList<String> PriceList = new ArrayList<String>();
    ArrayList<Bitmap> memoList = new ArrayList<Bitmap>();
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    String FromDate = "", ToDate = "";
    private int portType = BXLConfigLoader.DEVICE_BUS_USB;
    private String logicalName = "SRP-E302";
    private String address = "";
    BixolonPrinter printer;
    boolean print = false;
    // Create image-----------------------
    ProgressDialog pd;
    Button saveButton;
    LinearLayout savingLayout;
    private ViewGroup mLinearLayout;
    ArrayList<String> arrayListPrintImage;
    HashMap<String, String> map;
    private int flag=0;

    LinearLayout parentlayout;
    private  UsbDevice mUsbDevice =null;
    private ThreadPool threadPool;
    PortManager portManager=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_all_memos_layout);
        System.gc();
        Runtime.getRuntime().gc();

        savingLayout = (LinearLayout) findViewById(R.id.layout_save_image);

        printer = new BixolonPrinter(this);
        Bundle b = getIntent().getExtras();
        FromDate = b.getString("FromDate");
        ToDate = b.getString("ToDate");
        arrayList = new ArrayList<>();

        Button btnLastMemo = (Button) findViewById(R.id.btnLastMemo);

        btnLastMemo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                TempData.INVOICE_DETAILS_PRINT.clear();
                // TODO Auto-generated method stub
                DeletePOzipFile();

                startActivity(new Intent(PrintAllMemosActivityEn.this, DeliveryReport.class));
                finish();

            }
        });

        //Start--Create image for print android ---------------------------------
        ActivityCompat.requestPermissions(PrintAllMemosActivityEn.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        pd = new ProgressDialog(PrintAllMemosActivityEn.this);
        saveButton = findViewById(R.id.btnSave);

        saveButton.setOnClickListener(view -> {
            // SaveClick(view);
        });

        arrayListPrintImage = new ArrayList<>();

        //  MakeText();
        new Handler().post(() -> MakeNewLayoutText());


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnectBtn = (Button) findViewById(R.id.btn_connect);
        mEnableBtn = (Button) findViewById(R.id.btn_enable);
        mPrintDemoBtn = (Button) findViewById(R.id.btn_print_demo);
		/*mPrintBarcodeBtn 	= (Button) findViewById(R.id.btn_print_barcode);
		mPrintImageBtn 		= (Button) findViewById(R.id.btn_print_image);
		mPrintReceiptBtn 	= (Button) findViewById(R.id.btn_print_receipt);
		mPrintTextBtn		= (Button) findViewById(R.id.btn_print_text);*/
        mDeviceSp = (Spinner) findViewById(R.id.sp_device);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {

                    mDeviceList.addAll(pairedDevices);

                    updateDeviceLis();
                }
            }


            mProgressDlg = new ProgressDialog(this);

            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg = new ProgressDialog(this);

            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {

                @Override
                public void onStartConnecting() {
                    mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    mConnectingDlg.dismiss();

                    showConnected();
                }

                @Override
                public void onConnectionFailed(String error) {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onConnectionCancelled() {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onDisconnected() {
                    showDisonnected();
                }
            });

            //enable bluetooth
            mEnableBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, 1000);

                    } catch (Exception e) {
                        Toast.makeText(PrintAllMemosActivityEn.this, "Please enable bluetooth!", Toast.LENGTH_LONG).show();
                    }

                }
            });


            //connect/disconnect
            mConnectBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // printUsb();


                    printUsb();




                   /* print =  printer.printerOpen(portType,logicalName,address,true);
                    Log.e("print",print+"");
                    for (int i=0; i<memoList.size(); i++) {
                        if (print) {
                            // printer.printText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku, 1, 0, (0 + 1));
                            //printer.printText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku, 1, 1, (0 + 1));
                         //   printer.printText(memoList.get(i), 1, 2, (0 + 1));
                            printer.printImage(memoList.get(i),memoList.get(i).getWidth(),0,2);

                        }
                    }*/

              /*      new Handler().obtainMessage(0).sendToTarget();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            print = printer.printerOpen(portType, logicalName, address, true);
                            Log.e("print", print + "");
                            for (int i = 0; i < arrayListPrintImage.size(); i++) {
                                if (print) {

                                    //-----------Image Print for Office Copy---------------------------------------------------------------------------------------
                                    Bitmap fewlapsBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/office_" + i + ".png");// print report as a image from storage for supporting all font like bangla arabic and so on

                                    printer.printImage(fewlapsBitmap, 1200, 2, 300);

                                    //-----------Image Print for Outlet Copy---------------------------------------------------------------------------------------

                                    Bitmap fewlapsBitmap1 = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/outlet_" + i + ".png");// print report as a image from storage for supporting all font like bangla arabic and so on

                                    printer.printImage(fewlapsBitmap1, 1200, 2, 300);

                                }

                            }

                        }
                    }).start(); */
                }
            });

            //print demo text
            mPrintDemoBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    printDemoContent();
                }
            });
        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, filter);
        registerReceiver(usbReceiver, filter);
    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        if (mConnector != null) {
            try {
                mConnector.disconnect();
            } catch (P25ConnectionException e) {
                e.printStackTrace();
            }
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterReceiver(usbReceiver);

        super.onDestroy();
    }

    private String[] getArra(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size = data.size();
        list = new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateDeviceLis() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, getArra(mDeviceList));
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mDeviceSp.setAdapter(adapter);
        //mDeviceSp.setSelection(0);
    }


    private void showDisabled() {
        showToast("Bluetooth disabled");

        mEnableBtn.setVisibility(View.GONE);
        mConnectBtn.setVisibility(View.VISIBLE);
        mDeviceSp.setVisibility(View.GONE);
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");

        mEnableBtn.setVisibility(View.GONE);
        mConnectBtn.setVisibility(View.VISIBLE);
        mDeviceSp.setVisibility(View.VISIBLE);
    }

    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");

        mConnectBtn.setEnabled(false);
        mPrintDemoBtn.setEnabled(false);

        mDeviceSp.setEnabled(false);
    }

    private void showConnected() {
        showToast("Connected");

        mConnectBtn.setText("Disconnect");

        mPrintDemoBtn.setEnabled(true);


        mDeviceSp.setEnabled(false);
    }

    private void showDisonnected() {
        showToast("Disconnected");

        mConnectBtn.setText("Connect");

        mPrintDemoBtn.setEnabled(false);


        mDeviceSp.setEnabled(true);
    }

    private void connect() {

		/*	if (mDeviceList == null || mDeviceList.size() == 0) {
			return;
		}*/
        //"00:19:5D:25:3A:2A"

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        BluetoothDevice device1 = null;

        Log.e("NUmber of devices:", "" + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                Log.e("adnan", "Device :" + device.toString());

                device1 = device;
                Log.e("DEVICE NAME:", ".........." + device1.getName());


                Log.e("DEVICE:", device1.toString());

            }
        }
        //mmDevice = mBluetoothAdapter.getRemoteDevice("00:19:5D:25:3A:2A");
        mmDevice = mBluetoothAdapter.getRemoteDevice(device1.toString());
        //Log.e("Device", mmDevice.toString());

        if (mmDevice.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(mmDevice);
            } catch (Exception e) {
                showToast("Failed to pair device");

                return;
            }
        }

        try {
            if (!mConnector.isConnected()) {
                mConnector.connect(mmDevice);
            } else {
                mConnector.disconnect();

                showDisonnected();
            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void createBond(BluetoothDevice device) throws Exception {

        try {
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};

            Method method = cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    private void sendData(byte[] bytes) {
        try {
            //	Log.d("adnan","Here bb "+bytes.toString());
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void printDemoContent() {


        byte[] top = Printer.printfont(appContent, FontDefine.FONT_24PX,
                FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
        byte[] bottom = Printer.printfont(bottomContent, FontDefine.FONT_26PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

        byte[] topText = Printer.printfont(appContent,
                FontDefine.FONT_24PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
        Log.e("TOP:", Arrays.toString(top));
        Log.e("TOP(String):", new String(top));
        Log.e("", "................................................");
        Log.e("BOTTOM:", Arrays.toString(bottom));
        Log.e("BOTTOM(String):", new String(bottom));
        Log.e("", "................................................");

        top = removeBytes(top, getByteIndex(top));
        bottom = removeBytes(bottom, getByteIndex(bottom));

        byte[] totladata = new byte[top.length + bottom.length];
        int offset = 0;
        System.arraycopy(top, 0, totladata, offset, top.length);
        offset += top.length;

        System.arraycopy(bottom, 0, totladata, offset, bottom.length);
        offset += bottom.length;

        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totladata, 0, totladata.length);


        Log.e("TOP:", Arrays.toString(top));
        Log.e("TOP(String):", new String(top));
        Log.e("", "................................................");
        Log.e("BOTTOM:", Arrays.toString(bottom));
        Log.e("BOTTOM(String):", new String(bottom));
        Log.e("", "................................................");
        Log.e("SEND:", Arrays.toString(senddata));
        Log.e("SEND(String):", new String(senddata));
        Log.e("", "................................................");

        senddata = removeBytes(senddata, getByteIndex(senddata));
        senddata = Arrays.copyOfRange(senddata, 0, getByteIndex1(senddata));

        Log.e("SEND:", Arrays.toString(senddata));
        Log.e("SEND(String):", new String(senddata));
        Log.e("", "................................................");


        sendData(senddata);
    }

    public int getByteIndex(byte[] a) {
        for (int i = 4; i < a.length; i++) {
            if (a[i] == 27) {
                return i;
            }
        }

        return 0;

    }

    public int getByteIndex1(byte[] a) {
        for (int i = 4; i < a.length; i++) {
/*		       if(a[i]==10&&a[i+1]==10&&a[i+2]==10)
		       {
		    	   return i+2;
		       }
*/
            if (a[i] == 90 && a[i + 1] == 88 && a[i + 2] == 90 && a[i + 3] == 88) {
                return i - 1;
            }
        }

        return 0;

    }

    public byte[] removeBytes(byte[] a, int n) {
        for (int i = n; i < a.length - 1; i++) {
            a[i - n] = a[i];
        }

        return a;
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    showDisabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                //updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED) {
                    showToast("Paired");

                    connect();
                }
            }
        }
    };

    public void MakeNewLayoutText() {
        System.gc();
        Runtime.getRuntime().gc();

        pd.setMessage("Generating invoice...");
        pd.show();

        TempData.TempGift = "";
        TempData.TempBonus_EN = "";

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int mScreenWidth = metric.widthPixels; // screen width (pixels)
        int mScreenHeight = metric.heightPixels; // screen height (pixels)

        Data_Source db = new Data_Source(PrintAllMemosActivityEn.this);
        int i = 0;

        String Query = "";
        if (getPreference("data").equalsIgnoreCase("all")) {
            Query = "SELECT order_number, order_date_time, outlet_id, gross_value, cash_received, credit_amount, market_id,discount_value,total_vat,total_discount FROM order_table where  status='1' ORDER BY _id DESC";
        } else {
            Query = "SELECT order_number, order_date_time, outlet_id, gross_value, cash_received, credit_amount, market_id,discount_value,total_vat,total_discount FROM order_table where  status='1' and order_date>=" + "'" + FromDate + "'" + " and order_date <=" + "'" + ToDate + "' ORDER BY _id DESC";
        }
        Cursor c = db.sqLiteDatabase.rawQuery(Query,null);
        Log.e("Query", "SELECT memo_number, memo_date_time, outlet_id, gross_value, cash_received, credit_amount, market_id,discount_value,total_vat,total_discount FROM order_table where memo_date>=" + "'" + FromDate + "'" + " and memo_date <=" + "'" + ToDate + "' ORDER BY _id DESC");
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {

                    TempData.INVOICE_DETAILS_PRINT.clear();

                    RecyclerView printRecylerview;
                    PrintRecyclerAdapterEN mAdapter;

                    View contentLayout;
                    TextView area_office, outlet_name_category_address, market_thana, order_memo_no_date,outlet_address_phone;
                    TextView discountTxt, bonus, gift = null, total_bill, discount, vatCal, net_payable, sr_db_name, mobile_no, sr_address, office_copy;
                    LinearLayout  layout_save_image,extra;
                    String memo_no = "";

                    //test work-----------------start------------------------------------------------------------
                    mLinearLayout = (ViewGroup) findViewById(R.id.layout_save_image);

                    contentLayout = LayoutInflater.from(this).inflate(R.layout.content_add_layout_en, mLinearLayout, false);


                    Log.e("activity_height", contentLayout.getHeight() + "-" + contentLayout.getWidth());

                    contentLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    printRecylerview = (RecyclerView) contentLayout.findViewById(R.id.product_list_recycler_view);

                    area_office = (TextView) contentLayout.findViewById(R.id.area_office);
                    outlet_address_phone = (TextView) contentLayout.findViewById(R.id.outlet_address_phone);



                    office_copy = (TextView) contentLayout.findViewById(R.id.office_copy);
                    outlet_name_category_address = (TextView) contentLayout.findViewById(R.id.outlet_name_category_address);
                    market_thana = (TextView) contentLayout.findViewById(R.id.market_thana);
                    order_memo_no_date = (TextView) contentLayout.findViewById(R.id.order_memo_no_date);
                    discountTxt = (TextView) contentLayout.findViewById(R.id.discountTxt);
                    bonus = (TextView) contentLayout.findViewById(R.id.bonus);
                    extra = contentLayout.findViewById(R.id.extra);
                    total_bill = (TextView) contentLayout.findViewById(R.id.total_bill);
                    discount = (TextView) contentLayout.findViewById(R.id.discount);
                    vatCal = (TextView) contentLayout.findViewById(R.id.vatCal);
                    net_payable = (TextView) contentLayout.findViewById(R.id.net_payable);
                    sr_db_name = (TextView) contentLayout.findViewById(R.id.sr_db_name);
                    sr_address = (TextView) contentLayout.findViewById(R.id.sales_officer_address);
                    mobile_no = (TextView) contentLayout.findViewById(R.id.sales_officer_phone);
                    office_copy = (TextView) contentLayout.findViewById(R.id.office_copy);
                    layout_save_image = (LinearLayout) contentLayout.findViewById(R.id.layout_save_image);



                    memo_no = c.getString(0);
                    String date = c.getString(1);
                    String outlet_id = c.getString(2);
                    Double gross_value = c.getDouble(3)+c.getDouble(7);
                    Double cash_received = c.getDouble(4);
                    Double credit_amount = c.getDouble(5);
                    String market_id = c.getString(6);
                    TempData.DISCOUNT = c.getDouble(7);
                    TempData.VAT = c.getDouble(8);

                    Cursor c2 = db.sqLiteDatabase.rawQuery("SELECT O.outlet_name, M.market_name,O.address, O.mobile FROM outlets O LEFT JOIN markets M ON(O.market_id=M.market_id) WHERE O.outlet_id='" + outlet_id + "'",null);
                    if (c2 != null) {
                        if (c2.moveToFirst()) {
                            do {

                                TempData.OutletName = c2.getString(0);
                                TempData.tempMarket = c2.getString(1);
                                TempData.Outlet_Address = c2.getString(2);
                                TempData.Outlet_Mobile_MEM = c2.getString(3);

                            } while (c2.moveToNext());
                        }
                    }

                   /* if (TempData.Outlet_Address=="" || TempData.Outlet_Mobile_MEM==""){
                        outlet_address_phone.setVisibility(View.GONE);
                        Log.e("shah",  getPreference("Outletaddress"));
                    }
                    else {
                        outlet_address_phone.setVisibility(View.VISIBLE);
                    }*/

                    Cursor c3 = db.sqLiteDatabase.rawQuery("SELECT T.thana_name FROM markets M LEFT JOIN thana T ON(M.thana_id=T.thana_id) WHERE M.market_id='" + market_id + "'",null);
                    if (c3 != null) {
                        if (c3.moveToFirst()) {
                            do {

                                TempData.tempThana = c3.getString(0);

                            } while (c3.moveToNext());
                        }
                    }

                    Cursor c4 = db.sqLiteDatabase.rawQuery("SELECT OC.outlet_category_name FROM outlets O LEFT JOIN outlet_categories OC ON (O.outlet_category_id = OC.outlet_category_id) where O.outlet_id='" + outlet_id + "'",null);
                    if (c4.getCount() > 0) {
                        if (c4.moveToFirst()) {
                            do {

                                String outlet_category_name = c4.getString(0);
                                savePreference("OutletCategoryName", outlet_category_name);


                            } while (c4.moveToNext());
                        }
                    }

                    date = date.substring(0, date.length() - 3);


                    String MemoDetails = "SELECT MD.product_id,P.product_name ,MD.quantity, MD.price,MD.vat,MD.discount_type,MD.discount_amount FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='" + memo_no + "' and MD.product_type='0'";
                    Log.e("MemoDetails", "MemoDetails: " + MemoDetails);
                    discount_info = "";
                    Cursor cursor = db.sqLiteDatabase.rawQuery(MemoDetails,null);


                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            do {
                                discount_info = discount_info + getdiscount(cursor.getDouble(2), cursor.getDouble(3), cursor.getInt(5), cursor.getDouble(6), cursor.getString(1));

                                String product_id = cursor.getString(0);
                                String product_name = cursor.getString(1);
                                String quantity = cursor.getString(2);
                                String price = cursor.getString(3);
                                String vat = cursor.getString(4);
                                if (TextUtils.isEmpty(vat)) {
                                    vat = "0.0%";
                                }
                                String total_price = roundTwoDecimals(cursor.getDouble(2) * cursor.getDouble(3));

                                Log.e("productname22", product_name);

                                map = new HashMap<String, String>();
                                map.put(PRODUCT_PRODUCT_NAME, product_name);
                                map.put(PRODUCT_BOOLEAN_QUANTITY, quantity);
                                map.put(PRODUCT_PRICE_PRICE, price);

                                map.put("total_price", String.valueOf(total_price));
                                map.put("vat", roundTwoDecimals(cursor.getDouble(4)) + "%");
                                map.put(PRODUCT_ID, product_id);
                                TempData.INVOICE_DETAILS_PRINT.add(map);


                            } while (cursor.moveToNext());
                        }
                    }


                    String Gift1 = "";
                    String MemoDetailsForGift = "SELECT MD.product_id,P.product_name ,MD.quantity FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='" + memo_no + "' and MD.product_type='1'";
                    Log.e("MemoDetailsForGift", "MemoDetailsForGift: " + MemoDetailsForGift);
                    Cursor cur2 = db.sqLiteDatabase.rawQuery(MemoDetailsForGift,null);
                    if (cur2 != null) {
                        if (cur2.moveToFirst()) {
                            do {
                                String product_id = cur2.getString(0);
                                String product_name = cur2.getString(1);
                                String quantity = cur2.getString(2);
                                String Gift2 = product_name + "(" + quantity + ")";
                                Gift1 = Gift1 + "," + Gift2;
                                String Gift = Gift1.substring(1);
                                TempData.TempGift = Gift;

                            } while (cur2.moveToNext());
                        }
                    }


                    String Bonus1 = "", Bonus = "";
                    String MemoDetailsForBonus = "SELECT MD.product_id,P.product_name ,MD.quantity , MD.measurement_unit_id  FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='" + memo_no + "' and MD.product_type='2'";
                    Log.e("MemoDetailsForBonus", "MemoDetailsForBonus: " + MemoDetailsForBonus);
                    Cursor cur = db.sqLiteDatabase.rawQuery(MemoDetailsForBonus,null);
                    if (cur != null) {
                        if (cur.moveToFirst()) {
                            do {
                                String product_id = cur.getString(0);
                                String product_name = cur.getString(1);
                                String quantity = cur.getString(2);
                                String Bonus2 = product_name + "(" + quantity + " " + getMeasurementUnitName(cur.getString(3)) + ")";
                                Bonus1 = Bonus1 + "," + Bonus2;
                                Bonus = Bonus1.substring(1);
                                TempData.TempBonus_EN = Bonus;

                            } while (cur.moveToNext());
                        }
                    }

                    mAdapter = new PrintRecyclerAdapterEN(this, TempData.INVOICE_DETAILS_PRINT);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    printRecylerview.setLayoutManager(mLayoutManager);
                    printRecylerview.setAdapter(mAdapter);

                    office_copy.setText("Office Copy");
                    area_office.setText("Area Office: " + getPreference("office_name") +", "+ getPreference("office_address") + ", " + getPreference("office_phone"));

                    outlet_name_category_address.setText(TempData.OutletName + " " + "(" + getPreference("OutletCategoryName") + ") ");

                    try {
                        outlet_address_phone.setText(TempData.Outlet_Address +", "+ TempData.Outlet_Mobile_MEM);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    market_thana.setText(TempData.tempMarket + ", " + TempData.tempThana);
                       /* if (MEMO_EDIT)
                            order_memo_no_date.setText("Memo# " + memo_no + ", " + date);
                        else*/
                    order_memo_no_date.setText("Order# " + memo_no + ", " + date);


                    sr_db_name.setText(getPreference("sr_name") + "  DB:(" + getPreference("db_name") + ")");
                    mobile_no.setText("Mobile No: " + getPreference("db_mobile"));
                    sr_address.setText("Address: " + getPreference("db_address"));

                    total_bill.setText("Total Bill:   " + roundTwoDecimals(gross_value));
                    discount.setText("Discount:   " + roundTwoDecimals(TempData.DISCOUNT));
                    vatCal.setText("Vat:   " + roundTwoDecimals(TempData.VAT));
                    net_payable.setText("Net-Payable:   " + roundTwoDecimals(gross_value - TempData.DISCOUNT));

                    Log.e("Net-Payable", "MakeNewLayoutText: ");
                    if (TempData.TempGift != "Nill" && TempData.TempGift != "") {
                        gift.setText("Gift:" + TempData.TempGift);
                        extra.setVisibility(View.VISIBLE);
                    }
                    if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus.equals("")) {
                        bonus.setText("Bonus:" + TempData.TempBonus_EN);
                        extra.setVisibility(View.VISIBLE);
                    }

                    if (TempData.TempBonus_EN.equals("") && TempData.TempExtraBonus != "") {
                        bonus.setText("Bonus:" + TempData.TempExtraBonus);
                        extra.setVisibility(View.VISIBLE);
                    }

                    if (!DetailsOrderReport.discount_info.equals("") && DetailsOrderReport.discount_info.length() > 0) {
                        discountTxt.setText("Discount:" + DetailsOrderReport.discount_info);
                        extra.setVisibility(View.VISIBLE);
                    }

                    /*if (!discount_data.equals("") && discount_data.length() > 0)
                        discountTxt.setText("Discount:" + discount_data);*/

                    if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus != "") {
                        bonus.setText("Bonus:" + TempData.TempBonus_EN + "," + TempData.TempExtraBonus);
                        extra.setVisibility(View.VISIBLE);
                    }


                    TempData.TempGift = "";
                    TempData.TempBonus_EN = "";
                    TempData.TempExtraBonus = "";

                    contentLayout.measure(View.MeasureSpec.makeMeasureSpec(mScreenWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(mScreenHeight, View.MeasureSpec.AT_MOST));

                    contentLayout.layout(0, 0, contentLayout.getMeasuredWidth(), contentLayout.getMeasuredHeight());
                    if(layout_save_image.getParent() != null) {
                        ((ViewGroup)layout_save_image.getParent()).removeView(layout_save_image); // <- fix
                    }
                    savingLayout.addView(layout_save_image);
                    Log.e("Net-Payable", "getBitmapFromView: ");
                    memoList.add(getBitmapFromView(layout_save_image));
                    arrayListPrintImage.add("office_" + i);

                    office_copy.setText("Outlet Copy");
                    if(layout_save_image.getParent() != null) {
                        ((ViewGroup)layout_save_image.getParent()).removeView(layout_save_image); // <- fix
                    }
                    savingLayout.addView(layout_save_image);
                    memoList.add(getBitmapFromView(layout_save_image));

                    arrayListPrintImage.add("outlet_" + i);

                    i++;
                } while (c.moveToNext());

            }
        }
        pd.dismiss();
    }


    // Create image for printing Bangla----------------------------------------------------------------------------
    public void SaveClick(View view, String fileName) {

    }



    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        System.gc();
        Runtime.getRuntime().gc();

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        ImageView iv = (ImageView)findViewById(R.id.image);
        iv.setImageBitmap(returnedBitmap);
        return returnedBitmap;
    }

    // used for scanning gallery
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue scanning gallery.");
        }
    }

    public void DeletePOzipFile() {

        for (int i = 0; i < arrayListPrintImage.size(); i++) {

            Uri officeCopy = Uri.parse("/storage/emulated/0/Pictures/office_" + i + ".png");
            Uri outLetCopy = Uri.parse("/storage/emulated/0/Pictures/outlet_" + i + ".png");

            File officeCopyDelete = new File(officeCopy.getPath());
            File outletCopyDelete = new File(outLetCopy.getPath());

            if (officeCopyDelete.exists()) {
                if (officeCopyDelete.delete()) {
                    Log.d(" Zip File:", "Deleted !");
                } else {
                    Log.d(" Zip File:", "Not Found !");
                }
            }

            if (outletCopyDelete.exists()) {
                if (outletCopyDelete.delete()) {
                    Log.d(" Zip File:", "Deleted !");
                } else {
                    Log.d(" Zip File:", "Not Found !");
                }
            }
        }

    }


    private String getMeasurementUnitName(String mesurement_unit_id) {
        Data_Source db = new Data_Source(PrintAllMemosActivityEn.this);
        Cursor c = db.sqLiteDatabase.rawQuery("select unit_name from unit where unit_id='" + mesurement_unit_id + "'",null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            return c.getString(0);
        }
        return "Not specified";
    }

    private String getdiscount(double qty, double price, int distype, double disamount, String product_name) {

        if (disamount <= 0) {
            return "";
        }
        Double discount = 0.0;

        Log.e("discount_type", distype + "");

        return product_name + "(" + (roundTwoDecimals(disamount * qty)) + ")";

    }

    public String roundTwoDecimals(double d) {

        Log.e("Double:", String.valueOf(d));
        Log.e("Modulus:", String.valueOf(d % 1));
        return String.format("%.2f", d);
    }

    public static String rightPadding(String str, int num) {
        return String.format("%1$-" + num + "s", str);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TempData.INVOICE_DETAILS_PRINT.clear();
        // TODO Auto-generated method stub
        DeletePOzipFile();
        startActivity(new Intent(PrintAllMemosActivityEn.this, DeliveryReport.class));
        finish();
    }

    private void LogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PrintAllMemosActivityEn.this);
        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //startActivity(new Intent(PrintActivity.this,Login.class));
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }

        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
                || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
                || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
                || connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }


    public String getCurrentDateTime1() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = new Date();
        String CurrentDate = dateFormat.format(date);

        return CurrentDate;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)


            return false;
        return false;

    }



      /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    int i = 0;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            i = 0;
            String action = intent.getAction();
            if (PrintAllMemosActivityEn.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            Log.e("arrayListPrintImage", String.valueOf(memoList.size()));


                            PrintedListener printedListener = new PrintedListener() {
                                @Override
                                public void PrinteCompeleted() {

                                    try {
                                        if (i<memoList.size()) {
                                            i++;
                                            new AsyncUsbEscPosPrint(context, this).execute(getAsyncEscPosPrinter(new UsbConnection(usbManager, usbDevice), memoList.get(i)));
                                            // new AsyncUsbEscPosPrint(context).execute(getAsyncEscPosPrinter1(new UsbConnection(usbManager, usbDevice), i));
                                            Log.e("memo", i + "");
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();

                                        Log.e("exception", e.getLocalizedMessage());
                                    }
                                }

                                @Override
                                public void PrintFiled() {
                                    if (i<memoList.size())
                                        new AsyncUsbEscPosPrint(context,this).execute(getAsyncEscPosPrinter(new UsbConnection(usbManager, usbDevice), memoList.get(i)));
                                }
                            };

                            new AsyncUsbEscPosPrint(context,printedListener).execute(getAsyncEscPosPrinter(new UsbConnection(usbManager, usbDevice), memoList.get(i)));

                            // }
                        }
                    }
                }
            }

        }
    };




    /**
     * Asynchronous printing
     */

    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection, Bitmap i) {

        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 111, 78, 32);

        //-----------Image Print for Office Copy---------------------------------------------------------------------------------------
        // Bitmap fewlapsBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/office_" + i + ".png");// print report as a image from storage for supporting all font like bangla arabic and so on

        printer.setTextToPrint(
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, i) + "</img>\n \n \n \n  "
        );


        return printer;
    }

    //ESCPOSPrinter---------------------------------------------------------------------------------

    /**
     * Asynchronous printing
     */

    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter1(DeviceConnection printerConnection, int i) {

        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 188, 78, 32);


        //-----------Image Print for Outlet Copy---------------------------------------------------------------------------------------

        Bitmap fewlapsBitmap1 = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/outlet_" + i + ".png");// print report as a image from storage for supporting all font like bangla arabic and so on

        printer.setTextToPrint(
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, fewlapsBitmap1) + "</img>\n \n \n \n  "
        );

        return printer;
    }

    public void printUsb() {
        UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(this);
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        if (usbConnection == null || usbManager == null) {
            new AlertDialog.Builder(this)
                    .setTitle("USB Connection")
                    .setMessage("No USB printer found.")
                    .show();
            return;
        }
        mUsbDevice = usbConnection.getDevice();
        threadPool= ThreadPool.getInstantiation();
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                portManager=new UsbPort(PrintAllMemosActivityEn.this, mUsbDevice);//实例化对象
                boolean result=portManager.openPort();//连接端口 成功返回true 失败返回 false
                mHandler.obtainMessage(result? Constant.Connect_cuccess:Constant.Connect_fail).sendToTarget();
            }
        });
        //btnPrinterState();

       /* PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(PrintActivity.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(PrintActivity.ACTION_USB_PERMISSION);
        registerReceiver(this.usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);*/
    }
    public void btnPhotoTest(Bitmap bitmap) {
        if (portManager== null) {
            // Utils.toast(this, getString(R.string.str_cann_printer));
            return;
        }
        threadPool = ThreadPool.getInstantiation();
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                try {

                    boolean bool = portManager==null;

                    Log.e("portManager",String.valueOf(bool));
                    boolean success=portManager.writeDataImmediately(PrintContent.getPhoto(bitmap));
                    portManager.writeDataImmediately(PrintContent.openCut());
                    //  Log.e(TAG,"发送内容：\r\n"+Utils.bytesToHexString(Utils.convertVectorByteToBytes(PrintContent.get80Menu())));//成功返回true  失败返回false
                    //  Log.e(TAG,"发送结果："+success);//成功返回true  失败返回false

                    // btnCutTest();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                        }
                    },1000);
                } catch (IOException e) {
                    mHandler.obtainMessage(Constant.CONN_STATE_DISCONN).sendToTarget();
                }
            }
        });
    }



    public void btnPrinterState() {
        //打印机状态查询
        if (portManager == null ){
            Utils.toast(this, "Port manager is null");
            return;
        }
        ThreadPool.getInstantiation().addParallelTask(new Runnable() {
            @Override
            public void run() {
                int stauts = 0;
                try {
                    stauts = portManager.getPrinterStatus(Command.ESC);
                    sendStatus(stauts);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                        }
                    },1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private void sendStatus(int status){
        Message msg=new Message();
        msg.what=Constant.PRINTER_STATUS;
        msg.arg1=status;
        mHandler.sendMessage(msg);
    }


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_STATE_DISCONN:

                    break;
                case Connect_cuccess:
                    Log.e("status","Connect");

                    for (int i=0; i<memoList.size(); i++) {

                        btnPhotoTest(memoList.get(i));

                    }

                    break;
                case Connect_fail:
                    Log.e("status","Connect_fail");
                    break;
                case Constant.tip:

                    break;
                case Constant.PRINTER_STATUS:

                    break;
                case Constant.MESSAGE_UPDATE_PARAMETER://wifi或以太网连接

                    break;




            }
        }
    };
    public void btnCutTest() {
        if (portManager== null) {
            // Utils.toast(this, getString(R.string.str_cann_printer));
            return;
        }
        threadPool = ThreadPool.getInstantiation();
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success=portManager.writeDataImmediately(PrintContent.openCut());
                  /*  Log.e(TAG,"发送内容：\r\n"+Utils.bytesToHexString(Utils.convertVectorByteToBytes(PrintContent.openCut())));//成功返回true  失败返回false
                    Log.e(TAG,"发送结果："+success);*///成功返回true  失败返回false
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                        }
                    },100);
                } catch (IOException e) {
                    mHandler.obtainMessage(Constant.CONN_STATE_DISCONN).sendToTarget();
                }
            }
        });
    }


}


