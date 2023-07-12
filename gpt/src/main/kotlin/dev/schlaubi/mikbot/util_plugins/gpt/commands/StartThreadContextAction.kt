package dev.schlaubi.mikbot.util_plugins.gpt.commands

import com.kotlindiscord.kord.extensions.checks.channelType
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralMessageCommand
import dev.kord.common.entity.ChannelType
import dev.kord.core.behavior.channel.asChannelOf

suspend fun Extension.startThreadContextAction() = ephemeralMessageCommand {
    name = "commands.start_thread_action.name"

    check {
        channelType(ChannelType.GuildText)
    }

    action {
        val target = event.interaction.target.asMessage()
        createConversation(channel.asChannelOf(), target)
    }
}
