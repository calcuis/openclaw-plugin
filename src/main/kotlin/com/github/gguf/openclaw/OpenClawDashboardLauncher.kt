package com.github.gguf.openclaw

import com.intellij.ide.BrowserUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

object OpenClawDashboardLauncher {

    private val urlRegex = Regex("""https?://127\.0\.0\.1:\d+[^\s\n]*""")

    fun launch(project: Project) {
        OpenClawTerminalHelper.notify(project, "Starting OpenClaw dashboard...", NotificationType.INFORMATION)
        ApplicationManager.getApplication().executeOnPooledThread {
            val command = if (SystemInfo.isWindows) {
                listOf("wsl.exe", "-d", "Ubuntu", "openclaw", "dashboard")
            } else {
                listOf("openclaw", "dashboard")
            }

            runCatching {
                val process = ProcessBuilder(command).redirectErrorStream(true).start()
                val output = StringBuilder()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val deadline = System.currentTimeMillis() + 10_000

                while (System.currentTimeMillis() < deadline) {
                    while (reader.ready()) {
                        output.append(reader.readLine()).append('\n')
                        val url = urlRegex.find(output)?.value
                        if (url != null) {
                            open(url)
                            return@executeOnPooledThread
                        }
                    }
                    if (process.waitFor(200, TimeUnit.MILLISECONDS)) break
                }
                open("http://127.0.0.1:18789/")
            }.onFailure {
                open("http://127.0.0.1:18789/")
                OpenClawTerminalHelper.notify(project, "Could not read dashboard output; opened the default URL.", NotificationType.WARNING)
            }
        }
    }

    private fun open(url: String) {
        ApplicationManager.getApplication().invokeLater {
            BrowserUtil.browse(url)
        }
    }
}
