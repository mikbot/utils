package dev.schlaubi.mikbot.util_plugins.answering_machine

import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import dev.kord.common.entity.Snowflake
import dev.schlaubi.mikbot.plugin.api.io.getCollection
import dev.schlaubi.mikbot.plugin.api.util.database
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

object AnsweringMachineDatabase : KordExKoinComponent{
    val regexes = database.getCollection<AnswerRegex>("answer_regexes")
}

@Serializable
data class AnswerRegex(
    @SerialName("_id")
    val id: @Contextual Id<AnswerRegex> = newId(),
    val guildId: Snowflake,
    val regex: SerializableRegex,
    val replacement: String,
    val delete: Boolean
)
