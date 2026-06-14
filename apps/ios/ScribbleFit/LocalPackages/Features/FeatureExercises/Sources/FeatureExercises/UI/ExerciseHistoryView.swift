import SwiftUI
import CoreModel
import CoreDesignSystem

public struct ExerciseHistoryView: View {
    @Bindable var store: ExerciseHistoryStore
    
    public init(store: ExerciseHistoryStore) {
        self.store = store
    }
    
    public var body: some View {
        ZStack {
            Color.scribbleBackground.ignoresSafeArea()
            
            if store.state.isLoading {
                ProgressView()
                    .tint(Color.scribblePrimary)
            } else if let error = store.state.error {
                Text(error)
                    .foregroundColor(Color.red)
            } else if store.state.history.isEmpty {
                Text("No history available.")
                    .foregroundColor(Color.scribbleMidGray)
            } else {
                ScrollView {
                    LazyVStack(spacing: 16, pinnedViews: [.sectionHeaders]) {
                        ForEach(store.state.groupedHistory, id: \.0) { monthYear, sessions in
                            Section(header: headerView(title: monthYear)) {
                                ForEach(sessions, id: \.exercise.id) { session in
                                    SessionRowView(session: session) {
                                        store.onIntent(.navigateToScribble(session.scribbleId, session.date))
                                    }
                                }
                            }
                        }
                    }
                    .padding(.horizontal, 24)
                    .padding(.vertical, 16)
                }
            }
        }
        .navigationTitle(store.state.exerciseName)
        #if os(iOS)
        .navigationBarTitleDisplayMode(.large)
        #endif
    }
    
    private func headerView(title: String) -> some View {
        HStack {
            Text(title)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(Color.scribbleMidGray)
                .letterSpacing(1)
            Spacer()
        }
        .padding(.vertical, 8)
        .background(Color.scribbleBackground)
    }
}

private struct SessionRowView: View {
    let session: ExerciseHistorySession
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(formatDate(session.date))
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(Color.scribblePrimary)
                    
                    Spacer()
                    
                    if session.isPersonalBest {
                        Text("PB")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(Color(red: 1.0, green: 0.84, blue: 0.0)) // Gold
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color(red: 1.0, green: 0.84, blue: 0.0).opacity(0.2))
                            .cornerRadius(4)
                    }
                }
                
                Text(session.summary)
                    .font(.system(size: 16))
                    .foregroundColor(Color.scribblePrimary)
                    .multilineTextAlignment(.leading)
                
                Text("\(String(localized: "Volume")): \(Int(session.totalVolume))")
                    .font(.system(size: 12))
                    .foregroundColor(Color.scribbleMidGray)
            }
            .padding(16)
            .background(Color.scribbleSurface)
            .cornerRadius(16)
        }
        .buttonStyle(PlainButtonStyle())
    }
    
    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "EEE, MMM d"
        return formatter
    }()
    
    private func formatDate(_ date: Date) -> String {
        return Self.dateFormatter.string(from: date)
    }
}

// Extension to support letter spacing in SwiftUI easily if not already present
extension View {
    func letterSpacing(_ spacing: CGFloat) -> some View {
        if #available(iOS 16.0, *) {
            return self.tracking(spacing)
        } else {
            return self
        }
    }
}
