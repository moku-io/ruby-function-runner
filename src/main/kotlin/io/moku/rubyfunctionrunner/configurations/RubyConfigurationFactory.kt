package io.moku.rubyfunctionrunner.configurations

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import io.moku.rubyfunctionrunner.RootFunction
import io.moku.rubyfunctionrunner.function_parameters.models.ParameterModel
import io.moku.rubyfunctionrunner.settings.AppSettings
import org.jetbrains.plugins.ruby.ruby.run.configuration.RubyRunConfigurationType
import org.jetbrains.plugins.ruby.ruby.run.configuration.rubyScript.RubyRunConfiguration
import java.io.File

class RubyConfigurationFactory(function: RootFunction, parameters: List<ParameterModel>? = null): RunRootFunctionFactory(function, parameters) {
    private val project = function.file.project
    private val module: Module?
    private val file by lazy {
        val f = File.createTempFile("RubyRunConfiguration", ".rb")
        f.writeText(getCommand())
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
        val sdkName = AppSettings.instance.state.rubySdkName
        if (sdkName != null) {
            template.alternativeSdkName = sdkName
            template.setShouldUseAlternativeSdk(true)
        } else{
            template.alternativeSdkName = null
            template.setShouldUseAlternativeSdk(false)
        }
        return factory.createConfiguration(name, template) as RubyRunConfiguration
    }
}
