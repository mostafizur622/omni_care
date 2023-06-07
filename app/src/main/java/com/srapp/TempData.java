package com.srapp;

import android.util.Log;

import com.srapp.bonusPolicy.PolicyBonusProduct;
import com.srapp.bonusPolicy.PolicyID;

import java.util.ArrayList;
import java.util.HashMap;

public class TempData {

    public static  Double DISCOUNTP =0.0 ;
    public static  int DISTYPE =0 ;
    public static  Double DISCOUNT =0.0 ;
    public static  Double VAT =0.0 ;
    public static ArrayList<HashMap<String,String>> INVOICE_DETAILS_FOR_ALL_PRODUCT =new ArrayList<>() ;
    public static String editMemo="";
    public static String orderNumber ="";
    public static String memoNumber ="";
    public static String sales_person_id="";
    public static String TargetCustomer="";
    public static String MemoDateTime="";
    public static String InstituteID="";


    public static String OutletCatagoryID="";
    public static String SalesUPricePosition="";
    public static String SalesUPrice="";
    public static String Test="";
    public static ArrayList<HashMap<String,String>> BonusShowList = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> INVOICE_DETAILS = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> INVOICE_DETAILS_DRAFT_PRINT = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> TotalBonusProductList = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> BonusProductQuantity = new ArrayList<>();
    public static ArrayList<String> BonusChxSelected = new ArrayList<>();
    public static String OutletID="";
    public static String OutletName="";
    public static String SaleScreen="";
    public static String MarketID="";
    public static String tempThana="";
    public static String tempMarket="";
    public static boolean policyClear=true;

    public static String TempBonus_EN ="";
    public static String TempBonus_BN ="";
    public static String TempExtraBonus="";
    public static String From_App="";
    public static String for_memo_delete="";
    public static String MemoDate="";
    public static String DayCloseMemoEditable="";
    public static String InvoiceTotal="";
    public static String TempGift="";
    public static String CurrentInventoryID="";
    public static String SelectedOrderID;
    public static String MemoTotalView;
    public static String isPushed;
    public static String TeritoryID;
    public static String gross_value;
    public static ArrayList<HashMap<String, String>> BonusArrayList=new ArrayList<>();
    public static ArrayList<HashMap<String, String>> GiftArrayList=new ArrayList<>();
    public static   HashMap<String, String> policyMap = new HashMap<String, String>();
    public static ArrayList<HashMap<String, String>> BPbonus_product = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> BPbonus_product_temp = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> BPbonus_product_t = new ArrayList<HashMap<String, String>>();
    public static int ORDER_STATUE;
    public static int ORDER_PlanVisit;
    public static int ORDER_plan_id;
    public static int ORDER_TO_MEMO;
    public static int PROCESSING_ON_SERVER=7;
    public static boolean MEMO_EDIT;
    public static String InvoicePayment;
    public static String InvoiceGrand;
    public static String printBlankContent;
    public static String printTopContent;
    public static String PrintOutlate;
    public static String PrintBody;
    public static String printBottomContent;
    public static String printName;
    public static String AllprintName;
    public static String printboldthank;
    public static String printThanku;
    public static String AllprintValue;
    public static String printTopContentProductReceive;
    public static String SO_Stock_print;
    public static String printBottonText;
    public static Object Outlet_Address;
    public static Object Outlet_Mobile;
    public static Object Outlet_Mobile_MEM;
    public static Object Outlet_Mobile_DE;

    //---------------------2021-----------------
    public static String PriceSlabID="";
    public static String PriceCombinationID="";
    public static int policy_type = 0;
    public static String discount_info_flag = "0";
    public static String discount_data = "";
    public static String SetRelation="";

    public static ArrayList<PolicyID> policyIdsArrList=new ArrayList<>();
    public static ArrayList<HashMap<String, String>> policyArrayList= new ArrayList<>();
    public static ArrayList<PolicyBonusProduct> policyBonusProductArrList=new ArrayList<>();
    public static HashMap<String, String> priceMap = new HashMap<String, String>();
    public static HashMap<String, String> price_idMap = new HashMap<String, String>();
    public static HashMap<String, String> combination_idMap = new HashMap<String, String>();
    public static HashMap<String, String> vatMap = new HashMap<String, String>();

    //----------------------------------------------------------------------------------------------
    public static HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> BPSelected_bonus = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>();
    public static HashMap<String, String> BPSelected_set = new HashMap<String, String>();
    public static HashMap<String, String> BPSelected_policy_type = new HashMap<String, String>();
    public static HashMap<String, String> BPSelected_option_id = new HashMap<String, String>();
    public static HashMap<String, String> PolicySetRelation = new HashMap<String, String>();
    public static HashMap<String,ArrayList<HashMap<String, String>>> BPBonusProductView = new HashMap<String, ArrayList<HashMap<String, String>>>();
    public static ArrayList<HashMap<String, String>> BPSelected_product = new ArrayList<HashMap<String, String>>();

    public static HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> OldBPSelected_bonus = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>();

    public static HashMap<String, String> OldBPSelected_set = new HashMap<String, String>();
    public static HashMap<String, String> OldBPSelected_policy_type = new HashMap<String, String>();
    public static HashMap<String, String> OldBPSelected_option_id = new HashMap<String, String>();


    public static String policy_id = "";
    public static String disselection = "";
    public static String edit_data_remove = "0";

    public static HashMap<String, String> discountmap = new HashMap<String, String>();
    public static HashMap<String, String> discountmapview = new HashMap<String, String>();
    public static HashMap<String, String> discountoffer = new HashMap<String, String>();
    public static HashMap<String, String> discountofferPolicyid = new HashMap<String, String>();
    public static HashMap<String, String> discounttype = new HashMap<String, String>();

    public static ArrayList<HashMap<String,String>> INVOICE_DETAILS_PRINT = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> INVOICE_SUMMERY_PRINT = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> STOCK_STORE_PRINT = new HashMap<>();

    public static String summeryStartDate = "";
    public static String summeryEndDate = "";
    public static String summeryReportName = "";

    public static String ConvertTOBangla(String input){


        return input.replaceAll("0","০").replaceAll("1","১").replaceAll("2","২").replaceAll("3","৩").replaceAll("4","৪").replaceAll("5","৫").replaceAll("6","৬").replaceAll("7","৭").replaceAll("8","৮").replaceAll("9","৯");
    }


}
