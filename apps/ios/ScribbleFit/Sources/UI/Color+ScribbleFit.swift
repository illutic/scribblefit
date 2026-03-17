import SwiftUI

extension Color {
    static func scribbleFitBackground(for scheme: ColorScheme) -> Color {
        scheme == .dark ? Color(hex: "000000") : Color(hex: "FFFFFF")
    }
    
    static func scribbleFitSoftGray(for scheme: ColorScheme) -> Color {
        scheme == .dark ? Color(hex: "1A1A1A") : Color(hex: "F7F7F8")
    }
    
    static func scribbleFitRichBlack(for scheme: ColorScheme) -> Color {
        scheme == .dark ? Color(hex: "FFFFFF") : Color(hex: "101010")
    }
    
    static func scribbleFitMidGray(for scheme: ColorScheme) -> Color {
        Color(hex: "8E8EA0") // Same for both in Android spec
    }
    
    static func scribbleFitLightGray(for scheme: ColorScheme) -> Color {
        scheme == .dark ? Color(hex: "2C2C2E") : Color(hex: "E5E5EA")
    }
    
    static func scribbleFitStrongGray(for scheme: ColorScheme) -> Color {
        Color(hex: "636366") // Same for both in Android spec
    }
    
    static func scribbleFitDangerRed(for scheme: ColorScheme) -> Color {
        scheme == .dark ? Color(hex: "FF453A") : Color(hex: "FF3B30")
    }
    
    static func scribbleFitSuccessGreen(for scheme: ColorScheme) -> Color {
        scheme == .dark ? Color(hex: "30D158") : Color(hex: "34C759")
    }
    
    static func scribbleFitBlue(for scheme: ColorScheme) -> Color {
        Color(hex: "2B8CEE")
    }
    
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
