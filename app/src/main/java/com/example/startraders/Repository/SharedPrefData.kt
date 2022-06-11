package com.example.startraders.Repository

import android.app.Activity
import android.content.Context
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.StringDef
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class SharedPrefData() {


    private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "MainSharedData")


    companion object {
        var IS_USER_LOGGED_IN = booleanPreferencesKey(name = "isUserLoggedIn")
        var EXECUTIVE_NAME = stringPreferencesKey(name = "executiveName")
        var INVOICE_ID = stringPreferencesKey(name = "invoice_id")
        var COLLECTION_AGENT_ID = stringPreferencesKey(name = "collectionAgentID")
    }


    fun <T> saveDataToDataStore(context : Context, key : Preferences.Key<T>, data : T) {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[key] = data
            }
        }


    }

    fun <T> getDataWithoutLiveData(context : Activity, key : Preferences.Key<T>, defaultValue : T) : T {
        val value = runBlocking {

            context.dataStore.data.first().asMap().getOrDefault(key, defaultValue) as T
        }
        return value

    }

    fun <T> getDataStoreLiveData(context : Activity, key : Preferences.Key<T>, defaultValue : T) : LiveData<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }.asLiveData()

    }


}
