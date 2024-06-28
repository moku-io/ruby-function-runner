package io.moku.rubyfunctionrunner.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import java.util.*
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
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        return mySettingsComponent?.rubySdkName != state.rubySdkName ||
                mySettingsComponent?.railsPath != state.railsExecutablePath
    }

    override fun apply() {
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        state.rubySdkName = mySettingsComponent?.rubySdkName
        state.railsExecutablePath = mySettingsComponent?.railsPath
    }

    override fun reset() {
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        mySettingsComponent?.rubySdkName = state.rubySdkName
        mySettingsComponent?.railsPath = state.railsExecutablePath
    }

    private fun resetToDefault() {
        val state = AppSettings.State()
        mySettingsComponent?.rubySdkName = state.rubySdkName
        mySettingsComponent?.railsPath = state.railsExecutablePath
    }


    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
