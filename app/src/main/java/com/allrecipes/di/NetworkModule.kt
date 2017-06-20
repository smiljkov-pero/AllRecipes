package com.allrecipes.di

import com.allrecipes.managers.GoogleYoutubeApiManager

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule(private val mBaseUrl: String) {

    @Singleton
    @Provides
    internal fun provideRetrofit(): Retrofit {
        val httpClient: OkHttpClient
        val builder: OkHttpClient.Builder
        builder = OkHttpClient.Builder()

        httpClient = builder.build()

        return Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    internal fun provideApi(retrofit: Retrofit): NetworkApi {
        return retrofit.create(NetworkApi::class.java)
    }

    @Provides
    @Singleton
    internal fun provideGoogleYoutubeApiManager(networkApi: NetworkApi): GoogleYoutubeApiManager {
        return GoogleYoutubeApiManager(networkApi)
    }
}
