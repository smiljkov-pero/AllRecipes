package com.allrecipes.ui.views

interface AbstractPresenterView {

    fun showToast(messageResId: Int)

    fun showToast(message: String)

    fun showLoading()

    fun hideLoading()

    fun isFinishing(): Boolean

    fun handleApiError(throwable: Throwable, predicate : () -> Unit)

    fun hideConnectivityError()
}