package de.foodora.android.managers.updater


import java.net.URL

class AppUpdate(val version: String, val versionCode: Int, val releaseNotes: String, val url: URL) {
    var updateMessage: String? = null
    var hasNewVersion = false
    var isForceUpdate = false
}
