# Feature Specification: Manual Mode (Direct Exercise Entry)

## 1. Overview
Manual Mode provides an alternative training data entry method that bypasses the AI scribble parser entirely.
When enabled, the Canvas screen replaces the freeform text input with an **"Add Exercise"** button
that opens a structured form (bottom sheet) for entering exercises, sets, weights, and reps directly.

This serves users who prefer explicit control over their data, have no API key configured, or are in
environments where AI parsing is unreliable. Manual Mode and AI Mode are mutually exclusive — the
user toggles between them via a setting. The toggle is a global preference stored in `SystemConfig`.

## 2. User Stories
- **As a User**, I want to toggle Manual Mode in Settings **so that** I can choose between AI-parsed
  scribbles and direct exercise entry.
- **As a User**, I want to add exercises via a structured form **so that** I don't rely on AI parsing
  accuracy.
- **As a User**, I want to specify exercise name, muscle group, and multiple sets (weight × reps)
  **so that** my training data is precise.
- **As a User**, I want to add and remove individual sets within the form **so that** I can log
  variable set counts.
- **As a User**, I want to add optional notes to an exercise **so that** I can track RPE, tempo, or
  other observations.
- **As a User**, I want manually-added exercises to appear in the Canvas alongside any existing
  scribbles **so that** I have a unified view of my session.
- **As a User**, I want manually-added exercises to flow through the same confirm → log pipeline
  **so that** they appear in Insights and the Ledger identically to AI-parsed entries.

## 3. Acceptance Criteria

### 3.1 Settings Toggle
- [ ] A **Manual Mode** toggle appears in the **General** section of Settings, positioned as the
  first item (above Appearance).
- [ ] Label: "Manual Mode". No description text needed — the toggle is self-explanatory.
- [ ] Default value: **OFF** (AI Mode).
- [ ] The toggle persists across app restarts via `SystemConfig`.
- [ ] When toggled ON, the Canvas screen immediately reflects the change (reactive via config flow).

### 3.2 Canvas Screen Changes
- [ ] **When Manual Mode is OFF (AI Mode):** No changes. The existing freeform text input bar and AI
  parsing flow remain.
- [ ] **When Manual Mode is ON:**
    - [ ] The freeform text input bar is **hidden**.
    - [ ] An **"Add Exercise"** button appears at the bottom of the Canvas (same position as the
      input bar, floating above the bottom nav).
    - [ ] The button uses the design system's pill shape with primary styling.
    - [ ] Tapping the button opens the **Add Exercise Bottom Sheet** (shared component).
    - [ ] Existing scribble cards remain visible and interactive.
    - [ ] AI insight cards remain visible (they operate on training data, not raw scribbles).

### 3.3 Add Exercise Bottom Sheet
Defined in `specs/add-exercise-bottom-sheet.md`. This shared component handles the input form for exercise details, sets, and notes.

### 3.4 Data Pipeline Integration
- [ ] When invoked from the Canvas in Manual Mode, saving the bottom sheet produces a `Scribble` object with status `SUCCESS`.
- [ ] The `rawText` field on these scribbles should contain a human-readable summary (e.g., "Bench Press 100kg 3×10").
- [ ] The confirmation flow, persistence, Insights aggregation, and Ledger display treat manual entries identically to AI-parsed entries.

### 3.5 Contextual UI
- [ ] **Header:** Same as Canvas — date navigation, settings icon.
- [ ] **Body:** Scribble list (same as AI mode) + "Add Exercise" button replacing the input bar.
- [ ] **Footer:** Bottom navigation bar (shared across the app).

## 4. Development Guidelines (Android)
- **Architecture:** MVI. No new modules needed — changes live in `:feature:canvas:ui`,
  `:feature:canvas:domain`, `:core:config:domain`.
- **Config Change:** Add `isManualMode: Boolean = false` to `SystemConfig`. Follow the 8-step
  synchronization pattern (see `android-project-guidelines.md` Section 6).
- **Canvas UI:**
    - `CanvasState` gains `isManualMode: Boolean` derived from `ConfigRepository.config`.
    - `CanvasIntent` gains `AddExerciseManually` (opens sheet) and `SaveManualExercise(...)`.
    - `CanvasScreen` conditionally renders the input bar or the "Add Exercise" button.
    - `ManualExerciseBottomSheet` is a new composable in `:feature:canvas:ui`.
- **Domain:** Add `AddManualExerciseUseCase` in `:feature:canvas:domain` that creates a `Scribble`
  with status `SUCCESS` and the structured exercise data, then persists it via `ScribbleRepository`.
- **DI:** Provide the new use case via the existing `:feature:canvas:data` Hilt module.
- **Settings UI:** Add the toggle in `SettingsSections.kt` under a new "General" section at the top.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI. Changes live in `FeatureCanvas` and `FeatureSettings` targets.
- **Config Change:** Add `isManualMode: Bool = false` to `SystemConfig`. Update
  `ConfigRepositoryImpl` to persist via `UserDefaults`.
- **Canvas UI:**
    - `CanvasState` gains `isManualMode: Bool` from config observation.
    - `CanvasIntent` gains `.addExerciseManually` and `.saveManualExercise(...)`.
    - `CanvasView` conditionally renders `ScribbleInputBar` or an "Add Exercise" button.
    - `ManualExerciseSheet` is a new SwiftUI view presented as `.sheet()`.
- **Domain:** Add `AddManualExerciseUseCase` in `FeatureCanvas/Domain/UseCases/` that creates a
  `Scribble` with status `.success` and the structured exercise data.
- **Settings UI:** Add the toggle in `SettingsView.swift` under a new "General" section using a
  native SwiftUI `Toggle`.

## 5. Stitch Design References

| Screen | Description |
|--------|-------------|
| Settings with Manual Mode Toggle | Manual Mode toggle as first item in GENERAL section |
| ScribbleFit Canvas (Manual Entry) | Canvas with "Add Exercise Manually" button at bottom, coexisting with AI-parsed scribble cards |
| Manual Add Exercise Bottom Sheet | Form: exercise name, muscle group, sets (weight × reps), add/remove set, notes, save/cancel |
| Confirm Exercise (No Nav) | Existing confirmation bottom sheet — unchanged, manual entries use it |

## 6. Out of Scope
- Exercise name autocomplete/suggestions (future enhancement).
- Muscle group picker with predefined options (text input for now; picker is a future enhancement).
- Hybrid mode (AI + manual simultaneously) — the modes are mutually exclusive via toggle.
- Bulk entry (adding multiple exercises at once) — one exercise per sheet invocation.

## 7. Validation
- **Unit Tests:**
    - `AddManualExerciseUseCase`: Verify it creates a `Scribble` with `SUCCESS` status, correct
      exercises/sets, and a generated `rawText`.
    - `CanvasViewModel`/`CanvasStore`: Verify `isManualMode` reacts to config changes.
    - Config persistence: Verify `isManualMode` round-trips through `SystemConfig`.
- **UI Tests:**
    - Verify the input bar is hidden and "Add Exercise" button is visible when Manual Mode is ON.
    - Verify the bottom sheet opens, accepts input, and produces a scribble card on save.
    - Verify the confirmation flow works identically for manual entries.
