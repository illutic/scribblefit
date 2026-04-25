import SwiftUI
import CoreDesignSystem

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
                .foregroundStyle(Color.scribblePrimary)
            
            Button(action: onDateRangeTapped) {
                HStack {
                    Image(systemName: "calendar")
                    Text(dateRange)
                    Spacer()
                }
                .padding()
                .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 12))
                .foregroundStyle(Color.scribblePrimary)
            }
        }
        .padding(.horizontal)
    }
}
