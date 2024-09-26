package dev.schlaubi.mikbot.util_plugins.botblock

import dev.schlaubi.mikbot.plugin.api.EnvironmentConfig

object Config : EnvironmentConfig("") {
    val REDIS_URL by this
    val BOTBLOCK_DELAY by getEnv(10) { it.toInt() }
    @OptIn(ExperimentalStdlibApi::class)
    val SUPPORTED_BOT_LISTS by getEnv {
        it.split(",\\s*".toRegex()).map { listName ->
            it to buildString(listName.length) {
                listName.forEach { char ->
                    if (char.isLetterOrDigit()) append(char.uppercaseChar()) else append('_')
                }
            }
        }
    }
}
