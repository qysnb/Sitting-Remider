## MODIFIED Requirements

### Requirement: Next reminder display
The system SHALL display the time remaining until the next scheduled reminder, decrementing in real-time every second. The countdown SHALL remain accurate even when the Room database re-emits settings.

#### Scenario: Countdown shows correct time after enabling reminders
- **WHEN** user enables reminders by toggling master ON
- **THEN** the countdown shows the time remaining until the next alarm (e.g., "55分0秒") and does NOT reset to "--" after settings are saved

#### Scenario: Countdown continues after alarm reschedule
- **WHEN** a reminder alarm fires and the service reschedules the next alarm
- **THEN** the countdown continues displaying the time to the newly scheduled alarm without going to "--" or "0分0秒"

### Requirement: Compact time picker editing
The time unit selectors in the interval and delay pickers SHALL support direct numeric text input by tapping on the displayed value.

#### Scenario: Tap compact value to edit
- **WHEN** user taps on the displayed "55" in minutes in the compact (interval/delay) picker
- **THEN** the display switches to a text input field pre-filled with "55"
- **WHEN** user types "30" and presses Enter or taps elsewhere
- **THEN** the value is validated (clamped to 0-59) and display shows "30"

#### Scenario: Out-of-range input in compact mode
- **WHEN** user enters "99" in the minutes field and presses Enter
- **THEN** the value is clamped to 59 and the display shows "59"

## REMOVED Requirements

### Requirement: Interval and delay on same row
**Reason**: Not removed — the compact row layout from v0.2 remains unchanged. This removal entry is a placeholder indicating no layout regression.

### Requirement: Direct text input for time pickers
**Reason**: Not removed — the direct text input requirement is carried forward. This modified requirement replaces it with the fixed click-to-edit interaction.
