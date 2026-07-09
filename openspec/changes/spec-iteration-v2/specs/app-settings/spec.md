## MODIFIED Requirements

### Requirement: Reminder interval setting
The system SHALL allow users to configure the reminder interval in hours (0-23), minutes (0-59), and seconds (0-59) format, stored as total seconds.

#### Scenario: Set interval to 55 minutes
- **WHEN** user sets interval to 0 hours, 55 minutes, 0 seconds
- **THEN** the system stores 3300 seconds and the next stand-up reminder fires after 3300 seconds

#### Scenario: Set interval to 1 hour 30 minutes 30 seconds
- **WHEN** user sets interval to 1 hour, 30 minutes, 30 seconds
- **THEN** the system stores 5430 seconds

### Requirement: Next reminder display
The system SHALL display the time remaining until the next scheduled reminder, decrementing in real-time every second.

#### Scenario: Countdown decrements correctly
- **WHEN** the reminder engine is active and the next alarm is scheduled for 55 minutes from now
- **THEN** the main screen shows "55分0秒" and after 5 seconds shows "54分55秒"

#### Scenario: Countdown reaches zero
- **WHEN** the countdown reaches 0 seconds
- **THEN** the display shows "0分0秒" until the next alarm is scheduled

## ADDED Requirements

### Requirement: Interval and delay on same row
The reminder interval and sit-back delay pickers SHALL be displayed in a single compact row with smaller font size, rather than stacked vertically.

#### Scenario: Compact layout
- **WHEN** user opens the settings panel
- **THEN** the interval and delay pickers are on the same horizontal row with label font reduced to `bodyMedium`

### Requirement: Direct text input for time pickers
Each time unit selector (hours, minutes, seconds) SHALL support direct numeric text input by tapping on the displayed value, switching to a TextField for manual entry.

#### Scenario: Tap to edit
- **WHEN** user taps on the displayed "55" in minutes
- **THEN** the display switches to a text input field pre-filled with "55"
- **WHEN** user types "30" and presses Enter or taps elsewhere
- **THEN** the value is validated (clamped to 0-59) and the display shows "30"

#### Scenario: Out-of-range input
- **WHEN** user enters "99" in the minutes field and presses Enter
- **THEN** the value is clamped to 59 and the display shows "59"
