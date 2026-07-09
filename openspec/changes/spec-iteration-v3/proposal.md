## Why

v0.2.0 shipped with several bugs and UX gaps found during testing. The countdown display breaks when Room re-emits settings, the ringtone picker opens a file browser instead of the system ringtone gallery, time pickers in compact mode are read-only because the Surface has no click handler, and the ringtone card layout is inconsistent with other cards.

## What Changes

1. **Fix countdown reset** — `MainViewModel.observeSettings()` recreates `UiState` without preserving `nextTriggerTimeMillis`, so Room flow emissions reset it to `null`. Add `nextTriggerTimeMillis` preservation.
2. **Systems ringtone picker** — Replace `ActivityResultContracts.OpenDocument()` with `RingtoneManager.ACTION_RINGTONE_PICKER` intent so the user sees the system ringtone gallery instead of a file browser.
3. **Ringtone card UI alignment** — Make the ringtone card's internal layout `fillMaxWidth()` and align button widths to match the module width.
4. **Fix compact time picker editing** — The `TimeSelector` Surface in compact mode has no `onClick` handler to toggle `isEditing`, making the displayed values read-only. Add click-to-edit.

## Capabilities

### New Capabilities
- _(none)_ — All changes are modifications to existing capabilities.

### Modified Capabilities
- `app-settings`: Fix countdown display bug (preserve `nextTriggerTimeMillis` in Room flow). Make interval/delay editable in compact mode by adding click handler to `TimeSelector` Surface.
- `ringtone-picker`: Switch to system ringtone picker intent. Align card UI layout width.

## Impact

- **MainViewModel.kt** — Two-line fix: preserve `nextTriggerTimeMillis` in `observeSettings()` when Room re-emits.
- **TimePickerGroup.kt** — One-line fix: add `clickable` / `onClick` to the Surface in `TimeSelector` to toggle `isEditing = true`.
- **RingtoneSelector.kt** — Replace `OpenDocument` launcher with `ACTION_RINGTONE_PICKER` intent; align `Row` and buttons to `fillMaxWidth()`.
- **MainScreen.kt** — Ensure ringtone card content uses `Modifier.fillMaxWidth()`.
