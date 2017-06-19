package com.allrecipes;

import android.app.Application;

import com.allrecipes.di.AppComponent;
import com.allrecipes.di.AppModule;
import com.allrecipes.di.DaggerAppComponent;
import com.allrecipes.di.NetworkApi;
import com.allrecipes.di.NetworkModule;


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
