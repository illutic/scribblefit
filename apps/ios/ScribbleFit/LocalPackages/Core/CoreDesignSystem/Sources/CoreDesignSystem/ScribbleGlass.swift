import SwiftUI

public struct ScribbleGlassModifier: ViewModifier {
    public let cornerRadius: CGFloat
    
    public init(cornerRadius: CGFloat = 16) {
        self.cornerRadius = cornerRadius
    }
    
    @MainActor
    public func body(content: Content) -> some View {
        let shape = RoundedRectangle(cornerRadius: cornerRadius)
        
        #if canImport(SwiftUI) && swift(>=6.0)
        // Check for the existence of glassEffect if possible, otherwise fallback
        // Since we can't easily check for a specific modifier's existence at compile time 
        // without a custom flag, we'll use the available check but we need to ensure 
        // it only compiles on environments that support this experimental API.
        if #available(iOS 26.0, macOS 26.0, *) {
            content
                .glassEffect(in: shape)
        } else {
            standardGlass(content: content, shape: shape)
        }
        #else
        standardGlass(content: content, shape: shape)
        #endif
    }
    
    @MainActor
    private func standardGlass(content: Content, shape: RoundedRectangle) -> some View {
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
