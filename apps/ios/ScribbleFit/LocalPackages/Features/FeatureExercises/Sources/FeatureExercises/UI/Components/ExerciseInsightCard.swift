import SwiftUI
import CoreModel
import CoreDesignSystem

struct ExerciseInsightCard: View {
    let insight: ExercisePerformanceInsight?
    let isGenerating: Bool
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            if isGenerating {
                HStack {
                    Spacer()
                    ProgressView()
                        .tint(Color.scribblePrimary)
                    Spacer()
                }
                .padding(.vertical, 8)
            } else if let insight = insight {
                HStack(alignment: .top, spacing: 8) {
                    Text("🔥")
                        .font(.system(size: 20))
                    
                    VStack(alignment: .leading, spacing: 12) {
                        Text(String(localized: "RECOMMENDATION"))
                            .font(.scribbleLabelMedium)
                            .fontWeight(.bold)
                            .kerning(1)
                            .foregroundStyle(Color.scribbleMidGray)
                        
                        Text(insight.breakdownText)
                            .font(.scribbleBodyMedium)
                            .fontWeight(.regular)
                            .foregroundStyle(Color.scribblePrimary)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                }
            } else {
                Text(String(localized: "Perform more sessions to get AI recommendations."))
                    .font(.scribbleBodyMedium)
                    .foregroundStyle(Color.scribbleMidGray)
            }
        }
        .padding(24)
        .frame(maxWidth: .infinity, alignment: .leading)
        .scribbleGlass(cornerRadius: 16)
    }
}
