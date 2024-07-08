package io.moku.rubyfunctionrunner

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import io.moku.rubyfunctionrunner.functions.RunnableFunction

class RunnableFunctionLineMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? =
        RunnableFunction.get(element)?.let { function ->
            Info(
                AllIcons.RunConfigurations.TestState.Run,
                arrayOf(
                    RunnableFunctionAction(function, false),
                    RunnableFunctionAction(function, true)
                )
            ) { _ -> "Run ${function.name}" }
        }
}
