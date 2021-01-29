package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import generator.BuilderGenerator
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtUserType


class GenerateBuilderAction: AnAction() {

    // TODO implement a check which makes the action invisible when caret is not on a valid class

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
            handleDataClassUnderCursor(classUnderCursor, event.project)
        }
    }

    private val allowedTypes = arrayOf("String", "Int", "Boolean")

    private fun handleDataClassUnderCursor(dataClass: KtClass, project: Project?) {

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

            val poetBuilderFileSpec = BuilderGenerator.generateBuilderForDataClass(dataClass)

            // TODO overwrite + warning?
            WriteCommandAction.runWriteCommandAction(project) {
                val factory = PsiFileFactory.getInstance(project);
                val psiFile = factory.createFileFromText(poetBuilderFileSpec.name + ".kt", KotlinFileType.INSTANCE ,poetBuilderFileSpec.toString())
                val writtenPsiFile = dataClass.containingFile.containingDirectory.add(psiFile) as PsiFile
                OpenFileDescriptor(project!!, writtenPsiFile.virtualFile).navigate(true)
            }

        }
    }
}
