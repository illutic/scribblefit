# Feature Specification: Exercise Details

## 1. Overview
The Exercise Details screen provides a deep dive into a specific exercise's performance metrics, trends, and history. It empowers users to understand their progress for a particular movement through AI-driven insights, summarized weekly statistics, and a complete session log.

## 2. User Stories
- **As a User**, I want to see a summary of my performance for a specific exercise this week **so that** I can track my immediate progress.
- **As a User**, I want to receive AI-driven recommendations for my next session **so that** I can optimize my training intensity (e.g., progressive overload).
- **As a User**, I want to see my estimated One-Rep Max (1RM) and volume trends **so that** I can visualize my long-term improvement.
- **As a User**, I want to access a complete history of all sessions where I performed this exercise **so that** I can review my past performance in detail.

## 3. Acceptance Criteria
- [x] **Navigation:** Accessible by tapping an exercise in the Canvas (logged scribble card), the Scribble Details screen, or the Ledger. (Android: `Screen.ExerciseDetails(exerciseName)` data class; iOS: `selectedExerciseName` sheet binding.) Implemented 2026-04-21.
- [ ] **AI Insight Card:**
    - [ ] Display a prominent card with a specific recommendation (e.g., "Increase weight by 5 lb based on your last session's performance").
    - [ ] Include a visual indicator (e.g., fire emoji) for positive reinforcement.
    - [ ] Handle loading and error states for AI-generated insights.
- [ ] **Weekly Stats Card:**
    - [ ] Display "Activity" (number of sessions this week).
    - [ ] Display "Volume" (total weight moved this week).
    - [ ] Display "Max Weight" (heaviest weight lifted this week).
- [ ] **Trends Section:**
    - [ ] **Current 1RM:** Display the calculated 1RM with a status badge (e.g., "IMPROVING" with a green upward arrow).
    - [ ] **Last Volume:** Display the volume from the most recent session with a status (e.g., "STABLE").
    - [ ] "VIEW ALL" shortcut to a more detailed trends/charts view.
- [ ] **Session History:**
    - [ ] A prominent button/row to "View all [X] tracked sessions" with a history icon and chevron.
- [ ] **Contextual UI Splitting:**
    - [ ] **Header:** Back arrow button and the Exercise Name (e.g., "Barbell Bench Press").
    - [ ] **Body:** Scrollable content containing the Insight Card, Weekly Stats Card, Trends Section, and Session History button.
    - [ ] **Footer:** Standard Bottom Navigation bar (Canvas, Insights, Ledger).

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:exercises` (existing) or a sub-package/module if it becomes too large.
- **UI:** 100% Jetpack Compose using `ScribbleFitTheme`.
    - Split into `ExerciseDetailsHeader`, `ExerciseDetailsBody`, and `ExerciseDetailsFooter`.
    - Use `Card` and `ListItem` components from `:core:designsystem`.
- **Domain:**
    - `GetExerciseDetailsUseCase`: Orchestrates fetching stats, trends, and recent history.
    - `GetExerciseAIInsightUseCase`: Integrates with `:feature:ai:domain`'s `LLMEngine` to generate personalized recommendations.
- **Data:**
    - Extend `ExerciseRepository` or create `ExerciseDetailsRepository` to provide specific performance metrics (Volume, Max Weight, 1RM calculations).
    - Use Room `@Transaction` for atomic fetching of exercise history and stats.
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `ExercisesFeature` (existing) or a sub-target.
- **UI:** 100% SwiftUI with `ScribbleFitTheme`.
    - Split into `ExerciseDetailsHeaderView`, `ExerciseDetailsBodyView`, and `ExerciseDetailsFooterView`.
    - Use `GroupBox` or custom `CardView` for metrics.
- **Domain:**
    - `GetExerciseDetailsUseCase` (Swift Actor) for fetching stats and history.
    - `GetExerciseAIInsightUseCase` for AI recommendations.
- **Data:**
    - Extend `ExerciseRepository` implementation with SwiftData queries for performance metrics.
    - Use `AsyncSequence` for reactive updates to stats.
- **Background Tasks:** Swift Concurrency for calculating trends and 1RM.

## 5. Validation
- **Unit Tests:**
    - `ExerciseDetailsViewModel`/`ExerciseDetailsStore` tests for state transitions and data mapping.
    - 1RM calculation logic verification.
    - `GetExerciseAIInsightUseCase` tests with mocked `LLMEngine`.
- **Integration Tests:**
    - Verifying database queries for volume and max weight calculations over specific time periods.
    - Verifying history fetching returns the correct list of sessions.
- **UI Tests:**
    - Verify all sections (Insight, Stats, Trends, History) are displayed correctly with mock data.
    - Verify navigation back to the previous screen.
    - Verify the "View all sessions" button triggers the correct navigation intent.
