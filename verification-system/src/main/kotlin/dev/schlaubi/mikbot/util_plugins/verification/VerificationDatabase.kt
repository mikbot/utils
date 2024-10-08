package dev.schlaubi.mikbot.util_plugins.verification

import com.kotlindiscord.kord.extensions.commands.application.slash.converters.ChoiceEnum
import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.Snowflake
import dev.schlaubi.mikbot.plugin.api.io.getCollection
import dev.schlaubi.mikbot.plugin.api.util.database
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class InviteType(val integrationType: ApplicationIntegrationType, override val readableName: String) : ChoiceEnum {
    GUILD(ApplicationIntegrationType.GuildInstall, "Guild"),
    USER(ApplicationIntegrationType.UserInstall, "User")
}

object VerificationDatabase : KordExKoinComponent {
    val collection = database.getCollection<VerificationListEntry>("verified_guilds")
    val invites = database.getCollection<Invitation>("invites")
}

@Serializable
data class VerificationListEntry(
    @SerialName("_id") val guildId: Snowflake,
    val verified: Boolean,
    val type: InviteType = InviteType.GUILD
)
