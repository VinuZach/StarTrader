package com.example.startraders

import com.example.startraders.Repository.RetrofitManger
import com.example.startraders.Repository.RetrofitMethods
import com.example.startraders.Repository.SharedPrefData

class RepositoryManager {
    companion object {

        val retrofitObject = RetrofitMethods
        val sharedPrefData = SharedPrefData()

    }


}