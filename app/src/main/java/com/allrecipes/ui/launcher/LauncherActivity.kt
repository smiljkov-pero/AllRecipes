package com.allrecipes.ui.launcher

import android.os.Bundle
import android.widget.ImageView

import com.allrecipes.R
import com.allrecipes.presenters.LauncherScreenPresenter
import com.allrecipes.ui.BaseActivity
import com.allrecipes.ui.LoginActivity

import javax.inject.Inject

class LauncherActivity : BaseActivity(), LauncherView {

    @Inject
    lateinit var presenter: LauncherScreenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        getApp().createLauncherScreenComponent(this).inject(this)

        presenter.onCreate()
    }

    override fun startLoginActivity() {
        startActivity(LoginActivity.newIntent(this))
    }
}
