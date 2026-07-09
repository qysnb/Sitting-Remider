package com.qysnb.sittingreminder.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.content.ContextCompat
import com.qysnb.sittingreminder.SittingReminderApp
import com.qysnb.sittingreminder.data.Settings
import com.qysnb.sittingreminder.data.SettingsRepository
import com.qysnb.sittingreminder.service.ReminderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val masterEnabled: Boolean = false,
    val reminderIntervalSeconds: Long = 3300,
    val sitBackDelaySeconds: Long = 300,
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 23,
    val endMinute: Int = 0,
    val ringtoneUri: String? = null,
    val nextTriggerTimeMillis: Long? = null,
    val isLoaded: Boolean = false,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SettingsRepository
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val db = (application as SittingReminderApp).database
        repository = SettingsRepository(db.settingsDao())
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            repository.observeSettings().collect { settings ->
                if (settings != null) {
                    _uiState.value = UiState(
                        masterEnabled = settings.masterEnabled,
                        reminderIntervalSeconds = settings.reminderIntervalSeconds,
                        sitBackDelaySeconds = settings.sitBackDelaySeconds,
                        startHour = settings.startHour,
                        startMinute = settings.startMinute,
                        endHour = settings.endHour,
                        endMinute = settings.endMinute,
                        ringtoneUri = settings.ringtoneUri,
                        isLoaded = true,
                        nextTriggerTimeMillis = settings.nextTriggerTimeMillis
                            ?: _uiState.value.nextTriggerTimeMillis,
                    )
                    if (settings.masterEnabled && !_uiState.value.masterEnabled && !_uiState.value.isLoaded) {
                        val context = getApplication<SittingReminderApp>()
                        val intent = Intent(context, ReminderService::class.java).apply {
                            action = ReminderService.ACTION_START_SERVICE
                        }
                        runCatching { ContextCompat.startForegroundService(context, intent) }
                    }
                }
            }
        }
    }

    fun toggleMaster(enabled: Boolean) {
        viewModelScope.launch {
            val current = repository.getSettings()
            val context = getApplication<SittingReminderApp>()
            if (enabled) {
                val triggerTime = System.currentTimeMillis() + current.reminderIntervalSeconds * 1000
                _uiState.value = _uiState.value.copy(nextTriggerTimeMillis = triggerTime)
                repository.updateSettings(current.copy(masterEnabled = enabled, nextTriggerTimeMillis = triggerTime))
                val intent = Intent(context, ReminderService::class.java).apply {
                    action = ReminderService.ACTION_START_SERVICE
                }
                ContextCompat.startForegroundService(context, intent)
            } else {
                _uiState.value = _uiState.value.copy(nextTriggerTimeMillis = null)
                repository.updateSettings(current.copy(masterEnabled = enabled, nextTriggerTimeMillis = null))
                val intent = Intent(context, ReminderService::class.java).apply {
                    action = ReminderService.ACTION_STOP_SERVICE
                }
                context.startService(intent)
            }
        }
    }

    fun updateReminderInterval(seconds: Long) {
        viewModelScope.launch {
            val current = repository.getSettings()
            val triggerTime = System.currentTimeMillis() + seconds * 1000
            _uiState.value = _uiState.value.copy(nextTriggerTimeMillis = triggerTime)
            repository.updateSettings(current.copy(reminderIntervalSeconds = seconds, nextTriggerTimeMillis = triggerTime))
            val context = getApplication<SittingReminderApp>()
            val intent = Intent(context, ReminderService::class.java).apply {
                action = ReminderService.ACTION_RESCHEDULE
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    fun updateSitBackDelay(seconds: Long) {
        viewModelScope.launch {
            val current = repository.getSettings()
            repository.updateSettings(current.copy(sitBackDelaySeconds = seconds))
        }
    }

    fun updateStartTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val current = repository.getSettings()
            repository.updateSettings(current.copy(startHour = hour, startMinute = minute))
        }
    }

    fun updateEndTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val current = repository.getSettings()
            repository.updateSettings(current.copy(endHour = hour, endMinute = minute))
        }
    }

    fun updateRingtoneUri(uri: String?) {
        viewModelScope.launch {
            val current = repository.getSettings()
            repository.updateSettings(current.copy(ringtoneUri = uri))
        }
    }
}
