
package com.srapp.Db_Actions;

import com.srapp.Util.Parent;

public class Tables extends Parent {

    public static final String DATABASE_NAME = "SR_Database_"+URL.VERSION;
    public static final int PROCESSING_PENDING = 0;
    public static final int PROCESSING_COMPELETE = 1;
    public static final int DALIVEERY_COMPELETE = 2;
    public static final String ORDER_DETAILS_policy_ID = "policy_id";
    public static final String Quantity = "quantity";
    public static final String BookedQuantity = "booked_quantity";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    public static final int DATABASE_VIRSION = 5;

    public static final String ProductName = "product_name";
    public static final String TargetQty = "target_qty";
    public static final String TargetVol = "target_vol";
    public static final String AchieveQty = "achieve_qty";
    public static final String AchieveVol = "achieve_vol";
    public static String MinPrice = "min_price";
    public static String MinQty = "min_qty";


    public static final String SR_ID = "sales_person_id";


    /*..................................dist_bonus_product......................................... */

    public static final String TABLE_NAME_DIST_BONUS_PRODUCT= "dist_bonus_product";

    public static final String DIST_BONUS_id = "_id";
    public static final String DIST_BONUS_PRODUCT_ID = "product_id";
    public static final String DIST_BONUS_PRODUCT_NAME= "product_name";
    public static final String DIST_BONUS_BONUS_QTY = "bonus_qty";

    public static final String DISCOUNT_created_at = "created_at";
    public static final String DISCOUNT_updated_at = "updated_at";

    public static final String DIST_BONUS[] = {TABLE_NAME_DIST_BONUS_PRODUCT,DIST_BONUS_id, DIST_BONUS_PRODUCT_ID, DIST_BONUS_PRODUCT_NAME,DIST_BONUS_BONUS_QTY,
            DISCOUNT_created_at, DISCOUNT_updated_at};
    public static final String CREATE_DIST_BONUS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DIST_BONUS_PRODUCT +
            " ( " +
            DIST_BONUS_id + " INTEGER PRIMARY KEY,   " +
            DIST_BONUS_PRODUCT_ID + "  INTEGER,   " +
            DIST_BONUS_PRODUCT_NAME + "  TEXT,   " +
            DIST_BONUS_BONUS_QTY + "  DOUBLE,   " +
            DISCOUNT_created_at + "  DATETIME,   " +
            DISCOUNT_updated_at + "  DATETIME );";

    /*..................................Bank Branch Table......................................... */

    public static final String TABLE_NAME_DISCOUNT = "discounts";

    public static final String DISCOUNT_table_id = "_id";
    public static final String DISCOUNT_DATE_FROM = "date_from";
    public static final String DISCOUNT_DATE_TO = "date_to";
    public static final String DISCOUNT_MEMO_VALUE = "memo_value";
    public static final String DISCOUNT_DISCOUNT_ID = "discount_id";
    public static final String DISCOUNT_DISCOUNT_TYPE = "discount_type";
    public static final String DISCOUNT_PERSENTANGE = "discount_percent";



    public static final String DISCOUNT[] = {TABLE_NAME_DISCOUNT,DISCOUNT_table_id, DISCOUNT_DISCOUNT_ID, DISCOUNT_DATE_FROM,DISCOUNT_DISCOUNT_TYPE,DISCOUNT_DATE_TO,DISCOUNT_MEMO_VALUE,DISCOUNT_PERSENTANGE,
            DISCOUNT_created_at, DISCOUNT_updated_at};
    public static final String CREATE_DISCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DISCOUNT +
            " ( " +
            DISCOUNT_table_id + " INTEGER PRIMARY KEY,   " +
            DISCOUNT_DISCOUNT_ID + "  INTEGER,   " +
            DISCOUNT_DISCOUNT_TYPE + "  INTEGER,   " +
            DISCOUNT_PERSENTANGE + "  DOUBLE,   " +
            DISCOUNT_DATE_FROM + "  DATE,   " +
            DISCOUNT_DATE_TO + "  DATE,   " +
            DISCOUNT_MEMO_VALUE + "  DOUBLE,   " +
            DISCOUNT_created_at + "  DATETIME,   " +
            DISCOUNT_updated_at + "  DATETIME );";




    public static final String TABLE_NAME_BANK_BRANCH = "bank_branch";

    public static final String BANK_BRANCH_table_id = "_id";
    public static final String BANK_BRANCH_bank_branch_id = "bank_branch_id";
    public static final String BANK_BRANCH_bank_branch_name = "bank_branch_name";
    public static final String BANK_BRANCH_created_at = "created_at";
    public static final String BANK_BRANCH_updated_at = "updated_at";


    public static final String BANK_BRANCH[] = {TABLE_NAME_BANK_BRANCH,BANK_BRANCH_table_id, BANK_BRANCH_bank_branch_id, BANK_BRANCH_bank_branch_name,
            BANK_BRANCH_created_at, BANK_BRANCH_updated_at};


    public static final String CREATE_BANK_BRANCH_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BANK_BRANCH +
            " ( " +
            BANK_BRANCH_table_id + " INTEGER PRIMARY KEY,   " +
            BANK_BRANCH_bank_branch_id + "  INTEGER,   " +
            BANK_BRANCH_bank_branch_name + "  VARCHAR,   " +
            BANK_BRANCH_created_at + "  DATETIME,   " +
            BANK_BRANCH_updated_at + "  DATETIME );";

    public static final String OUTLET_ID = "outlet_id";
    public static final String THANA_ID = "thana_id";
    public static final String MARKET_ID = "market_id";
    public static final String PRODUCT_ID = "product_id";
/*
    *//*..................................Territories Table......................................... *//*

    public static final String TABLE_NAME_Territories = "territories";

    public static final String Territories_table_id = "_id";
    public static final String Territories_teritory_id = "territory_id";
    public static final String Territories_teritory_name = "territory_name";
    public static final String Territories_created_at = "created_at";
    public static final String Territories_updated_at = "updated_at";


    public static final String Territories[] = {TABLE_NAME_Territories,Territories_table_id, Territories_teritory_id, Territories_teritory_name,
            Territories_created_at, Territories_updated_at};


    public static final String CREATE_Territories_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_Territories +
            " ( " +
            Territories_table_id + " INTEGER PRIMARY KEY,   " +
            Territories_teritory_id + "  INTEGER,   " +
            Territories_teritory_name + "  VARCHAR,   " +
            Territories_created_at + "  DATETIME,   " +
            Territories_updated_at + "  DATETIME );";*/


    /*..................................Bonus Party Table......................................... */

    public static final String TABLE_NAME_BONUS_PARTY = "bonus_party";

    public static final String BONUS_PARTY_table_id = "_id";
    public static final String BONUS_PARTY_bonus_party_id = "bonus_party_id";
    public static final String BONUS_PARTY_market_id = "market_id";
    public static final String BONUS_PARTY_outlet_id = "outlet_id";
    public static final String BONUS_PARTY_product_id = "product_id";
    public static final String BONUS_PARTY_target_quantity = "target_quantity";
    public static final String BONUS_PARTY_achieve_quantity = "achieve_quantity";
    public static final String BONUS_PARTY_call_quantity = "call_quantity";
    public static final String BONUS_PARTY_stump = "stump";
    public static final String BONUS_PARTY_is_pushed = "is_pushed";
    public static final String BONUS_PARTY_created_at = "created_at";
    public static final String BONUS_PARTY_updated_at = "updated_at";


    public static final String BONUS_PARTY[] = {TABLE_NAME_BONUS_PARTY,BONUS_PARTY_table_id, BONUS_PARTY_bonus_party_id, MARKET_ID,
            OUTLET_ID, BONUS_PARTY_product_id, BONUS_PARTY_target_quantity, BONUS_PARTY_achieve_quantity, BONUS_PARTY_call_quantity,
            BONUS_PARTY_stump, BONUS_PARTY_is_pushed, BONUS_PARTY_created_at, BONUS_PARTY_updated_at};


    public static final String CREATE_BONUS_PARTY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BONUS_PARTY +
            " ( " +
            BONUS_PARTY_table_id + " INTEGER PRIMARY KEY,   " +
            BONUS_PARTY_bonus_party_id + "  INTEGER,   " +
            BONUS_PARTY_market_id + "  INTEGER,   " +
            BONUS_PARTY_outlet_id + "  INTEGER,   " +
            BONUS_PARTY_product_id + "  INTEGER,   " +
            BONUS_PARTY_target_quantity + " DOUBLE, " +
            BONUS_PARTY_achieve_quantity + "  DOUBLE,   " +
            BONUS_PARTY_call_quantity + "  DOUBLE,   " +
            BONUS_PARTY_stump + "  DOUBLE,   " +
            BONUS_PARTY_is_pushed + "  INTEGER,   " +
            BONUS_PARTY_created_at + "  DATETIME,   " +
            BONUS_PARTY_updated_at + "  DATETIME );";

    /*..................................Bonuses Table......................................... */

    public static final String TABLE_NAME_BONUSES = "bonuses";

    public static final String BONUSES_table_id = "_id";
    public static final String BONUSES_mother_product_id = "mother_product_id";
    public static final String BONUSES_mother_product_qty = "mother_product_qty";
    public static final String BONUSES_bonus_product_id = "bonus_product_id";
    public static final String BONUSES_bonus_product_qty = "bonus_product_qty";
    public static final String BONUSES_start_date = "start_date";
    public static final String BONUSES_end_date = "end_date";
    public static final String BONUSES_created_at = "created_at";
    public static final String BONUSES_updated_at = "updated_at";


    public static final String BONUSES[] = {TABLE_NAME_BONUSES,BONUSES_table_id,BONUSES_bonus_product_id, BONUSES_mother_product_id, BONUSES_mother_product_qty,  BONUSES_bonus_product_qty,
            BONUSES_start_date, BONUSES_end_date, BONUSES_created_at, BONUSES_updated_at};


    public static final String CREATE_BONUSES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BONUSES +
            " ( " +
            BONUSES_table_id + " INTEGER PRIMARY KEY,   " +
            BONUSES_mother_product_id + "  INTEGER,   " +
            BONUSES_mother_product_qty + "  DOUBLE,   " +
            BONUSES_bonus_product_id + "  INTEGER,   " +
            BONUSES_bonus_product_qty + "  DOUBLE,   " +
            BONUSES_start_date + "  DATE,   " +
            BONUSES_end_date + "  DATE , " +
            BONUSES_created_at + "  DATETIME , " +
            BONUSES_updated_at + "  DATETIME );";

    /*..................................Credit Collection......................................... */

    public static final String TABLE_NAME_CREDIT_COLLECTION = "credit_collection";

    public static final String CREDIT_COLLECTION_table_id = "_id";
    public static final String CREDIT_COLLECTION_outlet_id = "outlet_id";
    public static final String CREDIT_COLLECTION_memo_number = "memo_number";
    public static final String CREDIT_COLLECTION_memo_value = "memo_value";
    public static final String CREDIT_COLLECTION_date = "date";
    public static final String CREDIT_COLLECTION_collection_date = "collection_date";
    public static final String CREDIT_COLLECTION_instrument_type = "instrument_type";
    public static final String CREDIT_COLLECTION_due_amount = "due_amount";
    public static final String CREDIT_COLLECTION_paid_amount = "paid_amount";
    public static final String CREDIT_COLLECTION_is_pushed = "is_pushed";
    public static final String CREDIT_COLLECTION_created_at = "created_at";
    public static final String CREDIT_COLLECTION_updated_at = "updated_at";


    public static final String CREDIT_COLLECTION[] = {TABLE_NAME_CREDIT_COLLECTION,CREDIT_COLLECTION_table_id, CREDIT_COLLECTION_outlet_id, CREDIT_COLLECTION_memo_number, CREDIT_COLLECTION_memo_value, CREDIT_COLLECTION_date,
            CREDIT_COLLECTION_collection_date, CREDIT_COLLECTION_instrument_type,SR_ID, CREDIT_COLLECTION_due_amount, CREDIT_COLLECTION_paid_amount, CREDIT_COLLECTION_is_pushed,
            CREDIT_COLLECTION_created_at, CREDIT_COLLECTION_updated_at};


    public static final String CREATE_CREDIT_COLLECTION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CREDIT_COLLECTION +
            " ( " +
            CREDIT_COLLECTION_table_id + " INTEGER PRIMARY KEY,   " +
            CREDIT_COLLECTION_outlet_id + "  INTEGER,   " +
            CREDIT_COLLECTION_memo_number + "  VARCHAR,   " +
            SR_ID + "  VARCHAR,   " +
            CREDIT_COLLECTION_memo_value + "  DOUBLE,   " +
            CREDIT_COLLECTION_date + "  DATE,   " +
            CREDIT_COLLECTION_collection_date + "  DATE,   " +
            CREDIT_COLLECTION_instrument_type + "  INTEGER , " +
            CREDIT_COLLECTION_due_amount + "  DOUBLE,   " +
            CREDIT_COLLECTION_paid_amount + "  DOUBLE,   " +
            CREDIT_COLLECTION_is_pushed + "  INTEGER,   " +
            CREDIT_COLLECTION_created_at + "  DATETIME , " +
            CREDIT_COLLECTION_updated_at + "  DATETIME );";

    /*..................................Current Promotion Products......................................... */

    public static final String TABLE_NAME_CURRENT_PROMOTION_PRODUCTS = "current_promotion_products";

    public static final String CURRENT_PROMOTION_PRODUCTS_table_id = "_id";
    public static final String CURRENT_PROMOTION_PRODUCTS_current_promotion_id = "current_promotion_id";
    public static final String CURRENT_PROMOTION_PRODUCTS_product_id = "product_id";
    public static final String CURRENT_PROMOTION_PRODUCTS_created_at = "created_at";
    public static final String CURRENT_PROMOTION_PRODUCTS_updated_at = "updated_at";

    public static final String CURRENT_PROMOTION_PRODUCTS[] = {TABLE_NAME_CURRENT_PROMOTION_PRODUCTS,CURRENT_PROMOTION_PRODUCTS_table_id, CURRENT_PROMOTION_PRODUCTS_current_promotion_id,
            CURRENT_PROMOTION_PRODUCTS_product_id, CURRENT_PROMOTION_PRODUCTS_created_at, CURRENT_PROMOTION_PRODUCTS_updated_at};


    public static final String CREATE_CURRENT_PROMOTION_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CURRENT_PROMOTION_PRODUCTS +
            " ( " +
            CURRENT_PROMOTION_PRODUCTS_table_id + " INTEGER PRIMARY KEY,   " +
            CURRENT_PROMOTION_PRODUCTS_current_promotion_id + "  INTEGER,   " +
            CURRENT_PROMOTION_PRODUCTS_product_id + "  INTEGER,   " +
            CURRENT_PROMOTION_PRODUCTS_created_at + "  DATETIME , " +
            CURRENT_PROMOTION_PRODUCTS_updated_at + "  DATETIME );";

    /*..................................Delete Memo Details......................................... */

    public static final String TABLE_NAME_DELETE_MEMO_DETAILS = "delete_memo_details";

    public static final String DELETE_MEMO_DETAILS_table_id = "_id";
    public static final String DELETE_MEMO_DETAILS_memo_number = "memo_number";
    public static final String DELETE_MEMO_DETAILS_product_id = "product_id";
    public static final String DELETE_MEMO_DETAILS_quantity = "quantity";
    public static final String DELETE_MEMO_DETAILS_price = "price";
    public static final String DELETE_MEMO_DETAILS_memo_date = "memo_date";
    public static final String DELETE_MEMO_DETAILS_is_bonus = "is_bonus";
    public static final String DELETE_MEMO_DETAILS_deletion_time = "delete_time";
    public static final String DELETE_MEMO_DETAILS_created_at = "created_at";
    public static final String DELETE_MEMO_DETAILS_updated_at = "updated_at";

    public static final String DELETE_MEMO_DETAILS[] = {TABLE_NAME_DELETE_MEMO_DETAILS,DELETE_MEMO_DETAILS_table_id, DELETE_MEMO_DETAILS_memo_number,
            DELETE_MEMO_DETAILS_product_id, DELETE_MEMO_DETAILS_quantity, DELETE_MEMO_DETAILS_price, DELETE_MEMO_DETAILS_memo_date, DELETE_MEMO_DETAILS_is_bonus, DELETE_MEMO_DETAILS_deletion_time,
            DELETE_MEMO_DETAILS_created_at, DELETE_MEMO_DETAILS_updated_at};


    public static final String CREATE_DELETE_MEMO_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DELETE_MEMO_DETAILS +
            " ( " +
            DELETE_MEMO_DETAILS_table_id + " INTEGER PRIMARY KEY,   " +
            DELETE_MEMO_DETAILS_memo_number + "  INTEGER,   " +
            DELETE_MEMO_DETAILS_product_id + "  INTEGER,   " +
            DELETE_MEMO_DETAILS_quantity + "  DOUBLE,   " +
            DELETE_MEMO_DETAILS_price + "  DOUBLE,   " +
            DELETE_MEMO_DETAILS_memo_date + "  DATE,   " +
            DELETE_MEMO_DETAILS_is_bonus + "  INTEGER,   " +
            DELETE_MEMO_DETAILS_deletion_time + "  DATETIME,   " +
            DELETE_MEMO_DETAILS_created_at + "  DATETIME , " +
            DELETE_MEMO_DETAILS_updated_at + "  DATETIME );";

    /*..................................Deleted Memos......................................... */

    public static final String TABLE_NAME_DELETED_MEMOS = "deleted_memos";

    public static final String DELETED_MEMOS_table_id = "_id";
    public static final String DELETED_MEMOS_memo_number = "memo_number";
    public static final String DELETED_MEMOS_memo_date_time = "memo_date_time";
    public static final String DELETED_MEMOS_memo_date = "memo_date";
    public static final String DELETED_MEMOS_sales_person_id = "sales_person_id";
    public static final String DELETED_MEMOS_sales_to = "sales_to";
    public static final String DELETED_MEMOS_outlet_id = "outlet_id";
    public static final String DELETED_MEMOS_market_id = "market_id";
    public static final String DELETED_MEMOS_gross_value = "gross_value";
    public static final String DELETED_MEMOS_cash_received = "cash_received";
    public static final String DELETED_MEMOS_credit_amount = "credit_amount";
    public static final String DELETED_MEMOS_is_active = "is_active";
    public static final String DELETED_MEMOS_latitude = "latitude";
    public static final String DELETED_MEMOS_longitude = "longitude";
    public static final String DELETED_MEMOS_from_app = "from_app";
    public static final String DELETED_MEMOS_for_memo_delete = "for_memo_delete";
    public static final String DELETED_MEMOS_editable = "editable";
    public static final String DELETED_MEMOS_is_pushed = "is_pushed";
    public static final String DELETED_MEMOS_deletion_time = "deletion_time";
    public static final String DELETED_MEMOS_created_at = "created_at";
    public static final String DELETED_MEMOS_updated_at = "updated_at";

    public static final String DELETED_MEMOS[] = {TABLE_NAME_DELETED_MEMOS,DELETED_MEMOS_table_id, DELETED_MEMOS_memo_number,
            DELETED_MEMOS_memo_date_time, DELETED_MEMOS_memo_date, DELETED_MEMOS_sales_person_id, DELETED_MEMOS_sales_to, DELETED_MEMOS_outlet_id,
            DELETED_MEMOS_market_id, DELETED_MEMOS_gross_value, DELETED_MEMOS_cash_received,
            DELETED_MEMOS_credit_amount, DELETED_MEMOS_is_active,SR_ID, DELETED_MEMOS_latitude, DELETED_MEMOS_longitude,
            DELETED_MEMOS_from_app, DELETED_MEMOS_for_memo_delete, DELETED_MEMOS_editable, DELETED_MEMOS_is_pushed,
            DELETED_MEMOS_deletion_time, DELETED_MEMOS_created_at, DELETED_MEMOS_updated_at};


    public static final String CREATE_DELETED_MEMOS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DELETED_MEMOS +
            " ( " +
            DELETED_MEMOS_table_id + " INTEGER PRIMARY KEY,   " +
            DELETED_MEMOS_memo_number + "  INTEGER,   " +
            DELETED_MEMOS_memo_date_time + "  DATETIME,   " +
            DELETED_MEMOS_memo_date + " DATETIME," +
            DELETED_MEMOS_sales_person_id + "  INTEGER,   " +
            DELETED_MEMOS_sales_to + "  INTEGER,   " +
            DELETED_MEMOS_outlet_id + "  INTEGER,   " +
            DELETED_MEMOS_market_id + "  INTEGER,   " +
            DELETED_MEMOS_gross_value + "  DOUBLE,   " +
            DELETED_MEMOS_cash_received + "  DOUBLE,   " +
            DELETED_MEMOS_credit_amount + "  DOUBLE,   " +
            DELETED_MEMOS_is_active + "  INTEGER,   " +
            DELETED_MEMOS_latitude + "  DOUBLE,   " +
            DELETED_MEMOS_longitude + "  DOUBLE,   " +
            DELETED_MEMOS_from_app + "  VARCHAR,   " +
            DELETED_MEMOS_for_memo_delete + "  INTEGER,   " +
            DELETED_MEMOS_editable + "  INTEGER,   " +
            DELETED_MEMOS_is_pushed + "  INTEGER,   " +
            DELETED_MEMOS_deletion_time + "  DATETIME,   " +
            DELETED_MEMOS_created_at + "  DATETIME , " +
            DELETED_MEMOS_updated_at + "  DATETIME );";

    /*..................................Deposit Balance......................................... */

    public static final String TABLE_NAME_DEPOSIT_BALANCE = "deposit_balance";

    public static final String DEPOSIT_BALANCE_table_id = "_id";
    public static final String DEPOSIT_BALANCE_deposit_balance_amount = "deposit_balance_amount";
    public static final String DEPOSIT_BALANCE_instrument_type_id = "instrument_type_id";
    public static final String DEPOSIT_BALANCE_created_at = "created_at";
    public static final String DEPOSIT_BALANCE_updated_at = "updated_at";

    public static final String DEPOSIT_BALANCE[] = {TABLE_NAME_DEPOSIT_BALANCE,DEPOSIT_BALANCE_table_id, DEPOSIT_BALANCE_deposit_balance_amount,
            DEPOSIT_BALANCE_instrument_type_id, DEPOSIT_BALANCE_created_at, DEPOSIT_BALANCE_updated_at};


    public static final String CREATE_DEPOSIT_BALANCE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DEPOSIT_BALANCE +
            " ( " +
            DEPOSIT_BALANCE_table_id + " INTEGER PRIMARY KEY,   " +
            DEPOSIT_BALANCE_deposit_balance_amount + "  INTEGER,   " +
            DEPOSIT_BALANCE_instrument_type_id + "  INTEGER,   " +
            DEPOSIT_BALANCE_created_at + "  DATETIME , " +
            DEPOSIT_BALANCE_updated_at + "  DATETIME );";

    /*..................................fiscal_year......................................... */

    public static final String TABLE_NAME_FISCAL_YEAR = "fiscal_year";

    public static final String FISCAL_YEAR_table_id = "_id";
    public static final String FISCAL_YEAR_id = "id";
    public static final String FISCAL_YEAR_YEAR_CODE = "year_code";
    public static final String FISCAL_YEAR_START_DATE = "start_date";
    public static final String FISCAL_YEAR_END_DATE = "end_date";
    public static final String FISCAL_YEAR_created_at = "created_at";
    public static final String FISCAL_YEAR_updated_at = "updated_at";

    public static final String FISCAL_YEAR[] = {TABLE_NAME_FISCAL_YEAR,FISCAL_YEAR_table_id, FISCAL_YEAR_id,
            FISCAL_YEAR_YEAR_CODE, FISCAL_YEAR_START_DATE, FISCAL_YEAR_END_DATE,FISCAL_YEAR_created_at,FISCAL_YEAR_updated_at};


    public static final String CREATE_FISCAL_YEAR = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_FISCAL_YEAR +
            " ( " +
            FISCAL_YEAR_table_id + " INTEGER PRIMARY KEY,   " +
            FISCAL_YEAR_id + "  INTEGER,   " +
            FISCAL_YEAR_YEAR_CODE + "  VARCHAR,   " +
            FISCAL_YEAR_START_DATE + "  DATETIME , " +
            FISCAL_YEAR_END_DATE + "  DATETIME , " +
            FISCAL_YEAR_created_at + "  DATETIME , " +
            FISCAL_YEAR_updated_at + "  DATETIME );";

    /*..................................Deposit Delete......................................... */

    public static final String TABLE_NAME_DEPOSIT_DELETE = "deposit_delete";

    public static final String DEPOSIT_DELETE_table_id = "_id";
    public static final String DEPOSIT_DELETE_deposit_id = "deposit_id";
    public static final String DEPOSIT_DELETE_is_pushed = "is_pushed";
    public static final String DEPOSIT_DELETE_created_at = "created_at";
    public static final String DEPOSIT_DELETE_updated_at = "updated_at";

    public static final String DEPOSIT_DELETE[] = {TABLE_NAME_DEPOSIT_DELETE,DEPOSIT_DELETE_table_id, DEPOSIT_DELETE_deposit_id, DEPOSIT_DELETE_is_pushed,
            DEPOSIT_DELETE_created_at, DEPOSIT_DELETE_updated_at};


    public static final String CREATE_DEPOSIT_DELETE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DEPOSIT_DELETE +
            " ( " +
            DEPOSIT_DELETE_table_id + " INTEGER PRIMARY KEY,   " +
            DEPOSIT_DELETE_deposit_id + "  INTEGER,   " +
            DEPOSIT_DELETE_is_pushed + "  INTEGER,   " +
            DEPOSIT_DELETE_created_at + "  DATETIME , " +
            DEPOSIT_DELETE_updated_at + "  DATETIME );";

    /*..................................Deposits................................................... */

    public static final String TABLE_NAME_DEPOSITS = "deposits";

    public static final String DEPOSITS_table_id = "_id";
    public static final String DEPOSITS_deposit_id = "deposit_id";
    public static final String DEPOSITS_instrument_type_id = "instrument_type_id";
    public static final String DEPOSITS_instrument_id = "instrument_id";
    public static final String DEPOSITS_instrument_date = "instrument_date";
    public static final String DEPOSITS_instrument_number_id = "instrument_number_id";
    public static final String DEPOSITS_instrument_reference_number = "instrument_reference_number";
    public static final String DEPOSITS_deposit_slip_number = "deposit_slip_number";
    public static final String DEPOSITS_deposit_slip_date = "deposit_slip_date";
    public static final String DEPOSITS_sales_week_id = "sales_week_id";
    public static final String DEPOSITS_deposit_amount = "deposit_amount";
    public static final String DEPOSITS_bank_branch_id = "bank_branch_id";
    public static final String DEPOSITS_memo_number = "memo_number";
    public static final String DEPOSITS_is_pushed = "is_pushed";
    public static final String DEPOSITS_deposit_date = "deposit_date";
    public static final String DEPOSITS_payment_id = "payment_id";
    public static final String DEPOSITS_created_at = "created_at";
    public static final String DEPOSITS_updated_at = "updated_at";

    public static final String DEPOSITS[] = {TABLE_NAME_DEPOSITS,DEPOSITS_table_id, DEPOSITS_deposit_id, DEPOSITS_instrument_type_id, DEPOSITS_instrument_id,
            DEPOSITS_instrument_date, DEPOSITS_instrument_number_id, DEPOSITS_instrument_reference_number, DEPOSITS_deposit_slip_number,
            DEPOSITS_deposit_slip_date, DEPOSITS_sales_week_id, DEPOSITS_deposit_amount, DEPOSITS_bank_branch_id,
             DEPOSITS_memo_number, DEPOSITS_is_pushed, DEPOSITS_deposit_date, DEPOSITS_payment_id,
            DEPOSITS_created_at, DEPOSITS_updated_at};


    public static final String CREATE_DEPOSITS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DEPOSITS +
            " ( " +
            DEPOSITS_table_id + " INTEGER PRIMARY KEY,   " +
            DEPOSITS_deposit_id + "  INTEGER,   " +
            DEPOSITS_instrument_type_id + "  INTEGER,   " +
            DEPOSITS_instrument_id + "  INTEGER,   " +
            DEPOSITS_instrument_date + "  DATE,   " +
            DEPOSITS_instrument_number_id + "  INTEGER,   " +
            DEPOSITS_instrument_reference_number + "  VARCHAR,   " +
            DEPOSITS_deposit_slip_number + "  VARCHAR,   " +
            DEPOSITS_deposit_slip_date + "  DATE,   " +
            DEPOSITS_sales_week_id + "  INTEGER,   " +
            DEPOSITS_deposit_amount + "  DOUBLE,   " +
            DEPOSITS_bank_branch_id + "  INTEGER,   " +
            DEPOSITS_memo_number + "  VARCHAR,   " +
            DEPOSITS_is_pushed + "  INTEGER,   " +
            DEPOSITS_deposit_date + "  DATE,   " +
            DEPOSITS_payment_id + "  VARCHAR,   " +
            DEPOSITS_created_at + "  DATETIME , " +
            DEPOSITS_updated_at + "  DATETIME );";

    /*..................................DESIGNATION......................................... */

    public static final String TABLE_NAME_DESIGNATION = "designation";

    public static final String DESIGNATION_table_id = "_id";
    public static final String DESIGNATION_designation_id = "designation_id";
    public static final String DESIGNATION_designation_name = "designation_name";
    public static final String DESIGNATION_created_at = "created_at";
    public static final String DESIGNATION_updated_at = "updated_at";

    public static final String DESIGNATION[] = {TABLE_NAME_DESIGNATION,DESIGNATION_table_id, DESIGNATION_designation_id, DESIGNATION_designation_name,
            DESIGNATION_created_at, DESIGNATION_updated_at};


    public static final String CREATE_DESIGNATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DESIGNATION +
            " ( " +
            DESIGNATION_table_id + " INTEGER PRIMARY KEY,   " +
            DESIGNATION_designation_id + "  INTEGER,   " +
            DESIGNATION_designation_name + "  VARCHAR,   " +
            DESIGNATION_created_at + "  DATETIME , " +
            DESIGNATION_updated_at + "  DATETIME );";

    /*..................................DISTRIBUTOR PRODUCT PRICES......................................... */

    public static final String TABLE_NAME_DISTRIBUTOR_PRODUCT_PRICES = "distributor_product_prices";

    public static final String DISTRIBUTOR_PRODUCT_PRICES_table_id = "_id";
    public static final String DISTRIBUTOR_PRODUCT_PRICES_product_id = "product_id";
    public static final String DISTRIBUTOR_PRODUCT_PRICES_target_customer = "target_customer";
    public static final String DISTRIBUTOR_PRODUCT_PRICES_institute_id = "institute_id";
    public static final String DISTRIBUTOR_PRODUCT_PRICES_price = "price";
    public static final String DISTRIBUTOR_PRODUCT_PRICES_effective_date = "effective_date";
    public static final String DISTRIBUTOR_PRODUCT_PRICES_created_at = "created_at";
    public static final String DISTRIBUTOR_PRODUCT_PRICES_updated_at = "updated_at";

    public static final String DISTRIBUTOR_PRODUCT_PRICES[] = {TABLE_NAME_DISTRIBUTOR_PRODUCT_PRICES,DISTRIBUTOR_PRODUCT_PRICES_table_id, DISTRIBUTOR_PRODUCT_PRICES_product_id,
            DISTRIBUTOR_PRODUCT_PRICES_target_customer, DISTRIBUTOR_PRODUCT_PRICES_institute_id, DISTRIBUTOR_PRODUCT_PRICES_price,
            DISTRIBUTOR_PRODUCT_PRICES_effective_date, DISTRIBUTOR_PRODUCT_PRICES_created_at, DISTRIBUTOR_PRODUCT_PRICES_updated_at};


    public static final String CREATE_DISTRIBUTOR_PRODUCT_PRICES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DISTRIBUTOR_PRODUCT_PRICES +
            " ( " +
            DISTRIBUTOR_PRODUCT_PRICES_table_id + " INTEGER PRIMARY KEY,   " +
            DISTRIBUTOR_PRODUCT_PRICES_product_id + "  INTEGER,   " +
            DISTRIBUTOR_PRODUCT_PRICES_target_customer + "  INTEGER,   " +
            DISTRIBUTOR_PRODUCT_PRICES_institute_id + "  INTEGER,   " +
            DISTRIBUTOR_PRODUCT_PRICES_price + "  DOUBLE,   " +
            DISTRIBUTOR_PRODUCT_PRICES_effective_date + "  DATE,   " +
            DISTRIBUTOR_PRODUCT_PRICES_created_at + "  DATETIME , " +
            DISTRIBUTOR_PRODUCT_PRICES_updated_at + "  DATETIME );";


    /*..................................DISTRIBUTOR PRODUCT COMBINATIONS......................................... */

    public static final String TABLE_NAME_DISTRIBUTOR_PRODUCT_COMBINATIONS = "distributor_product_combinations";

    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS_table_id = "_id";
    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS_product_id = "product_id";
    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS_combination_id = "combination_id";
    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS_minimum_quantity = "minimum_quantity";
    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS_price = "price";
    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS_effective_date = "effective_date";
    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS_created_at = "created_at";
    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS_updated_at = "updated_at";

    public static final String DISTRIBUTOR_PRODUCT_COMBINATIONS[] = {TABLE_NAME_DISTRIBUTOR_PRODUCT_COMBINATIONS,DISTRIBUTOR_PRODUCT_COMBINATIONS_table_id, DISTRIBUTOR_PRODUCT_COMBINATIONS_product_id,
            DISTRIBUTOR_PRODUCT_COMBINATIONS_combination_id, DISTRIBUTOR_PRODUCT_COMBINATIONS_minimum_quantity, DISTRIBUTOR_PRODUCT_COMBINATIONS_price,
            DISTRIBUTOR_PRODUCT_COMBINATIONS_effective_date, DISTRIBUTOR_PRODUCT_COMBINATIONS_created_at, DISTRIBUTOR_PRODUCT_COMBINATIONS_updated_at};


    public static final String CREATE_DISTRIBUTOR_PRODUCT_COMBINATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DISTRIBUTOR_PRODUCT_COMBINATIONS +
            " ( " +
            DISTRIBUTOR_PRODUCT_COMBINATIONS_table_id + " INTEGER PRIMARY KEY,   " +
            DISTRIBUTOR_PRODUCT_COMBINATIONS_product_id + "  INTEGER,   " +
            DISTRIBUTOR_PRODUCT_COMBINATIONS_combination_id + "  INTEGER,   " +
            DISTRIBUTOR_PRODUCT_COMBINATIONS_minimum_quantity + "  DOUBLE,   " +
            DISTRIBUTOR_PRODUCT_COMBINATIONS_price + "  DOUBLE,   " +
            DISTRIBUTOR_PRODUCT_COMBINATIONS_effective_date + "  DATE,   " +
            DISTRIBUTOR_PRODUCT_COMBINATIONS_created_at + "  DATETIME , " +
            DISTRIBUTOR_PRODUCT_COMBINATIONS_updated_at + "  DATETIME );";

    /*..................................General Notice......................................... */

    public static final String TABLE_NAME_GENERAL_NOTICE = "general_notice";

    public static final String GENERAL_NOTICE_table_id = "_id";
    public static final String GENERAL_NOTICE_notice_id = "notice_id";
    public static final String GENERAL_NOTICE_notice_title = "notice_title";
    public static final String GENERAL_NOTICE_created_at = "created_at";
    public static final String GENERAL_NOTICE_updated_at = "updated_at";

    public static final String GENERAL_NOTICE[] = {TABLE_NAME_GENERAL_NOTICE,GENERAL_NOTICE_table_id,
            GENERAL_NOTICE_notice_id, GENERAL_NOTICE_notice_title, GENERAL_NOTICE_created_at, GENERAL_NOTICE_updated_at};


    public static final String CREATE_GENERAL_NOTICE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_GENERAL_NOTICE +
            " ( " +
            GENERAL_NOTICE_table_id + " INTEGER PRIMARY KEY,   " +
            GENERAL_NOTICE_notice_id + "  INTEGER,   " +
            GENERAL_NOTICE_notice_title + "  VARCHAR,   " +
            GENERAL_NOTICE_created_at + "  DATETIME , " +
            GENERAL_NOTICE_updated_at + "  DATETIME );";

    /*..................................GIFT ISSUE......................................... */

    public static final String TABLE_NAME_GIFT_ISSUE = "gift_issue";

    public static final String GIFT_ISSUE_table_id = "_id";
    public static final String GIFT_ISSUE_outlet_id = "outlet_id";
    public static final String GIFT_ISSUE_gift_issue_id = "gift_issue_id";
    public static final String GIFT_ISSUE_gift_issued_by = "gift_issued_by";
    public static final String GIFT_ISSUE_gift_issue_date = "gift_issue_date";
    public static final String GIFT_ISSUE_memo_numer = "order_number";
    public static final String GIFT_ISSUE_is_pushed = "is_pushed";
    public static final String GIFT_ISSUE_created_at = "created_at";
    public static final String GIFT_ISSUE_updated_at = "updated_at";

    public static final String GIFT_ISSUE[] = {TABLE_NAME_GIFT_ISSUE,GIFT_ISSUE_table_id,
            GIFT_ISSUE_gift_issue_id, GIFT_ISSUE_outlet_id,  GIFT_ISSUE_gift_issued_by, GIFT_ISSUE_gift_issue_date, GIFT_ISSUE_memo_numer,
            GIFT_ISSUE_is_pushed, GIFT_ISSUE_created_at, GIFT_ISSUE_updated_at};


    public static final String CREATE_GIFT_ISSUE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_GIFT_ISSUE +
            " ( " +
            GIFT_ISSUE_table_id + " INTEGER PRIMARY KEY,   " +
            GIFT_ISSUE_outlet_id + "  INTEGER,   " +
            GIFT_ISSUE_gift_issue_id + "  INTEGER,   " +
            GIFT_ISSUE_gift_issued_by + "  INTEGER , " +
            GIFT_ISSUE_gift_issue_date + "  DATE , " +
            GIFT_ISSUE_memo_numer + "  VARCHAR , " +
            GIFT_ISSUE_is_pushed + "  INTEGER , " +
            GIFT_ISSUE_created_at + "  DATETIME , " +
            GIFT_ISSUE_updated_at + "  DATETIME );";

    /*..................................GIFT ISSUE DETAILS......................................... */

    public static final String TABLE_NAME_GIFT_ISSUE_DETAILS = "gift_issue_details";

    public static final String GIFT_ISSUE_DETAILS_table_id = "_id";
    public static final String GIFT_ISSUE_DETAILS_productt_id = "product_id";
    public static final String GIFT_ISSUE_DETAILS_gift_issue_id = "gift_issue_id";
    public static final String GIFT_ISSUE_DETAILS_quantity = "quantity";
    ;
    public static final String GIFT_ISSUE_DETAILS_created_at = "created_at";
    public static final String GIFT_ISSUE_DETAILS_updated_at = "updated_at";

    public static final String GIFT_ISSUE_DETAILS[] = {TABLE_NAME_GIFT_ISSUE_DETAILS,GIFT_ISSUE_DETAILS_table_id,  GIFT_ISSUE_DETAILS_gift_issue_id,GIFT_ISSUE_DETAILS_productt_id,
            GIFT_ISSUE_DETAILS_quantity,GIFT_ISSUE_DETAILS_created_at,GIFT_ISSUE_DETAILS_updated_at};


    public static final String CREATE_GIFT_ISSUE_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_GIFT_ISSUE_DETAILS +
            " ( " +
            GIFT_ISSUE_DETAILS_table_id + " INTEGER PRIMARY KEY,   " +
            GIFT_ISSUE_DETAILS_productt_id + "  INTEGER,   " +
            GIFT_ISSUE_DETAILS_gift_issue_id + "  INTEGER,   " +
            GIFT_ISSUE_DETAILS_quantity + "  DOUBLE,   " +
            GIFT_ISSUE_DETAILS_created_at + "  DATETIME , " +
            GIFT_ISSUE_DETAILS_updated_at + "  DATETIME );";

    /*..................................GPS TRACKER......................................... */

    public static final String TABLE_NAME_GPS_TRACKER = "gps_tracker";

    public static final String GPS_TRACKER_table_id = "_id";
    public static final String GPS_TRACKER_latitude = "latitude";
    public static final String GPS_TRACKER_longitude = "longitude";
    public static final String GPS_TRACKER_GPS_BTS = "gps_bts";
    public static final String GPS_TRACKER_is_pushed = "is_pushed";
    public static final String GPS_TRACKER_created_at = "created_at";
    public static final String GPS_TRACKER_updated_at = "updated_at";

    public static final String GPS_TRACKER[] = {TABLE_NAME_GPS_TRACKER,GPS_TRACKER_table_id, GPS_TRACKER_latitude,
            GPS_TRACKER_longitude, GPS_TRACKER_GPS_BTS, GPS_TRACKER_is_pushed, GPS_TRACKER_created_at, GPS_TRACKER_updated_at};


    public static final String CREATE_GPS_TRACKER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_GPS_TRACKER +
            " ( " +
            GPS_TRACKER_table_id + " INTEGER PRIMARY KEY,   " +
            GPS_TRACKER_latitude + "  DOUBLE,   " +
            GPS_TRACKER_longitude + "  DOUBLE,   " +
            GPS_TRACKER_GPS_BTS + "  VARCHAR,   " +
            GPS_TRACKER_is_pushed + "  INTEGER,   " +
            GPS_TRACKER_created_at + "  DATETIME , " +
            GPS_TRACKER_updated_at + "  DATETIME );";

    /*..................................GPS TRACKING TIME......................................... */

    public static final String TABLE_NAME_GPS_TRACKING_TIME = "gps_tracking_time";

    public static final String GPS_TRACKING_TIME_table_id = "_id";
    public static final String GPS_TRACKING_TIME_start_tme = "start_tme";
    public static final String GPS_TRACKING_TIME_end_time = "end_time";
    public static final String GPS_TRACKING_TIME_interval = "interval";
    public static final String GPS_TRACKING_TIME_created_at = "created_at";
    public static final String GPS_TRACKING_TIME_updated_at = "updated_at";

    public static final String GPS_TRACKING_TIME[] = {TABLE_NAME_GPS_TRACKING_TIME,GPS_TRACKING_TIME_table_id, GPS_TRACKING_TIME_start_tme, GPS_TRACKING_TIME_end_time,
            GPS_TRACKING_TIME_interval, GPS_TRACKING_TIME_created_at, GPS_TRACKING_TIME_updated_at};


    public static final String CREATE_GPS_TRACKING_TIME_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_GPS_TRACKING_TIME +
            " ( " +
            GPS_TRACKING_TIME_table_id + " INTEGER PRIMARY KEY,   " +
            GPS_TRACKING_TIME_start_tme + "  TIME,   " +
            GPS_TRACKING_TIME_end_time + "  TIME,   " +
            GPS_TRACKING_TIME_interval + "  INTEGER,   " +
            GPS_TRACKING_TIME_created_at + "  DATETIME , " +
            GPS_TRACKING_TIME_updated_at + "  DATETIME );";

    /*..................................INSTRUMENT......................................... */

    /*public static final String TABLE_NAME_INSTRUMENT="instrument";

    public static final String INSTRUMENT_table_id="_id";
    public static final String INSTRUMENT_instrument_type_id="instrument_type_id";
    public static final String INSTRUMENT_instrument_type_name="instrument_type_name";
    public static final String INSTRUMENT__created_at="created_at";
    public static final String INSTRUMENT__updated_at="updated_at";

    public static final String INSTRUMENT []={INSTRUMENT_table_id,INSTRUMENT_instrument_type_id,INSTRUMENT_instrument_type_name,
            INSTRUMENT__created_at,INSTRUMENT__updated_at};


    public static final String CREATE_INSTRUMENT_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME_INSTRUMENT+
            " ( "+
            INSTRUMENT_table_id+" INTEGER PRIMARY KEY,   "+
            INSTRUMENT_instrument_type_id+"  INTEGER,   "+
            INSTRUMENT_instrument_type_name+"  VARCHAR,   "+
            INSTRUMENT__created_at+"  DATETIME , "+
            INSTRUMENT__updated_at+"  DATETIME );";
*/

    /*..................................INSTRUMENT NUMBER......................................... */

    public static final String TABLE_NAME_INSTRUMENT_NUMBER = "instrument_number";

    public static final String INSTRUMENT_NUMBER_table_id = "_id";
    public static final String INSTRUMENT_NUMBER_instrument_number_id = "instrument_number_id";
    public static final String INSTRUMENT_NUMBER_instrument_number_name = "instrument_number_name";
    public static final String INSTRUMENT_NUMBER_memo_number = "memo_number";
    public static final String INSTRUMENT_NUMBER_memo_value = "memo_value";
    public static final String INSTRUMENT_NUMBER_is_used = "is_used";
    public static final String INSTRUMENT_NUMBER_payment = "payment";
    public static final String INSTRUMENT_NUMBER_payment_id = "payment_id";
    public static final String INSTRUMENT_NUMBER_is_pushed = "is_pushed";
    public static final String INSTRUMENT_NUMBER_created_at = "created_at";
    public static final String INSTRUMENT_NUMBER_updated_at = "updated_at";

    public static final String INSTRUMENT_NUMBER[] = {TABLE_NAME_INSTRUMENT_NUMBER,INSTRUMENT_NUMBER_table_id, INSTRUMENT_NUMBER_instrument_number_id, INSTRUMENT_NUMBER_instrument_number_name,
            INSTRUMENT_NUMBER_memo_number, INSTRUMENT_NUMBER_memo_value, INSTRUMENT_NUMBER_is_used, INSTRUMENT_NUMBER_payment,
            INSTRUMENT_NUMBER_payment_id, INSTRUMENT_NUMBER_is_pushed, INSTRUMENT_NUMBER_created_at, INSTRUMENT_NUMBER_updated_at};


    public static final String CREATE_INSTRUMENT_NUMBER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_INSTRUMENT_NUMBER +
            " ( " +
            INSTRUMENT_NUMBER_table_id + " INTEGER PRIMARY KEY,   " +
            INSTRUMENT_NUMBER_instrument_number_id + "  INTEGER,   " +
            INSTRUMENT_NUMBER_instrument_number_name + "  VARCHAR,   " +
            INSTRUMENT_NUMBER_memo_number + "  INTEGER,   " +
            INSTRUMENT_NUMBER_memo_value + "  VARCHAR,   " +
            INSTRUMENT_NUMBER_is_used + "  INTEGER,   " +
            INSTRUMENT_NUMBER_payment + "  VARCHAR,   " +
            INSTRUMENT_NUMBER_payment_id + "  INTEGER,   " +
            INSTRUMENT_NUMBER_is_pushed + "  VARCHAR,   " +
            INSTRUMENT_NUMBER_created_at + "  DATETIME , " +
            INSTRUMENT_NUMBER_updated_at + "  DATETIME );";

    /*..................................INSTRUMENT TYPE......................................... */

    public static final String TABLE_NAME_INSTRUMENT_TYPE = "instrument_type";

    public static final String INSTRUMENT_TYPE_table_id = "_id";
    public static final String INSTRUMENT_TYPE_instrument_type_id = "instrument_type_id";
    public static final String INSTRUMENT_TYPE_instrument_type_name = "instrument_type_name";
    public static final String INSTRUMENT_TYPE_created_at = "created_at";
    public static final String INSTRUMENT_TYPE_updated_at = "updated_at";

    public static final String INSTRUMENT_TYPE[] = {TABLE_NAME_INSTRUMENT_TYPE,INSTRUMENT_TYPE_table_id, INSTRUMENT_TYPE_instrument_type_id, INSTRUMENT_TYPE_instrument_type_name,
            INSTRUMENT_TYPE_created_at, INSTRUMENT_TYPE_updated_at};


    public static final String CREATE_INSTRUMENT_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_INSTRUMENT_TYPE +
            " ( " +
            INSTRUMENT_TYPE_table_id + " INTEGER PRIMARY KEY,   " +
            INSTRUMENT_TYPE_instrument_type_id + "  INTEGER,   " +
            INSTRUMENT_TYPE_instrument_type_name + "  VARCHAR,   " +
            INSTRUMENT_TYPE_created_at + "  DATETIME , " +
            INSTRUMENT_TYPE_updated_at + "  DATETIME );";

    /*..................................LOCATION......................................... */

    public static final String TABLE_NAME_LOCATION = "location";

    public static final String LOCATION_table_id = "_id";
    public static final String LOCATION_location_id = "location_id";
    public static final String LOCATION_location_name = "location_name";
    public static final String LOCATION_created_at = "created_at";
    public static final String LOCATION_updated_at = "updated_at";

    public static final String LOCATION[] = {TABLE_NAME_LOCATION,LOCATION_table_id, LOCATION_location_id, LOCATION_location_name,
            LOCATION_created_at, LOCATION_updated_at};


    public static final String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_LOCATION +
            " ( " +
            LOCATION_table_id + " INTEGER PRIMARY KEY,   " +
            LOCATION_location_id + "  INTEGER,   " +
            LOCATION_location_name + "  VARCHAR,   " +
            LOCATION_created_at + "  DATETIME , " +
            LOCATION_updated_at + "  DATETIME );";

    /*..................................MARKETS......................................... */

    public static final String TABLE_NAME_MARKETS = "markets";

    public static final String MARKETS_table_id = "_id";
    public static final String MARKETS_thana_id = "thana_id";
    public static final String MARKETS_location_type_id = "location_type_id";
    public static final String MARKETS_market_id = "market_id";
    public static final String MARKETS_market_name = "market_name";
    public static final String MARKETS_Territory_id = "territory_id";
    public static final String MARKETS_is_active = "is_active";
    public static final String MARKETS_market_code = "market_code";
    public static final String MARKETS_root_id = "route_id";
    public static final String MARKETS_address = "address";
    public static final String MARKETS_is_pushed = "is_pushed";
    public static final String MARKETS_created_at = "created_at";
    public static final String MARKETS_updated_at = "updated_at";

    public static final String MARKETS[] = {TABLE_NAME_MARKETS,MARKETS_table_id,
            MARKETS_market_id,MARKETS_market_name, MARKETS_thana_id, MARKETS_location_type_id,  MARKETS_Territory_id, MARKETS_is_active, MARKETS_market_code, MARKETS_address, MARKETS_root_id, MARKETS_is_pushed,
            MARKETS_created_at, MARKETS_updated_at};


    public static final String CREATE_MARKETS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MARKETS +
            " ( " +
            MARKETS_table_id + " INTEGER PRIMARY KEY,   " +
            MARKETS_thana_id + "  INTEGER,   " +
            MARKETS_location_type_id + "  INTEGER,   " +
            MARKETS_market_id + "  INTEGER,   " +
            MARKETS_market_name + "  VARCHAR,   " +
            MARKETS_is_active + "  INTEGER,   " +
            MARKETS_Territory_id + "  INTEGER,   " +
            MARKETS_root_id + "  VARCHAR,   " +
            MARKETS_market_code + "  VARCHAR,   " +
            MARKETS_address + "  VARCHAR,   " +
            MARKETS_is_pushed + "  INTEGER,   " +
            MARKETS_created_at + "  DATETIME , " +
            MARKETS_updated_at + "  DATETIME );";

    /*..................................MATERIALS......................................... */

    public static final String TABLE_NAME_MATERIALS = "materials";

    public static final String MATERIALS_table_id = "_id";
    public static final String MATERIALS_material_id = "material_id";
    public static final String MATERIALS_material_name = "material_name";
    public static final String MATERIALS_created_at = "created_at";
    public static final String MATERIALS_updated_at = "updated_at";

    public static final String MATERIALS[] = {TABLE_NAME_MATERIALS,MATERIALS_table_id, MATERIALS_material_id, MATERIALS_material_name,
            MATERIALS_created_at, MATERIALS_updated_at};


    public static final String CREATE_MATERIALS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MATERIALS +
            " ( " +
            MATERIALS_table_id + " INTEGER PRIMARY KEY,   " +
            MATERIALS_material_id + "  INTEGER,   " +
            MATERIALS_material_name + "  VARCHAR,   " +
            MATERIALS_created_at + "  DATETIME , " +
            MATERIALS_updated_at + "  DATETIME );";

    /*..................................MEMO DETAILS......................................... */

    public static final String TABLE_NAME_MEMO_DETAILS = "memo_details";

    public static final String MEMO_DETAILS_table_id = "_id";
    public static final String MEMO_DETAILS_memo_number = "memo_number";
    public static final String MEMO_DETAILS_order_number = "order_number";
    public static final String MEMO_DETAILS_current_inventory_id = "current_inventory_id";
    public static final String MEMO_DETAILS_product_id = "product_id";
    public static final String MEMO_DETAILS_product_type = "product_type";
    public static final String MEMO_DETAILS_quantity = "quantity";
    public static final String MEMO_DETAILS_price = "price";
    public static final String MEMO_DETAILS_vat = "vat";
    public static final String MEMO_DETAILS_bonus_id = "bonus_id";
    public static final String MEMO_DETAILS_memo_date = "memo_date";
    public static final String MEMO_DETAILS_is_bonus = "is_bonus";
    public static final String MEMO_DETAILS_discount_amount = "discount_amount";
    public static final String MEMO_DETAILS_discount_type = "discount_type";
    public static final String MEMO_DETAILS_policy_type = "policy_type";
    public static final String MEMO_DETAILS_Unit_id = "measurement_unit_id";
    public static final String MEMO_DETAILS_POLICY_Id = "policy_id";
    public static final String MEMO_DETAILS_PRICE_Id = "price_id";
    public static final String MEMO_DETAILS_COMBINATION_Id = "combination_id";
    public static final String MEMO_DETAILS_selected_set = "selected_set";
    public static final String MEMO_DETAILS_provided_qty = "provided_qty";
    public static final String MEMO_DETAILS_created_at = "created_at";
    public static final String MEMO_DETAILS_updated_at = "updated_at";

    public static final String MEMO_DETAILS[] = {
            TABLE_NAME_MEMO_DETAILS,
            MEMO_DETAILS_table_id,
            MEMO_DETAILS_memo_number,
            MEMO_DETAILS_memo_date,
            MEMO_DETAILS_current_inventory_id,
            MEMO_DETAILS_product_id,
            MEMO_DETAILS_product_type,
            MEMO_DETAILS_quantity,
            MEMO_DETAILS_order_number,
            MEMO_DETAILS_vat,
            MEMO_DETAILS_price,
            MEMO_DETAILS_bonus_id,
            MEMO_DETAILS_is_bonus,
            MEMO_DETAILS_discount_amount,
            MEMO_DETAILS_discount_type,
            MEMO_DETAILS_policy_type,
            MEMO_DETAILS_Unit_id,
            MEMO_DETAILS_POLICY_Id,
            MEMO_DETAILS_PRICE_Id,
            MEMO_DETAILS_COMBINATION_Id,
            MEMO_DETAILS_selected_set,
            MEMO_DETAILS_provided_qty,
            MEMO_DETAILS_created_at,
            MEMO_DETAILS_updated_at
    };

    public static final String CREATE_MEMO_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMO_DETAILS +
            " ( " +
            MEMO_DETAILS_table_id + " INTEGER PRIMARY KEY,   " +
            MEMO_DETAILS_memo_number + "  VARCHAR,   " +
            MEMO_DETAILS_order_number + "  VARCHAR,   " +
            MEMO_DETAILS_current_inventory_id + "  INTEGER,   " +
            MEMO_DETAILS_product_id + "  INTEGER,   " +
            MEMO_DETAILS_product_type + "  INTEGER,   " +
            MEMO_DETAILS_quantity + "  DOUBLE,   " +
            MEMO_DETAILS_vat + "  DOUBLE,   " +
            MEMO_DETAILS_price + "  DOUBLE,   " +
            MEMO_DETAILS_bonus_id + "  INTEGER,   " +
            MEMO_DETAILS_memo_date + "  DATE,   " +
            MEMO_DETAILS_is_bonus + "  INTEGER,   " +
            MEMO_DETAILS_discount_type + "  INTEGER,   " +
            MEMO_DETAILS_discount_amount + "  DOUBLE,   " +
            MEMO_DETAILS_policy_type + "  INTEGER,   " +
            MEMO_DETAILS_Unit_id + "  INTEGER,   " +
            MEMO_DETAILS_POLICY_Id + "  INTEGER,   " +
            MEMO_DETAILS_PRICE_Id + "  INTEGER,   " +
            MEMO_DETAILS_COMBINATION_Id + "  INTEGER,   " +
            MEMO_DETAILS_selected_set + "  INTEGER,   " +
            MEMO_DETAILS_provided_qty + "  DOUBLE,   " +
            MEMO_DETAILS_created_at + "  DATETIME , " +
            MEMO_DETAILS_updated_at + "  DATETIME );";

    public static final String CREATE_TEMP_MEMO_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + "temp_memo_details" +
            " ( " +
            MEMO_DETAILS_table_id + " INTEGER PRIMARY KEY,   " +
            MEMO_DETAILS_memo_number + "  VARCHAR,   " +
            MEMO_DETAILS_order_number + "  VARCHAR,   " +
            MEMO_DETAILS_current_inventory_id + "  INTEGER,   " +
            MEMO_DETAILS_product_id + "  INTEGER,   " +
            MEMO_DETAILS_product_type + "  INTEGER,   " +
            MEMO_DETAILS_quantity + "  DOUBLE,   " +
            MEMO_DETAILS_vat + "  DOUBLE,   " +
            MEMO_DETAILS_price + "  DOUBLE,   " +
            MEMO_DETAILS_bonus_id + "  INTEGER,   " +
            MEMO_DETAILS_memo_date + "  DATE,   " +
            MEMO_DETAILS_is_bonus + "  INTEGER,   " +
            MEMO_DETAILS_discount_type + "  INTEGER,   " +
            MEMO_DETAILS_discount_amount + "  DOUBLE,   " +
            MEMO_DETAILS_policy_type + "  INTEGER,   " +
            MEMO_DETAILS_Unit_id + "  INTEGER,   " +
            MEMO_DETAILS_POLICY_Id + "  INTEGER,   " +
            MEMO_DETAILS_PRICE_Id + "  INTEGER,   " +
            MEMO_DETAILS_COMBINATION_Id + "  INTEGER,   " +
            MEMO_DETAILS_selected_set + "  INTEGER,   " +
            MEMO_DETAILS_provided_qty + "  DOUBLE,   " +
            MEMO_DETAILS_created_at + "  DATETIME , " +
            MEMO_DETAILS_updated_at + "  DATETIME );";


    /*..................................MEMOS......................................... */

    public static final String TABLE_NAME_MEMOS = "memos";

    public static final String MEMOS_table_id = "_id";
    public static final String MEMOS_memo_number = "memo_number";
    public static final String MEMOS_memo_date = "memo_date";
    public static final String MEMOS_memo_date_time = "memo_date_time";
    public static final String MEMOS_sales_to = "sales_to";
    public static final String MEMOS_outlet_id = "outlet_id";
    public static final String MEMOS_market_id = "market_id";
    public static final String MEMOS_gross_value = "gross_value";
    public static final String MEMOS_ORDER_NUMBER = "order_number";
    public static final String MEMOS_cash_received = "cash_received";
    public static final String MEMOS_credit_amount = "credit_amount";
    public static final String MEMOS_discount_amount = "discount_value";
    public static final String MEMOS_TOTAL_Vat = "total_vat";
    public static final String MEMOS_DISCOUNT_TYPE = "discount_type";
    public static final String MEMOS_discount_Percentage = "discount_percent";
    public static final String MEMOS_is_active = "is_active";
    public static final String MEMOS_sales_person_id = "sales_person_id";
    public static final String MEMOS_latitude = "latitude";
    public static final String MEMOS_longitude = "longitude";
    public static final String MEMOS_from_app = "from_app";
    public static final String MEMOS_for_memo_delete = "for_memo_delete";
    public static final String MEMOS_editable = "editable";
    public static final String MEMOS_is_pushed = "is_pushed";
    public static final String MEMOS_is_distributor = "is_distributor";
    public static final String MEMOS_total_discount = "total_discount";
    public static final String MEMOS_created_at = "created_at";
    public static final String MEMOS_updated_at = "updated_at";

    public static final String MEMOS[] = {
            TABLE_NAME_MEMOS,MEMOS_table_id,
            MEMOS_memo_number,
            MEMOS_for_memo_delete,
            MEMOS_memo_date,
            MEMOS_memo_date_time,
            MEMOS_ORDER_NUMBER,
            MEMOS_sales_to,
            MEMOS_outlet_id,
            MEMOS_market_id,
            MEMOS_gross_value,
            MEMOS_cash_received,
            MEMOS_credit_amount,
            MEMOS_is_active,
            MEMOS_latitude,
            MEMOS_longitude,
            MEMOS_from_app,
            MEMOS_sales_person_id,
            SR_ID,
            MEMOS_TOTAL_Vat,
            MEMOS_DISCOUNT_TYPE,
            MEMOS_discount_amount,
            MEMOS_discount_Percentage,
            MEMOS_editable,
            MEMOS_is_pushed,
            MEMOS_is_distributor,
            MEMOS_total_discount,
            MEMOS_created_at,
            MEMOS_updated_at
    };


    public static final String CREATE_MEMOS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEMOS +
            " ( " +
            MEMOS_table_id + " INTEGER PRIMARY KEY,   " +
            MEMOS_memo_number + "  VARCHAR,   " +
            MEMOS_ORDER_NUMBER + "  VARCHAR,   " +
            MEMOS_memo_date + "  DATE,   " +
            MEMOS_memo_date_time + "  DATETIME,   " +
            MEMOS_sales_person_id + "  INTEGER,   " +
            MEMOS_sales_to + "  INTEGER,   " +
            MEMOS_outlet_id + "  INTRGER,   " +
            MEMOS_market_id + "  INTEGER,   " +
            MEMOS_DISCOUNT_TYPE + "  INTEGER,   " +
            MEMOS_gross_value + "  DOUBLE,   " +
            MEMOS_TOTAL_Vat + "  DOUBLE,   " +
            MEMOS_cash_received + "  DOUBLE,   " +
            MEMOS_discount_Percentage + "  DOUBLE,   " +
            MEMOS_discount_amount + "  DOUBLE,   " +
            MEMOS_credit_amount + "  DOUBLE,   " +
            MEMOS_is_active + "  INTEGER,   " +
            MEMOS_latitude + "  DOUBLE,   " +
            MEMOS_longitude + "  DOUBLE,   " +
            MEMOS_from_app + "  INTEGER,   " +
            MEMOS_for_memo_delete + "  INTEGER,   " +
            MEMOS_editable + "  INTEGER,   " +
            MEMOS_is_pushed + "  VARCHAR,   " +
            MEMOS_is_distributor + "  INTEGER,   " +
            MEMOS_total_discount + "  DOUBLE,   " +
            MEMOS_created_at + "  DATETIME , " +
            MEMOS_updated_at + "  DATETIME );";


    public static final String CREATE_TEMP_MEMOS_TABLE = "CREATE TABLE IF NOT EXISTS " + "temp_memos " +
            " ( " +
            MEMOS_table_id + " INTEGER PRIMARY KEY,   " +
            MEMOS_memo_number + "  VARCHAR,   " +
            MEMOS_ORDER_NUMBER + "  VARCHAR,   " +
            MEMOS_memo_date + "  DATE,   " +
            MEMOS_memo_date_time + "  DATETIME,   " +
            MEMOS_sales_person_id + "  INTEGER,   " +
            MEMOS_sales_to + "  INTEGER,   " +
            MEMOS_outlet_id + "  INTRGER,   " +
            MEMOS_market_id + "  INTEGER,   " +
            MEMOS_DISCOUNT_TYPE + "  INTEGER,   " +
            MEMOS_gross_value + "  DOUBLE,   " +
            MEMOS_TOTAL_Vat + "  DOUBLE,   " +
            MEMOS_cash_received + "  DOUBLE,   " +
            MEMOS_discount_Percentage + "  DOUBLE,   " +
            MEMOS_discount_amount + "  DOUBLE,   " +
            MEMOS_credit_amount + "  DOUBLE,   " +
            MEMOS_is_active + "  INTEGER,   " +
            MEMOS_latitude + "  DOUBLE,   " +
            MEMOS_longitude + "  DOUBLE,   " +
            MEMOS_from_app + "  INTEGER,   " +
            MEMOS_for_memo_delete + "  INTEGER,   " +
            MEMOS_editable + "  INTEGER,   " +
            MEMOS_is_pushed + "  VARCHAR,   " +
            MEMOS_is_distributor + "  INTEGER,   " +
            MEMOS_total_discount + "  DOUBLE,   " +
            MEMOS_created_at + "  DATETIME , " +
            MEMOS_updated_at + "  DATETIME );";



    /*..................................order DETAILS......................................... */

    public static final String TABLE_NAME_ORDER_DETAILS = "order_details";

    public static final String ORDER_DETAILS_table_id = "_id";
    public static final String ORDER_DETAILS_order_number = "order_number";
    public static final String ORDER_DETAILS_current_inventory_id = "current_inventory_id";
    public static final String ORDER_DETAILS_product_id = "product_id";
    public static final String ORDER_DETAILS_product_type = "product_type";
    public static final String ORDER_DETAILS_quantity = "quantity";
    public static final String ORDER_DETAILS_price = "price";
    public static final String ORDER_DETAILS_vat = "vat";
    public static final String ORDER_DETAILS_bonus_id = "bonus_id";
    public static final String ORDER_DETAILS_order_date = "order_date";
    public static final String ORDER_DETAILS_is_bonus = "is_bonus";
    public static final String ORDER_DETAILS_discount_amount = "discount_amount";
    public static final String ORDER_DETAILS_discount_type = "discount_type";
    public static final String ORDER_DETAILS_policy_type = "policy_type";
    public static final String ORDER_DETAILS_selected_set = "selected_set";
    public static final String ORDER_DETAILS_provided_qty = "provided_qty";
    public static final String ORDER_DETAILS_PRICE_Id = "price_id";
    public static final String ORDER_DETAILS_COMBINATION_Id = "combination_id";
    public static final String ORDER_DETAILS_created_at = "created_at";
    public static final String ORDER_DETAILS_updated_at = "updated_at";

    public static final String ORDER_DETAILS[] = {
            TABLE_NAME_ORDER_DETAILS,
            ORDER_DETAILS_table_id,
            ORDER_DETAILS_order_number,
            ORDER_DETAILS_current_inventory_id,
            ORDER_DETAILS_product_id,
            ORDER_DETAILS_product_type,
            ORDER_DETAILS_quantity,
            ORDER_DETAILS_vat,
            ORDER_DETAILS_price,
            MEMO_DETAILS_Unit_id,
            ORDER_DETAILS_bonus_id,
            ORDER_DETAILS_order_date,
            ORDER_DETAILS_is_bonus,
            ORDER_DETAILS_discount_amount,
            ORDER_DETAILS_discount_type,
            ORDER_DETAILS_policy_ID,
            ORDER_DETAILS_PRICE_Id,
            ORDER_DETAILS_COMBINATION_Id,
            ORDER_DETAILS_policy_type,
            ORDER_DETAILS_selected_set,
            ORDER_DETAILS_provided_qty,
            ORDER_DETAILS_created_at,
            ORDER_DETAILS_updated_at};


    public static final String CREATE_ORDER_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ORDER_DETAILS +
            " ( " +
            ORDER_DETAILS_table_id + " INTEGER PRIMARY KEY,   " +
            ORDER_DETAILS_order_number + "  INTEGER,   " +
            ORDER_DETAILS_current_inventory_id + "  INTEGER,   " +
            ORDER_DETAILS_product_id + "  INTEGER,   " +
            ORDER_DETAILS_product_type + "  INTEGER,   " +
            ORDER_DETAILS_quantity + "  DOUBLE,   " +
            ORDER_DETAILS_price + "  DOUBLE,   " +
            ORDER_DETAILS_vat + "  DOUBLE,   " +
            ORDER_DETAILS_bonus_id + "  INTEGER,   " +
            ORDER_DETAILS_order_date + "  DATE,   " +
            ORDER_DETAILS_is_bonus + "  INTEGER,   " +
            MEMO_DETAILS_discount_type + "  INTEGER,   " +
            MEMO_DETAILS_discount_amount + "  DOUBLE,   " +
            MEMO_DETAILS_policy_type + "  INTEGER,   " +
            MEMO_DETAILS_Unit_id + "  INTEGER,   " +
            ORDER_DETAILS_policy_ID + "  INTEGER,   " +
            MEMO_DETAILS_PRICE_Id + "  INTEGER,   " +
            MEMO_DETAILS_COMBINATION_Id + "  INTEGER,   " +
            ORDER_DETAILS_selected_set + "  INTEGER,   " +
            ORDER_DETAILS_provided_qty + "  DOUBLE,   " +
            ORDER_DETAILS_created_at + "  DATETIME , " +
            ORDER_DETAILS_updated_at + "  DATETIME );";


    /*..................................ORDER......................................... */

    public static final String TABLE_NAME_ORDER = "order_table";

    public static final String ORDER_table_id = "_id";
    public static final String ORDER_order_number = "order_number";
    public static final String ORDER_order_date = "order_date";
    public static final String ORDER_order_date_time = "order_date_time";
    public static final String ORDER_sales_to = "sales_to";
    public static final String ORDER_outlet_id = "outlet_id";
    public static final String ORDER_market_id = "market_id";
    public static final String ORDER_discount_amount = "discount_value";
    public static final String ORDER_discount_Percentage = "discount_percent";
    public static final String ORDER_discount_Type = "discount_type";
    public static final String ORDER_gross_value = "gross_value";
    public static final String ORDER_TOTAL_VAT = "total_vat";
    public static final String ORDER_cash_received = "cash_received";
    public static final String ORDER_credit_amount = "credit_amount";
    public static final String ORDER_is_active = "is_active";
    public static final String ORDER_sales_person_id = "sales_person_id";
    public static final String ORDER_latitude = "latitude";
    public static final String ORDER_longitude = "longitude";
    public static final String ORDER_from_app = "from_app";
    public static final String ORDER_IS_processed = "is_processed";
    public static final String ORDER_for_order_delete = "for_order_delete";
    public static final String ORDER_editable = "editable";
    public static final String ORDER_is_out_of_plan = "is_out_of_plan";
    public static final String ORDER_plan_id = "plan_id";
    public static final String ORDER_STATUS = "status";
    public static final String ORDER_is_pushed = "is_pushed";
    public static final String ORDER_is_Complete = "is_complete";
    public static final String ORDER_total_discount = "total_discount";
    public static final String ORDER_is_distributor = "is_distributor";

    public static final String ORDER_created_at = "created_at";
    public static final String ORDER_updated_at = "updated_at";

    public static final String ORDER[] = {TABLE_NAME_ORDER,ORDER_table_id, ORDER_order_number, ORDER_order_date, ORDER_order_date_time, ORDER_sales_to, ORDER_STATUS,ORDER_outlet_id, ORDER_market_id,  ORDER_gross_value, ORDER_cash_received, ORDER_credit_amount, ORDER_is_active, ORDER_latitude,
            ORDER_longitude, ORDER_from_app,ORDER_sales_person_id,SR_ID,ORDER_discount_Type,ORDER_is_out_of_plan,ORDER_plan_id, ORDER_TOTAL_VAT,ORDER_discount_amount, ORDER_discount_Percentage,ORDER_for_order_delete, ORDER_editable, ORDER_is_pushed, ORDER_is_distributor,ORDER_total_discount,
            ORDER_created_at, ORDER_updated_at
    };


    public static final String CREATE_ORDER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ORDER +
            " ( " +
            ORDER_table_id + " INTEGER PRIMARY KEY,   " +
            ORDER_order_number + "  VARCHAR,   " +
            ORDER_order_date + "  DATE,   " +
            ORDER_order_date_time + "  DATETIME,   " +
            ORDER_sales_person_id + "  INTEGER,   " +
            ORDER_sales_to + "  INTEGER,   " +
            ORDER_outlet_id + "  INTRGER,   " +
            ORDER_market_id + "  INTEGER,   " +
            ORDER_gross_value + "  DOUBLE,   " +
            ORDER_cash_received + "  DOUBLE,   " +
            ORDER_TOTAL_VAT + "  DOUBLE,   " +
            ORDER_discount_Percentage + "  DOUBLE,   " +
            ORDER_discount_amount + "  DOUBLE,   " +
            ORDER_credit_amount + "  DOUBLE,   " +
            ORDER_is_active + "  INTEGER,   " +
            ORDER_is_out_of_plan + "  INTEGER,   " +
            ORDER_plan_id + "  INTEGER,   " +
            ORDER_is_Complete + "  INTEGER,   " +
            ORDER_STATUS + "  INTEGER,   " +
            ORDER_discount_Type + "  INTEGER,   " +
            ORDER_IS_processed + "  INTEGER,   " +
            ORDER_latitude + "  DOUBLE,   " +
            ORDER_longitude + "  DOUBLE,   " +
            ORDER_from_app + "  INTEGER,   " +
            ORDER_for_order_delete + "  INTEGER,   " +
            ORDER_editable + "  INTEGER,   " +
            ORDER_is_pushed + "  VARCHAR,   " +
            ORDER_is_distributor + "  INTEGER,   " +
            ORDER_total_discount + "  DOUBLE,   " +
            ORDER_created_at + "  DATETIME , " +
            ORDER_updated_at + "  DATETIME );";

    /*..................................Monthly Effective Call......................................... */

    public static final String TABLE_NAME_MONTHLY_EFFECTIVE_CALL = "monthly_effective_call";

    public static final String MONTHLY_EFFECTIVE_CALL_table_id = "_id";

    public static final String MONTHLY_EFFECTIVE_CALL_pharma = "pharma";
    public static final String MONTHLY_EFFECTIVE_CALL_non_pharma = "non_pharma";
    public static final String MONTHLY_EFFECTIVE_CALL_total = "total";
    public static final String MONTHLY_EFFECTIVE_created_at = "created_at";
    public static final String MONTHLY_EFFECTIVE_updated_at = "updated_at";

    public static final String MONTHLY_EFFECTIVE_CALL[] = {TABLE_NAME_MONTHLY_EFFECTIVE_CALL,MONTHLY_EFFECTIVE_CALL_table_id,
             MONTHLY_EFFECTIVE_CALL_pharma, MONTHLY_EFFECTIVE_CALL_non_pharma,
            MONTHLY_EFFECTIVE_CALL_total, MONTHLY_EFFECTIVE_created_at, MONTHLY_EFFECTIVE_updated_at

    };


    public static final String CREATE_MONTHLY_EFFECTIVE_CALL_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MONTHLY_EFFECTIVE_CALL +
            " ( " +
            MONTHLY_EFFECTIVE_CALL_table_id + " INTEGER PRIMARY KEY,   " +
            MONTHLY_EFFECTIVE_CALL_pharma + "  DOUBLE,   " +
            MONTHLY_EFFECTIVE_CALL_non_pharma + "  DOUBLE,   " +
            MONTHLY_EFFECTIVE_CALL_total + "  DOUBLE,   " +
            MONTHLY_EFFECTIVE_created_at + "  DATETIME , " +
            MONTHLY_EFFECTIVE_updated_at + "  DATETIME );";


    /*..................................NCP_CHALLAN......................................... */

    public static final String TABLE_NAME_NCP_CHALLAN = "ncp_challan";

    public static final String NCP_CHALLAN_table_id = "_id";
    public static final String NCP_CHALLAN_challan_id = "challan_id";
    public static final String NCP_CHALLAN_challan_number = "challan_number";
    public static final String NCP_CHALLAN_challan_date = "challan_date";
    public static final String NCP_CHALLAN_received_date = "received_date";
    public static final String NCP_CHALLAN_status = "status";
    public static final String NCP_CHALLAN_is_processed = "is_processed";
    public static final String NCP_CHALLAN_is_acknowledged = "is_acknowledged";
    public static final String NCP_CHALLAN__created_at = "created_at";
    public static final String NCP_CHALLAN__updated_at = "updated_at";

    public static final String NCP_CHALLAN[] = {TABLE_NAME_NCP_CHALLAN,NCP_CHALLAN_table_id,
            NCP_CHALLAN_challan_id, NCP_CHALLAN_challan_number, NCP_CHALLAN_challan_date, NCP_CHALLAN_received_date, NCP_CHALLAN_status,
            NCP_CHALLAN_is_processed, NCP_CHALLAN_is_acknowledged, NCP_CHALLAN__created_at, NCP_CHALLAN__updated_at
    };


    public static final String CREATE_NCP_CHALLAN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NCP_CHALLAN +
            " ( " +
            NCP_CHALLAN_table_id + " INTEGER PRIMARY KEY,   " +
            NCP_CHALLAN_challan_id + "  INTEGER,   " +
            NCP_CHALLAN_challan_number + "  VARCHAR,   " +
            NCP_CHALLAN_challan_date + "  DATE,   " +
            NCP_CHALLAN_received_date + "  DATE,   " +
            NCP_CHALLAN_status + "  INTEGER,   " +
            NCP_CHALLAN_is_processed + "  INTEGER,   " +
            NCP_CHALLAN_is_acknowledged + "  INTEGER,   " +
            NCP_CHALLAN__created_at + "  DATETIME , " +
            NCP_CHALLAN__updated_at + "  DATETIME );";

    /*..................................NCP_CHALLAN_DETAILS......................................... */

    public static final String TABLE_NAME_NCP_CHALLAN_DETAILS = "ncp_challan_details";

    public static final String NCP_CHALLAN_DETAILS_table_id = "_id";
    public static final String NCP_CHALLAN_DETAILS_challan_id = "challan_id";
    public static final String NCP_CHALLAN_DETAILS_challan_number = "challan_number";
    public static final String NCP_CHALLAN_DETAILS_product_id = "product_id";
    public static final String NCP_CHALLAN_DETAILS_quantity = "quantity";
    public static final String NCP_CHALLAN_DETAILS_unit = "unit";
    public static final String NCP_CHALLAN_DETAILS_batch_no = "batch_no";
    public static final String NCP_CHALLAN_DETAILS_expire_date = "expire_date";
    public static final String NCP_CHALLAN_DETAILS_current_inventory_id = "current_inventory_id";
    public static final String NCP_CHALLAN_DETAILS_created_at = "created_at";
    public static final String NCP_CHALLAN_DETAILS_updated_at = "updated_at";

    public static final String NCP_CHALLAN_DETAILS[] = {TABLE_NAME_NCP_CHALLAN_DETAILS,NCP_CHALLAN_DETAILS_table_id,
            NCP_CHALLAN_DETAILS_challan_id, NCP_CHALLAN_DETAILS_challan_number, NCP_CHALLAN_DETAILS_product_id, NCP_CHALLAN_DETAILS_quantity,
            NCP_CHALLAN_DETAILS_unit, NCP_CHALLAN_DETAILS_batch_no, NCP_CHALLAN_DETAILS_expire_date, NCP_CHALLAN_DETAILS_current_inventory_id,
            NCP_CHALLAN_DETAILS_created_at, NCP_CHALLAN_DETAILS_updated_at
    };


    public static final String CREATE_NCP_CHALLAN_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NCP_CHALLAN_DETAILS +
            " ( " +
            NCP_CHALLAN_DETAILS_table_id + " INTEGER PRIMARY KEY,   " +
            NCP_CHALLAN_DETAILS_challan_id + "  INTEGER,   " +
            NCP_CHALLAN_DETAILS_challan_number + "  VARCHAR,   " +
            NCP_CHALLAN_DETAILS_product_id + "  INTEGER,   " +
            NCP_CHALLAN_DETAILS_quantity + "  DOUBLE,   " +
            NCP_CHALLAN_DETAILS_unit + "  DOUBLE,   " +
            NCP_CHALLAN_DETAILS_batch_no + "  VARCHAR,   " +
            NCP_CHALLAN_DETAILS_expire_date + "  DATE,   " +
            NCP_CHALLAN_DETAILS_current_inventory_id + "  INTEGER,   " +
            NCP_CHALLAN_DETAILS_created_at + "  DATETIME , " +
            NCP_CHALLAN_DETAILS_updated_at + "  DATETIME );";


    /*..................................NCP Return......................................... TANVIR */


    public static final String TABLE_NAME_NCP_RETURN = "ncp_returns";

    public static final String NCP_RETURN_table_id = "_id";
    public static final String NCP_RETURN_MARKET_ID = "market_id";
    public static final String NCP_RETURN_OUTLET_ID = "outlet_id";
    public static final String NCP_RETURN_CHALLAN_NO = "challan_no";
    public static final String NCP_RETURN_CHALLAN_DATES = "challan_date";
    public static final String NCP_RETURN_PRODUCT_ID = "product_id";
    public static final String NCP_RETURN_TEMP_ID = "temp_id";
    public static final String NCP_RETURN_QUANTITY = "quantity";
    public static final String NCP_RETURN_PROVIDED_QUANTITY = "provide_quantity";
    public static final String NCP_RETURN_UNIT = "unit";
    public static final String NCP_RETURN_BATCH_NO = "batch_no";
    public static final String NCP_RETURN_EXPIRE_DATE = "expire_date";
    public static final String NCP_RETURN_PRODUCT_CATAGORY_ID = "product_category_id";
    public static final String NCP_RETURN_REMARKS = "remarks";
    public static final String NCP_RETURN_ISPUSHED = "isPushed";
    public static final String NCP_RETURN_ISSTORED = "isStored";
    public static final String NCP_RETURN_UPDATED_AT = "updated_at";
    public static final String NCP_RETURN_CREATE_AT = "created_at";

    public static final String NCP_RETURNS[] = {TABLE_NAME_NCP_RETURN,NCP_RETURN_table_id,NCP_RETURN_CHALLAN_NO,
            NCP_RETURN_MARKET_ID, NCP_RETURN_OUTLET_ID,  NCP_RETURN_CHALLAN_DATES,
            NCP_RETURN_PRODUCT_ID, NCP_RETURN_TEMP_ID, NCP_RETURN_QUANTITY, NCP_RETURN_PROVIDED_QUANTITY,
            NCP_RETURN_UNIT, NCP_RETURN_BATCH_NO, NCP_RETURN_EXPIRE_DATE, NCP_RETURN_PRODUCT_CATAGORY_ID,
            NCP_RETURN_REMARKS, NCP_RETURN_ISPUSHED, NCP_RETURN_ISSTORED, NCP_RETURN_UPDATED_AT, NCP_RETURN_CREATE_AT};


    public static final String CREATE_NCP_RETURNS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NCP_RETURN + "("
            + NCP_RETURN_table_id + " INTEGER PRIMARY KEY," +
            NCP_RETURN_MARKET_ID + " VARCHAR," +
            NCP_RETURN_OUTLET_ID + " VARCHAR," +
            NCP_RETURN_CHALLAN_NO + " VARCHAR," +
            NCP_RETURN_CHALLAN_DATES + " DATE," +
            NCP_RETURN_PRODUCT_ID + " INTEGER," +
            NCP_RETURN_TEMP_ID + " VARCHAR," +
            NCP_RETURN_QUANTITY + " DOUBLE," +
            NCP_RETURN_PROVIDED_QUANTITY + " DOUBLE," +
            NCP_RETURN_UNIT + " INTEGER," +
            NCP_RETURN_BATCH_NO + " VARCHAR," +
            NCP_RETURN_EXPIRE_DATE + " DATE," +
            NCP_RETURN_PRODUCT_CATAGORY_ID + " INTEGER," +
            NCP_RETURN_REMARKS + " VARCHAR," +
            NCP_RETURN_ISPUSHED + " INTEGER," +
            NCP_RETURN_ISSTORED + " INTEGER," +
            NCP_RETURN_CREATE_AT + " DATETIME," +
            NCP_RETURN_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" + ");";


    //.......................................... NCP STOCK TABLE.....................................


    public static final String TABLE_NAME_STOCKS = "stock_info";

    public static final String STOCKS_id = "_id";
    public static final String STOCKS_PRODUCT_ID = "product_id";
    public static final String STOCKS_QUANTITY = "quantity";
    public static final String STOCKS_UNIT = "unit";
    public static final String STOCKS_BOOKING_QTY = "booking_quantity";
    public static final String STOCKS_INVOICE_QTY = "invoice_qty";
    public static final String STOCKS_BONUS_QTY = "bonus_quantity";
    public static final String STOCKS_BONUS_QTY_BOOKING = "bonus_booking_quantity";
    public static final String STOCKS_CURRENT_UPDATED_AT = "updated_at";
    public static final String STOCKS_CURRENT_CREATED_AT = "created_at";

    public static final String STOCKS[] = {TABLE_NAME_STOCKS,STOCKS_id,
            STOCKS_PRODUCT_ID, STOCKS_QUANTITY, STOCKS_UNIT, STOCKS_BOOKING_QTY,
            STOCKS_INVOICE_QTY, STOCKS_BONUS_QTY, STOCKS_BONUS_QTY_BOOKING,
             STOCKS_CURRENT_UPDATED_AT, STOCKS_CURRENT_CREATED_AT};


    public static final String CREATE_STOCKS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_STOCKS + "("
            + STOCKS_id + " INTEGER PRIMARY KEY," +
            STOCKS_PRODUCT_ID + " INTEGER," +
            STOCKS_QUANTITY + " DOUBLE," +
            STOCKS_UNIT + " INTEGER," +
            STOCKS_BOOKING_QTY + " DOUBLE," +
            STOCKS_INVOICE_QTY + " DOUBLE," +
            STOCKS_BONUS_QTY + " DOUBLE," +
            STOCKS_BONUS_QTY_BOOKING + " DOUBLE," +
            STOCKS_CURRENT_CREATED_AT + " DATETIME," +
            STOCKS_CURRENT_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" + ");";


    //...................................NGO.........................................

    public static final String TABLE_NAME_NGO = "ngo";

    public static final String NGO_id = "_id";
    public static final String NGO_ngo_ID = "ngo_id";
    public static final String NGO_NAME = "ngo_name";
    public static final String NGO__UPDATED_AT = "updated_at";
    public static final String NGO__CREATED_AT = "created_at";

    public static final String NGO[] = {TABLE_NAME_NGO,NGO_id, NGO_ngo_ID,
            NGO_NAME, NGO__UPDATED_AT, NGO__CREATED_AT};


    public static final String CREATE_NGO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NGO + "("
            + NGO_id + " INTEGER PRIMARY KEY," +
            NGO_ngo_ID + " INTEGER," +
            NGO_NAME + " VARCHAR," +
            NGO__CREATED_AT + " DATETIME," +
            NGO__UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";
    //...................................Message.........................................

    public static final String TABLE_NAME_MESSAGE = "message";

    public static final String MESSAGE_id = "_id";
    public static final String MESSAGE_TEXT = "message";
    public static final String MESSAGE_PROMOTION_ID = "current_promotion_id";
    public static final String MESSAGE_SEND_TO = "send_to";
    public static final String MESSAGE_DATE = "message_date";
    public static final String MESSAGE_ISPUSHED = "is_pushed";
    public static final String MESSAGE_TYPE = "message_type";
    public static final String MESSAGE_UPDATED_AT = "updated_at";
    public static final String MESSAGE_CREATED_AT = "created_at";

    public static final String MESSAGES[] = {TABLE_NAME_MESSAGE,MESSAGE_id, MESSAGE_TEXT,
            MESSAGE_SEND_TO, MESSAGE_DATE,MESSAGE_PROMOTION_ID, MESSAGE_ISPUSHED, MESSAGE_UPDATED_AT, MESSAGE_CREATED_AT};


    public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MESSAGE + "("
            + MESSAGE_id + " INTEGER PRIMARY KEY," +
            MESSAGE_TEXT + " TEXT," +
            MESSAGE_SEND_TO + " INTEGER," +
            MESSAGE_PROMOTION_ID + " INTEGER," +
            MESSAGE_DATE + " DATE," +
            MESSAGE_ISPUSHED + " INTEGER," +
            MESSAGE_TYPE + " INTEGER," +
            MESSAGE_CREATED_AT + " DATE," +
            MESSAGE_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')));";


    //...............................OUTLET CATEGORY......................................................


    public static final String TABLE_NAME_OUTLET_CATEGORY = "outlet_categories";

    public static final String OUTLET_CATEGORY_id = "_id";
    public static final String OUTLET_CATEGORY_CATEGORY_ID = "outlet_category_id";
    public static final String OUTLET_CATEGORY_NAME = "outlet_category_name";
    public static final String OUTLET_CATEGORY_IS_ACTIVE = "is_active";
    public static final String OUTLET_CATEGORY_UPDATED_AT = "updated_at";
    public static final String OUTLET_CATEGORY_CREATED_AT = "created_at";

    public static final String OUTLET_CATAGORY[] = {TABLE_NAME_OUTLET_CATEGORY,OUTLET_CATEGORY_id, OUTLET_CATEGORY_CATEGORY_ID,
            OUTLET_CATEGORY_NAME, OUTLET_CATEGORY_IS_ACTIVE, OUTLET_CATEGORY_UPDATED_AT, OUTLET_CATEGORY_CREATED_AT};


    public static final String CREATE_OUTLET_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_OUTLET_CATEGORY + "("
            + OUTLET_CATEGORY_id + " INTEGER PRIMARY KEY," +
            OUTLET_CATEGORY_CATEGORY_ID + " INTEGER," +
            OUTLET_CATEGORY_NAME + " TEXT," +
            OUTLET_CATEGORY_IS_ACTIVE + " INTEGER," +
            OUTLET_CATEGORY_CREATED_AT + " DATETIME," +
            OUTLET_CATEGORY_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


    //.................................outlet VISIT...........................................
    public static final String TABLE_NAME_OUTLET_VISIT = "outlet_visit";

    public static final String OUTLET_VISIT_id = "_id";
    public static final String OUTLET_VISIT_OUTLET_ID = "outlet_id";
    public static final String OUTLET_VISIT_LATITUTEDE = "latitude";
    public static final String OUTLET_VISIT_LONGITUD = "longitude";
    public static final String OUTLET_VISIT_DATE = "visit_date";
    public static final String OUTLET_VISIT_ISPUSHED = "isPushed";
    public static final String OUTLET_VISIT_UPDATED_AT = "updated_at";
    public static final String OUTLET_VISIT_CREATED_AT = "created_at";

    public static final String OUTLET_VISIT[] = {TABLE_NAME_OUTLET_VISIT,OUTLET_VISIT_id, OUTLET_VISIT_OUTLET_ID,
            SR_ID, OUTLET_VISIT_LATITUTEDE, OUTLET_VISIT_LONGITUD,OUTLET_VISIT_DATE,OUTLET_VISIT_ISPUSHED,OUTLET_VISIT_UPDATED_AT,OUTLET_VISIT_CREATED_AT};

    public static final String CREATE_OUTLET_VISIT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_OUTLET_VISIT + "("
            + OUTLET_VISIT_id + " INTEGER PRIMARY KEY," +
            OUTLET_VISIT_OUTLET_ID + " VARCHAR," +
            SR_ID + " INTEGER," +
            OUTLET_VISIT_LATITUTEDE + " DOUBLE," +
            OUTLET_VISIT_DATE + " DATETIME," +
            OUTLET_VISIT_LONGITUD + " DOUBLE," +
            OUTLET_VISIT_ISPUSHED + " INTEGER," +
            OUTLET_VISIT_CREATED_AT + " DATETIME," +
            OUTLET_VISIT_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


    //.................................outlet delete...........................................


    public static final String TABLE_NAME_OUTLET_DELETE = "outlet_delete";

    public static final String OUTLET_DELETE_id = "_id";
    public static final String OUTLET_DELETE_OUTLET_ID = "outlet_id";
    public static final String OUTLET_DELETE_ISPUSHED = "isPushed";
    public static final String OUTLET_DELETE_UPDATED_AT = "updated_at";
    public static final String OUTLET_DELETE_CREATED_AT = "created_at";

    public static final String OUTLET_DELETE[] = {TABLE_NAME_OUTLET_DELETE,OUTLET_DELETE_id, OUTLET_DELETE_OUTLET_ID,
            OUTLET_DELETE_ISPUSHED, OUTLET_DELETE_UPDATED_AT, OUTLET_DELETE_CREATED_AT};


    public static final String CREATE_OUTLET_DALETE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_OUTLET_DELETE + "("
            + OUTLET_DELETE_id + " INTEGER PRIMARY KEY," +
            OUTLET_DELETE_OUTLET_ID + " VARCHAR," +
            OUTLET_DELETE_ISPUSHED + " INTEGER," +
            OUTLET_DELETE_CREATED_AT + " DATETIME," +
            OUTLET_DELETE_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";

    //...............................OUTLET TABLE......................................................

    public static final String TABLE_NAME_OUTLETS = "outlets";

    public static final String OUTLETS_P_id = "_id";
    public static final String OUTLETS_ID = "outlet_id";
    public static final String OUTLETS_THANA_ID = "thana_id";
    public static final String OUTLETS_MARKET_ID = "market_id";
    public static final String OUTLETS_CATAGORY_ID = "outlet_category_id";
    public static final String OUTLETS_NGO_INST_ID = "ngo_institute_id";
    public static final String OUTLETS_PROJECT_ID = "project_id";
    public static final String OUTLETS_OUTLET_NAME = "outlet_name";
    public static final String OUTLETS_INCHARGE_NAME = "incharge_name";
    public static final String OUTLETS_OWNER_NAME = "owner_name";
    public static final String OUTLETS_MOBILE = "mobile";
    public static final String OUTLETS_ADDRESS = "address";
    public static final String OUTLETS_OUTLE_CODE = "outlet_code";
    public static final String OUTLETS_PHARMA_TYPE = "pharma_type";
    public static final String OUTLETS_LATITUDE = "latitude";
    public static final String OUTLETS_LONGITUTE = "longitude";
    public static final String OUTLETS_ISNGO = "isNgo";
    public static final String OUTLETS_ISPUSHED = "isPushed";
    public static final String OUTLETS_ISACTIVATED = "isActivated";
    public static final String OUTLETS_BONUS_PARTY = "bonus_party_type";
    public static final String OUTLETS_IS_UPDATED = "isUpdated";
    public static final String OUTLETS_IS_WITHIN_GROUP = "is_within_group";
    public static final String OUTLETS_Image = "images";
    public static final String OUTLETS_UPDATED_AT = "updated_at";
    public static final String OUTLETS_CREATED_AT = "created_at";
    public static final String OUTLETS_ROOT_ID = "route_id";

    public static final String OUTLETS[] = {TABLE_NAME_OUTLETS,OUTLETS_P_id, OUTLETS_ID,OUTLETS_OUTLET_NAME,
             OUTLETS_THANA_ID,
            OUTLETS_MARKET_ID, OUTLETS_CATAGORY_ID, OUTLETS_NGO_INST_ID,
            OUTLETS_PROJECT_ID,  OUTLETS_INCHARGE_NAME,
            OUTLETS_OWNER_NAME, OUTLETS_MOBILE,OUTLETS_Image, OUTLETS_ADDRESS,
            OUTLETS_OUTLE_CODE, OUTLETS_PHARMA_TYPE, OUTLETS_LATITUDE,
            OUTLETS_LONGITUTE, OUTLETS_ISNGO, OUTLETS_ISPUSHED,
            OUTLETS_ISACTIVATED, OUTLETS_BONUS_PARTY, OUTLETS_IS_UPDATED,
            OUTLETS_IS_WITHIN_GROUP, OUTLETS_ROOT_ID, OUTLETS_UPDATED_AT, OUTLETS_CREATED_AT};

    public static final String CREATE_OUTLETS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_OUTLETS + "("
            + OUTLETS_P_id + " INTEGER PRIMARY KEY," +
            OUTLETS_ID + " VARCHAR," +
            OUTLETS_THANA_ID + " INTEGER," +
            OUTLETS_MARKET_ID + " VARCHAR," +
            OUTLETS_CATAGORY_ID + " INTEGER," +
            OUTLETS_NGO_INST_ID + " INTEGER," +
            OUTLETS_PROJECT_ID + " INTEGER," +
            OUTLETS_OUTLET_NAME + " VARCHAR," +
            OUTLETS_INCHARGE_NAME + " VARCHAR," +
            OUTLETS_OWNER_NAME + " VARCHAR," +
            OUTLETS_MOBILE + " VARCHAR," +
            OUTLETS_ADDRESS + " VARCHAR," +
            OUTLETS_OUTLE_CODE + " VARCHAR," +
            OUTLETS_PHARMA_TYPE + " INTEGER," +
            OUTLETS_LATITUDE + " DOUBLE," +
            OUTLETS_LONGITUTE + " DOUBLE," +
            OUTLETS_ISNGO + " INTEGER," +
            OUTLETS_ISPUSHED + " INTEGER," +
            OUTLETS_ISACTIVATED + " INTEGER," +
            OUTLETS_BONUS_PARTY + " INTEGER," +
            OUTLETS_Image + " Text," +
            OUTLETS_IS_UPDATED + " INTEGER," +
            OUTLETS_IS_WITHIN_GROUP + " INTEGER," +
            OUTLETS_ROOT_ID + " INTEGER," +
            OUTLETS_CREATED_AT + " DATETIME," +
            OUTLETS_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


//...............................OUTLET TABLE......................................................

    public static final String TABLE_NAME_PAYMENTS = "payments";

    public static final String PAYMENTS_P_id = "_id";
    public static final String PAYMENTS_OUTLET_ID = "outlet_id";
    public static final String PAYMENTS_MEMO_NO = "memo_no";
    public static final String PAYMENTS_MEMO_VALUE = "memo_value";
    public static final String PAYMENTS_DUE_AMOUNT = "due_amount";
    public static final String PAYMENTS_PAID_AMOUNT = "paid_amount";
    public static final String PAYMENTS_INSTRUMENT_TYPE = "inst_type";
    public static final String PAYMENTS_INSTRUMENT = "inst";
    public static final String PAYMENTS_BANK_BRANCH = "bank_branch_id";
    public static final String PAYMENTS_COLLECT_DATE = "collect_date";
    public static final String PAYMENTS_MEMO_DATE = "memo_date";
    public static final String PAYMENTS_IS_PUSHED = "is_pushed";
    public static final String PAYMENTS_IS_CREDIT_COLLECTION = "is_credit_collection";
    public static final String PAYMENTS_TAX_AMOUNT = "tax_amount";
    public static final String PAYMENTS_TAX_NO = "tax_no";
    public static final String PAYMENTS_INSTRUMENT_NO = "instrument_no";
    public static final String PAYMENTS_PAYMENT_ID = "payment_id";
    public static final String PAYMENTS_UPDATED_AT = "updated_at";
    public static final String PAYMENTS_CREATED_AT = "created_at";
    public static final String PAYMENTS_VAT_AMOUNT = "vat_amount";

    public static final String PAYMENTS[] = {TABLE_NAME_PAYMENTS,PAYMENTS_P_id,PAYMENTS_PAYMENT_ID, PAYMENTS_OUTLET_ID,
            PAYMENTS_MEMO_NO, PAYMENTS_MEMO_VALUE, PAYMENTS_DUE_AMOUNT,

            PAYMENTS_PAID_AMOUNT, PAYMENTS_INSTRUMENT_TYPE, PAYMENTS_INSTRUMENT,
            PAYMENTS_BANK_BRANCH, PAYMENTS_COLLECT_DATE, PAYMENTS_MEMO_DATE,
            PAYMENTS_IS_PUSHED, PAYMENTS_IS_CREDIT_COLLECTION, PAYMENTS_TAX_AMOUNT,
            PAYMENTS_TAX_NO, PAYMENTS_INSTRUMENT_NO, PAYMENTS_VAT_AMOUNT,
            PAYMENTS_UPDATED_AT, PAYMENTS_CREATED_AT};

    public static final String CREATE_PAYMENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PAYMENTS + "("
            + PAYMENTS_P_id + " INTEGER PRIMARY KEY," +
            PAYMENTS_OUTLET_ID + " INTEGER," +
            PAYMENTS_MEMO_NO + " VARCHAR," +
            PAYMENTS_MEMO_VALUE + " DOUBLE," +
            PAYMENTS_DUE_AMOUNT + " DOUBLE," +
            PAYMENTS_PAID_AMOUNT + " DOUBLE," +
            PAYMENTS_VAT_AMOUNT + " DOUBLE," +
            PAYMENTS_INSTRUMENT_TYPE + " INTEGER," +
            PAYMENTS_INSTRUMENT + " INTEGER," +
            PAYMENTS_BANK_BRANCH + " INTEGER," +
            PAYMENTS_COLLECT_DATE + " DATE," +
            PAYMENTS_MEMO_DATE + " DATE," +
            PAYMENTS_IS_PUSHED + " INTEGER," +
            PAYMENTS_IS_CREDIT_COLLECTION + " DOUBLE," +
            PAYMENTS_TAX_AMOUNT + " DOUBLE," +
            PAYMENTS_TAX_NO + " VARCHAR," +
            PAYMENTS_INSTRUMENT_NO + " INTEGER," +
            PAYMENTS_PAYMENT_ID + " VARCHAR," +
            PAYMENTS_CREATED_AT + " DATETIME," +
            PAYMENTS_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" + ")";


    //...............................Product_boolean......................................................

    public static final String TABLE_NAME_PRODUCT_BOOLEAN = "product_boolean";

    public static final String PRODUCT_BOOLEAN_id = "_id";
    public static final String PRODUCT_BOOLEAN_OUTLET_ID = "outlet_id";
    public static final String PRODUCT_BOOLEAN_PRODUCT_ID = "product_id";
    public static final String PRODUCT_BOOLEAN_QUANTITY = "quantity";
    public static final String PRODUCT_BOOLEAN_BOOLEAN = "boolean";
    public static final String PRODUCT_BOOLEAN_PRODUCT_CATAGORY_ID = "product_category_id";
    public static final String PRODUCT_BOOLEAN_PRODUCT_TYPE_ID = "product_type_id";
    public static final String PRODUCT_BOOLEAN_UPDATED_AT = "updated_at";
    public static final String PRODUCT_BOOLEAN_CREATED_AT = "created_at";

    public static final String PRODUCT_BOOLEANS[] = {TABLE_NAME_PRODUCT_BOOLEAN,PRODUCT_BOOLEAN_id,
            PRODUCT_BOOLEAN_OUTLET_ID, PRODUCT_BOOLEAN_PRODUCT_ID, PRODUCT_BOOLEAN_QUANTITY,
            PRODUCT_BOOLEAN_BOOLEAN, PRODUCT_BOOLEAN_PRODUCT_CATAGORY_ID, PRODUCT_BOOLEAN_PRODUCT_TYPE_ID,
            PRODUCT_BOOLEAN_UPDATED_AT, PRODUCT_BOOLEAN_CREATED_AT};

    public static final String CREATE_PRODUCT_BOOLEAN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PRODUCT_BOOLEAN + "("
            + PRODUCT_BOOLEAN_id + " INTEGER PRIMARY KEY," +
            PRODUCT_BOOLEAN_OUTLET_ID + " INTEGER," +
            PRODUCT_BOOLEAN_PRODUCT_ID + " INTEGER," +
            PRODUCT_BOOLEAN_QUANTITY + " DOUBLE," +
            PRODUCT_BOOLEAN_BOOLEAN + " VARCHAR," +
            PRODUCT_BOOLEAN_PRODUCT_CATAGORY_ID + " INTEGER," +
            PRODUCT_BOOLEAN_PRODUCT_TYPE_ID + " INTEGER," +
            PRODUCT_BOOLEAN_CREATED_AT + " DATETIME," +
            PRODUCT_BOOLEAN_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


    //.................................PRODUCT CATEGORY...........................................
    public static final String TABLE_NAME_PRODUCT_CATEGORY = "product_categories";

    public static final String PPRODUCT_CATEGORY_id = "_id";
    public static final String PPRODUCT_CATEGORY_C_id = "category_id";
    public static final String PPRODUCT_CATEGORY_IS_ACTIVE = "is_active";
    public static final String PPRODUCT_CATEGORY_C_NAME = "category_name";
    public static final String PRODUCT_CATEGORY_UPDATED_AT = "updated_at";
    public static final String PRODUCT_CATEGORY_CREATED_AT = "created_at";


    public static final String PRODUCT_CATEGORYS[] = {TABLE_NAME_PRODUCT_CATEGORY,PPRODUCT_CATEGORY_id,PPRODUCT_CATEGORY_C_id, PPRODUCT_CATEGORY_C_NAME,
            PPRODUCT_CATEGORY_IS_ACTIVE,  PRODUCT_CATEGORY_UPDATED_AT,
            PRODUCT_CATEGORY_CREATED_AT};


    public static final String CREATE_PRODCUCT_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PRODUCT_CATEGORY + "("
            + PPRODUCT_CATEGORY_id + " INTEGER PRIMARY KEY," +
            PPRODUCT_CATEGORY_IS_ACTIVE + " INTEGER," +
            PPRODUCT_CATEGORY_C_id + " INTEGER," +
            PPRODUCT_CATEGORY_C_NAME + " VARCHAR," +
            PRODUCT_CATEGORY_CREATED_AT + " DATETIME," +
            PRODUCT_CATEGORY_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


    //.................................PRODUCT COMBINATION...........................................


    public static final String TABLE_NAME_PRODUCT_COMBINATION = "product_combinations";

    public static final String PRODUCT_COMBINATION_P_id = "_id";
    public static final String PRODUCT_COMBINATION_PRODUCT_ID = "product_id";
    public static final String PRODUCT_COMBINATION_COMBINE_ID = "combination_id";
    public static final String PRODUCT_COMBINATION_MIN_QUANTITY = "min_quantity";
    public static final String PRODUCT_COMBINATION_PRICE = "price";
    public static final String PRODUCT_COMBINATION_EFFECTIVE = "effective_date";
    public static final String PRODUCT_COMBINATION_SLAB_ID = "slab_id";
    public static final String PRODUCT_COMBINATION_UPDATED_AT = "updated_at";
    public static final String PRODUCT_COMBINATION_CREATED_AT = "created_at";


    public static final String PRODUCT_COMBINATION[] = {TABLE_NAME_PRODUCT_COMBINATION,PRODUCT_COMBINATION_P_id,  PRODUCT_COMBINATION_COMBINE_ID, PRODUCT_COMBINATION_PRODUCT_ID,
            PRODUCT_COMBINATION_COMBINE_ID, PRODUCT_COMBINATION_MIN_QUANTITY, PRODUCT_COMBINATION_PRICE,
            PRODUCT_COMBINATION_EFFECTIVE,PRODUCT_COMBINATION_SLAB_ID, PRODUCT_COMBINATION_CREATED_AT, PRODUCT_COMBINATION_UPDATED_AT};


    public static final String CREATE_PRODUCT_COMBINATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PRODUCT_COMBINATION + "("
            + PRODUCT_COMBINATION_P_id + " INTEGER PRIMARY KEY," +
            PRODUCT_COMBINATION_PRODUCT_ID + " INTEGER," +
            PRODUCT_COMBINATION_COMBINE_ID + " INTEGER," +
            PRODUCT_COMBINATION_MIN_QUANTITY + " DOUBLE," +
            PRODUCT_COMBINATION_PRICE + " DOUBLE," +
            PRODUCT_COMBINATION_EFFECTIVE + " DATE," +
            PRODUCT_COMBINATION_SLAB_ID + " INTEGER," +
            PRODUCT_COMBINATION_CREATED_AT + " DATE," +
            PRODUCT_COMBINATION_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" + ")";


    //.................................product history........................................

    public static final String TABLE_NAME_PRODUCT_HISTORY = "product_history";

    public static final String PRODUCT_HISTORY_P_id = "_id";
    public static final String PRODUCT_HISTORY_IS_BONUS = "is_bonus";
    public static final String PRODUCT_HISTORY_START_DATE = "start_date";
    public static final String PRODUCT_HISTORY_END_DATE = "end_date";
    public static final String PRODUCT_HISTORY_PRODUCT_ID = "product_id";
    public static final String PRODUCT_HISTORY_COMBINATION_ID = "combination_id";
    public static final String PRODUCT_HISTORY_UPDATED_AT = "updated_at";
    public static final String PRODUCT_HISTORY_CREATED_AT = "created_at";


    public static final String PRODUCT_HISTORY[] = {TABLE_NAME_PRODUCT_HISTORY,PRODUCT_HISTORY_P_id, PRODUCT_HISTORY_COMBINATION_ID, PRODUCT_HISTORY_IS_BONUS,
            PRODUCT_HISTORY_START_DATE, PRODUCT_HISTORY_END_DATE, PRODUCT_HISTORY_PRODUCT_ID,
            PRODUCT_HISTORY_UPDATED_AT, PRODUCT_HISTORY_CREATED_AT};

    public static final String CREATE_PRODUCT_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PRODUCT_HISTORY + "("
            + PRODUCT_HISTORY_P_id + " INTEGER PRIMARY KEY," +
            PRODUCT_HISTORY_IS_BONUS + " INTEGER," +
            PRODUCT_HISTORY_START_DATE + " DATETIME," +
            PRODUCT_HISTORY_END_DATE + " DATETIME," +
            PRODUCT_HISTORY_PRODUCT_ID + " INTEGER," +
            PRODUCT_HISTORY_COMBINATION_ID + " INTEGER," +
            PRODUCT_COMBINATION_CREATED_AT + " DATE," +
            PRODUCT_COMBINATION_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" + ")";


    //.................................product price........................................

    public static final String TABLE_NAME_PRODUCT_PRICE = "product_price";

    public static final String PRODUCT_PRICE_P_id = "_id";
    public static final String PRODUCT_PRICE_PRODUCT_id = "product_id";
    public static final String PRODUCT_PRICE_TERGET_COUSTOMMER = "target_custommer";
    public static final String PRODUCT_PRICE_INSTITUTE_ID = "institute_id";
    public static final String PRODUCT_PRICE_PRICE = "price";
    public static final String PRODUCT_PRICE_EFFECTIVE_DATE = "effective_date";
    public static final String PRODUCT_PRICE_VAT = "vat";
    public static final String PRODUCT_PRICE_UPDATED_AT = "updated_at";
    public static final String PRODUCT_PRICE_CREATED_AT = "created_at";


    public static final String PRODUCT_PRICE[] = {TABLE_NAME_PRODUCT_PRICE,PRODUCT_PRICE_P_id, PRODUCT_PRICE_PRODUCT_id,
            PRODUCT_PRICE_TERGET_COUSTOMMER, PRODUCT_PRICE_INSTITUTE_ID, PRODUCT_PRICE_PRICE,
            PRODUCT_PRICE_EFFECTIVE_DATE,PRODUCT_PRICE_VAT, PRODUCT_PRICE_UPDATED_AT, PRODUCT_PRICE_CREATED_AT};

    public static final String CREATE_PRODUCT_PRICE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PRODUCT_PRICE + "("
            + PRODUCT_PRICE_P_id + " INTEGER PRIMARY KEY," +
            PRODUCT_PRICE_PRODUCT_id + " INTEGER," +
            PRODUCT_PRICE_TERGET_COUSTOMMER + " INTEGER," +
            PRODUCT_PRICE_INSTITUTE_ID + " INTEGER," +
            PRODUCT_PRICE_VAT + " INTEGER," +
            PRODUCT_PRICE_PRICE + " DOUBLE," +
            PRODUCT_PRICE_EFFECTIVE_DATE + " DATE," +
            PRODUCT_PRICE_UPDATED_AT + " DATE," +
            PRODUCT_PRICE_CREATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" + ")";


    //.................................PRODUCT TYPE ...........................................
    public static final String TABLE_NAME_PRODCUT_TYPE = "product_type";

    public static final String PPRODCUT_TYPE_P_id = "_id";
    public static final String PPRODCUT_TYPE_PRODUCT_TYPE_ID = "product_type_id";
    public static final String PPRODCUT_TYPE_PRODUCT_NAME = "product_type_name";


    public static final String PPRODCUT_TYPE_UPDATED_AT = "updated_at";
    public static final String PPRODCUT_TYPE_CREATED_AT = "created_at";


    public static final String PPRODCUT_TYPE[] = {TABLE_NAME_PRODCUT_TYPE,PPRODCUT_TYPE_P_id,
            PPRODCUT_TYPE_PRODUCT_TYPE_ID, PPRODCUT_TYPE_PRODUCT_NAME, PPRODCUT_TYPE_UPDATED_AT, PPRODCUT_TYPE_CREATED_AT};


    public static final String CREATE_PRODUCT_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PRODCUT_TYPE + "("
            + PPRODCUT_TYPE_P_id + " INTEGER PRIMARY KEY," +
            PPRODCUT_TYPE_PRODUCT_TYPE_ID + " INTEGER," +
            PPRODCUT_TYPE_PRODUCT_NAME + " VARCHAR," +
            PPRODCUT_TYPE_CREATED_AT + " DATETIME," +
            PPRODCUT_TYPE_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";

    //.................................PRODUCT ...........................................
    public static final String TABLE_NAME_PRODUCT = "product";

    public static final String PRODUCT_P_id = "_id";
    public static final String PRODUCT_PRODUCT_ID = "product_id";
    public static final String PRODUCT_PRODUCT_NAME = "product_name";
    public static final String PRODUCT_CATAGORY_ID = "product_category_id";
    public static final String PRODUCT_TYPE_ID = "product_type_id";
    public static final String PRODUCT_ORDER = "product_order";
    public static final String PRODUCT_IS_INJECTABLE = "is_injectable";
    public static final String PRODUCT_IMAGE_URL = "image_url";
    public static final String PRODUCT_UPDATED_AT = "updated_at";
    public static final String PRODUCT_CREATED_AT = "created_at";


    public static final String PPRODCUT[] = {TABLE_NAME_PRODUCT,PRODUCT_P_id,
            PRODUCT_PRODUCT_ID, PRODUCT_PRODUCT_NAME, PRODUCT_CATAGORY_ID, PRODUCT_TYPE_ID, PRODUCT_ORDER, PRODUCT_IS_INJECTABLE,PRODUCT_IMAGE_URL, PRODUCT_UPDATED_AT, PRODUCT_CREATED_AT};


    public static final String CREATE_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PRODUCT + "("
            + PRODUCT_P_id + " INTEGER PRIMARY KEY," +
            PRODUCT_PRODUCT_ID + " INTEGER," +
            PRODUCT_PRODUCT_NAME + " VARCHAR," +
            PRODUCT_CATAGORY_ID + " INTEGER," +
            PRODUCT_TYPE_ID + " INTEGER," +
            PRODUCT_ORDER + " INTEGER," +
            PRODUCT_IS_INJECTABLE + " INTEGER," +
            PRODUCT_IMAGE_URL + " Text," +
            PRODUCT_CREATED_AT + " DATETIME," +
            PRODUCT_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";

    //.................................SALES WEEK...........................................


    public static final String TABLE_NAME_SALES_WEEK = "sales_week";

    public static final String SALES_WEEK_P_id = "_id";
    public static final String SALES_WEEK_id = "sales_week_id";
    public static final String SALES_WEEK_NAME = "sales_week_name";
    public static final String SALES_WEEK_UPDATED_AT = "updated_at";
    public static final String SALES_WEEK_CREATED_AT = "created_at";


    public static final String SALES_WEEK[] = {TABLE_NAME_SALES_WEEK,SALES_WEEK_P_id,
            SALES_WEEK_id, SALES_WEEK_NAME, SALES_WEEK_UPDATED_AT, SALES_WEEK_CREATED_AT};

    public static final String CREATE_SALES_WEEKS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SALES_WEEK + "("
            + SALES_WEEK_P_id + " INTEGER PRIMARY KEY," +
            SALES_WEEK_id + " INTEGER," +
            SALES_WEEK_NAME + " VARCHAR," +
            SALES_WEEK_CREATED_AT + " DATETIME," +
            SALES_WEEK_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


    //.....................................SO Targets...........................................


    public static final String TABLE_NAME_SO_TARGETS = "so_targets";

    public static final String SO_TARGETS_P_id = "_id";
    public static final String SO_TARGETS_SO_TARGET_ID = "so_target_id";
    public static final String SO_TARGETS_TYPE_ID = "type_id";
    public static final String SO_TARGETS_FISCAL_YEAR_ID = "fiscal_year_id";
    public static final String SO_TARGETS_MONTH_ID = "month_id";
    public static final String SO_TARGETS_T_PH_OC = "target_pharma_oc";
    public static final String SO_TARGETS_T_NON_PH_OC = "target_non_pharma_oc";
    public static final String SO_TARGETS_T_PH_EC = "target_pharma_ec";
    public static final String SO_TARGETS_T_NON_PH_EC = "target_non_pharma_ec";
    public static final String SO_TARGETS_IS_PUSHED = "isPushed";
    public static final String SO_TARGETS_AC_PH_OC = "achieve_pharma_oc";
    public static final String SO_TARGETS_AC_NON_PH_OC = "achieve_non_pharma_oc";
    public static final String SO_TARGETS_AC__PH_EC = "achieve_pharma_ec";
    public static final String SO_TARGETS_AC_NON_PH_EC = "achieve_non_pharma_ec";

    public static final String SO_TARGETS_UPDATED_AT = "updated_at";
    public static final String SO_TARGETS_CREATED_AT = "created_at";


    public static final String SO_TARGETS[] = {TABLE_NAME_SO_TARGETS,SO_TARGETS_P_id,
            SO_TARGETS_SO_TARGET_ID, SO_TARGETS_TYPE_ID, SO_TARGETS_FISCAL_YEAR_ID, SO_TARGETS_MONTH_ID, SO_TARGETS_T_PH_OC,
            SO_TARGETS_T_NON_PH_OC, SO_TARGETS_T_PH_EC, SO_TARGETS_T_NON_PH_EC, SO_TARGETS_IS_PUSHED, SO_TARGETS_AC_PH_OC, SO_TARGETS_AC_NON_PH_OC, SO_TARGETS_AC__PH_EC,
            SO_TARGETS_AC_NON_PH_EC, SO_TARGETS_UPDATED_AT, SO_TARGETS_CREATED_AT};


    public static final String CREATE_SO_TARGETS_TABLE = "CREATE TABLE IF NOT EXISTS " + "so_targets" + "("
            + SO_TARGETS_P_id + " INTEGER PRIMARY KEY," +
            SO_TARGETS_SO_TARGET_ID + " INTEGER," +
            SO_TARGETS_TYPE_ID + " INTEGER," +  //1 for OC,2 for EC and 3 for Session
            SO_TARGETS_FISCAL_YEAR_ID + " INTEGER," +
            SO_TARGETS_MONTH_ID + " INTEGER," +
            SO_TARGETS_AC_PH_OC + " DOUBLE," +
            SO_TARGETS_AC_NON_PH_OC + " DOUBLE," +
            SO_TARGETS_AC__PH_EC + " DOUBLE," +
            SO_TARGETS_AC_NON_PH_EC + " DOUBLE," +
            SO_TARGETS_T_PH_OC + " DOUBLE," +
            SO_TARGETS_T_NON_PH_OC + " DOUBLE," +
            SO_TARGETS_T_PH_EC + " DOUBLE," +
            SO_TARGETS_T_NON_PH_EC + " DOUBLE," +
            SO_TARGETS_IS_PUSHED + " INTEGER," +
            SO_TARGETS_CREATED_AT + " DATETIME," +
            SO_TARGETS_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


    //.................................TERRITORY ...........................................
    public static final String TABLE_NAME_TERRITORY = "territories";

    public static final String TERRITORY_P_id = "_id";
    public static final String TERRITORY_T_id = "territory_id";
    public static final String TERRITORY_NAME = "territory_name";
    public static final String TERRITORY_TYPE = "type";

    public static final String TERRITORY_UPDATED_AT = "updated_at";
    public static final String TERRITORY_CREATED_AT = "created_at";


    public static final String TERRITORY[] = {TABLE_NAME_TERRITORY,TERRITORY_P_id,
            TERRITORY_T_id, TERRITORY_NAME, TERRITORY_TYPE, TERRITORY_UPDATED_AT, TERRITORY_CREATED_AT};


    public static final String CREATE_TERRITORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TERRITORY + "("
            + TERRITORY_P_id + " INTEGER PRIMARY KEY," +
            TERRITORY_T_id + " INTEGER," +
            TERRITORY_NAME + " VARCHAR," +
            TERRITORY_TYPE + " INTEGER," +
            TERRITORY_CREATED_AT + " DATETIME," +
            TERRITORY_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


    //.................................THANA...........................................
    public static final String TABLE_NAME_THANA = "thana";

    public static final String THANA_P_id = "_id";

    public static final String THANA_TH_id = "thana_id";
    public static final String THANA_NAME = "thana_name";
    public static final String THANA_TeritoryId = "territory_id";


    public static final String THANA_UPDATED_AT = "updated_at";
    public static final String THANA_CREATED_AT = "created_at";


    public static final String THANA[] = {TABLE_NAME_THANA,THANA_P_id,
             THANA_TH_id, THANA_NAME,THANA_TeritoryId, THANA_UPDATED_AT, THANA_CREATED_AT};


    public static final String CREATE_THANA_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_THANA + "("
            + THANA_P_id + " INTEGER PRIMARY KEY," +
            THANA_TH_id + " INTEGER," +
            THANA_TeritoryId + " INTEGER," +
            THANA_NAME + " VARCHAR," +
            THANA_CREATED_AT + " DATETIME," +
            THANA_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";


    //.................................thana_territory...........................................
    public static final String TABLE_NAME_THANA_TERRITORY = "thana_territory";

    public static final String THANA_TERRITORY_P_id = "_id";
    public static final String THANA_TERRITORY_T_ID = "thana_id";
    public static final String THANA_TERRITORY_ID = "territory_id";
    public static final String THANA_TERRITORY_UPDATED_AT = "updated_at";
    public static final String THANA_TERRITORY_CREATED_AT = "created_at";

    public static final String THANA_TERRITORY[] = {TABLE_NAME_THANA_TERRITORY,THANA_TERRITORY_P_id,
            THANA_TERRITORY_T_ID, THANA_TERRITORY_ID,THANA_TERRITORY_UPDATED_AT,THANA_TERRITORY_CREATED_AT};

    public static final String CREATE_THANA_TERRITORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_THANA_TERRITORY + "("
            + THANA_TERRITORY_P_id + " INTEGER PRIMARY KEY," +
            THANA_TERRITORY_T_ID + " INTEGER," +
            THANA_TERRITORY_ID + " INTEGER," +
            THANA_TERRITORY_CREATED_AT + " DATETIME," +
            THANA_TERRITORY_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";

    //.................................UNIT...........................................

    public static final String TABLE_NAME_UNIT = "unit";

    public static final String UNIT_P_id = "_id";
    public static final String UNIT_U_ID = "unit_id";
    public static final String UNIT_UNAME = "unit_name";
    public static final String UNIT_UPDATED_AT = "updated_at";
    public static final String UNIT_CREATED_AT = "created_at";


    public static final String UNIT[] = {TABLE_NAME_UNIT,UNIT_P_id,
            UNIT_U_ID,UNIT_UNAME,UNIT_UPDATED_AT,UNIT_CREATED_AT};


    public static final String CREATE_UNIT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_UNIT + "("
            + UNIT_P_id + " INTEGER PRIMARY KEY," +
            UNIT_U_ID + " INTEGER," +
            UNIT_UNAME + " VARCHAR," +
            UNIT_CREATED_AT + " DATETIME," +
            UNIT_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";

    //.................................VISIT PLAN...........................................

    public static final String TABLE_NAME_VISIT_PLAN = "visit_list";

    public static final String VISIT_PLAN_id = "_id";
    public static final String VISIT_PLAN_Sid = "plan_id";
    public static final String VISIT_PLAN_sat = "sat";
    public static final String VISIT_PLAN_sun = "sun";
    public static final String VISIT_PLAN_mon = "mon";
    public static final String VISIT_PLAN_tue = "tue";
    public static final String VISIT_PLAN_wed = "wed";
    public static final String VISIT_PLAN_thu = "thu";
    public static final String VISIT_PLAN_fri = "fri";
    public static final String VISIT_PLAN_route_id = "route_id";
    public static final String VISIT_PLAN_weekid = "week_id";
    public static final String VISIT_PLAN_UPDATED_AT = "updated_at";
    public static final String VISIT_PLAN_CREATED_AT = "created_at";


    public static final String VISIT_PLAN[] = {TABLE_NAME_VISIT_PLAN,VISIT_PLAN_id,
            VISIT_PLAN_Sid, VISIT_PLAN_sat, VISIT_PLAN_sun, VISIT_PLAN_mon, VISIT_PLAN_tue,VISIT_PLAN_wed,VISIT_PLAN_thu,VISIT_PLAN_fri,VISIT_PLAN_route_id,VISIT_PLAN_weekid,
            VISIT_PLAN_UPDATED_AT, VISIT_PLAN_CREATED_AT};

    public static final String CREATE_VISIT_PLAN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_VISIT_PLAN + "("
            + VISIT_PLAN_id + " INTEGER PRIMARY KEY," +
            VISIT_PLAN_Sid + " INTEGER," +
            VISIT_PLAN_sun + " INTEGER," +
            VISIT_PLAN_mon + " INTEGER," +
            VISIT_PLAN_tue + " INTEGER," +
            VISIT_PLAN_wed + " INTEGER," +
            VISIT_PLAN_thu + " INTEGER," +
            VISIT_PLAN_fri + " INTEGER," +
            VISIT_PLAN_sat + " INTEGER," +
            VISIT_PLAN_route_id + " INTEGER," +
            VISIT_PLAN_weekid + " INTEGER," +
            VISIT_PLAN_CREATED_AT + " DATETIME, " +
            VISIT_PLAN_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" + ")";


    public static final String TABLE_NAME_ROOT = "route";

    public static final String ROOT_P_id = "_id";
    public static final String ROUTE_ID = "route_id";

    public static final String ROUTE_NAME = "route_name";
    public static final String ROOT_UPDATED_AT = "updated_at";
    public static final String ROOT_CREATED_AT = "created_at";


    public static final String ROOT[] = {TABLE_NAME_ROOT,ROOT_P_id,
            ROUTE_ID, ROUTE_NAME, ROOT_UPDATED_AT, ROOT_CREATED_AT};


    public static final String CREATE_ROOT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ROOT + "("
            + ROOT_P_id + " INTEGER PRIMARY KEY," +
            ROUTE_ID + " INTEGER," +
            ROUTE_NAME + " VARCHAR," +
            ROOT_CREATED_AT + " DATETIME," +
            ROOT_UPDATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
            ")";
    public static final String All_TABLE_NAME[] = {TABLE_NAME_DISCOUNT,TABLE_NAME_BANK_BRANCH, TABLE_NAME_BONUS_PARTY, TABLE_NAME_BONUSES, TABLE_NAME_CREDIT_COLLECTION, TABLE_NAME_CURRENT_PROMOTION_PRODUCTS, TABLE_NAME_DEPOSIT_BALANCE
            , TABLE_NAME_DEPOSITS, TABLE_NAME_DESIGNATION, TABLE_NAME_DISTRIBUTOR_PRODUCT_PRICES, TABLE_NAME_DISTRIBUTOR_PRODUCT_COMBINATIONS, TABLE_NAME_GENERAL_NOTICE, TABLE_NAME_GIFT_ISSUE, TABLE_NAME_GIFT_ISSUE_DETAILS, TABLE_NAME_GPS_TRACKING_TIME, TABLE_NAME_INSTRUMENT_NUMBER, TABLE_NAME_INSTRUMENT_TYPE
            , TABLE_NAME_LOCATION, TABLE_NAME_MARKETS, TABLE_NAME_MATERIALS, TABLE_NAME_MEMO_DETAILS, TABLE_NAME_MEMOS, TABLE_NAME_MEMOS, TABLE_NAME_MONTHLY_EFFECTIVE_CALL, TABLE_NAME_NCP_CHALLAN_DETAILS, TABLE_NAME_NCP_RETURN
            , TABLE_NAME_STOCKS, TABLE_NAME_NGO, TABLE_NAME_MESSAGE, TABLE_NAME_OUTLET_CATEGORY, TABLE_NAME_OUTLET_VISIT, TABLE_NAME_OUTLET_DELETE, TABLE_NAME_OUTLETS, TABLE_NAME_PAYMENTS, TABLE_NAME_PRODUCT_BOOLEAN, TABLE_NAME_PRODUCT_CATEGORY, TABLE_NAME_PRODUCT_COMBINATION,
            TABLE_NAME_PRODUCT_HISTORY, TABLE_NAME_PRODUCT_PRICE, TABLE_NAME_PRODCUT_TYPE,TABLE_NAME_DIST_BONUS_PRODUCT, TABLE_NAME_PRODUCT,TABLE_NAME_ORDER,TABLE_NAME_ORDER_DETAILS ,TABLE_NAME_SALES_WEEK, TABLE_NAME_SO_TARGETS, TABLE_NAME_TERRITORY, TABLE_NAME_THANA, TABLE_NAME_THANA_TERRITORY, TABLE_NAME_UNIT, TABLE_NAME_ROOT, TABLE_NAME_VISIT_PLAN,TABLE_NAME_FISCAL_YEAR};

    public static final String[][] Allfild = {DISCOUNT,BANK_BRANCH, BONUS_PARTY, BONUSES, CREDIT_COLLECTION, CURRENT_PROMOTION_PRODUCTS, DEPOSIT_BALANCE, DEPOSITS, DESIGNATION
            , DISTRIBUTOR_PRODUCT_PRICES, DISTRIBUTOR_PRODUCT_COMBINATIONS, GENERAL_NOTICE, GIFT_ISSUE, GIFT_ISSUE_DETAILS, GPS_TRACKING_TIME, INSTRUMENT_NUMBER, INSTRUMENT_TYPE, LOCATION, MARKETS, MATERIALS,
            MEMO_DETAILS, MEMOS, MONTHLY_EFFECTIVE_CALL, NCP_RETURNS, STOCKS, NGO, MESSAGES, OUTLET_CATAGORY,ORDER,ORDER_DETAILS, OUTLET_VISIT, OUTLETS, PAYMENTS, PRODUCT_CATEGORYS, PRODUCT_COMBINATION, PRODUCT_HISTORY, PRODUCT_PRICE,
            PPRODCUT_TYPE, PPRODCUT, SALES_WEEK, SO_TARGETS, TERRITORY, THANA, THANA_TERRITORY, UNIT, VISIT_PLAN, ROOT,DIST_BONUS,FISCAL_YEAR};

    public static final String TABLE_NAME_ALL_DATA[] = {TABLE_NAME_OUTLETS,TABLE_NAME_MARKETS,TABLE_NAME_PRODCUT_TYPE,TABLE_NAME_BANK_BRANCH,TABLE_NAME_DESIGNATION,TABLE_NAME_INSTRUMENT_TYPE,TABLE_NAME_MATERIALS,TABLE_NAME_OUTLET_CATEGORY,TABLE_NAME_NGO,TABLE_NAME_ROOT,TABLE_NAME_UNIT,TABLE_NAME_SALES_WEEK};



}
