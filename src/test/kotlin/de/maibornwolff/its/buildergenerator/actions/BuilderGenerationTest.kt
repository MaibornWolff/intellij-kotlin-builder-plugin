package de.maibornwolff.its.buildergenerator.actions

import org.junit.Test

class BuilderGenerationTest: GenerateBuilderActionTestBase() {

    @Test
    fun testGenerateBuilderForSimpleDataClass() =
        testBuilderGeneratedCorrectlyForDataClass("SimpleDataClass", 30)

    @Test
    fun testGenerateBuilderForComplexDataClass() =
        testBuilderGeneratedCorrectlyForDataClass("DataClassWithComplexTypes", 30)
}