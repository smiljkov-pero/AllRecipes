package com.allrecipes.ui.launcher

import android.accounts.Account
import android.os.Bundle
import android.util.Log

import com.allrecipes.R
import com.allrecipes.presenters.LauncherScreenPresenter
import com.allrecipes.ui.BaseActivity
import com.allrecipes.ui.LoginActivity
import com.allrecipes.ui.home.activity.HomeActivity
import com.allrecipes.util.Constants
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope

import javax.inject.Inject

class LauncherActivity : BaseActivity(), LauncherView, GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var account: Account? = null

    @Inject
    lateinit var presenter: LauncherScreenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        getApp().createLauncherScreenComponent(this).inject(this)

        presenter.onCreate()
    }

    public override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun checkGoogleLogin() {
        val opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient)
        if (opr.isDone) {
            Log.d("LauncherActivity", "Got cached sign-in")
            val result = opr.get()
            handleSignInResult(result)
        } else {
            Log.d("LauncherActivity", "Not cached sign-in")
            opr.setResultCallback { googleSignInResult ->
                handleSignInResult(googleSignInResult)
            }
        }
    }

    override fun startLoginActivity() {
        startActivity(LoginActivity.newIntent(this))
    }

    override fun startHomeActivity() {
        startActivity(HomeActivity.newIntent(this))
    }

    override fun reloadLoggedInUser() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Constants.YOUTUBE_SCOPE))
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.w("LauncherActivity", "onConnectionFailed:" + connectionResult)
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d("LauncherActivity", "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            val account = result.signInAccount
            this.account = account!!.account

            startHomeActivityWithGoogleAccount()
        } else {
            account = null
        }
    }

    fun startHomeActivityWithGoogleAccount() {
        val intent = HomeActivity.newIntent(this, account!!)
        startActivity(intent)
    }
}
