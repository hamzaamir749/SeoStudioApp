package com.seo.app.seostudio.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.seo.app.seostudio.R
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat


fun <T> Context.openActivity(it: Class<T>) {
    val intent = Intent(this, it)
    startActivity(intent)
}

fun Context.shareApp() {
    try {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "\n" +
                    "Let me recommend you this application for keyword suggestions plan\n" +
                    "\n https://play.google.com/store/apps/details?id=" + this.applicationContext.packageName
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    } catch (exc: java.lang.Exception) {
        exc.printStackTrace()
    }
}


fun Context.openBrowser(link: String) {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )
        )
    } catch (e: Exception) {
    }
}


fun Context.rateUs() {
    val uri = Uri.parse("market://details?id=" + this.applicationContext.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    // To count with Play market backstack, After pressing back button,
    // to taken back to our application, we need to add following flags to intent.
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    )
    try {
        startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + this.applicationContext.packageName)
            )
        )
    }
}


fun Context.feedbackUs(mail: String) {
    val uriText = mail +
            "?subject=" + Uri.encode("keywords Suggestions Tool app")
    val uri = Uri.parse(uriText)
    val sendIntent = Intent(Intent.ACTION_SENDTO)
    sendIntent.data = uri
    try {
        startActivity(Intent.createChooser(sendIntent, "Send email"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Context.getBaseUrl(): String {
    return this.getString(R.string.KEYWORD_SEARCH_BASE_URL)
}

fun parseError(response: Response<*>): String {
    var message = String()
    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
    try {
        message = jsonObj.getString("message")
    } catch (e: IOException) {
        return message
    }
    return message
}


fun roundOffDecimal(number: Double): String {
    val df = DecimalFormat("#.###")
    df.roundingMode = RoundingMode.CEILING
    Log.i("datadat", "$number   ----> ${df.format(number).toDouble().toString()}")
    var num = df.format(number).toDouble().toString()
    return if (num == "0.0" || num == "0.00" || num == "0.000" || num == "0") {
        "0.001"
    } else {
        num
    }
}

fun competitionValue(value: Double): String {
    if (value < 0.01) {
        return "Very Low"
    } else if (value in 0.01..0.20) {
        return "Low"
    } else if (value in 0.21..0.40) {
        return "Medium"
    } else if (value in 0.41..0.60) {
        return "high"
    } else {
        return "very high"
    }

}


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun inAppMessagingInitialization(
    context: Context?,
    setSuppressed: Boolean,
    eventName: String
) { //setSuppressed false means start getting message
    FirebaseInAppMessaging.getInstance()
        .setMessagesSuppressed(setSuppressed) //true==Stop inAppMessaging
    if (eventName != "") {
        FirebaseAnalytics.getInstance(context!!).logEvent(
            eventName,
            null
        ) //To Show InAppMessage in MainActivity. Need to add this event name in Web console campaign
        FirebaseInAppMessaging.getInstance().triggerEvent(eventName)
    }
}


fun Context.createBannerAd(frameLayout: FrameLayout) {
    var nativeAd: MaxAd? = null
    val nativeAdLoader: MaxNativeAdLoader =
        MaxNativeAdLoader(this.resources.getString(R.string.banner_ad_unit_id), this)
    nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {

        override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
            if (nativeAd != null) {
                nativeAdLoader.destroy(nativeAd)
            }

            nativeAd = ad

            frameLayout.removeAllViews()
            frameLayout.addView(nativeAdView)
        }

        override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
            nativeAdLoader.loadAd()
        }

        override fun onNativeAdClicked(ad: MaxAd) {
        }
    })
    nativeAdLoader.loadAd()
}
