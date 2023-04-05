package com.srapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Toast;

import com.srapp.Adapter.AdapterForGiftIssueList;
import com.srapp.Db_Actions.Data_Source;
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

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiftIssueList extends AppCompatActivity implements BasicFunctionListener {

    ImageView homeBtn,backBtn;
    BasicFunction bf;
    Button startdate, enddate,newgift;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    boolean onstart =false;
    Spinner RouteSp, MarketSp;

    ArrayList<String> RouteID=new ArrayList<String>();
    ArrayList<String> RouteName=new ArrayList<String>();
    ArrayList<HashMap<String,String>> list=new ArrayList<>();
    String _RouteID;
    Data_Source db;
    boolean apicall = true;

    ArrayList<String> MarketID=new ArrayList<String>();
    ArrayList<String> MarketName=new ArrayList<String>();
    String _MarketID;
    ListView list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_issue_list);
        startdate = findViewById(R.id.start_date);
        bf = new BasicFunction(this,this);
        db = new Data_Source(this);
        newgift = findViewById(R.id.newgift);
        enddate = findViewById(R.id.end_date);
        startdate = findViewById(R.id.start_date);
        list = new ArrayList<>();
        enddate = findViewById(R.id.end_date);
        list_view = findViewById(R.id.list_view);
        RouteSp = (Spinner)findViewById(R.id.ThanaSp);
        MarketSp = (Spinner)findViewById(R.id.MarketSp);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( GiftIssueList.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( GiftIssueList.this, Tools.class));
                finish();
            }
        });
        newgift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( GiftIssueList.this, GiftIssueActivity.class);
                startActivity(i);
                finish();

            }
        });

        setDateTimeField();
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

        ThanaParse();

        RouteSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                _RouteID = RouteID.get(arg2);
                String name = RouteName.get(arg2);
                MarketParse(_RouteID);
                DataView();
                //BonusPartyReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        MarketSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                _MarketID = MarketID.get(arg2);

                DataView();
                //OutletParse(_MarketID);
                //  BonusPartyReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });


    }


    private void ThanaParse()
    {
        RouteID.clear();
        RouteName.clear();
        Cursor c = db.rawQueryCoustom("SELECT * FROM route");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Thana_Id = c.getString(c.getColumnIndex("route_id"));
                    String Thana_Name = c.getString(c.getColumnIndex("route_name"));

                    RouteID.add(Thana_Id);
                    RouteName.add(Thana_Name);

                } while (c.moveToNext());
            }

            RouteName.add(0,"All");
            RouteID.add(0,"0");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GiftIssueList.this,R.layout.spinner_text, RouteName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            RouteSp.setAdapter(dataAdapter);

        }

    }

    private void MarketParse(String Thana_ID)
    {
        MarketID.clear();
        MarketName.clear();
        MarketID.add("0");
        MarketName.add("All");
        Cursor c = db.rawQueryCoustom("SELECT * FROM markets where route_id='"+Thana_ID+"'   ORDER BY market_name COLLATE NOCASE ASC");
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Market_Id = c.getString(c.getColumnIndex("market_id"));
                    String Market_Name = c.getString(c.getColumnIndex("market_name"));

                    MarketID.add(Market_Id);
                    MarketName.add(Market_Name);

                    Log.e("MarketID", Market_Id);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GiftIssueList.this,R.layout.spinner_text, MarketName);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            MarketSp.setAdapter(dataAdapter);
        }

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
                jsonObject.put("start_date", startdate.getText().toString().trim());
                jsonObject.put("end_date", enddate.getText().toString().trim());
                jsonObject.put("route_id", _RouteID);
                jsonObject.put("market_id", _MarketID);
                jsonObject.put("so_id", bf.getPreference(SR_ID));
                jsonObject.put("mac", bf.getPreference("mac"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (apicall) {
               // bf.getResponceData(URL.GIFT_ITEM_LIST, jsonObject.toString(), 101);

                ProgressDialog dailog = CheckConnection(GiftIssueList.this,"Gift Issue List...");
                if (dailog==null)
                    return;
                getJAPi().GIFT_ITEM_LIST(convertTORequestdata(jsonObject)).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            dailog.dismiss();
                            apicall=true;
                            onstart=true;
                            ArrayList<HashMap<String,String>> list = new ArrayList<>();
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("giftitem_list");
                                for (int i=0 ; i<jsonArray.length() ; i++){
                                    HashMap<String,String> map = new HashMap<>();
                                    map.put("date",jsonArray.getJSONObject(i).getString("date"));
                                    map.put("outlet",jsonArray.getJSONObject(i).getString("outlet"));
                                    map.put("id",jsonArray.getJSONObject(i).getString("id"));
                                    map.put("is_editable",jsonArray.getJSONObject(i).getString("is_editable"));

                                    list.add(map);


                                }


                                AdapterForGiftIssueList adapter = new AdapterForGiftIssueList(GiftIssueList.this,list);
                                list_view.setAdapter(adapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
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
                apicall=false;
            }
        } else {
            Toast.makeText(this, "NO Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( GiftIssueList.this, Tools.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int RequestCode) {


    }

    @Override
    public void OnConnetivityError() {

    }
}
