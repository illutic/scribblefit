# ScribbleFit

A cross-platform fitness tracking app that turns freeform text scribbles into structured workout data using AI. Built natively for Android and iOS with an offline-first, editorial minimalist design.

## Concept

ScribbleFit lets users jot down workouts in natural language (e.g., "bench press 3x10 80kg"). An LLM parses the text into structured exercises, sets, and reps -- no forms, no dropdowns. The interface is designed to feel like a premium physical journal.

## Features

- **Canvas** -- Home screen for workout entry via freeform text scribbles, parsed by AI into structured data
- **Ledger** -- Scrollable workout history with date summaries and exercise details
- **Insights** -- AI-generated progress overviews, volume charts, frequency stats, and muscle distribution
- **Settings** -- Theme (Light/Dark/System), AI provider (Cloud/Local), unit preferences (kg/lbs), data export

## Architecture

Both platforms follow **MVI (Model-View-Intent)** with strict layer separation:

```
Domain (Models, Use Cases, Repository Interfaces)
  |
Data (Repository Implementations, Mappers, Persistence)
  |
UI (State, Intent, ViewModel/Store, Screens)
```

### Android

- **Language:** Kotlin
- **UI:** 100% Jetpack Compose
- **Persistence:** Room (offline-first)
- **DI:** Hilt
- **Concurrency:** Coroutines & Flow
- **Build:** Gradle with convention plugins (`build-logic/`)
- **Modules:** Multi-module (`core:database`, `core:designsystem`, `core:navigation`, `feature:canvas`, `feature:ai`, `feature:insights`, `feature:ledger`, `feature:settings`, etc.)

### iOS

- **Language:** Swift 6 (strict concurrency)
- **UI:** 100% SwiftUI (no UIKit)
- **Persistence:** SwiftData
- **Concurrency:** async/await, `@Observable`, `@MainActor`
- **Build:** Swift Package Manager
- **Target:** iOS 17+ (iOS 26 enhancements via `#available`)

## Design System

**"Editorial Minimalism" -- The Digital Atelier**

- Monochromatic palette (Rich Black on Pure White)
- Inter typeface with dramatic scale contrast
- Zero borders -- boundaries defined by white space and tonal shifts
- Glassmorphism for floating elements (navigation, bottom sheets)
- 12dp rounded corners, pill-shaped buttons

See [`guidelines/DESIGN.md`](guidelines/DESIGN.md) for the full specification.

## Project Structure

```
scribblefit/
├── apps/
│   ├── android/          # Native Android app
│   │   ├── app/          # Application module
│   │   ├── core/         # Shared modules (database, designsystem, navigation, network, config)
│   │   ├── feature/      # Feature modules (canvas, ai, insights, ledger, settings, etc.)
│   │   └── build-logic/  # Gradle convention plugins
│   └── ios/              # Native iOS app (SPM)
├── guidelines/           # Architecture & design standards
│   ├── DESIGN.md
│   ├── project/          # Platform-specific project guidelines
│   └── specification/    # Feature implementation workflows
└── specs/                # Feature specifications (canvas, ledger, insights, settings)
```

## AI Integration

ScribbleFit uses LLMs for:
- **Scribble Parsing** -- Natural language to structured workout data
- **Insights Generation** -- AI-powered progress summaries and trend analysis

Supports both cloud (Gemini API) and on-device local models. AI provider is user-configurable in Settings.

## Development

### Android

```bash
./gradlew :app:assembleDebug
```

### iOS

Open `apps/ios/ScribbleFit.xcodeproj` in Xcode or build via:

```bash
cd apps/ios && swift build
```

## Guidelines

- [`guidelines/DESIGN.md`](guidelines/DESIGN.md) -- Design system specification
- [`guidelines/project/android-project-guidelines.md`](guidelines/project/android-project-guidelines.md) -- Android architecture & patterns
- [`guidelines/project/ios-project-guidelines.md`](guidelines/project/ios-project-guidelines.md) -- iOS architecture & patterns
- [`guidelines/specification/android-guidelines.md`](guidelines/specification/android-guidelines.md) -- Android implementation workflow
- [`guidelines/specification/ios-guidelines.md`](guidelines/specification/ios-guidelines.md) -- iOS implementation workflow
