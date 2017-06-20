package com.allrecipes.di

import com.allrecipes.ui.HomeActivity

import dagger.Subcomponent

@Subcomponent(modules = arrayOf(HomeScreenModule::class))
interface HomeScreenComponent {

    fun inject(activity: HomeActivity)
}
