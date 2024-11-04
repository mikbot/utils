package dev.schlaubi.mikbot.util_plugins.gpt.commands

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.channel
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.InteractionContextType
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.channel.MessageChannel
import dev.schlaubi.mikbot.utils.translations.DeppgptTranslations

class NewChannelArgs : Arguments() {
    val channel by channel {
        name = DeppgptTranslations.Commands.NewGptChannel.Arguments.Channel.name
        description = DeppgptTranslations.Commands.NewGptChannel.Arguments.Channel.description
        requiredChannelTypes.add(ChannelType.GuildText)
    }
}

suspend fun Extension.newChannelCommand() = ephemeralSlashCommand(::NewChannelArgs) {
    name = DeppgptTranslations.Commands.NewGptChannel.name
    description = DeppgptTranslations.Commands.NewGptChannel.description

    allowedContexts.add(InteractionContextType.Guild)
    requirePermission(Permission.ManageChannels)
    requireBotPermissions(Permission.ManageChannels)

    action {
        createConversation(arguments.channel.asChannelOf<MessageChannel>())
    }
}
