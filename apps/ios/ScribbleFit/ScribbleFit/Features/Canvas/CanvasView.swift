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
    }
}

// Re-defining components locally to ensure visibility or checking CanvasComponents.swift
struct CanvasHeader: View {
    let userName: String
    
    var body: some View {
        HStack {
            Text("EVENING, \(userName.uppercased())")
                .font(ScribbleFitFont.labelMedium().bold())
                .kerning(0.8)
                .foregroundColor(ScribbleFitColor.primaryText)
            Spacer()
            Image(systemName: "line.3.horizontal")
                .font(.system(size: 20, weight: .medium))
                .foregroundColor(ScribbleFitColor.primaryText)
        }
    }
}

struct QuickActionPills: View {
    let pills: [String]
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(pills, id: \.self) { pill in
                    ScribbleFitPill(pill, action: { })
                }
            }
        }
    }
}

struct ScribbleInputArea: View {
    @Binding var text: String
    let isSyncing: Bool
    let onSubmit: () -> Void
    
    var body: some View {
        ScribbleFitTextField(
            text: $text,
            placeholder: "Message ScribbleFit...",
            trailingIcon: AnyView(
                Group {
                    if !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        Button(action: onSubmit) {
                            if isSyncing {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: ScribbleFitColor.primaryText))
                            } else {
                                Image(systemName: "arrow.up.circle.fill")
                                    .font(.system(size: 32))
                                    .foregroundColor(ScribbleFitColor.primaryText)
                            }
                        }
                        .disabled(isSyncing)
                    } else {
                        Button(action: { /* Mic action */ }) {
                            Image(systemName: "mic.fill")
                                .font(.system(size: 20))
                                .foregroundColor(ScribbleFitColor.primaryText)
                        }
                    }
                }
            )
        )
    }
}
