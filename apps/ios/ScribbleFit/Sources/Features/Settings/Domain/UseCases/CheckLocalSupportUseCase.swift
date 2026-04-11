import Foundation
#if SWIFT_PACKAGE
import CoreModel
import FeatureAI
#endif

@MainActor
public final class CheckLocalSupportUseCase {
    private let localLLM: LLMService?
    
    public init(localLLM: LLMService?) {
        self.localLLM = localLLM
    }
    
    public func execute() async -> Bool {
        if #available(iOS 26.0, *) {
            if !(self.localLLM is LocalLLMService) {
                return false
            }
            let localLLM = self.localLLM as! LocalLLMService
            return await localLLM.isSupported()
        } else {
            return false
        }
    }
}
