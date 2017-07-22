package de.foodora.android.managers.updater

import android.text.TextUtils
import com.allrecipes.managers.remoteconfig.RemoteConfigManager
import de.foodora.android.managers.remoteconfig.ForceUpdate
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.net.URL
import java.util.concurrent.TimeUnit

class AppUpdatesManager @JvmOverloads constructor(
    private val appUpdater: AppUpdater,
    private val remoteConfigManager: RemoteConfigManager,
    private val refreshPeriodSeconds: Long = AppUpdatesManager.DEFAULT_REFRESH_PERIOD_IN_SECONDS
) {
    companion object {
        val DEFAULT_REFRESH_PERIOD_IN_SECONDS: Long = 3600
        val FORCE_UPDATE_KEY = "NEXTGEN_FORCE_UPDATE_MESSAGE"
        val UPDATE_MESSAGE_KEY = "NEXTGEN_UPDATE_MESSAGE"
        val FALLBACK_FORCE_UPDATE_MESSAGE
            = "An updated version is available, in order to proceed you must update to the latest version"
        val FALLBACK_NORMAL_UPDATE_MESSAGE
            = "A new version is available, to make use of the latest features please update now"
    }

    private val lastCheckedFromScreenAt = HashMap<String, Long>()
    private var forceUpdateAppVersion = 0
    private var forceAppUpdate: AppUpdate? = null

    fun checkForUpdates(currentAppVersionCode: Int, screen: String = "generic"): Observable<AppUpdate> {
        var updateObservable: Observable<AppUpdate> = Observable.just(noUpdate())

        if (shouldForceUpdate(currentAppVersionCode)) {
            updateObservable = Observable.just(forceAppUpdate)
        } else if (shouldCheckUpdate(screen)) {
            updateObservable = appUpdater.checkForNewAppVersion()
                .subscribeOn(Schedulers.io())
                .doOnNext { appUpdate ->
                    onVersionRetrieved(appUpdate, currentAppVersionCode)
                    updateLastCheckTime(screen)
                }
        }

        return updateObservable
    }

    private fun noUpdate() = AppUpdate("1", 0, "", URL("https://www.foodora.com"))

    private fun shouldForceUpdate(currentAppVersionCode: Int): Boolean {
        return forceUpdateAppVersion == currentAppVersionCode
    }

    private fun onVersionRetrieved(appUpdate: AppUpdate, appVersionCode: Int) {
        val googlePlayVersion = appUpdate.versionCode
        appUpdate.hasNewVersion = appVersionCode < googlePlayVersion
        if (appUpdate.hasNewVersion) {
            val forceUpdateConfig = remoteConfigManager.forceUpdate
            val forceUpdateVersion = findMatchingForceUpdate(appVersionCode, forceUpdateConfig)
            appUpdate.isForceUpdate = forceUpdateVersion != null
            if (appUpdate.isForceUpdate) {
                addLocalizedForceUpdateMessage(appVersionCode, appUpdate, forceUpdateVersion!!)
                forceAppUpdate = appUpdate
                forceUpdateAppVersion = appVersionCode
            } else {
                addLocalizedNormalUpdateMessage(appVersionCode, appUpdate)
            }
        }
    }

    private fun addLocalizedNormalUpdateMessage(appVersionCode: Int, appUpdate: AppUpdate) {
        /*appUpdate.updateMessage = localizer.getTranslation(UPDATE_MESSAGE_KEY + appVersionCode)
            ?: localizer.getTranslation(UPDATE_MESSAGE_KEY)
                ?: FALLBACK_NORMAL_UPDATE_MESSAGE*/
    }

    private fun addLocalizedForceUpdateMessage(
        appVersionCode: Int,
        appUpdate: AppUpdate,
        forceUpdateVersion: ForceUpdate.VersionRange
    ) {
        var updateMessage: String? = null
        /*if (forceUpdateVersion.translationKey != null) {
            updateMessage = localizer.getTranslation(forceUpdateVersion.translationKey!!)
        }
        updateMessage = updateMessage ?: localizer.getTranslation(FORCE_UPDATE_KEY + appVersionCode.toString())
            ?: localizer.getTranslation(FORCE_UPDATE_KEY)
*/
        if (updateMessage == null && !TextUtils.isEmpty(forceUpdateVersion.alternativeMessage)) {
            updateMessage = forceUpdateVersion.alternativeMessage
        }
        if (updateMessage == null) {
            updateMessage = FALLBACK_FORCE_UPDATE_MESSAGE
        }
        appUpdate.updateMessage = updateMessage
    }

    private fun findMatchingForceUpdate(appVersion: Int, forceUpdate: ForceUpdate): ForceUpdate.VersionRange? {
        val matchedForceUpdate: ForceUpdate.VersionRange? = forceUpdate.forceUpdateBuildNumbers.firstOrNull {
            appVersion in it.from..it.to
        }

        return matchedForceUpdate
    }

    private fun updateLastCheckTime(screen: String) {
        lastCheckedFromScreenAt[screen] = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
    }

    private fun shouldCheckUpdate(screen: String): Boolean {
        val lastCheckedAt = lastCheckedFromScreenAt[screen] ?: 0

        return lastCheckedAt < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - refreshPeriodSeconds
    }
}
