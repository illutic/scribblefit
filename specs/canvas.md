# Feature Specification: Canvas

## 1. Overview
The Canvas is the home screen of ScribbleFit, designed for rapid, low-friction entry of training data. It allows users to "scribble" their sessions in plain text (e.g., "Bench press 100kg 3x10"). These scribbles are then processed by an LLM to extract structured exercise data.

The feature follows an offline-first approach, where scribbles are saved locally before being processed. It integrates with the `:feature:ai` module for session parsing and insights generation.

## 2. User Stories
- **As a fitness enthusiast**, I want to quickly type my training data in plain text **so that** I don't spend time navigating complex menus during my session.
- **As a user**, I want to see the parsing status of my scribbles **so that** I know when they have been successfully processed.
- **As a user**, I want to navigate to previous days **so that** I can review my past training logs.
- **As a user**, I want to receive AI-generated insights about my training patterns directly on the Canvas **so that** I can stay motivated and informed.

## 3. Acceptance Criteria

### 3.1 Core Functionality
- [ ] **Offline-First Scribbles:** Users can submit raw text scribbles even when offline. Scribbles are stored in the local database immediately.
- [x] **Scribble Lifecycle:** Scribbles must transition through the following statuses: `PENDING` (stored locally), `PARSING` (sent to LLM), `SUCCESS` (parsed), `FAILED` (parsing error), and `COMPLETED` (confirmed by user and officially logged).
- [x] **Date Persistence:** Scribbles are associated with a specific date. The UI must only show scribbles for the currently selected date.
- [x] **Real-time Updates:** The list of scribbles must update reactively as statuses change.

### 3.2 UI Components (Contextual Splitting)
The UI must strictly adhere to the "The Input Canvas (Home)" design project.

**Design Tokens (Project Global):**
- **Font:** Inter
- **Roundness:** 12dp (Round Twelve)
- **Primary Color:** #2b8cee
- **Saturation:** 3

#### **Header**
- [ ] Displays the app title "ScribbleFit".
- [ ] **Date Navigation:**
    - [ ] Displays the selected date (Format: "Monday, March 16").
    - [ ] Previous/Next chevron buttons to navigate between days.
    - [ ] Prevent navigation to future dates.
    - [ ] Limit backward navigation to 30 days.
    - [ ] Tapping the date opens a native Date Picker for quick selection.
- [ ] **Settings Button:** A trailing icon button that navigates to the Settings screen.
- [ ] **Profile Button:** An icon button next to Settings.

#### **Body**
The body must handle four distinct states as defined in the design:

1.  **Default Empty (`Canvas Screen - Default Empty`):**
    - [ ] Displays "What did you lift today?" message.
    - [ ] Centered graphic or illustration.
2.  **Pending (`Canvas Screen - Pending`):**
    - [x] Shows raw text scribbles with a "PENDING" badge.
    - [x] Status indicator: Simple text label or subtle pulse effect.
3.  **Parsed (`Canvas Screen - Parsed/Completed`):**
    - [x] Shows structured exercise data (Exercise Name, Sets x Reps @ Weight). Verified fallback for empty exercises in `ScribbleCard.swift`.
    - [x] Border color matches status (e.g., light gray for parsed).
4.  **Completed (`Canvas Screen - Parsed/Completed`):**
    - [x] Shows "COMPLETED" badge in success green.
    - [x] Non-interactive once confirmed. Verified fallback for empty exercises in `ScribbleCard.swift`.
    - [x] **Exercise-Level Navigation (2026-04-21):** Individual exercises within logged scribble cards are tappable and navigate to the Exercise Details screen. Tapping the card itself (non-exercise area) navigates to the **Scribble Details** screen.

- [ ] **AI Insights Section:**
    - [ ] Displays motivational summaries or patterns at the top of the body.
    - [ ] Integrated with `GetAIOverviewUseCase`.

- [x] **Scribble Confirmation Sheet (`Canvas Screen - Confirm/Edit/Delete parsed exercise`):**
    - [x] Triggered by tapping a parsed scribble.
    - [x] **Confirm Button:** Primary action, styled with the project's primary color. Marks the scribble as `COMPLETED`.
    - [x] **Edit Button:** Secondary action, opens manual adjustment form.
    - [x] **Delete Button:** Destructive action, styled in red.

#### **Footer**
- [ ] **Scribble Input**
- [ ] **Floating Navigation Bar**
- 
### 3.3 Unit & Preference Support
- [ ] **Weight Unit:** Displays weight in `kg` or `lbs` based on the user's preference stored in `ConfigRepository`.

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:canvas` with sub-modules `:data`, `:domain`, `:ui`.
- **State Management:**
    - Use a single `CanvasState` data class for the entire screen.
    - Handle string resolution in the `State` layer using `@Composable @ReadOnlyComposable` extensions if needed.
- **Use Cases:**
    - `GetScribblesForDateUseCase`: Returns a `Flow<List<Scribble>>`.
    - `AddScribbleUseCase`: Persists a new raw text scribble.
    - `ConfirmScribbleUseCase`: Updates the scribble status to `COMPLETED`.
    - `GetAIOverviewUseCase`: Fetches insights from `:feature:ai:domain`.
- **AI Integration:** Use `LLMEngine.parseScribble(rawText)` from `:feature:ai:data` for processing.
- **Database:** Room entities for `Scribble` with a `status` field.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `CanvasFeature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI. Use `.glassEffect()` for the footer input and bottom navigation bar.
- **Database:** `SwiftData` for `Scribble` entity persistence.
- **Theming:** Use `ScribbleFitTheme` Color extensions. Ensure parity with Android (Light/Dark mode).
- **Concurrency:** Use `AsyncSequence` for reactive database updates.

## 5. Validation
- **Unit Tests:**
    - `CanvasViewModel` (Android) / `CanvasStore` (iOS) state transitions.
    - `AddScribbleUseCase` business logic (e.g., preventing future dates).
- **Integration Tests:**
    - Database insertion and retrieval of `Scribble` entities.
    - Mapping between `Scribble` (Data) and `Scribble` (Domain Model).
- **UI Tests:**
    - Verify "Send" button enablement logic.
    - Verify date navigation updates the scribble list.
    - Verify the bottom sheet appears when tapping a successful scribble.
