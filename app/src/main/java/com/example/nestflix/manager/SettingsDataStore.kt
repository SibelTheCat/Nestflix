package com.example.nestflix.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map





class SettingsDataStore(context: Context) {
private val dataStore: DataStore<Preferences> = context.createDataStore(name = "settings")
private val ipAddressDefault = ""

    companion object {
        val PREF_IPADDRESS = preferencesKey<String>("ipAddress")

        private var INSTANCE: SettingsDataStore? = null

        fun getInstance(context: Context) : SettingsDataStore {
            return INSTANCE?: synchronized(this){
            INSTANCE?.let {
                return it
            }
            val instance = SettingsDataStore(context)
            INSTANCE = instance
            instance
        }}
    }
//set Value
    suspend fun setIpAddress(ipAddress: String){
        dataStore.edit {settings ->
            settings[PREF_IPADDRESS] = ipAddress
        }
    }
//get Value
    val getIpAddress: Flow<String> = dataStore.data
    .map { settings ->
        settings[PREF_IPADDRESS] ?:ipAddressDefault


    }

}