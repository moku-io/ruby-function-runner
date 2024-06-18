package io.moku.railsnotebooks

import com.google.common.escape.Escaper
import com.google.common.escape.Escapers
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.ExpUiIcons.Run
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RNameImpl
import org.jetbrains.plugins.terminal.TerminalToolWindowManager

class MyRunLineMarkerContributor: RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (element.elementType == RubyTokenTypes.kDEF && element.parent.parent.parent is RFile) {
            return Info(
                Run.Run,
                { element -> "run action"},
                Action(
                    element.siblings().toList().filterIsInstance<RNameImpl>().first().name,
                    element.parent.parent.parent.text
                )
            )
        }
        return null
    }
}

class Action(val functionName: String, val fileText: String): AnAction() {
    val SHELL_ESCAPE: Escaper

    init {
        val builder = Escapers.builder()
        builder.addEscape('\"', "\\\"")
        SHELL_ESCAPE = builder.build()
    }
    override fun actionPerformed(e: AnActionEvent) {
        try {
            val ttwm = TerminalToolWindowManager.getInstance(getEventProject(e)!!)
            val tw = ttwm.createNewSession()
            tw.sendCommandToExecute("rails r \"${SHELL_ESCAPE.escape(fileText)}\n${functionName}\"")
        } catch (e: Exception) {
            print(e)
        }
    }
}
