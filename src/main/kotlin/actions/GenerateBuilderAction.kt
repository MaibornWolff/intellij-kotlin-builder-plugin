package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class GenerateBuilderAction: AnAction() {

    override fun actionPerformed(event: AnActionEvent) {

        val element = event.dataContext.getData("psi.Element") as? PsiClass
        if (element == null) {
            Messages.showMessageDialog(event.project,
                                       "Bitte nur in Klassen verwenden",
                                       "Fehler",
                                       Messages.getInformationIcon())
        } else {
            Messages.showMessageDialog(event.project,
                                       element.name,
                                       "Dialog Title",
                                       Messages.getInformationIcon())
        }

    }

}