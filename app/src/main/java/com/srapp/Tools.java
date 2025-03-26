package com.srapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Adapter.SR_TargetAdapter;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.SR_Target;
import com.srapp.Util.ParentActivity;
import com.srapp.Util.RecyclerTouchListener;
import com.srapp.kotlin.BonusCampaignActivity;
import com.srapp.kotlin.CurrentOfferProductActivity;

import java.util.ArrayList;
import java.util.List;

public class Tools extends ParentActivity {
    RecyclerView recyclerView;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);

        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Tools.this, Dashboard.class));
                finishAffinity();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Tools.this, Dashboard.class));
                finishAffinity();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.SR_Target_recycleView);

        SR_TargetAdapter adapter = new SR_TargetAdapter(getList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                switch (position){

                    case 0:
                        startActivity(new Intent(Tools.this,OutletList.class));
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(Tools.this,MarketList.class));
                        finish();
                        break;

                    case 2:
                        startActivity(new Intent(Tools.this,PriceList.class));
                        finish();
                        break;

//                    case 3:
//                        startActivity(new Intent(Tools.this,OrderProcess.class));
//                        finish();
//                        break;
//                    case 4:
//                        startActivity(new Intent(Tools.this,UnProcessOrder.class));
//                        finish();
//                        break;
//
//                        case 5:
//                        startActivity(new Intent(Tools.this,Multiple_Invoice_print.class));
//                        finish();
//                        break;
                    case 3:
                        startActivity(new Intent(Tools.this,CancelOrder.class));
                        finish();
                        break;

                    case 4:
                        startActivity(new Intent(Tools.this,MakePDF.class));
                        finish();
                        break;
                        case 5:
                        startActivity(new Intent(Tools.this,Product_Catalog.class));
                        finish();
                        break;
                        case 6:
                        startActivity(new Intent(Tools.this,GiftIssueList.class));
                        finish();
                        break;

                    case 7:
                        startActivity(new Intent(Tools.this, BonusCampaignActivity.class));
                        finish();
                        break;

                    case 8:
                        startActivity(new Intent(Tools.this, CurrentOfferProductActivity.class));
                        finish();
                        break;

                        case 9:
                        startActivity(new Intent(Tools.this, outletStatus.class));
                        finish();
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }


    private List<SR_Target> getList() {
        List<SR_Target> sr_targetList = new ArrayList<>();
        // src Wikipedia--------------------------------------------------
        sr_targetList.add(new SR_Target("Outlet List",null));
        sr_targetList.add(new SR_Target("Market List",null));
        sr_targetList.add(new SR_Target("Price List",null));
/*        sr_targetList.add(new SR_Target("Order Process",null));
        sr_targetList.add(new SR_Target("Cancel Invoice ",null));
        sr_targetList.add(new SR_Target("Print Invoice",null));*/
        sr_targetList.add(new SR_Target("Cancel Order ",null));
        sr_targetList.add(new SR_Target("Generate Order PDF",null));
        sr_targetList.add(new SR_Target("Product Catalog",null));
        sr_targetList.add(new SR_Target("Gift Issue",null));
        sr_targetList.add(new SR_Target("Bonus Campaign",null));
        sr_targetList.add(new SR_Target("Current Offer Product",null));
        sr_targetList.add(new SR_Target("Pending Outlet",null));
       // sr_targetList.add(new SR_Target("",null));

        return sr_targetList;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( Tools.this, Dashboard.class));
            finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
