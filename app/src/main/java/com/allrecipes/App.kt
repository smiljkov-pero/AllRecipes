package com.allrecipes

import android.app.Application
import android.content.Context
import android.os.Build
import android.support.multidex.MultiDex
import android.widget.ImageView
import com.allrecipes.di.*
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.ui.launcher.LauncherView
import com.allrecipes.ui.videodetails.views.VideoDetailsView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.squareup.picasso.Picasso

class App : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this, BuildConfig.AD_MOB_ID)
        DebugUtils.initDebugTools(this)

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
