import SwiftUI
import CoreDesignSystem

struct ExerciseDetailsHeader: View {
    let exerciseName: String
    let onBackClick: () -> Void
    
    var body: some View {
        HStack(spacing: 16) {
            Text(exerciseName)
                .font(.scribbleHeadlineSmall)
                .foregroundStyle(Color.scribblePrimary)
            
            Spacer()
        }
        .padding(.horizontal, 24)
        .padding(.vertical, 16)
        .background(Color.scribbleBackground)
    }
}
