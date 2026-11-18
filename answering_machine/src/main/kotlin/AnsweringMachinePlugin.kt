package dev.schlaubi.mikbot.util_plugins.answering_machine

import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kordex.core.builders.ExtensionsBuilder
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.module.MikBotModule

@PluginMain
class AnsweringMachinePlugin(context: PluginContext) : Plugin(context) {
    override fun ExtensionsBuilder.addExtensions() {
        add(::AnsweringMachineExtension)
    }
}

private class AnsweringMachineExtension(context: PluginContext) : MikBotModule(context) {
    override val name: String = "Answering Machine"

    @OptIn(PrivilegedIntent::class)
    override suspend fun setup() {
        intents.add(Intent.MessageContent)
        executor()
        addAnsweringMachine()
        deleteAnsweringMachine()
    }
}
