package io.moku.rubyfunctionrunner.function_arguments.views

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.dsl.builder.panel
import io.moku.rubyfunctionrunner.functions.RunnableFunction
import io.moku.rubyfunctionrunner.function_arguments.models.ArgumentModel

fun RunnableFunction.showArgumentsDialog(): List<ArgumentModel>? {
    val models = arguments.orEmpty().map { ArgumentModel(it) }
    val db = DialogBuilder().centerPanel(
        panel {
            render(models, psiFile.project)
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
