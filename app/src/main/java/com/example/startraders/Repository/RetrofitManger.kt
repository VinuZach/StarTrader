package com.example.startraders.Repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

abstract class RetrofitManger<endpoint> {

    protected abstract var baseUrl : String
    protected abstract var apiEndPoint : Class<endpoint>

    protected open val client = OkHttpClient.Builder().build()

    val retrofitBuilder by lazy {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        val client = OkHttpClient.Builder().dispatcher(dispatcher).build()
        client.connectTimeoutMillis()
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).client(client).build().create(apiEndPoint)

    }



    fun createStringMultiPartBody(parameterName:String,data : String) : MultipartBody.Part {
      return  MultipartBody.Part.createFormData(parameterName, data);
    }
    fun createFileMultiPartBody(parameterName:String,x : File) : MultipartBody.Part {
        val to_server : RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"),x)
        val filePartBody =
            MultipartBody.Part.createFormData(parameterName,x.getName(), to_server)
        return filePartBody


    }

    fun getBasicErrorMessage(errorBody : ResponseBody?) : BasicResponseBody {
        Log.d("asdsads", "getBasicErrorMessage: " + errorBody)
        return getCustomErrorMessage(errorBody)
    }

    fun getCustomErrorMessage(errorBody : ResponseBody?) : BasicResponseBody {
        val gson = Gson()
        val type = object : TypeToken<BasicResponseBody>() {}.type
        return gson.fromJson(errorBody!!.charStream(), type)

    }



    fun handleErrorResponse(apiResponse : ApiResponse, t : Throwable) {
        apiResponse.onResponseObtained(false, t.localizedMessage)
    }

    interface ApiResponse {

        fun onResponseObtained(isSuccess : Boolean, responseData : Any?)
    }

    class BasicResponseBody {

        @SerializedName("message")
        @Expose
        var message : String = ""

        @SerializedName("errorCode")
        @Expose
        var errorCode : Int = 0

    }


}