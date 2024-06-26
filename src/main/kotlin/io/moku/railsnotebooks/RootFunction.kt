package io.moku.railsnotebooks

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RFunctionArgumentListImpl
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RNameImpl

class RootFunction(private val defElement: PsiElement) {
    class NotARootFunctionException(defElement: PsiElement): Exception("$defElement is not a top level RubyTokenTypes.kDEF")
    private val ROOT_FUNCTION_CHECK = { defElement: PsiElement -> defElement.elementType == RubyTokenTypes.kDEF && defElement.parent.parent.parent is RFile }

    init {
        if (!ROOT_FUNCTION_CHECK.invoke(defElement)) {
            throw NotARootFunctionException(defElement)
        }
    }

    val name: String
        get() = defElement.siblings().toList().filterIsInstance<RNameImpl>().first().name

    val arguments: List<ArgumentInfo>?
        get() = defElement.siblings().toList().filterIsInstance<RFunctionArgumentListImpl>().firstOrNull()?.getArgumentInfos(true)

    val file: PsiFile = defElement.containingFile
}
