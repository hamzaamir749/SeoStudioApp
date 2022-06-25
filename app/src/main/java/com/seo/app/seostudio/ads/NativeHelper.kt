package com.seo.app.seostudio.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.seo.app.seostudio.billing.billingUtil

class NativeHelper(private val context: Context) {
    var nativeView: MaxNativeAdView? = null
    fun loadAdsWithConfiguration(
        nativeContainer: ConstraintLayout,
        applovinContainer: FrameLayout,  /* NativeAdLayout fbContainer,*/
        adId: String?,
        config: Int
    ) {
        if (!billingUtil.isPremium) {
            if (config.toDouble() == 0.0) { //No Ads
                nativeContainer.visibility = View.GONE
            } else if (config.toDouble() == 1.0 || config.toDouble() == 3.0) { //1 = AdMob Only, 3 = AdMob -> Facebook
                showNative(
                    nativeContainer,
                    applovinContainer,  /*fbContainer,*/
                    adId,
                )
            }
        } else {
            nativeContainer.visibility = View.GONE
        }
    }

    private fun showNative(
        nativeContainer: ConstraintLayout,
        applovinContainer: FrameLayout,  /*NativeAdLayout fbContainer,*/
        adId: String?,
    ) {
        if (adMobNativeAd == null) {
            Log.i("SingleAdsStatus", "Null")

            val nativeAdLoader = MaxNativeAdLoader(adId, context)
            nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
                    Log.i("SingleAdsStatus", "Loaded")
                    if ((context as Activity).window.decorView.rootView.isShown) {

                        nativeContainer.visibility = View.VISIBLE
                        applovinContainer.visibility = View.VISIBLE
                        nativeView = nativeAdView
                        applovinContainer.addView(nativeView)
                        adMobNativeAd = null
                    }
                }

                override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.i("SingleAdsStatus", "Failed; code: " + error.message)
                    nativeContainer.visibility = View.GONE
                }

                override fun onNativeAdClicked(ad: MaxAd) {
                    // Optional click callback
                }
            })
            nativeAdLoader.loadAd()


        } else {
            Log.i("SingleAdsStatus", "not null")
            nativeContainer.visibility = View.VISIBLE
            applovinContainer.visibility = View.VISIBLE
            adMobNativeAd = null
        }
    }


    companion object {
        //---------------------------------------           MAX      ---------------------------------------------//
        var adMobNativeAd: MaxNativeAdLoader? = null

    }
}