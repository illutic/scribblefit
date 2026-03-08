# AI / SYNC SPEC — ScribbleFit

> Covers the LLM parsing pipeline, sync queue, background workers, and all AI engine implementations.
> Depends on: `specs/CORE.md`

---

## 1. OVERVIEW & DATA FLOW

```
User types text
      │
      ▼
CanvasRepository.addScribble(rawText)
      │  inserts SyncQueue item (status=PENDING)
      │  publishes to allItemsSubject
      ▼
ListenForSyncItemsUseCase (running in background Task)
      │  observes allSyncItems publisher
      │  sees hasPending=true
      ▼
SyncRepository.syncWorkouts()
      │
      ▼
SyncWorkoutUseCase.execute()  [@MainActor on iOS, WorkManager on Android]
      │  getPendingSyncItems()
      │  updateSyncStatus(id, PROCESSING)
      │  engine.parseWorkout(rawText)  → ParsedWorkoutResult
      │
      ├─ SUCCESS → saveParsedWorkout(id, workout)
      │              encodes workout as JSON
      │              updateParsedResult(id, status=COMPLETED, jsonData)
      │              refreshes subject → CanvasRepository.getFeed() emits ConfirmationItem
      │
      └─ FAILURE → updateSyncStatus(id, FAILED)
```

### Android vs iOS background execution
| | Android | iOS |
|--|---------|-----|
| Trigger | `ListenForSyncItemsUseCase` launches `OneTimeWorkRequest` via WorkManager | `ListenForSyncItemsUseCase` runs in a `Task` loop on MainActor |
| Worker class | `SyncWorker : CoroutineWorker` | N/A — direct async call |
| Network constraint | `NetworkType.CONNECTED` | URLSession (no explicit constraint) |
| Retry | Exponential backoff (WorkManager) | Manual retry via `retryScribble(id)` |

---

## 2. DOMAIN LAYER — `:feature:ai:domain`

### File: `engine/LLMEngine.kt` (Android) / `Core/AI/LLMEngine.swift` (iOS)
```kotlin
interface LLMEngine {
    suspend fun parseWorkout(rawText: String): ParsedWorkoutResult
}
```

### File: `engine/AnalysisEngine.kt` / `Core/AI/AnalysisEngine.swift`
```kotlin
interface AnalysisEngine {
    suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion>
    suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary>
    suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight>
}
```

### File: `engine/SyncRepository.kt` / `Core/AI/SyncRepository.swift`
```kotlin
// Android
interface SyncRepository {
    fun getPendingSyncItems(): Flow<List<SyncItem>>
    fun getAllSyncItems(): Flow<List<SyncItem>>
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
    suspend fun saveParsedWorkout(syncItemId: String, workout: ParsedWorkout)
    suspend fun enqueueScribble(id: String, rawText: String)
    suspend fun saveFeedItem(id: String, type: String, jsonData: String, status: SyncStatus)
    suspend fun deleteSyncItem(id: String)
    suspend fun syncWorkouts()
}

// iOS — same methods but:
// getPendingSyncItems() → async throws -> [AISyncItem]
// getAllSyncItems() → async throws -> [AISyncItem]
// observeAllSyncItems() → AnyPublisher<[AISyncItem], Never>
// syncWorkouts() → async throws
```

### File: `engine/ConfigRepository.kt` / `Core/AI/ConfigRepository.swift`
```kotlin
interface ConfigRepository {
    fun getConfig(): Flow<SystemConfig?>       // iOS: async -> SystemConfig?
    suspend fun updateConfig(config: SystemConfig)
}
```

### File: `security/SecureKeyStorage.kt` / `Core/AI/SecureKeyStorage.swift`
```kotlin
interface SecureKeyStorage {
    suspend fun saveApiKey(key: String): Result<Unit>  // iOS: async throws
    suspend fun getApiKey(): String?                    // iOS: async -> String?
    suspend fun clearApiKey(): Result<Unit>             // iOS: async throws
}
```

### Domain Models

**File: `model/ParsedWorkout.kt`**
```kotlin
@Serializable
data class ParsedWorkout(
    val date: String,           // ISO8601
    val location: String?,
    val exercises: List<ParsedExercise>
)

@Serializable
data class ParsedExercise(
    @SerialName("canonical_name") val canonicalName: String,
    @SerialName("muscle_group") val muscleGroup: String,
    val sets: List<ParsedSet>
)

@Serializable
data class ParsedSet(
    val weight: Double,
    val reps: Int,
    val rpe: Double? = null,
    val notes: String? = null
)
```

**File: `model/ParsedWorkoutResult.kt`**
```kotlin
data class ParsedWorkoutResult(
    val workout: ParsedWorkout?,
    val rawText: String,
    val status: ParsingStatus,
    val modelUsed: String? = null,
    val processingTimeMs: Long = 0,
    val reasoning: String? = null,
    val error: String? = null
)

enum class ParsingStatus { SUCCESS, PARTIAL_SUCCESS, FAILURE }
```

**File: `model/SyncItem.kt`** (Android) / `Core/AI/AISyncItem.swift` (iOS)
```kotlin
data class SyncItem(
    val id: String,
    val type: String,           // "SCRIBBLE", "CONFIRMATION", "PROMPT", "INSIGHT"
    val rawText: String?,
    val status: SyncStatus,
    val createdAt: Long,        // iOS: Date
    val jsonData: String?,
    val parsedResult: ParsedWorkout?  // iOS: derived by decoding jsonData when status=completed
)

enum class SyncStatus { PENDING, PROCESSING, COMPLETED, FAILED }
```

**File: `model/SystemConfig.kt`**
```kotlin
data class SystemConfig(
    val promptVersion: String,
    val promptText: String,
    val exerciseVersion: String = "0.0.0",
    val preferredLlmProvider: LLMProvider = LLMProvider.GEMINI,
    val preferredModel: String = "",
    val parsingMode: String = "managed",
    val weightUnit: String = "lbs",
    val themePreference: String = "system",
    val updatedAt: Long
)

enum class LLMProvider(val rawValue: String) {
    OPENAI("openai"), GEMINI("gemini"), LOCAL("local")
}
```

**Analysis Models** (`model/AnalysisSuggestion.kt`, `AnalysisSummary.kt`, `ExerciseInsight.kt`):
```kotlin
data class AnalysisSuggestion(val text: String, val emoji: String, val type: SuggestionType, val timestamp: Long)
enum class SuggestionType { RECOVERY, PATTERN, MILESTONE, REST }

data class AnalysisSummary(
    val period: SummaryPeriod,
    val summaryText: String,
    val highlights: List<String>,
    val muscleDistribution: List<MuscleGroupStat>,
    val focusArea: String,
    val volumeDelta: Double,
    val timestamp: Long
)
data class MuscleGroupStat(val muscleGroup: String, val volumePercentage: Double)
enum class SummaryPeriod { DAY, WEEK, MONTH, YEAR }

data class ExerciseInsight(
    val exerciseId: String,
    val estimated1RM: Double,
    val prDetected: Boolean,
    val trendDirection: InsightTrend,
    val breakdownText: String,
    val timestamp: Long
)
enum class InsightTrend { IMPROVING, STABLE, PLATEAUED, DECLINING }
```

### Use Cases

**File: `usecase/SyncWorkoutUseCase.kt`**
```kotlin
class SyncWorkoutUseCase(
    private val syncRepository: SyncRepository,
    private val engine: LLMEngine
) {
    suspend operator fun invoke() {
        val pendingItems = syncRepository.getPendingSyncItems() // Android: Flow.first()
        for (item in pendingItems) {
            val rawText = item.rawText ?: continue
            syncRepository.updateSyncStatus(item.id, SyncStatus.PROCESSING)
            val result = engine.parseWorkout(rawText)
            if (result.status == ParsingStatus.SUCCESS && result.workout != null) {
                syncRepository.saveParsedWorkout(item.id, result.workout)
            } else {
                syncRepository.updateSyncStatus(item.id, SyncStatus.FAILED)
            }
        }
    }
}
```

**File: `usecase/ListenForSyncItemsUseCase.kt`**
```kotlin
class ListenForSyncItemsUseCase(private val syncRepository: SyncRepository) {
    // Android: suspend operator fun invoke()
    // iOS: func execute() async
    suspend operator fun invoke() {
        syncRepository.getAllSyncItems().collect { items ->
            if (items.any { it.status == SyncStatus.PENDING }) {
                syncRepository.syncWorkouts()
            }
        }
    }
}
// iOS uses for-await on observeAllSyncItems().values
```

---

## 3. DATA LAYER — `:feature:ai:data`

### Engine Implementations

**`DynamicLLMEngine`** — selects engine based on `ConfigRepository.getConfig().preferredLlmProvider`
- Injects `@Named("openai")`, `@Named("gemini")`, `LocalAIEngine`
- Default provider: `gemini`
- Also implements `AnalysisEngine` by delegating to the active engine

**`GeminiAIEngine`** — calls `https://generativelanguage.googleapis.com/v1beta`
- API key from `SecureKeyStorage.getApiKey()`
- Model: auto-discovers flash models via `GET /v1beta/models`, caches in `activeModelPath`
- If user has set `preferredModel` in config, uses that
- Request body: `GeminiRequest(contents, generationConfig(responseMimeType="application/json"))`
- Implements `AnalysisEngine` via same Gemini API

**`OpenAIEngine`** — calls `POST https://api.openai.com/v1/responses`
- API key from `SecureKeyStorage.getApiKey()`
- Model from `configRepository.getConfig().preferredModel ?: "gpt-4o-mini"`
- Uses Responses API with `text.format.type = "json_object"`
- Implements `AnalysisEngine`

**`LocalAIEngine`** — on-device inference
- Android: Gemini Nano via ML Kit `GenerativeModel` (`com.google.mlkit.genai.prompt`)
- iOS: Apple Intelligence / FoundationModels (stub until API stable)

### AI Prompt

The system prompt is stored in `System_Config.prompt_text` and defaults to the embedded `SystemConfig.defaultPrompt`:

```
You are ScribbleFit AI, a fitness parsing assistant.
Parse raw gym shorthand into this JSON schema:
{
  "date": "YYYY-MM-DD",
  "location": "String or null",
  "exercises": [{ "canonical_name": "String", "muscle_group": "String", "sets": [{ "weight": number, "reps": integer, "rpe": number|null, "notes": "String|null" }] }]
}
Output ONLY valid JSON. No markdown, no extra text.
```

### `SyncRepositoryImpl`

**Android** — `SyncQueueDao` + `WorkManager`
```kotlin
override suspend fun saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) {
    val jsonString = json.encodeToString(workout)
    syncQueueDao.updateParsedResult(syncItemId, EntitySyncStatus.COMPLETED, jsonString)
}
// updateParsedResult is a @Query("UPDATE Sync_Queue SET status=:status, parsed_json=:jsonData WHERE id=:id")
```

**iOS** — `ScribbleFitDatabase` + `CurrentValueSubject`
```swift
func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) async throws {
    let jsonData = try JSONEncoder().encode(workout)
    guard let jsonString = String(data: jsonData, encoding: .utf8) else { return }
    await database.updateParsedResult(id: syncItemId, status: .completed, jsonData: jsonString)
    await refreshAllItemsSubject()
}
// database.updateParsedResult fetches SyncQueue by id, sets syncStatus + jsonData, saves
```

### `ConfigRepositoryImpl`

- No network calls — reads/writes `SystemConfig` from local database only
- Exercises self-register via `LedgerRepositoryImpl.logWorkout()`

### DI Module (`SyncModule.kt`)

```kotlin
@Module @InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds @Singleton abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository
    @Binds @Singleton abstract fun bindConfigRepository(impl: ConfigRepositoryImpl): ConfigRepository
    @Binds @Singleton abstract fun bindSecureKeyStorage(impl: SecureKeyStorageImpl): SecureKeyStorage

    companion object {
        @Provides @Singleton fun provideGenerativeModel(): GenerativeModel = Generation.getClient()
        @Provides @Singleton @Named("openai") fun provideOpenAIEngine(...): LLMEngine
        @Provides @Singleton @Named("gemini") fun provideGeminiAIEngine(...): LLMEngine
        @Provides @Singleton fun provideLLMEngine(...): LLMEngine  // returns DynamicLLMEngine
        @Provides @Singleton fun provideAnalysisEngine(llmEngine: LLMEngine): AnalysisEngine
        @Provides @Singleton fun provideSyncWorkoutUseCase(...): SyncWorkoutUseCase
        @Provides @Singleton fun provideListenForSyncItemsUseCase(...): ListenForSyncItemsUseCase
    }
}
```

---

## 4. SECURITY

- API keys stored in `EncryptedSharedPreferences` (Android) / Keychain `kSecClassGenericPassword` (iOS)
- Keys NEVER logged, NEVER included in crash reports
- `SecureKeyStorage.clearApiKey()` wipes the stored key
- `SettingsRepository.clearAllData()` wipes ALL local data (nuclear option)
