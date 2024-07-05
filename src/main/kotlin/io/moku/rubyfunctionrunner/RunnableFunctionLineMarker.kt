package io.moku.rubyfunctionrunner

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.ExpUiIcons.Run
import com.intellij.psi.PsiElement
import io.moku.rubyfunctionrunner.functions.RunnableFunction

class RunnableFunctionLineMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? =
        RunnableFunction.get(element)?.let { function ->
            Info(
                Run.Run,
                { _ -> "Run ${function.name}" },
                RunnableFunctionAction(function, false),
                RunnableFunctionAction(function, true)
            )
        }
}
