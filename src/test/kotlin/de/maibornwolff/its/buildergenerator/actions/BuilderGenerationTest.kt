package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.ui.TestDialog
import com.intellij.openapi.ui.TestDialogManager
import org.junit.Test

class BuilderGenerationTest: GenerateBuilderActionTestBase() {

    @Test
    fun testGenerateBuilderForSimpleDataClass() {
        TestDialogManager.setTestDialog(TestDialog.OK)

        testBuilderGeneratedCorrectlyForDataClass("SimpleDataClass", 30)
    }

    @Test
    fun testGenerateBuilderForComplexDataClass() {
        TestDialogManager.setTestDialog(TestDialog.OK)

        testBuilderGeneratedCorrectlyForDataClass("DataClassWithComplexTypes", 30)
    }
}