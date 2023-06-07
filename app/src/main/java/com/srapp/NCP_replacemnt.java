package com.srapp;

import static com.srapp.Db_Actions.URL.CheckConnection;
import static com.srapp.Db_Actions.URL.convertTORequestdata;
import static com.srapp.Db_Actions.URL.getJAPi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NCP_replacemnt extends AppCompatActivity {

    TextView quantity,outlet,p_quantity;

    HashMap<String,String> map;

    Double qty=0.0;

    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp_replacemnt);

        quantity=findViewById(R.id.qty);
        p_quantity=findViewById(R.id.pqty);
        save=findViewById(R.id.save);
        outlet=findViewById(R.id.outlet);
        map = (HashMap<String, String>)  getIntent().getSerializableExtra("map");

        quantity.setText(map.get("qty"));
        outlet.setText(map.get("outlet_name"));
        qty=Double.parseDouble(map.get("qty"));
        p_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Double pqty;
                try {
                     pqty = Double.parseDouble(editable.toString());

                     if (pqty>qty){
                         p_quantity.setText(qty+"");
                     }
                }catch (Exception e){
                    Toast.makeText(NCP_replacemnt.this,"Input Correctly",Toast.LENGTH_LONG).show();

                }




            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Double pqty = Double.parseDouble(p_quantity.getText().toString());
                if (pqty<=0){
                    Toast.makeText(NCP_replacemnt.this,"Please Input Replacement Quantity",Toast.LENGTH_LONG).show();

                    return;
                }

                JSONObject mainjsonObject =new JSONObject();


                try {
                    mainjsonObject.put("product_id", map.get("product_id"));
                    mainjsonObject.put("provided_qty", pqty);
                    mainjsonObject.put("outlet_id", map.get("outlet_id"));
                    mainjsonObject.put("collection_id", map.get("collection_id"));
                    mainjsonObject.put("collection_date", map.get("collection_date"));





                    ProgressDialog dailog = CheckConnection(NCP_replacemnt.this,"Replacement Ncp...");
                    if (dailog==null)
                        return;
                    getJAPi().NCP_Replacemnt(convertTORequestdata(mainjsonObject)).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("res");
                                dailog.dismiss();

                                if (jsonObject.getString("status").equalsIgnoreCase("1")){
                                    startActivity(new Intent(NCP_replacemnt.this, NcpCollectionList.class));
                                    finish();
                                }
                                Toast.makeText(NCP_replacemnt.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();




                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            dailog.dismiss();
                        }
                    });




                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }




            }
        });



    }
}