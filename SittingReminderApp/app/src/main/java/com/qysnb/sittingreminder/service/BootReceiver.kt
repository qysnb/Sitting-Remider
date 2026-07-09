package com.qysnb.sittingreminder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, ReminderService::class.java).apply {
                action = ReminderService.ACTION_START_SERVICE
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
