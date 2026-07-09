# 久坐助手 (Sitting Reminder) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a native Android app that periodically reminds users to stand up and sit back down, with configurable intervals, time ranges, and ringtone selection.

**Architecture:** Single-activity Jetpack Compose app with ForegroundService + AlarmManager for reliable background alarm delivery. Room database for settings persistence. ViewModel + StateFlow for state management.

**Tech Stack:** Kotlin, Jetpack Compose + Material 3, Room, AlarmManager, ForegroundService, MediaPlayer

**Package:** `com.qysnb.sittingreminder`

---

## File Structure

```
SittingReminderApp/
├── build.gradle.kts (project-level)
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   └── libs.versions.toml
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/qysnb/sittingreminder/
│       │   ├── SittingReminderApp.kt
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── Settings.kt
│       │   │   ├── SettingsDao.kt
│       │   │   ├── AppDatabase.kt
│       │   │   └── SettingsRepository.kt
│       │   ├── service/
│       │   │   ├── ReminderService.kt
│       │   │   └── BootReceiver.kt
│       │   ├── engine/
│       │   │   ├── ReminderStateManager.kt
│       │   │   ├── AlarmScheduler.kt
│       │   │   └── TimeRangeChecker.kt
│       │   ├── audio/
│       │   │   └── AudioPlayer.kt
│       │   └── ui/
│       │       ├── theme/Theme.kt
│       │       ├── theme/Color.kt
│       │       ├── theme/Type.kt
│       │       ├── MainScreen.kt
│       │       ├── MainViewModel.kt
│       │       └── components/
│       │           ├── MasterToggle.kt
│       │           ├── TimePickerGroup.kt
│       │           ├── RingtoneSelector.kt
│       │           ├── NextReminderDisplay.kt
│       │           └── AboutDialog.kt
│       └── res/
│           ├── values/strings.xml
│           ├── values/colors.xml
│           └── mipmap-*/
```

### Task 1: Project Scaffold & Gradle Config

**Files:**
- Create: `SittingReminderApp/build.gradle.kts`
- Create: `SittingReminderApp/settings.gradle.kts`
- Create: `SittingReminderApp/gradle.properties`
- Create: `SittingReminderApp/gradle/libs.versions.toml`
- Create: `SittingReminderApp/app/build.gradle.kts`
- Create: `SittingReminderApp/app/src/main/AndroidManifest.xml`

- [ ] **1.1 Create project-level build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
```

- [ ] **1.2 Create settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SittingReminder"
include(":app")
```

- [ ] **1.3 Create gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **1.4 Create gradle/libs.versions.toml**

```toml
[versions]
agp = "8.7.3"
kotlin = "2.1.0"
coreKtx = "1.15.0"
lifecycleRuntimeKtx = "2.8.7"
activityCompose = "1.9.3"
composeBom = "2024.12.01"
room = "2.6.1"
ksp = "2.1.0-1.0.29"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

- [ ] **1.5 Create app/build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.qysnb.sittingreminder"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.qysnb.sittingreminder"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.ui.tooling)
}
```

- [ ] **1.6 Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />

    <application
        android:name=".SittingReminderApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.SittingReminder">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.SittingReminder">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".service.ReminderService"
            android:exported="false"
            android:foregroundServiceType="specialUse" />

        <receiver
            android:name=".service.BootReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>

    </application>
</manifest>
```

### Task 2: Resources & Application Class

**Files:**
- Create: `SittingReminderApp/app/src/main/res/values/strings.xml`
- Create: `SittingReminderApp/app/src/main/res/values/colors.xml`
- Create: `SittingReminderApp/app/src/main/res/values/themes.xml`
- Create: `SittingReminderApp/app/src/main/java/com/qysnb/sittingreminder/SittingReminderApp.kt`
- Create: `SittingReminderApp/app/src/main/java/com/qysnb/sittingreminder/ui/theme/Color.kt`
- Create: `SittingReminderApp/app/src/main/java/com/qysnb/sittingreminder/ui/theme/Type.kt`
- Create: `SittingReminderApp/app/src/main/java/com/qysnb/sittingreminder/ui/theme/Theme.kt`

- [ ] **2.1 Create res/values/strings.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">久坐助手</string>
    <string name="master_toggle">提醒开关</string>
    <string name="master_toggle_on">已开启</string>
    <string name="master_toggle_off">已关闭</string>
    <string name="next_reminder">下次提醒</string>
    <string name="reminder_interval">提醒间隔</string>
    <string name="sit_back_delay">入座延迟</string>
    <string name="hours">时</string>
    <string name="minutes">分</string>
    <string name="seconds">秒</string>
    <string name="start_time">开始时间</string>
    <string name="end_time">结束时间</string>
    <string name="ringtone">提醒铃声</string>
    <string name="default_ringtone">默认闹钟铃声</string>
    <string name="select_ringtone">选择铃声</string>
    <string name="test_play">测试</string>
    <string name="about">关于</string>
    <string name="app_version">v 0.1.0</string>
    <string name="developer">Qysnb with DeepSeek V4 Flash</string>
    <string name="license">Apache Licence 2.0</string>
    <string name="app_description">久坐助手是一款轻量级提醒应用，定时提醒您起身活动，预防久坐带来的健康问题。</string>
    <string name="github_link">待定</string>
    <string name="notification_channel_name">久坐助手提醒</string>
    <string name="notification_channel_desc">显示当前提醒状态</string>
    <string name="notification_text">提醒已开启，将在 %1$s 后提醒</string>
    <string name="battery_optimization_title">请关闭电池优化</string>
    <string name="battery_optimization_message">为了确保提醒准时送达，请允许久坐助手在后台运行。</string>
    <string name="open_settings">打开设置</string>
    <string name="permission_exact_alarm">请授予精确闹钟权限</string>
    <string name="permission_exact_alarm_message">久坐助手需要精确闹钟权限才能准时提醒您。</string>
</resources>
```

- [ ] **2.2 Create res/values/colors.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="seed">#FF4CAF50</color>
</resources>
```

- [ ] **2.3 Create res/values/themes.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.SittingReminder" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

- [ ] **2.4 Create SittingReminderApp.kt**

```kotlin
package com.qysnb.sittingreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
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
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "sitting_reminder_channel"
    }
}
```

- [ ] **2.5 Create Color.kt**

```kotlin
package com.qysnb.sittingreminder.ui.theme

import androidx.compose.ui.graphics.Color

val Green40 = Color(0xFF2E7D32)
val Green80 = Color(0xFF81C784)
val GreenGrey40 = Color(0xFF546E7A)
val GreenGrey80 = Color(0xFFA5D6A7)
val LightGreen40 = Color(0xFF558B2F)
val LightGreen80 = Color(0xFFAED581)
```

- [ ] **2.6 Create Type.kt**

```kotlin
package com.qysnb.sittingreminder.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
)
```

- [ ] **2.7 Create Theme.kt**

```kotlin
package com.qysnb.sittingreminder.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = GreenGrey40,
    tertiary = LightGreen40,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    secondary = GreenGrey80,
    tertiary = LightGreen80,
)

@Composable
fun SittingReminderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### Task 3: Data Layer (Room)

**Files:**
- Create: `data/Settings.kt`
- Create: `data/SettingsDao.kt`
- Create: `data/AppDatabase.kt`
- Create: `data/SettingsRepository.kt`

- [ ] **3.1 Create Settings.kt**

```kotlin
package com.qysnb.sittingreminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1,
    val reminderIntervalMinutes: Long = 55,
    val sitBackDelaySeconds: Long = 300,
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 23,
    val endMinute: Int = 0,
    val ringtoneUri: String? = null,
    val masterEnabled: Boolean = false,
)
```

- [ ] **3.2 Create SettingsDao.kt**

```kotlin
package com.qysnb.sittingreminder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE id = 1")
    fun observeSettings(): Flow<Settings?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettings(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: Settings)
}
```

- [ ] **3.3 Create AppDatabase.kt**

```kotlin
package com.qysnb.sittingreminder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Settings::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sitting_reminder.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

- [ ] **3.4 Create SettingsRepository.kt**

```kotlin
package com.qysnb.sittingreminder.data

import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dao: SettingsDao) {

    fun observeSettings(): Flow<Settings?> = dao.observeSettings()

    suspend fun getSettings(): Settings = dao.getSettings() ?: Settings()

    suspend fun updateSettings(settings: Settings) = dao.upsertSettings(settings)
}
```

### Task 4: Reminder Engine

**Files:**
- Create: `engine/TimeRangeChecker.kt`
- Create: `engine/AlarmScheduler.kt`
- Create: `engine/ReminderStateManager.kt`

- [ ] **4.1 Create TimeRangeChecker.kt**

```kotlin
package com.qysnb.sittingreminder.engine

import java.util.Calendar

class TimeRangeChecker {

    fun isInActiveWindow(
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int,
        now: Calendar = Calendar.getInstance()
    ): Boolean {
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        return if (startMinutes <= endMinutes) {
            currentMinutes in startMinutes..endMinutes
        } else {
            currentMinutes >= startMinutes || currentMinutes <= endMinutes
        }
    }

    fun nextActiveTime(
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int,
        now: Calendar = Calendar.getInstance()
    ): Calendar {
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        val result = now.clone() as Calendar

        if (startMinutes <= endMinutes) {
            if (currentMinutes < startMinutes) {
                result.set(Calendar.HOUR_OF_DAY, startHour)
                result.set(Calendar.MINUTE, startMinute)
                result.set(Calendar.SECOND, 0)
            } else if (currentMinutes > endMinutes) {
                result.add(Calendar.DAY_OF_YEAR, 1)
                result.set(Calendar.HOUR_OF_DAY, startHour)
                result.set(Calendar.MINUTE, startMinute)
                result.set(Calendar.SECOND, 0)
            }
        } else {
            if (currentMinutes > endMinutes && currentMinutes < startMinutes) {
                result.set(Calendar.HOUR_OF_DAY, startHour)
                result.set(Calendar.MINUTE, startMinute)
                if (currentMinutes < startMinutes) {
                } else {
                    result.add(Calendar.DAY_OF_YEAR, 1)
                }
                result.set(Calendar.SECOND, 0)
            }
        }
        return result
    }
}
```

- [ ] **4.2 Create AlarmScheduler.kt**

```kotlin
package com.qysnb.sittingreminder.engine

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.qysnb.sittingreminder.service.ReminderService
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleExact(timeInMillis: Long, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.SCHEDULE_EXACT_ALARM
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }
        val intent = Intent(context, ReminderService::class.java).apply {
            action = ACTION_TRIGGER_REMINDER
            putExtra(EXTRA_REQUEST_CODE, requestCode)
        }
        val pendingIntent = PendingIntent.getForegroundService(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
        )
    }

    fun cancelAlarm(requestCode: Int) {
        val intent = Intent(context, ReminderService::class.java)
        val pendingIntent = PendingIntent.getForegroundService(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val ACTION_TRIGGER_REMINDER =
            "com.qysnb.sittingreminder.action.TRIGGER_REMINDER"
        const val EXTRA_REQUEST_CODE = "extra_request_code"
        const val REQUEST_CODE_STAND_UP = 1001
        const val REQUEST_CODE_SIT_BACK = 1002
    }
}
```

- [ ] **4.3 Create ReminderStateManager.kt**

```kotlin
package com.qysnb.sittingreminder.engine

enum class ReminderState {
    IDLE,
    STAND_UP_PENDING,
    STAND_UP_TRIGGERED,
    SIT_BACK_PENDING
}

class ReminderStateManager {

    var currentState: ReminderState = ReminderState.IDLE
        private set

    fun reset() {
        currentState = ReminderState.IDLE
    }

    fun startCycle() {
        currentState = ReminderState.STAND_UP_PENDING
    }

    fun onStandUpTriggered() {
        currentState = ReminderState.STAND_UP_TRIGGERED
    }

    fun onSitBackScheduled() {
        currentState = ReminderState.SIT_BACK_PENDING
    }

    fun onCycleComplete() {
        currentState = ReminderState.STAND_UP_PENDING
    }

    fun onStop() {
        currentState = ReminderState.IDLE
    }
}
```

### Task 5: Foreground Service & Boot Receiver

**Files:**
- Create: `service/ReminderService.kt`
- Create: `service/BootReceiver.kt`

- [ ] **5.1 Create ReminderService.kt**

```kotlin
package com.qysnb.sittingreminder.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.qysnb.sittingreminder.MainActivity
import com.qysnb.sittingreminder.R
import com.qysnb.sittingreminder.SittingReminderApp
import com.qysnb.sittingreminder.audio.AudioPlayer
import com.qysnb.sittingreminder.data.SettingsRepository
import com.qysnb.sittingreminder.engine.AlarmScheduler
import com.qysnb.sittingreminder.engine.ReminderState
import com.qysnb.sittingreminder.engine.ReminderStateManager
import com.qysnb.sittingreminder.engine.TimeRangeChecker
import java.util.Calendar

class ReminderService : Service() {

    private lateinit var audioPlayer: AudioPlayer
    private lateinit var alarmScheduler: AlarmScheduler
    private val stateManager = ReminderStateManager()
    private val timeRangeChecker = TimeRangeChecker()
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        audioPlayer = AudioPlayer()
        alarmScheduler = AlarmScheduler(this)
        val db = (application as SittingReminderApp).database
        settingsRepository = SettingsRepository(db.settingsDao())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> start()
            ACTION_STOP_SERVICE -> stop()
            AlarmScheduler.ACTION_TRIGGER_REMINDER -> handleAlarm(intent)
        }
        return START_STICKY
    }

    private fun start() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, SittingReminderApp.CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_text, "—"))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        scheduleNextStandUp()
    }

    private fun stop() {
        audioPlayer.stop()
        alarmScheduler.cancelAlarm(AlarmScheduler.REQUEST_CODE_STAND_UP)
        alarmScheduler.cancelAlarm(AlarmScheduler.REQUEST_CODE_SIT_BACK)
        stateManager.onStop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun handleAlarm(intent: Intent) {
        val requestCode = intent.getIntExtra(AlarmScheduler.EXTRA_REQUEST_CODE, 0)
        when (requestCode) {
            AlarmScheduler.REQUEST_CODE_STAND_UP -> triggerStandUp()
            AlarmScheduler.REQUEST_CODE_SIT_BACK -> triggerSitBack()
        }
    }

    private fun triggerStandUp() {
        stateManager.onStandUpTriggered()
        playRingtone()
        updateNotification(getString(R.string.notification_text, "该起身了"))
        scheduleSitBack()
    }

    private fun triggerSitBack() {
        stateManager.onSitBackScheduled()
        playRingtone()
        updateNotification(getString(R.string.notification_text, "请入座"))
        stateManager.onCycleComplete()
        scheduleNextStandUp()
    }

    private fun scheduleNextStandUp() {
        kotlinx.coroutines.runBlocking {
            val settings = settingsRepository.getSettings()
            if (!settings.masterEnabled) return@runBlocking
            if (!timeRangeChecker.isInActiveWindow(
                    settings.startHour, settings.startMinute,
                    settings.endHour, settings.endMinute
                )) return@runBlocking

            val triggerTime = Calendar.getInstance().apply {
                add(Calendar.MINUTE, settings.reminderIntervalMinutes.toInt())
            }
            alarmScheduler.scheduleExact(
                triggerTime.timeInMillis, AlarmScheduler.REQUEST_CODE_STAND_UP
            )
            stateManager.startCycle()
        }
    }

    private fun scheduleSitBack() {
        kotlinx.coroutines.runBlocking {
            val settings = settingsRepository.getSettings()
            val triggerTime = Calendar.getInstance().apply {
                add(Calendar.SECOND, settings.sitBackDelaySeconds.toInt())
            }
            alarmScheduler.scheduleExact(
                triggerTime.timeInMillis, AlarmScheduler.REQUEST_CODE_SIT_BACK
            )
        }
    }

    private fun playRingtone() {
        kotlinx.coroutines.runBlocking {
            val settings = settingsRepository.getSettings()
            val ringtoneUri = settings.ringtoneUri?.let { Uri.parse(it) }
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            audioPlayer.play(this@ReminderService, ringtoneUri)
        }
    }

    private fun updateNotification(text: String) {
        val notification = NotificationCompat.Builder(this, SittingReminderApp.CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
        val manager = getSystemService(android.app.NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        audioPlayer.release()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START_SERVICE =
            "com.qysnb.sittingreminder.action.START_SERVICE"
        const val ACTION_STOP_SERVICE =
            "com.qysnb.sittingreminder.action.STOP_SERVICE"
        const val NOTIFICATION_ID = 1001
    }
}
```

- [ ] **5.2 Create BootReceiver.kt**

```kotlin
package com.qysnb.sittingreminder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

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
```

### Task 6: Audio Player

**Files:**
- Create: `audio/AudioPlayer.kt`

- [ ] **6.1 Create AudioPlayer.kt**

```kotlin
package com.qysnb.sittingreminder.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    suspend fun play(context: Context, uri: Uri, durationMs: Long = 0L) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_ALARM)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            setDataSource(context, uri)
            setOnCompletionListener { release() }
            prepare()
            start()
        }
        if (durationMs > 0) {
            handler.postDelayed({ release() }, durationMs)
        }
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
```

### Task 7: UI Components

**Files:**
- Create: `ui/MainViewModel.kt`
- Create: `ui/MainScreen.kt`
- Create: `ui/components/MasterToggle.kt`
- Create: `ui/components/TimePickerGroup.kt`
- Create: `ui/components/RingtoneSelector.kt`
- Create: `ui/components/NextReminderDisplay.kt`
- Create: `ui/components/AboutDialog.kt`

- [ ] **7.1 Create MainViewModel.kt**

```kotlin
package com.qysnb.sittingreminder.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qysnb.sittingreminder.SittingReminderApp
import com.qysnb.sittingreminder.data.Settings
import com.qysnb.sittingreminder.data.SettingsRepository
import com.qysnb.sittingreminder.service.ReminderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class UiState(
    val masterEnabled: Boolean = false,
    val reminderIntervalMinutes: Long = 55,
    val sitBackDelaySeconds: Long = 300,
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 23,
    val endMinute: Int = 0,
    val ringtoneUri: String? = null,
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
                        reminderIntervalMinutes = settings.reminderIntervalMinutes,
                        sitBackDelaySeconds = settings.sitBackDelaySeconds,
                        startHour = settings.startHour,
                        startMinute = settings.startMinute,
                        endHour = settings.endHour,
                        endMinute = settings.endMinute,
                        ringtoneUri = settings.ringtoneUri,
                        isLoaded = true,
                    )
                } else {
                    val defaults = Settings()
                    repository.updateSettings(defaults)
                }
            }
        }
    }

    fun toggleMaster(enabled: Boolean) {
        viewModelScope.launch {
            val current = repository.getSettings()
            repository.updateSettings(current.copy(masterEnabled = enabled))
            val context = getApplication<SittingReminderApp>()
            if (enabled) {
                val intent = Intent(context, ReminderService::class.java).apply {
                    action = ReminderService.ACTION_START_SERVICE
                }
                ContextCompat.startForegroundService(context, intent)
            } else {
                val intent = Intent(context, ReminderService::class.java).apply {
                    action = ReminderService.ACTION_STOP_SERVICE
                }
                context.startService(intent)
            }
        }
    }

    fun updateReminderInterval(minutes: Long) {
        viewModelScope.launch {
            val current = repository.getSettings()
            repository.updateSettings(current.copy(reminderIntervalMinutes = minutes))
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
```

- [ ] **7.2 Create MainScreen.kt**

```kotlin
package com.qysnb.sittingreminder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qysnb.sittingreminder.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAbout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("久坐助手") },
                actions = {
                    IconButton(onClick = { showAbout = true }) {
                        Icon(Icons.Default.Info, contentDescription = "关于")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            MasterToggle(
                enabled = state.masterEnabled,
                onToggle = viewModel::toggleMaster
            )

            NextReminderDisplay(
                isActive = state.masterEnabled,
                reminderIntervalMinutes = state.reminderIntervalMinutes
            )

            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "提醒设置",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TimePickerGroup(
                        title = "提醒间隔",
                        valueMinutes = state.reminderIntervalMinutes,
                        showSeconds = false,
                        onValueChange = viewModel::updateReminderInterval
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TimePickerGroup(
                        title = "入座延迟",
                        valueMinutes = state.sitBackDelaySeconds / 60,
                        valueSeconds = (state.sitBackDelaySeconds % 60).toInt(),
                        showSeconds = true,
                        onValueChange = { mins, secs ->
                            viewModel.updateSitBackDelay(mins * 60L + secs)
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TimeRangeSelector(
                        startHour = state.startHour,
                        startMinute = state.startMinute,
                        endHour = state.endHour,
                        endMinute = state.endMinute,
                        onStartChange = viewModel::updateStartTime,
                        onEndChange = viewModel::updateEndTime
                    )
                }
            }

            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "铃声设置",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RingtoneSelector(
                        ringtoneUri = state.ringtoneUri,
                        onRingtoneSelected = viewModel::updateRingtoneUri
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}
```

- [ ] **7.3 Create MasterToggle.kt**

```kotlin
package com.qysnb.sittingreminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MasterToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "提醒状态",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    if (enabled) "已开启" else "已关闭",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}
```

- [ ] **7.4 Create TimePickerGroup.kt**

```kotlin
package com.qysnb.sittingreminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimePickerGroup(
    title: String,
    valueMinutes: Long,
    valueSeconds: Int = 0,
    showSeconds: Boolean = false,
    onValueChange: (Long) -> Unit = {},
    onValueChangeWithSeconds: ((Long, Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var hours by remember(valueMinutes) {
        mutableStateOf((valueMinutes / 60).toInt())
    }
    var minutes by remember(valueMinutes) {
        mutableStateOf((valueMinutes % 60).toInt())
    }
    var seconds by remember(valueSeconds) {
        mutableStateOf(valueSeconds)
    }

    fun notifyChange() {
        val totalMinutes = hours * 60L + minutes
        if (showSeconds && onValueChangeWithSeconds != null) {
            onValueChangeWithSeconds(totalMinutes, seconds)
        } else {
            onValueChange(totalMinutes)
        }
    }

    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeSelector(
                value = hours,
                range = 0..23,
                label = "时",
                onValueChange = { hours = it; notifyChange() }
            )
            TimeSelector(
                value = minutes,
                range = 0..59,
                label = "分",
                onValueChange = { minutes = it; notifyChange() }
            )
            if (showSeconds) {
                TimeSelector(
                    value = seconds,
                    range = 0..59,
                    label = "秒",
                    onValueChange = { seconds = it; notifyChange() }
                )
            }
        }
    }
}

@Composable
fun TimeSelector(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        TextButton(onClick = {
            onValueChange(if (value + 1 > range.last) range.first else value + 1)
        }) {
            Text("+", style = MaterialTheme.typography.titleMedium)
        }
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(min = 56.dp)
        ) {
            Text(
                text = String.format("%02d", value),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        TextButton(onClick = {
            onValueChange(if (value - 1 < range.first) range.last else value - 1)
        }) {
            Text("-", style = MaterialTheme.typography.titleMedium)
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TimeRangeSelector(
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    onStartChange: (Int, Int) -> Unit,
    onEndChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("时间范围", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("开始", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TimeSelector(
                        value = startHour, range = 0..23, label = "",
                        onValueChange = { onStartChange(it, startMinute) }
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    TimeSelector(
                        value = startMinute, range = 0..59, label = "",
                        onValueChange = { onStartChange(startHour, it) }
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("结束", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TimeSelector(
                        value = endHour, range = 0..23, label = "",
                        onValueChange = { onEndChange(it, endMinute) }
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    TimeSelector(
                        value = endMinute, range = 0..59, label = "",
                        onValueChange = { onEndChange(endHour, it) }
                    )
                }
            }
        }
    }
}
```

- [ ] **7.5 Create RingtoneSelector.kt**

```kotlin
package com.qysnb.sittingreminder.ui.components

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.qysnb.sittingreminder.audio.AudioPlayer
import kotlinx.coroutines.launch

@Composable
fun RingtoneSelector(
    ringtoneUri: String?,
    onRingtoneSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val audioPlayer = remember { AudioPlayer() }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            onRingtoneSelected(uri.toString())
        }
    }

    val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    val displayName = remember(ringtoneUri) {
        if (ringtoneUri == null) {
            "默认闹钟铃声"
        } else {
            try {
                val uri = Uri.parse(ringtoneUri)
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) cursor.getString(nameIndex)
                        else "自定义铃声"
                    } else "自定义铃声"
                } ?: "自定义铃声"
            } catch (e: Exception) {
                "默认闹钟铃声"
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { audioPlayer.release() }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(displayName, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                ringtonePickerLauncher.launch(arrayOf("audio/*"))
            }) {
                Text("选择铃声")
            }
            OutlinedButton(onClick = {
                scope.launch {
                    val uri = ringtoneUri?.let { Uri.parse(it) } ?: defaultUri
                    audioPlayer.play(context, uri, durationMs = 2000L)
                }
            }) {
                Text("测试")
            }
        }
    }
}
```

- [ ] **7.6 Create NextReminderDisplay.kt**

```kotlin
package com.qysnb.sittingreminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun NextReminderDisplay(
    isActive: Boolean,
    reminderIntervalMinutes: Long,
    modifier: Modifier = Modifier
) {
    var countdownText by remember { mutableStateOf("--") }

    LaunchedEffect(isActive, reminderIntervalMinutes) {
        if (!isActive) {
            countdownText = "--"
            return@LaunchedEffect
        }
        while (true) {
            val nextTime = Calendar.getInstance().apply {
                add(Calendar.MINUTE, reminderIntervalMinutes.toInt())
            }
            val diff = nextTime.timeInMillis - Calendar.getInstance().timeInMillis
            val minutes = diff / 60000
            val seconds = (diff % 60000) / 1000
            countdownText = "${minutes}分${seconds}秒"
            delay(1000)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "下次提醒",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (isActive) countdownText else "提醒已关闭",
                style = MaterialTheme.typography.headlineLarge,
                color = if (isActive)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

- [ ] **7.7 Create AboutDialog.kt**

```kotlin
package com.qysnb.sittingreminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "关于",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AboutItem("软件名", "久坐助手")
                AboutItem("版本号", "v 0.1.0")
                AboutItem("GitHub", "待定")
                AboutItem("简介",
                    "久坐助手是一款轻量级提醒应用，定时提醒您起身活动，预防久坐带来的健康问题。")
                AboutItem("开发者", "Qysnb with DeepSeek V4 Flash")
                AboutItem("开源许可", "Apache Licence 2.0")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun AboutItem(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
```

### Task 8: MainActivity & Permission Handling

**Files:**
- Create: `MainActivity.kt`

- [ ] **8.1 Create MainActivity.kt**

```kotlin
package com.qysnb.sittingreminder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.qysnb.sittingreminder.ui.MainScreen
import com.qysnb.sittingreminder.ui.theme.SittingReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SittingReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
        requestPermissionsOnFirstLaunch()
    }

    private fun requestPermissionsOnFirstLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_NOTIFICATIONS
                )
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_NOTIFICATIONS = 100
    }
}
```

### Task 9: Permission Helper & Battery Optimization Dialog

**Files:**
- Modify: `MainActivity.kt` (add launcher and dialog)

- [ ] **9.1 Add battery optimization / exact alarm permission flows to MainScreen**

```kotlin
// Add this at the top of MainScreen composable, before Scaffold:

@Composable
fun PermissionGate(
    onBatteryOptimizationDismiss: () -> Unit,
    onExactAlarmGranted: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showBatteryDialog by remember { mutableStateOf(true) }
    var showExactAlarmDialog by remember { mutableStateOf(true) }

    if (showBatteryDialog) {
        AlertDialog(
            onDismissRequest = { showBatteryDialog = false },
            title = { Text("请关闭电池优化") },
            text = { Text("为了确保提醒准时送达，请允许久坐助手在后台运行。") },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        android.net.Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                    showBatteryDialog = false
                }) { Text("打开设置") }
            },
            dismissButton = {
                TextButton(onClick = { showBatteryDialog = false }) { Text("稍后") }
            }
        )
    }

    if (showExactAlarmDialog && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.SCHEDULE_EXACT_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            AlertDialog(
                onDismissRequest = { showExactAlarmDialog = false },
                title = { Text("请授予精确闹钟权限") },
                text = { Text("久坐助手需要精确闹钟权限才能准时提醒您。") },
                confirmButton = {
                    TextButton(onClick = {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        context.startActivity(intent)
                        showExactAlarmDialog = false
                    }) { Text("打开设置") }
                },
                dismissButton = {
                    TextButton(onClick = { showExactAlarmDialog = false }) { Text("取消") }
                }
            )
        }
    }

    content()
}
```

### Task 10: Build & Verify

- [ ] **10.1 Build the project**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **10.2 Verify manifest permissions**

Check `AndroidManifest.xml` contains all required permissions.

- [ ] **10.3 Verify Room schema**

Confirm Room entities, DAOs, and database class compile correctly.
