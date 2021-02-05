package de.maibornwolff.its.buildergenerator.util

import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.psi.KtClass
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun AnActionEvent.getClassUnderCaret() = this.dataContext.getData("psi.Element") as? KtClass

@ExperimentalContracts
fun KtClass?.isNonNullDataClass(): Boolean {
    contract {
        returns(true) implies (this@isNonNullDataClass != null)
    }
    return this != null && this.isData()
}

fun KtClass.getContainingDirectory() = this.containingFile.containingDirectory