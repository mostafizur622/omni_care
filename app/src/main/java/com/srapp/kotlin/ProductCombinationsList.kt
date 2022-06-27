package com.srapp.kotlin

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class ProductCombinationsList {
    @SerializedName("combinations")
    @Expose
    private var combinations: List<Combination>? = null

    fun getCombinations(): List<Combination?>? {
        return combinations
    }

    fun setCombinations(combinations: List<Combination?>?) {
        this.combinations = combinations as List<Combination>?
    }

    class Combination {
        @SerializedName("id")
        @Expose
        private var id: String? = null

        @SerializedName("name")
        @Expose
        private var name: String? = null

        @SerializedName("effective_date")
        @Expose
        private var effectiveDate: String? = null

        @SerializedName("create_for")
        @Expose
        private var createFor: String? = null

        @SerializedName("reffrence_id")
        @Expose
        private var reffrenceId: String? = null

        @SerializedName("combined_qty")
        @Expose
        private var combinedQty: String? = null

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
        private var updatedBy: String? = null

        @SerializedName("details")
        @Expose
        private var details: List<Detail>? = null


        fun getId(): String? {
            return id
        }

        fun setId(id: String?) {
            this.id = id
        }

        fun getName(): String? {
            return name
        }

        fun setName(name: String?) {
            this.name = name
        }

        fun getEffectiveDate(): String? {
            return effectiveDate
        }

        fun setEffectiveDate(effectiveDate: String?) {
            this.effectiveDate = effectiveDate
        }

        fun getCreateFor(): String? {
            return createFor
        }

        fun setCreateFor(createFor: String?) {
            this.createFor = createFor
        }

        fun getReffrenceId(): String? {
            return reffrenceId
        }

        fun setReffrenceId(reffrenceId: String?) {
            this.reffrenceId = reffrenceId
        }

        fun getCombinedQty(): String? {
            return combinedQty
        }

        fun setCombinedQty(combinedQty: String?) {
            this.combinedQty = combinedQty
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

        fun getUpdatedBy(): String? {
            return updatedBy
        }

        fun setUpdatedBy(updatedBy: String?) {
            this.updatedBy = updatedBy
        }

        fun getDetails(): List<Detail?>? {
            return details
        }

        fun setDetails(details: List<Detail?>?) {
            this.details = details as List<Detail>?
        }

        class Detail {
            @SerializedName("id")
            @Expose
            private var id: String? = null

            @SerializedName("combination_id")
            @Expose
            private var combinationId: String? = null

            @SerializedName("product_id")
            @Expose
            private var productId: String? = null

            @SerializedName("product_combination_id")
            @Expose
            private var productCombinationId: String? = null

            fun getId(): String? {
                return id
            }

            fun setId(id: String?) {
                this.id = id
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

            fun getProductCombinationId(): String? {
                return productCombinationId
            }

            fun setProductCombinationId(productCombinationId: String?) {
                this.productCombinationId = productCombinationId
            }
        }

    }

}