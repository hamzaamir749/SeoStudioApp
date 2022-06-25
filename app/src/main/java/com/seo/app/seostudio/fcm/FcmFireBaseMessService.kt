package com.seo.app.seostudio.fcm;

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.seo.app.seostudio.BuildConfig
import java.util.concurrent.atomic.AtomicInteger


/**
 *
 * Created by SSS on 11/30/2017.
 */
open class FcmFireBaseMessService : FirebaseMessagingService() {

    companion object {

        val ICON_KEY = "icon"
        val APP_TITLE_KEY = "title"
        val SHORT_DESC_KEY = "short_desc"
        val LONG_DESC_KEY = "long_desc"
        val APP_FEATURE_KEY = "feature"
        val APP_URL_KEY = "app_url"

        const val IS_PREMIUM = "is_premium"

        private val seed = AtomicInteger()

        fun getNextInt(): Int {
            return seed.incrementAndGet()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            val iconURL = data[ICON_KEY]
            val title = data[APP_TITLE_KEY]
            val shortDesc = data[SHORT_DESC_KEY]
            val longDesc = data[LONG_DESC_KEY]
            val feature = data[APP_FEATURE_KEY]
            val appURL = data[APP_URL_KEY]
            val notificationID = getNextInt()

            if (iconURL != null && title != null && shortDesc != null && feature != null && appURL != null) {
                val standard = "https://play.google.com/store/apps/details?id="

                try {
                    val id = appURL.substring(standard.length)
                    if (BuildConfig.DEBUG) Log.e("package sent ", id)

                    if (!isAppInstalled(id, this) && !TinnyDB.getInstance(this).getBoolean(
                            IS_PREMIUM
                        )
                    ) {
                    }


                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) Log.e("FcmFireBase", "package not valid")
                }
            }
        }

        if (BuildConfig.DEBUG) remoteMessage.from?.let { Log.e("From: ", it) }
        if (remoteMessage.notification != null) {
            if (BuildConfig.DEBUG) remoteMessage.notification!!.body?.let { Log.e("Message  Body:", it) }
        }

    }


    private fun isAppInstalled(uri: String, context: Context): Boolean {
        val pm = context.packageManager
        try {
            val applicationInfo = pm.getApplicationInfo(uri, 0)
            //            packageInfo
            return applicationInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }
}