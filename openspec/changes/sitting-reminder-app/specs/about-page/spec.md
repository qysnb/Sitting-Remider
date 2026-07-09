## ADDED Requirements

### Requirement: About dialog
The system SHALL provide an "About" button on the main screen that opens a dialog with application information.

#### Scenario: About button opens dialog
- **WHEN** user taps the "About" button
- **THEN** a dialog opens displaying app information

### Requirement: App name display
The system SHALL display the application name "久坐助手" in the About dialog.

#### Scenario: App name shown
- **WHEN** the About dialog is open
- **THEN** "久坐助手" is displayed as the app name

### Requirement: Version number display
The system SHALL display the version number "v 0.1.0" in the About dialog.

#### Scenario: Version shown
- **WHEN** the About dialog is open
- **THEN** "v 0.1.0" is displayed

### Requirement: GitHub link placeholder
The system SHALL display a GitHub link entry in the About dialog, initially set to "TBD" or empty.

#### Scenario: GitHub link shown
- **WHEN** the About dialog is open
- **THEN** a GitHub link section is displayed, currently showing "待定" (TBD)

### Requirement: App description
The system SHALL display a brief description of the app's purpose in the About dialog.

#### Scenario: Description shown
- **WHEN** the About dialog is open
- **THEN** a description explaining the app's purpose (periodic sitting reminders) is displayed

### Requirement: Developer credits
The system SHALL display developer credits "Qysnb with DeepSeek V4 Flash" in the About dialog.

#### Scenario: Developer credits shown
- **WHEN** the About dialog is open
- **THEN** "Qysnb with DeepSeek V4 Flash" is displayed as the developer info

### Requirement: Open source license
The system SHALL display "Apache Licence 2.0" as the open source license in the About dialog.

#### Scenario: License shown
- **WHEN** the About dialog is open
- **THEN** "Apache Licence 2.0" is displayed as the license
