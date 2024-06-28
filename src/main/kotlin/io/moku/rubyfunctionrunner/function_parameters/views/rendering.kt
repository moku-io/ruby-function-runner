package io.moku.rubyfunctionrunner.function_parameters.views

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.EditorTextField
import com.intellij.ui.JBColor
import com.intellij.ui.SideBorder
import com.intellij.ui.dsl.builder.*
import io.moku.rubyfunctionrunner.function_parameters.models.ParameterModel
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTable


fun <T: EditorTextField> Cell<T>.required(): Cell<T> {
    return validationOnApply {  element ->
        if (element.text.isBlank()) {
            ValidationInfo("This field is required")
        } else {
            null
        }
    }
}

fun Panel.render(models: List<ParameterModel>, project: Project) {
    models.forEachIndexed { index, model ->
        row(model.argumentInfo.name + ":") {
            model.tableModel?.let { tableModel ->
                topGap(TopGap.SMALL)
                cell(JPanel(BorderLayout()).apply {
                    border = SideBorder(JBColor.border(), SideBorder.TOP or SideBorder.LEFT)

                    val table = JTable(tableModel)
                    table.setDefaultEditor(String::class.java, RubyTableCellEditor(project))
                    table.rowHeight = 20
                    add(table, BorderLayout.CENTER)

                    val tableHeader = table.tableHeader
                    tableHeader.border = SideBorder(JBColor.border(), SideBorder.RIGHT)
                    add(tableHeader, BorderLayout.NORTH)
                }).align(AlignX.FILL)
                if (index + 1 < models.count()) {
                    bottomGap(BottomGap.SMALL)
                }
            } ?: run {
                cell(EditorTextField(project, RubyFileType.RUBY))
                    .bind(EditorTextField::getText, EditorTextField::setText, model::currentValue.toMutableProperty())
                    .align(AlignX.FILL)
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
