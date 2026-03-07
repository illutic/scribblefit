import SwiftUI

public struct CanvasView: View {
    @StateObject private var viewModel: CanvasViewModel
    
    public init(viewModel: CanvasViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    public var body: some View {
        
            // Workout Tab (Home)
            ZStack {
                VStack(alignment: .leading, spacing: 0) {
                    Spacer().frame(height: 20)
                    
                    CanvasHeader(userName: "George")
                        .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                    
                    Spacer().frame(height: ScribbleFitSpacing.xl)
                    
                    ContextualInsightCard(text: "You hit chest on Thursday.\nReady for a Pull day? 💪")
                        .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                    
                    Spacer().frame(height: ScribbleFitSpacing.xl)
                    
                    QuickActionPills(pills: ["Repeat last Pull Day", "Log 5k Southsea run", "Rest Day"])
                        .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                    
                    Spacer() // Feed area
                }
                
                VStack {
                    Spacer()
                    ScribbleInputArea(
                        text: $viewModel.scribbleText,
                        isSyncing: viewModel.isSyncing,
                        onSubmit: viewModel.submitScribble
                    )
                    .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                    .padding(.bottom, ScribbleFitSpacing.medium)
                }
            }
            .background(ScribbleFitColor.background)
    }
}
