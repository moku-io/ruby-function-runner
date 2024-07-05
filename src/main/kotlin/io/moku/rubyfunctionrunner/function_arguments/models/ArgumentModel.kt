package io.moku.rubyfunctionrunner.function_arguments.models

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentInfoWithDefaultArgs

class ArgumentModel(val argumentInfo: ArgumentInfo, var currentValue: String = "") {
    val tableModel: ArgumentTableModel?

    init {
        (argumentInfo as? RArgumentInfoWithDefaultArgs)?.defaultValue?.let {
            currentValue = it
        }
        tableModel = when(argumentInfo.type) {
            ArgumentInfo.Type.ARRAY -> ArrayArgumentTableModel()
            ArgumentInfo.Type.HASH -> HashArgumentTableModel()
            else -> null
        }
    }
}
