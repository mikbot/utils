package dev.schlaubi.mikbot.util_plugins.leaderboard.commands

import dev.kordex.core.extensions.publicSlashCommand
import dev.schlaubi.mikbot.plugin.api.util.forList
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.leaderboard.LeaderBoardDatabase
import dev.schlaubi.mikbot.util_plugins.leaderboard.core.LeaderBoardModule
import dev.schlaubi.mikbot.util_plugins.leaderboard.leaderboardForGuild
import dev.schlaubi.mikbot.utils.translations.LeaderboardTranslations
import kotlinx.coroutines.flow.toList

suspend fun LeaderBoardModule.leaderBoardCommand() = publicSlashCommand {
    name = LeaderboardTranslations.Commands.Leaderboard.name
    description = LeaderboardTranslations.Commands.Leaderboard.description
    leaderboardContext()

    action {
        val leaderboard = LeaderBoardDatabase.leaderboardEntries.leaderboardForGuild(safeGuild.id)
            .toList()
        if (leaderboard.isEmpty()) {
            respond {
                content = translate(LeaderboardTranslations.Command.Leaderboard.empty)
            }
        } else {
            editingPaginator {
                forList(user, leaderboard, {
                    translate(LeaderboardTranslations.Commands.Leaderboard.item, "<@${it.userId}>", it.level)
                }, { current, total -> translate(LeaderboardTranslations.Commands.Leaderboard.title, current, total) })
            }.send()
        }
    }
}
