# Feature Specification: Ledger (Workout History)

## 1. Overview
The Ledger is a comprehensive, chronological log of all physical activity recorded by the user. It allows users to browse their training history, filter by date ranges, and drill down into specific session details. It serves as the primary source of truth for past performance.

## 2. User Stories
- **As a User**, I want to see a chronological list of my past workouts **so that** I can track my consistency.
- **As a User**, I want to filter my history by date range **so that** I can focus on a specific period (e.g., this month).
- **As a User**, I want to see a summary of each workout (date, exercises, and total volume) **so that** I can quickly identify sessions.
- **As a User**, I want to tap a workout entry **so that** I can view the full Workout Details.
- **As a User**, I want to see a clear empty state **so that** I know I haven't recorded anything yet.
- **As a User**, I want to see a loading state (skeleton loaders) **so that** I know my history is being fetched.

## 3. Acceptance Criteria

### 3.1 Header
- [ ] **Title:** "Ledger" (Large, bold).
- [ ] **Date Range Selector:**
    - [ ] Displays the current filter range (e.g., "Mar 1, 2026 – Mar 31, 2026").
    - [ ] Includes a calendar icon (`calendar_today`).
    - [ ] Tapping the selector opens a native Date Range Picker.
    - [ ] Default range: Current Month.

### 3.2 Workout List (Chronological History)
- [ ] **Grouping:** Workouts are grouped by date, displayed in descending order (newest first).
- [ ] **Workout Card/Item:**
    - [ ] **Header:** Displays the day and date (e.g., "Monday, March 16").
    - [ ] **Interactivity Indicator:** A trailing chevron-right (`chevron_right`) suggesting navigation to details.
    - [ ] **Exercise Summary:** A list of exercises performed in that session.
    - [ ] **Metrics:** Displays the total volume or specific stats for each exercise (e.g., "Bench Press 2,450 lbs").
- [ ] **Navigation:** Tapping any part of a workout entry navigates to the **Workout Details** screen for that session.

### 3.3 States
- [ ] **Loading State:**
    - [ ] Display skeleton loaders that match the layout of the workout cards.
    - [ ] Reference: `Ledger (Loading State)` design.
- [ ] **Empty State:**
    - [ ] Displayed when no workouts are found in the selected range or at all.
    - [ ] Includes a clear message (e.g., "Your history is empty") and a call-to-action (e.g., "Start your first session on the Canvas").
    - [ ] Reference: `Ledger (Minimal Empty State)` design.

### 3.4 UI Design Tokens
- **Background:** Minimalist, following `DESIGN.md`.
- **Card Styling:** Zero borders, glassmorphism or subtle elevation as per platform guidelines.
- **Typography:** Inter (Android) / San Francisco (iOS).

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:ledger` with `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose.
    - Use `LazyColumn` for the scrollable list.
    - Implement `LedgerHeader`, `WorkoutItem`, and `EmptyLedgerContent` as separate contextual Composables.
    - Use skeleton loading library or custom shimmer effects.
- **Database:** Room with `Flow<List<WorkoutWithAllDetails>>` for reactive updates.
- **Navigation:** Use `workoutId` to navigate to the Workout Details screen.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `LedgerFeature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI.
    - Use `List` or `ScrollView` with `LazyVStack`.
    - Apply `.glassEffect()` or consistent spacing for cards.
    - Implement skeleton loaders using overlaid shapes with opacity animation.
- **Database:** SwiftData with `@Query` or `AsyncStream` repositories.
- **Concurrency:** Swift 6 strict concurrency for repository fetching.

## 5. Stitch Design References

| Screen | Description |
|--------|-------------|
| Ledger with Interactivity Indicators | Main chronological history with date range and chevrons |
| Ledger (Minimal Empty State) | Empty state view when no workouts exist |
| Ledger (Loading State) | Skeleton/Shimmer view during data fetch |

## 6. Validation
- **Unit Tests:**
    - `LedgerViewModel`/`LedgerStore`: Verify state transitions (Loading -> Data -> Empty).
    - `FilterWorkoutsUseCase`: Verify filtering logic for different date ranges.
- **Integration Tests:** 
    - Verifying Room/SwiftData queries return workouts correctly filtered and sorted.
- **UI Tests:** 
    - Verify the date range picker updates the displayed list.
    - Verify navigation to Workout Details on item tap.
    - Verify the empty state is visible when no data is provided.
