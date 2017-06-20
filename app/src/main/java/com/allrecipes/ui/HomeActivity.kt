package com.allrecipes.ui

import android.os.Bundle
import com.allrecipes.R
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.ui.views.HomeScreenView
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeScreenView {

    @Inject
    lateinit var presenter: HomeScreenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getApp().createHomeScreenComponent(this).inject(this)

        presenter.fetchYoutubeChannelVideos()
    }
}
