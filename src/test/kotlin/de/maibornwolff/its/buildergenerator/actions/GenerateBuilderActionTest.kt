package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.idea.core.moveCaret
import org.junit.Before
import org.junit.Test
import java.io.File

class GenerateBuilderActionTest: BasePlatformTestCase() {

    @Before
    public override fun setUp() {
        super.setUp()
        assertThat(testDataPath).isNotNull
    }

    override fun getTestDataPath(): String = File(this::class.java.getResource("/testdata/SimpleDataClass.kt").toURI()).parent

    @Test
    fun testGenerateBuilderForSimpleDataClass() = runTestForSpecifiedDataClass(
        dataClassUnderTest = "SimpleDataClass",
        caretOffsetOnDataClassName = 30
                                                                              )

    @Test
    fun testGenerateBuilderForComplexDataClass() = runTestForSpecifiedDataClass(
        dataClassUnderTest = "DataClassWithComplexTypes",
        caretOffsetOnDataClassName = 30
                                                                              )

    private fun runTestForSpecifiedDataClass(dataClassUnderTest: String, caretOffsetOnDataClassName: Int) {
        // arrange
        val inputFile = "$dataClassUnderTest.kt"
        myFixture.configureByFile(inputFile)

        // act
        myFixture.editor.moveCaret(caretOffsetOnDataClassName)
        myFixture.testAction(GenerateBuilderAction())

        // assert
        val generatedBuilderFile = myFixture.file.containingDirectory.files.singleOrNull { it.name == "${dataClassUnderTest}Builder.kt" }
        assertThat(generatedBuilderFile).isNotNull

        val generatedBuilderCode = VfsUtil.loadText(generatedBuilderFile!!.virtualFile)
        val expectedBuilderCode = File("$testDataPath/expected/${dataClassUnderTest}Builder.kt").readText()
        assertThat(generatedBuilderCode).isEqualToIgnoringWhitespace(expectedBuilderCode)
    }

}