package com.allrecipes.di

import android.content.Context
import com.allrecipes.di.managers.FirebaseDatabaseManager

import com.allrecipes.util.AppPreferences

import javax.inject.Singleton

import dagger.Component

/**
 * Created by Vladimir on 11/14/2016.
 */
@Singleton
@Component(modules = arrayOf(NetworkModule::class, AppModule::class))
interface AppComponent {

    val networkApi: NetworkApi

    fun applicationContext(): Context

    fun appPreferences(): AppPreferences

    fun firebaseDatabaseManager(): FirebaseDatabaseManager

    operator fun plus(homeScreenModule: HomeScreenModule): HomeScreenComponent
}
