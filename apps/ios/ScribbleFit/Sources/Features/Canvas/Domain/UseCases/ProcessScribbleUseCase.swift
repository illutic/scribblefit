import Foundation

public final class ProcessScribbleUseCase: Sendable {
    private let scribbleRepository: any ScribbleRepository

    public init(scribbleRepository: any ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }

    public func execute(rawText: String) async {
        await scribbleRepository.enqueueScribble(rawText: rawText)
    }
}
