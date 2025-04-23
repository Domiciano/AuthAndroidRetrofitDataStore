package com.example.authclase10.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object LocalDataSourceProvider{

    private var instance: LocalDataStore? = null

    fun init(dataStore: DataStore<Preferences>){
        if(instance == null){
            instance = LocalDataStore(dataStore)
        }
    }

    fun get():LocalDataStore{
        return instance ?: throw IllegalStateException("LocalDataStore no está incializado")
    }

}




//Datastore está en la UI
class LocalDataStore(val dataStore: DataStore<Preferences>) {
    //Save
    suspend fun save(key:String, value:String){
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    //Load
    fun load(key:String): Flow<String> = dataStore.data.map { prefs ->
        prefs[stringPreferencesKey(key)] ?: ""
    }

}

//DataStore -> localstorage