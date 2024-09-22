package dev.schlaubi.mikbot.util_plugins.gpt.commands

import com.kotlindiscord.kord.extensions.checks.channelType
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.InteractionContextType
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.channel.TextChannel

class StartThreadArgs : Arguments() {
    val name by defaultingString {
        name = "name"
        description = "commands.start_thread.arguments.name.description"
        defaultValue = "deppgpt"
    }
}

suspend fun Extension.newThreadCommand() = ephemeralSlashCommand(::StartThreadArgs) {
    name = "new-gpt-thread"
    description = "commands.start_thread.description"
    allowedContexts.add(InteractionContextType.Guild)
    allowedInstallTypes.addAll(listOf(ApplicationIntegrationType.GuildInstall, ApplicationIntegrationType.UserInstall))

    check {
        channelType(ChannelType.GuildText)
    }

    action {
        createConversation(channel.asChannelOf<TextChannel>(), arguments.name)
    }
}
