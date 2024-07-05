package io.moku.rubyfunctionrunner.configurations

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import io.moku.rubyfunctionrunner.functions.RunnableFunction
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import io.moku.rubyfunctionrunner.settings.AppSettings
import org.jetbrains.plugins.ruby.ruby.run.configuration.RubyRunConfigurationType
import org.jetbrains.plugins.ruby.ruby.run.configuration.rubyScript.RubyRunConfiguration
import java.io.File

class RubyConfigurationFactory(function: RunnableFunction, arguments: List<ArgumentModel>? = null): RunnableFunctionActionFactory(function, arguments) {
    private val project = function.psiFile.project
    private val module: Module?
    private val file by lazy {
        val f = File.createTempFile("RubyRunConfiguration", ".rb")
        f.writeText(getCommand())
        f
    }
    init {
        val index = ProjectRootManager.getInstance(project).fileIndex
        module = index.getModuleForFile(function.psiFile.virtualFile)
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
