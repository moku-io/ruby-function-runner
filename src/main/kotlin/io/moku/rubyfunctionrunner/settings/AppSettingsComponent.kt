package io.moku.rubyfunctionrunner.settings

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.observable.properties.AtomicProperty
import com.intellij.openapi.project.BaseProjectDirectories.Companion.getBaseDirectories
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil
import org.jetbrains.plugins.ruby.wizard.ui.RubySdkComboBox
import java.awt.event.ActionEvent
import javax.swing.JComponent
import javax.swing.JPanel


/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    val panel: JPanel
    private var rubySdk = AtomicProperty<Sdk?>(null)
    var rubySdkName: String?
        get() = rubySdk.get()?.name
        set(value) {
            rubySdkPicker.item = rubySdk.updateAndGet {
                value?.let { sdkNameToSdk(it) }
            }
        }
    var railsPath: String?
        get() = railsScriptPathPicker.text
        set(value) {
            railsScriptPathPicker.text = value ?: ""
        }

    private fun sdkNameToSdk(name: String): Sdk? {
        val sdk = ProjectJdkTable.getInstance().findJdk(name)
        return if (RubySdkUtil.isSDKValid(sdk)) sdk else null
    }
    private lateinit var railsScriptPathPicker: TextFieldWithBrowseButton
    private val rubySdkPicker: RubySdkComboBox = RubySdkComboBox(
        { RubySdkUtil.getAllRubySdks() },
        rubySdk, false
    ).apply {

    }

    init {
        panel = panel {
            row {
                label("Ruby sdk:")
            }
            row {
                cell(rubySdkPicker).align(AlignX.FILL)
            }
            row {
                label("Rails script path:")
            }
            row {
                textFieldWithBrowseButton(
                    fileChooserDescriptor = FileChooserDescriptor(
                        true,
                        false,
                        false,
                        false,
                        false,
                        false
                    ).apply {
                        ProjectUtil.getActiveProject()?.let { project ->
                            roots = project.getBaseDirectories().toList()
                        }
                    }
                ).apply {
                    railsScriptPathPicker = this.component
                }.align(AlignX.FILL)
            }
            row {
                button("Default") {
                    onResetListener?.invoke(it)
                }
            }
        }
    }

    var onResetListener: ((event: ActionEvent) -> Unit)? = null

    val preferredFocusedComponent: JComponent
        get() = rubySdkPicker
}
