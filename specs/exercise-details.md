# Feature Specification: Exercise Details

**Feature Branch**: `feature/exercise-details`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "The Exercise Details screen provides a deep dive into a specific exercise's performance metrics, trends, and history. It empowers users to understand their progress for a particular movement through AI-driven insights, summarized weekly statistics, and a complete session log."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Weekly Performance Summary (Priority: P1)

As a User, I want to see a summary of my performance for a specific exercise this week so that I can track my immediate progress.

**Why this priority**: Immediate feedback on current week's performance is the most basic level of insight needed.

**Independent Test**: Can be fully tested by verifying that activity count, total volume, and max weight for the current week are displayed correctly for a selected exercise.

**Acceptance Scenarios**:

1. **Given** a user has logged sessions for an exercise this week, **When** they navigate to the Exercise Details for that exercise, **Then** they see "Activity" (session count), "Volume" (total weight), and "Max Weight" for the week.
2. **Given** a user has no sessions for an exercise this week, **When** they view its details, **Then** the stats show zeros or appropriate empty states.

---

### User Story 2 - AI-Driven Recommendations (Priority: P2)

As a User, I want to receive AI-driven recommendations for my next session so that I can optimize my training intensity (e.g., progressive overload).

**Why this priority**: Personalized AI insights distinguish the app and provide high value for progressive overload.

**Independent Test**: Can be tested independently by mocking the AI response and verifying the UI displays the recommendation and loading/error states.

**Acceptance Scenarios**:

1. **Given** an exercise has past performance data, **When** the details screen loads, **Then** an AI insight card suggests an actionable recommendation (e.g., "Increase weight by 5 lb") with positive reinforcement.
2. **Given** the AI engine is processing, **When** the screen is viewed, **Then** a loading state is shown for the insight card.
3. **Given** the AI engine fails to generate an insight, **When** the screen is viewed, **Then** an appropriate error state is handled without crashing the app.

---

### User Story 3 - Long-Term Trends & 1RM (Priority: P3)

As a User, I want to see my estimated One-Rep Max (1RM) and volume trends so that I can visualize my long-term improvement.

**Why this priority**: Visualizing long-term progress helps with motivation but is secondary to immediate session planning.

**Independent Test**: Can be tested by ensuring 1RM and last volume are calculated correctly and UI shortcuts function properly.

**Acceptance Scenarios**:

1. **Given** an exercise has historical weight data, **When** viewing the details, **Then** the current calculated 1RM and its trend status (e.g., "IMPROVING" with green arrow) are displayed.
2. **Given** a user is viewing the trends section, **When** they tap "VIEW ALL", **Then** they are navigated to a more detailed trends/charts view.

---

### User Story 4 - Complete Session History (Priority: P4)

As a User, I want to access a complete history of all sessions where I performed this exercise so that I can review my past performance in detail.

**Why this priority**: A detailed log is useful for deep auditing of past workouts but accessed less frequently.

**Independent Test**: Can be tested by verifying the history list accurately reflects the database entries.

**Acceptance Scenarios**:

1. **Given** the exercise details screen, **When** the user scrolls to the bottom, **Then** they see a button/row to "View all [X] tracked sessions".
2. **Given** the user taps the history button, **When** triggered, **Then** they navigate to a complete session history log.

### Edge Cases

- What happens when an exercise has absolutely no historical data?
- How does the system handle AI recommendation generation timeouts?
- What happens when the 1RM calculation encounters anomalous data (e.g., unrealistically high weight logged by mistake)?
- How does the UI adapt if the user has an extremely large number of logged sessions for one exercise?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow navigation to Exercise Details from the Canvas, Scribble Details, and Ledger screens.
- **FR-002**: System MUST display an AI Insight Card generated via the `LLMEngine`.
- **FR-003**: System MUST calculate and display Weekly Stats (Activity, Volume, Max Weight) using Room `@Transaction` (Android) or `AsyncSequence` (iOS).
- **FR-004**: System MUST calculate and display Current 1RM with a trend status badge.
- **FR-005**: System MUST provide navigation to a detailed Trends/Charts view.
- **FR-006**: System MUST provide navigation to view all tracked sessions for the exercise.
- **FR-007**: System MUST implement contextual UI splitting (Header, Body, Footer) on both Android (Jetpack Compose) and iOS (SwiftUI) using the `ScribbleFitTheme`.
- **FR-008**: System MUST utilize MVI architecture, specifically `GetExerciseDetailsUseCase` and `GetExerciseAIInsightUseCase`.

### Key Entities *(include if feature involves data)*

- **Exercise**: Represents a specific movement, linking to historical session data for metrics calculation.
- **Session (Scribble)**: The logged workout instances used to calculate Volume, Max Weight, and 1RM.
- **Insight**: The generated AI recommendation text and status indicators.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Unit, Integration, and UI tests pass on both Android and iOS platforms verifying calculations, AI integration, and state transitions.
- **SC-002**: Users can successfully view their Weekly Stats and 1RM immediately upon navigating to the Exercise Details screen.
- **SC-003**: AI Insights are generated and displayed without significant UI blocking or loading delays.
- **SC-004**: All architectural layers (MVI, Use Cases, Repositories) for Exercise Details are implemented according to guidelines.

## Assumptions

- Assumes the underlying database (`ExerciseRepository`) already tracks and stores historical sets, weights, and reps accurately.
- Assumes the `:feature:ai:domain`'s `LLMEngine` (Android) and `RoutingLLMService` (iOS) are readily available to process insights.
- Assumes the calculation formula for 1RM is standardized and defined within the Domain layer.
- Assumes standard development guidelines (MVI, DI, Coroutines/Swift Concurrency) as specified in `GEMINI.md` apply to this implementation.
