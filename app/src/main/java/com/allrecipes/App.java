package com.allrecipes;

import android.app.Application;

import com.allrecipes.di.AppComponent;
import com.allrecipes.di.AppModule;
import com.allrecipes.di.DaggerAppComponent;
import com.allrecipes.di.HomeScreenComponent;
import com.allrecipes.di.HomeScreenModule;
import com.allrecipes.di.NetworkApi;
import com.allrecipes.di.NetworkModule;
import com.allrecipes.ui.views.HomeScreenView;


/**
 * Created by Vladimir on 11/14/2016.
 */

public class App extends Application {
    static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
            .networkModule(new NetworkModule(NetworkApi.Companion.getBASE_URL()))
            .appModule(new AppModule(getApplicationContext())).build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public HomeScreenComponent createHomeScreenComponent(HomeScreenView homeScreenView) {
        return appComponent.plus(new HomeScreenModule(homeScreenView));
    }
}
