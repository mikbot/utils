package dev.schlaubi.mikbot.util_plugins.gpt.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(val role: Role, val content: String) {
    @Serializable
    enum class Role {
        @SerialName("assistant")
        ASSISTANT,
        @SerialName("user")
        USER
    }
}
