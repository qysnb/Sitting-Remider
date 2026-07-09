## ADDED Requirements

### Requirement: Foreground service lifecycle
The system SHALL start a foreground service when the master toggle is ON and stop it when the toggle is OFF.

#### Scenario: Service starts on toggle on
- **WHEN** user turns the master toggle ON
- **THEN** the foreground service starts with a persistent notification

#### Scenario: Service stops on toggle off
- **WHEN** user turns the master toggle OFF
- **THEN** the foreground service stops and cancels all pending alarms

### Requirement: Persistent notification
The foreground service SHALL display a low-priority persistent notification indicating the app is active and monitoring reminders.

#### Scenario: Notification displays service status
- **WHEN** foreground service is running
- **THEN** a notification is shown in the status bar with the text "Reminder active" and the next scheduled reminder time

### Requirement: Alarm delivery via AlarmManager
The system SHALL use `AlarmManager.setExactAndAllowWhileIdle()` to schedule reminder alarms.

#### Scenario: Exact alarm scheduling
- **WHEN** a reminder is scheduled
- **THEN** the system uses `setExactAndAllowWhileIdle()` for precise timing

#### Scenario: Exact alarm permission prompt
- **WHEN** app runs on API 31+ and `SCHEDULE_EXACT_ALARM` is not granted
- **THEN** the system shows a dialog requesting the user to grant the permission in Settings

### Requirement: Boot receiver reschedule
The system SHALL register a `BOOT_COMPLETED` broadcast receiver that restarts the foreground service and reschedules alarms after device boot.

#### Scenario: Auto-start after boot
- **WHEN** device finishes booting and the app's master toggle was ON before shutdown
- **THEN** the foreground service restarts and alarms are rescheduled

### Requirement: Battery optimization exemption guidance
The system SHALL display a prompt on first launch asking the user to disable battery optimization for the app.

#### Scenario: Battery optimization prompt
- **WHEN** user opens the app for the first time and master toggle is ON
- **THEN** a dialog suggests disabling battery optimization with an "Open Settings" button
