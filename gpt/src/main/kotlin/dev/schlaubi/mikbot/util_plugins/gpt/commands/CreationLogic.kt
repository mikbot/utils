package dev.schlaubi.mikbot.util_plugins.gpt.commands

import dev.kordex.core.commands.CommandContext
import dev.kordex.core.types.EphemeralInteractionContext
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.gpt.Conversation
import dev.schlaubi.mikbot.util_plugins.gpt.GptDatabase
import dev.schlaubi.mikbot.utils.translations.DeppgptTranslations
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
                }
            }
        }
    } else {
        conversation
    }
    GptDatabase.conversations.save(filledConversation)

    respond {
        content = translate(DeppgptTranslations.Commands.addSuccessful, channel.mention)
    }
}
