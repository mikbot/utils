package dev.schlaubi.mikbot.util_plugins.brieftaube

import dev.kord.core.behavior.channel.createWebhook
import dev.kord.core.entity.channel.thread.ThreadChannel
import org.litote.kmongo.bson

suspend fun findOrCreateWebhookFor(targetChannel: ThreadChannel): Webhook {
    return BrieftaubeDatabase.channels.findOne(
        """{"webhook.channelId": new NumberLong("${targetChannel.parentId}")}""".bson
    )?.webhook ?: run {
        val actualWebhook = targetChannel.parent.createWebhook("Channel mirror")
        Webhook(actualWebhook.channelId, actualWebhook.id, actualWebhook.token!!)
    }
}
