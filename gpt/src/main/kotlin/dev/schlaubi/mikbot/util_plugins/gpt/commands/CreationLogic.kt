package dev.schlaubi.mikbot.util_plugins.gpt.commands

import com.kotlindiscord.kord.extensions.commands.CommandContext
import com.kotlindiscord.kord.extensions.types.EphemeralInteractionContext
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.schlaubi.mikbot.util_plugins.gpt.Conversation
import dev.schlaubi.mikbot.util_plugins.gpt.GptDatabase
import kotlinx.datetime.Clock

context(CommandContext)
suspend fun EphemeralInteractionContext.createConversation(
    baseChannel: TextChannel,
    initialMessage: Message
) {
    val thread = baseChannel.startPublicThreadWithMessage(initialMessage.id, "deppgpt") {}

    createConversation(thread, initialMessage)
}

context(CommandContext)
suspend fun EphemeralInteractionContext.createConversation(
    baseChannel: TextChannel,
    name: String
) {
    val thread = baseChannel.startPublicThread(name) {}

    createConversation(thread)
}

context(CommandContext)
suspend fun EphemeralInteractionContext.createConversation(channel: MessageChannel, initialMessage: Message? = null) {
    val conversation = Conversation(channel.id, emptyList(), Clock.System.now())
    val filledConversation = if (initialMessage != null) {
        channel.withTyping {
            conversation.requestAnswer(initialMessage.content).also {
                channel.createMessage {
                    content = it.messages.last().content
                    messageReference = initialMessage.id
                }
            }
        }
    } else {
        conversation
    }
    GptDatabase.conversations.save(filledConversation)

    respond {
        content = translate("commands.add_successful", arrayOf(channel.mention))
    }
}
