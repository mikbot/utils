package dev.schlaubi.mikbot.util_plugins.birthdays.commands

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.InteractionContextType
import dev.kord.common.toMessageFormat
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.SlashCommand
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.kordex.core.commands.converters.impl.optionalMember
import dev.schlaubi.mikbot.plugin.api.util.discordError
import dev.schlaubi.mikbot.plugin.api.util.kord
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.birthdays.calculate
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayDatabase
import dev.schlaubi.mikbot.util_plugins.birthdays.database.UserBirthday
import dev.schlaubi.mikbot.utils.translations.BirthdaysTranslations
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.or

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
        if (arguments.member != null && event.interaction.context != InteractionContextType.Guild) {
            discordError(BirthdaysTranslations.Commands.Birthday.Get.userInstall)
        }
        val id = arguments.member?.id ?: user.id
        val birthday = BirthdayDatabase.birthdays.findOne(
            and(UserBirthday::id eq id, or(UserBirthday::guildId eq null, UserBirthday::guildId eq guild?.id))
        )

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
