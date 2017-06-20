package com.allrecipes.ui

import android.os.Bundle
import com.allrecipes.R
import com.allrecipes.model.Category
import com.allrecipes.util.LogWrapper
import com.google.firebase.database.*
import com.google.firebase.database.GenericTypeIndicator

import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.ui.views.HomeScreenView
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeScreenView {
    var fireBaseDb : DatabaseReference? = null

    @Inject
    lateinit var presenter: HomeScreenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getApp().createHomeScreenComponent(this).inject(this)

        presenter.fetchYoutubeChannelVideos()

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
