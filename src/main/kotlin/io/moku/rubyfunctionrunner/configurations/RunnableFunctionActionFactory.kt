package io.moku.rubyfunctionrunner.configurations

import com.intellij.execution.configurations.RunConfiguration
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel
import io.moku.rubyfunctionrunner.functions.RunnableFunction
import io.moku.rubyfunctionrunner.settings.AppSettings

abstract class RunnableFunctionActionFactory(private val function: RunnableFunction, private val arguments: List<ArgumentModel>? = null) {
    internal val projectSettings
        get() = AppSettings.instance?.state
    abstract fun build(name: String): RunConfiguration

    fun getCommand() = function.getCommand(arguments)
}
