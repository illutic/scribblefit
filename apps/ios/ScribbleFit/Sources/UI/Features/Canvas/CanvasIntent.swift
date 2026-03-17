import Foundation

public enum CanvasIntent {
    // Canvas
    case updateScribbleText(String)
    case addScribble(String)
    case clickOnScribble(Scribble)
    case onPreviousDayClick
    case onNextDayClick

    // Scribble Dialog
    case updateScribble(Scribble)
    case deleteScribble(Scribble)
    case confirmScribble(Scribble)
    case dismissScribbleDialog

    // Navigation
    case navigateBack
    case navigateToProfile
}
