package io.moku.rubyfunctionrunner

import com.intellij.psi.PsiFile
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo

interface RunnableFunction {
    val name: String
    val psiFile: PsiFile
    val arguments: List<ArgumentInfo>?
    fun getCommand(arguments: List<ArgumentModel>?): String
}
