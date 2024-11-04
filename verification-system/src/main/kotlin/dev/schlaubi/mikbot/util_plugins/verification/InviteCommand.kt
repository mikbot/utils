package dev.schlaubi.mikbot.util_plugins.verification

import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.schlaubi.mikbot.plugin.api.owner.OwnerModule
import dev.schlaubi.mikbot.plugin.api.owner.ownerOnly
import dev.schlaubi.mikbot.utils.translations.VerificationSystemTranslations
import org.litote.kmongo.newId

suspend fun OwnerModule.inviteCommand() = ephemeralSlashCommand(::VerificationArguments) {
    name = VerificationSystemTranslations.Commands.Invite.name
    description = VerificationSystemTranslations.Commands.Invite.description

    ownerOnly()

    action {
        val invite = Invitation(newId(), arguments.guildId, arguments.type)
        VerificationDatabase.invites.save(invite)

        respond {
            content = "<${invite.url}>"
        }
    }
}
