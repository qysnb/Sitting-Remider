## ADDED Requirements

### Requirement: Settings persistence
The system SHALL persist all user settings using Room database so they survive app restarts.

#### Scenario: Settings restored after restart
- **WHEN** user sets interval to 45 minutes and restarts the app
- **THEN** the interval field shows 45 minutes

### Requirement: Master toggle
The system SHALL provide a master toggle switch on the main screen to enable or disable the entire reminder system.

#### Scenario: Toggle on enables reminders
- **WHEN** user flips the master toggle to ON
- **THEN** the reminder engine starts scheduling alarms

#### Scenario: Toggle off disables reminders
- **WHEN** user flips the master toggle to OFF
- **THEN** all pending alarms are canceled and the foreground service stops

### Requirement: Reminder interval setting
The system SHALL provide hour (0-23) and minute (0-59) number pickers for the reminder interval.

#### Scenario: Set interval
- **WHEN** user adjusts the interval pickers
- **THEN** the value is saved and applied to the next reminder cycle

### Requirement: Sit-back delay setting
The system SHALL provide minute (0-59) and second (0-59) number pickers for the sit-back delay.

#### Scenario: Set sit-back delay
- **WHEN** user adjusts the sit-back delay pickers
- **THEN** the value is saved and applied to the sit-back reminder timing

### Requirement: Start time setting
The system SHALL provide hour (0-23) and minute (0-59) pickers to set the daily start time for reminders.

#### Scenario: Set start time to 08:00
- **WHEN** user sets start time to 8:00
- **THEN** reminders will not fire before 08:00

### Requirement: End time setting
The system SHALL provide hour (0-23) and minute (0-59) pickers to set the daily end time for reminders.

#### Scenario: Set end time to 23:00
- **WHEN** user sets end time to 23:00
- **THEN** reminders will not fire after 23:00

### Requirement: Settings validation
The system SHALL validate that the end time is after the start time (or handle overnight wrapping) and provide feedback if settings are invalid.

#### Scenario: Invalid time range
- **WHEN** user sets start time to 23:00 and end time to 08:00 (non-wrapping)
- **THEN** the system treats it as an overnight window spanning midnight

### Requirement: Next reminder display
The system SHALL display the time remaining until the next scheduled reminder on the main screen.

#### Scenario: Display next reminder time
- **WHEN** the reminder engine is active
- **THEN** the main screen shows "Next reminder in X minutes" or the exact time
