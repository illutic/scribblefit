# Feature Specification: Manual Mode (Direct Exercise Entry)

**Feature Branch**: `[###-manual-mode]`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "Manual Mode provides an alternative training data entry method that bypasses the AI scribble parser entirely."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Toggle Manual Mode (Priority: P1)

As a User, I want to toggle Manual Mode in Settings so that I can choose between AI-parsed scribbles and direct exercise entry.

**Why this priority**: It is the core mechanism that allows the user to access the feature and switch out of AI mode.

**Independent Test**: Can be fully tested by navigating to Settings, toggling the switch, and verifying that the Canvas screen updates its UI immediately without requiring an app restart.

**Acceptance Scenarios**:

1. **Given** the app is in the default state (AI Mode), **When** the user navigates to Settings > General and toggles Manual Mode to ON, **Then** the preference is saved and the Canvas screen hides the freeform text input.
2. **Given** Manual Mode is ON, **When** the user views the Canvas screen, **Then** an "Add Exercise" button is displayed in place of the freeform input bar.

---

### User Story 2 - Add Exercise via Structured Form (Priority: P1)

As a User, I want to add exercises via a structured form so that I can specify exercise name, muscle group, multiple sets (weight × reps), and optional notes precisely.

**Why this priority**: This is the primary functional value of the feature, allowing data entry without relying on AI parsing.

**Independent Test**: Can be fully tested by tapping "Add Exercise" and filling out the form to ensure a structured entry is created successfully.

**Acceptance Scenarios**:

1. **Given** the Canvas screen in Manual Mode, **When** the user taps the "Add Exercise" button, **Then** the Add Exercise Bottom Sheet opens.
2. **Given** the Add Exercise Bottom Sheet is open, **When** the user inputs exercise details, adds multiple sets, and taps Save, **Then** a `Scribble` object is created with status `SUCCESS`.

---

### User Story 3 - Unified Data Pipeline (Priority: P2)

As a User, I want manually-added exercises to appear in the Canvas alongside existing scribbles and flow through the same logging pipeline.

**Why this priority**: Ensures that manual entries are treated as first-class citizens in the app ecosystem (Ledger, Insights).

**Independent Test**: Can be fully tested by creating a manual exercise and verifying its appearance in the Ledger and Insights sections.

**Acceptance Scenarios**:

1. **Given** a manually added exercise was just saved, **When** the user views the Canvas, **Then** the exercise appears as a scribble card alongside any AI-parsed scribbles.
2. **Given** a manually added exercise in the Canvas, **When** the user confirms the session, **Then** it appears in the Insights and Ledger identically to AI-parsed entries.

### Edge Cases

- What happens when there is no API key configured? (Manual Mode should still function normally as it bypasses AI).
- How does the system handle an empty exercise form submission?
- What happens if the user toggles Manual Mode while an AI scribble is actively being parsed?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a Manual Mode toggle in the General section of Settings (positioned as the first item, above Appearance).
- **FR-002**: System MUST persist the Manual Mode toggle state across app restarts via `SystemConfig`.
- **FR-003**: System MUST hide the freeform text input bar and display an "Add Exercise" primary pill button on the Canvas when Manual Mode is ON.
- **FR-004**: System MUST open the shared "Add Exercise Bottom Sheet" component when the "Add Exercise" button is tapped.
- **FR-005**: System MUST produce a `Scribble` object with status `SUCCESS` upon saving a manual exercise.
- **FR-006**: System MUST generate a human-readable summary in the `rawText` field of the manual `Scribble` (e.g., "Bench Press 100kg 3×10").
- **FR-007**: System MUST process manual entries through the same confirmation, persistence, Insights aggregation, and Ledger display pipelines as AI-parsed entries.
- **FR-008**: System MUST ensure AI insight cards and existing scribble cards remain visible and interactive on the Canvas in Manual Mode.

### Key Entities *(include if feature involves data)*

- **SystemConfig**: Stores the `isManualMode` boolean preference to persist the toggle state.
- **Scribble**: The core training data entity. For manual entries, its status is explicitly set to `SUCCESS` and its `rawText` serves as a human-readable summary.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully toggle between AI and Manual modes seamlessly, with the Canvas updating reactively without an app restart.
- **SC-002**: Users can manually add a multi-set exercise with notes and see it logged correctly in the Ledger.
- **SC-003**: 100% of manual entries successfully bypass the AI parsing pipeline and flow directly into the structured data format.

## Assumptions

- The `Add Exercise Bottom Sheet` is an existing or parallel shared component defined in `specs/add-exercise-bottom-sheet.md`.
- The existing confirmation, logging, Insights, and Ledger pipelines can handle pre-parsed (status `SUCCESS`) `Scribble` objects without modification.
- Exercise name autocomplete and predefined muscle group pickers are out of scope for this version (text input will be used).
- Hybrid mode (AI + manual simultaneously) is out of scope; the modes are mutually exclusive via the toggle.
- Bulk entry (adding multiple exercises at once) is out of scope; one exercise is added per sheet invocation.
