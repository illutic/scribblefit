import SwiftUI
#if SWIFT_PACKAGE
import CoreDesignSystem
#endif

public struct LedgerLoadingView: View {
    @State private var isAnimating = false

    public init() {}

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            ForEach(0..<4) { _ in
                skeletonCard
            }
        }
        .padding(.horizontal)
        .onAppear {
            withAnimation(.easeInOut(duration: 1.5).repeatForever(autoreverses: true)) {
                isAnimating = true
            }
        }
    }

    private var skeletonCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            RoundedRectangle(cornerRadius: 4)
                .fill(.ultraThinMaterial)
                .frame(width: 150, height: 20)
                .opacity(isAnimating ? 0.3 : 0.1)

            VStack(alignment: .leading, spacing: 8) {
                ForEach(0..<2) { _ in
                    RoundedRectangle(cornerRadius: 4)
                        .fill(.ultraThinMaterial)
                        .frame(maxWidth: .infinity)
                        .frame(height: 16)
                        .opacity(isAnimating ? 0.3 : 0.1)
                }
            }
        }
        .padding()
        .background(.ultraThinMaterial.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }
}
