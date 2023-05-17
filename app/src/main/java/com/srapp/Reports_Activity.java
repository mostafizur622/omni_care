package com.srapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Adapter.Reports_Adapter;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.Order_Report;
import com.srapp.Model.Reports;
import com.srapp.Util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Reports_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView report_icon,home,back;
    TextView userIdTV,titleTV  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        recyclerView = (RecyclerView) findViewById(R.id.Reports_recycleView);
        home = (ImageView) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Reports_Activity.this, Dashboard.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Reports_Activity.this, Dashboard.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        Reports_Adapter adapter = new Reports_Adapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    startActivity(new Intent(Reports_Activity.this, Order_Report_Activity.class));
                    finish();
                } else if (position == 1) {
                    startActivity(new Intent(Reports_Activity.this, MemoReport.class));
                    finish();
                } else if (position == 2) {
                    startActivity(new Intent(Reports_Activity.this, BonusReport.class));
                    finish();
                }else if (position == 3) {
                    startActivity(new Intent(Reports_Activity.this, ProductWiseDeliveryReport.class));
                    finish();
                }else if (position == 4) {
                    startActivity(new Intent(Reports_Activity.this, Product_Wise_Order_Summery.class).putExtra("report_name","Invoice Summery Report").putExtra("flag",1));
                    finish();
                }else if (position == 5) {
                    startActivity(new Intent(Reports_Activity.this, Product_Wise_Order_Summery.class).putExtra("report_name","Delivery Summery Report").putExtra("flag",2));
                    finish();
                }else if (position == 6) {
                    startActivity(new Intent(Reports_Activity.this, Product_Wise_Order_Summery.class).putExtra("report_name","Order Summery Report").putExtra("flag",3));
                    finish();
                }else if (position == 7) {
                    startActivity(new Intent(Reports_Activity.this, Product_Wise_Bonus_Summery.class));
                    finish();
                }else if (position == 8) {
                    startActivity(new Intent(Reports_Activity.this, OutletVisitReport.class));
                    finish();
                }else if (position == 9) {
                    startActivity(new Intent(Reports_Activity.this, Outlet_wise_sales_report.class));
                    finish();
                }else if (position == 10) {
                    startActivity(new Intent(Reports_Activity.this, ReportBonusPartyReport.class));
                    finish();
                }else if (position == 11) {
                    startActivity(new Intent(Reports_Activity.this, Outlet_wise_Product_report.class));
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private List<Reports> getList() {
        List<Reports> report_List = new ArrayList<>();
        // src Wikipedia
        report_List.add(new Reports("Order Report", null));
        report_List.add(new Reports("Invoice Report", null));
        report_List.add(new Reports("Order Bonus Report", null));
        report_List.add(new Reports("Product Wise Order Report", null));
        report_List.add(new Reports("Invoice Summery Report", null));
        report_List.add(new Reports("Delivery Summery Report", null));
        report_List.add(new Reports("Order Summery Report", null));
        report_List.add(new Reports("Memo Bonus Summery Report", null));
        report_List.add(new Reports("Outlet Visit Report", null));
        report_List.add(new Reports("Outlet Wise Sales Report", null));
        report_List.add(new Reports("Incentive Party report", null));
        report_List.add(new Reports("Outlet Wise Product Report", null));

        return report_List;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(Reports_Activity.this, Dashboard.class);
            startActivity(intent);
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
