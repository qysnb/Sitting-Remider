package com.qysnb.sittingreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.qysnb.sittingreminder.data.AppDatabase

class SittingReminderApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.notification_channel_desc)
        }
        val alertChannel = NotificationChannel(
            ALERT_CHANNEL_ID,
            getString(R.string.notification_alert_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.notification_alert_channel_desc)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        manager.createNotificationChannel(alertChannel)
    }

    companion object {
        const val CHANNEL_ID = "sitting_reminder_channel"
        const val ALERT_CHANNEL_ID = "sitting_reminder_alert_channel"
    }
}
