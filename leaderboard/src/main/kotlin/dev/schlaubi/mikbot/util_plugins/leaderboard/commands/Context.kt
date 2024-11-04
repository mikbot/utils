package dev.schlaubi.mikbot.util_plugins.leaderboard.commands

import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.InteractionContextType
import dev.kordex.core.commands.application.ApplicationCommand

fun ApplicationCommand<*>.leaderboardContext() {
    allowedContexts.add(InteractionContextType.Guild)
    allowedInstallTypes.add(ApplicationIntegrationType.GuildInstall)
}
