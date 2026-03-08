# CORE SPEC — ScribbleFit

> Ground truth for conventions, schemas, navigation, and design tokens.
> All feature specs depend on this document. Read it first.

---

## 1. MODULE / FOLDER STRUCTURE

### Android (Gradle modules)
```
:app                                     ← Entry point, Hilt app component, MainActivity
:core:database                           ← Room DB, all entities, DAOs, migrations
:core:network                            ← Ktor client, DTOs, ScribbleFitApi interface
:core:designsystem                       ← Compose components, theme tokens
:core:navigation                         ← Navigator interface, Screen sealed class

:feature:ai:domain                       ← LLMEngine, SyncRepository, ConfigRepository,
                                           AnalysisEngine, SecureKeyStorage protocols + all AI domain models
:feature:ai:data                         ← All engine impls (Gemini, OpenAI, Local, Dynamic),
                                           SyncRepositoryImpl, ConfigRepositoryImpl,
                                           SyncWorker (WorkManager), SyncModule (Hilt), SecureKeyStorageImpl
:feature:canvas:domain                   ← CanvasRepository, WorkoutSessionRepository, FeedItem model, use cases
:feature:canvas:data                     ← CanvasRepositoryImpl, WorkoutSessionRepositoryImpl
:feature:canvas:ui                       ← CanvasViewModel, CanvasScreen, CanvasComponents
:feature:ledger                          ← LedgerRepository + impl, WorkoutHistory models, LedgerViewModel/Screen
:feature:analytics:domain                ← AnalysisRepository, AnalysisSummary/Suggestion/ExerciseInsight models
:feature:analytics:data                  ← AnalysisRepositoryImpl
:feature:profile:domain                  ← UserRepository, SettingsRepository, ModelRepository + models
:feature:profile:data                    ← All profile repository impls
:feature:profile:ui                      ← SettingsViewModel, ProfileViewModel, Screens
```

### iOS (Swift folders under `ScribbleFit/`)
```
Core/
  AI/               ← Protocols: LLMEngine, SyncRepository, ConfigRepository,
                      AnalysisEngine, SecureKeyStorage
                      Models: ParsedWorkout, ParsedWorkoutResult, AISyncItem, AISyncStatus, SyncStatus
  Database/
    Models/         ← SwiftData @Model classes: WorkoutLog, WorkoutSet, SyncQueue,
                      SystemConfig, ExerciseDictionary, InsightsCache, ActiveSession
    ScribbleFitDatabase.swift   ← All DB operations (MainActor)
    DatabaseContainer.swift
  Network/
    Models/         ← DTOs: MetadataResponse, ConfigResponse, ExerciseDto, AuthModels, TelemetryRequest
    ScribbleFitNetworkClient.swift
  DesignSystem.swift
  Navigation/NavigationManager.swift

Features/
  Canvas/
    Domain/
      Repository/   ← CanvasRepository.swift, WorkoutSessionRepository.swift (protocols)
      Models/       ← FeedItem.swift (enum + subtypes), WorkoutSession.swift
      UseCases/     ← ProcessScribbleUseCase.swift, ConfirmWorkoutUseCase.swift,
                      ExecuteQuickActionUseCase.swift
    Data/Repository/← CanvasRepositoryImpl.swift, WorkoutSessionRepositoryImpl.swift
    CanvasView.swift, CanvasViewModel.swift
    Components/CanvasComponents.swift  (iOS equivalent of Android's CanvasComponents.kt)
  Ledger/
    Domain/
      Repository/   ← LedgerRepository.swift (protocol)
      Models/       ← WorkoutHistory.swift
    Data/Repository/← LedgerRepositoryImpl.swift
    UI/LedgerView.swift, LedgerViewModel.swift
  Profile/
    Domain/
      Repository/   ← ProfileRepositories.swift (UserRepository, SettingsRepository, ModelRepository)
      Models/       ← ProfileModels.swift (AppSettings, UserStats, WeightUnit, ThemePreference, LLMProvider)
    Data/Repository/← SettingsRepositoryImpl.swift, ModelRepositoryImpl.swift
    UI/SettingsView.swift, SettingsViewModel.swift, ProfileView.swift
  AI/
    Domain/UseCases/← SyncWorkoutUseCase.swift, ListenForSyncItemsUseCase.swift
    Data/
      Engines/      ← GeminiAIEngine.swift, ScribbleFitProxyEngine.swift, LocalAIEngine.swift
      Models/       ← AIWorkoutDTO.swift
      Repository/   ← SyncRepositoryImpl.swift, ConfigRepositoryImpl.swift, AuthRepositoryImpl.swift
  Analytics/
    Domain/
      Repository/   ← AnalysisRepository.swift (protocol)
      Models/       ← AnalysisSummary.swift
    Data/           ← AnalysisRepositoryImpl.swift
```

---

## 2. DATABASE SCHEMAS

### Android (Room) — `core:database`

**Table: `Sync_Queue`**
| Column | Type | Constraints |
|--------|------|-------------|
| `id` | TEXT | PRIMARY KEY |
| `type` | TEXT | NOT NULL |
| `raw_text` | TEXT | NOT NULL, default "" |
| `status` | TEXT | NOT NULL (enum: PENDING/PROCESSING/COMPLETED/FAILED) |
| `created_at` | INTEGER | NOT NULL (epoch ms) |
| `parsed_json` | TEXT | nullable |

**Table: `Workout_Logs`**
| Column | Type | Constraints |
|--------|------|-------------|
| `id` | TEXT | PRIMARY KEY |
| `date` | INTEGER | NOT NULL (epoch ms) |
| `location` | TEXT | nullable |
| `total_volume` | REAL | nullable |

**Table: `Sets`**
| Column | Type | Constraints |
|--------|------|-------------|
| `id` | TEXT | PRIMARY KEY |
| `workout_id` | TEXT | FK → Workout_Logs.id (CASCADE delete) |
| `exercise_id` | TEXT | FK → Exercise_Dictionary.id (RESTRICT delete) |
| `weight` | REAL | NOT NULL |
| `reps` | INTEGER | NOT NULL |
| `rpe` | REAL | nullable |
| `notes` | TEXT | nullable |

**Table: `Exercise_Dictionary`**
| Column | Type | Constraints |
|--------|------|-------------|
| `id` | TEXT | PRIMARY KEY (= canonicalName when self-registered) |
| `canonical_name` | TEXT | NOT NULL |
| `muscle_group` | TEXT | NOT NULL, default "" |
| `aliases` | TEXT | NOT NULL (JSON array) |

**Table: `System_Config`**
| Column | Type | Constraints |
|--------|------|-------------|
| `id` | TEXT | PRIMARY KEY, default "config" |
| `prompt_version` | TEXT | NOT NULL |
| `prompt_text` | TEXT | NOT NULL |
| `exercise_version` | TEXT | NOT NULL, default "0.0.0" |
| `preferred_llm_provider` | TEXT | NOT NULL, default "proxy" |
| `preferred_model` | TEXT | NOT NULL, default "" |
| `parsing_mode` | TEXT | NOT NULL, default "managed" |
| `weight_unit` | TEXT | NOT NULL, default "lbs" |
| `theme_preference` | TEXT | NOT NULL, default "system" |
| `updated_at` | INTEGER | NOT NULL (epoch ms) |

**Table: `Insights_Cache`**
| Column | Type | Constraints |
|--------|------|-------------|
| `key` | TEXT | PRIMARY KEY |
| `json_data` | TEXT | NOT NULL |
| `created_at` | INTEGER | NOT NULL (epoch ms) |

### iOS (SwiftData) — `Core/Database/Models/`

Each model is a `@Model public final class`. Relationships use `@Relationship(deleteRule:inverse:)`.

- **WorkoutLog**: id, date (Date), location (String?), totalVolume (Double?), sets ([WorkoutSet]?) with cascade delete
- **WorkoutSet**: id, weight, reps, rpe (Double?), notes (String?), exerciseId (String), workout (WorkoutLog?) inverse
- **SyncQueue**: id, itemType (String), rawText (String?), status (String — raw value of SyncStatus enum), jsonData (String?), createdAt (Date). Computed `syncStatus: SyncStatus` get/set
- **ExerciseDictionary**: id (@Attribute .unique), canonicalName, muscleGroup, aliases ([String])
- **SystemConfig**: id (default "config"), promptVersion, promptText (default defaultPrompt), exerciseVersion, preferredLlmProvider, preferredModel, parsingMode, weightUnit, themePreference, updatedAt (Date)
- **InsightsCache**: key (@Attribute .unique), jsonData, createdAt (Date)
- **ActiveSession**: id, jsonData (String), updatedAt (Date)

---

## 3. NAVIGATION

### Android
- `Navigator` interface in `:core:navigation`
- `Screen` sealed class: `Canvas`, `Ledger`, `Analytics`, `Settings`, `ExerciseLibrary`
- `navigator.navigateTo(Screen.X)`, `navigator.goBack()`, `navigator.switchToTab(Screen.X)`
- Navigation handled in `MainActivity` with `NavHost`

### iOS
- `NavigationManager` class (`@MainActor ObservableObject`) in `Core/Navigation/`
- `AppDestination` enum: `.canvas`, `.ledger`, `.analytics`, `.settings`, `.exerciseLibrary`
- `NavigationStack` enum for tab contexts: `.workout`, `.profile`, `.ledger`
- `navManager.navigate(to: .settings, in: .workout)`
- `navManager.pop(in: .profile)`

---

## 4. DESIGN SYSTEM TOKENS

### Colors
| Token | Hex | Use |
|-------|-----|-----|
| Background | `#FFFFFF` | Screen backgrounds |
| SoftGray | `#F7F7F8` | Cards, input pills, bubbles |
| RichBlack | `#101010` | Primary text, icons |
| MidGray | `#8E8EA0` | Secondary text, placeholders |
| LightGray | `#E5E5EA` | Dividers |
| DangerRed | `#FF3B30` | Destructive actions |
| ErrorBackground | `#FEE2E2` | Failed state bubble bg |
| ErrorText | `#991B1B` | Failed state text |

### Android (`core:designsystem`)
- `ScribbleFitColors` object with all color vals
- `ScribbleFitSpacing` object: `Small = 8.dp`, `Medium = 16.dp`, `Large = 24.dp`, `screenPadding = 24.dp`
- `ScribbleFitShapes` object: `Small = 8.dp`, `Medium = 12.dp`, `Large = 20.dp`
- Components: `ScribbleFitCard`, `ScribbleFitPill`, `ScribbleFitTextField`

### iOS (`Core/DesignSystem.swift`)
- `ScribbleFitColor` enum (static Color properties)
- `ScribbleFitSpacing` struct (static CGFloat properties)
- Reusable views: equivalent card/pill/text field components

---

## 5. DEPENDENCY INJECTION

### Android (Hilt)
- All Hilt modules are in `data/di/` within each feature
- Use `@Singleton` for all repositories and engines
- Use `@Named("openai")`, `@Named("gemini")`, `@Named("proxy")` for engine qualifiers
- `@HiltViewModel` for all ViewModels
- Bind interfaces with `@Binds @Singleton abstract fun bind...`
- Provide implementations with `@Provides @Singleton fun provide...`

### iOS (Manual DI)
- All dependencies constructed in `ScribbleFitApp.swift` and `MainView.swift`
- Pass dependencies down via init parameters (constructor injection)
- No global singletons except `ScribbleFitDatabase.shared` and `ScribbleFitNetworkClient.shared`

---

## 6. ASYNC / REACTIVE PATTERNS

### Android
- All suspend functions for single operations
- `Flow<T>` for observable streams
- `StateFlow<UiState>` in ViewModels using `stateIn(WhileSubscribed(FLOW_TIMEOUT_MS))`
- `FLOW_TIMEOUT_MS = 5_000L` (private const in ViewModel file)
- Use `viewModelScope.launch` for fire-and-forget
- Use `combine(...)` for merging multiple flows into UI state

### iOS
- `async throws` for single operations
- `AnyPublisher<T, Never>` for observable streams (Combine)
- `@Published var uiState` in `@MainActor ObservableObject` ViewModels
- Use `Task { ... }` for fire-and-forget from sync contexts
- Use `CurrentValueSubject` as the backing source for publishers

---

## 7. NAMING CONVENTIONS

| Concept | Android | iOS |
|---------|---------|-----|
| Repository interface | `FooRepository` | `FooRepository` (protocol) |
| Repository impl | `FooRepositoryImpl` | `FooRepositoryImpl` |
| ViewModel | `FooViewModel` | `FooViewModel: ObservableObject` |
| Use case | `FooUseCase` | `FooUseCase` |
| DB entity | `FooEntity` | `Foo` (@Model class) |
| Domain model | `Foo` | `Foo` (struct) |
| DTO | `FooDto` | `FooDto` (struct, Codable) |
| DI module | `FooModule` | N/A (manual) |
| Composable | `FooScreen`, `FooCard`, `FooRow` | `FooView`, `FooCard` |

### Magic Numbers
- Extract ALL numeric literals used outside of `.dp`/`.sp` extension properties as `private const val`
- Color hex values: define as `private val XxxColor = Color(0xFFXXXXXX)` at file level
- Flow timeouts: `private const val FLOW_TIMEOUT_MS = 5_000L`
- Animation durations: `private const val ANIM_DURATION_MS = 300`

---

## 8. DETEKT / SWIFT LINTING RULES

### Android (Detekt)
- No wildcard imports (`import foo.*` → explicit imports)
- No magic numbers (extract constants)
- `WildcardImport`, `MagicNumber`, `UnusedImports`, `UnusedPrivateMember` are enforced
- Max cyclomatic complexity: 15; max method length: 60 lines

### iOS (Swift)
- No `internal import` (use plain `import`)
- `@Model` classes do not need `: @unchecked Sendable` (macro provides it)
- Use two-parameter `onChange(of:) { _, newValue in }` (iOS 17+)
- `@MainActor` on ViewModels and all `@Model`-accessing classes
