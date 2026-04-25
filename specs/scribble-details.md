# Feature Specification: Scribble Details

## 1. Overview
The Scribble Details screen provides a comprehensive view of a single training session (a Scribble). It serves two primary purposes:
1.  **Review & Edit:** Acting as the final step in the "Scribble -> Parse -> Review" flow, where users can fine-tune parsed data before officially logging the session.
2.  **History View:** Providing a detailed breakdown of a completed session when accessed from the Ledger (Training History).

The screen allows for granular control over exercises, sets, weights, and repetitions, ensuring the logged data is 100% accurate.

## 2. User Stories
- **As a User**, I want to review the exercises and sets parsed from my scribble **so that** I can ensure the AI captured everything correctly.
- **As a User**, I want to manually adjust weights and reps for any set **so that** my logs reflect what I actually lifted.
- **As a User**, I want to add or remove sets from an exercise **so that** I can correct parsing errors or reflect last-minute changes.
- **As a User**, I want to add new exercises to the session **so that** I can include things I forgot to scribble.
- **As a User**, I want to add session-wide notes **so that** I can record how I felt, injuries, or specific focus areas.
- **As a User**, I want to delete an entire exercise from the session **so that** I can remove mis-parsed or unwanted entries.
- **As a User**, I want to "Confirm & Log" my scribble **so that** it is saved to my permanent history and contributes to my insights.

## 3. Acceptance Criteria

### 3.1 Header
- [x] Displays a **Back Button** (chevron-left) to return to the previous screen (Canvas or Ledger).
- [x] Displays the **Session Date** (Format: "Monday, October 23").
- [x] **Summary Stats Row:** Displays aggregate EXERCISES count, SETS count, and VOLUME total as StatCards.
- [ ] Sticky header behavior on scroll is preferred.

### 3.2 Exercise List (Read-Only View)
- [x] Displays a list of exercises included in the scribble in glassmorphism cards matching canvas visual style.
- [x] Each exercise card shows:
    - [x] **Exercise Name:** Bold 28sp with -1 kerning, matching LoggedScribbleCard.
    - [x] **Formatted Summary:** Weight, sets, and reps (e.g., "80.0 kg * 3 sets * 10 reps").
    - [x] **Stats:** EST. 1RM and INTENSITY stat cards when available.
- [x] **Navigation:** Tapping an exercise card navigates to Exercise Details.
- [ ] **Set List:** Individual set breakdown with weight/reps (not yet implemented — current view shows summary only).
- [ ] **Editing capabilities** (Add Set, Delete Exercise, weight/reps inputs) are not yet implemented. The current screen is read-only.

### 3.3 Session Actions
- [ ] **Create New Exercise Button:** A prominent button below the exercise list to manually add a new exercise block.
- [ ] **Session Notes:** A multiline text area labeled "SESSION NOTES" at the bottom of the list for qualitative feedback.
- [ ] **Confirm & Log Button:**
    - [ ] A large, primary action button at the very bottom (or floating).
    - [ ] Styling: Matches the design system's primary color (`#2b8cee`).
    - [ ] Action: Updates the scribble status to `COMPLETED` and navigates to the Ledger or back to the Canvas.

### 3.4 Data Integrity & Lifecycle
- [ ] **Atomic Updates:** When saving changes to an existing scribble, the implementation must follow the project's atomic update pattern:
    1.  Clear all existing `Set` entries for the `Exercise`.
    2.  Clear all existing `Exercise` entries for the `Scribble`.
    3.  Re-insert the updated structure to ensure data consistency.
- [x] **Persistence:** Changes are persisted to the `Scribble`, `Exercise`, and `Set` tables.

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:scribble` or `:feature:canvas` (Scribble Details) with `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose.
    - Use `LazyColumn` for the exercise list.
    - Each exercise block should be a separate Composable (`ExerciseBlock`).
    - Use `OutlinedTextField` or custom thin-bordered inputs for weight/reps.
- **Data Models:** Use `ScribbleWithExercises` for fetching and representing the session.
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `ScribbleFeature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI.
    - Use `ScrollView` with `LazyVStack` for performance.
    - Custom row views for sets with `TextField` and `NumberFormatter`.
    - Apply `.glassEffect()` as per `DESIGN.md`.
- **Concurrency:** Swift 6 strict concurrency. Ensure all database updates are on `@MainActor`.

## 5. Validation
- **Unit Tests:**
    - Verify adding/removing sets updates the `ScribbleDetailsState` correctly.
    - Verify deleting an exercise removes it from the state.
    - Verify "Confirm & Log" updates the status correctly.
- **UI Tests:**
    - Verify the "Confirm & Log" button is enabled only when at least one exercise with one valid set exists.
    - Verify date display and back button navigation.
