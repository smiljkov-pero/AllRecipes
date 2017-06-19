package com.allrecipes.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Vladimir on 11/14/2016.
 */
@Module
public class NetworkModule {

    private String mBaseUrl;

    @Singleton
    @Provides
    Retrofit provideRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .build();
        return retrofit;
    }

    public NetworkModule(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }

    @Provides
    @Singleton
    NetworkApi provideApi(Retrofit retrofit) {
        return retrofit.create(NetworkApi.class);
    }
}
