package com.allrecipes

import android.content.Context

import com.facebook.stetho.Stetho

class DebugUtils {
    companion object {

        fun initDebugTools(appContext: Context) {
            Stetho.initializeWithDefaults(appContext)
        }
    }
}
