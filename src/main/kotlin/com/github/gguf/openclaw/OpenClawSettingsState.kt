package com.github.gguf.openclaw

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.gguf.openclaw.OpenClawSettingsState",
    storages = [Storage("OpenClawPlugin.xml")]
)
class OpenClawSettingsState : PersistentStateComponent<OpenClawSettingsState> {

    var autoConnect: Boolean = false

    override fun getState(): OpenClawSettingsState = this

    override fun loadState(state: OpenClawSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): OpenClawSettingsState =
            ApplicationManager.getApplication().getService(OpenClawSettingsState::class.java)
    }
}
