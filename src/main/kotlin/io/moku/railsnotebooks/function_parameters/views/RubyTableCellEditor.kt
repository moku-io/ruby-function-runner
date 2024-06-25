package io.moku.railsnotebooks.function_parameters.views

import com.intellij.openapi.observable.util.whenDocumentChanged
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import com.intellij.util.ui.AbstractTableCellEditor
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.JTable
import javax.swing.KeyStroke

class RubyTableCellEditor(private val project: Project): AbstractTableCellEditor() {
    private var currentValue: String = ""


    override fun getCellEditorValue(): String {
        return currentValue
    }

    override fun getTableCellEditorComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        row: Int,
        column: Int
    ): Component {
        val editor = EditorTextField(project, RubyFileType.RUBY)
        editor.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "my_key")
        editor.actionMap.put("my_key", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                stopCellEditing()
            }
        })
        editor.setOneLineMode(true)
        (value as? String)?.let {
            currentValue = it
            editor.text = it
        }
        editor.whenDocumentChanged { event ->
            currentValue = event.document.text
        }
        return editor
    }
}
