package com.srapp.kotlin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SpecialGroupData {
    @SerializedName("special_group")
    @Expose
    private var specialGroup: List<SpecialGroup>? = null

    fun getSpecialGroup(): List<SpecialGroup?>? {
        return specialGroup
    }

    fun setSpecialGroup(specialGroup: List<SpecialGroup?>?) {
        this.specialGroup = specialGroup as List<SpecialGroup>?
    }

    class SpecialGroup {
        @SerializedName("id")
        @Expose
        private var id: String? = null

        @SerializedName("name")
        @Expose
        private var name: String? = null

        @SerializedName("remarks")
        @Expose
        private var remarks: String? = null

        @SerializedName("start_date")
        @Expose
        private var startDate: String? = null

        @SerializedName("end_date")
        @Expose
        private var endDate: String? = null

        @SerializedName("is_dist")
        @Expose
        var isDist: String? = null

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

        fun getRemarks(): String? {
            return remarks
        }

        fun setRemarks(remarks: String?) {
            this.remarks = remarks
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

        fun getIsDist(): String? {
            return isDist
        }

        fun setIsDist(isDist: String?) {
            this.isDist = isDist
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

            @SerializedName("special_group_id")
            @Expose
            private var specialGroupId: String? = null

            @SerializedName("create_for")
            @Expose
            private var createFor: String? = null

            @SerializedName("reffrence_id")
            @Expose
            private var reffrenceId: String? = null

            fun getId(): String? {
                return id
            }

            fun setId(id: String?) {
                this.id = id
            }

            fun getSpecialGroupId(): String? {
                return specialGroupId
            }

            fun setSpecialGroupId(specialGroupId: String?) {
                this.specialGroupId = specialGroupId
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

        }

    }
}