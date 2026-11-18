package dev.schlaubi.mikbot.util_plugins.misc.commands

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.EphemeralSlashCommandContext
import dev.kordex.core.commands.converters.impl.long
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.utils.translations.MiscTranslations
import kotlin.random.Random

suspend fun Extension.randomCommands() {
    randomCommand()
    diceCommand()
}

interface RandomArguments {
    val min: Long
    val max: Long
}

private class RandomCommandArguments : Arguments(), RandomArguments {
    override val min: Long by long {
        name = MiscTranslations.Commands.Random.Arguments.From.name
        description = MiscTranslations.Commands.Random.Arguments.From.description
    }
    override val max: Long by long {
        name = MiscTranslations.Commands.Random.Arguments.To.name
        description = MiscTranslations.Commands.Random.Arguments.To.description
    }
}

private class DiceArguments : Arguments(), RandomArguments {
    override val min: Long = 0
    override val max: Long by long {
        name = MiscTranslations.Commands.Dice.Arguments.Sides.name
        description = MiscTranslations.Commands.Dice.Arguments.Sides.description
    }
}

private suspend fun Extension.randomCommand() = ephemeralSlashCommand(::RandomCommandArguments) {
    name = MiscTranslations.Commands.Random.name
    description = MiscTranslations.Commands.Random.description

    action { handle() }
}

private suspend fun Extension.diceCommand() = ephemeralSlashCommand(::DiceArguments) {
    name = MiscTranslations.Commands.Dice.name
    description = MiscTranslations.Commands.Dice.description

    action { handle() }
}

private suspend fun <T> EphemeralSlashCommandContext<T, *>.handle() where T : Arguments, T : RandomArguments {
    respond {
        val number = Random.nextLong(arguments.min, arguments.max)

        content = translate(MiscTranslations.Commands.Random.result, number)
    }
}