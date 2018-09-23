package com.allrecipes.di

import com.allrecipes.ui.videodetails.activity.VideoActivity
import dagger.Subcomponent

@Subcomponent(modules = [(VideoDetailsScreenModule::class)])
interface VideoDetailsScreenComponent {

    fun inject(activity: VideoActivity)
}
