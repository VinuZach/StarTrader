package com.example.startraders.models

import android.app.Activity
import com.example.startraders.Repository.SharedPrefData
import com.example.startraders.RepositoryManager
import com.google.gson.Gson
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


class LoginResponse {

    @SerializedName("status")
    @Expose
    var status : String = ""

    @SerializedName("id")
    @Expose
    var id : String = ""

    @SerializedName("new_invoice")
    @Expose
    var newInvoice : String = ""


}