package com.qysnb.sittingreminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1,
    val reminderIntervalSeconds: Long = 3300,
    val sitBackDelaySeconds: Long = 300,
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 23,
    val endMinute: Int = 0,
    val ringtoneUri: String? = null,
    val masterEnabled: Boolean = false,
    val nextTriggerTimeMillis: Long? = null,
    val silentMode: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val vibrationIntensity: Int = 3,
)
