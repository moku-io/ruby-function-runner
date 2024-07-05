package io.moku.rubyfunctionrunner

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
import com.intellij.openapi.project.Project
import io.moku.rubyfunctionrunner.configurations.RailsConfigurationFactory
import io.moku.rubyfunctionrunner.configurations.RubyConfigurationFactory
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import io.moku.rubyfunctionrunner.function_arguments.views.showArgumentsDialog
import io.moku.rubyfunctionrunner.functions.RunnableFunction
import org.jetbrains.plugins.ruby.rails.model.RailsApp

private fun RunnableFunction.configurationName(debug: Boolean) =
    if (debug) {
        "Debug $name"
    } else {
        "Run $name"
    }.replace("_", " ")

private fun configurationIcon(debug: Boolean) =
    if (debug) {
        Run.Debug
    } else {
        Run.Run
    }

class RunnableFunctionAction(private val function: RunnableFunction, private val debug: Boolean) : AnAction(
    function.configurationName(debug),
    null,
    configurationIcon(debug)
) {
    private lateinit var project: Project

    private fun getConfiguration(argModels: List<ArgumentModel>?): RunConfiguration =
        if (RailsApp.fromPsiElement(function.psiFile) != null) {
            RailsConfigurationFactory(function, argModels)
        } else {
            RubyConfigurationFactory(function, argModels)
        }.build(function.configurationName(debug))

    private fun runConfiguration(argModels: List<ArgumentModel>? = null) {
        try {
            ProgramRunnerUtil.executeConfiguration(
                RunnerAndConfigurationSettingsImpl(
                    RunManager.getInstance(project) as RunManagerImpl,
                    getConfiguration(argModels),
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

    override fun actionPerformed(e: AnActionEvent) {
        project = getEventProject(e)!!
        if (function.arguments.isNullOrEmpty()) {
            runConfiguration()
        } else {
            function.showArgumentsDialog()?.let { argModels ->
                runConfiguration(argModels)
            }
        }
    }
}

