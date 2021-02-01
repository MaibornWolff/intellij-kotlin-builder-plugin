package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
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

    private fun handleDataClassUnderCursor(dataClass: KtClass, project: Project?) {

        val poetBuilderFileSpec = BuilderGenerator.generateBuilderForDataClass(dataClass)

        val targetPsiDirectory = dataClass.containingFile.containingDirectory
        val fileName = "${poetBuilderFileSpec.name}.${KotlinFileType.EXTENSION}"
        val fileContents = poetBuilderFileSpec.toString()

        val existingBuilderFile = targetPsiDirectory.files.firstOrNull { it.name == fileName }
        if (existingBuilderFile == null || getOverwriteConfirmation(project!!, fileName)) {
            WriteCommandAction.runWriteCommandAction(project) {
                val psiFileToOpen = if (existingBuilderFile == null)
                    createNewFile(project, targetPsiDirectory, fileName, fileContents)
                else
                    overwriteFile(project, existingBuilderFile, fileContents)

                OpenFileDescriptor(project!!, psiFileToOpen.virtualFile).navigate(true)
            }
        }
    }

    private fun getOverwriteConfirmation(project: Project, fileName: String): Boolean {
        val result = Messages.showOkCancelDialog(
            project,
            "Target file '$fileName' already exists and will be overwritten. Continue?",
            "Overwrite Existing File?", "Overwrite", "Cancel",
            Messages.getWarningIcon()
                                                )

        return result == Messages.OK
    }

    private fun createNewFile(
        project: Project?,
        targetPsiDirectory: PsiDirectory,
        fileName: String,
        fileContents: String
                             ): PsiFile {
        val inMemoryPsiFile = PsiFileFactory.getInstance(project)
            .createFileFromText(fileName, KotlinFileType.INSTANCE, fileContents)
        return targetPsiDirectory.add(inMemoryPsiFile) as PsiFile
    }

    private fun overwriteFile(project: Project?, file: PsiFile, fileContents: String): PsiFile {
        val containingDirectory = file.containingDirectory
        val fileName = file.name
        file.delete()
        return createNewFile(project, containingDirectory, fileName, fileContents)
    }
}
