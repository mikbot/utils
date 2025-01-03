package dev.schlaubi.mikbot.util_plugins.birthdays

import dev.kord.core.Kord
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.rest.request.RestRequestException
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayDatabase
import dev.schlaubi.mikbot.util_plugins.birthdays.database.BirthdayNotification
import dev.schlaubi.mikbot.util_plugins.birthdays.database.ServerSettings
import dev.schlaubi.mikbot.util_plugins.birthdays.database.UserBirthday
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.litote.kmongo.eq
import org.litote.kmongo.nin
import org.litote.kmongo.not
import kotlin.time.Duration.Companion.minutes

private val timeZone = TimeZone.currentSystemDefault()

private val LOG = KotlinLogging.logger { }

fun CoroutineScope.launchNotifier(kord: Kord) = launch {
    while (isActive) {
        cycle(kord)
        delay(10.minutes)
    }
}

private suspend fun cycle(kord: Kord) {
    val currentYear = Clock.System.now().toLocalDateTime(timeZone).year.toUInt()
    val now = Clock.System.now()
    BirthdayDatabase.notifications.deleteMany(not(BirthdayNotification::year eq currentYear))
    val currentNotifications = BirthdayDatabase.notifications.find()
        .toFlow()
        .map { it.birthdayId }
        .toList()
    val birthdays = BirthdayDatabase.birthdays.find(UserBirthday::id nin currentNotifications)
        .toFlow()
        .filter { it.time.atYear(currentYear.toInt()) == now.toLocalDateTime(it.timeZone).toLocalDate() }
        .toList()

    BirthdayDatabase.settings.find(not(ServerSettings::birthdayChannel eq null)).toList().forEach {
        try {
            val guild = kord.getGuild(it.id)
            val members = kord.getGuild(it.id).members.map { member -> member.id }.toList()
            val guildBirthdays = birthdays.filter { birthday -> birthday.id in members }

            val message = buildString {
                guildBirthdays.forEach { birthday ->
                    val age = birthday.time.atStartOfDayIn(birthday.timeZone).yearsUntil(now, birthday.timeZone)

                    appendLine(
                        it.message
                            .replace("%user%", "<@${birthday.id}>")
                            .replace("%age%", age.toString())
                    )
                }
            }

            val notifications = guildBirthdays.map { BirthdayNotification(birthdayId = it.id, year = currentYear) }
            BirthdayDatabase.notifications.insertMany(notifications)
            if (message.isNotBlank()) {
                guild.getChannelOf<GuildMessageChannel>(it.birthdayChannel!!).createMessage(message)
            }
        } catch (e: RestRequestException) {
            LOG.warn(e) { "An error occurred whilst sending birthday notifications" }
        }
    }
}
