package de.maibornwolff.its.buildergenerator.util

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.psi.KtClass
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun AnActionEvent.getClassUnderCaret() = this.dataContext.getData("psi.Element") as? KtClass

@OptIn(ExperimentalContracts::class)
fun KtClass?.isNonNullDataClass(): Boolean {
    contract {
        returns(true) implies (this@isNonNullDataClass != null)
    }
    return this != null && this.isData()
}

fun KtClass.getContainingDirectory(): PsiDirectory = this.containingFile.containingDirectory