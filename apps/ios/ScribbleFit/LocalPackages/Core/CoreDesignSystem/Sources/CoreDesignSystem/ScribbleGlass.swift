import SwiftUI

public struct ScribbleGlassModifier: ViewModifier {
    public let cornerRadius: CGFloat
    
    public init(cornerRadius: CGFloat = 16) {
        self.cornerRadius = cornerRadius
    }
    
    @MainActor
    public func body(content: Content) -> some View {
        let shape = RoundedRectangle(cornerRadius: cornerRadius)
        content
            .background(.ultraThinMaterial, in: shape)
            .overlay(
                shape
                    .stroke(Color.white.opacity(0.15), lineWidth: 0.5)
            )
    }
}

public extension View {
    @MainActor
    public func scribbleGlass(cornerRadius: CGFloat = 16) -> some View {
        self.modifier(ScribbleGlassModifier(cornerRadius: cornerRadius))
    }
}
