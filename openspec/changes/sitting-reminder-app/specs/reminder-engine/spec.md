## ADDED Requirements

### Requirement: Configurable reminder interval
The system SHALL allow users to configure the reminder interval in hours (0-23) and minutes (0-59) format.

#### Scenario: Set interval to 55 minutes
- **WHEN** user sets interval to 0 hours and 55 minutes
- **THEN** the next stand-up reminder fires after 55 minutes

#### Scenario: Set interval to 1 hour 30 minutes
- **WHEN** user sets interval to 1 hour and 30 minutes
- **THEN** the next stand-up reminder fires after 90 minutes

### Requirement: Configurable sit-back delay
The system SHALL allow users to configure the sit-back reminder delay in minutes (0-59) and seconds (0-59) format, fired after the stand-up reminder.

#### Scenario: Set sit-back delay to 5 minutes
- **WHEN** user sets sit-back delay to 5 minutes and 0 seconds
- **THEN** sit-back reminder fires 5 minutes after stand-up reminder

#### Scenario: Set sit-back delay to 0 minutes
- **WHEN** user sets sit-back delay to 0 minutes and 0 seconds
- **THEN** sit-back reminder fires immediately after stand-up reminder

### Requirement: Time range restriction
The system SHALL only fire reminders within a configurable start and end time window. Outside this window, no reminders fire.

#### Scenario: Reminder inside active window
- **WHEN** current time is 14:00 and the active window is 08:00-23:00
- **THEN** the reminder fires normally

#### Scenario: Reminder outside active window
- **WHEN** current time is 23:30 and the active window is 08:00-23:00
- **THEN** no reminder fires until the window reopens at 08:00

#### Scenario: End time earlier than start time (crosses midnight)
- **WHEN** user sets start time to 22:00 and end time to 06:00
- **THEN** the system treats this as an overnight window spanning midnight

### Requirement: Toggle resets reminder timer
The system SHALL reset the reminder cycle when the master toggle is turned off and back on.

#### Scenario: Toggle off then on resets timer
- **WHEN** user turns the toggle off and then on again
- **THEN** the stand-up reminder fires after the configured interval from the toggle-on time (not from the previously scheduled time)

### Requirement: Reminder cycle state machine
The system SHALL maintain a reminder state machine with states: IDLE, STAND_UP_PENDING, STAND_UP_TRIGGERED, SIT_BACK_PENDING.

#### Scenario: Normal cycle flow
- **WHEN** interval elapses
- **THEN** state transitions from STAND_UP_PENDING to STAND_UP_TRIGGERED, alarm sounds, sit-back timer starts
- **WHEN** sit-back delay elapses
- **THEN** state transitions to SIT_BACK_PENDING, sit-back alarm sounds, then resets to STAND_UP_PENDING for next cycle

### Requirement: State persistence across reboot
The system SHALL persist reminder state and schedule alarms after device reboot using `BOOT_COMPLETED` broadcast receiver.

#### Scenario: Reboot recovery
- **WHEN** device reboots
- **THEN** the system restores reminder state and reschedules any pending alarms
