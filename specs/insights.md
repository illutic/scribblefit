# Feature Specification: Insights (Data Visualization)

## 1. Overview
The Insights feature provides a data-driven view of the user's progress. It aggregates workout data from the Ledger and visualizes it through charts and statistics, helping users understand their training volume, frequency, and overall progress. It also includes AI-generated summaries for personalized coaching.

## 2. User Stories
- **As a User**, I want to see an AI-generated overview of my progress **so that** I can get quick, personalized insights.
- **As a User**, I want to see my weekly activity via a bar chart **so that** I can monitor my consistency.
- **As a User**, I want to see my volume distribution across exercises **so that** I can identify my focus areas.
- **As a User**, I want to see my monthly trends for volume and max weight **so that** I can visualize long-term strength gains.
- **As a User**, I want to see progress for key exercises (e.g., Bench Press, Squat) **so that** I can track my 1RM improvements.
- **As a User**, I want to filter these insights by month **so that** I can review past performance periods.

## 3. Acceptance Criteria

### 3.1 Header
- [ ] **Title:** "Monthly Insights" (Large, bold).
- [ ] **Month Selector:**
    - [ ] Displays the currently selected month (e.g., "March 2026").
    - [ ] Includes a calendar icon (`calendar_today`).
    - [ ] Tapping the selector opens a Month Picker.

### 3.2 AI Performance Summary
- [ ] **AI Overview Card:**
    - [ ] Positioned at the top of the insights list.
    - [ ] Displays a natural language summary (e.g., "Your bench volume is up 8% this week. Consider increasing the weight by 2.5 lbs in your next session.").
    - [ ] Uses a distinct background or border to highlight AI-generated content.
    - [ ] Includes a "Refresh" or "Re-generate" action.

### 3.3 Visualizations (Charts)
- [ ] **Weekly Activity Chart:**
    - [ ] Type: Bar Chart.
    - [ ] X-axis: Days of the week (M, T, W, T, F, S, S).
    - [ ] Y-axis: Number of sets or sessions.
- [ ] **Volume Distribution Chart:**
    - [ ] Type: Pie or Donut Chart.
    - [ ] Shows the percentage of total volume contributed by different exercise categories or specific exercises.
- [ ] **Monthly Trends Chart:**
    - [ ] Type: Multi-line or Area Chart.
    - [ ] Tracks "Total Volume" and "Max Weight" over the selected month.

### 3.4 Key Exercises List
- [ ] Displays a list of frequently performed exercises.
- [ ] For each exercise:
    - [ ] **Exercise Name:** (e.g., "Barbell Bench Press").
    - [ ] **Current 1RM:** Displays the calculated One-Rep Max.
    - [ ] **Progress Badge:** A status indicator (e.g., "IMPROVING" with a green upward arrow).
    - [ ] **Navigation:** Tapping an exercise navigates to the **Exercise Details** screen.

### 3.5 States
- [x] **Loading State:** Shimmer effects for charts and cards. Verified in `InsightsStore.swift`.
- [ ] **Empty State:** Displayed when fewer than two sessions are recorded. Reference: `Insights (Empty State)` design.

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:insights` with `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose.
    - Use **Vico** for the Weekly Activity and Monthly Trends charts.
    - Use custom Canvas or a dedicated library for the Volume Distribution pie chart.
    - Implement `InsightsHeader`, `AIOverviewCard`, and `ExerciseProgressList`.
- **AI Integration:** Use `:feature:ai:domain`'s `LLMEngine` to generate the summary based on workout statistics.
- **Database:** Room with `Flow` to reactively update stats as new workouts are added.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `InsightsFeature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI.
    - Use **Swift Charts** for all visualizations (Bar, Pie, Line charts).
    - Apply `.glassEffect()` as per `DESIGN.md`.
- **AI Integration:** Use `RoutingLLMService` to generate the summary.
- **Database:** SwiftData with reactive `AsyncStream` repositories.
- **Concurrency:** Swift 6 strict concurrency for calculating trends.

## 5. Stitch Design References

| Screen | Description |
|--------|-------------|
| Insights (Populated State) | Main insights view with charts, AI summary, and key exercises |
| Insights (Empty State) | Empty state view when insufficient data exists |
| Insights (Loading State) | Shimmer/Skeleton view during calculations |
| Exercise Details (Refined) | Detail screen for a single exercise accessed from the Key Exercises list |

## 6. Validation
- **Unit Tests:**
    - Statistical calculation logic (Volume, 1RM, Weekly Distribution).
    - `InsightsViewModel`/`InsightsStore` state management.
- **Integration Tests:** Verifying database aggregation queries return correct values.
- **UI Tests:** 
    - Verify charts are rendered correctly with mock data.
    - Verify the Month Selector updates the insights.
    - Verify navigation to Exercise Details on exercise item tap.
