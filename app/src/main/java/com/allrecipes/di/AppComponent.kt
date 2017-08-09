package com.allrecipes.di

import android.content.Context
import com.allrecipes.managers.FirebaseDatabaseManager
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.tracking.providers.firebase.FirebaseTracker
import com.allrecipes.ui.LoginActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetworkModule::class, AppModule::class))
interface AppComponent {

    fun applicationContext(): Context

    fun firebaseDatabaseManager(): FirebaseDatabaseManager

    fun remoteConfigManager(): RemoteConfigManager

    fun firebaseTracker(): FirebaseTracker

    abstract fun inject(loginActivity: LoginActivity)

    operator fun plus(homeScreenModule: HomeScreenModule): HomeScreenComponent

    operator fun plus(launcherScreenModule: LauncherScreenModule): LauncherScreenComponent

    operator fun plus(videoDetailsScreenModule: VideoDetailsScreenModule): VideoDetailsScreenComponent
}
