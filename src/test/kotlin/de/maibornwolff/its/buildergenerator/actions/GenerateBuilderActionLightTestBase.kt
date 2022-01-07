package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.ui.TestDialog
import com.intellij.openapi.ui.TestDialogManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.idea.core.moveCaret
import org.junit.After
import org.junit.Before
import java.io.File

abstract class GenerateBuilderActionLightTestBase: BasePlatformTestCase() {

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

    protected fun testBuilderGeneratedCorrectlyForDataClass(dataClassUnderTest: String,
                                                            caretOffsetOnDataClassName: Int,
                                                            builderSuffix: String = "Builder") {
        // arrange
        val inputFile = "$dataClassUnderTest.kt"
        myFixture.configureByFile(inputFile)

        // act
        myFixture.editor.moveCaret(caretOffsetOnDataClassName)
        myFixture.testAction(GenerateBuilderAction())

        // assert
        val generatedBuilderFile =
            myFixture.file.containingDirectory.files.singleOrNull { it.name == "$dataClassUnderTest$builderSuffix.kt" }
        assertThat(generatedBuilderFile).isNotNull

        val generatedBuilderCode = VfsUtil.loadText(generatedBuilderFile!!.virtualFile)
        val expectedBuilderCode = File("$testDataPath/expected/$dataClassUnderTest$builderSuffix.kt").readText()
        assertThat(generatedBuilderCode).isEqualToIgnoringWhitespace(expectedBuilderCode)
    }

    protected fun testBuilderGeneratedCorrectlyForDataClassWithOtherBuilder(dataClassUnderTest: String,
                                                                            otherBuilder: String,
                                                                            caretOffsetOnDataClassName: Int,
                                                                            builderSuffix: String = "Builder") {
        // arrange
        val inputFile = "$dataClassUnderTest.kt"
        myFixture.configureByFile(inputFile)
        myFixture.copyFileToProject("$testDataPath/$otherBuilder")

        // act
        myFixture.editor.moveCaret(caretOffsetOnDataClassName)
        myFixture.testAction(GenerateBuilderAction())

        // assert
        val generatedBuilderFile =
                myFixture.file.containingDirectory.files.singleOrNull { it.name == "$dataClassUnderTest$builderSuffix.kt" }
        assertThat(generatedBuilderFile).isNotNull

        val generatedBuilderCode = VfsUtil.loadText(generatedBuilderFile!!.virtualFile)
        val expectedBuilderCode = File("$testDataPath/expected/$dataClassUnderTest$builderSuffix.kt").readText()
        assertThat(generatedBuilderCode).isEqualToIgnoringWhitespace(expectedBuilderCode)
    }

}