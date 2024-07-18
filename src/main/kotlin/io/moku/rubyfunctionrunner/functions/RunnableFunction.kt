package io.moku.rubyfunctionrunner.functions

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import io.moku.rubyfunctionrunner.settings.AppSettings
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo
import java.lang.reflect.InvocationTargetException
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
                } catch (exception: InvocationTargetException) {
                    if (exception.targetException is ParseException) {
                        null
                    } else {
                        throw exception
                    }
                }
            }
        }
    }

    protected val printCommand
        get() = AppSettings.instance?.state?.printCommand

    sealed class ParseException(message: String): Exception(message)
}
