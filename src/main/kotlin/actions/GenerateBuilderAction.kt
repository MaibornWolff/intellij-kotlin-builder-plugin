package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import org.jetbrains.kotlin.psi.KtClass

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

    private fun handleDataClassUnderCursor(dataClass: KtClass) {

        dataClass.primaryConstructor

        Messages.showMessageDialog(
            null,
            dataClass.name,
            "Generator Called Successfully",
            Messages.getInformationIcon()
                                  )
    }
}