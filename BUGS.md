# Bug Report & Logic Gaps (April 15, 2026)

This document tracks identified bugs, data integrity issues, and architectural gaps in the ScribbleFit Android implementation.

## 🔴 Critical: Data Loss & Integrity

### 1. Scribble Deletion Integrity [FIXED]
*   **Fix:** Updated deletion logic to correctly use `ForeignKey.CASCADE` on `scribbles` -> `exercises` -> `sets`.

### 2. Pre-Confirmation Edits Are Discarded [FIXED]
*   **Fix:** `ConfirmScribbleUseCase` now persists UI edits to the database before marking status as `COMPLETED`.

### 3. Canvas Edits Are Not Persisted [FIXED]
*   **Fix:** `CanvasViewModel` now implements debounced persistence for exercise and set updates.

### 4. Data Export Loss (Incomplete JSON) [FIXED]
*   **Fix:** `exportUserData` now includes the full hierarchy (Scribble -> Exercise -> Set).

---

## 🟡 Major: Reliability & Performance

### 5. AI Parsing Race Condition [FIXED]
*   **Fix:** Added an in-memory tracking set in `ParsePendingScribblesUseCase` to prevent concurrent parsing of the same scribble.

### 6. Local AI Model Download "Hanging" [FIXED]
*   **Fix:** Updated `LLMEngine` implementation to check for local availability and fallback to Cloud engine immediately.

### 7. "Parsing" Status Deadlock [FIXED]
*   **Fix:** `ParsePendingScribblesUseCase` now retries `PARSING` scribbles on app start.

### 8. Redundant Database Queries [FIXED]
*   **Fix:** `GetScribblesForDateUseCase` optimized to use pre-fetched exercises from Room `@Relation`.

---

## 🔵 Minor / Maintenance

### 9. Brittle Date Arithmetic [FIXED]
*   **Fix:** DAOs updated to use robust local calendar range queries.

### 10. Insights Period Inconsistency [FIXED]
*   **Fix:** `InsightsViewModel` now passes selected date ranges to the AI overview use case.
