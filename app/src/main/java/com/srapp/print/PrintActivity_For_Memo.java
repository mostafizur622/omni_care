package com.srapp.print;

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
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.DetailsOrderReport;
import com.srapp.R;
import com.srapp.TempData;

import org.json.JSONArray;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class PrintActivity_For_Memo extends ParentActivity {
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
    ArrayList<String> productList = new ArrayList<String>();
    ArrayList<String> quantityList = new ArrayList<String>();
    ArrayList<String> PriceList = new ArrayList<String>();
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.print_main);

        Bundle b = getIntent().getExtras();
        final String FROM = b.getString("From");


        Button btnLastMemo = (Button) findViewById(R.id.btnLastMemo);

        btnLastMemo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                startActivity(new Intent(PrintActivity_For_Memo.this, DetailsOrderReport.class));
                finish();

            }
        });


        MakeText();


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
                        Toast.makeText(PrintActivity_For_Memo.this, "Please enable bluetooth!", Toast.LENGTH_LONG).show();
                    }

                }
            });

            //connect/disconnect
            mConnectBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    try {
                        connect();
                    } catch (Exception e) {
                        Toast.makeText(PrintActivity_For_Memo.this, "Please pair your mobile with bluetooth printer!", Toast.LENGTH_LONG).show();
                    }

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

        mEnableBtn.setVisibility(View.VISIBLE);
        mConnectBtn.setVisibility(View.GONE);
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

    private void printDemoContent() {
        byte[] initialtop = Printer.printfont(initialContent, FontDefine.FONT_24PX,
                FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
        byte[] top = Printer.printfont(appContent, FontDefine.FONT_24PX,
                FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
        byte[] outlate = Printer.printfont(OutlateBold, FontDefine.FONT_24PX,
                FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
        byte[] bodyb = Printer.printfont(body, FontDefine.FONT_24PX,
                FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
        byte[] bottom = Printer.printfont(bottomContent, FontDefine.FONT_24PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

        byte[] topText = Printer.printfont(appContent,
                FontDefine.FONT_24PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
        Log.e("TOP:", Arrays.toString(top));
        Log.e("TOP(String):", new String(top));
        Log.e("", "................................................");
        Log.e("BOTTOM:", Arrays.toString(bottom));
        Log.e("BOTTOM(String):", new String(bottom));
        Log.e("", "................................................");

        initialtop = removeBytes(initialtop, getByteIndex(initialtop));
        top = removeBytes(top, getByteIndex(top));
        bottom = removeBytes(bottom, getByteIndex(bottom));
        outlate = removeBytes(outlate, getByteIndex(outlate));
        bodyb = removeBytes(bodyb, getByteIndex(bodyb));


        byte[] totladata = new byte[initialtop.length + top.length + outlate.length + bodyb.length + bottom.length];
        int offset = 0;

        System.arraycopy(initialtop, 0, totladata, offset, initialtop.length);
        offset += initialtop.length;

        System.arraycopy(top, 0, totladata, offset, top.length);
        offset += top.length;

        System.arraycopy(outlate, 0, totladata, offset, outlate.length);
        offset += outlate.length;

        System.arraycopy(bodyb, 0, totladata, offset, bodyb.length);
        offset += bodyb.length;

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

    public void MakeText() {
        //date=getCurrentDateTime1();
        Data_Source db = new Data_Source(PrintActivity_For_Memo.this);
        String Query="";
      /*  if (MEMO_EDIT){*/

            Query = "SELECT memo_date_time FROM " + Tables.TABLE_NAME_MEMOS + " WHERE " + Tables.MEMOS_memo_number + "='" + TempData.memoNumber + "'";
        /*}else {

            Query = "SELECT order_date_time FROM " + Tables.TABLE_NAME_ORDER + " WHERE " + Tables.ORDER_order_number + "='" + TempData.orderNumber + "'";
        }*/

        Log.e("Query222",Query);

        Cursor c = db.rawQueryCoustom(Query);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) do {
                date = c.getString(0);
            } while (c.moveToNext());
        }

        date = date.substring(0, date.length() - 3);

        SpannableStringBuilder receiptHeadBuffer56 = new SpannableStringBuilder();
        receiptHeadBuffer56.append(" ");

        SpannableStringBuilder receiptHeadBuffer = new SpannableStringBuilder();
        receiptHeadBuffer.append("\n");
        String value3 = String.format("%1$-11s %2$10s %3$11s  %4$20s"," "," "," ","Mushak - 6.3");
        receiptHeadBuffer.append(value3);
        receiptHeadBuffer.append("\n");
        receiptHeadBuffer.append(Util.center("Government of the People's Republic of", 40));
        receiptHeadBuffer.append(Util.center("    Bangladesh.", 40));
        receiptHeadBuffer.append(Util.center("National Board of Revenue",50));
        receiptHeadBuffer.append(Util.center("VAT Challan Patra",40));
        receiptHeadBuffer.append(Util.center("SMC Enterprise Ltd.\n",40));
        receiptHeadBuffer.append(Util.center("Central BIN: 000049992-0101.\n",15));
        receiptHeadBuffer.append("Area Office: " + getPreference("office_name"));
        receiptHeadBuffer.append("\n");
        receiptHeadBuffer.append(getPreference("office_address") + ", " + getPreference("office_phone"));


        receiptHeadBuffer.append("\n");
        SpannableStringBuilder receiptHeadBufferOutlate = new SpannableStringBuilder();
        receiptHeadBufferOutlate.append(TempData.OutletName + " " + "(" + getPreference("OutletCategoryName") + ")");

        receiptHeadBufferOutlate.append("\n");
        SpannableStringBuilder receiptHeadBufferBody = new SpannableStringBuilder();
        receiptHeadBufferBody.append(TempData.tempMarket + ", " + TempData.tempThana);
        receiptHeadBufferBody.append("\n");

        receiptHeadBufferBody.append("Memo # " + TempData.memoNumber + ", " + date);
        receiptHeadBufferBody.append("\n..........................................");
        //receiptHeadBuffer.append("\n");

        String value = String.format("%1$-11s %2$8s %3$8s  %4$11s", "Items", "Price", "Qty", "Total");
        receiptHeadBufferBody.append(value);
        receiptHeadBufferBody.append("..........................................");

        Log.e("-----------", "----TempData.INVOICE_DETAILS.size()------" + TempData.INVOICE_DETAILS.size());


        if (TempData.INVOICE_DETAILS.size() == 1) {
            int i;
            for (i = 0; i < TempData.INVOICE_DETAILS.size(); i++) {
                HashMap<String, String> map = TempData.INVOICE_DETAILS.get(i);

                String price = String.valueOf(roundTwoDecimals(Double.parseDouble(map.get("price"))));
                String total_price = String.valueOf(roundTwoDecimals(Double.parseDouble(map.get("price")) * Double.parseDouble(map.get("quantity"))));

                if (map.get("product_name").length() <= 11) {
                    String value1 = String.format("%1$-11s %2$8s %3$8s  %4$11s", map.get("product_name"), price, map.get("quantity"), total_price);
                    receiptHeadBufferBody.append(value1);
                } else {
                    String firstPart = map.get("product_name").substring(0, 10);
                    String secondPart = map.get("product_name").substring(10, map.get("product_name").length());
                    String value1 = String.format("%1$-11s %2$8s %3$8s  %4$11s", firstPart, price, map.get("quantity"), total_price);
                    receiptHeadBufferBody.append(value1);

                    String value2 = String.format("%1$-11s %2$8s %3$8s  %4$11s", secondPart, "", "", "");
                    receiptHeadBufferBody.append(value2);
                }

                if (TempData.INVOICE_DETAILS.size() - 1 != i)
                    receiptHeadBufferBody.append("\n");


            }


            if ((TempData.TempGift != "Nill" && TempData.TempGift != "") || TempData.TempBonus != "" || TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n..........................................");


            if (TempData.TempGift != "Nill" && TempData.TempGift != "") {
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Gift:" + TempData.TempGift));
            }
            if (TempData.TempBonus != "" && TempData.TempExtraBonus.equals(""))
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempBonus));

            if (TempData.TempBonus.equals("") && TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempExtraBonus));

            if (TempData.TempBonus != "" && TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempBonus + "," + TempData.TempExtraBonus));

            Log.e("BUFFER1 AFTER APPEND:", "" + receiptHeadBuffer.toString());


            receiptHeadBufferBody.append("\n..........................................");


            SpannableStringBuilder receiptHeadBuffer1 = new SpannableStringBuilder();

            String total = String.valueOf(roundTwoDecimals(Double.parseDouble(TempData.InvoiceTotal)));

            receiptHeadBuffer1.append("" + String.format("%1$-3s %2$3s %3$2s  %4$10s", "", "",
                    "Total Bill:", total));
            receiptHeadBuffer1.append("" + String.format("%1$-6s %2$3s %3$6s  %4$10s", "", "",
                    "Discount:", roundTwoDecimals(TempData.DISCOUNT)));
            receiptHeadBuffer1.append("" + String.format("%1$-5s %2$3s %3$9s  %4$10s", "", "",
                    "Sub-Total:", roundTwoDecimals(Double.parseDouble(total)- TempData.DISCOUNT)));


            SpannableStringBuilder receiptHeadBuffer3 = new SpannableStringBuilder();
            receiptHeadBuffer3.append("\n\n"+"Sales Representative");
            receiptHeadBuffer3.append("\n"+getPreference("sr_name")+"  DB:("+getPreference("db_name")+")");
            receiptHeadBuffer3.append("\nAddress: "+getPreference("db_address"));
            receiptHeadBuffer3.append("\nMobile No: "+getPreference("db_mobile"));
            receiptHeadBuffer3.append("\n\n" + "Thank You!");
            receiptHeadBuffer3.append("\n\n\n\n");

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
            TempData.PrintOutlate = OutlateBold;
            TempData.PrintBody = body;
            TempData.printBottomContent = bottomContent;
            TempData.printName = "memo";
            TempData.AllprintName = "memo";
            TempData.printboldthank = "thanku";
            TempData.printThanku = boldthanku;
            Log.e("Tetst", TempData.TempBonus);
            TempData.INVOICE_DETAILS.clear();
            TempData.TempGift = "";
            TempData.TempBonus = "";
            TempData.TempExtraBonus = "";
            Log.e("length", TempData.printThanku.getBytes().length + "");


        } else {
            int i;
            for (i = 0; i < TempData.INVOICE_DETAILS.size(); i++) {
                HashMap<String, String> map = TempData.INVOICE_DETAILS.get(i);

                String price = String.valueOf(roundTwoDecimals(Double.parseDouble(map.get("price"))));
                String total_price = String.valueOf(roundTwoDecimals(Double.parseDouble(map.get("price")) * Double.parseDouble(map.get("quantity"))));

                if (map.get("product_name").length() <= 11) {
                    String value1 = String.format("%1$-11s %2$8s %3$8s  %4$11s", map.get("product_name"), price, map.get("quantity"), total_price);
                    receiptHeadBufferBody.append(value1);
                } else {
                    String firstPart = map.get("product_name").substring(0, 10);
                    String secondPart = map.get("product_name").substring(10, map.get("product_name").length());
                    String value1 = String.format("%1$-11s %2$8s %3$8s  %4$11s", firstPart, price, map.get("quantity"), total_price);
                    receiptHeadBufferBody.append(value1);

                    String value2 = String.format("%1$-11s %2$8s %3$8s  %4$11s", secondPart, "", "", "");
                    receiptHeadBufferBody.append(value2);
                }


                if (TempData.INVOICE_DETAILS.size() - 1 != i)
                    receiptHeadBufferBody.append("\n");

            }

            if ((TempData.TempGift != "Nill" && TempData.TempGift != "") || TempData.TempBonus != "" || TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n..........................................");


            Log.e("Tetst", TempData.TempBonus + "no data");
            if (TempData.TempGift != "Nill" && TempData.TempGift != "") {
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Gift:" + TempData.TempGift));
            }
            if (TempData.TempBonus != "" && TempData.TempExtraBonus.equals("")) {
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempBonus));
                TempData.TempBonus = "";
            }


            if (TempData.TempBonus.equals("") && TempData.TempExtraBonus != "")
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempExtraBonus));

            if (TempData.TempBonus != "" && TempData.TempExtraBonus != "") {
                receiptHeadBufferBody.append("\n" + String.format("%1$-38s", "Bonus:" + TempData.TempBonus + "," + TempData.TempExtraBonus));
                TempData.TempBonus = "";
            }

            Log.e("BUFFER12 AFTER APPEND:", "" + receiptHeadBufferBody.toString());


            receiptHeadBufferBody.append("\n..........................................");


            SpannableStringBuilder receiptHeadBuffer1 = new SpannableStringBuilder();

            String total1 = String.valueOf(roundTwoDecimals(Double.parseDouble(TempData.InvoiceTotal)));

            receiptHeadBuffer1.append("" + String.format("%1$-3s %2$3s %3$2s  %4$10s", "", "",
                    "Total Bill:", total1));
            receiptHeadBuffer1.append("" + String.format("%1$-6s %2$3s %3$6s  %4$10s", "", "",
                    "Discount:", roundTwoDecimals(TempData.DISCOUNT)));
            receiptHeadBuffer1.append("" + String.format("%1$-5s %2$3s %3$9s  %4$10s", "", "",
                    "Sub-Total:", roundTwoDecimals(Double.parseDouble(total1)- TempData.DISCOUNT)));


            SpannableStringBuilder receiptHeadBuffer3 = new SpannableStringBuilder();
            receiptHeadBuffer3.append("\n\n" + "Thank You!");
            receiptHeadBuffer3.append("\n\n\n\n");

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
            TempData.TempBonus = "";
            TempData.TempExtraBonus = "";
            Log.e("Tetst", TempData.TempBonus + "no data");
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
        startActivity(new Intent(PrintActivity_For_Memo.this, DetailsOrderReport.class));
        finish();
    }

    private void LogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PrintActivity_For_Memo.this);
        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //startActivity(new Intent(PrintActivity_For_Memo.this,Login.class));
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
}
	
