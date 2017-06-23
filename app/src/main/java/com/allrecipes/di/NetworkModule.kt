package com.allrecipes.di

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class NetworkModule(val mBaseUrl: String) {

    @Singleton
    @Provides
    internal fun provideRetrofit(context: Context): Retrofit {
        val httpClient: OkHttpClient
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()

        val CACHE_SIZE_BYTES = 1024 * 1024 * 2

        builder.cache(
            Cache(context.cacheDir, CACHE_SIZE_BYTES.toLong())
        )

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
}
