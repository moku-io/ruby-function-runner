package io.moku.railsnotebooks.function_parameters

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.moku.railsnotebooks.RootFunction
import io.moku.railsnotebooks.function_parameters.models.ParameterModel
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTable

fun Panel.render(models: List<ParameterModel>) {
    models.forEach { model ->
        row(model.argumentInfo.name + ":") {
            model.tableModel?.let { tableModel ->
                cell(JPanel(BorderLayout()).apply {
                    val table = JTable(tableModel)
                    add(table, BorderLayout.CENTER)
                    add(table.tableHeader, BorderLayout.NORTH)
                }).widthGroup("input")
            } ?: run {
                textField().bindText(model::currentValue).widthGroup("input")
            }
        }
        onApply {
            model.tableModel?.let {
                model.currentValue = it.toValue()
            }
        }
    }
}

fun RootFunction.showArgumentsDialog(): List<ParameterModel>? {
    val models = arguments.map { ParameterModel(it) }
    val db = DialogBuilder().centerPanel(
        panel {
            render(models)
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
