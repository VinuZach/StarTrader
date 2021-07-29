package com.example.startraders.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class CustomerList {

    @SerializedName("status")
    @Expose
    var status : String = ""

    @SerializedName("data")
    @Expose
    var data : List<CustomerDetails>? = null
}

class CustomerDetails {

    @SerializedName("customer_name")
    @Expose
    var customerName : String = ""

    @SerializedName("id")
    @Expose
    var id : String = ""

    override fun toString() : String {
        return customerName
    }
}