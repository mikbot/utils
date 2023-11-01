package dev.schlaubi.mikbot.utils.roleselector.command.roleselection

import com.kotlindiscord.kord.extensions.commands.application.slash.EphemeralSlashCommand
import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.utils.roleselector.RoleSelectorDatabase
import dev.schlaubi.mikbot.utils.roleselector.util.replace
import dev.schlaubi.mikbot.utils.roleselector.util.setTranslationKey
import dev.schlaubi.mikbot.utils.roleselector.util.toPartialEmoji
import dev.schlaubi.mikbot.utils.roleselector.util.translateString
import dev.schlaubi.mikbot.utils.roleselector.util.updateMessage

suspend fun EphemeralSlashCommand<*, *>.editRoleSelectionCommand() = ephemeralSubCommand(::AddRoleSelectionArguments) {
    name = "edit-role"
    description = "commands.edit_role.description"
    guildAdminOnly()
    setTranslationKey()

    action {
        val message = arguments.message
        val role = arguments.role
        val label = arguments.label
        val emojiArg = arguments.emoji

        if (label == null && emojiArg == null) {
            respond {
                content = translateString("commands.role_selection.message.nothing-to-edit")
            }
            return@action
        }

        val oldRoleSelectionMessage = RoleSelectorDatabase.roleSelectionCollection.findOneById(message.id)

        if (oldRoleSelectionMessage == null) {
            respond {
                content = translateString("commands.error.not_a_role_selection_message")
            }
            return@action
        }

        if (oldRoleSelectionMessage.roleSelections.any { it.roleId == role.id }) {
            val oldRoleSelectionButton =
                oldRoleSelectionMessage.roleSelections.find { it.roleId == role.id } ?: return@action
            val newRoleSelectionMessage = oldRoleSelectionMessage.copy(
                messageId = message.id,
                guildId = safeGuild.id,
                roleSelections = oldRoleSelectionMessage.roleSelections.replace(
                    oldRoleSelectionButton,
                    oldRoleSelectionButton.copy(
                        buttonId = oldRoleSelectionButton.buttonId,
                        label = label ?: oldRoleSelectionButton.label,
                        emoji = emojiArg.toPartialEmoji()
                            ?: oldRoleSelectionButton.emoji,
                        roleId = oldRoleSelectionButton.roleId,
                    )
                )
            )

            RoleSelectorDatabase.roleSelectionCollection.save(newRoleSelectionMessage)

            updateMessage(message, newRoleSelectionMessage)

            respond {
                content = translateString("commands.role_selection.message.edited")
            }
        }
    }
}
