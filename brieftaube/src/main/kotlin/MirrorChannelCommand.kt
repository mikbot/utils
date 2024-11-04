package dev.schlaubi.mikbot.util_plugins.brieftaube

import dev.kordex.core.checks.guildFor
import dev.kordex.core.checks.isInThread
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.channel
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.utils.suggestStringMap
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.interaction.GuildInteraction
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.utils.translations.BrieftaubeTranslations
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.litote.kmongo.eq

class MirrorChannelArgs : Arguments() {
    val channel by channel {
        name = BrieftaubeTranslations.Commands.MirrorChannel.Arguments.Channel.name
        description = BrieftaubeTranslations.Commands.MirrorChannel.Arguments.Channel.description

        requireChannelType(ChannelType.GuildText)
        requireChannelType(ChannelType.GuildNews)
    }
}

class UnMirrorChannelArgs : Arguments() {
    val channel by string {
        name = BrieftaubeTranslations.Commands.UnmirrorChannel.Arguments.Channel.name
        description = BrieftaubeTranslations.Commands.UnmirrorChannel.Arguments.Channel.description

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
    name = BrieftaubeTranslations.Commands.MirrorChannel.name
    description = BrieftaubeTranslations.Commands.MirrorChannel.description

    guildAdminOnly()

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
            content = translate(BrieftaubeTranslations.Commands.MirrorChannel.success, arguments.channel.mention)
        }
    }
}

suspend fun Extension.unMirrorChannelCommand() = ephemeralSlashCommand(::UnMirrorChannelArgs) {
    name = BrieftaubeTranslations.Commands.UnmirrorChannel.name
    description = BrieftaubeTranslations.Commands.UnmirrorChannel.description

    guildAdminOnly()

    action {
        val item = BrieftaubeDatabase.channels.findOneById(ObjectId(arguments.channel))

        if (item == null) {
            respond {
                content = translate(BrieftaubeTranslations.Commands.UnmirrorChannel.notFound, arguments.channel)
            }
            return@action
        }

        respond {
            content = translate(BrieftaubeTranslations.Commands.UnmirrorChannel.success)
        }
    }
}
