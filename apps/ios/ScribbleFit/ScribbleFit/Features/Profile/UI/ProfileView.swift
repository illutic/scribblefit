import SwiftUI

public struct ProfileView: View {
    @StateObject private var viewModel: ProfileViewModel

    public init(viewModel: ProfileViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: ScribbleFitSpacing.large) {
                // Profile Header
                HStack(spacing: 16) {
                    Circle()
                        .fill(ScribbleFitColor.secondaryBackground)
                        .frame(width: 80, height: 80)
                        .overlay(
                            Text(viewModel.uiState.userName.prefix(1).uppercased())
                                .font(.system(size: 32, weight: .bold))
                                .foregroundColor(ScribbleFitColor.primaryText)
                        )

                    VStack(alignment: .leading, spacing: 4) {
                        Text(viewModel.uiState.userName)
                            .font(ScribbleFitFont.titleLarge())
                            .foregroundColor(ScribbleFitColor.primaryText)

                        Text("Member since \(formatDate(viewModel.uiState.stats?.joinDate ?? Date()))")
                            .font(ScribbleFitFont.bodyMedium())
                            .foregroundColor(ScribbleFitColor.secondaryText)
                    }
                }
                .padding(.top, 24)

                // Stats Grid
                VStack(alignment: .leading, spacing: 12) {
                    Text("LIFETIME STATS")
                        .font(ScribbleFitFont.labelMedium().bold())
                        .kerning(0.8)
                        .foregroundColor(ScribbleFitColor.secondaryText)

                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        StatCard(label: "Total Workouts", value: "\(viewModel.uiState.stats?.totalWorkouts ?? 0)")
                        StatCard(label: "Total Volume", value: formatVolume(viewModel.uiState.stats?.lifetimeVolume ?? 0))
                        StatCard(label: "PRs Hit", value: "\(viewModel.uiState.stats?.prCount ?? 0)")
                        StatCard(label: "Current Streak", value: "3 days")
                    }
                }

                // Actions
                VStack(spacing: 0) {
                    Button(action: viewModel.onSettingsClick) {
                        HStack {
                            Image(systemName: "gearshape.fill")
                                .foregroundColor(ScribbleFitColor.primaryText)
                            Text("App Settings")
                                .font(ScribbleFitFont.bodyLarge())
                                .foregroundColor(ScribbleFitColor.primaryText)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(ScribbleFitColor.secondaryText)
                        }
                        .padding(.vertical, 16)
                    }
                    Divider().background(ScribbleFitColor.divider)
                }

                Spacer()
            }
            .padding(.horizontal, ScribbleFitSpacing.screenPadding)
        }
        .background(ScribbleFitColor.background)
    }

    private func StatCard(label: String, value: String) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(ScribbleFitFont.labelMedium())
                .foregroundColor(ScribbleFitColor.secondaryText)
            Text(value)
                .font(ScribbleFitFont.titleLarge())
                .foregroundColor(ScribbleFitColor.primaryText)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(ScribbleFitColor.secondaryBackground)
        .cornerRadius(ScribbleFitShape.large)
    }

    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM yyyy"
        return formatter.string(from: date)
    }

    private func formatVolume(_ volume: Double) -> String {
        if volume >= 1000 {
            return "\(Int(volume / 1000))k lbs"
        } else {
            return "\(Int(volume)) lbs"
        }
    }
}
