# Platform Gaps & Functional Discrepancies

This document tracks functional and feature gaps between Android and iOS. Native architectural differences (e.g., BottomBar vs TabView) are accepted and not listed as gaps.

## Feature: Canvas & AI

| Feature | Android Implementation | iOS Implementation | Gap Type | Status |
|---------|------------------------|--------------------|----------|--------|
| **AI Parsing Logic** | Returns structured `WorkoutResponseDto { exercises }` with `estimated1RM` and `intensity`. | Returns structured `WorkoutResponse { exercises }` with `estimated1RM` and `intensity`. | **Resolved** | Aligned |
| **Scribble Cards** | Shows `intensity` and `improvement` via UI-layer calculation in ViewModel. | Shows `intensity` and `improvement` via on-the-fly calculation in Store. | **Resolved** | Consistent |

## Feature: Ledger (History)

| Feature | Android Implementation | iOS Implementation | Gap Type | Status |
|---------|------------------------|--------------------|----------|--------|
| **Primary Data Model** | **Session-centric (Scribble)**. Groups scribbles by date, shows raw text + exercise pills. | **Session-centric (Scribble)**. Groups scribbles by date, shows raw text + exercise pills. | **Resolved** | Aligned |
| **Refresh** | Refresh button in header. | Refresh button in header. | Accepted | Native patterns |
| **Navigation** | Exercise pills navigate to Exercise Details. | Exercise pills navigate to Exercise Details via sheet. | **Resolved** | Aligned |

## Feature: Exercise Details

| Feature | Android Implementation | iOS Implementation | Gap Type | Status |
|---------|------------------------|--------------------|----------|--------|
| **Trends** | Shows 1RM, Intensity, Weight vs Last, and Last Volume trends. | Shows 1RM, Intensity, Weight vs Last, and Last Volume trends. | **Resolved** | Aligned |
| **AI Insights** | Returns unified `AIInsight` (insightType + text). | Returns unified `AIInsight` (insightType + text). | **Resolved** | Aligned |

## Summary

All critical platform gaps have been resolved. Both platforms now share:

1. **Unified AI models:** Same parsing structure (`WorkoutResponseDto`/`WorkoutResponse`) and insight model (`AIInsight`).
2. **Session-centric Ledger:** Both platforms display completed scribbles grouped by date with exercise pills for navigation.
3. **Full Exercise Trends:** Both platforms show 4 trend metrics (1RM, Intensity, Weight vs Last, Last Volume).
4. **Ledger → Exercise Details navigation:** Both platforms support tapping individual exercises to view details.
