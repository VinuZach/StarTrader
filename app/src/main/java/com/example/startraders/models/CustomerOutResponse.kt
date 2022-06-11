package com.example.startraders.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class CustomerOutResponse {

    @SerializedName("status")
    @Expose
    var status : String? = null

    @SerializedName("data")
    @Expose
    var data : OutstandingData? = null

    override fun toString() : String {
        return data.toString()+" "+status
    }

}
class OutstandingData
{

    @SerializedName("outstanding_balance")
    @Expose
    var outstandingBalance : String=""
    override fun toString() : String {
        return outstandingBalance
    }
}