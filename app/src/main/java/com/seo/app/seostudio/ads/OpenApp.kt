package com.seo.app.seostudio.ads


import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.seo.app.seostudio.R
import com.seo.app.seostudio.SeoStudioApp
import com.seo.app.seostudio.billing.BillingUtil
import com.seo.app.seostudio.ui.activities.SplashScreenActivity
import org.jetbrains.annotations.NotNull

class OpenApp(private val globalClass: SeoStudioApp) :
    Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    private val log = "AppOpenManager"

    private var adVisible = false
    private var appOpenAd: AppOpenAd? = null

    private var currentActivity: Activity? = null
    private var myApplication: SeoStudioApp? = globalClass
    private var fullScreenContentCallback: FullScreenContentCallback? = null
    private var appOpenLoading: AppOpenLoading? = null

    companion object {
        var COUNTER = 1
        var isInterstitialShown = false
        var isAdShowing: Boolean = false
        var isShowingAd = false
    }


    init {
        this.myApplication?.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /**
     * Request an ad
     */
    fun fetchAd() {
        Log.d(log, "fetchAd " + isAdAvailable())
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return
        }

        /*
          Called when an app open ad has failed to load.
          @param loadAdError the error.
         */
        // Handle the error.
        val loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */

            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                Log.d(log, "loaded")
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
                Log.d(log, "error")
            }
        }
        val request: AdRequest = getAdRequest()
        AppOpenAd.load(
            myApplication!!, globalClass.getString(R.string.admob_app_open_id), request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    private fun showAdIfAvailable() {
        COUNTER++
        if (!isShowingAd && isAdAvailable() && !isInterstitialShown) {
            Log.d(log, "Will show ad.")
            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.

                    appOpenAd = null
                    isShowingAd = false
                    adVisible = false
                    fetchAd()
                    dismissLoading(1)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {

                    dismissLoading(3)
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }

            adVisible = true
            showLoading()
            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            currentActivity?.let { appOpenAd?.show(it) }

        } else {
            Log.d(log, "Can not show ad.")
            dismissLoading(4)
            fetchAd()
        }
    }

    private fun showLoading() {
        if (!currentActivity?.isFinishing!!) {
            appOpenLoading = AppOpenLoading(currentActivity!!)
            appOpenLoading?.show()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {
        //App in background
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        if (!BillingUtil.isPremium) {
            currentActivity?.let {
                if (it !is SplashScreenActivity) {
                    showAdIfAvailable()
                }
            }
        }
    }

    private fun dismissLoading(from: Int) {
        Log.d("dismiss55", "11$from")
        if (!adVisible)
            appOpenLoading?.dismiss()
    }


    @NotNull
    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }
}