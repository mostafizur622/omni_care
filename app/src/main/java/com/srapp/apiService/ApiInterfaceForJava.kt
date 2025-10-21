package com.srapp.apiService

import com.google.gson.JsonObject
import com.srapp.Outlet_wise_Product_report
import com.srapp.kotlin.*
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterfaceForJava {


    @Headers("Content-Type: application/json")
    @POST("get_sr_check_in_out.json")
    fun GET_ATTENDANCE_STATUS(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("set_sr_check_in_out.json")
    fun SET_ATTENDANCE_STATUS(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json")
    @POST("update_data_push_time.json")
    fun UpdatePushTime(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("dist_user_login.json")
    fun Login(@Body JSONObject: RequestBody): Call<String>
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("dist_new_user_login.json")
    fun loginNew(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("save_sr_image.json")
    fun saveSrImage(@Body JSONObject: RequestBody): Call<String>
    @Headers("Content-Type: application/json")
    @POST("create_outlet.json")
    fun Push(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_data_pull.json")
    fun PULL(@Body JSONObject: RequestBody): Call<String>
    @Headers("Content-Type: application/json")
    @POST("get_dist_sr_leave_types.json")
    fun leaveType(@Body JSONObject: RequestBody): Call<String>
    @Headers("Content-Type: application/json")
    @POST("create_dist_sr_leave.json")
    fun leaveCreate(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_dist_sr_leave_list.json")
    fun leaveList(@Body JSONObject: RequestBody): Call<String>
    @Headers("Content-Type: application/json")
    @POST("dist_update_outlet.json")
    fun GPS_UPDATE(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_create_memo.json")
    fun CREATE_MEMO(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_create_order.json")
    fun ORDERPUSH(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_order_ready_for_process_list.json")
    fun ORDERS_FOR_PROCESS(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_process_order_list.json")
    fun ORDERS_FOR_UNPROCESS(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_outlet_wise_sales_report.json")
    fun OUTLET_WISE_SALES_REPORT(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_outlet_product_wise_sales_report.json")
    fun Outlet_wise_Product_report(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json")
    @POST("get_bonus_cards_report.json")
    fun INCENTIVE_PARTY(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("giftitem_received.json")
    fun GIFT_ISSUE(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_route_wise_outlet_visit_report.json")
    fun OUTLET_VISIT_REPORT(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_policy_list_v2.json")
    fun Bonus_Policy(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_outlet_group.json")
    fun Bonus_Policy_Outlet(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_product_units.json")
    fun Measurement_Unit__Details_Table(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_last_orders.json")
    fun GET_LAST_RECORD(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_last_memos.json")
    fun GET_LAST_MEMO(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_gift_item_list.json")
    fun GIFT_ITEM_LIST(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_gift_item_details.json")
    fun GIFT_ITEM_Details(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_order_details.json")
    fun GET_ORDER_DETAILS(@Body JSONObject: RequestBody): Call<String>
    @Headers("Content-Type: application/json")
    @POST("sr_attendance_in_out.json")
    fun Policy_Bonus_Applicable(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_order_delivery_schedule.json")
    fun PROCESS_ORDER_LIST(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_order_delivery_unprocess.json")
    fun UNPROCESS_ORDER_LIST(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_memo_create_pdf.json")
    fun MAKE_PFD(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_schedule_order_details.json")
    fun ORDER_DETAILS(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_outlet_details.json")
    fun OutletDetails(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_create_outlet.json")
    fun CreateOutlet(@Body JSONObject: RequestBody): Call<String>
    @Headers("Content-Type: application/json")
    @POST("dist_create_temp_outlet.json")
    fun CreateTempOutlet(@Body JSONObject: RequestBody): Call<String>
    @Headers("Content-Type: application/json")
    @POST("get_temp_outlet_status.json")
    fun Get_OUTLET_STATUS(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_outlet_list.json")
    fun OutletList(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_market_details.json")
    fun MarketDetails(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_create_market.json")
    fun CreateMarket(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_map_sales_tracking.json")
    fun pushLocation(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("lost_time_tracking.json")
    fun lostTime(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("failed_time_tracking.json")
    fun failedTime(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("push_service_stop.json")
    fun pushServiceStop(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("create_outlet_visit.json")
    fun pushOutletVisit(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_market_list.json")
    fun MarketList(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_visit_plan_list.json")
    fun VisitPlan(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json")
    @POST("dist_order_cancel.json")
    fun CANCEL_ORDER(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_system_current_date_time.json")
    fun ServerTime(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_order_bounce.json")
    fun BOUNCE_ORDER(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_order_cancel_list.json")
    fun CANCEL_ORDER_LIST(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_process_order_list_for_delivery.json")
    fun PROCESS_ORDER_LIST_FOR_MEMO(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_change_password.json")
    fun CHANGE_PASSWORD(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_price_list.json")
    fun PriceList(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json")
    @POST("dist_bonus_party_affiliation.json")
    fun BonusParty(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_product_order_bonus_report.json")
    fun PRODUCT_WISE_BONUS(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_product_bonus_report.json")
    fun PRODUCT_WISE_BONUS_SUMMERY(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_order_stock_info.json")
    fun Stock(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_product_order_report.json")
    fun PRODUCT_WISE_SALES(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json")
    @POST("sr_login_report.json")
    fun ATTENDANCE_HISTORY(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("get_ncp_collection_list.json")
    fun GET_NCP_Collection(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_product_processed_order_report.json")
    fun PRODUCT_WISE_SALES_SUMMERY_Invoece(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_product_sales_report.json")
    fun PRODUCT_WISE_SALES_SUMMERY_DELIVERy(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json")
    @POST("save_ncp_collection.json")
    fun SAVE_NCP(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("update_ncp_collection.json")
    fun Update_NCP(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json")
    @POST("ncpReplacement.json")
    fun NCP_Replacemnt(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_sales_targets.json")
    fun ProductTarget(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_last_memo_details.json")
    fun LAST_MEMO(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("dist_create_out_of_plan_visit.json")
    fun OUT_OF_PLAN_VISIT(@Body JSONObject: RequestBody): Call<String>


    @Headers("Content-Type: application/json")
    @POST("get_temp_outlet_status.json")
    fun tempOutletStatus(@Body JSONObject: RequestBody): Call<String>

    @Headers("Content-Type: application/json")
    @POST("http://103.134.90.33:8181/new_life/api_data_dist02_retrives/get_system_current_date_time.json")
    fun getTimeDate(@Body JSONObject: RequestBody): Call<String>


}


