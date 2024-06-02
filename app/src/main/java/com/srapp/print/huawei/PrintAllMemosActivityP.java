package com.srapp.print.huawei;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bxl.config.editor.BXLConfigLoader;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.DeliveryReport;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.Util.ParentActivity;
import com.srapp.print.FontDefine;
import com.srapp.print.P25ConnectionException;
import com.srapp.print.P25Connector;
import com.srapp.print.PocketPos;
import com.srapp.print.Printer;
import com.srapp.print.Util;

import org.json.JSONArray;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import NewPrint.BixolonPrinter;

import static com.srapp.DetailsOrderReport.discount_info;

public class PrintAllMemosActivityP extends ParentActivity
{
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
    String invoice_no ="",shop_code="",total_priceMain="",date12="";
    ArrayList<String> arrayList;
    String appName = "";
    String appContent="";
    String bottomContent="";
    ArrayList<String> productList=new ArrayList<String>();
    ArrayList<String> quantityList=new ArrayList<String>();
    ArrayList<String> PriceList=new ArrayList<String>();
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    String FromDate ="", ToDate="";
    private int portType = BXLConfigLoader.DEVICE_BUS_USB;
    private String logicalName = "SRP-E302";
    private String address = "";
    static BixolonPrinter printer;
    boolean print=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.print_main);
        printer= new BixolonPrinter(this);
        Bundle b = getIntent().getExtras();
         FromDate = b.getString("FromDate");
         ToDate = b.getString("ToDate");
        arrayList = new ArrayList<>();


        Button btnLastMemo=(Button)findViewById(R.id.btnLastMemo);

        btnLastMemo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                startActivity(new Intent(PrintAllMemosActivityP.this, DeliveryReport.class));
                finish();

            }
        });


        MakeText();



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnectBtn			= (Button) findViewById(R.id.btn_connect);
        mEnableBtn			= (Button) findViewById(R.id.btn_enable);
        mPrintDemoBtn 		= (Button) findViewById(R.id.btn_print_demo);
		/*mPrintBarcodeBtn 	= (Button) findViewById(R.id.btn_print_barcode);
		mPrintImageBtn 		= (Button) findViewById(R.id.btn_print_image);
		mPrintReceiptBtn 	= (Button) findViewById(R.id.btn_print_receipt);
		mPrintTextBtn		= (Button) findViewById(R.id.btn_print_text);*/
        mDeviceSp 			= (Spinner) findViewById(R.id.sp_device);

        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showUnsupported();
        }
        else {
            if (!mBluetoothAdapter.isEnabled())
            {
                showDisabled();
            }
            else {
                showEnabled();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);

                    updateDeviceLis();
                }
            }




            mProgressDlg 	= new ProgressDialog(this);

            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg 	= new ProgressDialog(this);

            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            mConnector 		= new P25Connector(new P25Connector.P25ConnectionListener() {

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
                        Toast.makeText(PrintAllMemosActivityP.this, "Please enable bluetooth!", Toast.LENGTH_LONG).show();
                    }

                }
            });

            //connect/disconnect
            mConnectBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    new Handler().obtainMessage(0).sendToTarget();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            print =  printer.printerOpen(portType,logicalName,address,true);
                            Log.e("print",print+"");
                            for (int i=0; i<arrayList.size(); i++) {
                                if (print) {
                                    // printer.printText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku, 1, 0, (0 + 1));
                                    //printer.printText(initialContent + appContent + OutlateBold + body + bottomContent + boldthanku, 1, 1, (0 + 1));
                                    printer.printText(arrayList.get(i), 1, 2, (0 + 1));


                                }
                            }/*else {
                              *//*Looper.prepare();
                              Toast.makeText(PrintActivity.this,"Sorry",Toast.LENGTH_LONG).show();*//*
                            }*/

                        }
                    }).start();




                  /*  try {
                        connect();
                    } catch (Exception e) {
                        Toast.makeText(PrintAllMemosActivityP.this, "Please pair your mobile with bluetooth printer!", Toast.LENGTH_LONG).show();
                    }*/

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

        super.onDestroy();
    }

    private String[] getArra(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size	= data.size();
        list		= new String[size];

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

        Log.e("NUmber of devices:", ""+pairedDevices.size());
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                Log.e("adnan","Device :"+device.toString());

                device1=device;
                Log.e("DEVICE NAME:",	".........."+device1.getName());



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
            Class<?> cl 	= Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par 	= {};

            Method method 	= cl.getMethod("createBond", par);

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

    private void printDemoContent(){


        byte[] top = Printer.printfont(appContent, FontDefine.FONT_24PX,
                FontDefine.Align_CENTER,(byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
        byte[] bottom	= Printer.printfont(bottomContent, FontDefine.FONT_26PX, FontDefine.Align_CENTER,(byte)0x1A, PocketPos.LANGUAGE_ENGLISH);

        byte[] topText = Printer.printfont(appContent,
                FontDefine.FONT_24PX, FontDefine.Align_CENTER,(byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
        Log.e("TOP:", Arrays.toString(top));
        Log.e("TOP(String):", new String(top));
        Log.e("","................................................");
        Log.e("BOTTOM:", Arrays.toString(bottom));
        Log.e("BOTTOM(String):", new String(bottom));
        Log.e("","................................................");

        top=removeBytes(top,getByteIndex(top));
        bottom=removeBytes(bottom,getByteIndex(bottom));

        byte[] totladata =  new byte[top.length + bottom.length  ];
        int offset = 0;
        System.arraycopy(top, 0, totladata, offset, top.length);
        offset += top.length;

        System.arraycopy(bottom, 0, totladata, offset, bottom.length);
        offset+= bottom.length;

        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totladata, 0, totladata.length);


        Log.e("TOP:", Arrays.toString(top));
        Log.e("TOP(String):", new String(top));
        Log.e("","................................................");
        Log.e("BOTTOM:", Arrays.toString(bottom));
        Log.e("BOTTOM(String):", new String(bottom));
        Log.e("","................................................");
        Log.e("SEND:", Arrays.toString(senddata));
        Log.e("SEND(String):", new String(senddata));
        Log.e("","................................................");

        senddata=removeBytes(senddata,getByteIndex(senddata));
        senddata=Arrays.copyOfRange(senddata, 0, getByteIndex1(senddata));

        Log.e("SEND:", Arrays.toString(senddata));
        Log.e("SEND(String):", new String(senddata));
        Log.e("","................................................");



        sendData(senddata);
    }

    public int getByteIndex(byte[] a)
    {
        for (int i = 4; i < a.length; i++) {
            if(a[i]==27)
            {
                return i;
            }
        }

        return 0;

    }

    public int getByteIndex1(byte[] a)
    {
        for (int i = 4; i < a.length; i++) {
/*		       if(a[i]==10&&a[i+1]==10&&a[i+2]==10)
		       {
		    	   return i+2;
		       }
*/		       if(a[i]==90&&a[i+1]==88&&a[i+2]==90&&a[i+3]==88)
            {
                return i-1;
            }
        }

        return 0;

    }

    public byte[] removeBytes(byte[] a,int n) {
        for (int i = n; i < a.length-1; i++) {
            a[i-n]=a[i];
        }

        return a;
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state 	= intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

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

    public void MakeText()
    {
        TempData.TempGift="";
        TempData.TempBonus_EN ="";
        String AllPrint="";
        Data_Source db=new Data_Source(PrintAllMemosActivityP.this);
        Cursor c=db.rawQueryCoustom("SELECT order_number, order_date_time, outlet_id, gross_value, cash_received, credit_amount, market_id,discount_value,total_vat,total_discount FROM order_table where order_date>="+"'"+FromDate+"'"+" and order_date <="+"'"+ToDate+"' and status='1' ORDER BY _id DESC");
       Log.e("Query","SELECT memo_number, memo_date_time, outlet_id, gross_value, cash_received, credit_amount, market_id,discount_value FROM memos where memo_date>="+"'"+FromDate+"'"+" and memo_date <="+"'"+ToDate+"' ORDER BY _id DESC");
        String memo_no="";
        if (c!= null && c.getCount()>0)
        {
            if(c.moveToFirst())
            {
                do{

                    memo_no = c.getString(0);
                    String date=c.getString(1);
                    String outlet_id=c.getString(2);
                    Double gross_value=c.getDouble(3);
                    Double cash_received=c.getDouble(4);
                    Double credit_amount=c.getDouble(5);
                    String market_id=c.getString(6);
                     TempData.DISCOUNT=c.getDouble(7);
                     TempData.VAT=c.getDouble(8);

                    Cursor c2 = db.rawQueryCoustom("SELECT O.outlet_name, M.market_name FROM outlets O LEFT JOIN markets M ON(O.market_id=M.market_id) WHERE O.outlet_id='"+outlet_id+"'");
                    if (c2 != null) {
                        if (c2.moveToFirst()) {
                            do {


                                TempData.OutletName = c2.getString(0);
                                TempData.tempMarket = c2.getString(1);


                            } while (c2.moveToNext());
                        }
                    }

                    Cursor c3 = db.rawQueryCoustom("SELECT T.thana_name FROM markets M LEFT JOIN thana T ON(M.thana_id=T.thana_id) WHERE M.market_id='"+market_id+"'");
                    if (c3 != null) {
                        if (c3.moveToFirst()) {
                            do {

                                TempData.tempThana = c3.getString(0);

                            } while (c3.moveToNext());
                        }
                    }

                    Cursor c4=db.rawQueryCoustom("SELECT OC.outlet_category_name FROM outlets O LEFT JOIN outlet_categories OC ON (O.outlet_category_id = OC.outlet_category_id) where O.outlet_id='"+outlet_id+"'");
                    if(c4.getCount()>0) {
                        if (c4.moveToFirst()) {
                            do {

                                String outlet_category_name = c4.getString(0);
                                savePreference("OutletCategoryName", outlet_category_name);


                            }while(c4.moveToNext());
                        }
                    }

                    date=date.substring(0, date.length()-3);
                    StringBuffer receiptHeadBuffer = new StringBuffer();
                    receiptHeadBuffer.append("\n");

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
                    receiptHeadBuffer.append(TempData.OutletName + " " + "(" + getPreference("OutletCategoryName") + ")");

                    receiptHeadBuffer.append("\n");
                    SpannableStringBuilder receiptHeadBufferBody = new SpannableStringBuilder();
                    receiptHeadBuffer.append(TempData.tempMarket + ", " + TempData.tempThana);
                    receiptHeadBuffer.append("\n");

                    receiptHeadBuffer.append("Order# " + memo_no + ", " + date);
                    receiptHeadBuffer.append("\n................................................................");
                    //receiptHeadBuffer.append("\n");

                    String value = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", "Items","vat", "Price", "Qty", "Total");
                    receiptHeadBuffer.append(value);
                    receiptHeadBuffer.append("\n................................................................");


//                    String  MemoDetails="SELECT product_id, quantity, price FROM memo_details WHERE memo_no='"+memo_no+"'";
                    String  MemoDetails="SELECT MD.product_id,P.product_name ,MD.quantity, MD.price,MD.vat,MD.discount_type,MD.discount_amount FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='"+memo_no+"' and MD.product_type='0'";
//                    Cursor c3 = db.rawQuery("SELECT T.thana_name FROM markets M LEFT JOIN thana T ON(M.market_id=T.market_id) WHERE market_id='"+market_id+"'");
                    Log.e("MemoDetails","MemoDetails: "+MemoDetails);
                    discount_info="";
                    Cursor cursor=db.rawQueryCoustom(MemoDetails);
                    if(cursor!=null)
                    {
                        if(cursor.moveToFirst())
                        {
                            do{
                                discount_info = discount_info+getdiscount(cursor.getDouble(2),cursor.getDouble(3),cursor.getInt(5),cursor.getDouble(6),cursor.getString(1));

                                String product_id = cursor.getString(0);
                                String product_name = cursor.getString(1);
                                String quantity = cursor.getString(2);
                                String price = cursor.getString(3);
                                String vat = cursor.getString(4);
                                if (TextUtils.isEmpty(vat)){
                                    vat="0.0%";
                                }
                                String total_price=roundTwoDecimals(cursor.getDouble(2)*cursor.getDouble(3));

                                Log.e("productname22",product_name);
                                if(product_name.length()<=21)
                                {
                                    String value1 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", product_name,vat, price, quantity,total_price);
                                    receiptHeadBuffer.append(value1);
                                }
                                else
                                {
                                    String firstPart=product_name.substring(0,21);
                                    String secondPart=product_name.substring(21,product_name.length());
                                    String value1 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", firstPart,vat, price, quantity, total_price);
                                    receiptHeadBuffer.append(value1);

                                    String value2 = String.format("%1$-21s %2$7s %3$10s %4$8s  %5$13s", secondPart, "","", "", "");
                                    receiptHeadBuffer.append(value2);
                                }
                                if (!cursor.isLast())
                                receiptHeadBuffer.append("\n");

                            }while(cursor.moveToNext());
                        }
                    }

                    String Gift1="";
                    String  MemoDetailsForGift="SELECT MD.product_id,P.product_name ,MD.quantity FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='"+memo_no+"' and MD.product_type='1'";
                    Log.e("MemoDetailsForGift","MemoDetailsForGift: "+MemoDetailsForGift);
                    Cursor cur2=db.rawQueryCoustom(MemoDetailsForGift);
                    if(cur2!=null)
                    {
                        if(cur2.moveToFirst())
                        {
                            do {
                                String product_id = cur2.getString(0);
                                String product_name = cur2.getString(1);
                                String quantity = cur2.getString(2);
                                String Gift2= product_name+"("+quantity+")";
                                Gift1 = Gift1+","+Gift2;
                               String Gift = Gift1.substring(1,Gift1.length());
                                TempData.TempGift = Gift;

                            }while(cur2.moveToNext());
                        }
                    }


                    String Bonus1="",Bonus="";
                    String  MemoDetailsForBonus="SELECT MD.product_id,P.product_name ,MD.quantity FROM ORDER_DETAILS MD LEFT JOIN product P ON(MD.product_id=P.product_id) WHERE MD.order_number='"+memo_no+"' and MD.product_type='2'";
                    Log.e("MemoDetailsForBonus","MemoDetailsForBonus: "+MemoDetailsForBonus);
                    Cursor cur=db.rawQueryCoustom(MemoDetailsForBonus);
                    if(cur!=null)
                    {
                        if(cur.moveToFirst())
                        {
                            do {
                                String product_id = cur.getString(0);
                                String product_name = cur.getString(1);
                                String quantity = cur.getString(2);
                                String Bonus2= product_name+"("+quantity+")";
                                Bonus1 = Bonus1+","+Bonus2;
                                Bonus = Bonus1.substring(1,Bonus1.length());
                                TempData.TempBonus_EN = Bonus;

                            }while(cur.moveToNext());
                        }
                    }
                    if((TempData.TempGift !="Nill"&& TempData.TempGift!="")|| TempData.TempBonus_EN !="")
                        receiptHeadBuffer.append("\n................................................................");



                    if(TempData.TempGift !="Nill"&& TempData.TempGift!="")
                    {
                        receiptHeadBuffer.append("\n"+String.format("%1$-38s", "Gift:"+ TempData.TempGift));
                    }
                    if(!discount_info .equalsIgnoreCase("")&& discount_info.length()>0)
                    {
                        receiptHeadBuffer.append("\n\n"+String.format("%1$-38s", "Discount:"+ discount_info));
                    }
                    if(TempData.TempBonus_EN !="")
                        receiptHeadBuffer.append("\n"+String.format("%1$-38s", "Bonus:"+ TempData.TempBonus_EN));

                    Log.e("BUFFER1 AFTER APPEND:", ""+receiptHeadBuffer.toString());

                    receiptHeadBuffer.append("\n................................................................");


                    StringBuffer receiptHeadBuffer1 = new StringBuffer();

                    receiptHeadBuffer1.append(""+String.format("%1$-30s %2$4s %3$12s  %4$10s", "","","Total Bill:",String.valueOf(roundTwoDecimals(gross_value))));


                    receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                            "Discount:", roundTwoDecimals(TempData.DISCOUNT)));
                    receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                            "Vat:", roundTwoDecimals(TempData.VAT)));
                    receiptHeadBuffer1.append("\n" + String.format("%1$-30s %2$4s %3$12s  %4$10s", "", "",
                            "Net-Payable:", roundTwoDecimals(gross_value- TempData.DISCOUNT)));


                    receiptHeadBuffer1.append("\n\n"+getPreference("sr_name")+"  DB:("+getPreference("db_name")+")");
                    receiptHeadBuffer1.append("\n"+"Sales Officer");
                    receiptHeadBuffer1.append("\n"+"* Product(s) price are SD free & including VAT");
                    receiptHeadBuffer1.append("\n"+"Thank You!");
                    receiptHeadBuffer1.append("\n\n\n");


                    appContent = receiptHeadBuffer.toString();
                    bottomContent = receiptHeadBuffer1.toString();
                    String value3 = String.format("%1$-11s %2$10s %3$11s  %4$25s","Office Copy"," "," ","Mushak - 6.3");
                    String value4 = String.format("%1$-11s %2$10s %3$11s  %4$25s","Outlet Copy"," "," ","Mushak - 6.3");

                    String AllPrint2 = value3+appContent+receiptHeadBufferOutlate.toString()+receiptHeadBufferBody.toString()+bottomContent;
                    String AllPrint3 = value4+appContent+receiptHeadBufferOutlate.toString()+receiptHeadBufferBody.toString()+bottomContent;

                    arrayList.add(AllPrint2);
                    arrayList.add(AllPrint3);
                     AllPrint = AllPrint+AllPrint2;
                     Log.e("print",print+"");

                    TextView txtText=(TextView)findViewById(R.id.txtText);
//                    txtText.setText(printcontent+bottomContent);
                    txtText.setText(AllPrint);

                    /*TempData.printTopContent=printcontent;
                    TempData.printBottomContent=bottomContent;*/
                    TempData.AllprintName="AllprintName";
                    TempData.AllprintValue = AllPrint;

//                    TempData.INVOICE_DETAILS.clear();
                    TempData.TempGift="";
                    TempData.TempBonus_EN ="";
                    TempData.TempExtraBonus="";

                }while(c.moveToNext());
            }
        }

    }

    private String getdiscount(double qty, double price, int distype, double disamount, String product_name) {

        if (disamount<=0){
            return "";
        }
        Double  discount = 0.0;

        Log.e("discount_type", distype+"");

        if (distype == 0) {

            discount = (price / 100.00) * disamount;

        } else {
            discount = disamount;

        }

        return product_name+"("+(roundTwoDecimals(discount*qty))+")";

    }

    public String roundTwoDecimals(double d)
    {

        Log.e("Double:", String.valueOf(d));
        Log.e("Modulus:", String.valueOf(d%1));
        return String.format("%.2f", d);
    }
    public static String rightPadding(String str, int num) {
        return String.format("%1$-" + num + "s", str);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PrintAllMemosActivityP.this, DeliveryReport.class));
        finish();
    }

    private void LogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PrintAllMemosActivityP.this);
        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setNegativeButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //startActivity(new Intent(PrintActivity.this,Login.class));
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
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


    public String getCurrentDateTime1()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = new Date();
        String CurrentDate =dateFormat.format(date);

        return CurrentDate;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)


            return false;
        return false;

    }
}


