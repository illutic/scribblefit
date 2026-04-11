import SwiftUI
#if canImport(UIKit)
import UIKit
#endif

extension Color {
    public static let scribblePrimary = Color.primary
    
    public static let scribbleOnPrimary = Color(light: Color(hex: "E5E2E1"), dark: Color(hex: "000000"))
    
    public static let scribbleBackground = Color(light: Color(white: 0.98), dark: Color(white: 0.06))
    
    public static let scribbleSurface = Color(light: Color(white: 0.95), dark: Color(white: 0.12))
    
    public static let scribbleSurfaceContainerLow = Color(light: Color(hex: "F3F3F4"), dark: Color(hex: "242424"))
    
    public static let scribbleMidGray = Color(hex: "8E8EA0")
    public static let scribbleDanger = Color(light: Color(hex: "FF3B30"), dark: Color(hex: "FF453A"))
    public static let scribbleSuccess = Color(light: Color(hex: "34C759"), dark: Color(hex: "30D158"))
    
    public static let scribbleSurfaceContainerHigh = Color(light: Color(hex: "E8E8E8"), dark: Color(hex: "3C3C3E"))
    
    // Internal helper for light/dark mode colors without assets
    init(light: Color, dark: Color) {
        #if canImport(UIKit)
        self.init(uiColor: UIColor { traitCollection in
            switch traitCollection.userInterfaceStyle {
            case .dark:
                return UIColor(dark)
            default:
                return UIColor(light)
            }
        })
        #else
        self.init(light)
        #endif
    }
}

extension Font {
    public static let scribbleDisplayLarge = Font.system(size: 56, weight: .bold)
    public static let scribbleHeadlineSmall = Font.system(size: 24, weight: .semibold)
    public static let scribbleTitleMedium = Font.system(size: 18, weight: .medium)
    public static let scribbleBodyMedium = Font.system(size: 14, weight: .regular)
    public static let scribbleLabelMedium = Font.system(size: 12, weight: .semibold)
}

public struct ScribbleFitSpacing {
    public static let small: CGFloat = 8
    public static let medium: CGFloat = 16
    public static let large: CGFloat = 24
    public static let screenPadding: CGFloat = 24
}

extension View {
    public func scribbleGlass(cornerRadius: CGFloat = 16) -> some View {
        #if os(iOS)
        if #available(iOS 26.0, *) {
            return self.glassEffect(.regular, in: RoundedRectangle(cornerRadius: cornerRadius))
        } else {
            return self.background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: cornerRadius))
        }
        #else
        return self.background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: cornerRadius))
        #endif
    }
}

extension ThemePreference {
    public func getColorScheme() -> ColorScheme? {
        switch self {
        case .dark: return .dark
        case .light: return .light
        case .system: return nil
        }
    }
}
