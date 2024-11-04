package dev.schlaubi.mikbot.util_plugins.gpt.commands

import dev.kordex.core.checks.channelType
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.defaultingString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.InteractionContextType
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.channel.TextChannel
import dev.schlaubi.mikbot.utils.translations.DeppgptTranslations

class StartThreadArgs : Arguments() {
    val name by defaultingString {
        name = DeppgptTranslations.Commands.StartThread.Arguments.Name.name
        description = DeppgptTranslations.Commands.StartThread.Arguments.Name.description
        defaultValue = "deppgpt"
    }
}

suspend fun Extension.newThreadCommand() = ephemeralSlashCommand(::StartThreadArgs) {
    name = DeppgptTranslations.Commands.StartThread.name
    description = DeppgptTranslations.Commands.StartThread.description
    allowedContexts.add(InteractionContextType.Guild)
    allowedInstallTypes.addAll(listOf(ApplicationIntegrationType.GuildInstall, ApplicationIntegrationType.UserInstall))

    check {
        channelType(ChannelType.GuildText)
    }

    action {
        createConversation(channel.asChannelOf<TextChannel>(), arguments.name)
    }
}
