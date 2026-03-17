# ScribbleFit Android App

Native Android application built with Jetpack Compose, focusing on offline-first resilience and minimalist design.

## 🚀 Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Local Database:** Room

## 🏗️ Architecture Highlights
- **Offline-First:** All writes hit the local database first. Network sync happens in the background, ensuring a responsive UI regardless of connectivity.
- **Design:** Hyper-minimalist, text-focused UI following the ScribbleFit Design System.

## 📝 Development Conventions
- **Build Logic:** Use **Gradle Convention Plugins** (located in a `build-logic` or `gradle/plugins` directory) to share build configuration across modules, ensuring a clean and maintainable build setup.
- **Minimalism First:** Avoid third-party UI libraries. Stick to standard Jetpack Compose components.
- **Offline-First:** No UI action should ever block on a network request. All data-changing operations must be written to the local database first.

## 🛠️ Development Guidelines
- **Architecture:** Follow the **MVI (Model-View-Intent)** pattern with a feature-based module structure. 
- **UI:** 100% Jetpack Compose. Use `CompositionLocal` sparingly and prefer explicit state hoisting.
- **Concurrency:** Use **Kotlin Coroutines and Flow** for asynchronous operations and reactive data streams from Room.
- **Dependency Injection:** Use **Hilt** for dependency injection.
- **Testing:**
    - **Unit Tests:** Mandatory for all business logic, ViewModels, and data parsing.
    - **Instrumentation Tests:** Use Compose Test Rule for verifying UI interactions.
- **Code Style:** Adhere to the official [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html) and use `detekt` for static analysis.

## 🛠️ Development
- **Build:** `./gradlew assembleDebug`
- **Run:** `./gradlew installDebug`
- **Test:** `./gradlew test`
