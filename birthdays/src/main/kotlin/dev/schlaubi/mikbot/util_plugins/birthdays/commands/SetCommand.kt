package dev.schlaubi.mikbot.util_plugins.birthdays.commands

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.asJavaLocale
import dev.kord.common.entity.Permission
import dev.kord.common.toMessageFormat
import dev.kord.core.behavior.interaction.followup.edit
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.EphemeralSlashCommandContext
import dev.kordex.core.commands.application.slash.SlashCommand
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.optionalMember
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.i18n.toKey
import dev.kordex.core.utils.hasPermission
import dev.schlaubi.mikbot.plugin.api.util.confirmation
import dev.schlaubi.mikbot.plugin.api.util.discordError
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayDatabase
import dev.schlaubi.mikbot.util_plugins.birthdays.database.UserBirthday
import dev.schlaubi.mikbot.util_plugins.birthdays.server.receiveTimeZone
import dev.schlaubi.mikbot.util_plugins.ktor.api.buildBotUrl
import dev.schlaubi.mikbot.utils.translations.BirthdaysTranslations
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.yearsUntil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.*
import kotlin.time.Duration.Companion.seconds

class SetArguments : Arguments() {
    val birthday by optionalString {
        name = BirthdaysTranslations.Commands.Birthday.Set.Arguments.Birthday.name
        description = BirthdaysTranslations.Commands.Birthday.Set.Arguments.Birthday.description
    }

    val target by optionalMember {
        name = BirthdaysTranslations.Commands.Birthday.Set.Arguments.Target.name
        description = BirthdaysTranslations.Commands.Birthday.Set.Arguments.Target.description
    }
}

private val LOG = KotlinLogging.logger { }

private val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

suspend fun SlashCommand<*, *, *>.setCommand() = ephemeralSubCommand(::SetArguments) {
    name = BirthdaysTranslations.Commands.Birthday.Set.name
    description = BirthdaysTranslations.Commands.Birthday.Set.description

    action {
        if (arguments.target != null && !member!!.asMember().hasPermission(Permission.ManageGuild)) {
            discordError(BirthdaysTranslations.Commands.Birthday.Set.noPermission)
        }
        if (arguments.birthday != null) {
            val locale = event.interaction.locale?.asJavaLocale()
                ?: event.interaction.guildLocale?.asJavaLocale()
                ?: Locale.getDefault()
            val localizedFormat = format
                .localizedBy(locale)
            val key = generateNonce()
            val url = buildBotUrl {
                appendPathSegments("birthdays", "timezone", key)
            }
            val date = try {
                val parsed = localizedFormat.parse(arguments.birthday!!)
                LocalDate.from(parsed)
            } catch (e: DateTimeParseException) {
                val sampleDate = localizedFormat.format(LocalDate.now())
                LOG.debug(e) { "Date parsing failed" }
                discordError(BirthdaysTranslations.Commands.Birthday.Set.invalidDate.withOrdinalPlaceholders(sampleDate))
            }
            val now = Clock.System.now()

            val preliminaryInstant = date.toKotlinLocalDate().atStartOfDayIn(TimeZone.UTC)
            val preliminaryAge = preliminaryInstant.yearsUntil(now, TimeZone.UTC)
            if (preliminaryInstant > now || preliminaryAge > 150) {
                invalidDateError(preliminaryAge)
                return@action
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
            val userBirthday = UserBirthday(arguments.target?.id ?: user.id, kotlinDate, timeZone, guild?.id?.takeIf { arguments.target != null })
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


private suspend fun EphemeralSlashCommandContext<*, *>.invalidDateError(actualAge: Int) {
    val confirmation = confirmation {
        content = translate(BirthdaysTranslations.Commands.Birthday.Set.InvalidDate.confirmation, actualAge)
    }

    if (!confirmation.value) {
        edit { content = translate(BirthdaysTranslations.Commands.Birthday.Set.InvalidDate.denied) }
        return
    }

    val parachuteStory = LinkedList((1..5).toList())
    val randomFacts = LinkedList((1..35).toList().shuffled().take(5))

    repeat(9) {
        edit {
            content = ""
            embed {
                title = translate(BirthdaysTranslations.Commands.Birthday.Set.InvalidDate.title)
                val message = if (it % 2 == 0) {
                    "commands.birthday.set.invalid_date.stories.parachute.${parachuteStory.poll()}".toKey(bundle = "birthdays")
                } else {
                    "commands.birthday.set.invalid_date.stories.${randomFacts.poll()}".toKey(bundle = "birthdays")
                }

                description = "<a:loading:1304227341274321028> ${translate(message)}"
            }
        }
        delay(4.seconds)
    }

    edit {
        embeds = mutableListOf()
        content = translate(BirthdaysTranslations.Commands.Birthday.Set.InvalidDate.denied)
    }
}
