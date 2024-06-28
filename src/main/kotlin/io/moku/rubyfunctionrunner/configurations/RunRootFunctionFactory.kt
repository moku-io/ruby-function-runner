package io.moku.rubyfunctionrunner.configurations

import com.intellij.execution.configurations.RunConfiguration
import io.moku.rubyfunctionrunner.RootFunction
import io.moku.rubyfunctionrunner.function_parameters.models.ParameterModel
import io.moku.rubyfunctionrunner.settings.AppSettings

abstract class RunRootFunctionFactory(private val function: RootFunction, private val parameters: List<ParameterModel>? = null) {
    internal val projectSettings
        get() = AppSettings.instance.state
    abstract fun build(name: String): RunConfiguration

    fun getCommand(): String {
        val commandBuilder = StringBuilder()
        commandBuilder.appendLine("require \"${function.file.virtualFile.path}\"")
        commandBuilder.append("${function.name}(")
        var addComma = false
        parameters?.forEach { parameter ->
            if (parameter.currentValue.isNotBlank()) {
                if (addComma) { commandBuilder.append(", ") }
                if (parameter.argumentInfo.type.hasName()) {
                    commandBuilder.append("${parameter.argumentInfo.name}: ")
                }
                commandBuilder.append(parameter.currentValue)
                addComma = true
            }
        }
        commandBuilder.append(")")
        return commandBuilder.toString()
    }
}
