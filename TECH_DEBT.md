# Technical Debt & Architectural Improvements

This document tracks identified technical debt, performance bottlenecks, and deviations from project guidelines in the ScribbleFit Android implementation.

## 🏛️ Architectural Deviations

### 1. Incomplete Atomic Update Pattern [FIXED]
*   **Fix:** Implemented `ConfirmScribbleUseCase` to sync UI edits back to DB before re-linking. Fixed DAO methods for targeted updates.

### 2. Business Logic in ViewModels [FIXED]
*   **Fix:** Moved set re-indexing logic to `ReorderSetsUseCase` in the domain layer.

### 3. Brittle Date Calculations [FIXED]
*   **Fix:** DAOs updated to use robust range queries instead of fixed millisecond constants.

---

## ⚡ Performance & Efficiency

### 4. N+1 Query Problem on Canvas [FIXED]
*   **Fix:** `GetScribblesForDateUseCase` now uses the `@Relation` data from `ScribbleWithExercises` directly.

### 5. Over-Collection in Insights [FIXED]
*   **Fix:** `InsightsViewModel` refactored to use `stateIn` with `flatMapLatest`.

---

## 🛡️ Resilience & UX

### 6. Stuck "Parsing" Status Recovery [FIXED]
*   **Fix:** `ParsePendingScribblesUseCase` now retries `PARSING` scribbles on app start and includes a session-level tracker to prevent redundant parsing.

### 7. Generic Error Handling
*   **Issue:** Use cases return generic `Result<Unit>`, losing semantic error context (e.g., "Invalid LLM Format" vs "Database Constraint Violation").
*   **Improvement:** Standardize on typed error classes (e.g., `ScribbleError`, `WorkoutError`) to allow the UI to provide more specific feedback/retry logic.

## 📐 Guideline Violations (UI & Layout)

### 10. Missing Adaptive Width Constraints [FIXED]
*   **Fix:** Applied `widthIn(max = 600.dp)` to content columns in `Canvas`, `Insights`, `Ledger`, and `ScribbleConfirmationBottomSheet`.

### 11. Hardcoded Strings in UI [FIXED]
*   **Fix:** Moved "Set", "reps", and "x" strings to `strings.xml` and resolved them via `CanvasState`.

### 12. Missing IME Padding on Canvas [FIXED]
*   **Fix:** Applied `.imePadding()` to the root layout of `CanvasScreen`.

### 13. Insecure Client-Side API Keys
*   **Issue:** `GEMINI_API_KEY` is injected via `BuildConfig` and stored in the application binary. This is insecure for production as it can be reverse-engineered.
*   **Solution:** For production release, migrate to Firebase Vertex AI (which uses Firebase App Check for security) or implement a backend proxy to keep keys off the client.

---

## 🍎 iOS Specific Debt

### 14. Single-Component File Pattern Violations
*   **Issue:** Several iOS UI files (e.g., `SettingsView.swift`, `ScribbleCard.swift`) "dump" multiple View structs into a single file instead of extracting them to the `Components/` folder.
*   **Refactor Needed:** Extract `AppearanceSection`, `AIConfigurationSection`, `UnitPreferencesSection`, `DataManagementSection`, and `FooterSection` from `SettingsView.swift`. Extract status-specific cards from `ScribbleCard.swift`.
