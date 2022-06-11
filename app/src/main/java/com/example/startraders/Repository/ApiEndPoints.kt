package com.example.startraders.Repository

import com.example.startraders.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndPoints
{

    @Multipart
    @POST("login")
    fun verifyUser(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<LoginResponse>>

    @Multipart
    @POST("GetCustomer")
    fun retrieveUserList(@Part partMap: List<MultipartBody.Part>): Call<ArrayList<CustomerList>>

    @GET("GetLastFiveCollection")
    fun getLastFiveReceipts(@Query("executive_id") executive_id: String, @Query("from_date") from_date: String,
        @Query("to_date") to_date: String, @Query("customer_id") customer_id: String): Call<ArrayList<LastFiveReceiptsResponse>>

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


    @GET("GetLastDayCollection")
    fun getLastDayCollections(@Query("executive_id") executive_id: String,
        @Query("to_date") to_date: String): Call<ArrayList<LastDayCollection>>;
}