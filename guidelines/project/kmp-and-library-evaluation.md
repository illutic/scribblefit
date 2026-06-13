# Kotlin Multiplatform (KMP) and Library Evaluation Guidelines

This document outlines the evaluation criteria and architectural guardrails for adopting Kotlin Multiplatform (KMP) code sharing or integrating third-party agentic/AI library frameworks in the ScribbleFit codebase.

---

## 1. Decision Matrix: Platform-Native vs. Kotlin Multiplatform (KMP)

Before proposing sharing code via KMP, evaluate the feature or component against the following checklist:

* **Is the business logic highly volatile and complex?**
  * *Yes:* Potential candidate for KMP (e.g., complex calculation engines, custom sync protocol parsing).
  * *No:* Keep platform-native (e.g., simple API wrappers, CRUD local operations, single-turn LLM completions).
* **Does it interact heavily with OS-specific or hardware-accelerated APIs?**
  * *Yes:* Keep platform-native. Platform wrappers (like MLKit Gemini Nano on Android, FoundationModels on iOS) introduce delegate boilerplate that defeats the purpose of sharing.
  * *No:* KMP is acceptable.
* **Does it require sharing core persistence models?**
  * *Yes:* Re-evaluate. Database layer divergence (e.g., Room's auto-incrementing `Long` IDs on Android vs. SwiftData's `UUID`s on iOS) means entities cannot be shared without massive mapping layers or complex migrations.
  * *No:* KMP is acceptable if using stateless DTOs.

---

## 2. Integration Rules for Kotlin Multiplatform (KMP)

If KMP is approved for a component, the following implementation guidelines must be enforced:

### A. Build System Isolation & Framework Distribution
* **Rule**: iOS developers must not be forced to run Gradle tasks locally or maintain a Kotlin/Native environment for standard iOS UI and repository development.
* **Implementation**: KMP modules must be distributed to iOS via pre-compiled `XCFrameworks` using Swift Package Manager (SPM) or CocoaPods as a binary target. CI/CD must automate framework compilation and versioning.

### B. Core Model & Identifier Agnosticism
* **Rule**: Shared KMP modules must never define database schemas or primary key types. They must operate strictly on stateless Data Transfer Objects (DTOs) and use generic/string-based identifiers.
* **Rationale**: Prevents friction between Android's auto-incrementing integer IDs and iOS's UUIDs.

### C. Concurrency Boundary Alignment (Swift 6)
* **Rule**: All exposed KMP asynchronous APIs must align with Swift 6 strict concurrency before integration on iOS.
* **Implementation**:
  * Avoid exposing complex Kotlin `Flow`s directly to Swift. Wrap them in native Swift `AsyncStream`s or suspend functions mapped to Swift `async/await` methods.
  * Ensure Kotlin objects passed across the boundary are either immutable (`Sendable`-equivalent) or wrapped in Swift `actor`s at the data-source layer.

---

## 3. Dependency Budget & YAGNI Principle

When evaluating heavy libraries or frameworks (e.g., stateful agentic AI frameworks like JetBrains Koog):

1. **Abstain from State Over-engineering**: Do not adopt stateful agentic loop libraries if the requirement can be met with stateless, single-turn functions.
2. **Dependency Overhead Audit**: Every third-party library must be scrutinized for its dependency tree size, binary footprint increase, and compilation time impact.
3. **No Black-Box Logic**: Avoid frameworks that hide standard operations (like HTTP requests) behind proprietary, hard-to-debug abstraction layers.

---

## 4. Case Study: JetBrains Koog Evaluation Summary

* **Context**: Evaluated JetBrains Koog (`ai.koog:koog-agents`) to replace independent AI engines on Android and iOS.
* **Outcome**: Rejected for immediate implementation and deferred to future evaluation due to:
  * **Build Overhead**: Required introducing a hybrid Gradle build loop for iOS developers, increasing Xcode compile times.
  * **Model Mismatch**: Room database used `Long` auto-incrementing IDs, SwiftData used `UUID`s. Sharing domain models would require complex mapping.
  * **Local AI Mismatch**: Koog had no integration for native on-device engines (MLKit Gemini Nano and iOS FoundationModels), requiring platform delegates anyway.
  * **Swift 6 Issues**: Bridging Koog's async agent loops introduced Swift 6 concurrency `Sendable` boundary issues.
  * **YAGNI**: ScribbleFit needs simple single-turn completions; Koog's agentic framework was excessive.
