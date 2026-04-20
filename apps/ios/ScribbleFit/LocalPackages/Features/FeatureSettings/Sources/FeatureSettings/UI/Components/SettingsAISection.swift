import SwiftUI
import CoreModel
import CoreDesignSystem

struct SettingsAISection: View {
    @Binding var provider: LLMProvider
    let isLocalSupported: Bool
    let onIntent: (SettingsIntent) -> Void
    
    var body: some View {
        SettingsContainer(title: String(localized: "AI Configuration")) {
            VStack(spacing: 20) {
                // Provider Selection
                VStack(alignment: .leading, spacing: 12) {
                    Picker(String(localized: "Provider"), selection: $provider) {
                        Text(String(localized: "Local")).tag(LLMProvider.local)
                        Text(String(localized: "Cloud")).tag(LLMProvider.gemini)
                    }
                    .pickerStyle(.segmented)
                    
                    VStack(alignment: .leading, spacing: 4) {
                        Text(provider == .gemini ? String(localized: "Cloud-based parsing via Gemini for Firebase") : String(localized: "On-device parsing via Apple System Language Models"))
                            .font(.scribbleLabelMedium)
                            .foregroundStyle(Color.scribbleMidGray)
                    }
                    .padding(.horizontal, 4)
                    
                    if provider == .local && !isLocalSupported {
                        HStack(alignment: .top, spacing: 12) {
                            Image(systemName: "info.circle.fill")
                                .foregroundStyle(Color.scribblePrimary)
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(String(localized: "LOCAL AI NOT SUPPORTED"))
                                    .font(.scribbleLabelMedium)
                                    .fontWeight(.bold)
                                    .foregroundStyle(Color.scribblePrimary)
                                
                                Text(String(localized: "Your device doesn't support on-device LLMs. Please switch to Cloud for AI features."))
                                    .font(.scribbleLabelMedium)
                                    .foregroundStyle(Color.scribbleMidGray)
                            }
                        }
                        .padding(16)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.scribbleSurfaceContainerLow)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .transition(.opacity.combined(with: .move(edge: .top)))
                    }
                }
            }
        }
    }
}
