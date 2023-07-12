package dev.schlaubi.mikbot.util_plugins.gpt

import dev.kord.common.entity.Snowflake
import dev.schlaubi.mikbot.util_plugins.gpt.model.Message
import dev.schlaubi.mikbot.util_plugins.gpt.model.MessageRequest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    @SerialName("_id")
    val channelId: Snowflake,
    val messages: List<Message>,
    val lastReset: Instant
) {
    fun reset() = copy(messages = emptyList(), lastReset = Clock.System.now())
    suspend fun requestAnswer(input: String): Conversation {
        val request = Message(Message.Role.USER, input)
        val (answer) = GptClient.requestMessages(MessageRequest(4,messages + request))
        val allMessages = messages + request + Message(Message.Role.ASSISTANT, answer)

        return copy(messages = allMessages)
    }
}
