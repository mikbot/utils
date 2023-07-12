package dev.schlaubi.mikbot.util_plugins.gpt.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(val answer: String, val totalTokens: Int)
