package com.example.startraders.Repository

import com.example.startraders.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiEndPoints
{

    @Multipart
    @POST("login")
    fun verifyUser(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<LoginResponse>>

    @Multipart
    @POST("GetCustomer")
    fun retrieveUserList(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<CustomerList>>

    @Multipart
    @POST("Logout")
    fun logoutUser(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<BaseResponse>>

    @Multipart
    @POST("RequestStatement")
    fun requestStatement(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<BaseResponse>>

    @Multipart
    @POST("NewReceipt")
    fun uploadReceipt(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<BaseResponse>>


    @Multipart
    @POST("GetCustomerDates")
    fun retrieveCustomerDiscountDates(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<CustomerDiscountDates>>

    @Multipart
    @POST("GetInvoiceByCustomer")
    fun retrieveCustomerPrevBill(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<CustomerDiscountDates>>

    @Multipart
    @POST("OutStandingBalance")
    fun retrieveCustomerBalance(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<CustomerOutResponse>>

}