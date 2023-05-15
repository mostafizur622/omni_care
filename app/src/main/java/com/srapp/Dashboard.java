package com.srapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.Parent;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONObject;

public class Dashboard extends Parent implements BasicFunctionListener {

    Button reportBtn, stockBtn, accountBtn, syncBtn, toolsBtn, deliveryBtn;
    ImageView backBtn, homeBtn;
    LinearLayout order_or_delivery;
    TextView cash_number, oc_value, userIdTV, titleTV,oc;
    Data_Source ds;
    BasicFunction basicFunction;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
            builder.setCancelable(true);
            builder.setTitle("App close");
            builder.setMessage("Do You Want to Exit?");
            builder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        basicFunction = new BasicFunction(this, this);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        FirebaseCrashlytics.getInstance().setUserId(basicFunction.getPreference("sr_uname"));
        userIdTV.setText(value);
        ds = new Data_Source(this);
        reportBtn = findViewById(R.id.reportBtn);
        cash_number = findViewById(R.id.cash_number);
        oc_value = findViewById(R.id.oc_value);
        oc = findViewById(R.id.oc);
        stockBtn = findViewById(R.id.stockBtn);
        accountBtn = findViewById(R.id.accountBtn);
        syncBtn = findViewById(R.id.sync_btn);
        toolsBtn = findViewById(R.id.toolBtn);
        deliveryBtn = findViewById(R.id.deliveryBtn);
        backBtn = findViewById(R.id.back);
        homeBtn = findViewById(R.id.home);


        order_or_delivery = findViewById(R.id.logo);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Reports_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        deliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, DeliveryReport.class);
                startActivity(intent);
                finish();
            }
        });

        stockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, SrStoreStatus.class);
                startActivity(intent);
                finish();
            }
        });

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, SR_Account_Activity.class);
                startActivity(intent);
                finish();
            }
        });


        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, SyncActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //more button
        toolsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Tools.class);
                startActivity(intent);
                finish();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                builder.setCancelable(true);
                builder.setTitle("Logout");
                builder.setMessage("Do You Want to Logout?");
                builder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                                startActivity(intent);
                                finishAffinity();
                                finish();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        //outlets button
        order_or_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, Create_New_Memo.class);
                i.putExtra("flag1", 1);
                startActivity(i);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        cash_number.setText(ds.getTotalCashOfCurrentDay());
        oc_value.setText(ds.getTotalOCofCurrentDay());
        oc.setText(ds.getOc());


    }

    @Override
    protected void onResume() {
        super.onResume();
        cash_number.setText(ds.getTotalCashOfCurrentDay());
        oc_value.setText(ds.getTotalOCofCurrentDay());
        oc.setText(ds.getOc());
    }

    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {

    }

    @Override
    public void OnConnetivityError() {

    }
}
