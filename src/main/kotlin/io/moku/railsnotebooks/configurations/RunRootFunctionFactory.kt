package io.moku.railsnotebooks.configurations

import com.intellij.execution.configurations.RunConfiguration
import io.moku.railsnotebooks.RootFunction
import io.moku.railsnotebooks.function_parameters.models.ParameterModel

abstract class RunRootFunctionFactory(private val function: RootFunction, private val parameters: List<ParameterModel>? = null) {
    abstract fun build(name: String): RunConfiguration

    fun getCommand(): String {
        val commandBuilder = StringBuilder()
        commandBuilder.appendLine("require \"${function.file.virtualFile.path}\"")
        commandBuilder.append("${function.name}(")
        var addComma = false
        parameters?.forEach { parameter ->
            if (parameter.currentValue.isNotBlank()) {
                if (addComma) { commandBuilder.append(", ") }
                commandBuilder.append(parameter.currentValue)
                addComma = true
            }
        }
        commandBuilder.append(")")
        return commandBuilder.toString()
    }
}
