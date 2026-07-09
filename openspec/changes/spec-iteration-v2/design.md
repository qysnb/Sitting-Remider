## Context

v0.1.0 shipped with several UX issues identified during testing. The changes are isolated to the UI layer and the database schema, with no architectural changes to the reminder engine or foreground service.

## Goals / Non-Goals

**Goals:**
- Fix static countdown display to show real-time decrementing
- Add seconds precision to reminder interval; merge interval + delay pickers into one row
- Add direct text input to time pickers as an alternative to +/- buttons
- Suppress permission/battery dialogs after first dismissal
- Bump About dialog version to v0.2.0

**Non-Goals:**
- No changes to AlarmManager scheduling logic
- No changes to foreground service lifecycle
- No Room migration for existing users (schema version stays at 1; new users get the updated schema)

## Decisions

1. **Countdown fix: store next trigger time** — The current bug is that `NextReminderDisplay` recomputes from `Calendar.getInstance()` every second, always adding `reminderIntervalMinutes` from "now". Fix: have the ViewModel expose a `nextTriggerTime` (long, millis) that's set when the service schedules the alarm. The composable counts down from that fixed timestamp.

2. **Schema change: `reminderIntervalMinutes` → `reminderIntervalSeconds`** — Store interval as total seconds (Long) for consistency with `sitBackDelaySeconds`. Breaking change but no production data exists yet.

3. **Direct input via TextField** — Each `TimeSelector` unit (hours/minutes/seconds) gets an optional `isEditing` state. By default it shows the labeled display. On tap it switches to a `TextField` for direct numeric entry. On focus loss or Enter it validates and switches back.

4. **First-launch dialog tracking via SharedPreferences** — A boolean flag `permissions_dismissed` is stored in `SharedPreferences`. Checked at startup; if true, all permission dialogs are skipped. This is simpler than extending the Room schema.

## Risks / Trade-offs

- **Direct text input validation** → User could enter non-numeric or out-of-range values. Mitigation: clamp to range on focus loss and show inline error on invalid input.
- **Countdown drift** → If the device clock changes, the countdown could be wrong. Acceptable for v0.2; the alarm itself relies on AlarmManager which handles clock changes.
- **SharedPreferences vs Room for flag** → Two persistence mechanisms is slightly messy, but SharedPreferences is zero-setup and appropriate for a single boolean flag.
