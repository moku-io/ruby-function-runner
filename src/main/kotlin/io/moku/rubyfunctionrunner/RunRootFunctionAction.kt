package io.moku.rubyfunctionrunner

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunConfigurationLevel
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import io.moku.rubyfunctionrunner.configurations.RailsConfigurationFactory
import io.moku.rubyfunctionrunner.configurations.RubyConfigurationFactory
import io.moku.rubyfunctionrunner.function_parameters.models.ParameterModel
import io.moku.rubyfunctionrunner.function_parameters.views.showArgumentsDialog
import org.jetbrains.plugins.ruby.rails.model.RailsApp

private fun RootFunction.configurationName(debug: Boolean) =
    if (debug) {
        "Debug $name"
    } else {
        "Run $name"
    }.replace("_", " ")

private fun configurationIcon(debug: Boolean) =
    if (debug) {
        AllIcons.Actions.StartDebugger
    } else {
        AllIcons.RunConfigurations.TestState.Run
    }

class RunRootFunctionAction(private val function: RootFunction, private val debug: Boolean) : AnAction(
    function.configurationName(debug),
    null,
    configurationIcon(debug)
) {
    private lateinit var project: Project

    private fun getConfiguration(argModels: List<ParameterModel>?): RunConfiguration =
        if (RailsApp.fromPsiElement(function.file) != null) {
            RailsConfigurationFactory(function, argModels)
        } else {
            RubyConfigurationFactory(function, argModels)
        }.build(function.configurationName(debug))

    private fun runConfiguration(argModels: List<ParameterModel>? = null) {
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

