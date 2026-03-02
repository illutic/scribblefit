# 🏋️ ScribbleFit: Native "Zero-UI" Fitness Tracker

## 📖 Project Overview

ScribbleFit is a fully native mobile fitness application designed to eliminate the friction of
logging workouts. Instead of navigating complex dropdown menus mid-workout, users input raw, messy
gym shorthand via text or voice (e.g., *"Bench 135x5, 135x5. Incline taken so did DBs"*). A
background AI parser instantly translates this into clean, structured local database entries.

## 🎯 Core Engineering Principles

1. **Offline-First Resilience:** Gym network connections are unreliable. The app must never block
   the UI waiting for a network request. All data is written to a local queue and processed/synced
   quietly in the background.
2. **Utilitarian Minimalism:** The UI is hyper-minimalist, airy, and text-focused. We use a crisp
   Light Mode (pure white backgrounds, rich black text, soft gray input pills) with zero heavy drop
   shadows or cluttered gridlines.
3. **Event-Driven Intelligence:** AI features do not run on wasteful cron jobs. They are triggered
   strictly by database insert events.

## 🛠️ Tech Stack

* **iOS Target:** Swift & SwiftUI. Local Storage via SwiftData or SQLite.swift. Background tasks via
  `BGTaskScheduler`.
* **Android Target:** Kotlin & Jetpack Compose. Local Storage via Room Database. Background tasks
  via `WorkManager`.
* **Backend / API:** The API should be a KTOR server application acting as an MCP orchestration server.
* **AI Engine:** OpenAI or Anthropic for high-speed, strictly structured JSON parsing.

## 🗂️ Documentation Reference

* **`UI_UX_SPEC.md`:** Contains the exact visual requirements and component hierarchies for the core
  screens.
* **`ARCHITECTURE_SPEC.md`:** Contains the background queue logic, local database schemas, and API
  endpoint structures.

## ⚠️ Agent Instructions

* **Strict Adherence:** Do not deviate from the minimalist design system. Rely on native UI
  frameworks (SwiftUI/Compose) but strip away default corporate styling to achieve the requested
  clean aesthetic.
* **Assume Offline:** When writing data fetching or posting logic, always read/write to the local
  database first.
* **Native Performance:** Avoid web-views or cross-platform wrappers.