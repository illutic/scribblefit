# CANVAS SPEC — ScribbleFit

> Covers the conversational feed, scribble input, workout confirmation, and session management.
> Depends on: `specs/CORE.md`, `specs/AI_SYNC.md`

---

## 1. OVERVIEW & DATA FLOW

```
User types rawText
      │
      ▼
CanvasRepository.addScribble(rawText)
      │  creates SyncQueue item (status=PENDING)
      │  AI pipeline picks it up (see AI_SYNC.md)
      │
      ▼ (after AI completes)
SyncQueue item: status=COMPLETED, jsonData=ParsedWorkout JSON
      │
      ▼
CanvasRepository.getFeed()
      │  observeAllSyncItems() → maps COMPLETED SCRIBBLE → ConfirmationItem
      │  returns [FeedItem] sorted by timestamp ascending
      │
      ▼
CanvasViewModel.uiState.feedItems
      │
      ▼
User taps "Confirm" on ConfirmationCard
      │
      ▼
ConfirmWorkoutUseCase.execute(confirmation)
      │  ledgerRepository.logWorkout(workout)   ← writes WorkoutLog + Sets
      │  sessionRepository.clearActiveSession()
      │  canvasRepository.removeFeedItem(id)    ← removes from SyncQueue
```

---

## 2. DOMAIN LAYER

### File: `Canvas/Domain/Repository/CanvasRepository.swift` (iOS) / `CanvasRepository.kt` (Android)

```swift
// iOS
protocol CanvasRepository: Sendable {
    func getFeed() -> AnyPublisher<[FeedItem], Never>
    func addScribble(rawText: String) async throws
    func retryScribble(id: String) async throws
    func addConfirmation(item: ConfirmationItem) async throws
    func addInsight(item: InsightItem) async throws
    func removeFeedItem(id: String) async throws
    func clearFeed() async throws
}
```

```kotlin
// Android
interface CanvasRepository {
    fun getFeed(): Flow<List<FeedItem>>
    suspend fun addScribble(rawText: String)
    suspend fun retryScribble(id: String)
    suspend fun addConfirmation(item: FeedItem.Confirmation)
    suspend fun addInsight(item: FeedItem.Insight)
    suspend fun removeFeedItem(id: String)
    suspend fun clearFeed()
}
```

### File: `Canvas/Domain/Models/FeedItem.swift` / `FeedItem.kt`

```swift
// iOS
enum FeedItem: Sendable, Identifiable {
    case prompt(PromptItem)
    case scribble(ScribbleItem)
    case confirmation(ConfirmationItem)
    case insight(InsightItem)
    // id computed from associated value
}

struct PromptItem: Identifiable, Codable, Sendable {
    let id: String
    let timestamp: Date
    let text: String
    let emoji: String
    let type: SuggestionType
}

struct ScribbleItem: Identifiable, Sendable {
    let id: String
    let timestamp: Date
    let rawText: String
    let status: ScribbleStatus
}

struct ConfirmationItem: Identifiable, Codable, Sendable {
    let id: String
    let timestamp: Date
    let workout: ParsedWorkout
    let scribbleId: String
}

struct InsightItem: Identifiable, Codable, Sendable {
    let id: String
    let timestamp: Date
    let text: String
    let emoji: String
}

enum ScribbleStatus: Sendable { case pending, processing, failed, completed }
```

```kotlin
// Android
sealed class FeedItem {
    data class Prompt(
        val id: String, val timestamp: Long,
        val text: String, val emoji: String, val type: SuggestionType
    ) : FeedItem()
    data class Scribble(
        val id: String, val timestamp: Long,
        val rawText: String, val status: ScribbleStatus
    ) : FeedItem()
    data class Confirmation(
        val id: String, val timestamp: Long,
        val workout: ParsedWorkout, val scribbleId: String
    ) : FeedItem()
    data class Insight(
        val id: String, val timestamp: Long,
        val text: String, val emoji: String
    ) : FeedItem()
}

enum class ScribbleStatus { PENDING, PROCESSING, FAILED, COMPLETED }
```

### File: `Canvas/Domain/Repository/WorkoutSessionRepository.swift` / `WorkoutSessionRepository.kt`

```swift
// iOS
protocol WorkoutSessionRepository: Sendable {
    func getActiveSession() async throws -> WorkoutSession?
    func updateSession(_ session: WorkoutSession) async throws
    func clearActiveSession() async throws
}
```

### File: `Canvas/Domain/Models/WorkoutSession.swift` / `WorkoutSession.kt`

```swift
// iOS
struct WorkoutSession: Codable, Sendable {
    let id: String
    let startTime: Date
    let lastActivityTime: Date
    let exercises: [SessionExercise]
}

struct SessionExercise: Codable, Sendable {
    let canonicalName: String
    let sets: [SessionSet]
}

struct SessionSet: Codable, Sendable {
    let weight: Double
    let reps: Int
    let rpe: Double?
    let notes: String?
}
```

### Use Cases

**`Canvas/Domain/UseCases/ProcessScribbleUseCase.swift`** / `.kt`
```swift
// iOS
class ProcessScribbleUseCase {
    func execute(rawText: String) async throws {
        try await canvasRepository.addScribble(rawText: rawText)
    }
}
```

**`Canvas/Domain/UseCases/ConfirmWorkoutUseCase.swift`** / `.kt`
```swift
// iOS
class ConfirmWorkoutUseCase {
    func execute(confirmation: ConfirmationItem) async throws {
        let history = WorkoutHistory(from: confirmation.workout)
        try await ledgerRepository.logWorkout(history)
        try await sessionRepository.clearActiveSession()
        try await canvasRepository.removeFeedItem(id: confirmation.id)
    }
}
```

**`Canvas/Domain/UseCases/ExecuteQuickActionUseCase.swift`** / `.kt`
- Maps `QuickActionType` to a rawText string and calls `canvasRepository.addScribble()`
- Quick action types: `repeatLast`, `restDay`, `run5k`

---

## 3. DATA LAYER

### `Canvas/Data/Repository/CanvasRepositoryImpl.swift` / `.kt`

**Source of truth**: `SyncRepository` (not a separate database)

**`getFeed()` mapping logic**:

```swift
// iOS — for each AISyncItem:
switch item.itemType {
case "SCRIBBLE":
    if item.status == .completed,
       let data = item.jsonData?.data(using: .utf8),
       let workout = try? jsonDecoder.decode(ParsedWorkout.self, from: data) {
        // → FeedItem.confirmation(ConfirmationItem(...))
    } else {
        let mappedStatus: ScribbleStatus = switch item.status {
        case .pending: .pending
        case .processing: .processing
        case .failed: .failed
        case .completed: .completed
        }
        // → FeedItem.scribble(ScribbleItem(..., status: mappedStatus))
    }
case "PROMPT":
    // decode PromptItem from jsonData → FeedItem.prompt(...)
case "CONFIRMATION":
    // decode ConfirmationItem from jsonData → FeedItem.confirmation(...)
case "INSIGHT":
    // decode InsightItem from jsonData → FeedItem.insight(...)
}
// Sort ascending by timestamp before returning
```

**Key invariant**: Only SCRIBBLE items with `status==COMPLETED` AND `jsonData!=nil` become ConfirmationItems. All other SCRIBBLE items (pending/processing/failed) appear as ScribbleItems with a status indicator.

**`addScribble(rawText:)`**
```swift
try await syncRepository.enqueueScribble(id: UUID().uuidString, rawText: rawText)
```

**`retryScribble(id:)`**
```swift
try await syncRepository.updateSyncStatus(id: id, status: .pending)
```

### `Canvas/Data/Repository/WorkoutSessionRepositoryImpl.swift`

- Serializes/deserializes `WorkoutSession` as JSON via `ActiveSession` database model
- Uses `database.upsertActiveSession()` / `database.getActiveSession()` / `database.clearActiveSession()`

---

## 4. VIEW MODEL

### File: `Canvas/CanvasViewModel.swift` / `CanvasViewModel.kt`

```swift
// iOS
struct CanvasUiState: Sendable {
    var greeting: String = ""
    var userName: String = "George"
    var quickActions: [QuickActionType] = []
    var homeSuggestion: AnalysisSuggestion? = nil
    var scribbleText: String = ""
    var feedItems: [FeedItem] = []
    var isSyncing: Bool = false
    var isRecording: Bool = false
}

@MainActor
class CanvasViewModel: ObservableObject {
    @Published var uiState = CanvasUiState()

    func onTextChange(_ newText: String)
    func submitScribble()                         // calls ProcessScribbleUseCase
    func onQuickActionClick(_ type: QuickActionType)  // calls ExecuteQuickActionUseCase
    func onRetryScribble(id: String)
    func onConfirmClick(confirmation: ConfirmationItem)  // calls ConfirmWorkoutUseCase
    func onMicClick()                            // toggles recording
    func getGreeting() -> String                 // hour-based greeting
}
```

```kotlin
// Android
data class CanvasUiState(
    val greeting: String = "",
    val userName: String = "George",
    val quickActions: List<QuickActionType> = emptyList(),
    val homeSuggestion: AnalysisSuggestion? = null,
    val scribbleText: String = "",
    val feedItems: List<FeedItem> = emptyList(),
    val isSyncing: Boolean = false,
    val isRecording: Boolean = false
)

@HiltViewModel
class CanvasViewModel @Inject constructor(...) : ViewModel() {
    val uiState: StateFlow<CanvasUiState>
    fun onTextChange(newText: String)
    fun submitScribble()
    fun onQuickActionClick(actionType: QuickActionType)
    fun onRetryScribble(id: String)
    fun onConfirmClick(confirmation: FeedItem.Confirmation)
    fun onMenuClick()
    fun onMicClick()
}
```

---

## 5. FEED ITEM TYPES SUMMARY

| itemType (DB) | Status | → FeedItem |
|--------------|--------|-----------|
| `SCRIBBLE` | PENDING / PROCESSING / FAILED | `.scribble(ScribbleItem)` with status indicator |
| `SCRIBBLE` | COMPLETED + jsonData present | `.confirmation(ConfirmationItem)` |
| `PROMPT` | any | `.prompt(PromptItem)` decoded from jsonData |
| `CONFIRMATION` | any | `.confirmation(ConfirmationItem)` decoded from jsonData |
| `INSIGHT` | any | `.insight(InsightItem)` decoded from jsonData |

Feed is always sorted **ascending by timestamp** (oldest first, newest at bottom).
