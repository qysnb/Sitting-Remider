## MODIFIED Requirements

### Requirement: Ringtone selection via system picker
The system SHALL open the Android system ringtone picker (showing built-in alarm ringtones, notification sounds, and custom ringtones registered with the media store) when the user taps "选择铃声", instead of opening a general file browser.

#### Scenario: Open system ringtone picker
- **WHEN** user taps "选择铃声" button
- **THEN** the system ringtone picker dialog opens showing the list of available alarm/notification ringtones
- **WHEN** user selects a ringtone from the picker
- **THEN** the selected ringtone URI is stored and its display name is shown

#### Scenario: Ringtone picker cancel
- **WHEN** user opens the ringtone picker and taps cancel or back
- **THEN** the previously selected ringtone (or default) remains unchanged

### Requirement: Ringtone card layout alignment
The ringtone settings card SHALL have the same internal width alignment as other setting cards. The "选择铃声" and "测试" buttons SHALL be distributed to fill the available card width evenly.

#### Scenario: Buttons fill card width
- **WHEN** user views the ringtone settings card
- **THEN** the "选择铃声" and "测试" buttons fill the width of the card evenly, consistent with other setting cards

## ADDED Requirements

### Requirement: Pre-selected ringtone in picker
The ringtone picker SHALL pre-select the currently active ringtone when opened.

#### Scenario: Current ringtone highlighted
- **WHEN** user opens the ringtone picker and a ringtone is already configured
- **THEN** that ringtone is pre-selected/highlighted in the picker dialog
