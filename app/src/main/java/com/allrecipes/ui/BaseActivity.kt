package com.allrecipes.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allrecipes.R
import com.google.firebase.analytics.FirebaseAnalytics

open class BaseActivity : AppCompatActivity() {
    var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }
}