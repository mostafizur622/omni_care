package com.srapp.apiService

import com.google.gson.JsonObject
import com.srapp.kotlin.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("get_product_combination_v2.json")
    fun getProductCombinationV2Data(@Body jsonObject: JsonObject): Call<ProductCombinationV2>

    @Headers("Content-Type: application/json")
    @POST("get_special_group.json")
    fun getSpecialGroupData(@Body jsonObject: JsonObject): Call<SpecialGroupData>

    @Headers("Content-Type: application/json")
    @POST("get_product_price_v2.json")
    fun getPriceV2Data(@Body jsonObject: JsonObject): Call<ProductPriceV2>

    @Headers("Content-Type: application/json")
    @POST("get_combination_list.json")
    fun getProductCombinationListData(@Body jsonObject: JsonObject): Call<ProductCombinationsList>

    @Headers("Content-Type: application/json")
    @POST("get_bonus_campaign_list.json")
    fun getBonusCampaignData(@Body jsonObject: JsonObject?): Call<BonusCampaign?>




}


