# Plan: Update Database Structure

Update the local database structure for ScribbleFit based on the provided Room specification. This
includes creating/updating entities, relationship classes, and DAOs.

## 1. Create/Update Entities

- [x] Create `Workout` entity in
  `core/database/src/main/java/com/scribblefit/core/database/entity/Workout.kt`.
- [x] Create `Exercise` entity in
  `core/database/src/main/java/com/scribblefit/core/database/entity/Exercise.kt`.
- [x] Create `WorkoutExercise` entity in
  `core/database/src/main/java/com/scribblefit/core/database/entity/WorkoutExercise.kt`.
- [x] Create `WorkoutSet` entity in
  `core/database/src/main/java/com/scribblefit/core/database/entity/WorkoutSet.kt`.
- [x] *Note: We will keep existing entities like `ScribbleEntity` and `SystemConfigEntity` as they
  were not part of the spec but might be needed.* (Wait, they are gone from the source!)

## 2. Create Relationship Classes

- [x] Create `WorkoutExerciseWithDetails` in
  `core/database/src/main/java/com/scribblefit/core/database/entity/WorkoutExerciseWithDetails.kt`.
- [x] Create `WorkoutWithAllDetails` in
  `core/database/src/main/java/com/scribblefit/core/database/entity/WorkoutWithAllDetails.kt`.

## 3. Create/Update DAOs

- [x] Update `WorkoutDao` in
  `core/database/src/main/java/com/scribblefit/core/database/dao/WorkoutDao.kt`.
- [x] Update `ExerciseDao` in
  `core/database/src/main/java/com/scribblefit/core/database/dao/ExerciseDao.kt`.
- [x] Create `WorkoutTrackerDao` in
  `core/database/src/main/java/com/scribblefit/core/database/dao/WorkoutTrackerDao.kt`.

## 4. Update Database Class

- [x] Update `ScribbleFitDatabase.kt` to include the new entities and DAOs.
- [x] Update `DatabaseModule.kt` to provide the new DAOs.

## 5. Cleanup

- [x] Old entity files `WorkoutEntity.kt`, `ExerciseEntity.kt`, `SetEntity.kt` are removed.
