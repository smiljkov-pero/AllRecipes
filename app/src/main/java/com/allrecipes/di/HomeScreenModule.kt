package com.allrecipes.di

import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.ui.home.views.HomeScreenView

import java.lang.ref.WeakReference

import dagger.Module
import dagger.Provides

@Module
class HomeScreenModule(view: HomeScreenView) {
    private val view: WeakReference<HomeScreenView> = WeakReference(view)

    @Provides
    fun providesHomeScreenPresenter(
        googleYoutubeApiManager: GoogleYoutubeApiManager,
        localStorageManager: LocalStorageManagerInterface,
        firebaseDatabaseManager: FirebaseDatabaseManager,
        remoteConfigManager: RemoteConfigManager
    ): HomeScreenPresenter {
        return HomeScreenPresenter(
            view,
            googleYoutubeApiManager,
            localStorageManager,
            firebaseDatabaseManager,
            remoteConfigManager
        )
    }
}
