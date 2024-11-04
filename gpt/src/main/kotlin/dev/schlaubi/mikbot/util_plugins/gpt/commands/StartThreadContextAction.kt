package dev.schlaubi.mikbot.util_plugins.gpt.commands

import dev.kordex.core.checks.channelType
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralMessageCommand
import dev.kord.common.entity.ChannelType
import dev.kord.core.behavior.channel.asChannelOf
import dev.schlaubi.mikbot.utils.translations.DeppgptTranslations

suspend fun Extension.startThreadContextAction() = ephemeralMessageCommand {
    name = DeppgptTranslations.Commands.StartThread.name

    check {
        channelType(ChannelType.GuildText)
    }

    action {
        val target = event.interaction.target.asMessage()
        createConversation(channel.asChannelOf(), target)
    }
}
