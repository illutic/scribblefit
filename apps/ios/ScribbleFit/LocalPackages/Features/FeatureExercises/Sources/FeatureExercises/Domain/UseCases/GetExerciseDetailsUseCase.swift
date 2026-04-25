import Foundation
import CoreModel
import CoreCommon

@MainActor
public final class GetExerciseDetailsUseCase {
    private let scribbleRepository: ScribbleRepository
    
    public init(scribbleRepository: ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }
    
    public func execute(exerciseName: String) -> AsyncStream<ExerciseDetails> {
        let stream = scribbleRepository.observeScribblesWithExercise(exerciseName: exerciseName)
        
        return AsyncStream { continuation in
            Task {
                for await scribbles in stream {
                    let history = scribbles.sorted(by: { $0.createdAt > $1.createdAt }).flatMap { scribble in
                        scribble.exercises.filter { $0.canonicalName.lowercased() == exerciseName.lowercased() }.map { exercise in
                            ExerciseHistorySession(
                                workoutId: scribble.id,
                                date: scribble.createdAt,
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
                    let totalVolumeThisWeek = thisWeekSessions.reduce(Float(0.0)) { total, session in
                        total + session.exercise.sets.reduce(Float(0.0)) { setTotal, set in
                            setTotal + (set.weight ?? Float(0.0)) * Float(set.reps)
                        }
                    }
                    let maxWeightThisWeek = thisWeekSessions.flatMap { $0.exercise.sets }
                        .compactMap { $0.weight }.max() ?? Float(0.0)
                    
                    let weeklyStats = WeeklyStats(
                        sessionsThisWeek: sessionsThisWeek,
                        totalVolumeThisWeek: totalVolumeThisWeek,
                        maxWeightThisWeek: maxWeightThisWeek
                    )
                    
                    // Trends
                    let current1RM = history.first?.exercise.sets.map {
                        Calculations.calculate1RM(weight: $0.weight ?? Float(0.0), reps: $0.reps)
                    }.max() ?? Float(0.0)
                    
                    let previousMax = (history.count > 1) ? history.dropFirst().map { session in
                        session.exercise.sets.map {
                            Calculations.calculate1RM(weight: $0.weight ?? Float(0.0), reps: $0.reps)
                        }.max() ?? Float(0.0)
                    }.max() ?? Float(0.0) : Float(0.0)
                    
                    let improvement = previousMax > 0 ? (current1RM - previousMax) / previousMax : Float(0.0)
                    
                    let trendDirection: TrendDirection = {
                        let threshold: Float = 0.05
                        if improvement > threshold { return .improving }
                        if improvement < -threshold { return .declining }
                        return .stable
                    }()
                    
                    let intensity = maxWeightThisWeek > 0 ? current1RM / maxWeightThisWeek : Float(0.0)
                    
                    let lastVolume = history.first?.exercise.sets.reduce(Float(0.0)) { $0 + ($1.weight ?? Float(0.0)) * Float($1.reps) } ?? Float(0.0)
                    let previousVolume = (history.count > 1) ? history[1].exercise.sets.reduce(Float(0.0)) { $0 + ($1.weight ?? Float(0.0)) * Float($1.reps) } ?? Float(0.0) : Float(0.0)
                    
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
                            lastVolumeTrend: lastVolumeTrend,
                            intensity: intensity,
                            improvement: improvement
                        ),
                        history: history
                    )
                    
                    continuation.yield(details)
                }
            }
        }
    }
}
