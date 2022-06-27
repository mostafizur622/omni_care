package com.srapp.print;

import static com.srapp.print.newprint.Constant.CONN_STATE_DISCONN;
import static com.srapp.print.newprint.Constant.Connect_cuccess;
import static com.srapp.print.newprint.Constant.Connect_fail;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.srapp.R;
import com.srapp.Reports_Activity;
import com.srapp.TempData;
import com.srapp.Util.PrintedListener;
import com.srapp.print.newprint.Constant;
import com.srapp.print.newprint.PrintContent;
import com.srapp.print.newprint.ThreadPool;
import com.srapp.print.newprint.Utils;
import com.srapp.thermalprint.async.AsyncEscPosPrinter;
import com.srapp.thermalprint.async.AsyncUsbEscPosPrint;
import com.srapp.thermalprint.async.PrinterTextParserImg;
import com.tanvir.BasicFun.BasicFunction;

import org.json.JSONArray;

import java.io.File;
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

public class SummeryPrintActivity extends ParentActivity {

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
    String invoice_no = "", shop_code = "", total_priceMain = "", date = "";
    public static final int FROM_HTML_MODE_LEGACY = 0;
    String appName = "";
    String appContent = "";
    String initialContent = "";
    String OutlateBold = "";
    String body = "";
    String bottomContent = "";
    String boldtext = "";
    String boldthanku = "";
    BasicFunction bf;
    Bitmap bitmap;
    private  UsbDevice mUsbDevice =null;
    private ThreadPool threadPool;
    PortManager portManager=null;

    ArrayList<String> productList = new ArrayList<String>();
    ArrayList<String> quantityList = new ArrayList<String>();
    ArrayList<String> PriceList = new ArrayList<String>();

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    private int portType = BXLConfigLoader.DEVICE_BUS_USB;
    private String logicalName = "SRP-E302";
    private String address = "";
     BixolonPrinter printer;

    LinearLayout savingLayout;
    // Create image-----------------------
    ProgressDialog pd;
    Button saveButton;

    TextView toDate, fromDate, reportName;
    TextView sr_db_name, mobile_no, sr_address, printingDate;

    private RecyclerView printRecylerview;
    private PrintInvoiceSummeryAdapter mAdapter;
    boolean print;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.invoice_summery_report);

        savingLayout = (LinearLayout) findViewById(R.id.layout_save_image);

        toDate = (TextView) findViewById(R.id.startDate);
        fromDate = (TextView) findViewById(R.id.endDate);
        reportName = (TextView) findViewById(R.id.reportName);

        sr_db_name = (TextView) findViewById(R.id.sr_db_name);
        sr_address = (TextView) findViewById(R.id.sales_officer_address);
        mobile_no = (TextView) findViewById(R.id.sales_officer_phone);
        printingDate = (TextView) findViewById(R.id.printDate);
        printer = new BixolonPrinter(this);
        //Start--Create image for print android ----------------------------
        ActivityCompat.requestPermissions(SummeryPrintActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        pd = new ProgressDialog(SummeryPrintActivity.this);

        /*  Bundle b = getIntent().getExtras();
        final String FROM = b.getString("From");*/

        Button btnLastMemo = (Button) findViewById(R.id.btnLastMemo);

        btnLastMemo.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            TempData.INVOICE_SUMMERY_PRINT.clear();
            DeleteReportImage();
            TempData.summeryStartDate="";
            startActivity(new Intent(SummeryPrintActivity.this, Reports_Activity.class));
            finish();
        });

        printRecylerview = (RecyclerView) findViewById(R.id.product_list_recycler_view);
        mAdapter = new PrintInvoiceSummeryAdapter(this, TempData.INVOICE_SUMMERY_PRINT);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());



        printRecylerview.setLayoutManager(mLayoutManager);

        printRecylerview.setAdapter(mAdapter);

        new Handler().post(() -> MakePrintData());

       // printer = new BixolonPrinter(this);

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
                    Toast.makeText(SummeryPrintActivity.this, "Please enable bluetooth!", Toast.LENGTH_LONG).show();
                }

            });


            //connect/disconnect
            mConnectBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    mPrintDemoBtn.setEnabled(true);
                    SaveClick();
                    Bitmap fewlapsBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/report.png");
                    printUsb();

                /*    print =  printer.printerOpen(portType,logicalName,address,true);
                    Log.e("print",print+"");

                    if (print) {
                        // printer.printText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku, 1, 0, (0 + 1));
                        //printer.printText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku, 1, 1, (0 + 1));
                        //   printer.printText(memoList.get(i), 1, 2, (0 + 1));
                        printer.printImage(fewlapsBitmap,fewlapsBitmap.getWidth(),0,10);

                    }*/



                    //SaveClick(arg0); // Save converted layout image to storage

                   // printUsb();

                  /*  new  Handler().obtainMessage(0).sendToTarget();

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            // TODO Auto-generated method stub
                            boolean print =  printer.printerOpen(portType,logicalName,address,true);
                            if (print){
                                Bitmap fewlapsBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/report.png");// print report as a image from storage for supporting all font like bangla arabic and so on
                                printer.printImage(fewlapsBitmap, 1200, 2, 300);
                            }
                        }
                    }, 100);*/

                }
            });

            //print demo text
            mPrintDemoBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        POSPrinter posPrinter = new POSPrinter(SummeryPrintActivity.this);
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

        toDate.setText("Start Date:" + TempData.summeryStartDate);
        fromDate.setText("End Date:" + TempData.summeryEndDate);
        reportName.setText(TempData.summeryReportName);

        sr_db_name.setText(getPreference("sr_name") + "  DB:(" + getPreference("db_name"));
        mobile_no.setText("Address: " + getPreference("db_address"));
        sr_address.setText("Mobile No: " + getPreference("db_mobile"));
        printingDate.setText("Printing date: " + getCurrentDateTime());
    }

    // Create image for printing Bangla----------------------------------------------------------------------------
    public void SaveClick() {
        pd.setMessage("saving your image");

        pd.show();

        File file = saveBitMap(SummeryPrintActivity.this, savingLayout);
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
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
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

    public String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String CurrentDate = dateFormat.format(date);

        return CurrentDate;

    }

    public void DeletePOzipFile() {

        Uri uriPOZip = Uri.parse("/storage/emulated/0/Download/bmfpoerp.zip");

        File zipPOfdelete = new File(uriPOZip.getPath());

        if (zipPOfdelete.exists()) {
            if (zipPOfdelete.delete()) {
                Log.d(" Zip File:", "Deleted !");
                // Toast.makeText(getApplicationContext(), "Previous File Deleted" + uriZip.getPath(), Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(getApplicationContext(), "Previous File not Deleted" + uriZip.getPath(), Toast.LENGTH_LONG).show();
                Log.d(" Zip File:", "Not Found !");
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
        // TODO Auto-generated method stub
        TempData.INVOICE_SUMMERY_PRINT.clear();
        DeleteReportImage();
        startActivity(new Intent(SummeryPrintActivity.this, Reports_Activity.class));
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


      /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SummeryPrintActivity.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {




                            PrintedListener printedListener = new PrintedListener() {
                                @Override
                                public void PrinteCompeleted() {


                                }

                                @Override
                                public void PrintFiled() {

                                }
                            };


                            new AsyncUsbEscPosPrint(context,printedListener).execute(getAsyncEscPosPrinter(new UsbConnection(usbManager, usbDevice)));

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
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 188, 78, 32);

        Bitmap fewlapsBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/report.png");// print report as a image from storage for supporting all font like bangla arabic and so on
        return printer.setTextToPrint(
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, fewlapsBitmap) + "</img>\n \n \n \n  "
        );
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
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                portManager=new UsbPort(SummeryPrintActivity.this, mUsbDevice);//实例化对象
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

    public class MakeData extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            MakePrintData();

            return null;
        }
    }

}



