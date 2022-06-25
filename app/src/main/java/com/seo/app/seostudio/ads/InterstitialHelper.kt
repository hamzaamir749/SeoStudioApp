package com.seo.app.seostudio.ads

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError

import com.applovin.mediation.ads.MaxInterstitialAd
import com.seo.app.seostudio.R
import com.seo.app.seostudio.activties.MainActivity
import com.seo.app.seostudio.billing.billingUtil


import java.util.*
import java.util.concurrent.TimeUnit

object InterstitialHelper {
    private val TAG = "InterstitialADTag"
    private var dialogStartTime = 0L
    private var interstitialTimeElapsed = 0L
    private var mInterstitialAd: MaxInterstitialAd? = null

    private var timer: CountDownTimer? = null

    var isActivityOnPause = false
    var isAdLoading = false


    //Show Ad after 20 sec
    fun loadAndShowInterstitial(
        activity: Activity,
        isShownDialog: Boolean,
        dismissCallback: () -> Unit
    ) {

        if (!billingUtil.isPremium) {
            if (isNetworkAvailable(activity) && timeDifference(interstitialTimeElapsed) > 35 && !isAdLoading) {
                Log.i(TAG, "loadAndShowInterstitial: function start ")
                //LoadingDialog.showLoadingDialog(activity)
                MainActivity.intertitialShown = true
                timer = object : CountDownTimer(10000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (millisUntilFinished >= 1000L && mInterstitialAd != null) {
                            if (!isActivityOnPause) {
                                mInterstitialAd?.showAd()
                                timer?.cancel()
                            }
                            //    LoadingDialog.hideLoadingDialog()
                        }
                        Log.i(TAG, "onTick: ")
                    }

                    override fun onFinish() {
                        //   LoadingDialog.hideLoadingDialog()
                        dismissCallback()
                    }
                }
                if (mInterstitialAd == null) {
                    Log.i(TAG, "loadAndShowInterstitial: Ad Loading start ")
                    isAdLoading = true
                    mInterstitialAd =
                        MaxInterstitialAd(activity.getString(R.string.interstitial), activity)
                    mInterstitialAd!!.setListener(object : MaxAdListener {
                        override fun onAdLoaded(ad: MaxAd?) {

                        }

                        override fun onAdDisplayed(ad: MaxAd?) {

                        }

                        override fun onAdHidden(ad: MaxAd?) {
                            dismissCallback()
                            MainActivity.intertitialShown = false
                        }

                        override fun onAdClicked(ad: MaxAd?) {
                            TODO("Not yet implemented")
                        }

                        override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                            isAdLoading = false
                            Log.d(TAG, error!!.message)
                            mInterstitialAd = null
                            // LoadingDialog.hideLoadingDialog()
                            try {
                                timer?.cancel()
                            } catch (e: Exception) {
                            }
                            MainActivity.intertitialShown = false
                            interstitialTimeElapsed = Calendar.getInstance().timeInMillis
                            dismissCallback()
                        }

                        override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                            mInterstitialAd = null
                            interstitialTimeElapsed =
                                Calendar.getInstance().timeInMillis
                        }


                    })
                    mInterstitialAd!!.loadAd()

                }
                timer?.start()
            } else {
                MainActivity.intertitialShown = false
                Log.i(TAG, "loadAndShowInterstitial: function else part")
                dismissCallback()
            }
        } else {
            MainActivity.intertitialShown = false
            dismissCallback()
        }
    }


    // Show ad immediately its use for splash ads
    fun loadAndShowAd(context: Context, applovinId: String, dismissCallback: () -> Unit) {
        Log.i(TAG, "called")
        MainActivity.intertitialShown = true
        //  val adRequest: AdRequest = AdRequest.Builder().build()
        if (!billingUtil.isPremium) {
            if (isNetworkAvailable(context)) {
                //  LoadingDialog.showLoadingDialog(context)
                mInterstitialAd = MaxInterstitialAd(applovinId, context as Activity)
                mInterstitialAd!!.setListener(object : MaxAdListener {
                    override fun onAdLoaded(ad: MaxAd?) {
                        mInterstitialAd?.showAd()
                    }

                    override fun onAdDisplayed(ad: MaxAd?) {
                        dismissCallback()
                        MainActivity.intertitialShown = false
                    }

                    override fun onAdHidden(ad: MaxAd?) {
                        MainActivity.intertitialShown = false
                        dismissCallback()
                    }

                    override fun onAdClicked(ad: MaxAd?) {
                        TODO("Not yet implemented")
                    }

                    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                        //  LoadingDialog.hideLoadingDialog()
                        Log.i(TAG, error!!.message)
                        MainActivity.intertitialShown = false
                        dismissCallback()
                    }

                    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {

                    }


                })
                mInterstitialAd!!.loadAd()


            } else {
                dismissCallback()
            }
        } else {
            dismissCallback()
        }

    }

    private fun timeDifference(millis: Long): Int {
        val current = Calendar.getInstance().timeInMillis
        val elapsedTime = current - millis

        return TimeUnit.MILLISECONDS.toSeconds(elapsedTime).toInt()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}