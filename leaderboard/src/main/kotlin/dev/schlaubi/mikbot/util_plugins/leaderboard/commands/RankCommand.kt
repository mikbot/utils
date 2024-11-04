package dev.schlaubi.mikbot.util_plugins.leaderboard.commands

import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalMember
import dev.kordex.core.extensions.publicSlashCommand
import dev.schlaubi.mikbot.plugin.api.util.effectiveAvatar
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.leaderboard.*
import dev.schlaubi.mikbot.util_plugins.leaderboard.core.LeaderBoardModule
import dev.schlaubi.mikbot.utils.translations.LeaderboardTranslations

class RankArguments : Arguments() {
    val target by optionalMember {
        name = LeaderboardTranslations.Commands.Rank.Arguments.Target.name
        description = LeaderboardTranslations.Commands.Rank.Arguments.Target.description
    }
}

suspend fun LeaderBoardModule.rankCommand() = publicSlashCommand(::RankArguments) {
    name = LeaderboardTranslations.Commands.Rank.name
    description = LeaderboardTranslations.Commands.Rank.description

    leaderboardContext()

    action {
        val target = (arguments.target ?: user).asMember(safeGuild.id)
        val profile = LeaderBoardDatabase.leaderboardEntries.findByMember(target)
        val rank = LeaderBoardDatabase.leaderboardEntries.calculateRank(safeGuild.id, profile.points)

        respond {
            embed {
                author {
                    name = target.nickname ?: target.username
                    icon = target.memberAvatar?.cdnUrl?.toUrl()
                        ?: target.effectiveAvatar
                }

                field {
                    name = translate(LeaderboardTranslations.Commands.Rank.rank)
                    value = (rank + 1).toString()
                }

                field {
                    name = translate(LeaderboardTranslations.Commands.Rank.level)
                    value = profile.level.toString()
                }

                field {
                    name = translate(LeaderboardTranslations.Commands.Rank.progress)
                    value = formatProgress(
                        profile.points - calculateXpForLevel(profile.level),
                        calculateXPNeededForNextLevel(profile.level)
                    )
                }
            }
        }
    }
}

private fun formatProgress(current: Long, total: Long) =
    "█".repeat(((current.toDouble() / total.toDouble()) * 20).toInt())
        .padEnd(20, '▒') + " ${current.sanitizeNumber()}/${total.sanitizeNumber()}"

private fun Long.sanitizeNumber(): String = when (toDouble()) {
    in 1000.0..999999.0 -> "${div(1000.0)}k"
    in 1000000.0..9999999.0 -> "${div(1000000.0)}m"
    else -> toString()
}
