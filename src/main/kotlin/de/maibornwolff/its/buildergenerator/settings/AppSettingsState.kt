package de.maibornwolff.its.buildergenerator.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import de.maibornwolff.its.buildergenerator.generator.GeneratorConfig

@State(
    name = "de.maibornwolff.its.buildergenerator.settings.AppSettingsState",
    storages = [Storage("kotlinBuilderGeneratorSettings.xml")]
      )
class AppSettingsState: PersistentStateComponent<AppSettingsState> {

    var config: GeneratorConfig = GeneratorConfig()

    companion object {
        fun getInstance(): AppSettingsState =
            ServiceManager.getService(AppSettingsState::class.java)
    }

    override fun getState() = this

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}