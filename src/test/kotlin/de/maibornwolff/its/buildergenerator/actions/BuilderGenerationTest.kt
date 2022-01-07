package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.ui.TestDialog
import com.intellij.openapi.ui.TestDialogManager
import org.junit.Test

class BuilderGenerationTest: GenerateBuilderActionLightTestBase() {

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

    @Test
    fun testGenerateBuilderForSimpleDataWithWrappedPrimitivesClass() {
        TestDialogManager.setTestDialog(TestDialog.OK)

        testBuilderGeneratedCorrectlyForDataClass("SimpleDataClassWithWrappedPrimitives", 30)
    }

    @Test
    fun testGenerateBuilderForComplexDataClassWithBuilderDetectionForDefaults() {
        TestDialogManager.setTestDialog(TestDialog.OK)

        testBuilderGeneratedCorrectlyForDataClassWithOtherBuilder(dataClassUnderTest = "DataClassWithComplexTypeWithBuilder",
                                                                  otherBuilder = "PropertyTypeWithBuilderBuilder.kt",
                                                                  caretOffsetOnDataClassName = 30)
    }
}