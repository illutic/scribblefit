import XCTest

final class BottomSheetUITests: XCTestCase {
    private var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    override func tearDownWithError() throws {
        app = nil
    }

    // MARK: - Helpers

    /// Submits a scribble and waits for it to appear as a parsed card.
    private func addScribbleAndWaitForParsing(_ text: String, timeout: TimeInterval = 30) -> XCUIElement? {
        let textField = app.textFields.firstMatch
        guard textField.waitForExistence(timeout: 5) else { return nil }

        textField.tap()
        textField.typeText(text)

        // Tap the send button (arrow.up.circle.fill)
        let sendButton = app.buttons.matching(NSPredicate(format: "label CONTAINS 'arrow' OR identifier CONTAINS 'arrow'")).firstMatch
        if sendButton.waitForExistence(timeout: 3) {
            sendButton.tap()
        } else {
            // Try submitting via keyboard
            textField.typeText("\n")
        }

        // Wait for a parsed or logged scribble card to appear
        let parsedCard = app.buttons["parsedScribbleCard"]
        if parsedCard.waitForExistence(timeout: timeout) {
            return parsedCard
        }
        return nil
    }

    /// Opens the bottom sheet by tapping on a scribble card (parsed or logged).
    private func openBottomSheet() -> Bool {
        // Try parsed card first
        let parsedCard = app.buttons["parsedScribbleCard"]
        if parsedCard.waitForExistence(timeout: 3) {
            parsedCard.firstMatch.tap()
            return app.staticTexts["Confirm Exercise"].waitForExistence(timeout: 5)
        }

        // Try logged card
        let loggedCard = app.buttons["loggedScribbleCard"]
        if loggedCard.waitForExistence(timeout: 3) {
            loggedCard.firstMatch.tap()
            return app.staticTexts["Scribble Details"].waitForExistence(timeout: 5)
        }

        return false
    }

    // MARK: - Tests

    /// Verify that tapping a parsed scribble card opens the bottom sheet with confirm button visible.
    func testParsedScribbleShowsConfirmButton() throws {
        // Try to find or create a parsed scribble
        let parsedCard = app.buttons["parsedScribbleCard"]
        if !parsedCard.waitForExistence(timeout: 5) {
            // No parsed card available — add one
            guard addScribbleAndWaitForParsing("Bench press 80kg 3x10") != nil else {
                throw XCTSkip("Could not create a parsed scribble (AI service may be unavailable)")
            }
        }

        parsedCard.firstMatch.tap()

        let sheetTitle = app.staticTexts["Confirm Exercise"]
        XCTAssertTrue(sheetTitle.waitForExistence(timeout: 5), "Bottom sheet should appear")

        let confirmButton = app.buttons["confirmScribbleButton"]
        XCTAssertTrue(confirmButton.waitForExistence(timeout: 3), "Confirm button should be visible for parsed scribble")
    }

    /// Verify that tapping a logged (completed) scribble card opens the sheet.
    func testLoggedScribbleShowsDetails() throws {
        let loggedCard = app.buttons["loggedScribbleCard"]
        if !loggedCard.waitForExistence(timeout: 5) {
            // No logged card available — add and confirm one
            let parsed = addScribbleAndWaitForParsing("Squat 100kg 3x5")
            guard let parsed = parsed else {
                throw XCTSkip("No parsed scribble available to test")
            }
            parsed.tap()
            let confirmButton = app.buttons["confirmScribbleButton"]
            confirmButton.tap()
            
            // Wait for it to become logged
            guard loggedCard.waitForExistence(timeout: 10) else {
                throw XCTSkip("Confirm action didn't result in a logged card")
            }
        }

        loggedCard.firstMatch.tap()

        let sheetTitle = app.staticTexts["Scribble Details"]
        XCTAssertTrue(sheetTitle.waitForExistence(timeout: 5), "Scribble Details sheet should appear")

        let deleteButton = app.buttons["deleteScribbleButton"]
        XCTAssertTrue(deleteButton.exists, "Delete button should still be visible")
    }

    /// Verify that the delete set button removes a set row from the sheet.
    func testDeleteSetRemovesSetRow() throws {
        guard openBottomSheet() else {
            throw XCTSkip("No scribble card available to open bottom sheet")
        }

        // Count the initial number of "Set N:" labels
        let setLabels = app.staticTexts.matching(NSPredicate(format: "label BEGINSWITH 'Set '"))
        let initialCount = setLabels.count

        guard initialCount > 1 else {
            throw XCTSkip("Need at least 2 sets to test deletion (only \(initialCount) found)")
        }

        // Tap the first delete set button
        let deleteSetButton = app.buttons["deleteSetButton"]
        XCTAssertTrue(deleteSetButton.firstMatch.waitForExistence(timeout: 3), "Delete set button should exist")
        deleteSetButton.firstMatch.tap()

        // Wait for UI to update
        sleep(1)

        // Verify count decreased
        let newCount = app.staticTexts.matching(NSPredicate(format: "label BEGINSWITH 'Set '")).count
        XCTAssertEqual(newCount, initialCount - 1, "One set should have been removed (was \(initialCount), now \(newCount))")
    }

    /// Verify that the delete set button keeps the sheet open (doesn't dismiss it).
    func testDeleteSetKeepsSheetOpen() throws {
        guard openBottomSheet() else {
            throw XCTSkip("No scribble card available to open bottom sheet")
        }

        let deleteSetButton = app.buttons["deleteSetButton"]
        guard deleteSetButton.firstMatch.waitForExistence(timeout: 3) else {
            throw XCTSkip("No delete set button found (exercise may have no sets)")
        }

        deleteSetButton.firstMatch.tap()

        // Sheet should still be visible
        XCTAssertTrue(app.staticTexts["Confirm Exercise"].exists || app.staticTexts["Scribble Details"].exists, "Bottom sheet should remain open after deleting a set")
    }

    /// Verify that the delete (scribble) button exists and dismisses the sheet.
    func testDeleteScribbleButtonDismissesSheet() throws {
        guard openBottomSheet() else {
            throw XCTSkip("No scribble card available to open bottom sheet")
        }

        let deleteButton = app.buttons["deleteScribbleButton"]
        XCTAssertTrue(deleteButton.waitForExistence(timeout: 3), "Delete scribble button should exist")

        deleteButton.tap()

        // Sheet should dismiss
        let sheetTitleExists = app.staticTexts["Confirm Exercise"].exists || app.staticTexts["Scribble Details"].exists
        XCTAssertFalse(sheetTitleExists, "Bottom sheet should dismiss after deleting scribble")
    }
}
