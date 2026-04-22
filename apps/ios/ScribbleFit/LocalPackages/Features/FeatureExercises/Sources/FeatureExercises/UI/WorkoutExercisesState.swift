import Foundation
import CoreModel

public struct WorkoutExerciseUiModel: Identifiable, Sendable, Equatable {
    public let id: UUID
    public let name: String
    public let formattedSummary: String
    public let estimated1RMValue: Int?
    public let intensityValue: Int?
    public let improvementValue: Int?
    public let hasStats: Bool
}

public struct WorkoutExercisesState: Sendable {
    public let workoutId: UUID
    public var workoutDate: Date? = nil
    public var exercises: [Exercise] = []
    public var uiModels: [WorkoutExerciseUiModel] = []
    public var totalExercises: Int = 0
    public var totalSets: Int = 0
    public var totalVolume: String = ""
    public var isLoading: Bool = true
    public var weightUnit: WeightUnit = .lbs
    
    public init(workoutId: UUID) {
        self.workoutId = workoutId
    }
    
    public var dateString: String {
        guard let date = workoutDate else { return "" }
        let formatter = DateFormatter()
        formatter.dateFormat = "EEEE, MMMM d"
        return formatter.string(from: date)
    }
    
    public mutating func update(
        with workout: Workout,
        uiModels: [WorkoutExerciseUiModel],
        totalVolume: String
    ) {
        self.workoutDate = workout.date
        self.exercises = workout.exercises
        self.uiModels = uiModels
        self.totalExercises = workout.exercises.count
        self.totalSets = workout.exercises.reduce(0) { $0 + $1.sets.count }
        self.totalVolume = totalVolume
    }

    public func copy(
        workoutDate: Date?? = nil,
        exercises: [Exercise]? = nil,
        uiModels: [WorkoutExerciseUiModel]? = nil,
        totalExercises: Int? = nil,
        totalSets: Int? = nil,
        totalVolume: String? = nil,
        isLoading: Bool? = nil,
        weightUnit: WeightUnit? = nil
    ) -> WorkoutExercisesState {
        var newState = self
        if let workoutDate = workoutDate { newState.workoutDate = workoutDate }
        if let exercises = exercises { newState.exercises = exercises }
        if let uiModels = uiModels { newState.uiModels = uiModels }
        if let totalExercises = totalExercises { newState.totalExercises = totalExercises }
        if let totalSets = totalSets { newState.totalSets = totalSets }
        if let totalVolume = totalVolume { newState.totalVolume = totalVolume }
        if let isLoading = isLoading { newState.isLoading = isLoading }
        if let weightUnit = weightUnit { newState.weightUnit = weightUnit }
        return newState
    }
}
