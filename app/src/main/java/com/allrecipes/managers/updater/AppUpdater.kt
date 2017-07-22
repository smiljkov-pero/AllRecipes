package de.foodora.android.managers.updater

import io.reactivex.Observable

interface AppUpdater {
    fun checkForNewAppVersion(): Observable<AppUpdate>
}
