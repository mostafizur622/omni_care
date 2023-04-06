package com.srapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForSalesReport;
import com.srapp.Adapter.OrderAdapter;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.DBListener;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Model.Order_Report;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.srapp.Db_Actions.Tables.OUTLETS;
import static com.srapp.Db_Actions.Tables.OUTLETS_CATAGORY_ID;
import static com.srapp.Db_Actions.Tables.OUTLET_CATAGORY;
import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_Report_Activity extends AppCompatActivity implements View.OnClickListener, BasicFunctionListener, DBListener {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String[] SPINNERLIST = {"Rahim Store", "Jamal Electronics", "Jakir Pharmacy"};
    ListView listview;
    Boolean isLoading = false;
    int pageIndex=0;
    private final int THREAT_SHOT = 2;
    Button startdate, enddate,search;
    HashMap<String, ArrayList<String>> outlate = new HashMap<>();
    HashMap<String, ArrayList<String>> outlet_catagary = new HashMap<>();
    ListView Order_Report_recycleView;
    Data_Source db;
    BasicFunction bf;
    Spinner outlatesp, outlet_catagary_spinner;
    ArrayList<HashMap<String, String>> list;
    boolean onResunme=false;
    boolean onstart=true;
    AdapterForSalesReport adapter;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV  ;
    TextView txtTotalAmount;
    TextView TotalEC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__report_);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        search = findViewById(R.id.search);
         txtTotalAmount=findViewById(R.id.TotalAmount);
         TotalEC=findViewById(R.id.TotalEC);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        bf = new BasicFunction(this, this);
        Order_Report_recycleView = findViewById(R.id.Order_Report_recycleView);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        listview = findViewById(R.id.Order_Report_recycleView);
        list = new ArrayList<>();
        adapter = new AdapterForSalesReport(this, list);
        listview.setAdapter(adapter);
        db = new Data_Source(this,this);
        outlate = db.getAccessories(false, "00", OUTLETS, "no");
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        outlate.get(OUTLETS[3]).add(0, "All");
        outlate.get(OUTLETS[2]).add(0, "0");

        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, outlate.get(OUTLETS[3]));*/
        SpinnerAdapter arrayAdapter = new SpinnerAdapter(this, R.layout
                .spinner_item, outlate.get(OUTLETS[3]));


        outlatesp = findViewById(R.id.outlet_spinner);
        outlet_catagary_spinner = findViewById(R.id.outlet_catagary_spinner);

        outlet_catagary = db.getAccessories(false, "00", OUTLET_CATAGORY, "no");

        /*ArrayAdapter<String> arrayAdapter_catagory = new ArrayAdapter<String>(this,
                R.layout.spinner_item, outlet_catagary.get(OUTLET_CATAGORY[3]));*/

        SpinnerAdapter arrayAdapter_catagory = new SpinnerAdapter(this, R.layout
                .spinner_item, outlet_catagary.get(OUTLET_CATAGORY[3]));

        outlet_catagary_spinner.setAdapter(arrayAdapter_catagory);

        /*SpinnerAdapter arrayAdapter_catagory = new SpinnerAdapter(this, R.layout
                .spinner_item, outlet_catagary.get(OUTLET_CATAGORY[3]));*/

        outlatesp.setAdapter(arrayAdapter);
        setDateTimeField();

        bf.savePreference("outlet_id", "0");
        bf.savePreference("outlet_category_id_report", "0");


        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Order_Report_Activity.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Order_Report_Activity.this, Reports_Activity.class));
                finish();
            }
        });


        outlet_catagary_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }

                outlate.clear();
                outlate = db.getAccessories(true, outlet_catagary.get(OUTLETS_CATAGORY_ID).get(position), OUTLETS, OUTLETS_CATAGORY_ID);
                outlate.get(OUTLETS[3]).add(0, "All");
                outlate.get(OUTLETS[2]).add(0, "0");
                SpinnerAdapter arrayAdapter = new SpinnerAdapter(Order_Report_Activity.this, R.layout
                        .spinner_item, outlate.get(OUTLETS[3]));
                outlatesp.setAdapter(arrayAdapter);
                bf.savePreference("outlet_category_id_report",outlet_catagary.get(OUTLETS_CATAGORY_ID).get(position));
                /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Order_Report_Activity.this, R.layout.spinner_item, outlate.get(OUTLETS[3]));*/
                Log.e("DataView1", "outlet_catagary_spinner "+onstart);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // outlatesp.setOnItemSelectedListener()
        outlatesp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null) {
                    textView.setTextColor(getResources().getColor(R.color.background_card));
                    textView.setPadding(0, 0, 0, 0);
                }

                bf.savePreference("outlet_id", outlate.get(OUTLETS[2]).get(position));
                Log.e("DataView1", "outlatesp "+onstart);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



       // Log.e("DataView1", "oncreate");
        //DataView();


        //onstart=true;

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataView();
            }
        });

    }


    @Override
    public void onClick(View v) {

    }


    private void setDateTimeField() {
        bf.savePreference("start_Date", bf.getCurrentDate());
        bf.savePreference("end_date", bf.getCurrentDate());
        startdate.setText(bf.getCurrentDate());
        enddate.setText(bf.getCurrentDate());
        startdate.setOnClickListener(this);
        enddate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                bf.savePreference("start_Date", dateFormatter.format(newDate.getTime()));
                startdate.setText(bf.getPreference("start_Date"));
                Log.e("DataView1", "fromDatePickerDialog "+onstart);


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
                Log.e("DataView1", "toDatePickerDialog "+onstart);


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis()-5184000000l);
        toDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());


	       /* Intent idn = new Intent(SO_TargetActivity.this, SO_TargetActivity.class);
			 startActivity(idn);
			 finish();*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (onResunme) {
            DataView();
            onResunme = true;
            Log.e("DataView1", "onResume");
        }

    }


    private void  DataView() {


        if (checkDuration()){
            Toast.makeText(this,"Cannot select date more than 7 days Interval",Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("DataView1", "DataView");
        pageIndex=0;
        if (bf.isInternetOn()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("start_date", bf.getPreference("start_Date"));
                jsonObject.put("end_date", bf.getPreference("end_date"));
                jsonObject.put("outlet_id", bf.getPreference("outlet_id"));
                jsonObject.put("outlet_category_id", bf.getPreference("outlet_category_id_report"));
                jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                jsonObject.put("mac", bf.getPreference("mac"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


           // bf.getResponceData(URL.GET_LAST_RECORD, jsonObject.toString(), 101);

            ProgressDialog dailog = URL.CheckConnection(Order_Report_Activity.this,"Orders Loading...");
            if (dailog==null)
                return;
            getJAPi().GET_LAST_RECORD(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();
                        onstart=true;
                        TextView txtTotalAmount = findViewById(R.id.TotalAmount);
                        TextView TotalEC= findViewById(R.id.TotalEC);
                        txtTotalAmount.setText("0.0");
                        TotalEC.setText("0.0");

                        db.insertData(jsonObject,5);
                        


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
        else {
            onstart=true;
            setEC();
            Log.e("DataView1", "else "+onstart);
            list = db.getNotPushedOrder(bf.getPreference("start_Date"), bf.getPreference("end_date"), bf.getPreference("outlet_id"),bf.getPreference("outlet_category_id_report"),pageIndex);
            adapter = new AdapterForSalesReport(this, list);
            listview.setAdapter(adapter);
        }

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.e("data",  listview.getCount()+" "+THREAT_SHOT +" "+listview.getLastVisiblePosition()+" "+isLoading);
                if (!isLoading && listview.getCount()- THREAT_SHOT== listview.getLastVisiblePosition()){

                    pageIndex++;
                   // loadMoreData(initialPageIndex);
                    ArrayList<HashMap<String, String>> chanck_list = db.getNotPushedOrder(bf.getPreference("start_Date"), bf.getPreference("end_date"), bf.getPreference("outlet_id"),bf.getPreference("outlet_category_id_report"),pageIndex);
                    Log.e("entr scrolled..0n 402", "onScrolled: "+"On Scroll.."+chanck_list.size() );
                    if(chanck_list.size()>0) {
                        list.addAll(chanck_list);
                        adapter.notifyDataSetChanged();

                    } else {
                        isLoading = true;

                    }
                    //  Toast.makeText(MarketList.this," "+initialPageIndex,Toast.LENGTH_SHORT).show();

                    Log.e("last position", "onScrolled: "+listview.getLastVisiblePosition() );
                }
            }
        });

    }

    private void setEC() {

     HashMap<String,String> map =   db.getEcandOrderAmount(bf.getPreference("start_Date"), bf.getPreference("end_date"), bf.getPreference("outlet_id"),bf.getPreference("outlet_category_id_report"));

     txtTotalAmount.setText(map.get("memo_value"));
        TotalEC.setText(map.get("EC"));
    }

    private boolean checkDuration() {
        Date sd = convertTodate(bf.getPreference("start_Date"));
        Date ed = convertTodate(bf.getPreference("end_date"));
        long diffrence = TimeUnit.MILLISECONDS.toDays(ed.getTime()-sd.getTime());

        if (diffrence>7){
            return true;
        }


        return false;

    }

    private Date convertTodate(String start_date) {

        try {
            return dateFormatter.parse(start_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {

    }

    @Override
    public void OnConnetivityError() {

        Toast.makeText(this,"No Internet Connetion",Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( Order_Report_Activity.this, Reports_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    @Override
    public void OnLocalDBdataRetrive(String json) throws JSONException {
        if (Integer.parseInt(json)==5) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setEC();

                    list = db.getNotPushedOrder(bf.getPreference("start_Date"), bf.getPreference("end_date"), bf.getPreference("outlet_id"), bf.getPreference("outlet_category_id_report"), pageIndex);
                    adapter = new AdapterForSalesReport(Order_Report_Activity.this, list);
                    listview.setAdapter(adapter);

                }
            });

        }
    }

    @Override
    public void OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList) {

    }

    @Override
    public void OnLocalDBdataRetrive(HashMap<String, String> hasmap) {

    }
}
