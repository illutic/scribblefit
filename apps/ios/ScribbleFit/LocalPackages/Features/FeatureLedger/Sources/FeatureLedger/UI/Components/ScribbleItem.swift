import SwiftUI
import CoreModel
import CoreDesignSystem

public struct ScribbleItem: View {
    private let dateString: String
    private let scribbles: [Scribble]
    private let weightUnit: WeightUnit
    private let onScribbleTapped: (UUID) -> Void
    private let onExerciseTapped: (String) -> Void
    
    public init(
        dateString: String,
        scribbles: [Scribble],
        weightUnit: WeightUnit,
        onScribbleTapped: @escaping (UUID) -> Void,
        onExerciseTapped: @escaping (String) -> Void
    ) {
        self.dateString = dateString
        self.scribbles = scribbles
        self.weightUnit = weightUnit
        self.onScribbleTapped = onScribbleTapped
        self.onExerciseTapped = onExerciseTapped
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(dateString)
                .font(.headline)
                .foregroundStyle(Color.scribbleMidGray)
                
            ForEach(scribbles) { scribble in
                Button(action: { onScribbleTapped(scribble.id) }) {
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text(String(localized: "Scribble"))
                                .font(.system(size: 14, weight: .bold))
                                .foregroundStyle(Color.scribblePrimary)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(Color.scribblePrimary.opacity(0.1))
                                .cornerRadius(4)
                            
                            Spacer()
                            
                            Text(scribble.createdAt.formatted(date: .omitted, time: .shortened))
                                .font(.system(size: 14))
                                .foregroundStyle(Color.secondary)
                        }
                        
                        Text(scribble.rawText)
                            .font(.system(size: 16, weight: .medium))
                            .foregroundStyle(Color.primary)
                            .lineLimit(2)
                        
                        if !scribble.exercises.isEmpty {
                            FlowLayout(spacing: 8) {
                                ForEach(scribble.exercises, id: \.id) { exercise in
                                    Text(exercise.canonicalName)
                                        .font(.system(size: 12))
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 4)
                                        .background(Color.secondary.opacity(0.1))
                                        .cornerRadius(4)
                                        .onTapGesture {
                                            onExerciseTapped(exercise.canonicalName)
                                        }
                                }
                            }
                        }
                    }
                    .padding(16)
                    .background(Color.scribbleSurface)
                    .cornerRadius(12)
                }
                .buttonStyle(.plain)
            }
        }
    }
}

// Simple FlowLayout for exercises
struct FlowLayout: Layout {
    var spacing: CGFloat
    
    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let sizes = subviews.map { $0.sizeThatFits(.unspecified) }
        var totalHeight: CGFloat = 0
        var totalWidth: CGFloat = 0
        var currentLineWidth: CGFloat = 0
        var currentLineHeight: CGFloat = 0
        
        for size in sizes {
            if currentLineWidth + size.width > (proposal.width ?? .infinity) {
                totalHeight += currentLineHeight + spacing
                currentLineWidth = size.width + spacing
                currentLineHeight = size.height
            } else {
                currentLineWidth += size.width + spacing
                currentLineHeight = max(currentLineHeight, size.height)
            }
            totalWidth = max(totalWidth, currentLineWidth)
        }
        
        return CGSize(width: totalWidth, height: totalHeight + currentLineHeight)
    }
    
    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        var currentX = bounds.minX
        var currentY = bounds.minY
        var currentLineHeight: CGFloat = 0
        
        for subview in subviews {
            let size = subview.sizeThatFits(.unspecified)
            if currentX + size.width > bounds.maxX {
                currentX = bounds.minX
                currentY += currentLineHeight + spacing
                currentLineHeight = 0
            }
            
            subview.place(at: CGPoint(x: currentX, y: currentY), proposal: .unspecified)
            currentX += size.width + spacing
            currentLineHeight = max(currentLineHeight, size.height)
        }
    }
}
