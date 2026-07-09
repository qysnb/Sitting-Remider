## Why

First version testers reported UX issues: permission dialogs repeat on every launch, the countdown display is static, interval setting lacks seconds precision, time pickers don't support direct text input, and the about page version is outdated.

## What Changes

- Permission/battery-optimization dialogs: show only on first launch (persist a dismiss flag)
- Next reminder countdown: fix to decrement in real-time (currently stuck at interval value)
- Reminder interval: add seconds precision; merge interval + delay into one compact row
- Time pickers: support direct numeric text input alongside +/- buttons
- About page: version string updated to v0.2.0

## Capabilities

### New Capabilities

- `direct-input-picker`: Reusable time picker component that supports both button-based and direct text input for hours/minutes/seconds

### Modified Capabilities

- `app-settings`: Reminder interval now supports seconds (h:mm:ss format); interval and delay controls merged into one UI row; countdown display fixes
- `foreground-service`: Permission and battery-optimization dialogs only fire on first launch via SharedPreferences flag
- `about-page`: Version number requirement changes from "v 0.1.0" to "v 0.2.0"

## Impact

- Settings entity: `reminderIntervalMinutes` field changes to `reminderIntervalSeconds` (Long, stored as total seconds). Migration needed.
- `NextReminderDisplay` composable: fix countdown logic to track actual scheduled time instead of recomputing from interval
- `TimePickerGroup` composable: add `TextField` inline editing; merge interval + delay into one row
- `MainActivity.kt`: add `SharedPreferences` check for first-launch dialog tracking
- `AboutDialog.kt`: version string reference updated
