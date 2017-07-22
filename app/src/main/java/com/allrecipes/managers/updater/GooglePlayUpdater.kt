package de.foodora.android.managers.updater

import android.content.Context
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.objects.Update
import io.reactivex.Observable

class GooglePlayAppUpdater(private val context: Context) : AppUpdater {

    override fun checkForNewAppVersion(): Observable<AppUpdate> {
        return Observable.create { e ->
            val appUpdaterUtils = AppUpdaterUtils(context)
                .withListener(object : AppUpdaterUtils.UpdateListener {
                    override fun onSuccess(update: Update, aBoolean: Boolean?) {
                        val versionCode = update.latestVersionCode ?: 1
                        val appUpdate = AppUpdate(
                            update.latestVersion,
                            versionCode,
                            update.releaseNotes,
                            update.urlToDownload
                        )
                        if (!e.isDisposed) {
                            e.onNext(appUpdate)
                        }
                    }

                    override fun onFailed(ignore: AppUpdaterError) {
                        if (!e.isDisposed) {
                            e.onError(RuntimeException("Failed to retrieve app update info"))
                        }
                    }
                })
            appUpdaterUtils.start()
        }
    }
}
