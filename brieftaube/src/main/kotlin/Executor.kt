package dev.schlaubi.mikbot.util_plugins.brieftaube

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.request.RestRequestException
import org.litote.kmongo.eq

@OptIn(KordUnsafe::class, KordExperimental::class)
suspend fun Extension.mirrorChannelExecutor() = event<MessageCreateEvent> {
    action {
        BrieftaubeDatabase.channels
            .find(MirroredChannel::sourceChannelId eq event.message.channelId)
            .toFlow()
            .collect {
                try {
                    val target = kord.unsafe.messageChannel(it.targetChannelId)
                    target.createMessage {
                        content = event.message.content
                        embeds.addAll(event.message.embeds.map { EmbedBuilder().apply { it.apply(this) } })
                    }
                } catch (e: RestRequestException) {
                    BrieftaubeDatabase.channels.deleteOneById(it.id)
                }
            }
    }
}
