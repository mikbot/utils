package dev.schlaubi.mikbot.util_plugins.gpt

import dev.schlaubi.mikbot.plugin.api.EnvironmentConfig
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object Config : EnvironmentConfig() {
    val CONVERSATION_LIFETIME by getEnv(7.days) { it.toInt().toDuration(DurationUnit.MINUTES) }
}
