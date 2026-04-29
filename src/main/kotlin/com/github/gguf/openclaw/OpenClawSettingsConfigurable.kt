package com.github.gguf.openclaw

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class OpenClawSettingsConfigurable : Configurable {

    private var panel: JPanel? = null
    private val autoConnectCheckBox = JBCheckBox("Automatically run openclaw status on project startup")

    override fun getDisplayName(): String = "OpenClaw"

    override fun createComponent(): JComponent {
        panel = FormBuilder.createFormBuilder()
            .addComponent(autoConnectCheckBox)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        return panel!!
    }

    override fun isModified(): Boolean =
        autoConnectCheckBox.isSelected != OpenClawSettingsState.getInstance().autoConnect

    override fun apply() {
        OpenClawSettingsState.getInstance().autoConnect = autoConnectCheckBox.isSelected
    }

    override fun reset() {
        autoConnectCheckBox.isSelected = OpenClawSettingsState.getInstance().autoConnect
    }

    override fun disposeUIResources() {
        panel = null
    }
}
