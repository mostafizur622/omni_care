package com.srapp;

import java.util.ArrayList;
import java.util.HashMap;



import com.srapp.Adapter.AdapterForBonusShow;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Model.Product;
import com.srapp.R;
import com.srapp.TempData;
import com.srapp.Util.Parent;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class BonusShowListActivity extends Parent implements OnClickListener{


   /* private Button btnBack, btnHome;
    TextView HeaderTitleTv, CodeNoTv;*/

    Button NextButton, CancelButton;


    //	LinearLayout ChallanNoLayout;
    AdapterForBonusShow dataAdapter;


    ArrayList<String> ProductCategoryID=new ArrayList<String>();
    ArrayList<String> ProductCategoryName=new ArrayList<String>();
    String _ProductCategoryID;

    ArrayList<String> ProductID=new ArrayList<String>();
    ArrayList<String> ProductName=new ArrayList<String>();

    ArrayList<Product> ProductList = new ArrayList<Product>();

    ArrayList<HashMap<String, String>> BonusItemList = new ArrayList<HashMap<String, String>>();
    String back="";
//    String ActivityName="";


    ArrayList<String> ClaimID=new ArrayList<String>();
    ArrayList<String> ClaimName=new ArrayList<String>();
    String _ClaimType;

    ArrayList<String> ChallanID=new ArrayList<String>();
    ArrayList<String> ChallanNO=new ArrayList<String>();
    public String _ChallanID="",_ChallanNO = "";

    Data_Source db;
    //	String PlaceVisit,NightHolting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bonus_show_list);

        db=new Data_Source(this);


        NextButton = (Button)findViewById(R.id.NextButton);
        CancelButton = (Button)findViewById(R.id.CancelButton);


        CancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        Log.e("+TotalBonusProductList","TotalBonusProductList"+TempData.TotalBonusProductList);

        for(int j = 0; j< TempData.TotalBonusProductList.size(); j++)
        {
            HashMap<String, String> mapContent = new HashMap<String, String>();
            mapContent = TempData.TotalBonusProductList.get(j);
            String bonus_name = mapContent.get("product_name");
            String quantity = mapContent.get("quantity");

            Log.e("+++++","bonus_name"+bonus_name);
          /*  mapContent.put("bonus_name",mapContent.)
            txtName.setText( mapContent.get("market_name"));*/

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("bonus_name",bonus_name);
            map.put("quantity",quantity);

            BonusItemList.add(map);
        }

        Log.e("++BonusItemList+++","BonusItemList"+TempData.BonusShowList.toString());

        for(int i=0;i<TempData.BonusShowList.size();i++)
        {
            HashMap<String, String> mapContent = new HashMap<String, String>();
            mapContent = TempData.BonusShowList.get(i);
            String bonus_name = mapContent.get("bonus_name");
            String quantity = mapContent.get("quantity");

            Log.e("+++++","bonus_name"+bonus_name);
          /*  mapContent.put("bonus_name",mapContent.)
            txtName.setText( mapContent.get("market_name"));*/

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("bonus_name",bonus_name);
            map.put("quantity",quantity);

            BonusItemList.add(map);
        }


        Log.e("++BonusItemList+++","BonusItemList"+BonusItemList.toString());
        dataAdapter = new AdapterForBonusShow(BonusShowListActivity.this, BonusItemList);
        ListView listView = (ListView) findViewById(R.id.ProductListView);
        listView.setAdapter(dataAdapter);



    }


    int getIndex(String value,ArrayList<HashMap<String,String>> orderList){
        int pos = -1;
        for(int i=0;i<orderList.size();i++)
        {
            HashMap<String,String> map=orderList.get(i);
            Log.e("PRODUCT_ID", map.get("product_id"));
            if(map.get("product_id").equalsIgnoreCase(value))
                pos=i;
        }

        return pos;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
//            TempBonus();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initiateButtons(Button btn, int id)
    {
        btn = (Button) findViewById(id);
        btn.setOnClickListener((OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub


    }

    public void savePreferenceChallan(String key, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BonusShowListActivity.this);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    private void TempBonus()
    {

        for(int k=0; k<TempData.TotalBonusProductList.size(); k++)
        {
            savePreference("bonus"+TempData.TotalBonusProductList.get(k).get("product_id"),"0.0");
            Log.e("TempBonus()", "TempBonus()"+getPreference("bonus"+TempData.TotalBonusProductList.get(k).get("product_id")));
        }
    }
}
