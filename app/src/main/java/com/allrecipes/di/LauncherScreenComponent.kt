package com.allrecipes.di

import com.allrecipes.ui.launcher.LauncherActivity
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(LauncherScreenModule::class))
interface LauncherScreenComponent {
    fun inject(activity: LauncherActivity)
}