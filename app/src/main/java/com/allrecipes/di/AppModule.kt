package com.allrecipes.di

import android.content.Context
import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.managers.GoogleYoutubeApiManager
import com.allrecipes.managers.LocalStorageManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
        databaseReference: DatabaseReference
    ): FirebaseDatabaseManager {
        return FirebaseDatabaseManager(databaseReference)
    }

    @Provides
    @Singleton
    internal fun provideGoogleYoutubeApiManager(networkApi: NetworkApi): GoogleYoutubeApiManager {
        return GoogleYoutubeApiManager(networkApi)
    }
}
