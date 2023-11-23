package dev.schlaubi.mikbot.util_plugins.brieftaube

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.behavior.execute
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.request.RestRequestException
import dev.schlaubi.mikbot.plugin.api.util.effectiveAvatar
import dev.schlaubi.stdx.coroutines.forEachParallel
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import org.litote.kmongo.eq

private val client = HttpClient {
    expectSuccess = true
}

@OptIn(KordUnsafe::class, KordExperimental::class)
suspend fun Extension.mirrorChannelExecutor() = event<MessageCreateEvent> {
    action {
        BrieftaubeDatabase.channels
            .find(MirroredChannel::sourceChannelId eq event.message.channelId)
            .toFlow()
            .collect {
                try {
                    val webhook = it.webhook ?: run {
                        val thread = kord.getChannelOf<ThreadChannel>(it.targetChannelId)!!
                        findOrCreateWebhookFor(thread).also { webhook ->
                            BrieftaubeDatabase.channels.save(it.copy(webhook = webhook))
                        }
                    }
                    val target = kord.unsafe.webhook(webhook.webhookId)

                    target.execute(webhook.token, threadId = it.targetChannelId) {
                        username = event.message.author?.username
                        avatarUrl = event.message.author?.effectiveAvatar

                        content = event.message.content
                        embeds = event.message.embeds.map { EmbedBuilder().apply { it.apply(this) } }.toMutableList()
                        event.message.attachments.forEachParallel { attachment ->
                            val bytes = client.get(attachment.url).bodyAsChannel()
                            addFile(attachment.filename, ChannelProvider { bytes })
                        }
                    }
                } catch (e: RestRequestException) {
                    BrieftaubeDatabase.channels.deleteOneById(it.id)
                }
            }
    }
}
