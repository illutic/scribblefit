# Platform Gaps & Differences (Android vs. iOS)

This document tracks architectural, behavioral, and feature differences between the Android and iOS implementations of ScribbleFit.

## 🏗️ Core Architecture

| Feature | Android Implementation | iOS Implementation | Notes |
| :--- | :--- | :--- | :--- |
| **Language** | Kotlin 2.0 | Swift 6.0 | Both use strict concurrency/coroutines. |
| **UI Framework** | Jetpack Compose (Material 3) | SwiftUI | Both follow monochromatic design. |
| **DI** | Hilt (Dagger) | Manual / Factory Pattern | Android uses compile-time DI; iOS uses constructor injection. |
| **ID Types** | `Long` (Database Auto-inc) | `UUID` | iOS uses unique identifiers for all models. |
| **Date Handling** | `Long` (UTC Epoch Milli) | `Date` (ISO8601/Internal) | Android uses explicit UTC start-of-day offsets. |

---

## 💾 Data Persistence & Mapping

| Feature | Android (Room) | iOS (SwiftData) |
| :--- | :--- | :--- |
| **Schema** | SQLite + Room | SwiftData (Models as schema) |
| **Relationships** | Junction Tables + `@Relation` | Native Swift Collections |
| **Atomic Updates** | manual clear-and-reinsert | Automatic via SwiftData context |
| **Status Enums** | Uppercase String (mapped) | Native Enum (mapped) |

---

## 🤖 AI Integration

| Feature | Android | iOS |
| :--- | :--- | :--- |
| **Local LLM** | Gemini Nano (AICore) | CoreML / Local Llama |
| **Fallback** | Manual Proxy in Data Layer | `RoutingLLMService` |
| **Parsing** | Background Coroutine | Swift Actor-based service |

---

## 📱 Feature Parity Status

### ✅ Parity
*   **Canvas**: Basic scribble entry and parsing.
*   **Settings**: API Key management, unit selection, theme toggling.
*   **Insights**: Volume and Frequency charts.

### ⚠️ Gaps (Android Needs)
*   **Ledger**: iOS has a more mature list view for historical workouts.
*   **Export/Import**: iOS implementation differs in JSON structure for UUIDs.
*   **Glassmorphism**: iOS uses native `ultraThinMaterial`; Android uses custom brushes/alpha.

### ⚠️ Gaps (iOS Needs)
*   **Confirmation UI**: Android has a detailed BottomSheet for editing exercises before confirming.
*   **Set Reordering**: Android recently added `ReorderSetsUseCase`.
*   **N+1 Optimization**: Android has specific Room `@Relation` optimizations that iOS handles differently.

---

## 🛠️ Implementation Nuances

*   **Status Management**: Android uses a `Status` string in the DB that must be `.uppercase()`ed to match the `ScribbleStatus` enum.
*   **ViewModel logic**: Android ViewModels map to `Intent -> State`; iOS uses `Store` patterns.
