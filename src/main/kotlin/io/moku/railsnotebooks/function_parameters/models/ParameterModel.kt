package io.moku.railsnotebooks.function_parameters.models

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentInfoWithDefaultArgs

class ParameterModel(val argumentInfo: ArgumentInfo, var currentValue: String = "") {
    val tableModel: ParameterTableModel?

    init {
        (argumentInfo as? RArgumentInfoWithDefaultArgs)?.defaultValue?.let {
            currentValue = it
        }
        tableModel = when(argumentInfo.type) {
            ArgumentInfo.Type.ARRAY -> ArrayParameterTableModel()
            ArgumentInfo.Type.HASH -> HashParameterTableModel()
            else -> null
        }
    }
}
