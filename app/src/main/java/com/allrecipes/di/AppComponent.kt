package com.allrecipes.di

import android.content.Context
import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.util.AppPreferences
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Vladimir on 11/14/2016.
 */
@Singleton
@Component(modules = arrayOf(NetworkModule::class, AppModule::class))
interface AppComponent {

    fun applicationContext(): Context

    fun appPreferences(): AppPreferences

    fun firebaseDatabaseManager(): FirebaseDatabaseManager

    operator fun plus(homeScreenModule: HomeScreenModule): HomeScreenComponent
}
