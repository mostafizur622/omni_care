package com.srapp.kotlin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject

class DataViewModel(application: Application) : AndroidViewModel(application){

    private val dataRepository = DataRepository(application)
    private var pricePushStatus = dataRepository.pricePushStatus
    val bonusCampaignList = dataRepository.bonusCampaignList


    fun getSuccessStatus(): MutableLiveData<String> {
        if (pricePushStatus == null) {
            pricePushStatus = MutableLiveData()
        }
        return pricePushStatus
    }


    fun getProductCombinationV2Data(MyJson: JsonObject) {
        dataRepository.getProductCombinationV2Data(MyJson)
    }

    fun getSpecialGroupData(MyJson: JsonObject) {
        dataRepository.getSpecialGroupData(MyJson)
    }

    fun getProductCombinationListData(MyJson: JsonObject) {

        dataRepository.getProductCombinationListData(MyJson)
    }

    fun getBonusCampaignData(startDate: String?, endDate: String, mac_num: String, productId: String, SRID: String) {

        val jsonObj = JsonObject()
        jsonObj.addProperty("start_date", startDate)
        jsonObj.addProperty("end_date", endDate)
        jsonObj.addProperty("mac", mac_num)
        jsonObj.addProperty("product_id", productId)
        jsonObj.addProperty("so_id", SRID)

        dataRepository.getBonusCampaignData(jsonObj)
    }

}

    class DataViewModelFactory(var application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
                return DataViewModel(application) as T
            }

            throw IllegalStateException("Unknown ViewModel class")
        }
    }
