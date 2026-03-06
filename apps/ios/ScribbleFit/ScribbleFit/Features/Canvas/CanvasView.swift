import SwiftUI

public struct CanvasView: View {
    @StateObject private var viewModel: CanvasViewModel
    
    public init(viewModel: CanvasViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 32) {
            Spacer().frame(height: 60)
            
            Text("Scribble.")
                .font(.system(size: 32, weight: .bold))
                .foregroundColor(Color(hex: "101010"))
            
            ZStack(alignment: .topLeading) {
                if viewModel.scribbleText.isEmpty {
                    Text("Bench 135x5, 135x5...")
                        .font(.system(size: 20))
                        .foregroundColor(.gray)
                        .padding(.top, 8)
                }
                
                TextEditor(text: $viewModel.scribbleText)
                    .font(.system(size: 20))
                    .foregroundColor(Color(hex: "101010"))
                    .scrollContentBackground(.hidden)
                    .lineSpacing(8)
            }
            
            Button(action: viewModel.submitScribble) {
                HStack {
                    if viewModel.isSyncing {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    } else {
                        Text("Log Workout")
                            .font(.system(size: 16, weight: .medium))
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(viewModel.scribbleText.isEmpty ? Color.gray : Color(hex: "101010"))
                .foregroundColor(.white)
                .cornerRadius(12)
            }
            .disabled(viewModel.scribbleText.isEmpty || viewModel.isSyncing)
            
            Spacer().frame(height: 20)
        }
        .padding(.horizontal, 24)
        .background(Color.white)
    }
}

// Helper for Hex colors
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }

        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
