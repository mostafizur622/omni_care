package com.srapp.print;

import static com.srapp.TempData.MEMO_EDIT;
import static com.srapp.TempData.memoNumber;
import static com.srapp.TempData.orderNumber;
import static com.srapp.print.newprint.Constant.CONN_STATE_DISCONN;
import static com.srapp.print.newprint.Constant.Connect_cuccess;
import static com.srapp.print.newprint.Constant.Connect_fail;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bxl.config.editor.BXLConfigLoader;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.gprinter.io.PortManager;
import com.gprinter.io.UsbPort;
import com.gprinter.utils.Command;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.DetailsOrderReport;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.Util.NumberToWords;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import NewPrint.BixolonPrinter;
import jpos.JposException;
import jpos.POSPrinter;

public class PrintActivityEN<BarcodeFormat> extends ParentActivity {
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
    String  shop_code = "", total_priceMain = "", date = "";
    public static final int FROM_HTML_MODE_LEGACY = 0;

    String appName = "";
    String appContent = "";
    String initialContent = "";
    String OutlateBold = "";
    String body = "";
    String bottomContent = "";
    String boldtext = "";
    String boldthanku = "";
    ImageView imgall;
    ArrayList<String> productList = new ArrayList<String>();
    ArrayList<String> quantityList = new ArrayList<String>();
    ArrayList<String> PriceList = new ArrayList<String>();
    private  UsbDevice mUsbDevice =null;
    private ThreadPool threadPool;
    PortManager portManager=null;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    private int portType = BXLConfigLoader.DEVICE_BUS_USB;
    private String logicalName = "PRP-250C";
    private String address = "";
    static BixolonPrinter printer;

    LinearLayout savingLayout;
    //Create image------------------------------------------------------------------
    ProgressDialog pd;

    Button saveButton;

    TextView area_office, outlet_name_category_address, market_thana, order_memo_no_date, outlet_address_phone;

    TextView db_name,db_address,outlet_name,invoice_no,invoice_date,in_word,title;

    TextView discountTxt, bonus, gift, total_bill, discount, vatCal, net_payable, sr_db_name;
    LinearLayout extra;

    private RecyclerView printRecylerview;

    private PrintRecyclerAdapterEN mAdapter;

    private int flag = 0;

    ArrayList<Bitmap> memoList = new ArrayList<Bitmap>();

    Bitmap bitmap;
    boolean print = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.print_test_en);

        savingLayout = (LinearLayout) findViewById(R.id.layout_save_image);

        //Start--Create image for print android ----------------------------
        ActivityCompat.requestPermissions(PrintActivityEN.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        pd = new ProgressDialog(PrintActivityEN.this);
        saveButton = (Button) findViewById(R.id.btnSave);
        imgall = (ImageView) findViewById(R.id.imgall);
        saveButton.setOnClickListener(v -> {
            //  printUsb();
        });

        Bundle b = getIntent().getExtras();
        final String FROM = b.getString("From");

        Button btnLastMemo = (Button) findViewById(R.id.btnLastMemo);

        btnLastMemo.setOnClickListener(v -> {

            // TODO Auto-generated method stub

            DeleteReportImage();

            startActivity(new Intent(PrintActivityEN.this, DetailsOrderReport.class));

            finish();

        });

        printRecylerview = (RecyclerView) findViewById(R.id.product_list_recycler_view);
        printRecylerview.setNestedScrollingEnabled(false);
        mAdapter = new PrintRecyclerAdapterEN(this, TempData.INVOICE_DETAILS);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());



        printRecylerview.setLayoutManager(mLayoutManager);

        printRecylerview.setAdapter(mAdapter);

        area_office = (TextView) findViewById(R.id.area_office);
        outlet_name_category_address = (TextView) findViewById(R.id.outlet_name_category_address);
        market_thana = (TextView) findViewById(R.id.market_thana);
        order_memo_no_date = (TextView) findViewById(R.id.order_memo_no_date);
        outlet_address_phone = (TextView) findViewById(R.id.outlet_address_phone);

        discountTxt = (TextView) findViewById(R.id.discountTxt);
        bonus = (TextView) findViewById(R.id.bonus);
        total_bill = (TextView) findViewById(R.id.total_bill);
        discount = (TextView) findViewById(R.id.discount);
        vatCal = (TextView) findViewById(R.id.vatCal);
        net_payable = (TextView) findViewById(R.id.net_payable);
        sr_db_name = (TextView) findViewById(R.id.sr_db_name);
        gift = (TextView) findViewById(R.id.gift);

        db_name = (TextView) findViewById(R.id.db_name);
        db_address = (TextView) findViewById(R.id.db_address);
        outlet_name = (TextView) findViewById(R.id.outlet_name);
        invoice_no = (TextView) findViewById(R.id.invoice_no);
        invoice_date = (TextView) findViewById(R.id.invoice_date);
        in_word = (TextView) findViewById(R.id.in_word);
        title = (TextView) findViewById(R.id.title);


        extra = (LinearLayout) findViewById(R.id.extra);

        if (getPreference("Outletaddress") == null || getPreference("Outletaddress").equals("")) {
         //   outlet_address_phone.setVisibility(View.GONE);
            Log.e("shah", getPreference("Outletaddress"));
        } else {
            outlet_address_phone.setVisibility(View.VISIBLE);
        }

        new Handler().post(() -> {
            MakePrintData();
        });


        printer = new BixolonPrinter(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnectBtn = (Button) findViewById(R.id.btn_connect);
        mEnableBtn = (Button) findViewById(R.id.btn_enable);
        mPrintDemoBtn = (Button) findViewById(R.id.btn_print_demo);
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
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {

                dialog.dismiss();

                mBluetoothAdapter.cancelDiscovery();
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
            mEnableBtn.setOnClickListener(v -> {

                try {

                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1000);

                } catch (Exception e) {
                    Toast.makeText(PrintActivityEN.this, "Please enable bluetooth!", Toast.LENGTH_LONG).show();
                }

            });

            //connect/disconnect
            mConnectBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    mPrintDemoBtn.setEnabled(true);
                    printUsb();
                    SaveClick(arg0); // Save converted layout image to storage

                   //printUsb();

                   /* Bitmap fewlapsBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/report.png");
                    Log.e("print",print+"");
                    print =  printer.printerOpen(portType,logicalName,address,true);
                    Log.e("print",print+"");

                        if (print) {
                            // printer.printText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku, 1, 0, (0 + 1));
                            //printer.printText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku, 1, 1, (0 + 1));
                            //   printer.printText(memoList.get(i), 1, 2, (0 + 1));
                            printer.printImage(bitmap,bitmap.getWidth(),0,2);

                        }
*/


                  /*   new  Handler().obtainMessage(0).sendToTarget();

                   new Handler().postDelayed(new Runnable() {
                        public void run() {

                          *//*  // TODO Auto-generated method stub
                            boolean print =  printer.printerOpen(portType,logicalName,address,true);
                            if (print){
                                Bitmap fewlapsBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/report.png");// print report as a image from storage for supporting all font like bangla arabic and so on
                                printer.printImage(fewlapsBitmap, 1200, 2, 300);
                            }*//*
                        }

                    }, 100);*/
                }
            });

            //print demo text
            mPrintDemoBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        POSPrinter posPrinter = new POSPrinter(PrintActivityEN.this);
                        posPrinter.open("SRP-E302");
                        posPrinter.claim(5000);
                        posPrinter.setDeviceEnabled(true);
                        posPrinter.cutPaper(100);
                    } catch (JposException e) {
                        e.printStackTrace();
                    }
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

    public String StringToHexadecimal(String str) {

        StringBuffer sb = new StringBuffer();
        char ch[] = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        String result = sb.toString();
        return result;

    }

    @Override
    public void onResume() {

        super.onResume();


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
        // showToast("Bluetooth disabled");
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
            Log.e("error", e.getMessage());
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
            Log.e("error", e.getMessage());
            throw e;
        }
    }

    private void sendData(byte[] bytes) {
        try {
            //	Log.d("adnan","Here bb "+bytes.toString());
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }
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

    public void MakePrintData() {
        //date=getCurrentDateTime1();
        Data_Source db = new Data_Source(PrintActivityEN.this);
        String Query = "";
        if (MEMO_EDIT) {

            Query = "SELECT memo_date_time,outlet_id,market_id FROM " + Tables.TABLE_NAME_MEMOS + " WHERE " + Tables.MEMOS_memo_number + "='" + memoNumber + "'";

        } else {

            Query = "SELECT order_date_time,outlet_id,market_id FROM " + Tables.TABLE_NAME_ORDER + " WHERE " + Tables.ORDER_order_number + "='" + orderNumber + "'";
        }

        String outlet_id = "", market_id = "";
        Cursor c = db.sqLiteDatabase.rawQuery(Query,null);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) do {
                date = c.getString(0);
                outlet_id = c.getString(1);
                market_id = c.getString(2);
            } while (c.moveToNext());
        }

        Cursor c2 = db.sqLiteDatabase.rawQuery("SELECT O.outlet_name, M.market_name FROM outlets O LEFT JOIN markets M ON(O.market_id=M.market_id) WHERE O.outlet_id='" + outlet_id + "'",null);
        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {

                    TempData.OutletName = c2.getString(0);
                    TempData.tempMarket = c2.getString(1);

                } while (c2.moveToNext());
            }
        }

        Cursor c3 = db.sqLiteDatabase.rawQuery("SELECT T.thana_name FROM markets M LEFT JOIN thana T ON(M.thana_id=T.thana_id) WHERE M.market_id='" + market_id + "'",null);
        if (c3 != null) {
            if (c3.moveToFirst()) {
                do {

                    TempData.tempThana = c3.getString(0);

                } while (c3.moveToNext());
            }
        }

        date = date.substring(0, date.length() - 3);

        String total = String.valueOf(roundTwoDecimals(Double.parseDouble(TempData.InvoiceTotal)));

        //  if (TempData.INVOICE_DETAILS.size() == 1) {


        area_office.setText("Area Office: " + getPreference("office_name") +", "+ getPreference("office_address") + ", " + getPreference("office_phone"));

        outlet_name_category_address.setText(TempData.OutletName + " " + "(" + getPreference("OutletCategoryName") + ") ");

        outlet_name.setText("Outlet: "+TempData.OutletName);



        db_name.setText(getResponsiveText(getPreference("db_name")));
        db_address.setText(getDbAddressMobile());




        if (!getPreference("Outletaddress").equalsIgnoreCase("")) {

            outlet_address_phone.setText(getPreference("Outletaddress") + ", " + getPreference("OutletMobile"));

        }else {
            outlet_address_phone.setText(getPreference("OutletMobile"));
        }

        market_thana.setText(TempData.tempMarket + ", " + TempData.tempThana);

        if (MEMO_EDIT) {
            order_memo_no_date.setText("Memo# " + memoNumber + ", " + date);
            invoice_no.setText("Invoice No: " +memoNumber);
            invoice_date.setText("Invoice Date: "+DateFormatedConverter(date));
        }else {
            order_memo_no_date.setText("Order# " + orderNumber + ", " + date);
            invoice_no.setText("Invoice No: " +orderNumber);
            invoice_date.setText("Invoice Date: "+DateFormatedConverter(date));
        }




        sr_db_name.setText("SR: "+getPreference("sr_name"));
        total_bill.setText("Total Bill:   " + String.valueOf(roundTwoDecimals(Double.parseDouble(TempData.InvoiceTotal))));
        if (TempData.DISCOUNT>0) {
            discount.setVisibility(View.VISIBLE);
            discount.setText("Discount:   " + roundTwoDecimals(TempData.DISCOUNT));
        }
        vatCal.setText("Vat:   " + roundTwoDecimals(TempData.VAT));
        net_payable.setText("Net-Payable:   " + roundTwoDecimals(Double.parseDouble(total) - TempData.DISCOUNT));

        String word = NumberToWords.convert((int)Math.floor(Double.parseDouble(roundTwoDecimals(Double.parseDouble(total) - TempData.DISCOUNT))));
        String part1 = word.substring(0,1).toUpperCase();
        String part2 = word.substring(1);
        in_word.setText(part1+part2);
        //  if ((TempData.TempGift != "Nill" && TempData.TempGift != "") || TempData.TempBonus_EN != "" || TempData.TempExtraBonus != "")


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
            discountTxt.setText("Discount:" + DetailsOrderReport.discount_infoPtint);
            extra.setVisibility(View.VISIBLE);
        }

      /*  if (!discount_data.equals("") && discount_data.length() > 0)
            discountTxt.setText("Discount:" + discount_data);*/

        if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus != "") {
            bonus.setText("Bonus:" + TempData.TempBonus_EN + "," + TempData.TempExtraBonus);
            extra.setVisibility(View.VISIBLE);
        }

        // }

       /* else {

        }*/



       /* SpannableStringBuilder receiptHeadBuffer56 = new SpannableStringBuilder();
        receiptHeadBuffer56.append(" ");

        SpannableStringBuilder receiptHeadBuffer = new SpannableStringBuilder();
        receiptHeadBuffer.append("\n");
        String value3 = String.format("%1$-11s %2$10s %3$11s  %4$20s"," "," "," ","Mushak - 6.3");
        receiptHeadBuffer.append(value3);
        receiptHeadBuffer.append("\n");
        receiptHeadBuffer.append(Util.center("Government of the People's Republic of", 64));
        receiptHeadBuffer.append(Util.center("Bangladesh.", 64));
        receiptHeadBuffer.append(Util.center("National Board of Revenue",64));
        receiptHeadBuffer.append(Util.center("VAT Challan Patra",64));
        receiptHeadBuffer.append(Util.center("SMC Enterprise Ltd.",64));
        receiptHeadBuffer.append(Util.center("Central BIN: 000049992-0101.",64));
        receiptHeadBuffer.append("\nArea Office: " + getPreference("office_name"));
        receiptHeadBuffer.append("\n");
        receiptHeadBuffer.append(getPreference("office_address") + ", " + getPreference("office_phone"));


        receiptHeadBuffer.append("\n");
        SpannableStringBuilder receiptHeadBufferOutlate = new SpannableStringBuilder();
        receiptHeadBufferOutlate.append(TempData.OutletName + " " + "(" + getPreference("OutletCategoryName") + ") ,"+getPreference("Outletaddress  "));

        receiptHeadBufferOutlate.append("\n");
        SpannableStringBuilder receiptHeadBufferBody = new SpannableStringBuilder();
        receiptHeadBufferBody.append(TempData.tempMarket + ", " + TempData.tempThana);
        receiptHeadBufferBody.append("\n");
        if (MEMO_EDIT)
            receiptHeadBufferBody.append("Memo# " + TempData.memoNumber + ", " + date);
        else
            receiptHeadBufferBody.append("Order# " + TempData.orderNumber + ", " + date);
        receiptHeadBufferBody.append("\n................................................................");
        //receiptHeadBuffer.append("\n");

        String value = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", "Items","vat", "Price", "Qty", "Total");
        receiptHeadBufferBody.append(value);
        Log.e("txtvalue",value);
        receiptHeadBufferBody.append("\n................................................................");

        Log.e("-----------", "----TempData.INVOICE_DETAILS.size()------" + TempData.INVOICE_DETAILS.size());


        if (TempData.INVOICE_DETAILS.size() == 1) {
            int i;
            for (i = 0; i < TempData.INVOICE_DETAILS.size(); i++) {
                HashMap<String, String> map = TempData.INVOICE_DETAILS.get(i);

                String price = String.valueOf(roundTwoDecimals(Double.parseDouble(map.get("price"))));
                String total_price = String.valueOf(roundTwoDecimals(Double.parseDouble(map.get("price")) * Double.parseDouble(map.get("quantity"))));

                if (map.get("product_name").length() <= 21) {
                    String value1 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", map.get("product_name"), map.get("vat"), price, map.get("quantity"), total_price);
                    receiptHeadBufferBody.append(value1);
                } else {
                    String firstPart = map.get("product_name").substring(0, 21);
                    String secondPart = map.get("product_name").substring(21, map.get("product_name").length());
                    String value1 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", firstPart,map.get("vat"), price, map.get("quantity"), total_price);
                    receiptHeadBufferBody.append(value1);

                    String value2 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", secondPart, "","", "", "");
                    receiptHeadBufferBody.append(value2);
                }


                if (TempData.INVOICE_DETAILS.size() - 1 != i)
                    receiptHeadBufferBody.append("\n");


            }


            if ((TempData.TempGift != "Nill" && TempData.TempGift != "") || TempData.TempBonus_EN != "" || TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n................................................................");


            if (TempData.TempGift != "Nill" && TempData.TempGift != "") {
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Gift:" + TempData.TempGift));
            }
            if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus.equals(""))
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempBonus_EN));

            if (TempData.TempBonus_EN.equals("") && TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempExtraBonus));
            if (!DetailsOrderReport.discount_info.equals("") && DetailsOrderReport.discount_info.length()>0)
                receiptHeadBufferBody.append("\n\n" + String.format("%1$-38s", "Discount:" + DetailsOrderReport.discount_info));

            if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempBonus_EN + "," + TempData.TempExtraBonus));

            Log.e("BUFFER1 AFTER APPEND:", "" + receiptHeadBuffer.toString());


            receiptHeadBufferBody.append("\n................................................................");


            SpannableStringBuilder receiptHeadBuffer1 = new SpannableStringBuilder();

            String total = String.valueOf(roundTwoDecimals(Double.parseDouble(TempData.InvoiceTotal)));


            receiptHeadBuffer1.append(""+String.format("%1$-30s %2$4s %3$12s  %4$10s", "","","Total Bill:",String.valueOf(roundTwoDecimals(Double.parseDouble(total)))));


            receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                    "Discount:", roundTwoDecimals(TempData.DISCOUNT)));
            receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                    "Vat:", roundTwoDecimals(TempData.VAT)));
            receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                    "Net-Payable:", roundTwoDecimals(Double.parseDouble(total)- TempData.DISCOUNT)));

            receiptHeadBuffer1.append("\n\n"+getPreference("sr_name")+"  DB:("+getPreference("db_name")+")");
            receiptHeadBuffer1.append("\n"+"Sales Officer");
            receiptHeadBuffer1.append("\n"+"* Product(s) price are SD free & including VAT");
            receiptHeadBuffer1.append("\n"+"Thank You!");
            receiptHeadBuffer1.append("\n\n\n");



            initialContent = receiptHeadBuffer56.toString();
            appContent = receiptHeadBuffer.toString();
            bottomContent = receiptHeadBuffer1.toString();
            //boldthanku = receiptHeadBuffer3.toString();
            OutlateBold = receiptHeadBufferOutlate.toString();
            body = receiptHeadBufferBody.toString();
            TextView txtText = (TextView) findViewById(R.id.txtText);
            txtText.setText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku);

            TempData.printBlankContent = initialContent;
            TempData.printTopContent = appContent;
            TempData.PrintOutlate = OutlateBold;
            TempData.PrintBody = body;
            TempData.printBottomContent = bottomContent;
            TempData.printName = "memo";
            TempData.AllprintName = "memo";
            TempData.printboldthank = "thanku";
            TempData.printThanku = boldthanku;
            Log.e("Tetst", TempData.TempBonus_EN);
            TempData.INVOICE_DETAILS.clear();
            TempData.TempGift = "";
            TempData.TempBonus_EN = "";
            TempData.TempExtraBonus = "";
            Log.e("length", TempData.printThanku.getBytes().length + "");


        } else {
            int i;
            for (i = 0; i < TempData.INVOICE_DETAILS.size(); i++) {
                HashMap<String, String> map = TempData.INVOICE_DETAILS.get(i);

                String price = String.valueOf(roundTwoDecimals(Double.parseDouble(map.get("price"))));
                String total_price = String.valueOf(roundTwoDecimals(Double.parseDouble(map.get("price")) * Double.parseDouble(map.get("quantity"))));
                Log.e("productname",map.get("product_name"));
                if (map.get("product_name").length() <= 21) {
                    String value1 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", map.get("product_name"), map.get("vat"), price, map.get("quantity"), total_price);
                    receiptHeadBufferBody.append(value1);
                } else {
                    String firstPart = map.get("product_name").substring(0, 21);
                    String secondPart = map.get("product_name").substring(21, map.get("product_name").length());
                    String value1 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", firstPart,map.get("vat"), price, map.get("quantity"), total_price);
                    receiptHeadBufferBody.append(value1);

                    String value2 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", secondPart, "","", "", "");
                    receiptHeadBufferBody.append(value2);


                }


                if (TempData.INVOICE_DETAILS.size() - 1 != i)
                    receiptHeadBufferBody.append("\n");

            }

            if ((TempData.TempGift != "Nill" && TempData.TempGift != "") || TempData.TempBonus_EN != "" || TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n................................................................");


            Log.e("Tetst", TempData.TempBonus_EN + "no data");
            if (TempData.TempGift != "Nill" && TempData.TempGift != "") {
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Gift:" + TempData.TempGift));
            }
            if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus.equals("")) {
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempBonus_EN));
                TempData.TempBonus_EN = "";
            }
            if (!DetailsOrderReport.discount_info.equals("") && DetailsOrderReport.discount_info.length()>0)
                receiptHeadBufferBody.append("\n\n" + String.format("%1$-38s", "Discount:" + DetailsOrderReport.discount_info));

            if (TempData.TempBonus_EN.equals("") && TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempExtraBonus));

            if (TempData.TempBonus_EN != "" && TempData.TempExtraBonus != "") {
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempBonus_EN + "," + TempData.TempExtraBonus));
                TempData.TempBonus_EN = "";

            }

            Log.e("BUFFER12 AFTER APPEND:", "" + receiptHeadBufferBody.toString());


            receiptHeadBufferBody.append("\n................................................................");


            SpannableStringBuilder receiptHeadBuffer1 = new SpannableStringBuilder();

            String total1 = String.valueOf(roundTwoDecimals(Double.parseDouble(TempData.InvoiceTotal)));

            receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                    "Total Bill:", total1));
            receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                    "Discount:", roundTwoDecimals(TempData.DISCOUNT)));
            receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                    "Vat:", roundTwoDecimals(TempData.VAT)));
            receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                    "Net-Payable:", roundTwoDecimals(Double.parseDouble(total1)- TempData.DISCOUNT)));


            SpannableStringBuilder receiptHeadBuffer3 = new SpannableStringBuilder();
            receiptHeadBuffer3.append("\n\n"+getPreference("sr_name")+"  DB:("+getPreference("db_name")+")");
            receiptHeadBuffer3.append("\n"+"Sales Officer");
            receiptHeadBuffer3.append("\n"+"* Product(s) price are SD free & including VAT");
            receiptHeadBuffer3.append("\n\n" + "Thank You!\n\n");
            receiptHeadBuffer3.append("\n\n\n\n\n");

            initialContent = receiptHeadBuffer56.toString();
            appContent = receiptHeadBuffer.toString();
            bottomContent = receiptHeadBuffer1.toString();
            boldthanku = receiptHeadBuffer3.toString();
            OutlateBold = receiptHeadBufferOutlate.toString();
            body = receiptHeadBufferBody.toString();
            TextView txtText = (TextView) findViewById(R.id.txtText);
            txtText.setText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku);

            TempData.printBlankContent = initialContent;
            TempData.printTopContent = appContent;
            TempData.printBottomContent = bottomContent;
            TempData.PrintOutlate = OutlateBold;
            TempData.PrintBody = body;
            TempData.printName = "memo";
            TempData.AllprintName = "memo";
            TempData.printboldthank = "thanku";
            TempData.printThanku = boldthanku;
            Log.e("Tetst", receiptHeadBuffer.toString());
            TempData.INVOICE_DETAILS.clear();
            TempData.TempGift = "";
            TempData.TempBonus_EN = "";
            TempData.TempExtraBonus = "";
            Log.e("Tetst", TempData.TempBonus_EN + "no data");
        }*/


    }




    // Create image for printing Bangla----------------------------------------------------------------------------
    public void SaveClick(View view) {
        pd.setMessage("saving your image");

        pd.show();

        File file = saveBitMap(PrintActivityEN.this, savingLayout);
        if (file != null) {
            pd.cancel();
            Log.i("TAG", "Drawing saved to the gallery!");
        } else {
            pd.cancel();
            Log.i("TAG", "Oops! Image could not be saved.");
        }
    }

    private File saveBitMap(Context context, View drawView) {

        File pictureFileDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));

        Log.d("imagepath", String.valueOf(pictureFileDir));

        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save the image");
            return null;
        }

        // String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".png";
        String filename = pictureFileDir.getPath() + File.separator + "report" + ".png";

        Log.d("imagename", filename);

        File pictureFile = new File(filename);

        bitmap = getBitmapFromView(drawView);


      /*  try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }*/

        scanGallery(context, pictureFile.getAbsolutePath());

        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it---------------------
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

    public void DeleteReportImage() {

        Uri uriPOZip = Uri.parse("/storage/emulated/0/Pictures/report.png");

        File zipPOfdelete = new File(uriPOZip.getPath());

        if (zipPOfdelete.exists()) {
            if (zipPOfdelete.delete()) {
                Log.d(" report File:", "Deleted !");
                // Toast.makeText(getApplicationContext(), "Previous File Deleted" + uriZip.getPath(), Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(getApplicationContext(), "Previous File not Deleted" + uriZip.getPath(), Toast.LENGTH_LONG).show();
                Log.d(" report File:", "Not Found !");
            }
        }
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

        DeleteReportImage();
        startActivity(new Intent(PrintActivityEN.this, DetailsOrderReport.class));
        finish();
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

    String dirpath;

    public void imageToPDF() throws FileNotFoundException {
        try {
            Document document = new Document();
            dirpath = Environment.getExternalStorageDirectory().toString();
            Log.e("pdfPath", dirpath);
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/NewPDF.pdf")); //  Change pdf's name.
            document.open();
            //com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");

            Image img = Image.getInstance("/storage/emulated/0/Pictures/report.png");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scaler);

            img.setAlignment(Image.ALIGN_BOTTOM | Image.ALIGN_TOP);
            document.add(img);
            document.close();
            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

     /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (PrintActivityEN.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    mUsbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && mUsbDevice != null) {
                            // printIt(new UsbConnection(usbManager, usbDevice));


                            PrintedListener printedListener = new PrintedListener() {
                                @Override
                                public void PrinteCompeleted() {


                                }

                                @Override
                                public void PrintFiled() {

                                }
                            };

                            try {
                                new AsyncUsbEscPosPrint(context,printedListener).execute(getAsyncEscPosPrinter(new UsbConnection(usbManager, mUsbDevice)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    };



    //ESCPOSPrinter---------------------------------------------------------------------------------

    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection) {
        SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection,188, 75, 32);

     //   Bitmap fewlapsBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/report.png");// print report as a image from storage for supporting all font like bangla arabic and so on



        //printer.printImage(fewlapsBitmap, 1200, 2, 300);
        //imgall.setImageBitmap(bitmap);
        return printer.setTextToPrint(
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, bitmap)+ "</img>\n \n"
        );

       /* return printer.setTextToPrint(
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.report, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n \n \n \n  "
        );*/

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
        threadPool=ThreadPool.getInstantiation();

        Log.e("device_name",mUsbDevice.getDeviceName());
        Log.e("device_name",mUsbDevice.toString());

        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                portManager=new UsbPort(PrintActivityEN.this, mUsbDevice);//实例化对象
                boolean result=portManager.openPort();//连接端口 成功返回true 失败返回 false
                mHandler.obtainMessage(result?Constant.Connect_cuccess:Constant.Connect_fail).sendToTarget();
            }
        });
        //btnPrinterState();

       /* PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(PrintActivityBN.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(PrintActivityBN.ACTION_USB_PERMISSION);
        registerReceiver(this.usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);*/
    }

    public void btnPhotoTest() {
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
                  //  Log.e(TAG,"发送内容：\r\n"+Utils.bytesToHexString(Utils.convertVectorByteToBytes(PrintContent.get80Menu())));//成功返回true  失败返回false
                  //  Log.e(TAG,"发送结果："+success);//成功返回true  失败返回false

                   btnCutTest();
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
                    btnPhotoTest();
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



