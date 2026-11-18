package dev.schlaubi.mikbot.util_plugins.misc

import dev.kordex.core.builders.ExtensionsBuilder
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.module.MikBotModule
import dev.schlaubi.mikbot.util_plugins.misc.commands.randomCommands

@PluginMain
class MiscPlugin(context: PluginContext) : Plugin(context) {
    override fun ExtensionsBuilder.addExtensions() {
        add(::MiscExtension)
    }
}

private class MiscExtension(context: PluginContext) : MikBotModule(context) {
    override val name: String = "Answering Machine"

    override suspend fun setup() {
        randomCommands()
    }
}
