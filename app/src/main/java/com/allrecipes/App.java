package com.allrecipes;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.allrecipes.di.AppComponent;
import com.allrecipes.di.AppModule;
import com.allrecipes.di.DaggerAppComponent;
import com.allrecipes.di.HomeScreenComponent;
import com.allrecipes.di.HomeScreenModule;
import com.allrecipes.di.NetworkApi;
import com.allrecipes.di.NetworkModule;
import com.allrecipes.di.VideoDetailsScreenComponent;
import com.allrecipes.di.VideoDetailsScreenModule;
import com.allrecipes.ui.home.views.HomeScreenView;
import com.allrecipes.ui.videodetails.views.VideoDetailsView;

public class App extends Application {
    static AppComponent appComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

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

    public VideoDetailsScreenComponent createVideoDetailsScreenComponent(
        VideoDetailsView homeScreenView
    ) {
        return appComponent.plus(new VideoDetailsScreenModule(homeScreenView));
    }
}
