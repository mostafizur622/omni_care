package com.srapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Adapter.SrProductTarget_Adapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.widget.LinearLayout.VERTICAL;

public class SrProductTarget extends AppCompatActivity implements BasicFunctionListener {
    Spinner monthSpinner;
    ImageView img;
    TextView monthPick;
    String Result;
    BasicFunction basicFunction;
    HashMap<String, ArrayList<String>> productTargetHashMap;
    ArrayList<String> productNameList;
    ArrayList<String> targetQtyList;
    ArrayList<String> targetVolList;
    ArrayList<String> achieveVolList;
    ArrayList<String> achieveQtyList;
    RecyclerView recyclerView;
    SrProductTarget_Adapter adapter;
    String monthId;
    private int initialPageIndex = 1;
    ArrayList<String> FiscalYearID =new ArrayList<String>();
    ArrayList<String> FIscalyearCode =new ArrayList<String>();
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV;
    Data_Source db ;
    Spinner fiscalyearsp;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr_product_target);
        basicFunction = new BasicFunction(this,this);
        recyclerView = findViewById(R.id.sr_product_target_rec);
        db = new Data_Source(this);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (fiscalyearsp.getSelectedItemPosition()<=0){
                    Toast.makeText(SrProductTarget.this,"Please Select Fiscal year first",Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject primaryData = new JSONObject();
                try {
                    primaryData.put("mac",basicFunction.getPreference("mac"));
                    primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                    primaryData.put("month",monthId);
                    primaryData.put("fiscal_year_id",FiscalYearID.get(fiscalyearsp.getSelectedItemPosition()));
                    //primaryData.put("page",initialPageIndex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("json format", "onDateSet: "+primaryData );

                basicFunction.getResponceData(URL.ProductTarget,primaryData.toString(),901);
            }
        });
        userIdTV = findViewById(R.id.user_txt_view);
        fiscalyearsp = findViewById(R.id.fiscal);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);


        monthPick = findViewById(R.id.month_picker);

        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        Log.e("Month----->",dateFormat.format(date));

        final Calendar today = Calendar.getInstance();
        //tday.set(selectedYear,selectedMonth,20);
        today.set(Calendar.MONTH, Integer.parseInt(dateFormat.format(date))-1);
        //today.set(Calendar.MONTH,selectedMonth);
        Date selectedDay = today.getTime();
        DateFormat fmt = new SimpleDateFormat("MMMM", Locale.US);
        Log.e("month","month "+ "formated "+fmt
                .format(selectedDay));
        Log.e("---", "onCreate: SELECTED DAY-------->"+selectedDay );
        Result = fmt.format(selectedDay);
        monthId = String.valueOf(dateFormat.format(date));

        Log.e("month id------>", "onCreate: "+monthId );

        //.........For Test...........//

        /*productTargetHashMap = new HashMap<>();
        productNameList = new ArrayList<>();
        targetQtyList = new ArrayList<>();
        targetVolList = new ArrayList<>();
        achieveQtyList = new ArrayList<>();
        achieveVolList = new ArrayList<>();

        for (int i = 0; i< 20; i++){
            productNameList.add("Product "+i+1);
            targetQtyList.add(""+i*5+1);
            targetVolList.add(""+i*4+1);
            achieveQtyList.add(""+i*3+1);
            achieveVolList.add(""+i*6+1);
        }



        productTargetHashMap.put(Tables.ProductName,productNameList);
        productTargetHashMap.put(Tables.TargetQty,targetQtyList);
        productTargetHashMap.put(Tables.TargetVol,targetVolList);
        productTargetHashMap.put(Tables.AchieveQty,achieveQtyList);
        productTargetHashMap.put(Tables.AchieveVol,achieveVolList);


        adapter = new SrProductTarget_Adapter(productTargetHashMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(SrProductTarget.this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), VERTICAL));*/


        //........END TEST.............//


        monthPick.setText(Result);


        //..............api call end..................//



        monthPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getMonthYear();

            }
        });


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SrProductTarget.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( SrProductTarget.this, SR_Account_Activity.class));
                finish();
            }
        });

        FiscalyearParse();
    }
    private void FiscalyearParse()
    {
        FiscalYearID.clear();
        FIscalyearCode.clear();
        FiscalYearID.add("0");
        FIscalyearCode.add("All");

        Cursor c ;

        c= db.rawQuery("SELECT * FROM fiscal_year");



        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String Market_Id = c.getString(c.getColumnIndex("id"));
                    String Market_Name = c.getString(c.getColumnIndex("year_code"));

                    FiscalYearID.add(Market_Id);
                    FIscalyearCode.add(Market_Name);

                    Log.e("OUTLET ID", Market_Id);

                } while (c.moveToNext());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SrProductTarget.this,R.layout.spinner_text, FIscalyearCode);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fiscalyearsp.setAdapter(dataAdapter);
        }

    }
    public String getMonthYear() {
        final Calendar calendar = Calendar.getInstance();

        final Calendar tday = Calendar.getInstance();

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(SrProductTarget.this, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {

                Log.e("month","month "+selectedMonth+" Year "+selectedYear);

                //final Calendar tday = Calendar.getInstance();
                //tday.set(selectedYear,selectedMonth,20);
                tday.set(Calendar.YEAR,selectedYear);
                tday.set(Calendar.MONTH,selectedMonth);
                Date selectedDay = tday.getTime();
                DateFormat fmt = new SimpleDateFormat("MMMM", Locale.US);
                Log.e("month","month "+selectedMonth+" Year "+selectedYear+" formated "+fmt
                        .format(selectedDay));
                monthId = String.valueOf(selectedMonth+1);
                Result = fmt.format(selectedDay);
                monthPick.setText(Result);
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //selectedmonyh= sdf.format(selectedDay);

                // call api for data here
                //................ call api for this month...........//

                //..............api call end..................//


            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH));



        builder.build().show();

        return Result;
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {

        if (i == 901){
            Log.e("response", "OnServerResponce: REQ 901-------> "+jsonObject );

            // set all new here
            try {



                if (jsonObject.getJSONArray("product_targets") != null && jsonObject.getJSONArray("product_targets").length()>0) {
                    productTargetHashMap = new HashMap<>();
                    productNameList = new ArrayList<>();
                    targetQtyList = new ArrayList<>();
                    targetVolList = new ArrayList<>();
                    achieveQtyList = new ArrayList<>();
                    achieveVolList = new ArrayList<>();

                    for (int k = 0; k< jsonObject.getJSONArray("product_targets").length();k++){
                        productNameList.add(jsonObject.getJSONArray("product_targets").getJSONObject(k).getString("product_name"));
                        targetQtyList.add(jsonObject.getJSONArray("product_targets").getJSONObject(k).getString("target_qty"));
                        achieveQtyList.add(jsonObject.getJSONArray("product_targets").getJSONObject(k).getString("achieve_qty"));
                        targetVolList.add(jsonObject.getJSONArray("product_targets").getJSONObject(k).getString("target_amount"));
                        achieveVolList.add(jsonObject.getJSONArray("product_targets").getJSONObject(k).getString("achieve_amount"));
                    }


                    productTargetHashMap.put(Tables.ProductName, productNameList);
                    productTargetHashMap.put(Tables.TargetQty, targetQtyList);
                    productTargetHashMap.put(Tables.TargetVol, targetVolList);
                    productTargetHashMap.put(Tables.AchieveQty, achieveQtyList);
                    productTargetHashMap.put(Tables.AchieveVol, achieveVolList);


                    adapter = new SrProductTarget_Adapter(productTargetHashMap);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SrProductTarget.this));
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), VERTICAL));
                }
                else {
                    recyclerView.setAdapter(null);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //
        }

        //set adOnScrol to recyclerview and on scroll call api for more data

    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(SrProductTarget.this,"No Internet Connection",Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( SrProductTarget.this, SR_Account_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
