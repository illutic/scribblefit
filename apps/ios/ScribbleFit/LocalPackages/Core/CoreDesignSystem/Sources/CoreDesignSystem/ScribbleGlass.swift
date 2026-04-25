import SwiftUI

public struct ScribbleGlassModifier: ViewModifier {
    public let cornerRadius: CGFloat
    
    public init(cornerRadius: CGFloat = 16) {
        self.cornerRadius = cornerRadius
    }
    
    @MainActor
    public func body(content: Content) -> some View {
        let shape = RoundedRectangle(cornerRadius: cornerRadius)
        
        if #available(iOS 26.4.1, macOS 15.4.1, *) {
            // Using the shim to avoid compile-time errors on older SDKs
            content.glassEffectShim(in: shape)
        } else {
            content
                .background(.ultraThinMaterial, in: shape)
                .overlay(
                    shape
                        .stroke(Color.white.opacity(0.15), lineWidth: 0.5)
                )
        }
    }
}

private extension View {
    @ViewBuilder
    func glassEffectShim(in shape: some Shape) -> some View {
        #if canImport(_experimental_SwiftUI) || swift(>=6.1)
        // If the compiler supports it, use it. 
        // We use a #available check at the call site for runtime safety.
        if #available(iOS 26.4.1, macOS 15.4.1, *) {
            self.glassEffect(in: shape)
        } else {
            self.background(.ultraThinMaterial, in: shape)
        }
        #else
        // Fallback for older compilers that don't even have the symbol
        self
            .background(.ultraThinMaterial, in: shape)
            .overlay(
                shape
                    .stroke(Color.white.opacity(0.15), lineWidth: 0.5)
            )
        #endif
    }
}

#if !swift(>=6.1) && !canImport(_experimental_SwiftUI)
// Dummy implementation to satisfy the compiler on older SDKs
// The runtime #available check ensures this is never actually called on newer OSs
extension View {
    @available(iOS 26.4.1, macOS 15.4.1, *)
    func glassEffect(in shape: some Shape) -> some View {
        self
    }
}
#endif

public extension View {
    @MainActor
    func scribbleGlass(cornerRadius: CGFloat = 16) -> some View {
        self.modifier(ScribbleGlassModifier(cornerRadius: cornerRadius))
    }
}
