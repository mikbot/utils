package dev.schlaubi.mikbot.util_plugins.answering_machine

import com.kotlindiscord.kord.extensions.checks.guildFor
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingBoolean
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.ephemeralButton
import com.kotlindiscord.kord.extensions.components.forms.ModalForm
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.suggestStringMap
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.interaction.response.edit
import dev.schlaubi.mikbot.plugin.api.util.discordError
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import java.util.regex.PatternSyntaxException

class AddAnsweringMachineArgs : Arguments() {
    val pattern by string {
        name = "pattern"
        description = "commands.add_answering_machine.arguments.pattern.description"
    }

    val replacement by string {
        name = "replacement"
        description = "commands.add_answering_machine.arguments.replacement.description"
    }

    val delete by defaultingBoolean {
        name = "delete"
        description = "commands.add_answering_machine.arguments.delete.description"

        defaultValue = false
    }

    val ignoreCase by defaultingBoolean {
        name = "ignore-case"
        description = "commands.add_answering_machine.arguments.ignore_case.description"
        defaultValue = true
    }
}

class TestInputModal : ModalForm() {
    override var title: String = "Test your input"

    val testInput = lineText {
        label = "Test Message"
    }
}

class DeleteAnsweringMachineArgs : Arguments() {
    val machine by string {
        name = "pattern"
        description = "commands.delete_answering_machine.arguments.machine.description"

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
    name = "add-answering-machine"
    description = "commands.add_answering_machine.description"

    requirePermission(Permission.ManageGuild)
    requireBotPermissions(Permission.ManageMessages)
    allowInDms = false

    action { input ->
        val response = interactionResponse
        val regex = try {
            if (arguments.ignoreCase) {
                arguments.pattern.toRegex(RegexOption.IGNORE_CASE)
            } else {
                arguments.pattern.toRegex()
            }
        } catch (e: PatternSyntaxException) {
            discordError(e.message!!)
        }
        val replacement = input!!.testInput.value!!.replace(regex, arguments.replacement)


        respond {
            content = translate("commands.add_answering_machine.confirmation.received", arrayOf(replacement))

            components {
                ephemeralButton {
                    bundle = "answering_machine"
                    style = ButtonStyle.Success
                    label = translate("commands.add_answering_machine.confirmation.confirm")
                    action {
                        val entity = AnswerRegex(
                            guildId = guild!!.id,
                            regex = regex,
                            replacement = arguments.replacement,
                            delete = arguments.delete
                        )
                        AnsweringMachineDatabase.regexes.save(entity)

                        response.edit {
                            content = translate("commands.add_answering_machine.created")
                            components = mutableListOf()
                        }
                    }

                }

                ephemeralButton {
                    bundle = "answering_machine"
                    style = ButtonStyle.Danger
                    label = translate("commands.add_answering_machine.confirmation.decline")

                    action {
                        response.edit {
                            content = translate("commands.add_answering_machine.declined")
                            components = mutableListOf()
                        }
                    }
                }
            }
        }
    }
}

suspend fun Extension.deleteAnsweringMachine() = ephemeralSlashCommand(::DeleteAnsweringMachineArgs) {
    name = "delete-answering-machine"
    description = "commands.delete_answering_machine.description"

    allowInDms = false
    requirePermission(Permission.ManageGuild)

    action {
        AnsweringMachineDatabase.regexes.deleteOneById(ObjectId(arguments.machine))

        respond {
            content = translate("commands.delete_answering_machine.success")
        }
    }
}
