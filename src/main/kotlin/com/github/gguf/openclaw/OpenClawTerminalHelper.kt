package com.github.gguf.openclaw

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import org.jetbrains.plugins.terminal.TerminalView

object OpenClawTerminalHelper {

    @Volatile
    private var clawWidget: ShellTerminalWidget? = null

    @Volatile
    private var ggcWidget: ShellTerminalWidget? = null

    fun runClawCommand(project: Project, command: String) {
        if (command == "ggc oc") {
            runTerminalCommand(project, "Magnet", command, useWsl = false, reuseGgc = true)
        } else {
            runTerminalCommand(project, "OpenClaw", command, useWsl = SystemInfo.isWindows, reuseGgc = false)
        }
    }

    private fun runTerminalCommand(project: Project, title: String, command: String, useWsl: Boolean, reuseGgc: Boolean) {
        val terminalView = TerminalView.getInstance(project)
        val workDir = project.basePath
        val actualCommand = if (useWsl) wslWrap(command) else command

        ApplicationManager.getApplication().invokeLater {
            val widget = terminalView.createLocalShellWidget(workDir, title, true)
            if (reuseGgc) {
                ggcWidget = widget
            } else {
                clawWidget = widget
            }
            ApplicationManager.getApplication().executeOnPooledThread {
                Thread.sleep(800)
                ApplicationManager.getApplication().invokeLater {
                    runCatching { widget.executeCommand(actualCommand) }
                        .onFailure { notify(project, "Failed to execute $command: ${it.message}", NotificationType.ERROR) }
                }
            }
        }
    }

    private fun wslWrap(command: String): String {
        val escaped = command.replace("'", "'\\''")
        return "wsl -d Ubuntu -e sh -lc '$escaped'"
    }

    fun notify(project: Project, message: String, type: NotificationType) {
        Notifications.Bus.notify(Notification("OpenClaw", "OpenClaw", message, type), project)
    }
}
