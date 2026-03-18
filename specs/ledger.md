# Feature Specification: Ledger (Workout History)

## 1. Overview
The Ledger feature provides a comprehensive, scrollable history of all past workouts recorded by the user. It serves as the primary log for reviewing progress and accessing detailed information about individual training sessions.

## 2. User Stories
- **As a User**, I want to see a list of my past workouts **so that** I can track my consistency over time.
- **As a User**, I want to see a summary of each workout (date, exercises performed) **so that** I can quickly identify a specific session.
- **As a User**, I want to be informed when I haven't recorded any workouts yet **so that** I know the app is ready for my first entry.
- **As a User**, I want to see a loading state **so that** I know the app is fetching my history from the local database.

## 3. Acceptance Criteria
- [ ] The Ledger screen must be accessible via the bottom navigation bar.
- [ ] **Empty State:** If no workouts exist, display a clear empty state with a call-to-action to start a new workout.
- [ ] **Loading State:** Display a progress indicator while fetching workout data.
- [ ] **Data State:**
    - [ ] Display a scrollable list of workouts ordered by date (newest first).
    - [ ] Each list item must show the workout date (e.g., "Monday, March 16").
    - [ ] Each list item must show a summary of exercises (e.g., "Bench Press, Squat, Pull-ups").
    - [ ] Tapping a workout item should (potentially) navigate to a detailed view (out of scope for this initial spec, but planned).
- [ ] **Contextual UI Splitting:**
    - [ ] **Header:** Title "Ledger".
    - [ ] **Body:** The workout list, empty state, or loading indicator.
    - [ ] **Footer:** Bottom navigation bar (shared across the app).

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:ledger` with `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose using `ScribbleFitTheme`.
    - Use `LazyColumn` for the workout list.
    - Implement `LedgerHeader`, `LedgerBody`, and `LedgerFooter` as separate contextual Composables.
- **Database:** Room with `Flow<List<Workout>>` for reactivity.
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `LedgerFeature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI with `ScribbleFitTheme`.
    - Use `List` or `ScrollView` with `LazyVStack` for the history.
    - Implement `LedgerHeaderView`, `LedgerBodyView`, and `LedgerFooterView` as separate contextual Views.
- **Database:** SwiftData with `@Query` or `AsyncSequence` for reactivity.
- **Background Tasks:** Swift Concurrency for database operations.

## 5. Validation
- **Unit Tests:**
    - `LedgerViewModel`/`LedgerStore` tests for state transitions (Loading -> Data, Loading -> Empty).
    - Use Case tests for fetching workouts from the repository.
- **Integration Tests:** Verifying Room/SwiftData queries return the expected list of workouts.
- **UI Tests:** 
    - Verify the empty state is displayed when the database is empty.
    - Verify the workout list displays the correct number of items and data.
