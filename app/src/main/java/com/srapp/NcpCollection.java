package com.srapp;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForAttendanceHistory;
import com.srapp.Adapter.NCPAdapterForProductReturnDetails;
import com.srapp.Adapter.NcpCollectionAdapter;
import com.srapp.Adapter.SpinnerAdapter;
import com.srapp.Db_Actions.Data_Source;
import com.srapp.Db_Actions.Tables;
import com.srapp.Util.ParentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NcpCollection extends ParentActivity {

    NCPAdapterForProductReturnDetails adapter;
    ListView recyclerView;
    ArrayList<HashMap<String,String>> data;
    ImageView img;

    ArrayList<String> products;

    Data_Source ds ;

    Button save,add_product;
    EditText remarks;
    String outlet_id;
    boolean is_edit;
    Spinner productsp;
    ImageView homeBtn,backBtn;
    TextView userIdTV,titleTV ;
    HashMap<String,String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_collection);

        products=getIntent().getStringArrayListExtra("products");
        outlet_id = getIntent().getStringExtra("outlet_id");
        is_edit = getIntent().getBooleanExtra("is_edit",false);
        map = (HashMap<String, String>)  getIntent().getSerializableExtra("map");
        data=new ArrayList<>();
        ds=new Data_Source(this);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.back);
        add_product = findViewById(R.id.add_product);
        productsp = findViewById(R.id.productsp);
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        titleTV.setText("NCP Collection");


        recyclerView = findViewById(R.id.ncp_collection_rec);
        save = findViewById(R.id.save);
        remarks = findViewById(R.id.remarks);


        if (is_edit)
        setUpProducts();



        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( NcpCollection.this, Dashboard.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_edit){
                    startActivity(new Intent( NcpCollection.this, NcpCollectionList.class));
                }else {
                    startActivity(new Intent( NcpCollection.this, NcpProductCollection.class));
                }

                finish();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!is_edit)
                    saveData();
                    else 
                        updateData();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            setdata();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpProducts() {
        ArrayList<HashMap<String, String>> products = ds.GetproductlistbyCategory("0");
        ArrayList<String> product_name = new ArrayList<>();

        for (int i =0 ; i<products.size(); i++){
            product_name.add(products.get(i).get("product_name"));
        }

        SpinnerAdapter productCategoryAdapter =  new SpinnerAdapter(this, R.layout
                .spinner_item, product_name);
        productsp.setAdapter(productCategoryAdapter);

        productsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)parent.getChildAt(0);
                if (textView!=null)
                    textView.setTextColor(getResources().getColor(R.color.background_card));




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,String> map = new HashMap<>();

                //    Log.e("inobject",product.toString());

                String product_name = products.get(productsp.getSelectedItemPosition()).get("product_name");
                String product_id =products.get(productsp.getSelectedItemPosition()).get("product_id");


                Boolean should_add=true;
               for (int i = 0 ; i<data.size();i++){
                   if (data.get(i).get("product_id").equalsIgnoreCase(product_id)){
                       should_add=false;
                   }
               }

                map.put("product_name", product_name);
                map.put("product_id", product_id);
                map.put("qty", "");
                map.put("batch", "");
                map.put("exp", "");
                map.put("remarks", "");


                if (should_add) {
                    data.add(map);
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(NcpCollection.this,"This Product Already in List",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void updateData() throws JSONException {

        ArrayList<HashMap<String, String>> batch_list = adapter.getAlldata();
        if (batch_list.size()>0) {
            JSONObject mainjsonObject = new JSONObject();

            JSONArray products = new JSONArray();
            for (HashMap<String,String> item:batch_list) {
                

                JSONObject jsonObject = new JSONObject();
                if (item.containsKey("id"))
                jsonObject.put("id",item.get("id"));
                jsonObject.put("product_id",item.get("product_id"));
                jsonObject.put("quantity",item.get("qty"));
                jsonObject.put("expiredate",item.get("exp"));
                jsonObject.put("batch_id",batch_list.get(0).get("batch"));
                jsonObject.put("remarks", remarks.getText().toString());
                products.put(jsonObject);
            }


            mainjsonObject.put("products", products);
            mainjsonObject.put("outlet_id", map.get("outlet_id"));
            mainjsonObject.put("collection_date", map.get("collection_date"));
            mainjsonObject.put("ncp_id", map.get("collection_id"));

            ProgressDialog dailog = CheckConnection(NcpCollection.this,"Update Ncp...");
            if (dailog==null)
                return;
            getJAPi().Update_NCP(convertTORequestdata(mainjsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("res");
                        dailog.dismiss();

                        if (jsonObject.getString("status").equalsIgnoreCase("1")){
                            startActivity(new Intent(NcpCollection.this, NcpCollectionList.class));
                            finish();
                        }
                        Toast.makeText(NcpCollection.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();




                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dailog.dismiss();
                }
            });


        }





    }

    private void saveData() throws JSONException {
        ArrayList<HashMap<String, String>> batch_list = adapter.getAlldata();
        if (batch_list.size()>0){
            JSONObject mainjsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (int i =0 ; i<batch_list.size(); i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("product_id",batch_list.get(i).get("product_id"));
                jsonObject.put("quantity",batch_list.get(i).get("qty"));
                jsonObject.put("expiredate",batch_list.get(i).get("exp"));
                jsonObject.put("batch_id",batch_list.get(i).get("batch"));
                jsonObject.put("remarks","");
                jsonArray.put(jsonObject);
            }
            mainjsonObject.put("products",jsonArray);
            mainjsonObject.put("outlet_id",outlet_id);
            mainjsonObject.put("collection_date",getCurrentDate());
            mainjsonObject.put("remarks",remarks.getText().toString());


            ProgressDialog dailog = CheckConnection(NcpCollection.this,"Save Ncp...");
            if (dailog==null)
                return;
            getJAPi().SAVE_NCP(convertTORequestdata(mainjsonObject)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("res");
                        dailog.dismiss();

                        if (jsonObject.getString("status").equalsIgnoreCase("1")){
                            startActivity(new Intent(NcpCollection.this, SR_NCP_Activity.class));
                            finish();
                        }
                        Toast.makeText(NcpCollection.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();




                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dailog.dismiss();
                }
            });

        }

    }

    private void setdata() throws JSONException {
        data.clear();
        Log.e("data",data.size()+"");

        if (is_edit) {
            JSONArray products = new JSONArray(map.get("products"));
            for (int i = 0 ; i<products.length();i++) {
                HashMap<String,String> map = new HashMap<>();
                JSONObject product = products.getJSONObject(i);
                //    Log.e("inobject",product.toString());
                String quantity = product.getString("quantity");
                String batch_id = product.getString("batch_id");
                String remarks = product.getString("remarks");
                String product_name = product.getString("name");
                String product_id = product.getString("product_id");
                String id = product.getString("id");
                // String status = product.getString("status");
                String expire_date = (product.has("expire_date")) ? product.getString("expire_date") : "no Ex date in api ";


                map.put("product_name", product_name);
                map.put("id", id);
                map.put("product_id", product_id);
                map.put("qty", quantity);
                map.put("batch", batch_id);
                map.put("exp", expire_date);
                map.put("remarks", remarks);
                Log.e("inobject", map.toString());


                data.add(map);

            }
            remarks.setText(map.get("remarks"));
        }else
          data.addAll(ds.getproductListWithbatch(products));

        adapter = new NCPAdapterForProductReturnDetails(this,data);
        recyclerView.setAdapter(adapter);

    }

    private void updatedata(String value,String key,int position){
        data.get(position).put(key,value);
        Log.e("data","value="+value+" key="+key+" pos"+position);
    }
}
