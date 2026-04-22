# Technical Debt & Architectural Improvements

This document tracks identified technical debt, performance bottlenecks, and deviations
from project guidelines. Fixed items are archived in `fixed_tech_debt.md`.

---

## Remaining Tech Debt

### 41. Mapping Logic in ScribbleRepositoryImpl (Android)
*   **Severity:** Low
*   **File:** `ScribbleRepositoryImpl.kt`
*   **Problem:** Contains significant mapping-like operations that should be moved to a dedicated mapper file in `:core:database`.
*   **Fix:** Extract mapping logic to `com.scribblefit.core.database.mapper`.

### 42. Date Pattern Hardcoded in State (Android)
*   **Severity:** Low
*   **Files:** `WorkoutExercisesState.kt`, `CanvasState.kt`
*   **Problem:** Date patterns are hardcoded in static formatters.
*   **Fix:** Move patterns to `strings.xml`.
