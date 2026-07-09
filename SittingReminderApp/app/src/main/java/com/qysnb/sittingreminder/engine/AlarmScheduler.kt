package com.qysnb.sittingreminder.engine

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.qysnb.sittingreminder.service.ReminderService

class AlarmScheduler(private val context: Context) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleExact(timeInMillis: Long, requestCode: Int) {
        val intent = Intent(context, ReminderService::class.java).apply {
            action = ACTION_TRIGGER_REMINDER
            putExtra(EXTRA_REQUEST_CODE, requestCode)
        }
        val pendingIntent = PendingIntent.getForegroundService(
            context, requestCode, intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
            )
        }
    }

    fun cancelAlarm(requestCode: Int) {
        val intent = Intent(context, ReminderService::class.java).apply {
            action = ACTION_TRIGGER_REMINDER
        }
        val pendingIntent = PendingIntent.getForegroundService(
            context, requestCode, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    companion object {
        const val ACTION_TRIGGER_REMINDER =
            "com.qysnb.sittingreminder.action.TRIGGER_REMINDER"
        const val EXTRA_REQUEST_CODE = "extra_request_code"
        const val REQUEST_CODE_STAND_UP = 1001
        const val REQUEST_CODE_SIT_BACK = 1002
    }
}
