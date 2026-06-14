import XCTest
import CoreModel
@testable import FeatureSets

final class ReorderSetsUseCaseTests: XCTestCase {

    private var sut: ReorderSetsUseCase!

    override func setUp() {
        super.setUp()
        sut = ReorderSetsUseCase()
    }

    override func tearDown() {
        sut = nil
        super.tearDown()
    }

    // MARK: - Happy Path

    func test_execute_renumbersSetsContinuously() {
        let sets = [
            ExerciseSet(id: UUID(), setNumber: 5, weight: 100.0, reps: 10),
            ExerciseSet(id: UUID(), setNumber: 3, weight: 90.0, reps: 8),
            ExerciseSet(id: UUID(), setNumber: 1, weight: 80.0, reps: 6)
        ]

        let result = sut.execute(sets: sets)

        XCTAssertEqual(result[0].setNumber, 1)
        XCTAssertEqual(result[1].setNumber, 2)
        XCTAssertEqual(result[2].setNumber, 3)
    }

    func test_execute_preservesWeightAndReps() {
        let sets = [
            ExerciseSet(id: UUID(), setNumber: 99, weight: 120.5, reps: 5),
            ExerciseSet(id: UUID(), setNumber: 50, weight: nil, reps: 12)
        ]

        let result = sut.execute(sets: sets)

        XCTAssertEqual(result[0].weight, 120.5)
        XCTAssertEqual(result[0].reps, 5)
        XCTAssertNil(result[1].weight)
        XCTAssertEqual(result[1].reps, 12)
    }

    func test_execute_preservesIds() {
        let id1 = UUID()
        let id2 = UUID()
        let sets = [
            ExerciseSet(id: id1, setNumber: 7, weight: 50.0, reps: 3),
            ExerciseSet(id: id2, setNumber: 2, weight: 60.0, reps: 4)
        ]

        let result = sut.execute(sets: sets)

        XCTAssertEqual(result[0].id, id1)
        XCTAssertEqual(result[1].id, id2)
    }

    func test_execute_preservesRpeAndNotes() {
        let sets = [
            ExerciseSet(id: UUID(), setNumber: 3, weight: 80.0, reps: 6, rpe: 8.5, notes: "Hard")
        ]

        let result = sut.execute(sets: sets)

        XCTAssertEqual(result[0].rpe, 8.5)
        XCTAssertEqual(result[0].notes, "Hard")
    }

    func test_execute_singleSet_numberedOne() {
        let sets = [ExerciseSet(id: UUID(), setNumber: 42, weight: 60.0, reps: 8)]

        let result = sut.execute(sets: sets)

        XCTAssertEqual(result.count, 1)
        XCTAssertEqual(result[0].setNumber, 1)
    }

    // MARK: - Edge Cases

    func test_execute_emptySets_returnsEmpty() {
        let result = sut.execute(sets: [])

        XCTAssertTrue(result.isEmpty)
    }

    func test_execute_alreadyOrderedSets_stillCorrect() {
        let sets = [
            ExerciseSet(id: UUID(), setNumber: 1, weight: 100.0, reps: 10),
            ExerciseSet(id: UUID(), setNumber: 2, weight: 100.0, reps: 10),
            ExerciseSet(id: UUID(), setNumber: 3, weight: 100.0, reps: 10)
        ]

        let result = sut.execute(sets: sets)

        XCTAssertEqual(result.map { $0.setNumber }, [1, 2, 3])
    }

    func test_execute_largeBatch_correctlyNumbered() {
        let count = 20
        let sets = (0..<count).map { i in
            ExerciseSet(id: UUID(), setNumber: count - i, weight: Float(i * 10), reps: i + 1)
        }

        let result = sut.execute(sets: sets)

        XCTAssertEqual(result.count, count)
        for (index, set) in result.enumerated() {
            XCTAssertEqual(set.setNumber, index + 1)
        }
    }
}
