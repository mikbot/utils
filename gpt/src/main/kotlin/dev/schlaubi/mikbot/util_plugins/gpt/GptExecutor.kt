package dev.schlaubi.mikbot.util_plugins.gpt

import com.kotlindiscord.kord.extensions.checks.isNotBot
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.datetime.Clock

suspend fun Extension.gptExecutor() = event<MessageCreateEvent> {
    check {
        isNotBot()
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
            event.message.channel.createMessage(newRequest.messages.last().content)
        }
    }
}
