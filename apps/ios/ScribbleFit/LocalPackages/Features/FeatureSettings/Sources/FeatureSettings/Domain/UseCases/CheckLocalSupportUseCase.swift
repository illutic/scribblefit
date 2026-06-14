import Foundation
import CoreModel
import FeatureAI

@MainActor
public final class CheckLocalSupportUseCase {
    private let localLLM: LLMService?

    public init(localLLM: LLMService?) {
        self.localLLM = localLLM
    }

    public func execute() async -> Bool {
        if #available(iOS 26.0, *) {
            if let localLLM = self.localLLM as? LocalLLMService {
                return await localLLM.isSupported()
            }
            return false
        } else {
            return false
        }
    }
}
