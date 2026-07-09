package com.qysnb.sittingreminder.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.qysnb.sittingreminder.MainActivity
import com.qysnb.sittingreminder.R
import com.qysnb.sittingreminder.SittingReminderApp
import com.qysnb.sittingreminder.audio.AudioPlayer
import com.qysnb.sittingreminder.data.SettingsRepository
import com.qysnb.sittingreminder.engine.AlarmScheduler
import com.qysnb.sittingreminder.engine.ReminderStateManager
import com.qysnb.sittingreminder.engine.TimeRangeChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

class ReminderService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var alarmScheduler: AlarmScheduler
    private val stateManager = ReminderStateManager()
    private val timeRangeChecker = TimeRangeChecker()
    private lateinit var settingsRepository: SettingsRepository
    private var nextTriggerTimeMillis: Long? = null
    private var countdownJob: Job? = null
    private lateinit var vibrator: Vibrator

    override fun onCreate() {
        super.onCreate()
        audioPlayer = AudioPlayer()
        alarmScheduler = AlarmScheduler(this)
        val db = (application as SittingReminderApp).database
        settingsRepository = SettingsRepository(db.settingsDao())
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> start()
            ACTION_STOP_SERVICE -> stop()
            ACTION_DISMISS_ALERT -> dismissAlert()
            ACTION_RESCHEDULE -> reschedule()
            AlarmScheduler.ACTION_TRIGGER_REMINDER -> {
                ensureForeground()
                handleAlarm(intent)
            }
            else -> {
                ensureForeground()
                scheduleNextStandUp()
            }
        }
        return START_STICKY
    }

    private fun buildForegroundNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val dismissIntent = Intent(this, ReminderService::class.java).apply {
            action = ACTION_DISMISS_ALERT
        }
        val dismissPendingIntent = PendingIntent.getService(
            this, 1, dismissIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val countdownText = nextTriggerTimeMillis?.let { triggerTime ->
            val remaining = triggerTime - System.currentTimeMillis()
            if (remaining > 0) {
                val totalSec = remaining / 1000
                val h = (totalSec / 3600).toInt()
                val m = ((totalSec % 3600) / 60).toInt()
                val s = (totalSec % 60).toInt()
                if (h > 0) "下次提醒: ${h}时${m}分" else "下次提醒: ${String.format("%02d:%02d", m, s)}"
            } else {
                getString(R.string.notification_text)
            }
        } ?: getString(R.string.notification_text)

        return NotificationCompat.Builder(this, SittingReminderApp.CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(countdownText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.stop_ringtone), dismissPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateForegroundNotification() {
        foregroundNotification = buildForegroundNotification()
        startForeground(NOTIFICATION_ID, foregroundNotification!!)
    }

    private fun startCountdownUpdates() {
        countdownJob?.cancel()
        countdownJob = scope.launch {
            while (isActive) {
                updateForegroundNotification()
                val now = System.currentTimeMillis()
                val nextSecond = (now / 1000 + 1) * 1000
                delay(nextSecond - now)
            }
        }
    }

    private fun stopCountdownUpdates() {
        countdownJob?.cancel()
        countdownJob = null
    }

    private var foregroundNotification: Notification? = null

    private fun ensureForeground() {
        if (foregroundNotification == null) {
            foregroundNotification = buildForegroundNotification()
        }
        startForeground(NOTIFICATION_ID, foregroundNotification!!)
    }

    private fun start() {
        foregroundNotification = buildForegroundNotification()
        startForeground(NOTIFICATION_ID, foregroundNotification!!)
        startCountdownUpdates()
        scheduleNextStandUp()
    }

    private fun stop() {
        audioPlayer.stop()
        stopCountdownUpdates()
        alarmScheduler.cancelAlarm(AlarmScheduler.REQUEST_CODE_STAND_UP)
        alarmScheduler.cancelAlarm(AlarmScheduler.REQUEST_CODE_SIT_BACK)
        stateManager.onStop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun handleAlarm(intent: Intent) {
        val requestCode = intent.getIntExtra(AlarmScheduler.EXTRA_REQUEST_CODE, 0)
        scope.launch {
            if (!settingsRepository.getSettings().masterEnabled) return@launch
            when (requestCode) {
                AlarmScheduler.REQUEST_CODE_STAND_UP -> triggerStandUp()
                AlarmScheduler.REQUEST_CODE_SIT_BACK -> triggerSitBack()
            }
        }
    }

    private fun triggerStandUp() {
        nextTriggerTimeMillis = null
        updateForegroundNotification()
        stateManager.onStandUpTriggered()
        showAlertNotification(getString(R.string.stand_up_reminder))
        vibrate()
        playRingtone()
        scheduleSitBack()
    }

    private fun triggerSitBack() {
        nextTriggerTimeMillis = null
        updateForegroundNotification()
        showAlertNotification(getString(R.string.sit_back_reminder))
        vibrate()
        playRingtone()
        stateManager.onCycleComplete()
        scheduleNextStandUp()
    }

    private fun scheduleNextStandUp() {
        scope.launch {
            val settings = settingsRepository.getSettings()

            if (!timeRangeChecker.isInActiveWindow(
                    settings.startHour, settings.startMinute,
                    settings.endHour, settings.endMinute
                )
            ) {
                val nextActive = timeRangeChecker.nextActiveStart(
                    settings.startHour, settings.startMinute,
                    settings.endHour, settings.endMinute
                )
                nextTriggerTimeMillis = nextActive.timeInMillis
                settingsRepository.updateSettings(settings.copy(nextTriggerTimeMillis = nextTriggerTimeMillis))
                updateForegroundNotification()
                stateManager.startCycle()
                alarmScheduler.scheduleExact(
                    nextActive.timeInMillis, AlarmScheduler.REQUEST_CODE_STAND_UP
                )
                return@launch
            }

            val triggerTime = Calendar.getInstance().apply {
                add(Calendar.SECOND, settings.reminderIntervalSeconds.toInt())
            }
            nextTriggerTimeMillis = triggerTime.timeInMillis
            settingsRepository.updateSettings(settings.copy(nextTriggerTimeMillis = nextTriggerTimeMillis))
            updateForegroundNotification()
            alarmScheduler.scheduleExact(
                triggerTime.timeInMillis, AlarmScheduler.REQUEST_CODE_STAND_UP
            )
            stateManager.startCycle()
        }
    }

    private fun scheduleSitBack() {
        scope.launch {
            val settings = settingsRepository.getSettings()
            stateManager.onSitBackScheduled()
            val triggerTime = Calendar.getInstance().apply {
                add(Calendar.SECOND, settings.sitBackDelaySeconds.toInt())
            }
            nextTriggerTimeMillis = triggerTime.timeInMillis
            settingsRepository.updateSettings(settings.copy(nextTriggerTimeMillis = nextTriggerTimeMillis))
            updateForegroundNotification()
            alarmScheduler.scheduleExact(
                triggerTime.timeInMillis, AlarmScheduler.REQUEST_CODE_SIT_BACK
            )
        }
    }

    private fun showAlertNotification(text: String) {
        val dismissIntent = Intent(this, ReminderService::class.java).apply {
            action = ACTION_DISMISS_ALERT
        }
        val dismissPendingIntent = PendingIntent.getService(
            this, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, SittingReminderApp.ALERT_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setFullScreenIntent(dismissPendingIntent, true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.dismiss), dismissPendingIntent)
            .setAutoCancel(false)
            .setOngoing(false)
            .build()
        NotificationManagerCompat.from(this).notify(ALERT_NOTIFICATION_ID, notification)
    }

    private fun dismissAlert() {
        audioPlayer.stop()
        NotificationManagerCompat.from(this).cancel(ALERT_NOTIFICATION_ID)
    }

    private fun playRingtone() {
        scope.launch {
            val settings = settingsRepository.getSettings()
            if (settings.silentMode) return@launch
            val ringtoneUri = settings.ringtoneUri?.let { Uri.parse(it) }
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            audioPlayer.play(this@ReminderService, ringtoneUri)
        }
    }

    private fun vibrate() {
        scope.launch {
            val settings = settingsRepository.getSettings()
            if (!settings.vibrationEnabled) return@launch
            val intensity = settings.vibrationIntensity.coerceIn(1, 5)
            val amplitude = (intensity * 51).coerceAtMost(255)
            val timings = longArrayOf(0, 600, 300, 600)
            val amplitudes = intArrayOf(0, amplitude, 0, amplitude)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        audioPlayer.release()
        stopCountdownUpdates()
        super.onDestroy()
    }

    private fun reschedule() {
        alarmScheduler.cancelAlarm(AlarmScheduler.REQUEST_CODE_STAND_UP)
        alarmScheduler.cancelAlarm(AlarmScheduler.REQUEST_CODE_SIT_BACK)
        audioPlayer.stop()
        ensureForeground()
        startCountdownUpdates()
        scheduleNextStandUp()
    }

    companion object {
        const val ACTION_START_SERVICE =
            "com.qysnb.sittingreminder.action.START_SERVICE"
        const val ACTION_STOP_SERVICE =
            "com.qysnb.sittingreminder.action.STOP_SERVICE"
        const val ACTION_DISMISS_ALERT =
            "com.qysnb.sittingreminder.action.DISMISS_ALERT"
        const val ACTION_RESCHEDULE =
            "com.qysnb.sittingreminder.action.RESCHEDULE"
        const val NOTIFICATION_ID = 1001
        const val ALERT_NOTIFICATION_ID = 1002
    }
}
