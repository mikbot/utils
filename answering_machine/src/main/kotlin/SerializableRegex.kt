package dev.schlaubi.mikbot.util_plugins.answering_machine

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerializableRegex = @Serializable(with = RegexSerializer::class) Regex

object RegexSerializer : KSerializer<Regex> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Regex", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Regex) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): Regex = decoder.decodeString().toRegex()
}
