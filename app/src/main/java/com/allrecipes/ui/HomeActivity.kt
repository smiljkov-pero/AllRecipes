package com.allrecipes.ui

import android.os.Bundle
import com.allrecipes.R
import com.allrecipes.model.Category
import com.allrecipes.util.LogWrapper
import com.google.firebase.database.*
import com.google.firebase.database.GenericTypeIndicator




class HomeActivity : BaseActivity() {
    var fireBaseDb : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        fireBaseDb = FirebaseDatabase.getInstance().reference

       val ref =  fireBaseDb?.database?.getReference("categories")

        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val t = object : GenericTypeIndicator<List<@JvmSuppressWildcards Category>>() {}
                val categories: List<Category> = dataSnapshot.getValue(t)
                LogWrapper.d("AAA", categories.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })

    }
}
