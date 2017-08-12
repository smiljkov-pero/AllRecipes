package com.allrecipes.di

import com.allrecipes.managers.FirebaseDatabaseManager
import com.allrecipes.managers.FavoritesManager
import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.tracking.providers.firebase.FirebaseTracker
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
        remoteConfigManager: RemoteConfigManager,
        firebaseTracker: FirebaseTracker,
        favoritesManager: FavoritesManager

    ): HomeScreenPresenter {
        return HomeScreenPresenter(
            view,
            googleYoutubeApiManager,
            localStorageManager,
            firebaseDatabaseManager,
            remoteConfigManager,
            firebaseTracker,
            favoritesManager
        )
    }
}
