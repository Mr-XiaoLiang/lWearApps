package com.lollipop.filebrowser

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import be.ppareit.swiftp.App
import be.ppareit.swiftp.FsNotification

class LApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        App.onCreate(this)
        FsNotification.notificationBuilder = FtpServiceNotification
    }

    private object FtpServiceNotification : FsNotification.NotificationBuilder {

        private const val NOTIFICATION_CHANNEL_ID = "com.lollipop.filebrowser.ftp_service"

        override fun buildNotification(context: Context): Notification {
            val manager = context.getSystemService(NotificationManager::class.java)

            val name = context.getString(R.string.label_ftp_service_channel)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = context.getString(R.string.desc_ftp_service_channel)
            manager.createNotificationChannel(channel)

            return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
                setSmallIcon(R.drawable.baseline_wifi_tethering_24)
                setContentTitle(context.getString(R.string.label_notification_ftp_service))
                setWhen(System.currentTimeMillis())
                setOngoing(true)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setCategory(NotificationCompat.CATEGORY_SERVICE)
                setPriority(NotificationManager.IMPORTANCE_MIN)
                setShowWhen(false)
            }.build()
        }

    }

}