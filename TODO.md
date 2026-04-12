# ScribbleFit Implementation Roadmap

Current status and upcoming features for ScribbleFit (Android & iOS).

## Phase 2: History & Detailed Tracking

- [ ] **Ledger (Workout History)**
  - [ ] Android: Implement `:feature:ledger` (Domain, Data, UI)
  - [ ] iOS: Implement `LedgerFeature` (Domain, Data, UI)
  - [ ] Functional: Chronological workout log, empty states, loading shimmer.
  - [ ] Spec: `specs/ledger.md`

- [ ] **Workout Details**
  - [ ] Android & iOS UI: Detailed breakdown of a completed workout.
  - [ ] Functional: Navigation from Ledger, exercise list, set details.
  - [ ] Spec: `specs/workout-details.md`

- [ ] **Exercise Details**
  - [ ] Android & iOS UI: Single exercise view with history/charts.
  - [ ] Functional: Navigation from Workout Details or Search.
  - [ ] Spec: `specs/exercise-details.md`

## Phase 3: Manual Entry & Direct Control

- [ ] **Add Exercise Bottom Sheet**
  - [ ] Android & iOS UI: Structured form for direct exercise entry.
  - [ ] Functional: Exercise name, muscle group, dynamic set management.
  - [ ] Spec: `specs/add-exercise-bottom-sheet.md`

- [ ] **Manual Mode**
  - [ ] Global toggle in Settings.
  - [ ] Canvas update: Switch between AI Scribble and "Add Exercise" button.
  - [ ] Domain: Unified `Scribble` success status for manual entries.
  - [ ] Spec: `specs/manual-mode.md`
