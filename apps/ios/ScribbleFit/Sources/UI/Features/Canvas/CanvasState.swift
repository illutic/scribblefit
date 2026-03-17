import Foundation

public struct CanvasState: Equatable {
    public var isLoading: Bool = false
    public var currentDate: Date = Date()
    public var error: Error? = nil
    public var currentScribbleText: String = ""
    public var editingScribbleId: UUID? = nil
    public var scribbles: [Scribble] = []
    public var selectedScribble: Scribble? = nil

    public init() {}

    public var isCurrentDate: Bool {
        Calendar.current.isDateInToday(currentDate)
    }

    public var dateString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM d, yyyy"
        return formatter.string(from: currentDate)
    }

    public var emptyScribbleText: String {
        "Start scribbling.\nType your first set below."
    }

    public var textfieldPlaceholder: String {
        "Enter workout (e.g., Bench 100kg 3x5)"
    }

    public var appName: String {
        "ScribbleFit"
    }

    public static func == (lhs: CanvasState, rhs: CanvasState) -> Bool {
        lhs.isLoading == rhs.isLoading &&
        lhs.currentDate == rhs.currentDate &&
        lhs.currentScribbleText == rhs.currentScribbleText &&
        lhs.editingScribbleId == rhs.editingScribbleId &&
        lhs.scribbles == rhs.scribbles &&
        lhs.selectedScribble == rhs.selectedScribble
    }
}
