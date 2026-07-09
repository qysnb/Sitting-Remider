## ADDED Requirements

### Requirement: Default alarm ringtone
The system SHALL use the Android default alarm ringtone as the default sound for reminders.

#### Scenario: First launch default
- **WHEN** user opens the app for the first time
- **THEN** the default alarm ringtone is selected as the reminder sound

### Requirement: Custom ringtone selection
The system SHALL allow users to pick an audio file from device storage as the reminder ringtone using Android's system file picker.

#### Scenario: Select audio file
- **WHEN** user taps "Select Ringtone"
- **THEN** system opens `ACTION_OPEN_DOCUMENT` filtered to audio files
- **WHEN** user picks an audio file
- **THEN** the selected URI is saved as the reminder ringtone

#### Scenario: Ringtone access permission
- **WHEN** app targets API 33+ and user picks a ringtone
- **THEN** system requests `READ_MEDIA_AUDIO` permission automatically via the document picker

### Requirement: Test playback button
The system SHALL provide a button to test the selected ringtone by playing it for 2 seconds.

#### Scenario: Test plays for 2 seconds
- **WHEN** user taps the test playback button
- **THEN** the selected ringtone plays for 2 seconds and then stops

#### Scenario: Test with no ringtone selected
- **WHEN** user taps test playback but no ringtone is selected
- **THEN** the system plays the default alarm ringtone for 2 seconds

### Requirement: Audio player cleanup
The system SHALL release audio resources when playback completes or the app is destroyed.

#### Scenario: Resource release after playback
- **WHEN** test playback ends (2 seconds elapsed)
- **THEN** MediaPlayer resources are released
