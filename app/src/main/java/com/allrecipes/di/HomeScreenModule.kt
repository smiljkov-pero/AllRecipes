package com.allrecipes.di

import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.LocalStorageManagerInterface
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
        localStorageManager: LocalStorageManagerInterface
    ): HomeScreenPresenter {
        return HomeScreenPresenter(view.get(), googleYoutubeApiManager, localStorageManager)
    }
}
