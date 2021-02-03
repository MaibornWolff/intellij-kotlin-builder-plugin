@file:Suppress("DialogTitleCapitalization")

package de.maibornwolff.its.buildergenerator.settings

import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import de.maibornwolff.its.buildergenerator.generator.GeneratorConfig
import javax.swing.JLabel
import javax.swing.JPanel

class AppSettingsComponent {

    private val txtBuilderClassSuffix = JBTextField()
    private val txtWithFunctionPrefix = JBTextField()
    private val txtWithoutFunctionPrefix = JBTextField()
    private val txtBuildFunctionName = JBTextField()

    private val mainPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent("ClassBuilder class name suffix: ", txtBuilderClassSuffix, 1)
        .addTooltip("Suffix is added to the end of target class name.")
        .addSeparator()
        .addLabeledComponent("build() function name: ", txtBuildFunctionName)
        .addSeparator()
        .addLabeledComponent("withProperty(value: Type) function prefix: ", txtWithFunctionPrefix)
        .addLabeledComponent("withoutProperty() function prefix: ", txtWithoutFunctionPrefix)
        .addTooltip("withoutProperty() functions are generated for Nullable properties, to set their value to null.")
        .addComponentFillVertically(JLabel(""), 0)
        .panel

    val panel: JPanel get() = mainPanel

    fun setValues(config: GeneratorConfig) {
        txtBuilderClassSuffix.text = config.builderClassSuffix
        txtWithFunctionPrefix.text = config.withFunctionPrefix
        txtWithoutFunctionPrefix.text = config.withoutFunctionPrefix
        txtBuildFunctionName.text = config.buildFunctionName
    }

    fun getValues() = GeneratorConfig(
        builderClassSuffix = txtBuilderClassSuffix.text,
        withFunctionPrefix = txtWithFunctionPrefix.text,
        withoutFunctionPrefix = txtWithoutFunctionPrefix.text,
        buildFunctionName = txtBuildFunctionName.text,
                                     )

}