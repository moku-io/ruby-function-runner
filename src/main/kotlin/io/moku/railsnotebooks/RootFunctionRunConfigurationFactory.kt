package io.moku.railsnotebooks

import com.google.common.escape.Escaper
import com.google.common.escape.Escapers
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import org.jetbrains.plugins.ruby.console.config.IrbConsoleType
import org.jetbrains.plugins.ruby.console.config.IrbRunConfiguration
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationFactory
import org.jetbrains.plugins.ruby.console.config.IrbRunConfigurationType
import org.jetbrains.plugins.ruby.rails.model.RailsApp


class RootFunctionRunConfigurationFactory(private val function: RootFunction) {

    private val railsApp = RailsApp.fromPsiElement(function.file)
    private val railsPath = railsApp?.staticPaths?.binRootURL?.replace("file://", "")?.let { "$it/rails" }
    private val project = function.file.project
    private val module: Module?
    init {
        val index = ProjectRootManager.getInstance(project).fileIndex
        module = index.getModuleForFile(function.file.virtualFile)
    }

    private fun getCommand(): String {
        val commandBuilder = StringBuilder()
        commandBuilder.appendLine("require \"${function.file.virtualFile.path}\"")
        commandBuilder.append(function.name)
        return commandBuilder.toString()
    }

    fun build(name: String): IrbRunConfiguration? {
        val factory = IrbRunConfigurationFactory(IrbRunConfigurationType.getInstance())
        val settings = module?.let {
            factory.createConfigurationSettings(
                it,
                name,
                railsPath,
                listOf("r", getCommand()),
                IrbConsoleType.RAILS
            )
        }
        val configuration = settings?.configuration as IrbRunConfiguration?
        configuration?.setWorkingDirectory(project.basePath)
        return configuration
    }
}
