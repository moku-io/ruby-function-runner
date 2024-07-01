package io.moku.rubyfunctionrunner

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement

class RunRootFunctionLineMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        try {
            val function = RootFunction(element)
            return Info(
                AllIcons.RunConfigurations.TestState.Run,
                arrayOf(
                    RunRootFunctionAction(function, false),
                    RunRootFunctionAction(function, true)
                )
            ) { _ -> "Run ${function.name}" }
        } catch (_: RootFunction.NotARootFunctionException) { }
        return null
    }
}
