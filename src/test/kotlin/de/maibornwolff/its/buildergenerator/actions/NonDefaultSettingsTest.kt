package de.maibornwolff.its.buildergenerator.actions

import de.maibornwolff.its.buildergenerator.generator.GeneratorConfig
import de.maibornwolff.its.buildergenerator.settings.AppSettingsState
import org.junit.Test

class NonDefaultSettingsTest: GenerateBuilderActionLightTestBase() {

    @Test
    fun testUsesNonDefaultSettingsForBuilderGeneration() {
        // arrange
        val dataClassUnderTest = "NonDefaultSettingsTestDataClass"
        val caretOffset = 30

        val nonDefaultConfig = GeneratorConfig(builderClassSuffix = "Bauer",
                                               withFunctionPrefix = "mit",
                                               withoutFunctionPrefix = "ohne",
                                               buildFunctionName = "baue")

        AppSettingsState.getInstance().config = nonDefaultConfig

        try {
            // act, assert
            testBuilderGeneratedCorrectlyForDataClass(dataClassUnderTest = dataClassUnderTest,
                                                      caretOffsetOnDataClassName = caretOffset,
                                                      builderSuffix = nonDefaultConfig.builderClassSuffix)
        } finally {
            // teardown
            AppSettingsState.getInstance().config = GeneratorConfig()
        }
    }
}