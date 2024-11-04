package dev.schlaubi.mikbot.util_plugins.brieftaube

import dev.kordex.core.koin.KordExKoinComponent
import dev.kord.common.entity.Snowflake
import dev.schlaubi.mikbot.plugin.api.io.getCollection
import dev.schlaubi.mikbot.plugin.api.util.database
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

object BrieftaubeDatabase : KordExKoinComponent{
    val channels = database.getCollection<MirroredChannel>("mirrored_chaannels")
}

@Serializable
data class MirroredChannel(
    @SerialName("_id")
    val id: @Contextual Id<MirroredChannel> = newId(),
    val guildId: Snowflake,
    val sourceChannelId: Snowflake,
    val targetChannelId: Snowflake,
    val webhook: Webhook? = null
)

@Serializable
data class Webhook(
    val channelId: Snowflake,
    val webhookId: Snowflake,
    val token: String
)
