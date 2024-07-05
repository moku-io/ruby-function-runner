package io.moku.rubyfunctionrunner

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo
import kotlin.reflect.full.primaryConstructor

sealed class RunnableFunction {
    abstract val name: String
    abstract val psiFile: PsiFile
    abstract val arguments: List<ArgumentInfo>?
    abstract fun getCommand(arguments: List<ArgumentModel>?): String

    companion object {
        fun get(element: PsiElement): RunnableFunction? {
            return RunnableFunction::class.sealedSubclasses.firstNotNullOfOrNull {
                try {
                    it.primaryConstructor?.call(element)
                } catch (_: ParseException) { null }
            }
        }
    }

    sealed class ParseException(message: String): Exception(message)
}
