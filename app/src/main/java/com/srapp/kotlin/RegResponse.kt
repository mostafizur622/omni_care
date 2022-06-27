package com.srapp.kotlin

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class RegResponse {
    @SerializedName("user_info")
    @Expose
    private var userInfo: UserInfo? = null

    fun getUserInfo(): UserInfo? {
        return userInfo
    }

    fun setUserInfo(userInfo: UserInfo?) {
        this.userInfo = userInfo
    }

    class UserInfo {
        @SerializedName("status")
        @Expose
        private var status: Int? = null

        fun getStatus(): Int? {
            return status
        }

        fun setStatus(status: Int?) {
            this.status = status
        }
    }

}