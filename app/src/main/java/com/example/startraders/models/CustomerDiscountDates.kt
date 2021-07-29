package com.example.startraders.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class CustomerDiscountDates {

    @SerializedName("status")
    @Expose
    var status : String? = null

    @SerializedName("date")
    @Expose
    var date : List<String>? = null
}