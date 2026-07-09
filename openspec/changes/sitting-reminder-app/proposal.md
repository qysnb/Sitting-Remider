## Why

Office workers and sedentary individuals often sit for prolonged periods without breaks, increasing health risks. "久坐助手" (Sitting Reminder) solves this by providing configurable periodic reminders to stand up and move, with a follow-up reminder to sit back down.

## What Changes

- Build a new Android native app from scratch using Kotlin
- Core reminder engine with configurable intervals, sit-back delay, and time-range restrictions
- Ringtone selection from device audio files with a test-play button
- Master on/off toggle that resets the reminder timer on each toggle
- Foreground service for reliable background reminder execution
- About dialog with app version, license, and developer info

## Capabilities

### New Capabilities
- `reminder-engine`: Configurable periodic reminder scheduling with sit-stand-sit cycle, time-range support, and toggle reset behavior
- `audio-player`: Ringtone selection from device storage, default alarm sound fallback, and 2-second test playback
- `foreground-service`: Persistent background service for reliable alarm delivery across app lifecycle
- `app-settings`: UI for configuring reminder interval, sit-back delay, start/end time, and master toggle
- `about-page`: Application info dialog with version, license, developer infomation, and GitHub link

### Modified Capabilities
<!-- None - this is a greenfield application -->

## Impact

- New Android project with Kotlin, targeting modern Android (API 26+)
- Uses Android's `AlarmManager` and `ForegroundService` for background execution
- Requires `SCHEDULE_EXACT_ALARM`, `FOREGROUND_SERVICE`, `POST_NOTIFICATIONS` permissions
- READ_EXTERNAL_STORAGE or READ_MEDIA_AUDIO for ringtone selection
- No external dependencies beyond AndroidX and Material Design
