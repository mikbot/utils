package dev.schlaubi.mikbot.util_plugins.gpt

import dev.kordex.core.checks.isNotBot
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.datetime.Clock

suspend fun Extension.gptExecutor() = event<MessageCreateEvent> {
    check {
        isNotBot()
        failIf { event.message.content.startsWith('#') }
    }

    action {
        val conversation = GptDatabase.conversations.findOneById(event.message.channelId) ?: return@action
        val realConversation = if (Clock.System.now() - conversation.lastReset > Config.CONVERSATION_LIFETIME) {
            conversation.reset()
        } else {
            conversation
        }

        event.message.channel.withTyping {
            val newRequest = realConversation.requestAnswer(event.message.content)
            GptDatabase.conversations.save(newRequest)
            event.message.reply {
                content = newRequest.messages.last().content
            }
        }
    }
}
