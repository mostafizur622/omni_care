package com.srapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import com.srapp.Adapter.AdapterForBonusPolicySet;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Util.Parent;

import static com.srapp.Adapter.SalesOrderDetailsAdaper1.bonus_policylistener;
import static com.srapp.TempData.policyArrayList;

public class BonusPolicySet extends Parent {

    Button NextButton, CancelButton;
    AdapterForBonusPolicySet dataAdapter;
    Data_Source db;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bonus_show_dialog);

        NextButton = (Button) findViewById(R.id.NextButton);
        CancelButton = (Button) findViewById(R.id.CancelButton);
        Log.e("befoeremove", "before");

        CancelButton.setOnClickListener(view -> {
            bonus_policylistener.setBonus_type(2);
            finishAndRemoveTask(); // Before- finish();
        });

        dataAdapter = new AdapterForBonusPolicySet(BonusPolicySet.this, policyArrayList);
        ListView listView = (ListView) findViewById(R.id.ProductListView);
        listView.setAdapter(dataAdapter);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
