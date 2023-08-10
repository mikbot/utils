package dev.schlaubi.mikbot.util_plugins.answering_machine

import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private const val separator = EmbedBuilder.ZERO_WIDTH_SPACE

typealias SerializableRegex = @Serializable(with = RegexSerializer::class) Regex

object RegexSerializer : KSerializer<Regex> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Regex", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Regex) {
        val pattern = "$value$separator${value.options.joinToString(",")}"
        encoder.encodeString(pattern)
    }

    override fun deserialize(decoder: Decoder): Regex {
        val split = decoder.decodeString().split(separator)
        if (split.size == 1) return split.first().toRegex()
        val (pattern, optionsRaw) = split
        val options = optionsRaw.split(',').map(RegexOption::valueOf)
        return pattern.toRegex(options.toSet())
    }
}
