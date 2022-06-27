package com.srapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.srapp.Adapter.SpinnerAdapter;

import java.util.Arrays;

public class NcpCollectionList extends AppCompatActivity {

    Spinner thanaSpinner,marketSpinner,outletSpinner;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_collection_list);

        thanaSpinner = findViewById(R.id.thana_spinner);
        marketSpinner = findViewById(R.id.market_spinner);
        outletSpinner = findViewById(R.id.outlet_spinner);

        String b[] = new String[3];
        String c[] = new String[3];
        String d[] = new String[3];

        b[0]="";
        b[1]="Chatmohar";
        b[2]="Bhangura";

        c[0]="";
        c[1]="Old Market";
        c[2]="SP Market";

        d[0]="";
        d[1]="Old Market";
        d[2]="SP Market";

        SpinnerAdapter thanaData = new SpinnerAdapter(this, R.layout
                .spinner_item, Arrays.asList(b));
        thanaSpinner.setAdapter(thanaData);
        SpinnerAdapter marketData = new SpinnerAdapter(this, R.layout
                .spinner_item, Arrays.asList(c));
        SpinnerAdapter outletData = new SpinnerAdapter(this, R.layout
                .spinner_item, Arrays.asList(d));

        marketSpinner.setAdapter(marketData);
        outletSpinner.setAdapter(outletData);

        thanaSpinner.requestFocus();
        marketSpinner.requestFocus();
        outletSpinner.requestFocus();

        img = findViewById(R.id.home);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NcpCollectionList.this, BonusReport.class));
            }
        });
    }
}
