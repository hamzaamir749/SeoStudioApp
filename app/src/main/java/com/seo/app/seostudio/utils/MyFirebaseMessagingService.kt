package com.seo.app.seostudio.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.seo.app.seostudio.R

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        getFirebaseMessage(
            message.notification?.title.toString(),
            message.notification?.body.toString(),
            message.notification?.imageUrl.toString()
        )
    }

    private fun getFirebaseMessage(title: String, message: String, url: String) {
        val resultIntent = Intent(Intent.ACTION_VIEW)
        resultIntent.data = Uri.parse(url)
        val pendingResultIntent =
            PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(this, "Keyword Planner")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingResultIntent)
        notificationBuilder.setVibrate(longArrayOf(500, 500, 500))
        val notificationCompat = NotificationManagerCompat.from(this)
        notificationCompat.notify(101, notificationBuilder.build())
        playDefaultSound(this)
    }


}

private fun playDefaultSound(context: Context) {
    try {
        val defaultSoundUris = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(context, defaultSoundUris)
        r.play()
    } catch (e: Exception) {
    }
}
