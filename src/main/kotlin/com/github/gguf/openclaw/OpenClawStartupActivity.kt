package com.github.gguf.openclaw

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class OpenClawStartupActivity : StartupActivity.DumbAware {

    override fun runActivity(project: Project) {
        if (!OpenClawSettingsState.getInstance().autoConnect) return
        ApplicationManager.getApplication().executeOnPooledThread {
            Thread.sleep(1000)
            ApplicationManager.getApplication().invokeLater {
                OpenClawTerminalHelper.runClawCommand(project, "openclaw status")
            }
        }
    }
}
