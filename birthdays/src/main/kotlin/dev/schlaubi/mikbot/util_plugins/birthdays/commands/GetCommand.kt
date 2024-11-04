package dev.schlaubi.mikbot.util_plugins.birthdays.commands

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.toMessageFormat
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.SlashCommand
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.kordex.core.commands.converters.impl.optionalMember
import dev.schlaubi.mikbot.plugin.api.util.kord
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.birthdays.calculate
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayDatabase
import dev.schlaubi.mikbot.utils.translations.BirthdaysTranslations

class GetArguments : Arguments() {
    val member by optionalMember {
        name = BirthdaysTranslations.Commands.Birthday.Get.Arguments.Member.name
        description = BirthdaysTranslations.Commands.Birthday.Get.Arguments.Member.description
    }
}

@OptIn(KordUnsafe::class, KordExperimental::class)
suspend fun SlashCommand<*, *, *>.getCommand() = publicSubCommand(::GetArguments) {
    name = BirthdaysTranslations.Commands.Birthday.Get.name
    description = BirthdaysTranslations.Commands.Birthday.Get.description

    action {
        val birthday = BirthdayDatabase.birthdays.findOneById(arguments.member?.id ?: user.id)

        if (birthday != null) {
            val (_, nextBirthday, days, age) = birthday.calculate()
            val mention = kord.unsafe.user(birthday.id).mention
            respond {
                content = translate(
                    BirthdaysTranslations.Commands.Birthday.Get.info,
                    mention, age, days, nextBirthday.toMessageFormat(DiscordTimestampStyle.LongDate)
                )
            }
        } else {
            respond {
                content = translate(BirthdaysTranslations.Commands.Birthday.Get.unknown, user.mention)
            }
        }
    }
}
