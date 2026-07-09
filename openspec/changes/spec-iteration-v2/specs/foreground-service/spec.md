## MODIFIED Requirements

### Requirement: Battery optimization exemption guidance
The system SHALL display a prompt asking the user to disable battery optimization, but only on the first launch (tracked via a SharedPreferences flag).

#### Scenario: First launch shows prompt
- **WHEN** user opens the app for the first time and master toggle is ON
- **THEN** a dialog suggests disabling battery optimization with an "Open Settings" button
- **THEN** a boolean flag `permissions_dismissed` is saved to SharedPreferences

#### Scenario: Subsequent launches skip prompt
- **WHEN** user opens the app on the second time and `permissions_dismissed` is true
- **THEN** no battery optimization dialog appears

### Requirement: Exact alarm permission prompt
The system SHALL show the SCHEDULE_EXACT_ALARM permission dialog only on first launch, gated by the same SharedPreferences flag.

#### Scenario: First launch shows permission prompt
- **WHEN** app runs on API 31+ for the first time and `SCHEDULE_EXACT_ALARM` is not granted
- **THEN** a dialog requesting the user to grant the permission appears

#### Scenario: Subsequent launches skip prompt
- **WHEN** `permissions_dismissed` is true
- **THEN** the exact alarm permission dialog is not shown
