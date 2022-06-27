package com.srapp.kotlin

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class BonusCampaign {
    @SerializedName("bonus_cam_info")
    @Expose
    private var bonusCamInfo: List<BonusCamInfo>? = null

    fun getBonusCamInfo(): List<BonusCamInfo?>? {
        return bonusCamInfo
    }

    fun setBonusCamInfo(bonusCamInfo: List<BonusCamInfo>) {
        this.bonusCamInfo = bonusCamInfo
    }

    class BonusCamInfo {
        @SerializedName("id")
        @Expose
        private var id: String? = null

        @SerializedName("product_list")
        @Expose
        private var productList: String? = null

        @SerializedName("start_date")
        @Expose
        private var startDate: String? = null

        @SerializedName("end_date")
        @Expose
        private var endDate: String? = null

        @SerializedName("details")
        @Expose
        private var details: String? = null

        @SerializedName("attachmentLink")
        @Expose
        private var attachmentLink: String? = null

        fun getId(): String? {
            return id
        }

        fun setId(id: String?) {
            this.id = id
        }

        fun getProductList(): String? {
            return productList
        }

        fun setProductList(productList: String?) {
            this.productList = productList
        }

        fun getStartDate(): String? {
            return startDate
        }

        fun setStartDate(startDate: String?) {
            this.startDate = startDate
        }

        fun getEndDate(): String? {
            return endDate
        }

        fun setEndDate(endDate: String?) {
            this.endDate = endDate
        }

        fun getDetails(): String? {
            return details
        }

        fun setDetails(details: String?) {
            this.details = details
        }

        fun getAttachmentLink(): String? {
            return attachmentLink
        }

        fun setAttachmentLink(attachmentLink: String?) {
            this.attachmentLink = attachmentLink
        }
    }
}