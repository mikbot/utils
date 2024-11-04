package dev.schlaubi.mikbot.util_plugins.birthdays.commands

import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.InteractionContextType
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalChannel
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayDatabase
import dev.schlaubi.mikbot.util_plugins.birthdays.database.ServerSettings
import dev.schlaubi.mikbot.utils.translations.BirthdaysTranslations

class SettingsArguments : Arguments() {
    val channel by optionalChannel {
        name = BirthdaysTranslations.Commands.Settings.Arguments.Channel.name
        description = BirthdaysTranslations.Commands.Settings.Arguments.Channel.description

        requireChannelType(ChannelType.GuildText)
    }

    val message by optionalString {
        name = BirthdaysTranslations.Commands.Settings.Arguments.Message.name
        description = BirthdaysTranslations.Commands.Settings.Arguments.Message.description

        maxLength = 2000
    }
}

suspend fun Extension.settingsCommand() = ephemeralSlashCommand(::SettingsArguments) {
    guildAdminOnly()
    allowedContexts.add(InteractionContextType.Guild)
    allowedInstallTypes.add(ApplicationIntegrationType.GuildInstall)
    name = BirthdaysTranslations.Commands.Settings.name
    description = BirthdaysTranslations.Commands.Settings.description

    action {
        if (arguments.channel == null && arguments.message == null) {
            BirthdayDatabase.settings.save(ServerSettings(guild!!.id, null))
        }
        val currentSettings = BirthdayDatabase.settings.findOneById(guild!!.id) ?: ServerSettings(guild!!.id, null)
        val newSettings = currentSettings.copy(
            birthdayChannel = arguments.channel?.id ?: currentSettings.birthdayChannel,
            message = arguments.message ?: currentSettings.message
        )
        BirthdayDatabase.settings.save(newSettings)


        respond {
            content = translate(BirthdaysTranslations.Commands.Settings.success)
        }
    }
}
