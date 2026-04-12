# Feature Specification: Workout Details

## 1. Overview
The Workout Details screen provides a comprehensive view of a single training session. It serves two primary purposes:
1.  **Review & Edit:** Acting as the final step in the "Scribble -> Parse -> Review" flow, where users can fine-tune parsed data before officially logging the workout.
2.  **History View:** Providing a detailed breakdown of a completed workout when accessed from the Ledger (Workout History).

The screen allows for granular control over exercises, sets, weights, and repetitions, ensuring the logged data is 100% accurate.

## 2. User Stories
- **As a User**, I want to review the exercises and sets parsed from my scribble **so that** I can ensure the AI captured everything correctly.
- **As a User**, I want to manually adjust weights and reps for any set **so that** my logs reflect what I actually lifted.
- **As a User**, I want to add or remove sets from an exercise **so that** I can correct parsing errors or reflect last-minute changes.
- **As a User**, I want to add new exercises to the workout **so that** I can include things I forgot to scribble.
- **As a User**, I want to add session-wide notes **so that** I can record how I felt, injuries, or specific focus areas.
- **As a User**, I want to delete an entire exercise from the workout **so that** I can remove mis-parsed or unwanted entries.
- **As a User**, I want to "Finish & Log" my workout **so that** it is saved to my permanent history and contributes to my insights.

## 3. Acceptance Criteria

### 3.1 Header
- [ ] Displays a **Back Button** (chevron-left) to return to the previous screen (Canvas or Ledger).
- [ ] Displays the **Workout Date** (Format: "Monday, October 23").
- [ ] Sticky header behavior on scroll is preferred.

### 3.2 Exercise List
- [ ] Displays a list of exercises included in the workout.
- [ ] Each exercise block includes:
    - [ ] **Exercise Name:** Clearly displayed at the top of the block.
    - [ ] **Set List:** A numbered list of sets (1, 2, 3...).
    - [ ] **Set Inputs:** Each set row has:
        - [ ] **Weight Input:** Numeric field with unit label (e.g., "lbs" or "kg").
        - [ ] **Reps Input:** Numeric field for repetition count.
    - [ ] **Add Set Button:** Appends a new set row to the exercise.
    - [ ] **Delete Exercise Button:** A destructive action (typically an icon or "Delete" text) to remove the exercise from the workout.

### 3.3 Workout Actions
- [ ] **Create New Exercise Button:** A prominent button below the exercise list to manually add a new exercise block.
- [ ] **Session Notes:** A multiline text area labeled "SESSION NOTES" at the bottom of the list for qualitative feedback.
- [ ] **Finish & Log Workout Button:**
    - [ ] A large, primary action button at the very bottom (or floating).
    - [ ] Styling: Matches the design system's primary color (`#2b8cee`).
    - [ ] Action: Persists the workout to the database with `COMPLETED` status and navigates to the Ledger or back to the Canvas.

### 3.4 Data Integrity & Lifecycle
- [ ] **Atomic Updates:** When saving changes to an existing workout, the implementation must follow the project's atomic update pattern:
    1.  Clear all existing `WorkoutSet` entries for the `WorkoutExercise`.
    2.  Clear all existing `WorkoutExercise` entries for the `Workout`.
    3.  Re-insert the updated structure to ensure data consistency.
- [ ] **Persistence:** Changes are only persisted to the permanent `Workout`, `WorkoutExercise`, and `WorkoutSet` tables when "Finish & Log" is tapped.
- [ ] **Scribble Sync:** Tapping "Finish & Log" must also update the source `Scribble` status to `COMPLETED`.

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:workouts` with `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose.
    - Use `LazyColumn` for the exercise list.
    - Each exercise block should be a separate Composable (`ExerciseBlock`).
    - Use `OutlinedTextField` or custom thin-bordered inputs for weight/reps.
- **Data Models:** Use `WorkoutWithAllDetails` for fetching and representing the session.
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `WorkoutFeature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI.
    - Use `ScrollView` with `LazyVStack` for performance.
    - Custom row views for sets with `TextField` and `NumberFormatter`.
    - Apply `.glassEffect()` as per `DESIGN.md`.
- **Concurrency:** Swift 6 strict concurrency. Ensure all database updates are on `@MainActor`.

## 5. Validation
- **Unit Tests:**
    - Verify adding/removing sets updates the `WorkoutDetailsState` correctly.
    - Verify deleting an exercise removes it from the state.
    - Verify "Finish & Log" triggers the `LogWorkoutUseCase` with the correct aggregate data.
- **UI Tests:**
    - Verify the "Finish & Log" button is enabled only when at least one exercise with one valid set exists.
    - Verify date display and back button navigation.
