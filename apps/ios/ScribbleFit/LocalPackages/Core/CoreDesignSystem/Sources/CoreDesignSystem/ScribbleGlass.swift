import SwiftUI

public struct ScribbleGlassModifier: ViewModifier {
    public let cornerRadius: CGFloat
    
    public init(cornerRadius: CGFloat = 16) {
        self.cornerRadius = cornerRadius
    }
    
    @MainActor
    public func body(content: Content) -> some View {
        let shape = RoundedRectangle(cornerRadius: cornerRadius)
        if #available(iOS 26.0, macOS 26.0, *) {
            content
                .glassEffect(in: shape)
        } else {
            content
                .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: cornerRadius))
                .overlay(
                    shape
                        .stroke(Color.white.opacity(0.15), lineWidth: 0.5)
                )
        }
    }
}

public extension View {
    @MainActor
    public func scribbleGlass(cornerRadius: CGFloat = 16) -> some View {
        self.modifier(ScribbleGlassModifier(cornerRadius: cornerRadius))
    }
}
