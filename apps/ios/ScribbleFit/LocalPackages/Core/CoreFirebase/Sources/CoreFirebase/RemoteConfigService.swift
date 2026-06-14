import Foundation
import FirebaseRemoteConfig
import CoreModel

@MainActor
public final class RemoteConfigService {
    public static let shared = RemoteConfigService()

    private let remoteConfig: FirebaseRemoteConfig.RemoteConfig

    private init() {
        self.remoteConfig = FirebaseRemoteConfig.RemoteConfig.remoteConfig()
        let settings = RemoteConfigSettings()
        settings.minimumFetchInterval = 3600 // 1 hour
        remoteConfig.configSettings = settings

        remoteConfig.setDefaults([
            "suggestion_prompt": CoreModel.RemoteConfig.defaultSuggestionPrompt as NSObject,
            "summary_prompt": CoreModel.RemoteConfig.defaultSummaryPrompt as NSObject,
            "insight_prompt": CoreModel.RemoteConfig.defaultInsightPrompt as NSObject,
            "parse_prompt": CoreModel.RemoteConfig.defaultParsePrompt as NSObject
        ])
    }

    public func fetchConfig() async -> CoreModel.RemoteConfig {
        do {
            let status = try await remoteConfig.fetchAndActivate()
            print("Remote config fetch status: \(status)")
        } catch {
            print("Error fetching remote config: \(error)")
        }

        return CoreModel.RemoteConfig(
            summaryPrompt: remoteConfig.configValue(forKey: "summary_prompt").stringValue ?? CoreModel.RemoteConfig.defaultSummaryPrompt,
            suggestionPrompt: remoteConfig.configValue(forKey: "suggestion_prompt").stringValue ?? CoreModel.RemoteConfig.defaultSuggestionPrompt,
            insightPrompt: remoteConfig.configValue(forKey: "insight_prompt").stringValue ?? CoreModel.RemoteConfig.defaultInsightPrompt,
            parsePrompt: remoteConfig.configValue(forKey: "parse_prompt").stringValue ?? CoreModel.RemoteConfig.defaultParsePrompt
        )
    }
}
