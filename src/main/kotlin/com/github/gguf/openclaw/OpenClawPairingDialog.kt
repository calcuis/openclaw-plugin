package com.github.gguf.openclaw

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class OpenClawPairingDialog(project: Project) : DialogWrapper(project) {

    private val appBox = ComboBox(arrayOf("telegram", "whatsapp", "signal", "discord", "slack", "feishu", "line", "imessage"))
    private val codeField = JBTextField()

    val selectedApp: String
        get() = appBox.selectedItem as String

    val pairingCode: String
        get() = codeField.text.trim()

    init {
        title = "Pair Device"
        init()
    }

    override fun createCenterPanel(): JComponent =
        FormBuilder.createFormBuilder()
            .addLabeledComponent("App:", appBox)
            .addLabeledComponent("Pairing code:", codeField)
            .addComponentFillVertically(JPanel(), 0)
            .panel

    override fun doValidate(): ValidationInfo? =
        if (pairingCode.isBlank()) ValidationInfo("Enter a pairing code.", codeField) else null
}
