package io.moku.rubyfunctionrunner.functions

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RSingletonMethod
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RObjectClassImpl
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RFunctionArgumentListImpl
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RNameImpl

@Suppress("unused")
class SingletonFunction(private val defElement: PsiElement) : RunnableFunction() {
    class NotASingletonFunctionException(defElement: PsiElement) :
        ParseException("$defElement is not a singleton RubyTokenTypes.kDEF")

    private val SINGLETON_FUNCTION_CHECK = { defElement: PsiElement ->
        if (defElement.elementType != RubyTokenTypes.kDEF) {
            false
        } else {
            when (val context = defElement.context) {
                is RSingletonMethod -> {
                    when (val container = context.parentContainer) {
                        is RClass -> !container.qualifiedName.isNullOrEmpty()
                        is RModule -> !container.qualifiedName.isNullOrEmpty()
                        else -> false
                    }
                }
                is RMethod -> {
                    val container = context.parentContainer
                    container is RObjectClassImpl && container.isSelfObject
                }
                else -> {
                    false
                }
            }
        }
    }

    init {
        if (!SINGLETON_FUNCTION_CHECK.invoke(defElement)) {
            throw NotASingletonFunctionException(defElement)
        }
    }

    override val name: String
        get() = defElement.siblings().toList().filterIsInstance<RNameImpl>().first().name

    override val arguments: List<ArgumentInfo>?
        get() = defElement.siblings().toList().filterIsInstance<RFunctionArgumentListImpl>().firstOrNull()?.getArgumentInfos(true)

    private val file: PsiFile = defElement.containingFile

    override val psiFile
        get() = file

    private val context = defElement.context as RMethod

    private val containerName: String
        get() {
            return context.parentContainer.let { container ->
                if (container is RClass) {
                    container.qualifiedName!!
                } else {
                    (container as RModule).qualifiedName!!
                }
            }.replaceFirst("::$\$SINGLETON$$", "")
        }

    override fun getCommand(arguments: List<ArgumentModel>?): String {
        val commandBuilder = StringBuilder()

        commandBuilder.appendLine("require \"${file.virtualFile.path}\" unless defined? $containerName")

        if (!printCommand.isNullOrBlank()) {
            commandBuilder.append("${printCommand}(")
        }
        commandBuilder.append("${containerName}.${name.replace("self.", "")}(")
        var addComma = false
        arguments?.forEach { argument ->
            if (argument.currentValue.isNotBlank()) {
                if (addComma) {
                    commandBuilder.append(", ")
                }
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
