package com.srapp.Adapter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.URL;
import com.srapp.Faq;
import com.srapp.R;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SrProductTarget extends AppCompatActivity implements BasicFunctionListener {

    Spinner monthSpinner;
    ImageView img;
    TextView monthPick;
    String Result;
    BasicFunction basicFunction;
    private int initialPageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr_product_target);
        basicFunction = new BasicFunction(this,this);

        monthPick = findViewById(R.id.month_picker);

        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        Log.d("Month",dateFormat.format(date));

        final Calendar today = Calendar.getInstance();
        //tday.set(selectedYear,selectedMonth,20);
        today.set(Calendar.MONTH, Integer.parseInt(dateFormat.format(date))-1);
        //today.set(Calendar.MONTH,selectedMonth);
        Date selectedDay = today.getTime();
        DateFormat fmt = new SimpleDateFormat("MMMM", Locale.US);
        Log.e("month","month "+ "formated "+fmt
                .format(selectedDay));
        Result = fmt.format(selectedDay);

        monthPick.setText(Result);

        //................ call api for this month...........//
        JSONObject primaryData = new JSONObject();
        try {
            primaryData.put("mac",basicFunction.getPreference("mac"));
            primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
            primaryData.put("month",Result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        basicFunction.getResponceData(URL.ProductTarget,primaryData.toString(),901);
        //..............api call end..................//



        monthPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getMonthYear();

            }
        });


        img = findViewById(R.id.home);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SrProductTarget.this, Faq.class));
            }
        });
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
                Result = fmt.format(selectedDay);
                monthPick.setText(Result);
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //selectedmonyh= sdf.format(selectedDay);

                // call api for data here
                //................ call api for this month...........//
                JSONObject primaryData = new JSONObject();
                try {
                    primaryData.put("mac",basicFunction.getPreference("mac"));
                    primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                    primaryData.put("month",Result);
                    primaryData.put("page",initialPageIndex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                basicFunction.getResponceData(URL.ProductTarget,primaryData.toString(),901);
                //..............api call end..................//


            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH));



        builder.build().show();

        return Result;
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {

        if (i == 901){

            // set all new here

            //
        }

        //set adOnScrol to recyclerview and on scroll call api for more data

    }

    @Override
    public void OnConnetivityError() {

    }
}
