package dev.schlaubi.mikbot.util_plugins.leaderboard.commands

import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import dev.schlaubi.mikbot.plugin.api.util.forList
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.util_plugins.leaderboard.LeaderBoardDatabase
import dev.schlaubi.mikbot.util_plugins.leaderboard.core.LeaderBoardModule
import dev.schlaubi.mikbot.util_plugins.leaderboard.leaderboardForGuild
import kotlinx.coroutines.flow.toList

suspend fun LeaderBoardModule.leaderBoardCommand() = publicSlashCommand {
    name = "leaderboard"
    description = "commands.leaderboard.description"
    leaderboardContext()

    action {
        val leaderboard = LeaderBoardDatabase.leaderboardEntries.leaderboardForGuild(safeGuild.id)
            .toList()
        if (leaderboard.isEmpty()) {
            respond {
                content = translate("command.leaderboard.empty")
            }
        } else {
            editingPaginator {
                forList(user, leaderboard, {
                    translate("commands.leaderboard.item", arrayOf("<@${it.userId}>", it.level))
                }, { current, total -> translate("commands.leaderboard.title", arrayOf(current, total)) })
            }.send()
        }
    }
}
