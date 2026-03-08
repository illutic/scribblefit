# LEDGER SPEC — ScribbleFit

> Covers workout history storage, retrieval, and exercise self-registration.
> Depends on: `specs/CORE.md`, `specs/AI_SYNC.md` (for ParsedWorkout → WorkoutHistory mapping)

---

## 1. OVERVIEW

The Ledger is a read/write store of confirmed workout sessions. It is populated by `ConfirmWorkoutUseCase` (see `CANVAS.md`) and read by `LedgerViewModel`.

**Exercise Self-Registration**: When a workout is logged, each `ParsedExercise.canonicalName` is automatically inserted into `Exercise_Dictionary` using insert-or-ignore semantics. This satisfies the FK constraint on the `Sets` table (`exercise_id → Exercise_Dictionary.id`) without requiring a remote exercise sync.

---

## 2. DOMAIN LAYER

### File: `Ledger/Domain/Repository/LedgerRepository.swift` / `LedgerRepository.kt`

```swift
// iOS
protocol LedgerRepository: Sendable {
    func getWorkoutHistory() async throws -> [WorkoutHistory]
    func logWorkout(_ workout: WorkoutHistory) async throws
}
```

```kotlin
// Android
interface LedgerRepository {
    fun getWorkoutHistory(): Flow<List<WorkoutHistory>>
    suspend fun logWorkout(workout: WorkoutHistory)
}
```

### File: `Ledger/Domain/Models/WorkoutHistory.swift` / `WorkoutHistory.kt`

```swift
// iOS
struct WorkoutHistory: Sendable, Identifiable {
    let id: String
    let date: Date
    let location: String?
    let totalVolume: Double
    let exercises: [ExerciseHistory]
}

struct ExerciseHistory: Sendable, Identifiable {
    var id: String { canonicalName }
    let canonicalName: String
    let sets: [SetHistory]
}

struct SetHistory: Sendable, Identifiable {
    let id: UUID
    let weight: Double
    let reps: Int
    let rpe: Double?
    let notes: String?
}
```

```kotlin
// Android
data class WorkoutHistory(
    val id: String,
    val date: Long,          // epoch ms
    val location: String?,
    val totalVolume: Double,
    val exercises: List<ExerciseHistory>
)

data class ExerciseHistory(
    val canonicalName: String,
    val sets: List<SetHistory>
)

data class SetHistory(
    val weight: Double,
    val reps: Int,
    val rpe: Double?,
    val notes: String?
)
```

### ParsedWorkout → WorkoutHistory mapping

When `ConfirmWorkoutUseCase` converts a `ParsedWorkout` to `WorkoutHistory`:
- `date`: parse ISO8601 string → `Date` / epoch ms
- `location`: pass through
- `totalVolume`: sum of `weight * reps` across all sets
- `exercises`: map `ParsedExercise` → `ExerciseHistory`
- `sets`: map `ParsedSet` → `SetHistory` (new UUIDs for set IDs)

---

## 3. DATA LAYER

### File: `Ledger/Data/Repository/LedgerRepositoryImpl.swift` / `LedgerRepositoryImpl.kt`

#### `logWorkout()` — critical implementation

```swift
// iOS
func logWorkout(_ workout: WorkoutHistory) async throws {
    // 1. SELF-REGISTER EXERCISES (insert-or-ignore)
    let exercises = workout.exercises.map { exercise in
        ExerciseDictionary(
            id: exercise.canonicalName,
            canonicalName: exercise.canonicalName,
            muscleGroup: "",
            aliases: []
        )
    }
    await database.insertExercisesIfAbsent(exercises)

    // 2. UPSERT WORKOUT LOG
    let log = WorkoutLog(
        id: workout.id,
        date: workout.date,
        location: workout.location,
        totalVolume: workout.totalVolume
    )
    await database.upsertWorkoutLog(log)

    // 3. UPSERT SETS (exerciseId = canonicalName)
    let sets = workout.exercises.flatMap { exercise in
        exercise.sets.map { set in
            WorkoutSet(
                id: UUID().uuidString,
                weight: set.weight,
                reps: set.reps,
                rpe: set.rpe,
                notes: set.notes,
                exerciseId: exercise.canonicalName  // FK satisfied by step 1
            )
        }
    }
    await database.upsertWorkoutSets(sets)
}
```

```kotlin
// Android
override suspend fun logWorkout(workout: WorkoutHistory) {
    // 1. SELF-REGISTER EXERCISES
    val exerciseEntities = workout.exercises.map { exercise ->
        ExerciseDictionaryEntity(
            id = exercise.canonicalName,
            canonicalName = exercise.canonicalName,
            muscleGroup = "",
            aliases = emptyList()
        )
    }
    exerciseDictionaryDao.insertExercisesIfAbsent(exerciseEntities)

    // 2. UPSERT WORKOUT LOG
    val logEntity = WorkoutLogEntity(
        id = workout.id,
        date = workout.date,
        location = workout.location,
        totalVolume = workout.totalVolume
    )
    workoutLogDao.upsert(logEntity)

    // 3. UPSERT SETS
    val setEntities = workout.exercises.flatMap { exercise ->
        exercise.sets.map { set ->
            SetEntity(
                id = UUID.randomUUID().toString(),
                workoutId = workout.id,
                exerciseId = exercise.canonicalName,  // FK satisfied by step 1
                weight = set.weight,
                reps = set.reps,
                rpe = set.rpe,
                notes = set.notes
            )
        }
    }
    setDao.upsertAll(setEntities)
}
```

#### `getWorkoutHistory()` — query logic

```swift
// iOS
func getWorkoutHistory() async throws -> [WorkoutHistory] {
    let logs = await database.getAllWorkoutLogs()  // sorted by date DESC
    return await logs.asyncMap { log in
        let sets = await database.getSetsForWorkout(id: log.id)
        // Group sets by exerciseId
        let grouped = Dictionary(grouping: sets, by: \.exerciseId)
        let exercises = grouped.map { (exerciseId, sets) -> ExerciseHistory in
            ExerciseHistory(
                canonicalName: exerciseId,  // canonicalName == id
                sets: sets.map { SetHistory(id: UUID(), weight: $0.weight, reps: $0.reps, rpe: $0.rpe, notes: $0.notes) }
            )
        }
        return WorkoutHistory(
            id: log.id,
            date: log.date,
            location: log.location,
            totalVolume: log.totalVolume ?? 0,
            exercises: exercises
        )
    }
}
```

#### Exercise Dictionary DAO — insert-or-ignore

```kotlin
// Android DAO
@Insert(onConflict = OnConflictStrategy.IGNORE)
suspend fun insertExercisesIfAbsent(exercises: List<ExerciseDictionaryEntity>)
```

```swift
// iOS Database
public func insertExercisesIfAbsent(_ exercises: [ExerciseDictionary]) async {
    for exercise in exercises {
        let id = exercise.id
        let descriptor = FetchDescriptor<ExerciseDictionary>(predicate: #Predicate { $0.id == id })
        if (try? context.fetch(descriptor).first) == nil {
            context.insert(exercise)
        }
    }
    try? context.save()
}
```

---

## 4. VIEW MODEL

### File: `Ledger/UI/LedgerViewModel.swift` / `LedgerViewModel.kt`

```swift
// iOS
@MainActor
class LedgerViewModel: ObservableObject {
    @Published var history: [WorkoutHistory] = []

    func fetchHistory() async {
        history = (try? await ledgerRepository.getWorkoutHistory()) ?? []
    }
}
```

```kotlin
// Android
@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository
) : ViewModel() {
    val workoutHistory: StateFlow<List<WorkoutHistory>> = ledgerRepository
        .getWorkoutHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS), emptyList())
}
```

---

## 5. EXERCISE DICTIONARY INVARIANTS

- `ExerciseDictionary.id == ExerciseDictionary.canonicalName` for self-registered exercises
- `WorkoutSet.exerciseId` always equals a `canonicalName` (and thus an `id` in `ExerciseDictionary`)
- Self-registered exercises have `muscleGroup = ""` and `aliases = []`
- Future enrichment (muscle group, aliases) preserves existing records via insert-or-ignore
- No remote exercise sync — exercises accumulate organically from user workout data
