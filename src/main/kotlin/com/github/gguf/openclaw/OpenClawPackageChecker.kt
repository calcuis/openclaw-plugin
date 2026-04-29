package com.github.gguf.openclaw

import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.SystemInfo
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

object OpenClawPackageChecker {

    fun check(project: Project) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Checking openclaw package...", false) {
            override fun run(indicator: com.intellij.openapi.progress.ProgressIndicator) {
                val installedVersion = runCatching { installedVersion() }.getOrNull()
                if (installedVersion.isNullOrBlank()) {
                    ApplicationManager.getApplication().invokeLater {
                        val result = Messages.showOkCancelDialog(
                            project,
                            "OpenClaw is not installed.",
                            "OpenClaw",
                            "Install OpenClaw",
                            "Cancel",
                            Messages.getWarningIcon()
                        )
                        if (result == Messages.OK) {
                            OpenClawTerminalHelper.runClawCommand(project, "curl -fsSL https://openclaw.ai/install.sh | bash")
                        }
                    }
                    return
                }

                val latestVersion = runCatching { latestVersion() }.getOrNull()
                if (latestVersion.isNullOrBlank()) {
                    notify(project, "Failed to check latest OpenClaw version from npm.", NotificationType.ERROR)
                    return
                }

                if (isOlder(installedVersion, latestVersion)) {
                    ApplicationManager.getApplication().invokeLater {
                        val result = Messages.showOkCancelDialog(
                            project,
                            "OpenClaw update available.\n\nCurrent: $installedVersion\nLatest: $latestVersion",
                            "OpenClaw",
                            "Update OpenClaw",
                            "Cancel",
                            Messages.getInformationIcon()
                        )
                        if (result == Messages.OK) {
                            OpenClawTerminalHelper.runClawCommand(project, "npm update -g openclaw")
                        }
                    }
                } else {
                    notify(project, "OpenClaw is up to date (v$installedVersion).", NotificationType.INFORMATION)
                }
            }
        })
    }

    private fun installedVersion(): String? {
        val output = runCommand(npmCommand("list", "-g", "openclaw", "--json", "--depth=0"))
        return Regex(""""openclaw"\s*:\s*\{[^}]*"version"\s*:\s*"([^"]+)"""").find(output)?.groupValues?.get(1)
    }

    private fun latestVersion(): String = runCommand(npmCommand("view", "openclaw", "version")).trim()

    private fun npmCommand(vararg args: String): List<String> =
        if (SystemInfo.isWindows) listOf("wsl", "-d", "Ubuntu", "npm", *args) else listOf("npm", *args)

    private fun runCommand(command: List<String>): String {
        val process = ProcessBuilder(command).redirectErrorStream(true).start()
        val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
        process.waitFor(30, TimeUnit.SECONDS)
        if (process.exitValue() != 0) error(output.ifBlank { "Command failed: ${command.joinToString(" ")}" })
        return output
    }

    private fun isOlder(current: String, latest: String): Boolean {
        val currentVersion = Semver.parse(current)
        val latestVersion = Semver.parse(latest)

        for (i in 0 until maxOf(currentVersion.core.size, latestVersion.core.size)) {
            val a = currentVersion.core.getOrElse(i) { 0 }
            val b = latestVersion.core.getOrElse(i) { 0 }
            if (a < b) return true
            if (a > b) return false
        }

        if (currentVersion.pre.isNotEmpty() && latestVersion.pre.isEmpty()) return true
        if (currentVersion.pre.isEmpty() && latestVersion.pre.isNotEmpty()) return false
        return currentVersion.pre.isNotEmpty() && currentVersion.pre < latestVersion.pre
    }

    private data class Semver(val core: List<Int>, val pre: String) {
        companion object {
            fun parse(value: String): Semver {
                val normalized = value.trim().removePrefix("v")
                val parts = normalized.split("-", limit = 2)
                return Semver(parts[0].split(".").map { it.toIntOrNull() ?: 0 }, parts.getOrElse(1) { "" })
            }
        }
    }

    private fun notify(project: Project, message: String, type: NotificationType) {
        ApplicationManager.getApplication().invokeLater {
            OpenClawTerminalHelper.notify(project, message, type)
        }
    }
}
