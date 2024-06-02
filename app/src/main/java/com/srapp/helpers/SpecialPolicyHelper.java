package com.srapp.helpers;

import static com.srapp.Db_Actions.Tables.BANK_BRANCH_bank_branch_id;
import static com.srapp.Db_Actions.Tables.BANK_BRANCH_created_at;
import static com.srapp.Db_Actions.Tables.BANK_BRANCH_table_id;
import static com.srapp.Db_Actions.Tables.BANK_BRANCH_updated_at;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/******
 **** Created By  TANVIR3488 AT 30/5/24 7:58 PM
 ******/


public class SpecialPolicyHelper extends Helper{
    public SpecialPolicyHelper(Context context) {
        super(context);
    }

    public static String TABLE_NAME_INGONORE_OUTLET_FOR_POLICY="sr_offer_outlet";
    public static String OUTLET_IGNORE_OUTLET_ID="outlet_id";
    public static final String OUTLET_IGNORE[] = {TABLE_NAME_INGONORE_OUTLET_FOR_POLICY,BANK_BRANCH_table_id, OUTLET_IGNORE_OUTLET_ID,  BANK_BRANCH_created_at, BANK_BRANCH_updated_at};

    public static String CREATE_IGNORE_OUTLET_FOR_POLICY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_INGONORE_OUTLET_FOR_POLICY + "("
            + "_id" + " INTEGER PRIMARY KEY," +
            "outlet_id" + " VARCHAR," +
            "created_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))," +
            "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";



    public void saveToTable(String outlet_id){
        HashMap<String,String> map = new HashMap<>();
        map.put("outlet_id",outlet_id);
        db.InsertTable(map,TABLE_NAME_INGONORE_OUTLET_FOR_POLICY);
    }

    public boolean checkOutlet(String outlet_id){

        Cursor c = db.rawQueryCoustom("Select * from "+TABLE_NAME_INGONORE_OUTLET_FOR_POLICY+" where outlet_id='"+outlet_id+"'");
        c.moveToFirst();

        return c.getCount()>0;
    }


}
