package io.moku.railsnotebooks

import com.google.common.escape.Escaper
import com.google.common.escape.Escapers
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.terminal.ui.TerminalWidget
import org.jetbrains.plugins.ruby.rails.model.RailsApp
import org.jetbrains.plugins.terminal.TerminalToolWindowManager

class RunRootFunctionAction(val function: RootFunction): AnAction() {
    val SHELL_ESCAPE: Escaper

    init {
        val builder = Escapers.builder()
        builder.addEscape('\"', "\\\"")
        SHELL_ESCAPE = builder.build()
    }

    private fun getTerminal(project: Project): TerminalWidget {
        val manager = TerminalToolWindowManager.getInstance(project)
        return manager.terminalWidgets.firstOrNull {
            it.terminalTitle.defaultTitle == "Rails Notebooks"
        } ?: manager.createShellWidget(project.basePath, "Rails Notebooks", true, true)
    }

    private fun getCommand(): String {
        val commandBuilder = StringBuilder()
        if (function.hasRails) {
            commandBuilder.append("rails r \"")
        } else {
            commandBuilder.append("ruby -e \"")
        }
        commandBuilder.appendLine(SHELL_ESCAPE.escape(function.fileContent))
        commandBuilder.append(function.name)
        commandBuilder.append('"')
        return commandBuilder.toString()
    }

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val project = getEventProject(e)!!
            val terminal = getTerminal(project)
            terminal.requestFocus()
            terminal.sendCommandToExecute(getCommand())
        } catch (e: Exception) {
            print(e)
        }
    }
}
