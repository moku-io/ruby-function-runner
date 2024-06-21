package io.moku.railsnotebooks

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunConfigurationLevel
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.icons.ExpUiIcons.Run
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import io.moku.railsnotebooks.configurations.RailsConfigurationFactory
import io.moku.railsnotebooks.configurations.RubyConfigurationFactory
import org.jetbrains.plugins.ruby.rails.model.RailsApp

private fun RootFunction.configurationName(debug: Boolean) =
    if (debug) {
        "Debug $name"
    } else {
        "Run $name"
    }

private fun configurationIcon(debug: Boolean) =
    if (debug) {
        Run.Debug
    } else {
        Run.Run
    }

class RunRootFunctionAction(private val function: RootFunction, private val debug: Boolean) : AnAction(
    function.configurationName(debug),
    null,
    configurationIcon(debug)
) {

    private fun getConfiguration(): RunConfiguration =
        if (RailsApp.fromPsiElement(function.file) != null) {
            RailsConfigurationFactory(function)
        } else {
            RubyConfigurationFactory(function)
        }.build(function.configurationName(debug))

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val project = getEventProject(e)!!
            ProgramRunnerUtil.executeConfiguration(
                RunnerAndConfigurationSettingsImpl(
                    RunManager.getInstance(project) as RunManagerImpl,
                    getConfiguration(),
                    false,
                    RunConfigurationLevel.TEMPORARY
                ),
                if (debug) {
                    DefaultDebugExecutor()
                } else {
                    DefaultRunExecutor()
                }
            )
        } catch (e: Exception) {
            print(e)
        }
    }
}
