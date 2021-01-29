package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtUserType

class GenerateBuilderAction: AnAction() {

    override fun update(event: AnActionEvent) {
        val classUnderCursor = event.dataContext.getData("psi.Element") as? KtClass
        event.presentation.isEnabledAndVisible = classUnderCursor != null && classUnderCursor.isData()
    }

    override fun actionPerformed(event: AnActionEvent) {

        val classUnderCursor = event.dataContext.getData("psi.Element") as? KtClass
        if (classUnderCursor == null || !classUnderCursor.isData()) {
            Messages.showMessageDialog(
                event.project,
                "Builder generation only works for Kotlin data classes",
                "Builder Generator Error",
                Messages.getErrorIcon()
                                      )
        } else {
            handleDataClassUnderCursor(classUnderCursor)
        }
    }

    private val allowedTypes = arrayOf("String", "Int", "Boolean")

    private fun handleDataClassUnderCursor(dataClass: KtClass) {

        val allParameters = dataClass.primaryConstructorParameters
            .map { it.name to (it.typeReference?.typeElement as? KtUserType)?.referencedName }
        val disallowedParameters = allParameters
            .filter { (_, type) -> type !in allowedTypes }

        if (disallowedParameters.isNotEmpty()) {
            Messages.showMessageDialog(
                null,
                "Builder generator currently cannot handle the following parameters: \n" +
                        disallowedParameters.joinToString(separator = "\n") { (name, type) -> " - $name (type $type is not supported)" },
                "Builder Generator Error",
                Messages.getErrorIcon()
                                      )
        } else {
            Messages.showMessageDialog(
                null,
                "Generating builder for ${dataClass.name} with fields: \n" +
                        allParameters.joinToString(separator = "\n") { (name, type) -> " - $name: $type" },
                "Generator Called Successfully",
                Messages.getInformationIcon()
                                      )
        }
    }
}