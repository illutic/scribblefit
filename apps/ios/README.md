# ScribbleFit iOS App

Native iOS application built with SwiftUI, focusing on offline-first resilience and minimalist design.

## 🚀 Tech Stack
- **Language:** Swift
- **UI Framework:** SwiftUI
- **Local Database:** SwiftData / SQLite.swift
- **Background Tasks:** `BGTaskScheduler`

## 🏗️ Architecture Highlights
- **Offline-First:** All writes hit the local `Sync_Queue` first.
- **Event-Driven AI:** Background tasks process the sync queue using `URLSession` background configurations.
- **Design:** Hyper-minimalist, text-focused UI following the ScribbleFit Design System.

## 📝 Development Conventions
- **Minimalism First:** Avoid third-party UI libraries. Stick to standard SwiftUI components styled according to the `UI_UX_SPEC.md`.
- **Offline-First:** No UI action should ever block on a network request. All data-changing operations must be written to the local database first.

## 🛠️ Development Guidelines
- **Architecture:** Follow **MVVM** or SwiftUI's native state management patterns (Observable, State, Binding) with a clear separation of concerns.
- **UI:** 100% SwiftUI. Prioritize native system components and styling.
- **Concurrency:** Use modern **Swift Concurrency** (async/await, Tasks) for all asynchronous operations.
- **Data Persistence:** Use **SwiftData** (or SQLite.swift if targeting older OS versions) for local storage.
- **Dependency Injection:** Prefer native SwiftUI Dependency Injection (Environment objects/values) or simple constructor injection.
- **Testing:**
    - **Unit Tests:** Mandatory for all business logic and data parsing using **XCTest**.
    - **UI Tests:** Use XCUITest for verifying critical user flows.
- **Code Style:** Adhere to the [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/).

## 🛠️ Development
- **Setup:** 
  - Ensure you have **Xcode 15+** and **CocoaPods/Swift Package Manager** (SPM) configured.
  - Open `ScribbleFit.xcodeproj` or `ScribbleFit.xcworkspace` in Xcode.
- **Build:** `xcodebuild -scheme ScribbleFit -archivePath build/ScribbleFit.xcarchive archive` (or build directly in Xcode).
- **Run:** Use the Xcode Play button or `xcrun simctl` to launch on a simulator.
- **Testing:** 
  - Run tests in Xcode using `Cmd + U`.
  - CLI: `xcodebuild test -scheme ScribbleFit -destination 'platform=iOS Simulator,name=iPhone 15'`
- **Linting:** Use **SwiftLint** (`swiftlint lint`).
