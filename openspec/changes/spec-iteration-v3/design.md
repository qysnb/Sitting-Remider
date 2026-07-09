## Context

v0.2.0 shipped four bugs/UX gaps. Three are single-line fixes in the UI layer; the countdown fix requires preserving `nextTriggerTimeMillis` across Room flow re-emissions. No new architectural patterns, dependencies, or data model changes needed.

All changes are isolated to:
- `MainViewModel.kt` — preserve transient UI state across Room flow
- `TimePickerGroup.kt` — add click handler to compact Surface
- `RingtoneSelector.kt` — switch intent + layout alignment
- `MainScreen.kt` — ringtone card width alignment

## Goals / Non-Goals

**Goals:**
- Countdown shows correct remaining time after enabling reminders and after alarm reschedules
- Tapping a compact time selector value switches to edit mode
- "选择铃声" opens the system ringtone gallery (not file browser)
- Ringtone card buttons/width match other cards

**Non-Goals:**
- No changes to Room schema, database version, or migration
- No changes to AlarmManager, foreground service, or permission dialogs
- No new dependencies or libraries

## Decisions

1. **Preserve `nextTriggerTimeMillis` in Room flow** — The simplest fix: in `observeSettings().collect`, after creating the new `UiState`, copy over `nextTriggerTimeMillis` from the current state. This prevents the Room Flow from wiping the transient value. Alternative considered: moving `nextTriggerTimeMillis` to a separate `StateFlow` — overkill for one field.

2. **Click-to-edit on compact Surface** — Add `.clickable { isEditing = true }` to the Surface in `TimeSelector` when `compact` is true. No alternative needed — this is the missing interaction.

3. **`ACTION_RINGTONE_PICKER`** — Use `ActivityResultContracts.StartActivityForResult` with `RingtoneManager.ACTION_RINGTONE_PICKER` intent. This shows the system ringtone list. The result URI is passed back directly via intent data. Alternative considered: `RingtoneManager.getRingtoneList()` with custom picker — unnecessary when the system picker exists.

4. **Card width alignment** — Ensure `RingtoneSelector` uses `Modifier.fillMaxWidth()` and the button `Row` uses `Arrangement.SpaceEvenly` or `fillMaxWidth()` so buttons stretch to card width.

## Risks / Trade-offs

- **ACTION_RINGTONE_PICKER on different OEMs** — Some manufacturers (Xiaomi, OPPO) may show a limited or customized ringtone list. Fallback: continue using `RingtoneManager.getDefaultUri()` as default. Acceptable — all Android devices support this intent.
- **Countdown still drifts after alarm fires** — If the service reschedules with a different delay than the ViewModel's `nextTriggerTimeMillis`, the countdown will jump. Mitigation: add a mechanism (e.g., `LocalBroadcastManager`) for the service to send the actual alarm time back. Out of scope for this fix; the current fix at least prevents the null reset.
