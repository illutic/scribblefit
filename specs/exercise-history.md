# Feature Specification: Exercise History (Tracked Sessions)

**Feature Branch**: `exercise-history`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "The Exercise History screen provides a chronological log of every session where the user performed a specific exercise. It allows for a detailed review of past sets, weight, and repetitions, providing the historical context necessary for tracking long-term progression."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Chronological History (Priority: P1)

As a User, I want to see a full list of my past performances for an exercise so that I can see exactly what I lifted and when.

**Why this priority**: It is the core functional premise of the feature to see a historical list.

**Independent Test**: Can be fully tested by opening the history view for a specific exercise and checking the chronologically sorted sessions.

**Acceptance Scenarios**:

1. **Given** I am on the Exercise Details screen, **When** I tap the "View all [X] tracked sessions" row, **Then** I am navigated to the Exercise History screen showing a list sorted by date (newest first).
2. **Given** I am viewing the history, **Then** I should see sticky date headers (e.g., "OCTOBER 2025", "SEPTEMBER 2025") grouping the sessions.
3. **Given** an exercise has no history, **When** I navigate to the history screen, **Then** I see clear messaging that there is no history.

---

### User Story 2 - View Session Details (Priority: P1)

As a User, I want to see the sets, reps, and volume for each historical session so that I can compare them to my current performance.

**Why this priority**: Without performance details, the history view provides no actionable context.

**Independent Test**: Can be fully tested by viewing the rows in the history list.

**Acceptance Scenarios**:

1. **Given** I am viewing a session row, **Then** I see the Date, Day of the week, total volume, and a summarized set/rep string (e.g., "100 kg x 3, 3, 3").

---

### User Story 3 - Highlight Personal Records (Priority: P2)

As a User, I want to see my progress markers (e.g., a "PB" or "PR" badge) next to specific historical entries so that I can celebrate my records.

**Why this priority**: Motivation is secondary to the core functionality of tracking history.

**Independent Test**: Can be tested by adding a session with a maximum weight or volume and ensuring the badge displays.

**Acceptance Scenarios**:

1. **Given** a session where a personal best was achieved, **Then** a "Record" badge (e.g., gold star or "PB" text) is displayed on that session's row item.

---

### User Story 4 - Navigate to Full Workout Context (Priority: P2)

As a User, I want to tap on a historical session so that I can see the full workout context (the scribble it belonged to).

**Why this priority**: Helpful for deeper analysis but not strictly required for the core historical view.

**Independent Test**: Can be fully tested by tapping a row item and verifying navigation occurs.

**Acceptance Scenarios**:

1. **Given** I am viewing a session row, **When** I tap it, **Then** I navigate to the Scribble Details screen for the associated workout.

### Edge Cases

- What happens when an exercise has no recorded history?
- What happens when a user scrolls through a massive list of hundreds of past sessions?
- What happens when a session contains no sets/reps but was recorded as part of a scribble?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide navigation via "View all [X] tracked sessions" row in Exercise Details screen.
- **FR-002**: System MUST display sessions grouped by month and sorted chronologically (newest first).
- **FR-003**: System MUST display for each session: Date, Day of the week, total volume, and summarized set/rep string.
- **FR-004**: System MUST calculate and display a PB (Personal Best) badge if the session contains the maximum weight/volume.
- **FR-005**: System MUST navigate to Scribble Details screen when a session row is tapped.
- **FR-006**: System MUST use contextual UI splitting on Android/iOS (Header with back button/exercise name/session count, Body with scrollable list/sticky headers, Footer with Bottom Navigation).
- **FR-007**: System MUST use MVI architecture on Android (100% Jetpack Compose, Hilt) with `GetExerciseHistoryUseCase` and Room `@Transaction` queries.
- **FR-008**: System MUST use MVI architecture on iOS (100% SwiftUI) with `GetExerciseHistoryUseCase` (Swift Actor) and SwiftData queries matching the `canonicalName`.

### Key Entities *(include if feature involves data)*

- **Exercise**: Represents a historically performed instance of a movement, including sets, reps, and weight.
- **Scribble**: Represents the full workout context metadata that the exercise is associated with.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Unit tests verify correct sorting and grouping of sessions by month/year.
- **SC-002**: Unit tests verify PB identification logic correctly identifies sessions containing max weight/volume.
- **SC-003**: UI tests verify list scrolling performance is smooth.
- **SC-004**: UI tests verify sticky header behavior functions correctly.
- **SC-005**: UI tests verify navigation to `Scribble Details` works correctly on row tap.

## Assumptions

- Users will only access this screen if an exercise generally has history, though an empty state should be handled.
- The UI structure will use contextual UI splitting components (Header, Body, Footer) across platforms.
