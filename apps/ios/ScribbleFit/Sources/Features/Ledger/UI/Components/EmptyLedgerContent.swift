import SwiftUI
#if SWIFT_PACKAGE
import CoreDesignSystem
#endif

public struct EmptyLedgerContent: View {
    let message: String
    let ctaLabel: String
    let onCTATapped: () -> Void

    public init(
        message: String,
        ctaLabel: String,
        onCTATapped: @escaping () -> Void
    ) {
        self.message = message
        self.ctaLabel = ctaLabel
        self.onCTATapped = onCTATapped
    }

    public var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "calendar.badge.exclamationmark")
                .font(.system(size: 64))
                .foregroundStyle(.scribblePrimary.opacity(0.4))
            
            Text(message)
                .font(.headline)
                .foregroundStyle(.scribblePrimary)
                .multilineTextAlignment(.center)
            
            Button(action: onCTATapped) {
                Text(ctaLabel)
                    .font(.headline)
                    .padding()
                    .background(Color.scribblePrimary)
                    .foregroundStyle(.scribbleBackground)
                    .clipShape(Capsule())
            }
        }
        .padding(40)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
