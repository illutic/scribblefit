# Platform Gaps & Differences (Android vs. iOS)

This document tracks architectural, behavioral, and feature differences between the Android and iOS
implementations of ScribbleFit.

## 🏗️ Core Architecture

| Feature           | Android Implementation       | iOS Implementation        | Notes                                                         |
|:------------------|:-----------------------------|:--------------------------|:--------------------------------------------------------------|
| **Language**      | Kotlin 2.0                   | Swift 6.0                 | Both use strict concurrency/coroutines.                       |
| **UI Framework**  | Jetpack Compose (Material 3) | SwiftUI                   | Both follow monochromatic design.                             |
| **DI**            | Hilt (Dagger)                | Manual / Factory Pattern  | Android uses compile-time DI; iOS uses constructor injection. |
| **ID Types**      | `Long` (Database Auto-inc)   | `UUID`                    | iOS uses unique identifiers for all models.                   |
| **Date Handling** | `Long` (UTC Epoch Milli)     | `Date` (ISO8601/Internal) | FIXED: Both now use 'Local Day Boundary Rule' (system timezone). |

---

## 💾 Data Persistence & Mapping

| Feature            | Android (Room)                | iOS (SwiftData)                 | Notes |
|:-------------------|:------------------------------|:--------------------------------| :--- |
| **Schema**         | SQLite + Room                 | SwiftData (Models as schema)    | |
| **Relationships**  | Junction Tables + `@Relation` | Direct `@Relationship` (shared entities) | FIXED: Both use 'Object Identity Synchronization' to prevent data loss. |
| **Atomic Updates** | manual clear-and-reinsert     | Automatic via SwiftData context | |
| **Status Enums**   | Uppercase String (mapped)     | Native Enum (mapped)            | |

---

## 🦾 Implementation Logic

| Feature | Android Baseline | iOS Implementation | Notes |
| :--- | :--- | :--- | :--- |
| **Scribble Creation** | Local Day Boundary | Local Day Boundary | FIXED: iOS removed forced UTC normalization. |
| **Confirmation Flow** | Manual re-linking in DB | Deep-copy + workoutId Linkage | FIXED: iOS now uses `workoutId` to link scribbles and workouts. |
| **Set Reordering** | `ReorderSetsUseCase` | `ReorderSetsUseCase` | PARITY: Both use shared domain use case logic. |
| **Deletion Integrity** | Orphaned-only exercise cleanup | Orphaned-only exercise cleanup | FIXED: iOS now checks for workout links before deleting exercises. |

---

## 📊 Insights & Analytics

| Feature | Android Baseline | iOS Implementation | Notes |
| :--- | :--- | :--- | :--- |
| **Data Flow** | Reactive `Flow<T>` from Repository | Reactive `AsyncStream<T>` | PARITY: Both platforms are now fully reactive. |
| **Calculation Logic** | Performed in Repository layer | Performed in Use Case layer | |
| **Period Management** | `flatMapLatest` on period change | Observation tasks in `Store` | |

---

## 📜 Ledger & History

| Feature | Android Baseline | iOS Implementation | Notes |
| :--- | :--- | :--- | :--- |
| **Data Fetching** | Continuous `Flow<List<T>>` | Reactive `AsyncStream<T>` | PARITY: Both use persistent data streams. |
| **Grouping** | Consolidated Daily Card | Consolidated Daily Card | PARITY: Both group multiple sessions into a single daily card. |
| **Date Selection** | `ScribbleFitDateRangePickerDialog`| `Sheet` with Start/End pickers | Both allow flexible range selection. |
| **Persistence** | `updateWorkout` supported | `saveWorkout` supported | PARITY: Both platforms can edit historical workouts. |

---

## 🦾 Implementation Logic

| Feature | Android Baseline | iOS Implementation | Notes |
| :--- | :--- | :--- | :--- |
| **Local LLM** | Gemini Nano (AICore) | CoreML / Local Llama | |
| **Fallback** | Manual Proxy in Data Layer | `RoutingLLMService` | |
| **Parsing Prompt** | Dotprompt (Handlebars) | Dotprompt (Handlebars) | PARITY: Both use identical prompt templates. |
| **JSON Mapping** | Uses `snake_case` in DTOs. | Native `Codable` (mapped) | |
| **Default Provider** | Gemini (Cloud) | Gemini (Cloud) | PARITY: Both default to Gemini 2.5 Flash Lite. |

---

## 📱 Feature Parity Status

### ✅ Parity

* **Canvas**: Basic scribble entry and parsing.
* **Settings**: API Key management, unit selection, theme toggling.
* **Insights**: Volume and Frequency charts (Matched aesthetics).
* **Multi-Exercise UI**: Both platforms correctly list all parsed exercises in cards.
* **Confirmation UI**: Both platforms provide detailed editing before saving.
* **Set Reordering**: Sequential integrity maintained on both platforms.
* **Historical Edits**: Edits to completed workouts persist correctly.
* **Date Integrity**: All queries respect local day boundaries.

### ⚠️ Gaps (Android Needs)

* **Export/Import**: iOS implementation differs in JSON structure for UUIDs.
* **Glassmorphism**: iOS uses native `ultraThinMaterial`; Android uses custom brushes/alpha.

### ⚠️ Gaps (iOS Needs)

*   **N+1 Optimization**: Android has specific Room `@Relation` optimizations that iOS handles differently.

---

## 🛠️ UI Implementation Nuances

*   **Status Management**: Android uses a `Status` string in the DB that must be `.uppercase()`ed to match the `ScribbleStatus` enum.
*   **Edit Handling**: Both platforms now use domain-layer logic for consistent updates, though Android uses targeted use cases while iOS uses a broader `UpdateScribbleWithWorkoutUseCase`.

