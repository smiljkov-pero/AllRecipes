package com.allrecipes.managers

import android.util.Log
import com.allrecipes.model.Channel
import com.allrecipes.util.RxFirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Maybe

class FirebaseDatabaseManager(databaseReference: DatabaseReference, localStorageManagerInterface: LocalStorageManagerInterface) {

    private val fireBaseDb: DatabaseReference = databaseReference
    private val localStorageManagerInterface: LocalStorageManagerInterface = localStorageManagerInterface

    companion object {
        val APP_CACHED_FIREBASE_CONFIG = "app.cachedFirebaseConfig"
    }

    fun fetchChannels(): Maybe<DataSnapshot> {
        val ref = fireBaseDb.database.getReference("categories")

        return RxFirebaseDatabase.observeSingleValueEvent(ref)
    }

    fun storeFirebaseConfig(channels: List<Channel>) {
        localStorageManagerInterface.putString(
            APP_CACHED_FIREBASE_CONFIG,
            GsonBuilder().create().toJson(channels)
        )
    }

    fun restoreFirebaseConfig(): List<Channel> {
        val appConfig = localStorageManagerInterface.getString(APP_CACHED_FIREBASE_CONFIG, "")
        val listType = object : TypeToken<ArrayList<Channel>>() {}.type

        return GsonBuilder().create().fromJson<List<Channel>>(appConfig, listType)
    }
}