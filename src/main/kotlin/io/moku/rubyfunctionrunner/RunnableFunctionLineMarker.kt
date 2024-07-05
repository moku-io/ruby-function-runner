package io.moku.rubyfunctionrunner

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.ExpUiIcons.Run
import com.intellij.psi.PsiElement

class RunnableFunctionLineMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        try {
            val function = RootFunction(element)
            return Info(
                Run.Run,
                { _ -> "Run ${function.name}" },
                RunnableFunctionAction(function, false),
                RunnableFunctionAction(function, true)
            )
        } catch (_: RootFunction.NotARootFunctionException) {
        }
        return null
    }
}
