package dev.schlaubi.mikbot.util_plugins.leaderboard.commands

import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.InteractionContextType
import dev.kordex.core.commands.application.ApplicationCommand
import io.ktor.server.config.configLoaders

fun ApplicationCommand<*>.leaderboardContext() {
    scope {
        guildInstallable()
        contexts.allowInGuilds()
    }
}
