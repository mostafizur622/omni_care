package com.srapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bxl.config.editor.BXLConfigLoader;
import com.google.gson.Gson;
import com.srapp.Adapter.AdapterForProductOrderWiseSummery;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.print.SummeryPrintActivity;
import com.srapp.print.Util;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import NewPrint.BixolonPrinter;

import static com.srapp.Db_Actions.Tables.SR_ID;

public class Product_Wise_Order_Summery extends AppCompatActivity implements BasicFunctionListener {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    ListView listview;
    Button startdate, enddate;
    BasicFunction bf;
    String Url = "";
    boolean onstart;
    ImageView homeBtn, backBtn;
    String report_name;
    ImageButton print;
    ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__wise__order__summery);
        startdate = findViewById(R.id.start_date);
        enddate = findViewById(R.id.end_date);
        homeBtn = findViewById(R.id.home);
        print = findViewById(R.id.print);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        listview = findViewById(R.id.list_view);
        list = new ArrayList<>();
        backBtn = findViewById(R.id.back);
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        TextView userIdTV = findViewById(R.id.user_txt_view);
        TextView titleTV = findViewById(R.id.title_tv);
        if (getIntent() != null) {
            titleTV.setText(getIntent().getStringExtra("report_name"));
            if (getIntent().getIntExtra("flag", 0) == 1) {
                Url = URL.PRODUCT_WISE_SALES_SUMMERY_Invoece;
                report_name = "Product Wise Summery Report Invoice";
                TempData.summeryReportName=report_name;
            } else if (getIntent().getIntExtra("flag", 0) == 2) {
                Url = URL.PRODUCT_WISE_SALES_SUMMERY_DELIVERy;
                TempData.summeryReportName=report_name;
                report_name = "Product Wise Summery Report Delivery";
            } else if (getIntent().getIntExtra("flag", 0) == 3) {
                Url = URL.PRODUCT_WISE_SALES_SUMMERY_Order;
                report_name = "Product Wise Summery Report Order";
                TempData.summeryReportName=report_name;
            }
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Wise_Order_Summery.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Wise_Order_Summery.this, Reports_Activity.class));
                finish();
            }
        });

        bf = new BasicFunction(this, this);
        setDateTimeField();
        DataView();
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Product_Wise_Order_Summery.this, SummeryPrintActivity.class));
                finish();
                //  printReport();
            }
        });
    }

    private void printReport() {
        int portType = BXLConfigLoader.DEVICE_BUS_USB;
        String logicalName = "SRP-E302";
        String address = "";
        BixolonPrinter printer;
        boolean print = false;
        printer = new BixolonPrinter(this);
        print = printer.printerOpen(portType, logicalName, address, true);
        printer.printText(MakeReportText(), 1, 2, (0 + 1));

    }

    private String MakeReportText() {
        StringBuffer receiptHeadBuffer = new StringBuffer();
        String value = String.format("%1$-20s   %2$18s  %3$20s", "Start Date:" + startdate.getText().toString(), " ", "End Date:" + enddate.getText().toString());
        receiptHeadBuffer.append(value);
        receiptHeadBuffer.append("\n");
        receiptHeadBuffer.append(Util.center(report_name, 64));
        receiptHeadBuffer.append(Util.center("SMC Enterprise Ltd.", 64));


        receiptHeadBuffer.append("\n................................................................");
        //receiptHeadBuffer.append("\n");

        String value4 = String.format("%1$-21s %2$7s %3$10s %4$10s  %5$11s", "Items", "EC", "OC", "Qty", "B.Qty");
        receiptHeadBuffer.append(value4);
        receiptHeadBuffer.append("\n................................................................");
        for (int i = 0; i < list.size(); i++) {

            String product_name = list.get(i).get("product_name");
            String EC = list.get(i).get("EC");
            String OC = list.get(i).get("OC");
            String Qty = list.get(i).get("qty");
            String BQty = list.get(i).get("bqty");

            if (product_name.length() <= 25) {
                String value1 = String.format("%1$-25s %2$3s %3$10s %4$10s  %5$11s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append(value1);
            } else {
                String firstPart = product_name.substring(0, 25);
                String secondPart = product_name.substring(25, product_name.length());
                String value1 = String.format("%1$-25s %2$3s %3$10s %4$10s  %5$11s", firstPart, EC, OC, Qty, BQty);
                receiptHeadBuffer.append(value1);
                String value2 = String.format("%1$-21s %2$7s %3$10s %4$10s  %5$11s", secondPart, "", "", "", "");
                receiptHeadBuffer.append(value2);
            }
        }

        receiptHeadBuffer.append("\n................................................................");
        receiptHeadBuffer.append("\n\n" + "Sales Representative");
        receiptHeadBuffer.append("\n" + getPreference("sr_name") + "  DB:(" + getPreference("db_name") + ")");
        receiptHeadBuffer.append("\nAddress: " + getPreference("db_address"));
        receiptHeadBuffer.append("\nMobile No: " + getPreference("db_mobile"));

        receiptHeadBuffer.append("\n\n" + "Printing date: " + bf.getCurrentDateTime());
        receiptHeadBuffer.append("\n" + "Thank You!");
        receiptHeadBuffer.append("\n");


        return receiptHeadBuffer.toString();


    }

    public String getPreference(String key) {
        String value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        value = prefs.getString(key, "0");

        return value;

    }

    private void setDateTimeField() {

        bf.savePreference("start_Date", bf.getCurrentDate());
        bf.savePreference("end_date", bf.getCurrentDate());
        startdate.setText(bf.getCurrentDate());

        enddate.setText(bf.getCurrentDate());

        TempData.summeryEndDate=bf.getPreference("end_date");
        TempData.summeryStartDate=bf.getPreference("start_Date");

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("start_Date", dateFormatter.format(newDate.getTime()));
                startdate.setText(bf.getPreference("start_Date"));
                TempData.summeryStartDate=bf.getPreference("start_Date");

                if (onstart)
                    DataView();
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
        fromDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("end_date", dateFormatter.format(newDate.getTime()));
                enddate.setText(bf.getPreference("end_date"));
                TempData.summeryEndDate=bf.getPreference("end_date");
                if (onstart) {
                    DataView();
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
        toDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());

	       /* Intent idn = new Intent(SO_TargetActivity.this, SO_TargetActivity.class);
			 startActivity(idn);
			 finish();*/
    }

    private void DataView() {

        if (bf.isInternetOn()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("end_date", bf.getPreference("end_date"));
                jsonObject.put("start_date", bf.getPreference("start_Date"));
                jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                jsonObject.put("mac", bf.getPreference("mac"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("JSON", jsonObject.toString());

            bf.getResponceData(Url, jsonObject.toString(), 101);
        } else {
            Toast.makeText(this, "NO Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        list.clear();
        TempData.INVOICE_SUMMERY_PRINT.clear();
        onstart = true;
        if (i == 101) {
            try {

                JSONArray jsonArray = jsonObject.getJSONArray("product_sales_report");

                for (int j = 0; j < jsonArray.length(); j++) {

                    if (Double.parseDouble(jsonArray.getJSONObject(j).getString("total_qty")) > 0 || Double.parseDouble(jsonArray.getJSONObject(j).getString("bonus_qty")) > 0) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_name", jsonArray.getJSONObject(j).getString("product_name"));
                        map.put("EC", jsonArray.getJSONObject(j).getString("total_ec"));
                        map.put("OC", jsonArray.getJSONObject(j).getString("total_oc"));
                        map.put("qty", jsonArray.getJSONObject(j).getString("total_qty"));
                        map.put("total_price", jsonArray.getJSONObject(j).getString("total_price"));
                        map.put("bqty", jsonArray.getJSONObject(j).getString("bonus_qty"));
                        list.add(map);
                    }

                }

                Log.e("Report", new Gson().toJson(list));

                AdapterForProductOrderWiseSummery adapterForOrderSummery = new AdapterForProductOrderWiseSummery(this, list);
                listview.setAdapter(adapterForOrderSummery);
                TempData.INVOICE_SUMMERY_PRINT.addAll(list);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
            }

        }
    }

    @Override
    public void OnConnetivityError() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(Product_Wise_Order_Summery.this, Reports_Activity.class));
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}


/*
package com.srapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bxl.config.editor.BXLConfigLoader;
import com.srapp.Adapter.AdapterForProductOrderWiseSummery;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.print.PrintActivity;
import com.srapp.print.Util;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import NewPrint.BixolonPrinter;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.print.Util.saveBitMap;

public class Product_Wise_Order_Summery extends AppCompatActivity implements BasicFunctionListener {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    ListView listview;
    Button startdate, enddate;
    BasicFunction bf;
    String Url="";
    boolean onstart;
    ImageView homeBtn,backBtn;
    String report_name;
    ImageButton print, printfinal;
    ArrayList<HashMap<String,String>> list;

    // Create image-----------------------
    ProgressDialog pd;
    Button saveButton;

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__wise__order__summery);

        //Start--Create image for print android ----------------------------
        ActivityCompat.requestPermissions(Product_Wise_Order_Summery.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        pd = new ProgressDialog(Product_Wise_Order_Summery.this);

        //---------End-------------------------------------------------------

        startdate = findViewById(R.id.start_date);
        enddate = findViewById(R.id.end_date);
        homeBtn = findViewById(R.id.home);
        print = findViewById(R.id.print);
        printfinal = findViewById(R.id.printfinal);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        listview = findViewById(R.id.list_view);
        list = new ArrayList<>();
        backBtn = findViewById(R.id.back);
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        TextView userIdTV = findViewById(R.id.user_txt_view);
        TextView titleTV = findViewById(R.id.title_tv);
        if (getIntent()!=null){
            titleTV.setText(getIntent().getStringExtra("report_name"));
            if (getIntent().getIntExtra("flag",0)==1){
                Url = URL.PRODUCT_WISE_SALES_SUMMERY_Invoece;
                report_name="Product Wise Summery Report Invoice";
            }else if (getIntent().getIntExtra("flag",0)==2){
                Url = URL.PRODUCT_WISE_SALES_SUMMERY_DELIVERy;
                report_name="Product Wise Summery Report Delivery";
            }else if (getIntent().getIntExtra("flag",0)==3){
                Url = URL.PRODUCT_WISE_SALES_SUMMERY_Order;
                report_name="Product Wise Summery Report Order";
            }
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Product_Wise_Order_Summery.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Product_Wise_Order_Summery.this, Reports_Activity.class));
                finish();
            }
        });
        bf = new BasicFunction(this,this);
        setDateTimeField();
        DataView();

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag==0){
                    ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                    scrollView.setVisibility(View.VISIBLE);
                    MakeReportText();
                    flag=1;
                }

                else {
                    SaveClick(); // Save converted layout image to storage
                    printReport();
                }

            }
        });


        printfinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveClick(); // Save converted layout image to storage
                printReport();
            }
        });
    }

    private void printReport() {
         int portType = BXLConfigLoader.DEVICE_BUS_USB;
         String logicalName = "SRP-E302";
         String address = "";
         BixolonPrinter printer;
        boolean print=false;
        printer= new BixolonPrinter(this);
        print =  printer.printerOpen(portType,logicalName,address,true);

       // printer.printText(MakeReportText(), 1, 2, (0 + 1)); //previous System data print

        Bitmap reportPrintBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/report.png"); // print report as a image from storage for supporting all font like bangla arabic and so on
        printer.printImage(reportPrintBitmap, 1200,2, 300);

    }


    private String MakeReportText() {


        StringBuffer receiptHeadBuffer = new StringBuffer();
        String value = String.format("%1$-20s   %2$18s  %3$20s", "Start Date:"+startdate.getText().toString()," ", "End Date:"+enddate.getText().toString());
        receiptHeadBuffer.append(value);
        receiptHeadBuffer.append("\n");
        receiptHeadBuffer.append(Util.center(report_name,64));
        receiptHeadBuffer.append(Util.center("\nSMC Enterprise Ltd.",64));

        receiptHeadBuffer.append("\n...........................................................................................");
        //receiptHeadBuffer.append("\n");

        String value4 = String.format("%1$-27s                      %2$8s   %3$8s   %4$8s    %5$8s", "Items","EC", "OC", "Qty", "B.Qty");
        receiptHeadBuffer.append("\n"+value4);
        receiptHeadBuffer.append("\n...........................................................................................\n");
        for(int i = 0 ; i<list.size() ; i++) {

            String product_name = list.get(i).get("product_name");
            String EC = list.get(i).get("EC");
            String OC = list.get(i).get("OC");
            String Qty = list.get(i).get("qty");
            String BQty = list.get(i).get("bqty");
           */
/* String value1 = String.format("%1$-27s                      %2$8s %3$8s %4$8s %5$8s", product_name, EC, OC, Qty, BQty);
            receiptHeadBuffer.append("\n"+value1);*//*


        if (product_name.length() <= 26) {
            String value1 = "";
              if (product_name.length() == 7 || product_name.length() == 8) {
                value1 = String.format("%1$-27s                      %2$8s   %3$8s   %4$8s   %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }

           else if (product_name.length() == 9 || product_name.length() == 10) {
                value1 = String.format("%1$-27s                     %2$8s   %3$8s   %4$8s   %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }

           else if (product_name.length() == 11 || product_name.length() == 12) {
                value1 = String.format("%1$-27s                    %2$8s   %3$8s   %4$8s   %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }

            else if (product_name.length() ==13 || product_name.length() ==14 || product_name.length() ==15) {
                value1 = String.format("%1$-27s                %2$8s   %3$8s   %4$8s   %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }

            else if (product_name.length() ==16 || product_name.length() ==17 || product_name.length() ==18) {
                value1 = String.format("%1$-27s               %2$8s   %3$8s   %4$8s   %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }

            else if (product_name.length() ==19 || product_name.length() ==20 || product_name.length() ==21 || product_name.length() ==22) {
                value1 = String.format("%1$-27s              %2$8s   %3$8s   %4$8s   %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }
*/
/*
            else if (product_name.length() ==19 || product_name.length() ==20) {
                value1 = String.format("%1$-27s          %2$8s %3$8s %4$8s %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }

            else if (product_name.length() ==21 || product_name.length() ==22) {
                value1 = String.format("%1$-27s        %2$8s %3$8s %4$8s %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }*//*


            else if (product_name.length() ==23 || product_name.length() ==24 || product_name.length() ==25 || product_name.length() ==26) {
                value1 = String.format("%1$-27s             %2$8s %3$8s %4$8s %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }

           */
/* else if (product_name.length() ==25 || product_name.length() ==26) {
                value1 = String.format("%1$-27s %2$8s %3$8s %4$8s %5$8s", product_name, EC, OC, Qty, BQty);
                receiptHeadBuffer.append("\n" + value1 + "\n");
            }*//*


        }

        else {
            String firstPart = product_name.substring(0, 26);
            String secondPart = product_name.substring(26, product_name.length());
            String value1 = String.format("%1$-25s        %2$10s %3$10s %4$10s %5$10s", firstPart, EC, OC, Qty, BQty);
            receiptHeadBuffer.append("\n" + value1+"\n");
            String value2 = String.format("%1$-25s        %2$10s %3$10s %4$10s %5$10s", secondPart, "", "", "", "");
            receiptHeadBuffer.append(value2);
        }
        }

        receiptHeadBuffer.append("\n................................................................");
        receiptHeadBuffer.append("\n\n"+"Sales Representative");
        receiptHeadBuffer.append("\n"+getPreference("sr_name")+"  DB:("+getPreference("db_name")+")");
        receiptHeadBuffer.append("\nAddress: "+getPreference("db_address"));
        receiptHeadBuffer.append("\nMobile No: "+getPreference("db_mobile"));

        receiptHeadBuffer.append("\n\n"+"Printing date: "+bf.getCurrentDateTime());
        receiptHeadBuffer.append("\n"+"Thank You!");
        receiptHeadBuffer.append("\n");


        TextView txtText = (TextView) findViewById(R.id.txtText);
        txtText.setText(receiptHeadBuffer.toString());


       return receiptHeadBuffer.toString();


    }

    public String getPreference(String key)
    {
        String value="";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        value = prefs.getString(key, "0");

        return value;

    }

    private void setDateTimeField() {

        bf.savePreference("start_Date", bf.getCurrentDate());
        bf.savePreference("end_date", bf.getCurrentDate());
        startdate.setText(bf.getCurrentDate());
        enddate.setText(bf.getCurrentDate());


        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("start_Date", dateFormatter.format(newDate.getTime()));
                startdate.setText(bf.getPreference("start_Date"));
                if (onstart)
                    DataView();
                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                scrollView.setVisibility(View.GONE);
                flag=0;

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                bf.savePreference("end_date", dateFormatter.format(newDate.getTime()));
                enddate.setText(bf.getPreference("end_date"));
                if (onstart) {
                    DataView();
                    ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                    scrollView.setVisibility(View.GONE);
                    flag=0;
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


	       */
/* Intent idn = new Intent(SO_TargetActivity.this, SO_TargetActivity.class);
			 startActivity(idn);
			 finish();*//*

    }

    private void DataView() {

        if (bf.isInternetOn()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("end_date",  bf.getPreference("end_date"));
                jsonObject.put("start_date", bf.getPreference("start_Date"));
                jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                jsonObject.put("mac", bf.getPreference("mac"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("JSON",jsonObject.toString());

            bf.getResponceData(Url, jsonObject.toString(), 101);
        } else {
            Toast.makeText(this, "NO Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
    list.clear();
        onstart=true;
        if (i==101){
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("product_sales_report");


                for (int j = 0 ; j<jsonArray.length(); j++ ){
                    if (Double.parseDouble(jsonArray.getJSONObject(j).getString("total_qty"))>0 || Double.parseDouble(jsonArray.getJSONObject(j).getString("bonus_qty"))>0) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_name", jsonArray.getJSONObject(j).getString("product_name"));
                        map.put("EC", jsonArray.getJSONObject(j).getString("total_ec"));
                        map.put("OC", jsonArray.getJSONObject(j).getString("total_oc"));
                        map.put("qty", jsonArray.getJSONObject(j).getString("total_qty"));
                        map.put("total_price", jsonArray.getJSONObject(j).getString("total_price"));
                        map.put("bqty", jsonArray.getJSONObject(j).getString("bonus_qty"));
                        list.add(map);
                    }

                }

                AdapterForProductOrderWiseSummery adapterForOrderSummery = new AdapterForProductOrderWiseSummery(this,list);
                listview.setAdapter(adapterForOrderSummery);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("error",e.getMessage());
            }

        }

    }


    // Create image for printing Bangla----------------------------------------------------------------------------
    private void SaveClick(){
        pd.setMessage("saving your image");
        pd.show();

        LinearLayout savingLayout =(LinearLayout)findViewById(R.id.layout_save);
        File file = saveBitMap(Product_Wise_Order_Summery.this, savingLayout);
        if (file != null) {
            pd.cancel();
            Log.i("TAG", "Drawing saved to the gallery!");
        } else {
            pd.cancel();
            Log.i("TAG", "Oops! Image could not be saved.");
        }
    }

    // Create image for printing Bangla----------------------------------------------------------------------------


    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
        Log.d("imagepath", String.valueOf(pictureFileDir));

        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save the image");
            return null;
        }

        // String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".png";
        String filename = pictureFileDir.getPath() + File.separator +"report"+".png";
        Log.d("imagename", filename);

        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
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
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
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

    @Override
    public void OnConnetivityError() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( Product_Wise_Order_Summery.this, Reports_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
*/
