# Bug Report & Logic Gaps (April 15, 2026)

This document tracks identified bugs, data integrity issues, and architectural gaps in the ScribbleFit Android implementation.

## 🔴 Critical: Data Loss & Integrity

### 1. Scribble Deletion Wipes Confirmed Workouts [FIXED]
*   **Fix:** Updated `clearScribbleExercises` to only delete `workout_exercise` entries with `workoutId IS NULL`.

### 2. Pre-Confirmation Edits Are Discarded [FIXED]
*   **Fix:** `ConfirmScribbleUseCase` now persists UI edits to the database before linking to a workout.

### 3. Canvas Edits Are Not Persisted [FIXED]
*   **Fix:** `CanvasViewModel` now calls persistence use cases for completed workouts.

### 4. Data Export Loss (Incomplete JSON) [FIXED]
*   **Fix:** `exportUserData` now includes exercise sets for scribbles.

---

## 🟡 Major: Reliability & Performance

### 5. AI Parsing Race Condition [FIXED]
*   **Fix:** Added an in-memory tracking set in `ParsePendingScribblesUseCase` to prevent concurrent parsing of the same scribble.

### 6. Local AI Model Download "Hanging" [FIXED]
*   **Fix:** Updated `LLMEngineProxy` to check `isSupported()` (which now checks for `AVAILABLE` status) and fallback to Cloud engine immediately. `LocalAIEngine` no longer blocks on download.

### 7. "Parsing" Status Deadlock [FIXED]
*   **Fix:** `ParsePendingScribblesUseCase` now retries `PARSING` scribbles on app start.

### 8. Redundant Database Queries [FIXED]
*   **Fix:** `GetScribblesForDateUseCase` optimized to use pre-fetched exercises from Room `@Relation`.

---

## 🔵 Minor / Maintenance

### 9. Brittle Date Arithmetic [FIXED]
*   **Fix:** DAOs updated to use robust range queries (`createdAt >= :date AND createdAt < :date + 24h`) instead of fuzzy `ABS` or fixed constants.

### 10. Insights Period Inconsistency [FIXED]
*   **Fix:** `InsightsViewModel` now passes selected date ranges to the AI overview use case.
