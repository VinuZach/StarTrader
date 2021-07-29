package com.example.startraders.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class BaseResponse {

    @SerializedName("status")
    @Expose
    var status : String? = null

    @SerializedName("message")
    @Expose
    var message : String? = null
}