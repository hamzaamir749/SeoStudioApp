package com.seo.app.seostudio.activties


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.applovin.sdk.AppLovinSdk
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.seo.app.seostudio.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Fcm topic
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic(applicationContext.packageName)
        AppLovinSdk.getInstance(this).mediationProvider = "max"
        AppLovinSdk.getInstance(this).initializeSdk()

        val name = "Suggestion Notification"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(getString(R.string.notification_channel_id), name, importance)
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.setShowBadge(true)
        channel.lightColor = Color.RED
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }

    companion object {

        var intertitialShown = false
    }
}