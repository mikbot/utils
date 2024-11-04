package dev.schlaubi.epic_games_notifier

import dev.kord.common.entity.Permission
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.schlaubi.mikbot.plugin.api.settings.SettingsExtensionPoint
import dev.schlaubi.mikbot.plugin.api.settings.SettingsModule
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.utils.translations.EpicGamesNotifierTranslations
import io.ktor.http.*
import org.pf4j.Extension

@Extension
class EpicGamesNotifierSettingsExtension : SettingsExtensionPoint {
    override suspend fun SettingsModule.apply() {
        notifierCommand()
    }
}

suspend fun SettingsModule.notifierCommand() = ephemeralSlashCommand {
    name = EpicGamesNotifierTranslations.Commands.Enable.name
    description = EpicGamesNotifierTranslations.Commands.Enable.description
    guildAdminOnly()

    check {
        hasPermission(Permission.ManageWebhooks)
        requireBotPermissions(Permission.ManageWebhooks)
    }

    action {
        respond {
            val url = URLBuilder("https://discord.com/oauth2/authorize").apply {
                parameters.append("client_id", this@notifierCommand.kord.selfId.toString())
                parameters.append("redirect_uri", redirectUri.toString())
                parameters.append("response_type", "code")
                parameters.append("scope", "webhook.incoming")
            }.buildString()
            content = translate(EpicGamesNotifierTranslations.Commands.EpicGamesNotifier.description, url)
        }
    }
}
