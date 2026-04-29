package com.github.gguf.openclaw

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class OpenClawStatusBarWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = OpenClawStatusBarWidget.ID

    override fun getDisplayName(): String = "OpenClaw"

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget = OpenClawStatusBarWidget(project)

    override fun disposeWidget(widget: StatusBarWidget) = widget.dispose()

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}
