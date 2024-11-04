package dev.schlaubi.mikbot.util_plugins.leaderboard.commands

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalChannel
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.schlaubi.mikbot.plugin.api.settings.SettingsModule
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.leaderboard.LeaderBoardDatabase
import dev.schlaubi.mikbot.util_plugins.leaderboard.LeaderBoardSettings
import dev.schlaubi.mikbot.utils.translations.LeaderboardTranslations

class LeaderBoardSettingsArguments : Arguments() {
    val message by optionalString {
        name = LeaderboardTranslations.Commands.Settings.Arguments.Message.name
        description = LeaderboardTranslations.Commands.Settings.Arguments.Message.description
    }

    val channel by optionalChannel {
        name = LeaderboardTranslations.Commands.Settings.Arguments.Channel.name
        description = LeaderboardTranslations.Commands.Settings.Arguments.Channel.description
        requiredChannelTypes.add(ChannelType.GuildText)
    }
}

suspend fun SettingsModule.leaderBoardCommand() =
    ephemeralSlashCommand(::LeaderBoardSettingsArguments) {
        name = LeaderboardTranslations.Commands.Settings.name
        description = LeaderboardTranslations.Commands.Settings.description

        guildAdminOnly()

        action {
            val newSettings =
                (LeaderBoardDatabase.settings.findOneById(safeGuild.id) ?: LeaderBoardSettings(safeGuild.id))
                    .merge(arguments.channel?.id, arguments.message)

            LeaderBoardDatabase.settings.save(newSettings)

            respond {
                content = translate(LeaderboardTranslations.Commands.Settings.Saved.title)
            }
        }
    }

private fun LeaderBoardSettings.merge(
    levelUpChannel: Snowflake?,
    levelUpMessage: String?
) = copy(
    levelUpChannel = levelUpChannel ?: this.levelUpChannel,
    levelUpMessage = levelUpMessage ?: this.levelUpMessage
)
