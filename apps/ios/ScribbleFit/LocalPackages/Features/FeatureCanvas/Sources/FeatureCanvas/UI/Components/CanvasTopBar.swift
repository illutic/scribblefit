import SwiftUI
import CoreDesignSystem

struct CanvasTopBar: View {
    let dateString: String
    @Binding var isDatePickerVisible: Bool
    let onSettingsClick: () -> Void
    let onPrevDayClick: () -> Void
    let onNextDayClick: () -> Void
    let onDateRangeClick: () -> Void

    var body: some View {
        HStack(spacing: 0) {
            Button(action: onSettingsClick) {
                Image(systemName: "gearshape.fill")
                    .font(.system(size: 20))
                    .foregroundStyle(Color.scribblePrimary)
            }
            .padding(.leading, 24)

            Spacer()

            HStack(spacing: 12) {
                Button(action: onPrevDayClick) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(Color.scribblePrimary)
                }

                Button(action: onDateRangeClick) {
                    Text(dateString)
                        .font(.scribbleTitleMedium)
                        .foregroundStyle(Color.scribblePrimary)
                }

                Button(action: onNextDayClick) {
                    Image(systemName: "chevron.right")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(Color.scribblePrimary)
                }
            }

            Spacer()

            // Empty placeholder for symmetry
            Color.clear
                .frame(width: 44, height: 44)
                .padding(.trailing, 24)
        }
        .frame(height: 64)
        .background(Color.scribbleBackground)
    }
}
