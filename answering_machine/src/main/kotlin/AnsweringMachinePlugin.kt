package dev.schlaubi.mikbot.util_plugins.answering_machine

import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.module.MikBotModule

@PluginMain
class AnsweringMachinePlugin(context: PluginContext) : Plugin(context) {
    override fun ExtensibleBotBuilder.ExtensionsBuilder.addExtensions() {
        add(::AnsweringMachineExtension)
    }
}

private class AnsweringMachineExtension(context: PluginContext) : MikBotModule(context) {
    override val name: String = "Answering Machine"
    override val bundle: String = "answering_machine"

    override suspend fun setup() {
        executor()
        addAnsweringMachine()
        deleteAnsweringMachine()
    }
}
