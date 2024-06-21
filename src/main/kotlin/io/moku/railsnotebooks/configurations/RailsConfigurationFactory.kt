package io.moku.railsnotebooks.configurations

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import io.moku.railsnotebooks.RootFunction
import org.jetbrains.plugins.ruby.console.config.IrbConsoleType
import org.jetbrains.plugins.ruby.console.config.IrbRunConfiguration
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationFactory
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationType
import org.jetbrains.plugins.ruby.rails.model.RailsApp

class RailsConfigurationFactory(private val function: RootFunction) : RunRootFunctionFactory {
    private val railsApp = RailsApp.fromPsiElement(function.file)!!
    private val railsPath = railsApp.staticPaths.binRootURL.replace("file://", "").let { "$it/rails" }
    private val project = function.file.project
    private val module: Module

    init {
        val index = ProjectRootManager.getInstance(project).fileIndex
        module = index.getModuleForFile(function.file.virtualFile)!!
    }

    private fun getCommand(): String {
        val commandBuilder = StringBuilder()
        commandBuilder.appendLine("require \"${function.file.virtualFile.path}\"")
        commandBuilder.append(function.name)
        return commandBuilder.toString()
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
