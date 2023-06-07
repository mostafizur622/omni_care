package com.srapp;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.srapp.Adapter.AdapterForAttendanceHistory;
import com.srapp.Adapter.NCPAdapterForProductReturnDetails;
import com.srapp.Adapter.NcpCollectionAdapter;
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

    Button save;
    EditText remarks;
    String outlet_id;
    boolean is_edit;

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
        userIdTV = findViewById(R.id.user_txt_view);
        titleTV = findViewById(R.id.title_tv);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String value = prefs.getString(Tables.SR_ID, "0");
        userIdTV.setText(value);
        titleTV.setText("NCP Collection");


        recyclerView = findViewById(R.id.ncp_collection_rec);
        save = findViewById(R.id.save);
        remarks = findViewById(R.id.remarks);




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
                startActivity(new Intent( NcpCollection.this, NcpProductCollection.class));
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

        setdata();
    }

    private void updateData() throws JSONException {

        ArrayList<HashMap<String, String>> batch_list = adapter.getAlldata();
        if (batch_list.size()>0) {
            JSONObject mainjsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < batch_list.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("product_id", batch_list.get(i).get("product_id"));
                jsonObject.put("quantity", batch_list.get(i).get("qty"));
                jsonObject.put("expiredate", batch_list.get(i).get("exp"));
                jsonObject.put("batch_id",batch_list.get(i).get("batch"));
                jsonObject.put("remarks", "");
                jsonArray.put(jsonObject);
            }
            mainjsonObject.put("products", jsonArray);
            mainjsonObject.put("outlet_id", map.get("outlet_id"));
            mainjsonObject.put("collection", map.get("collection"));

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

    private void setdata() {
        data.clear();
        Log.e("data",data.size()+"");

        if (is_edit)
            data.add(map);
            else
          data.addAll(ds.getproductListWithbatch(products));

        adapter = new NCPAdapterForProductReturnDetails(this,data);
        recyclerView.setAdapter(adapter);

    }

    private void updatedata(String value,String key,int position){
        data.get(position).put(key,value);
        Log.e("data","value="+value+" key="+key+" pos"+position);
    }
}
