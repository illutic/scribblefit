import SwiftUI
import CoreDesignSystem

struct CanvasDatePickerView: View {
    let initialDate: Date
    let onDateSelected: (Date) -> Void
    let onDismiss: () -> Void

    @State private var selection: Date

    init(initialDate: Date, onDateSelected: @escaping (Date) -> Void, onDismiss: @escaping () -> Void) {
        self.initialDate = initialDate
        self.onDateSelected = onDateSelected
        self.onDismiss = onDismiss
        self._selection = State(initialValue: initialDate)
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack {
                    DatePicker(
                        String(localized: "Select Date"),
                        selection: $selection,
                        in: ...Date(),
                        displayedComponents: .date
                    )
                    .datePickerStyle(.graphical)
                    .tint(.scribblePrimary)
                    .padding()

                    Spacer()
                }
            }
            .navigationTitle(String(localized: "Calendar"))
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .toolbar {
                #if os(iOS)
                ToolbarItem(placement: .topBarLeading) {
                    Button(String(localized: "Cancel")) { onDismiss() }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button(String(localized: "Done")) { onDateSelected(selection) }
                }
                #else
                ToolbarItem(placement: .cancellationAction) {
                    Button(String(localized: "Cancel")) { onDismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(String(localized: "Done")) { onDateSelected(selection) }
                }
                #endif
            }
        }
        .presentationDetents([.medium])
    }
}
