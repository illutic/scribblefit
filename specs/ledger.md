# Feature Specification: Ledger (Training History)

**Feature Branch**: `[feature/ledger]`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "Ledger for browsing training history, filtering by date ranges, and viewing specific session details."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Browse Chronological History (Priority: P1)

As a User, I want to see a chronological list of my past training sessions, complete with summaries (date, exercises, total volume), so that I can track my consistency and quickly identify sessions.

**Why this priority**: Core functionality of the Ledger; without a history list, the ledger provides no value.

**Independent Test**: Can be fully tested by generating mock sessions, navigating to the Ledger screen, and verifying the sessions are sorted in descending order of date.

**Acceptance Scenarios**:

1. **Given** the user has recorded sessions, **When** they navigate to the Ledger, **Then** they see a list of sessions grouped by date (newest first).
2. **Given** a session in the list, **When** viewed, **Then** it displays the day, date, an interactivity indicator (chevron), exercise summary, and metrics (e.g., total volume).

---

### User Story 2 - View Session Details (Priority: P1)

As a User, I want to tap a session entry so that I can view the full details of that training session.

**Why this priority**: Essential for drilling down into the specific exercises and sets performed during a past workout.

**Independent Test**: Can be fully tested by tapping on any session in the history list and confirming the app navigates to the detailed view.

**Acceptance Scenarios**:

1. **Given** the user is viewing the session list, **When** they tap a session entry, **Then** the app navigates to the Scribble Details screen for that specific session.

---

### User Story 3 - Filter by Date Range (Priority: P2)

As a User, I want to filter my history by date range so that I can focus on a specific period (e.g., this month).

**Why this priority**: Important for users with long histories, but not strictly necessary for an MVP with few records.

**Independent Test**: Can be fully tested by tapping the date filter, selecting a new range, and verifying the list updates correctly.

**Acceptance Scenarios**:

1. **Given** the Ledger screen is open, **When** the user taps the Date Range Selector, **Then** a native Date Range Picker opens.
2. **Given** a selected date range, **When** applied, **Then** the list only shows sessions within that range.
3. **Given** the default state, **When** the Ledger is opened, **Then** the filter defaults to the Last 30 Days.

---

### User Story 4 - View Empty and Loading States (Priority: P3)

As a User, I want to see a clear empty state if I haven't recorded anything, and a loading state when my history is being fetched.

**Why this priority**: Crucial for good UX, preventing user confusion when data is absent or slow to load.

**Independent Test**: Can be fully tested by artificially delaying the data fetch (to see skeleton loaders) or returning an empty list (to see the empty state message).

**Acceptance Scenarios**:

1. **Given** the history is being fetched, **When** the screen is drawn, **Then** skeleton loaders matching the layout of session cards are displayed.
2. **Given** no records are found in the selected range or overall, **When** fetching completes, **Then** an empty state is displayed with a clear message ("Your history is empty") and a call-to-action ("Start your first session on the Canvas").

### Edge Cases

- What happens when a user selects a date range in the future? (Should show empty state or prevent selection).
- How does system handle a very large number of sessions on a single day? (Should scroll smoothly within the grouped day).
- What happens if the database query fails? (Should show an error state instead of infinite loading or empty state).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display training sessions chronologically grouped by date (descending).
- **FR-002**: System MUST allow filtering of sessions via a native Date Range Picker, defaulting to the last 30 days.
- **FR-003**: System MUST provide a summary for each session including date, exercises, and metrics (e.g., volume).
- **FR-004**: System MUST navigate to the detailed `Scribble Details` screen upon tapping a session entry (via `scribbleId` on Android, `UUID` on iOS).
- **FR-005**: System MUST display skeleton loaders during data fetching.
- **FR-006**: System MUST display an empty state message with a CTA when no sessions match the criteria.
- **FR-007**: System MUST use Jetpack Compose (Android) and SwiftUI (iOS) following MVI architecture.
- **FR-008**: System MUST adhere to minimal design tokens (zero borders, glassmorphism, specified typography).

### Key Entities *(include if feature involves data)*

- **ScribbleWithExercises**: Represents a single logged session, including date, a list of associated exercises, and computed metrics.
- **DateRangeFilter**: Represents the selected start and end dates for filtering the ledger queries.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully filter their past workouts, with UI responding without noticeable lag.
- **SC-002**: Empty and Loading states transition smoothly and convey the correct system status.
- **SC-003**: Navigation to session details is fluid and passes the correct `scribbleId` or `UUID`.
- **SC-004**: Automated tests (Unit, Integration, UI) for Room/SwiftData queries, filtering logic, and state transitions pass consistently.

## Assumptions

- Assumes the existence of `Scribble` data and a functional `Scribble Details` screen to navigate to.
- Assumes the underlying database (Room on Android, SwiftData on iOS) is configured to handle reactive queries (`Flow` or `AsyncStream`).
- Assumes skeleton loader libraries or shimmer effect utilities are available in both platforms' core design systems.
