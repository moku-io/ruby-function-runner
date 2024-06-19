package io.moku.railsnotebooks

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.ExpUiIcons.Run
import com.intellij.psi.PsiElement

class RunRootFunctionLineMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        try {
            val function = RootFunction(element)
            return Info(
                Run.Run,
                { _ -> "Run ${function.name}" },
                RunRootFunctionAction(function)
            )
        } catch (_: RootFunction.NotARootFunctionException) {
        }
        return null
    }
}
