package dev.schlaubi.mikbot.util_plugins.answering_machine

import dev.kordex.core.checks.guildFor
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.defaultingBoolean
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.components.components
import dev.kordex.core.components.ephemeralButton
import dev.kordex.core.components.forms.ModalForm
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.utils.suggestStringMap
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.interaction.response.edit
import dev.kordex.core.i18n.toKey
import dev.kordex.core.i18n.types.Key
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.discordError
import dev.schlaubi.mikbot.plugin.api.util.translate
import dev.schlaubi.mikbot.utils.translations.AnsweringMachineTranslations
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import java.util.regex.PatternSyntaxException

class AddAnsweringMachineArgs : Arguments() {
    val pattern by string {
        name = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Arguments.Pattern.name
        description = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Arguments.Pattern.description
    }

    val replacement by string {
        name = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Arguments.Replacement.name
        description = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Arguments.Replacement.description
    }

    val delete by defaultingBoolean {
        name = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Arguments.Delete.name
        description = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Arguments.Delete.description

        defaultValue = false
    }

    val ignoreCase by defaultingBoolean {
        name = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Arguments.IgnoreCase.name
        description = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Arguments.IgnoreCase.description
        defaultValue = true
    }
}

class TestInputModal : ModalForm() {
    override var title: Key = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Modals.Test.tile

    val testInput = lineText {
        label = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Modals.Test.Fields.Text.label
    }
}

class DeleteAnsweringMachineArgs : Arguments() {
    val machine by string {
        name = AnsweringMachineTranslations.Commands.DeleteAnsweringMachine.Arguments.Machine.name
        description = AnsweringMachineTranslations.Commands.DeleteAnsweringMachine.Arguments.Machine.description

        autoComplete { event ->
            val answers = AnsweringMachineDatabase.regexes
                .find(AnswerRegex::guildId eq guildFor(event)?.id)
                .toFlow()
                .take(25)
                .map { "${it.regex} -> ${it.replacement}" to it.id.toString() }
                .toList()
                .toMap()

            suggestStringMap(answers)
        }
    }
}

suspend fun Extension.addAnsweringMachine() = ephemeralSlashCommand(::AddAnsweringMachineArgs, ::TestInputModal) {
    name = AnsweringMachineTranslations.Commands.AddAnsweringMachine.name
    description = AnsweringMachineTranslations.Commands.AddAnsweringMachine.description

    guildAdminOnly()

    requireBotPermissions(Permission.ManageMessages)

    action { input ->
        val response = interactionResponse
        val regex = try {
            if (arguments.ignoreCase) {
                arguments.pattern.toRegex(RegexOption.IGNORE_CASE)
            } else {
                arguments.pattern.toRegex()
            }
        } catch (e: PatternSyntaxException) {
            discordError(e.message!!.toKey())
        }
        val replacement = input!!.testInput.value!!.replace(regex, arguments.replacement)


        respond {
            content = translate(AnsweringMachineTranslations.Commands.AddAnsweringMachine.Confirmation.received, replacement)

            components {
                ephemeralButton {
                    style = ButtonStyle.Success
                    label = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Confirmation.confirm
                    action {
                        val entity = AnswerRegex(
                            guildId = guild!!.id,
                            regex = regex,
                            replacement = arguments.replacement,
                            delete = arguments.delete
                        )
                        AnsweringMachineDatabase.regexes.save(entity)

                        response.edit {
                            content = translate(AnsweringMachineTranslations.Commands.AddAnsweringMachine.created)
                            components = mutableListOf()
                        }
                    }

                }

                ephemeralButton {
                    style = ButtonStyle.Danger
                    label = AnsweringMachineTranslations.Commands.AddAnsweringMachine.Confirmation.decline

                    action {
                        response.edit {
                            content = translate(AnsweringMachineTranslations.Commands.AddAnsweringMachine.declined)
                            components = mutableListOf()
                        }
                    }
                }
            }
        }
    }
}

suspend fun Extension.deleteAnsweringMachine() = ephemeralSlashCommand(::DeleteAnsweringMachineArgs) {
    name = AnsweringMachineTranslations.Commands.DeleteAnsweringMachine.name
    description = AnsweringMachineTranslations.Commands.DeleteAnsweringMachine.description

    guildAdminOnly()

    action {
        AnsweringMachineDatabase.regexes.deleteOneById(ObjectId(arguments.machine))

        respond {
            content = translate(AnsweringMachineTranslations.Commands.DeleteAnsweringMachine.success)
        }
    }
}
