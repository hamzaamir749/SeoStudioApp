package com.seo.app.seostudio.activties

import android.app.Application
import com.seo.app.seostudio.ads.AppOpenManager
import com.seo.app.seostudio.billing.billingUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    private var appOpenManager: AppOpenManager? = null
    override fun onCreate() {
        super.onCreate()
        billingUtil(applicationContext)
        appOpenManager = AppOpenManager(this)
    }
}