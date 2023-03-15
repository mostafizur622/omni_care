package com.srapp;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.OutletViewAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Db_Actions.URL;
import com.srapp.Util.StaticFlags;
import com.tanvir.BasicFun.BasicFunction;
import com.tanvir.BasicFun.BasicFunctionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletView extends AppCompatActivity implements BasicFunctionListener {

    RecyclerView recyclerView;
    OutletViewAdapter adapter;
    Data_Source ds;
    HashMap<String, ArrayList<String>>outletList;
    ArrayList<String> valueList, attributeList, imageList;
    BasicFunction basicFunction;
    String pharmaType;

    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_view);

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
                startActivity(new Intent( OutletView.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OutletView.this,OutletList.class);
                intent.putExtra("resume",1);
                startActivity(intent);
                finish();
            }
        });

        basicFunction = new BasicFunction(this,this);
        ds = new Data_Source(this);
        outletList = new HashMap<>();
        attributeList = new ArrayList<>();
        valueList = new ArrayList<>();
        imageList = new ArrayList<>();

        Intent intent = getIntent();
        String outletId = intent.getStringExtra("outlet_id");
        if (outletId != null) {
            Log.e("intent pas----->", "onCreate: "+outletId );
            //outletList = ds.getIndividualOutlet(outletId);
            //for back to outlet list to set scroll position for outlet
            StaticFlags.OUTLET_ID_FOR_SCROLL = outletId;
            JSONObject primaryData = new JSONObject();
            try {
                primaryData.put("mac",basicFunction.getPreference("mac"));
                primaryData.put("sales_person_id",basicFunction.getPreference("sales_person_id"));
                primaryData.put("outlet_id",outletId);
            } catch (JSONException e) {
                e.printStackTrace();
            }


          //  basicFunction.getResponceData(URL.OutletDetails, String.valueOf(primaryData),501);

            ProgressDialog dailog = CheckConnection(OutletView.this,"Checking...");
            if (dailog==null)
                return;
            getJAPi().OutletDetails(convertTORequestdata(primaryData)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        dailog.dismiss();
                        valueList.add(jsonObject.getJSONObject("outlets").getJSONObject("0").getString("outlet_name"));
                        attributeList.add("Outlet Name");

                        int position = getPosition(ds.getOutletCategories().get(Tables.OUTLET_CATEGORY_CATEGORY_ID),jsonObject.getJSONObject("outlets").getJSONObject("0").getString("outlet_category_id"));
                        String outletType;
                        if (position>0)
                            outletType = ds.getOutletCategories().get(Tables.OUTLET_CATEGORY_NAME).get(position);
                        else
                            outletType = ds.getOutletCategories().get(Tables.OUTLET_CATEGORY_NAME).get(0);


                        valueList.add(outletType);
                        attributeList.add("Outlet Type");

                        if (jsonObject.getJSONObject("outlets").getJSONObject("0").getString("pharma_type").equals("0")){
                            pharmaType = "No";
                        }else {
                            pharmaType = "Yes";
                        }

                        valueList.add(pharmaType);
                        attributeList.add("Pharma Type");

                        valueList.add(jsonObject.getJSONObject("outlets").getJSONObject("0").getString("owner_name"));
                        attributeList.add("Contact Name");

                        valueList.add(jsonObject.getJSONObject("outlets").getJSONObject("0").getString("mobile"));
                        attributeList.add("Mobile");

                        valueList.add(jsonObject.getJSONObject("outlets").getJSONObject("0").getString("market_name"));
                        attributeList.add("Market Name");

                        valueList.add(jsonObject.getJSONObject("outlets").getJSONObject("0").getString("thana_name"));
                        attributeList.add("Thana Name");

            /*valueList.add(jsonObject.getJSONObject("outlets").getJSONArray("images").getString(0));
            attributeList.add("Image1");

            valueList.add(jsonObject.getJSONObject("outlets").getJSONArray("images").getString(1));
            attributeList.add("Image2");

            valueList.add(jsonObject.getJSONObject("outlets").getJSONArray("images").getString(2));
            attributeList.add("Image2");*/
                        attributeList.add("for image");

                        outletList.put("Attributes",attributeList);
                        outletList.put("Values",valueList);

                        if (jsonObject.getJSONObject("outlets").getJSONArray("images").length()>0) {

                            for (int z = 0; z < jsonObject.getJSONObject("outlets").getJSONArray("images").length(); z++) {
                                imageList.add(jsonObject.getJSONObject("outlets").getJSONArray("images").getString(z));
                            }
                        }

            /*imageList.add(jsonObject.getJSONObject("outlets").getJSONArray("images").getString(0));
            imageList.add(jsonObject.getJSONObject("outlets").getJSONArray("images").getString(1));
            imageList.add(jsonObject.getJSONObject("outlets").getJSONArray("images").getString(2));*/

                        outletList.put("image_list",imageList);

                        Log.e("Outlet List", "OnServerResponce:----------->>> "+outletList );

                        adapter = new OutletViewAdapter(outletList,OutletView.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(OutletView.this));
                        recyclerView.setAdapter(adapter);
                        recyclerView.addItemDecoration(new DividerItemDecoration(OutletView.this,DividerItemDecoration.VERTICAL));

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

            recyclerView =  findViewById(R.id.outlet_view_recycler);

        }



    }


    @Override
    public void OnServerResponce(JSONObject jsonObject, int i) {
        Log.e(" 501 response", "OnServerResponce: "+jsonObject );









    }

    @Override
    public void OnConnetivityError() {
        Toast.makeText(OutletView.this,"No Internet Connection",Toast.LENGTH_SHORT).show();

    }
    public int getPosition(ArrayList<String>data,String id){

        int position = data.indexOf(id);

        return  position;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(OutletView.this,OutletList.class);
            intent.putExtra("resume",1);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
