package com.allrecipes.di.managers

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseManager {
    val fireBaseDb : DatabaseReference? = null

    constructor(){
        FirebaseDatabase.getInstance().reference
    }

    fun getChannels() {

    }
}