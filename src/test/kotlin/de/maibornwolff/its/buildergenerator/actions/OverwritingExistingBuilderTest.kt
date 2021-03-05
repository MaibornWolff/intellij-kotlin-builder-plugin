package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TestDialogManager
import com.intellij.openapi.vfs.VfsUtil
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.idea.core.moveCaret
import org.junit.Test
import java.io.File

class OverwritingExistingBuilderTest: GenerateBuilderActionTestBase() {

    @Test
    fun testOverwritePromptedWhenBuilderAlreadyExists() {
        // arrange
        val inputFile = "ClassWithExistingBuilder.kt"
        val caretOffset = 30
        val promptReturnResult = Messages.CANCEL

        val builderFileResource = "ClassWithExistingBuilderBuilder.kt"
        myFixture.copyFileToProject(builderFileResource)

        myFixture.configureByFile(inputFile)

        var shownMessage = ""
        TestDialogManager.setTestDialog { dialogMessage ->
            shownMessage = dialogMessage
            return@setTestDialog promptReturnResult
        }

        // act
        myFixture.editor.moveCaret(caretOffset)
        myFixture.testAction(GenerateBuilderAction())

        // assert
        assertThat(shownMessage)
            .isEqualTo("Target file 'ClassWithExistingBuilderBuilder.kt' already exists and will be overwritten. Continue?")
    }

    @Test
    fun testOverwritesExistingBuilderWhenPromptConfirmed() {
        // arrange
        val dataClassUnderTest = "ClassWithExistingBuilder"
        val caretOffset = 30
        val promptReturnResult = Messages.OK

        val builderFileResource = "${dataClassUnderTest}Builder.kt"
        myFixture.copyFileToProject(builderFileResource)

        TestDialogManager.setTestDialog { promptReturnResult }

        // act, assert
        testBuilderGeneratedCorrectlyForDataClass(dataClassUnderTest, caretOffset)
    }

    @Test
    fun testDoesNotOverwriteExistingBuilderWhenPromptCancelled() {
        // arrange
        val dataClassUnderTest = "ClassWithExistingBuilder"
        val inputFile = "$dataClassUnderTest.kt"
        val caretOffset = 30
        val promptReturnResult = Messages.CANCEL

        val builderFileResource = "${dataClassUnderTest}Builder.kt"
        myFixture.copyFileToProject(builderFileResource)

        myFixture.configureByFile(inputFile)

        TestDialogManager.setTestDialog { promptReturnResult }

        // act
        myFixture.editor.moveCaret(caretOffset)
        myFixture.testAction(GenerateBuilderAction())

        // assert
        val builderFile =
            myFixture.file.containingDirectory.files.singleOrNull { it.name == "${dataClassUnderTest}Builder.kt" }
        assertThat(builderFile).isNotNull

        val builderFileCode = VfsUtil.loadText(builderFile!!.virtualFile)
        val expectedGeneratedBuilderCode = File("$testDataPath/expected/${dataClassUnderTest}Builder.kt").readText()
        assertThat(builderFileCode).isNotEqualToIgnoringWhitespace(expectedGeneratedBuilderCode)
    }

}