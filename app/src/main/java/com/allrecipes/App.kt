package com.allrecipes

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.widget.ImageView

import com.allrecipes.di.AppComponent
import com.allrecipes.di.AppModule
import com.allrecipes.di.DaggerAppComponent
import com.allrecipes.di.HomeScreenComponent
import com.allrecipes.di.HomeScreenModule
import com.allrecipes.di.LauncherScreenComponent
import com.allrecipes.di.LauncherScreenModule
import com.allrecipes.di.NetworkApi
import com.allrecipes.di.NetworkModule
import com.allrecipes.di.VideoDetailsScreenComponent
import com.allrecipes.di.VideoDetailsScreenModule
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.ui.launcher.LauncherActivity
import com.allrecipes.ui.launcher.LauncherView
import com.allrecipes.ui.videodetails.views.VideoDetailsView
import com.squareup.picasso.Picasso

class App : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .networkModule(NetworkModule(NetworkApi.BASE_URL))
            .appModule(AppModule(applicationContext)).build()
    }

    fun getAppComponent(): AppComponent {
        return appComponent
    }

    fun createHomeScreenComponent(homeScreenView: HomeScreenView): HomeScreenComponent {
        return appComponent.plus(HomeScreenModule(homeScreenView))
    }

    fun createVideoDetailsScreenComponent(
        homeScreenView: VideoDetailsView
    ): VideoDetailsScreenComponent {
        return appComponent.plus(VideoDetailsScreenModule(homeScreenView))
    }

    fun createLauncherScreenComponent(launcherView: LauncherView): LauncherScreenComponent {
        return appComponent.plus(LauncherScreenModule(launcherView))
    }

    companion object {
        internal lateinit var appComponent: AppComponent
    }

    inline fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).placeholder(R.drawable.ic_app_launcher).into(this)
    }
}
