package com.srapp.Db_Actions;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.srapp.Db_Actions.Tables.CREATE_BONUS_PARTY_TABLE;
import static com.srapp.helpers.SpecialPolicyHelper.CREATE_IGNORE_OUTLET_FOR_POLICY_TABLE;

public class DB_Helper extends SQLiteOpenHelper {

    int oldv, newv;

    public DB_Helper(Context context) {
        super(context, Tables.DATABASE_NAME, null, Tables.DATABASE_VIRSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //.................................DiscountBonusPolicyToExcludingOutletGroupSr ...............................................

        String DiscountBonusPolicyToExcludingOutletGroupSr  = "CREATE TABLE IF NOT EXISTS " + "DiscountBonusPolicyToExcludingOutletGroupSr" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "_id" + " VARCHAR," +
                "discount_bonus_policy_id" + " VARCHAR," +
                "create_for" + " VARCHAR," +
                "reffrence_id" + " VARCHAR," +
                "for_so_sr" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";

        sqLiteDatabase.execSQL(DiscountBonusPolicyToExcludingOutletGroupSr);

        //.................................product_price_other_for_slabs_v2 ...............................................

        String product_price_other_for_slabs_v2  = "CREATE TABLE IF NOT EXISTS " + "product_price_other_for_slabs_v2" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "product_combination_id" + " INT," +
                "type" + " INT," +
                "reference_id" + " INT," +
                "price" + " decimal," +                  //no need to app
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(product_price_other_for_slabs_v2);

        //.................................Special_Group ..................................................................

        String special_group  = "CREATE TABLE IF NOT EXISTS " + "special_group" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "pid" + " VARCHAR," +
                "name" + " VARCHAR," +
                "remarks" + " VARCHAR," +
                "start_date" + " DATE," +
                "end_date" + " DATE," +
                "is_dist" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(special_group);

        //.................................Special_Group_Details ...............................................

        String special_group_details  = "CREATE TABLE IF NOT EXISTS " + "special_group_details" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "_id" + " VARCHAR," +
                "special_group_id" + " VARCHAR," +
                "create_for" + " VARCHAR," +
                "reffrence_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(special_group_details);

        //.................................Product_combination_list .......................................................

        String product_combination_list  = "CREATE TABLE IF NOT EXISTS " + "product_combination_list" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "_id" + " VARCHAR," +
                "name" + " VARCHAR," +
                "effective_date" + " VARCHAR," +
                "create_for" + " VARCHAR," +
                "reference_id" + " VARCHAR," +
                "combined_qty" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(product_combination_list);

        //.................................Product_combination_list_details ...............................................

        String Product_combination_list_details_v2  = "CREATE TABLE IF NOT EXISTS " + "Product_combination_list_details_v2" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "_id" + " VARCHAR," +
                "combination_id" + " VARCHAR," +
                "product_id" + " VARCHAR," +
                "product_combination_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";

        sqLiteDatabase.execSQL(Product_combination_list_details_v2);

        //.................................DiscountBonusPolicyToSpecialGroupSo ...............................................

        String DiscountBonusPolicyToSpecialGroupSo  = "CREATE TABLE IF NOT EXISTS " + "DiscountBonusPolicyToSpecialGroupSo" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "_id" + " VARCHAR," +
                "discount_bonus_policy_id" + " VARCHAR," +
                "create_for" + " VARCHAR," +
                "reffrence_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";

        sqLiteDatabase.execSQL(DiscountBonusPolicyToSpecialGroupSo);

        //.................................DiscountBonusPolicyOptionExclusionProduct ...............................................

        String DiscountBonusPolicyOptionExclusionProduct  = "CREATE TABLE IF NOT EXISTS " + "DiscountBonusPolicyOptionExclusionProduct" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "discount_bonus_policy_id" + " VARCHAR," +
                "discount_bonus_policy_option_id" + " VARCHAR," +
                "product_id" + " VARCHAR," +
                "min_qty" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";

        sqLiteDatabase.execSQL(DiscountBonusPolicyOptionExclusionProduct);

        //.................................DiscountBonusPolicyOptionExclusionProduct ...............................................

        String DiscountBonusPolicyOptionInclusionProduct  = "CREATE TABLE IF NOT EXISTS " + "DiscountBonusPolicyOptionInclusionProduct" + "("
                + "id" + " INTEGER PRIMARY KEY," +
                "discount_bonus_policy_id" + " VARCHAR," +
                "discount_bonus_policy_option_id" + " VARCHAR," +
                "product_id" + " VARCHAR," +
                "min_qty" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";

        sqLiteDatabase.execSQL(DiscountBonusPolicyOptionInclusionProduct);



        //.................................Bonus Eligible Group...........................................
        String CREATE_Bonus_Eligible_Group = "CREATE TABLE IF NOT EXISTS " + "Bonus_Eligible_Group" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "outlet_group_id" + " VARCHAR," +
                "policy_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(CREATE_Bonus_Eligible_Group);


        //.................................Bonus Eligible Outlets...........................................
        String CREATE_Bonus_Eligible_Outlets = "CREATE TABLE IF NOT EXISTS " + "Bonus_Eligible_Outlets" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "outlet_id" + " VARCHAR," +
                "Group_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(CREATE_Bonus_Eligible_Outlets);

        //.................................Bonus Eligible Outlet Categories ...........................................
        String Bonus_Eligible_Outlet_Categories = "CREATE TABLE IF NOT EXISTS " + "Bonus_Eligible_Outlet_Categories" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "category_id" + " VARCHAR," +
                "policy_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(Bonus_Eligible_Outlet_Categories);

        //.................................Policy Table ...........................................
        String Policy_Table = "CREATE TABLE IF NOT EXISTS " + "Policy_Table" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "policy_id" + " VARCHAR," +
                "policy_name" + " TEXT," +
                "start_date" + " DATE," +
                "end_date" + " DATE," +
                "bonus_applicable" + " TEXT," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(Policy_Table);

        //.................................policy_Root_product ...........................................
        String policy_Root_product = "CREATE TABLE IF NOT EXISTS " + "policy_root_product" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "policy_id" + " VARCHAR," +
                "root_product_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(policy_Root_product);


        //.................................Policy_product_Option ...........................................
        String policy_product_Option = "CREATE TABLE IF NOT EXISTS " + "policy_product_Option" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "policy_id" + " VARCHAR," +
                "option_id" + " VARCHAR," +
                "option_product_id" + " VARCHAR," +
                "min_qty" + " DOUBLE," +
                "main_min_qty" + " DOUBLE," +
                "bonus_product_id" + " VARCHAR," +
                "bonus_qty" + " Double," +
                "discount_type" + " VARCHAR," +
                "discount_amount" + " VARCHAR," +
                "policy_type" + " VARCHAR," +
                "unit_id" + " VARCHAR," +
                "min_memo_val" + " VARCHAR," +
                "formula" + " VARCHAR," +
                "formula_text" + " VARCHAR," +
                "qty_value_flag" + " INTEGER," +
                "deduct_from_value" + " INTEGER," +
                "min_value" + " DOUBLE," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(policy_product_Option);

        //.................................Policy_product_Option_temp ...........................................
        String policy_product_Option_temp = "CREATE TABLE IF NOT EXISTS " + "policy_product_Option_temp" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "policy_id" + " VARCHAR," +
                "option_id" + " VARCHAR," +
                "policy_type" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(policy_product_Option_temp);

        //................................. default Bonus product ...........................................
        String policy_default_product = "CREATE TABLE IF NOT EXISTS " + "policy_default_product" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "policy_id" + " VARCHAR," +
                "product_id" + " VARCHAR," +
                "discount_bonus_policy_option_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(policy_default_product);

        //.................................policy_option_price_slab ...........................................
        String Policy_option_price_slab  = "CREATE TABLE IF NOT EXISTS " + "policy_option_price_slab" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "policy_product_option_id" + " VARCHAR," +
                "option_product_id" + " VARCHAR," +
                "slab_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(Policy_option_price_slab);


        //.................................UNIT_Details...........................................
        String CREATE_TABLE_UNIT_details = "CREATE TABLE IF NOT EXISTS " + "unit_details" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "product_id" + " INTEGER," +
                "qty_in_base" + " INTEGER," +
                "unit_name" + " VARCHAR," +
                "measurement_unit_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(CREATE_TABLE_UNIT_details);

        //.................................policy_bonus_product ...........................................
        String Policy_Bonus_product  = "CREATE TABLE IF NOT EXISTS " + "policy_bonus_product" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "policy_id" + " VARCHAR," +
                "option_id" + " VARCHAR," +
                "bonus_product_id" + " VARCHAR," +
                "bonus_qty" + " Double," +
                "relation" + " VARCHAR," +
                "unit_id" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(Policy_Bonus_product);



        //.................................discount Policy ...........................................
        String discount_policy  = "CREATE TABLE IF NOT EXISTS " + "discount_policy" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "dis_policy" + " INT," +
                "start_date" + " DATETIME," +
                "end_date" + " DATETIME," +
                "total_price" + " DOUBLE," +
                "percentage" + " DOUBLE," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(discount_policy);

        sqLiteDatabase.execSQL("INSERT INTO discount_policy (dis_policy, start_date, end_date,percentage,total_price ) VALUES ( '1','2022-12-06','2023-01-31',10,1000 )");
        sqLiteDatabase.execSQL("INSERT INTO discount_policy (dis_policy, start_date, end_date,percentage,total_price ) VALUES ( '2','2022-12-13','2022-12-31',12,500 )");
        sqLiteDatabase.execSQL("INSERT INTO discount_policy (dis_policy, start_date, end_date,percentage,total_price ) VALUES ( '2','2022-12-13','2022-12-31',16,1501 )");
        sqLiteDatabase.execSQL("INSERT INTO discount_policy (dis_policy, start_date, end_date,percentage,total_price ) VALUES ( '2','2023-01-03','2023-02-28',8,500 )");
        sqLiteDatabase.execSQL("INSERT INTO discount_policy (dis_policy, start_date, end_date,percentage,total_price ) VALUES ( '2','2023-01-03','2023-02-28',10,1501 )");


        //.................................discount Policy Products...........................................
        String discount_policy_products  = "CREATE TABLE IF NOT EXISTS " + "discount_policy_products" + "("
                + "_id" + " INTEGER PRIMARY KEY," +
                "product_list" + " VARCHAR," +
                "updated_at" + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ")";
        sqLiteDatabase.execSQL(discount_policy_products);

        sqLiteDatabase.execSQL("INSERT INTO discount_policy_products (  product_list ) VALUES ( '135,136,137,138,142,143,144,145,146')");
        sqLiteDatabase.execSQL("INSERT INTO discount_policy_products (  product_list ) VALUES ( '51,52,451')");

        sqLiteDatabase.execSQL(Tables.CREATE_BANK_BRANCH_TABLE);
        sqLiteDatabase.execSQL(CREATE_BONUS_PARTY_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_BONUSES_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_CREDIT_COLLECTION_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_CURRENT_PROMOTION_PRODUCTS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DELETE_MEMO_DETAILS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DELETED_MEMOS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DEPOSIT_BALANCE_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DEPOSIT_DELETE_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DEPOSITS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DESIGNATION_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DISTRIBUTOR_PRODUCT_PRICES_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DISTRIBUTOR_PRODUCT_COMBINATIONS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_GENERAL_NOTICE_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_GIFT_ISSUE_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_GIFT_ISSUE_DETAILS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_GPS_TRACKER_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_GPS_TRACKING_TIME_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_GPS_TRACKING_GAPE_TIME_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_GPS_TRACKING_FAILED_TIME_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_INSTRUMENT_NUMBER_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_INSTRUMENT_TYPE_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_MARKETS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_MATERIALS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_MEMO_DETAILS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_MEMOS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_MEMOS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_MONTHLY_EFFECTIVE_CALL_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_NCP_CHALLAN_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_NCP_CHALLAN_DETAILS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_NCP_RETURNS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_STOCKS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_NGO_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_MESSAGES_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_OUTLET_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_OUTLET_VISIT_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_OUTLET_DALETE_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_OUTLETS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_PAYMENTS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_PRODUCT_BOOLEAN_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_PRODCUCT_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_PRODUCT_COMBINATIONS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_PRODUCT_HISTORY_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_PRODUCT_PRICE_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_PRODUCT_TYPE_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_PRODUCT_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_SALES_WEEKS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_SO_TARGETS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_TERRITORY_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_THANA_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_THANA_TERRITORY_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_UNIT_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_ROOT_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_VISIT_PLAN_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_ORDER_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_ORDER_DETAILS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_VISIT_PLAN_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DISCOUNT_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_TEMP_MEMOS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_TEMP_MEMO_DETAILS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_DIST_BONUS_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_FISCAL_YEAR);
        sqLiteDatabase.execSQL(Tables.CREATE_DataCheck_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_OFFER_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATED_OUTLET_PERMISSION_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_TEMP_OUTLETS_TABLE);
        sqLiteDatabase.execSQL(CREATE_IGNORE_OUTLET_FOR_POLICY_TABLE);
        sqLiteDatabase.execSQL(Tables.CREATE_TABLE_FRACTION);

        Log.e("DB Create test", "onCreate:  created db" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        oldv = i;
        newv = i1;
        Log.e("versionDB","N="+newv+" old="+oldv);


        if (i == 1 && i1 == 2) {
//            sqLiteDatabase.execSQL(Tables.DROP_TABLE + TABLE_NAME_CLASS);
//            sqLiteDatabase.execSQL(Tables.DROP_TABLE + Tables.TABLE_NAME_TEACHER);
            onCreate(sqLiteDatabase);
            return;
        }


        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_BANK_BRANCH);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_BONUS_PARTY);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_BONUSES);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_CREDIT_COLLECTION);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_CURRENT_PROMOTION_PRODUCTS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DELETE_MEMO_DETAILS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DELETED_MEMOS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DEPOSIT_BALANCE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DEPOSIT_DELETE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DEPOSITS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DESIGNATION);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DISTRIBUTOR_PRODUCT_PRICES);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DISTRIBUTOR_PRODUCT_COMBINATIONS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_GENERAL_NOTICE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_GIFT_ISSUE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_GIFT_ISSUE_DETAILS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_GPS_TRACKER);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_GPS_TRACKING_TIME);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_INSTRUMENT_NUMBER);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_INSTRUMENT_TYPE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_LOCATION);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_MARKETS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_MATERIALS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_MEMO_DETAILS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_MEMOS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_MONTHLY_EFFECTIVE_CALL);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_NCP_CHALLAN);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_NCP_CHALLAN_DETAILS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_NCP_RETURN);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_STOCKS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_NGO);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_MESSAGE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_OUTLET_CATEGORY);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_OUTLET_VISIT);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_OUTLET_DELETE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_OUTLETS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_PAYMENTS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_PRODUCT_BOOLEAN);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_PRODUCT_CATEGORY);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_PRODUCT_COMBINATION);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_PRODUCT_HISTORY);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_PRODUCT_PRICE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_PRODCUT_TYPE);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_PRODUCT);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_SALES_WEEK);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_SO_TARGETS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_TERRITORY);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_THANA);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_THANA_TERRITORY);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_UNIT);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_ROOT);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_VISIT_PLAN);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_ORDER_DETAILS);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_ORDER);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DISCOUNT);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_DIST_BONUS_PRODUCT);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.TABLE_NAME_FISCAL_YEAR);
        sqLiteDatabase.execSQL(Tables.DROP_TABLE+ Tables.CREATE_OFFER_TABLE);



        onCreate(sqLiteDatabase);

    }


    public long InsertTable(HashMap<String, String> data, String TableName) {

        try {

            ContentValues values = new ContentValues();

            Iterator it = data.entrySet().iterator();
            long insertedID = 0;

            while (it.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry) it.next();
                values.put(pair.getKey(), pair.getValue()); // Contact Name

                it.remove();
            }
            if (TableName.equalsIgnoreCase("product_history")) {
                Log.e("Content producthistory:", values.toString());
            }

            if (TableName.equalsIgnoreCase("delete_memo_details")) {
                Log.e("delete_memo_detailsLOG:", values.toString());
            }
            Log.e(" Content values  :", values.toString());
            Log.e(TableName, "   :" + "INSERTED");
            SQLiteDatabase db = this.getWritableDatabase();
            insertedID = db.insert(TableName, null, values);
            this.close();
            db.close();

            return insertedID;
        }catch (SQLiteAbortException e){
            Log.e("sqlexp",e.getMessage());
        }
        return 0;
    }
}
