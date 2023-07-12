package dev.schlaubi.mikbot.util_plugins.gpt

import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import dev.schlaubi.mikbot.plugin.api.io.getCollection
import dev.schlaubi.mikbot.plugin.api.util.database

object GptDatabase : KordExKoinComponent {
    val conversations = database.getCollection<Conversation>("conversations")
}
