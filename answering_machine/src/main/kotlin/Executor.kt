package dev.schlaubi.mikbot.util_plugins.answering_machine

import com.kotlindiscord.kord.extensions.checks.anyGuild
import com.kotlindiscord.kord.extensions.checks.isNotBot
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.litote.kmongo.eq

suspend fun Extension.executor() = event<MessageCreateEvent> {
    check {
        anyGuild()
        isNotBot()
    }

    action {
        val expression = AnsweringMachineDatabase.regexes
            .find(AnswerRegex::guildId eq event.guildId)
            .toFlow()
            .firstOrNull { event.message.content.matches(it.regex) } ?: return@action

        val answer = event.message.content.replace(expression.regex, expression.replacement)
        if (expression.delete) {
            event.message.delete()
            event.message.channel.createMessage(answer)
        } else {
            event.message.reply {
                content = answer
            }
        }
    }
}
