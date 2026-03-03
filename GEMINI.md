# GEMINI Context: ScribbleFit

## 🏋️ Project Overview
ScribbleFit is a fully native mobile fitness application designed to eliminate the friction of logging workouts. It uses AI to parse raw, messy gym shorthand (e.g., *"Bench 135x5, 135x5"*) into clean, structured database entries.

### Core Engineering Principles
1.  **Offline-First Resilience:** All data is written to a local queue and processed/synced in the background. The UI never blocks for network requests.
2.  **Utilitarian Minimalism:** Hyper-minimalist, airy, and text-focused UI. Pure white backgrounds, rich black text, and soft gray input pills.
3.  **Event-Driven Intelligence:** AI features are triggered by database insert events, not cron jobs.
4.  **Robust Abstraction:** Prioritize interfaces for all services to ensure testability and modularity. Avoid hardcoding; use configuration files or environment variables for all variable parameters.

## 🛠️ Tech Stack
- **Android:** Kotlin & Jetpack Compose, Room Database, WorkManager for background tasks.
- **iOS:** Swift & SwiftUI, SwiftData or SQLite.swift, `BGTaskScheduler` for background tasks.
- **Backend/API:** Ktor Edge Functions (Supabase or Cloudflare Workers) as an MCP orchestration server.
- **AI Engine:** OpenAI or Anthropic for high-speed, structured JSON parsing.

## 🏗️ Architecture
- **Local Storage (Source of Truth):** Room (Android) / SwiftData (iOS).
- **Offline-First Parser:** Background workers (`WorkManager`/`BGTaskScheduler`) process the `Sync_Queue`.
- **Edge API:** Orchestrates metadata sync, exercise dictionary seeding, and LLM proxying for text parsing.

## 🎨 UI/UX Design System
- **Aesthetic:** Minimalist, airy, text-focused.
- **Color Palette:** Pure White (`#FFFFFF`), Very Soft Gray (`#F7F7F8`), Rich Black (`#101010`).
- **Typography:** System Native (SF Pro on iOS, Roboto/Inter on Android).
- **Key Screens:** Intelligent Canvas (Home), Structured Ledger (Workout Summary), Insights Dashboard (Analytics), Exercise Library, and Duo Dashboard (Social).

## 🚀 Building and Running
The project structure is planned to be scaffolded into `apps/` and `api/` directories.

### Android (`apps/android`) - *Planned*
- **Build:** `./gradlew assembleDebug`
- **Run:** `./gradlew installDebug`
- **Test:** `./gradlew test`

### iOS (`apps/ios`) - *Planned*
- *Status: Scaffolding pending.*

### API (`api`) - *Planned*
- **Deploy:** `npm run deploy`
- **Dev:** `npm run dev`

## 📝 Development Conventions
- **Strict Adherence to Minimalism:** Do not deviate from the design system. Strip away default corporate styling.
- **Assume Offline:** Always read/write to the local database first.
- **Native Performance:** Avoid web-views or cross-platform wrappers.
- **Sync Logic:** All AI parsing must happen through the background sync queue to ensure offline resilience.

## 🗂️ Key Documentation
- `README.md`: Project overview and agent instructions.
- `ARCHITECTURE_SPEC.md`: Detailed local storage, background worker, and API specifications.
- `UI_UX_SPEC.md`: Global design system and screen-by-screen requirements.
