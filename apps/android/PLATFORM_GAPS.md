# Platform Gaps & Functional Discrepancies

This document tracks functional and feature gaps between Android and iOS. Native architectural differences (e.g., BottomBar vs TabView) are accepted and not listed as gaps.

## Feature: Canvas & AI

| Feature | Android Implementation | iOS Implementation | Gap Type |
|---------|------------------------|--------------------|----------|
| **AI Parsing Logic** | Returns `List<Exercise>`. Prompt is simpler. | Returns `WorkoutResponse { exercises: [...] }`. Prompt requests `estimated1RM` and `intensity`. | **Functional High** |
| **Scribble Cards** | Shows `intensity` and `improvement` via UI-layer calculation. | Shows `intensity` and `improvement` via on-the-fly calculation in Store. | Logic Parity |

## Feature: Ledger (History)

| Feature | Android Implementation | iOS Implementation | Gap Type |
|---------|------------------------|--------------------|----------|
| **Primary Data Model** | **Exercise-centric**. Shows individual exercise entries. | **Session-centric (Scribble)**. Groups exercises by "scribble" session. | **Functional High** |
| **Refresh** | No Pull-to-Refresh. | Supports Pull-to-Refresh. | Feature Gap |
| **Navigation** | Navigate to Exercise Details from list. | Navigation to Exercise Details is missing in Ledger. | Navigation |

## Feature: Exercise Details

| Feature | Android Implementation | iOS Implementation | Gap Type |
|---------|------------------------|--------------------|----------|
| **Trends** | Only shows 1RM trend. | Shows 1RM, Intensity, Weight vs Last, and Volume trends. | **Feature Gap** |
| **AI Insights** | Returns generic `AIInsight` (text). | Returns structured `ExercisePerformanceInsight` (1RM, PR, Trend). | **Functional High** |

## Summary of Critical Gaps

1.  **AI Brain Inconsistency:** The prompts and expected JSON schemas for workout parsing and exercise insights differ significantly. This means the AI behavior and accuracy will vary between platforms.
2.  **Ledger Philosophy:** Android treats the history as a log of exercises; iOS treats it as a log of scribbles (sessions).
3.  **Insight Depth:** iOS provides structured data for exercise-specific insights (PR detection, trend direction), while Android only provides a text summary.
4.  **Exercise Trends:** iOS now provides more granular metrics (Intensity and session-over-session improvement) which are missing from the Android Exercise Details screen.
