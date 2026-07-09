## Context

A greenfield Android application for periodic sit-stand-sit reminders. The app must function reliably in the background, survive app swipes, and accommodate configurable timing. Target is modern Android (API 26+, Android 8.0 Oreo).

## Goals / Non-Goals

**Goals:**
- Reliable foreground service that delivers alarms regardless of app visibility
- Configurable reminder interval (hours:minutes), sit-back delay (minutes:seconds), and active time window
- Ringtone picker integrated with Android's audio content resolver
- Master toggle that resets the reminder cycle
- About screen with version, license, and credits
- Modern Material 3 UI with clean information hierarchy

**Non-Goals:**

- Wear OS / tablet-specific adaptations (phone-only for v0.1)
- Multi-language support (Must using Chinese, hardcoded strings)
- Cloud sync or backup
- Health tracking or statistics
- Smart skip detection (e.g., motion sensors)

## Decisions

1. **AlarmManager + ForegroundService (not WorkManager)**: WorkManager defers to doze mode too aggressively for time-sensitive reminders. ForegroundService with a persistent notification keeps the process alive and AlarmManager provides exact scheduling. The service shows an unobtrusive low-priority notification.

2. **Jetpack Compose over XML layouts**: Modern, less boilerplate, better state management. Material 3 components provide a polished look.

3. **ViewModel + StateFlow**: Survives config changes, clean separation of UI and business logic. The reminder engine runs in the ViewModel layer, while the service only handles alarm delivery.

4. **Room database for persistence**: Stores settings (interval, delay, time range) and reminder state. Survives reboots and process death.

5. **Scoped storage for ringtone picker**: Use `ACTION_OPEN_DOCUMENT` with `Audio` MIME filter. On API 33+ use `READ_MEDIA_AUDIO`; fall back to `READ_EXTERNAL_STORAGE` for older versions.

6. **Single Activity architecture**: One activity with Compose navigation. Simple enough that multiple activities add unnecessary complexity.

## Risks / Trade-offs

- **Battery optimization killing service** → Guide user through disabling battery optimization for the app on first launch
- **Exact alarm permission denied** → On API 31+, `SCHEDULE_EXACT_ALARM` must be requested; prompt user if denied
- **App swiped from recents** → ForegroundService with high-priority notification mitigates but doesn't guarantee survival on all OEMs (Xiaomi, Huawei, etc.)
- **Ringtone file deleted after selection** → Store content URI, and if playback fails, fall back to default alarm ringtone
