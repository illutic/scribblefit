# Feature Specification: Insights (Data Visualization)

**Feature Branch**: `feature/insights`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "The Insights feature provides a data-driven view of the user's progress. It aggregates training data from Scribbles (specifically COMPLETED ones) and visualizes it through charts and statistics, helping users understand their training volume, frequency, and overall progress. It also includes AI-generated summaries for personalized coaching."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - AI Performance Summary (Priority: P1)

As a User, I want to see an AI-generated overview of my progress so that I can get quick, personalized insights.

**Why this priority**: Personalized coaching and interpretation of data is a core value proposition of ScribbleFit's AI integration.

**Independent Test**: Can be fully tested by verifying the AI Overview Card is displayed at the top of the insights list with a natural language summary when sufficient data is present, and testing the refresh action.

**Acceptance Scenarios**:

1. **Given** a user has recorded training data, **When** they view the Insights screen, **Then** an AI Overview Card is displayed with a natural language summary of their progress.
2. **Given** the AI Overview Card is visible, **When** the user taps "Refresh" or "Re-generate", **Then** a new summary is generated based on current data.

---

### User Story 2 - Weekly Activity Monitoring (Priority: P2)

As a User, I want to see my weekly activity via a bar chart so that I can monitor my consistency.

**Why this priority**: Consistency is key in fitness tracking, and a simple bar chart is universally understood.

**Independent Test**: Can be fully tested by ensuring the Bar Chart accurately displays the number of sets/sessions per day of the week.

**Acceptance Scenarios**:

1. **Given** training data exists for the current week, **When** the user views the Insights screen, **Then** a Bar Chart is displayed showing days on the X-axis and volume/sessions on the Y-axis.

---

### User Story 3 - Volume Distribution and Monthly Trends (Priority: P2)

As a User, I want to see my volume distribution across exercises and monthly trends for volume and max weight so that I can visualize long-term strength gains and focus areas.

**Why this priority**: Allows users to understand their effort allocation and track long-term improvements over the selected month.

**Independent Test**: Can be tested by validating the Pie/Donut chart for volume distribution and Multi-line/Area chart for monthly trends.

**Acceptance Scenarios**:

1. **Given** training data spans across various exercises, **When** the user views Insights, **Then** a Pie/Donut Chart shows the percentage of total volume contributed by different exercises.
2. **Given** a selected month with data, **When** the user views Insights, **Then** a Multi-line or Area Chart tracks Total Volume and Max Weight over time.

---

### User Story 4 - Key Exercises Progress and Filtering (Priority: P3)

As a User, I want to see progress for key exercises and filter these insights by month so that I can track 1RM improvements and review past performance.

**Why this priority**: Detailed breakdown of specific exercises and historical filtering provides deep analytical value.

**Independent Test**: Verify the key exercises list displays accurate 1RM and progress badges, and the month selector updates all insights on the screen.

**Acceptance Scenarios**:

1. **Given** the user taps the Month Selector, **When** they select a different month, **Then** all charts and summaries update to reflect data for that month.
2. **Given** the Insights view, **When** the Key Exercises List is displayed, **Then** each item shows the exercise name, calculated 1RM, and a progress badge.
3. **Given** the Key Exercises List, **When** the user taps an exercise, **Then** they navigate to the Exercise Details screen.

### Edge Cases

- What happens when fewer than two sessions are recorded? System MUST display an Empty State view.
- How does the system handle loading states during complex statistical calculations? System MUST display Shimmer/Skeleton effects for charts and cards (Verified in `InsightsStore.swift`).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST aggregate training data from `COMPLETED` Scribbles.
- **FR-002**: System MUST display a "Monthly Insights" title and a Month Selector with a calendar icon (`calendar_today`).
- **FR-003**: System MUST provide an AI-generated summary using the `:feature:ai:domain` `LLMEngine` (Android) or `RoutingLLMService` (iOS).
- **FR-004**: System MUST visualize data using a Weekly Activity Bar Chart, Volume Distribution Pie/Donut Chart, and Monthly Trends Multi-line/Area Chart.
- **FR-005**: System MUST display a list of key exercises with calculated 1RM and progress badges (e.g., "IMPROVING" with a green upward arrow).
- **FR-006**: System MUST allow navigation from key exercises to an Exercise Details screen.
- **FR-007**: System MUST handle loading states using shimmer effects.
- **FR-008**: System MUST display an empty state when insufficient data exists (< 2 sessions) as per Stitch design references.
- **FR-009 (Android-Specific)**: UI MUST use 100% Jetpack Compose with Vico for bar/line charts and custom Canvas for pie charts. Architecture MUST follow MVI with Room + Flow for reactive stats.
- **FR-010 (iOS-Specific)**: UI MUST use 100% SwiftUI with Swift Charts and `.glassEffect()`. Architecture MUST follow MVI with SwiftData, reactive `AsyncStream` repositories, and Swift 6 strict concurrency.

### Key Entities

- **Scribble**: Represents a training session. Must have a `status` of `COMPLETED` to be included in insights. Contains related `Exercise` and `Set` records.
- **Exercise**: Represents a specific movement performed during a Scribble. Associated with `Set`s and contributes to Volume and 1RM metrics.
- **Set**: Individual effort containing weight, reps, etc. Used to calculate total volume and identify max weights.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: AI summaries generate and display correctly when data is available.
- **SC-002**: Charts render accurately based on mock data in UI tests.
- **SC-003**: Changing the selected month updates the data views to reflect the selected period.
- **SC-004**: System correctly calculates and displays 1RM, volume distributions, and weekly distributions (validated by Unit and Integration Tests).
- **SC-005**: Database aggregation queries successfully return correct values for insights views.

## Assumptions

- Users have a sufficient amount of historical data to generate meaningful insights (otherwise empty state is shown).
- The underlying AI service (LLMEngine/RoutingLLMService) is available and responsive for generating summaries.
- The user's device can handle the rendering of multiple charts smoothly.
- Data synchronization and aggregation queries across the database are atomic and accurate.
