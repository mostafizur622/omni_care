package com.srapp.kotlin

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class ProductCombinationV2 {
    @SerializedName("product_combination")
    @Expose
    private var productCombination: List<ProductCombination>? = null

    fun getProductCombination(): List<ProductCombination?>? {
        return productCombination
    }

    fun setProductCombination(productCombination: List<ProductCombination?>?) {
        this.productCombination = productCombination as List<ProductCombination>?
    }

    class ProductCombination {
        @SerializedName("id")
        @Expose
        private var id: String? = null

        @SerializedName("product_price_id")
        @Expose
        private var productPriceId: String? = null

        @SerializedName("combination_id")
        @Expose
        private var combinationId: String? = null

        @SerializedName("product_id")
        @Expose
        private var productId: String? = null

        @SerializedName("indivisual_or_total")
        @Expose
        private var indivisualOrTotal: String? = null

        @SerializedName("min_qty")
        @Expose
        private var minQty: String? = null

        @SerializedName("min_total_qty")
        @Expose
        private var minTotalQty: String? = null

        @SerializedName("price")
        @Expose
        private var price: String? = null

        @SerializedName("parent_slab_id")
        @Expose
        private var parentSlabId: String? = null

        @SerializedName("offer_type")
        @Expose
        private var offerType: String? = null

        @SerializedName("effective_date")
        @Expose
        private var effectiveDate: String? = null

        @SerializedName("created_at")
        @Expose
        private var createdAt: String? = null

        @SerializedName("created_by")
        @Expose
        private var createdBy: String? = null

        @SerializedName("updated_at")
        @Expose
        private var updatedAt: String? = null

        @SerializedName("updated_by")
        @Expose
        private var updatedBy: Any? = null

        @SerializedName("end_date")
        @Expose
        private var endDate: Any? = null

        @SerializedName("other_pricing")
        @Expose
        private var otherPricing: List<OtherPricing>? = null

        fun getId(): String? {
            return id
        }

        fun setId(id: String?) {
            this.id = id
        }

        fun getProductPriceId(): String? {
            return productPriceId
        }

        fun setProductPriceId(productPriceId: String?) {
            this.productPriceId = productPriceId
        }

        fun getCombinationId(): String? {
            return combinationId
        }

        fun setCombinationId(combinationId: String?) {
            this.combinationId = combinationId
        }

        fun getProductId(): String? {
            return productId
        }

        fun setProductId(productId: String?) {
            this.productId = productId
        }

        fun getIndivisualOrTotal(): String? {
            return indivisualOrTotal
        }

        fun setIndivisualOrTotal(indivisualOrTotal: String?) {
            this.indivisualOrTotal = indivisualOrTotal
        }

        fun getMinQty(): String? {
            return minQty
        }

        fun setMinQty(minQty: String?) {
            this.minQty = minQty
        }

        fun getMinTotalQty(): String? {
            return minTotalQty
        }

        fun setMinTotalQty(minTotalQty: String?) {
            this.minTotalQty = minTotalQty
        }

        fun getPrice(): String? {
            return price
        }

        fun setPrice(price: String?) {
            this.price = price
        }

        fun getParentSlabId(): String? {
            return parentSlabId
        }

        fun setParentSlabId(parentSlabId: String?) {
            this.parentSlabId = parentSlabId
        }

        fun getOfferType(): String? {
            return offerType
        }

        fun setOfferType(offerType: String?) {
            this.offerType = offerType
        }

        fun getEffectiveDate(): String? {
            return effectiveDate
        }

        fun setEffectiveDate(effectiveDate: String?) {
            this.effectiveDate = effectiveDate
        }

        fun getCreatedAt(): String? {
            return createdAt
        }

        fun setCreatedAt(createdAt: String?) {
            this.createdAt = createdAt
        }

        fun getCreatedBy(): String? {
            return createdBy
        }

        fun setCreatedBy(createdBy: String?) {
            this.createdBy = createdBy
        }

        fun getUpdatedAt(): String? {
            return updatedAt
        }

        fun setUpdatedAt(updatedAt: String?) {
            this.updatedAt = updatedAt
        }

        fun getUpdatedBy(): Any? {
            return updatedBy
        }

        fun setUpdatedBy(updatedBy: Any?) {
            this.updatedBy = updatedBy
        }

        fun getEndDate(): Any? {
            return endDate
        }

        fun setEndDate(endDate: Any?) {
            this.endDate = endDate
        }

        fun getOtherPricing(): List<OtherPricing?>? {
            return otherPricing
        }

        fun setOtherPricing(otherPricing: List<OtherPricing?>?) {
            this.otherPricing = otherPricing as List<OtherPricing>?
        }

        class OtherPricing {
            @SerializedName("type")
            @Expose
            private var type: String? = null

            @SerializedName("reffrence_id")
            @Expose
            private var reffrenceId: String? = null

            @SerializedName("price")
            @Expose
            private var price: String? = null

            fun getType(): String? {
                return type
            }

            fun setType(type: String?) {
                this.type = type
            }

            fun getReffrenceId(): String? {
                return reffrenceId
            }

            fun setReffrenceId(reffrenceId: String?) {
                this.reffrenceId = reffrenceId
            }

            fun getPrice(): String? {
                return price
            }

            fun setPrice(price: String?) {
                this.price = price
            }
        }

    }
}