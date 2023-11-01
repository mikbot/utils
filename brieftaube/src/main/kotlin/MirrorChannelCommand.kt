package dev.schlaubi.mikbot.util_plugins.brieftaube

import com.kotlindiscord.kord.extensions.checks.guildFor
import com.kotlindiscord.kord.extensions.checks.isInThread
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.utils.suggestStringMap
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.interaction.GuildInteraction
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.litote.kmongo.eq

class MirrorChannelArgs : Arguments() {
    val channel by channel {
        name = "channel"
        description = "commands.mirror_channel.arguments.channel.description"

        requireChannelType(ChannelType.GuildText)
        requireChannelType(ChannelType.GuildNews)
    }
}

class UnMirrorChannelArgs : Arguments() {
    val channel by string {
        name = "channel"
        description = "commands.unmirror_channel.arguments.channel.description"

        autoComplete {
            val items = BrieftaubeDatabase.channels
                .find(MirroredChannel::guildId eq guildFor(it)?.id)
                .toFlow()
                .take(25)
                .mapNotNull { (id, _, sourceChannelId, targetChannelId) ->
                    val sourceChannel = kord.getChannelOf<GuildChannel>(sourceChannelId) ?: return@mapNotNull null
                    val targetChannel = kord.getChannelOf<ThreadChannel>(targetChannelId) ?: return@mapNotNull null

                    "${sourceChannel.name} -> ${targetChannel.getParent().name}(#${targetChannel.name})" to id.toString()
                }
                .toList()
                .toMap()

            suggestStringMap(items)
        }
    }
}

suspend fun Extension.mirrorChannelCommand() = ephemeralSlashCommand(::MirrorChannelArgs) {
    name = "mirror"
    description = "commands.mirror_channel.description"

    check {
        isInThread()
        requireBotPermissions(Permission.ManageChannels)
    }

    action {
        val targetChannel = event.interaction.channel.asChannelOf<ThreadChannel>()

        val webhook = findOrCreateWebhookFor(targetChannel)

        val item = MirroredChannel(
            guildId = (event.interaction as GuildInteraction).guildId,
            sourceChannelId = arguments.channel.id,
            targetChannelId = event.interaction.channelId,
            webhook = webhook
        )
        BrieftaubeDatabase.channels.save(item)

        respond {
            content = translate("commands.mirror_channel.success", arrayOf(arguments.channel.mention))
        }
    }
}

suspend fun Extension.unMirrorChannelCommand() = ephemeralSlashCommand(::UnMirrorChannelArgs) {
    name = "un-mirror"
    description = "commands.unmirror_channel.description"

    action {
        val item = BrieftaubeDatabase.channels.findOneById(ObjectId(arguments.channel))

        if (item == null) {
            respond {
                content = translate("commands.unmirror_channel.not_found", arrayOf(arguments.channel))
            }
            return@action
        }

        respond {
            content = translate("commands.unmirror_channel.success")
        }
    }
}
