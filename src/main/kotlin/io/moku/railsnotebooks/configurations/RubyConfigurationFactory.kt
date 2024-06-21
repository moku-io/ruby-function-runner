package io.moku.railsnotebooks.configurations

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import io.moku.railsnotebooks.RootFunction
import org.jetbrains.plugins.ruby.ruby.run.configuration.RubyRunConfigurationType
import org.jetbrains.plugins.ruby.ruby.run.configuration.rubyScript.RubyRunConfiguration
import java.io.File

class RubyConfigurationFactory(private val function: RootFunction): RunRootFunctionFactory {
    private val project = function.file.project
    private val module: Module?
    private val file by lazy {
        val f = File.createTempFile("RubyRunConfiguration", ".rb")
        f.writeText("require \"${function.file.virtualFile.path}\"\n${function.name}")
        f
    }
    init {
        val index = ProjectRootManager.getInstance(project).fileIndex
        module = index.getModuleForFile(function.file.virtualFile)
    }

    override fun build(name: String): RubyRunConfiguration {
        val factory = RubyRunConfigurationType.getInstance().rubyScriptFactory
        val template = factory.createTemplateConfiguration(project) as RubyRunConfiguration
        template.setScriptPath(file.path)
        template.setWorkingDirectory(project.basePath)
        return factory.createConfiguration(name, template) as RubyRunConfiguration
    }
}
