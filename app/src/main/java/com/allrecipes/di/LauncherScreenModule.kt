package com.allrecipes.di

import com.allrecipes.managers.FirebaseDatabaseManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.presenters.LauncherScreenPresenter
import com.allrecipes.ui.launcher.LauncherView
import com.allrecipes.util.NetworkUtils
import dagger.Module
import dagger.Provides
import java.lang.ref.WeakReference

@Module
class LauncherScreenModule(view: LauncherView) {
    private val view: WeakReference<LauncherView> = WeakReference(view)

    @Provides
    fun providesVideoDetailsScreenPresenter(
        remoteConfigManager: RemoteConfigManager,
        firebaseDatabaseManager: FirebaseDatabaseManager,
        localStorageManagerInterface: LocalStorageManagerInterface,
        networkUtils: NetworkUtils
    ): LauncherScreenPresenter {
        return LauncherScreenPresenter(
            view,
            remoteConfigManager,
            firebaseDatabaseManager,
            localStorageManagerInterface,
            networkUtils
        )
    }
}