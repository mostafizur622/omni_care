
package com.srapp;

import android.app.DatePickerDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.srapp.Adapter.AdapterForMemoReport;
        import com.srapp.Adapter.AdapterForSalesReport;
        import com.srapp.Adapter.SpinnerAdapter;
        import com.srapp.Db_Actions.Data_Source;
        import com.srapp.Db_Actions.Tables;
        import com.srapp.Db_Actions.URL;
        import com.tanvir.BasicFun.BasicFunction;
        import com.tanvir.BasicFun.BasicFunctionListener;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Locale;
        import java.util.concurrent.TimeUnit;

        import androidx.appcompat.app.AppCompatActivity;

        import org.json.JSONException;
        import org.json.JSONObject;

        import static com.srapp.Db_Actions.Tables.OUTLETS;
        import static com.srapp.Db_Actions.Tables.OUTLETS_CATAGORY_ID;
        import static com.srapp.Db_Actions.Tables.OUTLET_CATAGORY;
        import static com.srapp.Db_Actions.Tables.SR_ID;

public class MemoReport extends AppCompatActivity implements View.OnClickListener, BasicFunctionListener {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String[] SPINNERLIST = {"Rahim Store", "Jamal Electronics", "Jakir Pharmacy"};
    Button startdate, enddate;
    HashMap<String, ArrayList<String>> outlate = new HashMap<>();
    HashMap<String, ArrayList<String>> outlet_catagary = new HashMap<>();
    ListView listview;
    Data_Source db;
    BasicFunction bf;
    Spinner outlatesp, outlet_catagary_spinner;
    ArrayList<HashMap<String, String>> list;
    boolean onResunme=false;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV  ;
    boolean onstart=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_report);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( MemoReport.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( MemoReport.this, Reports_Activity.class));
                finish();
            }
        });
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        bf = new BasicFunction(this, this);
        listview = findViewById(R.id.Order_Report_recycleView);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        list = new ArrayList<>();
        db = new Data_Source(this);
        outlate = db.getAccessories(false, "00", OUTLETS, "no");
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        outlate.get(OUTLETS[3]).add(0, "All");
        outlate.get(OUTLETS[2]).add(0, "0");

        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, outlate.get(OUTLETS[3]));*/

        outlatesp = findViewById(R.id.outlet_spinner);


        outlet_catagary_spinner = findViewById(R.id.outlet_catagary_spinner);

        outlet_catagary = db.getAccessories(false, "00", OUTLET_CATAGORY, "no");

        ArrayAdapter<String> arrayAdapter_catagory = new ArrayAdapter<String>(this,
                R.layout.spinner_item, outlet_catagary.get(OUTLET_CATAGORY[3]));
        outlet_catagary_spinner.setAdapter(arrayAdapter_catagory);

        SpinnerAdapter arrayAdapter = new SpinnerAdapter(this, R.layout
                .spinner_item, outlate.get(OUTLETS[3]));
        outlatesp.setAdapter(arrayAdapter);

        setDateTimeField();

        bf.savePreference("outlate_id", "0");
        bf.savePreference("outlet_category_id_mreport", "0");

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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MemoReport.this, R.layout.spinner_item, outlate.get(OUTLETS[3]));
                outlatesp.setAdapter(arrayAdapter);
                bf.savePreference("outlet_category_id_mreport",outlet_catagary.get(OUTLETS_CATAGORY_ID).get(position));
                if (onstart) {
                    DataView();
                    onstart=false;
                }
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
                bf.savePreference("outlate_id", outlate.get(OUTLETS[2]).get(position));
                if (onstart) {
                    DataView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listview = findViewById(R.id.Order_Report_recycleView);


        DataView();
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
                DataView();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        toDatePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            bf.savePreference("end_date", dateFormatter.format(newDate.getTime()));
            enddate.setText(bf.getPreference("end_date"));
            DataView();

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


	       /* Intent idn = new Intent(SO_TargetActivity.this, SO_TargetActivity.class);
			 startActivity(idn);
			 finish();*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (onResunme)
            DataView();
        onResunme=true;

    }

    private void DataView() {

        Log.e("DataView", "DataView");

        if (bf.isInternetOn()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("start_date", bf.getPreference("start_Date"));
                jsonObject.put("end_date", bf.getPreference("end_date"));
                jsonObject.put("outlet_id", bf.getPreference("outlate_id"));
                jsonObject.put("outlet_category_id", bf.getPreference("outlet_category_id_mreport"));
                jsonObject.put(SR_ID, bf.getPreference(SR_ID));
                jsonObject.put("mac", bf.getPreference("mac"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            bf.getResponceData(URL.GET_LAST_MEMO, jsonObject.toString(), 101);
        }else {
            TextView txtTotalAmount = findViewById(R.id.TotalAmount);
            TextView TotalEC= findViewById(R.id.TotalEC);
            txtTotalAmount.setText("0.0");
            TotalEC.setText("0.0");
            list = db.getMemos(bf.getPreference("start_Date"), bf.getPreference("end_date"), bf.getPreference("outlate_id"), bf.getPreference("outlet_category_id_mreport"));
            AdapterForMemoReport adapter = new AdapterForMemoReport(this, list);
            listview.setAdapter(adapter);
        }




    }

    @Override
    public void OnServerResponce(final JSONObject jsonObject, int i) {
        onstart=true;
        TextView txtTotalAmount = findViewById(R.id.TotalAmount);
        TextView TotalEC= findViewById(R.id.TotalEC);
        txtTotalAmount.setText("0.0");
        TotalEC.setText("0.0");
        Log.e("MemoJsonResponse",jsonObject.toString());
        db.updateMemoWithServer(jsonObject);
       /* if (i!=11)
        bf.getResponceData(URL.Log,jsonObject.toString(),11);*/

        list = db.getMemos(bf.getPreference("start_Date"), bf.getPreference("end_date"), bf.getPreference("outlate_id"), bf.getPreference("outlet_category_id_mreport"));
        AdapterForMemoReport adapter = new AdapterForMemoReport(this, list);
        listview.setAdapter(adapter);
    }

    @Override
    public void OnConnetivityError() {

        Toast.makeText(this,"No Internet Connetion",Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( MemoReport.this, Reports_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
