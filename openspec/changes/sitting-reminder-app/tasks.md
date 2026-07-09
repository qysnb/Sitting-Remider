## 1. Project Setup

- [ ] 1.1 Create Android project with Kotlin, Jetpack Compose, Material 3, and Room
- [ ] 1.2 Add AndroidManifest permissions: FOREGROUND_SERVICE, POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM, RECEIVE_BOOT_COMPLETED, READ_MEDIA_AUDIO
- [ ] 1.3 Configure targetSdk 34, minSdk 26, compileSdk 34

## 2. Data Layer (Room Database)

- [ ] 2.1 Define Settings entity with columns: reminderIntervalMinutes (Long), sitBackDelaySeconds (Long), startHour, startMinute, endHour, endMinute, ringtoneUri (String?), masterEnabled (Boolean)
- [ ] 2.2 Create SettingsDao with insert/update/select operations
- [ ] 2.3 Create AppDatabase with Room singleton
- [ ] 2.4 Create SettingsRepository as single source of truth for settings

## 3. Reminder Engine

- [ ] 3.1 Implement ReminderStateManager with state machine: IDLE, STAND_UP_PENDING, STAND_UP_TRIGGERED, SIT_BACK_PENDING
- [ ] 3.2 Implement AlarmScheduler using AlarmManager.setExactAndAllowWhileIdle()
- [ ] 3.3 Implement time range checker (respects start/end time including overnight window)
- [ ] 3.4 Implement reminder cycle logic: interval → stand-up alarm → sit-back delay → sit-back alarm → repeat
- [ ] 3.5 Implement toggle reset: on toggle off → cancel all alarms; on toggle on → schedule from current time

## 4. Foreground Service

- [ ] 4.1 Create ReminderService extending Service with onStartCommand/onDestroy
- [ ] 4.2 Implement persistent notification with next reminder info
- [ ] 4.3 Handle alarm broadcast via BroadcastReceiver in the service
- [ ] 4.4 Register BootReceiver to restart service after device reboot
- [ ] 4.5 Request SCHEDULE_EXACT_ALARM permission with user dialog on API 31+

## 5. Audio Player

- [ ] 5.1 Implement ringtone picker using ACTION_OPEN_DOCUMENT with audio MIME filter
- [ ] 5.2 Implement audio playback with MediaPlayer for reminder alarms
- [ ] 5.3 Implement 2-second test playback button
- [ ] 5.4 Fall back to default alarm ringtone when custom URI is invalid
- [ ] 5.5 Release MediaPlayer resources after playback

## 6. UI (Jetpack Compose + Material 3)

- [ ] 6.1 Create MainActivity with single-screen Compose layout
- [ ] 6.2 Implement MasterToggle composable with switch and state management
- [ ] 6.3 Implement ReminderIntervalPicker with hour (0-23) and minute (0-59) selectors
- [ ] 6.4 Implement SitBackDelayPicker with minute (0-59) and second (0-59) selectors
- [ ] 6.5 Implement TimeRangePicker with start/end time selectors
- [ ] 6.6 Implement ringtone selector row with picker button and test-play button
- [ ] 6.7 Implement next reminder countdown display
- [ ] 6.8 Implement battery optimization exemption dialog on first launch
- [ ] 6.9 Implement About dialog with all required info
- [ ] 6.10 Wire ViewModel with SettingsRepository, ReminderStateManager, AlarmScheduler

## 7. Permissions & Edge Cases

- [ ] 7.1 Handle SCHEDULE_EXACT_ALARM permission denial on API 31+
- [ ] 7.2 Handle POST_NOTIFICATIONS permission on API 33+
- [ ] 7.3 Handle ringtone URI invalidation (file deleted) gracefully
- [ ] 7.4 Test app behavior when swiped from recents
- [ ] 7.5 Test reboot recovery flow

## 8. Build & Verify

- [ ] 8.1 Build debug APK and verify no compilation errors
- [ ] 8.2 Verify reminder cycle works end-to-end
- [ ] 8.3 Verify settings persist across app restarts
- [ ] 8.4 Verify About dialog displays all required fields
