package io.moku.rubyfunctionrunner.configurations

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import io.moku.rubyfunctionrunner.functions.RunnableFunction
import org.jetbrains.plugins.ruby.console.config.IrbConsoleType
import org.jetbrains.plugins.ruby.console.config.IrbRunConfiguration
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationFactory
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationType

class RailsConfigurationFactory(function: RunnableFunction, arguments: List<ArgumentModel>? = null) : RunnableFunctionActionFactory(function, arguments) {
    private val project = function.psiFile.project
    private val module: Module

    init {
        val index = ProjectRootManager.getInstance(project).fileIndex
        module = index.getModuleForFile(function.psiFile.virtualFile)!!
    }

    override fun build(name: String): IrbRunConfiguration {
        val factory = IrbRunConfigurationFactory(IrbRunConfigurationType.getInstance())
        val settings = factory.createConfigurationSettings(
            module,
            name,
            projectSettings?.railsExecutablePath,
            listOf("r", getCommand()),
            IrbConsoleType.RAILS
        )
        val configuration = settings.configuration as IrbRunConfiguration
        val sdkName = projectSettings?.rubySdkName
        if (sdkName != null) {
            configuration.alternativeSdkName = sdkName
            configuration.setShouldUseAlternativeSdk(true)
        } else{
            configuration.alternativeSdkName = null
            configuration.setShouldUseAlternativeSdk(false)
        }
        configuration.setWorkingDirectory(project.basePath)
        return configuration
    }
}
