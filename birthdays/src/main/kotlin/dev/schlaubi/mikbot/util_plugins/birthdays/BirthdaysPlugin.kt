package dev.schlaubi.mikbot.util_plugins.birthdays

import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kordex.core.builders.ExtensionsBuilder
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.util.AllShardsReadyEvent
import dev.schlaubi.mikbot.util_plugins.birthdays.commands.birthdayCommand
import dev.schlaubi.mikbot.util_plugins.birthdays.commands.settingsCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

@PluginMain
class BirthdaysPlugin(wrapper: PluginContext) : Plugin(wrapper) {
    override fun ExtensionsBuilder.addExtensions() {
        add(::BirthdaysModule)
    }
}

class BirthdaysModule : Extension(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default + SupervisorJob()
    override val name: String = "birthdays"

    // We want to use a new scope here, since we can't suspend setup() forever
    @OptIn(PrivilegedIntent::class)
    @Suppress("SuspendFunctionOnCoroutineScope")
    override suspend fun setup() {
        intents.add(Intent.GuildMembers)
        intents.add(Intent.GuildPresences)

        birthdayCommand()
        settingsCommand()


        event<AllShardsReadyEvent> {
            action {
                launchNotifier(kord)
            }
        }
    }

    override suspend fun unload() {
        coroutineContext.cancel()
    }
}
