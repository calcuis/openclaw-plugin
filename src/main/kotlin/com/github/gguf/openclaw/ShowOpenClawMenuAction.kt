package com.github.gguf.openclaw

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.ui.popup.JBPopupFactory

class ShowOpenClawMenuAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        showMenu(e.project ?: return)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {

        fun showMenu(project: Project) {
            showChooser(
                project,
                "OpenClaw",
                listOf("Dashboard", "Check", "Panel", "Setup", "Gateway", "Terminal")
            ) { selected ->
                when (selected) {
                    "Dashboard" -> OpenClawTerminalHelper.runClawCommand(project, "openclaw dashboard")
                    "Check" -> OpenClawPackageChecker.check(project)
                    "Panel" -> OpenClawDashboardLauncher.launch(project)
                    "Setup" -> showSetupMenu(project)
                    "Gateway" -> showGatewayMenu(project)
                    "Terminal" -> OpenClawTerminalHelper.runClawCommand(project, "ggc oc")
                }
            }
        }

        private fun showGatewayMenu(project: Project) {
            val commands = linkedMapOf(
                "Run" to "openclaw gateway run",
                "Status" to "openclaw gateway status",
                "Start" to "openclaw gateway start",
                "Stop" to "openclaw gateway stop",
                "Restart" to "openclaw gateway restart"
            )
            showChooser(project, "OpenClaw Gateway", commands.keys.toList()) { selected ->
                OpenClawTerminalHelper.runClawCommand(project, commands.getValue(selected))
            }
        }

        private fun showSetupMenu(project: Project) {
            showChooser(project, "OpenClaw Setup", listOf("Onboard", "Pair up", "Doctor", "Fix", "Console")) { selected ->
                when (selected) {
                    "Onboard" -> OpenClawTerminalHelper.runClawCommand(project, "openclaw onboard")
                    "Doctor" -> OpenClawTerminalHelper.runClawCommand(project, "openclaw doctor")
                    "Fix" -> OpenClawTerminalHelper.runClawCommand(project, "openclaw doctor --fix")
                    "Console" -> OpenClawTerminalHelper.runClawCommand(project, "openclaw tui")
                    "Pair up" -> showPairingDialog(project)
                }
            }
        }

        private fun showPairingDialog(project: Project) {
            val dialog = OpenClawPairingDialog(project)
            if (dialog.showAndGet()) {
                OpenClawTerminalHelper.runClawCommand(
                    project,
                    "openclaw pairing approve ${dialog.selectedApp} ${dialog.pairingCode}"
                )
            }
        }

        private fun showChooser(project: Project, title: String, items: List<String>, onSelect: (String) -> Unit) {
            JBPopupFactory.getInstance()
                .createPopupChooserBuilder(items)
                .setTitle(title)
                .setMovable(true)
                .setResizable(true)
                .setRequestFocus(true)
                .setItemChosenCallback { onSelect(it) }
                .createPopup()
                .apply {
                    val frame = WindowManager.getInstance().getFrame(project)
                    if (frame != null) showInCenterOf(frame) else showInFocusCenter()
                }
        }
    }
}
