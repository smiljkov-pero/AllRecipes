package com.allrecipes.di.managers

import com.allrecipes.model.Category
import com.google.firebase.database.*
import com.kelvinapps.rxfirebase.DataSnapshotMapper
import com.kelvinapps.rxfirebase.RxFirebaseDatabase

class FirebaseDatabaseManager(databaseReference: DatabaseReference) {
    val fireBaseDb: DatabaseReference = databaseReference

    fun getCategories(): rx.Observable<MutableList<Category>> {
        val ref = fireBaseDb.database.getReference("categories")

        return RxFirebaseDatabase
                .observeSingleValueEvent(ref, DataSnapshotMapper.listOf(Category::class.java))
                .cache()
    }
}