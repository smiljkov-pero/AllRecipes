package com.allrecipes.di

import android.content.Context
import com.allrecipes.BuildConfig
import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.LocalStorageManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.managers.remoteconfig.FirebaseConfig
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun providesLocalStorageManager(): LocalStorageManagerInterface {
        return LocalStorageManager(context)
    }

    @Provides
    @Singleton
    fun providesFirebaseDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }

    @Provides
    @Singleton
    fun providesFirebaseDatabaseManager(
        databaseReference: DatabaseReference,
        localStorageManagerInterface: LocalStorageManagerInterface
    ): FirebaseDatabaseManager {
        return FirebaseDatabaseManager(databaseReference, localStorageManagerInterface)
    }

    @Provides
    @Singleton
    internal fun provideGoogleYoutubeApiManager(networkApi: NetworkApi): GoogleYoutubeApiManager {
        return GoogleYoutubeApiManager(networkApi)
    }

    @Provides
    @Singleton
    fun providesRemoteConfigManager(firebaseRemoteConfig: FirebaseRemoteConfig): RemoteConfigManager {
        return FirebaseConfig(firebaseRemoteConfig)
    }

    @Provides
    @Singleton
    fun providesFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setConfigSettings(configSettings)

        return firebaseRemoteConfig
    }
}
