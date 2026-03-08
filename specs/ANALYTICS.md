# ANALYTICS SPEC — ScribbleFit

> Covers AI-powered workout analysis: home suggestions, period summaries, and exercise insights.
> Depends on: `specs/CORE.md`, `specs/AI_SYNC.md` (for AnalysisEngine protocol)

---

## 1. OVERVIEW

Analytics generates three types of AI-powered insights:
1. **Home Suggestion** — a one-liner shown on the Canvas screen (e.g. "You're due for a rest day 🛌")
2. **Period Summary** — narrative summary for a day/week/month/year
3. **Exercise Insight** — estimated 1RM, PR detection, trend for a specific exercise

All insights are cached as JSON strings in `InsightsCache` (database). The cache is read-through: if a cached value exists, return it; trigger a background refresh as needed.

---

## 2. DOMAIN LAYER

### File: `Analytics/Domain/Repository/AnalysisRepository.swift` / `AnalysisRepository.kt`

```swift
// iOS
protocol AnalysisRepository: Sendable {
    // Observable streams (backed by cache)
    func getHomeSuggestion() -> AnyPublisher<AnalysisSuggestion?, Never>
    func getSummary(period: SummaryPeriod) async throws -> AnalysisSummary?
    func getExerciseInsight(exerciseId: String) async throws -> ExerciseInsight?

    // Write cache
    func saveHomeSuggestion(_ suggestion: AnalysisSuggestion) async throws
    func saveSummary(_ summary: AnalysisSummary) async throws
    func saveExerciseInsight(_ insight: ExerciseInsight) async throws
    func clearOldInsights() async throws
}
```

```kotlin
// Android
interface AnalysisRepository {
    fun getHomeSuggestion(): Flow<AnalysisSuggestion?>
    fun getSummary(period: SummaryPeriod): Flow<AnalysisSummary?>
    fun getExerciseInsight(exerciseId: String): Flow<ExerciseInsight?>

    suspend fun saveHomeSuggestion(suggestion: AnalysisSuggestion)
    suspend fun saveSummary(summary: AnalysisSummary)
    suspend fun saveExerciseInsight(insight: ExerciseInsight)
    suspend fun clearOldInsights()
}
```

### Domain Models

**`Core/AI/AnalysisSuggestion.swift`** / `feature/ai/domain/model/AnalysisSuggestion.kt`
```swift
// iOS
struct AnalysisSuggestion: Codable, Sendable {
    let text: String
    let emoji: String
    let type: SuggestionType
    let timestamp: Date
    var fullText: String { "\(text) \(emoji)" }
}

enum SuggestionType: String, Codable, Sendable {
    case recovery, pattern, milestone, rest
}
```

```kotlin
// Android
data class AnalysisSuggestion(
    val text: String,
    val emoji: String,
    val type: SuggestionType,
    val timestamp: Long
) {
    val fullText: String get() = "$text $emoji"
}

enum class SuggestionType { RECOVERY, PATTERN, MILESTONE, REST }
```

**`Analytics/Domain/Models/AnalysisSummary.swift`** / `.kt`
```swift
// iOS
struct AnalysisSummary: Codable, Sendable {
    let period: SummaryPeriod
    let summaryText: String
    let highlights: [String]
    let muscleDistribution: [MuscleGroupStat]
    let focusArea: String
    let volumeDelta: Double
    let timestamp: Date
}

struct MuscleGroupStat: Codable, Sendable {
    let muscleGroup: String
    let volumePercentage: Double
}

enum SummaryPeriod: String, Codable, Sendable { case day, week, month, year }
```

**`Analytics/Domain/Models/ExerciseInsight.swift`** / `.kt`
```swift
// iOS
struct ExerciseInsight: Codable, Sendable {
    let exerciseId: String
    let estimated1RM: Double
    let prDetected: Bool
    let trendDirection: InsightTrend
    let breakdownText: String
    let timestamp: Date
}

enum InsightTrend: String, Codable, Sendable {
    case improving, stable, plateaued, declining
}
```

### Use Case

**`Analytics/Domain/UseCases/AnalyzeWorkoutsUseCase.swift`** / `AnalyzeWorkoutsUseCase.kt`

```swift
// iOS
class AnalyzeWorkoutsUseCase {
    private let engine: AnalysisEngine
    private let repository: AnalysisRepository

    func refreshHomeSuggestion(context: String) async throws {
        let result = try await engine.generateSuggestion(context: context)
        try await repository.saveHomeSuggestion(result)
    }

    func refreshSummary(period: SummaryPeriod, workoutData: String) async throws {
        let result = try await engine.generateSummary(period: period, workoutData: workoutData)
        try await repository.saveSummary(result)
    }

    func refreshExerciseInsight(
        exerciseId: String,
        exerciseName: String,
        historyData: String
    ) async throws {
        var insight = try await engine.generateExerciseInsight(
            exerciseName: exerciseName,
            historyData: historyData
        )
        // Normalize exerciseId to local database value
        insight = ExerciseInsight(
            exerciseId: exerciseId,
            estimated1RM: insight.estimated1RM,
            prDetected: insight.prDetected,
            trendDirection: insight.trendDirection,
            breakdownText: insight.breakdownText,
            timestamp: insight.timestamp
        )
        try await repository.saveExerciseInsight(insight)
    }
}
```

---

## 3. DATA LAYER

### Cache Key Convention

| Insight Type | Cache Key |
|-------------|-----------|
| Home suggestion | `"home_suggestion"` |
| Period summary | `"summary_{period.rawValue}"` e.g. `"summary_week"` |
| Exercise insight | `"exercise_insight_{exerciseId}"` |

### `AnalysisRepositoryImpl`

**iOS** — backed by `ScribbleFitDatabase` (`InsightsCache` SwiftData model)

```swift
// iOS
public final class AnalysisRepositoryImpl: AnalysisRepository {
    private let database: ScribbleFitDatabase
    private let homeSuggestionSubject = CurrentValueSubject<AnalysisSuggestion?, Never>(nil)

    func getHomeSuggestion() -> AnyPublisher<AnalysisSuggestion?, Never> {
        homeSuggestionSubject.eraseToAnyPublisher()
    }

    func saveHomeSuggestion(_ suggestion: AnalysisSuggestion) async throws {
        let data = try JSONEncoder().encode(suggestion)
        guard let json = String(data: data, encoding: .utf8) else { return }
        let cache = InsightsCache(key: "home_suggestion", jsonData: json, createdAt: Date())
        await database.upsertInsight(cache)
        homeSuggestionSubject.send(suggestion)
    }

    func getSummary(period: SummaryPeriod) async throws -> AnalysisSummary? {
        guard let cache = await database.getInsightByKey(key: "summary_\(period.rawValue)"),
              let data = cache.jsonData.data(using: .utf8)
        else { return nil }
        return try? JSONDecoder().decode(AnalysisSummary.self, from: data)
    }

    // getExerciseInsight, saveSummary, saveExerciseInsight follow the same pattern
    // clearOldInsights → database.clearInsights()
}
```

**Android** — backed by `InsightsCacheDao`

```kotlin
// Android
class AnalysisRepositoryImpl(
    private val dao: InsightsCacheDao,
    private val json: Json
) : AnalysisRepository {
    override fun getHomeSuggestion(): Flow<AnalysisSuggestion?> =
        dao.getByKey("home_suggestion").map { entity ->
            entity?.jsonData?.let { json.decodeFromString<AnalysisSuggestion>(it) }
        }

    override suspend fun saveHomeSuggestion(suggestion: AnalysisSuggestion) {
        val encoded = json.encodeToString(suggestion)
        dao.upsert(InsightsCacheEntity(key = "home_suggestion", jsonData = encoded, createdAt = System.currentTimeMillis()))
    }

    // getSummary, getExerciseInsight, saveSummary, saveExerciseInsight follow same pattern
    // clearOldInsights → dao.deleteAll()
}
```

### `AnalysisEngine` Protocol (AI_SYNC.md)

```swift
// iOS
protocol AnalysisEngine: Sendable {
    func generateSuggestion(context: String) async throws -> AnalysisSuggestion
    func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary
    func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight
}
```

```kotlin
// Android — returns Result<T>
interface AnalysisEngine {
    suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion>
    suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary>
    suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight>
}
```

`DynamicLLMEngine` implements `AnalysisEngine` by delegating to the active LLM engine.

---

## 4. VIEW MODEL

### `AnalyticsViewModel` (if present) / inline in screen

Analytics insights are typically triggered from `CanvasViewModel` (home suggestion) or a dedicated analytics screen.

**Home suggestion flow** (CanvasViewModel):
```swift
// iOS CanvasViewModel
private func refreshHomeSuggestion() {
    Task {
        let context = buildWorkoutContext(history)
        try? await analyzeWorkoutsUseCase.refreshHomeSuggestion(context: context)
    }
}
// uiState.homeSuggestion comes from analysisRepository.getHomeSuggestion() publisher
```

---

## 5. CACHE STRATEGY

| Decision | Detail |
|---------|--------|
| Cache storage | `InsightsCache` table — key-value JSON strings |
| Cache reads | Synchronous from subject/Flow (iOS/Android) |
| Cache writes | Triggered by `AnalyzeWorkoutsUseCase` |
| Cache invalidation | `clearOldInsights()` wipes all entries |
| Staleness | Not automatically checked — caller decides when to refresh |
| Home suggestion | Backed by `CurrentValueSubject` on iOS for instant UI updates |
