package com.example.startraders.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class LastFiveReceiptsResponse
{

    @SerializedName("id")
    @Expose
    val id: String= ""

    @SerializedName("invoice_number")
    @Expose
    val invoiceNumber: String= ""

    @SerializedName("customer_name")
    @Expose
    val customer_name: String= ""

    @SerializedName("customer_id")
    @Expose
    val customerId: String= ""

    @SerializedName("exicutive_id")
    @Expose
    val exicutiveId: String= ""

    @SerializedName("date")
    @Expose
    val date: String = ""

    @SerializedName("discount_status")
    @Expose
    val discountStatus: String= ""

    @SerializedName("discount_date")
    @Expose
    val discountDate: String= ""

    @SerializedName("quantity")
    @Expose
    val quantity: String= ""

    @SerializedName("request_amount")
    @Expose
    val requestAmount: String= ""

    @SerializedName("received_status")
    @Expose
    val receivedStatus: String= ""

    @SerializedName("cheque_amount")
    @Expose
    val chequeAmount: String= ""

    @SerializedName("cash_amount")
    @Expose
    val cashAmount: String= ""

    @SerializedName("neft_amount")
    @Expose
    val neftAmount: String= ""

    @SerializedName("total_amount")
    @Expose
    val totalAmount: String= ""

    @SerializedName("admin_discount_status")
    @Expose
    val adminDiscountStatus: String= ""

    @SerializedName("remarks")
    @Expose
    val remarks: String= ""
}