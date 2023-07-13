package dev.schlaubi.mikbot.util_plugins.gpt.commands

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.channel.MessageChannel

class NewChannelArgs : Arguments() {
    val channel by channel {
        name = "channel"
        description = "commands.new_gpt_channel.arguments.channel.description"
        requiredChannelTypes.add(ChannelType.GuildText)
    }
}

suspend fun Extension.newChannelCommand() = ephemeralSlashCommand(::NewChannelArgs) {
    name = "new-gpt-channel"
    description = "commands.new_gpt_channel.description"
    requirePermission(Permission.ManageChannels)

    action {
        createConversation(arguments.channel.asChannelOf<MessageChannel>())
    }
}
