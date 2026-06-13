# Feature Specification: Canvas

**Feature Branch**: `canvas-feature`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "Convert legacy canvas specification into the new spec-kit format."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Rapid Plain Text Data Entry (Priority: P1)

As a fitness enthusiast, I want to quickly type my training data in plain text so that I don't spend time navigating complex menus during my session.

**Why this priority**: The fundamental value proposition of ScribbleFit is low-friction entry of workout data without menu-diving.

**Independent Test**: Can be fully tested by opening the app without network access, typing a text scribble, and verifying it is saved and shown in the UI immediately as pending.

**Acceptance Scenarios**:

1. **Given** the Canvas screen is open, **When** the user enters a plain text scribble, **Then** the scribble is stored locally as PENDING immediately, even if offline.
2. **Given** a parsed scribble, **When** the user taps the confirm button, **Then** the scribble status transitions to COMPLETED.

---

### User Story 2 - Scribble Status Tracking (Priority: P2)

As a user, I want to see the parsing status of my scribbles so that I know when they have been successfully processed.

**Why this priority**: Users need visual feedback on whether the system's AI successfully understood and structured their raw input.

**Independent Test**: Can be tested by observing the status badges on a newly entered scribble transitioning from PENDING -> PARSING -> SUCCESS/FAILED.

**Acceptance Scenarios**:

1. **Given** a PENDING scribble, **When** the AI parser processes it successfully, **Then** it updates reactively to show structured exercise data and a Parsed state.
2. **Given** a parsed scribble, **When** the user taps it, **Then** a confirmation sheet opens allowing editing or deletion.

---

### User Story 3 - Date Navigation & Review (Priority: P2)

As a user, I want to navigate to previous days so that I can review my past training logs.

**Why this priority**: Reviewing past workouts is essential for fitness tracking and progression.

**Independent Test**: Can be tested by using the date navigation controls and verifying the scribbles list updates to show only that specific date's entries.

**Acceptance Scenarios**:

1. **Given** the Canvas screen, **When** the user taps the previous day chevron, **Then** the screen displays scribbles for the previous day.
2. **Given** the Canvas screen, **When** the user tries to navigate to a future date, **Then** navigation is prevented.
3. **Given** the Canvas screen, **When** the user taps the date text, **Then** a native Date Picker opens for quick selection.

---

### User Story 4 - AI Training Insights (Priority: P3)

As a user, I want to receive AI-generated insights about my training patterns directly on the Canvas so that I can stay motivated and informed.

**Why this priority**: Enhances the user experience with smart insights but is secondary to the core logging functionality.

**Independent Test**: Can be tested by verifying motivational summaries appear correctly when the `GetAIOverviewUseCase` yields data.

**Acceptance Scenarios**:

1. **Given** a history of logged sessions, **When** the Canvas loads, **Then** motivational summaries or patterns are displayed at the top of the body.

---

### Edge Cases

- What happens when the LLM parsing fails due to completely unrecognized text or empty strings?
- How does the system handle rapid sequential additions of scribbles while the device is offline?
- What happens when a user attempts backward date navigation exceeding the 30-day limit?
- How does the UI gracefully handle very long plain text scribbles?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support offline-first raw text scribble entry and immediately persist it to the local database.
- **FR-002**: System MUST transition scribbles through specific lifecycle statuses: `PENDING`, `PARSING`, `SUCCESS`, `FAILED`, and `COMPLETED`.
- **FR-003**: System MUST associate scribbles with a specific date and restrict the view to a single date at a time.
- **FR-004**: System MUST allow date navigation up to 30 days backward and prevent future date navigation.
- **FR-005**: System MUST provide reactive, real-time updates to the UI as scribble statuses change.
- **FR-006**: System MUST parse natural language scribbles into structured exercise data via AI integration.
- **FR-007**: System MUST provide a confirmation sheet to Confirm, Edit, or Delete parsed exercises.
- **FR-008**: System MUST display weight units in `kg` or `lbs` based on user preferences.
- **FR-009**: System MUST strictly adhere to the defined "The Input Canvas (Home)" design tokens (Inter font, 12dp roundness, #2b8cee primary color).

### Key Entities

- **Scribble**: Represents a user's training session entry. Key attributes include raw text, parsed structure (exercises/sets), date, and lifecycle status.
- **Exercise**: Structured sub-entity linked to a scribble. Can be individually navigated to or edited within the UI.
- **Set**: Granular records of reps and weights, linked directly to an Exercise.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully log a plain text workout offline in under 10 seconds.
- **SC-002**: AI parsing succeeds (`SUCCESS` status) for >90% of reasonable fitness scribbles.
- **SC-003**: 100% of scribbles correctly persist locally regardless of network connectivity.

## Assumptions

- **Architecture**: MVI architecture strictly enforced on both iOS and Android. Zero business logic resides in ViewModels/Stores.
- **Persistence**: Relational local database (Room for Android, SwiftData for iOS) handles all offline-first capabilities.
- **LLM Capabilities**: The integrated AI engine (`RoutingLLMService` / `LLMEngine`) is capable of extracting exercises, sets, reps, and weights accurately from raw text.
- **Theme Parity**: Light/Dark mode parity will be strictly maintained across both platforms.
