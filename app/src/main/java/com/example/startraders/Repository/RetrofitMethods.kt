package com.example.startraders.Repository

import android.util.Log
import com.example.startraders.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlin.collections.ArrayList

//https://docs.google.com/document/u/0/d/1X8ZKPd8pLLBlxyPiaw4DZz-oGSsOW_UMYoF8Ap5Eq8Q/mobilebasic
object RetrofitMethods : RetrofitManger<ApiEndPoints>()
{

    override var baseUrl: String = "https://appsonline.in/star/api/"
    override var apiEndPoint: Class<ApiEndPoints> = ApiEndPoints::class.java
    private const val TAG = "RetrofitMethods12321"

    fun verifyUser(userName: String, password: String, apiResponse: ApiResponse)
    {
        val partList: MutableList<MultipartBody.Part> = ArrayList()
        partList.add(createStringMultiPartBody("email", userName))
        partList.add(createStringMultiPartBody("password", password))
        retrofitBuilder.verifyUser(partList).enqueue(object : Callback<ArrayList<LoginResponse>>
        {
            override fun onResponse(call: Call<ArrayList<LoginResponse>>, response: Response<ArrayList<LoginResponse>>)
            {
                Log.d(TAG, "onResponse: " + response.isSuccessful)
                Log.d(TAG, "onResponse: " + response.body())
                if (response.isSuccessful)
                {
                    try
                    {
                        val loginResponse = response.body()?.get(0)
                        if (loginResponse?.status == "true")
                        {
                            apiResponse.onResponseObtained(true, loginResponse)
                        }
                        else apiResponse.onResponseObtained(false, "Please enter valid user details")
                    } catch (e: Exception)
                    {
                        apiResponse.onResponseObtained(false, "Response not obtained")
                    }
                }
                else apiResponse.onResponseObtained(false, "Response not obtained")
            }

            override fun onFailure(call: Call<ArrayList<LoginResponse>>, t: Throwable)
            {
                Log.d(TAG, "onResponse:verifyUser  " + t.message)
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }


    fun retrieveLastFiveBills(executiveID: String, startDate: String, endDate: String, customerId: String, apiResponse: ApiResponse)
    {
        Log.d(TAG, "retrieveLastFiveBills: startDate " + startDate)
        Log.d(TAG, "retrieveLastFiveBills: endDate " + endDate)
        Log.d(TAG, "retrieveLastFiveBills: customerId " + customerId)

        retrofitBuilder.getLastFiveReceipts(executiveID, startDate, endDate, customerId)
            .enqueue(object : Callback<ArrayList<LastFiveReceiptsResponse>>
            {
                override fun onResponse(call: Call<ArrayList<LastFiveReceiptsResponse>>,
                    response: Response<ArrayList<LastFiveReceiptsResponse>>)
                {
                    if (response.isSuccessful)
                    {
                        Log.d(TAG, "onResponse: 1 " + response.body()?.size)
                        response.body()?.let {

                            apiResponse.onResponseObtained(true, it)
                        } ?: run {
                            apiResponse.onResponseObtained(false, "Response not obtained")
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<LastFiveReceiptsResponse>>, t: Throwable)
                {
                    Log.d(TAG, "onFailure: " + t.localizedMessage)
                    apiResponse.onResponseObtained(false, "Sever connection error")
                }

            })

    }

    fun retrieveCustomerList(collectionAgentId: String, apiResponse: ApiResponse)
    {
        val partList: MutableList<MultipartBody.Part> = ArrayList()
        Log.d(TAG, "retrieveCustomerList: " + collectionAgentId)
        partList.add(createStringMultiPartBody("customer_id", collectionAgentId))

        retrofitBuilder.retrieveUserList(partList).enqueue(object : Callback<ArrayList<CustomerList>>
        {
            override fun onResponse(call: Call<ArrayList<CustomerList>>, response: Response<ArrayList<CustomerList>>)
            {
                Log.d(TAG, "retrieveCustomerList: " + response.isSuccessful)
                if (response.isSuccessful)
                {

                    try
                    {
                        val customerList = response.body()?.get(0)
                        if (customerList?.status == "true")
                        {
                            apiResponse.onResponseObtained(true, customerList)

                        }
                        else apiResponse.onResponseObtained(false, "No customers available ")
                    } catch (e: Exception)
                    {
                        apiResponse.onResponseObtained(false, "Response not obtained")
                    }
                }
                else apiResponse.onResponseObtained(false, "Response not obtained")


            }

            override fun onFailure(call: Call<ArrayList<CustomerList>>, t: Throwable)
            {
                Log.d(TAG, "onFailure: retrieveCustomerList " + t.message)
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }

    fun logoutUser(collectionAgentId: String, apiResponse: ApiResponse)
    {
        val partList: MutableList<MultipartBody.Part> = ArrayList()
        Log.d(TAG, "retrieveCustomerList: " + collectionAgentId)
        partList.add(createStringMultiPartBody("customer_id", collectionAgentId))
        retrofitBuilder.logoutUser(partList).enqueue(object : Callback<ArrayList<BaseResponse>>
        {
            override fun onResponse(call: Call<ArrayList<BaseResponse>>, response: Response<ArrayList<BaseResponse>>)
            {
                if (response.isSuccessful)
                {

                    try
                    {
                        val baseRespose = response.body()?.get(0)
                        if (baseRespose?.status == "true")
                        {
                            apiResponse.onResponseObtained(true, baseRespose)

                        }
                        else apiResponse.onResponseObtained(false, "No customers available ")
                    } catch (e: Exception)
                    {
                        apiResponse.onResponseObtained(false, "Response not obtained")
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<BaseResponse>>, t: Throwable)
            {
                Log.d(TAG, "onFailure: logoutUser " + t.message)
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }

    fun requestStatement(customerId: String, fromDate: String, toDate: String, apiResponse: ApiResponse)
    {

        val partList: MutableList<MultipartBody.Part> = ArrayList()
        partList.add(createStringMultiPartBody("customerId", customerId))
        partList.add(createStringMultiPartBody("fromDate", fromDate))
        partList.add(createStringMultiPartBody("toDate", toDate))
        retrofitBuilder.requestStatement(partList).enqueue(object : Callback<ArrayList<BaseResponse>>
        {
            override fun onResponse(call: Call<ArrayList<BaseResponse>>, response: Response<ArrayList<BaseResponse>>)
            {
                if (response.isSuccessful)
                {

                    try
                    {
                        val baseRespose = response.body()?.get(0)
                        if (baseRespose?.status == "true")
                        {
                            apiResponse.onResponseObtained(true, baseRespose)

                        }
                        else apiResponse.onResponseObtained(false, "Request not processed ")
                    } catch (e: Exception)
                    {
                        apiResponse.onResponseObtained(false, "Response not obtained")
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<BaseResponse>>, t: Throwable)
            {
                Log.d(TAG, "onFailure: requestStatement " + t.message)
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }

    var partList: MutableList<MultipartBody.Part> = ArrayList()
    fun uploadReceipt_cash(customer: CustomerDetails, invoiceNumber: String, date: String, remark: String, executiveID: String,
        totalAmount: String, discountDetails: DiscountModel, apiResponse: ApiResponse)
    {
        partList = ArrayList()
        Log.d(TAG, "uploadReceipt_cash: invoiceNumber " + invoiceNumber)
        partList.add(createStringMultiPartBody("received_status", "cash"))
        partList.add(createStringMultiPartBody("exId", executiveID))
        partList.add(createStringMultiPartBody("remark", remark))
        partList.add(createStringMultiPartBody("date", date))
        partList.add(createStringMultiPartBody("invoice_number", invoiceNumber))

        checkAndAddDiscount(discountDetails)
        partList.add(createStringMultiPartBody("customer_name", customer.id))
        partList.add(createStringMultiPartBody("customer_id", customer.id))
        partList.add(createStringMultiPartBody("total_amount", totalAmount))

        partList.add(createStringMultiPartBody("rtgs_num", ""))
        partList.add(createStringMultiPartBody("rtgs_date", ""))
        partList.add(createStringMultiPartBody("rtgs_amount", ""))

        partList.add(createStringMultiPartBody("cheque_num", ""))
        partList.add(createStringMultiPartBody("cheque_date", ""))
        partList.add(createStringMultiPartBody("cheque_amount", ""))
        callUploadReceipt(partList, apiResponse)

    }

    private fun checkAndAddDiscount(discountDetails: DiscountModel)
    {
        if (discountDetails.receiptDate.length == 0)
        {
            partList.add(createStringMultiPartBody("discount", "no"))
            partList.add(createStringMultiPartBody("pre_date", ""))
            partList.add(createStringMultiPartBody("quntity", ""))
            partList.add(createStringMultiPartBody("req_amount", "0"))
        }
        else
        {
            partList.add(createStringMultiPartBody("discount", "yes"))
            partList.add(createStringMultiPartBody("pre_date", discountDetails.receiptDate))
            partList.add(createStringMultiPartBody("quntity", discountDetails.receiptNo))
            partList.add(createStringMultiPartBody("req_amount", discountDetails.requestedAmt))
        }
    }

    fun uploadReceipt_cheque(remark: String, date: String, invoiceNumber: String, executiveID: String, customer: CustomerDetails,
        chequeNumber: String, chequeDate: String, chequeAmount: String, discountDetails: DiscountModel, apiResponse: ApiResponse)
    {

        partList = ArrayList()

        partList.add(createStringMultiPartBody("received_status", "cheque"))

        partList.add(createStringMultiPartBody("cheque_num", chequeNumber))
        partList.add(createStringMultiPartBody("cheque_date", chequeDate))
        partList.add(createStringMultiPartBody("cheque_amount", chequeAmount))

        partList.add(createStringMultiPartBody("rtgs_num", ""))
        partList.add(createStringMultiPartBody("rtgs_date", ""))
        partList.add(createStringMultiPartBody("rtgs_amount", ""))
        checkAndAddDiscount(discountDetails)
        partList.add(createStringMultiPartBody("total_amount", "0"))

        partList.add(createStringMultiPartBody("customer_name", customer.id))
        partList.add(createStringMultiPartBody("customer_id", customer.id))

        partList.add(createStringMultiPartBody("exId", executiveID))
        partList.add(createStringMultiPartBody("invoice_number", invoiceNumber))
        partList.add(createStringMultiPartBody("remark", remark))
        partList.add(createStringMultiPartBody("date", date))

        partList.add(createStringMultiPartBody("discount", "no"))
        partList.add(createStringMultiPartBody("pre_date", ""))
        partList.add(createStringMultiPartBody("quntity", ""))
        partList.add(createStringMultiPartBody("req_amount", "0"))

        callUploadReceipt(partList, apiResponse)
    }

    fun uploadReceipt_RTGS(executiveID: String, date: String, invoiceNumber: String, customer: CustomerDetails, rtgsAmount: String,
        rtgsDate: String, rtgsNumber: String, remark: String, discountDetails: DiscountModel, apiResponse: ApiResponse)
    {


        partList = ArrayList()

        partList.add(createStringMultiPartBody("received_status", "rtgs"))

        partList.add(createStringMultiPartBody("cheque_num", ""))
        partList.add(createStringMultiPartBody("cheque_date", ""))
        partList.add(createStringMultiPartBody("cheque_amount", ""))

        partList.add(createStringMultiPartBody("rtgs_num", rtgsNumber))
        partList.add(createStringMultiPartBody("rtgs_date", rtgsDate))
        partList.add(createStringMultiPartBody("rtgs_amount", rtgsAmount))

        partList.add(createStringMultiPartBody("total_amount", "0"))
        partList.add(createStringMultiPartBody("customer_name", customer.id))
        partList.add(createStringMultiPartBody("customer_id", customer.id))
        checkAndAddDiscount(discountDetails)
        partList.add(createStringMultiPartBody("invoice_number", invoiceNumber))
        partList.add(createStringMultiPartBody("date", date))

        partList.add(createStringMultiPartBody("discount", "no"))
        partList.add(createStringMultiPartBody("pre_date", ""))
        partList.add(createStringMultiPartBody("quntity", ""))
        partList.add(createStringMultiPartBody("req_amount", "0"))

        partList.add(createStringMultiPartBody("exId", executiveID))
        partList.add(createStringMultiPartBody("remark", remark))

        callUploadReceipt(partList, apiResponse)
    }

    private fun callUploadReceipt(partList: MutableList<MultipartBody.Part>, apiResponse: ApiResponse)
    {


        retrofitBuilder.uploadReceipt(partList).enqueue(object : Callback<ArrayList<BaseResponse>>
        {
            override fun onResponse(call: Call<ArrayList<BaseResponse>>, response: Response<ArrayList<BaseResponse>>)
            {
                Log.d(TAG, "onResponse: uploadReceipt " + response.toString())
                if (response.isSuccessful)
                {


                    try
                    {

                        val baseRespose = response.body()?.get(0)
                        Log.d(TAG, "onResponse: uploadReceipt " + baseRespose.toString())
                        if (baseRespose?.status == "true")
                        {
                            apiResponse.onResponseObtained(true, baseRespose)

                        }
                        else apiResponse.onResponseObtained(false, "Request not uploaded ")
                    } catch (e: Exception)
                    {
                        apiResponse.onResponseObtained(false, "Response not obtained")
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<BaseResponse>>, t: Throwable)
            {
                Log.d(TAG, "onFailure: uploadReceipt " + t.message)
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }

    fun retrieveCustomerDiscountDate(customerName: String, apiResponse: ApiResponse)
    {
        val partList: MutableList<MultipartBody.Part> = ArrayList()
        partList.add(createStringMultiPartBody("customerName", customerName))
        retrofitBuilder.retrieveCustomerDiscountDates(partList).enqueue(object : Callback<ArrayList<CustomerDiscountDates>>
        {
            override fun onResponse(call: Call<ArrayList<CustomerDiscountDates>>, response: Response<ArrayList<CustomerDiscountDates>>)
            {
                if (response.isSuccessful)
                {
                    try
                    {
                        val customerDiscountDates = response.body()?.get(0)
                        if (customerDiscountDates?.status == "true")
                        {
                            apiResponse.onResponseObtained(true, customerDiscountDates)
                        }
                        else apiResponse.onResponseObtained(false, "No discount dates available")
                    } catch (e: Exception)
                    {
                        apiResponse.onResponseObtained(false, "Response not obtained")
                    }
                }
                else apiResponse.onResponseObtained(false, "Response not obtained")
            }

            override fun onFailure(call: Call<ArrayList<CustomerDiscountDates>>, t: Throwable)
            {
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }

    fun retrieveCustomerPrevReceipt(customerName: String, prevReceiptDate: String, apiResponse: ApiResponse)
    {
        val partList: MutableList<MultipartBody.Part> = ArrayList()
        partList.add(createStringMultiPartBody("customerName", customerName))
        partList.add(createStringMultiPartBody("date", prevReceiptDate))

        retrofitBuilder.retrieveCustomerPrevBill(partList).enqueue(object : Callback<ArrayList<CustomerDiscountDates>>
        {
            override fun onResponse(call: Call<ArrayList<CustomerDiscountDates>>, response: Response<ArrayList<CustomerDiscountDates>>)
            {
                if (response.isSuccessful)
                {
                    try
                    {
                        val customerDiscountDates = response.body()?.get(0)
                        if (customerDiscountDates?.status == "true")
                        {
                            apiResponse.onResponseObtained(true, customerDiscountDates)
                        }
                        else apiResponse.onResponseObtained(false, "No discount dates available")
                    } catch (e: Exception)
                    {
                        apiResponse.onResponseObtained(false, "Response not obtained")
                    }
                }
                else apiResponse.onResponseObtained(false, "Response not obtained")
            }

            override fun onFailure(call: Call<ArrayList<CustomerDiscountDates>>, t: Throwable)
            {
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }


    fun retrieveCollectionReport(executiveID: String, toDate: String, apiResponse: ApiResponse)
    {
        retrofitBuilder.getLastDayCollections(executiveID, toDate).enqueue(object : Callback<ArrayList<LastDayCollection>>
        {
            override fun onResponse(call: Call<ArrayList<LastDayCollection>>, response: Response<ArrayList<LastDayCollection>>)
            {

                if (response.isSuccessful)
                {


                    apiResponse.onResponseObtained(true, response.body())


                }
                else apiResponse.onResponseObtained(false, "Response not obtained")
            }

            override fun onFailure(call: Call<ArrayList<LastDayCollection>>, t: Throwable)
            {
                Log.d(TAG, "onFailure: getLastDayCollections")
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }

    fun retrieveCustomerBalance(customerId: String, apiResponse: ApiResponse)
    {
        val partList: MutableList<MultipartBody.Part> = ArrayList()
        partList.add(createStringMultiPartBody("customerId", customerId))
        retrofitBuilder.retrieveCustomerBalance(partList).enqueue(object : Callback<ArrayList<CustomerOutResponse>>
        {
            override fun onResponse(call: Call<ArrayList<CustomerOutResponse>>, response: Response<ArrayList<CustomerOutResponse>>)
            {
                Log.d(TAG, "onResponse: " + response.body())
                if (response.isSuccessful)
                {
                    try
                    {
                        val customerDiscountDates = response.body()?.get(0)
                        if (customerDiscountDates?.status == "true")
                        {
                            apiResponse.onResponseObtained(true, customerDiscountDates)
                        }
                        else apiResponse.onResponseObtained(false, "No Outstanding Amount available")
                    } catch (e: Exception)
                    {
                        apiResponse.onResponseObtained(false, "Response not obtained")
                    }
                }
                else apiResponse.onResponseObtained(false, "Response not obtained")
            }

            override fun onFailure(call: Call<ArrayList<CustomerOutResponse>>, t: Throwable)
            {
                apiResponse.onResponseObtained(false, "Sever connection error")
            }

        })
    }


}