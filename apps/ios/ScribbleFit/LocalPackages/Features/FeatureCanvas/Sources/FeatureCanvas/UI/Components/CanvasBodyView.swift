import SwiftUI
import CoreModel
import CoreDesignSystem

struct CanvasBodyView: View {
    let scribbles: [Scribble]
    let aiInsights: [AIInsight]
    let isGeneratingInsights: Bool
    let weightUnit: WeightUnit
    let emptyText: String
    let onScribbleClick: (Scribble) -> Void
    let onIntent: (CanvasIntent) -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 40) {
                if isGeneratingInsights {
                    AIInsightsLoadingView()
                } else if !aiInsights.isEmpty {
                    AIInsightsList(insights: aiInsights)
                }
                
                if scribbles.isEmpty {
                    VStack(spacing: 24) {
                        Spacer().frame(height: 60)
                        Text(emptyText)
                            .font(.scribbleHeadlineSmall)
                            .multilineTextAlignment(.center)
                            .foregroundStyle(Color.scribbleMidGray)
                            .lineSpacing(8)
                    }
                    .padding(.horizontal, 40)
                } else {
                    LazyVStack(spacing: 24) {
                        ForEach(scribbles) { scribble in
                            ScribbleCard(
                                scribble: scribble,
                                weightUnit: weightUnit,
                                onClick: { onScribbleClick(scribble) },
                                onIntent: onIntent
                            )
                        }
                    }
                }
                
                Spacer().frame(height: 80) // Bottom padding for floating input bar
            }
            .padding(.horizontal, 24)
            .padding(.top, 24)
        }
        .scrollDismissesKeyboard(.interactively)
    }
}

struct AIInsightsLoadingView: View {
    @State private var isAnimating = false
    
    var body: some View {
        VStack(spacing: 16) {
            ForEach(0..<1, id: \.self) { _ in
                HStack(alignment: .top, spacing: 16) {
                    Circle()
                        .fill(Color.scribblePrimary.opacity(0.1))
                        .frame(width: 32, height: 32)
                    
                    VStack(alignment: .leading, spacing: 8) {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.scribbleMidGray.opacity(0.2))
                            .frame(width: 80, height: 12)
                        
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.scribblePrimary.opacity(0.1))
                            .frame(maxWidth: .infinity)
                            .frame(height: 16)
                        
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.scribblePrimary.opacity(0.1))
                            .frame(width: 200, height: 16)
                    }
                }
                .padding(16)
                .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 16))
                .opacity(isAnimating ? 0.5 : 1.0)
            }
        }
        .onAppear {
            withAnimation(.easeInOut(duration: 1.0).repeatForever(autoreverses: true)) {
                isAnimating = true
            }
        }
    }
}

struct AIInsightsList: View {
    let insights: [AIInsight]
    @State private var expandedIds: Set<UUID> = []

    var body: some View {
        VStack(spacing: 16) {
            ForEach(insights) { insight in
                let isExpanded = expandedIds.contains(insight.id)
                HStack(alignment: .top, spacing: 16) {
                    Image(systemName: insight.iconName)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundStyle(Color.scribblePrimary)
                        .frame(width: 32, height: 32)
                        .background(Color.scribblePrimary.opacity(0.1))
                        .clipShape(Circle())

                    VStack(alignment: .leading, spacing: 4) {
                        Text(insight.insightType.rawValue.uppercased())
                            .font(.scribbleLabelMedium)
                            .fontWeight(.bold)
                            .kerning(1)
                            .foregroundStyle(Color.scribbleMidGray)

                        Text(insight.text)
                            .font(.scribbleBodyMedium)
                            .fontWeight(.medium)
                            .foregroundStyle(Color.scribblePrimary)
                            .lineSpacing(4)
                            .lineLimit(isExpanded ? nil : 2)
                    }

                    Spacer()
                }
                .padding(16)
                .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 16))
                .contentShape(Rectangle())
                .onTapGesture {
                    withAnimation(.easeInOut(duration: 0.25)) {
                        if isExpanded {
                            expandedIds.remove(insight.id)
                        } else {
                            expandedIds.insert(insight.id)
                        }
                    }
                }
            }
        }
    }
}

private extension AIInsight {
    var iconName: String {
        switch insightType {
        case .summary: return "sparkles"
        case .trend: return "chart.line.uptrend.xyaxis"
        case .advice: return "lightbulb.fill"
        }
    }
}
