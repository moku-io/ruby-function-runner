package io.moku.rubyfunctionrunner.functions

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RFunctionArgumentListImpl
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RNameImpl

@Suppress("unused")
class RootFunction(private val defElement: PsiElement): RunnableFunction() {
    class NotARootFunctionException(defElement: PsiElement): ParseException("$defElement is not a top level RubyTokenTypes.kDEF")
    private val ROOT_FUNCTION_CHECK = { defElement: PsiElement -> defElement.elementType == RubyTokenTypes.kDEF && defElement.parent.parent.parent is RFile }

    init {
        if (!ROOT_FUNCTION_CHECK.invoke(defElement)) {
            throw NotARootFunctionException(defElement)
        }
    }

    override val name: String
        get() = defElement.siblings().toList().filterIsInstance<RNameImpl>().first().name

    override val arguments
        get() = defElement.siblings().toList().filterIsInstance<RFunctionArgumentListImpl>().firstOrNull()?.getArgumentInfos(true)

    val file: PsiFile = defElement.containingFile

    override val psiFile
        get() = file

    override fun getCommand(arguments: List<ArgumentModel>?): String {
        val commandBuilder = StringBuilder()
        commandBuilder.appendLine("require \"${file.virtualFile.path}\"")
        if (!printCommand.isNullOrBlank()) {
            commandBuilder.append("$printCommand(")
        }
        commandBuilder.append("${name}(")
        var addComma = false
        arguments?.forEach { argument ->
            if (argument.currentValue.isNotBlank()) {
                if (addComma) { commandBuilder.append(", ") }
                if (argument.argumentInfo.type.hasName()) {
                    commandBuilder.append("${argument.argumentInfo.name}: ")
                }
                commandBuilder.append(argument.currentValue)
                addComma = true
            }
        }
        commandBuilder.append(")")
        if (!printCommand.isNullOrBlank()) {
            commandBuilder.append(")")
        }
        return commandBuilder.toString()
    }
}
