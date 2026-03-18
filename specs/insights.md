# Feature Specification: Insights (Data Visualization)

## 1. Overview
The Insights feature provides a data-driven view of the user's progress. It aggregates workout data from the Ledger and visualizes it through charts and statistics, helping users understand their training volume, frequency, and overall progress.

## 2. User Stories
- **As a User**, I want to see an AI-generated overview of my progress **so that** I can get quick, personalized insights without analyzing charts myself.
- **As a User**, I want to see my total training volume **so that** I can track my strength progress over time.
- **As a User**, I want to see a chart of my workout frequency **so that** I can monitor my consistency.
- **As a User**, I want to see which muscle groups I've trained most **so that** I can ensure a balanced routine.
- **As a User**, I want to see a loading state **so that** I know the app is calculating my statistics.
- **As a User**, I want a clear empty state **so that** I understand I need to record workouts to see insights.

## 3. Acceptance Criteria
- [ ] The Insights screen must be accessible via the bottom navigation bar.
- [ ] **Empty State:** Displayed when fewer than two workouts are recorded. Provide encouragement to log more sessions.
- [ ] **Loading State:** Display a progress indicator while stats are being calculated from the database.
- [ ] **Data State:**
    - [ ] **AI Overview:**
        - [ ] A card at the top of the screen providing a natural language summary of progress.
        - [ ] Displays trends in volume, frequency, and muscle group focus.
        - [ ] Includes actionable advice based on the data (e.g., "Consider adding more leg volume").
        - [ ] Loading state: Show a shimmer or placeholder while generating.
        - [ ] Refresh capability: Users can trigger a re-generation of the summary.
    - [ ] **Volume Chart:** A line chart showing total volume (sum of weight * reps) over a selectable time period (week/month/year).
    - [ ] **Frequency Stats:** A grid or list showing workouts per week and total workouts.
    - [ ] **Muscle Distribution:** A pie or bar chart showing the percentage of sets/exercises per muscle group.
- [ ] **Contextual UI Splitting:**
    - [ ] **Header:** Title "Insights".
    - [ ] **Body:** The charts and metrics, empty state, or loading indicator.
    - [ ] **Footer:** Bottom navigation bar (shared across the app).

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:insights` with `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose using `ScribbleFitTheme`.
    - Use a charting library (e.g., Vico or custom Canvas) for visualizations.
    - Implement `InsightsHeader`, `InsightsBody`, and `InsightsFooter` as separate contextual Composables.
    - **AI Overview Card:** A dedicated component at the top of the `InsightsBody` that displays the AI summary.
- **AI Integration:** Use `:feature:ai`'s `LLMEngine` to generate the summary based on workout statistics.
- **Database:** Room with `Flow` to reactively update stats as new workouts are added.
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `InsightsFeature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI with `ScribbleFitTheme`.
    - Use `Swift Charts` for all visualizations.
    - Implement `InsightsHeaderView`, `InsightsBodyView`, and `InsightsFooterView` as separate contextual Views.
    - **AI Overview View:** A card-like view at the top of `InsightsBodyView` for the AI summary.
- **AI Integration:** Integration with a local or remote LLM (e.g., via CoreML or an API) to generate the summary.
- **Database:** SwiftData with `@Query` or `AsyncSequence`.
- **Background Tasks:** Swift Concurrency for statistical calculations and AI generation.

## 5. Validation
- **Unit Tests:**
    - Calculations logic (volume, frequency, muscle distribution) in Use Cases.
    - `InsightsViewModel`/`InsightsStore` state management.
- **Integration Tests:** Verifying that the database queries return correct aggregated data.
- **UI Tests:** 
    - Verify charts are rendered correctly when data is present.
    - Verify the empty state is displayed for new users.
