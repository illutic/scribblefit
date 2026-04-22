import Foundation

public enum Calculations {
    /**
     * Epley formula: Weight × (1 + Reps/30)
     */
    public static func calculate1RM(weight: Float, reps: Int) -> Float {
        if reps <= 0 { return 0 }
        if reps == 1 { return weight }
        return weight * (1.0 + Float(reps) / 30.0)
    }

    public static func calculateVolume(weight: Float?, reps: Int) -> Float {
        return (weight ?? 0) * Float(reps)
    }
}
