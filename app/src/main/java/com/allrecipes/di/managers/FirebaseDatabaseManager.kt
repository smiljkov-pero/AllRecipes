package com.allrecipes.di.managers

import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.model.Channel
import com.google.firebase.database.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kelvinapps.rxfirebase.DataSnapshotMapper
import com.kelvinapps.rxfirebase.RxFirebaseDatabase
import java.util.ArrayList

class FirebaseDatabaseManager(databaseReference: DatabaseReference, localStorageManagerInterface: LocalStorageManagerInterface) {

    private val fireBaseDb: DatabaseReference = databaseReference
    private val localStorageManagerInterface: LocalStorageManagerInterface = localStorageManagerInterface

    companion object {
        val APP_CACHED_FIREBASE_CONFIG = "app.cachedFirebaseConfig"
    }

    fun fetchChannels(): rx.Observable<MutableList<Channel>> {
        val ref = fireBaseDb.database.getReference("categories")

        return RxFirebaseDatabase
                .observeSingleValueEvent(ref, DataSnapshotMapper.listOf(Channel::class.java))
                .cache()
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