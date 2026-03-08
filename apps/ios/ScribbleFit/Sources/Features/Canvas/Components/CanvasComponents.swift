import SwiftUI

// Shared canvas UI components placeholder for future extraction
public struct EmptyFeedView: View {
    public init() {}
    public var body: some View {
        VStack(spacing: ScribbleFitSpacing.medium) {
            Text("Start logging").font(.headline).foregroundStyle(ScribbleFitColor.midGray)
            Text("Type a workout below to get started").font(.subheadline).foregroundStyle(ScribbleFitColor.midGray)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
