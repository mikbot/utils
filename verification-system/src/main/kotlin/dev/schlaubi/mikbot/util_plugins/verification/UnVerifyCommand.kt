package dev.schlaubi.mikbot.util_plugins.verification

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.converters.impl.defaultingEnumChoice
import dev.kordex.core.commands.converters.impl.snowflake
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.i18n.EMPTY_KEY
import dev.schlaubi.mikbot.plugin.api.MikBotTranslations
import dev.schlaubi.mikbot.plugin.api.owner.OwnerModule
import dev.schlaubi.mikbot.plugin.api.owner.ownerOnly
import dev.schlaubi.mikbot.plugin.api.util.confirmation
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.utils.translations.VerificationSystemTranslations

class VerificationArguments : Arguments() {
    val guildId by snowflake {
        name = VerificationSystemTranslations.Arguments.Verification.Id.name
        description = VerificationSystemTranslations.Arguments.Verification.Id.description
    }

    val type by defaultingEnumChoice<InviteType> {
        name = VerificationSystemTranslations.Arguments.Verification.Type.name
        description = VerificationSystemTranslations.Arguments.Verification.Type.description
        defaultValue = InviteType.GUILD
        typeName = EMPTY_KEY
    }
}

suspend fun OwnerModule.unVerifyCommand() =
    ephemeralSlashCommand(::VerificationArguments) {
        name = VerificationSystemTranslations.Commands.UnVerify.name
        description = VerificationSystemTranslations.Commands.UnVerify.description

        ownerOnly()

        action {
            val guild = runCatching { this@ephemeralSlashCommand.kord.getGuildOrNull(arguments.guildId) }.getOrNull()

            if (guild == null) {
                respond {
                    content = translate(VerificationSystemTranslations.Command.Verify.unknownId)
                }
                return@action
            }

            val botGuild = VerificationDatabase.collection.findOneById(guild.id)
            if (botGuild?.verified != true) {
                respond { content = translate(VerificationSystemTranslations.Command.Verify.notVerified) }
                return@action
            }

            val (confirmed) = confirmation {
                content = translate(
                    VerificationSystemTranslations.Command.Verify.confirm,
                    arrayOf(guild.name)
                )
            }

            if (!confirmed) {
                respond {
                    translate(MikBotTranslations.General.aborted)
                }
                return@action
            }

            VerificationDatabase.collection.save(botGuild.copy(verified = false))
            guild.leave()

            respond {
                content = translate(VerificationSystemTranslations.Command.Verify.success, arrayOf(guild.name))
            }
        }
    }
