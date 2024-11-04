package dev.schlaubi.mikbot.util_plugins.gpt

import dev.kordex.core.builders.ExtensionsBuilder
import dev.kordex.core.extensions.Extension
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.util_plugins.gpt.commands.newChannelCommand
import dev.schlaubi.mikbot.util_plugins.gpt.commands.newThreadCommand
import dev.schlaubi.mikbot.util_plugins.gpt.commands.startThreadContextAction

@PluginMain
class GptPlugin(context: PluginContext) : Plugin(context) {
    override fun ExtensionsBuilder.addExtensions() {
        add(::GptExtension)
    }
}

private class GptExtension : Extension() {
    override val name: String = "gpt"

    override suspend fun setup() {
        newChannelCommand()
        newThreadCommand()
        startThreadContextAction()
        gptExecutor()
    }
}
