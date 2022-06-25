package com.seo.app.seostudio.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.RoundingMode
import java.text.DecimalFormat

class utils {
    companion object {
        //real ad
        var appOpenAdID = "ca-app-pub-3534006675523302/8012824951"

        //test ad
        var appOpenAdID_debug = "ca-app-pub-3940256099942544/3419835294"
        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) view = View(activity)
            imm.hideSoftInputFromWindow(view.windowToken, 0)

        }

        fun isconnected(c: Context): Boolean {
            val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting
        }

        fun roundOffDecimal(number: Double): String {
            val df = DecimalFormat("#.###")
            df.roundingMode = RoundingMode.CEILING
            Log.i("datadat", "$number   ----> ${df.format(number).toDouble().toString()}")
            var num = df.format(number).toDouble().toString()
            return if (num == "0.0" || num == "0.00" || num == "0.000" || num == "0" || num == "") {
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
    }
}