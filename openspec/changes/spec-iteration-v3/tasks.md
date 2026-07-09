## 1. Fix Countdown Reset

- [x] 1.1 In `MainViewModel.kt`, add `nextTriggerTimeMillis` preservation: when Room flow emits, copy `_uiState.value.nextTriggerTimeMillis` into the new UiState after collect
- [x] 1.2 In `MainViewModel.kt`, verify `updateReminderInterval()` and `toggleMaster()` set `nextTriggerTimeMillis` before Room flow resubscribes overwrites it — ensure Room flow handler preserves transient fields

## 2. Fix Compact Time Picker Editing

- [x] 2.1 In `TimePickerGroup.kt`, add `Modifier.clickable { isEditing = true }` to the compact Surface in `TimeSelector` so tapping the displayed value switches to edit mode

## 3. Switch to System Ringtone Picker

- [x] 3.1 In `RingtoneSelector.kt`, replace `ActivityResultContracts.OpenDocument()` with `ActivityResultContracts.StartActivityForResult()` using `RingtoneManager.ACTION_RINGTONE_PICKER`
- [x] 3.2 Extract the selected ringtone URI from the intent result extras (`RingtoneManager.EXTRA_RINGTONE_PICKED_URI`)
- [x] 3.3 Set `RingtoneManager.EXTRA_RINGTONE_EXISTING_URI` in the intent to pre-select the currently configured ringtone
- [x] 3.4 Remove `takePersistableUriPermission` call (no longer needed with system picker)

## 4. Align Ringtone Card Width

- [x] 4.1 In `RingtoneSelector.kt`, add `Modifier.fillMaxWidth()` to the root Column
- [x] 4.2 Change button Row arrangement to `Arrangement.SpaceEvenly` or add `Modifier.fillMaxWidth()` so buttons distribute evenly across card width

## 5. Build & Verify

- [x] 5.1 Build `assembleDebug` and fix any compilation errors
- [ ] 5.2 Verify countdown stays accurate after enabling reminders (no "--" flicker) — needs device
- [ ] 5.3 Verify tapping compact time picker value enters edit mode — needs device
- [ ] 5.4 Verify "选择铃声" opens system ringtone picker (not file browser) — needs device
- [ ] 5.5 Verify ringtone card buttons fill width evenly — needs device
