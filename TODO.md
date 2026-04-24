# ScribbleFit Implementation Roadmap

Current status and upcoming features for ScribbleFit (Android & iOS).

> [!IMPORTANT]
> **Foundation Stabilized**: Critical data loss bugs and architectural gaps identified in [BUGS.md](BUGS.md) and [TECH_DEBT.md](TECH_DEBT.md) have been resolved. See those files for fix details.

## Phase 2: History & Detailed Tracking

- [ ] **Ledger (Training History)**
  - [ ] Android: Implement `:feature:ledger` (Domain, Data, UI)
  - [ ] iOS: Implement `LedgerFeature` (Domain, Data, UI)
  - [ ] Functional: Chronological log of COMPLETED scribbles, empty states, loading shimmer.
  - [ ] Spec: `specs/ledger.md`

- [ ] **Scribble Details**
  - [ ] Android & iOS UI: Detailed breakdown of a completed or parsed session.
  - [ ] Functional: Navigation from Ledger, exercise list, set details.
  - [ ] Spec: `specs/scribble-details.md`

- [ ] **Exercise Details**
  - [ ] Android & iOS UI: Single exercise view with history/charts.
  - [ ] Functional: Navigation from Scribble Details or Search.
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
