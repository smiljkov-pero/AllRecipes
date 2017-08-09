package com.allrecipes.ui

import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.allrecipes.App
import com.allrecipes.R
import com.allrecipes.network.OfflineException
import com.allrecipes.util.ToastUtils
import com.google.firebase.analytics.FirebaseAnalytics


open class BaseActivity : AppCompatActivity() {
    lateinit var firebaseAnalytics: FirebaseAnalytics
    var loadingView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
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

            rootView.addView(loadingView)
        }
    }

    fun hideLoading() {
        if (loadingView != null) {
            val rootView = findViewById(android.R.id.content) as ViewGroup
            if (loadingView != null) {
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

    fun isAtLeastLollipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    protected fun setStatusBarColor(colorRes: Int) {
        if (isAtLeastLollipop()) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(colorRes)
        }
    }
    fun handleApiError(throwable: Throwable, predicate: () -> Unit) {
        when (throwable) {
            is OfflineException -> showConnectivityError(predicate)
            else -> showError(throwable, predicate)
        }
    }

    private fun showConnectivityError(predicate: () -> Unit) {
        val errorLayout = LayoutInflater.from(this).inflate(R.layout.error_connectivity_layout, null)
        val rootView = findViewById(android.R.id.content) as ViewGroup
        rootView.addView(errorLayout)
        errorLayout.findViewById(R.id.retryButton).setOnClickListener {
            hideConnectivityError()
            predicate()
        }
    }

    private fun showError(throwable: Throwable, predicate: () -> Unit) {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.error_happened_try_again),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.retry)) { predicate() }
        .show()
    }

    fun hideConnectivityError() {
        (findViewById(android.R.id.content) as ViewGroup).removeView(findViewById(R.id.error_connectivity_layout))
    }

    open fun getScreenNameForTracking(): String? {
        return null
    }
}