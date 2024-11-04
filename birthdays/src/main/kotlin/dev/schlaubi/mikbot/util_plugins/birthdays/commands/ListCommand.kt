package dev.schlaubi.mikbot.util_plugins.birthdays.commands

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.toMessageFormat
import dev.kordex.core.commands.application.slash.SlashCommand
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.schlaubi.mikbot.plugin.api.util.forList
import dev.schlaubi.mikbot.plugin.api.util.kord
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.birthdays.calculate
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayDatabase
import dev.schlaubi.mikbot.utils.translations.BirthdaysTranslations
import kotlinx.datetime.atStartOfDayIn

@OptIn(KordUnsafe::class, KordExperimental::class)
suspend fun SlashCommand<*, *, *>.listCommand() = publicSubCommand {
    name = BirthdaysTranslations.Commands.Birthday.List.name
    description = BirthdaysTranslations.Commands.Birthday.List.description

    action {
        val birthdays = BirthdayDatabase.birthdays.find().toList()

        editingPaginator {
            forList(
                user,
                birthdays,
                {
                    val (birthday, nextBirthday, _, nextAge) = it.calculate()

                    "${kord.unsafe.user(it.id).mention} - ${
                        birthday.atStartOfDayIn(it.timeZone).toMessageFormat(DiscordTimestampStyle.LongDate)
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

