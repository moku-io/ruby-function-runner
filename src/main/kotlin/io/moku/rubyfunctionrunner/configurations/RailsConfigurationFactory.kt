package io.moku.rubyfunctionrunner.configurations

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import io.moku.rubyfunctionrunner.RootFunction
import io.moku.rubyfunctionrunner.function_parameters.models.ParameterModel
import org.jetbrains.plugins.ruby.console.config.IrbConsoleType
import org.jetbrains.plugins.ruby.console.config.IrbRunConfiguration
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationFactory
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationType

class RailsConfigurationFactory(function: RootFunction, parameters: List<ParameterModel>? = null) : RunRootFunctionFactory(function, parameters) {
    private val project = function.file.project
    private val module: Module

    init {
        val index = ProjectRootManager.getInstance(project).fileIndex
        module = index.getModuleForFile(function.file.virtualFile)!!
    }

    override fun build(name: String): IrbRunConfiguration {
        val factory = IrbRunConfigurationFactory(IrbRunConfigurationType.getInstance())
        val settings = factory.createConfigurationSettings(
            module,
            name,
            projectSettings.railsExecutablePath,
            listOf("r", getCommand()),
            IrbConsoleType.RAILS
        )
        val configuration = settings.configuration as IrbRunConfiguration
        val sdkName = projectSettings.rubySdkName
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
