package de.maibornwolff.its.buildergenerator.service

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.KotlinFileType

interface FileService: FileReader {
    fun withWriter(actions: FileReaderWriter.() -> Unit)
}

interface FileReader {
    fun getFileOrNull(directory: PsiDirectory, fileName: String): PsiFile?
    fun openInTab(file: PsiFile)
}

interface FileWriter {
    fun createFile(directory: PsiDirectory, name: String, content: String): PsiFile
    fun overwriteFile(file: PsiFile, content: String): PsiFile
    fun reformat(file: PsiFile)
}

interface FileReaderWriter: FileReader, FileWriter

class FileServiceImpl(private val project: Project): FileService, FileReaderWriter {

    override fun withWriter(actions: FileReaderWriter.() -> Unit) {
        WriteCommandAction.runWriteCommandAction(project) {
            actions.invoke(this)
        }
    }

    override fun createFile(directory: PsiDirectory, name: String, content: String): PsiFile {
        val inMemoryPsiFile = PsiFileFactory.getInstance(project)
                .createFileFromText(name, KotlinFileType.INSTANCE, content)
        return directory.add(inMemoryPsiFile) as PsiFile
    }

    override fun overwriteFile(file: PsiFile, content: String): PsiFile {
        file.delete()
        return createFile(file.containingDirectory, file.name, content)
    }

    override fun getFileOrNull(directory: PsiDirectory, fileName: String): PsiFile? {
        return directory.files.firstOrNull { it.name == fileName }
    }

    override fun reformat(file: PsiFile) {
        CodeStyleManager.getInstance(project).reformat(file)
    }

    override fun openInTab(file: PsiFile) {
        OpenFileDescriptor(project, file.virtualFile).navigate(true)
    }

}