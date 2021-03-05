package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TestDialogManager
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.idea.core.moveCaret
import org.junit.Test

class ErrorStatesTest: GenerateBuilderActionTestBase() {

    @Test
    fun testErrorMessageWhenNotInADataClass() {
        // arrange
        val inputFile = "NotADataClass.kt"
        val caretOffset = 30
        myFixture.configureByFile(inputFile)

        var shownMessage = ""
        TestDialogManager.setTestDialog { dialogMessage ->
            shownMessage = dialogMessage
            return@setTestDialog Messages.CANCEL
        }

        // act
        myFixture.editor.moveCaret(caretOffset)
        myFixture.testAction(GenerateBuilderAction())

        // assert
        assertThat(shownMessage)
            .isEqualTo("Builder generation only works for Kotlin data classes")
    }

}