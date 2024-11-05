package dev.schlaubi.mikbot.util_plugins.birthdays.commands

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.asJavaLocale
import dev.kord.common.toMessageFormat
import dev.kord.core.behavior.interaction.followup.edit
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.SlashCommand
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.optionalString
import dev.schlaubi.mikbot.plugin.api.util.discordError
import dev.schlaubi.mikbot.plugin.api.util.executableEverywhere
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayDatabase
import dev.schlaubi.mikbot.util_plugins.birthdays.database.UserBirthday
import dev.schlaubi.mikbot.util_plugins.birthdays.server.receiveTimeZone
import dev.schlaubi.mikbot.util_plugins.ktor.api.buildBotUrl
import dev.schlaubi.mikbot.utils.translations.BirthdaysTranslations
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toKotlinLocalDate
import java.text.ParseException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.*

class SetArguments : Arguments() {
    val birthday by optionalString {
        name = BirthdaysTranslations.Commands.Birthday.Set.Arguments.Birthday.name
        description = BirthdaysTranslations.Commands.Birthday.Set.Arguments.Birthday.description
    }
}

private val LOG = KotlinLogging.logger { }

suspend fun SlashCommand<*, *, *>.setCommand() = ephemeralSubCommand(::SetArguments) {
    name = BirthdaysTranslations.Commands.Birthday.Set.name
    description = BirthdaysTranslations.Commands.Birthday.Set.description

    action {
        if (arguments.birthday != null) {
            val locale = event.interaction.locale?.asJavaLocale()
                ?: event.interaction.guildLocale?.asJavaLocale()
                ?: Locale.getDefault()
            val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            val key = generateNonce()
            val url = buildBotUrl {
                appendPathSegments("birthdays", "timezone", key)
            }
            val date = try {
                val parsed = format.parse(arguments.birthday!!)
                LocalDate.from(parsed)
            } catch (e: DateTimeParseException) {
                LOG.debug(e) { "Date parsing failed" }
                discordError(BirthdaysTranslations.Commands.Birthday.Set.invalidDate)
            }
            val message = respond {
                embed {
                    title = translate(BirthdaysTranslations.Commands.Birthday.Set.Timezone.title)
                    description = translate(BirthdaysTranslations.Commands.Birthday.Set.Timezone.description, url)
                }
            }
            val timeZone = receiveTimeZone(key)
            if (timeZone == null) {
                message.edit {
                    content = translate(BirthdaysTranslations.Commands.Birthday.Set.timeout)
                    embeds = mutableListOf()
                }
                return@action
            }
            val kotlinDate = date.toKotlinLocalDate()
            val instant = kotlinDate.atStartOfDayIn(timeZone)
            val userBirthday = UserBirthday(user.id, kotlinDate, timeZone)
            BirthdayDatabase.birthdays.save(userBirthday)

            message.edit {
                embeds = mutableListOf()
                content = translate(
                    BirthdaysTranslations.Commands.Birthday.Set.success,
                    instant.toMessageFormat(DiscordTimestampStyle.LongDate)
                )
            }
        } else {
            BirthdayDatabase.birthdays.deleteOneById(user.id)

            respond {
                content = translate(BirthdaysTranslations.Commands.Birthday.delete)
            }
        }
    }
}
