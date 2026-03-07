import SwiftUI

public struct CanvasView: View {
    @StateObject private var viewModel: CanvasViewModel
    @EnvironmentObject private var navManager: NavigationManager
    
    public init(viewModel: CanvasViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    public var body: some View {
        ZStack {
            ScribbleFitColor.background.ignoresSafeArea()
            
            VStack(alignment: .leading, spacing: 0) {
                CanvasHeader(
                    userName: viewModel.uiState.userName,
                    greeting: viewModel.uiState.greeting,
                    onMenuClick: { navManager.navigate(to: AppDestination.settings, in: .workout) }
                )
                .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                .padding(.top, 10)
                
                ScrollViewReader { proxy in
                    ScrollView {
                        VStack(alignment: .leading, spacing: ScribbleFitSpacing.small) {
                            // If feed is empty and we have a suggestion, show it first
                            if viewModel.uiState.feedItems.isEmpty, let suggestion = viewModel.uiState.homeSuggestion {
                                PromptBubble(item: PromptItem(
                                    id: "initial",
                                    timestamp: Date(),
                                    text: suggestion.text,
                                    emoji: suggestion.emoji,
                                    type: .pattern
                                ))
                                .padding(.bottom, 16)
                            }

                            ForEach(viewModel.uiState.feedItems) { item in
                                FeedItemRow(
                                    item: item,
                                    onRetry: viewModel.onRetryScribble
                                )
                                .id(item.id)
                            }
                            
                            if viewModel.uiState.feedItems.count <= 1 {
                                QuickActionPills(
                                    actions: viewModel.uiState.quickActions,
                                    onActionClick: viewModel.onQuickActionClick
                                )
                                .padding(.top, 16)
                            }
                        }
                        .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                        .padding(.vertical, ScribbleFitSpacing.medium)
                    }
                    .onChange(of: viewModel.uiState.feedItems.count) { _ in
                        withAnimation {
                            proxy.scrollTo(viewModel.uiState.feedItems.last?.id, anchor: .bottom)
                        }
                    }
                }
                
                Spacer()
                
                // Input Pill
                ScribbleInputArea(
                    text: Binding(
                        get: { viewModel.uiState.scribbleText },
                        set: { viewModel.onTextChange($0) }
                    ),
                    isSyncing: viewModel.uiState.isSyncing,
                    isRecording: viewModel.uiState.isRecording,
                    onSubmit: viewModel.submitScribble,
                    onMicClick: viewModel.onMicClick
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
