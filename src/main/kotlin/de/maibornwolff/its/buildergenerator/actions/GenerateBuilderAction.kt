package de.maibornwolff.its.buildergenerator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import de.maibornwolff.its.buildergenerator.generator.BuilderGenerator
import de.maibornwolff.its.buildergenerator.service.FileService
import de.maibornwolff.its.buildergenerator.settings.AppSettingsState
import de.maibornwolff.its.buildergenerator.util.getContainingDirectory
import de.maibornwolff.its.buildergenerator.util.getClassUnderCaret
import de.maibornwolff.its.buildergenerator.util.isNonNullDataClass
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.slf4j.LoggerFactory
import kotlin.contracts.ExperimentalContracts

class GenerateBuilderAction: AnAction() {

    private val LOGGER = LoggerFactory.getLogger(GenerateBuilderAction::class.java)

    // TODO implement a check which makes the action invisible when caret is not on a valid class

    @ExperimentalContracts
    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let {
            val classUnderCaret = event.getClassUnderCaret()
            if (classUnderCaret.isNonNullDataClass()) {
                generateBuilder(classUnderCaret, it)
            } else {
                showOnlyDataClassesAllowedMessage(it)
            }
        } ?: LOGGER.warn("no project")
    }

    private fun generateBuilder(dataClass: KtClass, project: Project) {
        val currentConfig = AppSettingsState.getInstance().config
        val builderSpec = BuilderGenerator(currentConfig).generateBuilderForDataClass(dataClass)
        val builderDirectory = dataClass.getContainingDirectory()
        val builderFileName = "${builderSpec.name}.${KotlinFileType.EXTENSION}"
        val builderFileContents = builderSpec.toString()
        val fileService = project.service<FileService>()
        overwriteWithPromptAndOpen(project, fileService, builderDirectory, builderFileName, builderFileContents)
    }

    private fun overwriteWithPromptAndOpen(project: Project, fileService: FileService, directory: PsiDirectory, fileName: String, contents: String) {
        val existingBuilderFile = fileService.getFileOrNull(directory, fileName)
        if (existingBuilderFile == null || getOverwriteConfirmation(project, fileName)) {
            fileService.withWriter {
                val psiFileToOpen = if (existingBuilderFile == null)
                    this.createFile(directory, fileName, contents)
                else
                    this.overwriteFile(existingBuilderFile, contents)
                this.reformat(psiFileToOpen)
                this.openInTab(psiFileToOpen)
            }
        }
    }

    private fun showOnlyDataClassesAllowedMessage(project: Project) {
        Messages.showMessageDialog(
                project,
                "Builder generation only works for Kotlin data classes",
                "Builder Generator Error",
                Messages.getErrorIcon()
        )
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

}
