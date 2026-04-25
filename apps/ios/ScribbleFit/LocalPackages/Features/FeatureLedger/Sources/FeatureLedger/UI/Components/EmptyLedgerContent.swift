import SwiftUI
import CoreDesignSystem

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
                .foregroundStyle(Color.scribblePrimary.opacity(0.4))
            
            Text(message)
                .font(.scribbleHeadlineSmall)
                .foregroundStyle(Color.scribblePrimary)
                .multilineTextAlignment(.center)
            
            Button(action: onCTATapped) {
                Text(ctaLabel)
                    .font(.scribbleTitleMedium)
                    .padding()
                    .background(Color.scribblePrimary)
                    .foregroundStyle(Color.scribbleOnPrimary)
                    .clipShape(Capsule())
            }
        }
        .padding(40)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
