import Foundation
import CoreModel

public struct ScribbleDetailsState: Equatable, Sendable {
    public var scribble: Scribble?
    public var isLoading: Bool = false
    public var error: String?
    public var weightUnit: WeightUnit = .kgs

    public init(
        scribble: Scribble? = nil,
        isLoading: Bool = false,
        error: String? = nil,
        weightUnit: WeightUnit = .kgs
    ) {
        self.scribble = scribble
        self.isLoading = isLoading
        self.error = error
        self.weightUnit = weightUnit
    }

    public func copy(
        scribble: Scribble?? = nil,
        isLoading: Bool? = nil,
        error: String?? = nil,
        weightUnit: WeightUnit? = nil
    ) -> ScribbleDetailsState {
        var newState = self
        if let scribble = scribble { newState.scribble = scribble }
        if let isLoading = isLoading { newState.isLoading = isLoading }
        if let error = error { newState.error = error }
        if let weightUnit = weightUnit { newState.weightUnit = weightUnit }
        return newState
    }
    
    public var canLog: Bool {
        guard let scribble = scribble else { return false }
        return scribble.status != .completed
    }
}
