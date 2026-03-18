import Foundation
import SwiftData
@preconcurrency import Combine

@MainActor
public final class InsightsRepositoryImpl: InsightsRepository {
    private let modelContainer: ModelContainer
    private let modelContext: ModelContext
    private let changeSubject = PassthroughSubject<Void, Never>()

    public init(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
        self.modelContext = ModelContext(modelContainer)
    }

    public func getVolumeInsights(startDate: Date, endDate: Date) -> AsyncStream<[VolumeDataPoint]> {
        AsyncStream { continuation in
            let predicate = #Predicate<ScribbleEntity> { scribble in
                scribble.createdAt >= startDate && scribble.createdAt <= endDate
            }
            
            let descriptor = FetchDescriptor<ScribbleEntity>(predicate: predicate, sortBy: [SortDescriptor(\.createdAt)])
            
            let cancellable = changeSubject
                .prepend(())
                .sink { [weak self] _ in
                    guard let self = self else { return }
                    Task { @MainActor in
                        do {
                            let entities = try self.modelContext.fetch(descriptor)
                            let points = entities.map { entity in
                                let totalVolume = entity.exercises.reduce(0.0) { acc, exercise in
                                    acc + exercise.sets.reduce(0.0) { setAcc, set in
                                        setAcc + (Double(set.reps) * set.weight)
                                    }
                                }
                                return VolumeDataPoint(date: entity.createdAt, volume: totalVolume)
                            }
                            continuation.yield(points)
                        } catch {
                            continuation.yield([])
                        }
                    }
                }
            
            continuation.onTermination = { _ in
                cancellable.cancel()
            }
        }
    }

    public func getFrequencyInsights() -> AsyncStream<FrequencyData> {
        AsyncStream { continuation in
            let descriptor = FetchDescriptor<ScribbleEntity>(sortBy: [SortDescriptor(\.createdAt)])
            
            let cancellable = changeSubject
                .prepend(())
                .sink { [weak self] _ in
                    guard let self = self else { return }
                    Task { @MainActor in
                        do {
                            let entities = try self.modelContext.fetch(descriptor)
                            guard !entities.isEmpty else {
                                continuation.yield(FrequencyData(totalWorkouts: 0, workoutsPerWeek: 0))
                                return
                            }
                            
                            let totalWorkouts = entities.count
                            let firstDate = entities.first!.createdAt
                            let lastDate = entities.last!.createdAt
                            
                            let days = max(1, Calendar.current.dateComponents([.day], from: firstDate, to: lastDate).day ?? 1)
                            let weeks = Double(days) / 7.0
                            let workoutsPerWeek = Double(totalWorkouts) / max(1.0, weeks)
                            
                            continuation.yield(FrequencyData(totalWorkouts: totalWorkouts, workoutsPerWeek: workoutsPerWeek))
                        } catch {
                            continuation.yield(FrequencyData(totalWorkouts: 0, workoutsPerWeek: 0))
                        }
                    }
                }
            
            continuation.onTermination = { _ in
                cancellable.cancel()
            }
        }
    }

    public func getMuscleDistributionInsights() -> AsyncStream<[MuscleGroupDistribution]> {
        AsyncStream { continuation in
            let descriptor = FetchDescriptor<ScribbleEntity>()
            
            let cancellable = changeSubject
                .prepend(())
                .sink { [weak self] _ in
                    guard let self = self else { return }
                    Task { @MainActor in
                        do {
                            let entities = try self.modelContext.fetch(descriptor)
                            var counts: [String: Int] = [:]
                            var totalExercises = 0
                            
                            for scribble in entities {
                                for exercise in scribble.exercises {
                                    counts[exercise.muscleGroup, default: 0] += 1
                                    totalExercises += 1
                                }
                            }
                            
                            guard totalExercises > 0 else {
                                continuation.yield([])
                                return
                            }
                            
                            let distribution = counts.map { (muscleGroup, count) in
                                MuscleGroupDistribution(
                                    muscleGroup = muscleGroup,
                                    percentage: Double(count) / Double(totalExercises)
                                )
                            }.sorted { $0.percentage > $1.percentage }
                            
                            continuation.yield(distribution)
                        } catch {
                            continuation.yield([])
                        }
                    }
                }
            
            continuation.onTermination = { _ in
                cancellable.cancel()
            }
        }
    }
}
