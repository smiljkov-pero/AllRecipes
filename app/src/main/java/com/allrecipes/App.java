package com.allrecipes;

import android.app.Application;

import com.allrecipes.di.AppComponent;
import com.projectsetup.vlad.projectsetup.di.AppComponent;
import com.projectsetup.vlad.projectsetup.di.AppModule;
import com.projectsetup.vlad.projectsetup.di.DaggerAppComponent;
import com.projectsetup.vlad.projectsetup.di.NetworkApi;
import com.projectsetup.vlad.projectsetup.di.NetworkModule;

/**
 * Created by Vladimir on 11/14/2016.
 */

public class App extends Application {
    static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().networkModule(new NetworkModule(NetworkApi.BASE_URL)).appModule(new AppModule(getApplicationContext())).build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

}
