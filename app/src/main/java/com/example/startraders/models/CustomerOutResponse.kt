package com.example.startraders.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class CustomerOutResponse {

    @SerializedName("status")
    @Expose
    var status : String? = null

    @SerializedName("data")
    @Expose
    var date : Date? = null

    override fun toString() : String {
        return date.toString()+" "+status
    }

}
class Date
{

    @SerializedName("outstanding_balance")
    @Expose
    var outstandingBalance : String=""
    override fun toString() : String {
        return outstandingBalance
    }
}