package com.srapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.srapp.Util.ParentActivity;
import com.srapp.helpers.SpecialPolicyHelper;

public class WebView extends ParentActivity {

    android.webkit.WebView webView;

    Button close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        close = findViewById(R.id.close);
        webView = findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(getIntent().getStringExtra("url"));

        Log.e("url",getIntent().getStringExtra("url"));

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("orsaline_offer_success")) {
                    String[] parts = url.split("/");

                    Log.e("url", parts.length + " " + parts[parts.length - 1]);
                    int status = Integer.parseInt(parts[parts.length - 1]);
                    String outlet_id = parts[parts.length - 3];
                    if (status==1){
                        SpecialPolicyHelper specialPolicyHelper = new SpecialPolicyHelper(WebView.this);
                        specialPolicyHelper.saveToTable(outlet_id);
                    }
                }
                Log.e("url",url);
            }


            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                webView.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}