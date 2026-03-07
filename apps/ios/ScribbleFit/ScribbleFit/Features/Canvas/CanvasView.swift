import SwiftUI

public struct CanvasView: View {
    @StateObject private var viewModel: CanvasViewModel
    
    public init(viewModel: CanvasViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    public var body: some View {
        ZStack {
            ScribbleFitColor.background.ignoresSafeArea()
            
            VStack(alignment: .leading, spacing: 0) {
                CanvasHeader(userName: "George")
                    .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                    .padding(.top, 10)
                
                ScrollViewReader { proxy in
                    ScrollView {
                        VStack(alignment: .leading, spacing: ScribbleFitSpacing.small) {
                            ForEach(viewModel.feedItems) { item in
                                FeedItemRow(item: item, onRetry: { _ in })
                                    .id(item.id)
                            }
                            
                            if viewModel.feedItems.count <= 1 {
                                QuickActionPills(pills: ["Repeat last Pull Day", "Log 5k Run", "Rest Day"])
                                    .padding(.top, 16)
                            }
                        }
                        .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                        .padding(.vertical, ScribbleFitSpacing.medium)
                    }
                    .onChange(of: viewModel.feedItems.count) { _ in
                        withAnimation {
                            proxy.scrollTo(viewModel.feedItems.last?.id, anchor: .bottom)
                        }
                    }
                }
                
                Spacer()
                
                // Input Pill
                ScribbleInputArea(
                    text: $viewModel.scribbleText,
                    isSyncing: viewModel.isSyncing,
                    onSubmit: viewModel.submitScribble
                )
                .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                .padding(.bottom, 8)
            }
        }
        .onAppear {
            viewModel.refreshFeed()
        }
    }
}
