# Feature Specification: Exercise Trends

**Feature Branch**: `[###-exercise-trends]`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "The Exercise Trends screen provides a comprehensive visual analysis of a user's performance for a specific exercise over time. It expands on the summarized data from the Exercise Details screen, offering interactive charts and deep-dive metrics for Estimated 1RM, Total Volume, and Repetition consistency."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visualize Progress (Priority: P1)

As a User, I want to visualize my progress through interactive charts so that I can identify performance plateaus or periods of significant improvement.

**Why this priority**: Core value of the feature, providing the primary visualization for historical data.

**Independent Test**: Can be fully tested by opening the exercise trends screen and verifying that a chart renders showing historical data points correctly.

**Acceptance Scenarios**:

1. **Given** a user is on the Exercise Details screen, **When** they tap the "VIEW ALL" button in the Trends section, **Then** the Exercise Trends screen should open displaying the default 1RM (Est.) chart for the last 3 months.
2. **Given** a chart with rendered data points, **When** a user touches a specific data point, **Then** a tooltip with the exact date and value should be displayed.

---

### User Story 2 - Toggle Metrics (Priority: P2)

As a User, I want to toggle between different metrics (1RM, Volume, Max Weight) so that I can analyze my training from various performance angles.

**Why this priority**: Essential for deeper analysis, allowing users to switch contexts and view different aspects of their training.

**Independent Test**: Can be tested by tapping different metric selectors and observing the chart update.

**Acceptance Scenarios**:

1. **Given** the Exercise Trends screen is open, **When** the user switches the metric from "1RM (Est.)" to "Total Volume", **Then** the chart and analysis summary should immediately update to reflect Total Volume data.

---

### User Story 3 - Time Period Filtering (Priority: P3)

As a User, I want to filter the trend data by different time periods (e.g., 1 Month, 3 Months, 1 Year, All Time) so that I can see both short-term gains and long-term history.

**Why this priority**: Adds flexibility to the primary visualization but is secondary to just seeing the data.

**Independent Test**: Can be tested by selecting different time chips and verifying the X-axis range updates.

**Acceptance Scenarios**:

1. **Given** the Exercise Trends screen is open with the default 3M view, **When** the user selects the "1Y" filter, **Then** the chart should update to display data points over the last year.

### Edge Cases

- What happens when an exercise has no historical data? (Should show a "No data" state).
- What happens when an exercise has only one logged session? (Chart should handle single data point gracefully without crashing).
- How does system handle sparse data with large gaps between sessions? (Chart lines should connect appropriately or show gaps).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide navigation to the Exercise Trends screen via the "VIEW ALL" button in the Trends section of the `Exercise Details` screen.
- **FR-002**: System MUST allow selecting metrics via Segmented control or tabs: **1RM (Est.)**, **Total Volume**, and **Max Weight**. Default is 1RM (Est.).
- **FR-003**: System MUST provide time period filtering: 1M, 3M, 6M, 1Y, and ALL. Default is 3M.
- **FR-004**: System MUST display an interactive line chart showing performance over the selected period with clear X-axis (Date) and Y-axis (Metric Value).
- **FR-005**: System MUST allow touch-to-select data points on the chart to show a tooltip with the exact date and value.
- **FR-006**: System MUST style the chart according to `Editorial Minimalism` guidelines (monochromatic, thin lines, glassmorphism tooltips).
- **FR-007**: System MUST display an Analysis Summary below the chart with specific trend insights and the "Personal Best" (PB) for the selected metric.
- **FR-008**: System MUST split UI contextually into Header (Back button, Exercise Name, current Metric type), Body (selectors, filters, chart, summary), and Footer (Bottom Navigation bar).
- **FR-009**: System MUST use `GetExerciseTrendDataUseCase` to fetch historical data points for the specified exercise and metric.
- **FR-010**: System MUST include mapping logic to convert raw `Exercise` records into chart data points based on the selected metric.
- **FR-011**: (Android) System MUST use MVI architecture, 100% Jetpack Compose, and `:core:designsystem` tokens. Chart using lightweight library or Canvas.
- **FR-012**: (iOS) System MUST use MVI architecture, 100% SwiftUI, `Swift Charts` (iOS 16+), and `GetExerciseTrendDataUseCase` (Swift Actor) with SwiftData query.

### Key Entities *(include if feature involves data)*

- **Exercise**: Represents a logged exercise session, contains historical data for calculating 1RM, Volume, and Max Weight.
- **TrendDataPoint**: Represents a calculated value for a specific date to be plotted on the chart.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully navigate to and view the Exercise Trends screen.
- **SC-002**: Chart renders accurately for different data densities and metric selections.
- **SC-003**: Tooltips display correct date and value information upon interaction.
- **SC-004**: System handles edge cases like "No data" gracefully.
- **SC-005**: Unit tests pass for data processing logic (1RM calculation, Volume aggregation, time filtering).
- **SC-006**: UI tests pass for chart rendering, metric switching, and empty states.

## Assumptions

- Users have enough exercise data to render meaningful trends.
- The underlying `Exercise` data structure contains accurate weight and rep information.
- The selected chart libraries (or custom Canvas/Swift Charts) support the required interactions and styling.
