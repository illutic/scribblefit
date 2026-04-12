import SwiftUI
#if SWIFT_PACKAGE
import CoreDesignSystem
#endif

public struct LedgerHeader: View {
    let dateRange: String
    let onDateRangeTapped: () -> Void

    public init(dateRange: String, onDateRangeTapped: @escaping () -> Void) {
        self.dateRange = dateRange
        self.onDateRangeTapped = onDateRangeTapped
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(String(localized: "Ledger"))
                .font(.scribbleHeadlineSmall)
                .foregroundStyle(.scribblePrimary)
            
            Button(action: onDateRangeTapped) {
                HStack {
                    Image(systemName: "calendar")
                    Text(dateRange)
                    Spacer()
                }
                .padding()
                .scribbleGlass(cornerRadius: 12)
                .foregroundStyle(.scribblePrimary)
            }
        }
        .padding(.horizontal)
    }
}
