package com.allrecipes.di

import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.presenters.VideoDetailsScreenPresenter
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.ui.videodetails.views.VideoDetailsView

import java.lang.ref.WeakReference

import dagger.Module
import dagger.Provides

@Module
class VideoDetailsScreenModule(view: VideoDetailsView) {
    private val view: WeakReference<VideoDetailsView> = WeakReference(view)

    @Provides
    fun providesVideoDetailsScreenPresenter(
        googleYoutubeApiManager: GoogleYoutubeApiManager,
        remoteConfigManager: RemoteConfigManager
    ): VideoDetailsScreenPresenter {
        return VideoDetailsScreenPresenter(view.get(), googleYoutubeApiManager, remoteConfigManager)
    }
}
