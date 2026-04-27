# Feature Specification: Exercise Trends

## 1. Overview
The Exercise Trends screen provides a comprehensive visual analysis of a user's performance for a specific exercise over time. It expands on the summarized data from the Exercise Details screen, offering interactive charts and deep-dive metrics for Estimated 1RM, Total Volume, and Repetition consistency.

## 2. User Stories
- **As a User**, I want to visualize my progress through interactive charts **so that** I can identify performance plateaus or periods of significant improvement.
- **As a User**, I want to toggle between different metrics (1RM, Volume, Reps) **so that** I can analyze my training from various performance angles.
- **As a User**, I want to filter the trend data by different time periods (e.g., 1 Month, 3 Months, 1 Year, All Time) **so that** I can see both short-term gains and long-term history.
- **As a User**, I want to see specific data points on the chart (e.g., date and value) when I interact with it **so that** I can precisely track my records.

## 3. Acceptance Criteria
- [ ] **Navigation:** Accessible via the "VIEW ALL" button in the Trends section of the `Exercise Details` screen.
- [ ] **Metric Selection:**
    - [ ] Segmented control or tabs to switch between: **1RM (Est.)**, **Total Volume**, and **Max Weight**.
    - [ ] Default view is **1RM (Est.)**.
- [ ] **Time Period Filtering:**
    - [ ] Options for: 1M, 3M, 6M, 1Y, and ALL.
    - [ ] Default filter is **3M**.
- [ ] **Interactive Chart:**
    - [ ] Line chart showing performance over the selected period.
    - [ ] Clear X-axis (Date) and Y-axis (Metric Value).
    - [ ] Touch-to-select data points to show a tooltip with the exact date and value.
    - [ ] Chart styling must follow the `Editorial Minimalism` guidelines (monochromatic, thin lines, glassmorphism tooltips).
- [ ] **Analysis Summary:**
    - [ ] Below the chart, display specific trend insights (e.g., "3.5% increase in 1RM over the last 30 days").
    - [ ] Display the "Personal Best" (PB) for the selected metric.
- [ ] **Contextual UI Splitting:**
    - [ ] **Header:** Back button, Exercise Name, and the current Metric type.
    - [ ] **Body:** Metric selector, Time filter, Chart container, and Analysis summary.
    - [ ] **Footer:** Standard Bottom Navigation bar.

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **UI:** 100% Jetpack Compose.
    - Use `:core:designsystem` tokens.
    - Implement the chart using a lightweight charting library (e.g., Vico) or custom Canvas drawing as per project standards.
- **Domain:**
    - `GetExerciseTrendDataUseCase`: Fetches historical data points for the specified exercise and metric.
- **Data:**
    - Repository method `getExerciseHistory(exerciseName: String)` fetching all relevant `Exercise` records from Room.
    - Mapping logic to convert raw records into chart data points based on the selected metric.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, Store).
- **UI:** 100% SwiftUI.
    - Use `Swift Charts` (available in iOS 16+) for data visualization.
    - Consistent Glassmorphic tooltips.
- **Domain:**
    - `GetExerciseTrendDataUseCase` (Swift Actor) for processing historical data.
- **Data:**
    - SwiftData query filtering `Exercise` entities by `canonicalName`.
    - Reactive `AsyncStream` for data updates.

## 5. Validation
- **Unit Tests:**
    - Data processing logic for each metric (1RM calculation, Volume aggregation).
    - Time filtering logic (correct date range selection).
- **UI Tests:**
    - Verify chart rendering with different data densities (sparse vs. frequent sessions).
    - Verify metric switching updates the chart correctly.
    - Verify "No data" state when the exercise has minimal history.
