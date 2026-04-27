# Feature Specification: Exercise History (Tracked Sessions)

## 1. Overview
The Exercise History screen provides a chronological log of every session where the user performed a specific exercise. It allows for a detailed review of past sets, weight, and repetitions, providing the historical context necessary for tracking long-term progression.

## 2. User Stories
- **As a User**, I want to see a full list of my past performances for an exercise **so that** I can see exactly what I lifted and when.
- **As a User**, I want to see the sets and reps for each historical session **so that** I can compare them to my current performance.
- **As a User**, I want to tap on a historical session **so that** I can see the full workout context (the scribble it belonged to).
- **As a User**, I want to see my progress markers (e.g., a "PB" or "PR" badge) next to specific historical entries **so that** I can celebrate my records.

## 3. Acceptance Criteria
- [ ] **Navigation:** Accessible via the "View all [X] tracked sessions" row in the `Exercise Details` screen.
- [ ] **Chronological List:**
    - [ ] Sessions sorted by date (newest first).
    - [ ] Sticky date headers (e.g., "OCTOBER 2025", "SEPTEMBER 2025").
- [ ] **Session Row Item:**
    - [ ] Display the Date and Day of the week.
    - [ ] Display the total volume for that session.
    - [ ] Display a summarized set/rep string (e.g., "100 kg x 3, 3, 3").
    - [ ] Include a "Record" badge (e.g., gold star or "PB" text) if a personal best was achieved in that session.
- [ ] **Interactivity:**
    - [ ] Tapping a session row navigates to the `Scribble Details` screen for the associated workout.
- [ ] **Empty State:**
    - [ ] Clear messaging if the exercise has no history (though this should be rare as the screen is navigated from a specific exercise).
- [ ] **Contextual UI Splitting:**
    - [ ] **Header:** Back button, Exercise Name, and total session count.
    - [ ] **Body:** Scrollable list of session entries with sticky headers.
    - [ ] **Footer:** Standard Bottom Navigation bar.

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **UI:** 100% Jetpack Compose.
    - Use `LazyColumn` for the performance-optimized list.
    - Use `StickyHeader` for date groupings.
- **Domain:**
    - `GetExerciseHistoryUseCase`: Fetches all historical `Exercise` instances and their parent `Scribble` metadata.
- **Data:**
    - Room `@Transaction` query joining `ScribbleEntity` and `ExerciseEntity` filtered by name.
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, Store).
- **UI:** 100% SwiftUI.
    - Use `List` or `ScrollView` with `LazyVStack`.
    - Section-based grouping for chronological headers.
- **Domain:**
    - `GetExerciseHistoryUseCase` (Swift Actor).
- **Data:**
    - SwiftData query with predicate matching the `canonicalName`.
    - Relationship mapping to parent `Scribble`.

## 5. Validation
- **Unit Tests:**
    - Correct sorting and grouping of sessions by month/year.
    - PB identification logic (identifying which session contains the max weight/volume).
- **UI Tests:**
    - Verify list scrolling performance.
    - Verify sticky header behavior.
    - Verify navigation to `Scribble Details` on tap.
