package com.allrecipes.di

import android.content.Context
import android.net.ConnectivityManager
import com.allrecipes.managers.LocalStorageManagerInterface
import com.allrecipes.model.YoutubeId
import com.allrecipes.network.HttpClientBuilder
import com.allrecipes.network.RequestInterceptor
import com.allrecipes.network.YoutubeItemIdDeserializer
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule(val mBaseUrl: String) {

    @Provides
    @Singleton
    fun providesOkHttpClient(
        context: Context,
        localStorageManager: LocalStorageManagerInterface,
        requestInterceptor: RequestInterceptor
    ): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val CACHE_SIZE_BYTES = 1024 * 1024 * 2

        val client = HttpClientBuilder(localStorageManager)
            .addInterceptor(interceptor)
            .addInterceptor(requestInterceptor)
            .setCache(Cache(context.cacheDir, CACHE_SIZE_BYTES.toLong()))
            .build()

        return client
    }

    @Singleton
    @Provides
    internal fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        val gsonBuilder = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssz")
            .registerTypeAdapter(YoutubeId::class.java, YoutubeItemIdDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
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
    fun providesRequestInterceptor(connectivityManager: ConnectivityManager): RequestInterceptor {
        return RequestInterceptor(connectivityManager)
    }
}
