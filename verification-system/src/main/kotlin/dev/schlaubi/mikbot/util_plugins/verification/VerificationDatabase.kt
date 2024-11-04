package dev.schlaubi.mikbot.util_plugins.verification

import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.koin.KordExKoinComponent
import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.Snowflake
import dev.kordex.core.i18n.types.Key
import dev.schlaubi.mikbot.plugin.api.io.getCollection
import dev.schlaubi.mikbot.plugin.api.util.database
import dev.schlaubi.mikbot.utils.translations.VerificationSystemTranslations
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class InviteType(val integrationType: ApplicationIntegrationType, override val readableName: Key) : ChoiceEnum {
    GUILD(ApplicationIntegrationType.GuildInstall, VerificationSystemTranslations.Verification.Type.guild),
    USER(ApplicationIntegrationType.UserInstall, VerificationSystemTranslations.Verification.Type.user)
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
