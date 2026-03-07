# INTELLIGENT CANVAS SPECIFICATION: SCRIBBLEFIT

## 1. CONCEPTUAL OVERVIEW
The Intelligent Canvas is the primary interface where "Zero-UI" happens. It is not just a logging screen; it is a **dynamic conversation** between the user and their fitness data. It anticipates needs, provides real-time feedback during entry, and maintains a "Live Feed" of the current workout's state.

---

## 2. UI/UX REQUIREMENTS (PLATFORM-SPECIFIC)

### A. Android (Material 3 - "Expressive Minimalism")
*   **Input Component:** Use a `DockedSearchBar` style layout for the bottom pill. It should feel fixed but respond to the keyboard by lifting with a smooth `ImeAnimation`.
*   **Typography:** Utilize `HeadlineLarge` for the greeting and `TitleMedium` for quick action pills.
*   **Interactions:** Use `Predictive Back` to transition out of input focus. Quick action pills should use `Surface` with a tonal elevation of 0dp but a 1dp `Outline` in dark mode.
*   **States:** Processing states (Ghost Bubbles) should use a subtle `Brush.linearGradient` shimmer.

### B. iOS (Cupertino - "Clinical Precision")
*   **Input Component:** A custom `ScribbleInputBar` using `VisualEffectView` for a subtle backdrop blur.
*   **Haptics:** 
    *   `UIImpactFeedbackGenerator(.light)` when a Quick Action is tapped.
    *   `UINotificationFeedbackGenerator(.success)` when the AI parsing completes.
*   **Transitions:** Use `matchedGeometryEffect` to transform a Quick Action pill into a "Ghost Bubble" in the feed upon tapping.
*   **Keyboard:** Seamless integration with `toolbar` for auxiliary actions (e.g., photo attachment, unit toggle).

---

## 3. CORE FEATURES & LOGIC

### 1. The Contextual Feed
The feed is an append-only list of interactions for the "Current Session."
*   **Session Window:** A session is active for 4 hours since the last entry. After that, the canvas clears to a fresh greeting.
*   **Interaction Types:**
    *   `Prompt`: AI-generated suggestions (e.g., "Ready for Chest?").
    *   `Scribble`: The raw text typed/spoken by the user. Retained in the `Sync_Queue` table.
    *   `Confirmation`: A structured summary of what was parsed (with an "Edit" option).
    *   `Insight`: A real-time micro-insight (e.g., "That's a 5lb PR on Bench!").

### 2. Scribble History & Retries
Every user entry is a `SyncQueue` item that must be visible in the feed until successfully committed.
*   **Status Indicators:**
    *   `PENDING/PROCESSING`: Displayed as a "Ghost Bubble" with a shimmer effect.
    *   `FAILED`: Displayed with a soft red background/underline and a "Retry" button. The raw text remains editable in failure mode.
    *   `COMPLETED`: Replaced by the "Parsed Confirmation" card or a "Sync Success" checkmark.
*   **Retry Logic:** Tapping retry re-enqueues the item in `WorkManager`/`BGTaskScheduler`.
*   **Persistence:** Scribbles are never lost. Even if the app is killed, the feed is rebuilt from the `Sync_Queue` table on next launch.

### 3. Live Ghost Parsing
As the user types, the UI should provide visual confirmation that it "understands."
*   **Action:** If the user types "Bench 135x5", a small label above the keyboard might show "Exercise: Bench Press | Weight: 135".
*   **Logic:** This is handled by a high-speed, local heuristic parser before the full LLM sync.

---

## 4. ARCHITECTURAL COMPONENTS (REPOSITORIES & USE CASES)

### New Repositories
1.  **`CanvasRepository`**: 
    *   Manages the transient `FeedItem` list and persistent `Scribble` history.
    *   `fun getFeed(): Flow<List<FeedItem>>`
    *   `fun getScribbleHistory(): Flow<List<ScribbleItem>>`
    *   `suspend fun addScribble(text: String)`
    *   `suspend fun retryScribble(id: String)`
2.  **`ContextRepository`**:
    *   Aggregates data for the AI analysis (e.g., last 3 workouts, current location, body weight).
3.  **`WorkoutSessionRepository`**:
    *   Manages the "Active Workout" state. Unlike the permanent `Workout_Logs`, this tracks the sets *before* they are committed.

### New Use Cases
1.  **`ProcessScribbleUseCase`**: 
    *   Input: `rawText`. 
    *   Flow: Add to `CanvasRepository` as "Processing" -> Trigger AI Parser -> Update to "Parsed" -> Wait for user confirmation.
2.  **`GetHomeContextUseCase`**:
    *   Logic: Fetch last workout + recovery status -> Call `AnalysisEngine` -> Return `AnalysisSuggestion`.
3.  **`ConfirmWorkoutUseCase`**:
    *   Logic: Take the `ParsedWorkout` -> Save to `LedgerRepository` -> Clear the `ActiveSession` -> Add "Workout Saved" insight to Feed.
4.  **`ExecuteQuickActionUseCase`**:
    *   Logic: Map "Repeat last workout" to a specific `rawText` prompt and inject it into `ProcessScribbleUseCase`.

---

## 5. RECOVERY & OFFLINE LOGIC
*   **Draft Persistence:** If the app closes mid-scribble, the `CanvasRepository` must persist the current feed to ensure the user doesn't lose their "Live Session."
*   **Background Retries:** If the network is down during LLM parsing, the `SyncWorker` handles the retry, but the Canvas shows a "Waiting for connection..." status on the specific feed bubble.
