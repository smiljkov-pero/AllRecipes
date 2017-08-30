package com.allrecipes.di

import android.content.Context
import android.net.ConnectivityManager
import com.allrecipes.BuildConfig
import com.allrecipes.managers.*
import com.allrecipes.managers.remoteconfig.FirebaseConfig
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import com.allrecipes.network.RecipesNetworkInterceptor
import com.allrecipes.tracking.providers.firebase.FirebaseTracker
import com.allrecipes.tracking.providers.firebase.FirebaseTrackerImpl
import com.allrecipes.tracking.providers.firebase.UserPropertiesManager
import com.allrecipes.util.NetworkUtils
import com.google.firebase.analytics.FirebaseAnalytics
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

    @Provides
    @Singleton
    fun providesConnectivityManager(): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun providesFavoritesManager(localStorageManagerInterface: LocalStorageManagerInterface,
                                 context: Context): FavoritesManager {
        return FavoritesManager(localStorageManagerInterface, context)
    }

    @Singleton
    @Provides
    fun providesFirebaseTracker(context: Context): FirebaseTracker {
        return FirebaseTrackerImpl(FirebaseAnalytics.getInstance(context))
    }

    @Singleton
    @Provides
    fun providesNetworkUtils(context: Context): NetworkUtils {
        return NetworkUtils(context)
    }

    @Singleton
    @Provides
    fun providesUserPropertiesManager(firebaseTracker: FirebaseTracker): UserPropertiesManager {
        return UserPropertiesManager(firebaseTracker)
    }

    @Singleton
    @Provides
    fun providesNetworkInterceptor(firebaseTracker: FirebaseTracker): RecipesNetworkInterceptor {
        return RecipesNetworkInterceptor(firebaseTracker)
    }
}
