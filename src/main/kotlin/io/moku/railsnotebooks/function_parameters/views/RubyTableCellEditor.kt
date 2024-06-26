package io.moku.railsnotebooks.function_parameters.views

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.observable.util.addDocumentListener
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

class RubyTableCellEditor(private val project: Project): AbstractTableCellEditor(), Disposable, DocumentListener {
    private var currentValue: String = ""
    private var currentEditor: EditorTextField? = null

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
        dispose()
        currentEditor = EditorTextField(project, RubyFileType.RUBY)
        currentEditor!!.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "my_key")
        currentEditor!!.actionMap.put("my_key", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                stopCellEditing()
            }
        })
        currentEditor!!.setOneLineMode(true)
        (value as? String)?.let {
            currentValue = it
            currentEditor!!.text = it
        }
        currentEditor!!.addDocumentListener(this, this)
        return currentEditor!!
    }

    override fun documentChanged(event: DocumentEvent) {
        currentValue = event.document.text
    }

    override fun dispose() {
        currentEditor?.removeDocumentListener(this)
        currentEditor?.inputMap?.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0))
        currentEditor?.actionMap?.remove("my_key")
        val disp = Disposable {}
        currentEditor?.setDisposedWith(disp)
        disp.dispose()
    }
}
