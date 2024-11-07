package dev.schlaubi.mikbot.util_plugins.birthdays.database

import dev.kord.common.entity.Snowflake
import dev.kordex.core.koin.KordExKoinComponent
import dev.schlaubi.mikbot.plugin.api.io.getCollection
import dev.schlaubi.mikbot.plugin.api.util.database
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

object BirthdayDatabase : KordExKoinComponent {
    val birthdays = database.getCollection<UserBirthday>("birthdays")
    val settings = database.getCollection<ServerSettings>("birthdays_settings")
    val notifications = database.getCollection<BirthdayNotification>("birthday_notifications")
}

@Serializable
data class UserBirthday(@SerialName("_id") val id: Snowflake, val time: LocalDate, val timeZone: TimeZone, val guildId: Snowflake?)

@Serializable
data class ServerSettings(
    @SerialName("_id") val id: Snowflake,
    val birthdayChannel: Snowflake? = null,
    val message: String = "%user% is now %age% years old!"
)

@Serializable
data class BirthdayNotification(
    @SerialName("_id") val id: Id<BirthdayNotification> = newId(),
    val birthdayId: Snowflake,
    val year: UInt
)
