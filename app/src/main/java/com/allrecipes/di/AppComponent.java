package com.allrecipes.di;

import android.content.Context;

import com.projectsetup.vlad.projectsetup.util.AppPreferences;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Vladimir on 11/14/2016.
 */
@Singleton
@Component(modules = {NetworkModule.class, AppModule.class})
public interface AppComponent {

    NetworkApi getNetworkApi();

    Context applicationContext();

    AppPreferences appPreferences();
}
