package io.moku.rubyfunctionrunner.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent


/**
 * Provides controller functionality for application settings.
 */
internal class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null
        set(value) {
            field = value?.apply {
                onResetListener = {
                    resetToDefault()
                }
            }
        }

    // A default constructor with no arguments is required because
    // this implementation is registered as an applicationConfigurable
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Ruby Runner Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return mySettingsComponent?.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent?.panel
    }

    override fun isModified(): Boolean {
        val state: AppSettings.State = AppSettings.instance?.state ?: AppSettings.State()
        return mySettingsComponent?.rubySdkName != state.rubySdkName ||
                mySettingsComponent?.railsPath != state.railsExecutablePath ||
                mySettingsComponent?.printCommand != state.printCommand
    }

    override fun apply() {
        val state: AppSettings.State = AppSettings.instance?.state ?: AppSettings.State()
        state.rubySdkName = mySettingsComponent?.rubySdkName
        state.railsExecutablePath = mySettingsComponent?.railsPath
        state.printCommand = mySettingsComponent?.printCommand ?: ""
    }

    override fun reset() {
        val state: AppSettings.State = AppSettings.instance?.state ?: AppSettings.State()
        mySettingsComponent?.rubySdkName = state.rubySdkName
        mySettingsComponent?.railsPath = state.railsExecutablePath
        mySettingsComponent?.printCommand = state.printCommand
    }

    private fun resetToDefault() {
        val state = AppSettings.State()
        mySettingsComponent?.rubySdkName = state.rubySdkName
        mySettingsComponent?.railsPath = state.railsExecutablePath
        mySettingsComponent?.printCommand = state.printCommand
    }


    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
