package com.example.startraders.models

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable


class CustomerList
{

    @SerializedName("status")
    @Expose
    var status: String = ""

    @SerializedName("data")
    @Expose
    var data: List<CustomerDetails>? = null
}

class CustomerDetails : Serializable
{

    @SerializedName("customer_name")
    @Expose
    var customerName: String = ""

    @SerializedName("id")
    @Expose
    var id: String = ""

    constructor(customerName: String, id: String)
    {
        this.customerName = customerName
        this.id = id
    }


    override fun toString(): String
    {
        return customerName
    }
}