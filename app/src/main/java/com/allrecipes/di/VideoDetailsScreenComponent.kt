package com.allrecipes.di

import com.allrecipes.ui.home.activity.HomeActivity
import com.allrecipes.ui.videodetails.activity.VideoActivity

import dagger.Subcomponent

@Subcomponent(modules = arrayOf(VideoDetailsScreenModule::class))
interface VideoDetailsScreenComponent {

    fun inject(activity: VideoActivity)
}
