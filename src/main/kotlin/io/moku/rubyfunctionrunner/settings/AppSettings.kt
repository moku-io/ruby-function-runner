package io.moku.rubyfunctionrunner.settings

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.modules
import org.jetbrains.plugins.ruby.rails.model.RailsApp
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil


/*
* Supports storing the application settings in a persistent way.
* The {@link com.intellij.openapi.components.State State} and {@link Storage}
* annotations define the name of the data and the filename where these persistent
* application settings are stored.
*/
@State(name = "io.moku.rubyfunctionrunner.settings.AppSettings", storages = [Storage("RubyFunctionRunnerSettings.xml")])
internal class AppSettings : PersistentStateComponent<AppSettings.State> {
    internal class State {
        var rubySdkName: String?
        var railsExecutablePath: String?

        private val project by lazy { ProjectUtil.getActiveProject() }
        private val railsApp by lazy {
            project?.modules?.firstNotNullOf {
                RailsApp.fromModule(it)
            }
        }

        init {
            railsExecutablePath = defaultRailsExecutablePath()
            rubySdkName = defaultRubySdkName()
        }

        private fun defaultRubySdkName(): String? = runReadAction { project?.let { RubySdkUtil.findAnyRubySdkUsedInProject(it) }?.name }

        private fun defaultRailsExecutablePath(): String? =
            railsApp?.staticPaths?.binRootURL?.replace("file://", "")?.let { "$it/rails" }
    }

    private var myState = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        val instance: AppSettings
            get() = ApplicationManager.getApplication()
                .getService(AppSettings::class.java)
    }
}
