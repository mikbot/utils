package dev.schlaubi.mikbot.util_plugins.gpt.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(val maxMessages: Int, val messages: List<Message>)
