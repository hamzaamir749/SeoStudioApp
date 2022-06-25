package com.seo.app.seostudio

import androidx.multidex.MultiDexApplication
import com.seo.app.seostudio.ads.OpenApp
import com.seo.app.seostudio.billing.BillingUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SeoStudioApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        BillingUtil(this)
        OpenApp(this)
    }
}