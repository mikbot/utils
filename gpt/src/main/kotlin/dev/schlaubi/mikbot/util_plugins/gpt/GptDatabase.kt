package dev.schlaubi.mikbot.util_plugins.gpt

import dev.kordex.core.koin.KordExKoinComponent
import dev.schlaubi.mikbot.plugin.api.io.getCollection
import dev.schlaubi.mikbot.plugin.api.util.database

object GptDatabase : KordExKoinComponent {
    val conversations = database.getCollection<Conversation>("conversations")
}
