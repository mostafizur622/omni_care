package com.srapp.kotlin

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.srapp.Db_Actions.DB_Helper
import com.srapp.Db_Actions.Data_Source
import com.srapp.TempData
import com.srapp.apiService.ApiClient
import com.srapp.apiService.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class DataRepository(val application: Application) {

    val db = Data_Source(application)

    val pricePushStatus = MutableLiveData<String>()

    var bonusCampaignList = MutableLiveData<List<BonusCampaign.BonusCamInfo>>()

    fun getProductCombinationV2Data(MyJson: JsonObject) {
        val service = ApiClient.getClient?.create(ApiInterface::class.java)
        val call = service?.getProductCombinationV2Data(MyJson)

        Log.e("Service_Call_Details","Url= http://182.160.103.236:8079/api_data_dist133Retrives/get_product_combination_v2.json"+ MyJson.toString())
        val emptyList = ArrayList<String>()
        //calling the api ---------------------------------------------
        call?.enqueue(object : Callback<ProductCombinationV2> {
            override fun onResponse(call: Call<ProductCombinationV2>, response: Response<ProductCombinationV2>) {
                if (response.isSuccessful) {

                    try {
                        pricePushStatus.postValue("Successful")
                        Log.e("all_json_data", response.body().toString())

                        val datacheck = HashMap<String, String>()
                        datacheck["table_name"]="product_combinations"
                        datacheck["item_count"]=response.body()?.getProductCombination()?.size.toString()
                        db.InsertTable(datacheck,"dataCheck")
                        for (i in 0 until response.body()?.getProductCombination()?.size!!) {
                            var product_id = response.body()?.getProductCombination()?.get(i)?.getProductId()
                            var combination_id = response.body()?.getProductCombination()?.get(i)?.getCombinationId()
                            var min_qty = response.body()?.getProductCombination()?.get(i)?.getMinQty()
                            var price = response.body()?.getProductCombination()?.get(i)?.getPrice()
                            var effective_date = response.body()?.getProductCombination()?.get(i)?.getEffectiveDate()
                            var id = response.body()?.getProductCombination()?.get(i)?.getId() // Slab Id
                            var updated_at = response.body()?.getProductCombination()?.get(i)?.getUpdatedAt()
                            var product_price_id = response.body()?.getProductCombination()?.get(i)?.getProductPriceId()

                            val map2 = HashMap<String, String>()
                            map2["product_id"] = product_id!!
                            map2["combination_id"] = combination_id!! // No need
                            map2["min_quantity"] = min_qty!!
                            map2["price"] = price!!
                            map2["effective_date"] = effective_date!!
                            map2["product_price_id"] = product_price_id!!
                            map2["slab_id"] = id!! // No need
                            map2["updated_at"] = updated_at!!
                            db.InsertTable(map2, "product_combinations")


                            for (j in 0 until response.body()?.getProductCombination()?.get(i)?.getOtherPricing()?.size!!) {
                                if (!response.body()?.getProductCombination()?.get(i)?.getOtherPricing().isNullOrEmpty()) {
                                    val product_combination_id = response.body()?.getProductCombination()?.get(i)?.getId()
                                    val type = response.body()?.getProductCombination()?.get(i)?.getOtherPricing()?.get(j)?.getType()
                                    val reference_id = response.body()?.getProductCombination()?.get(i)?.getOtherPricing()?.get(j)?.getReffrenceId()
                                    val otherPrice = response.body()?.getProductCombination()?.get(i)?.getOtherPricing()?.get(j)?.getPrice()
                                    var updated_at = response.body()?.getProductCombination()?.get(i)?.getUpdatedAt()

                                    val map1 = HashMap<String, String>()
                                    map1["product_combination_id"] = product_combination_id!! // slab_id
                                    map1["type"] = type!!
                                    map1["reference_id"] = reference_id!!
                                    map1["price"] = otherPrice!!
                                    map1["updated_at"] = updated_at!!
                                    db.InsertTable(map1, "product_price_other_for_slabs_v2")

                                }
                            }

                        }
                    } catch (e: Exception) {
                        Log.e("Error", "Product price V2 data fail:" + e.message)
                    }

                } else {
                    Log.d("onResponse", "Product price V2 data fail:" + response.body())
                }
            }

            override fun onFailure(call: Call<ProductCombinationV2>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }
        })
    }

    fun getSpecialGroupData(MyJson: JsonObject) {
        val service = ApiClient.getClient?.create(ApiInterface::class.java)
        val call = service?.getSpecialGroupData(MyJson)

        Log.e("Service_Call_Details","Url= http://182.160.103.236:8079/api_data_dist133Retrives/get_special_group.json"+ MyJson.toString())
        val emptyList = ArrayList<String>()
        //calling the api ---------------------------------------------
        call?.enqueue(object : Callback<SpecialGroupData> {
            override fun onResponse(call: Call<SpecialGroupData>, response: Response<SpecialGroupData>) {
                if (response.isSuccessful) {
                    try {
                        pricePushStatus.postValue("Successfully")
                        Log.e("all_data_special_group", response.body().toString())

                        val datacheck = HashMap<String, String>()
                        datacheck["table_name"]="special_group"
                        datacheck["item_count"]=response.body()?.getSpecialGroup()?.size.toString()
                        db.InsertTable(datacheck,"dataCheck")
                        for (i in 0 until response.body()?.getSpecialGroup()?.size!!) {
                            var _id = response.body()?.getSpecialGroup()?.get(i)?.getId()
                            var name = response.body()?.getSpecialGroup()?.get(i)?.getName()
                            var remarks = response.body()?.getSpecialGroup()?.get(i)?.getRemarks()
                            var start_date = response.body()?.getSpecialGroup()?.get(i)?.getStartDate()
                            var end_date = response.body()?.getSpecialGroup()?.get(i)?.getEndDate()
                            var is_dist = response.body()?.getSpecialGroup()?.get(i)?.getIsDist()
                            var updated_at = response.body()?.getSpecialGroup()?.get(i)?.getUpdatedAt()

                            val map1 = HashMap<String, String>()
                            map1["pid"] = _id!!
                            map1["name"] = name!!
                            map1["remarks"] = remarks!!
                            map1["start_date"] = start_date!!
                            map1["end_date"] = end_date!!
                            map1["is_dist"] = is_dist!!
                            map1["updated_at"] = updated_at!!
                            db.InsertTable(map1, "special_group")

                            for (j in 0 until response.body()?.getSpecialGroup()?.get(i)?.getDetails()?.size!!) {
                                var details_id = response.body()?.getSpecialGroup()?.get(i)?.getDetails()?.get(j)?.getId()
                                var details_sp_grp_id = response.body()?.getSpecialGroup()?.get(i)?.getDetails()?.get(j)?.getSpecialGroupId()
                                var details_creat_for = response.body()?.getSpecialGroup()?.get(i)?.getDetails()?.get(j)?.getCreateFor()
                                var details_reference_id = response.body()?.getSpecialGroup()?.get(i)?.getDetails()?.get(j)?.getReffrenceId()

                                val map2ForDetails = HashMap<String, String>()
                                map2ForDetails["_id"] = details_id!!
                                map2ForDetails["special_group_id"] = details_sp_grp_id!!
                                map2ForDetails["create_for"] = details_creat_for!!
                                map2ForDetails["reffrence_id"] = details_reference_id!!
                                map2ForDetails["updated_at"] = updated_at!!
                                db.InsertTable(map2ForDetails, "special_group_details")
                            }
                        }
                    } catch (e: Exception) {
                    }

                } else {
                    Log.d("onResponse", "special group data fail:" + response.body())
                }
            }

            override fun onFailure(call: Call<SpecialGroupData>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }
        })
    }

    fun getProductCombinationListData(MyJson: JsonObject?) {
        val service = ApiClient.getClient?.create(ApiInterface::class.java)
        val call = MyJson?.let { service?.getProductCombinationListData(it) }

        Log.e("Service_Call_Details","Url= http://182.160.103.236:8079/api_data_dist133Retrives/get_combination_list.json"+ MyJson.toString())

        val emptyList = ArrayList<String>()

        //calling the api --------------------------------------------------------------------------
        call?.enqueue(object : Callback<ProductCombinationsList?> {
            override fun onResponse(call: Call<ProductCombinationsList?>, response: Response<ProductCombinationsList?>) {
                if (response.isSuccessful) {
                    try {
                      //  Log.i("PCombinationListData", Gson().toJson(response))

                        val datacheck = HashMap<String, String>()
                        datacheck["table_name"]="product_combination_list"
                        datacheck["item_count"]=response.body()?.getCombinations()?.size.toString()
                        db.InsertTable(datacheck,"dataCheck")

                        for (i in 0 until response.body()?.getCombinations()?.size!!) {
                            var _id = response.body()?.getCombinations()?.get(i)?.getId()
                            var name = response.body()?.getCombinations()?.get(i)?.getName()
                            var effective_date = response.body()?.getCombinations()?.get(i)?.getEffectiveDate()
                            // var create_for = response.body()?.getCombinations()?.get(i)?.getCreateFor()
                            var reference_id = response.body()?.getCombinations()?.get(i)?.getReffrenceId()
                            var combined_qty = response.body()?.getCombinations()?.get(i)?.getCombinedQty()
                            var updated_at = response.body()?.getCombinations()?.get(i)?.getUpdatedAt()

                            val map1 = HashMap<String, String>()
                            map1["_id"] = _id!!
                            map1["name"] = name!!
                            map1["effective_date"] = effective_date!!
                            map1["create_for"] = "0"
                            map1["reference_id"] = reference_id!!
                            map1["combined_qty"] = combined_qty!!
                            map1["updated_at"] = updated_at!!
                            db.InsertTable(map1, "product_combination_list")

                            for (j in 0 until response.body()?.getCombinations()?.get(i)?.getDetails()?.size!!) {

                                var details_id=response.body()?.getCombinations()?.get(i)?.getDetails()?.get(j)?.getId()
                                var combination_id=response.body()?.getCombinations()?.get(i)?.getDetails()?.get(j)?.getCombinationId()
                                var p_id=response.body()?.getCombinations()?.get(i)?.getDetails()?.get(j)?.getProductId()
                                // var product_combination_id=response.body()?.getCombinations()?.get(i)?.getDetails()?.get(j)?.getProductCombinationId()

                                val map2 = HashMap<String, String>()
                                map2["_id"] = details_id!!
                                map2["combination_id"] = combination_id!!
                                map2["product_id"] = p_id!!
                                map2["product_combination_id"] = "0"
                                map1["updated_at"] = updated_at
                                db.InsertTable(map2, "Product_combination_list_details_v2")
                            }

                        }
                    } catch (e: Exception) {
                        e.message?.let { Log.d("Exception_p_combination", it) }
                    }

                } else {
                    Log.d("onResponse__", "p_combination_list data fail:" + response.body())
                }
            }

            override fun onFailure(call: Call<ProductCombinationsList?>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }
        })
    }

    fun getBonusCampaignData(MyJson: JsonObject?) {
        val service = ApiClient.getClient?.create(ApiInterface::class.java)
        val call = service?.getBonusCampaignData(MyJson)

        Log.e("Service_Call_Details","Url= http://182.160.103.236:8079/api_data_dist133Retrives/get_bonus_campaign_list.json"+ MyJson.toString())

        val emptyList = ArrayList<String>()

        //calling the api --------------------------------------------------------------------------
        call?.enqueue(object : Callback<BonusCampaign?> {
            override fun onResponse(call: Call<BonusCampaign?>, response: Response<BonusCampaign?>) {
                if (response.isSuccessful) {
                    try {
                        Log.e("bonus_campaign_list", response.body().toString())

                        if (response.isSuccessful) {
                            bonusCampaignList.value = response.body()?.getBonusCamInfo() as List<BonusCampaign.BonusCamInfo>?
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    Log.d("onResponse", "bonus campaign data fail:" + response.body())
                }
            }

            override fun onFailure(call: Call<BonusCampaign?>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }
        })
    }


    fun getProductPriceV2Data(MyJson: JsonObject) {
        val service = ApiClient.getClient?.create(ApiInterface::class.java)
        val call = service?.getPriceV2Data(MyJson)

        Log.e("TestMyJson", MyJson.toString())

        val emptyList = ArrayList<String>()

        //calling the api --------------------------------------------------------------------------
        call?.enqueue(object : Callback<ProductPriceV2> {
            override fun onResponse(call: Call<ProductPriceV2>, response: Response<ProductPriceV2>) {
                if (response.isSuccessful) {

                    Log.e("all_data_product_prices", response.body().toString())


                } else {
                    Log.d("onResponse", "special group data fail:" + response.body())
                }
            }

            override fun onFailure(call: Call<ProductPriceV2>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }
        })
    }

}