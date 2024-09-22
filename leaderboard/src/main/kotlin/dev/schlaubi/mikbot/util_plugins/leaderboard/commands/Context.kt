package dev.schlaubi.mikbot.util_plugins.leaderboard.commands

import com.kotlindiscord.kord.extensions.commands.application.ApplicationCommand
import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.InteractionContextType

fun ApplicationCommand<*>.leaderboardContext() {
    allowedContexts.add(InteractionContextType.Guild)
    allowedInstallTypes.add(ApplicationIntegrationType.GuildInstall)
}
