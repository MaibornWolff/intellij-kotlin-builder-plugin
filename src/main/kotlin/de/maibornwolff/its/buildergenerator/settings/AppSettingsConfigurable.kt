package de.maibornwolff.its.buildergenerator.settings

import com.intellij.openapi.options.Configurable

class AppSettingsConfigurable: Configurable {

    private val component = AppSettingsComponent()

    override fun createComponent() = component.panel

    override fun isModified() =
        AppSettingsState.getInstance().config != component.getValues()

    override fun apply() {
        AppSettingsState.getInstance().config = component.getValues()
    }

    override fun reset() {
        component.setValues(AppSettingsState.getInstance().config)
    }

    override fun getDisplayName() =
        "Kotlin Builder Generator"
}