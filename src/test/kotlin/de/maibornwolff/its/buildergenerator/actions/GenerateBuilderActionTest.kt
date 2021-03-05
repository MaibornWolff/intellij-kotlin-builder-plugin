package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TestDialog
import com.intellij.openapi.ui.TestDialogManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.idea.core.moveCaret
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class GenerateBuilderActionTest: BasePlatformTestCase() {

    @Before
    public override fun setUp() {
        super.setUp()
        assertThat(testDataPath).isNotNull
    }

    @After
    fun resetTestDialog() {
        TestDialogManager.setTestDialog(TestDialog.DEFAULT)
    }

    override fun getTestDataPath(): String =
        File(this::class.java.getResource("/testdata/SimpleDataClass.kt").toURI()).parent

    @Test
    fun testGenerateBuilderForSimpleDataClass() =
        testBuilderGeneratedCorrectlyForDataClass("SimpleDataClass", 30)

    @Test
    fun testGenerateBuilderForComplexDataClass() =
        testBuilderGeneratedCorrectlyForDataClass("DataClassWithComplexTypes", 30)

    private fun testBuilderGeneratedCorrectlyForDataClass(dataClassUnderTest: String, caretOffsetOnDataClassName: Int) {
        // arrange
        val inputFile = "$dataClassUnderTest.kt"
        myFixture.configureByFile(inputFile)

        // act
        myFixture.editor.moveCaret(caretOffsetOnDataClassName)
        myFixture.testAction(GenerateBuilderAction())

        // assert
        val generatedBuilderFile =
            myFixture.file.containingDirectory.files.singleOrNull { it.name == "${dataClassUnderTest}Builder.kt" }
        assertThat(generatedBuilderFile).isNotNull

        val generatedBuilderCode = VfsUtil.loadText(generatedBuilderFile!!.virtualFile)
        val expectedBuilderCode = File("$testDataPath/expected/${dataClassUnderTest}Builder.kt").readText()
        assertThat(generatedBuilderCode).isEqualToIgnoringWhitespace(expectedBuilderCode)
    }

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