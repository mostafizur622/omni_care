package com.srapp.Db_Actions;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.srapp.LoginActivity;
import com.srapp.Util.JAPIClient;
import com.srapp.apiService.ApiInterfaceForJava;

import org.json.JSONObject;

import okhttp3.RequestBody;

public class URL {





   public static final String Domain = "http://182.160.103.234:8079/api_data_dist140_retrives/"; //after_marge dec 6 2021

    //public static final String Domain = "http://202.126.123.157/smc_only_dms/api_data_dist139_retrives/"; //after_marge dec 6 2021
  // public static final String Domain = "http://202.126.123.157/smc_only_dms/api_data_dist141_retrives/"; //after_marge dec 6 2021



    public static final String GET_ATTENDANCE_STATUS = Domain + "get_sr_check_in_out.json";
    public static final String SET_ATTENDANCE_STATUS = Domain + "set_sr_check_in_out.json";
    public static String UpdatePushTime = Domain + "update_data_push_time.json";
    public static final String Login = Domain + "dist_user_login.json"; //
    public static final String Push = Domain + "create_outlet.json";
    public static final String PULL = Domain + "dist_data_pull.json"; //
    public static final String Log = "http://192.168.0.183/pushdata.php";
    public static final String GPS_UPDATE = Domain + "dist_update_outlet.json";
    public static final String CREATE_MEMO = Domain + "dist_create_memo.json";
    public static final String ORDERPUSH = Domain + "dist_create_order.json";
    public static final String ORDERS_FOR_PROCESS = Domain + "dist_order_ready_for_process_list.json";
    public static final String ORDERS_FOR_UNPROCESS = Domain + "dist_process_order_list.json";
    public static final String OUTLET_WISE_SALES_REPORT = Domain + "dist_outlet_wise_sales_report.json";
    public static final String INCENTIVE_PARTY = Domain + "get_bonus_cards_report.json";
    public static final String GIFT_ISSUE = Domain + "giftitem_received.json";
    public static final String VERSION = "1.4.0";
    public static final String [] EMAIL = {"tanvir.ahmed@arenaphonebd.net","abu.naser@arenaphonebd.net"};
    public static final String VERSION_txt = "Version(" + VERSION + ")";
    public static String release_date = "17-JAN-2024";
    public static final String OUTLET_VISIT_REPORT =Domain +"get_route_wise_outlet_visit_report.json";
    public static final String Bonus_Policy = Domain + "get_policy_list_v2.json"; //
    public static final String Bonus_Policy_Outlet = Domain + "get_outlet_group.json"; //
    public static final String Measurement_Unit__Details_Table = Domain +"get_product_units.json"; //
    public static final String GET_LAST_RECORD = Domain + "dist_last_orders.json";
    public static final String GET_LAST_MEMO = Domain + "dist_last_memos.json";
    public static final String GIFT_ITEM_LIST = Domain + "get_gift_item_list.json";
    public static final String GIFT_ITEM_Details = Domain + "get_gift_item_details.json";
    public static final String GET_ORDER_DETAILS = Domain + "dist_order_details.json";
    public static final String PROCESS_ORDER_LIST = Domain + "dist_order_delivery_schedule.json";
    public static final String UNPROCESS_ORDER_LIST = Domain + "dist_order_delivery_unprocess.json";
    public static final String MAKE_PFD = Domain + "dist_memo_create_pdf.json";
    public static final String ORDER_DETAILS = Domain + "dist_schedule_order_details.json";
    public static final String OutletDetails = Domain + "dist_outlet_details.json";
    public static final String CreateOutlet = Domain + "dist_create_outlet.json";
    public static final String OutletList = Domain + "dist_outlet_list.json";
    public static final String MarketDetails = Domain + "dist_market_details.json";
    public static final String CreateMarket = Domain + "dist_create_market.json";
    public static final String MarketList = Domain + "dist_market_list.json";
    public static final String VisitPlan = Domain + "dist_visit_plan_list.json";
    public static final String CANCEL_ORDER = Domain + "dist_order_cancel.json";
    public static final String CANCEL_ORDER_LIST = Domain + "dist_order_cancel_list.json";
    public static final String PROCESS_ORDER_LIST_FOR_MEMO = Domain + "dist_process_order_list_for_delivery.json";
    public static final String CHANGE_PASSWORD = Domain + "dist_change_password.json";
    public static String PriceList = Domain + "dist_price_list.json";
    public static String BonusParty = Domain + "dist_bonus_party_affiliation.json";
    public static String PRODUCT_WISE_BONUS = Domain + "dist_product_order_bonus_report.json";
    public static String PRODUCT_WISE_BONUS_SUMMERY = Domain + "dist_product_bonus_report.json";
    public static String Stock = Domain + "dist_order_stock_info.json";
    public static String PRODUCT_WISE_SALES = Domain + "dist_product_order_report.json";
    public static String ATTENDANCE_HISTORY = Domain + "sr_login_report.json";
    public static String PRODUCT_WISE_SALES_SUMMERY_Invoece = Domain + "dist_product_processed_order_report.json";
    public static String PRODUCT_WISE_SALES_SUMMERY_DELIVERy =Domain+"dist_product_sales_report.json";
    public static String PRODUCT_WISE_SALES_SUMMERY_Order =Domain+"dist_product_order_summery_report.json";
    public static final String ProductTarget = Domain + "dist_sales_targets.json";
    public static final String LAST_MEMO = Domain + "dist_last_memo_details.json";
    public static final String OUT_OF_PLAN_VISIT = Domain + "dist_create_out_of_plan_visit.json";

 public static ApiInterfaceForJava getJAPi() {
  android.util.Log.e("getJAPi", "getJAPi: ");
  return JAPIClient.getClient().create(ApiInterfaceForJava.class);
 }


 public static RequestBody convertTORequestdata(JSONObject jsonObject){

  return   RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(jsonObject).toString());
 }

 public static  boolean isInternetOn(Context context) {
  ConnectivityManager connec =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
  if ((connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED) ||
          (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING) ||
          (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING) ||
          (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)) {

   return true;
  } else if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {

   return false;
  }
  return false;
 }
 public static ProgressDialog CheckConnection(Context context,String message){
  if (!isInternetOn(context)){
   Toast.makeText(context , "No Internet Connection", Toast.LENGTH_LONG).show();
   return null;
  }else {
   ProgressDialog progressDialog = new ProgressDialog(context);
   progressDialog.setMessage(message);
   progressDialog.setCancelable(false);
   progressDialog.show();
   return progressDialog;
  }

 }

}
