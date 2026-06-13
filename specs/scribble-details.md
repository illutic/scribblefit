# Feature Specification: Scribble Details

**Feature Branch**: `feature/scribble-details`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "The Scribble Details screen provides a comprehensive view of a single training session (a Scribble). It acts as the final step in the 'Scribble -> Parse -> Review' flow, where users can fine-tune parsed data before officially logging the session, and also serves as a History View."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Review & Adjust Parsed Data (Priority: P1)

As a User, I want to review the exercises and sets parsed from my scribble and adjust weights/reps so that my logs accurately reflect what I lifted.

**Why this priority**: The core purpose of the screen is data accuracy and verifying the AI's output before finalizing the log.

**Independent Test**: Can be fully tested by loading a parsed scribble, verifying the visual display of exercises/sets, and adjusting a set's weight/reps to ensure updates reflect in the UI.

**Acceptance Scenarios**:

1. **Given** a parsed scribble, **When** I open the details screen, **Then** I see the session date, summary stats, and a list of exercises with their summaries.
2. **Given** a listed exercise, **When** I tap on the exercise card, **Then** I am navigated to the Exercise Details for fine-tuning.

---

### User Story 2 - Confirm & Log Session (Priority: P1)

As a User, I want to "Confirm & Log" my scribble so that it is saved to my permanent history and contributes to my insights.

**Why this priority**: Without this capability, the session remains incomplete and cannot be permanently recorded.

**Independent Test**: Can be fully tested by reviewing a session and tapping "Confirm & Log", verifying the status change and navigation.

**Acceptance Scenarios**:

1. **Given** a scribble with at least one valid exercise and set, **When** I tap "Confirm & Log", **Then** the scribble status updates to `COMPLETED` and I am navigated to the Ledger or Canvas.

---

### User Story 3 - Add/Remove Sets & Exercises (Priority: P2)

As a User, I want to add or remove sets, add new exercises, or delete an entire exercise from the session so that I can correct parsing errors or reflect last-minute changes.

**Why this priority**: Important for structural corrections to the data that simple adjustments cannot fix.

**Independent Test**: Can be tested by manually adding a new exercise, adding a set to an existing exercise, and deleting an entire exercise.

**Acceptance Scenarios**:

1. **Given** a session, **When** I tap to create a new exercise, **Then** a new exercise block is added to the session.
2. **Given** an exercise I want to remove, **When** I delete the exercise, **Then** it is completely removed from the session and UI.

---

### User Story 4 - Session Notes (Priority: P3)

As a User, I want to add session-wide notes so that I can record how I felt, injuries, or specific focus areas.

**Why this priority**: Qualitative feedback is valuable but not as critical as the quantitative exercise data.

**Independent Test**: Can be tested by entering text into the session notes area and verifying it saves with the scribble.

**Acceptance Scenarios**:

1. **Given** the scribble details screen, **When** I enter text into the "SESSION NOTES" multiline text area, **Then** the notes are persisted with the session data.

---

### Edge Cases

- What happens when the user deletes all exercises from the session? (e.g., The "Confirm & Log" button should become disabled).
- How does the system handle a session with 0 valid sets? (e.g., Prevention of logging empty sessions).
- What happens if the user navigates back without confirming? (Are changes auto-saved as drafts or discarded?)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display a Back Button to return to the previous screen (Canvas or Ledger).
- **FR-002**: System MUST display the Session Date (Format: "Monday, October 23") and Summary Stats Row (EXERCISES count, SETS count, and VOLUME total as StatCards).
- **FR-003**: System MUST display a list of exercises in glassmorphism cards matching canvas visual style.
- **FR-004**: System MUST show the Exercise Name (Bold 28sp, -1 kerning), Formatted Summary (e.g., "80.0 kg * 3 sets * 10 reps"), and Stats (EST. 1RM and INTENSITY) on each exercise card.
- **FR-005**: System MUST navigate to Exercise Details when an exercise card is tapped.
- **FR-006**: System MUST perform Atomic Updates when saving changes (1. Clear existing `Set` entries, 2. Clear existing `Exercise` entries, 3. Re-insert updated structure).
- **FR-007**: System MUST persist all changes to the `Scribble`, `Exercise`, and `Set` tables.
- **FR-008**: System MUST provide a prominent "Create New Exercise" button.
- **FR-009**: System MUST provide a "SESSION NOTES" multiline text area at the bottom of the list.
- **FR-010**: System MUST provide a "Confirm & Log" primary action button (`#2b8cee`) that updates status to `COMPLETED`.

### Key Entities *(include if feature involves data)*

- **Scribble**: Represents the training session root entity. Tracks status (e.g., `COMPLETED`) and session notes.
- **Exercise**: Represents a single exercise block within a Scribble.
- **Set**: Represents individual sets (weight, reps) within an Exercise.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully review and confirm a parsed session without data loss.
- **SC-002**: The "Confirm & Log" button is enabled only when at least one exercise with one valid set exists.
- **SC-003**: All database updates maintain data integrity through the defined atomic update pattern.
- **SC-004**: Adding/removing sets and exercises updates the state correctly in unit tests.

## Assumptions

- Assumption: The AI parsing phase has already successfully populated the initial Scribble, Exercise, and Set data before this screen is accessed.
- Assumption: Android development will follow MVI architecture with 100% Jetpack Compose and Hilt, using `LazyColumn` for performance.
- Assumption: iOS development will follow MVI architecture with Swift 6 strict concurrency, `@MainActor` state updates, and 100% SwiftUI with `.glassEffect()`.
- Assumption: "Confirm & Log" will transition the user away from this screen (back to Canvas or forward to Ledger).
