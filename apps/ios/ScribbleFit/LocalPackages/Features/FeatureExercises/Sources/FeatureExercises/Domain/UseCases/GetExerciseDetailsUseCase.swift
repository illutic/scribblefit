import Foundation
import CoreModel
import CoreCommon

@MainActor
public final class GetExerciseDetailsUseCase {
    private let workoutRepository: WorkoutRepository
    
    public init(workoutRepository: WorkoutRepository) {
        self.workoutRepository = workoutRepository
    }
    
    public func execute(exerciseName: String) -> AsyncStream<ExerciseDetails> {
        let stream = workoutRepository.getWorkoutsWithExercise(exerciseName: exerciseName)
        
        return AsyncStream { continuation in
            Task {
                for await workouts in stream {
                    let history = workouts.sorted(by: { $0.date > $1.date }).flatMap { workout in
                        workout.exercises.filter { $0.canonicalName == exerciseName }.map { exercise in
                            ExerciseHistorySession(
                                workoutId: workout.id,
                                date: workout.date,
                                exercise: exercise
                            )
                        }
                    }
                    
                    let muscleGroup = history.first?.exercise.muscleGroup ?? ""
                    
                    // Weekly Stats
                    let calendar = Calendar.current
                    let now = Date()
                    let startOfWeek = calendar.date(from: calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: now))!
                    
                    let thisWeekSessions = history.filter { $0.date >= startOfWeek }
                    let sessionsThisWeek = thisWeekSessions.count
                    let totalVolumeThisWeek = thisWeekSessions.reduce(0) { total, session in
                        total + session.exercise.sets.reduce(0) { setTotal, set in
                            setTotal + (set.weight ?? 0) * Float(set.reps)
                        }
                    }
                    let maxWeightThisWeek = thisWeekSessions.flatMap { $0.exercise.sets }
                        .compactMap { $0.weight }.max() ?? 0
                    
                    let weeklyStats = WeeklyStats(
                        sessionsThisWeek: sessionsThisWeek,
                        totalVolumeThisWeek: totalVolumeThisWeek,
                        maxWeightThisWeek: maxWeightThisWeek
                    )
                    
                    // Trends
                    let current1RM = history.first?.exercise.sets.map {
                        Calculations.calculate1RM(weight: $0.weight ?? 0, reps: $0.reps)
                    }.max() ?? 0
                    
                    let previous1RM = (history.count > 1) ? history[1].exercise.sets.map {
                        Calculations.calculate1RM(weight: $0.weight ?? 0, reps: $0.reps)
                    }.max() ?? 0 : 0
                    
                    let trendDirection: TrendDirection = {
                        if current1RM > previous1RM { return .improving }
                        if current1RM < previous1RM { return .declining }
                        return .stable
                    }()
                    
                    let lastVolume = history.first?.exercise.sets.reduce(0) { $0 + ($1.weight ?? 0) * Float($1.reps) } ?? 0
                    let previousVolume = (history.count > 1) ? history[1].exercise.sets.reduce(0) { $0 + ($1.weight ?? 0) * Float($1.reps) } ?? 0 : 0
                    
                    let lastVolumeTrend: TrendDirection = {
                        if lastVolume > previousVolume { return .improving }
                        if lastVolume < previousVolume { return .declining }
                        return .stable
                    }()
                    
                    let details = ExerciseDetails(
                        exerciseName: exerciseName,
                        muscleGroup: muscleGroup,
                        weeklyStats: weeklyStats,
                        trends: ExerciseTrends(
                            current1RM: current1RM,
                            trendDirection: trendDirection,
                            lastVolume: lastVolume,
                            lastVolumeTrend: lastVolumeTrend
                        ),
                        history: history
                    )
                    
                    continuation.yield(details)
                }
            }
        }
    }
}
