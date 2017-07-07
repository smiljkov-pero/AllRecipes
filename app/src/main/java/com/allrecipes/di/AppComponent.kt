package com.allrecipes.di

import android.content.Context
import com.allrecipes.di.managers.FirebaseDatabaseManager
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetworkModule::class, AppModule::class))
interface AppComponent {

    fun applicationContext(): Context

    fun firebaseDatabaseManager(): FirebaseDatabaseManager

    operator fun plus(homeScreenModule: HomeScreenModule): HomeScreenComponent

    operator fun plus(videoDetailsScreenModule: VideoDetailsScreenModule): VideoDetailsScreenComponent
}
