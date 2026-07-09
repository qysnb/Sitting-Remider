## ADDED Requirements

### Requirement: Editable time selector
The system SHALL provide a reusable time selector component that supports both +/- button adjustment and direct numeric text input.

#### Scenario: Default display mode
- **WHEN** the time selector renders
- **THEN** it displays the current value in a styled surface with +/- buttons above and below

#### Scenario: Tap-to-edit
- **WHEN** user taps on the displayed number
- **THEN** the display switches to a TextField with the current value pre-filled

#### Scenario: Confirm edit
- **WHEN** user types a new value and presses Enter or the field loses focus
- **THEN** the value is validated, clamped to the configured range (e.g., 0-59 for minutes), and the display mode resumes

#### Scenario: Cancel edit
- **WHEN** user taps outside the TextField without completing input
- **THEN** the original value is restored

### Requirement: Value validation
The system SHALL validate direct text input by clamping values to the configured integer range.

#### Scenario: Below minimum
- **WHEN** user enters "-5" for minutes
- **THEN** the value is clamped to 0

#### Scenario: Above maximum
- **WHEN** user enters "99" for minutes
- **THEN** the value is clamped to 59
