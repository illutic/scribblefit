package com.scribblefit.feature.canvas.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scribblefit.core.model.Set as WorkoutSet

@RunWith(AndroidJUnit4::class)
class ScribbleConfirmationBottomSheetTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun testScribble() = Scribble(
        id = 1L,
        rawText = "bench press 80kg 3x10",
        status = ScribbleStatus.SUCCESS,
        createdAt = System.currentTimeMillis(),
        exercises = listOf(
            Exercise(
                id = 1L,
                canonicalName = "Bench Press",
                muscleGroup = "Chest",
                sets = listOf(
                    WorkoutSet(id = 1L, setNumber = 1, weight = 80f, reps = 10),
                    WorkoutSet(id = 2L, setNumber = 2, weight = 85f, reps = 8),
                    WorkoutSet(id = 3L, setNumber = 3, weight = 90f, reps = 6),
                )
            )
        )
    )

    private fun setContent(
        scribble: Scribble = testScribble(),
        onConfirm: (Scribble) -> Unit = {},
        onEdit: (Scribble) -> Unit = {},
        onDelete: (Scribble) -> Unit = {},
        onDismiss: () -> Unit = {},
        onUpdateExerciseName: (Long, String) -> Unit = { _, _ -> },
        onUpdateSetWeight: (Long, Long, String) -> Unit = { _, _, _ -> },
        onUpdateSetReps: (Long, Long, String) -> Unit = { _, _, _ -> },
        onDeleteSet: (Long, Long) -> Unit = { _, _ -> },
    ) {
        composeTestRule.setContent {
            ScribbleFitTheme {
                ScribbleConfirmationBottomSheet(
                    scribble = scribble,
                    weightUnit = Weight.KGS,
                    onConfirm = onConfirm,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onDismiss = onDismiss,
                    onUpdateExerciseName = onUpdateExerciseName,
                    onUpdateSetWeight = onUpdateSetWeight,
                    onUpdateSetReps = onUpdateSetReps,
                    onDeleteSet = onDeleteSet,
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun bottomSheet_rendersExerciseData() {
        setContent()

        composeTestRule.onNodeWithText("Bench Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("80.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("10").assertIsDisplayed()
        composeTestRule.onNodeWithText("85.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("8").assertIsDisplayed()
        composeTestRule.onNodeWithText("Set 1: ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Set 2: ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Set 3: ").assertIsDisplayed()
    }

    @Test
    fun repsField_canBeCleared_withoutSnappingBack() {
        setContent()

        // Find the reps field showing "10" (set 1) and clear it
        composeTestRule.onNodeWithText("10").performTextReplacement("")
        composeTestRule.waitForIdle()

        // The field should NOT snap back to "10" — that was the bug
        composeTestRule.onNodeWithText("10").assertDoesNotExist()
    }

    @Test
    fun weightField_canBeCleared_withoutSnappingBack() {
        setContent()

        // Find the weight field showing "80.0" (set 1) and clear it
        composeTestRule.onNodeWithText("80.0").performTextReplacement("")
        composeTestRule.waitForIdle()

        // The field should NOT snap back to "80.0"
        composeTestRule.onNodeWithText("80.0").assertDoesNotExist()
    }

    @Test
    fun repsField_acceptsNewInput_andFiresCallback() {
        var capturedReps: String? = null

        setContent(
            onUpdateSetReps = { _, _, newReps -> capturedReps = newReps }
        )

        // Replace reps "10" with "12"
        composeTestRule.onNodeWithText("10").performTextReplacement("12")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("12").assertIsDisplayed()
        assert(capturedReps == "12") { "Expected onUpdateSetReps to be called with \"12\", got \"$capturedReps\"" }
    }

    @Test
    fun weightField_acceptsDecimalInput_andFiresCallback() {
        var capturedWeight: String? = null

        setContent(
            onUpdateSetWeight = { _, _, newWeight -> capturedWeight = newWeight }
        )

        // Replace weight "80.0" with "75.5"
        composeTestRule.onNodeWithText("80.0").performTextReplacement("75.5")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("75.5").assertIsDisplayed()
        assert(capturedWeight == "75.5") { "Expected onUpdateSetWeight to be called with \"75.5\", got \"$capturedWeight\"" }
    }

    @Test
    fun confirmButton_firesCallback() {
        var confirmCalled = false
        val scribble = testScribble()

        setContent(
            scribble = scribble,
            onConfirm = { confirmCalled = true }
        )

        val confirmText = composeTestRule.activity.getString(R.string.canvas_dialog_confirm)
        composeTestRule.onNodeWithText(confirmText).performClick()
        composeTestRule.waitForIdle()

        assert(confirmCalled) { "Expected onConfirm callback to be invoked" }
    }

    @Test
    fun deleteSetButton_firesCallback() {
        var deletedExerciseId: Long? = null
        var deletedSetId: Long? = null

        setContent(
            onDeleteSet = { exId, setId ->
                deletedExerciseId = exId
                deletedSetId = setId
            }
        )

        // Click the first "Remove set" button (matched by content description)
        composeTestRule
            .onAllNodes(hasContentDescription("Remove set"))[0]
            .performClick()
        composeTestRule.waitForIdle()

        assert(deletedExerciseId == 1L) { "Expected exerciseId 1, got $deletedExerciseId" }
        assert(deletedSetId == 1L) { "Expected setId 1, got $deletedSetId" }
    }
}
