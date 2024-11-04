package dev.schlaubi.mikbot.util_plugins.birthdays

import dev.schlaubi.mikbot.util_plugins.birthdays.database.UserBirthday
import kotlinx.datetime.*
import java.time.Month

data class BirthdayContainer(
    val birthday: LocalDate,
    val nextBirthday: Instant,
    val dayDifference: Int,
    val newAge: Int
)

fun UserBirthday.calculate(): BirthdayContainer {
    val now = Clock.System.now()
    val currentYear = now.toLocalDateTime(timeZone).year

    val thisYearBirthDay = time.atYear(currentYear).atStartOfDayIn(timeZone)
    val nextBirthday = if (thisYearBirthDay > now) {
        thisYearBirthDay
    } else {
        time.atYear(currentYear + 1).atStartOfDayIn(timeZone)
    }

    val nextAge = time.atStartOfDayIn(timeZone).yearsUntil(nextBirthday, timeZone)
    val dayDifference = now.daysUntil(nextBirthday, timeZone)

    return BirthdayContainer(time, nextBirthday, dayDifference, nextAge)
}

fun LocalDate.atYear(year: Int): LocalDate {
    if (dayOfMonth == 29 && month == Month.FEBRUARY) { // handle leap years
        return LocalDate(year, Month.MARCH, 1)
    }
    return LocalDate(year, month, dayOfMonth)
}

fun LocalDateTime.toLocalDate() = LocalDate(year, month, dayOfMonth)
