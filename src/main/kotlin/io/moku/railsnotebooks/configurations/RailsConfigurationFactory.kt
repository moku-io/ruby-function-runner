package io.moku.railsnotebooks.configurations

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import io.moku.railsnotebooks.RootFunction
import io.moku.railsnotebooks.function_parameters.models.ParameterModel
import org.jetbrains.plugins.ruby.console.config.IrbConsoleType
import org.jetbrains.plugins.ruby.console.config.IrbRunConfiguration
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationFactory
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationType
import org.jetbrains.plugins.ruby.rails.model.RailsApp

class RailsConfigurationFactory(function: RootFunction, parameters: List<ParameterModel>? = null) : RunRootFunctionFactory(function, parameters) {
    private val railsApp = RailsApp.fromPsiElement(function.file)!!
    private val railsPath = railsApp.staticPaths.binRootURL.replace("file://", "").let { "$it/rails" }
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
            railsPath,
            listOf("r", getCommand()),
            IrbConsoleType.RAILS
        )
        val configuration = settings.configuration as IrbRunConfiguration
        configuration.setWorkingDirectory(project.basePath)
        return configuration
    }
}
