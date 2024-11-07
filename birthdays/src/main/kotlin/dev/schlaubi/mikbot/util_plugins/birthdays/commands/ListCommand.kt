package dev.schlaubi.mikbot.util_plugins.birthdays.commands

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.InteractionContextType
import dev.kord.common.toMessageFormat
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.commands.application.slash.SlashCommand
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.schlaubi.mikbot.plugin.api.util.*
import dev.schlaubi.mikbot.util_plugins.birthdays.calculate
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayDatabase
import dev.schlaubi.mikbot.util_plugins.birthdays.database.UserBirthday
import dev.schlaubi.mikbot.utils.translations.BirthdaysTranslations
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.atStartOfDayIn
import org.litote.kmongo.descending
import org.litote.kmongo.`in`

@OptIn(KordUnsafe::class, KordExperimental::class)
suspend fun SlashCommand<*, *, *>.listCommand() = publicSubCommand {
    name = BirthdaysTranslations.Commands.Birthday.List.name
    description = BirthdaysTranslations.Commands.Birthday.List.description

    check {
        anyGuild()
        failIf(
            event.interaction.context != InteractionContextType.Guild,
            BirthdaysTranslations.Commands.Birthday.List.userInstall
        )
    }

    action {
        val members = safeGuild.members.map { it.id }.toList()
        val birthdays = BirthdayDatabase.birthdays.find(UserBirthday::id `in` members)
            .toList()
            .map { it.id to it.calculate() }
            .sortedBy { (_, calculation) -> calculation.dayDifference }

        editingPaginator {
            forList(
                user,
                birthdays,
                { (id, birthday) ->
                    val (_, nextBirthday, _, nextAge) = birthday
                    "${kord.unsafe.user(id).mention} - ${
                        nextBirthday.toMessageFormat(DiscordTimestampStyle.LongDate)
                    } (${
                        nextBirthday.toMessageFormat(DiscordTimestampStyle.RelativeTime)
                    }) ($nextAge)"
                },
                { current, total -> translate(BirthdaysTranslations.Commands.Birthday.List.title, current, total) },
                enumerate = false
            )
        }.send()
    }
}

