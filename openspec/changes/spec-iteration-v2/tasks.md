## 1. Database Schema: Interval → Seconds

- [x] 1.1 Rename `reminderIntervalMinutes` to `reminderIntervalSeconds` in Settings.kt entity (keep `id = 1`), update default from `55` (minutes) to `3300` (seconds)
- [x] 2.2 Update `SettingsDao.kt` and `SettingsRepository.kt` — no API changes needed since they use the entity directly
- [x] 1.3 Update `MainViewModel.kt`: change `UiState.reminderIntervalMinutes: Long` to `UiState.reminderIntervalSeconds: Long`, update default and mapping from entity

## 2. Countdown Fix: Real-Time Decrement

- [x] 2.1 Add `nextTriggerTimeMillis: Long?` to `UiState` in `MainViewModel.kt`
- [x] 2.2 Rewrite `NextReminderDisplay.kt`: accept `nextTriggerTimeMillis` instead of `reminderIntervalMinutes`; compute delta from that fixed timestamp in the `LaunchedEffect` loop
- [x] 2.3 Update `MainScreen.kt` to pass `nextTriggerTimeMillis` from state to `NextReminderDisplay`
- [x] 2.4 Update `ReminderService.kt`: broadcast the scheduled alarm time back to the ViewModel (via a PendingIntent extra or a shared data channel) so the countdown shows the correct remaining time

## 3. TimePicker: Direct Text Input

- [x] 3.1 Add editing state to `TimeSelector` composable: `isEditing` boolean, toggled by tapping the value display
- [x] 3.2 When editing, show a `TextField` pre-filled with the current value; on done/focus-loss, validate and clamp to range
- [x] 3.3 Add compact `valueStyle` param to `TimeSelector` for use in the merged row (smaller font)

## 4. UI: Merge Interval & Delay Row

- [x] 4.1 Update `TimePickerGroup.kt`: accept total-seconds for interval (valueSeconds instead of valueMinutes); always show seconds picker for interval
- [x] 4.2 Rewrite the settings card in `MainScreen.kt`: place interval and delay TimePickerGroups side-by-side in a Row with `Modifier.weight(1f)` and smaller labels
- [x] 4.3 Update `MainViewModel.kt` to use `reminderIntervalSeconds` (Long) for the interval update function

## 5. Permission Dialogs: First-Launch Only

- [x] 5.1 Add SharedPreferences check in `MainActivity.kt`: read `permissions_dismissed` flag; gate both battery and exact-alarm dialogs behind it
- [x] 5.2 On first dialog dismiss, write `permissions_dismissed = true` to SharedPreferences

## 6. About Page: Version Bump

- [x] 6.1 Change version string in `AboutDialog.kt` from "v 0.1.0" to "v 0.2.0"
- [x] 6.2 Update `strings.xml` app_version value to "v 0.2.0"

## 7. Build & Verify

- [ ] 7.1 Build `assembleDebug` and fix any compilation errors
- [ ] 7.2 Verify countdown decrements correctly on device
- [ ] 7.3 Verify interval set to 0h0m30s fires the alarm after 30 seconds
- [ ] 7.4 Verify permission dialogs only show on first launch
- [x] 7.5 Verify About page shows v0.2.0
