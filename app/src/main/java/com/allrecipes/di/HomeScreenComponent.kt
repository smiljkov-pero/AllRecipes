package com.allrecipes.di

import com.allrecipes.ui.home.activity.HomeActivity

import dagger.Subcomponent

@Subcomponent(modules = arrayOf(HomeScreenModule::class))
interface HomeScreenComponent {

    fun inject(activity: HomeActivity)
}
