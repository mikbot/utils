package dev.schlaubi.mikbot.util_plugins.birthdays.commands

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import dev.schlaubi.mikbot.plugin.api.util.executableEverywhere
import dev.schlaubi.mikbot.utils.translations.BirthdaysTranslations

suspend fun Extension.birthdayCommand() = publicSlashCommand {
    name = BirthdaysTranslations.Commands.Birthday.name
    description = "<unused>".toKey()
    executableEverywhere()

    setCommand()
    listCommand()
    getCommand()
}
