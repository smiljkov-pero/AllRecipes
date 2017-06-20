package com.allrecipes.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


import com.allrecipes.util.AppPreferences

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * Created by vladi on 11/12/2016.
 */
@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    internal fun providePreferenceManager(preferences: SharedPreferences): AppPreferences {
        return com.allrecipes.util.PreferenceManager(preferences)
    }
}
