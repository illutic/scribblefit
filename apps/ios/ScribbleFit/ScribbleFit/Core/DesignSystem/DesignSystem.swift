import SwiftUI

// MARK: - Color Tokens
public enum ScribbleFitColor {
    // Light Mode
    public static let white = Color(hex: "FFFFFF")
    public static let softGray = Color(hex: "F7F7F8")
    public static let richBlack = Color(hex: "101010")
    public static let midGray = Color(hex: "8E8EA0")
    public static let lightGray = Color(hex: "E5E5EA")

    // Dark Mode
    public static let darkBlack = Color(hex: "101010")
    public static let darkGray = Color(hex: "1C1C1E")
    public static let offWhite = Color(hex: "F2F2F7")
    public static let dividerGray = Color(hex: "2C2C2E")

    // Semantic Dynamic Colors
    public static var background: Color {
        Color(UIColor { traitCollection in
            return traitCollection.userInterfaceStyle == .dark ? UIColor(hex: "101010") : UIColor(hex: "FFFFFF")
        })
    }

    public static var secondaryBackground: Color {
        Color(UIColor { traitCollection in
            return traitCollection.userInterfaceStyle == .dark ? UIColor(hex: "1C1C1E") : UIColor(hex: "F7F7F8")
        })
    }

    public static var primaryText: Color {
        Color(UIColor { traitCollection in
            return traitCollection.userInterfaceStyle == .dark ? UIColor(hex: "F2F2F7") : UIColor(hex: "101010")
        })
    }

    public static var secondaryText: Color {
        Color(UIColor { traitCollection in
            return traitCollection.userInterfaceStyle == .dark ? UIColor(hex: "8E8EA0") : UIColor(hex: "8E8EA0")
        })
    }

    public static var divider: Color {
        Color(UIColor { traitCollection in
            return traitCollection.userInterfaceStyle == .dark ? UIColor(hex: "2C2C2E") : UIColor(hex: "E5E5EA")
        })
    }
}

// MARK: - Spacing Tokens
public enum ScribbleFitSpacing {
    public static let none: CGFloat = 0
    public static let xxs: CGFloat = 4
    public static let xs: CGFloat = 8
    public static let small: CGFloat = 12
    public static let medium: CGFloat = 16
    public static let large: CGFloat = 24
    public static let xl: CGFloat = 32
    public static let xxl: CGFloat = 48
    public static let xxxl: CGFloat = 64
    public static let huge: CGFloat = 80

    public static let screenPadding: CGFloat = 24
    public static let verticalGap: CGFloat = 32
}

// MARK: - Shape Tokens
public enum ScribbleFitShape {
    public static let small: CGFloat = 8
    public static let medium: CGFloat = 12
    public static let large: CGFloat = 16
    public static let extraLarge: CGFloat = 28
}

// MARK: - Typography Tokens
public enum ScribbleFitFont {
    public static func headlineLarge() -> Font { .system(size: 32, weight: .bold) }
    public static func headlineMedium() -> Font { .system(size: 28, weight: .bold) }
    public static func headlineSmall() -> Font { .system(size: 24, weight: .bold) }
    public static func titleLarge() -> Font { .system(size: 22, weight: .semibold) }
    public static func titleMedium() -> Font { .system(size: 18, weight: .semibold) }
    public static func titleSmall() -> Font { .system(size: 14, weight: .medium) }
    public static func bodyLarge() -> Font { .system(size: 16, weight: .regular) }
    public static func bodyMedium() -> Font { .system(size: 14, weight: .regular) }
    public static func labelMedium() -> Font { .system(size: 12, weight: .medium) }
}

// MARK: - Atomic Components

public struct ScribbleFitButton<Content: View>: View {
    let action: () -> Void
    let enabled: Bool
    let content: Content

    public init(enabled: Bool = true, action: @escaping () -> Void, @ViewBuilder content: () -> Content) {
        self.action = action
        self.enabled = enabled
        self.content = content()
    }

    public var body: some View {
        Button(action: action) {
            content
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(enabled ? ScribbleFitColor.primaryText : Color.gray.opacity(0.3))
                .foregroundColor(enabled ? ScribbleFitColor.background : .white)
                .cornerRadius(ScribbleFitShape.large)
        }
        .disabled(!enabled)
    }
}

public struct ScribbleFitPill: View {
    let text: String
    let action: () -> Void

    public init(_ text: String, action: @escaping () -> Void) {
        self.text = text
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            Text(text)
                .font(ScribbleFitFont.titleSmall())
                .foregroundColor(ScribbleFitColor.primaryText)
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .background(ScribbleFitColor.secondaryBackground)
                .cornerRadius(ScribbleFitShape.large)
        }
    }
}

public struct ScribbleFitTextField: View {
    @Binding var text: String
    let placeholder: String
    var trailingIcon: AnyView?

    public init(text: Binding<String>, placeholder: String, trailingIcon: AnyView? = nil) {
        self._text = text
        self.placeholder = placeholder
        self.trailingIcon = trailingIcon
    }

    public var body: some View {
        HStack(spacing: ScribbleFitSpacing.small) {
            ZStack(alignment: .leading) {
                if text.isEmpty {
                    Text(placeholder)
                        .font(ScribbleFitFont.bodyLarge())
                        .foregroundColor(ScribbleFitColor.secondaryText.opacity(0.6))
                }

                TextField("", text: $text)
                    .font(ScribbleFitFont.bodyLarge())
                    .foregroundColor(ScribbleFitColor.primaryText)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
            }

            if let icon = trailingIcon {
                icon
            }
        }
        .padding(.horizontal, 16)
        .frame(height: 56)
        .background(ScribbleFitColor.secondaryBackground)
        .cornerRadius(ScribbleFitShape.extraLarge)
    }
}

// MARK: - Helpers
extension UIColor {
    convenience init(hex: String) {
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
            (a, r, g, b) = (1, 1, 1, 0)
        }
        self.init(red: CGFloat(r) / 255, green: CGFloat(g) / 255, blue: CGFloat(b) / 255, alpha: CGFloat(a) / 255)
    }
}

extension Color {
    init(hex: String) {
        self.init(uiColor: UIColor(hex: hex))
    }
}
