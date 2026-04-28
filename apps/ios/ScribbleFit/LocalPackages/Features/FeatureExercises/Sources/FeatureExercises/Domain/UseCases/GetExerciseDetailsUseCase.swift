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
                    let sortedScribbles = scribbles.sorted(by: { $0.createdAt > $1.createdAt })
                    
                    var history: [ExerciseHistorySession] = []
                    
                    // First pass to calculate all-time bests for isPersonalBest flag
                    var allTimeMaxWeight: Float = 0
                    var allTimeMaxVolume: Float = 0
                    
                    for scribble in sortedScribbles {
                        for exercise in scribble.exercises where exercise.canonicalName.lowercased() == exerciseName.lowercased() {
                            let volume = exercise.sets.reduce(Float(0.0)) { $0 + Calculations.calculateVolume(weight: $1.weight, reps: $1.reps) }
                            let maxWeight = exercise.sets.compactMap { $0.weight }.max() ?? 0.0
                            
                            allTimeMaxWeight = max(allTimeMaxWeight, maxWeight)
                            allTimeMaxVolume = max(allTimeMaxVolume, volume)
                        }
                    }
                    
                    // Second pass to build history with flags
                    for scribble in sortedScribbles {
                        let matchingExercises = scribble.exercises.filter { $0.canonicalName.lowercased() == exerciseName.lowercased() }
                        for exercise in matchingExercises {
                            let volume = exercise.sets.reduce(Float(0.0)) { $0 + Calculations.calculateVolume(weight: $1.weight, reps: $1.reps) }
                            let maxWeight = exercise.sets.compactMap { $0.weight }.max() ?? 0.0
                            
                            let isPB = (maxWeight >= allTimeMaxWeight && allTimeMaxWeight > 0) || (volume >= allTimeMaxVolume && allTimeMaxVolume > 0)
                            
                            history.append(ExerciseHistorySession(
                                exercise: exercise,
                                totalVolume: volume,
                                maxWeight: maxWeight,
                                summary: exercise.summary(weightUnit: .kgs),
                                isPersonalBest: isPB,
                                scribbleId: scribble.id
                            ))
                        }
                    }
                    
                    let muscleGroup = history.first?.exercise.muscleGroup ?? ""
                    
                    // Weekly Stats
                    let calendar = Calendar.current
                    let now = Date()
                    let startOfWeek = calendar.date(from: calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: now))!
                    
                    let thisWeekSessions = history.filter { $0.date >= startOfWeek }
                    let sessionsThisWeek = thisWeekSessions.count
                    let totalVolumeThisWeek = thisWeekSessions.reduce(Float(0.0)) { $0 + $1.totalVolume }
                    let maxWeightThisWeek = thisWeekSessions.map { $0.maxWeight }.max() ?? Float(0.0)
                    
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
                    
                    let lastVolume = history.first?.totalVolume ?? Float(0.0)
                    let previousVolume = (history.count > 1) ? history[1].totalVolume : Float(0.0)
                    
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
