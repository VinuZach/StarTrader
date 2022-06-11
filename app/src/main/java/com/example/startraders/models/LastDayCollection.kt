package com.example.startraders.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LastDayCollection
{
    @SerializedName("total")
    @Expose
    var total : String = ""

    @SerializedName("received_status")
    @Expose
    var recievedStatus : String = ""

    @SerializedName("customer_name")
    @Expose
    var customerName : String = ""
}