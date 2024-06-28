package io.moku.rubyfunctionrunner.function_parameters.views

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.DialogValidation
import com.intellij.ui.EditorTextField
import com.intellij.ui.dsl.builder.*
import io.moku.rubyfunctionrunner.function_parameters.models.ParameterModel
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTable


fun <T: EditorTextField> Cell<T>.required(): Cell<T> {
    return validation(
        DialogValidation.WithParameter { par ->
            DialogValidation {
                if (par.text.isBlank()) {
                    ValidationInfo("This field is required")
                } else {
                    null
                }
            }
        }
    )
}

fun Panel.render(models: List<ParameterModel>, project: Project) {
    models.forEachIndexed { index, model ->
        row(model.argumentInfo.name + ":") {
            model.tableModel?.let { tableModel ->
                topGap(TopGap.SMALL)
                cell(JPanel(BorderLayout()).apply {
                    val table = JTable(tableModel)
                    val editor = RubyTableCellEditor(project)
                    table.setDefaultEditor(String::class.java, editor)
                    table.rowHeight = 20
                    add(table, BorderLayout.CENTER)
                    add(table.tableHeader, BorderLayout.NORTH)
                }).widthGroup("input")
                if (index + 1 < models.count()) {
                    bottomGap(BottomGap.SMALL)
                }
            } ?: run {
                cell(EditorTextField(project, RubyFileType.RUBY))
                    .bind(EditorTextField::getText, EditorTextField::setText, model::currentValue.toMutableProperty())
                    .widthGroup("input")
                    .required()
            }
        }
        onApply {
            model.tableModel?.let {
                model.currentValue = it.toValue()
            }
        }
    }
}
