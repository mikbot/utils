package dev.schlaubi.mikbot.util_plugins.leaderboard.commands

import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.schlaubi.mikbot.plugin.api.settings.SettingsModule
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.confirmation
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.leaderboard.core.importForGuild
import dev.schlaubi.mikbot.utils.translations.LeaderboardTranslations
import mu.KotlinLogging

private val LOG = KotlinLogging.logger { }

suspend fun SettingsModule.importMee6() = ephemeralSlashCommand {
    name = LeaderboardTranslations.Commands.ImportMee6Leaderboard.name
    description = LeaderboardTranslations.Commands.ImportMee6Leaderboard.description

    guildAdminOnly()

    action {
        val (confirmed) = confirmation {
            content = translate(LeaderboardTranslations.Commands.ImportMee6.confirm)
        }
        if (!confirmed) return@action

        try {
            importForGuild(safeGuild.id)

            respond {
                content = translate(LeaderboardTranslations.Commands.ImportMee6.success)
            }
        } catch (e: Exception) {
            LOG.warn(e) { "An error occurred during a mee6 import" }
            respond {
                content = translate(LeaderboardTranslations.Commands.ImportMee6.failed)
            }
        }
    }
}
