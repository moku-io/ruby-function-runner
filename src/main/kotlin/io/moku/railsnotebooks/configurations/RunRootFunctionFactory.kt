package io.moku.railsnotebooks.configurations

import com.intellij.execution.configurations.RunConfiguration

interface RunRootFunctionFactory {
    fun build(name: String): RunConfiguration
}
