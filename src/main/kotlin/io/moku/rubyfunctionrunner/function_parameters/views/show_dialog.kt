package io.moku.rubyfunctionrunner.function_parameters.views

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.dsl.builder.panel
import io.moku.rubyfunctionrunner.RootFunction
import io.moku.rubyfunctionrunner.function_parameters.models.ParameterModel

fun RootFunction.showArgumentsDialog(): List<ParameterModel>? {
    val models = arguments.orEmpty().map { ParameterModel(it) }
    val db = DialogBuilder().centerPanel(
        panel {
            render(models, file.project)
        }
    )
    db.addOkAction()
    db.addCancelAction()
    return if (db.showAndGet()) {
        models
    } else {
        null
    }
}