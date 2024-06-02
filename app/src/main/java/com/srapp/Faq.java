package com.srapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.srapp.Adapter.FaqAdapter;
import com.srapp.Db_Actions.Tables;
import com.srapp.Model.FaqModel;
import com.srapp.print.ParentActivity;

import java.util.ArrayList;

public class Faq extends ParentActivity {

    FaqAdapter adapter;
    ArrayList<FaqModel> data;
    RecyclerView recyclerView;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        recyclerView = findViewById(R.id.faq_rec);
        adapter = new FaqAdapter();
        data = new ArrayList<>();


            data.add(new FaqModel("অ্যাপ্লিকেশন  ইনস্টল  করব  কিভাবে ?","Web browser এ গিয়ে arenaphone.us/smc_dms  url টি লিখে প্রথমে ডাউনলোড করুন এবং তারপর ইনস্টল করুন"));
            data.add(new FaqModel("অ্যাপ্লিকেশন  আনইনস্টল  করব  কিভাবে ?","SR আইকনটি ধরে উপরে Delete Icon এ রাখলেই আন ইনস্টল হয়ে যাবে"));
            data.add(new FaqModel("কিভাবে লগ ইন করব ?","SRApps আইকনটি Click করলে User Name  & Password আসবে,সেটি Fill up করে Login এ Click করলেই Login হয়ে যাবে"));
            data.add(new FaqModel("কিভাবে market তৈরী করব ?"," More Icon এ গিয়ে Market List-> Create New Market  সব Field পূরণ করে Save দিলেই নতুন Market তৈরী হবে"));
            data.add(new FaqModel("কিভাবে আউটলেট তৈরী করব ?","More Icon গিয়ে Outlet List->Create New outlet এর সব Field পুরণ করে Save করলেই নতুন Outlet তৈরী হবে"));
            data.add(new FaqModel("Market কিভাবে  edit করব ?","More Icon গিয়ে Market Select করে Market List  এ যাইতে হবে এবং Edit করা যাবে"));
            data.add(new FaqModel("Outlet কিভাবে  edit করব ?","More Icon গিয়ে Outlet Select করে Outlet List এ যাইতে হবে এবং Edit করা যাবে"));
            data.add(new FaqModel("কিভাবে অর্ডার তৈরী করব ?","Outlet এ গিয়ে Route,Market,outlate type ,outlet fill up করে  Next button চাপতে হবে, এরপর Product select করে Product এর সংখ্যা দিয়ে Save দিতে হবে, এভাবে Order তৈরী হবে"));
            data.add(new FaqModel("কিভাবে অর্ডার Edit করব ?",": Report Icon এ গিয়ে Order Report এ Click করতে হবে ,তার পর Action Button চাপতে হবে Edit Order এ Click করতে হবে।"));
            data.add(new FaqModel("কিভাবে অর্ডার প্রিন্ট করব ?","Report Icon এ গিয়ে Order Report এ Click করতে হবে, ,তার পর Order Report এ Action  চাপতে হবে Details Order Report এ গিয়ে Print Order চাপতে হবে।"));
            data.add(new FaqModel("অর্ডার Cancel করব কিভাবে ?","More Icon এ গিয়ে Cancel Order তার পর অর্ডার Select করে Cancel Order Button এ ক্লিক করতে হবে "));
            data.add(new FaqModel("অর্ডার Process করব কিভাবে ?","More Icon এ গিয়ে Order Process->Process For Delivery তার পর ok Button এ ক্লিক করতে হবে "));
            data.add(new FaqModel("Invoice Cancel করব কিভাবে?","More Icon এ গিয়ে Cancel Invoice তার পর Invoice Select করে Cancel Button এ ক্লিক করতে হবে "));
            data.add(new FaqModel("অর্ডার Delivery করব কিভাবে?","Delivery Icon এ গিয়ে Pending এ ক্লিক  করতে হবে তার পর Delivery Now Button Click এ ক্লিক করতে হবে।"));
            data.add(new FaqModel("অর্ডার Delivery Edit করব কিভাবে?","Delivery Icon এ গিয়ে Pending এ ক্লিক  করতে হবে তার পর Edit Button Click এ ক্লিক করতে হবে।"));
            data.add(new FaqModel("কিভাবে মোবাইল সাথে প্রিন্টার জয়েন করব?","প্রথমে আপনাকে OTG  দ্বারা মোবাইল এবং প্রিন্টরের USB ক্যাবলের সাথে Connect করতে হবে তারপর আপনি অ্যাপ্লিকেশন থেকে print করতে পারেন"));
            data.add(new FaqModel("কিভাবে Server ডাটা পুশ করব?","SYNC Icon গিয়ে SYNC এ click করলেই Server থেকে তথ্য পাওয়া যাবে"));
            data.add(new FaqModel("স্টক কিভাবে চেক করব?","Stock Click করলেই Stock চেক করা যাবে"));
            data.add(new FaqModel("ডাটাবেস ব্যাকআপ কিভাবে  পাটাতে হবে?","Account  এ গিয়ে database backup  Click করতে হবে এবং Password লিখে Mail ID দিতে হবে"));


        adapter.setData(data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Faq.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Faq.this, SR_Account_Activity.class));
                finish();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent( Faq.this, SR_Account_Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
