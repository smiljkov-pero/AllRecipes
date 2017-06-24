package com.allrecipes.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.media.Image
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.allrecipes.App
import com.allrecipes.R
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.mikepenz.fastadapter.IAdapter
import android.R.raw
import com.allrecipes.util.ToastUtils
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget



open class BaseActivity : AppCompatActivity() {
    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    var loadingView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    fun getApp(): App {
        return application as App
    }

    open fun showLoading() {
        if (loadingView == null) {
            loadingView = LayoutInflater.from(this).inflate(R.layout.loading_view, null)
        }

        if (loadingView != null) {
            val rootView = findViewById(android.R.id.content) as ViewGroup
            try {
                rootView.removeView(loadingView)
            } catch (ignore: Exception) {
            }
            val image = loadingView!!.findViewById(R.id.loadingView) as ImageView
            val imageViewTarget = GlideDrawableImageViewTarget(image)
            Glide.with(this).load(R.drawable.eatstreet_loading).into(imageViewTarget)

            rootView.addView(loadingView)
        }
    }

    fun hideLoading() {
        if (loadingView != null) {
            val rootView = findViewById(android.R.id.content) as ViewGroup
            if (loadingView != null && rootView != null) {
                rootView.removeView(loadingView)
            }
        }
    }

    fun showToast(resId: Int) {
        ToastUtils.showToast(baseContext, resId)
    }

    fun showToast(txt: String) {
        ToastUtils.showToast(baseContext, txt)
    }
}